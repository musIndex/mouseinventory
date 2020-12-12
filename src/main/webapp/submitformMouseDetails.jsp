<%@ page import="edu.ucsf.mousedatabase.*"%>
<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<jsp:useBean id="newMouse"  class="edu.ucsf.mousedatabase.beans.MouseSubmission" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="newMouse" />
<jsp:useBean id="submitterData" class="edu.ucsf.mousedatabase.beans.UserData" scope="session"></jsp:useBean>


<%
  String errorsMessage = "";
  String savedMessage = "";

  newMouse.setMtaRequired(request.getParameter("mtaRequired"));
  newMouse.setCryopreserved(request.getParameter("cryopreserved"));

  newMouse.clearAllErrors();

  if ("true".equals(request.getParameter("process"))) {
    //check for save or submit button clicked
    if ("I'm done! Submit Rodent".equalsIgnoreCase(request.getParameter("submitButton"))) {
      if (newMouse.validateMouseDetails()) {
        %>
        <jsp:forward page="submitmouse.jsp" />
        <%
        return;
      }
      errorsMessage = "Please correct the errors listed in red below.";
    } else if ("I'm not done yet. Save data".equalsIgnoreCase(request.getParameter("submitButton"))) {
      savedMessage = "<span style='color:green'><b>Saved. Data will be lost if you close your browser window.</b></span>";
    }
  }

  String[] mtaOptions = {"Yes", "No", "Don't Know"};
  String mouseTypeTitle = newMouse.getFullMouseTypeTitle();
  String mouseNameStr = "<b>Rodent Name</b> (unofficial nomenclature used by holder):";
  if (newMouse.isIS()) {
    mouseNameStr = "<b>Inbred Strain Name</b>";
  }
%>



<%=getPageHeader("<script language=\"javascript\" type=\"text/javascript\">document.onkeypress = checkCR;</script>\r\n",
          true, false, "onload=\"setFocus('mouseDetails', 'mouseName')\"")%>
<%=getNavBar("submitforminit.jsp", false)%>


<script>
$(document).ready(function(){
  (function(){
    var mgiNumber = null;
    $('#popupDialog').dialog({
      autoOpen: false,
      buttons: {   "Close" : function(){ $(this).dialog("close"); }
      }
    });

    $('#mgiautofillbutton').click( function() {
        $('#popupDialog').dialog('open');
        var message = $("#popupDialogMessage");
      message.text("Please wait...");
      fillFormFromMgi();
    });

    function fillFormFromMgi()
    {
      var expected_type_name = $("#expected_type_name").data("name");
      mgiNumber = $.trim($("#mouseMGIID").val());
      if (mgiNumber == null || mgiNumber == "")
        {
        var message = $("#popupDialogMessage");
          message.css("color","red");
          message.text("Please enter an MGI ID.");
          return;
        }
      $.ajax({
          type: 'GET',
          url: '/mgidata?query=allele_properties&acc_id=' + mgiNumber + '&expected_type_name=' + expected_type_name,
          dataType: 'json',
          success: mgiLookupSuccess,
          error: mgiLookupError,
          data: {},
          async: true
      });

    }

    function mgiLookupSuccess(data){
      var result = null;

      if (data.is_valid != null && data.is_valid == "true") {

          if (!data.officialSymbol)
          {
            result = { success: false, message: "No data for MGI:" + mgiNumber};
          }
          else
          {
            var notes = "\n\n";
            var success = true;
            $("#mouseMGIID").val(mgiNumber);
            $("#mouseMGIIDValid").val(true);
            $("#mouseMGIValidation").html(replaceBrackets(data.officialSymbol + " - " + data.mouseName) + " " + formatMgiLink(mgiNumber)).switchClass("bp_invalid","bp_valid");

            if (data.pubMedID){
              $("#PMID").val(data.pubMedID);
              $("#PMIDValid").val(true);
              $("#PMIDValidation").html(replaceBrackets(data.pubMedTitle + " - " + data.pubMedAuthor) + " " + formatPubmedLink(data.pubMedID)).switchClass("bp_invalid","bp_valid");
            } else {
              $("#PMID").val("");
              $("#PMIDValid").val(false);
              $("#PMIDValidation").html("");
              notes += "NOTE: MGI does not show a reference for this rodent in Pubmed. If it is unpublished, please go back to step 2, select 'unpublished,' and complete the form including the MGI ID you entered here.";
              success = false;
            }

            $("#MAMgiGeneID").val(data.geneMgiID);
            $("#MAMgiGeneIDValid").val(true);
            $("#mutantAlleleMGIValidation").html(replaceBrackets(data.geneSymbol + " - " + data.geneName) + " " + formatMgiLink(data.geneMgiID)).switchClass("bp_invalid","bp_valid");
            $("#comment").val($.trim(data.description));
            $("#rawMGIComment").val($.trim(data.description));
            result = { success: success, message: "Properties for " + data.officialSymbol + " loaded into form.", note: notes};
            if (!$.trim(data.description)) {
              result.success = false;
              result.message = "Failed to load description from MGI.  Please try again.  If this error persists, please notify the administrator.  To complete your submission, please manually copy the description from the MGI website."
            }
          }
      }
      else
      {
        result = {success: false, message: data.error_string};
      }
      var message = $("#popupDialogMessage");
      message.text(result.message);
      if (result.success)
      {
        message.css("color","green");
      }
      else if (result.note){
        message.append($("<br>")).append($("<br>")).append($("<span>",{'class':'red',text: result.note})); 
      }
      else {
        message.css("color","red");  
      }

    }

    function mgiLookupError(data){
      var message = $("#popupDialogMessage");
      message.html("Unexpected error.  Please try again later.");
      message.css("color","red");
    }

    function formatMgiLink(mgiNumber)
    {

      return "<a class='MP' target='_blank' href='http://www.informatics.jax.org/accession/MGI:" + mgiNumber + "'>(MGI:" + mgiNumber + ")</a>";
    }

    function formatPubmedLink(pubmedId)
    {
      return "<a class='MP' target='_blank' href='http://www.ncbi.nlm.nih.gov/pubmed/"+pubmedId+"?dopt=Abstract'>(Pubmed:"+pubmedId+")</a>";
    }

    function htmlEncode(value) {
        return $('<div/>').text(value).html();
    }

    function htmlDecode(value) {
        return $('<div/>').html(value).text();
    }
    
    function nlToBr(text) {
      return text.replace(/\n/g,"<br>");
    }

  })();
});




