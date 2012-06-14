package edu.ucsf.mousedatabase.servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import edu.ucsf.mousedatabase.*;
import edu.ucsf.mousedatabase.objects.ImportReport;

public class ReportServlet extends HttpServlet
{

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public static final String HolderReportName = "Holder report";
  public static final String RecordsReportName = "Records report";
  public static final String LarcRecordHolderReportName = "LARC record holder report";
  public static final String PPTChangeRequestImportReportName = "TDU change request report";
  public static final String PurchaseChangeRequestImportReportName = "PDU change request report";
  public static final String PurchaseSubmissionsImportReportName = "PDU new submissions report";
  public static final String OtherInstitutionsChangeRequestImportReportName = "IDU change request report";
  public static final String OtherInstitutionsSubmissionsImportReportName = "IDU new submissions report";

  public void doGet(HttpServletRequest request, HttpServletResponse  response)
      throws IOException, ServletException {

    Object[] args = null;
    String reportName = request.getParameter("reportName");
    Boolean problem = false;
    String filename = reportName + ".csv";
    if (reportName.equals(LarcRecordHolderReportName))
    {
      filename = "holderreport.csv";
    }
    if (reportName.equals(PPTChangeRequestImportReportName) || reportName.equals(PurchaseChangeRequestImportReportName) || 
        reportName.equals(PurchaseSubmissionsImportReportName) || reportName.equals(OtherInstitutionsChangeRequestImportReportName) ||
        reportName.equals(OtherInstitutionsSubmissionsImportReportName))
    {
      args = new Object[1];
      args[0] = request.getParameter("importReportId");
      ArrayList<ImportReport> reports = DBConnect.getImportReport(Integer.parseInt((String)args[0]));
      filename = reportName + " " + reports.get(0).getName() + ".csv";
    }

    String errorMessage = "Problem running report.  Please contact Mouse Database Administrator\r\n";
    String report = "";
    if (reportName != null && !reportName.equals(""))
    {
      try
      {
        report = DBConnect.RunReport(reportName,args);
      }
      catch (IndexOutOfBoundsException e)
      {
        problem = true;
        errorMessage += e.getMessage();
      }
      catch (Exception e)
      {
        e.printStackTrace();
        problem = true;
        errorMessage += e.getMessage();
      }
    }
    else
    {
      errorMessage += "No report specified!";
      problem = true;
    }

    response.setContentType("application/text");
    filename = filename.replaceAll(" ", "_");
    response.setHeader("Content-disposition","attachment; filename=" +filename);
        if (!problem)
        {
          response.getWriter().write(report);
        }
        else
        {
          response.getWriter().write(errorMessage);
        }


  }


}
