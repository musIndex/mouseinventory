<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%@page import="edu.ucsf.mousedatabase.Log" %>
<%@ page isErrorPage="true" import="java.io.*" %>
<%=HTMLGeneration.getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar(null, false) %>

<%
  Log.Error("Unhandled Exception",exception);
%>

<div class="pagecontent">
<h2>Unknown Error.</h2>
<p>We're sorry, but the Mouse Inventory Database has encountered an error.  
Please notify the administrator.</p>
<h3>Error details:</h3>
<%-- Exception Handler --%>

<pre><font color="red"><%= exception %></font></pre>

<% if (request != null && request.getRemoteUser() != null && request.getRemoteUser().equals("admin"))
{
  %>
  <h4>Admin is logged in, showing stack trace.</h4>
  <div style="font-size: 11px; line-height:15px;">
  <%if (exception != null) { %>
  <PRE><% exception.printStackTrace(new PrintWriter(out)); %></PRE>
  <%} %>
  </div>
  <%
} 
%>



</div>