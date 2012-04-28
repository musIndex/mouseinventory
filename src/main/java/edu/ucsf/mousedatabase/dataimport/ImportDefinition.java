package edu.ucsf.mousedatabase.dataimport;



public class ImportDefinition
{
  public int Id;
    public String Name;
  public String Description;  
  //public List<ImportAction> rowActions;
  
  public ImportDefinition()
  {
    
  }
  
  public ImportDefinition(int id, String name, String description)
  {
    this.Id = id;
    this.Name = name;
    this.Description = description;
  }
  
  public ImportDefinition(String definitionXml)
  {
    //TODO parse XML
  }
  
  //TEMPORARY method until xml parsing is implemented
//  public static ImportDefinition getPPTReportDefinition()
//  {
//    ImportDefinition def = new ImportDefinition();
//
//    def.importType = ImportType.PPTCHANGEREQUEST;
//    def.name = "PI to PI Transfer Import";
//    def.description = "Create change requests to add holder to mice based on LARC mouse transfer report";
//
//    return def;
//  }
//  
//  public static ImportDefinition getPurchaseImportDefinition()
//  {
//    
//    ImportDefinition def = new ImportDefinition();
//    
//    def.importType = ImportType.PURCHASESUBMISSION;
//    def.name = "New Purchases Import";
//    def.description = "Create submissions for new mice or change requests to add holders to existing mice based on a LARC mouse purchases report";
//    
//    return def;
//  }
  
  
//  public String processRow(HashMap<String,String> row)
//  {
//    StringBuilder sb = new StringBuilder();
//    
//    //Determine row type
//    List<ImportAction> validRowTypes = new ArrayList<ImportAction>();
//    for (ImportAction rowDefinition : rowActions) 
//    {
//      if (rowDefinition.rowIsValid(row))
//      {
//        validRowTypes.add(rowDefinition);
//      }
//    }
//    
//    //Handle case where multiple Actions matched the row
//    if (validRowTypes.size() > 1)
//    {
//      String rowTypes = "";
//      for (ImportAction def : validRowTypes)
//      {
//        rowTypes += def.getName() + ", ";
//      }
//      HTMLUtilities.appendline(sb,HTMLUtilities.p("Rejected row because it matched multiple ImportRowActions: " + rowTypes) + rowToString(row));
//    }
//    else if (validRowTypes.size() > 0)
//    {
//      //normal case, we matched one definition.  
//      //extract values and output result
//      ImportAction def = validRowTypes.get(0);
//      HTMLUtilities.appendline(sb,HTMLUtilities.p("Accepted row.  Matched definition: " + def.getName()) + parseRow(row,def) + rowToString(row));
//      
//    }
//    else
//    {
//      //row did not match any Actions, reject it
//      HTMLUtilities.appendline(sb,HTMLUtilities.p("Rejected row because it did not match anyImportRowActions.") + rowToString(row));
//    }  
//    return row(sb.toString());
//  }
//  
//  private String parseRow(HashMap<String, String> row,ImportAction def) 
//  {
//    
//    
//    return null;
//  }
//
//  private static String row(String rowText)
//  {
//    return "<div class='ImportRow'>" + rowText + "</div>";
//  }
//  
//  
//    private static String rowToString(HashMap<String,String> row)
//  {
//    String rowString = "<div class='rawRecord'>";
//    for (Object key : row.keySet())
//    {
//      rowString += key + "=" + row.get(key) + " ";
//    }
//    rowString += "</div>";
//    return rowString;
//  }


}
