package edu.ucsf.mousedatabase.servlets;



import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.ucsf.mousedatabase.HTMLGeneration;
import edu.ucsf.mousedatabase.HTMLUtilities;
import edu.ucsf.mousedatabase.MGIConnect;
import edu.ucsf.mousedatabase.RGDConnect;
import edu.ucsf.mousedatabase.objects.MGIResult;
import edu.ucsf.mousedatabase.objects.RGDResult;

import java.util.*;

public class ValidationServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  //private static final String pmDBurl = "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&list_uids=";




  //private ServletContext context;


    public void init(ServletConfig config) throws ServletException {
      super.init(config);
        //this.context = config.getServletContext();

    }



    //output xml should look like this
    //<validationResults>
    //<fieldType/>
    //<resultFieldId/>
    //<inputValue/>
    //<outputValue/>
    //<showUrl/>
    //<valid/>
    //</validationResults>

    public  void doGet(HttpServletRequest request, HttpServletResponse  response)
        throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        String fieldType = request.getParameter("fieldType");
        String resultFieldId = request.getParameter("resultFieldId");
        String inputFieldId = request.getParameter("inputFieldId");
        String inputString = request.getParameter("inputString");
        String allowedValues = request.getParameter("allowedValues");

        ArrayList<String> childNodes = new ArrayList<String>();

        childNodes.add("<fieldType>" + fieldType + "</fieldType>");
        childNodes.add("<resultFieldId>" + resultFieldId + "</resultFieldId>");
        childNodes.add("<inputValue>" + addXMLEscapes(inputString) + "</inputValue>");
        childNodes.add("<inputFieldId>" + inputFieldId + "</inputFieldId>");

        ValidationResult result = null;
        if(fieldType.equals("pmId"))
        {
          result = doPubmedValidation(inputString, allowedValues);
        }
        else if (fieldType.equals("mgiAlleleId"))
        {
          result = doMGIAlleleIDValidation(inputString, allowedValues);
        }
        else if (fieldType.equals("mgiTransgeneId"))
        {
          result = doMGITransgeneIDValidation(inputString, allowedValues);
        }
        else if (fieldType.equals("mgiModifiedGeneId"))
        {
          result = doMGIGeneIDValidation(inputString, allowedValues,"modifiedgene");
        }
        else if (fieldType.equals("mgiExpressedGeneId"))
        {
          result = doMGIGeneIDValidation(inputString, allowedValues,"expressedgene");
        }
        else if (fieldType.equals("mgiKnockedInGeneId"))
        {
          result = doMGIGeneIDValidation(inputString, allowedValues,"knockedingene");
        }
        else if (fieldType.equals("email"))
        {
          result = checkAllowedValues(inputString, allowedValues, "Email Address");
        }
        else if (fieldType.equals("jaxID"))
        {
          result = doJaxIDValidation(inputString, allowedValues);
        }
        else  if (fieldType.equals("gensat"))
        {
          result = doGensatValidation(inputString, allowedValues);
        }
        else if (fieldType.equals("rgdTransgeneId")) {
            result = doRGDGeneValidation(inputString, allowedValues, "rgdTransgeneId");
        }
        else if (fieldType.equals("rgdModifiedGeneId")){
            result = doRGDGeneValidation(inputString, allowedValues, "rgdTransgeneId");

        }
        else if (fieldType.equals("rgdExpressedGeneId")){
            result = doRGDGeneValidation(inputString, allowedValues, "rgdTransgeneId");
        }
        else
        {
            String type = fieldType;
            result = new ValidationResult("Unknown fieldType error", false, false);
        }


        childNodes.add("<outputValue>" + addXMLEscapes(result.outputValue) + "</outputValue>");
        childNodes.add("<valid>" + result.valid + "</valid>");
        childNodes.add("<showUrl>" + result.showUrl + "</showUrl>");
        if(result.additionalNodes!= null && !result.additionalNodes.isEmpty())
          childNodes.add(result.additionalNodes);

      response.setContentType("text/xml");
        response.setHeader("Cache-Control", "no-cache");
        response.getWriter().write("<validationResults>\r\n");

      for(String childNode : childNodes)
      {
        response.getWriter().write(childNode + "\r\n");
      }
        response.getWriter().write("</validationResults>");
    }

    private ValidationResult doGensatValidation(String inputString,
      String allowedValues) {
      ValidationResult result = checkAllowedValues(inputString, allowedValues, "gensat");
      if(result != null)
      {
        return result;
      }
      if(inputString == null || inputString.equalsIgnoreCase(""))
      {
        return new ValidationResult("OK", true, false);
      }
      return new ValidationResult("OK", true, true);
  }



  private ValidationResult doJaxIDValidation(String inputString, String allowedValues) {

      ValidationResult result = checkAllowedValues(inputString, allowedValues, "JaxID");
      if(result != null)
      {
        return result;
      }

      if(inputString == null || inputString.isEmpty())
      {
        return new ValidationResult(" ", true, true);
      }

//      try
//      {
//        Integer.parseInt(inputString);
//      }catch(Exception e)
//      {
//        return new ValidationResult("Please enter only numbers (no spaces)", false, false);
//      }


      return new ValidationResult(" ", true, true);
  }

  private ValidationResult doPubmedValidation(String accessionID, String allowedValues)
    {
      ValidationResult result = checkAllowedValues(accessionID, allowedValues, "Pubmed ID");
      if(result != null)
      {
        return result;
      }
      MGIResult r = MGIConnect.DoReferenceQuery(accessionID);

      if(r.isValid())
      {
        if(r.getType() == MGIConnect.MGI_MARKER || r.getType() == MGIConnect.MGI_ALLELE)
        {
          result = new ValidationResult(r.getSymbol() + " - " + r.getName(), true, false);
          if(r.getSymbol() != null)
          {
            result.additionalNodes = "<symbol>" + addXMLEscapes(r.getSymbol()) + "</symbol>";
          }
        }
        else if (r.getType() == MGIConnect.MGI_REFERENCE)
        {
          result = new ValidationResult(r.getTitle() + " - " + r.getAuthors(),true, true);
        }
      }
      else
      {
        result = new ValidationResult(r.getErrorString(), false, true);
      }

      return result;
    }

  private ValidationResult doMGIAlleleIDValidation(String idString, String allowedValues)
  {
    return doMGIMouseIDValidation(idString, allowedValues, "allele");
  }

  private ValidationResult doMGITransgeneIDValidation(String idString, String allowedValues)
  {
    return doMGIMouseIDValidation(idString, allowedValues, "transgene");
  }

    private ValidationResult doMGIMouseIDValidation(String idString, String allowedValues, String type)
    {
      ValidationResult result = checkAllowedValues(idString, allowedValues, "MGI ID");
      if(result != null)
      {
        result.additionalNodes = "<symbol> </symbol>";
        return result;
      }
      MGIResult r = null;
      if(type.equalsIgnoreCase("allele"))
      {
        r = MGIConnect.DoMGIAlleleQuery(idString);
      }
      else if(type.equalsIgnoreCase("transgene"))
      {
        r = MGIConnect.DoMGITransgeneQuery(idString);
      }
      if(r== null)
      {
        return null;
      }

      if(r.isValid())
      {
        result = new ValidationResult(r.getSymbol() + " - " + r.getName(), true, true);
        if(r.getSymbol() != null)
        {
          result.additionalNodes = "<symbol>" + addXMLEscapes(r.getSymbol()) + "</symbol>";
        }
      }
      else
      {
        result = new ValidationResult(r.getErrorString(), false, true);
        result.additionalNodes = "<symbol> </symbol>";
      }


      return result;
    }

  private ValidationResult doMGIGeneIDValidation(String idString, String allowedValues, String type)
    {

      ValidationResult result = checkAllowedValues(idString, allowedValues, "MGI ID");
      if(result != null)
      {
        return result;
      }

      MGIResult r = null;

      if(type.equalsIgnoreCase("modifiedGene"))
      {
        r = MGIConnect.DoMGIModifiedGeneQuery(idString);
      }else if(type.equalsIgnoreCase("knockedingene"))
      {
        r = MGIConnect.DoMGIKnockedinGeneQuery(idString);
      }else if(type.equalsIgnoreCase("expressedgene"))
      {
        r = MGIConnect.DoMGIExpressedGeneQuery(idString);
      }

      if(r.isValid())
      {
        result = new ValidationResult(r.getSymbol() + " - " + r.getName(), true, true);
      }
      else
      {
        result = new ValidationResult(r.getErrorString(), false, true);
      }

      return result;
    }

    private ValidationResult doRGDGeneValidation(String idString, String allowedValues, String type) {
        ValidationResult result = checkAllowedValues(idString, allowedValues, type);
        if (result != null) {
            return result;
        }

        RGDResult r = null;

        if(type.equalsIgnoreCase("rgdTransgeneId")) {
            r = RGDConnect.getGeneQuery(idString);
        }

        if (r.isValid()) {
            result = new ValidationResult(r.getSymbol() + " - " + r.getName(), true, true);
        }
        else {
            result = new ValidationResult(r.getErrorString(), false, true);
        }

        return result;
    }

    //an allowed values string of '' means no allowed values.  ',' means blank is allowed
    private ValidationResult checkAllowedValues(String inputString, String allowedValues, String fieldTypeDescription)
    {
      ValidationResult result = null;

      String[] allowedVals = allowedValues.split(",");
      String prettyAllowedValString = "";
      boolean allowBlank = false;

      for (String allowedVal : allowedVals)
      {
        if(inputString.equalsIgnoreCase(allowedVal))
        {
          result = new ValidationResult("OK", true, false);
        }
        if(allowedVal.equals("") && allowedVals.length > 1)
        {
          allowBlank = true;
              prettyAllowedValString += " or leave this field blank";

        }
        else  {
          if (!allowedVal.equals(""))  //handle case when there are no allowed values
          {
            prettyAllowedValString += " or '" + allowedVal + "'";
          }
        }

      }
      if(allowedVals.length == 0 && allowedValues.equals(","))
        allowBlank = true;
      //check if it's blank and if we're allowing blanks
      if(allowBlank && inputString.equals(""))
      {
        return new ValidationResult("",true,false);
      }

    if(!allowBlank && inputString.equalsIgnoreCase(""))
    {
      if(fieldTypeDescription.equalsIgnoreCase("jaxid"))
        fieldTypeDescription = " the catalog ID (number only)";
        return new ValidationResult("Please enter " + fieldTypeDescription + prettyAllowedValString, false, false);
      }
    //it wasn't blank and it is an allowed value
    if(result != null)
    {
      return result;
    }

    if (fieldTypeDescription.equals("Pubmed ID") || fieldTypeDescription.equals("MGI ID") || fieldTypeDescription.equals("rgdTransgeneId"))
    {

        try
        {
          Long.parseLong(inputString);
        }
        catch (NumberFormatException e)
        {
          return new ValidationResult("Please enter only numbers (no spaces)" + prettyAllowedValString, false, false);
        }
    }
    else if (fieldTypeDescription.equals("Email Address"))
    {
        if(HTMLUtilities.validateEmail(inputString))
        {
          return new ValidationResult("OK", true, false);
        }
        return new ValidationResult("Invalid Address", false, false);
    }

    //Jax ID rules (currently none)
    //gensat url rules (currently none)

      return null;
    }

    private String addXMLEscapes(String input)
    {
      if(input == null)
      {
        return input;
      }
      String output;
      output = input.replace("<", "&lt;");
      output = output.replace(">", "&gt;");
      output = output.replace("&", "&amp;");
      output = output.replace("\"", "&quot;");
      output = output.replace("'", "&apos;");
      return output;
    }



    private class ValidationResult
    {
      public String outputValue;
      public boolean valid;
      public boolean showUrl;
      public String additionalNodes;

      public ValidationResult(String outputValue, boolean valid, boolean showUrl)
      {
        this.outputValue = outputValue;
        this.showUrl = showUrl;
        this.valid = valid;

      }


    }
}


