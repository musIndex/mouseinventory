<%@ page import="java.util.ArrayList"%>
<%@ page import="java.io.File"%>
<%@ page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@ page import="edu.ucsf.mousedatabase.objects.*"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.io.IOException"%>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%@page import="edu.ucsf.mousedatabase.servlets.*"%>
<%=HTMLGeneration.getPageHeader(null,false,false) %>
<%=HTMLGeneration.getNavBar("ChangeRequestForm.jsp", false) %>
<jsp:useBean id="changeRequest" class="edu.ucsf.mousedatabase.objects.ChangeRequest" scope="session"></jsp:useBean>

<div></div>

<h2>Upload Files</h2>
<form id=uploadfile action="<%=HTMLGeneration.siteRoot %>upload" enctype="multipart/form-data" method="post">
	<div>
		<span>New Filename</span>
		
		<input id ="newFileName" type="text" name="<%=UploadServlet.newNameFieldName %>"></input>
		
	</div>
	<input id="newName" type="text" value= <%=request.getParameter("mouseID")%> name="<%=UploadServlet.mouseFieldName %>" style="display:none"></input>
	<input type="file" id="file" data-validate='notempty' data-title='Input file' name="<%=UploadServlet.fileFieldName %>" size="75"></input>	
	<input type="submit" value="Submit File" name="submit" onclick="newFileArray()"/>
	
     
</form>

 <h3> Last File Uploaded: <%=request.getSession().getAttribute("fileName")%></h3>
 
 <h3>Submit change request when done uploading files.</h3>
 <div id = "test" style="display:none"><%=DBConnect.getFileNamesAsString(request.getParameter("mouseID")) %></div>
 <div id = "test2" style="display:none"><%=DBConnect.getIDsAsString((request.getParameter("mouseID")),"approved")%></div>
 <h3>Select File To Delete</h3>
<ul id = "listFiles"></ul>
<h3>File requested to be deleted: </h3>
<ul id = "listDelete"></ul>
     	
<script>
      
$(document).on("submit", "#uploadfile", function(event) {
var fileNames= String(document.getElementById(request.getAttribute("fileName")));
	
event.preventDefault(); // Important! Prevents submitting the form.
event.stopPropagation();
return false;

});
</script>
<script>
var  string1 = document.getElementById("test").innerHTML;
var  string2 = document.getElementById("test2").innerHTML;
var names = string1.split("/");
var nums = string2.split("/");	
var list = document.getElementById("listFiles");



for (var i = 1; i < names.length; i++) { 
	var btn = document.createElement("BUTTON");
	btn.innerHTML = "Delete";
	console.log("made button")
	
	var s = document.createElement('span');
			
	var a = document.createElement('a');
	var linkText = document.createTextNode(names[i]);
	a.appendChild(linkText); 
	var deletePhrase = "<%=HTMLGeneration.siteRoot %>RemoveServlet?id="
	var deletePhrase2 = deletePhrase + nums[i] + "&mouseID=" + <%=request.getParameter("mouseID")%>;
	
	
	var viewPhrase = "<%=HTMLGeneration.siteRoot %>"
	var viewPhrase2 = viewPhrase +"/download" + "?ID=" + nums[i];
	btn.setAttribute("onClick", "sendDelete(event,'" + deletePhrase2 + "')");
	 	
	a.href = viewPhrase2;
	
	s.appendChild(a);
	s.appendChild(btn);

	var entry = document.createElement('li');
	entry.appendChild(s);
		
	list.appendChild(entry);
}

 
</script>
 

<script>
function sendDelete(event, phrase) {
	event.preventDefault();
	console.log(event.target.parentElement.children[0].textContent);
	var deleteFileNames = String(event.target.parentElement.children[0].textContent);
	deleteFileNames = document.getElementById("listDelete");
	
	window.location=phrase;
	debugger;
	event.stopPropagation();
	return false;	
}
</script>



