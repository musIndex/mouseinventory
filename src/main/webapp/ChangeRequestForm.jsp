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
      table = getMouseTable(mice,false,false,true,true,true);
    }
%>
<script>
function updateRequestFormUI(selected) {
  if (selected) {
    $(".form_controls").show();
    validateInput();
  }
  if (selected == <%= Action.OTHER.ordinal() %>) {
    $(".add_holder").hide();
  }
  else if (selected == <%= Action.ADD_HOLDER.ordinal() %>) {
    $(".add_holder").show();
    $("#background_info").show();
  }
  else if (selected) {  //remove holder or change status
    $(".add_holder").show();
    $("#background_info").hide();
  }
  else {
    $(".form_controls").hide();
    $(".form_submission").hide();
    $(".form_invalid").hide();
  }
}

function validateInput() {
  var data = $("#changerequestform").serializeObject();
  var valid = true;
  
  if (!data.actionRequested){
    return;
  }
  if (data.actionRequested == <%= Action.OTHER.ordinal() %>) {
    valid = !!data.userComment;
  }
  else if (data.actionRequested == <%= Action.ADD_HOLDER.ordinal() %> ||
      	   data.actionRequested == <%= Action.REMOVE_HOLDER.ordinal() %> ||
  		   data.actionRequested == <%= Action.CHANGE_CRYO_LIVE_STATUS.ordinal() %>) {
    valid = valid && data.holderId != -1;
    valid = valid && (data.holderId > 0 || (data.holderId == -2 && data.holderName));
    valid = valid && data.facilityId != -1;
    valid = valid && (data.facilityId > 0 || (data.facilityId == -2 && data.facilityName));
  }
  
  
  if (valid) {
    $(".form_submission").show();
    $(".form_invalid").hide();
  }
  else {
    $(".form_submission").hide();
    $(".form_invalid").show();
  }
}


$(document).ready(function(){
  $("#holderId").change(function(){
    $("#otherHolderSpan").toggle($(this).val() == -2);
  }).change();
  $("#facilityId").change(function(){
    $("#otherFacilitySpan").toggle($(this).val() == -2);
  }).change();
  $(".change_request_form > ul > li a.btn").click(function(){
    var selected = $(this).parent().find('input[type=radio]').prop('checked',true).val();
    $(this).addClass('active');
    $(this).parent().siblings().find("a.btn").removeClass('active');
    updateRequestFormUI(selected);
    return false;
  });
  $("#changerequestform select").change(validateInput);
  $("#changerequestform input[type=text], form textarea").keyup(validateInput);
  updateRequestFormUI();
});

</script>
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
<div class='alert alert-error'><%=message.replace("|", "<br>") %></div>
<% } %>
<%= table %>
<form id="changerequestform" action="SubmitChangeRequest" method="post">
<div class='well' style="float:left; width: 400px; margin: 20px 20px 20px 0">
  <h3>1. Enter <font color="red">your</font> contact information:</h3>
    <input type="hidden" name="mouseID" value="<%= mouseID %>">
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
    </tr>
    </table>
  </div>
  <div style="float:left; min-width: 350px;">
    <div class='change_request_form well cf' style="margin:20px 20px 20px 0">
    <h3>2. Specify requested changes:</h3>
      <ul>
        <li>
          <input type="radio" name="actionRequested" value="<%= Action.ADD_HOLDER.ordinal() %>" <%= (changeRequest.actionRequested() == Action.ADD_HOLDER) ? "checked" : "" %> >
          <a class='btn btn-success' href='#'><i class='icon-white icon-plus'></i> Add a holder</a>
          
        </li>
        <li>
        
        <input type="radio" name="actionRequested" value="<%= Action.REMOVE_HOLDER.ordinal() %>"<%= (changeRequest.actionRequested() == Action.REMOVE_HOLDER) ? "checked" : "" %>>
        <a class='btn btn-danger' href='#'><i class='icon-white icon-remove'></i> Remove a holder</a>
        </li>
        <li>
        <input type="radio" name="actionRequested" value="<%= Action.CHANGE_CRYO_LIVE_STATUS.ordinal() %>"<%= (changeRequest.actionRequested() == Action.CHANGE_CRYO_LIVE_STATUS) ? "checked" : "" %>>
        <a class='btn btn-info' href='#'><i class='icon-white icon-tag'></i> Change status of a holder</a>
        </li>
        <!--
        <li>
        <input type="radio" name="actionRequested" value="<%//= Action.MARK_ENDANGERED.ordinal() %>" <%//= (changeRequest.actionRequested() == Action.MARK_ENDANGERED) ? "checked" : "" %>>
        Mark this mouse as Endangered. (Holder is considering eliminating
        this mouse from his/her colony. If that holder is the only one who
        maintains the mouse, or if there is only one other holder, the mouse
        will be added to the "endangered mouse" list)
        </li>
        -->
        <li>
        <input type="radio" name="actionRequested" value="<%= Action.OTHER.ordinal() %>" <%= (changeRequest.actionRequested() == Action.OTHER) ? "checked" : "" %>>
        <a class='btn' href='#'><i class='icon-pencil'></i> Make other changes</a>
        </li>
      </ul>
      <div class='form_controls'>
        <div class='add_holder'>
         <ul>
           <li>
            Holder: <%= getHolderSelect("holderId", changeRequest.getHolderId()) %>
            <span id="otherHolderSpan">
              Specify holder name:
              <input type="text" name="holderName" value="<%= emptyIfNull(changeRequest.getHolderName()) %>" size="20">
            </span>    
            </li>
            <li>
            Facility: <%= getFacilitySelect("facilityId", changeRequest.getFacilityId()) %>
            <span id="otherFacilitySpan">
               Specify facility name:
              <input type="text" name="facilityName" value="<%= emptyIfNull(changeRequest.getFacilityName()) %>" size="20">
            </span>
      
            </li>
            <li>
            Status: <%=genSelect("cryoLiveStatus", new String[]{"Live only","Live and Cryo","Cryo only"},"Live only", null)%>
            </li>
          </ul>
          <p id='background_info' style="margin-left:25px; width: 350px">
          <b>If you have <font color="red">genetic background information</font>
          for the mouse in the new holder's colony or if you want to add
          a different unoffical name for the mouse enter it here:</b><br>
          <input type="text" size="50" name="geneticBackgroundInfo"><br>
        If you have additional comments, add them in the box below.<br>
          </p>
        </div>
        Comments:
        <textarea rows="8" cols="80" name="userComment"></textarea><br>
        <div class='form_submission'>
          <input type="submit" class="btn btn-primary" value="Submit Change Request"></div>
        </div>
        <div class='form_invalid'>
          <i>Please completely fill out the form</i>
        </div>
      </div>
  </div>
</form>
<% } //end not success %>
</div>

