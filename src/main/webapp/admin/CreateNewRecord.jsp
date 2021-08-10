<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.*" %>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@page import="edu.ucsf.mousedatabase.DBConnect" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%=getPageHeader(null, false, true) %>
<%=getNavBar("EditMouseSelection.jsp", true) %>
<%@ include file="protectAgainstDuplicateHolders.jspf" %>
<%
    HTMLUtilities.logRequest(request);
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

    Boolean isRat = DBConnect.getSubmissionType(submissionID);
//    if (isRat) {
//      ArrayList<SubmittedRat> submissions = DBConnect.getRatSubmission(submissionID);
//    }
    ArrayList<SubmittedMouse> submissions = DBConnect.getMouseSubmission(submissionID);
    if (submissions.size() < 1) {
%>
<div class="site_container">
    <h2>ERROR - submission #<%=submissionID %> was not found</h2>
</div>
<%
        return;
    }
    SubmittedMouse submission = submissions.get(0);

    if (submission.getStatus().equalsIgnoreCase("accepted")) {
%>
<div class="site_container">
    <h2>ERROR - submission #<%=submissionID %> has already been accepted.</h2>
</div>
<%
        return;
    }


    MouseRecord record;
    ArrayList<MouseRecord> records;
    if (submission.getStatus().equalsIgnoreCase("need more info")) {
        records = DBConnect.getMouseRecordFromSubmission(submissionID);
        if (records.size() < 1) {
            DBConnect.updateSubmission(submissionID, "new", emptyIfNull(submission.getAdminComment()) + " \r\nAUTOMATICALLY MOVED TO 'NEW' - in holding without incomplete record");
            String submissionTable = getSubmissionTable(submissions, null, null, false);
%>
<div class="site_container">
    <h2>ERROR - no incomplete record associated with this submission in holding. Automatically moved to 'new', and a
        note was added:</h2>
    <%=submissionTable %>
</div>
<%
            return;
        }
        record = records.get(0);
        record.setRat(isRat);

        String link = record.getPreviewLink();
        otherRecordInfo = "Public record preview link <a href='" + link + "'>" + link + "</a><br>";
    } else {
        record = submission.toMouseRecord();
        if (isRat != null) {
            record.setRat(isRat);
        }
        records = new ArrayList<MouseRecord>();
        records.add(record);


    }


    String submissionTable = getSubmissionTable(submissions, null, null, false);

    String editForm = getEditMouseForm(record, submission);

    String recordPreview = getMouseTable(records, false, false, true, true, true);

%>


<div class="site_container">
    <p class="main_header">Editing Submission #<%=submission.getSubmissionID() %>: <%=record.getMouseName() %>
        (<%= record.getMouseType() %>)
    </p>
<%--    <%@ include file='_lastSubmissionListLink.jspf' %>--%>
    <%=submissionTable %>
    <p class="label_text" style="font-size: 24px">Record Preview:</p>
    <%=recordPreview %>
<%--    <%= otherRecordInfo %>--%>
    <br>
    <%=editForm %>
</div>
</div> <!-- This end div is here to end the site container div. For some reason it's not picked up by intellisense, but it is necessary. -->

<%=HTMLGeneration.getWebsiteFooter()%>
