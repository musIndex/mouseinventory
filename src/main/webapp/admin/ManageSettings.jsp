<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<script src="<%=scriptRoot %>jscolor.js"></script>
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
  
  String[] orderOptions = new String[]{"position","label","date_updated"};
  String[] orderLabels = new String[]{"Public-facing","Name","Date Revised"};
  
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
    <div class='alert' id='top_status_message' style='display:none'></div>
  <% } %>
  <% if (settings != null) { %>
    <h2><%= title %></h2>
     <form class='view_opts' action='ManageSettings.jsp'>
      Show: <%=genSelect("category_id",settingCategoryIds,settingCategoryNames,Integer.toString(category_id), null) %>  
      Sort by: <%=genSelect("orderby", orderOptions,orderLabels, orderby,"") %>
     </form>
     <a class='btn' href='#' id='sort_button'>Change public sort order</a>&nbsp;&nbsp;
     <div class='sort-instructions' style='display:none'><h3>Click and drag rows to reorder them.  Click 'Save changes' when done</h3></div>
    
     <% if (category.CanAddOrRemove) { %>
     <a class='btn btn-success' href='ManageSettings.jsp?command=edit&id=-1'><i class='icon-plus icon-white'></i> Add new '<%=category.Name %>' setting</a>
    
     <% } %>
     <table class='basic setting_list'>
     <tr>
        <th style='min-width:150px'>Description</th>
        <th>Value</th>
     </tr>
     <% for (Setting set : settings) { %>
     <tr>
       <td data-setting_id='<%= set.id %>'>
        <dl>
        <dt><b style='font-size: 110%'><%=set.label %></b></dt>
        <dt>Updated: <%=sdf.format(set.dateUpdated) %></dt>
        <dt><a class='btn btn-mini' href='ManageSettings.jsp?command=edit&id=<%=set.id%>'><i class='icon-edit'></i> Edit</a></dt>
        </dl>
       </td>
       <td>
        <div style='padding:2px; min-height:80px;background-color:white;border: 1px solid gainsboro;'> 
        <% if (category.RichText) { %>
        <%= set.value %> 
        <% } else if (category == Setting.SettingCategory.HOME_PAGE_CATEGORY_COLORS) { %>
        Background color:
        <div style="width:100%;background-color:  #<%= set.value%>">&nbsp;</div>
        Border color:
        <div style="width:100%;background-color: #<%= set.secondaryValue%>">&nbsp;</div>
        <% } else { %>
        <%= set.value.replace("\n","<br>") %>
        <% } %>
        </div>
        <% if(category.SecondaryValueName != null){ %>
           <%=category.SecondaryValueName %>:&nbsp;
           <%=emptyIfNull(set.secondaryValue) %>
       <% } %>
      <% } %>
      <% if (settings.size() == 0){ %>
        <tr><td style='text-align: center' colspan='3'>No settings yet</td></tr>
      <% } %>
       </td>
       </tr>
       
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
            <% } else if (category == Setting.SettingCategory.HOME_PAGE_CATEGORY_COLORS) { %>
              <td>Background color:</td>
              <td><input name="setting_value" class="color" type="text" value="<%=setting.value %>"></td>
              <td>Border color:</td>
              <td><input name="secondary_value" class="color" type="text" value="<%=setting.secondaryValue %>"></td>
            <% } else { %>
            <td>Value:</td><td><%=tInput("setting_value",setting.value) %></td>
            <% } %>
         </tr>
         <% if(category.SecondaryValueName != null){ %>
           <tr>
           <td><%=category.SecondaryValueName %>:</td>
           <% if(category.SecondaryValueName.equals("Custom style")) { %>
            <td><%=genSelect("secondary_value", new Object[]{"",
                "three_column_left", "three_column_center","three_column_right",
                "two_column_left","two_column_right","one_column_center", "one_column_center_two_thirds"}, 
                 new String[]{"none","Three columns, left","Three columns, center", "Three columns, right",
                "Two columns, left","Two columns, right","One column, centered, full width", 
                "One column, centered, 60% width"}, setting.secondaryValue, null) %></td>
            <% } else { %>
            <td><%=tInput("secondary_value",setting.secondaryValue) %></td>
           <% } %>
           </tr>
         <% } %>
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
      "indent | undo redo | cut copy paste pastetext | source",
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
<script>
!function($){
  var order = '<%=orderby%>';
	var sorting = false;
	var table_body = $("table.setting_list tbody");
	var sort_button = $("#sort_button");
	var instructions = $(".sort-instructions");
	var positions = "";
	sort_button.click(function(){
    	if (order != "position"){
    		updateStatus("To change the public-facing sort order, please choose 'Public-facing' from the sort menu first.");
    		return false;
    	}
    	if (sort_button.hasClass('disabled')){
    		return false;
    	} 
      sorting = !sorting;
      
      if (sorting) {
        clearStatus();
        table_body.addClass('sorting');
        sort_button.text('Save changes');
        instructions.show();
        table_body.sortable({
          placeholder: 'dndPlaceHolder',
          distance:15,
              items:'tr', 
              forcePlaceholderSize:true, 
              change : dndChange,
              update : dndUpdate
        }).disableSelection();
      }
      else {
        sort_button.addClass('disabled');
        sort_button.text('Saving...');
        table_body.removeClass('sorting');
        $.ajax({type: 'post',
                  url: '<%=HTMLGeneration.adminRoot %>UpdateSettingOrder',
                dataType: 'json',
                  success: updateOrderSuccess,
                error: updateOrderFailed,
                  data: 'positions=' + positions,
                  async: true
        });
        
      }
    
    
  });
  
  function updateOrderSuccess(data) {
    if (data.success) {
      updateStatus('Order saved',true);
    }
    else{
      updateOrderFailed(data);
    }
    sort_button.removeClass('disabled');
    sort_button.text('Change public sort order');
    instructions.hide();
    
  }
  function updateOrderFailed(data){
    updateStatus('Failed to save changes: ' + data.message,false);
  }
  
  function updateStatus(message,success) {
    $("#top_status_message").text(message)
      .removeClass('alert-success').removeClass('alert-error')
      .addClass(success ? 'alert-success' : 'alert-error')
      .text(message)
      .show();
  }
  
  function clearStatus() {
    $("#top_status_message").hide();
  }
  
  
  function dndChange(event,ui){
    positions = "";
    }

    function dndUpdate(event,ui){
      positions = getSortOrder();
    }
    
    function getSortOrder() {
      var order = "";
      $("tbody tr td:first-child").each(function(i,column){
        if (i > 0) {
          order += ',';
        }
        order += $(column).data('setting_id') + '-' + i;
      });
      return order;
    }

  dndUpdate();
}(jQuery);
</script>
<% } %>