<%@page import="java.util.HashMap"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@page import="java.util.ArrayList"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>
<%=getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=getNavBar(null, false) %>


<%
  String command = request.getParameter("com");
  int id = stringToInt(request.getParameter("obj"));

  String title = "";
  String table = "The link you entered is no longer valid";
  
  if (command.equals("rec")) {
    if (id > 0) {
      ArrayList<MouseRecord> records = DBConnect.getMouseRecord(id);
      MouseRecord record = records.get(0);
      if (record.getStatus().equalsIgnoreCase("incomplete")) {
        table = getMouseTable(records,false,false,false);
        title = "Preview of incomplete record #" + record.getMouseID();
      }
    }
  }
  

%>

<div class="site_container">
  <h2><%= title %></h2>
  <%= table %>
</div>