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

//        String emailAdminLink = getMailToLink(DBConnect.loadSetting("admin_info_email").value, null, holder.getLastname() + " rodent list reviewed",
//                "The list of rodents held by "+ holder.getFullname() + " " +
//                        "was thoroughly reviewed and any necessary deletions/additions/corrections " +
//                        "were made today" +
//                        "\n(If rodents still need to be added, please provide a list of " +
//                        "their names below.)", "email link");

        holderData = "<div class='holderData'>" +
                "<div>" +
                "<br>To update a rodent's details, add or remove yourself as a holder, click <b>request change in record.</b><br>"+
                "</div>" +
                "<br>\r\n<a class='btn btn-primary' style='' href='" + siteRoot + "MouseList" + (queryString.length() > 0 ? "?" + queryString : "") +
                "'>Download this list (pdf)</a>"
                + "\r\n<a class='btn btn-primary' style='' href='" + siteRoot + "MouseList2" + (queryString.length() > 0 ? "?" + queryString : "") +
                "'>Download this list (csv)</a>" +
                "</div>";
    }
    session.setAttribute("mouseListLastQuery", "MouseReport.jsp?" + queryString);
    session.setAttribute("mouseListLastTitle", mouseTypeStr);
    mouseTypeStr = "Listing" + mouseTypeStr;
%>

<div class='site_container'>
    <div id="mousecount" style="display:none">
        <%=mice.size() %>
    </div>
    <form class='view_opts' action="MouseReport.jsp">
    <table style="width: 100%">

            <tr>
                <td style="padding-left: 0px">
                    <p class="records_header">Rodent Records</p>
                    <table style="width: 100%">
                        <tr>
                            <p class="label_text" style='margin-top:0px;margin-bottom:5px;padding-left: 2px'><%=mouseTypeStr + ": " + mouseCountStr %></p>

                        </tr>
                        <tr>
                            <td style="width: 55%;padding: 0px">
                                <div class='clearfix' style='position:relative;'>
                                    <div id="controls">
                                        <%= mouseTypeSelectionLinks %>
                                        <% if (mice.size() > 0) { %>

                                        <% } %>
                                    </div>
                                </div>
                                <input type = hidden name="page" value="records_search">
                            </td>
                            <td style="width: 45%;vertical-align: top">
                                <div class="search_right">
                                    <input type="search" placeholder="Search..." style='font-size:120%;vertical-align:top;margin-top: 0px' class="input-xlarge" name="search_terms" id="search_terms"></input>
                                    <input onclick="return searchterm()" type="image" alt="Submit" src=/img/Eyeglass-black.svg style="height: 28px;margin: 0px">
                                    <input type="hidden" name="page" value="search_bar">
                                </div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <table style="min-width: 100%;">
                <tr style="width: 100%">
                    <td style="width: 100%">
                        <%= table %>
                        <div id="bottomControls">
                            <%--                            <% if (mice.size() > 3) { %>--%>
                            <%= bottomPageSelectionLinks %>
                            <%--                            <% } %>--%>
                        </div>
                        <input type = hidden name="page" value="records_search">
                    </td>
                </tr>
            </table>
    </table>
    </form>

</div>

</div> <!-- This end div is here to end the site container div. For some reason it's not picked up by intellisense, but it is necessary. -->

<%=HTMLGeneration.getWebsiteFooter()%>


<script>
    function pageSwitch(num){
        document.getElementById("pagenum").value = num;
        this.form.submit();
    }

    function searchterm(){
        if ((search_terms.value != null || search_terms.value != "")){
            document.getElementById("pagenum").value = 1;
            document.getElementById("limit").value = 10;
            location.replace("search.jsp#searchterms=" + search_terms.value + "&pagenum=1&search-source=search");
            return false;
        }
    }
    function resetPage(){
        document.getElementById("pagenum").value = 1;
        this.form.submit();
    }
</script>