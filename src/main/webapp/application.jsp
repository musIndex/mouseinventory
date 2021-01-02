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
    <table>
        <tr>
            <td>
                Welcome to the Rodent Research Database Application.<br>
                Please fill out the following information. Upon completing the form,
                your information will be sent to the site administrators who will then review and
                make a decision based upon your information.<br>
                Thank you!<br><br>
            </td>
        </tr>
    </table>
        <table>
        <tr>
            <td><label for="firstName">First name:</label></td>
            <td><input type="text" id="firstName" name="firstName"></td>
        </tr>
        <tr>
            <td><label for="lastName">Last name:</label></td>
            <td><input type="text" id="lastName" name="lastName"></td>
        </tr>
        <tr>
            <td><label for="email">Email address:</label></td>
            <td><input type="text" id="email" name="email"></td>
        </tr>
       <tr>
           <td><label for="MSU NetID">MSU NetID:</label></td>
           <td><input type="text" id="MSU NetID" name="MSU NetID"></td>
        </tr>
        <tr>
            <td><label for="AUF">AUF/Protocol numbers:</label></td>
            <td><input type="text" id="AUF" name="AUF"></td>
        </tr>
        <tr>
            <td>Lab affiliation:</td>
        <td>
            <select name="position" id="position">
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
    </form>
</div>
