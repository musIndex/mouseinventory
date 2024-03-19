<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("AddMouse.jsp", true) %>
<jsp:useBean id="newMouse" class="edu.ucsf.mousedatabase.objects.MouseRecord" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="newMouse"/>

<div class="site_container">
<form name="MouseTypeForm" id="MouseTypeForm" method="post" action="AddMouseForm.jsp">
<table class="inputForm">
<tr class="formField">
<td style="width:22%">
New Submission category:
</td>
<td style="width:150px">
<input type="radio"  value="Mutant Allele" name="mouseType"
<%=HTMLGeneration.isChecked(newMouse.isMA()) %> />
Mutant Allele
<br/>
<input type="radio" value="Transgene" name="mouseType"
<%=HTMLGeneration.isChecked(newMouse.isTG()) %>/>
Transgene
<br/>
<input type="radio" value="Inbred Strain" name="mouseType"
<%=HTMLGeneration.isChecked(newMouse.isIS()) %> />
Inbred Strain
</td>
</tr>
</table>

<input type="hidden" value="Random insertion" name="transgenicType">

<input id="nextButton" type="submit" class="btn btn-primary" value="Next"/>
</form>



</div>