</script>
<div id="popupDialog" title="Retrieving Properties from MGI">

  <div id="popupDialogMessage"></div>
</div>



<div class="site_container">
  <div class="formbody">
    <div class="introduction">
      <h2>New Submission Step 3: Rodent Details</h2>
      <%=savedMessage%>
      <h3>
        <span style="color: #23476b;text-emphasis: #23476b; font-style: italic"><%=errorsMessage%></span>
      </h3>
      <a href="submitformMouseType.jsp">Back to step 2</a>
      <br>
      <span style="color: #23476b;text-emphasis: #23476b; font-size: larger; font-style: italic">WARNING: if you leave this page without using the
      save button at the bottom of the page, the data you entered will be
      lost</span>
      <br> If you encounter any difficulties while completing
      this page, click 'Submit Feedback' at the top of the screen.
      <p>
        <span class=black>*</span>Indicates required field.
      </p>

      <span style="color: #23476b;text-emphasis: #23476b; font-style: italic">${newMouse.mouseTypeErr}</span>
      <span style="color: #23476b;text-emphasis: #23476b; font-style: italic">${newMouse.transgenicTypeErr}</span>
      <span style="color: #23476b;text-emphasis: #23476b; font-style: italic">${newMouse.isPublishedErr}</span>

      <%
        if (!newMouse.hasType()) {
          //TODO just redirect them back to step 2, step 1 if there is no contact info saved.
      %>
      <p>
        <span style="color: #23476b;text-emphasis: #23476b; font-style: italic"><b>Unknown Error. Please go back to
            step 1 and try again</b>
        </font>
      </p>

      <%
        return;
        }
      %>
    </div>
    <form action="submitformMouseDetails.jsp" name="mouseDetails" method="post" id="mouseDetails">
      <div id="mouseDetails">
        <table class="inputForm">
          <tr class="formFieldH">
            <th colspan="2"><%=  mouseTypeTitle%></th>
          </tr>
          <tr class="formField">
            <td width="50%">
              <span class=black>*</span> <%=mouseNameStr%>
            </td>
            <td>
              <input type="text" name="mouseName"  value="${ newMouse.mouseName}"
              size="40" maxlength="255">
              <span style="color: #23476b;text-emphasis: #23476b; font-style: italic">${ newMouse.mouseNameErr}</span>
            </td>
          </tr>
          <%
            if (newMouse.isMA()) {
              %> <tr id=expected_type_name data-name=allele style="display:none"></tr>  <%
              if (newMouse.isPublished()) {
                %>
                <%@ include file="submitformMAPublished.jspf" %>
                <input type="hidden" name="rawMGIComment" id="rawMGIComment" value="${ newMouse.rawMGIComment}" />
                <%
              } else  {
                %><%@ include file="submitformMAUnpublished.jspf" %><%
              }
            } else if (newMouse.isTG()) {
              %> <tr id=expected_type_name data-name=transgene style="display:none"></tr>  <%
              if (newMouse.isPublished()) {
                %>
                <%@ include file="submitformTGPublished.jspf" %>
                <input type="hidden" name="rawMGIComment" id="rawMGIComment" value="${ newMouse.rawMGIComment}" />
                <%
              } else  {
                %><%@ include file="submitformTGUnpublished.jspf" %><%
              }
            } else if (newMouse.isIS()) {
              %><%@ include file="submitformInbredStrain.jspf" %><%
            }
          %>
        </table>
      </div>
      <input type="hidden" name="process" value="true">
      <input type="submit" class="btn" name="submitButton" value="I'm not done yet. Save data">
      (This will save data as  long as your browser window stays open, or until you submit the  rodent).
      <br> <br>
      <input type="submit" class="btn btn-primary"  name="submitButton" value="I'm done! Submit Rodent">
    </form>
  </div>
</div>
