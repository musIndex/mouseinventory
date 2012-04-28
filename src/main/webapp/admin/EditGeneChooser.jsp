<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditGeneChooser.jsp", true) %>
<div class="pagecontent">
<%
  String command = request.getParameter("command");
  if (command == null || command.isEmpty() || command.equals("edit"))
  {
      String orderBy = "mgi";
      if (request.getParameter("orderby") != null)
      {
          orderBy = request.getParameter("orderby");
      }

      ArrayList<Gene> genes = DBConnect.getAllGenes(orderBy);
      String table = HTMLGeneration.getGeneTable(genes,true);
      %>

    <h2>Edit Genes</h2>
    <p><a href="EditGeneChooser.jsp?command=add">Add Gene...</a></p>
    <p>Sort by <a href="EditGeneChooser.jsp?orderby=symbol&command=edit">Symbol</a>,
          <a href="EditGeneChooser.jsp?orderby=fullname&command=edit">Full name</a>,
          <a href="EditGeneChooser.jsp?orderby=mgi&command=edit">MGI</a></p>
    <%= table %>
      <%
  }
  else if (command.equals("add"))
  {
    %>
      <h2>Add New Gene</h2>

      <form action="UpdateGene.jsp" method="post">
          <table>
              <tr>
                  <td>Full name</td>
                  <td><input type=text name="fullname" size=80></td>
              </tr>
              <tr>
                  <td>Symbol</td>
                  <td><input type=text name="symbol" size=20></td>
              </tr>
              <tr>
                  <td>MGI</td>
                  <td><input type=text name="mgiID" size="10"></td>
              </tr>
              <tr>
                  <td colspan="2">
                  <input type="hidden" name="command" value="Insert">
                  <input type="submit" class="btn btn-success" value="Create Gene"></td>
              </tr>
          </table>
      </form>
  <%}
%>
</div>
