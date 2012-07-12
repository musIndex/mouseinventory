<%@ page import="java.util.Enumeration" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.sql.ResultSet" %>

<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="edu.ucsf.mousedatabase.admin.RecordManager.PopulateMouseResult"%>
<%@page import="edu.ucsf.mousedatabase.admin.RecordManager"%>
<%@ page import="java.util.ArrayList" %>

<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditMouseSelection.jsp", true) %>
<jsp:useBean id="updatedRecord" class="edu.ucsf.mousedatabase.objects.MouseRecord" scope="request"/>
<jsp:setProperty property="*" name="updatedRecord"/>

<%

  int mouseID = HTMLGeneration.stringToInt(request.getParameter("mouseID"));
    int changeRequestID = HTMLGeneration.stringToInt(request.getParameter("changeRequestID"));

  String previousRecord = null;
  if(mouseID > 0)
  {
    previousRecord = HTMLGeneration.getMouseTable(DBConnect.getMouseRecord(mouseID),true,false,true);
  }

  String updateCommand = request.getParameter("submitButton");
     if(updateCommand == null || updateCommand.isEmpty() || !(updateCommand.equals("Complete Change Request") || updateCommand.equals("Move to Pending")))
     {
       %>
    <div class="pagecontent">
    <h3>ERROR: No valid command received</h3>
    </div>
    <%
    return;
     }


     String recordUpdateResult = null;
     String notes = request.getParameter("requestNotes");
     String pageHeader = "";
  String errors = "";
  String errortext = "";
  String resultingRecord = "";
  String updatedRequest = "";
  boolean errorsEncountered = false;

  PopulateMouseResult result = RecordManager.PopulateMouseDataFromRequest(updatedRecord,request);
     if (!result.Success)
     {
       errorsEncountered = true;
       errortext += result.Message;
     }
     else
     {
       if(updateCommand.equals("Complete Change Request"))
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
      else
      {
        pageHeader = "Completed change request #" +changeRequestID;
           //update the record
           recordUpdateResult = DBConnect.updateMouseRecord(updatedRecord);

           if(recordUpdateResult != null)
           {
             errors += recordUpdateResult;
           }
           else
           {
               resultingRecord = HTMLGeneration.getMouseTable(DBConnect.getMouseRecord(mouseID),true,false, true);
           }

           //update the change request
           DBConnect.updateChangeRequest(changeRequestID,"done",HTMLGeneration.emptyIfNull(notes));
      }

       }
       else if(updateCommand.equals("Move to Pending"))
       {
         pageHeader = "Moved change request #" +changeRequestID + " to Pending.  No changes have been made to record #" + mouseID;
         //don't update the record

         //update the change request
         DBConnect.updateChangeRequest(changeRequestID,"pending",HTMLGeneration.emptyIfNull(notes));
       }

       ArrayList<ChangeRequest> requests = DBConnect.getChangeRequest(changeRequestID);
      if(requests.size() < 1)
      {
        throw new ServletException("Change request not found, cannot edit");
      }

      updatedRequest = HTMLGeneration.getChangeRequestsTable(requests,null);
     }

%>
<div class="pagecontent-leftaligned">
<h2><%=pageHeader %></h2>
<%if(errors.isEmpty())
{
  if(updateCommand.equals("Move to Pending"))
  {
    %>
    <h3>Change request was:</h3>
    <%= updatedRequest%>
    <%

  }
  else
  {

    %>
    <h3>Change request was:</h3>
    <%= updatedRequest%>
    <h3>Record before changes:</h3>
    <%= previousRecord %>
    <h3>Record after changes:</h3>
    <%= resultingRecord %>
    <%
  }

}
else
{ %>
  <h3 class="validationError"><%=errors %></h3>
  <%=errortext %>
<%
} %>
</div>
