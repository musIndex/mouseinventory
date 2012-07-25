<%@page import="edu.ucsf.mousedatabase.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="java.util.ArrayList" %>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@page import="java.text.SimpleDateFormat" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("Options.jsp", true) %>
<% 
  SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");

  String orderby = request.getParameter("orderby");
  String category = request.getParameter("category");
  if (orderby == null) {
    if ((orderby = (String)session.getAttribute("emailLogorderby")) == null) {
      orderby = "date_created";
    }
  }

  if (category == null) {
    if ((category = (String)session.getAttribute("emailLogcategory")) == null) {
      category = "all";
    }
  }

  ArrayList<MouseMail> emails = DBConnect.getEmails(category,orderby);
  session.setAttribute("emailLogcategory",category);
  session.setAttribute("emailLogorderby",orderby);
 
  String[] orderOptions = new String[]{"date_created","category","subject"};
  String[] orderLabels = new String[]{"Date sent", "Category","Email subject"};
 
  String[] categories = EmailTemplate.getCategories();
  
  String[] templateOptions = new String[categories.length + 1];
  templateOptions[0] = "all";
  for (int i = 1; i < templateOptions.length; i++) {
    templateOptions[i] = categories[i-1];
  }
%>
<div class='site_container'>
<h2>Email log</h2>
<form class='view_opts' action='<%=adminRoot%>EmailLog.jsp'>
   Show: <%=genSelect("category",templateOptions,category) %>  
   Sort by: <%=genSelect("orderby",orderOptions,orderLabels,orderby,"") %>
</form>
<table class='basic'>
  <tr>
  <th width='170px'>Date</th>
  <th width='170px'>Category</th>
  <th width='200px'>Recipient(s)</th>
  <th width='20%'>Subject</th>
  <th>Message</th>
</tr>
<% for (MouseMail email : emails){ %>
  <tr class='mouselist'>
    <td><%=email.status %> <%= sdf.format(email.dateCreated) %></td>
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
<% if (emails.size() == 0){ %>
  <tr><td style='text-align: center' colspan='5'>No emails yet</td></tr>
<% } %>
</table>
</div>
