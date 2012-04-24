package edu.ucsf.mousedatabase.objects;

public class Holder {


	private int holderID;
	private String firstname;
	private String lastname;
	private String email;
	private String alternateEmail;
	private String dept;
	private String tel;
	private String dateValidated;
	private String validationComment;
	
	private int visibleMouseCount;
	
	
	public String getFullname()
	{
		return firstname + " " + lastname;
	}
	
	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public int getHolderID() {
		return holderID;
	}
	public void setHolderID(int holderID) {
		this.holderID = holderID;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getAlternateEmail() {
		return alternateEmail;
	}


	public void setAlternateEmail(String alternateEmail) {
		this.alternateEmail = alternateEmail;
	}


	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}


	public int getVisibleMouseCount()
	{
		return visibleMouseCount;
	}


	public void setVisibleMouseCount(int mouseCount)
	{
		this.visibleMouseCount = mouseCount;
	}


	public String getDateValidated() {
		return dateValidated;
	}


	public void setDateValidated(String dateValidated) {
		this.dateValidated = dateValidated;
	}


	public String getValidationComment() {
		return validationComment;
	}


	public void setValidationComment(String validationComment) {
		this.validationComment = validationComment;
	}
	
}
