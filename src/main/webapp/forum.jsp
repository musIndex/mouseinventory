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
<h1 style="margin-top:40px !important;">Find collaborators with similar animal needs and share costs
 </h1>
 <h2 ><i>Mouselist Current Posts</i></h2>
 <h2>    To view full POSTING or Submit post go to site <a class="btn btn-info" href="<%=DBConnect.loadSetting("general_site_mouselist")%>" 
style="margin-left:10px !important;">UCSF Mouselist App</a></h2>
<a href="<%=DBConnect.loadSetting("general_site_mouselist")%>" >
  <iframe src= "<%=DBConnect.loadSetting("mouselist_fnct")%>" width="50%" height="200" style="border:3px solid black" allowfullscreen sandbox="allow-same-origin">
  </iframe>
  
      <img src="<%=imageRoot %>mouselist_target.png" title='Click to Mouselist' width='250px' class="woodmouse" style="padding-left:100px !important;"/>
      </a>
      <h3>Mouselist runs best on <i>Chrome</i> or <i>Firefox</i>. Single sign-on required.</h3>
      <p>Contact Database Administrator with questions about UCSF Mouselist at <%=HTMLGeneration.formatEmail(adminEmail, adminEmail, null) %></p>
</div>
<%=getUCSFFooter()%>

