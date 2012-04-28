<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%=HTMLGeneration.getPageHeader(null, true,false,"onload=\"setFocus('MouseForm', 'firstName')\"") %>
<%=HTMLGeneration.getNavBar("submitforminit.jsp", false) %>

<jsp:useBean id="newMouse" class="edu.ucsf.mousedatabase.beans.MouseSubmission" scope="session"></jsp:useBean>
<jsp:useBean id="submitterData" class="edu.ucsf.mousedatabase.beans.UserData" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="newMouse"/>
<jsp:setProperty property="*" name="submitterData"/>

<%
  if("true".equals(request.getParameter("process")))
  {
    String holderName = request.getParameter("holderName");

    boolean contactInfoValid = submitterData.ValidateContactInfo();
    boolean holderDataValid = newMouse.ValidateHolderInfo();

    if(contactInfoValid && holderDataValid)
    {
      %>
        <jsp:forward page="submitformMouseType.jsp" />
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
<div class="pagecontent">
<div class="formbody">
<div class="introduction">
<h2>New Submission: Step 1</h2>

<p class="header3">
Each submission should be for an
<span style="color: red;text-decoration: underline">individual</span>
mutant allele or transgene (or inbred strain) - generically referred
to as a &quot;mouse&quot;, irrespective of whether the allele or transgene
is maintained in combination with other mutant alleles or transgenes.
</p>
<p class="header3"><span class=red>Before completing a
submission form, use the Search feature above to determine if the mouse
is already listed in the inventory.</span> If it is, and if you want to note
that it is also being housed by another investigator or to add
information about the mouse, <span class=red>do not</span> use this
form. Instead, go to the "<a href="MouseReport.jsp">Mouse List</a>," find the
record for that mouse, click on "Request change" (under the mouse name of that mouse),
and complete the form.
<br>
<br>
If that individual mutant allele or transgene is not in the database, even if a
similar one is, please complete submission form.</p>
<p><font color="red">*</font>Indicates required field.</p>
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
<form action="submitforminit.jsp" method="post" name="MouseForm" id="MouseForm">

<table class="inputForm" style="width:100%">


  <tr class="formFieldH">
    <th colspan="2"><b>Contact Info (name of person completing
    this form)</b></th>
  </tr>

  <tr class="formField">
    <td style="width: 40%"><font color="red">* </font>First Name</td>
    <td><INPUT TYPE="TEXT" name="firstName" value="<%=HTMLGeneration.emptyIfNull(submitterData.getFirstName()) %>" size="40" maxlength="128"
      id="userid"> <span class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getFirstNameErr()) %></span></td>
  </tr>
  <tr class="formFieldAlt">
    <td><font color="red">* </font>Last Name</td>
    <td><INPUT TYPE="TEXT" id="lastName" name="lastName" value="<%=HTMLGeneration.emptyIfNull(submitterData.getLastName()) %>" size="40" maxlength="128" >
    <span class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getLastNameErr()) %></span></td>
  </tr>
  <tr class="formField">
    <td><font color="red">* </font>Dept</td>
    <td><INPUT TYPE="TEXT" id="department" name="department" value="<%=HTMLGeneration.emptyIfNull(submitterData.getDepartment()) %>" size="40" maxlength="255">
    <span class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getDepartmentErr()) %></span></td>
  </tr>
  <tr class="formFieldAlt">
    <td><font color="red">* </font>Email</td>
    <td><INPUT TYPE="TEXT" name="email" id="Email" value="<%=HTMLGeneration.emptyIfNull(submitterData.getEmail()) %>" size="40" maxlength="128">
    <span class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getEmailErr()) %></span></td>
  </tr>
  <tr class="formField">
    <td><font color="red">* </font>Tel.</td>
    <td><INPUT TYPE="TEXT" id="telephoneNumber" name="telephoneNumber" value="<%=HTMLGeneration.emptyIfNull(submitterData.getTelephoneNumber()) %>" size="40" maxlength="32">
    <span class="validationError"><%=HTMLGeneration.emptyIfNull(submitterData.getTelephoneNumberErr()) %></span></td>
  </tr>

  <tr class="formFieldAlt">
    <td valign="top"><font color="red">* </font>Holder (investigator
    with an approved animal protocol)
    <br>
    Select the field and begin typing the last name of the holder you want to select until it appears in the field.
    <br>
        If the holder/facility is not included in the list, choose 'other' from the drop down list and enter the information
    </td>
    <td valign="top"><%=HTMLGeneration.genSelect("holderName",(String[])holderList,HTMLGeneration.chooseOneIfNull(newMouse.getHolderName()), "onChange='checkOtherHolderName()'")%>
    <span class="validationError"><%=HTMLGeneration.emptyIfNull(newMouse.getHolderNameErr()) %></span>
    <span id="otherHolderSpan" style="<%=HTMLGeneration.rowVisibility(newMouse.hasOtherHolderName()) %>"> Specify holder name: <input type="text" name="otherHolderName" value="<%=HTMLGeneration.emptyIfNull(newMouse.getOtherHolderName()) %>" size="20"><span class="validationError"><%=HTMLGeneration.emptyIfNull(newMouse.getOtherHolderNameErr()) %></span></span>
    </td>
  </tr>

  <tr class="formFieldAlt">
    <td valign="top"><font color="red">* </font>Facility where the mouse is housed:</td>
    <td valign="top"><%=HTMLGeneration.genSelect("holderFacility",(String[])facilityList,HTMLGeneration.emptyIfNull(newMouse.getHolderFacility()), "onChange='checkOtherFacility()'")%>
    <span class="validationError"><%=HTMLGeneration.emptyIfNull(newMouse.getHolderFacilityErr()) %></span>
    <span id="otherFacilitySpan" style="<%=HTMLGeneration.rowVisibility(newMouse.hasOtherFacilityName()) %>"> Specify facility name: <input type="text" name="otherHolderFacility" value="<%=HTMLGeneration.emptyIfNull(newMouse.getOtherHolderFacility()) %>" size="20"><span class="validationError"><%=HTMLGeneration.emptyIfNull(newMouse.getOtherFacilityErr()) %></span></span>
    </td>
  </tr>

</table>

<input type="hidden" name="process" value="true">
<input type="submit" class="btn btn-primary" value="Next">
</form>
</div>
</div>
