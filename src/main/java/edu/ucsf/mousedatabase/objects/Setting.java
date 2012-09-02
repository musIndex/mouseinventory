package edu.ucsf.mousedatabase.objects;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Setting {
      
  public enum SettingCategory {
    
    UNKNOWN(0,"unkown",false,true,false),
    ADMIN_NOTES(1,"Admin notes",true,true,true),
    HOME_PAGE_TEXTS(2,"Home page texts",false,false,true),
    WHATS_NEW_ITEMS(3,"Whats new items",true,false,true),
    ADMIN_DATA(4,"Admin information",false,false,false),
    GENERAL_SITE(5,"General settings",false,false,false),
    DATA_IMPORT(6,"Data upload settings",false,false,false);
    
    public final int Id;
    public final String Name;
    public final boolean CanAddOrRemove;
    public final boolean Excluded;
    public final boolean RichText;
    
    private SettingCategory(int id, String name, boolean canAddOrRemove, boolean excluded, boolean richText) {
      this.Id = id;
      this.Name = name;
      this.CanAddOrRemove = canAddOrRemove;
      this.Excluded = excluded;
      this.RichText = richText;
    }

  }
  
  public static SettingCategory getSettingCategory(int id)
  {
    switch(id) {
      case 1:
        return SettingCategory.ADMIN_NOTES;
      case 2:
        return SettingCategory.HOME_PAGE_TEXTS;
      case 3:
        return SettingCategory.WHATS_NEW_ITEMS;
      case 4:
        return SettingCategory.ADMIN_DATA;
      case 5:
        return SettingCategory.GENERAL_SITE;
      case 6:
        return SettingCategory.DATA_IMPORT;
    }
    return SettingCategory.UNKNOWN;
  }
  
  
  public static ArrayList<SettingCategory> getCategories() {
    ArrayList<SettingCategory> categories = new ArrayList<SettingCategory>();
    
    for (SettingCategory category : SettingCategory.values()) {
      if (category.Excluded) {
        continue;
      }
      categories.add(category);
    }
    
    return categories;
  }
 
  
  public int id;
  public int category_id;
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
