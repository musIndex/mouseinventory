package edu.ucsf.mousedatabase;

import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.*;

import edu.ucsf.mousedatabase.objects.*;

public class HTMLGeneration {

  // note these are not used everywhere they should be
  public static final String siteRoot = "/mouseinventory/";
  public static final String adminRoot = "/mouseinventory/admin/";
  public static final String imageRoot = "/mouseinventory/img/";
  public static final String scriptRoot = "/mouseinventory/js/";
  public static final String dataRoot = "/mouseinventory/rawdata/";

  public static void setGoogleAnalyticsId(String id, String domainSuffix)
  {
    googleAnalyticsScript =
           "<script type=\"text/javascript\">\r\n"
        + "  var _gaq = _gaq || [];\r\n"
        + "  _gaq.push(['_setAccount', '" + id  + "']);\r\n"
        + "  _gaq.push(['_setDomainName', '"+ domainSuffix + "']);\r\n"
        + "  _gaq.push(['_trackPageview']);\r\n"
        + "  (function() {\r\n"
        + "    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\r\n"
        + "    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\r\n"
        + "    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\r\n"
        + "  })();" + "</script>\r\n";
  }

  public static String googleAnalyticsScript;

  private static final Pattern jaxPattern = Pattern.compile("[^\\d]*([\\d]+)");

  public static String SiteName = "";
  public static String AdminEmail = "";

  /**********************************************/
  /* Page utility methods */
  /*********************************************/

  public static String getPageHeader(String additionalJavaScript,
      boolean disableCache, boolean isAdminPage) {
    return getPageHeader(additionalJavaScript, disableCache, isAdminPage,
        null);
  }

  public static String getPageHeader(String additionalJavaScript,
      boolean disableCache, boolean isAdminPage, String bodyParams) {

    StringBuffer buf = new StringBuffer();
    // TODO html5
    buf.append("<!DOCTYPE html>\r\n");

    buf.append("<html>\r\n");
    buf.append("<head>\r\n");
    // buf.append("<meta content=\"text/html; charset=utf-8\" http-equiv=\"content-type\"/>\r\n");
    buf.append("<meta name=\"robots\" content=\"noindex, nofollow\">");
    // TODO use some sort of caching?
    if (disableCache) {
      buf.append("<meta http-equiv=\"cache-control\" content=\"no-cache\"> <!-- tells browser not to cache -->\r\n");
      buf.append("<meta http-equiv=\"expires\" content=\"0\"> <!-- says that the cache expires 'now' -->\r\n");
      buf.append("<meta http-equiv=\"pragma\" content=\"no-cache\"> <!-- says not to use cached stuff, if there is any -->\r\n");
    }

    buf.append("<link href=\""
        + siteRoot
        + "css/bootstrap.css\" rel=\"stylesheet\" type=\"text/css\">\r\n");
    buf.append("<link href=\""
        + siteRoot
        + "css/chosen.css\" rel=\"stylesheet\" type=\"text/css\">\r\n");
    buf.append("<link href=\""
        + siteRoot
        + "css/MouseInventory.css\" rel=\"stylesheet\" type=\"text/css\">\r\n");

    buf.append("<title>" + SiteName + "</title>\r\n");

    buf.append("<script language=\"javascript\" type=\"text/javascript\" src=\""
        + scriptRoot + "uiHelperFunctions.js\"></script>\r\n");
    buf.append("<script language=\"javascript\" type=\"text/javascript\" src=\""
        + scriptRoot + "ajaxFunctions.js\"></script>\r\n");
    buf.append("<script language=\"javascript\" type=\"text/javascript\" src=\""
        + scriptRoot + "validationFunctions.js\"></script>\r\n");
    buf.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js\"></script>\r\n");

    buf.append("<script src='https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/jquery-ui.min.js'></script>\r\n");
    buf.append("<link href='https://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css 'rel='stylesheet' type='text/css' />");
    buf.append("<script src=\"" + scriptRoot + "chosen.jquery.min.js\"></script>\r\n");
    buf.append("<script src=\"" + scriptRoot + "jquery.ba-bbq.min.js\"></script>\r\n");
    buf.append("<script type=\"text/javascript\">\r\n" +
        "$(document).ready(function(){\r\n" +
          "$('.chzn-select').chosen();" +
        "\r\n})\r\n" +
        "</script>\r\n");

    buf.append(googleAnalyticsScript + "\r\n");
    if (additionalJavaScript != null) {
      buf.append(additionalJavaScript);
    }

    buf.append("</head>\r\n");
    buf.append("<body " + (bodyParams == null ? "" : bodyParams) + " >\r\n");

