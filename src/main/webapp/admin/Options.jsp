<%@page import="edu.ucsf.mousedatabase.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="java.util.*" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("Options.jsp", true) %>

<%
  String[] emailCategories = new String[]{"Change Request","Submission"};

  HashMap<String,ArrayList<Setting>> emailTemplates = new HashMap<String,ArrayList<Setting>>();

  for (String category : emailCategories) {
    emailTemplates.put(category, DBConnect.getCategorySettings(category));
  }
%>

<div class='site_container'>
<h2>Options</h2>

<h3>Email Options</h3>
<a href='EmailLog.jsp'>Email log</a>
<br>




</div>