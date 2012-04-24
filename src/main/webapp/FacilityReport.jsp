<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null, false, false)%>
<%=HTMLGeneration.getNavBar("FacilityReport.jsp", false)%>

<%
	ArrayList<Facility> facilities = DBConnect.getAllFacilities(false);	
	String table = HTMLGeneration.getFacilityTable(facilities,false);	
%>
<div class="pagecontent">
<h2>Facility List</h2>
<%=table%>
</div>