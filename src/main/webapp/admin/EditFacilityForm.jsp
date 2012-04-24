<%@page import="java.util.ArrayList" %>
<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditFacilityChooser.jsp", true) %>

<%
	int id = -1;;
	Facility facility = null;
	ArrayList<Integer> facilityMouseIDs = null;
	boolean mayDelete = false;   
	try
	{
	    id = HTMLGeneration.stringToInt(request.getParameter("facilityID")); 
		facility = DBConnect.getFacility(id);
		if (facility == null) throw new Exception("Facility not found");
	    facilityMouseIDs = DBConnect.getMiceInFacility(id);
	   	mayDelete = facilityMouseIDs.size() <= 0;
	}
	catch (Exception e)
	{
		%>
		<div class="pagecontent">
		<h2>Facility not found</h2>
		</div>
		<%
		return;
	}
%>
<div class="pagecontent">
<h2>Edit Facility  #<%=facility.getFacilityID() %></h2>

<form action="UpdateFacility.jsp" method="post">
    <table>
        <tr>
        	<td>Facility</td>
            <td>
            	<input type="text" name="facilityName" size="20" value="<%= facility.getFacilityName() %>">
            	<input type="hidden" name="facilityID" value="<%= id %>">
            	<input type="hidden" name="command" value="Edit">
            </td>
       </tr>
       <tr>
           <td>Description</td> 
            <td>
            	<input type="text" name="facilityDescription" size="50" value="<%= HTMLGeneration.emptyIfNull(facility.getFacilityDescription()) %>">
            </td>
            
       </tr>
       <tr>
       		<td>Code (for imports)</td>
            <td>
            	<input type="text" name="facilityCode" size="10" value="<%= HTMLGeneration.emptyIfNull(facility.getFacilityCode()) %>">
            </td>
        </tr>
        <tr>
            <td colspan="2"><input type="submit" value="Save Changes"></td>
        </tr>
    </table>
</form>
<br>

<% if (mayDelete) { %>
<form action="UpdateFacility.jsp" method="post">
    <input type="hidden" name="facilityID" value="<%= id %>">
    Delete this facility? 
    <input type="submit" name="command" value="Delete">
</form>
<%}else{ %>
	This Holder is linked to one or more mouse records and cannot be deleted:
	<dl>
	<%
	int max = 20;
	int i = 0;
	for(Integer mouseID : facilityMouseIDs)
	 {%>
		<dd><a href="EditMouseForm.jsp?id=<%=mouseID %>">Edit record #<%=mouseID %></a></dd>
	<%
		i++;
		if (i >= max)
		{
			%>
			<dd>(<%=facilityMouseIDs.size() - max %> others)</dd>
			<%
			break;
		}
	 } %>
	</dl>
<%} %>
</div>