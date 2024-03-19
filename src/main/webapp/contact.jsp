<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="edu.ucsf.mousedatabase.*" %>
<%=HTMLGeneration.getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar("contact.jsp", false) %>

<div class="site_container">
    <h2>Submit Feedback</h2>

Contact <%=DBConnect.loadSetting("admin_info_name").value %>, 
<a href="mailto:<%=DBConnect.loadSetting("admin_info_email").value %>"><%=DBConnect.loadSetting("general_site_name").value %> Administrator</a><br>

</div>
