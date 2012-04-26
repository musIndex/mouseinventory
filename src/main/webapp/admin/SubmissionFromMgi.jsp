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
String header;
boolean processed = false;
String bodyText = "";
StringBuilder duplicateBuffer = new StringBuilder();
StringBuilder invalidIdsBuffer = new StringBuilder();
List<String> badIds = new ArrayList<String>();


ArrayList<String> allFacs = DBConnect.getAllFacilityNames(false);
ArrayList<String> allHldrs = DBConnect.getAllHolderNames(false);

String[] facilityList = new String[allFacs.size()];
allFacs.toArray(facilityList);
String[] holderList = new String[allHldrs.size()];
allHldrs.toArray(holderList);


if (request.getParameter("mgiIds") != null)
{
	String ids = request.getParameter("mgiIds");
	
	String holderName = request.getParameter("holderName");
	String facilityName = request.getParameter("holderFacility");
	
	List<Integer> mgiIds = new ArrayList<Integer>();
	
	for (String id : ids.split("[\r\n]+"))
	{
		try
		{
			mgiIds.add(Integer.parseInt(id));
		}
		catch(Exception e)
		{
			badIds.add(id);
		}
	}
	String idList = "";
	boolean first = true;
	for(int id : mgiIds)
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
	}
	
	HashMap<Integer,MouseSubmission> requestedSubmissions = MGIConnect.SubmissionFromMGI(mgiIds,-1);
	
	List<MouseSubmission> submissions = new ArrayList<MouseSubmission>();
	
	for (int mgiId : requestedSubmissions.keySet())
	{
		if (requestedSubmissions.get(mgiId) != null)
		{
			submissions.add(requestedSubmissions.get(mgiId));
		}
		else
		{
			invalidIdsBuffer.append("<b>Not a valid Allele MGI ID: " + mgiId + ".  No submission generated.<b><br>");
		}
	}
	
	if (submissions.size() <= 0)
	{
		header = "No submissions created.  Input ids were: " + idList;
	}
	else
	{
		UserData submitterData = new UserData();
		submitterData.setFirstName("Database");
		submitterData.setLastName("Administrator");
		submitterData.setEmail(HTMLGeneration.AdminEmail);
		submitterData.setDepartment("database admin");
		submitterData.setTelephoneNumber(" ");
		List<Integer> subIds = new ArrayList<Integer>();	
		
		
		
		for(MouseSubmission sub : submissions)
		{
			
			if(DBConnect.isDuplicateSubmission(sub,duplicateBuffer))
			{
				continue;
			}
			sub.setHolderFacility("unassigned");
			sub.setHolderName("unassigned");
			if (holderName != null && !holderName.isEmpty() &&!holderName.equals("Choose One"))
			{
				sub.setHolderName(holderName);
			}
			if (facilityName != null && !facilityName.isEmpty() && !holderName.equals("Choose One"))
			{
				sub.setHolderFacility(facilityName);
			}
			int submissionID = DBConnect.insertSubmission(submitterData,sub,MouseSubmission.GetPropertiesString(submitterData,sub));
			DBConnect.updateSubmission(submissionID,"new","Auto-generated from MGI Submission tool");
			subIds.add(submissionID);
				

		}
		
		if (subIds.size() > 0)
		{		bodyText += HTMLGeneration.getSubmissionTable(DBConnect.getMouseSubmissions(subIds),null,null);
			header = "Created new submission(s): ";
		}
		else
		{
			header = "No submissions were created.";
		}
		processed = true;
		
		
	}
}
else
{
	header = "Create new submission from MGI number(s)";
}

%>

<div class="pagecontent">

<% if(badIds.size() > 0){ %>
<h3>Non-integer values were entered and ignored.  Please enter only numbers, one per line:</h3>
<ul style="color:red">
<%for(String badId : badIds){ %>
<li><%=badId %></li>
<%} %>
</ul>
<%} %>

<% if(duplicateBuffer.length() > 0 ) {%>

<h3>Duplicate entries were found:</h3>

<%=duplicateBuffer%>
<br><br>

<% } %>

<% if(invalidIdsBuffer.length() > 0 ) {%>

<h2>One or more of the MGI IDs were invalid:</h2>

<%=invalidIdsBuffer%>
<br><br>

<% } %>

<h2><%=header %></h2>

<% 
if(!processed) 
{
	%>
	<p>To process multiple MGI ids simultaneously, enter one per line.  
	NOTE - it may take a long time to process multiple IDs, please be patient.  Progress reporting to be implemented later.
	
	<form method="post" action="SubmissionFromMgi.jsp">
	<textarea  name=mgiIds rows="20" cols="20"></textarea>
	<br>
	<table>
	<tr>
		<td valign="top">Holder</td>
		<td valign="top"><%=HTMLGeneration.genSelect("holderName",(String[])holderList,"",null)%>
		</td>
	</tr>
	
	<tr >
		<td valign="top">Facility :</td>
		<td valign="top"><%=HTMLGeneration.genSelect("holderFacility",(String[])facilityList,"", null)%>
		</td>
	</tr>
	
	</table>
	<input type="submit" class="btn btn-primary" value="Submit">
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