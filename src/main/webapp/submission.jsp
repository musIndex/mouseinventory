<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.servlets.LoginServlet" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%=HTMLGeneration.getPageHeader(null, true, false, "onload=\"setFocus('MouseForm', 'firstName')\"") %>
<%=HTMLGeneration.getNavBar("submission.jsp", false)%>

<jsp:useBean id="newMouse" class="edu.ucsf.mousedatabase.beans.MouseSubmission" scope="session"></jsp:useBean>
<jsp:useBean id="submitterData" class="edu.ucsf.mousedatabase.beans.UserData" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="newMouse"/>
<jsp:setProperty property="*" name="submitterData"/>

<%
    if ("true".equals(request.getParameter("process"))) {
        String holderName = request.getParameter("holderName");

        boolean contactInfoValid = submitterData.ValidateContactInfo();
        boolean holderDataValid = newMouse.ValidateHolderInfo();

        if (contactInfoValid && holderDataValid) {
%>
<%
            return;
        }
    }
    ArrayList<String> allFacs = DBConnect.getAllFacilityNames(false);
    ArrayList<String> allHldrs = DBConnect.getAllHolderNames(false);

    String[] facilityList = new String[allFacs.size()];
    allFacs.toArray(facilityList);
    String[] holderList = new String[allHldrs.size()];
    allHldrs.toArray(holderList);
%>

<div class="site_container">
    <p class="main_header">New Submission: Step 1</p>
    <div class="category">
        <div class="two_column_left">
            <div class="formbody">
                <%

                    if (request != null && request.getRemoteUser() != null && request.getRemoteUser().equalsIgnoreCase("admin")) {
                %>
                <p><b>ADMIN LOGGED IN (<%=request.getRemoteUser() %>)</b>&nbsp;
                    <button class="btn" onclick="AutoPopulateContactInfo()" value="AutoFill">Auto Fill Data</button>
                </p>
                <%
                    }%>
                <form action="submission.jsp" method="post" name="MouseForm" id="MouseForm">

                    <table class="inputForm" style="width:55%;margin-left: 0px;margin-right: auto;border-spacing: 0px">


                        <tr class="formFieldH">
                            <td style="width: 100%;text-align: center;border: solid 1px black;height: 60px" colspan="2">Contact Information</td>
                        </tr>

                        <tr class="formField">
                            <td class="formLeft">First Name</td>
                            <td class="formRight">
                                <input class="formInput" type="text" name="firstName" value="<%=HTMLGeneration.emptyIfNull(submitterData.getFirstName()) %>" id="userid" required>
                                <%--                                <span class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getFirstNameErr()) %></span>--%>
                            </td>
                        </tr>
                        <tr class="formFieldAlt">
                            <td class="formLeft">Last Name</td>
                            <td class="formRight">
                                <input class="formInput" type="text" id="lastName" name="lastName" value="<%=HTMLGeneration.emptyIfNull(submitterData.getLastName()) %>" required>
                                <%--                                <span class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getLastNameErr()) %></span>--%>
                            </td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft">Department</td>
                            <td class="formRight">
                                <input class="formInput" type="text" id="department" name="department" value="<%=HTMLGeneration.emptyIfNull(submitterData.getDepartment()) %>" required>
                                <%--                                <span class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getDepartmentErr()) %></span>--%>
                            </td>
                        </tr>
                        <tr class="formFieldAlt">
                            <td class="formLeft">Email</td>
                            <td class="formRight">
                                <input class="formInput" type="email" name="email" id="Email" value="<%=HTMLGeneration.emptyIfNull(submitterData.getEmail()) %>" required>
                                <%--                                <span class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getEmailErr()) %></span>--%>
                            </td>
                        </tr>
                        <tr class="formFieldAlt">
                            <td class="formLeft">Holder</td>
                            <td class="formRight" onchange="">
                                <%=HTMLGeneration.genSelect("holderName", (String[]) holderList, HTMLGeneration.chooseOneIfNull(newMouse.getHolderName()), "onChange='checkOtherHolderName()' !important style=\"width:222.8px;margin-top:4px;margin-bottom: -7px;\" required")%>
                                <div class="spacing_div_minix2"></div>
                                <%--                                <span class="validationError"><%=HTMLGeneration.emptyIfNull(newMouse.getHolderNameErr()) %></span>--%>
                                <span id="otherHolderSpan" style="<%=HTMLGeneration.rowVisibility(newMouse.hasOtherHolderName()) %>">
                                    <input class="formInput" type="text" name="otherHolderName" value="<%=HTMLGeneration.emptyIfNull(newMouse.getOtherHolderName()) %>" style="margin-bottom: 4px">
