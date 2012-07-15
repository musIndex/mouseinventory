<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@page import="java.util.ArrayList"%>
<%=getPageHeader(null,false,true) %>
<%=getNavBar("ManageChangeRequests.jsp", true) %>
<%@ include file='SendMailForm.jspf' %>
<div class="site_container">
<%
  int id = stringToInt(request.getParameter("id"));
  String confirmed = request.getParameter("confirm");

  if (id < 0)
  {
    %>
      <h2>No change request ID found</h2>
    <%
  }
  else
  {

    ArrayList<ChangeRequest> reqs = DBConnect.getChangeRequest(id);
    String table = getChangeRequestsTable(reqs,null);
    if (reqs.size() != 1)
    {
      %>
        <h2>No change request with ID <%=id %> found.</h2>
      <%
    }
    else if (confirmed != null && confirmed.equals("Yes"))
    {

      DBConnect.deleteChangeRequest(id);
      %>
        <h3>Successfully deleted change request #<%=id %></h3>

      <%
    }
    else
    {
      %>
        <form action="DeleteChangeRequest.jsp" method="post">
          <input type="hidden" name="id" value="<%=id %>">
          <p>Are you sure you want to delete the change request below?</p>
          <input type="submit" class="btn btn-danger" name="confirm" value="Yes">
        </form>
        <%=table %>
      <%
    }
  }
%>
</div>
