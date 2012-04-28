<%@ page import="java.util.*" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>

<%
  

    String orderBy = request.getParameter("orderby");
  String entered = request.getParameter("entered");
  String status = request.getParameter("status");

  if (orderBy == null) 
    {
        orderBy = "submittedmouse.id";
    }
    
    if(entered!= null && entered.equalsIgnoreCase("null"))
    {
      entered = null;
    }


    /******/
    if(status == null)
  {
    if ((status = (String)session.getAttribute("listSubmissionStatus")) == null)
    {
      status = "new";
    }
  }
    if (status.equalsIgnoreCase("need")) 
    {
        status = "need more info";
    }
  session.setAttribute("listSubmissionStatus",status);
  session.setAttribute("listSubmissionOrderBy",orderBy);

    
  
  
    
    ArrayList<SubmittedMouse> submissions = DBConnect.getMouseSubmissions(status, entered, orderBy);
    
    String[] sortOptions = new String[] {"submittedmouse.id","mouse.id","firstname","lastname","date"};
    String[] sortOptionNiceNames = new String[] {"Submission #", "Record #", "Submitter first name", "Submitter last name", "Submission date"};
    
    String[] filterOptions = new String[] {"new","need more info","rejected","accepted","all"};
    String[] filterOptionNiceNames = new String[] {"New", "Hold", "Rejected","Currently Housed","All"};
    
    int kount = submissions.size();
 
    
    String table = HTMLGeneration.getSubmissionTable(submissions, status, entered);

    StringBuffer sortBuf = new StringBuffer();
    sortBuf.append("<form action=\"ListSubmissions.jsp\" method=\"post\">");
    sortBuf.append("&nbsp;Show: ");
    sortBuf.append(HTMLGeneration.genFlatRadio("status",filterOptions,filterOptionNiceNames, status,""));
    sortBuf.append("<br>&nbsp;Sort by: ");
    sortBuf.append(HTMLGeneration.genFlatRadio("orderby",sortOptions,sortOptionNiceNames, orderBy,""));
    sortBuf.append("<input type=\"hidden\" name=\"entered\" value=\"" + entered +"\">");
    sortBuf.append("<br>&nbsp;<input type=\"submit\" value=\"Update\">"); 
    sortBuf.append("</form>");
  
    
    String statusString = "Listing " + status + " submissions";
    if(status.startsWith("need"))
    {
      statusString = "Listing submissions in Holding";
      status="need";
    }else if(status.startsWith("accepted"))
    {
      statusString = "Listing submissions that have been converted to records";
    }
      
%>
<%=HTMLGeneration.getNavBar("ListSubmissions.jsp?status="+status+"&entered="+entered, true) %>
<div class="pagecontent-leftaligned">
<h2><%= statusString %></h2>
<h4><%= kount %> found.</h4>
<%= sortBuf.toString()%>
<%= table%>
</div>