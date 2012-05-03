<%@page import="java.net.URLEncoder"%>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.util.ArrayList"%>
<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLUtilities.*"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<% boolean isXhr = request.getParameter("xhr") != null; %>
<% if(!isXhr){ %>
  <%=getPageHeader(null,false,false, null) %>
  <%=getNavBar("search.jsp", false) %>
<% } %> 

<script type="text/javascript" src="<%=scriptRoot%>jquery.highlight.js" ></script>
<%@include file="mouselistcommon.jspf" %>
<script type="text/javascript">


$(document).ready(function(){
  var search_form = $("#searchForm");
  var instr_link = $("#show_search_instructions");
  var instr = $(".search-instructions");
  var search_button = $("#search_button");
  var results_div = $("#searchresults");
  var search_notices = $(".search-notices");
  var siteRoot = "<%=siteRoot %>";
  var search_box = $('input[name=searchterms]');
  instr_link.toggle(show_help,hide_help);
  
  
  search_notices.find(".alert").each(function(index,alert){
    var $alert = $(alert);
    var date = Date.parse($alert.data("date"));
    var name = $alert.data("name");
    var today = new Date();
    if (date < new Date(today.getTime()-1000*60*60*24*30)) {
      $alert.remove();
    }
  });
  if (search_notices.children().length == 0) {
    search_notices.remove();
  }
  
  function hide_help(){
    instr.slideUp();
    instr_link.text("how do I search?")
    search_box.focus();
  }
  function show_help(){
    instr.slideDown();
    instr_link.text("hide search help");
    search_box.focus();
  }

  
  function display(){
    var searchTerms = search_box.val().split(/[\s-\/\\]+/);
    if (searchTerms != null && searchTerms != "") {
      search_notices.remove();
    }
    $('.mouselist, .mouselistAlt').highlight(searchTerms, { className: 'highlight-searchterm' });
    if (results_div.text().trim() != "0 records match") {
      hide_help();
    } else {
      show_help();
    }
    //todo delegate this to pure css
    if (searchTerms != null && searchTerms != "")
    {
      $(".search-box").addClass("search-box-small");  
    }
    else
    {
      $(".search-box").removeClass("search-box-small");
    }
    $("#limit").change(function(){
      $.bbq.pushState({limit:$(this).val()});
      return false;
    });
    $(".pagination a").click(function(){
      if (!($(this).parent().hasClass("disabled"))){
      	$.bbq.pushState({pagenum:$(this).data("pagenum")});
      }
      return false;
    });
    $(".chzn-select").chosen(); //for the page selector, returned with results
    search_box.focus();
  }
  
  display();
  
  search_button.click(function(){
    $.bbq.pushState({searchterms: search_box.val(), pagenum: 1});
    return false;
  });

  function search(search_query){
    var $this = $(this);
    if (!search_query.limit) search_query.limit = 25;
    if (!search_query.pagenum) search_query.pagenum = 1;
    
    if (window.searchQuery != search_query ) {
      window.searchQuery = search_query;
      results_div.load(siteRoot + 'search.jsp?' + $.param(window.searchQuery) + '&xhr=true #searchresults', display);
  	}
    return false;
  } 
  
  $(window).bind( "hashchange", function(e) {
    var hash = $.bbq.getState( );
    var query = $.deparam.querystring();
     
    if ((hash == null || hash.searchterms == undefined) && query != null && query.searchterms != null) {
      hash.searchterms = query.searchterms;
    }
     
    search_box.val(hash.searchterms);
    $("#limit").val(hash.limit);
    search(hash);
  });

  $(window).trigger( "hashchange" );
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

    SearchResult result = null;
    

    if(searchterms != null && !searchterms.isEmpty())
    {
      try
      {
    	result = DBConnect.doMouseSearch(searchterms, "live");
  	    mouseCount = result.getTotal();
    	String topPageSelectionLinks = getNewPageSelectionLinks(limit,pagenum,mouseCount,false);
        String bottomPageSelectionLinks = getNewPageSelectionLinks(limit,pagenum,mouseCount,true);
        
        int startIndex = 0;
        int endIndex = mouseCount;
        if (offset + limit < mouseCount) {
          endIndex = offset + limit;
        }
        if (offset < mouseCount){
         startIndex = offset; 
        }
        
        ArrayList<MouseRecord> mice = DBConnect.getMouseRecords(result.getMatchingIds().subList(startIndex,endIndex));
        displayedMice = mice.size();
        if(mice.size() > 0)
        {
          results.append(topPageSelectionLinks);
          results.append(HTMLGeneration.getMouseTable(mice, false, true, false));
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
    Log.Info("Search:" + (searchsource != null ? searchsource : "search") + "(" + (result.getStrategy() != null ? result.getStrategy().getName() : "--") + "):[[[" + searchterms + "]]]:" + mouseCount);
  }
  else
  {
    searchterms = "";
  }
%>
  
<div class="pagecontent">
  <% if (request.getParameter("welcome") != null) {%>
  <div class="search-notices">
    <%@ include file="notices.jspf" %>
  </div>
  <% } %>
  <form id="searchForm" action="search.jsp" class="form-search" method="get">
    <div class="search-box search-box<%= searchPerformed ?  "-small" : "-primary centered" %> clearfix">
      <img src="<%=imageRoot %>mouse-img-istock.jpg"/>
      <div class="search-box-inner">
        <input type="text" class="input-xlarge search-query" name="searchterms" value="<%=searchterms%>">
        <input id="search_button" class="btn" type="submit" value="Mouse Search">
        <div class="search-instructions">
          <b>Search examples:</b>
          <dl>
            <dt>shh null</dt>
            <dd>Match records that contain both 'shh' <b>and</b> 'null'</dd>
            <dt>htr</dt>
            <dd>Match words start with htr, such as htr2c, or htr1a</dd>
            <dt>htr2c</dt>
            <dd>Match the specific gene 'htr2c'</dd>
            <dt>#101,#103</dt>
            <dd>Show record numbers 101 and 103</dd>
          </dl>
        </div>
        <p><a href="#" id="show_search_instructions">how do I search?</a></p>
      </div>
    </div> 
    <div id="searchresults">
      <p class="search-resultcount">
      <% if(searchPerformed){ %> 
        <%=mouseCount %> records match<% if( displayedMice > 0 ) { %>, <%=displayedMice%> shown
          <br>
         <span class='search-strategy-comment quality-<%=result.getStrategy().getQuality()%>'><%=result.getComment() %></span>
        <%} %>
      <%} %></p>
      <%= results.toString() %>      
      <% if(searchPerformed && mouseCount == 0){ %>
        <% //TODO show some suggestions if they don't find anything %>
      <% } %>
    </div>
  </form>
</div>
