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
        <div class="two_column_left" style="width: 62%;">
            <div class="formbody">
                <form id="changerequestform" action="SubmitChangeRequest" method="post">
                    <input type="hidden" name="mouseID" value="<%= mouseID %>">

                    <table class="inputForm" style="width: 92%">
                        <tbody style="vertical-align: top">
                        <tr>
                            <td style="width: 43%;padding-left: 0px;">
                                <table class="inputForm" style="vertical-align: top;width: 92%">
                                    <tr class="formFieldH">
                                        <td class="formHeaderCell" colspan="2">Your Contact Information</td>
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
                                        <td class="formRight"><input id="emailInput" type="email" maxlength=""
                                                                     name="email"
                                                                     value="${changeRequest.email}" required></td>
                                    </tr>
                                    <tr class="formFieldH">
                                        <td class="formHeaderCell" colspan="2" style="border-top: 0;">Holder
                                            Information
                                        </td>
                                    </tr>
                                    <tr class="formField">
                                        <td class="formLeft">* Holder</td>
                                        <td class="formRight">
                                            <%= getHolderSelect("holderId", changeRequest.getHolderId()) %>
                                            <div class="spacing_div_minix2"></div>
                                            <div id="otherHolderSpan" style="display: none">
                                                <input style="border: solid 1px black;margin-bottom: 4px" type="text"
                                                       id="holderName" name="holderName"
                                                       value="<%= emptyIfNull(changeRequest.getHolderName()) %>"
                                                       size="20">
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
                                                       value="<%= emptyIfNull(changeRequest.getFacilityName()) %>"
                                                       size="20">
                                            </div>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                            <td style="width: 1%">

                            </td>
                            <td style="width:43%">
                                <table class="inputForm" style="width: 100%">
                                    <tr class="formFieldH">
                                        <td class="formHeaderCell" colspan="2" style="border-top: 0;">Requested
                                            Changes
                                        </td>
                                    </tr>
                                    <tr class="formField">
                                        <td class="formLeft">* Action</td>
                                        <%
                                            String[] actionLabels = new String[]{"Add selected holder", "Remove selected holder", "Change live/cryo status", "Make other changes"};
                                            String[] actionValues = new String[]{"1", "2", "5", "4"};
                                        %>
                                        <td class="formRight">
                                            <div style="width: 222.8px;text-align: left;margin-left: auto;margin-top: 13px;margin-right: auto;margin-bottom: 13px;">
                                                <%=genRadio("actionRequested", actionValues, actionLabels, "Choose One", "onchange=\"actionChange(this.value)\" required")%>
                                            </div>
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
                                        <td class="formLeft"
                                            style="line-height: 19px;padding-top: 5px;padding-bottom: 5px;">
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
                                    <tr>
                                        <td colspan="2">
                                            <div class="spacing_div_minix2"></div>
                                            <div class='form_submission' id="formSubmission" style="display: none">
                                                <div class="MSU_green_button"
                                                     style="width: 32%;float: right;margin-right: -3px;">
                                                    <input type="submit" value="Submit"
                                                           style="width: 100%;height: 100%;background-color: transparent;border: none;font-size: 19px;color: white;">
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </form>
            </div>
        </div>
        <div class="two_column_right" style="width: 38%">
            <div class="sidebar_desc"
                 style="width: 100%;margin-left: -8px;padding-top: 3px;padding-left: 6px;height: 500px">
                <p class="block_form_label_text" style="text-align: center">If you leave this page without submitting,
                    ALL
                    the data entered will be lost.</p><br><br>
                <p class="block_form_desc_text">If you encounter any difficulties while completing this page, please
                    contact a database administrator.</p><br><br>
                <p class="block_form_desc_text">The first step in submitting a change request is to input</p>
                <p class="block_form_label_text"> your own contact information.</p>
                <p class="block_form_desc_text">This will allow the database administrators to contact you regarding
                    your inquiry if there is any additional information required.<br><br>
                    Then, please enter the information of either:<br>
                </p>
                <p class="block_form_label_text">A)</p>
                <p class="block_form_desc_text"> The current holder (if you are</p>
                <p class="block_form_label_text"> not </p>
                <p class="block_form_desc_text">requesting for rodent holder information to be updated).<br>
                </p>
                <p class="block_form_label_text">B)</p>
                <p class="block_form_desc_text"> The holder that you would like to remove, add, or edit.<br><br>

                    Finally, select what type of action you would like to take regarding the rodent record.
                    <br><br>

                    If you are </p>
                <p class="block_form_label_text">adding a new holder</p>
                <p class="block_form_desc_text">and have genetic background information for the
                    mouse in the new holder's colony, enter it in the designated field. If you want to add a different,
                    unofficial name for the mouse, or have other comments, enter them in the subsequent comments field.
                    <br><br>
                    If you are </p>
                <p class="block_form_label_text">removing a holder,</p>
                <p class="block_form_desc_text"> feel free to add any additional information or comments in the
                    provided space.
                    <br><br>
                    If you are </p>
                <p class="block_form_label_text">changing the rodent's live/cryo status,</p>
                <p class="block_form_desc_text"> please select its corresponding status. If you have any additional
                    information regarding the rodent or its colony, please include that information as well.
                    <br><br>
                    For </p>
                <p class="block_form_label_text">any other change requests,</p>
                <p class="block_form_desc_text"> please leave a detailed comment about what you would like to append,
                    edit, or remove from the record.
                    <br><br>
                    * Indicates required field.
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
        actionSummary.style = "display:table-cell;margin-left: auto;margin-right: auto;padding: 13px 0px;"
        actionRow.style = "display:table-row"
        commentsLabel.style = "display:table-row";
        backgroundInfo.style = "display:table-row";
        cryoLiveStatus.style = "display:none";
    }

    function removeHolder(actionSummary, cryoLiveStatus, backgroundInfo, commentsLabel, actionRow) {
        actionSummary.textContent = "The holder and facility selected in step 2 will be removed.";
        actionSummary.style = "display:table-cell;margin-left: auto;margin-right: auto;padding: 13px 0px;";
        actionRow.style = "display:table-row"
        commentsLabel.style = "display:table-row";
        backgroundInfo.style = "display:none";
        cryoLiveStatus.style = "display:none";
    }

    function changeStatus(actionSummary, cryoLiveStatus, backgroundInfo, commentsLabel, actionRow) {
        actionSummary.textContent = "Modify the cryo/live status of this mouse, which is being maintained by the holder/in the facility selected in step 2.";
        actionSummary.style = "display:table-cell;margin-left: auto;margin-right: auto;padding: 13px 0px;";
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

        if (action == "1") {
            addHolder(actionSummary, cryoLiveStatus, backgroundInfo, commentsLabel, actionRow);
        } else if (action == "2") {
            removeHolder(actionSummary, cryoLiveStatus, backgroundInfo, commentsLabel, actionRow);
        } else if (action == "5") {
            changeStatus(actionSummary, cryoLiveStatus, backgroundInfo, commentsLabel, actionRow);
        } else if (action == "4") {
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