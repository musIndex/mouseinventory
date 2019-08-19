package edu.ucsf.mousedatabase.objects;

import java.sql.Timestamp;

public class EmailTemplate {
  
  public static final String SUBMISSION = "Submissions";
  public static final String CHANGEREQUEST = "Change Requests";
  public static final String MOUSERECORD = "Edit Records";
  public static final String HOLDER = "Edit Holders";
  public static final String PDU_SUBMISSION = "PDU - Submissions";
  public static final String PDU_CHANGEREQUEST = "PDU - Change Requests";
  public static final String TDU_CHANGEREQUEST = "TDU - Change Requests";
  public static final String IDU_SUBMISSION_PUBLISHED = "IDU - Submissions (published)";
  public static final String IDU_SUBMISSION_UNPUBLISHED = "IDU - Submissions (unpublished)";
  
  
   
  public static String[] getCategories(){
    return new String[]{
        SUBMISSION,
        CHANGEREQUEST,
        MOUSERECORD,
        HOLDER,
        PDU_SUBMISSION,
        PDU_CHANGEREQUEST,
        TDU_CHANGEREQUEST,
        IDU_SUBMISSION_PUBLISHED,
        IDU_SUBMISSION_UNPUBLISHED
      };
  }
  
  public int id;
  public String name;
  public String subject;
  public String body;
  public String category;
  public Timestamp dateUpdated;
  
  
}
