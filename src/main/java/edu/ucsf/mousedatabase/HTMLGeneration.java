package edu.ucsf.mousedatabase;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
//import com.mysql.jdbc.Blob;
import java.sql.Blob;

import edu.ucsf.mousedatabase.admin.EmailRecipientManager;
import edu.ucsf.mousedatabase.admin.EmailRecipientManager.EmailRecipient;
import edu.ucsf.mousedatabase.objects.*;
import edu.ucsf.mousedatabase.objects.ChangeRequest.Action;

public class HTMLGeneration {
  //added ROOT to run locally -EW
  public static final String siteRoot = "/";
  public static final String adminRoot = siteRoot + "admin/";
  public static final String imageRoot = siteRoot + "img/";
  public static final String scriptRoot = siteRoot + "js/";
  public static final String styleRoot = siteRoot + "css/";
  public static final String dataRoot = siteRoot + "rawdata/";

  public static void setGoogleAnalyticsId(String id, String domainSuffix) {
    googleAnalyticsScript =
            "<script type=\"text/javascript\">\r\n"
                    + "  var _gaq = _gaq || [];\r\n"
                    + "  _gaq.push(['_setAccount', '" + id + "']);\r\n"
                    + "  _gaq.push(['_setDomainName', '" + domainSuffix + "']);\r\n"
                    + "  _gaq.push(['_trackPageview']);\r\n"
                    + "  (function() {\r\n"
                    + "    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\r\n"
                    + "    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\r\n"
                    + "    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\r\n"
                    + "  })();" + "</script>\r\n";
  }

  public static String googleAnalyticsScript;

  private static final Pattern jaxPattern = Pattern.compile("[^\\d]*([\\d]+)");

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
    buf.append("<!DOCTYPE html>\r\n");
    buf.append("<html>\r\n");
    buf.append("<head>\r\n");
    buf.append("<meta name='robots' content='noindex, nofollow'>");
    if (disableCache) {
      buf.append("<meta http-equiv='cache-control' content='no-cache'>\r\n");
      buf.append("<meta http-equiv='expires' content='0'>\r\n");
      buf.append("<meta http-equiv='pragma' content='no-cache'>\r\n");
    }
    buf.append("<title>" + DBConnect.loadSetting("general_site_name").value + "</title>\r\n");

    buf.append("<link href='" + styleRoot + "bootstrap.css' rel='stylesheet' type='text/css'>\r\n");
    buf.append("<link href='" + styleRoot + "bootstrap-collapse.css' rel='stylesheet' type='text/css'>\r\n");
    buf.append("<link href='" + styleRoot + "font-awesome.min.css' rel='stylesheet' type='text/css'>\r\n");
    buf.append("<link href='" + styleRoot + "font-awesome-ie7.min.css' rel='stylesheet' type='text/css'>\r\n");
    buf.append("<link href='" + styleRoot + "chosen.css' rel='stylesheet' type='text/css'>\r\n");
    buf.append("<link href='" + styleRoot + "MouseInventory.css' rel='stylesheet' type='text/css'>\r\n");
    buf.append("<link href='" + styleRoot + "jquery-ui.css 'rel='stylesheet' type='text/css' />");

    buf.append("<script src='" + scriptRoot + "jquery.min.js'></script>\r\n");
    buf.append("<script src='" + scriptRoot + "jquery-ui.min.js'></script>\r\n");
    buf.append("<script src='" + scriptRoot + "chosen.jquery.min.js'></script>\r\n");
    buf.append("<script src='" + scriptRoot + "jquery.ba-bbq.min.js'></script>\r\n");
    buf.append("<script src='" + scriptRoot + "uiHelperFunctions.js'></script>\r\n");
    buf.append("<script src='" + scriptRoot + "ajaxFunctions.js'></script>\r\n");
    buf.append("<script src='" + scriptRoot + "validationFunctions.js'></script>\r\n");
    buf.append("<script src='" + scriptRoot + "application.js'></script>\r\n");
    buf.append("<script src='" + scriptRoot + "respond.min.js'></script>\r\n"); //ie8 fix
    buf.append("<script src='" + scriptRoot + "bootstrap-collapse.js'></script>\r\n"); //ie8 fix

    if (isAdminPage) {
      buf.append("<link href='" + styleRoot + "jquery.cleditor.css' type='text/css' rel='stylesheet'>");
      buf.append("<script src='" + scriptRoot + "bootstrap.min.js'></script>");
      buf.append("<script src='" + scriptRoot + "jquery.cleditor.js'></script>");
    }

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
    table.append("<div class='site_container'>");
    table.append("<div id=\"pageTitleContainer\">");
    table.append("<div>"); //pagetitle

    table.append("<a href=\"" + siteRoot + "about.jsp\">"
            + "<img src=/img/MSU_logo.png title='Homepage' style='image-rendering: -webkit-optimize-contrast; padding-top: 10px !important; padding-bottom: 10px !important; background-color:#FFFFFF' width='7%' class='10year' >");
    table.append("<a href=\"" + siteRoot + "history.jsp\">"
            + "<img src=/img/MSURDB-Logo.png title='History of MouseDB' style='image-rendering: -webkit-optimize-contrast; padding-top: 10px !important; padding-bottom: 10px !important; padding-left: 15px !important; background-color:#FFFFFF' width= 67%' class='10year' >");

//    table.append("<span id=\"pageTitle\">" + "<a href='" + siteRoot + "'>" + DBConnect.loadSetting("general_site_name").value + "</a></span>");


    table.append("</div>");

    table.append("<div>"); // About, faq, contact links
//    table.append("<span class=\"titleSubText\">");
//    table.append("<a href=\"" + siteRoot + "about.jsp\">Home</a>&nbsp;");
//    // table.append("&nbsp;<a href=\""+siteRoot+"faq.jsp\">FAQ</a>&nbsp;");
//    table.append("&nbsp;<a href=\"" + siteRoot
//        + "contact.jsp\">Submit Feedback</a>");
    table.append("</span>");

    table.append("</div>"); // About, faq, contact links
    table.append("</div>"); //pagetitle
    // Quick Search bar

    table.append("</div>"); //pagetitlecontainer
    table.append("</div>"); //pageheader
    table.append("</div>"); //pageheadercontainer
    // Navigation Bar
    table.append("<div id=\"navigationLinksContainer\" class='clearfix'>");
    table.append("<div id='navigationLinks' class='site_container'>");
    table.append("<div id='wrap' class='wrapper'>");
    table.append("<ul class=\"navLinkUL\">");
    //Navigation links for the header bar
//    table.append(addNavLink("Search", "search.jsp", null,
//        currentPageFilename, false,"nav-search-link"));

    table.append("<li style=padding-top:3px;border-right:none class=\"NavLinkItem\">");
    table.append("<a href=\"" + siteRoot + "about.jsp\"> <img style=\"width:30px;\" src=/img/House.svg></a>");
    table.append("</li>");

    table.append("<li style=padding-top:3px;padding-left:7px;border-right:none class=\"NavLinkItem\">");
    table.append("<a href=mailto:ORA.MSURodentDatabase@msu.edu> <img style=\"width:30px;padding-right:12px\" src=/img/Email.svg></a>");
    table.append("</li>");

    table.append(addNavLink("Submit Rodents", "submission.jsp", null,
            currentPageFilename, false, "", true, true));
    table.append(addNavLink("Rodent Records", "MouseReport.jsp", null,
            currentPageFilename, false, "nav-mouselist", true, false));
    table.append(addNavLink("Gene List", "GeneReport.jsp", null,
            currentPageFilename, false, "", true, false));
    table.append(addNavLink("Holder List", "HolderReport.jsp", null,
            currentPageFilename, false, "", true, false));
    table.append(addNavLink("Facility List", "FacilityReport.jsp", null,
            currentPageFilename, false, "", true, false));
    // table.append(addNavLink("Endangered Mice", "EndangeredReport.jsp",
    // null,currentPageFilename,false));
    table.append(addNavLink("About", "aboutTab.jsp", null, currentPageFilename, false, "", true, false));


    String action = isAdminPage ? (adminRoot + "AdminSearch.jsp") : (siteRoot + "search.jsp");

    table.append("<li style=padding-top:5px;padding-left:12px class=\"NavLinkItem\">");
    table.append("<form style = \"display: inline;\" id=\"quickSearchForm\"action=\"" + action + "\" method=\"get\">\r\n");
    table.append("<input type=\"text\" placeholder=\"Search...\" style='font-size:80%;outline:none' class=\"input-medium\"  name=\"searchterms\" >\r\n");

    table.append("<input type='hidden' name='search-source' value='quicksearch:" + currentPageFilename + "'>\r\n");
    table.append("<input id='quicksearchbutton' type=\"image\" alt=\"Submit\" src=/img/Eyeglass.svg style=\"height:20px;\">");
    table.append("<script type='text/javascript'>\r\n$('input[name=searchterms]').focus()\r\n");
    table.append("$(\"#quicksearchbutton\").click(function(){ \r\n");
    table.append("window.location.href = '" + action + "#' + $(\"#quickSearchForm\").serialize();\r\nreturn false; });");
    table.append("</script>\r\n");
    table.append("</form>");
    table.append("</li>");

    if (isAdminPage && showAdminControls) {
      table.append(addNavLink("Logout", "logout.jsp", null,
              currentPageFilename, false, "pull-right", false, true));
    } else {
      table.append(addNavLink("Admin", "admin.jsp", null,
              isAdminPage ? "admin.jsp" : currentPageFilename, true, "pull-right", false, true));

    }
    table.append("</ul>");
    table.append("</div>");
    table.append("</div>"); //navigationlinks
    table.append("</div>"); //navigationlinkscontainer

    // Admin Row
    if (isAdminPage && showAdminControls) {
      table.append("<div id=\"adminLinksContainer\" class='clearfix'>");
      table.append("<div id='adminLinks' class='site_container'>");
      table.append("<ul class=\"navLinkUL\">");
      table.append(addNavLink("Admin Home", "admin.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Change Requests", "ManageChangeRequests.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Submissions", "ListSubmissions.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Admin Search", "AdminSearch.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Edit Records", "EditMouseSelection.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Edit Holders", "EditHolderChooser.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Edit Facilities", "EditFacilityChooser.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Data Upload", "ImportReports.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Reports", "Reports.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Notes", "ManageAdminNotes.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Options", "Options.jsp", null, currentPageFilename, true));
      table.append("</ul>");
      table.append("</div>"); //adminlinks
      table.append("</div>"); //adminlinkscontainer
    }

