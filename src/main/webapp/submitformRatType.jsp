<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%=HTMLGeneration.getPageHeader(null, true,false) %>
<%=HTMLGeneration.getNavBar("submitforminit.jsp", false) %>
<jsp:useBean id="newRat" class="edu.ucsf.mousedatabase.beans.RatSubmission" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="newRat"/>
<jsp:useBean id="submitterData" class="edu.ucsf.mousedatabase.beans.UserData" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="submitterData"/>

<div class="site_container">
    <%
    
      if(!submitterData.ValidateContactInfo())
      {
        %><h2><span style="color: #23476b;text-emphasis: #23476b;font-style: italic">Invalid contact information. Please go back to step 1.</font></h2>
        <a href="submitratinit.jsp">Back to step 1</a>
        <%
      }
      else
      {
    
    %>
    
<div class="formbody">
    <div class="introduction"><div style="width:850px">
    <h2>New Submission Step 2: Choose Rodent Category</h2>
    <a href="submitratinit.jsp">Back to step 1</a>
    <br>
    <p>For the rodent you want to submit, please select a category from the three choices listed.
    For transgenes, respond to the query about publication.
    </p>
    <b><span style="color: #23476b;text-emphasis: #23476b; font-size: larger;font-style: italic">Choose 'Transgene'</span></b> when a DNA construct (perhaps a BAC) has been randomly
    inserted into the genome, such that the rodent expresses a particular sequence
    (e.g. Cre, a reporter gene, a rodent or human gene) under the control of a
    regulatory sequence included in the construct. Note: a knock-in into a specific locus
    is classified as a ‘mutant allele.’  However, if a gene is disrupted by a random insertion of a DNA construct,
    then the modification, while it is an allele of the disrupted gene, is classified as a transgene.
    </p>
    <p><b><span style="color: #23476b;text-emphasis: #23476b; font-size: larger;font-style: italic">Choose 'Inbred Strain/Outbred/Mixed Strain'</span></b> when the major genetic characteristic of the rodent is a member of a particular inbred strain.</p>
        After selecting a category, please choose the rodent's publication status.
        <p><b><span style="color: #23476b;text-emphasis: #23476b; font-size: larger;font-style: italic">Choose Published</span></b> when the rodent has been published in a research/scientific
            paper and has a pubmed ID.</p>

        <p><b><span style="color: #23476b;text-emphasis: #23476b; font-size: larger;font-style: italic">Choose Unpublished</span></b> when the rodent has NOT been published in a research/scientific
            paper and does NOT have a pubmed ID.</p>

    </div>
    </div>

    <form name="RatTypeForm" id="RatTypeForm" method="post" action="submitformRatDetails.jsp">
        <table class="inputForm">
        <tr class="formField">
        <td style="width:22%">
        New Submission category:
        </td>
        <td style="width:150px">
        <input type="radio" value="Transgene" name="ratType" <%=HTMLGeneration.isChecked(newRat.isTG()) %>onclick="UpdateSelectedRatType()"/>
        Transgene
        <br/>
        <input type="radio" value="Inbred Strain" name="ratType" <%=HTMLGeneration.isChecked(newRat.isIS()) %> onclick="UpdateSelectedRatType()"/>
        Inbred Strain/Outbred/Mixed Strain
        </td>
        </tr>
        <tr class="formField" id="isPublishedSection" style="<%=HTMLGeneration.rowVisibility(newRat.isTG() || newRat.isMA()) %>">
        <td>
        Publication status:
        </td>
        <td colspan="2">
            <input type="radio" name="isPublished"
                value="Yes" onclick="UpdateSelectedRatType()" <%=HTMLGeneration.isChecked(newRat.getIsPublished() != null && newRat.isPublished()) %> >Published<br>
            <input type="radio" name="isPublished"
                value="No" onclick="UpdateSelectedRatType()" <%=HTMLGeneration.isChecked(newRat.getIsPublished() != null && !newRat.isPublished()) %> >Unpublished
            </td>
            </tr>
        </table>

        <input type="hidden" value="Random insertion" name="transgenicType">

<input id="nextButton" type="submit" class="btn btn-primary" style="<%=HTMLGeneration.elementVisibility(newRat.hasType()) %>" value="Next"/>
</form>
</div>
<%
}  //closes if(!submitterData.validateContactInfo())
%>
</div>
