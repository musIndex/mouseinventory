<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%=HTMLGeneration.getPageHeader(null, true,false) %>
<%=HTMLGeneration.getNavBar("submission.jsp", false, false)%>


<jsp:useBean id="newMouse" class="edu.ucsf.mousedatabase.beans.MouseSubmission" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="newMouse"/>
<jsp:useBean id="submitterData" class="edu.ucsf.mousedatabase.beans.UserData" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="submitterData"/>
<div class="site_container">
  <p class="main_header">New Submission: Step 2</p>
  <div class="category">
    <div class="two_column_left">
      <div class="formbody">
        <form name="MouseTypeForm" id="MouseTypeForm" method="post" action="submitformMouseDetails.jsp">
          <table class="inputForm" style="width: 60%">
            <tr class="formFieldH">
              <td class="formHeaderCell" colspan="2">Rodent Information</td>
            </tr>
            <tr class="formField">
              <td class="formLeft">
                Submisssion<br>
                Category
              </td>
              <td class="formRight">
                <div class="formRightButtonSelect" style="width: 238.8px">
                  <div class="formTopButton">
                    <input style="height: 17px;width: 17px;" type="radio"  value="Mutant Allele" name="mouseType" <%=HTMLGeneration.isChecked(newMouse.isMA()) %>onclick="UpdateSelectedMouseType()" required>
                    <p class="formButtonText">Mutant Allele</p>
                  </div>
                  <div class="formMidButton">
                    <input style="height: 17px;width: 17px;" type="radio" value="Transgene" name="mouseType" <%=HTMLGeneration.isChecked(newMouse.isTG()) %>onclick="UpdateSelectedMouseType()" required>
                    <p class="formButtonText">Transgene</p>
                  </div>
                  <div class="formBottomButton">
                    <input style="height: 17px;width: 17px;" type="radio" value="Inbred Strain" name="mouseType" <%=HTMLGeneration.isChecked(newMouse.isIS()) %> onclick="UpdateSelectedMouseType()" required>
                    <p class="formButtonText">Inbred/Outbred/Mixed Strain</p>
                  </div>
                </div>
              </td>
            </tr>
            <tr class="formField" id="isPublishedSection" style="<%=HTMLGeneration.rowVisibility(newMouse.isTG() || newMouse.isMA()) %>">
              <td class="formLeft">
                Publication Status
              </td>
              <td class="formRight">
                <div class="formRightButtonSelect" style="width: 238.8px">
                  <div class="formTopButton">
                    <input style="height: 17px;width: 17px;" type="radio" name="isPublished"
                           value="Yes" onclick="UpdateSelectedMouseType()" <%=HTMLGeneration.isChecked(newMouse.getIsPublished() != null && newMouse.isPublished()) %> >
                    <p class="formButtonText">Published</p>

                  </div>
                  <div class="formBottomButton">
                    <input style="height: 17px;width: 17px;" type="radio" name="isPublished"
                           value="No" onclick="UpdateSelectedMouseType()" <%=HTMLGeneration.isChecked(newMouse.getIsPublished() != null && !newMouse.isPublished()) %> >
                    <p class="formButtonText">Unpublished</p>

                  </div>
                </div>
              </td>
            </tr>
          </table>

          <input type="hidden" value="Random insertion" name="transgenicType">
          <div class="spacing_div_minix2"></div>
          <div id="nextButton" class="MSU_green_button" style="width: 20%;margin-left: 293.2px;<%=HTMLGeneration.elementVisibility(newMouse.hasType()) %>">
            <input type="hidden" name="process" value="true">
            <input type="submit" value="Next" style="width: 100%;height: 100%;background-color: transparent;border: none;font-size: 19px;color: white;">
          </div>
        </form>
      </div>
    </div>
    <div class="two_column_right">
      <div class="sidebar_desc" style="height:400px;width: 100%;margin-left:-100px;padding-left: 10px;margin-top: 0px;padding-top: 3px;padding-right: 6px;">
        <div>
          <a class="anchor_no_underline" href="submission.jsp"><p style="text-decoration: underline;margin-block-start: 0em" class="label_text">Back to step 1</p></a>
          <p class="block_form_desc_text">For the rodent you want to submit, please select a category from the three choices listed. For mutant alleles or transgenes, respond to the query about publication.<br><br>

            Choose </p><p class="block_form_label_text">Mutant Allele</p> <p class="block_form_desc_text">when a gene in the rodent has been modified in some way (e.g. targeted disruption such as replacement with a neo cassette; creation of a floxed allele by
            insertion of loxP sites; targeted knock-in of a sequence such as Cre, a reporter gene, or a rodent or human gene; spontaneous mutation, endonuclease-mediated, etc.)<br><br>

          Choose </p><p class="block_form_label_text">Transgene </p> <p class="block_form_desc_text">when a DNA construct (perhaps a BAC) has been randomly inserted into the genome, such that the rodent expresses a particular sequence
          (e.g. Cre, a reporter gene, a rodent or human gene) under the control of a regulatory sequence included in the construct. Note: a knock-in into a specific locus is classified as a ‘mutant allele.’ However, if a gene is disrupted by a random insertion of
          a DNA construct, then the modification, while it is an allele of the disrupted gene, is classified as a transgene.<br><br>

          Choose </p><p class="block_form_label_text">Inbred/Outbred/Mixed Strain </p> <p class="block_form_desc_text">when the major genetic characteristic of the rodent is a member of a particular inbred strain.<br><br>

          After selecting a category, please choose the rodent's publication status.<br>

          Choose </p><p class="block_form_label_text">Published </p> <p class="block_form_desc_text">when the rodent has been published in a research/scientific paper and has a pubmed ID.<br><br>

          Choose </p><p class="block_form_label_text">Unpublished</p> <p class="block_form_desc_text"> when the rodent has NOT been published in a research/scientific paper and does NOT have a pubmed ID.
        </div>
      </div>
    </div>
  </div>
</div>

<%=HTMLGeneration.getWebsiteFooter()%>