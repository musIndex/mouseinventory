<%@page import="java.util.SortedMap"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@page import="java.util.ArrayList"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="edu.ucsf.mousedatabase.*" %>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>

<%=getPageHeader(null, false, false, "onload=\"setFocus('quickSearchForm', 'searchterms')\"")%>
<%=getNavBar("forum.jsp", false)%>
<%
  String adminEmail = DBConnect.loadSetting("admin_info_email").value;
%>
<div class="site_container">
<h2 style="margin-top:40px !important;">Find collaborators with similar animal needs and share costs<a class="btn btn-info" href="<%=DBConnect.loadSetting("general_site_mouselist")%>" style="margin-left:20px !important;">Mouselist App</a>
 </h2>
<a href="<%=DBConnect.loadSetting("general_site_mouselist")%>" >
      <img src="<%=imageRoot %>mouselist_target.png" title='Click to Mouselist' width='250px' class="woodmouse" style="padding-left:200px !important;"/>
      </a>
      <p>Contact Database Administrator with questions about UCSF Mouselist at <%=HTMLGeneration.formatEmail(adminEmail, adminEmail, null) %></p>
</div>


