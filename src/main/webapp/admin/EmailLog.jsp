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

  ArrayList<MouseMail> emails = DBConnect.getEmails(category,orderby,null);
  session.setAttribute("emailLogcategory",category);
  session.setAttribute("emailLogorderby",orderby);

  String[] orderOptions = new String[]{"date_created","date_created_inv","category","template_name","recipients"};
  String[] orderLabels = new String[]{"Date sent", "Date sent (reverse)","Category","Template name","Recipient"};
 
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
  <th>Category</th>
  <th>Template Name</th>
  <th>Attachment names</th>
  <th style='max-width: 200px'>Recipient(s)</th>
  <th style='width: 20'>Subject</th>
  <th>Message</th>
</tr>
<% for (MouseMail email : emails){ %>
  <tr id='email_<%=email.id %>' class='mouselist'>
    <td><%=email.status %> <%= sdf.format(email.dateCreated) %>
    <% if(email.status.equals(MouseMail.DraftStatus)) { %><br>
      <a class='btn btn-mini btn-danger delete-draft-btn' data-draft_subject='<%=email.subject %>' 
         data-draft_id='<%=email.id %>' href='#'><i class='icon-remove'></i> delete</a>
    <% } %>
    </td>
    <td><%=emptyIfNull(email.category) %></td>
    <td><%=emptyIfNull(email.templateName) %></td>
    <td><%=emptyIfNull(email.attachmentNames) %></td>
    <td>
    <%=email.recipient %> 
    <%=email.ccs.length() > 0 ? "<br>cc: " + email.ccs : "" %>    
    <%=email.bccs.length() > 0 ? "<br>bcc: " + email.bccs : "" %>
    </td>
    <td><%=HTMLUtilities.getCommentForDisplay(email.subject) %></td>
    <td><%=email.body %></td>
  </tr>
<%} %>
<% if (emails.size() == 0){ %>
  <tr><td style='text-align: center' colspan='6'>No emails yet</td></tr>
<% } %>
</table>
</div>
<script>
$("a.delete-draft-btn").click(function(){
  if ($(this).hasClass('disabled')) { return false; }
  var link = $(this);
  var email_id = $(this).data('draft_id');
  var r = confirm("Are you sure you want to delete this draft?\n\n" + $(this).data('draft_subject'));
  if (r == true) {
    
    $.ajax({
      type: 'post',
      url: '<%=adminRoot%>SendMail?delete=' + email_id,
      dataType: 'json',
      success: function(data){deleteSuccess(link,data)},
      error: function(data){deleteSuccess(link,data)},
      async: true
  	});
  } 
  return false;
});

function deleteSuccess(elem, data) {
  elem.closest('tr').slideUp('500').remove();
}
function deleteError(data) {
  alert("Error deleting draft: " + data);
}

</script>
