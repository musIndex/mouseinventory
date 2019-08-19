<%@page import="edu.ucsf.mousedatabase.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="java.util.*" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("Options.jsp", true) %>

<div class='site_container'>
<h2>Options</h2>

<div style="width: 300px; float: left">
<h3>Site settings</h3>
<dl class="settings_list">
<dd><a href='ManageSettings.jsp'><i class='icon-list'></i> Manage settings</a></dd>
</dl>
</div>
<div style="width: 300px; float: left">
<h3>Email Options</h3>
<dl class="settings_list">
<dd><a href='ManageEmailTemplates.jsp'><i class='icon-pencil'></i> Manage email templates</a></dd>
<dd><a href='EmailLog.jsp'><i class='icon-envelope'></i> View email log</a></dd>
</dl>
</div>

</div>