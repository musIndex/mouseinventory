package edu.ucsf.mousedatabase.dataimport;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextExpression implements Expression
{
	private String name;
	private String columnName;
	private String regex;
	private String[] groupNames;
	
	public TextExpression(String name, String columnName, String regex, String[] groupNames)
	{
		this.name = name;
		this.columnName = columnName;
		this.regex = regex;
		this.groupNames = groupNames;
	}
	
	public ExpressionMatch match(HashMap<String,String> row)
	{	
		ExpressionMatch result = new ExpressionMatch();
		Pattern ptn = Pattern.compile(regex);
		Matcher match = ptn.matcher(row.get(columnName));

		//String result = null;	
		if (match.find()) 
		{
			result.setMatch(true);
			for (int i = 0; i < match.groupCount(); i++) 
			{
				result.addGroup(groupNames[i],match.group(i));
			}
		}

		return result;
	}

	public String[] getGroupNames() {
		return groupNames;
	}
	public String getName()
	{
		return name;
	}
	
}
