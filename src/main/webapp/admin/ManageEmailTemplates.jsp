<%@page import="java.util.ArrayList" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("Options.jsp", true) %>
<%
  SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
  String command = request.getParameter("command");
  String orderby = request.getParameter("orderby");
  String category = request.getParameter("category");
  String message = request.getParameter("message");
  boolean wasError = false;
  int id = stringToInt(request.getParameter("id"));
  
  String title = "";
  String table = "";
  ArrayList<EmailTemplate> templates = null;
  EmailTemplate template = null;
  
  String[] orderOptions = new String[]{"name","date_updated","subject"};
  String[] orderLabels = new String[]{"Template name","Date Revised", "Template subject"};
  
  boolean addingNew = id <= 0;
  
  if(command == null || command.isEmpty()) {
    command = "list"; 
  }
  if(orderby == null)
  {
    if ((orderby = (String)session.getAttribute("manageEmailTemplateorderby")) == null)
    {
      orderby = "name";
    }
  }
 
  if(category == null)
  {
    if ((category = (String)session.getAttribute("manageEmailTemplatecategory")) == null)
    {
      category = EmailTemplate.getCategories()[0];
    }
  }
  session.setAttribute("manageEmailTemplateorderby",orderby);
  session.setAttribute("manageEmailTemplatecategory",category);
  
  
  if (message == null){
   message = ""; 
  }
  
  if (command.equals("list") || command.equals("delete")){ 
    if (command.equals("delete")) {
      try {
        template = DBConnect.loadEmailTemplate(id);
      }catch(Exception e){
       //Log.Error("manageemailtemplates.jsp failed to load template " + id,e); 
      }
      if (template != null) {
        DBConnect.deleteEmailTemplate(id);
        message = "Successfully deleted " + template.category + " template " + template.name;
        template = null;
      }
      else {
        wasError = true;
        message = "Error - tried to delete template #" + id + " but it was not found."; 
      }
    }
    title = "Email templates";
    
    templates = DBConnect.getEmailTemplates(category,orderby);
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
  
  <% if (message.length() > 0){ %>
  
    <div style='margin-top:15px;' class='alert <%= wasError ? "alert-error" : "alert-success" %>'><%= message %></div>
  <% } %>
  <% if (templates != null) { %>
    <h2><%= title %></h2>
     <form class='view_opts' action='ManageEmailTemplates.jsp'>
      Show: <%=genSelect("category", EmailTemplate.getCategories(), category) %>  
      Sort by: <%=genSelect("orderby", orderOptions,orderLabels, orderby,"") %>
     </form>
     <a class='btn btn-success' href='ManageEmailTemplates.jsp?command=edit&id=-1'><i class='icon-plus icon-white'></i> Add new template</a>
     <table class='basic'>
     <tr>
        <th>Category</th>
        <th>Name of Template</th>
        <th>Template</th>
     </tr>
     <% for (EmailTemplate t : templates) { %>
     <tr>
       <td>
          <b><%= t.category %></b>
       </td>
       <td>
        <dl>
        <dt><b style='font-size: 110%'><%=t.name %></b></dt>
        <dt>Updated: <%=sdf.format(t.dateUpdated) %></dt>
        <dt><a class='btn btn-mini' href='ManageEmailTemplates.jsp?command=edit&id=<%=t.id%>'><i class='icon-edit'></i> Edit</a></dt>
        </dl>
       </td>
       <td>
        <table class='email_template_template'>
          <tr><td>Subject:</td><td><%=t.subject %></td></tr>
          <tr><td>Body:</td><td><%=t.body %></td></tr>
        </table>
        </td>
       </tr>
      <% } %>
      <% if (templates.size() == 0){ %>
        <tr><td style='text-align: center' colspan='3'>No templates yet</td></tr>
      <% } %>
      </table>
  <% }%>
  
  <% if (template != null){ %>
     <div style='width:600px;float:left'>
     <h2><%= title %></h2>
     <form method='post' action='UpdateTemplate' id='manageEmailForm'>
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
         <% if(!addingNew) { %>
         	<br><br>
            <div class='delete_controls'><a href='#' class='btn btn-warning btn_delete'><i class='icon-remove icon-white'></i> Delete...</a>
            <div class='alert alert-error delete_confirmation' style='display:none'>Are you sure you want to delete this template?
            <br>
            <a href='ManageEmailTemplates.jsp?command=delete&id=<%=id %>' class='btn btn-danger btn_confirm_delete'><i class='icon-ok icon-white'></i> Yes, delete it!</a>
            <a href='#' class='btn btn_cancel_delete'>No, cancel</a>
            </div>
            </div>
         <% } %>
        </td>
       </tr>
       </table>
     </form>
     </div>
     <div style='float:left; max-width:335px' class='template_help'>
     <h3>Data interpolation guide: <a style='font-size:10px; text-decoration:none' href='#' class='toggle_help'>toggle help</a></h3>
     
     <p class='hide'>Use one or more of the following tags to fill in data.  For example, 
     on a change request, {{request.requestID}} will be replaced with the actual
     change request ID.
     </p>
     <p class='hide'>Note that sometimes, a data field may be empty.  For example, if you want
     to show the mouse name, and use {{mouse.mouseName}} or {{submission.submissionName}}
     but the mouse has no name, or the submission was an automatic submission and none
     was specified, then the email won't look quite right without manual editing.
     </p>
     
     <b>All interpolation tags are case- and space-sensitive.</b>
     <dl>
     <dt>Submissions<dt>
     <dd>{{submission.submissionID}}</dd>
     <dd>{{submission.mouseName}}</dd>
     <dd>{{submission.officialMouseName}}</dd>
     <dd>{{submission.holderName}}</dd>
     <dd>{{submission.holderFacility}}</dd>
     <dd>{{submission.isPublished}}</dd>
     <dd>{{submission.cryoLiveStatus}}</dd>
     <dd>{{submission.ISSupplier}}</dd>
     <dd>{{submission.ISSupplierCatalogNumber}}</dd>
     <dd>{{submission.mouseMGIID}}</dd>
     <dd>{{submission.PMID}}</dd>
     <dd>{{submission.officialSymbol}}</dd>
     <dd>{{submission.recordPreviewLink}} <i>Only for submissions on hold</i></dd>
     <dt>Change requests</dt>
     <dd>{{request.requestID}}</dd>
     <dd>{{request.firstname}}</dd>
     <dd>{{request.lastname}}</dd>
     <dd>{{request.mouseID}}</dd>
     <dd>{{request.email}}</dd>
     <dd>{{request.requestDate}}</dd>
     <dd>{{request.userComment}}</dd>
     <dt>Mouse Records</dt>
     <dd>{{mouse.mouseID}}</dd>
     <dd>{{mouse.mouseName}}</dd>
     <dd>{{mouse.officialMouseName}}</dd>
     <dd>{{mouse.mouseType}}</dd>
     <dd>{{mouse.modificationType}}</dd>
     <dd>{{mouse.regulatoryElement}}</dd>
     <dd>{{mouse.expressedSequence}}</dd>
     <dd>{{mouse.source}}  <i>(official symbol)</i></dd>
     <dd>{{mouse.repositoryCatalogNumber}}  <i>(MGI ID)</i></dd>
     <dd>{{mouse.pubmedIDs}}</dd>
     <dt>Holders<dt>
     <dd>{{holder.firstname}}</dd>
     <dd>{{holder.lastname}}</dd>
     <dd>{{holder.holderID}}</dd>
     <dd>{{holder.email}}</dd>
     <dd>{{holder.dept}}</dd>
     <dd>{{holder.tel}}</dd>
     <dd>{{holder.alternateName}}</dd>
     <dd>{{holder.alternateEmail}</dd>
     </dl>
     </div>
  <% } %>
</div>
<script type='text/javascript'>
  $("textarea[name='body']").cleditor({
    width: 500,
    height: 200,
    controls: 
      "bold italic underline | font size " +
      "style | link unlink | color removeformat | bullets numbering | outdent " +
      "indent | undo redo | cut copy paste pastetext",
  });
  $(".btn_delete").click(function(){
    $(this).siblings().show();
    $(this).hide();
    return false;
  });
  $(".btn_cancel_delete").click(function(){
    $(this).parent().hide().parent().find(".btn_delete").show();
    return false;
  });
  $(".template_help a.toggle_help").click(function(){
    $(".template_help p").toggleClass('hide');
  });

</script>