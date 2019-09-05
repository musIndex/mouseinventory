package edu.ucsf.mousedatabase.servlets;

import static edu.ucsf.mousedatabase.HTMLGeneration.stringToInt;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontProvider;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
/*import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
*/

import com.opencsv.CSVReader;



import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.HTMLUtilities;
import edu.ucsf.mousedatabase.Log;
import edu.ucsf.mousedatabase.objects.Facility;
import edu.ucsf.mousedatabase.objects.Gene;
import edu.ucsf.mousedatabase.objects.Holder;
import edu.ucsf.mousedatabase.objects.MouseHolder;
import edu.ucsf.mousedatabase.objects.MouseRecord;
import edu.ucsf.mousedatabase.objects.MouseType;
import edu.ucsf.mousedatabase.servlets.CsvServletNew.MouseFontFactory;


/**
 * Servlet implementation class CsvServletNew
 */
public class CsvServletNew extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Font FONT_NORMAL,
  FONT_BOLD,
  FONT_MOUSENAME,
  FONT_TITLE;
private static Chunk NL;

private String documentName = DBConnect.loadSetting("general_site_name").value;
private String mutantAllele = "_Mutant Allele";
private String transgene = "_Transgene";
private String inbredStrain = "_Inbred Strain";

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

ArrayList<String> data = getMouseListCsv(request);
ServletOutputStream stream = response.getOutputStream();


response.setHeader("Expires", "0");
response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
response.setContentType("text/csv");


//This doesn't seem to take effect, oh well
response.setHeader("Content-Disposition", "attachment; filename=\"" + documentName + ".csv\"");
//Header for csv spreadsheet

for(String entry : data) {
stream.write(entry.getBytes()); //adding this to write. might need newline
}

Log.Info("Exported csv - " + documentName);
} catch (Exception e) {
response.setContentType("text/html");
response.getWriter().write("Error processing csv: " + e.getLocalizedMessage());
Log.Error("Error processing csv!",e);
}
}

private ArrayList<String> getMouseListCsv(HttpServletRequest request) throws Exception
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

ArrayList<String> stringList = new ArrayList<String>();
stringList.add("Mouse DB ID,Type,Common Name,Official Symbol,Official Name,Mouse MGI ID,"
    + "Gene/Regulatory Element,Inserted Sequence,PMID,Gene MGI ID"+"\n");
for(MouseRecord mouse : mice){  

String mouseName = getMouseName(mouse).getContent();
String mouseNumber = getMouseNumber(mouse).getContent();
String mouseStatus = getMouseStatus(mouse).getContent();
String mouseCategory = getMouseCategory(mouse).getContent();
//String mouseComment= getFormattedMouseComment(mouse.getGeneralComment()).getContent();
String mouseDetails = getMouseDetails(mouse).getContent();
String mouseGene = getMouseGene(mouse).getContent();
String mouseSeq = getExpressedSeq(mouse).getContent();
String mousePMID = getMousePMID(mouse).getContent();
String mouseGID = getMouseGeneID(mouse).getContent();
//String mouseHolders = getHolderList(mouse).getContent();

String csvLine = mouseNumber+mouseStatus+ "," +mouseCategory + "," +mouseName + "," +mouseDetails+ ","
+ mouseGene+","+mouseSeq+","+mousePMID+","+mouseGID+"\n";

stringList.add(csvLine);

//return stringList;
};
return stringList;

}

private static Phrase getMouseName(MouseRecord mouse){
Phrase p = phr();
String mName = "\" "+mouse.getMouseName()+ "\"";
//p.add(phr(mName, FONT_MOUSENAME));
p.add(mName);
if (mouse.isIS())
{
//p.add(NL);
//p.add(phrb("Supplier and catalog #:"));
String source =" " +mouse.getSource().replace(",",":");
p.add(source);

}
//p.add(NL);
return p;
}
private static Phrase getMouseNumber(MouseRecord mouse) {
  Phrase p = phr();
  p.add(phr(mouse.getMouseID()));
  return p;
  
}

