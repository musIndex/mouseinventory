<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page import="java.util.ArrayList"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditFacilityChooser.jsp", true) %>
<jsp:useBean id="editedFacility" class="edu.ucsf.mousedatabase.objects.Facility" scope="request"></jsp:useBean>
<jsp:setProperty property="*" name="editedFacility"/>

<%
HTMLUtilities.logRequest(request);
  String command = request.getParameter("command");
  StringBuffer buf = new StringBuffer();

  int id = HTMLGeneration.stringToInt(request.getParameter("facilityID"));
  Facility originalFacility = new Facility();
  ArrayList<Facility> facilities = new ArrayList<Facility>();
  String originalFacilityTable = "";
  if (id >= 0)
  {
    try
    {
      originalFacility = DBConnect.getFacility(id);
      facilities.add(originalFacility);
        originalFacilityTable = HTMLGeneration.getFacilityTable(facilities,true);
      }
    catch (Exception e)
    {
      %>
      <div class="site_container">
      <h2>Facility not found</h2>
      </div>
      <%
      return;
    }
  }
  if (command == null || command.isEmpty())
  {
    buf.append("<h2>No command received.  Expected 'Edit','Delete',or 'Insert'</h2>");
  }
  else if (command.equals("Edit"))
  {
    if (id <= 0)
    {
      buf.append("<h2>No facility ID received</h2>");
    }
    else
    {
        buf.append("<h2>Before edit:</h2> ");
        buf.append(originalFacilityTable);

      DBConnect.updateFacility(editedFacility);

      facilities.clear();
      facilities.add(DBConnect.getFacility(id));
        buf.append("<h2>After edit:</h2> ");
        buf.append(HTMLGeneration.getFacilityTable(facilities,true));
    }
  }
  else if (command.equals("Insert"))
  {
    id = DBConnect.insertFacility(editedFacility);
    facilities.clear();
    facilities.add(DBConnect.getFacility(id));
      buf.append("<h2>Added new facility:</h2> ");
      buf.append(HTMLGeneration.getFacilityTable(facilities,true));
  }
  else if (command.equals("Delete"))
  {

    boolean confirmed = request.getParameter("confirm") != null && request.getParameter("confirm").equals("yes");
    if (confirmed)
    {
      ArrayList<Integer> facilityMouseIDs = DBConnect.getMiceInFacility(id);
      if (id <=0)
      {
        buf.append("<h2>No facility ID received</h2>");
      }
      else if (facilityMouseIDs != null && facilityMouseIDs.size() > 0)
      {
        buf.append("<h2>This facility is still liked to a record and cannot be deleted!</h2>");
      }
      else
      {
        DBConnect.deleteFacility(id);
        buf.append("<h2>Facility " +  originalFacility.getFacilityName()+ " has been deleted</h2>");
      }
    }
    else
    {
        buf.append("<h2>Confirm that you want to delete this facility</h2>\r\n");
        buf.append(originalFacilityTable);
      buf.append("<form action=\"UpdateFacility.jsp\" method=\"post\">");
      buf.append("    <input type=\"hidden\" name=\"facilityID\" value=\"" + id + "\">");
      buf.append("    <input type=\"hidden\" name=\"confirm\" value=\"yes\">");
      buf.append("    Yes, I want to delete this facility: ");
      buf.append("    <input type=\"submit\" class='btn btn-danger' name=\"command\" value=\"Delete\">");
      buf.append("</form>");
      buf.append("<p>Else click your browser's BACK button.</p>");
    }
  }
  else
  {
    buf.append("<h2>Unrecognized command '" + command + "'.  Expected 'Edit','Delete',or 'Insert'</h2>");
  }
%>
<div class="site_container">
<%= buf.toString() %>
</div>
