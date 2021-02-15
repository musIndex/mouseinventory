package edu.ucsf.mousedatabase.servlets;

import edu.ucsf.mousedatabase.DBConnect;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/applicantServlet")

public class ApplicantServlet extends HttpServlet {
    private static String first_name;
    private static String last_name;
    private static String email;
    private static String netID;
    private static String AUF;
    private static String Position;
    private static int approved;
    public static int getApproved() {
        return approved;
    }
    public static void setApproved(int approved) {
        ApplicantServlet.approved = approved;
    }
    public static String getEmail() {
        return email;
    }
    public static void setEmail(String email) {
        ApplicantServlet.email = email;
    }
    public static String getNetID() {
        return netID;
    }
    public static void setNetID(String netID) {
        ApplicantServlet.netID = netID;
    }
    public static String getAUF() {
        return AUF;
    }
    public static void setAUF(String AUF) {
        ApplicantServlet.AUF = AUF;
    }
    public static String getPosition() {
        return Position;
    }
    public static void setPosition(String position) {
        Position = position;
    }
    public static String getFirst_name() {
        return first_name;
    }
    public static void setFirst_name(String first_name) {
        ApplicantServlet.first_name = first_name;
    }
    public static String getLast_name() {
        return last_name;
    }
    public static void setLast_name(String last_name) {
        ApplicantServlet.last_name = last_name;
    }

    /*
    This is the function that occurs after the submit button is clicked.
    It's linked in the above line which reads "@WebServlet("/applicantServlet")"/
    This connects the class to the ability to be a servlet. It's referenced in the application
    class.
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /*
        After the form is submitted, the values are passed as request parameters.
        Below, we get each parameter value that corresponds to an input.
        NOTE: parameters are based off the names of inputs, NOT ids
        */
        String getFirstName = request.getParameter("firstName");
        String getLastName = request.getParameter("lastName");
        String getEmail = request.getParameter("email");
        String getNetID = request.getParameter("MSU NetID");
        String getAUFProtocol = request.getParameter("AUF");
        String getPosition = request.getParameter("position");

        //Set variables
        setFirst_name(getFirstName);
        setLast_name(getLastName);
        setEmail(getEmail);
        setAUF(getAUFProtocol);
        setNetID(getNetID);
        setPosition(getPosition);
        setApproved(0);

        //Pass the set variables to the insertApplicant function in DBConnect.
        //This inserts the applicant into the SQL database.
        DBConnect.insertApplicant(getFirstName,getLastName,getEmail,getNetID,getAUFProtocol,getPosition,approved);
        //Redirect the user to the confirmation page.
        response.sendRedirect("applicationSubmission.jsp");
    }

    }
