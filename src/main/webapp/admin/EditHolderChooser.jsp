<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditHolderChooser.jsp", true) %>
<%@include file='SendMailForm.jspf' %>
<%

    //todo Order by links
%>
<div class="site_container">
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
    <p>Sort by <a href="EditHolderChooser.jsp?orderby=firstname,lastname&command=edit">Holder First Name</a>,
          <a href="EditHolderChooser.jsp?orderby=lastname,firstname&command=edit">Holder Last name</a>,
          <a href="EditHolderChooser.jsp?orderby=department&command=edit">Department</a>,
          <a href="EditHolderChooser.jsp?orderby=count&command=edit">Mice Held</a>,
          <a href="EditHolderChooser.jsp?orderby=datevalidated&command=edit">Last Review Date</a>,
          <a href="EditHolderChooser.jsp?orderby=datevalidated+desc&command=edit">Reverse Last Review Date</a></p>
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
                <td>Tel</td>
                <td><input type=text name="tel" size=20></td>
            </tr>
            
            <tr>
                <td>Primary Contact Name</td>
                <td><input type=text name="alternateName" size=40 >
                <br><i>The primary contact for communications about the database, appointed by the holder</i></td>
            </tr>
            <tr>
                <td>Primary Contact Email</td>
                <td><input type=text name="alternateEmail" size=40>
                <br><i>This email will be the recipient for email links for this holder in the mouse list,
                       with the holder's email address added as a cc.</i></td>
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
