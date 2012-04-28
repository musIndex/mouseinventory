<%@ page import="java.util.Enumeration" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.sql.ResultSet" %>
<%@page import="edu.ucsf.mousedatabase.admin.RecordManager.PopulateMouseResult"%>
<%@page import="edu.ucsf.mousedatabase.admin.RecordManager"%>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>

<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditMouseSelection.jsp", true) %>

<jsp:useBean id="updatedRecord" class="edu.ucsf.mousedatabase.objects.MouseRecord" scope="request"/>
<jsp:setProperty property="*" name="updatedRecord"/>


<%

  int mouseID = HTMLGeneration.stringToInt(request.getParameter("mouseID"));
    int submissionID = HTMLGeneration.stringToInt(request.getParameter("submittedMouseID"));
    String transgenicType = request.getParameter("transgenicType");
    //Log.Info(transgenicType);
    //Log.Info(request.getParameter("mouseType"));
    String submissionTable = "";
     String notes = request.getParameter("submissionNotes");
     String updateCommand = request.getParameter("submitButton");
     if(updateCommand == null || updateCommand.isEmpty() || !(updateCommand.equals("Convert to Record") || updateCommand.equals("Move to Hold")  || updateCommand.equals("Reject Submission") || updateCommand.equals("Undo conversion to Record")))
     {
       %>
      <div class="pagecontent">
      <h2>ERROR - no valid command received. Command was: <%=updateCommand %> </h2>
      </div>
      <%
      return;
     }
     String pageHeader = "";
  String errors = "";
  String errortext = "";
  boolean errorsEncountered = false;
  String resultingRecord = "";
  
  PopulateMouseResult result = RecordManager.PopulateMouseDataFromRequest(updatedRecord,request);
     if (!result.Success)
     {
       errorsEncountered = true;
       errortext += result.Message;
     }
     else
     {
    if(updateCommand.equals("Convert to Record") || updateCommand.equals("Move to Hold"))
    {
      
      int existingRecordID = RecordManager.RecordExists(updatedRecord);
      if(existingRecordID > 0)
      {
        //dupicate found
        String table = HTMLGeneration.getMouseTable(DBConnect.getMouseRecord(existingRecordID),true,false, true);
        errors += "Duplicate record found with MGI Allele/Transgene ID " + updatedRecord.getRepositoryCatalogNumber();
        errortext += "<h3>Existing Record:</h3>";
        errortext += table;
        errorsEncountered = true;
      }
      else if(updateCommand.equals("Convert to Record"))
      {
        //sanity check to make sure there is not already a record associated with this submission
        ArrayList<MouseRecord> records = DBConnect.getMouseRecordFromSubmission(submissionID);
        if(records.size() > 0 && mouseID < 0)
        {
          %>
          <div class="pagecontent">
          <h3>ERROR: a record has already been created for this submission.  Please do not use the back button after creating a record.</h3>
          </div>
          <%
          return;
        }
    
        DBConnect.updateSubmission(submissionID, "accepted", notes);
        updatedRecord.setStatus("live");
        if(mouseID < 0)
        {
          mouseID = DBConnect.insertMouseRecord(updatedRecord);
          DBConnect.updateMouseSearchTable(Integer.toString(mouseID)); //redundant, but adding in case this solves odd bug
             DBConnect.setSubmissionID(mouseID, submissionID);
        }
        else
        {
          DBConnect.updateMouseRecord(updatedRecord);
        }
           pageHeader = "Created new Record #" + mouseID + ":";
           resultingRecord = HTMLGeneration.getMouseTable(DBConnect.getMouseRecord(mouseID),true,false, true);
        
           submissionTable = HTMLGeneration.getSubmissionTable(DBConnect.getMouseSubmission(submissionID),null,null,false);
           
      }
      else if(updateCommand.equals("Move to Hold"))
      {
        updatedRecord.setStatus("incomplete");
        pageHeader = "Moved submission #" + submissionID + " to holding.  Changes to incomplete record have been saved:";
        DBConnect.updateSubmission(submissionID, "need more info", notes);
        String updateResult = null;
        if(mouseID < 0)
        {
          mouseID = DBConnect.insertMouseRecord(updatedRecord);
          DBConnect.setSubmissionID(mouseID, submissionID);
        }
        else
        {
          updateResult = DBConnect.updateMouseRecord(updatedRecord);
        }
        if(updateResult == null)  //null means there were no errors updating the submission
        {
            resultingRecord = HTMLGeneration.getMouseTable(DBConnect.getMouseRecord(mouseID),false,false, true);
            
        }
        else
        {
          errors += "Error: " + updateResult + "<br>";
        }
        submissionTable = HTMLGeneration.getSubmissionTable(DBConnect.getMouseSubmission(submissionID),null,null,true);
      }  
    }
    else if(updateCommand.equals("Reject Submission"))
    {
      DBConnect.updateSubmission(submissionID, "rejected", notes);
      pageHeader = "Rejected submission #" + submissionID;
      //also delete the incomplete mouse record if there is one.
      if (mouseID > 0) {
        DBConnect.deleteMouseRecord(mouseID);
        pageHeader += "<br>Incomplete mouse record #" + mouseID + " was deleted as a result";
      }
    }
    else if(updateCommand.equals("Undo conversion to Record"))
    {
      if (submissionID > 0 && mouseID > 0 )
      {
        DBConnect.updateSubmission(submissionID, "need more info", null);
        pageHeader = "Moved submission #" + submissionID + " back to hold.";
        //also delete the incomplete mouse record if there is one.
        if (mouseID > 0) 
        {
          updatedRecord = DBConnect.getMouseRecord(mouseID).get(0);
          updatedRecord.setStatus("incomplete");
          DBConnect.updateMouseRecord(updatedRecord);
        }
      }
      else
      {
        pageHeader = "Error - failed to undo conversion; no mouseID or submissionID!";
      }
    }
     }
%>
<div class="pagecontent">

<%if(errors.isEmpty())
{ 
  %>
  <h2><%=pageHeader %></h2>
  <%
  if(updateCommand.equals("Convert to Record"))
  {
    %>
    <h3>Submission was:</h3>
    <%= submissionTable%>
    <br>
    <h3>New Record:</h3>
    <form action="UpdateSubmission.jsp" method="post">
    <input type="hidden" name="mouseID" value="<%= mouseID %>"/>
    <input type="hidden" name="submittedMouseID" value="<%= submissionID %>">
    <input type="submit" class="btn btn-warning" name="submitButton" value="Undo conversion to Record">
    </form>
    <%
  }
  else if(updateCommand.equals("Move to Hold"))
  {
    %>
    <h3>Submission was:</h3>
    <%= submissionTable%>
    <br>
    <h3>Incomplete Record:</h3>
    <%
  }
  
  if(updateCommand.equals("Convert to Record") || updateCommand.equals("Move to Hold"))
  {
    %>
    <%= resultingRecord%>
    <%
  }
}
else
{
%>
  <h3 class="validationError"><%=errors %></h3>
  <%=errortext %>
<%
} 
%>
</div>

