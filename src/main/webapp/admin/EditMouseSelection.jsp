<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.microsoft.applicationinsights.core.dependencies.apachecommons.lang3.StringUtils" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%@include file="../mouselistcommon.jspf" %>
<%=HTMLGeneration.getNavBar("EditMouseSelection.jsp", true) %>
<%@include file="SendMailForm.jspf" %>
<script type="text/javascript" src="<%=scriptRoot%>jquery.highlight.js" ></script>

<%

  int holderID = stringToInt(request.getParameter("holder_id"));
  int geneID = stringToInt(request.getParameter("geneID"));
  int mouseTypeID = stringToInt(request.getParameter("mousetype_id"));

  String searchTerms = request.getParameter("searchterms");
  String orderBy = request.getParameter("orderby");
  String status = request.getParameter("status");
  boolean species = stringToBoolean(request.getParameter("species"));


  int creOnly = stringToInt(request.getParameter("creonly"));
  int facilityID = stringToInt(request.getParameter("facility_id"));
  int pagenum = stringToInt(request.getParameter("pagenum"));
  int limit = stringToInt(request.getParameter("limit"));


  if (limit == -1) {
    if (session.getAttribute("limit") != null) {
      limit = Integer.parseInt(session.getAttribute("limit").toString());
    }
    else {
      limit = 10;
    }
  }
  session.setAttribute("limit",limit);
  if (pagenum == -1) {
    pagenum = 1;
  }
  int offset = limit * (pagenum - 1);
  if(status == null) {
    if ((status = (String)session.getAttribute("editMiceStatus")) == null) {
      status = "all";
    }
  }
  session.setAttribute("editMiceStatus",status);
  if(orderBy == null) {
    orderBy = (String)session.getAttribute("editMiceOrderBy");
  }
  else {
    session.setAttribute("editMiceOrderBy",orderBy);
  }
  
  if (searchTerms == null) {
    searchTerms = ""; 
  }
  
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


  int mouseCount = DBConnect.countMouseRecords(mouseTypeID, orderBy, holderID, geneID, "live", searchTerms, false, creOnly, facilityID,false, species);
  ArrayList<MouseRecord> mice = DBConnect.getMouseRecords(mouseTypeID, orderBy, holderID, geneID, "live", searchTerms, false, creOnly, facilityID,limit,offset,false,species);

  String table = HTMLGeneration.getMouseTable(mice, true, false, false); 

  //String table = generateMouseList(mouseTypeID, null, (orderBy== null || orderBy.equals("mouse.id")) ? "mouse.id desc" : orderBy, true);

  ArrayList<MouseType> mouseTypes = DBConnect.getMouseTypes();

  String mouseTypeSelectionLinks = HTMLGeneration.getMouseTypeSelectionLinks(mouseTypeID, orderBy,holderID,geneID, mouseTypes, status,searchTerms,-1,facilityID,species);
  String topPageSelectionLinks = HTMLGeneration.getNewPageSelectionLinks(limit,pagenum,mouseCount,true);
  String bottomPageSelectionLinks = HTMLGeneration.getNewPageSelectionLinks(limit,pagenum,mouseCount,false);


  Holder holder = DBConnect.getHolder(holderID);
  Facility facility = DBConnect.getFacility(facilityID);
  Gene gene = DBConnect.getGene(geneID);

  String mouseTypeStr = "";
  String mouseCountStr = "";
  if(mouseTypeID != -1) {
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
    mouseCountStr = mouseCount + " found (" + mice.size() + " shown)";
  }

  mouseTypeStr += " records";

  if (holder != null) {
    mouseTypeStr += " held by " + holder.getFullname();
  }
  else if(gene != null) {
    mouseTypeStr += " with gene <span class=\"geneSymbol\">" + gene.getSymbol() + "</span> - <span class=\"geneName\">" + gene.getFullname() + "</span>";
  }

  if(searchTerms != null && !searchTerms.isEmpty()) {
    mouseTypeStr += " matching search term '" + searchTerms + "'";
  }

  if(!status.equals("all")) {
    mouseTypeStr += " with status='" + status + "'";
  }
  session.setAttribute("editMiceLastQuery","EditMouseSelection.jsp?" + queryString);
  session.setAttribute("editMiceLastTitle",mouseTypeStr);

    mouseTypeStr = "Edit: Listing" + mouseTypeStr;
%>
<div class="site_container">

    <p class="main_header"><%=mouseTypeStr %></p>
    <p class="label_text"><%=mouseCountStr %></p>
<%--    <a href="CovertMice.jsp">Covert Rodents</a>--%>
    

  <form class='view_opts' action="EditMouseSelection.jsp">
    <div style='position:relative'>
      <table style="width: 100%">
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
              <input type="search" placeholder="Search..." style='font-size:120%;vertical-align:top;margin-top: 0px' class="input-xlarge" name="searchterms" id="mousetypeselection_searchterms"></input>
              <input type="image" alt="Submit" src=/img/Eyeglass-black.svg style="height: 28px;margin: 0px" value="Search">
            </div>
          </td>
        </tr>
      </table>
    <br>
    <div style='position:absolute;bottom:0;right:0;'>
<%--    <a class="btn" style="text-decoration:none" href="<%= siteRoot %>MouseList<%= (queryString.length() > 0 ? "?" + queryString : "") %> ">Download this list (pdf)</a>--%>
    </div>
    </div>
    <%= table %>
    <%= bottomPageSelectionLinks %>
  </form>
</div>
<%=HTMLGeneration.getWebsiteFooter()%>

<script type='text/javascript'>
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
