<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%@page import="edu.ucsf.mousedatabase.Log" %>
<%@ page isErrorPage="true" import="java.io.*" %>
<%=HTMLGeneration.getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar(null, false) %>

<%
  Log.Error("Unhandled Exception",exception);
%>

<div class="site_container">
  <p class="main_header">Unknown Error</p>
  <p class="description_text">We're sorry, but the MSU Rodent Database has encountered an error. <br>
    Please notify the administrator.</p>
  <p class="label_text" style="font-weight: bold">Error details:</p>
  <%-- Exception Handler --%>

  <pre class="description_text"><%= exception %></pre>

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

<%=HTMLGeneration.getWebsiteFooter()%>
