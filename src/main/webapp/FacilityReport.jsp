<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null, false, false)%>
<%=HTMLGeneration.getNavBar("FacilityReport.jsp", false)%>

<%
  ArrayList<Facility> facilities = DBConnect.getAllFacilities(false);
  String table = HTMLGeneration.getFacilityTable(facilities,false);
  String adminEmail = DBConnect.loadSetting("admin_info_email").value;
%>
<div class="site_container">
<h2>Facility List</h2>
<%=table%>
<br>
<p>You can contact the Database admin at <%=HTMLGeneration.formatEmail(adminEmail, adminEmail, null) %></p>
</div>

