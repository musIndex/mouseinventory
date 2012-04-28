<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%=getPageHeader(null, false,false) %>
<%=getNavBar(null, false) %>
<jsp:useBean id="submitterData" class="edu.ucsf.mousedatabase.beans.UserData"
  scope="session"></jsp:useBean>
<jsp:useBean id="mouseHolderData" class="edu.ucsf.mousedatabase.beans.MouseSubmission"
  scope="session"></jsp:useBean>
<%
    response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
    response.setHeader("Pragma", "no-cache"); //HTTP 1.0
    response.setDateHeader("Expires", 0); //prevents caching at the proxy server
%>

<%
    int mouseID = stringToInt(request.getParameter("mouseID"));
    ArrayList<MouseRecord> mice = DBConnect.getMouseRecord(mouseID);
    String table = getMouseTable(mice,false,false,true);

  ArrayList<String> allFacs = DBConnect.getAllFacilityNames(false);
  ArrayList<String> allHldrs = DBConnect.getAllHolderNames(false);

  String[] facilityList = new String[allFacs.size()];
  allFacs.toArray(facilityList);
  String[] holderList = new String[allHldrs.size()];
  allHldrs.toArray(holderList);

%>
<div class="pagecontent">
<h2>Submit a request to change Mouse Record # <%= mouseID %>
</h2>
<%= table %>
Enter <font color="red">your</font> name and e-mail address (required)<br>

<form action="SubmitChangeRequest.jsp" method="post">
    <input type="hidden" name="mouseID" value="<%= mouseID %>">
    <input type="hidden" name="challenge" value="">
    <div class="textwrapper">
    <div class="whatsnew">
    <!-- <a href="/media/ChangeForm.qtl"><img src="img/VideoDemonstration.gif" alt=""></a> --->
    </div>
    <div class="about">
    <table>
        <tr>
            <td><font color="red">*</font> First Name</td>
            <td><input type="text" size="30" name="firstname"
            value="${submitterData.firstName}"></td>
        </tr>
        <tr>
            <td><font color="red">*</font> Last Name</td>
            <td><input type="text" size="30" name="lastname"
            value="${submitterData.lastName}"></td>
        </tr>
        <tr>
            <td><font color="red">*</font> Email Address</td>
            <td><input type="text" size="30" maxlength="" name="email"
            value="${submitterData.email}"></td>
        </tr>
        <tr>
        <td  colspan="2">
        If you want to add or delete a holder, begin typing the last name of
        that person until it appears in the field and also select
        the facility in which the mouse is housed from the drop down list.
        If either the holder or facility is not included in the list,
        choose 'other' at the bottom of the drop down list and enter the information.
        <br>
        </td>
        </tr>
    <tr>
      <td valign="top" colspan="2">
      Holder:
      <%=genSelect("holderName",(String[])holderList, chooseOneIfNull(mouseHolderData.getHolderName()),
          "onChange='checkOtherHolderName()'")%>
      <span id="otherHolderSpan" style="<%=rowVisibility(mouseHolderData.hasOtherHolderName()) %>">
        Specify holder name:
        <input type="text" name="otherHolderName" value="${mouseHolderData.otherHolderName}" size="20">
      </span>

      <br>
      Facility: <%=genSelect("holderFacility",(String[])facilityList,
          chooseOneIfNull(mouseHolderData.getHolderFacility()),
          "onChange='checkOtherFacility()'")%>
      <span id="otherFacilitySpan" style="<%=rowVisibility( mouseHolderData.hasOtherFacilityName()) %>">
         Specify facility name:
        <input type="text" name="otherHolderFacility" value="${mouseHolderData.otherHolderFacility}" size="20">
      </span>

      <br>
      Status: <%=genSelect("cryoLiveStatus",
          new String[]{"Live only","Live and Cryo","Cryo only"},"Live only", null)%>

      </td>
    </tr>
    <tr>
    <td valign="top"  colspan="2">
      <input type="radio" name="requestType" value="addHolder">
      Add the selected holder to this record <br>
      <div style="position: relative; left: 25px;">
      <b>If you have <font color="red">genetic background information</font>
      for the mouse in the new holder's colony or if you want to add
      a different unoffical name for the mouse enter it here:</b><br>
      <input type="text" size="50" name="backgroundInfo"><br>
      If you have additional comments, add them in the box below.<br>
      </div>
      <input type="radio" name="requestType" value="deleteHolder">
      Delete the selected holder from this record <br>
      <!--
      <input type="radio" name="requestType" value="markEndangered">
      Mark this mouse as Endangered. (Holder is considering eliminating
      this mouse from his/her colony. If that holder is the only one who
      maintains the mouse, or if there is only one other holder, the mouse
      will be added to the "endangered mouse" list) <br>
      -->
      <input type="radio" name="requestType" value="other">
      Click here if you do not want to add or delete a holder, but do want to make
      suggestions for changes in the record, then enter them in the box below:
    </td>
    </tr>
        <tr>
            <td valign="top" colspan="2"><textarea rows="8" cols="80" name="userComment"></textarea></td>
        </tr>
        <%--<tr>--%>
        <%--<td valign="top"><font color=red>*</font>Enter security key "<%= secretKey %>"
        (no quotes)</td>--%>
        <%--<td><input type="text" name="response" size="4"></td>--%>
        <%--</tr>--%>
        <tr>
            <td colspan="2">
            <input type="submit" class="btn btn-primary" value="Submit change request">
            </td>
        </tr>
    </table>
    </div>
</div>
</form>
</div>

