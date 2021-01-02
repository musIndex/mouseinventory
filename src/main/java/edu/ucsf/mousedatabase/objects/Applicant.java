package edu.ucsf.mousedatabase.objects;

public class Applicant {
    /*
    The applicant class is used for storing information about each user who submits an
    applicant on the RRD to be able to look at the rodent records and submit new records.

    This class can be edited as needed, but be sure to change information relating to it
    in the following other classes:
    ~DBConnect 1811-1900
    ~HTMLGeneration 268-338
    ~Applicant Servlet
    ~Login Servlet (if you're editing login information)
    ~Status Servlet (if you're editing approval status)

    JSP files that may be affected by changing the applicant class:
    ~application.jsp
    ~applicationLoginRecords.jsp
    ~applicationLoginSubmit.jsp
    ~applicationSubmission.jsp
    */
    private String first_name;
    private String last_name;
    private String email;
    private String netID;
    private String AUF;
    private String position;
    private int approved;
    private int recordCount;
    private int id;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getRecordCount()
    {
        return recordCount;
    }
    public void setRecordCount(int recordCount)
    {
        this.recordCount = recordCount;
    }
    public String getFirst_name() {
        return first_name;
    }
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
    public String getLast_name() {
        return last_name;
    }
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getNetID() {
        return netID;
    }
    public void setNetID(String netID) {
        this.netID = netID;
    }
    public String getAUF() {
        return AUF;
    }
    public void setAUF(String AUF) {
        this.AUF = AUF;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public int getApproved() {
        return approved;
    }
    public void setApproved(int approved) {
        this.approved = approved;
    }



}
