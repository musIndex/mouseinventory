package edu.ucsf.mousedatabase.dataimport;

import java.util.HashMap;


public interface Expression
{
	public ExpressionMatch match(HashMap<String,String> row);
	public String[] getGroupNames();
	public String getName();
}
