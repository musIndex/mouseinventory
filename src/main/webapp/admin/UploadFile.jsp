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

<div class="site_container">
<div></div>
<div id = "adminStatus" style="display:none"></div>
<h2>Upload Files</h2>
<form action="<%=HTMLGeneration.adminRoot %>upload" enctype="multipart/form-data" method="post">
	<div>
		<span>New Filename</span>
		<input type="text" name="<%=UploadServlet.newNameFieldName %>"></input>
	</div>
	<input id="newName" type="text" value= <%=request.getParameter("mouseID")%> name="<%=UploadServlet.mouseFieldName %>" style="display:none"></input>
	<input type="file" id="file" data-validate='notempty' data-title='Input file' name="<%=UploadServlet.fileFieldName %>" size="75"></input>
	
	<input type="submit" />
</form>
<h2>Change Requests for Files</h2>
<p>Download New Files and make changes. Check that the file name is unique for this record.
	Upload edited file and delete "new" file from list.
</p>
<h3>New Files Uploaded by Users</h3>
<div id = "newFilename" style="display:none"><%=DBConnect.getFileNamesAsStringStatus((request.getParameter("mouseID")),"new")%></div>
<div id = "newID" style="display:none"><%=DBConnect.getIDsAsString((request.getParameter("mouseID")),"new")%></div>
<ul id = "newFile" style="color:blue"></ul>
<h3>Delete Files Requested by Users</h3>
<div id = "deleteName" style="display:none"><%=DBConnect.getFileNamesAsStringStatus((request.getParameter("mouseID")),"delete")%></div>
<div id = "deleteID" style="display:none"><%=DBConnect.getIDsAsString((request.getParameter("mouseID")),"delete")%></div>
<ul id = "listFiles" style="color:red"></ul>

</div>

<script>




function sendDelete(phrase){
	window.location = phrase;
}


function myFunction(){
	//setAdminStatus;
	//set link to download
	//add button for delete
	var  string1 = document.getElementById("newFilename").innerHTML;
	var  string2 = document.getElementById("newID").innerHTML;
	var names = string1.split("/");
	var nums = string2.split("/");	
	var list = document.getElementById("newFile");
	deleteButton(names, nums, list);
	
	var  string3 = document.getElementById("deleteName").innerHTML;
	var  string4 = document.getElementById("deleteID").innerHTML;
	var namesD = string3.split("/");
	var numsD = string4.split("/");	
	var listD = document.getElementById("listFiles");
	deleteButton(namesD, numsD, listD);

	function deleteButton(names, nums, list){
	for (var i = 1; i < names.length; i++) { 
		var btn = document.createElement("BUTTON");
		btn.innerHTML = "Delete";
		console.log("made button")
		
		var s = document.createElement('span');
				
		var a = document.createElement('a');
		var linkText = document.createTextNode(names[i]);
		a.appendChild(linkText); 
		var deletePhrase = "<%=HTMLGeneration.adminRoot %>RemoveServlet?id="
		var deletePhrase2 = deletePhrase + nums[i] + "&mouseID=" + <%=request.getParameter("mouseID")%>;

		var viewPhrase = "<%=HTMLGeneration.siteRoot %>"
		var viewPhrase2 = viewPhrase +"/download" + "?ID=" + nums[i];
		
		btn.setAttribute("onClick", "sendDelete('" + deletePhrase2 + "')");
		
		a.href = viewPhrase2;
		//a.href = deletePhrase2;
		s.appendChild(a);
		s.appendChild(btn);

		var entry = document.createElement('li');
		entry.appendChild(s);
		//entry.appendChild(a);		
		list.appendChild(entry);
	}
	}
}

window.onload = myFunction();

</script>