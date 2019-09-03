package edu.ucsf.mousedatabase;


import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class JaxMiceConnect {

  //URL changed for jax, example https://www.jax.org/strain/007676 -EW
  //private static final String baseUrl = "http://jaxmice.jax.org/strain/"; -EW turned off
  private static final String baseUrl = "http://jaxmice.jax.org/strain/";
  private static final String urlTail = ".html";
  //alleleRegex won't work, Alele Symbol: <a id="alleleid_54647_symbol" href="http://www.informatics.jax.org/accession/MGI:3716464"
  private static final String alleleRegex = "Allele&nbsp;Symbol[^\\n]*\\n[^\\n]*MGI:([\\d]+)";
  private static final String stockNumberRegex = "([\\d]{6})";


//  <tr><th colspan="2">Allele&nbsp;Symbol</th><td colspan="2">
//  <a href="http://www.informatics.jax.org/accession/MGI:2664398" target="_top">Tg(DO11.10)10Dlo</a></td></tr>

  public static ArrayList<Integer> GetMGINumbersFromJax(String jaxStockNumber)
  {
    ArrayList<Integer> mgiNumbers = new ArrayList<Integer>();

    String extractedNumber = HTMLUtilities.extractFirstGroup(stockNumberRegex, jaxStockNumber);
    if (extractedNumber == null)
    {
      Log.Error("No JAX stock number in string " + jaxStockNumber);
      return mgiNumbers;
    }
    Log.Info("Extracted JAX stock number '" + extractedNumber + "' from string " + jaxStockNumber);

    String strainData = getStrainData(extractedNumber);
    if (strainData == null)
    {
      return mgiNumbers;
    }



    Pattern ptn = Pattern.compile(alleleRegex);
    Matcher match = ptn.matcher(strainData);

    while (match.find())
    {
      if (match.groupCount() <= 0)
      {
        continue;
      }
      String possibleMgiNumber = match.group(1);
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

    return mgiNumbers;

  }


  private static String getStrainData(String stockNumber)
  {
    try
    {
      String httpResponse = HTMLUtilities.getHttpResponse(new URL(baseUrl + stockNumber + urlTail),"text/html");
      return httpResponse;
    }
    catch (Exception e) {
      Log.Error("Failed to get MGI ID for for stock number " + stockNumber + ": " + e.getMessage());
      return null;
    }

  }




}
