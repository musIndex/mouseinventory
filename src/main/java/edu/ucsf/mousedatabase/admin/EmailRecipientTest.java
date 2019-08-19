package edu.ucsf.mousedatabase.admin;

import org.junit.Test;
import static org.junit.Assert.*;

import edu.ucsf.mousedatabase.admin.EmailRecipientManager.EmailRecipient;
import edu.ucsf.mousedatabase.objects.Holder;
import edu.ucsf.mousedatabase.objects.IHolder;

public class EmailRecipientTest{
  
  
  public static IHolder holderWithoutAlternate() {
    IHolder holder = new Holder();
    holder.setEmail("holder@university.edu");
    return holder;
  }
  public static IHolder holderWithAlternate() {
    IHolder holder = holderWithoutAlternate();
    holder.setAlternateEmail("holder-assistant@university.edu");
    return holder;
  }
  
  
  @Test
  public void testUniqueRequestorHolderWithAlternate(){
    EmailRecipient rec = EmailRecipientManager.recipientsForRequestorAndHolder("requestor@university.edu", holderWithAlternate());
    assertEquals("requestor@university.edu",rec.recipients);
    assertTrue(rec.ccs.contains("holder@university.edu") && rec.ccs.contains("holder-assistant@university.edu"));
  }
  
  @Test
  public void testHolderRequestorAndHolderWithAlternate(){
    EmailRecipient rec = EmailRecipientManager.recipientsForRequestorAndHolder("holder@university.edu", holderWithAlternate());
    assertEquals("holder@university.edu",rec.recipients);
    assertEquals("holder-assistant@university.edu",rec.ccs);
  }
  
  @Test
  public void testHolderDifferentCaseRequestorAndHolderWithAlternate(){
    EmailRecipient rec = EmailRecipientManager.recipientsForRequestorAndHolder("Holder@University.edu", holderWithAlternate());
    assertEquals("holder@university.edu",rec.recipients);
    assertEquals("holder-assistant@university.edu",rec.ccs);
  }
  
  @Test
  public void testUniqueRequestorAndHolderWithoutAlternate(){
    EmailRecipient rec = EmailRecipientManager.recipientsForRequestorAndHolder("requestor@university.edu", holderWithoutAlternate());
    assertEquals("requestor@university.edu",rec.recipients);
    assertEquals("holder@university.edu",rec.ccs);
  }
  
  @Test
  public void testHolderRequestorAndHolderWithoutAlternate(){
    EmailRecipient rec = EmailRecipientManager.recipientsForRequestorAndHolder("holder@university.edu", holderWithoutAlternate());
    assertEquals("holder@university.edu",rec.recipients);
    assertEquals("",rec.ccs);
  }
  
  @Test
  public void testHolderWithAlternate() {
    EmailRecipient rec = EmailRecipientManager.recipientsForHolder(holderWithAlternate());
    assertEquals("holder-assistant@university.edu",rec.recipients);
    assertEquals("holder@university.edu",rec.ccs);
  }
  
  @Test
  public void testHolderWithoutAlternate() {
    EmailRecipient rec = EmailRecipientManager.recipientsForHolder(holderWithoutAlternate());
    assertEquals("holder@university.edu",rec.recipients);
    assertEquals("",rec.ccs);
  }
  
}