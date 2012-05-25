<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("", true) %>


<% Boolean rebuild = request.getParameter("rebuild") != null && request.getParameter("rebuild").equals("true");
   
   if (rebuild) {
    DBConnect.updateSearchIndex();
   }
     %>

  
<div class="pagecontent">
<h2>Rebuild Search Index</h2>
<% if(rebuild) { %>

<b>Search index rebuilt</b>
<% }else{ %>
<a href="<%=adminRoot%>RebuildIndex.jsp?rebuild=true" class='btn btn-danger' style='text-decoration:none'>Rebuild search index (may take a few minutes!)</a>
<% } %>
</div>

