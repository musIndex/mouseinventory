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

<table class='basic'>
  <tr>
  <th width='170px'>Date</th>
  <th width='170px'>Category</th>
  <th width='200px'>Recipient(s)</th>
  <th width='20%'>Subject</th>
  <th>Message</th>
</tr>
<% for(MouseMail email : emails){ %>
  <tr class='mouselist'>
    <td><%=email.status %> <%= email.dateCreated %></td>
    <td><%=email.category %></td>
    <td>
    <%=email.recipient %> 
    <%=email.ccs.length() > 0 ? "<br>cc: " + email.ccs : "" %>    
    <%=email.bccs.length() > 0 ? "<br>bcc: " + email.bccs : "" %>
    </td>
    <td><%=email.subject %></td>
    <td><%=email.body %></td>
  </tr>
<%} %>
</table>
</div>