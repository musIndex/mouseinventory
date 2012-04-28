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

    String mouseGene = request.getParameter("geneID");
    String geneSymbol = request.getParameter("geneSymbol");
    String whereClause = "";
    String resultCount = "";
    String newResults = "";

    boolean searchPerformed = false;

   

    if((searchterms != null && !searchterms.isEmpty()) || mouseGene != null)
    {
      try
      {
  	    if(mouseGene != null)
  	    {
  	      whereClause = " gene_id=" + mouseGene + " or target_gene_id=" + mouseGene;
  	    }

  	    String mouseIDregex = "^#([0-9]+)$";

  	    if(searchterms != null && searchterms.matches(mouseIDregex))
  	    {
  	      whereClause = " mouse.id=" + extractFirstGroup(mouseIDregex,searchterms);
  	    }
        
        int geneId = -1;
        if (mouseGene != null && !mouseGene.isEmpty())
        {
          geneId = Integer.parseInt(mouseGene);
        }
        ArrayList<MouseRecord> mice = DBConnect.getMouseRecords(-1, null, -1, geneId, "live", searchterms);
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
      newResults = "<p><font color=\"red\"><b>We're sorry, but an error prevented us from completing your search.  Please let the administrator know about this!</p><p>" + e.getLocalizedMessage() + "</b></font></p>";
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
        Enter search terms separated by spaces.
        <br>
    
        Use "-" before a term to exclude it. Enclose exact phrase in quotes. Punctuation marks such as ,.?! will be ignored.
        <br><br>
        <b>Examples:</b>
        <br>
        cre hedgehog
        <dl><dd>Search for all records which <b>include both</b> cre <b>and</b> hedgehog</dd></dl>
    
        cre -hedgehog
        <dl><dd>Search for all records which <b>include</b> cre and do <b>NOT include</b> hedgehog</dd></dl>
    
        "fibroblast growth factor 10"
        <dl><dd>Search for all records which include the <b>exact phrase</b> fibroblast growth factor 10. Compare to unquoted search
            for same phrase which also returns <b>fibroblast growth factor</b> 9, MGI <b>10</b>4723 (Fgf9)
        </dd></dl>
    
        <b>IMPORTANT:</b> You may limit the search to mouse type by adding "Mutant Allele", "Transgenic", or "Inbred Strain" to your search terms.
      </div>
      <a href="#" id="show_search_instructions">how do I search?</a>
    </div>
    <p class="search-resultcount"><%=resultCount %></p>
  </div> 
  <%= newResults %>
</div>

