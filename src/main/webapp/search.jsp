<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.net.URLEncoder"%>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.util.ArrayList"%>
<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLUtilities.*"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%=getPageHeader(null,true,false, null) %>
<%=getNavBar("search.jsp", false) %>
<script type="text/javascript" src="<%=scriptRoot%>jquery.highlight.js" ></script>
<script type="text/javascript" src="<%=scriptRoot%>search.js"></script>
<%@include file="mouselistcommon.jspf" %>

<%
    String searchterms = request.getParameter("searchterms");
    int pagenum = stringToInt(request.getParameter("pagenum"));
    int limit = stringToInt(request.getParameter("limit"));
    String searchsource = request.getParameter("search-source");

    if (limit == -1)
    {
      if (session.getAttribute("limit") != null)
      {
        limit = Integer.parseInt(session.getAttribute("limit").toString());
      }
      else
      {
        limit = 25;
      }
    }
    session.setAttribute("limit",limit);
    if (pagenum == -1)
    {
      pagenum = 1;
    }
    int offset = limit * (pagenum - 1);

    String whereClause = "";
    StringBuilder results = new StringBuilder();
    StringBuilder searchTips = new StringBuilder();
    int mouseCount = 0;
    int displayedMice = 0;
    boolean searchPerformed = false;

    String resultSummary = "";
    int exactMatches = 0;
    int partialMatches = 0;
    
    

    if(searchterms != null && !searchterms.isEmpty())
    {
      
      if (!searchterms.toLowerCase().equals(searchterms)) {
       searchTips.append("<p>Tip: searches are <b>not</b> case-sensitive</p>");
      }
      String trimmedTerms = searchterms.trim().toLowerCase();
      if (!trimmedTerms.matches(".*[-/\\)\\(\\.].*")){
        ArrayList<Holder> holders = DBConnect.getAllHolders();
        for(Holder holder : holders){
          if (holder.getLastname().toLowerCase().equals(trimmedTerms) ||
              holder.getFullname().toLowerCase().equals(trimmedTerms)) {
            searchTips.append("<p>Are you looking for <a href='" + siteRoot + 
                                "MouseReport.jsp?holder_id="+ holder.getHolderID() +
                                 "'>" + holder.getFullname() + "'s mouse list?</a>" +
                                 " (found on the <a href='" + siteRoot + 
                                 "HolderReport.jsp'>Holder List</a>)");
          }
        }
      }
      
      try
      {
        ArrayList<SearchResult> searchresults =  DBConnect.doMouseSearch(searchterms, "live");
        ArrayList<Integer> allMatches = new ArrayList<Integer>();
        for(SearchResult result : searchresults) {
          allMatches.addAll(result.getMatchingIds());
        }
        mouseCount = allMatches.size();
        String mouseTable = "";
        if (allMatches.size() > 0)
        {
    	  String topPageSelectionLinks = getNewPageSelectionLinks(limit,pagenum,mouseCount,true);
    	  results.append(topPageSelectionLinks);
        }
        int miceSeen = 0;
        String resultLog = "Search=" + searchterms + "||:source=" + (searchsource != null ? searchsource : "search");
    	for (SearchResult result : searchresults){
    	  resultLog += ":" + (result.getStrategy() != null ? result.getStrategy().getName() : "--") + "=" + result.getTotal();
          int resultMouseCount = result.getTotal();

          int startIndex = 0;
          int endIndex = resultMouseCount;
          if (offset + limit - miceSeen < resultMouseCount) {
            endIndex = offset + limit - miceSeen;
          }
          if (offset < resultMouseCount) {
           startIndex = offset - (pagenum > 1 ? miceSeen : 0); 
          }
          if (limit == -2) { //all
            startIndex = 0;
            endIndex = resultMouseCount;
          }
          
          miceSeen += result.getTotal();
          
          if (result.getStrategy().getQualityValue() == 0){
            exactMatches += result.getTotal(); 
          }
          else {
            partialMatches += result.getTotal(); 
          }

          if (miceSeen < offset || (displayedMice >= limit && limit != -2) || resultMouseCount == 0) {
            continue; 
          }
          
          if (result.getTotal() > 0 || displayedMice == 0){
            ArrayList<MouseRecord> mice = new ArrayList<MouseRecord>();
            if (endIndex > 0)
            {
              mice = DBConnect.getMouseRecords(result.getMatchingIds().subList(startIndex,endIndex));
            }
 
            results.append("<div class='search-strategy-header' data-tokens='" + 
                     StringUtils.join(result.getStrategy().getTokens(), ",") + "'>" + result.getStrategy().getComment() + ":");
            results.append("<a href='#' class='search-strategy-show-details'>Explain these results</a>");
            results.append("<div class='search-strategy-details'>" + 
                 result.getStrategy().getDetails());
            results.append("</div></div>");
            
            results.append("<div class='searchresults-mice " + result.getStrategy().getName() + "'>");
            results.append(HTMLGeneration.getMouseTable(mice, false, true, false,displayedMice == 0));
            results.append("</div>");
            displayedMice += mice.size();
          }
        }
       
    	Log.Info(resultLog + ":total=" + mouseCount + ":page=" + pagenum + ":limit=" + limit);
    	if (allMatches.size() > 0)
        {
          String bottomPageSelectionLinks = getNewPageSelectionLinks(limit,pagenum,mouseCount,true);
          results.append(bottomPageSelectionLinks);
        }
    	searchPerformed = true;
    }
    catch(Exception e)
    {
      results = new StringBuilder();
      results.append("<p class='red'><b>We're sorry, but an error prevented us from completing your search.  Please let the administrator know about this!</b></p>");
      Log.Error("Error searching", e);
    }
  }
