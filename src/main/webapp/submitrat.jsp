<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.beans.*" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%=HTMLGeneration.getPageHeader(null, true,false) %>
<%=HTMLGeneration.getNavBar("submitforminit.jsp", false) %>
<jsp:useBean id="newRat" class="edu.ucsf.mousedatabase.beans.RatSubmission" scope="session" />
<jsp:useBean id="submitterData" class="edu.ucsf.mousedatabase.beans.UserData" scope="session" />

<div class="site_container">

<%
    if(!submitterData.ValidateContactInfo() || ! newRat.ValidateHolderInfo())
    {
      %>
        <h2>Invalid contact information.  Please go back to step 1.</h2>
        <a href="submitforminit.jsp">Back to step 1</a>
      <%
      return;
    }
    if(!newRat.validateRatType())
    {
      %>
        <h2>Invalid rodent type.  Please go back to step 2.</h2>
        <a href="submitformRatType.jsp">Back to step 2</a>

      <%
      return;
    }
    if(!newRat.validateRatDetails())
    {
      %>
        <h2>Invalid rodent data.  Please go back to step 3.</h2>
        <a href="submitformRatDetails.jsp">Back to step 3</a>

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

  if(newRat.getRatRGDID() != null && !(newRat.getRatRGDID().isEmpty()) && (newRat.isTG() || newRat.isMA()))
  {
      String repositoryCatalogID = newRat.getRatRGDID();
      if (repositoryCatalogID != null && !repositoryCatalogID.equalsIgnoreCase("none")) {
          existingRecordID = DBConnect.checkForDuplicates(Integer.parseInt(newRat.getRatRGDID()),-1);
      }
  }
  else if(newRat.isIS())
  {
    //check supplier
    String supplier = newRat.getISSupplier();
    if (newRat.getISSupplierCatalogNumber() == null)
    {
      //don't dupe check records with no catalog number
      supplier = null;
    }
    else
    {
      supplier += ", " + newRat.getISSupplierCatalogNumber();
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
      MouseRecord existingRat = existingMice.get(0);

      if (!existingRat.isHidden())
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
        String id = newRat.getRatRGDID();
        RGDResult result = RGDConnect.getGeneQuery(id);
        newRat.setOfficialSymbol(result.getSymbol());
        Properties props = RatSubmission.GetPropertiesString(submitterData,newRat);
        submissionID = DBConnect.insertSubmission(submitterData,newRat,props,SubmittedMouse.SubmissionFormSource);
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
      newRat.clearRatData();
    }

%>

<% if (ok) { 
    HTMLUtilities.logRequest(request);
  %>
    <div class = "category">
    <div class = "two_column_left">
  <h2>Submission #<%=submissionID %> (<%=newRat.getRatName()%>) was successful.</h2>
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
    Your submission is complete! You now can either submit another rodent or go to the homepage.
  <br>
        <br>
        <a href="submission.jsp"><button class = "btn btn-success">Submit another rodent</button></a>   <a href="about.jsp"><button class = "btn btn-info">Return to homepage</button></a>
        <br/>

</div>
        <div class = "two_column_right">
            <br>
            <img src="img/database_check.png">
            <br>
            <br>
            New listings are checked every Tuesday and Friday
            <br>
            If you'd like to inquire about your listing, or get it approved sooner, email us at <a href = "mailto:ora.carrodentdatabase@msu.edu">ora.carrodentdatabase@msu.edu</a>.
        </div></div>
  <%
  newRat.clearRatData();
}
else
{%>
<br>
<h4><font color="red"><%= err %></font></h4>
<%=existingRecordTable %>
<%}%>
</div>
