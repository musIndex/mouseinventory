<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.ChangeRequest.*" %>
<%@ page import="java.io.File" %>
<%@ page import="edu.ucsf.mousedatabase.DBConnect" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="java.io.IOException" %>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration" %>
<%@page import="edu.ucsf.mousedatabase.servlets.*" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%=getPageHeader(null, false, false) %>
<%=getNavBar(null, false) %>
<jsp:useBean id="changeRequest" class="edu.ucsf.mousedatabase.objects.ChangeRequest" scope="session"></jsp:useBean>
<%
    boolean success = stringToBoolean(request.getParameter("success"));
    String message = request.getParameter("message");
    int mouseID = stringToInt(request.getParameter("mouseID"));
    String table = "";
    if (success) {
        changeRequest.clearData();
    } else {
        ArrayList<MouseRecord> mice = DBConnect.getMouseRecord(mouseID);
        table = getMouseTable(mice, false, true, true, true, true);
    }
%>

<div class="site_container">
    <% if (success) { %>
    <p class="main_header">Change request completed</p>
    <p class="description_text" style="font-size: 16px">
        We have received your request to change information about mouse <%= changeRequest.getMouseID() %> in our
        inventory. It will be reviewed by the administrator.
        <br>
        Thank you for helping to keep the database up to date!
    </p>
    <br>
    <br>
    <%@ include file='_lastMouseListLink.jspf' %>
    <% } else { %>
    <p class="main_header">Submit a request to change Mouse Record #<%= mouseID %>
    </p>
    <%= table %>

    <div class="spacing_div_mini"></div>

    <div class="category">
        <div class="two_column_left">
            <div class="formbody">
                <form id="changerequestform" action="SubmitChangeRequest" method="post">
                    <input type="hidden" name="mouseID" value="<%= mouseID %>">

                    <table class="inputForm">
                        <tr class="formFieldH">
                            <td class="formHeaderCell" colspan="2">Contact Information</td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft">* First Name</td>
                            <td class="formRight"><input id="firstnameInput" type="text" name="firstname"
                                                         value="${changeRequest.firstname}" required></td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft">* Last Name</td>
                            <td class="formRight"><input id="lastnameInput" type="text" name="lastname"
                                                         value="${changeRequest.lastname}" required></td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft">* Email Address</td>
                            <td class="formRight"><input id="emailInput" type="text" maxlength="" name="email"
                                                         value="${changeRequest.email}" required></td>
                        </tr>
                        <tr class="formFieldH">
                            <td class="formHeaderCell" colspan="2" style="border-top: 0;">Holder Information</td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft">* Holder</td>
                            <td class="formRight">
                                <%= getHolderSelect("holderId", changeRequest.getHolderId()) %>
                                <div class="spacing_div_minix2"></div>
                                <div id="otherHolderSpan" style="display: none">
                                    <input style="border: solid 1px black;margin-bottom: 4px" type="text"
                                           id="holderName" name="holderName"
                                           value="<%= emptyIfNull(changeRequest.getHolderName()) %>" size="20">
                                </div>
                            </td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft">* Facility</td>
                            <td class="formRight">
                                <%= getFacilitySelect("facilityId", changeRequest.getFacilityId()) %>
                                <div class="spacing_div_minix2"></div>
                                <div id="otherFacilitySpan" style="display: none">
                                    <input type="text" style="border: solid 1px black;margin-bottom: 4px"
                                           id="facilityName" name="facilityName"
                                           value="<%= emptyIfNull(changeRequest.getFacilityName()) %>" size="20">
                                </div>
                            </td>
                        </tr>
                        <tr class="formFieldH">
                            <td class="formHeaderCell" colspan="2" style="border-top: 0;">Requested Changes</td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft">* Action</td>
                            <%
                                String[] actionLabels = new String[]{"Choose One", "Add selected holder", "Remove selected holder", "Change live/cryo status", "Make other changes"};
                                String[] actionValues = new String[]{"Choose One", "addHolder", "removeHolder", "changeStatus", "other"};
                            %>
                            <td class="formRight"><%=genSelect("actionSelect", actionValues, actionLabels, "Choose One", "onchange=\"actionChange(this.value)\" required", false)%>
                            </td>
                        </tr>
                        <tr id="actionRow" class="formField" style="display:none">
                            <td class="formLeft">

                            </td>
                            <td class="formRight" id="action_summary">

                            </td>
                        </tr>
                        <tr class='formField' id='cryo_live_status' style="display:none">
                            <td class="formLeft">
                                * Status
                            </td>
                            <td class="formRight">
                                <div style="width: 222.8px;text-align: left;margin-left: auto;margin-top: 13px;margin-right: auto;margin-bottom: 13px;">
                                <%=genRadio("cryoLiveStatus", new String[]{"Live only", "Live and Cryo", "Cryo only"}, "Live only", "required")%>
                                </div>
                            </td>
                        </tr>
                        <tr class='formField' id='background_info' style="display:none">
                            <td class="formLeft" style="line-height: 19px">
                                Genetic background information
                            </td>
                            <td class="formRight">
                                <input type="text" name="geneticBackgroundInfo">
                            </td>
                        </tr>
                        <tr class='formField' id='comments_label' style="display:none">
                            <td class="formLeft">
                                Comments:
                            <td class="formRight">
                                <textarea rows="7"
                                          style="resize: none;margin-top: 13px;margin-bottom: 13px;width: 212.8px"
                                          name="userComment"></textarea>
                        </tr>
                        <tr >
                            <td colspan="2">
                                <div class="spacing_div_minix2"></div>
                                <div class='form_submission' id="formSubmission" style="display: none">
                                    <div class="MSU_green_button" style="width: 32%;float: right;margin-right: -3px;">
                                        <input type="submit" value="Submit"
                                               style="width: 100%;height: 100%;background-color: transparent;border: none;font-size: 19px;color: white;">
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
        </div>
        <div class="two_column_right">
            <div class="sidebar_desc"
                 style="width: 100%;margin-left: -8px;padding-top: 3px;padding-left: 6px;height: 500px">
                <p class="block_form_label_text">Before completing a submission form</p>
                <p class="block_form_desc_text">, use the search feature above to determine if the rodent is already
                    listed in the inventory. <br><br>Each submission should be for an</p>
                <p class="block_form_label_text"> individual</p>
                <p class="block_form_desc_text"> mutant allele or transgene (or inbred strain) - generically referred to
                    as a "rodent", irrespective of whether the allele or transgene is maintained in combination with
                    other mutant alleles or transgenes.
                    If it is, and if you want to note that it is also being housed by another investigator or to add
                    information about the rodent, do not use this form. Instead, go to the</p>
                <a class="anchor_no_underline" href="MouseReport.jsp">
                    <p style="color: black;display: inline;text-decoration: underline" class="block_form_label_text">
                        Rodent Records</p>
                </a>
                <p class="block_form_desc_text">page, find the one for that rodent, click on "Request change in record"
                    (under the name of that rodent), and complete the form.<br><br>
                    If that individual mutant allele or transgene is not in the database, even if a similar one is,
                    please complete submission form.<br><br>
                    Select the field and begin typing the last name of the holder you want to select until it appears in
                    the field. If the holder/facility is not included in the list, choose 'other' from the drop down
                    list and enter the information
                </p>
            </div>
        </div>
    </div>
    <%}%>
