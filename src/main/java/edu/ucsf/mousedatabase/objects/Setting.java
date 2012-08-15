package edu.ucsf.mousedatabase.objects;

import java.sql.Timestamp;

public class Setting {
  
  public static final String ADMIN_NOTES_CATEGORY = "admin_notes";
  
  public int id;
  public String category;
  public String name;
  public String label;
  public String value;
  public Timestamp dateUpdated;
  
  
  public int asInt(){
    return Integer.parseInt(value);
  }
  
  public boolean asBoolean(){
    return Boolean.parseBoolean(value);
  }
  
  public String asString(){
    return value;
  }
}
