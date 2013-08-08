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

  
<%

ArrayList<Setting.SettingCategory> categories = new ArrayList<Setting.SettingCategory>();
categories.add(Setting.SettingCategory.NEED_HELP_ITEMS);
categories.add(Setting.SettingCategory.WED_LIKE_TO_HEAR_FROM_YOU);
categories.add(Setting.SettingCategory.RECENT_SITE_UPDATES);
categories.add(Setting.SettingCategory.DID_YOU_KNOW);
ArrayList<String> categoryLabels = new ArrayList<String>();
categoryLabels.add("Need help using the database?");
categoryLabels.add("Help keep the database up-to-date:");
categoryLabels.add("Recent site updates:");
categoryLabels.add("Did you know...?");
%>

<div class='site_container'>
  <div class='about_container'>
    <div class='about_welcome'>
      <h1>Welcome to the UCSF Mouse Inventory Database</h1>
      <div class='purpose'><%= DBConnect.loadSetting("home_page_text_purpose").value %></div>
    </div>
    <div class='about_banner'>
      <div class='iwanna'>I would like to...</div>  
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
    %><div class='category cf'>
    <h3><%=categoryLabels.get(i) %></h3>
    
    <%
    boolean open = false;
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
        <b><%= newItem.label %></b>
        <br><%= newItem.value %>
      </div>     
    <% 
    } %>
    <%= open ? "</div>" : "" %>
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
      <div class='content'>
        <h3 id="how"><%=DBConnect.loadSetting("home_page_text_how_mice_are_listed").label%></h3>
        <%=DBConnect.loadSetting("home_page_text_how_mice_are_listed").value%>
      </div>
      <div class='content'>
        <h3 id="info"><%=DBConnect.loadSetting("home_page_text_information_about_each_mouse").label%></h3>
        <%=DBConnect.loadSetting("home_page_text_information_about_each_mouse").value%>
      </div>
      <div class='content'>
        <h3 id="submitting"><%=DBConnect.loadSetting("home_page_text_submitting_mice").label%></h3>

        <%=DBConnect.loadSetting("home_page_text_submitting_mice").value%>
      </div>
      
      <div class='content'>
        <h3 id="technical"><%=DBConnect.loadSetting("home_page_text_introduction").label%></h3>

        <%=DBConnect.loadSetting("home_page_text_introduction").value%>
      </div>
    </div>
  </div>
</div>