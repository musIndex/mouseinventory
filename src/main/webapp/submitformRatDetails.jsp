<%@ page import="edu.ucsf.mousedatabase.*"%>
<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<jsp:useBean id="newRat"  class="edu.ucsf.mousedatabase.beans.RatSubmission" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="newRat" />
<jsp:useBean id="submitterData" class="edu.ucsf.mousedatabase.beans.UserData" scope="session"></jsp:useBean>


<%
  String errorsMessage = "";
  String savedMessage = "";

  newRat.setIs_rat("1");
  newRat.setMtaRequired(request.getParameter("mtaRequired"));
  newRat.setCryopreserved(request.getParameter("cryopreserved"));

  newRat.clearAllErrors();

  if ("true".equals(request.getParameter("process"))) {
    //check for save or submit button clicked
    if ("I'm done! Submit Rodent".equalsIgnoreCase(request.getParameter("submitButton"))) {
      if (newRat.validateRatDetails()) {
        %>
        <jsp:forward page="submitrat.jsp" />
        <%
        return;
      }
      errorsMessage = "Please correct the errors listed in red below.";
    } else if ("I'm not done yet. Save data".equalsIgnoreCase(request.getParameter("submitButton"))) {
      savedMessage = "<span style='color:green'><b>Saved. Data will be lost if you close your browser window.</b></span>";
    }
  }

  String[] mtaOptions = {"Yes", "No", "Don't Know"};
  String ratTypeTitle = newRat.getFullRatTypeTitle();
  String ratNameStr = "<b>Rodent Name</b> (unofficial nomenclature used by holder):";
  if (newRat.isIS()) {
    ratNameStr = "<b>Inbred Strain Name</b>";
  }
%>



<%=getPageHeader("<script language=\"javascript\" type=\"text/javascript\">document.onkeypress = checkCR;</script>\r\n",
          true, false, "onload=\"setFocus('ratDetails', 'ratName')\"")%>
<%=HTMLGeneration.getNavBar("submission.jsp", false, false)%>


