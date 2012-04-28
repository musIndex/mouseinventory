package edu.ucsf.mousedatabase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.FileItem;

import com.csvreader.CsvReader;


public class HTMLUtilities {

  public static String p(String paragraphText)
  {
    return "<p>" + paragraphText + "</p>";
  }  
  
  public static String table(String tableText)
  {
    return "<table>" + tableText + "</table>";
  }
  
  public static String tr(String tableText)
  {
    return "<tr>" + tableText + "</tr>";
  }
  
  public static String td(String tableText)
  {
    return "<td>" + tableText + "</td>";
  }
      
  public static void append(StringBuilder sb, Object o)
  {
    sb.append(o.toString());
  }
  
  public static void appendline(StringBuilder sb, Object o)
  {
    sb.append(o.toString());
    sb.append("\r\n");
  }
  
  public static String extractFirstGroup(String regexPattern, String searchString)
  {  
    if(searchString == null)
    {
      return null;
    }
    Pattern ptn = Pattern.compile(regexPattern);
    Matcher match = ptn.matcher(searchString);

    String result = null;  
    if (match.find()) 
    {
      if (match.groupCount() > 0) {
        result = match.group(1);
      }
    }
    return result;
  }
  
  public static String getCommentForDisplay(String generalComment)
  {
    if (generalComment == null)
    {
      return null;
    }
    String fixed = generalComment.replaceAll("<", "&lt;");
    fixed = fixed.replaceAll(">", "&gt;");
    fixed = fixed.replaceAll("\r\n", "<br>");
    fixed = fixed.replaceAll("\n", "<br>");
    fixed = fixed.replaceAll("\\[B](.*)\\[/B\\]","<b>$1</b>");
    fixed = fixed.replaceAll("\\[URL\\](.*)\\[/URL\\]","<a href='$1'>$1</a>");
    return fixed;
  }
  
  public static String getHttpResponse(URL url, String expectedContentType) throws Exception
  {
    URLConnection conn = url.openConnection();
    // List all the response headers from the server.
      // Note: The first call to getHeaderFieldKey() will implicit send
      // the HTTP request to the server.
      for (int i=0; ; i++) {
          String headerName = conn.getHeaderFieldKey(i);
          String headerValue = conn.getHeaderField(i);

          if (headerName == null && headerValue == null) {
              // No more headers
              break;
          }
          if (headerName == null) {
              if (headerValue.equals("HTTP/1.1 404 Not Found"))
              {
                throw new Exception(url.toString() + " returned a 404 error");
              }
          }  
      }
      
      String contentType = conn.getContentType();
      
      if (expectedContentType != null && !contentType.equals(expectedContentType))
      {
        throw new Exception("received invalid content type: " + contentType + ", expected: " + expectedContentType);
      }
      
      StringBuilder content = new StringBuilder();
      
      BufferedReader in = new BufferedReader(
                new InputStreamReader(
                conn.getInputStream()));
    String inputLine;
    
    while ((inputLine = in.readLine()) != null) 
    {
      content.append(inputLine + "\n");
    }
    in.close();
      
      
      return content.toString();
  }
  
  public static ArrayList<HashMap<String,String>> readCSVFileFromStream(FileItem data, Charset encoding) throws Exception
  {
    ArrayList<HashMap<String,String>> csvData = new ArrayList<HashMap<String,String>>();
    

      InputStream uploadedStream = data.getInputStream();
      CsvReader reader = new CsvReader(uploadedStream,encoding);
      
      if(!reader.readHeaders())
      {
        throw new Exception("No headers found in input file " + data.getName());
      }
      ArrayList<String> headers = new ArrayList<String>();
      for(int i =0; i < reader.getHeaderCount();i++)
      {
        headers.add(i, reader.getHeader(i));
      }
      
      while(reader.readRecord())
      {
        HashMap<String,String> record = new HashMap<String,String>();
        for(String header : headers)
        {
          record.put(header.toLowerCase(), reader.get(header));
        }
        if (isBlankRow(record)) continue;
        csvData.add(record);
      }
        uploadedStream.close();


    Log.Info("Read input file: " + data.getName() + ", " + data.getSize() + "bytes (" + csvData.size() + " records)");
    
    return csvData;
  }
  
  private static boolean isBlankRow(HashMap<String,String> row)
  {
    for (String value : row.values())
    {
      if (value != null && !value.trim().equals("")) return false;      
    }
    return true;
  }
}
