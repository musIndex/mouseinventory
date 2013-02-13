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
<p>**The individuals listed here have volunteered to provide assistance to their neighbors in navigating the database, 
submitting change requests and entering new submissions.</p>
<p>Alternatively, you can contact Database admin at <%=HTMLGeneration.formatEmail(adminEmail, adminEmail, null) %></p>
</div>


