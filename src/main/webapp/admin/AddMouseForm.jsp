<%@ page import="java.util.ArrayList"%>
<%@ page import="java.sql.*"%>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@ page import="edu.ucsf.mousedatabase.objects.*"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditMouseSelection.jsp", true) %>
<jsp:useBean id="newMouse" class="edu.ucsf.mousedatabase.objects.MouseRecord" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="newMouse"/>

<%
  String mouseType = request.getParameter("mouseType");
  if (mouseType == null || mouseType.isEmpty())
  {
    %>
    <div class="site_container">
    <h2>Error - no rodent type received</h2>
    </div>

    <%
    return;
  }

    String editForm = HTMLGeneration.getNewMouseForm(newMouse);
    ArrayList<MouseRecord> records = new ArrayList<MouseRecord>();
    records.add(newMouse);
    String recordPreview = HTMLGeneration.getMouseTable(records,false,false,true);

%>


<div class="site_container">
<h2>Creating new record: <%=HTMLGeneration.emptyIfNull(newMouse.getMouseName()) %> (<%= newMouse.getMouseType() %>)
</h2>
<h2>Record Preview:</h2>
<%=recordPreview %>
<br>
<%=editForm %>
</div>
