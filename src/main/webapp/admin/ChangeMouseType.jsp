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

<div class="pagecontent">

<%
if (!confirmed){
	 String table = HTMLGeneration.getMouseTable(records,false,false,false);
%>

<h2><font color="red">Please Confirm that you want to change the mouse type of record # <%= mouseID %> 
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
    Yes, I want to change the mouse type of this mouse from <%= records.get(0).getMouseType() %> to <%= newMouseType.getTypeName() %>:
    <input type="submit" class="btn btn-primary" value="SUBMIT">
</form>
Else click your browser's BACK button.

<%}else{ 
	record.setMouseType(newMouseType.getTypeName());
	DBConnect.updateMouseRecord(record);
	records = DBConnect.getMouseRecord(mouseID);
%>
<font size="4" color="black">The type of mouse ID <%= mouseID %> has been changed. Please <a
        href="EditMouseForm.jsp?id=<%= mouseID %>">edit it
    now</a> to enter type-specific data.  If this is an incomplete submission, you will need to return to the sumbmission page to edit it.</font><br><br>
    <h3>Updated Record:</h3>
    <%=HTMLGeneration.getMouseTable(records, true, false, false) %>
</div>

<%} %>
</div>