    return buf.toString();
  }

  public static String getPageFooter() {
    StringBuffer buf = new StringBuffer();
    buf.append("</body>");
    buf.append("</html>");
    return buf.toString();
  }

  public static String getNavBar(String currentPageFilename,
      boolean isAdminPage) {
    return getNavBar(currentPageFilename, isAdminPage, true);
  }

  public static String getNavBar(String currentPageFilename,
      boolean isAdminPage, boolean showAdminControls) {
    StringBuffer table = new StringBuffer();
    table.append("<div id=\"navBarContainer\">");

    // Page header
    table.append("<div id=\"pageHeaderContainer\" class='clearfix'>");
    table.append("<div id=\"pageTitleContainer\">");
    table.append("<div>");
    table.append("<span id=\"pageTitle\">" +
        "<a href='" + siteRoot + "'>" + SiteName + "</a></span>");
    table.append("</div>");
    // About, faq, contact links
    table.append("<div>");
    table.append("<span class=\"titleSubText\">");
    table.append("&nbsp;<a href=\"" + siteRoot
        + "about.jsp\">About</a>&nbsp;");
    // table.append("&nbsp;<a href=\""+siteRoot+"faq.jsp\">FAQ</a>&nbsp;");
    table.append("&nbsp;<a href=\"" + siteRoot
        + "contact.jsp\">Submit Feedback</a>&nbsp;");
    table.append("</span>");
    table.append("</div>");
    table.append("</div>");
    // Quick Search bar
    if (currentPageFilename == null || !currentPageFilename.equals("search.jsp"))
    {
      table.append("<div id=\"quickSearchContainer\">");
      table.append("<form id=\"quickSearchForm\"action=\"" + siteRoot + "search.jsp\" method=\"get\">\r\n");
      table.append("<input type=\"text\" class=\"input-medium search-query\"  name=\"searchterms\" >");
      table.append("<input type='hidden' name='search-source' value='quicksearch:" + currentPageFilename + "'>");
      table.append("<input class=\"btn search-query\" type=\"submit\" value=\"Quick Search\">");
      table.append("<script type='text/javascript'>$('input[name=searchterms]').focus()</script>");
      table.append("</form>");
      table.append("</div>");

    }
    table.append("</div>");
    // Navigation Bar
    table.append("<div id=\"navigationLinksContainer\">");
    table.append("<ul class=\"navLinkUL\">");
    table.append(addNavLink("Search", "search.jsp", null,
        currentPageFilename, false));
    table.append(addNavLink("Mouse List", "MouseReport.jsp", null,
        currentPageFilename, false));
    table.append(addNavLink("Gene List", "GeneReport.jsp", null,
        currentPageFilename, false));
    table.append(addNavLink("Holder List", "HolderReport.jsp", null,
        currentPageFilename, false));
    table.append(addNavLink("Facility List", "FacilityReport.jsp", null,
        currentPageFilename, false));
    // table.append(addNavLink("Endangered Mice", "EndangeredReport.jsp",
    // null,currentPageFilename,false));
    table.append(addNavLink("Submit Mice", "submitforminit.jsp", null,
        currentPageFilename, false));
    table.append(addNavLink("Administration", "admin.jsp", null,
        isAdminPage ? "admin.jsp" : currentPageFilename, true));
    table.append("</ul>");
    table.append("</div>");

    // Admin Row
    if (isAdminPage && showAdminControls) {
      table.append("<div id=\"adminLinksContainer\">");
      table.append("<ul class=\"navLinkUL\">");
      table.append(addNavLink("Admin Home", "admin.jsp", null,
          currentPageFilename, true));
      table.append(addNavLink("Submissions", "ListSubmissions.jsp", null,
          currentPageFilename, true));
      table.append(addNavLink("Change Requests",
          "ManageChangeRequests.jsp", null, currentPageFilename, true));
      table.append(addNavLink("MGI Submission", "SubmissionFromMgi.jsp",
          null, currentPageFilename, true));
      table.append(addNavLink("Edit Mice", "EditMouseSelection.jsp",
          null, currentPageFilename, true));
      table.append(addNavLink("Edit Holders", "EditHolderChooser.jsp",
          null, currentPageFilename, true));
      table.append(addNavLink("Edit Facilities",
          "EditFacilityChooser.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Edit Genes", "EditGeneChooser.jsp", null,
          currentPageFilename, true));
      table.append(addNavLink("Reports", "Reports.jsp", null,
          currentPageFilename, true));

      table.append(addNavLink("Data Import", "ImportReports.jsp", null,
          currentPageFilename, true));
      table.append(addNavLink("Log out", "logout.jsp", null,
          currentPageFilename, false));
      table.append("</ul>");
      table.append("</div>");
    }

    table.append("</div>");
    return table.toString();
  }

  private static String addNavLink(String targetNiceName,
      String targetPageFilename, String targetPageArguments,
      String currentPageFilename, boolean isAdminPage) {

    String cssClass = targetPageFilename.equals(currentPageFilename) ? "current"
        : "";
    cssClass += "NavLinkItem";

    String url = (isAdminPage ? adminRoot : siteRoot) + targetPageFilename;
    if (targetPageArguments != null) {
      url += targetPageArguments;
    }

    return "<li class=\"" + cssClass
        + "\"><a class=\"navBarAnchor\" href=\"" + url + "\">"
        + targetNiceName + "</a></li>\r\n";

  }

  public static String getNewMouseForm(MouseRecord r) {
    return getEditMouseForm(r, null, null, true);
  }

  public static String getEditMouseForm(MouseRecord r) {
    return getEditMouseForm(r, null, null);
  }

  public static String getEditMouseForm(MouseRecord r, SubmittedMouse sub) {
    return getEditMouseForm(r, sub, null);
  }

  public static String getEditMouseForm(MouseRecord r, ChangeRequest req) {
    return getEditMouseForm(r, null, req);
  }

  public static String getEditMouseForm(MouseRecord r, SubmittedMouse sub,
      ChangeRequest req) {
    return getEditMouseForm(r, sub, req, false);
  }

  public static String getEditMouseForm(MouseRecord r, SubmittedMouse sub,
      ChangeRequest req, boolean isAdminCreating) {

    int size = 35; // default field size
    String field = "";
    StringBuilder buf = new StringBuilder();
    buf.append("<div class=\"mouseTable\">\r\n");

    if (sub != null) // sub is null when editing existing mice, not null
    // when this is a new submission
    {
      buf.append("<form name=\"mouseDetails\" action=\"UpdateSubmission.jsp\" method=\"post\">\r\n");
      buf.append("<input type=\"hidden\" name=\"submittedMouseID\" value=\"" + sub.getSubmissionID() + "\">");
      if (req != null)
        return null;
    } else if (req != null) {
      buf.append("<form name=\"mouseDetails\" action=\"UpdateChangeRequest.jsp\" method=\"post\">\r\n");
      buf.append("<input type=\"hidden\" name=\"changeRequestID\" value=\"" + req.getRequestID() + "\">");
    } else if (isAdminCreating) {
      buf.append("<form name=\"mouseDetails\" action=\"CreateRecord.jsp\" method=\"post\">\r\n");
    } else {
      buf.append("<form name=\"mouseDetails\" action=\"UpdateMouse.jsp\" method=\"post\">\r\n");
    }

    buf.append("<input type=\"hidden\" name=\"mouseID\" value=\""  + r.getMouseID() + "\">");
    buf.append("<input type=\"hidden\" name=\"mouseType\" value=\""  + r.getMouseType() + "\">");

    buf.append("<input type=\"hidden\" name=\"repositoryTypeID\" value=\"5\">");
    // everything is MGI now

    buf.append("<div class=\"editMouseFormContainer\">\r\n");
    // buf.append("<tr>\r\n");
    // buf.append("<td valign=\"top\" style=\"padding: 0px\">\r\n");
    buf.append("<div class=\"editMouseFormLeftColumn\">");
    buf.append("<table class=\"editMouseColumn\">\r\n");

    // holders
    ArrayList<MouseHolder> holderList = r.getHolders();
    if (holderList == null)
      holderList = new ArrayList<MouseHolder>();

    if (req != null)
    {
      int addHolderID = -1;
      int addFacilityId = -1;
      //if there is a holder and facility in the change requests, add it to the list
      for(String propName : req.Properties().stringPropertyNames())
      {
        String propertyValue = req.Properties().getProperty(propName);
        if (propName.equals("Add Holder"))
        {
          int splitterIndex = propertyValue.indexOf('|');
          if (splitterIndex > 0)
          {
            try
            {
              addHolderID = Integer.parseInt(propertyValue.substring(0,splitterIndex));
            }
            catch (Exception e)
            {
              Log.Error("Unable to parse int from holder property value in change request #" + req.getRequestID() + ": " + propertyValue,e);
            }
          }
        }
        else if (propName.equals("Add Facility"))
        {
          int splitterIndex = propertyValue.indexOf('|');
          if (splitterIndex > 0)
          {
            try
            {
              addFacilityId = Integer.parseInt(propertyValue.substring(0,splitterIndex));
            }
            catch (Exception e)
            {
              Log.Error("Unable to parse int from facility property value in change request #" + req.getRequestID() + ": " + propertyValue,e);
            }
          }
        }
        else if (propName.equals("Add Holder Name"))
        {
          Holder holder = DBConnect.findHolder(propertyValue);
          if (holder != null)
          {
            addHolderID = holder.getHolderID();
          }
        }
        else if (propName.equals("Add Facility Name"))
        {
          Facility facility = DBConnect.findFacility(propertyValue);
          if (facility != null)
          {
            addFacilityId = facility.getFacilityID();
          }
        }
      }
      if (addHolderID > 0)
      {
        MouseHolder addedHolder = new MouseHolder();
        addedHolder.setNewlyAdded(true);
        addedHolder.setHolderID(addHolderID);
        if (addFacilityId > 0)
        {
          addedHolder.setFacilityID(addFacilityId);
        }
        holderList.add(addedHolder);
      }

    }

//    TODO finish this - auto populate multiple holders on the submission - need a way to deal with holders that don't yet exisit
//    if (sub != null)
//    {
//      int i = 2;
//      Properties props = sub.getProperties();
//      String key = "Recipient PI Name-" + i;
//      while (props.containsKey(key))
//      {
//        String holderName = props.getProperty(key);
//        String holderFacility = props.getProperty("Recipient Facility-" + i);
//      }
//    }


    holderList.add(new MouseHolder());

    ArrayList<Holder> allHoldersObjs = DBConnect.getAllHolders();
    ArrayList<Facility> allFacilitiesObjs = DBConnect.getAllFacilities();

    String[] holderIDs = new String[allHoldersObjs.size()];
    String[] holderNames = new String[allHoldersObjs.size()];
    String[] facilityIDs = new String[allFacilitiesObjs.size()];
    String[] facilityNames = new String[allFacilitiesObjs.size()];

    int k = 0;
    for (Holder holder : allHoldersObjs) {
      holderIDs[k] = String.valueOf(holder.getHolderID());
      if (holder.getHolderID() == 1) {
        holderNames[k] = "NA";
      } else {
        holderNames[k] = holder.getLastname() + ", "
            + holder.getFirstname() + " - " + holder.getDept();
      }
      k++;
    }

    k = 0;
    for (Facility facility : allFacilitiesObjs) {
      facilityIDs[k] = String.valueOf(facility.getFacilityID());
      facilityNames[k] = facility.getFacilityName();
      k++;
    }

    String[] covertOptions = { "Covert" };

    k = 0;
    for (MouseHolder holder : holderList) {
      if (holder.isNewlyAdded())
      {
        buf.append("<tr class=\"editMouseRow\">");
        buf.append("<td colspan=\"2\"><b>Auto-filled from change request:</b></td></tr>");
      }
      buf.append("<tr class=\"editMouseRow\">");
      buf.append("<td colspan=\"2\"><div style=\"position: relative\">Holder:&nbsp;");
      buf.append(HTMLGeneration.genSelect("holder_id-" + k, holderIDs,
          holderNames, String.valueOf(holder.getHolderID()), null));
      buf.append("<br>Facility:");
      buf.append("&nbsp;");
      buf.append(HTMLGeneration.genSelect("facility_id-" + k,
          facilityIDs, facilityNames,
          String.valueOf(holder.getFacilityID()), null));
      buf.append("&nbsp;");
      buf.append("&nbsp;");
      buf.append(HTMLGeneration.genFlatRadio("cryoLiveStatus-" + k,
          new String[] { "Live only", "Live and Cryo", "Cryo only" },
          new String[] { "Live only", "Live and Cryo", "Cryo only" },
          holder.getCryoLiveStatus(), null));
      buf.append("&nbsp;");
      buf.append("&nbsp;");
      buf.append("&nbsp;");
      buf.append(HTMLGeneration.genCheckbox("covertHolder_-" + k,
          covertOptions, (holder.isCovert() ? "Covert" : "")));
      buf.append("<div style=\"position: absolute; top: 0pt; left: 310pt;\"><a href=\"javascript:\" onclick=\"clearHolder('"
          + k
          + "')\" ><img width=\"15\" src=\"../img/delete-icon2.png\" alt=\"\"></a></div></div>");
      buf.append("</td>");
      buf.append("</tr>\n");
      if (holder.isNewlyAdded())
      {
        buf.append("<tr><td>&nbsp;</td></tr>");
      }
      k++;
    }

    getTextInputRow(buf, "Mouse Name", "mouseName", r.getMouseName(), size,
        255, null, null, "editMouseRow");

    // boolean MGIConnectionAvailable = true;

    // Mutant Allele and Transgenic mice
    if (r.getMouseType().equalsIgnoreCase("Mutant Allele")
        || r.getMouseType().equalsIgnoreCase("Transgenic")) {
      if (r.getMouseType().equalsIgnoreCase("Mutant Allele")) {
        // Gene Section
        String mgiID = r.getGeneID();
        if ((mgiID == null || mgiID.isEmpty())
            && sub != null
            && (sub.getMAMgiGeneID() != null && !sub
                .getMAMgiGeneID().isEmpty())) {
          mgiID = sub.getMAMgiGeneID();
        }

        field = getTextInput(
            "geneMGIID",
            emptyIfNull(mgiID),
            size,
            11,
            "id=\"geneMGIID\" onkeyup=\"validateInput('geneMGIID', 'geneMGIIDValidation', 'mgiModifiedGeneId', '')\"");
        if (mgiID != null && !mgiID.isEmpty()) {

          String geneURL = HTMLGeneration.formatMGI(mgiID);
          String resultString = "";
          String validationStyle = "";
          String manualNameSymbolEntry = "<br>MGI SQL connection unavailable.  To continue editing this record, the gene Symbol and Name must be manually entered. <br>Symbol:&nbsp;"
              + getTextInput("geneManualSymbol", "", 15, 25, null)
              + "&nbsp;&nbsp;Name:&nbsp;"
              + getTextInput("geneManualName", "", 15, 25, null);

          Gene knownGene = DBConnect.findGene(mgiID);
          if (knownGene != null) {
            resultString = knownGene.getSymbol() + " - "
                + knownGene.getFullname();
            validationStyle = "bp_valid";
            replaceBrackets(resultString);
          } else {
            MGIResult geneResult = MGIConnect.doMGIQuery(mgiID,
                MGIConnect.MGI_MARKER,
                "This MGI ID does not correspond to a Gene",
                false);

            validationStyle = geneResult.isValid() ? "bp_valid"
                : "bp_invalid";
            if (geneResult.isValid()) {
              resultString = geneResult.getSymbol() + " - "
                  + geneResult.getName();
            } else if (geneResult.isMgiConnectionTimedout()
                || geneResult.isMgiOffline()) {
              resultString = manualNameSymbolEntry;
            } else {
              resultString = geneResult.getErrorString();
            }
          }

          field += "<span class='" + validationStyle
              + "' id='geneMGIIDValidation'>" + resultString
              + " (MGI:" + geneURL + ")</span>";

        } else {
          field += "<span id='geneMGIIDValidation'></span>";
        }
        getInputRow(buf, "Gene MGI ID", field, null, "editMouseRow");
      }
      // Modification type section
      String[] values = { "targeted disruption",
          "conditional allele (loxP/frt)", "targeted knock-in",
          "gene trap insertion", "Chemically induced (ENU)",
          "spontaneous mutation", "other (info in comment)" };
      getInputRow(
          buf,
          "Modification Type",
          genRadio("modificationType", values,
              r.getModificationType(), "onChange=\"UpdateModificationTypeEdit()\""),
           "style=\""  + rowVisibility(r.getMouseType().equalsIgnoreCase(
                  "mutant allele")) + "\"", "editMouseRow");

      // Expressed Sequence section
      String[] exprSeqValues = { "Reporter", "Cre",
          "Mouse Gene (unmodified)", "Modified mouse gene or Other" };
      getInputRow(
          buf,
          "Expressed Sequence",
          genRadio("expressedSequence", exprSeqValues,
              r.getExpressedSequence(), "onChange=\"UpdateExpressedSequenceEdit()\""),
          "id=\"trExprSeqRow\" style=\""
              + rowVisibility(r.getMouseType().equalsIgnoreCase(
                  "Transgenic")
                  || (r.getModificationType() != null && r
                      .getModificationType()
                      .equalsIgnoreCase("targeted knock-in"))) + "\"",
          "editMouseRow");

      String mgiID = r.getTargetGeneID();
      if ((mgiID == null || mgiID.isEmpty())
          && sub != null
          && (sub.getTGMouseGene() != null && !sub.getTGMouseGene()
              .isEmpty())) {
        mgiID = sub.getTGMouseGene();
      }
      // Mouse Gene section
      field = getTextInput(
          "targetGeneMGIID",
          emptyIfNull(mgiID),
          size,
          11,
          "id=\"targetGeneMGIID\" onkeyup=\"validateInput('targetGeneMGIID', 'targetGeneMGIIDValidation', 'mgiModifiedGeneId', '')\"");
      if (mgiID != null && !mgiID.isEmpty()) {
        String geneURL = HTMLGeneration.formatMGI(mgiID);
        String resultString = "";
        String validationStyle = "";
        String manualNameSymbolEntry = "<br>MGI SQL connection unavailable.  To continue editing this record, the gene Symbol and Name must be manually entered. <br>Symbol:&nbsp;"
            + getTextInput("targetGeneManualSymbol", "", size, 25,
                null)
            + "&nbsp;&nbsp;Name:&nbsp;"
            + getTextInput("targetGeneManualName", "", size, 25,
                null);

        Gene knownGene = DBConnect.findGene(mgiID);
        if (knownGene != null) {
          resultString = knownGene.getSymbol() + " - "
              + knownGene.getFullname();
          validationStyle = "bp_valid";
          replaceBrackets(resultString);
        } else {
          MGIResult geneResult = MGIConnect.doMGIQuery(mgiID,
              MGIConnect.MGI_MARKER,
              "This MGI ID does not correspond to a Gene", false);

          validationStyle = geneResult.isValid() ? "bp_valid"
              : "bp_invalid";
          if (geneResult.isValid()) {
            resultString = geneResult.getSymbol() + " - "
                + geneResult.getName();
          } else if (geneResult.isMgiConnectionTimedout()
              || geneResult.isMgiOffline()) {
            resultString = manualNameSymbolEntry;
          } else {
            resultString = geneResult.getErrorString();
          }
        }
        field += "<span class='" + validationStyle
            + "' id='targetGeneMGIIDValidation'>"
            + replaceBrackets(resultString) + " (MGI:" + geneURL
            + ")</span>";
      } else {
        field += "<span id='targetGeneMGIIDValidation'></span>";
      }
      getInputRow(
          buf,
          "Expr. Gene MGI ID:",
          field,
          "id=\"trGeneRow\" style=\""
              + rowVisibility(r.getExpressedSequence() != null
                  && (r.getExpressedSequence()
                      .equalsIgnoreCase("mouse gene") || r
                      .getExpressedSequence()
                      .equalsIgnoreCase(
                          "Mouse Gene (unmodified)")))
              + "\"", "editMouseRow");

      // getTextInputRow(buf, "Reporter", "reporter",
      // emptyIfNull(r.getReporter()), size, 255, null,
      // "id=\"trRepRow\" "
      // , "editMouseRow");
      field = "<textarea id=\"reporterTextArea\" name=\"reporter\" rows=\"4\" cols=\"40\" onkeypress=\"return imposeMaxLength(this,255);\" >"
          + emptyIfNull(r.getReporter()) + "</textarea>\r\n";
      getInputRow(buf, "Reporter", field,
          "style=\""
              + rowVisibility(r.getExpressedSequence() != null
                  && r.getExpressedSequence()
                      .equalsIgnoreCase("reporter"))
              + "\"", "editMouseRow", "trRepRow");

      // getTextInputRow(buf, "Other", "otherComment",
      // emptyIfNull(r.getOtherComment()), size, 255, null,
      // "id=\"trDescRow\" " +
      // "style=\"" + rowVisibility(r.getExpressedSequence() != null &&
      // r.getExpressedSequence().equalsIgnoreCase("other")) + "\"",
      // "editMouseRow");

      field = "<textarea id=\"otherCommentTextArea\" name=\"otherComment\" rows=\"4\" cols=\"40\" onkeypress=\"return imposeMaxLength(this,255);\" >"
          + emptyIfNull(r.getOtherComment()) + "</textarea>\r\n";
      getInputRow(
          buf,
          "Modified mouse gene or Other",
          field,
          "style=\""
              + rowVisibility(r.getExpressedSequence() != null
                  && (r.getExpressedSequence()
                      .equalsIgnoreCase("other") || r
                      .getExpressedSequence()
                      .equalsIgnoreCase(
                          "Modified mouse gene or Other")))
              + "\"", "editMouseRow", "trDescRow");

      // Regulatory Element
      if (r.getMouseType().equalsIgnoreCase("Transgenic")) {
        field = "<textarea id=\"regulatoryElement\" name=\"regulatoryElement\" rows=\"2\" cols=\"40\" >"
          + emptyIfNull(r.getRegulatoryElement()) + "</textarea>\r\n";
        getInputRow(buf, "Regulatory Element", field,null, "editMouseRow");

      }
    }
    buf.append("</table>\r\n");
    buf.append("</div>\r\n");
    buf.append("<div class=\"editMouseFormRightColumn\">");
    buf.append("<table class=\"editMouseColumn\">\r\n");
    if (r.getMouseType().equalsIgnoreCase("Mutant Allele")
        || r.getMouseType().equalsIgnoreCase("Transgenic")) {
      // Allele or Transgene MGI ID
      String mgiType = r.getMouseType().equalsIgnoreCase("mutant allele") ? "Allele"
          : "Transgene";

      String mgiID = r.getRepositoryCatalogNumber();
      if ((mgiID == null || mgiID.isEmpty())
          && sub != null
          && (sub.getMouseMGIID() != null && !sub.getMouseMGIID()
              .isEmpty())) {
        mgiID = sub.getMouseMGIID();
      }

      String officialSymbol = r.getSource();
      String officialMouseName = r.getOfficialMouseName();

      field = getTextInput(
          "repositoryCatalogNumber",
          emptyIfNull(mgiID),
          size,
          11,
          "id=\"repositoryCatalogNumber\" onkeyup=\"validateInput('repositoryCatalogNumber', 'repositoryCatalogNumberValidation', 'mgi"
              + mgiType + "Id', 'none')\"");

      if (mgiID != null && !mgiID.isEmpty()
          && !mgiID.equalsIgnoreCase("none")) {
        MGIResult mouseResult = null;
        String validationStyle = "";
        String resultString = "";
        String geneURL = HTMLGeneration.formatMGI(mgiID);

        if (r.getSource() == null || r.getSource().isEmpty()
            || officialMouseName == null
            || officialMouseName.isEmpty()) {
          if (r.getMouseType().equalsIgnoreCase("mutant allele")) {
            mouseResult = MGIConnect
                .doMGIQuery(
                    mgiID,
                    MGIConnect.MGI_ALLELE,
                    "This MGI ID does not correspond to an Allele Detail Page",
                    false);
          } else {
            mouseResult = MGIConnect
                .doMGIQuery(
                    mgiID,
                    MGIConnect.MGI_ALLELE,
                    "This MGI ID does not correspond to an Allele Detail Page",
                    false);
          }
          if (mouseResult.isMgiConnectionTimedout()
              || mouseResult.isMgiOffline()) {
            // buf = new StringBuilder();
            // buf
            // .append("<br><span class='error'>Unable to edit record because the MGI Connection is unavailable.  Please try again later</span>");
            // return buf.toString();
          }

          validationStyle = mouseResult.isValid() ? "bp_valid"
              : "bp_invalid";

          if (mouseResult.isValid()) {
            resultString = mouseResult.getSymbol();
            officialSymbol = mouseResult.getSymbol();
            officialMouseName = mouseResult.getName();
            r.setOfficialMouseName(officialMouseName);
            r.setSource(officialSymbol); // bad programmer!
          } else {
            resultString = mouseResult.getErrorString();
          }
        }
        field += "<span class='" + validationStyle
            + "' id='repositoryCatalogNumberValidation'>"
            + replaceBrackets(resultString) + " (MGI:" + geneURL
            + ")</span>";

      } else {
        field += "<span id='repositoryCatalogNumberValidation'></span>";
      }
      getInputRow(buf, mgiType + " MGI ID", field, null, "editMouseRow");

      getTextInputRow(buf, "Official Symbol", "source",
          emptyIfNull(officialSymbol), size, 255,
          "id=\"officialSymbol\"", null, "editMouseRow");

      getTextInputRow(buf, "Official Name", "officialMouseName",
          officialMouseName, size, 255, null, null, "editMouseRow");

      // PubMed ID(s)
      int pubMedNum = 1;
      for (String pmID : r.getPubmedIDs()) {
        if (pmID == null || pmID.isEmpty())
          continue;
        MGIResult pubmedResult = MGIConnect.DoReferenceQuery(pmID);
        if (pubmedResult.isMgiConnectionTimedout()
            || pubmedResult.isMgiOffline()) {
          // buf = new StringBuilder();
          // buf
          // .append("<br><span class='error'>Unable to edit record because the MGI Connection is unavailable.  Please try again later</span>");
          // return buf.toString();
        }
        String resultString = "";
        if (pubmedResult.isValid()) {
          resultString = pubmedResult.getTitle() + " ("
              + pubmedResult.getAuthors() + ")";
        } else {
          resultString = pubmedResult.getErrorString();
        }

        field = getTextInput("pmid" + pubMedNum, pmID, 20, 32, "id=\""
            + "pmid" + pubMedNum
            + "\" onkeyup=\"validateInput('pmid" + pubMedNum
            + "', 'pmID" + pubMedNum
            + "Validation', 'pmId', ',')\"");

        field += "<span class=\""
            + (pubmedResult.isValid() ? "bp_valid" : "bp_invalid")
            + "\" id=\"pmID" + pubMedNum + "Validation\">"
            + resultString + " (PubMed:" + formatPubMedID(pmID)
            + ")</span>";

        getInputRow(buf, "PubMed ID #" + pubMedNum, field, null,
            "editMouseRow");
        pubMedNum++;
      }
      field = getTextInput("pmid" + pubMedNum, null, 20, 32, "id=\""
          + "pmid" + pubMedNum + "\" onkeyup=\"validateInput('pmid"
          + pubMedNum + "', 'pmid" + pubMedNum
          + "Validation', 'pmId', ',')\" id=\"pmid" + pubMedNum
          + "\"");

      field += "<span id=\"pmid" + pubMedNum + "Validation\"></span>";

      getInputRow(buf, "PubMed ID #" + pubMedNum, field, null,
          "editMouseRow");

      // String[] mtaValues = { "Y", "N", "D" };
      // String[] mtaNiceNames = { "Yes", "No", "Don't Know" };
      // field = genSelect("mtaRequired", mtaValues, mtaNiceNames,
      // r.getMtaRequired(), null);
      // field = genCheckbox("mtaRequired", mtaValues,
      // r.getMtaRequired());
      // getInputRow(buf, "MTA Required?", field, null, "editMouseRow");

      field = "<input type=\"checkbox\" value=\"true\" name=\"endangered\" "
          + (r.isEndangered() ? "checked=\"checked\"" : "") + " >";
      getInputRow(buf, "Endangered?", field, null, "editMouseRow");

    }
    if (r.getMouseType().equalsIgnoreCase("inbred strain")) {
      buf.append("<tr class=\"editMouseRow\">\r\n");
      buf.append("<td valign=\"top\">\r\n");
      buf.append("Supplier and catalog link:</td>\r\n");
      buf.append("<td valign=\"top\">\r\n");
      buf.append("<input name=\"source\" type=\"text\" size=\"80\" value=\""
          + emptyIfNull(r.getSource()) + "\">\r\n");
      buf.append("<br>Jax format: JAX Mice, catalog number");
      buf.append("<br>Non-Jax format: supplier || url");
      buf.append("</td>\r\n");

      buf.append("</tr>\r\n");
    }

    if (sub != null && sub.getRawMGIComment() != null && !sub.getRawMGIComment().equals("")
        && sub.getComment() != null &&  !(sub.getComment().equals(sub.getRawMGIComment()))) {
      buf.append("<tr class=editMouseRow><td colspan=2>");
      buf.append("<span class=red>The submitter modified the description from MGI</span> (Original below)");
      buf.append("<p style='font-style: italic;'>" + sub.getRawMGIComment() + "</p>");
      buf.append("</td></tr>");
    }

    field = "<textarea name=\"generalComment\" rows=\"10\" cols=\"60\">"
        + emptyIfNull(r.getGeneralComment()) + "</textarea>\r\n";
    getInputRow(buf, "Comment", field, null, "editMouseRow");
    buf.append("<tr class=editMouseRow><td colspan=2>To make links, use [URL]http://example.com[/URL].  For bold, use [B]bold text here[/B]</td></tr>");
    // if (r.getMouseType().equalsIgnoreCase("transgenic"))
    // {
    // String[] transgenicTypes = { "Random Insertion" };
    // field = genSelect("transgenicType", transgenicTypes,
    // "Random Insertion");
    // getInputRow(buf, "Transgenic Type", field, null, "editMouseRow");
    // }
    // else
    // {
    buf.append("<input type=\"hidden\" name=\"transgenicType\" value=\""+ "Random Insertion" + "\">");
    // }

    // String[] cryoValues = { "Y" };
    // field = genCheckbox("cryopreserved", cryoValues,
    // r.getCryopreserved());
    // getInputRow(buf, "Cryopreserved only? (DEPRECATED)", field, null,
    // "editMouseRow");

    if (r.getMouseType().equalsIgnoreCase("Mutant Allele")
        || r.getMouseType().equalsIgnoreCase("Transgenic")) {
      field = "<textarea name=\"backgroundStrain\" rows=\"10\" cols=\"60\"  >"
          + emptyIfNull(r.getBackgroundStrain()) + "</textarea>\r\n";
      getInputRow(buf, "Background Strain", field, null, "editMouseRow");

      field = getTextInput("gensat", r.getGensat(), size, 100, null);
      getInputRow(buf, "Gensat Founder Line", field, null, "editMouseRow");

    }
    if (r.getStatus() != null) {
      if (r.getStatus().equalsIgnoreCase("live")
          || r.getStatus().equalsIgnoreCase("deleted")) {
        String[] statusValues = { "live", "deleted" };
        field = genRadio("status", statusValues, r.getStatus());
        getInputRow(buf, "Record Status", field, null, "editMouseRow");
      } else if (r.getStatus().equalsIgnoreCase("incomplete")) {
        buf.append("<input type=\"hidden\" name=\"status\" value=\"incomplete\">");
      }
    }
    buf.append("</table>");
    buf.append("</div>\r\n");
    buf.append("<div style=\"clear: both;\">\r\n");

    if (sub != null) {
      buf.append("<table style=\"width: 100%;\">");
      field = "<textarea name=\"submissionNotes\" rows=\"10\" cols=\"60\">"
          + emptyIfNull(sub.getAdminComment()) + "</textarea>\r\n";
      getInputRow(buf, "Submission Admin Comment", field, null,
          "editMouseRow");
      buf.append("</table>");
    }
    if (req != null) {
      buf.append("<table style=\"width: 100%;\">");
      field = "<textarea name=\"requestNotes\" rows=\"10\" cols=\"60\">"
          + emptyIfNull(req.getAdminComment()) + "</textarea>\r\n";
      getInputRow(buf, "Change Request Admin Comment", field, null,
          "editMouseRow");
      buf.append("</table>");
    }

    if (sub != null) {
      buf.append("<input type=\"submit\" class='btn btn-success' name=\"submitButton\" value=\"Convert to Record\">");
      buf.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + "<input type=\"submit\" class='btn btn-warning' name=\"submitButton\" value=\"Move to Hold\">");
      buf.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + "<input type=\"submit\" class='btn btn-danger' name=\"submitButton\" value=\"Reject Submission\">");
    } else if (req != null) {
      buf.append("<input type=\"submit\" class='btn btn-primary' name=\"submitButton\" value=\"Complete Change Request\">");
      buf.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + "<input type=\"submit\" class='btn btn-warning' name=\"submitButton\" value=\"Move to Pending\">");
    } else if (isAdminCreating) {
      buf.append("<input type=\"submit\" class='btn btn-success' name=\"submitButton\" value=\"Create Record\">");
      buf.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + "<input type=\"submit\" class='btn btn-warning' name=\"submitButton\" value=\"Save as Incomplete\">");
    } else {
      buf.append("<input type=\"submit\" class='btn btn-primary' name=\"submitButton\" value=\"Save Changes to Record\">");
    }

    buf.append("</form>\r\n");

    if (r.getMouseID() != null) {
      ArrayList<MouseType> mouseTypes = DBConnect.getMouseTypes();

      String[] mouseTypeIDs = new String[mouseTypes.size()];
      String[] mouseTypeNames = new String[mouseTypes.size()];

      String currentTypeIDstr = "";

      for (int i = 0; i < mouseTypes.size(); i++) {
        mouseTypeIDs[i] = Integer.toString((mouseTypes.get(i)
            .getMouseTypeID()));
        mouseTypeNames[i] = mouseTypes.get(i).getTypeName();
        if (mouseTypeNames[i].equalsIgnoreCase(r.getMouseType())) {
          currentTypeIDstr = mouseTypeIDs[i];
        }
      }

      String mouseTypeOptions = genSelect("mousetype_id", mouseTypeIDs,
          mouseTypeNames, currentTypeIDstr, "style='width:150px'");

      buf.append("<form name=\"changeMouseType\" action=\"ChangeMouseType.jsp\" method=\"post\">\r\n");
      buf.append("<input type=\"hidden\" name=\"mouse_id\" value=\"" + r.getMouseID() + "\">");
      buf.append("<br><p>Change Mouse Type to: " + mouseTypeOptions);
      buf.append("&nbsp;&nbsp;<input type=\"submit\" class='btn btn-small' value=\"Change Type\">");
      buf.append("</form>\r\n");
      buf.append("</div>\r\n");
      buf.append("</div>\r\n");
    }
    return buf.toString();
  }

  private static void getTextInputRow(StringBuilder buf, String label,
      String name, String current, int size, int maxLength,
      String inputParams, String rowParams, String cssClass) {
    buf.append("<tr "
        + (cssClass != null ? "class=\"" + cssClass + "\"" : "")
        + (rowParams != null ? rowParams : "") + ">\r\n");
    buf.append("<td>" + label + "</td>\r\n");
    buf.append("<td>"
        + getTextInput(name, current, size, maxLength, inputParams)
        + "</td>");
    buf.append("</tr>\r\n");
  }

  private static void getInputRow(StringBuilder buf, String label,
      String field, String params, String cssClass) {
    getInputRow(buf, label, field, params, cssClass, null);
  }

  private static void getInputRow(StringBuilder buf, String label,
      String field, String params, String cssClass, String rowId) {
    buf.append("<tr "
        + (cssClass != null ? "class=\"" + cssClass + "\"" : "")
        + (rowId != null ? "id=\"" + rowId + "\"" : "")
        + (params != null ? params : "") + ">\r\n");
    buf.append("<td>" + label + "</td>\r\n");
    buf.append("<td>" + field + "</td>");
    buf.append("</tr>\r\n");
  }

  /**********************************************/
  /* Table creation methods */
  /*********************************************/
  public static String getRowStyle(int rowNum, String style, String altStyle) {
    return ((rowNum % 2) == 0) ? style : altStyle;
  }

  private static String getSubmissionTableHeaders() {
    StringBuffer table = new StringBuffer();
    table.append("<tr class='submissionlistH'>\r\n");
    table.append("<td style='min-width:130px'\">\r\n");
    table.append("Status ");
    table.append("</td>\r\n");
    table.append("<td style='min-width:250px'>\r\n");
    table.append("Submission info");
    table.append("<td style='min-width:110px'>\r\n");
    table.append("Details");
    table.append("</td>\r\n");
    table.append("<td style='min-width:150px'>\r\n");
    table.append("Mouse info");
    table.append("</td style='min-width:200px'>\r\n");
    table.append("<td>\r\n");
    table.append("Comment ");
    table.append("</td>\r\n");
    table.append("<td >\r\n");
    table.append("Holders ");
    table.append("</td style='width:120px'>\r\n");
    table.append("</tr>\r\n");
    return table.toString();

  }

  public static String getSubmissionTable(ArrayList<SubmittedMouse> submissions,
      String currentPageStatus, String currentPageEntered) {

    return getSubmissionTable(submissions, currentPageStatus,
        currentPageEntered, true);
  }

  public static String getSubmissionTable(ArrayList<SubmittedMouse> submissions,
      String currentPageStatus, String currentPageEntered,
      boolean showStatusChangeLinks) {

    StringBuffer table = new StringBuffer();
    table.append("<div class=\"mouseTable\">\r\n");
    table.append("<table style='width:100%'>\r\n");
    int numSubmissions = 0;

    for (SubmittedMouse nextSubmission : submissions) {

      if (numSubmissions % 20 == 0)
        table.append(getSubmissionTableHeaders());

      String rowStyle = getRowStyle(numSubmissions, "submissionlist",
          "submissionlistAlt");

      table.append("<tr class='" + rowStyle + "'>\r\n");

      // FIRST COLUMN - status
      table.append("<td valign='top'>\r\n");
      table.append("<dl>\r\n");

      table.append("<dt>\r\n");
      table.append("Submission #" + nextSubmission.getSubmissionID());
      table.append("</dt>\r\n");

      table.append("<dt>\r\n");

      String statusString = nextSubmission.getStatus();
      if (statusString.equalsIgnoreCase("need more info")) {
        statusString = "Holding";
      }

      table.append("Status: <b>" + statusString + "</b>");
      table.append("</dt>\r\n");

      if (nextSubmission.isEntered()
          && !nextSubmission.getStatus().equalsIgnoreCase(
              "need more info")) {
        table.append("<dt>\r\n");
        table.append("Record #<b>" + nextSubmission.getMouseRecordID()
            + "</b>");
        table.append("</dt>\r\n");
      }
      if (showStatusChangeLinks) {
        if (!nextSubmission.isEntered()) {
          table.append("<dt>\r\n");
          table.append("<a href=\"CreateNewRecord.jsp?id="
              + nextSubmission.getSubmissionID()
              + "\">Convert to record</a>");
          table.append("</dt>\r\n");
        }
        if (nextSubmission.getStatus().equalsIgnoreCase("rejected")) {
          table.append("<dt>\r\n");
          table.append("<a href=\"DeleteSubmission.jsp?submissionID="
              + nextSubmission.getSubmissionID()
              + "\">Delete...</a>");
          table.append("</dt>\r\n");
        }
      }
      table.append("</dl>\r\n");
      table.append("</td>");

      // COLUMN - submitter data
      table.append("<td valign='top'>\r\n");
      table.append("<dl>\r\n");

      table.append("<dt>\r\n");
      table.append(nextSubmission.getFirstName() + " "
          + nextSubmission.getLastName());
      table.append("</dt>\r\n");

      table.append("<dt>\r\n");
      table.append(nextSubmission.getDepartment());
      table.append("</dt>\r\n");

      table.append("<dt>\r\n");
      table.append(nextSubmission.getTelephoneNumber());
      table.append("</dt>\r\n");

      table.append("<dt>\r\n");
      table.append(formatEmail(
          nextSubmission.getEmail(),
          nextSubmission.getEmail(),
          "Mouse Inventory Database Submission - "
              + nextSubmission.getMouseName()));
      table.append("</dt>\r\n");
      table.append("<dt>");
      table.append("Submission date: "
          + nextSubmission.getSubmissionDate());
      table.append("</dt></dl>");
      table.append("<div style='font-size:14px;font-weight:700'>Admin Comments:</div>");
      table.append("<span class=\"mouseComment\">"
          + emptyIfNull(HTMLUtilities.getCommentForDisplay(nextSubmission.getAdminComment())) + "</span>");
      table.append("</td>\r\n");


      // COLUMN - details

      table.append("<td valign='top'><dl>\r\n");
      if (nextSubmission.getMouseName() != null
          && !nextSubmission.getMouseName().isEmpty()) {
        table.append("<dt class='mouseName'>\r\n");
        table.append(nextSubmission.getMouseName());
        table.append("</dt>\r\n");
      }

      if (nextSubmission.getMouseType() != null) {
        table.append("<dt class='mouseType'>\r\n"
            + nextSubmission.getMouseType());
        if (nextSubmission.getMouseType()
            .equalsIgnoreCase("transgenic")) {
          if (nextSubmission.getTransgenicType().equalsIgnoreCase(
              "knock-in")) {
            table.append(" - <b>Knock-in</b></dt>\r\n");
          } else if (nextSubmission.getTransgenicType()
              .equalsIgnoreCase("random insertion")) {
            table.append(" - <b>Random insertion</b></dt>\r\n");
          }

          if (nextSubmission.getTGExpressedSequence()
              .equalsIgnoreCase("mouse gene")
              || nextSubmission
                  .getTGExpressedSequence()
                  .equalsIgnoreCase("Mouse Gene (unmodified)")) {
            table.append("<dt><b>Expressed Sequence:</b></dt>\r\n");
            table.append("<dd>"
                + formatMGI(nextSubmission.getTGMouseGene())
                + "</dd>\r\n");
            if (nextSubmission.getTGMouseGeneValid() != null
                && nextSubmission.getTGMouseGeneValid()
                    .equalsIgnoreCase("true")) {
              table.append("<dd>"
                  + nextSubmission
                      .getTGMouseGeneValidationString()
                  + "</dd>\r\n");
            }
            // formatGene(nextSubmission.getGeneSymbol(),
            // nextSubmission.getGeneName(),
            // nextSubmission.getGeneID()));
          } else if (nextSubmission.getTGExpressedSequence()
              .equalsIgnoreCase("reporter")) {
            table.append("<dt><b>Expressed Sequence:</b>\r\n");
            table.append(nextSubmission.getTGReporter()
                + "</dt>\r\n");
          } else if (nextSubmission.getTGExpressedSequence()
              .equalsIgnoreCase("other")
              || nextSubmission.getTGExpressedSequence()
                  .equalsIgnoreCase(
                      "Modified mouse gene or Other")) {
            table.append("<dt><b>Expressed Sequence:</b>\r\n");
            table.append(nextSubmission.getTGOther() + "</dt>\r\n");
          } else {
            table.append("<dt><b>Expressed Sequence:</b> "
                + nextSubmission.getTGExpressedSequence()
                + "</dt>\r\n");
          }
          if (nextSubmission.getTransgenicType().equalsIgnoreCase(
              "knock-in")) {
            table.append("<dt><b>Knocked-in to:</b></dt>\r\n");
            table.append("<dd>"
                + formatMGI(nextSubmission.getTGKnockedInGene())
                + "</dd>"); // formatGene(nextSubmission
            // .getTargetGeneSymbol(), nextSubmission
            // .getTargetGeneName(), nextSubmission
            // .getTargetGeneID()));

          } else if (nextSubmission.getTransgenicType()
              .equalsIgnoreCase("random insertion")) {
            table.append("<dt><b>Regulatory element:</b> "
                + nextSubmission.getTGRegulatoryElement()
                + "</dt>\r\n");
          }
        } else if (nextSubmission.getMouseType().equalsIgnoreCase(
            "mutant allele")) {
          table.append("</dt>\r\n");
          table.append("<dd>"
              + formatMGI(nextSubmission.getMAMgiGeneID())
              + "</dd>"); // formatGene(nextSubmission.getGeneSymbol(),
          if (nextSubmission.getMAMgiGeneIDValid() != null
              && nextSubmission.getMAMgiGeneIDValid()
                  .equalsIgnoreCase("true")) {
            table.append("<dd>"
                + nextSubmission
                    .getMAMgiGeneIDValidationString()
                + "</dd>\r\n");
          }
          // nextSubmission.getGeneName(), nextSubmission
          // .getGeneID()));
          table.append("<dt><b>Modification Type:</b> "
              + nextSubmission.getMAModificationType()
              + "</dt>\r\n");
          if (nextSubmission.getMAModificationType() != null
              && nextSubmission.getMAModificationType()
                  .equalsIgnoreCase("targeted knock-in")) {
            if (nextSubmission.getTGExpressedSequence() != null) {
              if (nextSubmission.getTGExpressedSequence()
                  .equalsIgnoreCase("mouse gene")
                  || nextSubmission.getTGExpressedSequence()
                      .equalsIgnoreCase(
                          "Mouse Gene (unmodified)")) {
                table.append("<dt><b>Expressed Sequence:</b></dt>\r\n");
                table.append("<dd>"
                    + formatMGI(nextSubmission
                        .getTGMouseGene()) + "</dd>");// formatGene(nextSubmission.getGeneSymbol(),
                if (nextSubmission.getTGMouseGeneValid() != null
                    && nextSubmission.getTGMouseGeneValid()
                        .equalsIgnoreCase("true")) {
                  table.append("<dd>"
                      + nextSubmission
                          .getTGMouseGeneValidationString()
                      + "</dd>\r\n");
                }
                // nextSubmission.getGeneName(),
                // nextSubmission.getGeneID()));
              } else if (nextSubmission.getTGExpressedSequence()
                  .equalsIgnoreCase("reporter")) {
                table.append("<dt><b>Expressed Sequence:</b>\r\n");
                table.append(nextSubmission.getTGReporter()
                    + "</dt>\r\n");
              } else if (nextSubmission.getTGExpressedSequence()
                  .equalsIgnoreCase("other")
                  || nextSubmission
                      .getTGExpressedSequence()
                      .equalsIgnoreCase(
                          "Modified mouse gene or Other")) {
                table.append("<dt><b>Expressed Sequence:</b>\r\n");
                table.append(nextSubmission.getTGOther()
                    + "</dt>\r\n");
              } else {
                table.append("<dt><b>Expressed Sequence:</b> "
                    + nextSubmission
                        .getTGExpressedSequence()
                    + "</dt>\r\n");
              }
            }
          }

        } else if (nextSubmission.getMouseType().equalsIgnoreCase(
            "inbred strain")) {

          String supplier = nextSubmission.getISSupplier();
          if (nextSubmission.getISSupplierCatalogNumber() != null) {
            supplier += ", "
                + nextSubmission.getISSupplierCatalogNumber();
          }

          String formattedUrl = null;

          if (nextSubmission.getISSupplierCatalogUrl() != null) {
            formattedUrl = formatInbredStrainURL(supplier,
                nextSubmission.getISSupplierCatalogUrl());
          } else {
            formattedUrl = formatInbredStrainURL(supplier);
          }

          table.append("</dt>\r\n");
          table.append("<dt>Supplier (and Catalog#, if available): "
              + formattedUrl + "</dt>\r\n");
        }
      } else {
        table.append("<dt>MISSING MOUSE TYPE</dt>");
      }

      if (nextSubmission.getProducedInLabOfHolder() != null) {
        table.append("<dt>Produced in lab of holder: "
            + nextSubmission.getProducedInLabOfHolder()
            + "</dt>\r\n");
      }
      // if (nextSubmission.getCryoLiveStatus() != null) {
      // table.append("<dt>Mouse Status: "
      // + nextSubmission.getCryoLiveStatus() + "</dt>\r\n");
      // } else {
      // table.append("<dt>Mouse Status: Unspecified</dt>\r\n");
      // }
      table.append("</dl></td>\r\n");

      // COLUMN - mouse info (transgenic and mutant allele)
      table.append("<td valign='top'>\r\n");
      table.append("<dl>\r\n");
      if (nextSubmission.getMouseType() != null
          && (nextSubmission.getMouseType().equalsIgnoreCase(
              "transgenic") || nextSubmission.getMouseType()
              .equalsIgnoreCase("mutant allele"))) {
        String source = "";
        if (nextSubmission.getOfficialSymbol() == null
            || nextSubmission.getOfficialSymbol().equals("")) {
          source = "none";
        } else {
          source = "<b>"
              + replaceBrackets(nextSubmission
                  .getOfficialSymbol()) + "</b>";
        }

        String repositoryCatalogNumber = nextSubmission.getMouseMGIID();

        if (repositoryCatalogNumber == null
            || repositoryCatalogNumber.equals("")
            || repositoryCatalogNumber.equals("null")) {
          repositoryCatalogNumber = "none";
        } else {
          repositoryCatalogNumber = formatMGI(repositoryCatalogNumber);
        }

        table.append("<dt>Official Symbol: " + source + "</dt>\r\n");
        if (nextSubmission.getOfficialMouseName() != null
            && !nextSubmission.getOfficialMouseName().isEmpty()) {
          table.append("<dt>(");
          table.append(nextSubmission.getOfficialMouseName());
          table.append(")</dt>\r\n");
        }
        table.append("<dt>MGI: " + repositoryCatalogNumber
            + "</dt>\r\n");
        if (nextSubmission.getPMID() == null) {
          // unpublished mice
          table.append("<dt>Unpublished</dt>\r\n");
        } else {
          table.append("<dt>PubMed: "
              + formatPubMedID(nextSubmission.getPMID())
              + "</dt>\r\n");
        }

        if (nextSubmission.getBackgroundStrain() != null
            && !nextSubmission.getBackgroundStrain().isEmpty()) {
          table.append("<dt>Background Strain: "
              + nextSubmission.getBackgroundStrain()
              + "</dt>\r\n");
        }
        if (nextSubmission.getMtaRequired() != null) {
          if (nextSubmission.getMtaRequired().equalsIgnoreCase("Y")) {
            // table.append("<dt>MTA is required.</dt>\r\n");
          } else if (nextSubmission.getMtaRequired()
              .equalsIgnoreCase("D")) {
            // table.append("<dt>Unknown if MTA required.</dt>\r\n");
          }
        }

        if (nextSubmission.getGensatFounderLine() != null
            && !nextSubmission.getGensatFounderLine().isEmpty()) {
          table.append("<dt>Gensat founder line: "
              + formatGensat(nextSubmission
                  .getGensatFounderLine()) + "</dt>\r\n");
        }

      }
      table.append("</dl>\r\n");
      table.append("</td>\r\n");

      // column - comment
      table.append("<td valign='top'>\r\n");
      table.append("<span class=\"mouseComment\">"
          + emptyIfNull(HTMLUtilities.getCommentForDisplay(nextSubmission.getComment()))
          + "</span>");
      table.append("</td>\r\n");

      // COLUMN - Holder
      table.append("<td valign='top'>\r\n");

      ArrayList<MouseHolder> holders = nextSubmission.getHolders();
      if (nextSubmission.getHolderName() != null && !nextSubmission.getHolderName().equals("unassigned"))
      {
        table.append("<dt>\r\n");
        table.append(nextSubmission.getHolderName());
        table.append("</dt>\r\n");
        if (nextSubmission.hasOtherHolderName()) {
          table.append("<dd>\r\n");
          table.append(nextSubmission.getOtherHolderName());
          table.append("</dd>\r\n");
        }
        table.append("<dt>\r\n");
        table.append("Facility: " + nextSubmission.getHolderFacility());
        table.append("</dt>\r\n");
        if (nextSubmission.hasOtherFacilityName()) {
          table.append("<dd>\r\n");
          table.append(nextSubmission.getOtherHolderFacility());
          table.append("</dd>\r\n");
        }
      }
      if (holders != null)
      {
        for (MouseHolder mouseHolder : holders)
        {
          table.append("<dt>\r\n");
          table.append(mouseHolder.getFullname());
          table.append("</dt>\r\n");

          table.append("<dt>\r\n");
          table.append("Facility: " + mouseHolder.getFacilityName());
          table.append("</dt>\r\n");
        }
      }
      if (nextSubmission.getCryoLiveStatus() != null) table.append("<dt>Status: " + nextSubmission.getCryoLiveStatus()+ "</dt>");
      table.append("</td>\r\n");

      table.append("</tr>\r\n");

      numSubmissions++;
    }
    table.append("</table>\r\n");
    table.append("</div>\r\n");
    if (numSubmissions <= 0)
      return "No results found";

    return table.toString();
  }

  private static String getMouseTableHeaders() {
    StringBuffer table = new StringBuffer();
    table.append("<tr class='mouselistH'>\r\n");
    table.append("<td class='mouselistcolumn-name' >\r\n");
    table.append("Name");
    table.append("<td class='mouselistcolumn-category'>\r\n");
    table.append("Category");
    table.append("</td>\r\n");
    table.append("<td class='mouselistcolumn-details'>\r\n");
    table.append("Details");
    table.append("</td>\r\n");
    table.append("<td class='mouselistcolumn-comment'>\r\n");
    table.append("Comment ");
    table.append("</td>\r\n");
    table.append("<td class='mouselistcolumn-holders' >\r\n");
    table.append("Holders ");
    table.append("</td>\r\n");
    table.append("</tr>\r\n");
    return table.toString();
  }

  public static String getMouseTable(ArrayList<MouseRecord> mice, boolean edit,
      boolean showChangeRequest, boolean showAllHolders) {
    StringBuffer table = new StringBuffer();
    table.append("<div class=\"mouseTable\">\r\n");
    table.append("<table style='width:100%'>\r\n");
    int numMice = 0;

    for (MouseRecord nextRecord : mice) {
      String rowStyle = getRowStyle(numMice, "mouselist", "mouselistAlt");

      if (numMice % 25 == 0)
        table.append(getMouseTableHeaders());

      table.append("<tr class='" + rowStyle + "'>\r\n");

      // FIRST COLUMN - name and record number
      table.append("<td class='mouselistcolumn-name'><dl>");
      if (nextRecord.getMouseName() != null) {
        table.append("<dt class='mouseName'>\r\n");
        table.append(nextRecord.getMouseName());
        table.append("</dt>\r\n");
      }

      if (edit) {
        table.append("<dt><a href=\"EditMouseForm.jsp?id="
            + nextRecord.getMouseID() + "\">Edit record #"
            + nextRecord.getMouseID() + "</a></dt>\r\n");
      } else {
        table.append("<dt>Record #" + nextRecord.getMouseID()
            + "</dt>\r\n");
      }
      if (showChangeRequest) {
        table.append("<dt><span class='changerequest'><a class=\"SL\" href=ChangeRequestForm.jsp?mouseID="
            + nextRecord.getMouseID()
            + ">request change in record</a></span></dt>\r\n");
      }
      if (edit && nextRecord.getStatus() != null) {
        String style = "";
        if (nextRecord.getStatus().equalsIgnoreCase("live")) {
          style = "style=\"color: green; font-weight: bold;\"";
        } else if (nextRecord.getStatus().equalsIgnoreCase("deleted")) {
          style = "style=\"color: red; font-weight: bold;\"";
        }
        table.append("<dt>Record status: <span " + style + ">"
            + nextRecord.getStatus() + "</span></dt>");

      }

      table.append("</dl></td>\r\n");

      // SECOND COLUMN - category
      table.append("<td class='mouselistcolumn-category'><dl>\r\n");
      table.append("<dt class='mouseType'>\r\n"
          + nextRecord.getMouseType());
      if (nextRecord.getMouseType().equalsIgnoreCase("transgenic")) {
        if (nextRecord.getExpressedSequence() != null) {
          if (nextRecord.getExpressedSequence().equalsIgnoreCase(
              "mouse gene")
              || nextRecord
                  .getExpressedSequence()
                  .equalsIgnoreCase("Mouse Gene (unmodified)")) {
            table.append("<dt><b>Expressed Sequence:</b></dt>\r\n");
            table.append(formatGene(
                nextRecord.getTargetGeneSymbol(),
                nextRecord.getTargetGeneName(),
                nextRecord.getTargetGeneID()));
          } else if (nextRecord.getExpressedSequence()
              .equalsIgnoreCase("reporter")) {
            table.append("<dt><b>Expressed Sequence:</b>\r\n");
            table.append(nextRecord.getReporter() + "</dt>\r\n");
          } else if (nextRecord.getExpressedSequence()
              .equalsIgnoreCase("other")
              || nextRecord.getExpressedSequence()
                  .equalsIgnoreCase(
                      "Modified mouse gene or Other")) {
            table.append("<dt><b>Expressed Sequence:</b>\r\n");
            table.append(nextRecord.getOtherComment() + "</dt>\r\n");
          } else {
            table.append("<dt><b>Expressed Sequence:</b> "
                + nextRecord.getExpressedSequence()
                + "</dt>\r\n");
          }
        }
        if (nextRecord.getTransgenicType() != null
            && nextRecord.getTransgenicType().equalsIgnoreCase(
                "knock-in")) {
          table.append("<dt><b>Knocked-in to:</b></dt>\r\n");
          table.append(formatGene(nextRecord.getTargetGeneSymbol(),
              nextRecord.getTargetGeneName(),
              nextRecord.getTargetGeneID()));

        } else if (nextRecord.getTransgenicType() != null
            && nextRecord.getTransgenicType().equalsIgnoreCase(
                "random insertion")) {
          table.append("<dt><b>Regulatory element:</b> "
              + nextRecord.getRegulatoryElement() + "</dt>\r\n");
        }
      } else if (nextRecord.getMouseType().equalsIgnoreCase(
          "mutant allele")) {
        table.append("</dt>\r\n");
        table.append(formatGene(nextRecord.getGeneSymbol(),
            nextRecord.getGeneName(), nextRecord.getGeneID()));
        table.append("<dt><b>Modification Type:</b> "
            + nextRecord.getModificationType() + "</dt>\r\n");

        if (!(nextRecord.getModificationType() == null || nextRecord
            .getModificationType().isEmpty())) {
          if (nextRecord.getModificationType().equalsIgnoreCase(
              "targeted knock-in")) {
            if (nextRecord.getExpressedSequence() != null) {
              if (nextRecord.getExpressedSequence()
                  .equalsIgnoreCase("mouse gene")
                  || nextRecord.getExpressedSequence()
                      .equalsIgnoreCase(
                          "Mouse Gene (unmodified)")) {
                table.append("<dt><b>Expressed Sequence:</b></dt>\r\n");
                table.append(formatGene(
                    nextRecord.getTargetGeneSymbol(),
                    nextRecord.getTargetGeneName(),
                    nextRecord.getTargetGeneID()));
              } else if (nextRecord.getExpressedSequence()
                  .equalsIgnoreCase("reporter")) {
                table.append("<dt><b>Expressed Sequence:</b>\r\n");
                table.append(nextRecord.getReporter()
                    + "</dt>\r\n");
              } else if (nextRecord.getExpressedSequence()
                  .equalsIgnoreCase("other")
                  || nextRecord
                      .getExpressedSequence()
                      .equalsIgnoreCase(
                          "Modified mouse gene or Other")) {
                table.append("<dt><b>Expressed Sequence:</b>\r\n");
                table.append(nextRecord.getOtherComment()
                    + "</dt>\r\n");
              } else {
                table.append("<dt><b>Expressed Sequence:</b> "
                    + nextRecord.getExpressedSequence()
                    + "</dt>\r\n");
              }
            } else {
              table.append("<dt><b>Expressed Sequence:</b> "
                  + nextRecord.getExpressedSequence()
                  + "</dt>\r\n");
            }
          }
        }

      } else if (nextRecord.getMouseType().equalsIgnoreCase(
          "inbred strain")) {
        table.append("</dt>\r\n");
        table.append("<dt>Supplier and link to on-line catalog page (if available): "
            + formatInbredStrainURL(nextRecord.getSource())
            + "</dt>\r\n");
      }

      table.append("</dl></td>\r\n");

      // COLUMN - details (transgenic and mutant allele)
      table.append("<td class='mouselistcolumn-details'>\r\n");
      table.append("<dl>\r\n");
      if (nextRecord.getMouseType().equalsIgnoreCase("transgenic")
          || nextRecord.getMouseType().equalsIgnoreCase(
              "mutant allele")) {
        String source = "";
        if (nextRecord.getSource() == null
            || nextRecord.getSource().equals("")) {
          source = "none";
        } else {
          source = "<b>" + replaceBrackets(nextRecord.getSource())
              + "</b>";
        }

        String repositoryCatalogNumber = nextRecord
            .getRepositoryCatalogNumber();

        if (repositoryCatalogNumber == null
            || repositoryCatalogNumber.equals("")
            || repositoryCatalogNumber.equals("null")) {
          repositoryCatalogNumber = "none";
        } else {
          repositoryCatalogNumber = formatMGI(repositoryCatalogNumber);
        }

        table.append("<dt>Official Symbol: " + source + "</dt>\r\n");
        String officialName = nextRecord.getOfficialMouseName();
        if (officialName != null && !officialName.isEmpty()) {
          table.append("<dt>(");
          table.append(officialName);
          table.append(")</dt>\r\n");
        }
        table.append("<dt>MGI: " + repositoryCatalogNumber
            + "</dt>\r\n");
        if (nextRecord.getPubmedIDs() == null
            || nextRecord.getPubmedIDs().isEmpty()) {
          // unpublished mice
          table.append("<dt>Unpublished</dt>\r\n");
        } else {
          String allIDs = "";
          boolean first = true;
          boolean hasValidPmIds = false;
          for (String pmid : nextRecord.getPubmedIDs()) {
            if (!first)
              allIDs += ", ";
            if (pmid != null && !pmid.isEmpty())
              hasValidPmIds = true;
            first = false;
            allIDs += formatPubMedID(pmid);
          }
          if (hasValidPmIds)
            table.append("<dt>PubMed: " + allIDs + "</dt>\r\n");
        }

        if (nextRecord.getBackgroundStrain() != null
            && !nextRecord.getBackgroundStrain().isEmpty()) {
          table.append("<dt>Background Strain: "
              + nextRecord.getBackgroundStrain() + "</dt>\r\n");
        }
        if (nextRecord.getMtaRequired() != null) {
          if (nextRecord.getMtaRequired().equalsIgnoreCase("Y")) {
            // table.append("<dt>MTA is required.</dt>\r\n");
          } else if (nextRecord.getMtaRequired()
              .equalsIgnoreCase("D")) {
            // table.append("<dt>Unknown if MTA required.</dt>\r\n");
          } else if (nextRecord.getMtaRequired()
              .equalsIgnoreCase("N")) {
            // table.append("<dt>MTA is not required</dt>\r\n");
          }
        }

        if (nextRecord.getGensat() != null
            && !nextRecord.getGensat().isEmpty()) {
          table.append("<dt>Gensat founder line: "
              + formatGensat(nextRecord.getGensat())
              + "</dt>\r\n");
        }

      }
      if (nextRecord.isCryoOnly()) {
        table.append("<dt><b>Cryopreserved only</b></dt>\r\n");
      }
      if (nextRecord.getCryopreserved() != null
          && nextRecord.getCryopreserved().equalsIgnoreCase("Y")) {
        table.append("<dt><i><b>Cryopreserved only</b></i></dt>\r\n");
      }

      if (edit && nextRecord.isEndangered()) {
        table.append("<dt><span class='endangered'>Endangered</span></dt>\r\n");
      }

      table.append("</dl>\r\n");
      table.append("</td>\r\n");

      // THIRD column - comment
      table.append("<td class='mouselistcolumn-comment'>\r\n");
      table.append("<span class=\"mouseComment\">"
          + emptyIfNull(HTMLUtilities.getCommentForDisplay(nextRecord.getGeneralComment()))
          + "</span>");
      table.append("</td>\r\n");

      // FOURTH column - holders
      table.append("<td class='mouselistcolumn-holders'>\r\n");

      int holderCount = 0;
      int maxHolders = 3;
      StringBuffer holderBuf = new StringBuffer("<dl class='mouselist-holderlist'>");

      if (nextRecord.getHolders() != null) {
        boolean overMax = false;
        for (MouseHolder holder : nextRecord.getHolders()) {

          if (holder.isCovert() && !edit) {
            continue;
          }

          if (holder.getFirstname() == null
              && holder.getLastname() == null) {
            continue;
          }

          if (holderCount == maxHolders && !showAllHolders) {
            overMax = true;
            holderBuf.append("<dt><a class=\"mouselist-holderlist-showall btn btn-mini\" style='text-decoration:none'  href='#'>show all</a></dt>");
          }

          String cryoLiveStatus = "";
          if (holder.getCryoLiveStatus() != null) {
            // NULL = ignore
            // Live = live only
            // LiveCryo = live and cryo
            // Cryo = cryo only
            if (holder.getCryoLiveStatus().equalsIgnoreCase(
                "Live only")) {
              cryoLiveStatus = "";
            } else if (holder.getCryoLiveStatus().equalsIgnoreCase(
                "Live and cryo")) {
              cryoLiveStatus = "(Cryo,Live)";
            } else if (holder.getCryoLiveStatus().equalsIgnoreCase(
                "Cryo only")) {
              cryoLiveStatus = "(Cryo)";
            }
          }

          String facilityName = holder.getFacilityID() == 1 ? " "
              : "(" + holder.getFacilityName() + ")";

          String firstInitial = "";
          if (holder.getFirstname() != null) {
            firstInitial = holder.getFirstname().substring(0, 1)
                + ". ";
          }

          holderBuf.append("<dt" + (overMax ? " style='display:none'" : "") + ">"
              + (holder.isCovert() ? "<b>CVT</b>-" : "")
              + "<a title='" + holder.getFirstname() + " "
              + holder.getLastname() + " (" + holder.getDept()
              + ")' href='mailto:" + holder.getEmail()
              + (holder.getAlternateEmail() != null && !holder.getAlternateEmail().equals("") ? "?cc=" + holder.getAlternateEmail() + "&" : "?" )
              + "subject=Regarding%20"
              + nextRecord.getMouseName() + "-Record#%20"
              + nextRecord.getMouseID() +"'>" + firstInitial
              + holder.getLastname()  + "</a>" + facilityName
              + cryoLiveStatus
              + "</dt>");

          holderCount++;
        }
      }
      holderBuf.append("</dl>");

      table.append(holderBuf.toString());
      table.append("</td>\r\n");
      table.append("</tr>\r\n");
      numMice++;
    }

    table.append("</table>\r\n");
    table.append("</div>\r\n");
    table.append("<script></script>");
    if (numMice <= 0)
      return "No results found";

    return table.toString();
  }

  private static String getChangeRequestTableHeaders() {
    StringBuffer table = new StringBuffer();
    table.append("<tr class='changerequestlistH'>\r\n");

    table.append("<td style='min-width:200px'\">\r\n");
    table.append("Status");
    table.append("</td>\r\n");

    table.append("<td style='min-width:200px'\">\r\n");
    table.append("Requestor Info");
    table.append("</td>\r\n");
    table.append("<td style='min-width:200px'>\r\n");
    table.append("Changes Requested");
    table.append("</td>\r\n");

    table.append("<td style='min-width:200px'>\r\n");
    table.append("Administration");
    table.append("</td>\r\n");

    table.append("<td style='min-width:200px'>\r\n");
    table.append("Admin Comment");
    table.append("</td>\r\n");

    return table.toString();

  }

  public static String getChangeRequestsTable(ArrayList<ChangeRequest> requests,
      String currentPageStatus) {
    StringBuffer table = new StringBuffer();
    table.append("<div class=\"mouseTable\">\r\n");
    table.append("<table style='width:100%'>\r\n");
    int numRequests = 0;

    for (ChangeRequest nextRequest : requests) {

      if (numRequests % 20 == 0)
        table.append(getChangeRequestTableHeaders());

      String rowStyle = getRowStyle(numRequests, "changerequestlist",
          "changerequestlistAlt");

      table.append("<tr class='" + rowStyle + "'>\r\n");

      // COLUMN - Status
      table.append("<td valign='top'>\r\n");
      table.append("<dl>\r\n");

      table.append("<dt>");
      table.append("Request# " + nextRequest.getRequestID());
      table.append("</dt>");

      table.append("<dt>");
      table.append("Request date: " + nextRequest.getRequestDate());
      table.append("</dt>");

      table.append("<dt>\r\n");
      table.append("Status: <b>" + nextRequest.getStatus() + "</b>");
      table.append("</dt>\r\n");

      table.append("<dt>\r\n");
      table.append("<a href='DeleteChangeRequest.jsp?id="
          + nextRequest.getRequestID() + "'>Delete</a>");
      table.append("</dt>\r\n");

      table.append("</dl>\r\n");
      table.append("</td>\r\n");

      // COLUMN - Requestor Info
      table.append("<td valign='top'>\r\n");
      table.append("<dl>\r\n");

      table.append("<dt>\r\n");
      table.append(nextRequest.getFirstname() + " "
          + nextRequest.getLastname());
      table.append("</dt>\r\n");

      table.append("<dt>\r\n");
      table.append(formatEmail(nextRequest.getEmail(),
          nextRequest.getEmail(),
          "Mouse Inventory Database Change Request for record #" + nextRequest.getMouseID() + " - " + nextRequest.getMouseName()));
      table.append("</dt>\r\n");

      table.append("</dl>\r\n");
      table.append("</td>\r\n");

      // COLUMN - Requested changes
      table.append("<td valign='top'>\r\n");
      table.append("<span class=\"mouseComment\">"
          + emptyIfNull(nextRequest.getUserComment()) + "</span>");
      if (nextRequest.Properties() != null) {
        table.append("<dl>");
        for (Object key : nextRequest.Properties().keySet()) {
          String propertyName = (String) key;
          String propertyValue = (String) nextRequest.Properties()
              .get(key);
          if (propertyName.equals("Add Holder")
              || propertyName.equals("Add Facility")) {
            int splitterIndex = propertyValue.indexOf('|');
            if (splitterIndex > 0) {
              propertyValue = propertyValue
                  .substring(splitterIndex + 1);
            }
            propertyValue = "<b>" + propertyValue + "</b>";
          }

          table.append("<dt>");
          table.append(propertyName + " = " + propertyValue);
          table.append("</dt>\r\n");
        }
        table.append("</dl>");
      }
      table.append("</td>\r\n");

      // COLUMN - Administration

      table.append("<td valign='top'><dl>\r\n");
      table.append("<dt>");
      table.append("<dt><a href=\"CompleteChangeRequest.jsp?id="
          + nextRequest.getRequestID() + "\">Edit record #"
          + nextRequest.getMouseID() + "</a></dt>\r\n");
      table.append("<dt><b>" + nextRequest.getMouseName() + "</b></dt>");
      table.append("</dt>");

      // status updates? - ignore, delete, etc
      table.append("<dt>");
      table.append("Last administered: "
          + emptyIfNull(nextRequest.getLastAdminDate()));
      table.append("</dt>");

      table.append("</dl></td>\r\n");

      // COLUMN - Admin comment
      table.append("<td valign='top'>\r\n");
      table.append("<span class=\"mouseComment\">"
          + emptyIfNull(nextRequest.getAdminComment()) + "</span>");

      table.append("</td>");

      table.append("</tr>\r\n");
      numRequests++;
    }
    table.append("</table>\r\n");
    table.append("</div>\r\n");
    if (numRequests <= 0)
      return "No results found";

    return table.toString();
  }

  private static String getFacilityTableHeaders(boolean edit) {
    StringBuilder table = new StringBuilder();
    table.append("<tr class='facilitylistH'>\r\n");
    if (edit) {
      table.append("<td>ID</td>");
    }
    table.append("<td style='min-width:400px'\">\r\n");
    table.append("Facility");
    table.append("</td>\r\n");
    table.append("<td style='min-width:100px'>\r\n");
    table.append("Records");
    table.append("</td>\r\n");
    if (edit) {
      table.append("<td style='min-width:60px'\">\r\n");
      table.append("Code (for imports)");
      table.append("</td>\r\n");
      table.append("<td style='min-width:60px'\">\r\n");
      table.append("Edit");
      table.append("</td>\r\n");
    }
    table.append("</tr>\r\n");

    return table.toString();
  }

  public static String getFacilityTable(ArrayList<Facility> facilities,
      boolean edit) {
    StringBuilder table = new StringBuilder();
    table.append("<div class=\"facilityTable\">\r\n");
    table.append("<table>\r\n");
    table.append(getFacilityTableHeaders(edit));
    int numFacilities = 0;

    for (Facility facility : facilities) {

      String rowStyle = getRowStyle(numFacilities, "facilitylist",
          "facilitylistAlt");
      table.append("<tr class='" + rowStyle + "'>\r\n");
      if (edit) {
        table.append("<td>" + facility.getFacilityID() + "</td>");
      }
      table.append("<td>\r\n");
      table.append("<span class=\"mouseName\">"
          + facility.getFacilityName() + "</span> &nbsp;-&nbsp;"
          + facility.getFacilityDescription());

      table.append("</td><td>");
      table.append("<span style=\"position:relative;left:5px\"><a class=\"TL\" href=\""
          + siteRoot
          + "MouseReport.jsp?facility_id="
          + facility.getFacilityID()
          + "\">"
          + facility.getRecordCount() + " records</a></span>\r\n");
      table.append("</td>\r\n");
      if (edit) {
        table.append("<td>" + HTMLGeneration.emptyIfNull(facility.getFacilityCode()) + "</td>");
        table.append("<td><a href=\"EditFacilityForm.jsp?facilityID="
            + facility.getFacilityID() + "\">Edit</a></td>\r\n");
      }
      table.append("</tr>");
      numFacilities++;
    }
    table.append("</table>\r\n");
    if (numFacilities <= 0)
      return "No results found";
    return table.toString();
  }

  private static String getGeneTableHeaders(boolean edit) {
    StringBuilder table = new StringBuilder();
    table.append("<tr class='facilitylistH'>\r\n");

    table.append("<td style='width:500px'\">\r\n");
    table.append("Gene Details");
    table.append("</td>\r\n");
    table.append("<td style='width:220px'\">\r\n");
    table.append("Records");
    table.append("</td>\r\n");
    if (edit) {
      table.append("<td style='width:50px'\">\r\n");
      table.append("Edit");
      table.append("</td>\r\n");
    }
    table.append("</tr>\r\n");

    return table.toString();
  }

  public static String getGeneTable(ArrayList<Gene> genes, boolean edit) {
    StringBuilder table = new StringBuilder();
    table.append("<div class=\"facilityTable\">\r\n");
    table.append("<table>\r\n");
    table.append(getGeneTableHeaders(edit));
    int numFacilities = 0;

    for (Gene gene : genes) {

      String rowStyle = getRowStyle(numFacilities, "facilitylist",
          "facilitylistAlt");

      table.append("<tr class='" + rowStyle + "'>\r\n");

      table.append("<td>\r\n");
      table.append(formatGene(gene.getSymbol(), gene.getFullname(),
          gene.getMgiID()));

      table.append("</td>\r\n<td>\r\n");
      table.append("<span style=\"position:relative;left:5px\">"
          + "<a class=\"TL\" href=\"" + siteRoot
          + "MouseReport.jsp?&geneID=" + gene.getGeneRecordID()
          + "&orderby=mouse.id&mousetype_id=-1\">"
          + gene.getRecordCount() + " record"
          + (gene.getRecordCount() != 1 ? "s" : "")
          + "</a></span>\r\n");
      table.append("</td>\r\n");
      if (edit) {
        table.append("<td><a href=\"EditGeneForm.jsp?geneRecordID="
            + gene.getGeneRecordID() + "\">Edit</a></td>\r\n");
      }
      table.append("</tr>");
      numFacilities++;
    }
    table.append("</table>\r\n");
    if (numFacilities <= 0)
      return "No results found";
    return table.toString();
  }

  private static String getHolderTableHeaders(boolean edit) {
    StringBuilder table = new StringBuilder();
    table.append("<tr class='facilitylistH'>\r\n");
    if (edit) {
      table.append("<td>ID</td>");
    }
    table.append("<td style='min-width:350px'\">\r\n");
    table.append("Holder Information");
    table.append("</td>\r\n");
    table.append("<td style='min-width:300px'\">\r\n");
    table.append("Contact Information");
    table.append("</td>\r\n");
    table.append("<td >\r\n");
    table.append("Last Review Date");
    table.append("</td>\r\n");
    table.append("<td style='min-width:100px'\">\r\n");
    table.append("Mice Held");
    table.append("</td>\r\n");
    if (edit) {
      table.append("<td style='width:100px'\">\r\n");
      table.append("Edit");
      table.append("</td>\r\n");
    }
    table.append("</tr>\r\n");

    return table.toString();
  }

  public static String getHolderTable(ArrayList<Holder> holders, boolean edit) {
    StringBuilder table = new StringBuilder();
    table.append("<div class=\"facilityTable\">\r\n");
    table.append("<table >\r\n");
    table.append(getHolderTableHeaders(edit));
    int numFacilities = 0;

    for (Holder holder : holders) {

      String rowStyle = getRowStyle(numFacilities, "holderlist",
          "holderlistAlt");
      table.append("<tr class='" + rowStyle + "'>\r\n");
      if (edit) {
        table.append("<td>" + holder.getHolderID() + "</td>");
      }
      table.append("<td>\r\n");

      table.append("<div style=\"position:relative; left:2px; float:left;\"><b>"
          + holder.getFullname() + "</b></div>");
      table.append(" <div style=\"position: relative; right: 10px; float:right;\">("
          + holder.getDept() + ")</div>");
      table.append("</td>\r\n");
      table.append("<td>\r\n");
      table.append("<div style=\"position:relative; left:2px; float:left;\">"
          + formatEmail(holder.getEmail(), holder.getEmail(), "")
          + "</div>");
      table.append(" <div style=\"position: relative; right: 10px; float:right;\">Tel: "
          + holder.getTel() + "</div>");
      if (edit)
      {
        table.append("<div style='clear:both'>Alternate email: " + HTMLGeneration.emptyIfNull(holder.getAlternateEmail()) + "</div>");
      }

      table.append("</td>\r\n");
      table.append("<td>\r\n");
      if (holder.getDateValidated() != null) {
        table.append(holder.getDateValidated());
      }
      table.append("</td>\r\n");

      table.append("<td >\r\n");
      table.append("<a class=\"TL\" href=\"" + siteRoot
          + "MouseReport.jsp?holder_id=" + holder.getHolderID()
          + "&mousetype_id=-1\">" + holder.getVisibleMouseCount()
          + " records</a>");

      table.append("</td>\r\n");
      if (edit) {
        table.append("<td><a href=\"EditHolderForm.jsp?holderID="
            + holder.getHolderID() + "\">Edit holder #" + holder.getHolderID() + "</a></td>\r\n");
      }
      table.append("</tr>");
      numFacilities++;
    }
    table.append("</table>\r\n");
    if (numFacilities <= 0)
      return "No results found";
    return table.toString();
  }

  private static String getImportReportTableHeaders(boolean edit) {
    StringBuilder table = new StringBuilder();
    table.append("<tr class='facilitylistH'>\r\n");

    table.append("<td style='width:150px'\">\r\n");
    table.append("Details");
    table.append("</td>\r\n");
    table.append("<td style='min-width:500px'\">\r\n");
    table.append("Data Import Results");
    table.append("</td>\r\n");
    // if (edit)
    // {
    // table.append("<td style='width:50px'\">\r\n");
    // table.append("Edit");
    // table.append("</td>\r\n");
    // }
    table.append("</tr>\r\n");

    return table.toString();
  }

  public static String getImportReportTable(ArrayList<ImportReport> reports,
      boolean edit) {
    StringBuilder table = new StringBuilder();
    table.append("<div class=\"facilityTable\">\r\n");
    table.append("<table>\r\n");
    table.append(getImportReportTableHeaders(edit));
    int numFacilities = 0;

    for (ImportReport report : reports) {

      String rowStyle = getRowStyle(numFacilities, "facilitylist",
          "facilitylistAlt");

      table.append("<tr class='" + rowStyle + "'>\r\n");

      table.append("<td valign=\"top\">\r\n");
      //table.append("<dl><dt>Report ID: " + report.getImportReportID()
      //    + "</dt>\r\n");
      table.append("<dt>Name: " + report.getName() + "</dt>\r\n");
      table.append("<dt>Created: " + report.getCreationDate()
          + "</dt>\r\n");
      table.append("<dt><a href='DeleteImportReport.jsp?id="
          + report.getImportReportID() + "'>Delete...</a></dt></dl>");
      table.append("</td>\r\n<td>\r\n");
      table.append(report.getReportText());
      table.append("</td>\r\n");
      // if (edit)
      // {
      // table.append("<td><a href=\"EditGeneForm.jsp?geneRecordID="
      // + gene.getGeneRecordID()
      // + "\">Edit</a></td>\r\n");
      // }
      table.append("</tr>");
      numFacilities++;
    }
    table.append("</table>\r\n");
    if (numFacilities <= 0)
      return "No results found";
    return table.toString();
  }

  /**********************************************/
  /* Small utility methods */
  /*********************************************/

  public static String emptyIfNull(Object input) {
    if (input != null) {
      return input.toString();
    } else {
      return "";
    }
  }

  public static String chooseOneIfNull(String input) {
    if (input != null) {
      return input;
    } else {
      return "Choose One";
    }
  }

  public static String isChecked(boolean checked) {
    if (checked) {
      return "checked=\"checked\"";
    }
    return "";
  }

  public static String elementVisibility(boolean show) {
    if (show) {
      return "display: block";
    }
    return "display: none";
  }

  public static String rowVisibility(boolean show) {
    if (show) {
      return "";
    }
    return "display: none";
  }

  // Superscript
  public static String replaceBrackets(String s) {
    String newStr = null;
    if (s != null) {
      newStr = s.replace("<", "<sup");
      newStr = newStr.replace(">", "</sup>");
      newStr = newStr.replace("<sup", "<sup>");
      if (newStr.contains("<sup>") && !newStr.contains("</sup>")) {
        newStr += "</sup>";
      }
    }
    return newStr;
  }

  private static String formatGene(String symbol, String name, String mgi) {
    return "<dd class=\"mouseSubDetail\"><span class='geneSymbol'>"
        + symbol
        + "</span> - <span class='geneName'>"
        + name
        + "</span></dd>\r\n"
        + "<dd class=\"mouseSubDetail\"><span class='mgiGeneLink'>MGI: "
        + formatMGI(mgi) + "</span></dd>\r\n";
  }

  public static String formatMGI(String id) {
    if (id == null)
      return "&lt;not available&gt;";
    try {
      Integer.parseInt(id.trim());
    } catch (Exception e) {
      return id;
    }
    String url = "http://www.informatics.jax.org/accession/MGI:"
        + id.trim();
    StringBuffer link = new StringBuffer();
    link.append("<a class=\"MP\" href=\"" + url + "\" target=\"_blank\">");
    link.append(id);
    link.append("</a>");
    return link.toString();
  }

  public static String formatPubMedID(String value) {
    if (value == null)
      return "&lt;not available&gt;";

    if (value.equalsIgnoreCase("null"))
      return "";
    return "<a class=\"MP\" href=\"http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;list_uids="
        + value
        + "&amp;dopt=Abstract\" target=\"_blank\">"
        + value
        + "</a>";
  }

  public static String formatInbredStrainURL(String supplier) {
    if (supplier == null) {
      return "";
    }

    String jaxUrl = "http://jaxmice.jax.org/strain/";
    String jaxUrlTail = ".html";
    // non jax urls are stored as <label>||<url>
    if (supplier.contains("||")) {
      String[] tokens = supplier.split("\\|\\|");
      if (tokens.length == 2) {
        return formatInbredStrainURL(tokens[0], tokens[1]);
      }
    } else if (supplier.toUpperCase().startsWith("JAX")) {
      Matcher match = jaxPattern.matcher(supplier);
      String catalogUrl = "";
      if (match.find()) {
        catalogUrl = jaxUrl + match.group(1) + jaxUrlTail;
      }

      return formatInbredStrainURL(supplier, catalogUrl);
    }
    return formatInbredStrainURL(supplier, null);
  }

  public static String formatInbredStrainURL(String label, String url) {
    if (label == null || label.isEmpty())
      return "&lt;not available&gt;";

    if (url == null || url.trim().isEmpty())
      return label;

    String fixedUrl = url;
    if (!(fixedUrl.toLowerCase().startsWith("http://") || fixedUrl
        .toLowerCase().startsWith("https://"))) {
      fixedUrl = "http://" + fixedUrl;

    }

    return "<a class=\"MP\" target=\"_blank\" href='" + fixedUrl + "'>"
        + label + "</a>";
  }

  public static String formatGensat(String value) {
    if (value == null)
      return "";
    String gensatUrl = "http://www.gensat.org/ShowFounderLineImages.jsp?gensatFounderLine=";
    String gensatUrlTail = "";
    return "<a class=\"MP\" href='" + gensatUrl + value + gensatUrlTail
        + "'>" + value + "</a>";
  }

  // To be retired
  @SuppressWarnings("rawtypes")
  public static void formatSearchTerms(StringBuffer buf, String title,
      Vector terms, int fontSize) {
    if (terms.size() > 0) {
      buf.append("<tr><td valign=top><font size=" + fontSize + ">"
          + title + "</font></td>");
      buf.append("<td valign=top>");
      for (Enumeration e = terms.elements(); e.hasMoreElements();) {
        buf.append("<font size=" + fontSize + ">"
            + (String) e.nextElement() + "<br></font>");
      }
      buf.append("</td></tr>");
    }
  }

  public static String formatEmail(String emailAddress, String linkText,
      String subject) {
    subject = subject.replace(" ", "%20");

    return "<a href=\"mailto:" + emailAddress + "?subject=" + subject
        + "\">" + linkText + "</a>";
  }

  public static String getMultiSelectWidget(String name,
      ArrayList<String> allOptions, ArrayList<String> allOptionsNiceNames,
      ArrayList<String> selectedOptions,
      ArrayList<String> selectedOptionsNiceNames) {
    StringBuffer sb = new StringBuffer();
    sb.append("<table cellspacing=\"0\" cellpadding=\"10\" border=\"1\" bgcolor=\"white\">");
    sb.append("<tbody>");
    sb.append("<tr>");
    sb.append("<td align=\"center\">");
    sb.append("<select multiple=\"\" size=\"10\" name=\"" + name
        + "All\" id=\"" + name + "All\">");
    for (int i = 0; i < allOptions.size(); i++) {
      sb.append("<option value=\"" + allOptions.get(i) + "\">"
          + allOptionsNiceNames.get(i) + "</option>\r\n");
    }
    sb.append("</select>");
    sb.append("<br/>");
    sb.append("<p align=\"center\">");
    sb.append("<input type=\"button\" value=\"Select\" onclick=\"one2two('"
        + name + "')\"/>");
    sb.append("</p>");
    sb.append("</td>");
    sb.append("<td align=\"center\">");
    sb.append("<select multiple=\"\" size=\"10\" name=\"" + name
        + "\" id=\"" + name + "\">");
    for (int i = 0; i < selectedOptions.size(); i++) {
      sb.append("<option value=\"" + selectedOptions.get(i) + "\">"
          + selectedOptionsNiceNames.get(i) + "</option>");
    }
    sb.append("</select>");
    sb.append("<br/>");
    sb.append("<p align=\"center\">");
    sb.append("<input type=\"button\" value=\" Deselect \" onclick=\"two2one('"
        + name + "')\"/>");
    sb.append("</p>");
    sb.append("</td>");
    sb.append("</tr>");
    sb.append("</tbody>");
    sb.append("</table>");

    return sb.toString();
  }

  /*****************************************/
  // Copied from fieldGenerators.jspf
  /*****************************************/
  public static String genSelect(String name, String[] values, String current) {
    return genSelect(name, values, current, "");
  }

  public static String genSelect(String name, String[] values,
      String current, String selectParams) {
    return genSelect(name, values, values, current, selectParams);
  }

  public static String genSelect(String name, String[] values,
      String[] niceNames, String current, String selectParams) {
    if (selectParams == null) selectParams = "";
    StringBuffer b = new StringBuffer();
    b.append("<select class='chzn-select' id=\"" + name + "\" name=\"" + name + "\" "
        + selectParams + ">");
    for (int i = 0; i < values.length; i++) {
      String value = values[i];

      String niceName = niceNames[i];

      b.append("<option value=\"" + value + "\"");
      if (current != null && value != null
          && value.equalsIgnoreCase(current)) {
        b.append(" selected=selected");
      }
      b.append(">" + niceName + "\n");

    }
    b.append("</select>");
    return b.toString();
  }

  /* ********************** Modification Type ******************************** */
  public static String getModificationTypeSelect(String current) {
    String name = "modificationType";
    String[] values = { "Select one", "targeted disruption",
        "conditional allele (loxP/frt)", "gene trap insertion",
        "Chemically induced (ENU)", "spontaneous mutation",
        "other (info in comment)" };
    return genSelect(name, values, current, "");
  }

  public static String getModificationTypeSelect() {
    return getModificationTypeSelect(null);
  }

  /* ********************** MouseType ******************************** */
  public static String getMouseTypeSelect() {
    return getMouseTypeSelect(null);
  }

  public static String getMouseTypeSelect(String current) {

    return getMouseTypeSelectWithParams(current, "");
  }

  public static String getMouseTypeSelectWithParams(String current,
      String selectParams) {
    String name = "MouseType";
    String[] values = { "Select one", "Mutant Allele", "Transgenic",
        "Inbred Strain" };
    return genSelect(name, values, current, selectParams);
  }

  /* ********************** ExpressedSequence ******************************** */
  public static String getExpressedSequenceSelect(String current) {
    String name = "ExpressedSequence";
    String[] values = { "Select one", "mouse gene", "Cre", "Reporter",
        "Other" };
    return genSelect(name, values, current);
  }

  public static String getExpressedSequenceSelect() {
    return getExpressedSequenceSelect(null);
  }

  /* ********************** TransgenicType ******************************** */
  public static String getTransgenicTypeSelect(String current) {
    String name = "TransgenicType";
    String[] values = { "Select one", "knock-in", "random insertion" };
    return genSelect(name, values, current);
  }

  public static String getTransgenicTypeSelect() {
    return getTransgenicTypeSelect(null);
  }

  public static String genRadio(String name, String[] values, String current) {
    return genRadio(name, values, current, "");
  }

  public static String genYesNoRadio(String name, String current) {
    String[] values = { "Yes", "No" };
    return genRadio(name, values, current);
  }

  public static String genRadio(String name, String[] values, String current,
      String selectParams) {

    StringBuffer b = new StringBuffer();
    for (int i = 0; i < values.length; i++) {
      String value = values[i];

      b.append("<input type=\"radio\" name=\"" + name + "\" value=\""
          + value + "\" " + selectParams);
      if (current != null && value.equalsIgnoreCase(current)) {
        b.append(" checked=checked");
      }
      b.append(">" + value + "\n<br>\n");
    }
    return b.toString();
  }

  public static String genFlatRadio(String name, String[] values,
      String[] niceNames, String current, String selectParams) {

    StringBuffer b = new StringBuffer();
    for (int i = 0; i < values.length; i++) {
      String value = values[i];

      String niceName = niceNames[i];

      b.append("<input type=\"radio\" name=\"" + name + "\" value=\""
          + value + "\" " + selectParams);
      if (current != null && value.equalsIgnoreCase(current)) {
        b.append(" checked=checked");
      }
      b.append(">" + niceName + "\n");
    }
    return b.toString();
  }

  /* ********************** Modification Type ******************************** */
  public static String getModificationTypeRadio(String current) {
    return getModificationTypeRadioWithParams(current, "");
  }

  public static String getModificationTypeRadioWithParams(String current,
      String selectParams) {
    String name = "MAModificationType";
    String[] values = { "targeted disruption",
        "conditional allele (loxP/frt)", "targeted knock-in",
        "gene trap insertion", "Chemically induced (ENU)",
        "spontaneous mutation", "other (info in comment)" };
    return genRadio(name, values, current, selectParams);
  }

  public static String getModificationTypeRadio() {
    return getModificationTypeRadio(null);
  }

  /* ********************** MouseType ******************************** */
  public static String getMouseTypeRadio() {
    return getMouseTypeRadio(null);
  }

  public static String getMouseTypeRadio(String current) {

    return getMouseTypeRadioWithParams(current, "");
  }

  public static String getMouseTypeRadioWithParams(String current,
      String selectParams) {
    String name = "mouseType";
    String[] values = { "Mutant Allele", "Transgenic", "Inbred Strain" };
    return genRadio(name, values, current, selectParams);
  }

  /* ********************** ExpressedSequence ******************************** */
  public static String getExpressedSequenceRadio(String current) {
    return getExpressedSequenceRadioWithParams(current, "");
  }

  public static String getExpressedSequenceRadioWithParams(String current,
      String selectParams) {

    String name = "TGExpressedSequence";
    String[] values = { "Reporter", "Cre", "Mouse Gene (unmodified)",
        "Modified mouse gene or Other" };
    return genRadio(name, values, current, selectParams);
  }

  public static String getExpressedSequenceRadio() {
    return getExpressedSequenceRadio(null);
  }

  /* ********************** TransgenicType ******************************** */

  public static String getTransgenicTypeRadio(String current) {
    return getTransgenicTypeRadioWithParams(current, "");
  }

  public static String getTransgenicTypeRadioWithParams(String current,
      String selectParams) {

    String name = "transgenicType";
    String[] values = { "knock-in", "random insertion" };
    return genRadio(name, values, current, selectParams);
  }

  public static String getTransgenicTypeRadio() {
    return getTransgenicTypeRadio(null);
  }

  public static String genCheckbox(String name, String[] values,
      String current) {
    return genCheckbox(name, values, current, "");
  }

  public static String genCheckbox(String name, String[] values,
      String current, String selectParams) {

    StringBuffer b = new StringBuffer();
    for (int i = 0; i < values.length; i++) {
      String value = values[i];

      b.append("<input type=\"checkbox\" name=\"" + name + "\" value=\""
          + value + "\" " + selectParams);
      if (current != null && value.equals(current)) {
        b.append(" checked=checked");
      }
      b.append(">" + value + "\n<br>\n");
    }

    return b.toString();
  }

  public static boolean isInArray(String value, String[] array) {
    if (value == null)
      return false;
    if (array == null || array.length <= 0)
      return false;

    for (String arrVal : array) {
      if (arrVal.equalsIgnoreCase(value))
        return true;
    }
    return false;
  }

  public static String getMouseTypeSelectionLinks(int checkedMouseTypeID,
      String checkedOrderBy, int holderID, int geneRecordID,
      ArrayList<MouseType> mouseTypes, String status, String searchTerms,
      int creOnly, int facilityID) {
    String checked = "";
    if (checkedMouseTypeID == -1) {
      checked = "checked";
    }
    StringBuffer buf = new StringBuffer();
    buf.append("<table style=\"line-height:12px;\">");
    if (status != null) {
      buf.append("<tr><td colspan=\"3\">Search: <input type=\"text\" size=\"20\" name=\"searchterms\""
          + (searchTerms != null ? "value=\"" + searchTerms + "\""
              : "") + "></td></tr>");
    }
    buf.append("<tr><td>Type:</td>");
    buf.append("<td><input type=\"radio\" name=\"mousetype_id\" value=\"-1\" "
        + checked + ">All</td>");
    for (MouseType type : mouseTypes) {
      checked = "";
      if (checkedMouseTypeID == type.getMouseTypeID()) {
        checked = "checked";
      }
      buf.append("<td><input type=\"radio\" name=\"mousetype_id\" value=\""
          + type.getMouseTypeID()
          + "\" "
          + checked
          + ">"
          + type.getTypeName() + "</td>");
    }
    buf.append("</tr>");
    if (creOnly >= 0) {
      buf.append("<tr><td></td><td colspan=\"4\"><input type=\"checkbox\" name=\"creonly\" value=\"1\" "
          + (creOnly == 1 ? "checked=\"checked\"" : "")
          + ">Cre-expressing mice only.</td></tr>");
    }
    buf.append("</tr>\n");
    if (status != null) {
      buf.append("<tr>\n");
      buf.append("<td>Status\n");
      buf.append(gimmeRadio("live", "Live", status, "status"));
      buf.append(gimmeRadio("deleted", "Deleted", status, "status"));
      buf.append(gimmeRadio("all", "All", status, "status"));
      buf.append("</td>\n");
      buf.append("</tr>\n");
    }
    buf.append("<tr><td>Sort by:</td>");
    buf.append(gimmeSortRadio("mouse.name", "Mouse Name", checkedOrderBy));
    buf.append(gimmeSortRadio("mouse.id", "Record #", checkedOrderBy));
    buf.append(gimmeSortRadio("mouse.id desc", "Record #(reverse)",
        checkedOrderBy));

    buf.append("</tr>\n");

    if (holderID != -1) {
      buf.append("<tr><td><input type=\"hidden\" name=\"holder_id\" value=\""
          + holderID + "\"></td></tr>");
    }
    if (geneRecordID != -1) {
      buf.append("<tr><td><input type=\"hidden\" name=\"geneID\" value=\""
          + geneRecordID + "\"></td></tr>");
    }
    if (facilityID != -1) {
      buf.append("<tr><td><input type=\"hidden\" name=\"facility_id\" value=\""
          + facilityID + "\"></td></tr>");
    }

    buf.append("<tr><td colspan=\"5\"><input type=\"submit\" class='btn btn-small' value=\"Update\"></td></tr>");
    buf.append("</table>");

    return buf.toString();
  }

  public static String getPageSelectionLinks(int limit, int pageNum,
      int total, boolean includeLimitSelector) {
    StringBuffer buf = new StringBuffer();

    String[] values = new String[] { "10", "25", "50", "100", "500", "-2" };
    String[] labels = new String[] { "10", "25", "50", "100", "500", "All" };

    String perPageDropDown = genSelect("limit", values, labels, Integer.toString(limit), "style='width:60px'");
    buf.append("<table>");
    if (includeLimitSelector) {
      buf.append("<tr>" + "<td>" + "Records per page: &nbsp;"  + perPageDropDown + "</td></tr>");
    }

    if (limit > 0)
    {
      buf.append("<tr><td>Page ");
      int pageCount = (total + limit - 1) / limit;
      // int buttonCount = 0;
      // int maxButtons = 10;
      boolean skipping = false;
      for (int i = 1; i <= pageCount; i++) {
        String cssClass = "linkButton";
        if (i == pageNum) {
          cssClass = "linkButtonCurrent";
        }
        if (i != 1 && i != pageCount && i != (pageCount - 1) && (Math.abs(pageNum - i) > 4)) {
          if (!skipping) {
            buf.append("...");
          }
          skipping = true;
          continue;
        }
        skipping = false;

        buf.append("<input class=\"" + cssClass
            + "\" type=\"submit\" name=\"pagenum\" value=\"" + i + "\">\r\n");
      }
      buf.append("</td>" + "</tr>");
    }
    buf.append("</table>");
    return buf.toString();
  }

  private static String gimmeSortRadio(String param, String niceName,
      String current) {
    return gimmeRadio(param, niceName, current, "orderby");
  }

  private static String gimmeRadio(String param, String niceName,
      String current, String inputName) {
    String checked = "";
    if (param != null && param.equalsIgnoreCase(current)) {
      checked = "checked";
    }
    String s = "<td>";
    if (niceName.length() > 10) {
      s = "<td colspan=2>";
    }
    s += "<input type=\"radio\" name=\"" + inputName + "\" value=\""
        + param + "\" " + checked + ">" + niceName + "</td>";
    return s;
  }

  public static int stringToInt(String str) {
    try {
      return Integer.parseInt(str);
    } catch (Exception e) {
      return -1;
    }
  }

  public static String getTextInput(String name, String current, int size,
      int maxLength, String params) {
    return "<input type=\"text\" name=\"" + name + "\""
        + (current != null ? " value=\"" + current + "\"" : "")
        + "size=\"" + size + "\" "
        + (maxLength > 0 ? "maxlength=\"" + maxLength + "\" " : "")
        + (params != null ? params : "") + ">\r\n";
  }

}
