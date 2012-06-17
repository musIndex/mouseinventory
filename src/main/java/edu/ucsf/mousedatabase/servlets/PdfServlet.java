package edu.ucsf.mousedatabase.servlets;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

import static edu.ucsf.mousedatabase.HTMLGeneration.*;

import java.io.IOException;
import java.io.StringReader;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.itextpdf.text.*;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.HTMLUtilities;
import edu.ucsf.mousedatabase.Log;
import edu.ucsf.mousedatabase.objects.Facility;
import edu.ucsf.mousedatabase.objects.Gene;
import edu.ucsf.mousedatabase.objects.Holder;
import edu.ucsf.mousedatabase.objects.MouseHolder;
import edu.ucsf.mousedatabase.objects.MouseRecord;
import edu.ucsf.mousedatabase.objects.MouseType;

public class PdfServlet extends HttpServlet {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static Font FONT_NORMAL,
            FONT_BOLD,
            FONT_MOUSENAME,
            FONT_TITLE;
  private static Chunk NL;

  private String documentName = SiteName;

  static {
    FONT_NORMAL = new Font(FontFamily.HELVETICA);
    FONT_NORMAL.setSize(10);
    FONT_BOLD = new Font(FONT_NORMAL);
    FONT_BOLD.setStyle(Font.BOLD);
    FONT_MOUSENAME = new Font(FONT_BOLD);
    FONT_TITLE = new Font(FONT_BOLD);
    FONT_TITLE.setSize(16);

    NL = Chunk.NEWLINE;
  }

  protected void doGet(
    HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    try {

      Element data = getMouseListPdf(request);
      ServletOutputStream stream = response.getOutputStream();

      Document document = new Document();
      PdfWriter.getInstance(document, stream);
      document.open();
      document.add(data);
      document.close();

      response.setHeader("Expires", "0");
      response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
      response.setContentType("application/pdf");

      //This doesn't seem to take effect, oh well
      response.setHeader("Content-Disposition", "attachment; filename=\"" + documentName + ".pdf\"");

      Log.Info("Exported pdf - " + documentName);
    } catch (Exception e) {
      response.setContentType("text/html");
      response.getWriter().write("Error processing pdf: " + e.getLocalizedMessage());
      Log.Error("Error processing pdf!",e);
    }
  }

