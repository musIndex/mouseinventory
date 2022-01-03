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

  //URL changed for jax, example https://www.jax.org/strain/007676 -EW
  //private static final String baseUrl = "http://jaxmice.jax.org/strain/"; -EW turned off
  private static final String baseUrl = "https://www.jax.org/strain/";
  private static final String baseJson = "https://3ddatasheet-services-prod.azurewebsites.net/Data/GetComponentData?stockNumber=";
  private static final String tailJson = "&componentType=HowItsMade";
  //private static final String urlTail = ".html"; -EW not used in new stock URL
  //alleleRegex won't work, Alele Symbol: <a id="alleleid_54647_symbol" href="http://www.informatics.jax.org/accession/MGI:3716464"
  //private static final String alleleRegex = "Allele&nbsp;Symbol[^\\n]*\\n[^\\n]*MGI:([\\d]+)";
  private static final String alleleRegex ="[href*=/allele/MGI:(\\d{7})]";
  //private static final String alleleRegex ="[href*=/accession/MGI:(\\d{7})]";
  private static final String stockNumberRegex = "([\\d]{4,6})";
  


//  <tr><th colspan="2">Allele&nbsp;Symbol</th><td colspan="2">
//  <a href="http://www.informatics.jax.org/accession/MGI:2664398" target="_top">Tg(DO11.10)10Dlo</a></td></tr>

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
/*
    String strainData = getStrainData(extractedNumber);
    if (strainData == null)
    {
      return mgiNumbers;
    }
    */
    //String urlConnect = baseUrl + extractedNumber;
    
    String urlConnect = "https://www.jax.org/strain/" + extractedNumber;
    Log.Info(urlConnect);
    String jsonConnect = baseJson + extractedNumber + tailJson;
    
    //Document document = Jsoup.connect(urlConnect).timeout(30000).get();        
    String data = Jsoup.connect(jsonConnect).ignoreContentType(true).execute().body();  
    Log.Info("Json Connect: " + data);
    
    //Elements links = document.select("a");
     //Log.Info("Links test = " + links);
     
        //Pattern expression = Pattern.compile("MGI:(\\d{7})");//will also find gene ID if 7 digits long
        //Matcher matcher = expression.matcher(links.toString());
        
        //If loop will find 1st occurance and ignore gene MGI ID number
    
    Gson gson = new Gson();
    Map<String, Object> asMap = gson.fromJson(data, Map.class);
    List<Map<String, Object>> geneticInfos = (List) asMap.get("geneticInfo");
    for (Map<String, Object> geneticInfo : geneticInfos) {
        // check that the response contains mgiAccessionID
        if (geneticInfo.containsKey("mgiAccessionID")) 
        {
            String mgiAccessionID = (String) geneticInfo.get("mgiAccessionID");
            if (mgiAccessionID != null) {
                Pattern expression = Pattern.compile("MGI:(\\d{7})");
                Matcher matcher = expression.matcher(mgiAccessionID);
                if (matcher.find()){
                    //This Prints out MGI:and number use 1 to print out the 1st capturing group
                    System.out.println(matcher.group(1));
                    String possibleMgiNumber =matcher.group(1);
                    
                
                    try
                    {
                      int mgiNumber = Integer.parseInt(possibleMgiNumber);
                      Log.Info("Conversion from jax: " + extractedNumber + " to MGI: " + mgiNumber);
                      mgiNumbers.add(mgiNumber);
                    }
                    catch(NumberFormatException e)
                    {
                      Log.Error("Failed to convert " + possibleMgiNumber + " to an MGI number",e);
                    }
                    }
            }
        }
    }

    return mgiNumbers;

  }

/*-EW turn off method to parse html use jsoup method
  private static String getStrainData(String stockNumber)
  {
    try
    {
      //removed +urlTail, change exptected "text/html" to "charset=utf-8" -EW
      String httpResponse = HTMLUtilities.getHttpResponse(new URL(baseUrl + stockNumber ),"text/html; charset=utf-8");
      //String httpResponse = HTMLUtilities.getHttpResponse(new URL(baseUrl + stockNumber ));
      return httpResponse;
    }
    catch (Exception e) {
      Log.Error("Failed to get MGI ID for for stock number " + stockNumber + ": " + e.getMessage());
      return null;
    }

  }

*/


}
