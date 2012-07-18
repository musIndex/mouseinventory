package edu.ucsf.mousedatabase.objects;

import java.sql.Timestamp;

public class EmailTemplate {
  
  public static final String SUBMISSION = "Submission";
  public static final String COMPLETESUBMISSION = "Completed Submission";
  public static final String CHANGREQUEST = "Change Request";
  public static final String MOUSERECORD = "Mouse Record";
  public static final String INCOMPLETESUBMISSION = "Incomplete Submission";
  
  public static final String MOUSE_TYPE = "mouse";
  public static final String REQUEST_TYPE = "request";
  public static final String SUBMISSION_TYPE = "submission";
  
  public static String[] getCategories(){
    return new String[]{
        SUBMISSION,
        COMPLETESUBMISSION,
        INCOMPLETESUBMISSION,
        CHANGREQUEST,
        MOUSERECORD
        };
  }
  
  public int id;
  public String name;
  public String subject;
  public String body;
  public String category;
  public Timestamp dateUpdated;
  
  
}
