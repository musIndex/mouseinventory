function UpdateExpressedSequenceDetail()
{
  len = document.mouseDetails.TGExpressedSequence.length;

  chosen = "none";

  for (i = 0; i < len; i++) {
    if (document.mouseDetails.TGExpressedSequence[i].checked) {
      chosen = document.mouseDetails.TGExpressedSequence[i].value;
    }
  }
  setElementVisibility("trGeneRow", "none");
  setElementVisibility("trRepRow", "none");
  setElementVisibility("trDescRow", "none");


  if(chosen == "Mouse Gene (unmodified)")
  {
    setElementVisibility("trGeneRow", "");
  }
  else if(chosen == "Reporter")
  {
    setElementVisibility("trRepRow", "");
  }
  else if(chosen == "Modified mouse gene or Other")
  {
    setElementVisibility("trDescRow", "");
  }

}
function UpdateExpressedSequenceEdit()
{
  len = document.mouseDetails.expressedSequence.length;

  chosen = "none";

  for (i = 0; i < len; i++) {
    if (document.mouseDetails.expressedSequence[i].checked) {
      chosen = document.mouseDetails.expressedSequence[i].value;
    }
  }
  setElementVisibility("trGeneRow", "none");
  setElementVisibility("trRepRow", "none");
  setElementVisibility("trDescRow", "none");

  clearFieldValue("targetGeneMGIID");
  clearFieldValue("reporterTextArea");
  clearFieldValue("otherCommentTextArea");

  if(chosen == "Mouse Gene (unmodified)")
  {
    setElementVisibility("trGeneRow", "");
  }
  else if(chosen == "Reporter")
  {
    setElementVisibility("trRepRow", "");
  }
  else if(chosen == "Modified mouse gene or Other")
  {
    setElementVisibility("trDescRow", "");
  }

}

function UpdateModificationType()
{
  len = document.mouseDetails.MAModificationType.length;

  chosen = "none";

  for (i = 0; i < len; i++) {
    if (document.mouseDetails.MAModificationType[i].checked) {
      chosen = document.mouseDetails.MAModificationType[i].value;
    }
  }
  setElementVisibility("trExprSeqRow", "none");

  setElementVisibility("trGeneRow", "none");
  setElementVisibility("trRepRow", "none");
  setElementVisibility("trDescRow", "none");

  if(chosen == "targeted knock-in"){
    setElementVisibility("trExprSeqRow","");
    UpdateExpressedSequenceDetail();

  }

}

function UpdateModificationTypeEdit()
{
  len = document.mouseDetails.modificationType.length;

  chosen = "none";

  for (i = 0; i < len; i++) {
    if (document.mouseDetails.modificationType[i].checked) {
      chosen = document.mouseDetails.modificationType[i].value;
    }
  }
  setElementVisibility("trExprSeqRow", "none");

  setElementVisibility("trGeneRow", "none");
  setElementVisibility("trRepRow", "none");
  setElementVisibility("trDescRow", "none");

  clearFieldValue("targetGeneMGIID");
  clearFieldValue("reporterTextArea");
  clearFieldValue("otherCommentTextArea");

  if(chosen == "targeted knock-in"){
    setElementVisibility("trExprSeqRow","");
    UpdateExpressedSequenceEdit();

  }

}

function clearHolder(id)
{
  SetFieldValue("holder_id-" + id, "1");
  SetFieldValue("facility_id-"+id, "1");
}

function clearFieldValue(elementName)
{
  SetFieldValue(elementName, "");
}

function SetFieldValue(elementName, value)
{
  $("#" + elementName).val(value).trigger("liszt:updated");
}

function UpdateTransgenicType()
{
  len = document.mouseDetails.TransgenicType.length;

  chosen = "none";

  for (i = 0; i < len; i++) {
    if (document.mouseDetails.TransgenicType[i].checked) {
      chosen = document.mouseDetails.TransgenicType[i].value;
    }
  }
  setElementVisibility("trKnockIn", "none");
  setElementVisibility("trRegEle", "none");


  if(chosen == "knock-in")
  {
    setElementVisibility("trKnockIn", "");
  }
  else if(chosen == "random insertion")
  {
    setElementVisibility("trRegEle", "");
  }


}

