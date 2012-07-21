<%@ page import="java.util.ArrayList"%>
<%@ page import="java.sql.*"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@ page import="edu.ucsf.mousedatabase.objects.*"%>
<%=getPageHeader(null,false,true) %>
<%=getNavBar("EditMouseSelection.jsp", true) %>
<%@ include file='SendMailForm.jspf' %>

<%@ include file="protectAgainstDuplicateHolders.jspf" %>
<%


  if (request.getParameter("id") == null)
  {
    %>
    <div class="site_container">
    <h2>No change request specified</h2>
    </div>
    <%
    return;
  }
  String requestIDstr = request.getParameter("id");
    int requestID = Integer.parseInt(requestIDstr);

    ArrayList<ChangeRequest> requests = DBConnect.getChangeRequest(requestID);
    if(requests.size() < 1)
    {
      %>
    <div class="site_container">
    <h2>Record #<%=requestID %> not found</h2>
    </div>
    <%
    return;
    }
    ChangeRequest currentRequest = requests.get(0);



    ArrayList<MouseRecord> records = DBConnect.getMouseRecord(currentRequest.getMouseID());
    if(records.size() < 1)
    {
      DBConnect.updateChangeRequest(currentRequest.getRequestID(),"done",emptyIfNull(currentRequest.getAdminComment()) + " \r\nAUTOMATICALLY MOVED TO DONE - Record no longer found");
      requests = DBConnect.getChangeRequest(requestID);
      String changeRequestTable = getChangeRequestsTable(requests,null);
    %>
    <div class="site_container">
    <h2>The record specified in this change request was not found.</h2>
    <p>This change request has been automatically marked as 'done', and a note has been added:</p>
    <%= changeRequestTable%>
    </div>
    <%
    return;
    }

    MouseRecord record = records.get(0);
    String changeRequestTable = getChangeRequestsTable(requests,null);
    String existingRecord = getMouseTable(records,true,false,true);
    String editForm = getEditMouseForm(record,currentRequest);

%>


<div class="site_container">
<h2>Completing change request #<%=requestID %> on record #<%=record.getMouseID() %>: <%=record.getMouseName() %> (<%= record.getMouseType() %>  )
</h2>
<h3>Change Request:</h3>
<%=changeRequestTable %>
<h3>Current Record:</h3>
<%=existingRecord %>
<br>
<%=editForm %>

</div>

