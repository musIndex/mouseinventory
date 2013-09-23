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
    $('#action_summary').hide();
    $(".add_holder").show();
    $('#cryo_live_status').hide();
  }
  else if (selected == <%= Action.ADD_HOLDER.ordinal() %>) {
    $(".add_holder").show();
    $('#cryo_live_status').show();
    $("#background_info").show();
    $('#action_summary').text("The holder and facility selected in step 2 will be added.").show();
  }
  else if (selected == <%= Action.REMOVE_HOLDER.ordinal() %>) {  //remove holder or change status
    $(".add_holder").show();
    $('#cryo_live_status').hide();
    $("#background_info").hide();
    $('#action_summary').text("The holder and facility selected in step 2 will be removed.").show();
  }
  else if (selected == <%= Action.CHANGE_CRYO_LIVE_STATUS.ordinal() %>) {  //remove holder or change status
    $(".add_holder").show();
    $('#cryo_live_status').show();
    $("#background_info").hide();
    $('#action_summary').text("The cryo/live status of the holder/facility selected in step 2 will be modified.").show();
  }
  else {
    $('#action_summary').hide();
    $(".form_controls").hide();
    $(".form_invalid").hide();
  }
}

function validateInput() {
  var data = $("#changerequestform").serializeObject();
  var valid = true;
  var validation_messages = [];

  if (!data.actionRequested){
    return;
  }
  if (!data.email) {
    validation_messages.push("Enter email address");
  }
  if (!data.firstname) {
    validation_messages.push("Enter first name");
  }
  if (!data.lastname) {
    validation_messages.push("Enter last name");
  }
  if (data.actionRequested == <%= Action.OTHER.ordinal() %>) {
    valid = !!data.userComment;
    if (!data.userComment)
      validation_messages.push("Specify other changes in the comment field.")
  }
  else if (data.actionRequested == <%= Action.ADD_HOLDER.ordinal() %> ||
      	   data.actionRequested == <%= Action.REMOVE_HOLDER.ordinal() %> ||
  		   data.actionRequested == <%= Action.CHANGE_CRYO_LIVE_STATUS.ordinal() %>) {
    valid = valid && data.holderId != -1;
    valid = valid && (data.holderId > 0 || (data.holderId == -2 && data.holderName));
    valid = valid && data.facilityId != -1;
    valid = valid && (data.facilityId > 0 || (data.facilityId == -2 && data.facilityName));
    if (!valid)
      validation_messages.push("Specify holder and facility names.")
  }
  
  $(".form_invalid > .details").html(validation_messages.join("<br>"));
  if (valid && validation_messages.length == 0) {
    $(".form_submission > input").prop('disabled',false).addClass('btn-primary');
    $(".form_invalid").hide();
  }
  else {
    $(".form_submission > input").prop('disabled',true).removeClass('btn-primary');
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
<div class='well' style="margin: 20px 20px 20px 0">
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
        </table>
     </div>
     <div class='well'>
        <table>
        <tr>
        <td colspan='2'><h3>2. Select Holder</h3></td>
    </tr>
    <tr>
           <td>Holder:</td>
        <td><%= getHolderSelect("holderId", changeRequest.getHolderId()) %>
        <span id="otherHolderSpan">
          Specify holder name:
          <input type="text" name="holderName" value="<%= emptyIfNull(changeRequest.getHolderName()) %>" size="20">
        </span>
        </td>
      </tr>
      <tr>
        <td>Facility:</td>
        <td><%= getFacilitySelect("facilityId", changeRequest.getFacilityId()) %>
        <span id="otherFacilitySpan">
           Specify facility name:
          <input type="text" name="facilityName" value="<%= emptyIfNull(changeRequest.getFacilityName()) %>" size="20">
        </span>
        </td>
       </tr>
       
    </table>
  </div>
  <div style='min-height: 600px'>
    <div class='change_request_form well cf' style="margin:20px 20px 20px 0">
    <h3>3. Specify requested changes: (Choose one of the four options)</h3>
      <ul class='cf'>
        <li>
          <input type="radio" name="actionRequested" value="<%= Action.ADD_HOLDER.ordinal() %>" <%= (changeRequest.actionRequested() == Action.ADD_HOLDER) ? "checked" : "" %> >
          <a class='btn btn-success' href='#'><i class='icon-white icon-plus'></i> Add selected holder</a>
          
        </li>
        <li>
        
        <input type="radio" name="actionRequested" value="<%= Action.REMOVE_HOLDER.ordinal() %>"<%= (changeRequest.actionRequested() == Action.REMOVE_HOLDER) ? "checked" : "" %>>
        <a class='btn btn-danger' href='#'><i class='icon-white icon-remove'></i> Remove selected holder</a>
        </li>
        <li>
        <input type="radio" name="actionRequested" value="<%= Action.CHANGE_CRYO_LIVE_STATUS.ordinal() %>"<%= (changeRequest.actionRequested() == Action.CHANGE_CRYO_LIVE_STATUS) ? "checked" : "" %>>
        <a class='btn btn-info' href='#'><i class='icon-white icon-tag'></i> Change live/cryo status of mouse</a>
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
      <span id='action_summary'></span>
      <table class='form_table'>
      <tr id='cryo_live_status'>
        <td>
        Status:
        </td>
        <td>
         <%=genRadio("cryoLiveStatus", new String[]{"Live only","Live and Cryo","Cryo only"},"Live only", null)%>
        </td>
       </tr>
       <tr id='background_info'><td style='width: 250px'>
        If you have <span class='red'>genetic background information</span> for the mouse in the new holder's colony enter it here:
  
       </td>
       <td>
          <input type="text" size="50" name="geneticBackgroundInfo">
          <br>
          If you want to add a different unofficial name for the mouse or have other comments, enter them in the box below.
          </td>
       </tr>
       <tr>
          <td>
        Comments:</td><td>
        <textarea rows="8" cols="80" name="userComment"></textarea>
        </tr>
        </table>
        <div class='form_invalid' style='margin-bottom: 5px'>
          <i>Please complete all three steps of the form:</i>
          <div class='details' style='margin: 3px 0 0 10px'></div>
        </div>
        <div class='form_submission'>
          <input type="submit" class="btn btn-primary" value="Submit Change Request"></div>
        </div>
      </div>
  </div>
</form>
<% } //end not success %>
</div>

