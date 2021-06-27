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
    if ("Submit Rodent".equalsIgnoreCase(request.getParameter("submitButton"))) {
      if (newRat.validateRatDetails()) {
        %>
        <jsp:forward page="submitrat.jsp" />
        <%
        return;
      }
      errorsMessage = "Please correct the errors listed in red below.";
    } else if ("Save Data".equalsIgnoreCase(request.getParameter("submitButton"))) {
      savedMessage = "<span style='color:green'><b>Saved. Data will be lost if you close your browser window.</b></span>";
    }
  }

  String[] mtaOptions = {"Yes", "No", "Don't Know"};
  String ratTypeTitle = newRat.getFullRatTypeTitle();
  String ratNameStr = "Rodent Name";
  if (newRat.isIS()) {
    ratNameStr = "Strain Name";
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
          url: 'https://rest.rgd.mcw.edu/rgdws/strains/' + rgdNumber,
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

            if (data.pubMedID){
              $("#PMID").val(data.pubMedID);
              $("#PMIDValid").val(true);
              $("#PMIDValidation").html(replaceBrackets(data.pubMedTitle + " - " + data.pubMedAuthor) + " " + formatPubmedLink(data.pubMedID)).switchClass("bp_invalid","bp_valid");
            } else {
              $("#PMID").val("");
              $("#PMIDValid").val(false);
              $("#PMIDValidation").html("");
              notes += "NOTE: RGD does not show a reference for this rodent in Pubmed. If it is unpublished, please go back to step 2, select 'unpublished,' and complete the form including the RGD ID you entered here.";
              success = false;
            }

            $("#MARgdGeneID").val(data.geneRgdID);
            $("#MARgdGeneIDValid").val(true);
            $("#mutantAlleleRGDValidation").html(replaceBrackets(data.geneSymbol + " - " + data.geneName) + " " + formatRgdLink(data.geneRgdID)).switchClass("bp_invalid","bp_valid");
            $("#comment").val($.trim(data.origin));
            $("#rawRGDComment").val($.trim(data.origin));
            result = { success: success, message: "Properties for " + data.symbol + " loaded into form.", note: notes};
            if (!$.trim(data.description)) {
              result.success = false;
              result.message = "Failed to load description from RGD.  Please try again.  If this error persists, please notify the administrator.  To complete your submission, please manually copy the description from the RGD website."
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

    function rgdLookupError(data){
      var message = $("#popupDialogMessage");
      message.html("Unexpected error.  Please try again later.");
      message.css("color","black");
    }

    function formatRgdLink(rgdNumber)
    {
      return "<a class='MP' target='_blank' href='https://rgd.mcw.edu/rgdweb/report/strain/main.html?id=" + rgdNumber + "'>(RGD:" + rgdNumber + ")</a>";
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
  <div id="popupDialogMessage">

  </div>
</div>

<div class="site_container">
  <p class="main_header">New Submission: Step 3</p>
  <div class="category">
    <div class="two_column_left">
      <div class="formbody">
        <form action="submitformRatDetails.jsp" name="ratDetails" method="post" id="ratDetails">
          <div id="ratDetails">
            <table class="inputForm"  style="width: 60%">
              <tr class="formFieldH">
                <td class="formHeaderCell" colspan="2"><%=ratTypeTitle%></td>
              </tr>
              <tr class="formField">
                <td class="formLeft" style="width: 17%">
                  * <%=ratNameStr%>
                </td>
                <td class="formRight" style="width: 15%">
                  <input type="text" name="ratName"  value="${ newRat.ratName}" required>
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
              <tr>
                <td colspan="2">
                  <div class="spacing_div_minix2"></div>
                  <div id="nextButton" class="MSU_green_button" style="margin-right:-3px;float:right;width: 32%;<%=HTMLGeneration.elementVisibility(newRat.hasType()) %>">
                    <input type="hidden" name="process" value="true">
                    <input type="submit" name="submitButton" value="Submit Rodent" style="width: 100%;height: 100%;background-color: transparent;border: none;font-size: 19px;color: white;">
                  </div>
                </td>
              </tr>
              <tr>
                <td colspan="2">
                  <div id="nextButton" class="MSU_green_button" style="background-color:#008183ff;margin-right:-3px;float:right;width: 32%;<%=HTMLGeneration.elementVisibility(newRat.hasType()) %>">
                    <input type="submit" name="submitButton" value="Save Data" style="width: 100%;height: 100%;background-color: transparent;border: none;font-size: 19px;color: white;">
                  </div>
                </td>
              </tr>
            </table>
          </div>
        </form>
      </div>
    </div>
    <div class="two_column_right">
      <div class="sidebar_desc" style="width: 100%;margin-left:-100px;padding-left: 10px;margin-top: 0px;padding-top: 3px;padding-right: 6px;height: 325px">
        <%=savedMessage%>
        <%--      <h3>--%>
        <%--        <span style="color: #23476b;text-emphasis: #23476b; font-style: italic"><%=errorsMessage%></span>--%>
        <%--      </h3>--%>
        <a class="anchor_no_underline" href="submitformRatType.jsp"><p style="text-decoration: underline;margin-block-start: 0em" class="label_text">Back to step 2</p></a>
        <p class="block_form_label_text" style="text-align: center">If you leave this page without saving, ALL the data entered will be lost.</p><br><br>
        <p class="block_form_desc_text">If you encounter any difficulties while completing this page, click 'Submit Feedback' at the top of the screen.</p><br><br>

        <p class="block_form_desc_text">Use the catalog link below to search for JAX mice.</p><br>
        <a class="anchor_no_underline" href="http://jaxmice.jax.org/query"><p style="text-decoration: underline;margin-block-start: 0em" class="label_text">JAX mice catalog</p></a>

        <p class="block_form_desc_text">If available, please enter the URL for the description of the rat on the supplier's website.<br><br>

        <p class="block_form_desc_text">When entering a </p><p class="block_form_label_text">comment</p><p class="block_form_desc_text">, use the field to provide a brief description of the strain, its uses,
        and other pertinent information. (Do not include detailed information that can be found
        via the link to the description in the supplier's catalog.)

        <br><br>

        *Indicates required field.</p>


        <%--      <span style="color: #23476b;text-emphasis: #23476b; font-style: italic">${newRat.mouseTypeErr}</span>--%>
        <%--      <span style="color: #23476b;text-emphasis: #23476b; font-style: italic">${newRat.transgenicTypeErr}</span>--%>
        <%--      <span style="color: #23476b;text-emphasis: #23476b; font-style: italic">${newRat.isPublishedErr}</span>--%>

        <%--        <%--%>
        <%--          if (!newRat.hasType()) {--%>
        <%--            //TODO just redirect them back to step 2, step 1 if there is no contact info saved.--%>
        <%--        %>--%>
        <%--        <p>--%>
        <%--        <span style="color: #23476b;text-emphasis: #23476b; font-style: italic"><b>Unknown Error. Please go back to--%>
        <%--            step 1 and try again</b>--%>
        <%--        </span>--%>
        <%--        </p>--%>

        <%--        <%--%>
        <%--            return;--%>
        <%--          }--%>
        <%--        %>--%>
      </div>

      <div style="height: 75px;width: 100%"></div>
      <div class="sidebar_desc" style="width: 100%;margin-left:-100px;padding-left: 10px;margin-top: 0px;padding-top: 3px;padding-right: 6px;height: 90px">
        <p class="block_form_desc_text">By clicking </p><p class="block_form_label_text">Save data</p><p class="block_form_desc_text">, you will save data that has been entered
        as long as your browser window stays open, or until you submit the rodent.<br><br>

        By clicking </p><p class="block_form_label_text">Submit rodent</p><p class="block_form_desc_text">, you will submit the rodent and complete the submission process.
      </p>
      </div>
    </div>
  </div>
</div>

<%=HTMLGeneration.getWebsiteFooter()%>
