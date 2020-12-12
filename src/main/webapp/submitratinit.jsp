<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html;charset=UTF-8" language="java"%>

<%=HTMLGeneration.getPageHeader(null, true,false,"onload=\"setFocus('RatForm', 'firstName')\"") %>
<%=HTMLGeneration.getNavBar("submitratinit.jsp", false) %>
<jsp:useBean id="newRat" class="edu.ucsf.mousedatabase.beans.RatSubmission" scope="session" />
<jsp:useBean id="submitterData" class="edu.ucsf.mousedatabase.beans.UserData" scope="session" />
<jsp:setProperty property="*" name="newRat"/>
<jsp:setProperty property="*" name="submitterData"/>

<%
    if("true".equals(request.getParameter("process")))
    {
        String holderName = request.getParameter("holderName");

        boolean contactInfoValid = submitterData.ValidateContactInfo();
        boolean holderDataValid = newRat.ValidateHolderInfo();

        if(contactInfoValid && holderDataValid)
        {
%>
<jsp:forward page="submitformRatType.jsp" />
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
    <div class="formbody">
        <div class="introduction">
            <h2>New Submission: Step 1</h2>

            <p class="header3">
                Each submission should be for an
                <span style="color: #23476b;text-emphasis: #23476b; font-size: larger;font-style: italic">individual</span>
                mutant allele or transgene (or inbred strain) - generically referred
                to as a &quot;rodent&quot;, irrespective of whether the allele or transgene
                is maintained in combination with other mutant alleles or transgenes.
            </p>
            <p class="header3"><span style="color: #23476b;text-emphasis: #23476b; font-size: larger;font-style: italic">Before completing a
submission form, use the Search feature above to determine if the rodent
is already listed in the inventory.</span> If it is, and if you want to note
                that it is also being housed by another investigator or to add
                information about the rodent, <span style="color: #23476b;text-emphasis: #23476b; font-size: larger;font-style: italic">do not</span> use this
                form. Instead, go to the "<a href="MouseReport.jsp">Rodent Records</a>" page, find the
                one for that rodent, click on "Request change in record" (under the name of that rodent),
                and complete the form.
                <br>
                <br>
                If that individual mutant allele or transgene is not in the database, even if a
                similar one is, please complete submission form.</p>
            <p><font color="black">*</font>Indicates required field.</p>
        </div>
        <%

            if (request != null && request.getRemoteUser() != null && request.getRemoteUser().equalsIgnoreCase("admin"))
            {
        %>
        <p><b>ADMIN LOGGED IN (<%=request.getRemoteUser() %>)</b>&nbsp;
            <button class="btn" onclick="AutoPopulateContactInfo()" value="AutoFill">Auto Fill Data</button>
        </p>
        <%
            }%>
        <form action="submitratinit.jsp" method="post" name="RatForm" id="RatForm">
            <table class="inputForm" style="width:100%">
                <tr class="formFieldH">
                    <th colspan="2"><b>Contact Info (name of person completing
                        this form)</b></th>
                </tr>

                <tr class="formField">
                    <td style="width: 40%"><font color="black">* </font>First Name</td>
                    <td><input type="text" name="firstName" value="<%=HTMLGeneration.emptyIfNull(submitterData.getFirstName()) %>" size="40" maxlength="128"
                               id="userid"> <span class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getFirstNameErr()) %></span></td>
                </tr>
                <tr class="formFieldAlt">
                    <td><font color="black">* </font>Last Name</td>
                    <td><input type="text" id="lastName" name="lastName" value="<%=HTMLGeneration.emptyIfNull(submitterData.getLastName()) %>" size="40" maxlength="128" >
                        <span class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getLastNameErr()) %></span></td>
                </tr>
                <tr class="formField">
                    <td><font color="black">* </font>Dept</td>
                    <td><input type="text" id="department" name="department" value="<%=HTMLGeneration.emptyIfNull(submitterData.getDepartment()) %>" size="40" maxlength="255">
                        <span class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getDepartmentErr()) %></span></td>
                </tr>
                <tr class="formFieldAlt">
                    <td><font color="black">* </font>Email</td>
                    <td><input type="text" name="email" id="Email" value="<%=HTMLGeneration.emptyIfNull(submitterData.getEmail()) %>" size="40" maxlength="128">
                        <span class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getEmailErr()) %></span></td>
                </tr>

                <tr class="formFieldAlt">
                    <td valign="top"><font color="black">* </font>Holder (investigator
                        with an approved animal protocol)
                        <br>
                        Select the field and begin typing the last name of the holder you want to select until it appears in the field.
                        <br>
                        If the holder/facility is not included in the list, choose 'other' from the drop down list and enter the information
                    </td>
                    <td valign="top"><%=HTMLGeneration.genSelect("holderName",(String[])holderList,HTMLGeneration.chooseOneIfNull(newRat.getHolderName()), "onChange='checkOtherHolderName()'")%>
                        <span style="color: #23476b;text-emphasis: #23476b; font-style: italic"><%=HTMLGeneration.emptyIfNull(newRat.getHolderNameErr()) %></span>
                        <span id="otherHolderSpan" style="<%=HTMLGeneration.rowVisibility(newRat.hasOtherHolderName()) %>"> Specify holder name: <input type="text" name="otherHolderName" value="<%=HTMLGeneration.emptyIfNull(newRat.getOtherHolderName()) %>" size="20"><span class="validationError"><%=HTMLGeneration.emptyIfNull(newRat.getOtherHolderNameErr()) %></span></span>
                    </td>
                </tr>

                <tr class="formFieldAlt">
                    <td valign="top"><font color="black">* </font>Facility where the rodent is housed:</td>
                    <td valign="top"><%=HTMLGeneration.genSelect("holderFacility",(String[])facilityList,HTMLGeneration.emptyIfNull(newRat.getHolderFacility()), "onChange='checkOtherFacility()'")%>
                        <span style="color: #23476b;text-emphasis: #23476b; font-style: italic"><%=HTMLGeneration.emptyIfNull(newRat.getHolderFacilityErr()) %></span>
                        <span id="otherFacilitySpan" style="<%=HTMLGeneration.rowVisibility(newRat.hasOtherFacilityName()) %>"> Specify facility name: <input type="text" name="otherHolderFacility" value="<%=HTMLGeneration.emptyIfNull(newRat.getOtherHolderFacility()) %>" size="20"><span class="validationError"><%=HTMLGeneration.emptyIfNull(newRat.getOtherFacilityErr()) %></span></span>
                    </td>
                </tr>

            </table>

            <input type="hidden" name="process" value="true">
            <input type="submit" class="btn btn-primary" value="Next">
        </form>
    </div>
</div>
<script>
    function AutoPopulateContactInfo()
    {
        SetFieldValue("userid", "<%= DBConnect.loadSetting("admin_info_auto_sub_firstname").value %>");
        SetFieldValue("lastName", "<%= DBConnect.loadSetting("admin_info_auto_sub_lastname").value %>");
        SetFieldValue("department", "<%= DBConnect.loadSetting("admin_info_auto_sub_department").value %>");
        SetFieldValue("Email", "<%= DBConnect.loadSetting("admin_info_auto_sub_email").value %>");
        SetFieldValue("telephoneNumber", "<%= DBConnect.loadSetting("admin_info_auto_sub_telephone").value %>");
        SetFieldValue("holderName", "<%= DBConnect.loadSetting("admin_info_auto_sub_holder").value %>");
        SetFieldValue("holderFacility", "<%= DBConnect.loadSetting("admin_info_auto_sub_facility").value %>");
    }

</script>