<script>
$(document).ready(function(){
  (function(){
    var rgdNumber = null;
    $('#popupDialog').dialog({
      autoOpen: false,
      buttons: {   "Close" : function(){ $(this).dialog("close"); }
      }
    });

    $('#rgdautofillbutton').click( function() {
        $('#popupDialog').dialog('open');
        var message = $("#popupDialogMessage");
      message.text("Please wait...");
      fillFormFromRgd();
    });

    function fillFormFromRgd()
    {
      var expected_type_name = $("#expected_type_name").data("name");
      rgdNumber = $.trim($("#ratRGDID").val());
      if (rgdNumber == null || rgdNumber == "")
        {
        var message = $("#popupDialogMessage");
          message.css("color","red");
          message.text("Please enter an RGD ID.");
          return;
        }
      $.ajax({
          type: 'GET',
          url: 'https://rest.rgd.mcw.edu/rgdws/genes/' + rgdNumber,
          dataType: 'json',
          success: rgdLookupSuccess,
          error: rgdLookupError,
          data: {},
          async: true
      });

    }

    function rgdLookupSuccess(data){
      var result = null;

      if (true) {

          if (!data.rgdId)
          {
            result = { success: false, message: "No data for RGD:" + rgdNumber};
          }
          else
          {
            var notes = "\n\n";
            var success = true;
            $("#ratRGDID").val(rgdNumber);
            $("#ratRGDIDValid").val(true);
            $("#ratRGDValidation").html(replaceBrackets(data.symbol + " - " + data.name) + " " + formatRgdLink(rgdNumber)).switchClass("bp_invalid","bp_valid");

            // if (data.pubMedID){
            //   $("#PMID").val(data.pubMedID);
            //   $("#PMIDValid").val(true);
            //   $("#PMIDValidation").html(replaceBrackets(data.pubMedTitle + " - " + data.pubMedAuthor) + " " + formatPubmedLink(data.pubMedID)).switchClass("bp_invalid","bp_valid");
            // } else {
            //   $("#PMID").val("");
            //   $("#PMIDValid").val(false);
            //   $("#PMIDValidation").html("");
            //   notes += "NOTE: RGD does not show a reference for this rodent in Pubmed. If it is unpublished, please go back to step 2, select 'unpublished,' and complete the form including the RGD ID you entered here.";
            //   success = false;
            // }

            // $("#MARgdGeneID").val(data.geneRgdID);
            // $("#MARgdGeneIDValid").val(true);
            // $("#mutantAlleleRGDValidation").html(replaceBrackets(data.geneSymbol + " - " + data.geneName) + " " + formatRgdLink(data.geneRgdID)).switchClass("bp_invalid","bp_valid");
            $("#comment").val($.trim(data.description));
            $("#rawRGDComment").val($.trim(data.description));
            result = { success: success, message: "Properties for " + data.symbol + " loaded into form.", note: notes};
            // if (!$.trim(data.description)) {
            //   result.success = false;
            //   result.message = "Failed to load description from RGD.  Please try again.  If this error persists, please notify the administrator.  To complete your submission, please manually copy the description from the RGD website."
            // }
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

    function rgdLookupError(data){
      var message = $("#popupDialogMessage");
      message.html("Unexpected error.  Please try again later.");
      message.css("color","black");
    }

    function formatRgdLink(rgdNumber)
    {
      return "<a class='MP' target='_blank' href='https://rgd.mcw.edu/rgdweb/report/gene/main.html?id=" + rgdNumber + "'>(RGD:" + rgdNumber + ")</a>";
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
<div id="popupDialog" title="Retrieving Properties from RGD">

  <div id="popupDialogMessage"></div>
</div>



<div class="site_container">
  <div class="formbody">
    <div class="introduction">
      <h2>New Submission Step 3: Rodent Details</h2>
      <%=savedMessage%>
      <h3>
        <span style="color: #23476b;text-emphasis: #23476b; font-size: larger;font-style: italic"><%=errorsMessage%></span>
      </h3>
      <a href="submitformRatType.jsp">Back to step 2</a>
      <br>
      <span style="color: #23476b;text-emphasis: #23476b; font-size: larger;font-style: italic">WARNING: if you leave this page without using the
      save button at the bottom of the page, the data you entered will be
      lost</span>
      <br> If you encounter any difficulties while completing
      this page, click 'Submit Feedback' at the top of the screen.
      <p>
        <span class=black>*</span>Indicates required field.
      </p>

      <span style="color: #23476b;text-emphasis: #23476b; font-style: italic">${newRat.ratTypeErr}</span>
      <span style="color: #23476b;text-emphasis: #23476b; font-style: italic">${newRat.transgenicTypeErr}</span>
      <span style="color: #23476b;text-emphasis: #23476b; font-style: italic">${newRat.isPublishedErr}</span>

      <%
        if (!newRat.hasType()) {
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
    <form action="submitformRatDetails.jsp" name="ratDetails" method="post" id="ratDetails">
      <div id="ratDetails">
        <table class="inputForm">
          <tr class="formFieldH">
            <th colspan="2"><%=  ratTypeTitle%></th>
          </tr>
          <tr class="formField">
            <td width="50%">
              <span class=black>*</span> <%=ratNameStr%>
            </td>
            <td>
              <input type="text" name="ratName"  value="${ newRat.ratName}"
              size="40" maxlength="255">
              <span style="color: #23476b;text-emphasis: #23476b; font-style: italic">${ newRat.ratNameErr}</span>
            </td>
          </tr>
          <%
            if (newRat.isMA()) {
              %> <tr id=expected_type_name data-name=allele style="display:none"></tr>  <%
              if (newRat.isPublished()) {
                %>
                <%@ include file="submitformMARatPublished.jspf" %>
                <input type="hidden" name="rawRGDComment" id="rawRGDComment" value="${ newRat.rawRGDComment}" />
                <%
              } else  {
                %><%@ include file="submitformMARatUnpublished.jspf" %><%
              }
            } else if (newRat.isTG()) {
              %> <tr id=expected_type_name data-name=transgene style="display:none"></tr>  <%
              if (newRat.isPublished()) {
                %>
                <%@ include file="submitformTGRatPublished.jspf" %>
                <input type="hidden" name="rawRGDComment" id="rawRGDComment" value="${ newRat.rawRGDComment}" />
                <%
              } else  {
                %><%@ include file="submitformTGRatUnpublished.jspf" %><%
              }
            } else if (newRat.isIS()) {
              %><%@ include file="submitformRatInbredStrain.jspf" %><%
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
