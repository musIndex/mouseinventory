<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar("HolderReport.jsp", false) %>
<%
  String orderby = request.getParameter("orderby");
    ArrayList<Holder> holders = DBConnect.getAllHolders(false,orderby);
    String table = HTMLGeneration.getHolderTable(holders,false);
    
%>

<div class="pagecontent">
    <h2>Holder List</h2>
    <p>Sort by <a href="HolderReport.jsp?orderby=firstname,lastname">First Name</a>,
          <a href="HolderReport.jsp?orderby=lastname,firstname">Last name</a>,
          <a href="HolderReport.jsp?orderby=department">Department</a>,
          <a href="HolderReport.jsp?orderby=datevalidated">Date reviewed</a>
          <a href="HolderReport.jsp?orderby=count">Mice Held</a></p>
          
<div style="width: 700px;"><b>
'Last review date' shows when the most recent review of the list of mice held by each investigator
was carried out.</b>
<br>To perform a review and notify admin that it has been done, 
go to the page listing the mice held by an individual investigator (click on the 
number in the last column on the right).
</div>
<br>
<%= table %>
</div>