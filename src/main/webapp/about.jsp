<%@page import="java.util.SortedMap"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@page import="java.util.ArrayList"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>

<%=getPageHeader(null, false, false, "onload=\"setFocus('quickSearchForm', 'searchterms')\"")%>
<%=getNavBar("about.jsp", false)%>
<style type="text/css">
.new_item.NEED_HELP_ITEMS  {
<% Setting set = DBConnect.loadSetting("category_11_color");%>
  background-color: #<%= set.value %>;
  border: 1px solid #<%= set.secondaryValue %>;
}

.new_item.RECENT_SITE_UPDATES  {
<% set = DBConnect.loadSetting("category_3_color");%>
  background-color: #<%= set.value %>;
  border: 1px solid #<%= set.secondaryValue %>;
}

.new_item.WED_LIKE_TO_HEAR_FROM_YOU {
<% set = DBConnect.loadSetting("category_7_color");%>
  background-color: #<%= set.value %>;
  border: 1px solid #<%= set.secondaryValue %>;
}

.new_item.DID_YOU_KNOW {
<% set = DBConnect.loadSetting("category_9_color");%>
  background-color: #<%= set.value %>;
  border: 1px solid #<%= set.secondaryValue %>;
}

</style>
  
<%

ArrayList<Setting.SettingCategory> categories = new ArrayList<Setting.SettingCategory>();
categories.add(Setting.SettingCategory.NEED_HELP_ITEMS);
categories.add(Setting.SettingCategory.WED_LIKE_TO_HEAR_FROM_YOU);
categories.add(Setting.SettingCategory.RECENT_SITE_UPDATES);
categories.add(Setting.SettingCategory.DID_YOU_KNOW);
ArrayList<String> categoryLabels = new ArrayList<String>();
categoryLabels.add("Mouse Inventory Updates");
categoryLabels.add("");
categoryLabels.add("");
categoryLabels.add("Mutant Mice Resources");
%>

<div class='site_container'>
  <div class='about_container'>
    <div class='about_welcome'>
      
      <div class='purpose'><%= DBConnect.loadSetting("home_page_text_purpose").value %></div>
    </div>
    <div class='about_banner'>
      <div class='iwanna'></div>  
      <div class='primary_actions'>
        <a href='<%=siteRoot%>search.jsp?search-source=about_banner'><i class='icon-search'></i> <span>Search for mice</span></a>
        <a href='<%=siteRoot%>submitforminit.jsp'><i class='icon-plus'></i> <span>Submit a new mouse</span></a>
        <a href='#about_details'><i class='icon-question'></i> <span>Learn about the database</span></a>
      </div>
    </div>
    <div class='about_new'>
<% 
int i = 0;
for(Setting.SettingCategory category : categories){ 
    ArrayList<Setting> recentSiteUpdateItems = DBConnect.getCategorySettings(category.Id);
    if (recentSiteUpdateItems.size() == 0) {
      i++;
     continue; 
    }
    %>
    <div class='category cf'>
    
    <h3><%=categoryLabels.get(i) %></h3>
    
    <%boolean open = false;
    String lastCustom = null;
    for (Setting newItem : recentSiteUpdateItems) {
      if (category.SecondaryValueName != null && category.SecondaryValueName.equals("Custom style")) {
        if (open && newItem.secondaryValue != null && !newItem.secondaryValue.equals(lastCustom)) {
          %></div><div class='<%=newItem.secondaryValue %>'><%
          
        }
        else if (open && newItem.secondaryValue == null) {
          %></div><%
          open = false;
        }
        else if (!open && newItem.secondaryValue != null) {
          %><div class='<%=newItem.secondaryValue %>'><%
          open = true;
        }
        lastCustom = newItem.secondaryValue;
      }
      
     %>
     
     
     <div class='new_item <%= category %>'>
        <% if (newItem.label != null && !newItem.label.isEmpty()){ %>
          <b><%= newItem.label %></b><br>
        <% } %>
        <%= newItem.value %>
      </div>     
    <% 
    } %>
    <%= open ? "</div>" : ""%>
    </div>
  <% 
    i++;
  } %> 
    </div>
   
    <div id='about_details'>
      
      <div class='content'>
<h3 id="faq">Frequently Asked Questions</h3>
        <%
    ArrayList<Setting> faqItems = DBConnect.getCategorySettings(Setting.SettingCategory.FAQ_ITEMS.Id);
    i = 0;
       %>

<div class="accordion" id="accordion2">
 <% for (Setting faqItem : faqItems) { 
 %>
  <div class="accordion-group">
    <div class="accordion-heading">
      <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#faq<%= faqItem.id %>">
        <%=faqItem.label %>
      </a>
    </div>
    <div id="faq<%= faqItem.id %>" class="accordion-body collapse">
      <div class="accordion-inner">
        <%= faqItem.value %>
      </div>
    </div>
  </div>
  <% } %>
</div>

<script>
$('#accordion2').collapse({
  toggle: true
});
</script> 
</div>


     <h3 id="details">Mouse Database Details and Resources</h3>
     <div class="accordion" id="accordion2">
      <div class="accordion-group">  
  <div class="panel-group" id="accordion">
  <div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title"> 
        <a data-toggle="collapse" data-parent="#accordion" style="padding-left: 10px; font-size: 15px" href="#collapse1"> 
        <%=DBConnect.loadSetting("home_page_text_how_mice_are_listed").label%></a>
      </h4>
    </div>
    <div id="collapse1" class="panel-collapse collapse in" >
      <div class="panel-body" style="padding-left: 10px" > 
       <%=DBConnect.loadSetting("home_page_text_how_mice_are_listed").value%>
      </div>
    </div>
  </div>
  <div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title">
        <a data-toggle="collapse" data-parent="#accordion" style="padding-left: 10px; font-size:15px " href="#collapse2">
        <%=DBConnect.loadSetting("home_page_text_information_about_each_mouse").label%></a>
      </h4>
    </div>
    <div id="collapse2" class="panel-collapse collapse">
      <div class="panel-body" style="padding-left: 10px">
      <%=DBConnect.loadSetting("home_page_text_information_about_each_mouse").value%>
      </div>
    </div>
  </div>
  <div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title">
        <a data-toggle="collapse" data-parent="#accordion" style="padding-left: 10px; font-size: 15px " href="#collapse3">
        <%=DBConnect.loadSetting("home_page_text_submitting_mice").label%>
        </a>
      </h4>
    </div>
    <div id="collapse3" class="panel-collapse collapse">
      <div class="panel-body" style="padding-left: 10px"> 
       <%=DBConnect.loadSetting("home_page_text_submitting_mice").value%>
      </div>
    </div>
  </div>
  <div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title">
        <a data-toggle="collapse" data-parent="#accordion" style="padding-left: 10px; font-size: 15px" href="#collapse4">
        <%=DBConnect.loadSetting("home_page_text_introduction").label%>
        </a>
      </h4>
    </div>
    <div id="collapse4" class="panel-collapse collapse">
      <div class="panel-body" style="padding-left: 10px">
     <%=DBConnect.loadSetting("home_page_text_introduction").value%>
     </div>
    </div>
  </div>
</div>
</div>
</div>
<div class='about_footer'>
<div>
<a href="mailto:<%=DBConnect.loadSetting("admin_info_email").value %>"> Contact Administrator</a>
</div>
<div>
v2.1 Mouse Inventory 2019
</div>
<div>
<a href="https://github.com/musIndex/mouseinventory">View in Github
</a>
<i class='icon-share'></i>

</div>
</div>
</div>
</div>
</div>


