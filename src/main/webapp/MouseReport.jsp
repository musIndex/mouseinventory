<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.util.concurrent.TimeUnit"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>
<%@ page import="edu.ucsf.mousedatabase.servlets.LoginServlet" %>
<%=getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar("MouseReport.jsp", false)%>
<%@include file="mouselistcommon.jspf" %>


<%
    int holderReviewDays = 180;

    int holderID = stringToInt(request.getParameter("holder_id"));
    int geneID = stringToInt(request.getParameter("geneID"));
    int mouseTypeID = stringToInt(request.getParameter("mousetype_id"));
    int creOnly = stringToInt(request.getParameter("creonly"));
    int facilityID = stringToInt(request.getParameter("facility_id"));
    int pagenum = stringToInt(request.getParameter("pagenum"));
    int limit = stringToInt(request.getParameter("limit"));
    boolean species = stringToBoolean(request.getParameter("species"));

    String orderBy = request.getParameter("orderby");

    if (orderBy == null || orderBy.isEmpty())
    {
        orderBy = "mouse.name";
    }

    String searchTerms = null;

    if(creOnly < 0) {
        creOnly = 0;
    }

    if (limit == -1) {
        if (session.getAttribute("limit") != null) {
            limit = Integer.parseInt(session.getAttribute("limit").toString());
        }
        else {
            limit = 25;
        }
    }
    session.setAttribute("limit",limit);
    if (pagenum == -1) {
        pagenum = 1;
    }
    int offset = limit * (pagenum - 1);

    ArrayList<String> query = new ArrayList<String>();
    query.add("holder_id=" + holderID);
    query.add("orderby=" + orderBy);
    query.add("geneID=" + geneID);
    query.add("creonly=" + creOnly);
    query.add("mousetype_id=" + mouseTypeID);
    query.add("facility_id=" + facilityID);
    query.add("status=live");
    if (species)
        query.add("is_rat=" + 1);
    else
        query.add("is_rat=" + 0);


    String queryString = StringUtils.join(query, "&");

    int mouseCount = DBConnect.countMouseRecords(mouseTypeID, orderBy, holderID, geneID, "live", null, false, creOnly, facilityID,false, species);
    ArrayList<MouseRecord> mice = DBConnect.getMouseRecords(mouseTypeID, orderBy, holderID, geneID, "live", null, false, creOnly, facilityID,limit,offset,false,species);

    String table = getMouseTable(mice, false, true, false);
    ArrayList<MouseType> mouseTypes = DBConnect.getMouseTypes();
    String mouseTypeSelectionLinks = getMouseTypeSelectionLinks(
            mouseTypeID, orderBy,holderID,geneID,mouseTypes,null,searchTerms,creOnly,facilityID,species);

    String topPageSelectionLinks = getNewPageSelectionLinks(limit,pagenum,mouseCount,true);
    String bottomPageSelectionLinks = getNewPageSelectionLinks(limit,pagenum,mouseCount,true);


    Holder holder = DBConnect.getHolder(holderID);
    Gene gene = DBConnect.getGene(geneID);
    Facility facility = DBConnect.getFacility(facilityID);
    String mouseTypeStr = "";
    String mouseCountStr = "";
    if(mouseTypeID != -1)  {
        for(MouseType type : mouseTypes) {
            if(type.getMouseTypeID() == mouseTypeID) {
                mouseTypeStr += " " + type.getTypeName();
                break;
            }
        }
    }
    else {
        mouseTypeStr += " all";
    }

    if(mice.size() > 0) {
        mouseCountStr = mouseCount + " found";
        if (limit > 0 && mice.size() == limit)
            mouseCountStr += " (" + limit + " shown per page)";
    }
    String holderData = "<p>" +
            "<i><b>To obtain a list of all rodents held by an investigator, go to the 'Holder List'."+
            "</b></i></p>";
    mouseTypeStr += " records";

    if (facility != null) {
        mouseTypeStr += " in facility " + facility.getFacilityName();
    }

    if (holder != null) {
        mouseTypeStr += " held by " + holder.getFullname();
    }
    else if(gene != null){
        mouseTypeStr += " with gene <span class=\"geneSymbol\">" + gene.getSymbol() + "</span> - <span class=\"geneName\">" + gene.getFullname() + "</span>";
    }

    if(creOnly > 0) {
        mouseTypeStr += " with expressed sequence type CRE";
    }
    if (holder != null) {
        String holderStatusHeading = "";
        String holderStatusHeadingStyle = "red";
        if (holder.getDateValidated() != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date validationDate = dateFormat.parse(holder.getDateValidated());
            long duration = System.currentTimeMillis() - validationDate.getTime();
            long days = TimeUnit.MILLISECONDS.toDays(duration);

            holderStatusHeading = "It has been " + days + " day" + (days != 1 ? "s" : "")
                    + " since this list was thoroughly reviewed.";
            holderStatusHeadingStyle = "";

            mouseTypeStr += " (Last reviewed: " + holder.getDateValidated() + ")";
        }
        else {
            holderStatusHeading = "";
        }

        String emailAdminLink = getMailToLink(DBConnect.loadSetting("admin_info_email").value, null, holder.getLastname() + " rodent list reviewed",
                "The list of rodents held by "+ holder.getFullname() + " " +
                        "was thoroughly reviewed and any necessary deletions/additions/corrections " +
                        "were made today" +
                        "\n(If rodents still need to be added, please provide a list of " +
                        "their names below.)", "email link");

        holderData = "<div class='holderData'>" +
                "<div>" +
                "<br>To update a rodent's details, add or remove yourself as a holder, click <b>request change in record.</b><br>"+
                "</div>" +
                "<br>\r\n<a class='btn btn-primary' style='' href='" + siteRoot + "MouseList" + (queryString.length() > 0 ? "?" + queryString : "") +
                "'>Download this list (pdf)</a>"
                 + "\r\n<a class='btn btn-primary' style='' href='" + siteRoot + "MouseList2" + (queryString.length() > 0 ? "?" + queryString : "") +
                "'>Download this list (csv)</a>" +
                "</div>"

        ;

        topPageSelectionLinks += "";

    }


    session.setAttribute("mouseListLastQuery", "MouseReport.jsp?" + queryString);
    session.setAttribute("mouseListLastTitle", mouseTypeStr);
    mouseTypeStr = "Listing" + mouseTypeStr;
