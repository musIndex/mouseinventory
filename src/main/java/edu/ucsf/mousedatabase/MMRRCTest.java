package edu.ucsf.mousedatabase;

public class MMRRCTest {
  public static void main(String args[])
  {
    Log.Initialize();
      
    
    MmrrcConnect mmrrc = new MmrrcConnect();
    
    if(!mmrrc.isAvailable())
    {
      System.out.println("MMRRC is unavailable, exiting.");
    }
    
    String[] strains = new String[]
        {
        "000001-UNC",
        "000024-MU"
        };
    
    for(String strain : strains)
    {
      System.out.println("MGI ID for strain '" + strain + "': " + mmrrc.lookupStrain(strain));
    }
      
  }
}
