<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.getNavBar" %>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>
<%=getPageHeader(null, false, false, "onload=\"setFocus('quickSearchForm', 'searchterms')\"")%>
<%=getNavBar("about.jsp", false)%>
<div>
    <table class = "site_container">
        <tr>
            <td style="width: 50%; vertical-align: top">
                <h2>Registration complete!</h2>
                <p>We have received your registration form.
                    It will be reviewed by the administrator.
                    <br>
                    <br>
                    If you believe you have made an error, click on "submit feedback"
                    or send an explanatory email to the Rodent Database Administrator.
                    <br>
                    Thank you.
                </p>
                <a href="about.jsp"><button class = "btn btn-success">Return to homepage</button></a>
                <a href="contact.jsp"><button class = "btn btn-info">Submit Feedback</button></a>

                <br/>
            </td>
            <td style="width: 50%">
                <br>
                <img src="img/database_check.png">
                <br>
                <br>
                Registration forms are checked every Tuesday and Friday.
                <br>
                If you'd like to inquire about your registration form, email us at <a href = "mailto:ora.carrodentdatabase@msu.edu">ora.carrodentdatabase@msu.edu</a>.
            </td>
        </tr>
    </table>
</div>