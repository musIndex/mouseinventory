
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
    <div class="pagecontent">
    <h2>Record #<%=requestID %> not found</h2>
    </div>
    <%
    return;
    }
    ChangeRequest currentRequest = requests.get(0);



    ArrayList<MouseRecord> records = DBConnect.getMouseRecord(currentRequest.getMouseID());
    if(records.size() < 1)
    {
      DBConnect.updateChangeRequest(currentRequest.getRequestID(),"done",HTMLGeneration.emptyIfNull(currentRequest.getAdminComment()) + " \r\nAUTOMATICALLY MOVED TO DONE - Record no longer found");
      requests = DBConnect.getChangeRequest(requestID);
      String changeRequestTable = HTMLGeneration.getChangeRequestsTable(requests,null);
    %>
    <div class="pagecontent">
    <h2>The record specified in this change request was not found.</h2>
    <p>This change request has been automatically marked as 'done', and a note has been added:</p>
    <%= changeRequestTable%>
    </div>
    <%
    return;
    }

    MouseRecord record = records.get(0);
    String changeRequestTable = HTMLGeneration.getChangeRequestsTable(requests,null);
    String existingRecord = HTMLGeneration.getMouseTable(records,false,false,true);
    String editForm = HTMLGeneration.getEditMouseForm(record,currentRequest);

%>


<div class="pagecontent pagecontent-leftaligned">
<h2>Completing change request #<%=requestID %> on record #<%=record.getMouseID() %>: <%=record.getMouseName() %> (<%= record.getMouseType() %>  )
</h2>
<h3>Change Request:</h3>
<%=changeRequestTable %>
<h3>Current Record:</h3>
<%=existingRecord %>
<br>
<%=editForm %>

</div>
