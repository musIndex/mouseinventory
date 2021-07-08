var _delay = 500;
var _count = 0;

//var _pmDBurl = "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&list_uids=";
//var _pmDBurlTail = "&dopt=Abstract";
var _pmDBurl = "http://www.ncbi.nlm.nih.gov/pubmed/";
var _pmDBurlTail = "?dopt=Abstract";

var _mgiDBurl = "http://www.informatics.jax.org/accession/MGI:";
var _mgiDBurlTail = "";

var _jaxUrl = "http://jaxmice.jax.org/strain/";
var _jaxUrlTail = ".html";

var _gensatUrl = "http://www.gensat.org/ShowFounderLineImages.jsp?gensatFounderLine=";
var _gensatUrlTail = "";

var _rgdDBurl = "https://rgd.mcw.edu/rgdweb/report/strain/main.html?id=";
var _rgdDBurlTail = "";

var _validateUrl = "/validate";

//start a timer every time this is called.  if more than <delay> ms elapses without this function being called again, the timer fires and we run the validation request
function validateInput(inputFieldId, resultFieldId, inputFieldType, allowedValueString)
{
  _count = _count+1;
  setTimeout("validateInputGo("+_count+",'"+inputFieldId+"','" + resultFieldId+"','" + inputFieldType + "', '" + allowedValueString+"', '" + _validateUrl + "')",_delay);
}

//form the url and send the validation request to the validation servlet
function validateInputGo(currCount, inputFieldId, resultFieldId, inputFieldType, allowedValueString, validationUrl)
{
  if(currCount == _count)
  {
    _count = 0;
    var inputString = document.getElementById(inputFieldId).value;
    inputString = trim(inputString);
    var url = validationUrl + "?fieldType=" + inputFieldType + "&resultFieldId=" + resultFieldId + "&inputFieldId=" + inputFieldId +"&inputString=" + encodeURIComponent(inputString) + "&allowedValues=" + encodeURIComponent(allowedValueString);
    var ajax = new AJAXInteraction(url, validateInputCallback);
    var resultSpan = document.getElementById(resultFieldId);
    resultSpan.innerHTML = "Validating '" + inputString + "'...";
    ajax.doGet();
  }
}