function UpdateSelectedMouseType()
{
  len = document.MouseTypeForm.mouseType.length;

  mouseType = "none";

  for (i = 0; i < len; i++) {
    if (document.MouseTypeForm.mouseType[i].checked) {
      mouseType = document.MouseTypeForm.mouseType[i].value;
    }
  }

  //len = document.MouseTypeForm.transgenicType.length;

  //transgenicType = "none";

  //for (i = 0; i < len; i++) {
  //  if (document.MouseTypeForm.transgenicType[i].checked) {
  //    transgenicType = document.MouseTypeForm.transgenicType[i].value;
  //  }
  //}

  len = document.MouseTypeForm.isPublished.length;

  isPublished = "none";

  for (i = 0; i < len; i++) {
    if (document.MouseTypeForm.isPublished[i].checked) {
      isPublished = document.MouseTypeForm.isPublished[i].value;
    }
  }

  if(mouseType == "Mutant Allele")
  {

    setElementVisibility("nextButton", "none");
    //setElementVisibility("transgenicTypes", "none");
    setElementVisibility("isPublishedSection", "");

    if(isPublished != "none")
    {
      setElementVisibility("nextButton", "block");
    }
  }
  else if (mouseType == "Transgene")
  {
    setElementVisibility("nextButton", "none");
    //setElementVisibility("transgenicTypes", "");
    setElementVisibility("isPublishedSection", "");

    //ADD red text label saying they have to choose one

    if(isPublished != "none")
    {
      setElementVisibility("nextButton", "block");
    }
  }
  else if (mouseType == "Inbred Strain")
  {
    setElementVisibility("nextButton", "block");
    //setElementVisibility("transgenicTypes", "none");
    setElementVisibility("isPublishedSection", "none");
  }

}

function UpdateSelectedMouseTypeOld() {

  len = document.MouseForm.MouseType.length;
  i = 0;
  chosen = "none";

  for (i = 0; i < len; i++) {
    if (document.MouseForm.MouseType[i].checked) {
      chosen = document.MouseForm.MouseType[i].value
    }
  }
  setElementVisibility("MutantAllele", "none");
  setElementVisibility("Transgene", "none");
  setElementVisibility("InbredStrain", "none");
  //setElementVisibility("OfficialSymbol", "none");


  if(chosen == "Mutant Allele")
  {
    setElementVisibility("MutantAllele", "block");
    //setElementVisibility("OfficialSymbol", "block");
  }
  else if(chosen == "Transgene")
  {
    setElementVisibility("Transgene", "block");
    //setElementVisibility("OfficialSymbol", "block");
  }
  else if (chosen == "Inbred Strain")
  {
    setElementVisibility("InbredStrain", "block");
  }
  else
  {
    setElementVisibility("AdditionalInfo", "none");
    return;
  }
  setElementVisibility("AdditionalInfo", "block");

}

function UpdateCatalogUrlVisibility()
{
  var ele = document.getElementById("ISSupplier");
  if (ele.value != "JAX Mice")
  {
    clearFieldValue("ISSupplierCatalogNumber");
    setElementVisibility("jaxInstructions","none");
    setElementVisibility("morejaxinstructions","none");
    setElementVisibility("jaxCatalogNumber", "none");
    setElementVisibility("nonJaxUrlField","block");
    setElementVisibility("nonJaxInstructions","block");
  }
  else
  {

    setElementVisibility("jaxCatalogNumber", "table-row");
    setElementVisibility("jaxInstructions","block");
    setElementVisibility("morejaxinstructions","block");
    setElementVisibility("nonJaxUrlField","none");
    setElementVisibility("nonJaxInstructions","none");
  }

}

function MouseIsPublished()
{
  setElementVisibility("mouseDetails", "block");
  setElementVisibility("publishedDetails", "block");
  setElementVisibility("notPublishedDetails", "none");
}

