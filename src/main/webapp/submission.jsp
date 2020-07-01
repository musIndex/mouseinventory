<%@page import="java.util.SortedMap"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@page import="java.util.ArrayList"%>  
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>
<%@page import="edu.ucsf.mousedatabase.RGDConnect"%>

<%=getPageHeader(null, false, false, "onload=\"setFocus('quickSearchForm', 'searchterms')\"")%>
<%=getNavBar("about.jsp", false)%>

<div class='iwanna'>I would like to...</div>  
<div class='primary_actions'>
  <a href='<%=siteRoot%>submitratinit.jsp'><i class='icon-plus'></i> <span>Submit a new rat</span></a>
  <a href='<%=siteRoot%>submitforminit.jsp'><i class='icon-plus'></i> <span>Submit a new mouse</span></a>
</div>
</div>