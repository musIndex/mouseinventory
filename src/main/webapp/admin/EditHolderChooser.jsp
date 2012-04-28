<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditHolderChooser.jsp", true) %>
<%

    //todo Order by links
%>
<div class="pagecontent">
<%
  String command = request.getParameter("command");
  String orderby = request.getParameter("orderby");
  if (command == null || command.isEmpty() || command.equals("edit"))
  {
      ArrayList<Holder> holders = DBConnect.getAllHolders(false,orderby);
      String table = HTMLGeneration.getHolderTable(holders,true);
    %>
    <h2>Edit Holders:</h2>
    <p><a href="EditHolderChooser.jsp?command=add">Add Holder...</a></p>
    <p>Sort by <a href="EditHolderChooser.jsp?orderby=firstname,lastname&command=edit">First Name</a>,
          <a href="EditHolderChooser.jsp?orderby=lastname,firstname&command=edit">Last name</a>,
          <a href="EditHolderChooser.jsp?orderby=department&command=edit">Department</a>,
          <a href="EditHolderChooser.jsp?orderby=count&command=edit">Mice Held</a></p>
    <%= table %>
    <%
  }
  else if (command.equals("add"))
  {
    %>
    <h2>Add Holder</h2>
    <form action="UpdateHolder.jsp" method="post">
        <table>
            <tr>
                <td>First name</td>
                <td><input type=text name="firstname" size=20></td>
            </tr>
            <tr>
                <td>Last name</td>
                <td><input type=text name="lastname" size=20></td>
            </tr>
            <tr>
                <td>Department</td>
                <td><input type=text name="dept" size="40"></td>
            </tr>
            <tr>
                <td>Email</td>
                <td><input type=text name="email" size=40></td>
            </tr>
            <tr>
                <td>Alternate Email</td>
                <td><input type=text name="alternateEmail" size=40>
                <br><i>This email will be added as a cc on email links for this holder in the mouse list</i></td>
            </tr>
            <tr>
                <td>Tel</td>
                <td><input type=text name="tel" size=20></td>
            </tr>
            <tr><td colspan=2 align="center">
            <input type="hidden" name="command" value="Insert">
            <input type="submit" class="btn btn-success" value="Create Holder"></td></tr>
        </table>
    </form>
    <%
  }
%>
</div>
