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
  
  search_button.click(function(){
    
    page_content.load(siteRoot + 'search.jsp?' + $('#searchForm').serialize() + '&xhr=true');
    $(".search-resultcount").text("searching...");
    //page_content.load(siteRoot + 'search.jsp?searchterms=cre&xhr=true');
    return false;
  });
  $('input[name=searchterms]').focus();
});
</script>

<%

    
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    String searchterms = request.getParameter("searchterms");
    String searchsource = request.getParameter("search-source");

    String whereClause = "";
    String resultCount = "";
    String newResults = "";

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
        //TODO rework this, not robust to spaces, need support for quoted groups
        searchterms = searchterms.toLowerCase();
        ArrayList<MouseRecord> mice = new ArrayList<MouseRecord>();
        String[] andClasues = searchterms.split("and");
        
        for (String andClause : andClasues) {
          ArrayList<MouseRecord> mouseGroup = new ArrayList<MouseRecord>();
          String[] orClauses = andClause.split("or");
          for (String orClause : orClauses) {
          	mouseGroup = addMice(mouseGroup, DBConnect.getMouseRecords(-1, null, -1, -1, "live", orClause.trim()));
          }
          if (mice.isEmpty()) {
            mice = mouseGroup;
          }
          else
          {
            mice.retainAll(mouseGroup);
          }
        }
        
        
        resultCount = mice.size() + " records found";
        if(mice.size() > 0)
        {
          newResults = HTMLGeneration.getMouseTable(mice, false, true, false);
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
      newResults = "<p class='red'><b>We're sorry, but an error prevented us from completing your search.  Please let the administrator know about this!</b></p>";
      Log.Error("Error searching", e);
    }
  }
  if (searchPerformed) {
    Log.Info("Search performed with terms \"" + searchterms + "\", " + resultCount);
    Log.Info("Search:" + (searchsource != null ? searchsource : "search") + ":[[[" + searchterms + "]]]:" + resultCount);
  }
  else
  {
    searchterms = ""; 
  }
%>

<div class="pagecontent">
  <div class="search-box search-box<%= searchPerformed ?  "-small" : "-primary centered" %> clearfix">
    <img src="<%=imageRoot %>mouse-img-istock.jpg"/>
    <div class="search-box-inner">
      <form id="searchForm" action="search.jsp" class="form-search" method="get">
          <input type="text" class="input-xlarge search-query" name="searchterms" value="<%=searchterms%>">
          <input id="search_button" class="btn" type="submit" value="Mouse Search">
      </form>
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
    <p class="search-resultcount"><%=resultCount %></p>
  </div> 
  <%= newResults %>
</div>

