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
<%@include file="mouselistcommon.jspf" %>
<%=getNavBar("MouseReport.jsp", false) %>


<%
    int holderReviewDays = 180;

    int holderID = stringToInt(request.getParameter("holder_id"));
    int geneID = stringToInt(request.getParameter("geneID"));
    int mouseTypeID = stringToInt(request.getParameter("mousetype_id"));
    int creOnly = stringToInt(request.getParameter("creonly"));
    int facilityID = stringToInt(request.getParameter("facility_id"));
    int pagenum = stringToInt(request.getParameter("pagenum"));
    int limit = stringToInt(request.getParameter("limit"));

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

    String queryString = StringUtils.join(query, "&");

    int mouseCount = DBConnect.countMouseRecords(mouseTypeID, orderBy, holderID, geneID, "live", null, false, creOnly, facilityID);
    ArrayList<MouseRecord> mice = DBConnect.getMouseRecords(mouseTypeID, orderBy, holderID, geneID, "live", null, false, creOnly, facilityID,limit,offset);

    String table = getMouseTable(mice, false, true, false);
    ArrayList<MouseType> mouseTypes = DBConnect.getMouseTypes();
    String mouseTypeSelectionLinks = getMouseTypeSelectionLinks(
            mouseTypeID, orderBy,holderID,geneID,mouseTypes,null,searchTerms,creOnly,facilityID);

    String topPageSelectionLinks = getNewPageSelectionLinks(limit,pagenum,mouseCount,true);
    String bottomPageSelectionLinks = getNewPageSelectionLinks(limit,pagenum,mouseCount,false);

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
    String holderData = "<p style='color: blue; font-weight: bold;''>" +
            "<i>To obtain a list of all rodents held by an investigator, go to the 'Holder List'."+
            "</i></p>";
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
            holderStatusHeading = "Please take this opportunity to update the list.";
        }

        String emailAdminLink = getMailToLink(DBConnect.loadSetting("admin_info_email").value, null, holder.getLastname() + " rodent list reviewed",
                "The list of rodents held by "+ holder.getFullname() + " " +
                        "was thoroughly reviewed and any necessary deletions/additions/corrections " +
                        "were made today" +
                        "\n(If rodents still need to be added, please provide a list of " +
                        "their names below.)", "email link");

        holderData = "<div class='holderData'>" +
                "<span class='boldheading "+holderStatusHeadingStyle + "'>" + holderStatusHeading + "<br>" +
                "Use this " +  emailAdminLink +
                " to notify admin when update is complete</span>" +
                "<div class='holderNotice'>" +
                "<b>To delete a rodent</b> that is no longer being maintained " +
                "but is still listed here, go to the record for that rodent, " +
                "click on 'request change in record,' and follow the instructions " +
                "for deleting a holder.<br><br>" +
                "If any corrections need to be made, click on 'request change in record,'" +
                "and follow the instructions for entering info about the changes.'" +
                "(For example, is an 'unpublished' allele/transgene now published?" +
                "Is there genetic background information that could be included?)" +
                "<br><br>" +
                "<b>To add a rodent</b> that is being maintained by this holder but " +
                "is not yet listed:"+
                "<ul>"+
                "<li>First do a quick search of the database "+
                "(top right of the screen) to see if there is already a record for that rodent."+
                "</li>"+
                "<li>"+
                "If so, click on 'request change in record,' and follow the instructions for adding a holder." +
                "</li>"+
                "<li>"+
                "If there is no record for that rodent in the database, complete a new submission for it or " +
                "contact database admin to request assistance in entering it." +
                "</li>" +
                "</ul>" +
                "</div>" +
                "\r\n<a class='btn btn-primary' style='' href='" + siteRoot + "MouseList" + (queryString.length() > 0 ? "?" + queryString : "") +
                "'>Download this list (pdf)</a>" +
                "</div>" +
                "<div>" +
                "\r\n<a class='btn btn-primary' style='' href='" + siteRoot + "MouseList2" + (queryString.length() > 0 ? "?" + queryString : "") +
                "'>Download this list (csv)</a>" +
                "</div>"

        ;

        topPageSelectionLinks += "";

    }

    session.setAttribute("mouseListLastQuery", "MouseReport.jsp?" + queryString);
    session.setAttribute("mouseListLastTitle", mouseTypeStr);
    mouseTypeStr = "Listing" + mouseTypeStr;
%>


