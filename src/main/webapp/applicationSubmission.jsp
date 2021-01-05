<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.getNavBar" %>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>
<%=getPageHeader(null, false, false, "onload=\"setFocus('quickSearchForm', 'searchterms')\"")%>
<%=getNavBar("about.jsp", false)%>
<div>
    <table class = "site_container">
        <tr>
            <td style="width: 50%; vertical-align: top">
                <h2>Application submission complete!</h2>
                <p>We have received your application.
                    It will be reviewed by the administrator.
                    <br>
                    <br>
                    If you believe you have made an error in your application, click on "submit feedback"
                    in the menu above and send an explanatory email to the Rodent Database Administrator.
                    <br>
                    Thank you.
                </p>
                <a href="about.jsp"><button class = "btn btn-success">Return to homepage</button></a>
                <br/>
            </td>
            <td style="width: 50%">
                <br>
                <img src="img/database_check.png">
                <br>
                <br>
                New applications are checked every Tuesday and Friday.
                <br>
                If you'd like to inquire about your application, email us at <a href = "mailto:ora.carrodentdatabase@msu.edu">ora.carrodentdatabase@msu.edu</a>.
            </td>
        </tr>
    </table>
</div>