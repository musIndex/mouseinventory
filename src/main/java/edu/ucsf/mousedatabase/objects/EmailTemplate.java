package edu.ucsf.mousedatabase.objects;

import java.sql.Timestamp;

public class EmailTemplate {
  
  public static final String CATEGORY_SUBMISSION = "Submission";
  public static final String CATEGORY_COMPLETESUBMISSION = "Completed Submission";
  public static final String CATEGORY_CHANGREQUEST = "Change Request";
  public static final String CATEGORY_MOUSERECORD = "Mouse Record";
  
  public static String[] getCategories(){
    return new String[]{
        CATEGORY_SUBMISSION,
        CATEGORY_COMPLETESUBMISSION,
        CATEGORY_CHANGREQUEST,
        CATEGORY_MOUSERECORD
        };
  }
  
  public int id;
  public String name;
  public String subject;
  public String body;
  public String category;
  public Timestamp dateUpdated;
  
  
}
