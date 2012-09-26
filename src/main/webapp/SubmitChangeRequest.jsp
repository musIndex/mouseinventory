<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Properties" %>
<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="edu.ucsf.mousedatabase.*" %>
<%=HTMLGeneration.getPageHeader(null, true,false) %>
<%=HTMLGeneration.getNavBar(null, false) %>

<jsp:useBean id="changeRequest" class="edu.ucsf.mousedatabase.objects.ChangeRequest" scope="request"></jsp:useBean>
<jsp:setProperty property="*" name="changeRequest"/>


<%!
    private boolean paramOK(String value)
  {
        return value != null && value.trim().length() > 0 && value.toLowerCase().indexOf("http") == -1;
    }
%>
<%

    String err = "";
    String insertString = null;
    StringBuffer buf = new StringBuffer();

    //String firstname = request.getParameter("firstName");
    //String lastname = request.getParameter("lastName");
    //String email = request.getParameter("email");
    //String comment = request.getParameter("comment");
    //String mouseID = request.getParameter("mouseID");
    String requestType = request.getParameter("requestType");
    String holderName = request.getParameter("holderName");
    String facilityName = request.getParameter("holderFacility");
    String otherHolder = request.getParameter("otherHolderName");
    String otherFacility = request.getParameter("otherHolderFacility");
    String cryoLiveStatus = request.getParameter("cryoLiveStatus");
    String backgroundInfo = request.getParameter("backgroundInfo");

    if(holderName != null && holderName.equalsIgnoreCase("Other(specify)"))
      holderName = otherHolder;

    if(facilityName != null && facilityName.equalsIgnoreCase("Other(specify)"))
      facilityName = otherFacility;

    boolean fieldMissing = false;

    if(!paramOK(changeRequest.getFirstname()))
    {
        err += "First name is required<br>";
        fieldMissing=true;
    }
    if(!paramOK(changeRequest.getLastname()))
    {
      err += "Last name is required<br>";
      fieldMissing=true;
    }
    if(!paramOK(changeRequest.getEmail()))
    {
      err += "Email address is required<br>";
      fieldMissing=true;
    }
    if(changeRequest.getEmail() != null && changeRequest.getEmail().indexOf("@") == -1)
    {
      err += "Invalid email address<br>";
      fieldMissing = true;
    }
    if(!paramOK(requestType))
    {
      err += "Please select the change request type (add holder, delete holder, or other)";
      fieldMissing = true;
    }
    if(changeRequest.getMouseID() < 0)
    {
      err += "Error processing request - no mouse was specified.  Please try again.";
      fieldMissing = true;
    }

    if(!fieldMissing && (requestType.equalsIgnoreCase("addHolder") ||
        requestType.equalsIgnoreCase("deleteHolder")) &&
        (!paramOK(holderName) || !paramOK(facilityName)))
    {
      err += "Please provide both the holder name and facility.";
      fieldMissing = true;
    }

    if(!fieldMissing && (requestType.equalsIgnoreCase("addHolder") ||
        requestType.equalsIgnoreCase("deleteHolder")) &&
          (holderName.equalsIgnoreCase("Choose one") ||
           facilityName.equalsIgnoreCase("Choose one")))
    {
      err += "Please provide both the holder name and facility.";
      fieldMissing = true;
    }

    if(!fieldMissing && requestType.equalsIgnoreCase("other") &&
        !paramOK(changeRequest.getUserComment()))
    {
      err += "Please specify the changes that should be made for this record";
      fieldMissing = true;
    }

    boolean ok = true;
    if (!fieldMissing)
    {
       String comment = emptyIfNull(changeRequest.getUserComment());
       if(requestType.equalsIgnoreCase("addHolder"))
       {
           comment += " \r\nADD HOLDER: " + holderName +
           " (" + facilityName + ")" + " (" + cryoLiveStatus + ")";
           Properties props = new Properties();
        props.setProperty("Add Holder Name", holderName);
        props.setProperty("Add Facility Name", facilityName);
        changeRequest.setProperties(props);

       }
       else if(requestType.equalsIgnoreCase("deleteHolder"))
       {
           comment += " \r\nDELETE HOLDER: " + holderName +
           " (" + facilityName + ")" + " (" + cryoLiveStatus + ")";
           Properties props = new Properties();
           props.setProperty("Delete Holder Name", holderName);
           props.setProperty("Delete Facility Name", facilityName);
           changeRequest.setProperties(props);
       }
       else if(requestType.equalsIgnoreCase("markEndangered"))
       {
         comment += " \r\nMARK AS ENDANGERED";
       }

       if (backgroundInfo != null && !backgroundInfo.isEmpty())
       {
         comment += " \r\nADD BACKGROUND INFO: " + backgroundInfo;
       }
       changeRequest.setUserComment(comment);
       changeRequest.setStatus("new");

       int existingRequest = DBConnect.changeRequestExists(changeRequest);

       if (existingRequest < 0)
       {
         int requestID = DBConnect.insertChangeRequest(changeRequest);
       }
       else
       {
           ok = false;
           err = "Duplicate request not added.";
       }

    } else {
        ok = false;
        err = "Fields missing or problematic input, please go back to the change record form and correct the following:<br><br>" + err;
    }


%>
<div class="site_container">
<% if(!ok || fieldMissing) { %>

<font color="red" size="3"><%= err %></font>
<% } %>

<% if (ok) { %>

<font size="4"><br>We have received your request to change information about mouse <%= changeRequest.getMouseID() %> in our inventory. It
    will be reviewed
    by the
    administrator.<br><br>Thank you for helping to keep the database up-to-date!.</font>




<% } %>
</div>
