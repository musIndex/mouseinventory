package edu.ucsf.mousedatabase;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.csvreader.CsvReader;

public class MmrrcConnect 
{
  private static final String CsvDataUrl = "http://www.mmrrc.org/about/mmrrc_catalog_data.csv";
  
  private static final String StrainStockIdCol = "STRAIN/STOCK_ID";
  //private static final String StrainStockDesignationCol = "STRAIN/STOCK_DESIGNATION";
  private static final String MgiAlleleCol = "MGI_ALLELE_ACCESSION_ID";
  
  private static final String MgiAlleleRegex = "([\\d]+)";
  
  //TODO download CSV data, return an object that contains strains mapped to MGI IDs
  /*
   * first lines of files
   * 
This MMRRC catalog data file was generated on 04-JUL-11

CONDITIONS OF USE
"MGI data are incorporated in these files; MGI software and data are copyrighted and use for commercial purpose is prohibited without the prior express written permission of the Jackson Laboratory."
"We recommend that data be refreshed frequently (at least monthly, as new strains are added each month), and the date last updated should be included with any public display of the data provided here."
"Proper attribution to the MMRRC and MGI programs should be included in any public display of the data provided here."

WARRANTY DISCLAIMER AND COPYRIGHT NOTICE
"THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES, EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS. THE SOFTWARE AND DATA ARE PROVIDED ""AS IS""."

STRAIN/STOCK_ID,STRAIN/STOCK_DESIGNATION,STRAIN_TYPE,STATE,MGI_ALLELE_ACCESSION_ID,ALLELE_SYMBOL,ALLELE_NAME,MUTATION_TYPE,CHROMOSOME,MGI_GENE_ACCESSION_ID,GENE_SYMBOL,GENE_NAME,SDS_URL,MPT_IDS
"000001-UNC","C57BL/6-Tg(Fga,Fgb,Fgg)1Unc/Mmnc","MSR","EM,SP","","","","TM","","","","","http://www.mmrrc.org/strains/1/0001.html","49821 49822 50047 55090 83358 83359 93925 2775046 4654427 5990334 5990336"

   * 
   * 
   */
  
  public HashMap<String,ArrayList<Integer>> data;
  
  public boolean isAvailable()
  {
    return data != null;
  }
  
  public MmrrcConnect()
  {
    try
    {
      data = downloadData();
    }
    catch (Exception e)
    {
      Log.Error("Unable to retreive MMRRC data for MGI lookup",e);
    }
    
  }
  
  public ArrayList<Integer> lookupStrain(String strain)
  {
    if (data == null)
    {
      return null;
    }
    
    if (data.containsKey(strain))
    {
      return data.get(strain);
    }
    Log.Error("No strain '" + strain + "' in MMRRC data");
    return null;
    
  }
  
  
  private HashMap<String,ArrayList<Integer>> downloadData() throws Exception
  {
    Log.Info("Downloading data from MMRRC: " + CsvDataUrl);
    HashMap<String,ArrayList<Integer>> strainMgiMap = new HashMap<String, ArrayList<Integer>>();
    
    String data = HTMLUtilities.getHttpResponse(new URL(CsvDataUrl),null);
    Log.Info("Download done, getting length");
    Log.Info("Downloaded " + data.length() + " bytes");
    
    int dataStart = data.indexOf(StrainStockIdCol);
    
    Log.Info("dataStart: " + dataStart);
    Log.Info("data starts with: " + data.substring(dataStart, dataStart+100));
    
    CsvReader reader = CsvReader.parse(data.substring(dataStart));
    
    if(!reader.readHeaders())
    {
      throw new Exception("Failed to read headers from MMRRC data, aborting");
    }
    
    String[] headers = reader.getHeaders();
    Log.Info("found headers: " + headers.length);
    for(String header : headers)
    {
      Log.Info(header);
    }
    
    while(reader.readRecord())
    {
      String mgi = reader.get(MgiAlleleCol);
      if (mgi == null || mgi.isEmpty())
      {
        continue;
      }
      String extracted = HTMLUtilities.extractFirstGroup(MgiAlleleRegex, mgi);
      
      String catalogNumber = reader.get(StrainStockIdCol);
      
      ArrayList<Integer> mgiIds = strainMgiMap.get(catalogNumber);
      if (mgiIds == null)
        mgiIds = new ArrayList<Integer>();
      
      mgiIds.add(Integer.parseInt(extracted));
      
      strainMgiMap.put(catalogNumber,mgiIds);
      
    }
    
    
    
    return strainMgiMap;
  }
  
  
  

  //test method
  
  
  
  
}
