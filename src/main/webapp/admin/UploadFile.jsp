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



<form action="<%=HTMLGeneration.adminRoot %>upload" enctype="multipart/form-data" method="post">
	<input type="text" value= <%=request.getParameter("mouseID")%> name="<%=UploadServlet.mouseFieldName %>" style="display:none"></input>
	<input type="file" data-validate='notempty' data-title='Input file' name="<%=UploadServlet.fileFieldName %>" size="75">
	<input type="submit"/>
</form>







<%

//<input type="button" value="Upload File" name="upload" onClick="uploadFile()"/>
	/*
ArrayList<File> files = (ArrayList<File>) request.getAttribute("files");
	String mouseID = (String) request.getAttribute("mouseID");
	String test = request.getHeader("MouseID");
	String fileName = request.getHeader("fileName");
	
	testFunction(test, fileName);
	*/

%>