%>


<%--//<script id="access_granted" type="text/template">--%>
    <div class='site_container'>
        <div id="mousecount" style="display:none">
            <%=mice.size() %>

        </div>
        <table>
            <tr>
                <td style="width: 50%;vertical-align: bottom">
                    <h2><%=mouseTypeStr %></h2>
                    <form class='view_opts' action="MouseReport.jsp" >

                        <div class='clearfix' style='position:relative;min-height:140px'>
                            <div id="controls">
                                <h4 style='margin-top:0px'><%=mouseCountStr %></h4>
                                <%= mouseTypeSelectionLinks %>
                                <% if (mice.size() > 0) { %>
                                <%= topPageSelectionLinks %>
                                <% } %>
                            </div>
                        </div>
                        <input type = hidden name="page" value="records_search">

                    </form>
                </td>
                <td style="width:50%; text-align: center;vertical-align: top">
                    <form method="post" action="loginServlet" style="horiz-align: center">

                        <table style="text-align: center">
                            <tr style="text-align: center">
                                <div class="flexBox">
                                    <div class="centered">
                                        <h2>Database Search:</h2>
                                        <input type="text" name="search_terms" id="search_terms">
                                        <input type="hidden" name="page" value="search_bar">
                                        <input type="submit" class = "btn btn-primary" value="Search">
                                        <%=emptyIfNull(holderData) %>
                                    </div>

                                </div>
                        </table>
                    </form>
                </td>
            </tr>
            <table>
                <tr style="width: 100%">
                    <td style="width: 100%">
                        <form class='view_opts' action="loginServlet" >
                            <%= table %>
                            <div id="bottomControls">
                                <% if (mice.size() > 3) { %>
                                <%= mouseTypeSelectionLinks %>
                                <%= bottomPageSelectionLinks %>
                                <% } %>
                            </div>
                            <input type = hidden name="page" value="records_search">
                        </form>
                    </td>
                </tr>
            </table>

        </table>
    </div>
