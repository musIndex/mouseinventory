
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditMouseSelection.jsp", true) %>

<%

	String orderBy = request.getParameter("orderby");
	String status = request.getParameter("status");
	String searchTerms = request.getParameter("searchterms");
	int mouseTypeID = HTMLGeneration.stringToInt(request.getParameter("mousetype_id"));
	
	if(status == null)
	{
		if ((status = (String)session.getAttribute("editMiceStatus")) == null)
		{
			status = "all";
		}
	}
	session.setAttribute("editMiceStatus",status);
	if(orderBy == null)
	{
		orderBy = (String)session.getAttribute("editMiceOrderBy");
	}
	else
	{
		session.setAttribute("editMiceOrderBy",orderBy);
	}
	
	
	ArrayList<MouseType> mouseTypes = DBConnect.getMouseTypes();
	String mouseTypeSelectionLinks = HTMLGeneration.getMouseTypeSelectionLinks(mouseTypeID, orderBy,-1,-1, mouseTypes, status,searchTerms,-1,-1);
	ArrayList<MouseRecord> mice = DBConnect.getCovertMice(orderBy,status,searchTerms,mouseTypeID);

	String table = HTMLGeneration.getMouseTable(mice, true, false, false);

	//String table = generateMouseList(mouseTypeID, null, (orderBy== null || orderBy.equals("mouse.id")) ? "mouse.id desc" : orderBy, true);	

	String mouseTypeStr = "Edit: Listing";
    if(mouseTypeID != -1)
    {
    	for(MouseType type : mouseTypes)
    	{
    		if(type.getMouseTypeID() == mouseTypeID)
    		{
    			mouseTypeStr += " " + type.getTypeName();
    			break;
    		}
    	}
    } 
    else
    {
    	mouseTypeStr += " all";	
    }
    
    mouseTypeStr += " records (with a covert holder)";

    if(searchTerms != null && !searchTerms.isEmpty())
    {
    	mouseTypeStr += " matching search term '" + searchTerms + "'";
    }
    
    if(!status.equals("all"))
	{
		mouseTypeStr += " with status='" + status + "'";
	}
    
%>
<div class="pagecontent">

    <h2><%=mouseTypeStr %></h2>
    <h4><%=mice.size() %> records found.</h4>
    <form action="CovertMice.jsp" method="post">
   	<%=mouseTypeSelectionLinks %>
	</form>
		<%= table %>
	
</div>