<script id="access_granted" type="text/template">
    <div class='site_container'>
        <div id="mousecount" style="display:none"><%=mice.size() %>
        </div>
        <table>
            <tr>
                <td style="width: 50%;vertical-align: bottom">
                    <h2><%=mouseTypeStr %></h2>
                    <form class='view_opts' action="MouseReport.jsp" >
                        <div class='clearfix' style='position:relative;min-height:140px'>
                            <div id="controls" style='position:absolute;bottom:0;left:0;'>
                                <h4 style='margin-top:0px'><%=mouseCountStr %></h4>
                                <%= mouseTypeSelectionLinks %>
                                <% if (mice.size() > 0) { %>
                                <%= topPageSelectionLinks %>
                                <% } %>
                            </div>
                            <div style='margin-left:550px'>
                                <%=emptyIfNull(holderData) %>
                            </div>
                        </div>
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
                                        <input type="hidden" name="page" value="GeneReport.jsp">
                                        <input type="submit" class = "btn btn-primary" value="Search">

<%--                                    <br>--%>
<%--                                    <br>--%>
<%--                                    <b>Search examples:</b>--%>
<%--                                    <br>--%>
<%--                                    <i>shh null</i><br>--%>
<%--                                    Match records that contain both 'shh' <b>and</b> 'null'<br>--%>
<%--                                    <i>htr</i><br>--%>
<%--                                    Match words that start with htr, such as htr2c, or htr1a<br>--%>
<%--                                    <i>htr2c</i><br>--%>
<%--                                    Find the specific gene 'htr2c'<br>--%>
<%--                                    <i>1346833</i><br>--%>
<%--                                    Look up MGI ID 1346833<br>--%>
<%--                                    <i>12590258</i><br>--%>
<%--                                    Look up Pubmed ID 1346833<br>--%>
<%--                                    <i>#101,#103</i><br>--%>
<%--                                    Show record numbers 101 and 103<br>--%>
                                </div>

                            </div>
                    </table>
                    </form>
                </td>
            </tr>
            <tr>
                <form>
                    <%= table %>
                    <div id="bottomControls" class="site_container">
                        <% if (mice.size() > 3) { %>
                        <%= bottomPageSelectionLinks %>
                        <% } %>
                    </div>
                </form>
            </tr>
        </table>
    </div>
</script>

<script id="access_denied" type="text/template">
    <div>
        <table class="site_container">
            <tr>
                <td style="width: 50%">
                    <h2>Rodent Records Login</h2>
                    Welcome to the Rodent Research Database Application's Rodent Records.<br>
                    Before you're able to view rodent records, ensure that
                    you have filled out the database application.<br>
                    If your application has been approved, please enter your information
                    below.<br><br>
                    <form method="post" action="loginServlet">
                        <table>

                            <tr>
                                <td><label for="email">Email address:</label></td>
                                <td><input type="text" id="email" name="email" required></td>
                            </tr>
                            <tr>
                                <td><label for="MSU NetID">MSU NetID:</label></td>
                                <td><input type="text" id="MSU NetID" name="MSU NetID" required></td>
                            </tr>
                            <tr>
                                <td>
                                    <input type = hidden name="page" value="applicationLoginRecords.jsp">
                                    <input type="submit" class ="button btn-primary" value="Login">
                                </td>
                            </tr>

                        </table>
                    </form>
                </td>
                <td style="vertical-align: top;width: 50%">
                    <h2>Application Information</h2>
                    In order to access the Rodent Records and submit
                    rodents to the database, you must first fill out an application.
                    <br>
                    This application can be found by following the button below, or
                    clicking on the "Database Application" tab in the navigation bar.
                    <br>
                    <br>
                    <a href="application.jsp"><button class = "btn btn-success">Database Appication</button></a>
                </td>
            </tr>
        </table>

    </div>
</script>

<div id="page_content">

</div>

<script>
    var access_status = <%=LoginServlet.getAccess_granted()%>;
    var granted = document.getElementById("access_granted").innerHTML;
    var denied = document.getElementById("access_denied").innerHTML;

    if (access_status == 1) {
        document.getElementById("page_content").innerHTML = granted;
    } else {
        document.getElementById("page_content").innerHTML = denied;
    }
    <%LoginServlet.setAccess_granted(0);%>;
</script>

<script>
    function setLink() {
        var keyword = document.getElementById("search_terms").value;
        var begin = "search.jsp#searchterms=";
        var end ="&pagenum=1&search-source=search";
        document.getElementById('search_button').setAttribute("href",begin+keyword+end);
    }
</script>