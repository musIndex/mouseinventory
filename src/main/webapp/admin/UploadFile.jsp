<%@ page import="java.util.ArrayList"%>
<%@ page import="java.io.File"%>
<%@ page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@ page import="edu.ucsf.mousedatabase.objects.*"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.io.IOException"%>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%@page import="edu.ucsf.mousedatabase.servlets.*"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditMouseSelection.jsp", true) %>


<div></div>
<h2>Upload Files</h2>
<form action="<%=HTMLGeneration.adminRoot %>upload" enctype="multipart/form-data" method="post">
	<div>
		<span>New Filename</span>
		<input type="text" name="<%=UploadServlet.newNameFieldName %>"></input>
	</div>
	<input type="text" value= <%=request.getParameter("mouseID")%> name="<%=UploadServlet.mouseFieldName %>" style="display:none"></input>
	<input type="file" data-validate='notempty' data-title='Input file' name="<%=UploadServlet.fileFieldName %>" size="75"></input>
	<input type="submit" />
</form>
<div id = "test"><%=DBConnect.getFileNamesAsString(request.getParameter("mouseID")) %></div>
<div id = "test2"><%=DBConnect.getIDsAsString(request.getParameter("mouseID")) %></div>
<div id = "test3">Hello</div>
 <h2>Files To Delete</h2>
 <ul id = "listFiles"></ul>



<script>
<%

String mouseID = request.getParameter("mouseID");

//ArrayList<String> filenames = DBConnect.getFilenamesByMouseID(mouseID);
ArrayList<Integer> ids = DBConnect.getFileIDsByMouseID(mouseID);


%>


function myFunction(){
	
	var  string1 = document.getElementById("test").innerHTML;
	var  string2 = document.getElementById("test2").innerHTML;
	var names = string1.split("/");
	var nums = string2.split("/");	
	var list = document.getElementById("listFiles");
	
	for (var i = 1; i < names.length; i++) { 
		var a = document.createElement('a');
		var linkText = document.createTextNode(names[i]);
		a.appendChild(linkText); 
		//need to add file num to request
		var phrase = "<%=HTMLGeneration.adminRoot %>RemoveServlet?id="
		var phrase2 = phrase + nums[i] + "&mouseID=" + <%=request.getParameter("mouseID")%>;
		a.href = phrase2;


		var entry = document.createElement('li');
		entry.appendChild(a);
		
		//entry.appendChild(document.createTextNode(names[i]));
		list.appendChild(entry);
	}
}

window.onload = myFunction();



//var filenames = DBConnect.getFilenamesByMouseID(mouseID);

/*

 var entry = document.createElement('li');
 entry.appendChile(document.createTextNode(filenames[0]));
 list.appendChild(entry);
 */
 


</script>