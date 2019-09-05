<%@ page import="java.util.ArrayList"%>
<%@ page import="java.sql.*"%>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@ page import="edu.ucsf.mousedatabase.objects.*"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditMouseSelection.jsp", true) %>
<%@ include file='SendMailForm.jspf' %>
<%@ include file="protectAgainstDuplicateHolders.jspf" %>
<%


  if (request.getParameter("id") == null)
  {
    %>
    <div class="site_container">
    <h2>No record specified</h2>
    </div>
    <%
    return;
  }
    int mouseID = Integer.parseInt(request.getParameter("id"));


    ArrayList<MouseRecord> records = DBConnect.getMouseRecord(mouseID);
    if(records.size() < 1)
    {
      %>
    <div class="site_container">
    <h2>Record #<%=mouseID %> not found</h2>
    </div>
    <%
    return;
    }

    MouseRecord record = records.get(0);
    String recordID = record.getMouseID();

    if(record.getStatus() != null && record.getStatus().equalsIgnoreCase("incomplete"))
  {
    %>
    <div class="site_container">
    <h2>This record is part of an incomplete submission.  Please go to the 'hold' submissions page and click the 'create new record' link to edit.</h2>
    </div>
    <%
    return;
  }

    String existingRecord = HTMLGeneration.getMouseTable(records,true,false,true);
    String editForm = HTMLGeneration.getEditMouseForm(record);

%>


<div class="site_container">
<h2>Editing record #<%=record.getMouseID() %>: <%=record.getMouseName() %> (<%= record.getMouseType() %>  )
</h2>
<%@ include file='_lastEditMiceLink.jspf' %>
<%=existingRecord %>
<%=editForm %>



</div>

<script>
function uploadFile(){
	const files = document.querySelector('[type=file]').files;
	console.log("calling upload");
	var formData = new FormData();
	for(i = 0; i<files.length; i++ ){
		formData.append("files", files[i].name, file);
	}	
	var recordID = document.getElementById("recordID").value;
	console.log("recordID = " + recordID);
	formData.append("MouseID", recordID);
	//formData.append("files", files);
	var testForm = new FormData();

	var xhr = new XMLHttpRequest();
	
	xhr.open('POST', 'UploadFile.jsp', true);
	xhr.setRequestHeader("MouseID", recordID);
	
	xhr.onload = function () {
		  if (xhr.status === 200) {
		    // File(s) uploaded.
		    //uploadButton.innerHTML = 'Upload';
		  } else {
		    alert('An error occurred!');
		  }
		};

	xhr.send(formData);
		

	/*
	$.ajax({
		url:"uploadFiles.php",
		type: 'POST',
		data: formData, //formData,
		success: function(response) {
	            console.log(response);
	        },
	    error: function(response) {
	            console.log(response);
	    }
	});*/
}
	

	
	//need to define record
	
	//cannot send javascript data to server in script, neet to send as ajax or similar.
	/*var xhttp = new XMLHttpRequest();
	for (file : files){
		xhttp.open("POST", uploadFiles.php, true);
		xhttp.send();
		}
	*/
	
	
	//record.addFiles(files);


	
	////Log.info("called record.addFiles");
	//const formData = new FormData();// change to blob
	//var myBlob = null; 
	/*
	
	for (let i = 0; i < files.length; i++) {
			let file = files[i]
			var blob = new Blob([file], type = "text");
			fileName = file.name;
			//submit 
			
			//formData.append('files[]', file) //change to blob
	}
	//set value
	
	record.setFiles(myBlob);*/
</script>

