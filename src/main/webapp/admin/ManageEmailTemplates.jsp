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
  String message = request.getParameter("message");
  boolean wasError = false;
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
    <div class='alert <%= wasError ? "alert-error" : "alert-success" %>'><%= message %></div>
  <% } %>
  <% if (templates != null) { %>
     <a class='btn btn-success' href='ManageEmailTemplates.jsp?command=edit&id=-1'><i class='icon-plus icon-white'></i> Add new template</a>
     <table class='basic'>
     <tr>
        <th width='300px'>Description</th>
        <th>Template</th>
     </tr>
     <% for (EmailTemplate t : templates) { %>
     <tr>
       <td>
        <dl>
        <dt><b style='font-size: 110%'><%=t.name %></b></dt>
        <dt>Category: <b><%=t.category %></b></dt>
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
      </table>
  <% }%>
  
  <% if (template != null){ %>
     <div style='width:750px;float:left'>
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
     <div style='float:left; max-width:500px' class='template_help'>
     <h3>Data interpolation guide</h3>
     <p>Use one or more of the following tags to fill in data.  For example, 
     on a change request, {{request.requestID}} will be replaced with the actual
     change request ID.
     </p>
     <p>Note that sometimes, a data field may be empty.  For example, if you want
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
     <dd>{{mouse.geneID}}  <i>(mutant allele mice)</i></dd>
     <dd>{{mouse.geneSymbol}}  <i>(mutant allele mice)</i></dd>
     <dd>{{mouse.targetGeneID}}  <i>(transgene mice)</i></dd>
     <dd>{{mouse.targetGeneSymbol}} <i>(transgene mice)</i></dd>
     <dd>{{mouse.modificationType}}</dd>
     <dd>{{mouse.regulatoryElement}}</dd>
     <dd>{{mouse.expressedSequence}}</dd>
     <dd>{{mouse.source}}  <i>(official symbol)</i></dd>
     <dd>{{mouse.repositoryCatalogNumber}}  <i>(MGI ID)</i></dd>
     <dd>{{mouse.pubmedIDs}}</dd>
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
      "style | link unlink | color removeformat | bullets numbering | outdent " +
      "indent | undo redo | " + " cut copy paste pastetext",
  });
  $(".btn_delete").click(function(){
    $(this).siblings().show();
    $(this).hide();
    return false;
  });
  $(".btn_cancel_delete").click(function(){
    $(this).parent().hide().parent().find(".btn_delete").show();
    return false;
  })
</script>