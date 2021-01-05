<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.servlets.LoginServlet" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%=HTMLGeneration.getPageHeader(null, true, false, "onload=\"setFocus('MouseForm', 'firstName')\"") %>
<%=HTMLGeneration.getNavBar("submission.jsp", false) %>

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
<jsp:forward page="submitformMouseType.jsp"/>
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

<%--<script>
    function rodentType() {
        var mouse = document.getElementById("mouse");
        var rat = document.getElementById("rat");
        if (mouse.checked == true) {
            document.MouseType.action = "submitforminit.jsp"
            <%System.out.println("Mouse");%>
        } else if (rat.checked == true) {
            <%System.out.println("Rat");%>
            document.MouseType.action = "submitratinit.jsp"
        }
    }
</script>--%>
<script id="access_granted" type="text/template">
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
                <p class="header3"><span style="color: #23476b;text-emphasis: #23476b; font-size: larger; font-style: italic">Before completing a
submission form, use the Search feature above to determine if the rodent
is already listed in the inventory.</span> <br>
                    If it is, and if you want to note
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

                if (request != null && request.getRemoteUser() != null && request.getRemoteUser().equalsIgnoreCase("admin")) {
            %>
            <p><b>ADMIN LOGGED IN (<%=request.getRemoteUser() %>)</b>&nbsp;
                <button class="btn" onclick="AutoPopulateContactInfo()" value="AutoFill">Auto Fill Data</button>
            </p>
            <%
                }%>
            <form action="submission.jsp" method="post" name="MouseForm" id="MouseForm">

                <table class="inputForm" style="width:100%">


                    <tr class="formFieldH">
                        <th colspan="2"><b>Contact Info (name of person completing
                            this form)</b></th>
                    </tr>

                    <tr class="formField">
                        <td style="width: 40%"><font color="black">* </font>First Name</td>
                        <td><input type="text" name="firstName"
                                   value="<%=HTMLGeneration.emptyIfNull(submitterData.getFirstName()) %>" size="40"
                                   maxlength="128"
                                   id="userid" required> <span
                                class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getFirstNameErr()) %></span>
                        </td>
                    </tr>
                    <tr class="formFieldAlt">
                        <td><font color="black">* </font>Last Name</td>
                        <td><input type="text" id="lastName" name="lastName"
                                   value="<%=HTMLGeneration.emptyIfNull(submitterData.getLastName()) %>" size="40"
                                   maxlength="128" required>
                            <span class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getLastNameErr()) %></span>
                        </td>
                    </tr>
                    <tr class="formField">
                        <td><font color="black">* </font>Dept</td>
                        <td><input type="text" id="department" name="department"
                                   value="<%=HTMLGeneration.emptyIfNull(submitterData.getDepartment()) %>" size="40"
                                   maxlength="255" required>
                            <span class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getDepartmentErr()) %></span>
                        </td>
                    </tr>
                    <tr class="formFieldAlt">
                        <td><font color="black">* </font>Email</td>
                        <td><input type="text" name="email" id="Email"
                                   value="<%=HTMLGeneration.emptyIfNull(submitterData.getEmail()) %>" size="40"
                                   maxlength="128" required>
                            <span class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getEmailErr()) %></span>
                        </td>
                    </tr>

                    <tr class="formFieldAlt">
                        <td valign="top"><font color="black">* </font>Holder (investigator
                            with an approved animal protocol)
                            <br>
                            Select the field and begin typing the last name of the holder you want to select until it
                            appears in the field.
                            <br>
                            If the holder/facility is not included in the list, choose 'other' from the drop down list and
                            enter the information
                        </td>
                        <td valign="top"><%=HTMLGeneration.genSelect("holderName", (String[]) holderList, HTMLGeneration.chooseOneIfNull(newMouse.getHolderName()), "onChange='checkOtherHolderName()'")%>
                            <span class="validationError"><%=HTMLGeneration.emptyIfNull(newMouse.getHolderNameErr()) %></span>
                            <span id="otherHolderSpan"
                                  style="<%=HTMLGeneration.rowVisibility(newMouse.hasOtherHolderName()) %>"> Specify holder name: <input
                                    type="text" name="otherHolderName"
                                    value="<%=HTMLGeneration.emptyIfNull(newMouse.getOtherHolderName()) %>" size="20"><span
                                    class="validationError"><%=HTMLGeneration.emptyIfNull(newMouse.getOtherHolderNameErr()) %></span></span>
                        </td>
                    </tr>

                    <tr class="formFieldAlt">
                        <td valign="top"><font color="black">* </font>Facility where the rodent is housed:</td>
                        <td valign="top"><%=HTMLGeneration.genSelect("holderFacility", (String[]) facilityList, HTMLGeneration.emptyIfNull(newMouse.getHolderFacility()), "onChange='checkOtherFacility()'")%>
                            <span class="validationError"><%=HTMLGeneration.emptyIfNull(newMouse.getHolderFacilityErr()) %></span>
                            <span id="otherFacilitySpan"
                                  style="<%=HTMLGeneration.rowVisibility(newMouse.hasOtherFacilityName()) %>"> Specify facility name: <input
                                    type="text" name="otherHolderFacility"
                                    value="<%=HTMLGeneration.emptyIfNull(newMouse.getOtherHolderFacility()) %>"
                                    size="20"><span
                                    class="validationError"><%=HTMLGeneration.emptyIfNull(newMouse.getOtherFacilityErr()) %></span></span>
                        </td>
                    </tr>

                    <tr class="formFieldAlt">
                        <td valign="top"><font color="black">* </font>Rodent type:</td>
                        <td style = "white-space: nowrap; display:inline-block">
                            <input type ="radio" id ="mouse" name ="rodent" value ="mouse" onclick="document.getElementById('MouseForm').action='submitforminit.jsp'" required>
                            <label for ="mouse" style = "white-space: nowrap; display:inline-block">Mouse</label>
                            <br>
                            <input type ="radio" id ="rat" name ="rodent" value ="rat" onclick="document.getElementById('MouseForm').action='submitratinit.jsp'" required>
                            <label for ="rat" style = "white-space: nowrap; display:inline-block">Rat</label>
                        </td>
                    </tr>

                </table>

                <input type="hidden" name="process" value="true">
                <input type="submit" class="btn btn-primary" value="Next">
            </form>
        </div>
    </div>
