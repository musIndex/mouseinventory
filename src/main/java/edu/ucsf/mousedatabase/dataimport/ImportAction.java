package edu.ucsf.mousedatabase.dataimport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ImportAction
{
  private String name;
  private List<Expression> acceptExpressions;
  private List<Expression> rejectExpressions;
  private List<Expression> outputExpressions;
  private String outputFormat;

  public ImportAction(String name, String outputFormat)
  {
    this.name = name;
    this.outputFormat = outputFormat;
  }

  public boolean rowIsValid(HashMap<String,String> row)
  {
    return meetsAcceptExpression(row) && passesRejectExpression(row);
  }


  //output format would be something like this:
  //Created new change request for holder ${holderid.name} who received mouse %{record.name}


  public String getOutput(HashMap<String,String> row)
  {
    String output = outputFormat;
    for (Expression expression : outputExpressions)
    {
      ExpressionMatch match = expression.match(row);
      if (!match.isMatch())
      {
        //this should not happen, but it might, so...
      }
      for (String groupName : expression.getGroupNames())
      {
        String replaceString = "${" + expression.getName() + "." + groupName + "}";
        String value = match.getGroup(groupName);
        output.replaceAll(replaceString, value);
      }

    }
    return output;
  }

  private boolean meetsAcceptExpression(HashMap<String,String> row)
  {
    for (Expression expression : acceptExpressions)
    {
      ExpressionMatch m = expression.match(row);
      if (!m.isMatch())
      {
        return false;
      }
    }
    return true;
  }

  private boolean passesRejectExpression(HashMap<String,String> row)
  {
    for (Expression expression : rejectExpressions)
    {
      ExpressionMatch m = expression.match(row);
      if (!m.isMatch())
      {
        return false;
      }
    }
    return true;
  }


  public String getName() {
    return name;
  }
  public String getOutputFormat() {
    return outputFormat;
  }



  public void addAcceptExpression(Expression expr)
  {
    addExpression(acceptExpressions, expr);
  }
  public void addRejectExpression(Expression expr)
  {
    addExpression(rejectExpressions, expr);
  }
  public void addOutputExpression(Expression expr)
  {
    addExpression(outputExpressions, expr);
  }
  private void addExpression(List<Expression> list, Expression expr)
  {
    if (list == null)
    {
      list = new ArrayList<Expression>();
    }
    list.add(expr);
  }
}
