
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.ResultSet" %>

<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("ManageChangeRequests.jsp", true) %>

<div class="pagecontent">



<%
	String newStatus = request.getParameter("newStatus");
	String idToUpdate = request.getParameter("idToUpdate");
	String updateMessage = "";
	
	String status = request.getParameter("status");
	String orderBy = request.getParameter("orderby");
	
	if(status == null)
	{
		if ((status = (String)session.getAttribute("manageChangeRequestStatus")) == null)
		{
			status = "new";
		}
	}
	session.setAttribute("manageChangeRequestStatus",status);
	if(orderBy == null)
	{
		if((orderBy = (String)session.getAttribute("manageChangeRequestOrderBy")) == null)
		{
			orderBy = "changerequest.id";
		}
	}
	else
	{
		session.setAttribute("manageChangeRequestOrderBy",orderBy);
	}
	

	

    String[] sortOptions = new String[] {"changerequest.id","requestdate","mouse_id","firstname","lastname"};
    String[] sortOptionNiceNames = new String[] {"Request #","Request date", "Record #", "Submitter first name", "Submitter last name"};
    
    String[] filterOptions = new String[] {"new","pending","done","all"};
    String[] filterOptionNiceNames = new String[] {"New", "Pending", "Done","All"};
    
    StringBuffer sortBuf = new StringBuffer();
    sortBuf.append("<form action=\"ManageChangeRequests.jsp\" method=\"post\">");
    sortBuf.append("&nbsp;Show: ");
    sortBuf.append(HTMLGeneration.genFlatRadio("status",filterOptions,filterOptionNiceNames, status,""));
    sortBuf.append("<br>&nbsp;Sort by: ");
    sortBuf.append(HTMLGeneration.genFlatRadio("orderby",sortOptions,sortOptionNiceNames, orderBy,""));
    sortBuf.append("<br>&nbsp;<input type=\"submit\" value=\"Update\">"); 
    sortBuf.append("</form>");
    
	ArrayList<ChangeRequest> requests = DBConnect.getChangeRequests(status, orderBy);
	
	String newTable = HTMLGeneration.getChangeRequestsTable(requests, status);
    
	String statusString = "Listing " + status + " change requests";
	if(status.equalsIgnoreCase("done"))
	{
		statusString = "Listing completed change requests";
	}
       
	int kount = requests.size();
%>

<h2><%= statusString %></h2>
<h4><%= kount %> found.</h4>
<%= updateMessage %>
<%= sortBuf.toString() %>
<%= newTable.toString() %>

</div>