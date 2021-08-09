<%@ page import="java.util.ArrayList"%>
<%@ page import="java.sql.*"%>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@ page import="edu.ucsf.mousedatabase.objects.*"%>
<%@ page import="javax.swing.text.html.HTML" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditMouseSelection.jsp", true) %>
<%@ include file='SendMailForm.jspf' %>
<%@ include file="protectAgainstDuplicateHolders.jspf" %>
<%


  if (request.getParameter("id") == null)
  {
    %>
    <div class="site_container">
    <p class="main_header">No record specified.</p>
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
    <p class="main_header">Record #<%=mouseID %> not found</p>
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
    <p class="main_header">This record is part of an incomplete submission.  <br>Please go to the 'hold' submissions page and click the 'create new record' link to edit.</p>
    </div>
    <%
    return;
  }

    String existingRecord = HTMLGeneration.getMouseTable(records,true,false,true);
    String editForm = HTMLGeneration.getEditMouseForm(record);

%>


<div class="site_container">
<p class="main_header">Editing record #<%=record.getMouseID() %>: <%=record.getMouseName() %> (<%= record.getMouseType() %>  )
</p>
<%--<%@ include file='_lastEditMiceLink.jspf' %>--%>
<%=existingRecord %>
<br>
<%=editForm %>



</div>
</div>
<%=HTMLGeneration.getWebsiteFooter()%>

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
}

</script>

