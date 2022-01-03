package edu.ucsf.mousedatabase;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
//Added Jsoup imports -EW
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

public class JaxMiceConnect {
  //private static final String baseUrl = "https://www.jax.org/strain/";
  private static final String baseJson = "https://3ddatasheet-services-prod.azurewebsites.net/Data/GetComponentData?stockNumber=";
  private static final String tailJson = "&componentType=HowItsMade";
  private static final String alleleRegex ="[href*=/allele/MGI:(\\d{7})]";
  private static final String stockNumberRegex = "([\\d]{4,6})";
  
  //Does not extract MGI ID for double or more mutations.
  public static ArrayList<Integer> GetMGINumbersFromJax(String jaxStockNumber) throws IOException
  {
    ArrayList<Integer> mgiNumbers = new ArrayList<Integer>();

    String extractedNumber = HTMLUtilities.extractFirstGroup(stockNumberRegex, jaxStockNumber);
    if (extractedNumber == null)
    {
      Log.Error("No JAX stock number in string " + jaxStockNumber);
      return mgiNumbers;
    }
    Log.Info("Extracted JAX stock number '" + extractedNumber + "' from string " + jaxStockNumber);

    String jsonConnect = baseJson + extractedNumber + tailJson;
         
    String data = Jsoup.connect(jsonConnect).ignoreContentType(true).execute().body();  
    Log.Info("Json Connect: " + data);
    
    Gson gson = new Gson();
    Map<String, Object> asMap = gson.fromJson(data, Map.class);
    List<Map<String, Object>> geneticInfos = (List) asMap.get("geneticInfo");
    for (Map<String, Object> geneticInfo : geneticInfos) {
        // check that the response contains mgiAccessionID
        if (geneticInfo.containsKey("mgiAccessionID")) 
        {
            String mgiAccessionID = (String) geneticInfo.get("mgiAccessionID");
            if (mgiAccessionID != null) {
                
                    try
                    {
                      int mgiNumber = Integer.parseInt(mgiAccessionID.replaceAll("[^0-9]", ""));
                      Log.Info("Conversion from jax: " + extractedNumber + " to MGI: " + mgiNumber);
                      mgiNumbers.add(mgiNumber);
                    }
                    catch(NumberFormatException e)
                    {
                      Log.Error("Failed to convert " + mgiAccessionID + " to an MGI number",e);
                    }
                    }
            }
        }
    
    return mgiNumbers;

  }


}
