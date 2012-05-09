package edu.ucsf.mousedatabase.objects;

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
  
  public int getQualityValue(){
    return this.quality;
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

  public void setComment(String comment) {
    this.comment = comment;
  }

  public void setQuality(int quality) {
    this.quality = quality;
  }
  
  
 
  
}