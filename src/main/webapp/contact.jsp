<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration" %>
<%=HTMLGeneration.getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar("contact.jsp", false) %>

<div class="pagecontent">
    <h2>Contact info</h2>

Contact <a href="mailto:<%=HTMLGeneration.AdminEmail %>">Mouse Inventory Administrator</a><br>

</div>