<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.beans.*" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%=HTMLGeneration.getPageHeader(null, true,false) %>
<%=HTMLGeneration.getNavBar("submitforminit.jsp", false) %>
<jsp:useBean id="newMouse" class="edu.ucsf.mousedatabase.beans.MouseSubmission" scope="session" />
<jsp:useBean id="submitterData" class="edu.ucsf.mousedatabase.beans.UserData" scope="session" />

<div class="site_container">

<%
    if(!submitterData.ValidateContactInfo() || ! newMouse.ValidateHolderInfo())
    {
      %>
        <h2>Invalid contact information.  Please go back to step 1.</h2>
        <a href="submitforminit.jsp">Back to step 1</a>
      <%
      return;
    }
    if(!newMouse.validateMouseType())
    {
      %>
        <h2>Invalid rodent type.  Please go back to step 2.</h2>
        <a href="submitformMouseType.jsp">Back to step 2</a>

      <%
      return;
    }
    if(!newMouse.validateMouseDetails())
    {
      %>
        <h2>Invalid rodent data.  Please go back to step 3.</h2>
        <a href="submitformMouseDetails.jsp">Back to step 3</a>

      <%
      return;
    }

    String err = "";
    boolean ok = true;
  String existingRecordTable = "";
    String submissionAdminComment = "";
  int submissionID = -1;
    boolean isDuplicate = false;
    int existingRecordID = -1;

  if(newMouse.getMouseMGIID() != null && !(newMouse.getMouseMGIID().isEmpty()) && (newMouse.isTG() || newMouse.isMA()))
  {
      String repositoryCatalogID = newMouse.getMouseMGIID();
      if (repositoryCatalogID != null && !repositoryCatalogID.equalsIgnoreCase("none")) {
          existingRecordID = DBConnect.checkForDuplicates(Integer.parseInt(newMouse.getMouseMGIID()),-1);
      }
  }
  else if(newMouse.isIS())
  {
    //check supplier
    String supplier = newMouse.getISSupplier();
    if (newMouse.getISSupplierCatalogNumber() == null)
    {
      //don't dupe check records with no catalog number
      supplier = null;
    }
    else
    {
      supplier += ", " + newMouse.getISSupplierCatalogNumber();
    }
      if (supplier != null)
      {
        //TODO have different validation rules for non-jax mice
      String supplierRegex = supplier.trim().replace(",","[,]*");
      supplierRegex = supplierRegex.replace(" ","[ ]*");
          existingRecordID = DBConnect.checkForDuplicates(supplierRegex);
      }
  }
  if (existingRecordID > 0 )
  {
    ArrayList<MouseRecord> existingMice = DBConnect.getMouseRecord(existingRecordID);
    if (existingMice.size() > 0)
    {
      MouseRecord existingMouse = existingMice.get(0);

      if (!existingMouse.isHidden())
      {
          existingRecordTable = HTMLGeneration.getMouseTable(existingMice,false,true,false);
        err = "This appears to be a duplicate entry and will not be processed.  The exisiting record is shown below.";
          err += "<br><br>To add another holder to <u>this</u> rodent record, click 'request change in record,' and submit a change request.";
          isDuplicate = true;
          ok = false;
      }
      else
      {
        submissionAdminComment += "NOTE: this is a DUPLICATE of record #" + existingRecordID +
          ", but the submission was allowed because that record is hidden.  (Incomplete, deleted, or only covert holders)";
      }
    }
  }

    if (!isDuplicate)
    {
        Properties props = MouseSubmission.GetPropertiesString(submitterData,newMouse);
        submissionID = DBConnect.insertSubmission(submitterData,newMouse,props,SubmittedMouse.SubmissionFormSource);
        if (!submissionAdminComment.isEmpty())
        {
          DBConnect.updateSubmission(submissionID,"new",submissionAdminComment);
        }
        if (submissionID < 0)
        {
          ok = false;
          err += "A problem occurred while entering your submission.  Please contact the administrator.";
        }
    }
    else
    {
      newMouse.clearMouseData();
    }

%>

<% if (ok) { 
    HTMLUtilities.logRequest(request);
  %>
  <h2>Submission #<%=submissionID %> (<%=newMouse.getMouseName()%>) was successful.</h2>
  <p>We have received your request to add your rodent to the inventory.
  It will be reviewed by the administrator.
  <br>
  <br>
  If you believe you have made an error in your submission, click on "submit feedback"
  in the menu above and send an explanatory email to the Rodent Inventory Administrator.
  <br>
  Thank you.
  </p>

  <br>
  <br>
  If you would like to submit another rodent, <span style="font-size:120%" class=red>do not use the back button</span>, but
  <a href="submission.jsp">click here</a> instead.
  <br/>

  <%
  newMouse.clearMouseData();
}
else
{%>
<br>
<h4><font color="red"><%= err %></font></h4>
<%=existingRecordTable %>
<%}%>
</div>
