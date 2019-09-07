<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%=HTMLGeneration.getPageHeader(null, true,false) %>
<%=HTMLGeneration.getNavBar("submitforminit.jsp", false) %>


<jsp:useBean id="newMouse" class="edu.ucsf.mousedatabase.beans.MouseSubmission" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="newMouse"/>
<jsp:useBean id="submitterData" class="edu.ucsf.mousedatabase.beans.UserData" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="submitterData"/>
<div class="site_container">
<%

  if(!submitterData.ValidateContactInfo())
  {
    %><h2><font color="red">Invalid contact information. Please go back to step 1.</font></h2>
    <a href="submitforminit.jsp">Back to step 1</a>
    <%
  }
  else
  {

%>

<div class="formbody">
<div class="introduction"><div style="width:850px">
<h2>New Submission Step 2: Choose Mouse Category</h2>
<a href="submitforminit.jsp">Back to step 1</a>
<br>
<p>For the mouse you want to submit, please select a category from the three choices listed.
For mutant alleles or transgenes, respond to the query about publication.</p>
<p>
<b><span class=red>Choose 'Mutant Allele'</span></b> when a gene in the mouse has been modified in some way
(e.g. targeted disruption such as replacement with a neo cassette; creation of a floxed allele
by insertion of loxP sites; targeted knock-in of a sequence such as Cre, a reporter gene,
or a mouse or human gene; spontaneous mutation, endonuclease-mediated, etc.)
</p>
<p>
<b><span class=red>Choose 'Transgene'</span></b> when a DNA construct (perhaps a BAC) has been randomly
inserted into the genome, such that the mouse expresses a particular sequence
(e.g. Cre, a reporter gene, a mouse or human gene) under the control of a
regulatory sequence included in the construct. Note: a knock-in into a specific locus
is classified as a ‘mutant allele.’  However, if a gene is disrupted by a random insertion of a DNA construct,
then the modification, while it is an allele of the disrupted gene, is classified as a transgene.
</p>
<p><b><span class=red>Choose 'Inbred Strain'</span></b> when the major genetic characteristic of the mouse you want to submit is that it is a
member of a particular inbred strain (mice that are genetically nearly identical as a result of extensive inbreeding -
usually at least 13 generations). These strains are generally purchased from suppliers such as JAX Mice.
This category should be used for mice that are maintained because they carry QTLs.</p>
</div>
</div>

<form name="MouseTypeForm" id="MouseTypeForm" method="post" action="submitformMouseDetails.jsp">
<table class="inputForm">
<tr class="formField">
<td style="width:22%">
New Submission category:
</td>
<td style="width:150px">
<input type="radio"  value="Mutant Allele" name="mouseType" <%=HTMLGeneration.isChecked(newMouse.isMA()) %>onclick="UpdateSelectedMouseType()" />
Mutant Allele
<br/>
<input type="radio" value="Transgene" name="mouseType" <%=HTMLGeneration.isChecked(newMouse.isTG()) %>onclick="UpdateSelectedMouseType()"/>
Transgene
<br/>
<input type="radio" value="Inbred Strain" name="mouseType" <%=HTMLGeneration.isChecked(newMouse.isIS()) %> onclick="UpdateSelectedMouseType()"/>
Inbred Strain
</td>
</tr>
<tr class="formField" id="isPublishedSection" style="<%=HTMLGeneration.rowVisibility(newMouse.isTG() || newMouse.isMA()) %>">
<td>
Is the mouse published?
</td>
<td colspan="2">
<input type="radio" name="isPublished"
    value="Yes" onclick="UpdateSelectedMouseType()" <%=HTMLGeneration.isChecked(newMouse.getIsPublished() != null && newMouse.isPublished()) %> >Yes<br>
<input type="radio" name="isPublished"
    value="No" onclick="UpdateSelectedMouseType()" <%=HTMLGeneration.isChecked(newMouse.getIsPublished() != null && !newMouse.isPublished()) %> >No
</td>
</tr>
</table>

<input type="hidden" value="Random insertion" name="transgenicType">

<input id="nextButton" type="submit" class="btn btn-primary" style="<%=HTMLGeneration.elementVisibility(newMouse.hasType()) %>" value="Next"/>
</form>



</div>


<%
}  //closes if(!submitterData.validateContactInfo())

%>
</div>
