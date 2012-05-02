
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.sql.*"%>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@ page import="edu.ucsf.mousedatabase.objects.*"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditMouseSelection.jsp", true) %>
<%@ include file="protectAgainstDuplicateHolders.jspf" %>
<%

  if (request.getParameter("id") == null) {
    %>
      <div class="pagecontent">
      <h2>ERROR - no submission specified.</h2>
      </div>
      <%
      return;
  }

    int submissionID = Integer.parseInt(request.getParameter("id"));


    ArrayList<SubmittedMouse> submissions = DBConnect.getMouseSubmission(submissionID);
    if(submissions.size() < 1)
    {
      %>
        <div class="pagecontent">
        <h2>ERROR - submission #<%=submissionID %> was not found</h2>
        </div>
      <%
      return;
    }
    SubmittedMouse submission = submissions.get(0);

    if(submission.getStatus().equalsIgnoreCase("accepted"))
    {
      %>
      <div class="pagecontent">
      <h2>ERROR - submission #<%=submissionID %> has already been accepted.</h2>
      </div>
      <%
      return;
    }


    MouseRecord record;
    ArrayList<MouseRecord> records;
    if(submission.getStatus().equalsIgnoreCase("need more info"))
    {
      records = DBConnect.getMouseRecordFromSubmission(submissionID);
         if(records.size() < 1)
         {
           DBConnect.updateSubmission(submissionID,"new",HTMLGeneration.emptyIfNull(submission.getAdminComment()) + " \r\nAUTOMATICALLY MOVED TO 'NEW' - in holding without incomplete record");
           String submissionTable = HTMLGeneration.getSubmissionTable(submissions,null,null,false);
           %>
           <div class="pagecontent">
           <h2>ERROR - no incomplete record associated with this submission in holding.  Automatically moved to 'new', and a note was added:</h2>
           <%=submissionTable %>
           </div>
           <%
           return;
         }
         record = records.get(0);
    }
    else
    {
      record = submission.toMouseRecord();
      records = new ArrayList<MouseRecord>();
      records.add(record);
    }



    String submissionTable = HTMLGeneration.getSubmissionTable(submissions,null,null,false);

    String editForm = HTMLGeneration.getEditMouseForm(record,submission);

    String recordPreview = HTMLGeneration.getMouseTable(records,false,false,true);

%>


<div class="pagecontent pagecontent-leftaligned">
<h2>Editing Submission #<%=submission.getSubmissionID() %>: <%=record.getMouseName() %> (<%= record.getMouseType() %>)
</h2>
<%=submissionTable %>
<h2>Record Preview:</h2>
<%=recordPreview %>
<br>
<%=editForm %>
</div>
