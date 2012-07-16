<%@page import="edu.ucsf.mousedatabase.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EmailOptions.jsp", true) %>
<% 

  ArrayList<MouseMail> emails = DBConnect.getEmails();
 
%>
<div class='site_container'>
<h2>Email log</h2>

<table class='mouseTable' style='width:100%'>
<tr class='mouselistH'>
<td width='170px'>
Date
</td>
<td width='200px'>
Recipient(s)
</td>
<td width='20%'>
Subject
</td>
<td>
Message
</td>
</tr>
<% for(MouseMail email : emails){ %>
<tr class='mouselist'>
<td>
<%=email.status %> <%= email.dateCreated %>
</td>
<td>
<%=email.recipient %>

<%=email.ccs.length() > 0 ? "<br>cc: " + email.ccs : "" %>

<%=email.bccs.length() > 0 ? "<br>bcc: " + email.bccs : "" %>
</td>
<td>
<%=email.subject %>
</td>
<td>
<%=email.body %>
</td>
</tr>
<%} %>
</table>
</div>