package edu.ucsf.mousedatabase.dataimport;
import edu.ucsf.mousedatabase.*;

import java.util.HashMap;

public class ExpressionMatch
{
  private boolean match;
  private HashMap<String,String> groups;
  public boolean isMatch() {
    return match;
  }
  public void setMatch(boolean match) {
    this.match = match;
  }
  public String getGroup(String groupName) {
    return groups.get(groupName);
  }
  public void addGroup(String name, String value) {
    if (groups == null)
    {
      groups = new HashMap<String,String>();
    }
    if (groups.containsKey(name))
    {
      Log.Info("WARNING: group " + name + " already exists!  Make sure your expression is set up to have unique group names  (ExpressionMatch in ImportDefnition.java");
      return;
    }
    groups.put(name, value);
  }
}