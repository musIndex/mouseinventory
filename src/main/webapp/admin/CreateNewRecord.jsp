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

  if (request.getParameter("id") == null) {
    %>
      <div class="site_container">
      <h2>ERROR - no submission specified.</h2>
      </div>
      <%
      return;
  }

    int submissionID = Integer.parseInt(request.getParameter("id"));
    String otherRecordInfo = "";

    ArrayList<SubmittedMouse> submissions = DBConnect.getMouseSubmission(submissionID);
    if(submissions.size() < 1)
    {
      %>
        <div class="site_container">
        <h2>ERROR - submission #<%=submissionID %> was not found</h2>
        </div>
      <%
      return;
    }
    SubmittedMouse submission = submissions.get(0);

    if(submission.getStatus().equalsIgnoreCase("accepted"))
    {
      %>
      <div class="site_container">
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
           DBConnect.updateSubmission(submissionID,"new",emptyIfNull(submission.getAdminComment()) + " \r\nAUTOMATICALLY MOVED TO 'NEW' - in holding without incomplete record");
           String submissionTable = getSubmissionTable(submissions,null,null,false);
           %>
           <div class="site_container">
           <h2>ERROR - no incomplete record associated with this submission in holding.  Automatically moved to 'new', and a note was added:</h2>
           <%=submissionTable %>
           </div>
           <%
           return;
         }
         record = records.get(0);
         String link = record.getPreviewLink();
         otherRecordInfo = "Public record preview link <a href='" + link + "'>" + link + "</a><br>";
    }
    else
    {
      record = submission.toMouseRecord();
      records = new ArrayList<MouseRecord>();
      records.add(record);
    }



    String submissionTable = getSubmissionTable(submissions,null,null,false);

    String editForm = getEditMouseForm(record,submission);

    String recordPreview = getMouseTable(records,false,false,true,true,true);

%>


<div class="site_container">
<h2>Editing Submission #<%=submission.getSubmissionID() %>: <%=record.getMouseName() %> (<%= record.getMouseType() %>)
</h2>
<%=submissionTable %>
<h2>Record Preview:</h2>
<%=recordPreview %>
<%= otherRecordInfo %>
<br>
<%=editForm %>
</div>
