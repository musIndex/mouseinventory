<%@page import="edu.ucsf.mousedatabase.HTMLGeneration" %>
<%@page import="edu.ucsf.mousedatabase.Log" %>
<%=HTMLGeneration.getPageHeader(null,false,true,"onload=\"setFocus('loginform', 'j_username')\"") %>
<%=HTMLGeneration.getNavBar(null, true, false) %>

<%
Log.Info("Admin loging in");
%>

<div class="pagecontent centered">
	<br>
	<br>	
	<form style="width:400px" class="centered-left" id="loginform" name="loginform" action="j_security_check" method="post">
	<h2>Sign in for administrative access</h2>
	<table>
		<tr>
			<td>Username:</td>
			<td><input type="text" name="j_username"></td>
			<td style="vertical-align:top" rowspan="2"><input class='btn btn-primary btn-large' type="submit" value="&nbsp;&nbsp;Login&nbsp;&nbsp;"></td>
		</tr>
		<tr>
			<td>Password:</td>
			<td><input type="password" name="j_password"></td>
		</tr>
	</table>
	</form>

</div>

