package edu.ucsf.mousedatabase.objects;

public class Setting {
  public int id;
  public String category;
  public String name;
  public String label;
  public String value;
  
  
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