private static Phrase getMouseCategory(MouseRecord mouse){
Phrase p = phr();
p.add(phrb(mouse.getMouseType()));

if (mouse.isMA() && mouse.getModificationType() != null)
{
p.add(phrb(": "));
p.add(phr(mouse.getModificationType()));
//p.add(",");
}
else
 p.add("");
//p.add(phr(mouse.getMouseName())+",");

return p;
}
private static Phrase getMouseGene(MouseRecord mouse) {
Phrase p = phr();

if (mouse.getGeneID() != null)
{
//p.add(NL);
  
p.add(formatGene(mouse.getGeneSymbol()));
}

if (mouse.getRegulatoryElement() != null) {
//p.add(",");
String tgPromoter = mouse.getRegulatoryElement().replace(",",";");
p.add(phrb("Regulatory Element:"));
p.add(tgPromoter);
}
return p;
}
private static Phrase getMouseGeneID(MouseRecord mouse) {
Phrase p = phr();
if (mouse.getGeneID() != null)
{
  p.add(mouse.getGeneID());
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

//p.add(phr("Official Symbol: "));
p.add(phr("\""+source+"\""));

String officialName = mouse.getOfficialMouseName();
if (officialName != null && !officialName.isEmpty()) {
  p.add(",");
  p.add(phr("\""+officialName+"\""));
}
p.add(",");

//p.add(phr(repositoryCatalogNumber));
p.add(repositoryCatalogNumber);
}
return p;
}
private static Phrase getMousePMID(MouseRecord mouse){
Phrase p = phr();
if (mouse.isTG() || mouse.isMA())
{
if (mouse.getPubmedIDs() == null || mouse.getPubmedIDs().isEmpty()) {
p.add(phr("Unpublished"));
} else {
String allIDs = "";
boolean first = true;
boolean hasValidPmIds = false;
for (String pmid : mouse.getPubmedIDs()) {
if (!first)
  allIDs += "; ";
if (pmid != null && !pmid.isEmpty())
  hasValidPmIds = true;
first = false;
allIDs += pmid;
}
if (hasValidPmIds){
//p.add(",");
String removeCommaID = allIDs.replaceAll(",",";");
p.add(removeCommaID);
}
}
}
return p;
}


/*
if (mouse.getBackgroundStrain() != null  && !mouse.getBackgroundStrain().isEmpty()) {
p.add(",");
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
p.add(",");
p.add(phr("Gensat founder line: " + mouse.getGensat()));
}

}
if (mouse.isCryoOnly()) {
p.add(",");
p.add(phrb("Cryopreserved only"));
}
if (mouse.getCryopreserved() != null && mouse.getCryopreserved().equalsIgnoreCase("Y")) {
p.add(",");
p.add(phrb("Cryopreserved only"));
}
//Keep new line
p.add(NL);
return p;
}
*/
/*
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
}
}
*/
private static Phrase getMouseStatus(MouseRecord mouse)
{
Phrase p = phr();
String cryoLiveStatus = "";
for (MouseHolder holder : mouse.getHolders()) {
if (holder.isCovert()) {
continue;
}
if (holder.getCryoLiveStatus() != null) {
// NULL = ignore
// Live = live only
// LiveCryo = live and cryo
// Cryo = cryo only
if (holder.getCryoLiveStatus().equalsIgnoreCase("Live only")) {
cryoLiveStatus = "";
} else if (holder.getCryoLiveStatus().equalsIgnoreCase("Live and cryo")) {
cryoLiveStatus = " (Cryo/Live)";
} else if (holder.getCryoLiveStatus().equalsIgnoreCase("Cryo only")) {
cryoLiveStatus = " (Cryo)";
}
}
}
p.add(cryoLiveStatus);
return p;
}

/*
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
*/

private static Phrase getExpressedSeq(MouseRecord mouse)
{
Phrase p = phr();

//p.add(phrb("Expressed Sequence: "));
if (mouse.getExpressedSequence() != null) {
if (mouse.getExpressedSequence().equalsIgnoreCase("mouse gene")
|| mouse.getExpressedSequence().equalsIgnoreCase("Mouse Gene (unmodified)")) {
p.add(formatGeneID(mouse.getTargetGeneSymbol(),
          mouse.getTargetGeneName(),
          mouse.getTargetGeneID()));
} else if (mouse.getExpressedSequence().equalsIgnoreCase("reporter")) {
 String reporter ="reporter: " +mouse.getReporter().replace(",", ";");
 //p.add(phr("\""+mouse.getReporter()+"\""));
 p.add(reporter);

} else if (mouse.getExpressedSequence().equalsIgnoreCase("other")||
   mouse.getExpressedSequence().equalsIgnoreCase("Modified mouse gene or Other")) {
p.add(phr(mouse.getOtherComment().replaceAll(",",";")));
} else {
p.add(phr("\""+mouse.getExpressedSequence()+"\""));
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
//List only symbol method
private static Phrase formatGene(String symbol) {
  Phrase gene = phr("");
  gene.add("\""+symbol+"\"");
  //gene.add(",");
  return gene;
}
private static Phrase formatGeneID(String symbol, String name, String id){
Phrase gene = phr("");
gene.add("Gene MGI: " + id);
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
       
    /**
     * @see HttpServlet#HttpServlet()
     */
   

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
