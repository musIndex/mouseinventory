<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Properties" %>
<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="edu.ucsf.mousedatabase.*" %>
<%=HTMLGeneration.getPageHeader(null, true,false) %>
<%=HTMLGeneration.getNavBar(null, false) %>

<jsp:useBean id="changeRequest" class="edu.ucsf.mousedatabase.objects.ChangeRequest" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="changeRequest"/>


<%!
  private boolean paramOK(String value) {
      return value != null && value.trim().length() > 0 && value.toLowerCase().indexOf("http") == -1;
  }
%>
<%

  String err = "";
  String insertString = null;
  StringBuffer buf = new StringBuffer();

  String cryoLiveStatus = request.getParameter("cryoLiveStatus");
  //TODO add cryo/live status to request field
  //todo move background info to request field
  //todo add client side form validation, or server side in the model, or combo
  
  String backgroundInfo = request.getParameter("backgroundInfo");
  

  if (changeRequest.getHolderId() > 0) {
   changeRequest.setHolderName(null); 
  }
  if (changeRequest.getFacilityId() > 0) {
   changeRequest.setFacilityName(null); 
  }

   String comment = emptyIfNull(changeRequest.getUserComment());
   
   if (backgroundInfo != null && !backgroundInfo.isEmpty())
   {
     comment += " \r\nADD BACKGROUND INFO: " + backgroundInfo;
   }
   changeRequest.setUserComment(comment);
   changeRequest.setStatus("new");

   int existingRequest = DBConnect.changeRequestExists(changeRequest);
   boolean ok = true;
   if (existingRequest < 0) {
     changeRequest.setRequestSource("Change request form");
     int requestID = DBConnect.insertChangeRequest(changeRequest);
   }
   else {
       ok = false;
       err = "Duplicate request not added.";
   }



%>
<div class="site_container">
<% if(!ok) { %>

<font color="red" size="3"><%= err %></font>
<% } %>

<% if (ok) { %>

<font size="4"><br>We have received your request to change information about mouse <%= changeRequest.getMouseID() %> in our inventory. It
    will be reviewed
    by the
    administrator.<br><br>Thank you for helping to keep the database up-to-date!.</font>
<br>
<br>
<%@ include file='_lastMouseListLink.jspf' %>




<% } %>
</div>