</div>

<script>
    function holderVisibility() {
        var holderNameVal = document.getElementById("holderId");
        var holderNameSpan = document.getElementById("otherHolderSpan");
        var holderNameInput = document.getElementById("holderName");

        if (holderNameVal.value == -2) {
            holderNameSpan.style = "display:inline";
            holderNameInput.required = true;
        } else {
            holderNameSpan.style = "display:none";
            holderNameInput.required = false;
        }
    }

    function facilityVisibility() {
        var facilityNameVal = document.getElementById("facilityId");
        var facilityNameSpan = document.getElementById("otherFacilitySpan");
        var facilityNameInput = document.getElementById("facilityName");

        if (facilityNameVal.value == -2) {
            facilityNameSpan.style = "display:inline";
            facilityNameInput.required = true;
        } else {
            facilityNameSpan.style = "display:none";
            facilityNameInput.required = false;
        }
    }

    function addHolder(actionSummary, cryoLiveStatus, backgroundInfo, commentsLabel, actionRow) {
        actionSummary.textContent = "The holder and facility selected in step 2 will be added.";
        actionSummary.style = "display:table-cell";
        actionRow.style = "display:table-row"
        commentsLabel.style = "display:table-row";
        backgroundInfo.style = "display:table-row";
        cryoLiveStatus.style = "display:none";
    }

    function removeHolder(actionSummary, cryoLiveStatus, backgroundInfo, commentsLabel, actionRow) {
        actionSummary.textContent = "The holder and facility selected in step 2 will be removed.";
        actionSummary.style = "display:table-cell";
        actionRow.style = "display:table-row"
        commentsLabel.style = "display:table-row";
        backgroundInfo.style = "display:none";
        cryoLiveStatus.style = "display:none";
    }

    function changeStatus(actionSummary, cryoLiveStatus, backgroundInfo, commentsLabel, actionRow) {
        actionSummary.textContent = "Modify the cryo/live status of this mouse, which is being maintained by the holder/in the facility selected in step 2.";
        actionSummary.style = "display:table-cell";
        actionRow.style = "display:table-row"
        commentsLabel.style = "display:table-row";
        backgroundInfo.style = "display:none";
        cryoLiveStatus.style = "display:table-row";
    }

    function other(actionSummary, cryoLiveStatus, backgroundInfo, commentsLabel, actionRow) {
        actionSummary.textContent = "";
        actionSummary.style = "display:none";
        actionRow.style = "display:none"
        commentsLabel.style = "display:table-row";
        backgroundInfo.style = "display:none";
        cryoLiveStatus.style = "display:none";
    }

    function actionChange(action) {
        var actionSummary = document.getElementById("action_summary");
        var actionRow = document.getElementById("actionRow");
        var formSubmission = document.getElementById("formSubmission");
        actionSummary.style = "display:inline";
        formSubmission.style = "display:inline";

        var commentsLabel = document.getElementById("comments_label");
        var cryoLiveStatus = document.getElementById("cryo_live_status");
        var backgroundInfo = document.getElementById("background_info");

        if (action == "addHolder") {
            addHolder(actionSummary, cryoLiveStatus, backgroundInfo, commentsLabel, actionRow);
        } else if (action == "removeHolder") {
            removeHolder(actionSummary, cryoLiveStatus, backgroundInfo, commentsLabel, actionRow);
        } else if (action == "changeStatus") {
            changeStatus(actionSummary, cryoLiveStatus, backgroundInfo, commentsLabel, actionRow);
        } else if (action == "other") {
            other(actionSummary, cryoLiveStatus, backgroundInfo, commentsLabel, actionRow);
        } else {
            actionSummary.textContent = "";
            actionSummary.style = "display:none";
            actionRow.style = "display:none"
            commentsLabel.style = "display:none";
            backgroundInfo.style = "display:none";
            cryoLiveStatus.style = "display:none";
            formSubmission.style = "display:none";
        }

    }
</script>

<%=HTMLGeneration.getWebsiteFooter()%>