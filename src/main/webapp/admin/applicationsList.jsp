<%--
  Created by IntelliJ IDEA.
  User: Chris
  Date: 12/17/2020
  Time: 10:08 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page import="java.util.SortedMap"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@page import="java.util.ArrayList"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.getNavBar" %>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>
<%@ page import="edu.ucsf.mousedatabase.HTMLGeneration" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("admin.jsp", true) %>


<div>
    <%=
    HTMLGeneration.getApplicantTable()
    %>
</div>
