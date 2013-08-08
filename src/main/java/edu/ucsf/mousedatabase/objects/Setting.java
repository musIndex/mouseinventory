package edu.ucsf.mousedatabase.objects;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Setting {

  public enum SettingCategory {
    
    UNKNOWN(0,"unkown",false,true,false,false),
    ADMIN_NOTES(1,"Admin notes",true,true,true,true),
    HOME_PAGE_TEXTS(2,"Home page texts",false,false,true,true),
    RECENT_SITE_UPDATES(3,"Recent site updates",true,false,true,true,"Custom style"),
    ADMIN_DATA(4,"Admin information"),
    GENERAL_SITE(5,"General settings"),
    DATA_IMPORT(6,"Data upload settings"),
    WED_LIKE_TO_HEAR_FROM_YOU(7,"Help keep the database up-to-date",true,false,true,true,"Custom style"),
    DOWNLOAD_FILES(8,"Downloadable files",false,false,false,true),
    DID_YOU_KNOW(9,"Did you know? items",true,false,true,true,"Custom style"),
    FAQ_ITEMS(10, "FAQ items", true, false, true, true), 
    NEED_HELP_ITEMS(11, "Need help using the database items?", true, false, true, true, "Custom style"), 
    HOLDER_LIST_TEXTS( 12, "Holder list texts", true, false, true, true);

    public final int Id;
    public final String Name;
    public final boolean CanAddOrRemove;
    public final boolean Excluded;
    public final boolean RichText;
    public final boolean CanChangeLabel;
    public final String SecondaryValueName;

    private SettingCategory(int id, String name) {
      this(id, name, false, false, false, false);
    }

    private SettingCategory(int id, String name, boolean canAddOrRemove, boolean excluded, boolean richText,
        boolean canChangeLabel) {
      this(id, name, canAddOrRemove, excluded, richText, canChangeLabel, null);
    }

    private SettingCategory(int id, String name, boolean canAddOrRemove, boolean excluded, boolean richText,
        boolean canChangeLabel, String secondaryValueName) {
      this.Id = id;
      this.Name = name;
      this.CanAddOrRemove = canAddOrRemove;
      this.Excluded = excluded;
      this.RichText = richText;
      this.CanChangeLabel = canChangeLabel;
      this.SecondaryValueName = secondaryValueName;
    }

  }

  public static SettingCategory getSettingCategory(int id) {
    switch (id) {
      case 1:
        return SettingCategory.ADMIN_NOTES;
      case 2:
        return SettingCategory.HOME_PAGE_TEXTS;
      case 3:
        return SettingCategory.RECENT_SITE_UPDATES;
      case 4:
        return SettingCategory.ADMIN_DATA;
      case 5:
        return SettingCategory.GENERAL_SITE;
      case 6:
        return SettingCategory.DATA_IMPORT;
      case 7:
        return SettingCategory.WED_LIKE_TO_HEAR_FROM_YOU;
      case 8:
        return SettingCategory.DOWNLOAD_FILES;
      case 9:
        return SettingCategory.DID_YOU_KNOW;
      case 10:
        return SettingCategory.FAQ_ITEMS;
      case 11:
        return SettingCategory.NEED_HELP_ITEMS;
      case 12:
        return SettingCategory.HOLDER_LIST_TEXTS;
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
  public String secondaryValue;
  public Timestamp dateUpdated;
  public int textAreaRows;

  public int asInt() {
    return Integer.parseInt(value);
  }

  public boolean asBoolean() {
    return Boolean.parseBoolean(value);
  }

  public String asString() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}
