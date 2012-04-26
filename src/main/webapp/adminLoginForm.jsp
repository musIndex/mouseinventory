<%@page import="edu.ucsf.mousedatabase.HTMLGeneration" %>
<%@page import="edu.ucsf.mousedatabase.Log" %>
<%=HTMLGeneration.getPageHeader(null,false,true,"onload=\"setFocus('loginform', 'j_username')\"") %>
<%=HTMLGeneration.getNavBar(null, true, false) %>

<%
Log.Info("Admin loging in");
%>

<div class="pagecontent">


	<h2>Please Log in for administrative access</h2>
	<form id="loginform" name="loginform" action="j_security_check" method="POST">
	<table>
		<tr>
			<td>Username:</td>
			<td><input type="text" name="j_username"></td>
		</tr>
		<tr>
			<td>Password:</td>
			<td><input type="password" name="j_password"></td>
		</tr>
		<tr>
			<td><input class='btn btn-primary' type="submit" value="Login"></td>
		</tr>
	</table>
	</form>

</div>

