<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.getNavBar" %>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>
<%=getPageHeader(null, false, false, "onload=\"setFocus('quickSearchForm', 'searchterms')\"")%>
<%=getNavBar("about.jsp", false)%>
<div class = "category">
    <div class = "two_column_left">
        <p>We have received your application.
            It will be reviewed by the administrator.
            <br>
            <br>
            If you believe you have made an error in your application, click on "submit feedback"
            in the menu above and send an explanatory email to the Rodent Database Administrator.
            <br>
            Thank you.
        </p>
        Your application is complete!
        <br>
        <br>
        <a href="about.jsp"><button class = "btn btn-info">Return to homepage</button></a>
        <br/>

    </div>
    <div class = "two_column_right">
        <br>
        <img src="img/database_check.png">
        <br>
        <br>
        New applications are checked every Tuesday and Friday
        <br>
        If you'd like to inquire about your application, email us at <a href = "mailto:ora.carrodentdatabase@msu.edu">ora.carrodentdatabase@msu.edu</a>.
    </div></div>