%>
  
<div class="pagecontent">
  <form id="searchForm" action="search.jsp" class="form-search" method="get">
    <div class="search-box <%= searchPerformed ?  "search-box-small" : "" %> " style="display:none">
      <img src="<%=imageRoot %>mouse-img-istock.jpg"/>
      <div class="search-box-inner">
        <input type="text" class="input-xlarge search-query" name="searchterms" value="<%=searchterms %>">
        <button id="search_button" class="btn btn-primary" type="submit" ><i class='icon-white icon-search'></i> Mouse Search</button>
        <div class="search-instructions-show"><a href="#" id="show_search_instructions">how do I search?</a></div>
        <div class="search-instructions">
          <b>Search examples:</b>
          <dl>
            <dt>shh null</dt>
            <dd>Match records that contain both 'shh' <b>and</b> 'null'</dd>
            <dt>htr</dt>
            <dd>Match words that start with htr, such as htr2c, or htr1a</dd>
            <dt>htr2c</dt>
            <dd>Find the specific gene 'htr2c'</dd>
            <dt>1346833</dt>
            <dd>Look up MGI ID 1346833</dd>
            <dt>12590258</dt>
            <dd>Look up Pubmed ID 1346833</dd>
            <dt>#101,#103</dt>
            <dd>Show record numbers 101 and 103</dd>
          </dl>
        </div>
      </div>
    </div>
    <div id="searchresults-container">
      <div id="searchresults">
        <div class="search-resultcount" data-resultcount="<%=mouseCount %>">
          <% if (searchPerformed && mouseCount > 0) { %> 
            <span class='<%=exactMatches > 0 ? "quality-good" : "quality-bad" %>'>
            <%=exactMatches > 0 ? exactMatches : "No" %> exact match<%=exactMatches == 1 ? "" : "es" %></span>, <%=partialMatches %> partial
          <%} else if (searchPerformed && mouseCount ==0) { %>
            No records match your query
          <%} %>
        </div>
        <div class="clearfix"></div>
        <div class="searchtips" >
        <%=searchTips.toString() %>
        </div>
        <%= results.toString() %>      
      </div>
    </div>
  </form>
</div>