  private Element getMouseListPdf(HttpServletRequest request) throws Exception
  {
    int holderID = stringToInt(request.getParameter("holder_id"));
    int geneID = stringToInt(request.getParameter("geneID"));
    int mouseTypeID = stringToInt(request.getParameter("mousetype_id"));
    int creOnly = stringToInt(request.getParameter("creonly"));
    int facilityID = stringToInt(request.getParameter("facility_id"));
    int offset = -1;
    int limit = -1;

    String orderBy = request.getParameter("orderby");
    String searchTerms = request.getParameter("searchterms");
    String status = request.getParameter("status");

    if (orderBy == null || orderBy.isEmpty()) {
      orderBy = "mouse.name";
    }

    if(status == null)
    {
      status = "all";
    }
    if (status == "all" && !request.isUserInRole("administrator")) {
      throw new Exception("You must be an administrator to view all mouse records");
    }

    if(creOnly < 0){
      creOnly = 0;
    }

    Holder holder = DBConnect.getHolder(holderID);
      Gene gene = DBConnect.getGene(geneID);
      Facility facility = DBConnect.getFacility(facilityID);
      ArrayList<MouseRecord> mice = DBConnect.getMouseRecords(mouseTypeID, orderBy, holderID, geneID, status, searchTerms, false, creOnly, facilityID,limit,offset);

    ArrayList<MouseType> mouseTypes = DBConnect.getMouseTypes();
    String mouseTypeStr = "Listing";
      String mouseCountStr = "";
      if(mouseTypeID != -1)
      {
        for(MouseType type : mouseTypes)
        {
          if(type.getMouseTypeID() == mouseTypeID)
          {
            mouseTypeStr += " " + type.getTypeName();
            break;
          }
        }
      }
      else
      {
        mouseTypeStr += " all";
      }
      mouseCountStr = Integer.toString(mice.size()) + " records total, as of " + new Date().toString();
      mouseTypeStr += " records";

      if (facility != null)
      {
        mouseTypeStr += " in facility " + facility.getFacilityName();
      }

      if (holder != null)
      {
      mouseTypeStr += " held by " + holder.getFullname();
      }
      else if(gene != null)
      {
      mouseTypeStr += " with gene " + gene.getSymbol() + " " + gene.getFullname();
    }

      if(creOnly > 0)
      {
        mouseTypeStr += " with expressed sequence type CRE";
      }

      if(searchTerms != null && !searchTerms.isEmpty())
      {
        mouseTypeStr += " matching search term '" + searchTerms + "'";
      }

      if(!status.equals("live") && !status.equals("all") && request.isUserInRole("administrator"))
      {
        mouseTypeStr += " with status='" + status + "'";
      }


    PdfPTable table = new PdfPTable(2);
    table.setWidthPercentage(100);
    table.setWidths(new int[]{1,5});
    for(MouseRecord mouse : mice){
      PdfPCell left = cell(getMouseNameAndNumber(mouse));
      table.addCell(left);
      PdfPTable subtable = new PdfPTable(2);
      subtable.setWidths(new int[]{5,4});
      subtable.addCell(cell(getMouseCategory(mouse)));
      int commentRowSpan = mouse.isIS() ? 1 : 2;
      subtable.addCell(cell(getFormattedMouseComment(mouse.getGeneralComment()),commentRowSpan,1));
      if (!mouse.isIS()){
        subtable.addCell(cell(getMouseDetails(mouse)));
      }
      subtable.addCell(cell(getHolderList(mouse),1,2));
      PdfPCell right = new PdfPCell(subtable);
      right.setBorder(Rectangle.NO_BORDER);
      table.addCell(right);
    }
    Phrase list = phr();
    documentName = mouseTypeStr;
    list.add(phr(mouseTypeStr,FONT_TITLE));
    list.add(NL);
    list.add(phr(mouseCountStr,FONT_BOLD));
    list.add(table);
    return list;
  }


  private static PdfPCell cell(Phrase phr){
    return cell(phr, 1,1);
  }
  private static PdfPCell cell(Phrase phr, int rowSpan, int colSpan){
    PdfPCell cell = new PdfPCell(phr);
    cell.setRowspan(rowSpan);
    cell.setColspan(colSpan);
    cell.setPaddingLeft(3);
    cell.setPaddingRight(1);
    cell.setPaddingBottom(4);
    cell.setPaddingTop(0);
    cell.setLeading(0f, 1.1f);
    return cell;
  }

  private static Phrase getMouseNameAndNumber(MouseRecord mouse){
    Phrase p = phr();
    p.add(phr(mouse.getMouseName(), FONT_MOUSENAME));
    p.add(NL);
    p.add(phr("Record #" + mouse.getMouseID()));
    return p;
  }

  private static Phrase getMouseCategory(MouseRecord mouse){
    Phrase p = phr();
    p.add(phrb(mouse.getMouseType()));
    if (mouse.getGeneID() != null)
    {
      p.add(NL);
      p.add(formatGenePdf(mouse.getGeneSymbol(),mouse.getGeneName(),mouse.getGeneID()));
    }
    if (mouse.isMA() && mouse.getModificationType() != null)
    {
      p.add(NL);
      p.add(phrb("Modification Type: "));
      p.add(phr(mouse.getModificationType()));
    }
    if (mouse.getExpressedSequence() != null) {
      p.add(NL);
      p.add(getExpressedSequence(mouse));
    }
    if (mouse.getRegulatoryElement() != null) {
      p.add(NL);
      p.add(phrb("Regulatory Element:"));
      p.add(phr(" " + mouse.getRegulatoryElement()));
    }
    if (mouse.isIS())
    {
      p.add(NL);
      p.add(phrb("Supplier and catalog #:"));
      String source = mouse.getSource();
      source = source.replaceAll("\\|\\|", ", ");
      p.add(phr(" " + source));
    }
    return p;
  }

