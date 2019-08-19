<%@page import="edu.ucsf.mousedatabase.dataimport.ImportHandler.ImportObjectType"%>
<%@ page import="edu.ucsf.mousedatabase.servlets.*" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.dataimport.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.io.ByteArrayInputStream" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("Reports.jsp", true) %>

<%!
private String getReportSelector(ImportObjectType reportType, String name)
{
  ArrayList<ImportReport> importReports = DBConnect.getImportReports(reportType);

  if (importReports.size() == 0)
  {
    return "[No reports available yet - run an import of this type first]";
  }
   String[] reportIds = new String[importReports.size()];
   String[] reportNames = new String[importReports.size()];

   for(int i = 0; i< importReports.size(); i++)
   {
     reportIds[i] = Integer.toString(importReports.get(i).getImportReportID());
     reportNames[i] = importReports.get(i).getName();
   }

   return HTMLGeneration.genSelect("importReportId",reportIds,reportNames,null,"style='width: 300px'",false);
}

private String getReportForm(String reportName, String reportDescription, String extraListItem){
 return getReportForm(reportName, reportDescription, extraListItem, null);
}

private String getReportForm(String reportName, String reportDescription, String extraListItem, String cssClass)
{
  StringBuilder sb = new StringBuilder();
  sb.append("<form method=\"get\" action=\"" + HTMLGeneration.siteRoot + "report\"\r\n");
  sb.append("<dl>\r\n");
  sb.append("<dt" + (cssClass != null ? " class='" + cssClass + "'" : "") + "><b>" + reportName + "</b></dt>\r\n");
  sb.append("<dd>" + reportDescription + "</dd>\r\n");
  if (extraListItem != null)
  {
    sb.append("<dd>" + extraListItem + "</dd>");
  }
  sb.append("<dd><button type='submit' name='reportName' class='btn btn-mini btn-primary' value='" + reportName
          + "' />Download csv</button>");
  sb.append("</dl>\r\n");
  sb.append("</form>\r\n");
  return sb.toString();

}

private String getAllRecordsPDFLink(){
 return "<a class='btn btn-info btn-mini' href='" + HTMLGeneration.siteRoot + "MouseList?status=live'>Download PDF</a><br>"; 
}


%>
<%
   String pptReportSelector = getReportSelector(ImportObjectType.PPTCHANGEREQUEST,"importReportId-ppt");
   String purchaseSubmissionReportSelector = getReportSelector(ImportObjectType.PURCHASESUBMISSION,"importReportId-purchase_sub");
   String purchaseChangeRequestReportSelector = getReportSelector(ImportObjectType.PURCHASECHANGEREQUEST,"importReportId-purchase_ppt");
   String otherInstitutionsSubmissionReportSelector = getReportSelector(ImportObjectType.OTHERINSTITUTIONSUBMISSION,"importReportId-otherinstitutions_sub");
   String otherInstitutionsChangeRequestReportSelector = getReportSelector(ImportObjectType.OTHERINSTITUTIONSCHANGEREQUEST,"importReportId-otherinstitutions_chrq");
   String otherInstitutionsUnpublishedReportSelector = getReportSelector(ImportObjectType.OTHERINSTITUTIONSUNPUBLISHED,"importReportId-otherinstitutions_unpub");

%>
<div class="site_container">

<h2>Reports</h2>
<%=getReportForm(ReportServlet.PPTChangeRequestImportReportName,"Change requests created by PI-to-PI transfer data uploads.","Select report: " + pptReportSelector,"upload-tdu" ) %>

<%=getReportForm(ReportServlet.PurchaseChangeRequestImportReportName,"Change requests created by purchase data uploads.","Select report: " + purchaseChangeRequestReportSelector , "upload-pdu") %>
<%=getReportForm(ReportServlet.PurchaseSubmissionsImportReportName,"Submissions created by purchase data uploads.","Select report: " + purchaseSubmissionReportSelector, "upload-pdu") %>

<%=getReportForm(ReportServlet.OtherInstitutionsChangeRequestImportReportName,"Change requests created by import data uploads.","Select report: " + otherInstitutionsChangeRequestReportSelector,"upload-idu" ) %>
<%=getReportForm(ReportServlet.OtherInstitutionsSubmissionsImportReportName,"Submissions created by import data uploads.","Select report: " + otherInstitutionsSubmissionReportSelector,"upload-idu" ) %>
<%=getReportForm(ReportServlet.OtherInstitutionsUnpublishedImportReportName,"Unpublished imports from import data uploads..","Select report: " + otherInstitutionsUnpublishedReportSelector,"upload-idu" ) %>

<%=getReportForm(ReportServlet.HolderReportName,"Lists all holders, name, department, email, primary contact name and email, number of mice held, and date of last review.",null ) %>
<%=getReportForm(ReportServlet.RecordsReportName,"Lists all live records, basic details, submitter name, and up to 5 holders.  PDF report contains full details for all live records.", getAllRecordsPDFLink() ) %>
<%=getReportForm(ReportServlet.LarcRecordHolderReportName,"Used by LARC to automatically populate dropdowns on their site.",null ) %>

<br>


<br><br><br><br><br>
</div>
