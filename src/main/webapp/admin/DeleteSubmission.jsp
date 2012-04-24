<%@ page import="java.util.ArrayList" %>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar(null, true) %>

<%@page import="edu.ucsf.mousedatabase.objects.SubmittedMouse"%>
<div class="pagecontent">
<%
	String confirmed = request.getParameter("confirm");
	int submissionID = HTMLGeneration.stringToInt(request.getParameter("submissionID"));	
	if(confirmed==null || !confirmed.equals("confirmCode123456"))
	{
		ArrayList<SubmittedMouse> submissions = DBConnect.getMouseSubmission(submissionID);
		
		if(submissions.size() > 0)
		{		
			SubmittedMouse submission = submissions.get(0);
			if(submission.getStatus().equalsIgnoreCase("rejected"))
			{
				%>
				<h2> Are you sure you want to delete submission# <%=submissionID %>?</h2>
				<form action="DeleteSubmission.jsp" method="POST">
				<input type="hidden" name="confirm" value="confirmCode123456">
				<input type="hidden" name="submissionID" value="<%=submissionID %>">
				<input type="submit" value="Yes">
				</form>			
				<%
			}
			else
			{
				%>
				<h2> Submission #<%=submissionID %> is a <%=submission.getStatus() %> submission and cannot be deleted.  Only rejected submissions can be deleted.</h2>
				<%
			}
		}
		else
		{
			%>
			<h2>Submission #<%=submissionID %> not found </h2>
			<%
		}
	}
	else
	{
	    DBConnect.deleteSubmission(submissionID);
	    %>
		<h2>Submission# <%=submissionID %> was successfully deleted</h2>
		<%
	}   
%>
</div>
