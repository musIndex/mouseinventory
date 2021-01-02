<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.getNavBar" %>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>
<%=getPageHeader(null, false, false, "onload=\"setFocus('quickSearchForm', 'searchterms')\"")%>
<%=getNavBar("about.jsp", false)%>


<div>
    <form method="post" action="loginServlet">
        <table>
            <tr>
                <td>
                    Welcome to the Rodent Research Database Application's Rodent Records.<br>
                    Before you're able to view rodent records, you must first ensure that
                    you have filled out the database application.<br>
                    If you've already filled out the application, please enter your information
                    below.<br>
                    Thank you!<br><br>
                </td>
            </tr>
        </table>
        <table>
            <%--<tr>
                <td><label for="firstName">First name:</label></td>
                <td><input type="text" id="firstName" name="firstName"></td>
            </tr>
            <tr>
                <td><label for="lastName">Last name:</label></td>
                <td><input type="text" id="lastName" name="lastName"></td>
            </tr>--%>
            <tr>
                <td><label for="email">Email address:</label></td>
                <td><input type="text" id="email" name="email"></td>
            </tr>
            <tr>
                <td><label for="MSU NetID">MSU NetID:</label></td>
                <td><input type="text" id="MSU NetID" name="MSU NetID"></td>
            </tr>
        </table>
        <table>
            <tr>
                <td>
                    <input type = hidden name="page" value="applicationLoginRecords.jsp">
                    <input type="submit" class ="button btn-primary" value="Login">
                </td>
            </tr>
        </table>
    </form>
</div>