    table.append("</div>"); //navbarcontainer
    Setting alert = DBConnect.loadSetting("general_site_alert");
    if (alert.value != null && !alert.value.trim().isEmpty()) {
      table.append("<div class='site_container'><div class='alert alert-error' style='margin-top: 15px'><b>" + alert.value + "</b></div></div>");
    }
    return table.toString();
  }

  /*public static String getLoggedInNavBar(String currentPageFilename,
                                 boolean isAdminPage, boolean showAdminControls) {
    StringBuffer table = new StringBuffer();
    table.append("<div id=\"navBarContainer\">");

    // Page header
    table.append("<div id=\"pageHeaderContainer\" class='clearfix'>");
    table.append("<div class='site_container'>");
    table.append("<div id=\"pageTitleContainer\">");
    table.append("<div>"); //pagetitle

    table.append("<img src=/img/logo_mouse_database_MSU.png width='120px'style='background-color:#DDE6E5' class='MDBlogo'>");
    table.append("<span id=\"pageTitle\">" + "<a href='" + siteRoot + "'>" + DBConnect.loadSetting("general_site_name").value + "</a></span>");


    table.append("</div>");

    table.append("<div>"); // About, faq, contact links
    table.append("<span class=\"titleSubText\">");

      table.append("<form name=home action=loggedInServlet method=post style=\"display: inline;\">"
              + "<a href=\"javascript: submitformhome()\">Home</a>"
              +"<input type=\"hidden\" name=\"page\" value=go_home>"
              + "</form>");
      table.append("<form name=submitfeedback action=loggedInServlet method=post style=\"display: inline;\">"
              + "<a href=\"javascript: submitformfeedback()\">Submit Feedback</a>"
              +"<input type=\"hidden\" name=\"page\" value=contact>"
              + "</form>");
      table.append("<form name=logout action=loggedInServlet method=post style=\"display: inline;\">"
              + "<a href=\"javascript: submitlogout()\">Logout</a>"
              +"<input type=\"hidden\" name=\"page\" value=logout>"
              + "</form>");


    table.append("</span>");

    table.append("</div>"); // About, faq, contact links
    table.append("</div>"); //pagetitle
    // Quick Search bar
    if (currentPageFilename == null || !currentPageFilename.equals("search.jsp"))
    {
      table.append("<div id=\"quickSearchContainer\">");
      String action = isAdminPage ? (adminRoot + "AdminSearch.jsp") : (siteRoot + "search.jsp");
      table.append("<form id=\"quickSearchForm\"action=\"" + action + "\" method=\"get\">\r\n");
      table.append("<input type=\"text\" class=\"input-medium search-query\"  name=\"searchterms\" >\r\n");
      table.append("<input type='hidden' name='search-source' value='quicksearch:" + currentPageFilename + "'>\r\n");
      table.append("<input id='quicksearchbutton' class=\"btn search-query\" type=\"submit\" value=\"" +
              (isAdminPage ? "Admin Quick" : "Quick") + " Search\">\r\n");
      table.append("<script type='text/javascript'>\r\n$('input[name=searchterms]').focus()\r\n");
      table.append("$(\"#quicksearchbutton\").click(function(){ \r\n");
      table.append("window.location.href = '" + action + "#' + $(\"#quickSearchForm\").serialize();\r\nreturn false; });");
      table.append("</script>\r\n");
      table.append("</form>");

      table.append("</div>");

    }
    table.append("<a href=\"" + siteRoot + "history.jsp\">"
            + "<img src=/img/UCSF_logo.png title='History of MouseDB' style='padding-top: 15px !important; background-color:#DDE6E5' width='120px' class='10year' >");



    table.append("</div>"); //pagetitlecontainer
    table.append("</div>"); //pageheader
    table.append("</div>"); //pageheadercontainer
    // Navigation Bar
    table.append("<div id=\"navigationLinksContainer\" class='clearfix'>");
    table.append("<div id='navigationLinks' class='site_container'>");
    table.append("<ul class=\"navLinkUL\">");
    //Navigation links for the header bar
//    table.append(addNavLink("Search", "search.jsp", null,
//        currentPageFilename, false,"nav-search-link"));
    table.append(addLoggedInNavLink("Registration", "application.jsp", null,
            currentPageFilename, false,""));
    table.append(addLoggedInNavLink("Rodent Records", "MouseReport.jsp", null,
            currentPageFilename, false,"nav-mouselist"));
    table.append(addLoggedInNavLink("Gene List", "GeneReport.jsp", null,
            currentPageFilename, false,""));
    table.append(addLoggedInNavLink("Holder List", "HolderReport.jsp", null,
            currentPageFilename, false,""));
    table.append(addLoggedInNavLink("Facility List", "FacilityReport.jsp", null,
            currentPageFilename, false,""));
    // table.append(addNavLink("Endangered Mice", "EndangeredReport.jsp",
    // null,currentPageFilename,false));
    table.append(addLoggedInNavLink("Submit Rodents", "submission.jsp", null,
            currentPageFilename, false,""));
    table.append(addLoggedInNavLink("About", "aboutTab.jsp", null, currentPageFilename, false,""));
    if (isAdminPage && showAdminControls){
      table.append(addNavLink("Log out", "logout.jsp", null,
              currentPageFilename, false,"pull-right small"));
    }
    else {
      table.append(addNavLink("Admin use only", "admin.jsp", null,
              isAdminPage ? "admin.jsp" : currentPageFilename, true, "pull-right small"));
    }

    table.append("</ul>");
    table.append("</div>"); //navigationlinks
    table.append("</div>"); //navigationlinkscontainer

    // Admin Row
    if (isAdminPage && showAdminControls) {
      table.append("<div id=\"adminLinksContainer\" class='clearfix'>");
      table.append("<div id='adminLinks' class='site_container'>");
      table.append("<ul class=\"navLinkUL\">");
      table.append(addNavLink("Admin Home", "admin.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Registration", "applicationsList.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Change Requests", "ManageChangeRequests.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Submissions", "ListSubmissions.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Admin Search", "AdminSearch.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Edit Records", "EditMouseSelection.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Edit Holders", "EditHolderChooser.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Edit Facilities","EditFacilityChooser.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Data Upload", "ImportReports.jsp", null,  currentPageFilename, true));
      table.append(addNavLink("Reports", "Reports.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Notes", "ManageAdminNotes.jsp", null, currentPageFilename, true));
      table.append(addNavLink("Options", "Options.jsp", null, currentPageFilename, true));
      table.append("</ul>");
      table.append("</div>"); //adminlinks
      table.append("</div>"); //adminlinkscontainer
    }

    table.append("</div>"); //navbarcontainer
    Setting alert = DBConnect.loadSetting("general_site_alert");
    if (alert.value != null && !alert.value.trim().isEmpty()) {
      table.append("<div class='site_container'><div class='alert alert-error' style='margin-top: 15px'><b>" + alert.value + "</b></div></div>");
    }
    return table.toString();
  }

  private static String addLoggedInNavLink(String targetNiceName,
                                   String targetPageFilename, String targetPageArguments,
                                   String currentPageFilename, boolean isAdminPage, String cssClass) {

    cssClass += targetPageFilename.equals(currentPageFilename) ? " current" : "";
    cssClass += " NavLinkItem";

    String url = (isAdminPage ? adminRoot : siteRoot) + targetPageFilename;
    if (targetPageArguments != null) {
      url += targetPageArguments;
    }

    String strippedNiceName = targetNiceName.toLowerCase().replaceAll(" ","");
    if (strippedNiceName.equals("registration")){
      strippedNiceName="mouseregister";
    }
    //System.out.println(strippedNiceName);
    return "<form name=\""+strippedNiceName+"\"method=post action=loggedInServlet style=\"display: inline;\">"+
            "<li class=\"" + cssClass + "\">"
            +"<input type=\"hidden\" name=\"page\" value=\"loggedIn_" + targetNiceName+"\">"
            +"<a class=\"navBarAnchor\" href=\"javascript: submitform"+strippedNiceName+"()\">"+targetNiceName
            +"</a></li>\r\n"
            +"</form>";

  }*/
  private static String addNavLink(String targetNiceName,
                                   String targetPageFilename, String targetPageArguments,
                                   String currentPageFilename, boolean isAdminPage) {
    return addNavLink(targetNiceName, targetPageFilename, targetPageArguments, currentPageFilename, isAdminPage, "");
  }

  private static String addNavLink(String targetNiceName,
                                   String targetPageFilename, String targetPageArguments,
                                   String currentPageFilename, boolean isAdminPage, String cssClass) {


    return addNavLink(targetNiceName, targetPageFilename, targetPageArguments, currentPageFilename, isAdminPage, "", false, false);

  }

  private static String addNavLink(String targetNiceName,
                                   String targetPageFilename, String targetPageArguments,
                                   String currentPageFilename, boolean isAdminPage, String cssClass, boolean rightBorder, boolean leftBorder) {

    cssClass += targetPageFilename.equals(currentPageFilename) ? " current" : "";
    cssClass += " NavLinkItem";

    String url = (isAdminPage ? adminRoot : siteRoot) + targetPageFilename;
    if (targetPageArguments != null) {
      url += targetPageArguments;
    }
    String style = "style=\"";

    if (rightBorder) {
      style += "border-right: 2px solid #FFFFFF;";
    }
    if (leftBorder) {
      style += "border-left: 2px solid #FFFFFF;";
    }
    return "<li " + style + "\" " + "class=\"" + cssClass

            + "\"><a class=\"navBarAnchor\" href=\"" + url + "\">"
            + targetNiceName + "</a></li>\r\n";

  }

  //Returns the HTML formatting for the applicant records table
  public static String getApplicantTable() {
    //First, we get all applicants based upon id
    ArrayList<Applicant> list_of_applicants = DBConnect.getAllApplicants("id");
    //Stylistic settings
    String style =
            "<style>" +
                    "table, td, th {border: 1px solid black;}" +
                    "table {width: 100%; border-collapse: collapse;}" +
                    "td{vertical-align:middle;}" +
                    "tr:nth-child(even) {background-color: #FFFFFF;}" +
                    "tr:nth-child(odd) {background-color: #f3f3f3ff;}" +
                    "tr:nth-child(1) {background-color: #d9d9d9ff;}" +
                    "</style>";

    //Break the page into one table with rows for each applicant
    //and columns for each different piece of information

    //This is the top row
    String formatting = "<table>" + "<tr>" +
            "<td>ID</td>" +
            "<td>First name</td>" +
            "<td>Last name</td>" +
            "<td>Email</td>" +
            "<td>NetID</td>" +
            "<td>AUF/Protocol Number</td>" +
            "<td>Position</td>" +
            "<td>Status</td>" +
            "<td>Change approval</td>" +
            "</tr>";

    //Iterate over each user, and add them into a new row
    for (Applicant user : list_of_applicants) {
      formatting += "<tr>";
      formatting += "<td>" + user.getId() + "</td>";
      formatting += "<td>" + user.getFirst_name() + "</td>";
      formatting += "<td>" + user.getLast_name() + "</td>";
      formatting += "<td>" + user.getEmail() + "</td>";
      formatting += "<td>" + user.getNetID() + "</td>";
      formatting += "<td>" + user.getAUF() + "</td>";
      formatting += "<td>" + user.getPosition() + "</td>";
      formatting += "<td>" + user.getApproved() + "</td>";

      //To make the change approval status button work, we need to create a
      // new form associated with each applicant tied into the last cell
      formatting += "<td>" + "<form method=\"post\" action=\"statusServlet\">";

      //Pass all the data as hidden inputs. Basically copying what we did above,
      //but hidden from the user
      formatting += "<input id=identity name=identity type=hidden value=" + user.getId() + ">";
      formatting += "<input id=first_name name=first_name type=hidden value=" + user.getFirst_name() + ">";
      formatting += "<input id=last_name name=last_name type=hidden value=" + user.getLast_name() + ">";
      formatting += "<input id=email name=email type=hidden value=" + user.getEmail() + ">";
      formatting += "<input id=net_id name=net_id type=hidden value=" + user.getNetID() + ">";
      formatting += "<input id=auf name=auf type=hidden value=" + user.getAUF() + ">";
      formatting += "<input id=position name=position type=hidden value=" + user.getPosition() + ">";
      formatting += "<input id=approved name=approved type=hidden value=" + user.getApproved() + ">";

      //Create the submit button
      formatting += "<input type=\"submit\" value=\"Change Access\">";
      //End the form
      formatting += "</form></td></tr>";


    }
    //End the table
    formatting += "</table>";
    //Return the formatted page
    return style + formatting;
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

    /*
     * This function has been edited to deal with both mice and rats.
     * In it, you will see references to r.isRat() - this determines what
     * methods are executed based upon the rodent's species. For a rat,
     * we query the RGD database and for a mouse, we use the MGI database.
     */

    //**BEGIN code performed for all operations
    //-----------------------------------------------------------------------------------------------------------------
    int size = 35; // default field size
    String field = "";
    StringBuilder buf = new StringBuilder();
    buf.append("<div class=\"mouseTable\">\r\n");
    if (sub != null) {// sub is null when editing existing mice, not null
      // when this is a new submission
      buf.append("<form name=\"mouseDetails\" action=\"UpdateSubmission.jsp\" method=\"post\">\r\n");
      buf.append("<input type=\"hidden\" name=\"submittedMouseID\" value=\"" + sub.getSubmissionID() + "\">");
      if (req != null) {
        return null;
      }
    } else if (req != null) {
      buf.append("<form name=\"mouseDetails\" action=\"UpdateChangeRequest.jsp\" method=\"post\">\r\n");
      buf.append("<input type=\"hidden\" name=\"changeRequestID\" value=\"" + req.getRequestID() + "\">");
    } else if (isAdminCreating) {
      buf.append("<form name=\"mouseDetails\" action=\"CreateRecord.jsp\" method=\"post\">\r\n");
    } else {
      buf.append("<form name=\"mouseDetails\" action=\"UpdateMouse.jsp\" method=\"post\">\r\n");
    }

    buf.append("<input type=\"hidden\" name=\"mouseID\" value=\"" + r.getMouseID() + "\">");
    buf.append("<input type=\"hidden\" name=\"mouseType\" value=\"" + r.getMouseType() + "\">");

    buf.append("<input type=\"hidden\" name=\"repositoryTypeID\" value=\"5\">");
    // everything is MGI now

    buf.append("<div class=\"editMouseFormContainer\">\r\n");
    // buf.append("<tr>\r\n");
    // buf.append("<td valign=\"top\" style=\"padding: 0px\">\r\n");
    buf.append("<div class=\"editMouseFormLeftColumn\">");
    buf.append("<table class=\"editMouseColumn\">\r\n");

    // holders
    ArrayList<MouseHolder> holderList = r.getHolders();
    if (holderList == null) {
      holderList = new ArrayList<MouseHolder>();
    }

    if (req != null && req.actionRequested() == Action.ADD_HOLDER && !req.getStatus().equals("done")) {
      if (req.getHolderId() > 0) {
        MouseHolder addedHolder = new MouseHolder();
        addedHolder.setNewlyAdded(true);
        addedHolder.setHolderID(req.getHolderId());
        if (req.getFacilityId() > 0) {
          addedHolder.setFacilityID(req.getFacilityId());
        }
        if (req.getCryoLiveStatus() != null) {
          if (req.getCryoLiveStatus().matches("Live only")) {
            addedHolder.setCryoLiveStatus("Live only");
          } else if (req.getCryoLiveStatus().matches("Live and Cryo")) {
            addedHolder.setCryoLiveStatus("Live and Cryo");
          } else if (req.getCryoLiveStatus().matches("Cryo only")) {
            addedHolder.setCryoLiveStatus("Cryo only");
          }
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
    for (IHolder holder : allHoldersObjs) {
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

    String[] covertOptions = {"Covert"};

    k = 0;
    for (MouseHolder holder : holderList) {
      if (holder.isNewlyAdded()) {
        buf.append("<tr class=\"editMouseRow\">");
        buf.append("<td colspan=\"2\"><b>Auto-filled from change request:</b></td></tr>");
      }
      buf.append("<tr class=\"editMouseRow\">");
      buf.append("<td colspan=\"2\"><div style=\"position: relative\">Holder:&nbsp;");
      buf.append(HTMLGeneration.genSelect("holder_id-" + k, holderIDs,
              holderNames, String.valueOf(holder.getHolderID()), null));
      buf.append("&nbsp;<a class='btn btn-mini btn-warning' href=\"javascript:\" onclick=\"clearHolder('"
              + k
              + "')\"><i class='icon-remove icon-white'></i></a>");
      buf.append("<br>Facility:");
      buf.append("&nbsp;");
      buf.append(HTMLGeneration.genSelect("facility_id-" + k,
              facilityIDs, facilityNames,
              String.valueOf(holder.getFacilityID()), null));
      buf.append("&nbsp;");
      buf.append("&nbsp;");
      buf.append(HTMLGeneration.genFlatRadio("cryoLiveStatus-" + k,
              new String[]{"Live only", "Live and Cryo", "Cryo only"},
              new String[]{"Live only", "Live and Cryo", "Cryo only"},
              holder.getCryoLiveStatus(), null));
      buf.append("&nbsp;");
      buf.append("&nbsp;");
      buf.append("&nbsp;");
      buf.append(HTMLGeneration.genCheckbox("covertHolder_-" + k,
              covertOptions, (holder.isCovert() ? "Covert" : "")));
      buf.append("</div>");
      buf.append("</td>");
      buf.append("</tr>\n");
      if (holder.isNewlyAdded()) {
        buf.append("<tr><td>&nbsp;</td></tr>");
      }
      k++;
    }

    getTextInputRow(buf, "Mouse Name", "mouseName", r.getMouseName(), size,
            255, null, null, "editMouseRow");

    // boolean MGIConnectionAvailable = true;
    //-----------------------------------------------------------------------------------------------------------------
    //**END code performed for all operations


    //This code block is executed if the rodent is a rat.
    //The else statement attatched to it corresponds to mice.
    //-----------------------------------------------------------------------------------------------------------------
    if (r.isRat()) {
      String mgiID = r.getRepositoryCatalogNumber();

      String rgdID = mgiID;
      field = getTextInput(
              "geneRGDID",
              emptyIfNull(mgiID),
              size,
              11,
              "id=\"geneRGDID\" onkeyup=\"validateInput('geneRGDID', 'geneRGDIDValidation', 'rgdModifiedGeneId', '')\"");

      if (rgdID != null && !rgdID.isEmpty()) {

        String geneURL = HTMLGeneration.formatRGD(rgdID);
        String resultString = "";
        String validationStyle = "";
        String manualNameSymbolEntry = "<br>RGD SQL connection unavailable.  To continue editing this record, the gene Symbol and Name must be manually entered. <br>Symbol:&nbsp;"
                + getTextInput("geneManualSymbol", "", 15, 25, null)
                + "&nbsp;&nbsp;Name:&nbsp;"
                + getTextInput("geneManualName", "", 15, 25, null);

        Gene knownGene = DBConnect.findGene(rgdID);
        if (knownGene != null) {
          resultString = knownGene.getSymbol() + " - "
                  + knownGene.getFullname();
          validationStyle = "bp_valid";
          replaceBrackets(resultString);
        } else {
          RGDResult geneResult = RGDConnect.getGeneQuery(rgdID);
          validationStyle = geneResult.isValid() ? "bp_valid"
                  : "bp_invalid";
          if (geneResult.isValid()) {
            resultString = geneResult.getSymbol() + " - "
                    + geneResult.getName() + " " + formatRGD(rgdID);
          } else {
            resultString = geneResult.getErrorString();
          }
        }

        field += "<span class='" + validationStyle
                + "' id='geneRGDIDValidation'>" + resultString
                + " (RGD:" + r.getGeneID() + ")</span>";

      } else {
        field += "<span id='geneRGDIDValidation'></span>";
      }
      getInputRow(buf, "Gene RGD ID", field, null, "editMouseRow");


    }
    //-----------------------------------------------------------------------------------------------------------------
    //End code performed only for rats


    //Code below is executed if the rodent is a mouse
    //-----------------------------------------------------------------------------------------------------------------
    else {
      if (r.isMA() || r.isTG()) {
        if (r.isMA()) {
          // Gene Section
          String mgiID = r.getGeneID();
          if ((mgiID == null || mgiID.isEmpty())
                  && sub != null
                  && (sub.getMAMgiGeneID() != null && !sub.getMAMgiGeneID().isEmpty())) {
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
            if (r.isRat()) {
              field += "<span class='" + validationStyle
                      + "' id='geneRGDIDValidation'>" + resultString
                      + " (RGD:" + formatRGD(r.getGeneID()) + ")</span>";
            } else {
              field += "<span class='" + validationStyle
                      + "' id='geneMGIIDValidation'>" + resultString
                      + " (MGI:" + geneURL + ")</span>";
            }

          } else {
            field += "<span id='geneMGIIDValidation'></span>";
          }
          getInputRow(buf, "Gene MGI ID", field, null, "editMouseRow");
        }
      }
      // Modification type section
      //Added endonuclease-mediated -EW
      String[] values = {"targeted disruption",
              "conditional allele (loxP/frt)", "targeted knock-in",
              "gene trap insertion", "Chemically induced (ENU)",
              "spontaneous mutation", "endonuclease-mediated", "other (info in comment)"};
      getInputRow(
              buf,
              "Modification Type",
              genRadio("modificationType", values,
                      r.getModificationType(), "onChange=\"UpdateModificationTypeEdit()\""),
              "style=\"" + rowVisibility(r.isMA()) + "\"", "editMouseRow");

      // Expressed Sequence section
      String[] exprSeqValues = {"Reporter", "Cre",
              "Mouse Gene (unmodified)", "Modified mouse gene or Other"};
      getInputRow(
              buf,
              "Expressed Sequence",
              genRadio("expressedSequence", exprSeqValues,
                      r.getExpressedSequence(), "onChange=\"UpdateExpressedSequenceEdit()\""),
              "id=\"trExprSeqRow\" style=\""
                      + rowVisibility(r.isTG() || (r.getModificationType() != null
                      && r.getModificationType().equalsIgnoreCase("targeted knock-in") ^ (r.getModificationType().equalsIgnoreCase("endonuclease-mediated")))) + "\"",
              "editMouseRow");

      field = "<textarea name='adminComment' rows='10' cols='60' >" + emptyIfNull(r.getAdminComment()) + "</textarea>\r\n";
      getInputRow(buf, "Record Admin Comment", field, "", "editMouseRow"); //testing

      buf.append("<a href=\"UploadFile.jsp?mouseID=" + r.getMouseID() + "\"> Upload/Delete Files</a>"); //trying this out


      buf.append("</table>\r\n");
      buf.append("</div>\r\n");
      buf.append("<div class=\"editMouseFormRightColumn\">");
      buf.append("<table class=\"editMouseColumn\">\r\n");
      if (r.isMA()
              || r.isTG()) {
        // Allele or Transgene MGI ID
        String mgiType = r.isMA() ? "Allele"
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
            if (r.isMA()) {
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
          if (r.isRat()) {
            field += "<span class='" + validationStyle
                    + "' id='geneRGDIDValidation'>" + resultString
                    + " (RGD:" + formatRGD(r.getGeneID()) + ")</span>";
          } else {
            field += "<span class='" + validationStyle
                    + "' id='geneMGIIDValidation'>" + resultString
                    + " (MGI:" + geneURL + ")</span>";
          }

        } else {
          field += "<span id='repositoryCatalogNumberValidation'></span>";
        }
        getInputRow(buf, mgiType + " MGI ID", field, null, "editMouseRow");

        getTextInputRow(buf, "Official Symbol", "source",
                emptyIfNull(officialSymbol), size, 255,
                "id=\"officialSymbol\"", null, "editMouseRow");

        getTextInputRow(buf, "Official Name", "officialMouseName",
                officialMouseName, size, 255, null, null, "editMouseRow");
      }
      //-----------------------------------------------------------------------------------------------------------------
      //End code performed for only mice


      //**BEGIN code performed for all operations
      //-----------------------------------------------------------------------------------------------------------------
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

//      field = "<input type=\"checkbox\" value=\"true\" name=\"endangered\" "
//          + (r.isEndangered() ? "checked=\"checked\"" : "") + " >";
//      getInputRow(buf, "Endangered?", field, null, "editMouseRow");

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
            && sub.getComment() != null && !(sub.getComment().equals(sub.getRawMGIComment()))) {
      buf.append("<tr class=editMouseRow><td colspan=2>");
      buf.append("<span class=red>The submitter modified the description from MGI</span> (Original below)");
      buf.append("<p style='font-style: italic;'>" + sub.getRawMGIComment() + "</p>");
      buf.append("</td></tr>");
    }

    field = "<textarea name=\"generalComment\" rows=\"10\" cols=\"60\">"
            + emptyIfNull(r.getGeneralComment()) + "</textarea>\r\n";
    getInputRow(buf, "Comment", field, null, "editMouseRow");
    buf.append("<tr class=editMouseRow><td colspan=2>To make links, use [URL]http://example.com[/URL] and display name use [link]example[/link].  For bold, use [B]bold text here[/B]</td></tr>");
    // if (r.isTG())
    // {
    // String[] transgenicTypes = { "Random Insertion" };
    // field = genSelect("transgenicType", transgenicTypes,
    // "Random Insertion");
    // getInputRow(buf, "Transgenic Type", field, null, "editMouseRow");
    // }
    // else
    // {
    buf.append("<input type=\"hidden\" name=\"transgenicType\" value=\"" + "Random Insertion" + "\">");
    // }

    // String[] cryoValues = { "Y" };
    // field = genCheckbox("cryopreserved", cryoValues,
    // r.getCryopreserved());
    // getInputRow(buf, "Cryopreserved only? (DEPRECATED)", field, null,
    // "editMouseRow");

    if (r.isMA()
            || r.isTG()) {
      field = "<textarea name=\"backgroundStrain\" rows=\"10\" cols=\"60\"  >"
              + emptyIfNull(r.getBackgroundStrain()) + "</textarea>\r\n";
      getInputRow(buf, "Background Strain", field, null, "editMouseRow");

      field = getTextInput("gensat", r.getGensat(), size, 100, null);
      getInputRow(buf, "Gensat Founder Line", field, null, "editMouseRow");

    }
    if (r.getStatus() != null) {
      if (r.getStatus().equalsIgnoreCase("live")
              || r.getStatus().equalsIgnoreCase("deleted")) {
        String[] statusValues = {"live", "deleted"};
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

    if (r.getMouseID() != null && req == null) {
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

      buf.append("<form name='changeMouseType' action='ChangeMouseType.jsp' method='post'>\r\n");
      buf.append("<input type='hidden' name='mouse_id' value='" + r.getMouseID() + "'>");
      buf.append("<br><p>Change Mouse Category to: " + mouseTypeOptions);
      buf.append("&nbsp;&nbsp;<input type='submit' class='btn btn-small' value='Change Category'>");
      buf.append("</form>\r\n");

    }
    if (req != null) {
      buf.append("<p>Should a change in mouse category be necessary, it can be made using the 'Edit Record' feature.</p>");
    }
    buf.append("</div>\r\n");
    buf.append("</div>\r\n");

    buf.append("");
    return buf.toString();

    //-----------------------------------------------------------------------------------------------------------------
    //**END code performed for all operations
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
    table.append("Category");
    table.append("</td>\r\n");
    table.append("<td style='min-width:150px'>\r\n");
    table.append("Details");
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
    table.append(getSubmissionTableHeaders());
    for (SubmittedMouse nextSubmission : submissions) {
      nextSubmission.prepareForSerialization();


      String rowStyle = getRowStyle(numSubmissions, "submissionlist",
              "submissionlistAlt");

      table.append("<tr class='" + rowStyle + "'>\r\n");

      // FIRST COLUMN - status
      table.append("<td valign='top'>\r\n");
      table.append("<dl>\r\n");

      table.append("<dt>\r\n");
      table.append("Submission #" + nextSubmission.getSubmissionID());
      table.append("</dt>\r\n");
      table.append("<dt>");
      table.append("Submission date: " + nextSubmission.getSubmissionDate());
      table.append("</dt>");

      table.append("<dt>\r\n");

      String statusString = nextSubmission.getStatus();
      if (statusString.equalsIgnoreCase("need more info")) {
        statusString = "on hold";
      }

      table.append("Status: <b>" + statusString + "</b>");
      table.append("</dt>\r\n");

      if (nextSubmission.isEntered()
              && !nextSubmission.getStatus().equalsIgnoreCase(
              "need more info")) {
        table.append("<dt>\r\n");
        table.append("<a href='" + adminRoot + "EditMouseSelection.jsp?searchterms=%23" + nextSubmission.getMouseRecordID() + "'>Record #<b>" + nextSubmission.getMouseRecordID()
                + "</b></a>");
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

      if (nextSubmission.isManualFormSubmission()) {
        table.append("<dt>\r\n");
        table.append(nextSubmission.getFirstName() + " " + nextSubmission.getLastName());
        table.append("</dt>\r\n");

        table.append("<dt>\r\n");
        table.append(nextSubmission.getDepartment());
        table.append("</dt>\r\n");

        table.append("<dt>\r\n");
        table.append(nextSubmission.getTelephoneNumber());
        table.append("</dt>\r\n");
      }


      table.append("<dt>\r\n");

//      String templateType = EmailTemplate.SUBMISSION;
//      if (nextSubmission.getSubmissionSource().contains("PDU")) {
//        templateType = EmailTemplate.PDU_SUBMISSION;
//      }
//      else if (nextSubmission.getSubmissionSource().contains("IDU")) {
//        if (nextSubmission.isPublished()) {
//          templateType = EmailTemplate.IDU_SUBMISSION_PUBLISHED;
//        }
//        else {
//          //wont get to this case because the IDU doesn't create submissions for unpublished
//        }
//      }
//
//      for(MouseHolder holder: nextSubmission.getHolders()){
//        String email = nextSubmission.isManualFormSubmission() ? nextSubmission.getEmail() : holder.getSubmitterEmail();
//        int submitterIndex = -1;
//        if (!nextSubmission.isManualFormSubmission()) {
//          table.append(holder.getSubmitterName() + "</dt><dt>");
//          submitterIndex = holder.getSubmitterIndex();
//        }
//        EmailRecipient rec = EmailRecipientManager.recipientsForRequestorAndHolder(email, holder);
//        table.append(getAdminMailLink(rec.recipients,rec.ccs, templateType,
//                                      nextSubmission.getSubmissionID(),-1,Integer.toString(nextSubmission.getMouseRecordID()),-1,submitterIndex));
//      }
//      table.append("</dt>\r\n");


      table.append("Source: " + nextSubmission.getSubmissionSource() + "</dt>");
      table.append("</dl>");
      table.append("<div style='font-size:14px;font-weight:700'>Admin Comments:</div>");
      String adminComment = nextSubmission.getAdminComment();

      table.append("<span class=\"mouseComment\">" + emptyIfNull(HTMLUtilities.getCommentForDisplay(adminComment)) + "</span>");
      table.append("</td>\r\n");


      // COLUMN - details

      table.append("<td valign='top'><dl>\r\n");
      if (nextSubmission.getMouseName() != null && !nextSubmission.getMouseName().isEmpty()) {
        table.append("<dt class='mouseName'>\r\n");
        table.append(nextSubmission.getMouseName());
        table.append("</dt>\r\n");
      }

      if (nextSubmission.getMouseType() != null) {
        table.append("<dt class='mouseType'>\r\n" + nextSubmission.getMouseType());
        if (nextSubmission.isTG()) {
          if ((nextSubmission.getTransgenicType().equalsIgnoreCase("knock-in")) ^ (nextSubmission.getTransgenicType().equalsIgnoreCase("endonuclease-mediated"))) {
            table.append(" - <b>Knock-in</b></dt>\r\n");
          } else if (nextSubmission.getTransgenicType()
                  .equalsIgnoreCase("random insertion")) {
            table.append(" - <b>Random insertion</b></dt>\r\n");
          }

          if (nextSubmission.getTGExpressedSequence()
                  .equalsIgnoreCase("mouse gene")
                  || nextSubmission
                  .getTGExpressedSequence()
                  .equalsIgnoreCase("Rodent Gene (unmodified)")) {
            table.append("<dt><b>Expressed Sequence:</b></dt>\r\n");
            if (nextSubmission.getIs_rat().equalsIgnoreCase("1")) {
              table.append("<dt>RGD: "
                      + formatRGD("" + nextSubmission.getMAMgiGeneID())
                      + "</dt>\r\n");
            } else {
              table.append("<dt>MGI: "
                      + formatMGI(nextSubmission.getTGMouseGene())
                      + "</dt>\r\n");
            }

            if (nextSubmission.getTGMouseGeneValid() != null
                    && nextSubmission.getTGMouseGeneValid()
                    .equalsIgnoreCase("true")) {
              table.append("<dt>"
                      + nextSubmission
                      .getTGMouseGeneValidationString()
                      + "</dt>\r\n");
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
                          "Modified rodent gene or Other")) {
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

          } else if (nextSubmission.getTransgenicType().equalsIgnoreCase("random insertion")) {
            table.append("<dt><b>Regulatory element:</b> "
                    + nextSubmission.getTGRegulatoryElement()
                    + "</dt>\r\n");
          }
        } else if (nextSubmission.isMA()) {
          table.append("</dt>\r\n");
          if (nextSubmission.getIs_rat() != null && nextSubmission.getIs_rat().equalsIgnoreCase("1")) {
            table.append("<dt>RGD: "
                    + formatRGD("" + nextSubmission.getMouseMGIID())
                    + "</dt>\r\n");
          } else {
            table.append("<dt>MGI: "
                    + formatMGI(nextSubmission.getTGMouseGene())
                    + "</dt>\r\n");
          } // formatGene(nextSubmission.getGeneSymbol(),
          if (nextSubmission.getMAMgiGeneIDValid() != null && nextSubmission.getMAMgiGeneIDValid().equalsIgnoreCase("true")) {
            table.append("<dt>"
                    + nextSubmission.getMAMgiGeneIDValidationString() + "</dt>\r\n");
          }
          // nextSubmission.getGeneName(), nextSubmission
          // .getGeneID()));
          table.append("<dt><b>Modification Type:</b> "
                  + nextSubmission.getMAModificationType()
                  + "</dt>\r\n");
          //Added endonuclease-mediated -EW
          if (nextSubmission.getMAModificationType() != null
                  && (nextSubmission.getMAModificationType()
                  .equalsIgnoreCase("targeted knock-in") || nextSubmission.getMAModificationType().equalsIgnoreCase("endonuclease-mediated"))) {
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
              && (nextSubmission.isTG() || nextSubmission.isMA())) {
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
          if (nextSubmission.getIs_rat() != null && nextSubmission.getIs_rat().equals("1")) {
            repositoryCatalogNumber = formatRGD(repositoryCatalogNumber);
          } else {
            repositoryCatalogNumber = formatMGI(repositoryCatalogNumber);
          }
        }

        table.append("<dt>Official Symbol: " + source + "</dt>\r\n");
        if (nextSubmission.getOfficialMouseName() != null
                && !nextSubmission.getOfficialMouseName().isEmpty()) {
          table.append("<dt>(");
          table.append(nextSubmission.getOfficialMouseName());
          table.append(")</dt>\r\n");
        }
        if (nextSubmission.getIs_rat() != null && nextSubmission.getIs_rat().equalsIgnoreCase("1")) {
          table.append("<dt>RGD: "
                  + formatRGD("" + nextSubmission.getMouseMGIID())
                  + "</dt>\r\n");
        } else {
          table.append("<dt>MGI: "
                  + formatMGI(nextSubmission.getTGMouseGene())
                  + "</dt>\r\n");
        }
        if (nextSubmission.getIs_rat() != null && nextSubmission.getIs_rat().equals("1") && nextSubmission.isPublished()) {
          // unpublished mice
          table.append("<dt>Published</dt>\r\n");
        } else if (nextSubmission.getPMID() == null) {
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

      for (MouseHolder mouseHolder : nextSubmission.getHolders()) {
        table.append("<dt>\r\n");
        table.append(mouseHolder.getFullname());
        table.append("</dt>\r\n");

        table.append("<dt>\r\n");
        table.append("Facility: " + mouseHolder.getFacilityName());
        table.append("</dt>\r\n");
      }
      if (nextSubmission.getCryoLiveStatus() != null)
        table.append("<dt>Status: " + nextSubmission.getCryoLiveStatus() + "</dt>");
      table.append("</td>\r\n");

      table.append("</tr>\r\n");

      numSubmissions++;
    }
    table.append("</table>\r\n");


    table.append("<script type='text/javascript'>\r\n");
    table.append("MouseConf.addSubmissions(" + new Gson().toJson(submissions) + ")");
    table.append("</script>");
    table.append("</div>\r\n");
    if (numSubmissions <= 0)
      return "No results found";

    return table.toString();
  }

  private static String getMouseTableHeaders() {
    StringBuffer table = new StringBuffer();
    table.append("<tr class='mouselistH'>\r\n");
    table.append("<td class='mouselistcolumn-name-header' >\r\n");
    table.append("Name");
    table.append("<td class='mouselistcolumn-category-header'>\r\n");
    table.append("Category");
    table.append("</td>\r\n");
    table.append("<td class='mouselistcolumn-details-header'>\r\n");
    table.append("Details");
    table.append("</td>\r\n");
    table.append("<td class='mouselistcolumn-comment-header'>\r\n");
    table.append("Comment ");
    table.append("</td>\r\n");
//    table.append("<td class='mouselistcolumn-files-header' >\r\n");
//    table.append("Files");
//    table.append("</td>\r\n");
    table.append("<td class='mouselistcolumn-holders-header' >\r\n");
    table.append("Holders ");
    table.append("</td>\r\n");
    table.append("</tr>\r\n");

    return table.toString();
  }

  //Called during edit record, need to add boolean showFile -EW
  public static String getMouseTable(ArrayList<MouseRecord> mice, boolean edit,
                                     boolean showChangeRequest, boolean showAllHolders) {
    return getMouseTable(mice, edit, showChangeRequest, showAllHolders, true);
  }

  public static String getMouseTable(ArrayList<MouseRecord> mice, boolean edit,
                                     boolean showChangeRequest, boolean showAllHolders, boolean showHeader) {
    return getMouseTable(mice, edit, showChangeRequest, showAllHolders, showHeader, edit);
  }

  public static String getMouseTable(ArrayList<MouseRecord> mice, boolean edit,
                                     boolean showChangeRequest, boolean showAllHolders, boolean showHeader, boolean includeJson) {
    StringBuffer table = new StringBuffer();
    table.append("<div class=\"mouseTable\">\r\n");
    table.append("<table class='facilityTableInside' style='width:100%'>\r\n");
    int numMice = 0;

    for (MouseRecord nextRecord : mice) {
      String rowStyle = getRowStyle(numMice, "mouselist", "mouselistAlt");

      if (numMice % 25 == 0 && showHeader)
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
        table.append("<dt><span class='lbl'>Record</span> #" + nextRecord.getMouseID()
                + "</dt>\r\n");
      }
      if (showChangeRequest) {
        table.append("<dt><span class='changerequest'><a class='geneItemLink' href=ChangeRequestForm.jsp?mouseID="
                + nextRecord.getMouseID()
                + "><span class='lbl'>Request record change</span></a></span></dt>\r\n");
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
      table.append("<dt class='mouseType'>\r\n<span class='lbl'>" + nextRecord.getMouseType() + "</span>");
      if (nextRecord.isTG()) {
        if (nextRecord.getExpressedSequence() != null) {
          if (nextRecord.getExpressedSequence().equalsIgnoreCase("mouse gene")
                  || nextRecord.getExpressedSequence().equalsIgnoreCase("Mouse Gene (unmodified)")) {
            table.append("<dt><b><span class='lbl'>Expressed Sequence:</span></b></dt>\r\n");
            table.append(formatGene(
                    nextRecord.getTargetGeneSymbol(),
                    nextRecord.getTargetGeneName(),
                    nextRecord.getTargetGeneID()));
          } else if (nextRecord.getExpressedSequence()
                  .equalsIgnoreCase("reporter")) {
            table.append("<dt><b><span class='lbl'>Expressed Sequence:</span></b>\r\n");
            table.append(nextRecord.getReporter() + "</dt>\r\n");
          } else if (nextRecord.getExpressedSequence()
                  .equalsIgnoreCase("other")
                  || nextRecord.getExpressedSequence()
                  .equalsIgnoreCase(
                          "Modified mouse gene or Other")) {
            table.append("<dt><b><span class='lbl'>Expressed Sequence:</span></b>\r\n");
            table.append(nextRecord.getOtherComment() + "</dt>\r\n");
          } else {
            table.append("<dt><b><span class='lbl'>Expressed Sequence:</span></b> "
                    + nextRecord.getExpressedSequence()
                    + "</dt>\r\n");
          }
        }
        if (nextRecord.getTransgenicType() != null
                && (nextRecord.getTransgenicType().equalsIgnoreCase(
                "knock-in") ^ nextRecord.getTransgenicType().equalsIgnoreCase("endonuclease-mediated"))) {
          table.append("<dt><b><span class='lbl'>Knocked-in to:</span></b></dt>\r\n");
          table.append(formatGene(nextRecord.getTargetGeneSymbol(),
                  nextRecord.getTargetGeneName(),
                  nextRecord.getTargetGeneID()));

        } else if (nextRecord.getTransgenicType() != null
                && nextRecord.getTransgenicType().equalsIgnoreCase(
                "random insertion")) {
          table.append("<dt><b><span class='lbl'>Regulatory element:</span></b> "
                  + nextRecord.getRegulatoryElement() + "</dt>\r\n");
        }
      } else if (nextRecord.isMA()) {
        table.append("</dt>\r\n");
        table.append(formatGene(nextRecord.getGeneSymbol(),
                nextRecord.getGeneName(), nextRecord.getGeneID()));
        table.append("<dt><b><span class='lbl'>Modification Type:</span></b> "
                + (nextRecord.getModificationType() != null ? nextRecord.getModificationType() : "TBD") + "</dt>\r\n");
//added endonuclease-mediated -EW
        if (!(nextRecord.getModificationType() == null || nextRecord.getModificationType().isEmpty())) {
          if (nextRecord.getModificationType().equalsIgnoreCase("targeted knock-in") || nextRecord.getModificationType().equalsIgnoreCase("endonuclease-mediated")) {
            if (nextRecord.getExpressedSequence() != null) {
              if (nextRecord.getExpressedSequence().equalsIgnoreCase("mouse gene")
                      || nextRecord.getExpressedSequence().equalsIgnoreCase("Mouse Gene (unmodified)")) {
                table.append("<dt><b><span class='lbl'>Expressed Sequence:</span></b></dt>\r\n");
                table.append(formatGene(
                        nextRecord.getTargetGeneSymbol(),
                        nextRecord.getTargetGeneName(),
                        nextRecord.getTargetGeneID()));
              } else if (nextRecord.getExpressedSequence()
                      .equalsIgnoreCase("reporter")) {
                table.append("<dt><b><span class='lbl'>Expressed Sequence:</span></b>\r\n");
                table.append(nextRecord.getReporter()
                        + "</dt>\r\n");
              } else if (nextRecord.getExpressedSequence()
                      .equalsIgnoreCase("other")
                      || nextRecord
                      .getExpressedSequence()
                      .equalsIgnoreCase(
                              "Modified mouse gene or Other")) {
                table.append("<dt><b><span class='lbl'>Expressed Sequence:</span></b>\r\n");
                table.append(nextRecord.getOtherComment()
                        + "</dt>\r\n");
              } else {
                table.append("<dt><b><span class='lbl'>Expressed Sequence:</span></b> "
                        + nextRecord.getExpressedSequence()
                        + "</dt>\r\n");
              }
            } else {
              table.append("<dt><b><span class='lbl'>Expressed Sequence:</span></b> "
                      + nextRecord.getExpressedSequence()
                      + "</dt>\r\n");
            }
          }
        }

      } else if (nextRecord.getMouseType().equalsIgnoreCase(
              "inbred strain")) {
        table.append("</dt>\r\n");
        table.append("<dt><span class='lbl'>Supplier and link to on-line catalog page (if available):</span> "
                + formatInbredStrainURL(nextRecord.getSource())
                + "</dt>\r\n");
      }

      table.append("</dl></td>\r\n");

      // COLUMN - details (transgenic and mutant allele)
      table.append("<td class='mouselistcolumn-details'>\r\n");
      table.append("<dl>\r\n");
      if (nextRecord.isTG()
              || nextRecord.isMA()) {
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
          if (nextRecord.isRat()) {
            repositoryCatalogNumber = formatRGD(repositoryCatalogNumber);
          } else {
            repositoryCatalogNumber = formatMGI(repositoryCatalogNumber);
          }

        }

        table.append("<dt><span class='lbl'> Symbol:</span> " + source + "</dt>\r\n");
        String officialName = nextRecord.getOfficialMouseName();
        if (officialName != null && !officialName.isEmpty()) {
          table.append("<dt>(");
          table.append(officialName);
          table.append(")</dt>\r\n");
        }
        if (nextRecord.isRat()) {
          table.append("<dt>RGD: "
                  + formatRGD("" + repositoryCatalogNumber)
                  + "</dt>\r\n");
        } else {
          table.append("<dt>MGI: "
                  + repositoryCatalogNumber
                  + "</dt>\r\n");
        }
        //For rats ------------------------------------------
        ArrayList<SubmittedMouse> props = new ArrayList<SubmittedMouse>();
        boolean is_null = true;
        boolean is_published = false;
        SubmittedMouse sub = null;
        if (nextRecord.getSubmittedMouseID() != null) {
          props = DBConnect.getMouseSubmission(Integer.parseInt(nextRecord.getSubmittedMouseID()));
        }
        if (!props.isEmpty()) {
          sub = props.get(0);
          is_null = (sub == null);
          is_published = sub.isPublished();
        }
        //--------------------------------

        if (!is_null && is_published && sub.getIs_rat() != null && sub.getIs_rat().equals("1")) {
          // unpublished mice
          table.append("<dt>Published</dt>\r\n");
        } else if (nextRecord.getPubmedIDs() == null
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
            table.append("<dt><span class='lbl'>PubMed:</span> " + allIDs + "</dt>\r\n");
        }

        if (nextRecord.getBackgroundStrain() != null
                && !nextRecord.getBackgroundStrain().isEmpty()) {
          table.append("<dt><span class='lbl'>Background Strain:</span> "
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
          table.append("<dt><span class='lbl'>Gensat founder line:</span> "
                  + formatGensat(nextRecord.getGensat())
                  + "</dt>\r\n");
        }

      }
      if (nextRecord.isCryoOnly()) {
        table.append("<dt><b>Cryopreserved <span class='lbl'>only</span></b></dt>\r\n");
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


//      // INTERIM column - filenames. adds a link for each file in the existing mouseRecord, add User file view
//      if (showChangeRequest || edit) {
//        table.append("<td class='mouselistcolumn-files'>\r\n");
//        //ArrayList<File> files = nextRecord.getFilenames(); //files should be set when mouseRecord made
//        ArrayList<String> filenames = nextRecord.getFilenames();
//        String fileComment = "";
//
//        for (String filename : filenames) {
//          //Log.Info("starting filename: " + filename);
//          String filenameHref = filename.replaceAll("\\s", "%20"); //check that this works
//          //Log.Info("ending filename: " + filename);
//          //"+adminRoot+"/download" not working /admin//download
//          fileComment = "<a href=" + siteRoot + "download" + "?fileName=" + filenameHref + "&mouseID=" + nextRecord.getMouseID() + ">" + filename + "</a>";
//          table.append("<div>" + fileComment + "</div>");
//        }
//
//      } else {
//        table.append("</td>\r\n<td>\r\n");
//      }

      // Fifth column - holders -EW change to last column
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

          EmailRecipient rec = EmailRecipientManager.recipientsForHolder(holder);

          String mailLink = edit ? getAdminMailLink(rec.recipients, rec.ccs,
                  EmailTemplate.MOUSERECORD,
                  firstInitial + holder.getLastname(),
                  holder.getFirstname() + " " + holder.getLastname() + " (" + holder.getDept() + ")",
                  -1, -1, nextRecord.getMouseID(), holder.getHolderID())
                  : getMailToLink(rec.recipients, rec.ccs,
                  "Regarding " + nextRecord.getMouseName() + "-Record# " + nextRecord.getMouseID(),
                  null, firstInitial + holder.getLastname(),
                  holder.getFirstname() + " " + holder.getLastname() + " (" + holder.getDept() + ")");


          holderBuf.append("<dt" + (overMax ? " style='display:none'" : "") + ">"
                  + (holder.isCovert() ? "<span class='covert_tag'>CVT</span>-" : "")
                  + mailLink + "<br>" + facilityName
                  + "<span class='lbl'>" + cryoLiveStatus + "</span>"
                  + "</dt>");

          holderCount++;
        }
        if (overMax) {
          holderBuf.append("<dt><a class=\"mouselist-holderlist-showall btn btn-mini\" style='text-decoration:none'  href='#'><span class='lbl'>show all</span></a></dt>");
        }
      }
      holderBuf.append("</dl>");

      table.append(holderBuf.toString());
      table.append("</td>\r\n");
      numMice++;
      nextRecord.prepareForSerialization();
    }

    table.append("</table>\r\n");
    table.append("</div>\r\n");
    if (includeJson) {
      table.append("<script type='text/javascript'>\r\n");
      table.append("MouseConf.addMice(" + new Gson().toJson(mice) + ")");
      table.append("</script>");
    }
    if (numMice <= 0) {
      return "No results found";
    }

    return table.toString();
  }

  private static String getChangeRequestTableHeaders() {
    StringBuffer table = new StringBuffer();
    table.append("<tr class='changerequestlistH'>\r\n");

    table.append("<td style='min-width:120px'>\r\n");
    table.append("Status");
    table.append("</td>\r\n");

    table.append("<td style='min-width: 140px'>\r\n");
    table.append("Requestor Info");
    table.append("</td>\r\n");
    table.append("<td style='min-width: 150px'>\r\n");
    table.append("Action requested");
    table.append("</td>\r\n");

    table.append("<td>\r\n");
    table.append("User comment");
    table.append("</td>\r\n");

    table.append("<td >\r\n");
    table.append("Administration");
    table.append("</td>\r\n");

    table.append("<td >\r\n");
    table.append("Admin comment");
    table.append("</td>\r\n");

    return table.toString();

  }

  public static String getChangeRequestsTable(ArrayList<ChangeRequest> requests, String currentPageStatus) {
    StringBuffer table = new StringBuffer();
    table.append("<div class=\"mouseTable\">\r\n");
    table.append("<table style='width:100%'>\r\n");
    int numRequests = 0;

    ArrayList<Integer> neededRecords = new ArrayList<Integer>();

    table.append(getChangeRequestTableHeaders());
    for (ChangeRequest nextRequest : requests) {


      String rowStyle = getRowStyle(numRequests, "changerequestlist",
              "changerequestlistAlt");

      table.append("<tr class='" + rowStyle + "'>\r\n");

      // COLUMN - Status
      table.append("<td valign='top'>\r\n");
      table.append("<dl>\r\n");

      table.append("<dt>");
      table.append("Request #" + nextRequest.getRequestID());
      table.append("&nbsp;</dt>");

      table.append("<dt>");
      table.append("Date: " + nextRequest.getRequestDate());
      table.append("&nbsp;</dt>");

      table.append("<dt>\r\n");
      table.append("Status: <b>" + nextRequest.getStatus() + "</b>");
      table.append("&nbsp;</dt>\r\n");

      table.append("<dt>\r\n");
      table.append("<a href='DeleteChangeRequest.jsp?id="
              + nextRequest.getRequestID() + "'>Delete</a>");
      table.append("&nbsp;</dt>\r\n");

      table.append("</dl>\r\n");
      table.append("</td>\r\n");

      // COLUMN - Requestor Info
      table.append("<td valign='top'>\r\n");
      table.append("<dl>\r\n");

      table.append("<dt>\r\n");
      table.append(nextRequest.getFirstname() + " "
              + nextRequest.getLastname());
      table.append("&nbsp;</dt>\r\n");

      table.append("<dt>\r\n");


      String emailRecipient = nextRequest.getEmail();
      String emailCc = null;


      IHolder holder = null;
      if (nextRequest.getHolderId() > 0) {
        holder = DBConnect.getHolder(nextRequest.getHolderId());
      } else if (nextRequest.getHolderName() != null) {
        String holderName = nextRequest.getHolderName();
        if (holderName != null) {
          String[] tokens = holderName.split(",");
          String name = "";
          for (int i = 1; i < tokens.length; i++) {
            name += tokens[i].trim() + " ";
          }
          name += tokens[0];
          holder = DBConnect.findHolder(name);
        }
      }
      if (holder != null) {
        EmailRecipient rec = EmailRecipientManager.recipientsForRequestorAndHolder(nextRequest.getEmail(), holder);
        emailRecipient = rec.recipients;
        emailCc = rec.ccs;
      }

      String emailType = EmailTemplate.CHANGEREQUEST;

      if (nextRequest.getRequestSource() != null) {
        if (nextRequest.getRequestSource().toLowerCase().contains("(pdu)") ||
                nextRequest.getUserComment().toLowerCase().contains("(pdu)")) {
          emailType = EmailTemplate.PDU_CHANGEREQUEST;
        } else if (nextRequest.getRequestSource().toLowerCase().contains("(tdu)") ||
                nextRequest.getUserComment().toLowerCase().contains("(tdu)")) {
          emailType = EmailTemplate.TDU_CHANGEREQUEST;
        }
      }

      table.append(getAdminMailLink(emailRecipient, emailCc, emailType, -1, nextRequest.getRequestID(), Integer.toString(nextRequest.getMouseID()), -1));
      table.append("&nbsp;</dt>\r\n");

      if (nextRequest.getRequestSource() != null) {
        table.append("<dt>Source: " + nextRequest.getRequestSource() + "&nbsp;</dt>");
      }

      table.append("</dl>\r\n");
      table.append("</td>\r\n");

      // COLUMN - Requested action
      table.append("<td valign='top'>\r\n");


      //new change request format, no properties
      table.append("<dl><dt class='" + nextRequest.actionRequested() + "'>");
      table.append(nextRequest.actionRequested().label + "&nbsp;</dt>");
      if (nextRequest.actionRequested() == Action.ADD_HOLDER || nextRequest.actionRequested() == Action.REMOVE_HOLDER ||
              nextRequest.actionRequested() == Action.CHANGE_CRYO_LIVE_STATUS || nextRequest.actionRequested() == Action.UPLOAD_FILE) {
        table.append("<dt>" + emptyIfNull(nextRequest.getHolderName()));
        if (nextRequest.getHolderId() == -2) {
          //TODO build a link to add new holder, with link back to this page.
          table.append(" (");
          if (nextRequest.getHolderEmail() != null) {
            table.append(nextRequest.getHolderEmail() + ", ");
          }
          table.append("not in holder list)");
        }
        table.append("</dt><dt>");
        table.append(emptyIfNull(nextRequest.getFacilityName()));
        if (nextRequest.getFacilityId() == -2) {
          table.append(" (not in facility list)");
        }
        if (nextRequest.getCryoLiveStatus() != null && !nextRequest.getCryoLiveStatus().isEmpty()) {
          table.append("</dt><dt>Status: " + nextRequest.getCryoLiveStatus() + "</dt>");
        }

      }


      table.append("</td>\r\n");

      // COLUMN - User comment

      table.append("<td>");
      if (nextRequest.getGeneticBackgroundInfo() != null && !nextRequest.getGeneticBackgroundInfo().isEmpty()) {
        table.append("<b>Genetic background info:</b> " + nextRequest.getGeneticBackgroundInfo() + "<br><br>");
      }
      if (nextRequest.getNewFileNames() != null && !nextRequest.getNewFileNames().isEmpty()) {
        table.append("<b>Add New Files:</b> " + nextRequest.getNewFileNames() + "<br><br>");
      }

      if (nextRequest.getDeleteFileNames() != null && !nextRequest.getDeleteFileNames().isEmpty()) {
        table.append("<b>Remove Files:</b> " + nextRequest.getDeleteFileNames() + "<br><br>");
      }
      table.append("<span class=\"mouseComment\">" + HTMLUtilities.getCommentForDisplay(emptyIfNull(nextRequest.getUserComment())) + "</span><br>");

      table.append("</td>");

      // COLUMN - Administration

      table.append("<td valign='top'><dl>\r\n");
      table.append("<dt>");
      table.append("<dt><a href=\"CompleteChangeRequest.jsp?id="
              + nextRequest.getRequestID() + "\">Edit record #"
              + nextRequest.getMouseID() + "</a></dt>\r\n");
      table.append("<dt><b>" + nextRequest.getMouseName() + "&nbsp;</b></dt>");
      table.append("</dt>");

      // status updates? - ignore, delete, etc
      table.append("<dt>");
      table.append("Last administered: " + emptyIfNull(nextRequest.getLastAdminDate()));
      table.append("</dt>");

      table.append("</dl></td>\r\n");

      // COLUMN - Admin comment
      table.append("<td valign='top'>\r\n");
      table.append("<span class=\"mouseComment\">" + emptyIfNull(nextRequest.getAdminComment()) + "</span>");

      table.append("</td>");

      table.append("</tr>\r\n");
      numRequests++;

      nextRequest.prepareForSerialization();
      neededRecords.add(nextRequest.getMouseID());
    }
    ArrayList<MouseRecord> records = DBConnect.getMouseRecordsByIds(neededRecords);
    for (MouseRecord record : records) {
      record.prepareForSerialization();
    }
    table.append("</table>\r\n");
    table.append("</div>\r\n");
    table.append("<script type='text/javascript'>\r\n");
    table.append("MouseConf.addChangeRequests(" + new Gson().toJson(requests) + ")\r\n");
    table.append("MouseConf.addMice(" + new Gson().toJson(records) + ")\r\n");
    table.append("</script>");
    if (numRequests <= 0) {
      return "No results found";
    }

    return table.toString();
  }

  private static String getFacilityTableHeaders(boolean edit) {
    StringBuilder table = new StringBuilder();
    table.append("<tr class='facilitylistH'>\r\n");
    if (edit) {
      table.append("<td>ID</td>");
    }
    table.append("<td class='facilitylistHeaderLeft'\">\r\n");
    table.append("Facility");
    table.append("</td>\r\n");
    table.append("<td class='facilitylistHeaderRight'\">\r\n");
    table.append("Records");
    table.append("</td>\r\n");
    if (edit) {
      table.append("<td class='facilitylistHeaderLeft'\">\r\n");
      table.append("Code (for data uploads)");
      table.append("</td>\r\n");
      table.append("<td class='facilitylistHeaderLeft'\">\r\n");
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
    table.append("<table class='facilityTableInside'><thead>\r\n");
    table.append(getFacilityTableHeaders(edit));
    table.append("</thead><tbody>");
    int numFacilities = 0;

    for (Facility facility : facilities) {

      String rowStyle = getRowStyle(numFacilities, "facilitylist",
              "facilitylistAlt");
      table.append("<tr class='" + rowStyle + "'>\r\n");
      if (edit) {
        table.append("<td>" + facility.getFacilityID() + "</td>");
      }
      table.append("<td class='facilitylistItemLeft'>\r\n");
      table.append("<span class=\"mouseName\">"
              + facility.getFacilityName() + "</span> &nbsp;-&nbsp;"
              + facility.getFacilityDescription());

      table.append("</td><td class='facilitylistItemLeft'>");
      table.append("<span style=\"position:relative;left:5px\">"
              + "<a class='facilityItemLink' href=\""

              + siteRoot
              + "MouseReport.jsp?facility_id="
              + facility.getFacilityID()
              + "\">"
              + facility.getRecordCount() + " records</a></span>\r\n");

      table.append("</td>\r\n");
      if (edit) {
        table.append("<td style='min-width:60px'>" + HTMLGeneration.emptyIfNull(facility.getFacilityCode()) + "</td>");
        table.append("<td style='min-width:60px'><a href=\"EditFacilityForm.jsp?facilityID="
                + facility.getFacilityID() + "\">Edit</a></td>\r\n");
      }
      table.append("</tr>");
      numFacilities++;
    }
    table.append("</tbody></table>\r\n");
    if (numFacilities <= 0)
      return "No results found";
    return table.toString();
  }

  private static String getGeneTableHeaders(boolean edit) {
    StringBuilder table = new StringBuilder();
    table.append("<tr class='facilitylistH'>\r\n");

    table.append("<td class='genelistHeaderLeft'\">\r\n");
    table.append("Gene Details");
    table.append("</td>\r\n");
    table.append("<td class='genelistHeaderRight'\">\r\n");
    table.append("Records");
    table.append("</td>\r\n");
    if (edit) {
      table.append("<td genelistHeaderRight'\">\r\n");
      table.append("Edit");
      table.append("</td>\r\n");
    }
    table.append("</tr>\r\n");

    return table.toString();
  }

  public static String getGeneTable(ArrayList<Gene> genes, boolean edit) {
    StringBuilder table = new StringBuilder();
    table.append("<div class=\"geneTable\">\r\n");
    table.append("<table class='geneTableInside'>\r\n");
    table.append(getGeneTableHeaders(edit));
    int numFacilities = 0;

    for (Gene gene : genes) {

      String rowStyle = getRowStyle(numFacilities, "facilitylist",
              "facilitylistAlt");

      table.append("<tr class='" + rowStyle + "'>\r\n");

      table.append("<td class='genelistItem'>\r\n");
      table.append(formatGene(gene.getSymbol(), gene.getFullname(),
              gene.getMgiID()));

      table.append("</td>\r\n<td class='genelistItem'>\r\n");
      table.append("<span style=\"position:relative;left:5px\">"
              + "<a class='holderItemLink' href=\"" + siteRoot
              + "MouseReport.jsp?&geneID=" + gene.getGeneRecordID()
              + "&orderby=mouse.id&mousetype_id=-1\">"
              + gene.getRecordCount() + " record"
              + (gene.getRecordCount() != 1 ? "s" : "")
              + "</a></span>\r\n");
      table.append("</td>\r\n");
      if (edit) {
        table.append("<td class='genelistItem'><a href=\"EditGeneForm.jsp?geneRecordID="
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
    table.append("<td class='holderHeaderCell'\">\r\n");
    table.append("Holder");
    table.append("</td>\r\n");
    table.append("<td class='holderHeaderCell'\">\r\n");
    table.append("Holder Email");
    table.append("</td>\r\n");
    table.append("<td class='holderHeaderCell'\">\r\n");
    table.append("Primary Contact");
    table.append("</td>\r\n");
    table.append("<td class='holderHeaderCell'\">\r\n");
    table.append("Colony Location");
    table.append("</td>\r\n");
    table.append("<td class='holderHeaderCell'\">\r\n");
    table.append("Review Date");
    table.append("</td>\r\n");
    table.append("<td class='holderHeaderCell'\">\r\n");
    table.append("Rodents Held");
    table.append("</td>\r\n");
    if (edit) {
      table.append("<td class='holderHeaderCell'\">\r\n");
      table.append("Edit");
      table.append("</td>\r\n");
    }
    table.append("</tr>\r\n");

    return table.toString();
  }

  public static String getHolderTable(ArrayList<Holder> holders, boolean edit) {
    StringBuilder table = new StringBuilder();
    table.append("<div class=\"holderTable\">\r\n");
    table.append("<table class='holderTableInside'>\r\n");
    table.append(getHolderTableHeaders(edit));
    int numFacilities = 0;

    for (IHolder holder : holders) {

      String rowStyle = getRowStyle(numFacilities, "holderlist",
              "holderlistAlt");
      if (holder.isDeadbeat()) rowStyle += " deadbeat_holder";
      table.append("<tr class='" + rowStyle + "'>\r\n");
      if (edit) {
        table.append("<td class='holderItemCell'>" + holder.getHolderID() + "</td>");
      }
      table.append("<td class='holderItemCell'>\r\n");

      table.append("<p class='holderName'>"
              + holder.getFullname() + "</p><br>");
      table.append("<p class='holderLocation'\">("
              + holder.getDept() + ")</p>");
      table.append("</td>\r\n");

      //contact information
      table.append("<td class='holderItemCell'>\r\n");

      String emailLink = edit ? getAdminMailLink(holder.getEmail(), null, EmailTemplate.HOLDER, -1, -1, null, holder.getHolderID())
              : formatEmail(holder.getEmail(), holder.getEmail(), "");

      table.append("<p>" + emailLink + "</p>");
      table.append("</td>\r\n");

      //primary contact
      table.append("<td class='holderItemCell'>\r\n");
      table.append(HTMLGeneration.emptyIfNull(holder.getAlternateName()));
      if (holder.getAlternateEmail() != null && !holder.getAlternateEmail().equals("")) {
        emailLink = edit ? getAdminMailLink(holder.getAlternateEmail(), null, EmailTemplate.HOLDER, -1, -1, null, holder.getHolderID())
                : formatEmail(holder.getAlternateEmail(), holder.getAlternateEmail(), "");
        table.append(", " + emailLink);
      }
      table.append("</td>\r\n");

      table.append("<td class='holderItemCell'>" + emptyIfNull(holder.getPrimaryMouseLocation()) + "</td>");

      //review date
      table.append("<td class='holderItemCell'>\r\n");
      if (holder.getDateValidated() != null) {
        table.append(holder.getDateValidated());
        if (holder.getValidationStatus() != null && !holder.getValidationStatus().isEmpty()) {
          table.append("<br>" + holder.getValidationStatus());
        }
      } else if (holder.getValidationStatus() != null && !holder.getValidationStatus().isEmpty()) {
        table.append(holder.getValidationStatus());
      }

      table.append("</td>\r\n");

      table.append("<td class='holderItemCell'>\r\n");
      String href = "";
      if (edit) {
        href = adminRoot + "EditMouseSelection.jsp";
      } else {
        href = siteRoot + "MouseReport.jsp";
      }
      String covertList = "";
      int count = holder.getVisibleMouseCount();
      if (edit && holder.getCovertMouseCount() > 0) {
        covertList = "<br>(" + holder.getCovertMouseCount() + " covert)";
        count += holder.getCovertMouseCount();
      }
      table.append(
              "<a class='holderItemLink' href=\"" + href + "?holder_id=" + holder.getHolderID()
                      + "&mousetype_id=-1\">" + (edit ? "edit " : "") +
                      count + " records</a>" + covertList + "");
      table.append("</td>\r\n");
      if (edit) {
        table.append("<td class='holderItemCell'><a href=\"EditHolderForm.jsp?holderID="
                + holder.getHolderID() + "\">Edit holder #" + holder.getHolderID() + "</a></td>\r\n");
      }
      table.append("</tr>");
      numFacilities++;
    }
    table.append("</table>\r\n");
    if (edit) {
      table.append("<script type='text/javascript'>\r\n");
      table.append("MouseConf.addHolders(" + new Gson().toJson(holders) + ")");
      table.append("</script>");
    }
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
    table.append("Data Upload Results");
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
            + "<dd class=\"mouseSubDetail\"><span class='mgiGeneLink'><span class='lbl'>MGI:</span> "
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
    link.append("<a class='holderItemLink' href=\"" + url + "\" target=\"_blank\">");
    link.append(id);
    link.append("</a>");
    return link.toString();
  }

  public static String formatRGD(String id) {
    if (id == null)
      return "&lt;not available&gt;";
    try {
      Integer.parseInt(id.trim());
    } catch (Exception e) {
      return id;
    }
    String url = "https://rgd.mcw.edu/rgdweb/report/strain/main.html?id="
            + id.trim();
    StringBuffer link = new StringBuffer();
    link.append("<a class='holderItemLink' href=\"" + url + "\" target=\"_blank\">");
    link.append(id);
    link.append("</a>");
    return link.toString();
  }

  public static String formatPubMedID(String value) {
    if (value == null)
      return "&lt;not available&gt;";

    if (value.equalsIgnoreCase("null"))
      return "";
    return "<a class='holderItemLink' href=\"http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;list_uids="
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

    return "<a class='holderItemLink' target=\"_blank\" href='" + fixedUrl + "'>" + label + "</a>";
  }

  public static String formatGensat(String value) {
    if (value == null)
      return "";
    String gensatUrl = "http://www.gensat.org/ShowFounderLineImages.jsp?gensatFounderLine=";
    String gensatUrlTail = "";
    return "<a href='" + gensatUrl + value + gensatUrlTail + "'>" + value + "</a>";
  }

  // To be retired
  @SuppressWarnings("rawtypes")
  public static void formatSearchTerms(StringBuffer buf, String title,
                                       Vector terms, int fontSize) {
    if (terms.size() > 0) {
      buf.append("<tr><td valign=top><font size=" + fontSize + ">"
              + title + "</font></td>");
      buf.append("<td valign=top>");
      for (Enumeration e = terms.elements(); e.hasMoreElements(); ) {
        buf.append("<font size=" + fontSize + ">"
                + (String) e.nextElement() + "<br></font>");
      }
      buf.append("</td></tr>");
    }
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
  public static String genSelect(String name, Object[] values, Object current) {
    return genSelect(name, values, current, "");
  }

  public static String genSelect(String name, Object[] values,
                                 Object current, String selectParams) {
    return genSelect(name, values, (String[]) values, current, selectParams);
  }

  public static String genSelect(String name, Object[] values,
                                 String[] niceNames, Object current, String selectParams) {
    return genSelect(name, values, niceNames, current, selectParams, true, true);
  }

  public static String genSelect(String name, Object[] values,
                                 String[] niceNames, Object current, String selectParams, boolean includeId) {
    return genSelect(name, values, niceNames, current, selectParams, includeId, true);
  }

  public static String genSelect(String name, Object[] values,
                                 String[] niceNames, Object current, String selectParams, boolean includeId, boolean chosenIfLarge) {
    if (selectParams == null) selectParams = "";
    StringBuffer b = new StringBuffer();

    String cssClass = values.length > 20 && chosenIfLarge ? "class='chzn-select'" : "";
    b.append("<select " + cssClass);
    if (includeId) {
      b.append("id='" + name + "' ");
    }
    b.append("name=\"" + name + "\" " + selectParams + ">");
    for (int i = 0; i < values.length; i++) {
      Object value = values[i];

      String niceName = niceNames[i];

      b.append("<option value=\"" + value + "\"");
      if (current != null && value != null
              && value.toString().equalsIgnoreCase(current.toString())) {
        b.append(" selected=selected");
      }
      b.append(">" + niceName + "\n");

    }
    b.append("</select>");
    return b.toString();
  }

  //Added endonuclease-mediated -EW
  /* ********************** Modification Type ******************************** */
  public static String getModificationTypeSelect(String current) {
    String name = "modificationType";
    String[] values = {"Select one", "targeted disruption",
            "conditional allele (loxP/frt)", "gene trap insertion",
            "Chemically induced (ENU)", "spontaneous mutation", "endonuclease-mediated",
            "other (info in comment)"};
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
    String[] values = {"Select one", "Mutant Allele", "Transgene",
            "Inbred Strain"};
    return genSelect(name, values, current, selectParams);
  }

  /* ********************** ExpressedSequence ******************************** */
  public static String getExpressedSequenceSelect(String current) {
    String name = "ExpressedSequence";
    String[] values = {"Select one", "mouse gene", "Cre", "Reporter",
            "Other"};
    return genSelect(name, values, current);
  }

  public static String getExpressedSequenceSelect() {
    return getExpressedSequenceSelect(null);
  }

  /* ********************** TransgenicType ******************************** */
  public static String getTransgenicTypeSelect(String current) {
    String name = "TransgenicType";
    String[] values = {"Select one", "knock-in", "random insertion"};
    return genSelect(name, values, current);
  }

  public static String getTransgenicTypeSelect() {
    return getTransgenicTypeSelect(null);
  }

  public static String genRadio(String name, String[] values, String current) {
    return genRadio(name, values, current, "");
  }

  public static String genYesNoRadio(String name, String current) {
    String[] values = {"Yes", "No"};
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

  public static String genRadio(String name, String[] values,
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
      b.append(">" + niceName + "<br>\n");
    }
    return b.toString();
  }

  //added endonuclease-mediated -EW
  /* ********************** Modification Type ******************************** */
  public static String getModificationTypeRadio(String current) {
    return getModificationTypeRadioWithParams(current, "");
  }

  public static String getModificationTypeRadioWithParams(String current,
                                                          String selectParams) {
    String name = "MAModificationType";
    String[] values = {"targeted disruption",
            "conditional allele (loxP/frt)", "targeted knock-in",
            "gene trap insertion", "Chemically induced (ENU)",
            "spontaneous mutation", "endonuclease-mediated", "other (info in comment)"};
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
    String[] values = {"Mutant Allele", "Transgene", "Inbred Strain"};
    return genRadio(name, values, current, selectParams);
  }

  /* ********************** ExpressedSequence ******************************** */
  public static String getExpressedSequenceRadio(String current) {
    return getExpressedSequenceRadioWithParams(current, "");
  }

  public static String getExpressedSequenceRadioWithParams(String current,
                                                           String selectParams) {

    String name = "TGExpressedSequence";
    String[] values = {"Reporter", "Cre", "Mouse Gene (unmodified)",
            "Modified mouse gene or Other"};
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
    String[] values = {"knock-in", "random insertion"};
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
                                                  int creOnly, int facilityID, boolean species) {
    StringBuffer buf = new StringBuffer();
    buf.append("<div class='mousetype_selection_links'>");
    buf.append("<ul class='label_text' style='columns:2;font-size:16px'>");

    buf.append("<li style='margin-top:0px'>Rodent species: ");
    buf.append(genSelect("species", new String[]{"false", "true",}, new String[]{"Mouse", "Rat"}, species, null));
    buf.append("</li>");

    buf.append("<li>Sort by: ");
    buf.append(genSelect("orderby", new String[]{"mouse.name", "mouse.id", "mouse.id desc"},
            new String[]{"Rodent Name", "Record #", "Record #(reverse)"}, checkedOrderBy, null));
    buf.append("</li>\n");
    buf.append("<li>Category: ");
    String[] mouseTypeIds = new String[mouseTypes.size() + 1];
    String[] mouseTypeNames = new String[mouseTypes.size() + 1];
    int i = 1;
    mouseTypeIds[0] = "-1";
    mouseTypeNames[0] = "All";
    for (MouseType type : mouseTypes) {
      mouseTypeIds[i] = Integer.toString(type.getMouseTypeID());
      mouseTypeNames[i] = type.getTypeName();
      i++;
    }
    buf.append(genSelect("mousetype_id", mouseTypeIds, mouseTypeNames, Integer.toString(checkedMouseTypeID), null));
    buf.append("</li>");

    if (status != null) {
      buf.append("<li>Status: ");
      buf.append(genSelect("status", new String[]{"live", "deleted", "all"}, new String[]{"Live", "Deleted", "All"}, status, null));
      buf.append("</li>\n");
    }
    if (creOnly >= 0) {
      buf.append("<li>Cre-expressing rodents only: <input type='checkbox' name='creonly' value='1' "
              + (creOnly == 1 ? "checked='checked'" : "") + "></li>");
    }
    if (status != null) {
      buf.append("<li class='cf'><input type='text' size='20' id='mousetypeselection_searchterms' name='searchterms'"
              + (searchTerms != null ? "value='" + searchTerms + "'" : "") + ">&nbsp;");
      buf.append("<input class='btn btn-small' type='submit' value='Search'></input></li>");
    }

    if (holderID != -1) {
      buf.append("<input type='hidden' name='holder_id' value='" + holderID + "'>");
    }
    if (geneRecordID != -1) {
      buf.append("<input type='hidden' name='geneID' value='" + geneRecordID + "'>");
    }
    if (facilityID != -1) {
      buf.append("<input type='hidden' name='facility_id' value='" + facilityID + "'>");
    }

    buf.append("</div>");

    return buf.toString();
  }

  public static String getPageSelectionLinks(int limit, int pageNum,
                                             int total, boolean includeLimitSelector) {
    StringBuffer buf = new StringBuffer();

    String[] values = new String[]{"10", "25", "50", "100", "500", "-2"};
    String[] labels = new String[]{"10", "25", "50", "100", "500", "All"};

    String perPageDropDown = genSelect("limit", values, labels, Integer.toString(limit), "style='width:60px'");
    buf.append("<table>");
    if (includeLimitSelector) {
      buf.append("<tr>" + "<td>" + "Records per page: &nbsp;" + perPageDropDown + "</td></tr>");
    }

    if (limit > 0) {
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

  public static String getNewPageSelectionLinks(int limit, int pageNum,
                                                int total, boolean includeLimitSelector) {

    StringBuffer buf = new StringBuffer();

    if (limit == -1) {
      return "";
    }
    buf.append("<div class='pagination-container clearfix' style='padding-top:15px;padding-left:0px'>");

    int pageCount = (total + limit - 1) / limit;
    if (limit == -2) {
      pageCount = 1;
    }
    String[] pageNums = new String[pageCount];
    for (int i = 0; i < pageCount; i++) {
      pageNums[i] = Integer.toString(i + 1);
    }
    String pageSelect = genSelect("pagenum_select", pageNums, pageNums, Integer.toString(pageNum), "style='vertical-align: 0%'", false, false);

    buf.append(((pageNum <= 1)
            ? "<div class='MSU_green_button_next_previous_disabled'><p class='MSU_green_button_next_previous_text'>Previous</p>"
            : "<div class='MSU_green_button_next_previous'><a href='#' data-pagenum='" + (pageNum - 1) + "'><p class='MSU_green_button_next_previous_text'>Previous</p></a>" ) +
            "</div>\r\n");
    buf.append(((pageNum >= pageCount)
            ? "<div class='MSU_green_button_next_previous_disabled'><p class='MSU_green_button_next_previous_text'>Next</p>"
            : "<div class='MSU_green_button_next_previous'><a href='#' data-pagenum='" + (pageNum + 1) + "'><p class='MSU_green_button_next_previous_text'>Next</p></a>" ) +
            "</div>\r\n");
    buf.append("<div><span class='well' style='vertical-align:middle;line-height:35px;font-size:16px;padding-top:2px'>Page " + pageSelect + " of " + pageCount + "</span></div>\r\n");

    if (includeLimitSelector) {

      String[] values = new String[]{"10", "25", "50", "100", "500", "-2"};
      String[] labels = new String[]{"10", "25", "50", "100", "500", "All"};

      String perPageDropDown = genSelect("limit", values, labels, Integer.toString(limit), "style='width:60px;font-size: 16px;'", false, false);
      buf.append("<div><span style='vertical-align:middle;font-size: 16px;line-height:35px;color:black'>&nbsp;&nbsp;" + perPageDropDown + " <span>per page</span></span></div>");
    }

    buf.append("</div>");

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

  public static boolean stringToBoolean(String str) {
    try {
      return Boolean.parseBoolean(str);
    } catch (Exception e) {
      return false;
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

  public static String tInput(String name, String current) {
    return "<input type='text' name='" + name + (current != null ? "' value=\"" + current + "\"" : "'") + " />";
  }

  public static String tArea(String name, String current) {
    return "<textarea name='" + name + "'>" + (current != null ? current : "") + "</textarea>";
  }

  public static String formatEmail(String emailAddress, String linkText, String subject) {
    return formatEmail(emailAddress, null, linkText, subject);
  }

  public static String formatEmail(String emailAddress, String ccAddress, String linkText, String subject) {
    return getMailToLink(emailAddress, ccAddress, subject, null, linkText);
  }


  public static String getMailToLink(String address, String cc, String subject, String body, String linkText) {
    return getMailToLink(address, cc, subject, body, linkText, null, false, null);
  }

  public static String getMailToLink(String address, String cc, String subject, String body, String linkText, String linkTitle) {
    return getMailToLink(address, cc, subject, body, linkText, linkTitle, false, null);
  }

  public static String getAdminMailLink(String address, String cc, String templateCategory, int subId, int requestId, String mouseId, int holderId) {
    return getAdminMailLink(address, cc, templateCategory, subId, requestId, mouseId, holderId, -1);
  }

  public static String getAdminMailLink(String address, String cc, String templateCategory, int subId, int requestId, String mouseId, int holderId, int submitterIndex) {
    return getAdminMailLink(address, cc, templateCategory, null, null, subId, requestId, mouseId, holderId, submitterIndex);
  }

  public static String getAdminMailLink(String address, String cc, String templateCategory, String linkText, String linkTitle, int subId, int requestId,
                                        String mouseId, int holderId) {
    return getAdminMailLink(address, cc, templateCategory, linkText, linkTitle, subId, requestId, mouseId, holderId, -1);

  }

  public static String getAdminMailLink(String address, String cc, String templateCategory, String linkText, String linkTitle, int subId, int requestId,
                                        String mouseId, int holderId, int submitterIndex) {

    Properties props = new Properties();
    if (subId > 0) {
      props.setProperty("submission_id", Integer.toString(subId));
    }
    if (requestId > 0) {
      props.setProperty("request_id", Integer.toString(requestId));
    }
    if (mouseId != null) {
      props.setProperty("mouse_id", mouseId);
    }
    if (holderId > 0) {
      props.setProperty("holder_id", Integer.toString(holderId));
    }
    if (submitterIndex >= 0) {
      props.setProperty("submitter_index", Integer.toString(submitterIndex));
    }
    props.setProperty("category", templateCategory);
    return getMailToLink(address, cc, null, null, linkText == null ? address : linkText, linkTitle, true, props);
  }

  private static String getMailToLink(String address, String cc, String subject, String body, String linkText, String linkTitle, boolean admin, Properties props) {
    if (address == null || address.isEmpty()) {
      address = cc;
      cc = null;
    }

    String recipient = address;
    String ccRecipient = null;
    String ccAddr = "?";
    if (cc != null && !cc.isEmpty() && !cc.equals(address)) {
      ccRecipient = cc;
      ccAddr = "?cc=" + cc + "&";
    }
    if (cc != null && !cc.isEmpty() && address == null) {
      address = cc;
      ccAddr = "?";
      recipient = cc;
    }
    if (admin) {
      String link = "<a class='holderItemLink' href='#'";
      link += " data-recipient='" + recipient + "'";
      if (ccRecipient != null) {
        link += " data-cc='" + ccRecipient + "'";
      }
      if (props != null) {
        for (String key : props.stringPropertyNames()) {
          link += " data-" + key + "='" + props.getProperty(key) + "'";
        }
      }
      if (linkTitle != null) {
        link += " title='" + linkTitle + "'";
      }
      link += ">" + linkText + "</a>";
      return link;
    } else {
      return "<a class='holderItemLink'" + (linkTitle != null ? " title='" + linkTitle + "'" : "") +
              " href=\"mailto:" + address + ccAddr +
              ((subject != null && !subject.isEmpty()) ? "subject=" + urlEncode(subject) : "") +
              (body != null ? "&body=" + urlEncode(body) : "") + "\">" + linkText + "</a>";
    }
  }

  public static String dataEncode(String text) {
    if (text == null || text.isEmpty()) {
      return text;
    }
    return text.replace("'", "&quot;");
  }

  public static String urlEncode(String text) {

    try {
      return URLEncoder.encode(text, "ISO-8859-1").replace("+", "%20");
    } catch (UnsupportedEncodingException e) {
      Log.Error("Failed to encode text with ISO-8859-1 encoding", e);
      return "failed to encode";
    }
  }

  //part of old Email Template Form improvemtents commit, is it used? -EW
  public static boolean isAdminUser(HttpServletRequest request) {
    return request.isUserInRole("administrator");
  }

  public static String getHolderSelect(String name, int currentHolderId) {
    return getHolderSelect(name, currentHolderId, true);
  }

  public static String getHolderSelect(String name, int currentHolderId, boolean includeOther) {
    ArrayList<Holder> holders = DBConnect.getAllHolders(false);

    String[] holderNames = new String[holders.size() + 2];
    Integer[] holderIds = new Integer[holderNames.length];

    int i = 0;
    holderNames[i] = "Choose one";
    holderIds[i] = -1;
    i++;
    for (Holder holder : holders) {
      holderNames[i] = holder.getLastname() + ", " + holder.getFirstname();
      holderIds[i] = holder.getHolderID();
      i++;
    }
    holderNames[i] = "Other(specify)";
    holderIds[i] = -2;
    i++;
    return genSelect(name, holderIds, holderNames, currentHolderId, null);
  }

  public static String getFacilitySelect(String name, int currentFacilityId) {
    ArrayList<Facility> facilities = DBConnect.getAllFacilities(false);

    String[] facilityNames = new String[facilities.size() + 2];
    Integer[] facilityIds = new Integer[facilityNames.length];

    int i = 0;
    facilityNames[i] = "Choose one";
    facilityIds[i] = -1;
    i++;
    for (Facility facility : facilities) {
      facilityNames[i] = facility.getFacilityName();
      facilityIds[i] = facility.getFacilityID();
      i++;
    }
    facilityNames[i] = "Other(specify)";
    facilityIds[i] = -2;
    i++;
    return genSelect(name, facilityIds, facilityNames, currentFacilityId, null);
  }

  /**Generates the footer for the MSU Rodent Database.<br>
   * Change version of database, year, and email inside this function.*/
  public static String getWebsiteFooter() {

    //Database version
    String version = "4.0";
    //Current date
    String year = "2021";
    //Email of database administrator
    String email = "ORA.MSURodentDatabase@msu.edu";

    //written HTML for the footer (includes a spacing div at the very beginning of height 100px.
    String footer_HTML = "<div class=\"spacing_div\"></div>" + "<div class=\"MSU_footer\">\n" +
            "\n" +
            " <div class=\"category\">\n" +
            "  <div class=\"two_column_left\">\n" +
            "   <img src=\"/img/MSU_wordmark.svg\" style=\"height: 80px;float: left;margin-top: 8px;margin-left: 5px;\n\">\n" +
            "  </div>\n" +
            "  <div class=\"two_column_right\">\n" +
            "   <p class=\"MSU_footer_text\">\n" +
            "    MSU Rodent Database " + year + " Version " + version + "<br>\n" +
            "    Developed by University of California, San Francisco (UCSF)<br>\n" +
            "\n" +
            "    <a class=\"anchor_no_underline\" href=\"mailto:" + email + "\" style=\"text-decoration: underline\">Contact Administrator</a><br>\n" +
            "    <a class=\"anchor_no_underline\" href=\"https://github.com/musIndex/mouseinventory\" style=\"text-decoration: underline\">View in GitHub</a><br>\n" +
            "   </p>\n" +
            "  </div>\n" +
            " </div>\n" +
            "\n" +
            "</div>";

    return footer_HTML;
  }

}