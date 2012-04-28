<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="java.util.ArrayList" %>
<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("ImportReports.jsp", true) %>

<%

  ArrayList<ImportReport> reports = DBConnect.getAllImportReports();
  String table = HTMLGeneration.getImportReportTable(reports,false);


%>
<div class="pagecontent">
<h2>Data Imports History</h2>

<a href="ImportData.jsp">Import new data...</a>
<br>
<a href="ImportStatus.jsp">Status of imports in progress...</a>
<br>

<%if(reports.size() > 0) {%>
<p>
<a id="showImpData" href="javascript:" onclick="changecss('span.rawRecord','display','');setTwoElementVisibility('showImpData','none','hideImpData','')">Show Raw Data</a>
<a style="display:none" id="hideImpData" href="javascript:" onclick="changecss('span.rawRecord','display','none');setTwoElementVisibility('showImpData','','hideImpData','none')">Hide Raw Data</a>
</p>
<%} %>
<%=table %>



</div>