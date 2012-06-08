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

private String getReportForm(String reportName, String reportDescription, String extraListItem)
{
  StringBuilder sb = new StringBuilder();
  sb.append("<form method=\"get\" action=\"" + HTMLGeneration.siteRoot + "report\"\r\n");
  sb.append("<dl>\r\n");
  sb.append("<dt><b>" + reportName + "</b></dt>\r\n");
  sb.append("<dd>" + reportDescription + "</dd>\r\n");
  if (extraListItem != null)
  {
    sb.append("<dd>" + extraListItem + "</dd>");
  }
  sb.append("<dd><button type='submit' name='reportName' class='btn btn-mini btn-primary' value='" + reportName
          + "' />Download</button>");
  sb.append("</dl>\r\n");
  sb.append("</form>\r\n");
  return sb.toString();

}


%>
<%
   String pptReportSelector = getReportSelector(ImportObjectType.PPTCHANGEREQUEST,"importReportId-ppt");
   String purchaseSubmissionReportSelector = getReportSelector(ImportObjectType.PURCHASESUBMISSION,"importReportId-purchase_sub");
   String purchaseChangeRequestReportSelector = getReportSelector(ImportObjectType.PURCHASECHANGEREQUEST,"importReportId-purchase_ppt");
   String otherInstitutionsSubmissionReportSelector = getReportSelector(ImportObjectType.OTHERINSTITUTIONSUBMISSION,"importReportId-otherinstitutions_sub");
   String otherInstitutionsChangeRequestReportSelector = getReportSelector(ImportObjectType.OTHERINSTITUTIONSCHANGEREQUEST,"importReportId-otherinstitutions_chrq");
%>
<div class="pagecontent">

<h2>Reports</h2>
<%=getReportForm(ReportServlet.HolderReportName,"Lists all holders, name, department, email, number of mice held, and date of last review.",null ) %>
<%=getReportForm(ReportServlet.RecordsReportName,"Lists all live records, basic details, submitter name, and up to 5 holders.",null ) %>
<%=getReportForm(ReportServlet.LarcRecordHolderReportName,"Used by LARC to automatically populate dropdowns on their site.",null ) %>
<%=getReportForm(ReportServlet.PPTChangeRequestImportReportName,"Change requests created by PPT data uploads.","Select report: " + pptReportSelector ) %>
<%=getReportForm(ReportServlet.PurchaseSubmissionsImportReportName,"Submissions created by Purchase data uploads.","Select report: " + purchaseSubmissionReportSelector ) %>
<%=getReportForm(ReportServlet.PurchaseChangeRequestImportReportName,"Change requests created by Purchase data uploads.","Select report: " + purchaseChangeRequestReportSelector ) %>
<%=getReportForm(ReportServlet.OtherInstitutionsSubmissionsImportReportName,"Submissions created by Transfers from other Institutions data uploads.","Select report: " + otherInstitutionsSubmissionReportSelector ) %>
<%=getReportForm(ReportServlet.OtherInstitutionsChangeRequestImportReportName,"Change requests created by Transfers from other Institutions data uploads.","Select report: " + otherInstitutionsChangeRequestReportSelector ) %>

</div>
