<%@page import="edu.ucsf.mousedatabase.dataimport.ImportHandler" %>
<%@page import="edu.ucsf.mousedatabase.dataimport.ImportDefinition" %>
<%@page import="edu.ucsf.mousedatabase.*" %>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="java.util.*" %>
<%=HTMLGeneration.getPageHeader(null, false, true) %>
<%=HTMLGeneration.getNavBar("admin.jsp", true) %>


<%
    DBConnect.initialTable();
    ArrayList<ArrayList<SubmittedMouse>> submissionLists = new ArrayList<ArrayList<SubmittedMouse>>();

    ArrayList<String> submissionListLabels = new ArrayList<String>();

    ArrayList<ArrayList<ChangeRequest>> changeRequestLists = new ArrayList<ArrayList<ChangeRequest>>();
    changeRequestLists.add(DBConnect.getChangeRequests("new", null, "Request form"));
    changeRequestLists.add(DBConnect.getChangeRequests("pending", null, "Request form"));
    String[] changeRequestListLabels = new String[]{"new", "pending"};

    StringBuffer buf = new StringBuffer();
    StringBuffer right_buf = new StringBuffer();
    StringBuffer hold_buf = new StringBuffer();
    StringBuffer data_upload = new StringBuffer();

    Map<String, String> sourceStatuses = new HashMap<String, String>();
    sourceStatuses.put("new", "new requests");
    sourceStatuses.put("pending", "pending requests");

    for (String sourceStatus : sourceStatuses.keySet()) {
        ArrayList<ArrayList<String>> openRequestSources = DBConnect.getOpenRequestSources(sourceStatus);
        if (openRequestSources.size() > 0) {
            data_upload.append("<dl>");

            for (ArrayList<String> source : openRequestSources) {
                String sourceName = source.get(0);
                int count = Integer.parseInt(source.get(1));
                if (sourceName.equals("Change request form")) {
                    continue;
                }
                for (ImportDefinition def : ImportHandler.getImportDefinitions()) {
                    sourceName = sourceName.replace(def.Name, "");
                }

                data_upload.append("<dt style=\"font-size:16px;color:black\">There " + (count > 1 ? "are" : "is") + " <b><a href='" + adminRoot + "ManageChangeRequests.jsp?status=" + sourceStatus + "&requestSource="
                        + sourceName + "'>" + count + " " + sourceStatuses.get(sourceStatus)
                        + " </a></b> from data upload <b>" + sourceName + "</b></dt>");
            }
            data_upload.append("</dl>");
        }
    }

    sourceStatuses = new HashMap<String, String>();
    sourceStatuses.put("new", "new submissions");
    sourceStatuses.put("need more info", "submissions on hold");

    for (String sourceStatus : sourceStatuses.keySet()) {
        ArrayList<ArrayList<String>> openSubmissionSources = DBConnect.getOpenSubmissionSources(sourceStatus);
        if (openSubmissionSources.size() > 0) {
//            buf.append("<br>");
            data_upload.append("<dl>");

            for (ArrayList<String> source : openSubmissionSources) {
                String sourceName = source.get(0);
                int count = Integer.parseInt(source.get(1));
                if (sourceName.equals(SubmittedMouse.SubmissionFormSource)) {
                    continue;
                }
                for (ImportDefinition def : ImportHandler.getImportDefinitions()) {
                    sourceName = sourceName.replace(def.Name, "");
                }

                if (sourceStatus.equals("new")) {
                    data_upload.append("<dt style=\"font-size:16px;color:black\">There " + (count > 1 ? "are" : "is") + "<b> <a href='" + adminRoot + "ListSubmissions.jsp?status=" + sourceStatus + "&submissionSource="
                            + sourceName + "'>" + count + " " + sourceStatuses.get(sourceStatus)
                            + "</a></b> from data upload <b>" + sourceName + "</b></dt>");
                } else {
                    submissionLists.add(DBConnect.getMouseSubmissions("need more info", null, null, source.get(0)));
                    submissionListLabels.add("submissions on hold from data upload " + sourceName);
                }
            }
            data_upload.append("</dl>");
        }
    }


    submissionLists.add(DBConnect.getMouseSubmissions("new", null, "id", SubmittedMouse.SubmissionFormSource));
    submissionListLabels.add("new manual submissions");

    for (int i = 0; i < changeRequestLists.size(); i++) {
        ArrayList<ChangeRequest> changeRequests = changeRequestLists.get(i);
        String label = changeRequestListLabels[i];
        if (changeRequests.size() > 0) {
            buf.append("<br>");
            buf.append("<div class=\"adminHomeTable\">");
            buf.append("<table class=\"adminHomeTableInside\">");
            buf.append("<tr class=\"adminHomeTableHeader\"><td colspan=\"2\">There are <b>" + changeRequests.size() + "</b> " + label + " manual change requests:</td></tr>");
            for (ChangeRequest changeRequest : changeRequests) {
                String changeRequestTitle = changeRequest.getFirstname() + " " + changeRequest.getLastname() + " requested: " +
                        changeRequest.actionRequested().label + " ";
                if (changeRequest.actionRequested() == ChangeRequest.Action.ADD_HOLDER ||
                        changeRequest.actionRequested() == ChangeRequest.Action.REMOVE_HOLDER ||
                        changeRequest.actionRequested() == ChangeRequest.Action.CHANGE_CRYO_LIVE_STATUS) {
                    changeRequestTitle += changeRequest.getHolderFirstname() + " " + changeRequest.getHolderLastname();
                }

                ArrayList<MouseRecord> mouse = DBConnect.getMouseRecord(changeRequest.getMouseID());
                if (mouse.size() > 0) {
                    buf.append("<tr><td class=\"adminTableLeftTd\">" + changeRequestTitle
                            + ": <span style=\"font-weight:bold\">" + HTMLGeneration.emptyIfNull(mouse.get(0).getMouseName()) + "</span> "
                            + "</td><td class=\"adminTableRightTd\"><div class=\"adminButton\"><a class=\"anchor_no_underline\"href=\"CompleteChangeRequest.jsp?id="
                            + changeRequest.getRequestID() + "\"><p class=\"adminButtonText\">Edit record"
//                            " #"
//                            + changeRequest.getMouseID()
                            + "</p></a></td>");
                }
                buf.append("</tr>");
            }
            buf.append("</table>");
            buf.append("</div>");
            buf.append("<br>");
        }
    }

    for (int i = 0; i < submissionLists.size(); i++) {
        ArrayList<SubmittedMouse> newSubmissions = submissionLists.get(i);
        String label = submissionListLabels.get(i);
        if (newSubmissions.size() > 0) {
            right_buf.append("<div class=\"adminHomeTable\" style=\"float:right\">");
            right_buf.append("<br>");
            right_buf.append("<table class=\"adminHomeTableInside\">");
            right_buf.append("<tr class=\"adminHomeTableHeader\"><td colspan=\"2\">There are <b>" + newSubmissions.size() + "</b> " + label + ":</td></tr>");

            for (SubmittedMouse mouse : newSubmissions) {
                String mouseName = "";
                if (mouse.getOfficialSymbol() == null || mouse.getOfficialSymbol().isEmpty()) {
                    mouseName = mouse.getMouseName();
                    if (mouseName == null || mouseName.isEmpty()) {
                        mouseName = mouse.getOfficialMouseName();
                    }
                }
                String action = "held by";
                if (!mouse.getSubmissionSource().equals(SubmittedMouse.SubmissionFormSource)) {
                    if (mouse.getSubmissionSource().contains("PDU") || mouse.getSubmissionSource().contains("Purchase")) {
                        action = "purchased by";
                    } else if (mouse.getSubmissionSource().contains("IDU")) {
                        action = "imported by";
                    }

                }
                String holders = "";
                ArrayList<MouseHolder> mouseHolders = mouse.getHolders();
                if (mouse.getHolderName() != null && !mouse.getHolderName().equals("unassigned")) {
                    holders += mouse.getHolderName();
                }
                if (mouseHolders != null) {
                    for (MouseHolder holder : mouseHolders) {
                        if (holders.length() > 0) holders += ", ";
                        holders += holder.getFullname();
                    }
                }

                right_buf.append("<tr><td class=\"adminTableLeftTd\">" + mouseName + "<span style=\"font-weight:bold\">"
                        + HTMLUtilities.getCommentForDisplay(HTMLGeneration.emptyIfNull(mouse.getOfficialSymbol()))
                        + "</span> " + action + "  " + holders
                        + "</td><td class=\"adminTableRightTd\">" +
                        "<div class=\"adminButton\">" +
                        "<a class=\"anchor_no_underline\" href=\"CreateNewRecord.jsp?id=" + mouse.getSubmissionID() + "\">" +
                        "<p class=\"adminButtonText\">Convert to record</p></a></td>");
            }
            right_buf.append("</tr>");
        }
        right_buf.append("</table>");
        right_buf.append("</div>");
    }

    submissionLists.add(DBConnect.getMouseSubmissions("need more info", null, null, SubmittedMouse.SubmissionFormSource));
    submissionListLabels.add("manual submissions on hold");
    for (int i = 1; i < submissionLists.size(); i++) {
        ArrayList<SubmittedMouse> newSubmissions = submissionLists.get(i);
        String label = submissionListLabels.get(i);
        if (newSubmissions.size() > 0) {
            hold_buf.append("<br>");
            hold_buf.append("<div class=\"adminHomeTable\">");
            hold_buf.append("<table class=\"adminHomeTableInside\">");
            hold_buf.append("<tr class=\"adminHomeTableHeader\"><td colspan=\"2\">There are <b>" + newSubmissions.size() + "</b> " + label + ":</td></tr>");
            for (SubmittedMouse mouse : newSubmissions) {
                String mouseName = "";
                if (mouse.getOfficialSymbol() == null || mouse.getOfficialSymbol().isEmpty()) {
                    mouseName = mouse.getMouseName();
                    if (mouseName == null || mouseName.isEmpty()) {
                        mouseName = mouse.getOfficialMouseName();
                    }
                }
                String action = "held by";
                if (!mouse.getSubmissionSource().equals(SubmittedMouse.SubmissionFormSource)) {
                    if (mouse.getSubmissionSource().contains("PDU") || mouse.getSubmissionSource().contains("Purchase")) {
                        action = "purchased by";
                    } else if (mouse.getSubmissionSource().contains("IDU")) {
                        action = "imported by";
                    }

                }
                String holders = "";
                ArrayList<MouseHolder> mouseHolders = mouse.getHolders();
                if (mouse.getHolderName() != null && !mouse.getHolderName().equals("unassigned")) {
                    holders += mouse.getHolderName();
                }
                if (mouseHolders != null) {
                    for (MouseHolder holder : mouseHolders) {
                        if (holders.length() > 0) holders += ", ";
                        holders += holder.getFullname();
                    }
                }
                hold_buf.append("<tr><td class=\"adminTableLeftTd\">" + mouseName + "<span style=\"font-weight:bold\">"
                        + HTMLUtilities.getCommentForDisplay(HTMLGeneration.emptyIfNull(mouse.getOfficialSymbol()))
                        + "</span> " + action + "  " + holders
                        + "</td><td class=\"adminTableRightTd\">" +
                        "<div class=\"adminButton\">" +
                        "<a class=\"anchor_no_underline\" href=\"CreateNewRecord.jsp?id=" + mouse.getSubmissionID() + "\">" +
                        "<p class=\"adminButtonText\">Convert to record</p></a></td>");

            }
            hold_buf.append("</tr>");
        }
        hold_buf.append("</table>");
        hold_buf.append("</div>");
    }


%>
<div class="site_container">
    <p class="main_header">MSU Rodent Database Administration</p>
    <div style="width: 100%">
        <br>
        <%=data_upload.toString()%>
    </div>
    <div class="category">
        <div class="two_column_left">
            <%=buf.toString() %>
            <%=hold_buf.toString()%>

        </div>
        <div class="two_column_right">
            <%=right_buf.toString()%>
        </div>
    </div>
</div>

<%=HTMLGeneration.getWebsiteFooter()%>