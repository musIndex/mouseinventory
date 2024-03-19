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
<script type='text/javascript'>
$(document).ready(function(){
  $("span.submission_number").wrap(function(){ return "<a href='<%=HTMLGeneration.adminRoot %>CreateNewRecord.jsp?id=" + $(this).text().substring(1) + "' target='blank' />";});
  $("span.changerequest_number").wrap(function(){ return "<a href='<%=HTMLGeneration.adminRoot %>CompleteChangeRequest.jsp?id=" + $(this).text().substring(1) + "' target='blank' />";});
  $("span.pubmed_number").wrap(function(){ return "<a href='http://www.ncbi.nlm.nih.gov/pubmed?term=" + $(this).text()+ "' target='blank' />";});

});
</script>
<div class="site_container">
<h2>Data Uploads History</h2>

<a href="ImportData.jsp">Upload new data...</a>
<br>
<a href="ImportStatus.jsp">Status of uploads in progress...</a>
<br>

<%if(reports.size() > 0) {%>
<p>
<a id="showImpData" href="javascript:" onclick="changecss('span.rawRecord','display','');setTwoElementVisibility('showImpData','none','hideImpData','')">Show Raw Data</a>
<a style="display:none" id="hideImpData" href="javascript:" onclick="changecss('span.rawRecord','display','none');setTwoElementVisibility('showImpData','','hideImpData','none')">Hide Raw Data</a>
</p>
<%} %>
</div><div class='site_container'>
<%=table %>



</div>
