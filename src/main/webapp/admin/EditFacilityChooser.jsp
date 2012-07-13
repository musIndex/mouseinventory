<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditFacilityChooser.jsp", true) %>
<div class="site_container">
<%
  String command = request.getParameter("command");
  String orderby = request.getParameter("orderby");
  if (command == null || command.isEmpty() || command.equals("edit"))
  {
    ArrayList<Facility> facilities = DBConnect.getAllFacilities(false,orderby);
    String table = HTMLGeneration.getFacilityTable(facilities,true);
    %>
    <h2>Edit Facilities:</h2>
    <p><a href="EditFacilityChooser.jsp?command=add">Add Facility...</a>
    <p>Sort by <a href="EditFacilityChooser.jsp?orderby=facility&command=edit">Name</a>,
          <a href="EditFacilityChooser.jsp?orderby=description&command=edit">Description</a>
          <a href="EditFacilityChooser.jsp?orderby=count&command=edit">Record count</a></p>

    <%= table %>
    <%
  }
  else if (command.equals("add"))
  {
    %>
    <h2>Add New Facility</h2>

    <form action="UpdateFacility.jsp" method="post">
        <table>
            <tr>
                <td>Facility Name: </td>
                <td><input type=text name="facilityName" size=50></td>
            </tr>
            <tr>
                <td>Facility Description: </td>
                <td><input type=text name="facilityDescription" size=50></td>
            </tr>
            <tr>
                <td>Facility Code: </td>
                <td><input type=text name="facilityCode" size=10></td>
            </tr>
            <tr>
                <td colspan="2">
                <input type="hidden" name="command" value="Insert">
                <input type="submit" class="btn btn-success" value="Create Facility"></td>
            </tr>
        </table>
    </form>
    <%
  }
%>
</div>
