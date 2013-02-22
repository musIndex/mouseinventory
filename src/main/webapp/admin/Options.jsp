<%@page import="edu.ucsf.mousedatabase.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="java.util.*" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("Options.jsp", true) %>

<div class='site_container'>
<h2>Options</h2>

<div style="width: 300px; float: left">
<h4>Site settings</h4>
<div class="settings_list">
<a href='ManageSettings.jsp'><i class='icon-list'></i> Manage settings</a>
<br>
</div>
</div>
<div style="width: 300px; float: left">
<h4>Email Options</h4>
<div class="settings_list">
<a href='ManageEmailTemplates.jsp'><i class='icon-pencil'></i> Manage email templates</a>
<br>
<a href='EmailLog.jsp'><i class='icon-envelope'></i> View email log</a>
<br>
</div>
</div>

</div>