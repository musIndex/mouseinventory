<%@page import="edu.ucsf.mousedatabase.dataimport.*"%>
<%@page import="edu.ucsf.mousedatabase.Log"%>

<%
//pull out param - task header or task body, and task Id

String taskIdParm = request.getParameter(ImportStatusTracker.TaskIdKey);
String command = request.getParameter(ImportStatusTracker.CommandKey);
String data = "";
if (taskIdParm != null && command != null)
{
	try
	{
		int taskId = Integer.parseInt(taskIdParm);
		ImportTask task = ImportStatusTracker.GetTask(taskId);
		if (command.equals(ImportStatusTracker.HistoryCommand)){
			data = task.History;
		}
		else if (command.equals(ImportStatusTracker.SummaryCommand))
		{
			data = task.GetSummary(taskId);
		}
	}
	catch(Exception e)
	{
		Log.Error("Error getting import status body",e);
	}
}
%>
<%=data%>