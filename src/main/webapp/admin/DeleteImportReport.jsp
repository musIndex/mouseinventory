<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page import="java.util.ArrayList"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("ManageImportReports.jsp", true) %>
<div class="pagecontent">
<%
  int id = HTMLGeneration.stringToInt(request.getParameter("id"));
  String confirmed = request.getParameter("confirm");

  if (id < 0)
  {
    %>
      <h2>No import report ID found</h2>
    <%
  }
  else
  {

    ArrayList<ImportReport> reqs = DBConnect.getImportReport(id);
    String table = HTMLGeneration.getImportReportTable(reqs,false);
    if (reqs.size() != 1)
    {
      %>
        <h2>No import report with ID <%=id %> found.</h2>
      <%
    }
    else if (confirmed != null && confirmed.equals("Yes"))
    {

      DBConnect.deleteImportReport(id);
      %>
        <h3>Successfully deleted import report #<%=id %></h3>

      <%
    }
    else
    {
      %>
        <form action="DeleteImportReport.jsp" method="post">
          <input type="hidden" name="id" value="<%=id %>">
          <p>Are you sure you want to delete the import report below?</p>
          <input type="submit" class="btn btn-danger" name="confirm" value="Yes">
        </form>
        <%=table %>
      <%
    }
  }
%>
</div>
