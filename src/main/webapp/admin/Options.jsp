<%@page import="edu.ucsf.mousedatabase.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="java.util.*" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("Options.jsp", true) %>

<div class='site_container'>
<h2>Options</h2>

<h4>Site settings</h4>

<a href='ManageSettings.jsp'>Manage settings</a>
<br>

<h4>Email Options</h4>

<a href='ManageEmailTemplates.jsp'>Manage email templates</a>
<br>
<a href='EmailLog.jsp'>View email log</a>
<br>
<br>


</div>