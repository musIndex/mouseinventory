<%@page import="edu.ucsf.mousedatabase.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="java.util.ArrayList" %>

<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar(null, true) %>
<%
    int mouseID = Integer.parseInt(request.getParameter("mouse_id"));
    String expressedsequenceID = request.getParameter("expressedsequence_id");
    String trangenicTypeID = request.getParameter("transgenictype_id");
    int newMouseTypeID = Integer.parseInt(request.getParameter("mousetype_id"));
    String inbredStrainID = request.getParameter("inbred_strain_id");

    ArrayList<MouseRecord> records = DBConnect.getMouseRecord(mouseID);
    MouseRecord record = records.get(0);
    MouseType newMouseType = DBConnect.getMouseType(newMouseTypeID);


    boolean confirmed = Boolean.parseBoolean(request.getParameter("confirm"));



%>

<div class="site_container">

<%
if (!confirmed){
   String table = HTMLGeneration.getMouseTable(records,false,false,false);
%>

<h2><font color="red">Please Confirm that you want to change the rodent category of record # <%= mouseID %>
from <%= record.getMouseType() %> to <%= newMouseType.getTypeName() %>
</font></h2>
<%= table %>
<form action="ChangeMouseType.jsp" method="post">
    <input type="hidden" name="mouse_id" value="<%= mouseID %>">
    <input type="hidden" name="mousetype_id" value="<%= newMouseTypeID %>">
    <input type="hidden" name="expressedsequence_id" value="<%= expressedsequenceID %>">
    <input type="hidden" name="transgenictype_id" value="<%= trangenicTypeID %>">
    <input type="hidden" name="inbred_strain_id" value="<%= inbredStrainID %>">
    <input type="hidden" name="confirm" value="true" >
    Yes, I want to change the category of this record from <%= records.get(0).getMouseType() %> to <%= newMouseType.getTypeName() %>:
    <input type="submit" class="btn btn-primary" value="SUBMIT">
</form>
If not click your browser's BACK button.

<%}else{
  record.setMouseType(newMouseType.getTypeName());
  DBConnect.updateMouseRecord(record);
  records = DBConnect.getMouseRecord(mouseID);
%>
<font size="4" color="black">The category of rodent ID <%= mouseID %> has been changed. <a
        href="EditMouseForm.jsp?id=<%= mouseID %>">Click here</a> to make other changes to record.  
        If this is an incomplete submission, you will need to 
        return to the submission page to edit it.</font><br><br>
    <h3>Updated Record:</h3>
    <%=HTMLGeneration.getMouseTable(records, true, false, false) %>
<%} %>
</div>
