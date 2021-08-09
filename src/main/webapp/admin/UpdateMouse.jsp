<%@page import="edu.ucsf.mousedatabase.admin.RecordManager.PopulateMouseResult" %>
<%@page import="edu.ucsf.mousedatabase.admin.RecordManager" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.sql.ResultSet" %>

<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>

<%@page import="edu.ucsf.mousedatabase.HTMLGeneration" %>
<%=HTMLGeneration.getPageHeader(null, false, true) %>
<%=HTMLGeneration.getNavBar("EditMouseSelection.jsp", true) %>

<jsp:useBean id="updatedRecord" class="edu.ucsf.mousedatabase.objects.MouseRecord" scope="request"/>
<jsp:setProperty property="*" name="updatedRecord"/>


<%

    HTMLUtilities.logRequest(request);
    int mouseID = HTMLGeneration.stringToInt(request.getParameter("mouseID"));
    String errors = "";
    String errortext = "";
    String resultingRecord = "";
    String previousRecord = null;
    String recordUpdateResult = null;
    String pageHeader = "";
    boolean errorsEncountered = false;

//TODO combine code in updatemouse,updatechangerequest,updatesubmission into servlet

    PopulateMouseResult result = RecordManager.PopulateMouseDataFromRequest(updatedRecord, request);
    if (!result.Success) {
        errorsEncountered = true;
        errortext += result.Message;
    } else if (mouseID > 0) {
        previousRecord = HTMLGeneration.getMouseTable(DBConnect.getMouseRecord(mouseID), true, false, true);

        int existingRecordID = RecordManager.RecordExists(updatedRecord);

        if (existingRecordID > 0) {
            //dupicate found
            String table = HTMLGeneration.getMouseTable(DBConnect.getMouseRecord(existingRecordID), true, false, true);
            errors += "Duplicate record found with MGI Allele/Transgene ID " + updatedRecord.getRepositoryCatalogNumber();
            errortext += "<h3>Existing Record:</h3>";
            errortext += table;
            errorsEncountered = true;
        } else {
            //update the record


            ArrayList<SubmittedMouse> props = new ArrayList<SubmittedMouse>();
            props = DBConnect.getMouseSubmission(Integer.parseInt(DBConnect.getMouseRecord(mouseID).get(0).getSubmittedMouseID()));
            int gene_id = Integer.parseInt(DBConnect.getMouseRecord(mouseID).get(0).getGeneID());
            //System.out.println(props.toString());
            SubmittedMouse sub = props.get(0);
            if (updatedRecord.getExpressedSequence() != null && updatedRecord.getExpressedSequence().equals("Mouse Gene (unmodified)") && updatedRecord.getTargetGeneID() == null && updatedRecord.getRegulatoryElement() == null) {
                updatedRecord.setTargetGeneID(sub.getTGMouseGene());
                if (updatedRecord.getGeneID() == null) {
                    updatedRecord.setGeneID("" + gene_id);
                }
                updatedRecord.setRegulatoryElement(sub.getTGRegulatoryElement());
            }
            if (updatedRecord.isMA() && (sub.getMAMgiGeneID() != null || !sub.getMAMgiGeneID().equals("")) && updatedRecord.getGeneLink() == null) {
                updatedRecord.setGeneLink(sub.getMAMgiGeneID());
            }
//        DBConnect.updateMouseRecord(updatedRecord);
        }
        recordUpdateResult = DBConnect.updateMouseRecord(updatedRecord);
        pageHeader = "Updated Record #" + mouseID;

    } else {
        errors += "Mouse ID not found, cannot update";
        errorsEncountered = true;
    }


    if (recordUpdateResult != null) {
        errors += recordUpdateResult;
    } else {
        resultingRecord = HTMLGeneration.getMouseTable(DBConnect.getMouseRecord(mouseID), true, false, true);
    }
%>
<div class="site_container">
    <p class="main_header"><%=pageHeader %>
    </p>
    <%@ include file='_lastEditMiceLink.jspf' %>
    <%
        if (errors.isEmpty()) {
    %>
    <p class="label_text" style="font-size: 24px">Before:</p>
    <%= previousRecord %>
    <p class="label_text" style="font-size: 24px">After:</p>
    <%= resultingRecord %>
    <%
    } else { %>
    <p class="label_text"><%=errors %>
    </p>
    <div style="overflow: hidden">
    <%=errortext %>
    </div>
    <%
        } %>
</div>

<%=HTMLGeneration.getWebsiteFooter()%>
