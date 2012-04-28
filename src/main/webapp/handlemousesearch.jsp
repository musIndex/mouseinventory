<%@ page import="java.util.ArrayList"%>
<%@ page import="edu.ucsf.mousedatabase.*"%>
<%@ page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%=HTMLGeneration.getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%@include file="mouselistcommon.jspf" %>
<%=HTMLGeneration.getNavBar("search.jsp", false) %>


<%
	
    String searchCacheKey = "handlemousesearch";

    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    String searchterms = request.getParameter("searchterms");
    
    String mouseGene = request.getParameter("geneID");
    String geneSymbol = request.getParameter("geneSymbol");
    String whereClause = "";
    String resultCount = "";
    String newResults = "";
    
    boolean ok = false;
    if (searchterms != null) {
     	ok = true;   
    }
    if (mouseGene != null && geneSymbol != null)
    {
    	ok = true;
    }
    
    if(!ok)
    {
    	%>
    	<jsp:forward page="search.jsp"></jsp:forward>
    	<%
    	return;
    }
	
    
		if(mouseGene != null)
	   {
		   whereClause = " gene_id=" + mouseGene + " or target_gene_id=" + mouseGene;
		   
	   }
    	
    	String mouseIDregex = "^#([0-9]+)$";
    	
    	if(searchterms.matches(mouseIDregex))
    	{
    		whereClause = " mouse.id=" + HTMLUtilities.extractFirstGroup(mouseIDregex,searchterms);
    	}
    
    
      if((searchterms == null || searchterms == "") && mouseGene == null)
      {
    	  %>
			<jsp:forward page="search.jsp"></jsp:forward>
			<%
    	  return;
      }
      else
    	  
      {
	    try
		{
	    	int geneId = -1;
	    	if (mouseGene != null && !mouseGene.isEmpty())
	    	{
	    		geneId = Integer.parseInt(mouseGene);
	    	}
	    	ArrayList<MouseRecord> mice = DBConnect.getMouseRecords(-1, null, -1, geneId, "live", searchterms);
	    	resultCount = mice.size() + " records found for terms '" + searchterms + "'";
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
		}
		catch(Exception e)
		{
			newResults = "<p><font color=\"red\"><b>We're sorry, but an error prevented us from completing your search.  Please let the administrator know about this!</p><p>" + e.getLocalizedMessage() + "</b></font></p>";
		}
      }
      
      Log.Info("Search performed with terms \"" + searchterms + "\", " + resultCount);
%>

<div class="pagecontent-leftaligned">

<h4><%=resultCount %></h4>

<div class="search-box">
	<form id="searchForm" action="handlemousesearch.jsp" class="form-search" method="get">
		Search again:&nbsp;&nbsp;
	    <input type="text" class="input-large search-query" name="searchterms" value="<%=searchterms %>">
	    <input class="btn" type="submit" value="Go">
	</form>
</div>
<%= newResults %>

</div>
