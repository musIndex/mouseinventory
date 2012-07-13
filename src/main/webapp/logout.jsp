<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration" %>
<%=HTMLGeneration.getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar(null, false) %>
<div class="site_container">
<%@ page session="true"%>

User '<%=request.getRemoteUser()%>' has been logged out.

<% session.invalidate(); %>

<br/><br/>
</div>