<%--//</script>--%>

<%--<script id="access_denied" type="text/template">--%>
<%--    <div>--%>
<%--        <table class="site_container">--%>
<%--            <tr>--%>
<%--                <td style="width: 50%">--%>
<%--                    <h2>Rodent Records Login</h2>--%>
<%--                    Welcome to the Rodent Research Database Application's Rodent Records.<br>--%>
<%--                    Before you're able to view rodent records, ensure that--%>
<%--                    you have filled out a registration form.<br>--%>
<%--                    If your registration form has been approved, please enter your information--%>
<%--                    below.<br><br>--%>
<%--                    <form method="post" action="loginServlet">--%>
<%--                        <table>--%>

<%--                            <tr>--%>
<%--                                <td><label for="email">Email address:</label></td>--%>
<%--                                <td><input type="text" id="email" name="email" required></td>--%>
<%--                            </tr>--%>
<%--                            <tr>--%>
<%--                                <td><label for="MSU NetID">MSU NetID:</label></td>--%>
<%--                                <td><input type="text" id="MSU NetID" name="MSU NetID" required></td>--%>
<%--                            </tr>--%>
<%--                            <tr>--%>
<%--                                <td>--%>
<%--                                    <input type = hidden name="page" value="applicationLoginRecords.jsp">--%>
<%--                                    <input type="submit" class ="button btn-primary" value="Login">--%>
<%--                                </td>--%>
<%--                            </tr>--%>

<%--                        </table>--%>
<%--                    </form>--%>
<%--                </td>--%>
<%--                <td style="vertical-align: top;width: 50%">--%>
<%--                    <h2>Registration Information</h2>--%>
<%--                    In order to access the Rodent Records and submit--%>
<%--                    rodents to the database, you must first fill out a registration form.--%>
<%--                    <br>--%>
<%--                    Registration can be found by following the button below, or--%>
<%--                    clicking on the "Registration" tab in the navigation bar.--%>
<%--                    <br>--%>
<%--                    <br>--%>
<%--                    <a href="application.jsp"><button class = "btn btn-success">Registration</button></a>--%>
<%--                </td>--%>
<%--            </tr>--%>
<%--        </table>--%>

<%--    </div>--%>
<%--</script>--%>

<%--<div id="page_content">--%>

<%--</div>--%>

<%--<script>--%>
<%--    var access_status = <%=LoginServlet.getAccess_granted()%>;--%>
<%--    var granted = document.getElementById("access_granted").innerHTML;--%>
<%--    var denied = document.getElementById("access_denied").innerHTML;--%>

<%--    if (access_status == 1) {--%>
<%--        document.getElementById("page_content").innerHTML = granted;--%>
<%--    } else {--%>
<%--        document.getElementById("page_content").innerHTML = denied;--%>
<%--    }--%>
<%--    <%LoginServlet.setAccess_granted(0);%>;--%>
<%--</script>--%>

<%--<script type="text/javascript">--%>
<%--    function submitformsubmitrodents() {   document.submitrodents.submit();}--%>
<%--    function submitformmouseregister() {   document.mouseregister.submit();}--%>
<%--    function submitformrodentrecords() {   document.rodentrecords.submit();}--%>
<%--    function submitformgenelist() {   document.genelist.submit();}--%>
<%--    function submitformabout() {   document.about.submit();}--%>
<%--    function submitformfacilitylist() {   document.facilitylist.submit();}--%>
<%--    function submitformholderlist() {   document.holderlist.submit();}--%>
<%--    function submitformhome(){document.home.submit();}--%>
<%--    function submitformfeedback(){document.submitfeedback.submit();}--%>
<%--    function submitlogout(){document.logout.submit();}--%>
<%--</script>--%>