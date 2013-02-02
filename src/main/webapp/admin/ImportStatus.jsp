<%@page import="edu.ucsf.mousedatabase.dataimport.ImportStatusTracker"%>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="java.util.ArrayList" %>
<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page import="edu.ucsf.mousedatabase.dataimport.*"%>

<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("ImportReports.jsp", true) %>


<script>
var scrollToBottom = true;
$(document).ready(function() {
  scrollToBottom = true;

  var refreshId = setInterval(loadReport, 1000);
  $.ajaxSetup({ cache: false });
  $('.importHistory').mousedown(pauseScrolling);

});

function loadReport()
{
    //todo make this operate on items individually
    $(".importStatusReport").each(function(){
      var taskId = $(this).attr("taskId");

      var status = $('#taskStatus-'+taskId).html();
      if (status == 'COMPLETED' || status == 'ERROR')
      {
        return;
      }

      $('#history-'+taskId).each(function(){
            $(this).load('/mouseinventory/rawdata/ImportStatusBody.jsp?command=history&taskid='+taskId,function(){
              if (scrollToBottom)
              {
                $('#history-'+taskId).prop({ scrollTop: $(this).prop("scrollHeight") - $(this).height() });
              }
            else
            {
              if (pausedTicks > 10)
              {
                scrollToBottom = true;
              }
              pausedTicks = pausedTicks + 1;
            }
              $('#history-'+taskId).mousedown(pauseScrolling);
            });
          });


       $('#summary-'+taskId).each(function(){
         $(this).load('/mouseinventory/rawdata/ImportStatusBody.jsp?command=summary&taskid='+taskId);
       });

      });

}

function pauseScrolling()
{
  scrollToBottom = false;
  pausedTicks = 0;
}

</script>
<%

StringBuilder sb = new StringBuilder();
for (int importId : ImportStatusTracker.ImportsInProgress())
{

  ImportTask task = ImportStatusTracker.GetTask(importId);

  sb.append("<div class=\"importStatusReport\" taskid=\"" + importId + "\">");
  sb.append("<div class=\"importTaskSummary\" id=\"summary-"+importId+"\">");
  sb.append(task.GetSummary(importId));
  sb.append("</div>");
  sb.append("<div class=\"importHistory\" id=\"history-"+importId+"\">" + HTMLGeneration.emptyIfNull(task.History) + "</div>");
  sb.append("</div>");
}
if (sb.length() == 0)
{
  sb.append("<h3>No active uploads</h3>");
}

%>

<div class="site_container">
<h2>Upload Status</h2>
<a href="ImportReports.jsp">View Results</a>
<br>
<br>
<div id="reports">
<%=sb.toString() %>
</div>

</div>