function MouseIsNotPublished()
{
  setElementVisibility("mouseDetails", "block");
  setElementVisibility("publishedDetails", "none");
  setElementVisibility("notPublishedDetails", "block");
}


function checkOtherHolderName()
{
  var inputFieldId = document.getElementById("holderName");
  if(inputFieldId.value == "Other(specify)")
  {
    setElementVisibility("otherHolderSpan", "");
  }
  else
  {
    setElementVisibility("otherHolderSpan", "none");
  }
}
function checkOtherFacility()
{
  var inputFieldId = document.getElementById("holderFacility");
  if(inputFieldId.value == "Other(specify)")
  {
    setElementVisibility("otherFacilitySpan", "");
  }
  else
  {
    setElementVisibility("otherFacilitySpan", "none");
  }
}
function checkOtherMouseDiscipline()
{
  var inputFieldId = document.getElementById("mouseDiscipline");
  if(inputFieldId.value == "Other(specify)")
  {
    setElementVisibility("otherDisciplineSpan", "");
  }
  else
  {
    setElementVisibility("otherDisciplineSpan", "none");
  }
}

function changecss(theClass,element,value) {
  //Last Updated on October 10, 1020
  //documentation for this script at
  //http://www.shawnolson.net/a/503/altering-css-class-attributes-with-javascript.html
   var cssRules;

   var added = false;
   for (var S = 0; S < document.styleSheets.length; S++){

    if (document.styleSheets[S]['rules']) {
    cssRules = 'rules';
   } else if (document.styleSheets[S]['cssRules']) {
    cssRules = 'cssRules';
   } else {
    //no rules found... browser unknown
   }

    for (var R = 0; R < document.styleSheets[S][cssRules].length; R++) {
     if (document.styleSheets[S][cssRules][R].selectorText == theClass) {
      if(document.styleSheets[S][cssRules][R].style[element]){
      document.styleSheets[S][cssRules][R].style[element] = value;
      added=true;
    break;
      }
     }
    }
    if(!added){
    try{
      document.styleSheets[S].insertRule(theClass+' { '+element+': '+value+'; }',document.styleSheets[S][cssRules].length);

    } catch(err){
        try{document.styleSheets[S].addRule(theClass,element+': '+value+';');}catch(err){}

    }

    //if(document.styleSheets[S].insertRule){
        //document.styleSheets[S].insertRule(theClass+' { '+element+': '+value+'; }',document.styleSheets[S][cssRules].length);
      //} else if (document.styleSheets[S].addRule) {
        //document.styleSheets[S].addRule(theClass,element+': '+value+';');
      //}
    }
   }
  }


function setElementVisibility(elementName, displayStyle)
{
  var ele = document.getElementById(elementName);
  ele.style.display = displayStyle;

}

function setTwoElementVisibility(elementName, displayStyle, elementTwoName, displayStyleTwo)
{
  var ele = document.getElementById(elementName);
  ele.style.display = displayStyle;

  ele = document.getElementById(elementTwoName);
  ele.style.display = displayStyleTwo;

}


function setFocus(formID, elementID) {
  var loginForm = document.getElementById(formID);
  if (loginForm) {
    loginForm[elementID].focus();
  }
}

function one2two(elementID) {
  m1 = document.getElementById(elementID);
  m1len = m1.length ;
  for ( i=0; i<m1len ; i++){
    if (m1.options[i].selected == true ) {
      m2len = m2.length;
      m2.options[m2len]= new Option(m1.options[i].text);
    }
  }

   for ( i = (m1len -1); i>=0; i--){
     if (m1.options[i].selected == true ) {
       m1.options[i] = null;
     }
   }
}

function two2one(elementID) {
   m2 = document.getElementById(elementID)
   m2len = m2.length ;
   for ( i=0; i<m2len ; i++){
     if (m2.options[i].selected == true ) {
       m1len = m1.length;
       m1.options[m1len]= new Option(m2.options[i].text);
     }
   }
   for ( i=(m2len-1); i>=0; i--) {
     if (m2.options[i].selected == true ) {
       m2.options[i] = null;
     }
   }
}