<%--                                    <span class="validationError"><%=HTMLGeneration.emptyIfNull(newMouse.getOtherHolderNameErr()) %></span>--%>
                                </span>
                            </td>
                        </tr>
                        <tr class="formFieldAlt">
                            <td class="formLeft">Facility</td>
                            <td class="formRight" onchange="">
                                <%=HTMLGeneration.genSelect("holderFacility", (String[]) facilityList, HTMLGeneration.emptyIfNull(newMouse.getHolderFacility()), "onChange='checkOtherFacility()' !important style=\"width:222.8px;margin-top:4px;margin-bottom: -7px;\" required")%>
                                <div class="spacing_div_minix2"></div>
                                <%--                                <span class="validationError"><%=HTMLGeneration.emptyIfNull(newMouse.getHolderFacilityErr()) %></span>--%>
                                <span id="otherFacilitySpan" style="<%=HTMLGeneration.rowVisibility(newMouse.hasOtherFacilityName()) %>">
                                    <input class="formInput" type="text" name="otherHolderFacility" value="<%=HTMLGeneration.emptyIfNull(newMouse.getOtherHolderFacility()) %>" style="margin-bottom: 4px">
<%--                                    <span class="validationError"><%=HTMLGeneration.emptyIfNull(newMouse.getOtherFacilityErr()) %></span>--%>
                                </span>
                            </td>
                        </tr>
                        <tr class="formFieldAlt">
                            <td class="formLeft">Rodent type</td>
                            <td class="formRight">
                                <div style="width: 222.8px;margin-left: auto;margin-right: auto;height: 50px">
                                    <div style="width: 50%;float: left;margin-top: 13px;text-align: left;">
                                        <input type ="radio" id ="mouse" name ="rodent" value ="mouse" onclick="document.getElementById('MouseForm').action='submitforminit.jsp'" style="height: 17px;width: 17px;" required>
                                        <p class="description_text" style="display: inline;font-size: 17px">Mouse</p>
                                    </div>
                                    <div style="width: 50%;float: right;margin-top: 13px;text-align: right;">
                                        <input type ="radio" id ="rat" name ="rodent" value ="rat" onclick="document.getElementById('MouseForm').action='submitratinit.jsp'" style="height: 17px;width: 17px;" required>
                                        <p class="description_text" style="display: inline;font-size: 17px">Rat</p>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                    <div class="spacing_div_minix2"></div>
                    <div class="MSU_green_button" style="width: 20%;margin-left: 255.98px;">
                        <input type="hidden" name="process" value="true">
                        <input type="submit" value="Next" style="width: 100%;height: 100%;background-color: transparent;border: none;font-size: 19px;color: white;">
                    </div>

                </form>
            </div>
        </div>
        <div class="two_column_right">
            <div class="sidebar_desc" style="width: 100%;margin-left:-100px;padding-left: 10px;margin-top: 0px;padding-top: 3px;padding-right: 6px;">
                <p class="block_form_label_text">Before completing a submission form</p><p class="block_form_desc_text">, use the search feature above to determine if the rodent is already listed in the inventory. <br><br>Each submission should be for an</p>
                <p class="block_form_label_text"> individual</p><p class="block_form_desc_text"> mutant allele or transgene (or inbred strain) - generically referred to as a "rodent", irrespective of whether the allele or transgene is maintained in combination with other mutant alleles or transgenes.
                If it is, and if you want to note that it is also being housed by another investigator or to add information about the rodent, do not use this form. Instead, go to the</p>
                <a class="anchor_no_underline" href="MouseReport.jsp">
                    <p style="color: black;display: inline;text-decoration: underline" class="block_form_label_text">Rodent Records</p>
                </a>
                <p class="block_form_desc_text">page, find the one for that rodent, click on "Request change in record" (under the name of that rodent), and complete the form.<br><br>
                    If that individual mutant allele or transgene is not in the database, even if a similar one is, please complete submission form.<br><br>
                    Select the field and begin typing the last name of the holder you want to select until it appears in the field. If the holder/facility is not included in the list, choose 'other' from the drop down list and enter the information
                </p>
            </div>
        </div>
    </div>
</div>

<%=HTMLGeneration.getWebsiteFooter()%>