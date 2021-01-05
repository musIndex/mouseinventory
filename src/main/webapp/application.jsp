<%@page import="java.util.SortedMap"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@page import="java.util.ArrayList"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.getNavBar" %>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>
<%=getPageHeader(null, false, false, "onload=\"setFocus('quickSearchForm', 'searchterms')\"")%>
<%=getNavBar("about.jsp", false)%>


<div>
    <form method="post" action="applicantServlet">
        <table class="site_container">
            <tr>
                <td style="width: 50%">
                    <h2>Rodent Research Database Access Application</h2>
                    Welcome to the Rodent Research Database Application.<br>
                    Please fill out the following information.
                    <br>Upon completing the form, your information will be sent to site administrators who will then review your information.<br>
                    Thank you!<br><br>

                    <table>
                        <tr>
                            <td><label for="firstName">First name:</label></td>
                            <td><input type="text" id="firstName" name="firstName" required></td>
                        </tr>
                        <tr>
                            <td><label for="lastName">Last name:</label></td>
                            <td><input type="text" id="lastName" name="lastName" required></td>
                        </tr>
                        <tr>
                            <td><label for="email">Email address:</label></td>
                            <td><input type="text" id="email" name="email" required></td>
                        </tr>
                        <tr>
                            <td><label for="MSU NetID">MSU NetID:</label></td>
                            <td><input type="text" id="MSU NetID" name="MSU NetID" required></td>
                        </tr>
                        <tr>
                            <td><label for="AUF">AUF/Protocol numbers:</label></td>
                            <td><input type="text" id="AUF" name="AUF" required></td>
                        </tr>
                        <tr>
                            <td style = "vertical-align: top">Lab affiliation:</td>
                            <td>
                                <select name="position" id="position" required>
                                    <option value="select">Select position</option>
                                    <option value="researcher">Researcher</option>
                                    <option value="assistant">Lab Assistant</option>
                                </select>
                                <br><br>
                            </td>
                        </tr>
                    </table>
                    <table>
                        <tr>
                            <td>
                                <input type="submit" class ="button btn-primary" value="Submit application">
                            </td>
                        </tr>
                    </table>
                </td>
                <td style="vertical-align: top; width: 50%">
                        <br>
                        <img src="img/database_check.png">
                        <br>
                        <br>
                        New applications are checked every Tuesday and Friday
                        <br>
                        If you'd like to inquire about your application, email us at <a href = "mailto:ora.carrodentdatabase@msu.edu">ora.carrodentdatabase@msu.edu</a>.
                </td>
            </tr>
        </table>
    </form>
</div>
