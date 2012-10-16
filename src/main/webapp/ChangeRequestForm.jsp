<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.ChangeRequest.*" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%=getPageHeader(null, false,false) %>
<%=getNavBar(null, false) %>
<jsp:useBean id="changeRequest" class="edu.ucsf.mousedatabase.objects.ChangeRequest" scope="session"></jsp:useBean>
<%
    boolean success = stringToBoolean(request.getParameter("success"));
    String message = request.getParameter("message");
    int mouseID = stringToInt(request.getParameter("mouseID"));
    String table = "";
    if (success) {
      changeRequest.clearData();
    }
    else {
      ArrayList<MouseRecord> mice = DBConnect.getMouseRecord(mouseID);
      table = getMouseTable(mice,false,false,true);
    }
%>
<div class="site_container">
<% if (success) { %>
<h2>Change request completed</h2>
  We have received your request to change information about mouse <%= changeRequest.getMouseID() %> in our inventory. It
  will be reviewed by the administrator.
  <br><br>
  Thank you for helping to keep the database up-to-date!.
  <br>
  <br>
  <%@ include file='_lastMouseListLink.jspf' %>
<% } else { %>
<h2>Submit a request to change Mouse Record # <%= mouseID %></h2>
<% if (message != null && !message.isEmpty()){ %>
<div class='alert alert-error'><%=message.replace(",", "<br>") %></div>
<% } %>
<%= table %>
Enter <font color="red">your</font> name and e-mail address (required)<br>

<form action="SubmitChangeRequest" method="post">
    <input type="hidden" name="mouseID" value="<%= mouseID %>">
    <div class="textwrapper">
    <div class="whatsnew">
    <!-- <a href="/media/ChangeForm.qtl"><img src="img/VideoDemonstration.gif" alt=""></a> --->
    </div>
    <div class="about">
    <table>
        <tr>
            <td><font color="red">*</font> First Name</td>
            <td><input type="text" size="30" name="firstname"
            value="${changeRequest.firstname}"></td>
        </tr>
        <tr>
            <td><font color="red">*</font> Last Name</td>
            <td><input type="text" size="30" name="lastname"
            value="${changeRequest.lastname}"></td>
        </tr>
        <tr>
            <td><font color="red">*</font> Email Address</td>
            <td><input type="text" size="30" maxlength="" name="email"
            value="${changeRequest.email}"></td>
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
      Holder: <%= getHolderSelect("holderId", changeRequest.getHolderId()) %>

      <span id="otherHolderSpan" style="<%=rowVisibility(changeRequest.getHolderName() != null) %>" >
        Specify holder name:
        <input type="text" name="holderName" 
        value="<%= emptyIfNull(changeRequest.getHolderName()) %>" size="20">
      </span>

      <br>
      Facility: <%= getFacilitySelect("facilityId", changeRequest.getFacilityId()) %>
      
      <span id="otherFacilitySpan" style="<%=rowVisibility(changeRequest.getFacilityName() != null) %>">
         Specify facility name:
        <input type="text" name="facilityName" 
           value="<%= emptyIfNull(changeRequest.getFacilityName()) %>" size="20">
      </span>

      <br>
      Status: <%=genSelect("cryoLiveStatus",
          new String[]{"Live only","Live and Cryo","Cryo only"},"Live only", null)%>

      </td>
    </tr>
    <tr>
    <td valign="top"  colspan="2">
      <input type="radio" name="actionRequested" value="<%= Action.ADD_HOLDER.ordinal() %>" <%= (changeRequest.actionRequested() == Action.ADD_HOLDER) ? "checked" : "" %> >
      Add the selected holder to this record <br>
      <div style="position: relative; left: 25px;">
      <b>If you have <font color="red">genetic background information</font>
      for the mouse in the new holder's colony or if you want to add
      a different unoffical name for the mouse enter it here:</b><br>
      <input type="text" size="50" name="geneticBackgroundInfo"><br>
      If you have additional comments, add them in the box below.<br>
      </div>
      <input type="radio" name="actionRequested" value="<%= Action.REMOVE_HOLDER.ordinal() %><%= (changeRequest.actionRequested() == Action.REMOVE_HOLDER) ? "checked" : "" %>">
      Delete the selected holder from this record <br>
      <!--
      <input type="radio" name="actionRequested" value="<%= Action.MARK_ENDANGERED.ordinal() %> <%= (changeRequest.actionRequested() == Action.MARK_ENDANGERED) ? "checked" : "" %>">
      Mark this mouse as Endangered. (Holder is considering eliminating
      this mouse from his/her colony. If that holder is the only one who
      maintains the mouse, or if there is only one other holder, the mouse
      will be added to the "endangered mouse" list) <br>
      -->
      <input type="radio" name="actionRequested" value="<%= Action.OTHER.ordinal() %> <%= (changeRequest.actionRequested() == Action.OTHER) ? "checked" : "" %>">
      Click here if you do not want to add or delete a holder, but do want to make
      suggestions for changes in the record, then enter them in the box below:
    </td>
    </tr>
      <tr>
        <td valign="top" colspan="2"><textarea rows="8" cols="80" name="userComment"></textarea></td>
      </tr>
      <tr>
        <td colspan="2">
        <input type="submit" class="btn btn-primary" value="Submit change request">
        </td>
      </tr>
    </table>
    </div>
</div>
</form>
<% } %>
</div>

