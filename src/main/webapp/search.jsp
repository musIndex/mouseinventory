<%@page import="java.net.URLEncoder"%>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.util.ArrayList"%>
<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="edu.ucsf.mousedatabase.objects.MouseRecord"%>
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
 	
    var instr_link = $("#show_search_instructions");
    var instr = $(".search-instructions");
    var search_button = $("#search_button");
    var results_div = $("#searchresults");
    var search_notices = $(".search-notices");
    var siteRoot = "<%=siteRoot %>";
    var search_box = $('input[name=searchterms]');
    instr_link.toggle(show_help,hide_help);
    
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
    
    function show_notices(){
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
        hide_notices();
        return;
      }
      search_notices.show();
      $(".search-box").css("margin-right","320px");
    }
    
    function hide_notices(){
      search_notices.hide();
      $(".search-box").css("margin-right","0px");
    }
    
    function display(){
      
      var searchTerms = search_box.val().split(/[\s-]+/);
      $('.mouselist, .mouselistAlt').highlight(searchTerms, { className: 'highlight-searchterm' });
      if (results_div.text().trim() != "0 records match") {
      	hide_help();
      } else {
        show_help();
      }
        
      if (searchTerms != null && searchTerms != "")
      {
      	$(".search-box").removeClass("search-box-primary").removeClass("centered").addClass("search-box-small");  
      	hide_notices();
      }
      else
      {
        $(".search-box").removeClass("search-box-small").addClass("centered").addClass("search-box-primary");
        show_notices();
      }
      $(".chzn-select").chosen();
    }
    
    display();
    
    
    $(".alert .close").click(function(){
      $(this).parent().remove();
      //TODO set cookie that it was dismissed!
    });
    search_button.click(do_search_ajax);
    $("#limit").change(do_search_ajax);
    $(".page-select-link").click(do_search_ajax);
    $('input[name=searchterms]').focus();

  function do_search_ajax(){
    results_div.load(siteRoot + 'search.jsp?' + $('#searchForm').serialize() + '&xhr=true #searchresults', display);
    if (search_box.val().trim().length != "")
    	$(".search-resultcount").text("searching...");
    //TODO update page location, use hash
    return false;
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

    

    if(searchterms != null && !searchterms.isEmpty())
    {
      try
      {
  	           
  	    mouseCount = DBConnect.countMouseRecords(-1, null, -1,-1, "live", searchterms, false,-1,-1);
    	String topPageSelectionLinks = getPageSelectionLinks(limit,pagenum,mouseCount,true);
        String bottomPageSelectionLinks = getPageSelectionLinks(limit,pagenum,mouseCount,false);
        ArrayList<MouseRecord> mice = DBConnect.getMouseRecords(-1, null, -1, -1, "live", searchterms, false,-1, -1, limit, offset);
        displayedMice = mice.size();
        if(mice.size() > 0)
        {
          
          results.append(topPageSelectionLinks);
          results.append(HTMLGeneration.getMouseTable(mice, false, true, false));
          results.append(bottomPageSelectionLinks);
        }
        /*
        for(String term : mouseSearchCache.getTermsToHighlight())
        {
          Pattern pattern = Pattern.compile("(>[A-Za-z0-9\\s'\"]+)(" + term + ")([A-Za-z0-9\\s'\"]+<)",Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

              // Replace all occurrences of pattern in input
              Matcher matcher = pattern.matcher(newResults);
              String output = matcher.replaceAll("$1<b>$2</b>$3");
          newResults = output;
        }
        */
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
    Log.Info("Search performed with terms \"" + searchterms + "\", " + mouseCount);
    Log.Info("Search:" + (searchsource != null ? searchsource : "search") + ":[[[" + searchterms + "]]]:" + mouseCount);
  }
  else
  {
    searchterms = "";
  }
%>
  
<div class="pagecontent">
  <div class="search-notices">
    <%@ include file="notices.jspf" %>
  </div>
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
            <dd>Match records that have words like 'shh' <b>and</b> like 'null'</dd>
            <dt>Htr</dt>
            <dd>Matches words that start with Htr, such as Htr2c, or Htr1a</dd>
            <dt>#101</dt>
            <dd>Show record number 101</dd>
            <dt>#101,#102</dt>
            <dd>Show record numbers 101 and 102</dd>
          </dl>
        </div>
        <p><a href="#" id="show_search_instructions">how do I search?</a></p>
      </div>
    </div> 
    <div id="searchresults">
      <p class="search-resultcount"><% if(searchPerformed){ %> <%=mouseCount %> records match <%= displayedMice > 0 ? " [" + displayedMice + " shown]" : ""%><%} %></p>
      <%= results.toString() %>      
      <% if(searchPerformed && mouseCount == 0){ %>
        <% //TODO show some suggestions if they don't find anything %>
      <% } %>
    </div>
  </form>
</div>
