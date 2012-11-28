<%@page import="java.util.HashMap"%>
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
  int category_id = stringToInt(request.getParameter("category_id"));
  String message = request.getParameter("message");
  boolean wasError = false;
  int id = stringToInt(request.getParameter("id"));
  
  String title = "";
  String table = "";
  ArrayList<Setting> settings = null;
  Setting setting = null;
  
  String[] orderOptions = new String[]{"label","date_updated"};
  String[] orderLabels = new String[]{"Name","Date Revised"};
  
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
  
  
  ArrayList<Setting.SettingCategory> settingCategories = Setting.getCategories();
  HashMap<Integer, Setting.SettingCategory> settingCategoriesMap = new HashMap<Integer, Setting.SettingCategory>();
  String[] settingCategoryNames = new String[settingCategories.size()];
  String[] settingCategoryIds = new String[settingCategories.size()];
  for(int i = 0; i < settingCategories.size(); i++) {
    settingCategoryNames[i] = settingCategories.get(i).Name;
    settingCategoryIds[i] = Integer.toString(settingCategories.get(i).Id);
    settingCategoriesMap.put(settingCategories.get(i).Id,settingCategories.get(i));
  }
  
  
  if(category_id < 0)
  {
    Object sessionAttr = session.getAttribute("manageSettingcategory");
    if (sessionAttr == null) {
      category_id = Integer.parseInt(settingCategoryIds[0]);
    }
    else {
     category_id = (Integer)sessionAttr; 
    }
  }
  Setting.SettingCategory category = settingCategoriesMap.get(category_id);
  session.setAttribute("manageSettingorderby",orderby);
  session.setAttribute("manageSettingcategory",category_id);
  
  
  if (message == null){
   message = ""; 
  }
  
  if (command.equals("list") || command.equals("delete")){ 
    if (command.equals("delete")) {
      try {
        setting = DBConnect.loadSetting(id);
      }catch(Exception e){
       //Log.Error("manageemailtemplates.jsp failed to load setting " + id,e); 
      }
      if (setting != null) {
        DBConnect.deleteSetting(id);
        message = "Successfully deleted " + category.Name + " setting " + setting.name;
        setting = null;
      }
      else {
        wasError = true;
        message = "Error - tried to delete setting #" + id + " but it was not found."; 
      }
    }
    title = "Settings";
    
    settings = DBConnect.getCategorySettings(category_id,orderby);
  }
  else if (command.equals("edit")){
    if (addingNew) {
      title = "Add new '" + category.Name + "' setting";
      setting = new Setting(); 
    }
    else {
      setting = DBConnect.loadSetting(id);
      category_id = setting.category_id;
      category = settingCategoriesMap.get(category_id);
      title = "Edit '" + category.Name + "' setting";
      
    }
  }

%>

<div class='site_container'>
  
  <% if (message.length() > 0){ %>
  
    <div style='margin-top:15px;' class='alert <%= wasError ? "alert-error" : "alert-success" %>'><%= message %></div>
  <% } %>
  <% if (settings != null) { %>
    <h2><%= title %></h2>
     <form class='view_opts' action='ManageSettings.jsp'>
      Show: <%=genSelect("category_id",settingCategoryIds,settingCategoryNames,Integer.toString(category_id), null) %>  
      Sort by: <%=genSelect("orderby", orderOptions,orderLabels, orderby,"") %>
     </form>
     <% if (category.CanAddOrRemove) { %>
     <a class='btn btn-success' href='ManageSettings.jsp?command=edit&id=-1'><i class='icon-plus icon-white'></i> Add new '<%=category.Name %>' setting</a>
     <% } %>
     <table class='basic'>
     <tr>
        <th style='min-width:150px'>Description</th>
        <th>Value</th>
     </tr>
     <% for (Setting set : settings) { %>
     <tr>
       <td>
        <dl>
        <dt><b style='font-size: 110%'><%=set.label %></b></dt>
        <dt>Updated: <%=sdf.format(set.dateUpdated) %></dt>
        <dt><a class='btn btn-mini' href='ManageSettings.jsp?command=edit&id=<%=set.id%>'><i class='icon-edit'></i> Edit</a></dt>
        </dl>
       </td>
       <td style='background-color:white;border: 1px solid gainsboro;'>
        <% if (category.RichText) { %>
        <%= set.value %> 
        <% } else { %>
        <%= set.value.replace("\n","<br>") %>
        <% } %>
       </td>
       </tr>
      <% } %>
      <% if (settings.size() == 0){ %>
        <tr><td style='text-align: center' colspan='3'>No settings yet</td></tr>
      <% } %>
      </table>
  <% }%>
  
  <% if (setting != null){ %>
     <div style='width:600px;float:left'>
     <h2><%= title %></h2>
     <form method='post' action='UpdateSetting' id='manageEmailForm'>
     <input type='hidden' name='redirect_page' value='ManageSettings.jsp'>
       <input type='hidden' name='redirect_params' value='command=list'>
       <input type='hidden' name='id' value='<%=setting.id %>'>
       <input type='hidden' name='category_id' value='<%= category_id %>'>
       <input type='hidden' name='name' value='<%= emptyIfNull(setting.name) %>'>
       <table class='emailTemplateForm'>
         <tr>
            <td>Label:</td>
            <td>
            <% if (category.CanChangeLabel) { %>
            	<%=tInput("label",setting.label) %>
            <% } else { %>
            	<input type='hidden' name='label' value='<%=setting.label%>'>
            	<b><%= setting.label %></b>
            <% } %>
            </td>
         </tr>
         <tr>
          <% if (category.RichText) { %>
            <td>Value:</td><td><%=tArea("setting_value",setting.value) %></td>
            <% } else if (setting.textAreaRows > 0) { %>
            <td>Value:</td><td>
              <textarea name='setting_value' rows='<%= setting.textAreaRows %>'><%=setting.value %></textarea>
              <br>To search, use CMD+F (mac) or CTRL+F (windows)
            </td>
            <% } else { %>
            <td>Value:</td><td><%=tInput("setting_value",setting.value) %></td>
            <% } %>
         </tr>
       <tr>
        <td></td>
        <td>
         <button class='btn <%= addingNew ? "btn-success" : "btn-primary" %>'>
          <%= addingNew ?  "Add setting" : "Save setting"%>
         </button>
         <% if(!addingNew && category.CanAddOrRemove) { %>
         	<br><br>
            <div class='delete_controls'><a href='#' class='btn btn-warning btn_delete'><i class='icon-remove icon-white'></i> Delete...</a>
            <div class='alert alert-error delete_confirmation' style='display:none'>Are you sure you want to delete this setting?
            <br>
            <a href='ManageSettings.jsp?command=delete&id=<%=id %>' class='btn btn-danger btn_confirm_delete'><i class='icon-ok icon-white'></i> Yes, delete it!</a>
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
<% if (category.RichText) { %>
<script type='text/javascript'>
  $("textarea[name='setting_value']").cleditor({
    width: 800,
    height: 300,
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
<% } %>