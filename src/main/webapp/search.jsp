<%@page import="java.net.URLEncoder"%>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="edu.ucsf.mousedatabase.*"%>
<%@ page import="edu.ucsf.mousedatabase.objects.MouseRecord"%>
<%@ page import="static edu.ucsf.mousedatabase.HTMLUtilities.*"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<% boolean isXhr = request.getParameter("xhr") != null; %>
<% if(!isXhr){ %>
  <%=getPageHeader(null,false,false, null) %>
  <%=getNavBar("search.jsp", false) %>
<% } %> 
<script type="text/javascript">
$(document).ready(function(){
  var instr_link = $("#show_search_instructions");
  var instr = $(".search-instructions");
  var search_button = $("#search_button");
  var page_content = $(".pagecontent");
  var siteRoot = "<%=siteRoot %>";
  instr_link.toggle(function(){
      instr.slideDown();
      instr_link.text("hide search help");
    },function(){
      instr.slideUp();
      instr_link.text("how do I search?")
   });
  
  
  
  search_button.click(do_search_ajax);
  $("#limit").change(do_search_ajax);
  $(".page-select-link").click(do_search_ajax);
  
  function do_search_ajax(){
    page_content.load(siteRoot + 'search.jsp?' + $('#searchForm').serialize() + '&xhr=true');
    $(".search-resultcount").text("searching...");
    //TODO update page location, use hash
    return false;
  }
  $('input[name=searchterms]').focus();
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
    boolean searchPerformed = false;

    

    if(searchterms != null && !searchterms.isEmpty())
    {
      try
      {
  	    
  	    String mouseIDregex = "^#([0-9]+)$";

  	    if(searchterms != null && searchterms.matches(mouseIDregex))
  	    {
  	      whereClause = " mouse.id=" + extractFirstGroup(mouseIDregex,searchterms);
  	    }

        
  	    mouseCount = DBConnect.countMouseRecords(-1, null, -1,-1, "live", searchterms, false,-1,-1);
    	String topPageSelectionLinks = getPageSelectionLinks(limit,pagenum,mouseCount,true);
        String bottomPageSelectionLinks = getPageSelectionLinks(limit,pagenum,mouseCount,true);
        ArrayList<MouseRecord> mice = DBConnect.getMouseRecords(-1, null, -1, -1, "live", searchterms, false,-1, -1, limit, offset);
        
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
  <form id="searchForm" action="search.jsp" class="form-search" method="get">
    <div class="search-box search-box<%= searchPerformed ?  "-small" : "-primary centered" %> clearfix">
      <img src="<%=imageRoot %>mouse-img-istock.jpg"/>
      <div class="search-box-inner">
        
            <input type="text" class="input-xlarge search-query" name="searchterms" value="<%=searchterms%>">
            <input id="search_button" class="btn" type="submit" value="Mouse Search">
        
        <div class="search-instructions">
          <b>Examples:</b>
          <br>
          shh null
          <br>
          Search for all records which <b>include the exact string</b> shh&lt;space&gt;null
          <br><br>
          shh or null
          <br>
          Search for all records which <b>include</b> shh <b>or</b> null
          <br><br>
          shh and null
          <br>
          Search for all records which include the shh <b>and</b> null
          <br><br>
          #101
          <br>
          Search for record #101
        </div>
        <a href="#" id="show_search_instructions">how do I search?</a>
      </div>
      <p class="search-resultcount"><% if(searchPerformed){ %> <%=mouseCount %> records match <%} %></p>
    </div> 
    <%= results.toString() %>
  </form>
</div>

