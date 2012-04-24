<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar("GeneReport.jsp", false) %>


<%
    String orderBy = request.getParameter("orderby");
    if (orderBy == null) 
    {
        orderBy = "fullname";
    }
    
    ArrayList<Gene> genes = DBConnect.getAllGenes(orderBy);
    String table = HTMLGeneration.getGeneTable(genes,false);
%>
<div class="pagecontent">
    <h2>Gene List</h2>

<p>Sort by <a href="GeneReport.jsp?orderby=symbol">Symbol</a>,
			<a href="GeneReport.jsp?orderby=fullname">Full name</a>,
			<a href="GeneReport.jsp?orderby=mgi">MGI</a></p>
        <%= table%>
        
</div>