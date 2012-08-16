<%@page import="java.util.ArrayList" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("ManageAdminNotes.jsp", true) %>
<%
  SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
  String command = request.getParameter("command");
  String orderby = request.getParameter("orderby");
  String message = request.getParameter("message");
  boolean wasError = false;
  int id = stringToInt(request.getParameter("id"));
  
  String title = "";
  String table = "";
  ArrayList<Setting> settings = null;
  Setting note = null;
  
  String[] orderOptions = new String[]{"name","label","date_updated"};
  String[] orderLabels = new String[]{"Note name","Added by","Date Revised"};
  
  boolean addingNew = id <= 0;
  
  if(command == null || command.isEmpty()) {
    command = "list"; 
  }
  if(orderby == null)
  {
    if ((orderby = (String)session.getAttribute("manageSettingorderby")) == null)
    {
      orderby = "name";
    }
  }
 
  
  session.setAttribute("manageSettingorderby",orderby);
  
  
  if (message == null){
   message = ""; 
  }
  
  if (command.equals("list") || command.equals("delete")){ 
    if (command.equals("delete")) {
      try {
        note = DBConnect.loadSetting(id);
      }catch(Exception e){
       //Log.Error("ManageAdminNotes.jsp failed to load note " + id,e); 
      }
      if (note != null) {
        DBConnect.deleteSetting(id);
        message = "Successfully deleted " + note.category + " note " + note.name;
        note = null;
      }
      else {
        wasError = true;
        message = "Error - tried to delete note #" + id + " but it was not found."; 
      }
    }
    title = "Admin notes";
    
    settings = DBConnect.getCategorySettings(Setting.ADMIN_NOTES_CATEGORY,orderby);
  }
  else if (command.equals("edit")){
    if (addingNew) {
      title = "Add new admin note";
      note = new Setting(); 
    }
    else {
      title = "Edit admin note";
      note = DBConnect.loadSetting(id);
    }
  }

%>

<div class='site_container'>
  
  <% if (message.length() > 0){ %>
  
    <div style='margin-top:15px;' class='alert <%= wasError ? "alert-error" : "alert-success" %>'><%= message %></div>
  <% } %>
  <% if (settings != null) { %>
    <h2><%= title %></h2>
     <form class='view_opts' action='ManageAdminNotes.jsp'>
      Sort by: <%=genSelect("orderby", orderOptions,orderLabels, orderby,"") %>
     </form>
     <a class='btn btn-success' href='ManageAdminNotes.jsp?command=edit&id=-1'><i class='icon-plus icon-white'></i> Add new note</a>
     <table class='basic'>
     <tr>
        <th style='width: 250px'>Name</th>
        <th>Note</th>
     </tr>
     <% for (Setting current_note : settings) { %>
     <tr>
       <td>
        <dl>
        <dt><b style='font-size: 110%'><%=current_note.name %></b></dt>
        <dt>Added by: <%=current_note.label %></dt>
        <dt>Updated: <%=sdf.format(current_note.dateUpdated) %></dt>
        <dt><a class='btn btn-mini' href='ManageAdminNotes.jsp?command=edit&id=<%=current_note.id%>'><i class='icon-edit'></i> Edit</a></dt>
        </dl>
       </td>
       <td>
        <%=current_note.asString() %>
        </td>
       </tr>
      <% } %>
      <% if (settings.size() == 0){ %>
        <tr><td style='text-align: center' colspan='2'>No notes yet</td></tr>
      <% } %>
      </table>
  <% }%>
  
  <% if (note != null){ %>
     <div style='width:600px;float:left'>
     <h2><%= title %></h2>
     <form method='post' action='UpdateSetting' id='manageAdminNotesForm'>
       <input type='hidden' name='category' value='<%=Setting.ADMIN_NOTES_CATEGORY %>'>
       <input type='hidden' name='redirect_page' value='ManageAdminNotes.jsp'>
       <input type='hidden' name='redirect_params' value='command=list'>
       <input type='hidden' name='id' value='<%=note.id %>'>
       <table class='emailTemplateForm'>
         <tr>
            <td>Name</td><td><%=tInput("name",note.name) %></td>
         </tr>
         <tr>
            <td>Added by</td><td><%=tInput("label",note.label) %></td>
         </tr>
         <tr>
            <td>Note</td><td><%=tArea("setting_value",note.asString()) %></td>
         </tr>
       <tr>
        <td></td>
        <td>
         <button class='btn <%= addingNew ? "btn-success" : "btn-primary" %>'>
          <%= addingNew ?  "Add note" : "Save note"%>
         </button>
         <% if(!addingNew) { %>
         	<br><br>
            <div class='delete_controls'><a href='#' class='btn btn-warning btn_delete'><i class='icon-remove icon-white'></i> Delete...</a>
            <div class='alert alert-error delete_confirmation' style='display:none'>Are you sure you want to delete this note?
            <br>
            <a href='ManageAdminNotes.jsp?command=delete&id=<%=id %>' class='btn btn-danger btn_confirm_delete'><i class='icon-ok icon-white'></i> Yes, delete it!</a>
            <a href='#' class='btn btn_cancel_delete'>No, cancel</a>
            </div>
            </div>
         <% } %>
        </td>
       </tr>
       </table>
     </form>
     </div>
  <% } %>
</div>
<script type='text/javascript'>
  $("textarea[name='setting_value']").cleditor({
    width: 500,
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
  });
</script>