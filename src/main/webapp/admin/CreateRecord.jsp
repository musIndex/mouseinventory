<%@page import="edu.ucsf.mousedatabase.beans.UserData"%>
<%@page import="edu.ucsf.mousedatabase.beans.MouseSubmission"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.Properties"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.net.URL"%>
<%@ page import="java.io.InputStreamReader"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.sql.ResultSet"%>

<%@ page import="edu.ucsf.mousedatabase.*"%>
<%@ page import="edu.ucsf.mousedatabase.admin.*"%>
<%@ page import="edu.ucsf.mousedatabase.admin.RecordManager.*"%>
<%@ page import="edu.ucsf.mousedatabase.objects.*"%>
<%@ page import="java.util.ArrayList"%>

<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%=HTMLGeneration.getPageHeader(null, false, true)%>
<%=HTMLGeneration.getNavBar("EditMouseSelection.jsp", true)%>

<jsp:useBean id="newMouse"
  class="edu.ucsf.mousedatabase.objects.MouseRecord" scope="request" />
<jsp:setProperty property="*" name="newMouse" />

<%
  int mouseID = -1;
  String transgenicType = request.getParameter("transgenicType");

  String updateCommand = request.getParameter("submitButton");
  if (updateCommand == null
      || updateCommand.isEmpty()
      || !(updateCommand.equals("Create Record") || updateCommand
          .equals("Save as Incomplete"))) {
%>
<div class="pagecontent">
  <h2>
    ERROR - no valid command received. Command was:
    <%=updateCommand%>
  </h2>
</div>
<%
  return;
  }
  String pageHeader = "";
  String resultingRecord = "";
  String recordStatus = "";
  if (updateCommand.equals("Create Record")) {
    recordStatus = "live";
  } else if (updateCommand.equals("Move to Hold")) {
    recordStatus = "incomplete";
  }

  PopulateMouseResult result = RecordManager
      .PopulateMouseDataFromRequest(newMouse, request);

  if (!result.Success) {
%>
<div class="pagecontent">
  <%=result.Message%>
</div>
<%
  return;
  }

  String recordLabel = "";

  newMouse.setStatus(recordStatus);

  mouseID = DBConnect.insertMouseRecord(newMouse);
  DBConnect.updateMouseSearchTable(Integer.toString(mouseID)); //redundant, but adding in case this solves odd bug

  String notes = "Admin submission.<br>" + newMouse.getMouseName()
      + " " + newMouse.getMouseType();
  String submissionStatus = "";

  UserData submitterData = new UserData();
  submitterData.setFirstName(request.getRemoteUser());
  submitterData.setLastName("");
  submitterData.setEmail("");
  submitterData.setDepartment("");
  submitterData.setTelephoneNumber("");

  Properties props = MouseRecord.GetPropertiesString(submitterData,
      newMouse);
  int subID = DBConnect.insertAdminSubmission(
      request.getRemoteUser(), notes, submissionStatus, props);
  DBConnect.setSubmissionID(mouseID, subID);

  resultingRecord = HTMLGeneration.getMouseTable(
      DBConnect.getMouseRecord(mouseID), true, false, true);

  if (updateCommand.equals("Save as Incomplete")) {
    pageHeader = "Created incomplete record #" + mouseID + ":";
    submissionStatus = "need more info";
    recordLabel = "To edit, use auto-generated submission "
        + "<a href = 'CreateNewRecord.jsp?id=" + subID + "'>#"
        + subID + "</a> (in holding)";
  } else if (updateCommand.equals("Create Record")) {
    submissionStatus = "accepted";
    recordLabel = "New Record:";
    pageHeader = "Created new record #" + mouseID + ":";
  }
%>
<div class="pagecontent">

  <h2><%=pageHeader%></h2>

  <h3><%=recordLabel%></h3>
  <%=resultingRecord%>


</div>
