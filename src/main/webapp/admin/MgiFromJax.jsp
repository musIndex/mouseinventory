<%@page import="edu.ucsf.mousedatabase.MGIConnect"%>
<%@page import="edu.ucsf.mousedatabase.beans.MouseSubmission"%>
<%@ page import="java.util.*"%>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@ page import="edu.ucsf.mousedatabase.objects.*"%>
<%@ page import="edu.ucsf.mousedatabase.*"%>
<%@ page import="edu.ucsf.mousedatabase.beans.*"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("SubmissionFromMgi.jsp", true) %>


<%
  String header = "MGI ID Lookup";
  boolean processed = false;
  String bodyText = "";
  StringBuilder resultsBuffer = new StringBuilder();
  if (request.getParameter("jaxIds") != null)
  {
    header = "Lookup results";
    String ids = request.getParameter("jaxIds");
    String[] jaxIds = ids.split("[\r\n]+");
    String idList = "";
    boolean first = true;

    for(String id : jaxIds)
    {
      if (first)
      {
        first = false;
      }
      else
      {
        idList += ", ";
      }
      idList += id;

      ArrayList<Integer> MGIids = JaxMiceConnect.GetMGINumbersFromJax(id);
      for(int MGIid : MGIids)
      {
        resultsBuffer.append("JAX: " + id + " - MGI: " + MGIid + "<br>");
      }

    }
    processed = true;
    bodyText = resultsBuffer.toString();

  }


%>

<div class="pagecontent">


<h2><%=header %></h2>

<%
if(!processed)
{
  %>
  <p>To look up an MGI ID from a Jax stock number, enter one stock number per line.  Be sure to include any leading zeros, and no other characters.
  <br>For example:
  <br>000123
  <br>003303
  <br>
  <br>NOT:
  <br>#000133
  <br>123

  <form method="post" action="MgiFromJax.jsp">
  <textarea  name=jaxIds rows="20" cols="20"></textarea>
  <br>
  <input type="submit" value="Submit">
  </form>
  <%
}
else
{
  %>

  <%=bodyText %>


  <%
}
%>
</div>
