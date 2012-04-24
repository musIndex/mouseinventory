package edu.ucsf.mousedatabase.dataimport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import edu.ucsf.mousedatabase.Log;

public class ImportStatusTracker 
{
	public static final String CommandKey = "command";
	public static final String TaskIdKey = "taskid";
	public static final String SummaryCommand = "summary";
	public static final String HistoryCommand = "history";
	
	public static final int CompletedTaskExpirationThreshold = 1000 * 60 *2;
	
	public enum ImportStatus
	{
		PENDING,
		PROCESSING,
		COMPLETED,
		ERROR
	}
	
	private static AtomicInteger jobCounter = new AtomicInteger();
	
	private static HashMap<Integer,ImportTask> CurrentImports = new HashMap<Integer,ImportTask>();
	
	public static int RegisterTask(String header)
	{
		
		ImportTask task = new ImportTask(header);
		task.History = "";
		int taskId = jobCounter.incrementAndGet();
		
		CurrentImports.put(taskId, task);
		
		return taskId;
	}
	
	public static void Update(int taskId,String header,ImportStatus status)
	{
		UpdateHeader(taskId,header);
		UpdateStatus(taskId,status);
	}
	
	public static void UpdateHeader(int taskId,String header)
	{
		if (!CurrentImports.containsKey(taskId))
		{
			return;
		}
		
		ImportTask task = CurrentImports.get(taskId);
		task.Header = header;
		Log.Info("Task " + taskId + " Header changed to " + header);
	}
	
	public static void UpdateTitle(int taskId,String title)
	{
		if (!CurrentImports.containsKey(taskId))
		{
			return;
		}
		
		ImportTask task = CurrentImports.get(taskId);
		task.Title = title;
		Log.Info("Task " + taskId + " Title changed to " + title);
	}
	
	public static void UpdateStatus(int taskId, ImportStatus status)
	{
		if (!CurrentImports.containsKey(taskId))
		{
			return;
		}
		
		ImportTask task = CurrentImports.get(taskId);
		task.Status = status;
		Log.Info("Task " + taskId + " status changed to: " + status);
		if (status == ImportStatus.COMPLETED || status == ImportStatus.ERROR)
		{
			task.EndDate = new Date(System.currentTimeMillis());
		}
	}
	
	public static void SetProgress(int taskId, double percentComplete)
	{
		if (!CurrentImports.containsKey(taskId))
		{
			return;
		}
		
		ImportTask task = CurrentImports.get(taskId);
		task.PercentComplete = percentComplete;
		//Log.Info("Task " + taskId + ": " + percentComplete + " percent complete");
	}
	
	public static void AppendMessage(int taskId, String message)
	{
		if (!CurrentImports.containsKey(taskId))
		{
			return;
		}
		
		ImportTask task = CurrentImports.get(taskId);
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss:SSS");
		Calendar cal = Calendar.getInstance();
		task.History += "<span class='historyTimestamp'>" + dateFormat.format(cal.getTime()) + "</span>";
		task.History +=	"<span class='historyMessage'>"+ message + "</span><br>";
		Log.Info("Task " + taskId + ": " + message);
	}
	
	public static ArrayList<Integer> ImportsInProgress()
	{
		ArrayList<Integer> imports = new ArrayList<Integer>();
		for (int importId = jobCounter.get();importId >= 1;importId--)
		{
			if (!CurrentImports.containsKey(importId))
				continue;
			
			ImportTask task = CurrentImports.get(importId);
			if (task.EndDate != null)
			{
				long timeSinceCompleted = System.currentTimeMillis() - task.EndDate.getTime();
				if (timeSinceCompleted >= CompletedTaskExpirationThreshold)
				{
					continue;
				}
			}
			imports.add(importId);
		}
		return imports;
	}
	
	public static ImportTask GetTask(int taskId)
	{
		if (!CurrentImports.containsKey(taskId))
			return null;
		return CurrentImports.get(taskId);
	}

	
}