</script>

<script>
    function AutoPopulateContactInfo() {
        SetFieldValue("userid", "<%= DBConnect.loadSetting("admin_info_auto_sub_firstname").value %>");
        SetFieldValue("lastName", "<%= DBConnect.loadSetting("admin_info_auto_sub_lastname").value %>");
        SetFieldValue("department", "<%= DBConnect.loadSetting("admin_info_auto_sub_department").value %>");
        SetFieldValue("Email", "<%= DBConnect.loadSetting("admin_info_auto_sub_email").value %>");
        SetFieldValue("telephoneNumber", "<%= DBConnect.loadSetting("admin_info_auto_sub_telephone").value %>");
        SetFieldValue("holderName", "<%= DBConnect.loadSetting("admin_info_auto_sub_holder").value %>");
        SetFieldValue("holderFacility", "<%= DBConnect.loadSetting("admin_info_auto_sub_facility").value %>");
    }

</script>


<script id="access_denied" type="text/template">
    <div>
        <form method="post" action="loginServlet">
            <table class="site_container">
                <tr>
                    <td>
                        <h2>Rodent Submission Login</h2>
                        Welcome to the Rodent Research Database Application's Rodent Submission Process.<br>
                        Before you're able to view rodent records, ensure that
                        you have filled out the database application.<br>
                        If your application has been approved, please enter your information
                        below.<br><br>

                        <table>
                            <form method="post" action="loginServlet">
                                <tr>
                                    <td><label for="email">Email address:</label></td>
                                    <td><input type="text" id="email" name="email" required></td>
                                </tr>
                                <tr>
                                    <td><label for="MSU NetID">MSU NetID:</label></td>
                                    <td><input type="text" id="MSU NetID" name="MSU NetID" required></td>
                                </tr>
                                <tr>
                                    <td>
                                        <input type = hidden name="page" value="applicationLoginSubmit.jsp">
                                        <input type="submit" class ="button btn-primary" value="Login">
                                    </td>
                                </tr>
                            </form>
                        </table>
                    </td>
                    <td style="vertical-align: top;width: 50%">
                        <h2>Application Information</h2>
                        In order to access the Rodent Records and submit
                        rodents to the database, you must first fill out an application.
                        <br>
                        This application can be found by following the button below, or
                        clicking on the "Database Application" tab in the navigation bar.
                        <br>
                        <br>
                        <a href="application.jsp"><button class = "btn btn-success">Database Appication</button></a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</script>

<div id="page_content">

</div>

<script>
    var access_status = <%=LoginServlet.getAccess_granted()%>;
    var granted = document.getElementById("access_granted").innerHTML;
    var denied = document.getElementById("access_denied").innerHTML;

    if (access_status == 1) {
        document.getElementById("page_content").innerHTML = granted;
    } else {
        document.getElementById("page_content").innerHTML = denied;
    }
    <%LoginServlet.setAccess_granted(0);%>;
</script>