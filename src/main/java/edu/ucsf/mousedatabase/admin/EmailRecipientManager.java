package edu.ucsf.mousedatabase.admin;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import edu.ucsf.mousedatabase.objects.IHolder;

public class EmailRecipientManager {
  
  
  public static class EmailRecipient {
    public String recipients;
    public String ccs;

  }
  
  private static String toLower(String str) {
    if (str == null) {
      return null;
    }
    return str.toLowerCase();
  }
  
  public static EmailRecipient recipientsForRequestorAndHolder(String requestor, IHolder holder) {
    
    EmailRecipient rec = new EmailRecipient();
    
    Set<String> recipients = new HashSet<String>();
    Set<String> ccs = new HashSet<String>(); 
    
    recipients.add(toLower(requestor));
    ccs.add(toLower(holder.getEmail()));
    if (holder.getAlternateEmail() != null && !holder.getAlternateEmail().isEmpty()) {
      ccs.add(toLower(holder.getAlternateEmail()));
    }
   
    ccs.removeAll(recipients);
    
    rec.recipients = StringUtils.join(recipients,", ");
    rec.ccs = StringUtils.join(ccs,", ");
    return rec;
  }
  
  public static EmailRecipient recipientsForHolder(IHolder holder) {
    EmailRecipient rec = new EmailRecipient();
    
    Set<String> recipients = new HashSet<String>();
    Set<String> ccs = new HashSet<String>(); 
    
    if (holder.getAlternateEmail() != null && !holder.getAlternateEmail().isEmpty()) {
      recipients.add(toLower(holder.getAlternateEmail()));
      ccs.add(toLower(holder.getEmail()));
    }
    else {
      recipients.add(toLower(holder.getEmail())); 
    }
      
    rec.recipients = StringUtils.join(recipients,", ");
    rec.ccs = StringUtils.join(ccs,", ");
    return rec;
  }
  
  
}