//called when the servlet sends a response
//parse the xml, and update the appropriate validation result field
function validateInputCallback(responseXML) {
  var fieldType = responseXML.getElementsByTagName("fieldType")[0].childNodes[0].nodeValue;
  var resultFieldId = responseXML.getElementsByTagName("resultFieldId")[0].childNodes[0].nodeValue;
  var inputFieldId = responseXML.getElementsByTagName("inputFieldId")[0].childNodes[0].nodeValue;

  var inputValue = "";
  try //input might be an empty string
  {
    inputValue = decodeXML(responseXML.getElementsByTagName("inputValue")[0].childNodes[0].nodeValue);
  }
  catch(Exception)
  {
    inputValue = "";
  }
  var currentInputValue = trim(document.getElementById(inputFieldId).value);

  if(currentInputValue != inputValue)
  {
    //this may happen if a older request took longer than a more recent one
    return;
  }

  var valid = responseXML.getElementsByTagName("valid")[0].childNodes[0].nodeValue;
  var showUrl = responseXML.getElementsByTagName("showUrl")[0].childNodes[0].nodeValue;
  var resultString = "";
  if (responseXML.getElementsByTagName("outputValue")[0].childNodes[0])
  {
    resultString = responseXML.getElementsByTagName("outputValue")[0].childNodes[0].nodeValue;
  }
  resultString = replaceBrackets(decodeXML(resultString));
  resultString = replaceNewlines(resultString);
  var resultSpan = document.getElementById(resultFieldId);
  var url = "";

  var linkText = "";

  if (fieldType == "mgiAlleleId" || fieldType == "mgiTransgeneId" || fieldType == "mgiModifiedGeneId" || fieldType == "mgiKnockedInGeneId" || fieldType == "mgiExpressedGeneId")
  {
    url = _mgiDBurl + inputValue + _mgiDBurlTail;
    linkText = "" + "MGI:" + inputValue + "";
    updateHiddenInputs(resultString, valid, inputFieldId);

  }
  else if (fieldType == "rgdModifiedGeneId"){
    url = _rgdDBurl + inputValue + _rgdDBurlTail;
    linkText = "" + "RGD:" + inputValue + "";
    updateHiddenInputs(resultString, valid, inputFieldId);
  }
  else if (fieldType == "rgdTransgeneId"){
    url = _rgdDBurl + inputValue + _rgdDBurlTail;
    linkText = "" + "RGD:" + inputValue + "";
    updateHiddenInputs(resultString, valid, inputFieldId);
  }
  else if (fieldType == "pmId")
  {
    url = _pmDBurl + inputValue + _pmDBurlTail;
    linkText = "" + "Pubmed:" + inputValue + "";

    //hack for mice with no MGI allele/transgene page - the pubmed id isn't in MGI so just generate the pubmed link
    //if(document.getElementById("mouseMGIID") != null && document.getElementById("mouseMGIID").value == "none")
    //{
    //  resultString = "OK";
    //  valid = "true";
    //}
    updateHiddenInputs(resultString, valid, inputFieldId);
  } else if (fieldType == "jaxID")
  {
    var ISSupplier = document.getElementById("ISSupplier");
    var otherUrl = document.getElementById("ISSupplierCatalogUrl");
    if((new String(ISSupplier.value).toLowerCase()== "jax mice"))
    {
      url = _jaxUrl + inputValue + _jaxUrlTail;
      linkText = "" + "JAX " + inputValue + "";
    }
    else if((new String(otherUrl.value)!= null))
    {
      url = otherUrl.value;
      linkText = "" + ISSupplier.value + " " + inputValue + "";
    }
  } else if (fieldType == "gensat")
  {
    url = _gensatUrl + inputValue + _gensatUrlTail;
    linkText = "GENSAT:" + inputValue + "";
  }


  if (valid != "true") {
    resultSpan.className = "bp_invalid";
  } else {
    resultSpan.className = "bp_valid";
  }

  if(showUrl == "true" && url != null && url != "")
  {
    resultString += " <a class=\"MP\" target=\"_blank\" href=" + url + ">" + linkText + "</a>";
  }

  resultSpan.innerHTML = resultString;

  try
  {
    var symbol = responseXML.getElementsByTagName("symbol")[0].childNodes[0].nodeValue;
    symbol = decodeXML(symbol);

    if(symbol != null && symbol != "null")
    {
      var symbolField = document.getElementById("officialSymbol");
      if(symbolField==null) //hack for old admin pages
        symbolField = document.getElementById("source");
      symbolField.value = symbol;
      symbolField = document.getElementById("officialSymbolSpan");
      if(symbolField!=null)
        symbolField.innerHTML = "<b>" + replaceBrackets(symbol) + "</b>";
    }

    if (fieldType == "rgdTransgeneId"){
      var responseXMLSymbol = responseXML.getElementsByTagName("symbol")[0];
      var responseXMLSymbolChild = responseXMLSymbol.childNodes[0];
      var responseXMLSymbolChildValue = responseXMLSymbolChild.value;
      symbol = decodeXML(symbol);
    }
  }
  catch(Exception)
  {}


}

function updateHiddenInputs(resultString, valid, inputFieldId)
{
    var validationStringInput = document.getElementById(inputFieldId + "ValidationString");
    var validInput = document.getElementById(inputFieldId + "Valid");
    if(validationStringInput != null)
      validationStringInput.value = resultString;

    if(validInput != null)
      validInput.value = valid;
}


function trim(str, chars) {
  return ltrim(rtrim(str, chars), chars);
}

function ltrim(str, chars) {
  chars = chars || "\\s";
  return str.replace(new RegExp("^[" + chars + "]+", "g"), "");
}

function rtrim(str, chars) {
  chars = chars || "\\s";
  return str.replace(new RegExp("[" + chars + "]+$", "g"), "");
}

function decodeXML(string) {
    return string.replace(/\&amp;/g,'&').replace(/\&lt;/g,'<')
        .replace(/\&gt;/g,'>').replace(/\&apos;/g,'\'').replace(/\&quot;/g,'\"');
}

function encodeXML(string) {
  return string.replace(/\&/g,'&'+'amp;').replace(/</g,'&'+'lt;')
    .replace(/>/g,'&'+'gt;').replace(/\'/g,'&'+'apos;').replace(/\"/g,'&'+'quot;');
}

function replaceBrackets(s)
{
    if (s != null) {
        s = s.replace("<", "<sup");
        s = s.replace(">", "</sup>");
        s = s.replace("<sup", "<sup>");
    }
    return s;

}

function replaceNewlines(s)
{
  if (s != null) {
        s = s.replace("\\r\\n", "<br>");
    }
    return s;
}

//***************************
//Disable enter key from submitting form
//by Volker Schubert (js@volker-schubert.de) @ the javascript source http://javascript.internet.com
function checkCR(evt) {

    var evt  = (evt) ? evt : ((event) ? event : null);

    var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);

    if ((evt.keyCode == 13) && (node.type=="text")) {return false;}

    return true;
  }

function imposeMaxLength(field, MaxLen)
{
  //blah bla
  if (field.value.length > MaxLen) // if too long...trim it!
    field.value = field.value.substring(0, MaxLen);
}



//***************************