  private static Phrase getMouseDetails(MouseRecord mouse){
    Phrase p = phr();

    if (mouse.isTG() || mouse.isMA()) {
      Phrase source;
      if (mouse.getSource() == null || mouse.getSource().equals("")) {
        source = phr("none");
      } else {
        source = formatSymbolPdf(mouse.getSource());
      }

      String repositoryCatalogNumber = mouse.getRepositoryCatalogNumber();

      if (repositoryCatalogNumber == null  || repositoryCatalogNumber.equals("") || repositoryCatalogNumber.equals("null")) {
        repositoryCatalogNumber = "none";
      }

      p.add(phr("Official Symbol: "));
      p.add(source);

      String officialName = mouse.getOfficialMouseName();
      if (officialName != null && !officialName.isEmpty()) {
        p.add(NL);
        p.add(phr(officialName));
      }
      p.add(NL);
      p.add(phr("MGI: " + repositoryCatalogNumber));
      if (mouse.getPubmedIDs() == null || mouse.getPubmedIDs().isEmpty()) {
        p.add(NL);
        p.add(phr("Unpublished"));
      } else {
        String allIDs = "";
        boolean first = true;
        boolean hasValidPmIds = false;
        for (String pmid : mouse.getPubmedIDs()) {
          if (!first)
            allIDs += ", ";
          if (pmid != null && !pmid.isEmpty())
            hasValidPmIds = true;
          first = false;
          allIDs += pmid;
        }
        if (hasValidPmIds){
          p.add(NL);
          p.add(phr("PubMed: " + allIDs));
        }
      }

      if (mouse.getBackgroundStrain() != null  && !mouse.getBackgroundStrain().isEmpty()) {
        p.add(NL);
        p.add(phr("Background Strain: " + mouse.getBackgroundStrain()));
      }
      if (mouse.getMtaRequired() != null) {
        if (mouse.getMtaRequired().equalsIgnoreCase("Y")) {
          // table.append("<dt>MTA is required.</dt>\r\n");
        } else if (mouse.getMtaRequired().equalsIgnoreCase("D")) {
          // table.append("<dt>Unknown if MTA required.</dt>\r\n");
        } else if (mouse.getMtaRequired().equalsIgnoreCase("N")) {
          // table.append("<dt>MTA is not required</dt>\r\n");
        }
      }

      if (mouse.getGensat() != null && !mouse.getGensat().isEmpty()) {
        p.add(NL);
        p.add(phr("Gensat founder line: " + mouse.getGensat()));
      }

    }
    if (mouse.isCryoOnly()) {
      p.add(NL);
      p.add(phrb("Cryopreserved only"));
    }
    if (mouse.getCryopreserved() != null && mouse.getCryopreserved().equalsIgnoreCase("Y")) {
      p.add(NL);
      p.add(phrb("Cryopreserved only"));
    }

//    if (mouse.isEndangered()) {
//      table.append("<dt><span class='endangered'>Endangered</span></dt>\r\n");
//    }


    return p;
  }


