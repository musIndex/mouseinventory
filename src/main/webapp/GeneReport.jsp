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

    String[] orderOpts = new String[]{"symbol","fullname","mgi"};
    String[] orderOptLabels = new String[]{"Symbol","Full name","MGI number"};
    
    
    ArrayList<Gene> genes = DBConnect.getAllGenes(orderBy);
    String table = HTMLGeneration.getGeneTable(genes,false);
%>
<div class="site_container">
    <h2>Gene List for Mutant Alleles</h2>

  <form class='view_opts' action='GeneReport.jsp'>
 	 Sort by <%= HTMLGeneration.genSelect("orderby",orderOpts,orderOptLabels,orderBy,null) %>
  </form>
        <%= table%>

</div>
