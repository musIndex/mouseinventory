package edu.ucsf.mousedatabase.beans;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserData {

  private String firstName;
  private String lastName;
  private String email;
  private String telephoneNumber;
  private String department;

  private String firstNameErr;
  private String lastNameErr;
  private String emailErr;
  private String telephoneNumberErr;
  private String departmentErr;

  public boolean ValidateContactInfo()
  {
    boolean valid = true;
    clearContactInfoErrors();
    if(firstName == null || firstName.isEmpty())
    {
      valid = false;
      firstNameErr = "Please enter your first name";
    }
    if(lastName == null || lastName.isEmpty())
    {
      valid = false;
      lastNameErr = "Please enter your last name";
    }
    if(email == null || email.isEmpty())
    {
      valid = false;
      emailErr = "Please enter your email address";
    }
    else
    {
      Pattern ptn = Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}", Pattern.CASE_INSENSITIVE);
      Matcher matcher = ptn.matcher(email);

        if(!(matcher.find() && matcher.matches()))
        {
          valid = false;
          emailErr = "Please enter a valid email address";
        }
    }
    if(department == null || department.isEmpty())
    {
      valid = false;
      departmentErr = "Please enter your department";
    }
    return valid;
  }

  private void clearContactInfoErrors()
  {
    this.firstNameErr = this.lastNameErr = this.departmentErr = this.emailErr = this.telephoneNumberErr = null;
  }

  public String getFullname()
  {
    return firstName + " " + lastName;
  }

  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getTelephoneNumber() {
    return telephoneNumber;
  }
  public void setTelephoneNumber(String telephoneNumber) {
    this.telephoneNumber = telephoneNumber;
  }
  public String getDepartment() {
    return department;
  }
  public void setDepartment(String department) {
    this.department = department;
  }

  public String getFirstNameErr() {
    return firstNameErr;
  }

  public void setFirstNameErr(String firstNameErr) {
    this.firstNameErr = firstNameErr;
  }

  public String getLastNameErr() {
    return lastNameErr;
  }

  public void setLastNameErr(String lastNameErr) {
    this.lastNameErr = lastNameErr;
  }

  public String getEmailErr() {
    return emailErr;
  }

  public void setEmailErr(String emailErr) {
    this.emailErr = emailErr;
  }

  public String getTelephoneNumberErr() {
    return telephoneNumberErr;
  }

  public void setTelephoneNumberErr(String telephoneNumberErr) {
    this.telephoneNumberErr = telephoneNumberErr;
  }

  public String getDepartmentErr() {
    return departmentErr;
  }

  public void setDepartmentErr(String departmentErr) {
    this.departmentErr = departmentErr;
  }

}