  private static Phrase getHolderList(MouseRecord mouse)
  {
    Phrase p = phr("Holders:  ");
    Boolean first = true;
    for (MouseHolder holder : mouse.getHolders()) {
      if (holder.isCovert()) {
        continue;
      }

      if (holder.getFirstname() == null && holder.getLastname() == null) {
        continue;
      }

      String cryoLiveStatus = "";
      if (holder.getCryoLiveStatus() != null) {
        // NULL = ignore
        // Live = live only
        // LiveCryo = live and cryo
        // Cryo = cryo only
        if (holder.getCryoLiveStatus().equalsIgnoreCase("Live only")) {
          cryoLiveStatus = "";
        } else if (holder.getCryoLiveStatus().equalsIgnoreCase("Live and cryo")) {
          cryoLiveStatus = "(Cryo,Live)";
        } else if (holder.getCryoLiveStatus().equalsIgnoreCase("Cryo only")) {
          cryoLiveStatus = "(Cryo)";
        }
      }

      String facilityName = holder.getFacilityID() == 1 ? " "  : "(" + holder.getFacilityName() + ")";

      String firstInitial = "";
      if (holder.getFirstname() != null) {
        firstInitial = holder.getFirstname().substring(0, 1)+ ". ";
      }
      if (!first){
        p.add(phr(", "));
      }
      else {
        first = false;
      }
      p.add(phr(firstInitial  + holder.getLastname() + " " + facilityName  + cryoLiveStatus));
    }



    return p;
  }

  private static Phrase getExpressedSequence(MouseRecord mouse)
  {
    Phrase p = phr();
    p.add(phrb("Expressed Sequence: "));
    if (mouse.getExpressedSequence() != null) {
      if (mouse.getExpressedSequence().equalsIgnoreCase("mouse gene")
          || mouse.getExpressedSequence().equalsIgnoreCase("Mouse Gene (unmodified)")) {
        p.add(formatGenePdf(mouse.getTargetGeneSymbol(),
                    mouse.getTargetGeneName(),
                    mouse.getTargetGeneID()));
      } else if (mouse.getExpressedSequence().equalsIgnoreCase("reporter")) {
        p.add(phr(mouse.getReporter()));
      } else if (mouse.getExpressedSequence().equalsIgnoreCase("other")||
             mouse.getExpressedSequence().equalsIgnoreCase("Modified mouse gene or Other")) {
        p.add(phr(mouse.getOtherComment()));
      } else {
        p.add(phr(mouse.getExpressedSequence()));
      }
    }
    return p;
  }

  private static Phrase getFormattedMouseComment(String comment) throws IOException {
    // hi there [URL]http://asdfasdfasdf[/URL] - > hi there http://asdfasdfasdf
    // some comments *an important comment in bold* and some more
    Phrase p = phr();
    if (comment == null) return p;
    String htmlComment = HTMLUtilities.getCommentForDisplay(comment);
    HashMap<String,Object> map = new HashMap<String,Object>();
    map.put("font_factory",new MouseFontFactory());
    java.util.List<Element> objects = HTMLWorker.parseToList(new StringReader(htmlComment), null, map);
    for (Element element : objects) p.add(element);
    return p;
  }

  private static Phrase phr(){
    return phr("");
  }

  private static Phrase phr(String text){
    return phr(text, FONT_NORMAL);
  }
  private static Phrase phrb(String text){
    return phr(text, FONT_BOLD);
  }
  private static Phrase phr(String text, Font font){
    return new Phrase(text, font);
  }

  private static Phrase formatGenePdf(String symbol, String name, String id){
    Phrase gene = phr("   ");
    gene.add(phrb(symbol));
    gene.add(phr(" - " + name));
    gene.add(NL);
    gene.add(phr("   MGI: " + id));
    return gene;
  }

  private static Phrase formatSymbolPdf(String symbol)
  {
    Phrase p = phrb(symbol);
    //TODO parse and set the rise for the chunk in brackets
    return p;
  }

  public static class MouseFontFactory implements FontProvider {
    public Font getFont(String fontname,
      String encoding, boolean embedded, float size, int style, BaseColor color) {
      return new Font(FONT_NORMAL.getFamily(), FONT_NORMAL.getSize(), style, color);
    }

    public boolean isRegistered(String fontname) {
      return false;
    }
  }
}
