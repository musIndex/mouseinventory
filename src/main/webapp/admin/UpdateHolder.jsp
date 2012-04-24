<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page import="java.util.ArrayList"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditHolderChooser.jsp", true) %>
<jsp:useBean id="editedHolder" class="edu.ucsf.mousedatabase.objects.Holder" scope="request"></jsp:useBean>
<jsp:setProperty property="*" name="editedHolder"/>
<%
	String command = request.getParameter("command");
	StringBuffer buf = new StringBuffer();
	
	int id = HTMLGeneration.stringToInt(request.getParameter("holderID"));
	Holder originalHolder = new Holder();
	ArrayList<Holder> holders = new ArrayList<Holder>();
	String originalHolderTable = "";
	if (id >= 0)
	{
		try
		{
			originalHolder = DBConnect.getHolder(id);
			holders.add(originalHolder);	    	    
		    originalHolderTable = HTMLGeneration.getHolderTable(holders,true);
			}
		catch (Exception e)
		{
			%>
			<div class="pagecontent">
			<h2>Holder not found</h2>
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
			buf.append("<h2>No holder ID received</h2>");
		}
		else
		{
		    buf.append("<h2>Before edit:</h2> ");
		    buf.append(originalHolderTable);
			
			DBConnect.updateHolder(editedHolder);
			
			holders.clear();
			holders.add(DBConnect.getHolder(id));	    
		    buf.append("<h2>After edit:</h2> ");
		    buf.append(HTMLGeneration.getHolderTable(holders,true));
		}
	}
	else if (command.equals("Insert"))
	{
		id = DBConnect.insertHolder(editedHolder);
		holders.clear();
		holders.add(DBConnect.getHolder(id));	    
	    buf.append("<h2>Added new holder:</h2> ");
	    buf.append(HTMLGeneration.getHolderTable(holders,true));
	}
	else if (command.equals("Delete"))
	{
		
		boolean confirmed = request.getParameter("confirm") != null && 
			request.getParameter("confirm").equals("yes");
		if (confirmed)
		{
			ArrayList<Integer> holderMouseIDs = DBConnect.getMiceWithHolder(id);
			if (id <=0)
			{
				buf.append("<h2>No holder ID received</h2>");
			}
			else if (holderMouseIDs != null && holderMouseIDs.size() > 0)
			{
				buf.append("<h2>This holder is still liked to a record and cannot be deleted!</h2>");
			}
			else
			{
				DBConnect.deleteHolder(id);
				buf.append("<h2>Holder " +  originalHolder.getFullname() + " has been deleted</h2>");
			}
		}
		else
		{			
		    buf.append("<h2>Confirm that you want to delete this holder</h2>\r\n");
		    buf.append(originalHolderTable);
			buf.append("<form action=\"UpdateHolder.jsp\" method=\"post\">");
			buf.append("    <input type=\"hidden\" name=\"holderID\" value=\"" + id + "\">");
			buf.append("    <input type=\"hidden\" name=\"confirm\" value=\"yes\">");
			buf.append("    Yes, I want to delete this holder: ");
			buf.append("    <input type=\"submit\" name=\"command\" value=\"Delete\">");
			buf.append("</form>");
			buf.append("<p>Else click your browser's BACK button.</p>");
		}
	}
	else
	{
		buf.append("<h2>Unrecognized command '" + command + "'.  Expected 'Edit','Delete',or 'Insert'</h2>");
	}
%>
<div class="pagecontent">
<%= buf.toString() %>
</div>