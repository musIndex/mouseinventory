package edu.ucsf.mousedatabase.dataimport;

import java.text.NumberFormat;
import java.util.Date;

import edu.ucsf.mousedatabase.HTMLGeneration;
import edu.ucsf.mousedatabase.dataimport.ImportStatusTracker.ImportStatus;

public class ImportTask
{
	public ImportStatus Status;
	public String Header;
	public double PercentComplete;
	public String History;
	public Date StartDate;
	public Date EndDate;
	public String Title;
	
	public ImportTask(String title)
	{
		StartDate = new Date(System.currentTimeMillis());
		Title = title;
		PercentComplete = 0;
		Status = ImportStatus.PENDING;
		EndDate = null;
	}
	
	public String GetSummary(int taskId)
	{
		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMaximumFractionDigits(1);
		StringBuilder sb = new StringBuilder();
		sb.append("<span  class=\"importStatusTitle\">" + HTMLGeneration.emptyIfNull(Title) 
				+ "</span>: <span id=\"taskStatus-"+taskId+"\">" +  Status +"</span><br>");
		sb.append("<span class=\"importStatusHeader\">" + HTMLGeneration.emptyIfNull(Header) + "</span> ");
		sb.append("<span class='progressPercentage'>" + percentFormat.format(PercentComplete) + " complete</span><br>");
		
		
		
		return sb.toString();
	}
	
}
