<%@ page import="java.util.ArrayList"%>
<%@ page import="java.io.File"%>
<%@ page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@ page import="edu.ucsf.mousedatabase.objects.*"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.io.IOException"%>

<%
	//ArrayList<File> files = (ArrayList<File>) request.getAttribute("files");
	//String mouseID = (String) request.getAttribute("mouseID");
	String test = request.getHeader("MouseID");

	testFunction(test);
	//sendFilesToDatabase(files, test);
		//upload to mysql
	
	
	
	//ArrayList<MouseRecord> records = DBConnect.getMouseRecord(mouseID);
	//MouseRecord record = records.get(0);
	//record.addFiles(files);
	

	//for(File file : files){
		
	//}

%>