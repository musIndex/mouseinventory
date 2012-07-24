package edu.ucsf.mousedatabase.objects;

import java.sql.Timestamp;

public class EmailTemplate {
  
  public static final String SUBMISSION = "Submissions";
  //public static final String COMPLETESUBMISSION = "Completed Submission";
  public static final String CHANGREQUEST = "Change Requests";
  //public static final String CHANGEREQUESTCOMPLETED = "Completed Change Request";
  //public static final String CHANGEREQUESTINCOMPLETE = "Incomplete Change Request";
  public static final String MOUSERECORD = "Edit Records";
  //public static final String INCOMPLETESUBMISSION = "Incomplete Submission";
  public static final String HOLDER = "Edit Holders";
  public static final String PDU_SUBMISSION = "PDU - Submissions";
  public static final String PDU_CHANGEREQUEST = "PDU - Change Requests";
  public static final String TDU_CHANGEREQUEST = "TDU - Change Requests";
  public static final String IDU_SUBMISSION = "IDU - Submissions";
  public static final String IDU_CHANGEREQUEST = "IDU - Change Requests";
  
  
   
  public static String[] getCategories(){
    return new String[]{
        SUBMISSION,
        //COMPLETESUBMISSION,
        //INCOMPLETESUBMISSION,
        CHANGREQUEST,
        //CHANGEREQUESTCOMPLETED,
        //CHANGEREQUESTINCOMPLETE,
        MOUSERECORD,
        HOLDER,
        PDU_SUBMISSION,
        PDU_CHANGEREQUEST,
        TDU_CHANGEREQUEST,
        IDU_SUBMISSION,
        IDU_CHANGEREQUEST
        };
  }
  
  public int id;
  public String name;
  public String subject;
  public String body;
  public String category;
  public Timestamp dateUpdated;
  
  
}
