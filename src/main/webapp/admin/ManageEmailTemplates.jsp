<%@page import="java.util.ArrayList" %>
<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("Options.jsp", true) %>
<%
  String command = request.getParameter("command");
  String orderby = request.getParameter("orderby");
  String message = request.getParameter("message");
  int id = stringToInt(request.getParameter("id"));
  
  String title = "";
  String table = "";
  ArrayList<EmailTemplate> templates = null;
  EmailTemplate template = null;
  
  boolean addingNew = id <= 0;
  
  if(command == null || command.isEmpty()) {
    command = "list"; 
  }
  if (message == null){
   message = ""; 
  }
  
  if (command.equals("list")) { 
    title = "Email templates";
    templates = DBConnect.getEmailTemplates();
  }
  else if (command.equals("edit")){
    if (addingNew) {
      title = "Add new email template";
      template = new EmailTemplate(); 
    }
    else {
      title = "Edit email template";
      template = DBConnect.loadEmailTemplate(id);
    }
  }

%>

<div class='site_container'>
  <h2><%= title %></h2>
  <% if (message.length() > 0){ %>
    <div class='alert alert-success'><%= message %></div>
  <% } %>
  <br><br>
  <% if (templates != null) { %>
     <a href='ManageEmailTemplates.jsp?command=edit&id=-1'>Add new template</a>
     <table class='basic'>
     <tr>
        <th width='150px'>Category</th>
        <th width='150px'>Name</th>
        <th width='150px'>Last updated</th>
        <th>Template</th>
     </tr>
	  <% for (EmailTemplate t : templates) { %>
        <tr>
          <td><%=t.category %></td>
          <td><a href='ManageEmailTemplates.jsp?command=edit&id=<%=t.id%>'><%=t.name %></a></td>
          <td><%=t.dateUpdated %></td>
          <td>
            <table>
              <tr><td><b>Subject:</b></td><td><%=t.subject %></td></tr>
              <tr><td><b>Body:</b></td><td><%=t.body %></td></tr>
            </table>
          </td>
       </tr>
      <% } %>
      </table>
  <% }%>
  
  <% if (template != null){ %>
     <form method='post' action='UpdateTemplate'>
       <input type='hidden' name='id' value='<%=template.id %>'/>
       <table class='emailTemplateForm'>
         <tr>
            <td>Category</td><td><%= genSelect("category", EmailTemplate.getCategories(), template.category) %></td>
         </tr>
         <tr>
            <td>Name</td><td><%=tInput("name",template.name) %></td>
         </tr>
         <tr>
            <td>Subject</td><td><%=tInput("subject",template.subject) %></td>
         </tr>
         <tr>
            <td>Body</td><td><%=tArea("body",template.body) %></td>
         </tr>
       <tr>
        <td></td>
        <td>
         <button class='btn <%= addingNew ? "btn-success" : "btn-primary" %>'>
          <%= addingNew ?  "Add template" : "Save template"%>
         </button>
        </td>
       </tr>
       </table>
     </form>
     <div class='template_help'>
     <dl>
     <dt>Submissions<dt>
     <dd>{{submission.submissionID}}</dd>
     <dd>{{submission.mouseName}}</dd>
     <dt>Change requests</dt>
     <dd>{{request.requestID}}</dd>
     <dd>{{request.firstname}}</dd>
     <dd>{{request.lastname}}</dd>
     <dd>{{request.mouseID}}</dd>
     <dt>Mouse Records</dt>
     <dd>{{mouse.mouseID}}</dd>
     <dd>{{mouse.mouseName}}</dd>
     <dd>{{mouse.officialSymbol}}</dd>
     </dl>
     </div>
  <% } %>
</div>
<script type='text/javascript'>
$("textarea[name='body']").cleditor({
  width: 670,
  height: 200,
  controls: 
    "bold italic underline | font size " +
    "style | color removeformat | bullets numbering | outdent " +
    "indent | undo redo | " + " cut copy paste pastetext",
});

</script>