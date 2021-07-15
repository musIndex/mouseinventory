<%@ page import="java.util.Enumeration" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.sql.ResultSet" %>
<%@page import="edu.ucsf.mousedatabase.admin.RecordManager.PopulateMouseResult" %>
<%@page import="edu.ucsf.mousedatabase.admin.RecordManager" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration" %>
<%@ page import="java.sql.Array" %>
<%=HTMLGeneration.getPageHeader(null, false, true) %>
<%=HTMLGeneration.getNavBar("EditMouseSelection.jsp", true) %>
<jsp:useBean id="updatedRecord" class="edu.ucsf.mousedatabase.objects.MouseRecord" scope="request"/>
<jsp:setProperty property="*" name="updatedRecord"/>


<%
    HTMLUtilities.logRequest(request);
    int mouseID = HTMLGeneration.stringToInt(request.getParameter("mouseID"));
    int submissionID = HTMLGeneration.stringToInt(request.getParameter("submittedMouseID"));
    String transgenicType = request.getParameter("transgenicType");
    //Log.Info(transgenicType);
    //Log.Info(request.getParameter("mouseType"));
    String submissionTable = "";
    SubmittedMouse submission = null;
    String holderEmail = null;
    String notes = request.getParameter("submissionNotes");
    String updateCommand = request.getParameter("submitButton");
    if (updateCommand == null || updateCommand.isEmpty() || !(updateCommand.equals("Convert to Record") || updateCommand.equals("Move to Hold") || updateCommand.equals("Reject Submission") || updateCommand.equals("Undo conversion to Record"))) {
%>
<div class="site_container">
    <h2>ERROR - no valid command received. Command was: <%=updateCommand %>
    </h2>
</div>
<%
        return;
    }
    String pageHeader = "";
    String errors = "";
    String errortext = "";
    boolean errorsEncountered = false;
    String resultingRecord = "";

    PopulateMouseResult result = RecordManager.PopulateMouseDataFromRequest(updatedRecord, request);
    if (!result.Success) {
        errorsEncountered = true;
        errortext += result.Message;
    } else {
        if (updateCommand.equals("Convert to Record") || updateCommand.equals("Move to Hold")) {

            int existingRecordID = RecordManager.RecordExists(updatedRecord);
            if (existingRecordID > 0) {
                //dupicate found
                String table = HTMLGeneration.getMouseTable(DBConnect.getMouseRecord(existingRecordID), true, false, true, true, true);
                errors += "Duplicate record found with MGI Allele/Transgene ID " + updatedRecord.getRepositoryCatalogNumber();
                errortext += "<h3>Existing Record:</h3>";
                errortext += table;
                errorsEncountered = true;
            } else if (updateCommand.equals("Convert to Record")) {
                //sanity check to make sure there is not already a record associated with this submission
                ArrayList<MouseRecord> records = DBConnect.getMouseRecordFromSubmission(submissionID);
                if (records.size() > 0 && mouseID < 0) {
%>
<div class="site_container">
    <h3>ERROR: a record has already been created for this submission. Please do not use the back button after creating a
        record.</h3>
</div>
<%
                    return;
                }

                DBConnect.updateSubmission(submissionID, "accepted", notes);
                updatedRecord.setStatus("live");
                if (mouseID < 0) {


                    mouseID = DBConnect.insertMouseRecord(updatedRecord);
                    DBConnect.updateMouseSearchTable(Integer.toString(mouseID)); //redundant, but adding in case this solves odd bug
                    DBConnect.setSubmissionID(mouseID, submissionID);
                    ArrayList<SubmittedMouse> props = new ArrayList<SubmittedMouse>();
                    props = DBConnect.getMouseSubmission(submissionID);
                    //System.out.println(props.toString());
                    SubmittedMouse sub = props.get(0);
                    if (sub.getIs_rat() != null && sub.getIs_rat().equalsIgnoreCase("1")) {
                        //SubmittedRat rat = DBConnect.ge
                        updatedRecord.setRat(1);
                        updatedRecord.setRepositoryCatalogNumber(sub.getMouseMGIID());
                        if (!updatedRecord.isIS()) {
                            updatedRecord.setSource(sub.getOfficialSymbol());
                        }
                        updatedRecord.setOfficialMouseName(sub.getMouseName());
                        updatedRecord.setAdminComment(sub.getAdminComment());
                        updatedRecord.setModificationType(sub.getTransgenicType());
                        updatedRecord.setExpressedSequence(sub.getTGExpressedSequence());
                        if (updatedRecord.getExpressedSequence() != null) {
                            if (updatedRecord.getExpressedSequence().equals("Reporter")) {
                                updatedRecord.setReporter(sub.getTGReporter());
                            } else if (updatedRecord.getExpressedSequence().equals("Mouse Gene (unmodified)")) {
                                updatedRecord.setGeneID(sub.getProperties().getProperty("rat gene"));
                            } else if (updatedRecord.getExpressedSequence().equals("Modified mouse gene or Other")) {
                                updatedRecord.setTargetGeneName(sub.getTGOther());
                                updatedRecord.setGeneName(sub.getTGOther());
                                updatedRecord.setOtherComment(sub.getTGOther());
                            }
                        }
                        updatedRecord.setRegulatoryElement(sub.getTGRegulatoryElement());
                        updatedRecord.setPubmedIDs(sub.toRatRecord().getPubmedIDs());
                        DBConnect.updateMouseRecord(updatedRecord);
                    } else {
                        if (updatedRecord.getExpressedSequence().equals("Mouse Gene (unmodified)")) {
                            updatedRecord.setGeneID(sub.getTGMouseGene());
                            updatedRecord.setRegulatoryElement(sub.getTGRegulatoryElement());
                            DBConnect.updateMouseRecord(updatedRecord);

                        }
                    }

                    pageHeader = "Created new Record #" + mouseID + ":";
                    resultingRecord = HTMLGeneration.getMouseTable(DBConnect.getMouseRecord(mouseID), true, false, true, true, true);
                    ArrayList<SubmittedMouse> submissions = DBConnect.getMouseSubmission(submissionID);
                    submissionTable = HTMLGeneration.getSubmissionTable(submissions, null, null, false);
                    submission = submissions.get(0);
                    try {
                        holderEmail = submission.getHolders().get(0).getEmail();
                    } catch (Exception e) {
                        Log.Error("no holder email for submission " + submissionID, e);
                    }
                }
            } else if (updateCommand.equals("Move to Hold")) {
                updatedRecord.setStatus("incomplete");
                pageHeader = "Moved submission #" + submissionID + " to holding.  Changes to incomplete record have been saved:";
                DBConnect.updateSubmission(submissionID, "need more info", notes);
                String updateResult = null;
                if (mouseID < 0) {
                    mouseID = DBConnect.insertMouseRecord(updatedRecord);
                    DBConnect.setSubmissionID(mouseID, submissionID);
                } else {
                    updateResult = DBConnect.updateMouseRecord(updatedRecord);
                }
                if (updateResult == null)  //null means there were no errors updating the submission
                {
                    ArrayList<MouseRecord> records = DBConnect.getMouseRecord(mouseID);
                    resultingRecord = HTMLGeneration.getMouseTable(records, false, false, true, true, true);
                    String link = records.get(0).getPreviewLink();
                    resultingRecord += "<br><br>Public record preview link <a href='" + link + "'>" + link + "</a>";
                } else {
                    errors += "Error: " + updateResult + "<br>";
                }
                submissionTable = HTMLGeneration.getSubmissionTable(DBConnect.getMouseSubmission(submissionID), null, null, true);
            }
        } else if (updateCommand.equals("Reject Submission")) {
            DBConnect.updateSubmission(submissionID, "rejected", notes);
            pageHeader = "Rejected submission #" + submissionID;
            //also delete the incomplete mouse record if there is one.
            if (mouseID > 0) {
                DBConnect.deleteMouseRecord(mouseID);
                pageHeader += "<br>Incomplete mouse record #" + mouseID + " was deleted as a result";
            }
        } else if (updateCommand.equals("Undo conversion to Record")) {
            if (submissionID > 0 && mouseID > 0) {
                DBConnect.updateSubmission(submissionID, "need more info", null);
                pageHeader = "Moved submission #" + submissionID + " back to hold.";
                //also delete the incomplete mouse record if there is one.
                if (mouseID > 0) {
                    updatedRecord = DBConnect.getMouseRecord(mouseID).get(0);
                    updatedRecord.setStatus("incomplete");
                    DBConnect.updateMouseRecord(updatedRecord);
                }
            } else {
                pageHeader = "Error - failed to undo conversion; no mouseID or submissionID!";
            }
        }
    }
%>
<div class="site_container">

    <%
        if (errors.isEmpty()) {
    %>
    <h2><%=pageHeader %>
    </h2>
    <%@ include file='_lastSubmissionListLink.jspf' %>
    <%
        if (updateCommand.equals("Convert to Record")) {
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
    } else if (updateCommand.equals("Move to Hold")) {
    %>
    <h3>Submission was:</h3>
    <%= submissionTable%>
    <br>
    <h3>Incomplete Record:</h3>
    <%
        }

        if (updateCommand.equals("Convert to Record") || updateCommand.equals("Move to Hold")) {
    %>
    <%= resultingRecord%>
    <%
        }
    } else {
    %>
    <h3 class="validationError"><%=errors %>
    </h3>
    <%=errortext %>
    <%
        }
    %>
</div>

