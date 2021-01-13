<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.getNavBar" %>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>
<%=getPageHeader(null, false, false, "onload=\"setFocus('quickSearchForm', 'searchterms')\"")%>
<%=getNavBar("about.jsp", false)%>


<div>
    <form method="post" action="loginServlet">
        <table class="site_container">
            <tr>
                <td>
                    <h2>Rodent Submission Login</h2>
                    Welcome to the Rodent Submission Process.<br>
                    Before you're able to view rodent records, ensure that
                    you have filled out a registration form.<br>
                    If your registration form has been approved, please enter your information
                    below.<br><br>

                    <table>
                        <form method="post" action="loginServlet">
                            <tr>
                                <td><label for="email">Email address:</label></td>
                                <td><input type="text" id="email" name="email" required></td>
                            </tr>
                            <tr>
                                <td><label for="MSU NetID">MSU NetID:</label></td>
                                <td><input type="text" id="MSU NetID" name="MSU NetID" required></td>
                            </tr>
                            <tr>
                                <td>
                                    <input type = hidden name="page" value="applicationLoginSubmit.jsp">
                                    <input type="submit" class ="button btn-primary" value="Login">
                                </td>
                            </tr>
                        </form>
                    </table>
                </td>
                <td style="vertical-align: top;width: 50%">
                    <h2>Registration Information</h2>
                    In order to access the Rodent Records and submit
                    rodents to the database, you must first fill out a registration form.
                    <br>
                    Registration can be found by following the button below, or
                    clicking on the "Registration" tab in the navigation bar.
                    <br>
                    <br>
                    <a href="application.jsp"><button class = "btn btn-success">Registration</button></a>
                </td>
            </tr>
        </table>
    </form>
</div>