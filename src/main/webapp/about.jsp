<%@page import="java.util.HashMap"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@page import="java.util.ArrayList"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%=HTMLGeneration.getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar("about.jsp", false) %>
  
<%
HashMap<Setting.SettingCategory,String> categories = new HashMap<Setting.SettingCategory,String>();
categories.put(Setting.SettingCategory.RECENT_SITE_UPDATES,"Recent site updates:");
categories.put(Setting.SettingCategory.WED_LIKE_TO_HEAR_FROM_YOU,"We'd like to hear from you:");
%>
 
<div class="site_container">

<div class="whatsnew">
<% for(Setting.SettingCategory category : categories.keySet()){ 
    String[] styles = new String[]{"alert alert-success","alert alert-info","alert alert-warning","alert alert-lovely"};
    ArrayList<Setting> recentSiteUpdateItems = DBConnect.getCategorySettings(category.Id, "id desc");
    int i = 0;
    for (Setting newItem : recentSiteUpdateItems) {
     %>
      <% if (i == 0) { %>
      	<h3><%=categories.get(category) %></h3>
      <% } %>
      <div class="<%= styles[i]%>">
        <b><%= newItem.label %></b>
        <br><%= newItem.value %>
      </div>     
    <% 
      i++;
      i %= styles.length;
    } %>
<% } %>
</div>
<div class='about'>
<div class='about-summary'>
<div class='cf'>
<h2 style='float:left'>About the UCSF Mouse Inventory Database</h2>
<% Setting setting = DBConnect.loadSetting("download_files_manual"); %>
<div style='float:right'><a class='btn btn-info' href='<%= setting.value %>'><%=setting.label %></a></div>
</div>
<ol>
  <li><a href="#introduction">Introduction</a></li>
  <li><a href="#purpose">Purpose</a></li>
  <li><a href="#how">How mice are listed</a></li>
  <li><a href="#info">Information provided about each mouse</a></li>
  <li><a href="#search">Search and sort functions</a></li>
  <li><a href="#submitting">Submitting mice, adding or deleting a
holder for a particular mouse, or making other modifications to
information in the Inventory</a></li>
  <li><b><a href="#faq">FAQ</a></b></li>
  <li><a href="#disclaimer">Disclaimer</a></li>
</ol>
</div>

<h3 id="introduction">Introduction</h3>
<%= DBConnect.loadSetting("home_page_text_introduction").value %>

<h3 id="purpose">Purpose</h3>
<%= DBConnect.loadSetting("home_page_text_purpose").value %>

<h3 id="how">How mice are listed</h3>
<%= DBConnect.loadSetting("home_page_text_how_mice_are_listed").value %>

<h3 id="info">Information provided about each mouse</h3>
<%= DBConnect.loadSetting("home_page_text_information_about_each_mouse").value %>

<h3 id="submitting">Submitting mice, adding or deleting a
holder for a particular mouse, or making other modifications to
information in the Inventory</h3>

<%= DBConnect.loadSetting("home_page_text_submitting_mice").value %>

<!-- Uncomment and revise once the endangered list is finalized
<p>"If a holder is considering eliminating a mouse from his/her
colony, it can be marked as "endangered" on the "request change form."
If that holder is the only one who maintains the mouse, or if there is
only one other holder, the mouse will be added to the "endangered mouse"
list, which is a selection on the "Mouse Records." The mouse will remain on
the "endangered list" until the holder who classified it as endangered
asks to be deleted as a holder.</p>
-->

<h3 id="faq">Frequently Asked Questions</h3>

<ul>
  <li><a href="faq.jsp">Who has access to the UCSF mouse inventory database?</a></li>
  <li><a href="faq.jsp">Are investigators required to list all the mice in their colonies?</a></li>
  <li><a href="faq.jsp">Does listing a mouse in the database obligate an investigator to provide
  it to other investigators at UCSF?</a></li>
  <li><a href="faq.jsp">Should unpublished mice be entered?</a></li>
  <li><a href="faq.jsp">If a mouse was originally obtained pursuant to a material transfer agreement (MTA),
  or if a mouse was produced at UCSF using materials obtained pursuant to an MTA, can the mouse
  (or its descendants) be passed to another investigator at UCSF without completing a new MTA?</a></li>
</ul>

<br><br>

<h3 id="disclaimer">Disclaimer:</h3>

<p>The accuracy of all information about a mouse is the
responsibility of the individual who submitted the data. Information
provided on submission forms is not vetted by the Administrator.</p>

</div>
</div>