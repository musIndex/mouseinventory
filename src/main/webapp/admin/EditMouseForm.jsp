<%@ page import="java.util.ArrayList"%>
<%@ page import="java.sql.*"%>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@ page import="edu.ucsf.mousedatabase.objects.*"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditMouseSelection.jsp", true) %>

<%@ include file="protectAgainstDuplicateHolders.jspf" %>
<%

  
  if (request.getParameter("id") == null) 
  {
    %>
    <div class="pagecontent">
    <h2>No record specified</h2>
    </div>
    <%
    return;
  }
    int mouseID = Integer.parseInt(request.getParameter("id"));
    
        
    ArrayList<MouseRecord> records = DBConnect.getMouseRecord(mouseID);
    if(records.size() < 1)
    {
      %>
    <div class="pagecontent">
    <h2>Record #<%=mouseID %> not found</h2>
    </div>
    <%
    return;
    }
    
    MouseRecord record = records.get(0);
    
    if(record.getStatus() != null && record.getStatus().equalsIgnoreCase("incomplete"))
  {
    %>
    <div class="pagecontent">
    <h2>This record is part of an incomplete submission.  Please go to the 'hold' submissions page and click the 'create new record' link to edit.</h2>
    </div>
    <%
    return;
  }
    
    String existingRecord = HTMLGeneration.getMouseTable(records,true,false,true);
    String editForm = HTMLGeneration.getEditMouseForm(record);
    
%>


<div class="pagecontent-leftaligned">
<h2>Editing record# <%=record.getMouseID() %>: <%=record.getMouseName() %> (<%= record.getMouseType() %>  )
</h2>
<%=existingRecord %>
<%=editForm %>

</div>

