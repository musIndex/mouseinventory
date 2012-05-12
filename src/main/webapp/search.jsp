<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.net.URLEncoder"%>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.util.ArrayList"%>
<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLUtilities.*"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%=getPageHeader(null,false,false, null) %>
<%=getNavBar("search.jsp", false) %>
<script type="text/javascript" src="<%=scriptRoot%>jquery.highlight.js" ></script>
<%@include file="mouselistcommon.jspf" %>

<script type="text/javascript">
$(document).ready(function(){
  var search_form = $("#searchForm");
  var instr_link = $("#show_search_instructions");
  var instr = $(".search-instructions");
  var search_button = $("#search_button");
  var results_div = $("#searchresults");
  var siteRoot = "<%=siteRoot %>";
  var search_box = $('input[name=searchterms]');
  var search_container = $(".search-box");

  instr_link.toggle(show_help,hide_help);
  search_button.click(search_button_clicked);
  $(window).bind("hashchange", function(){search(extract_search_params());});
  $(window).trigger("hashchange");

  function hide_help(){
    instr.hide();
    instr_link.text("how do I search?")
    search_box.focus();
  }
  function show_help(){
    instr.show();
    instr_link.text("hide search help");
    search_box.focus();
  }

  function search_results_loaded(){
    var hash = extract_search_params();

    //todo make the search results a js object, load them here after parsing some stuff out
    if (hash.searchterms) {
      highlight_searchterms(hash.searchterms);
    }
    hide_help(); 
    
    if (hash.searchterms != null && hash.searchterms != "") {
      search_container.addClass("search-box-small");  
    }
    else {
      search_container.removeClass("search-box-small");
    }
    search_container.show();
    
    //update the handlers for the pagination controls, which are returned by the search
    $("select[name=limit]").change(function(){
      $.bbq.pushState({limit:$(this).val(), pagenum:1});
      return false;
    }).chosen();
    $(".pagination-container a").click(function(){
      if (!($(this).hasClass("disabled"))){
      	$.bbq.pushState({pagenum:$(this).data("pagenum")});
      }
      return false;
    });
    $("a.search-strategy-show-details").click(function(){
      $(this).siblings().toggle();
      return false;
    })
  }
  
  function highlight_searchterms(searchterms){
    $('.searchresults-mice').each(function(){
      var $results = $(this);
      var $header = $(this).prev();
      var tokens = $header.data('tokens').split(',');
      $results.highlight(tokens,{className: 'highlight-searchterm'});
    });

    $("span.highlight-searchterm").parent().parent().each(function(){
      var $element = $(this);
      if($element.is("dt")) {
        if($element.parent().hasClass("mouselist-holderlist")){
          $element.show();
        }
      }
    });
  }
    
  function search_button_clicked(){
    $.bbq.pushState({searchterms: search_box.val(), pagenum: 1});
    return false;
  }
  
  function search(search_query){
    var $this = $(this);

    if (window.searchQuery != search_query ) {
      window.searchQuery = search_query;
      $("#searchresults-container").load(siteRoot + 'search.jsp?' + $.param(window.searchQuery) + ' #searchresults', search_results_loaded);
  	}
    return false;
  } 
  
  function extract_search_params(){
    var hash = $.bbq.getState( );
    var query = $.deparam.querystring();
     
    if ((hash == null || hash.searchterms == undefined) && query != null && query.searchterms != null) {
      hash.searchterms = query.searchterms;
    }
    if (!hash.limit) hash.limit = 25;
    if (!hash.pagenum) hash.pagenum = 1;
    
    search_box.val(hash.searchterms);
    $("select[name=limit]").val(hash.limit);
    return hash;
  }

});
</script>
<%
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

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
    int mouseCount = 0;
    int displayedMice = 0;
    boolean searchPerformed = false;

    String resultSummary = "";
    int exactMatches = 0;
    int partialMatches = 0;

    if(searchterms != null && !searchterms.isEmpty())
    {
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
        String resultLog = "Search:[[" + searchterms + "]]:source=" + (searchsource != null ? searchsource : "search");
    	for (SearchResult result : searchresults){
    	  resultLog += ":" + (result.getStrategy() != null ? result.getStrategy().getName() : "--") + "=" + result.getTotal();
          //TODO simplfy this.  We already just have next/prev buttons, so keep it dirt simple
    	  int resultMouseCount = result.getTotal();

          int startIndex = 0;
          int endIndex = resultMouseCount;
          if (offset + limit - miceSeen < resultMouseCount) {
            endIndex = offset + limit - miceSeen;
          }
          if (offset < resultMouseCount){
           startIndex = offset - (pagenum > 1 ? miceSeen : 0); 
          }
          
          miceSeen += result.getTotal();
          
          if (result.getStrategy().getQualityValue() == 0){
            exactMatches += result.getTotal(); 
          }
          else
          {
            partialMatches += result.getTotal(); 
          }

          if (miceSeen < offset || displayedMice >= limit || resultMouseCount == 0) {
            continue; 
          }
          
          if (result.getTotal() > 0 || displayedMice == 0){
            ArrayList<MouseRecord> mice = new ArrayList<MouseRecord>();
            if (endIndex > 0)
            {
              mice = DBConnect.getMouseRecords(result.getMatchingIds().subList(startIndex,endIndex));
            }

            if (displayedMice > 0) {
             results.append("<br><br>"); 
            }
            if (!result.getStrategy().getComment().startsWith("Exact record")) {
             results.append("<div class='search-strategy-header' data-tokens='" + 
                     StringUtils.join(result.getStrategy().getTokens(), ",") + "'>" + result.getStrategy().getComment() + ":");
             results.append("<a href='#' class='search-strategy-show-details'>Explain these results</a>");
             results.append("<div class='search-strategy-details'>" + 
                 result.getStrategy().getDetails());
             results.append("</div></div>");
            }
            results.append("<div class='searchresults-mice " + result.getStrategy().getName() + "'>");
            results.append(HTMLGeneration.getMouseTable(mice, false, true, false));
            results.append("</div>");
            displayedMice += mice.size();
          }
          
          /* System.out.println("Debug: record Ids for " + result.getStrategy().getName() + " (" + result.getTotal() + ")");
          int printed = 0;
          for(Integer mouseId : result.getMatchingIds()) {
              System.out.print(StringUtils.leftPad(Integer.toString(mouseId), 4) + " ");
              printed++;
              if (printed % limit == 0){
               System.out.println(); 
              }
          }
          System.out.println(); */
        }
        if (pagenum == 1) {
    	 Log.Info(resultLog + ":total=" + mouseCount);
        }
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
  if (searchPerformed) {
  }
  else
  {
    searchterms = "";
  }
%>
  
<div class="pagecontent">
  <form id="searchForm" action="search.jsp" class="form-search" method="get">
    <div class="search-box <%= searchPerformed ?  "search-box-small" : "" %> " style="display:none">
      <img src="<%=imageRoot %>mouse-img-istock.jpg"/>
      <div class="search-box-inner">
        <input type="text" class="input-xlarge search-query" name="searchterms" value="<%=searchterms%>">
        <button id="search_button" class="btn btn-primary" type="submit" ><i class='icon-white icon-search'></i> Mouse Search</button>
        <div class="search-instructions-show"><a href="#" id="show_search_instructions">how do I search?</a></div>
        <div class="search-instructions">
          <b>Search examples:</b>
          <dl>
            <dt>shh null</dt>
            <dd>Match records that contain both 'shh' <b>and</b> 'null'</dd>
            <dt>htr</dt>
            <dd>Match words start with htr, such as htr2c, or htr1a</dd>
            <dt>htr2c</dt>
            <dd>Find the specific gene 'htr2c'</dd>
            <dt>1346833</dt>
            <dd>Look up MGI ID 1346833</dd>
            <dt>#101,#103</dt>
            <dd>Show record numbers 101 and 103</dd>
          </dl>
        </div>
      </div>
    </div>
    <div id="searchresults-container">
      <div id="searchresults">
        <div class="search-resultcount">
          <% if(searchPerformed && mouseCount > 0){ %> 
            <span class='<%=exactMatches > 0 ? "quality-good" : "quality-bad" %>'>
            <%=exactMatches > 0 ? exactMatches : "No" %> exact matches</span>, <%=partialMatches %> partial
          <%} else if(searchPerformed && mouseCount ==0){ %>
            No records match your query
          <%} %>
        </div>
        <%= results.toString() %>      
      </div>
    </div>
  </form>
</div>
