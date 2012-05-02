package edu.ucsf.mousedatabase.objects;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

public class SearchStrategy {
  private int quality;
  private String name;
  private String comment;
  
  
  public SearchStrategy(int quality, String name, String comment) {
    super();
    this.quality = quality;
    this.name = name;
    this.comment = comment;
  }

  public String getQuality() {
    if (quality < 2)
      return "good";
    
    if (quality < 5)
      return "ok";
    
    if (quality < 8)
      return "poor";
    
    return "bad";
  }

  public String getName() {
    return name;
  }

  public String getComment() {
    return comment;
  }
  
 
  
}