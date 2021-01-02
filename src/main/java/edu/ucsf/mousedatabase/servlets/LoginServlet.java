package edu.ucsf.mousedatabase.servlets;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.objects.Applicant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;


@WebServlet("/loginServlet")

public class LoginServlet extends HttpServlet {
    private static String first_name;
    private static String last_name;
    private static String email;
    private static String netID;
    private static String AUF;
    private static String Position;
    private static int approved;


    /*
    This is the function that occurs after the submit button is clicked.
    It's linked in the above line which reads "@WebServlet("/applicantServlet")"/
    This connects the class to the ability to be a servlet. It's referenced in the application
    class.
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //After the form is submitted, the values are passed as request parameters.
        //Below, we get each parameter value that corresponds to an input.
        String getEmail = request.getParameter("email");
        String getNetID = request.getParameter("MSU NetID");
        String getPage = request.getParameter("page");
        String sendPage = "";

        //Create a map of login pages and where they should lead
        Map<String,String> pages = new HashMap<String,String>();
        pages.put("applicationLoginRecords.jsp","MouseReport.jsp");
        pages.put("applicationLoginSubmit.jsp","submission.jsp");

        //Iterate over the map to find the correct key-value pair
        for (Map.Entry<String,String> entry : pages.entrySet()){
            if (getPage.equals(entry.getKey())){
                sendPage=entry.getValue();
                break;
            }
        }

        //Check login information. We have four variables - email, netid,
        //where we're coming from, and where we're going if it all checks out
        String sendToPage = checkLogin(getEmail, getNetID, getPage, sendPage);

        //If sendToPage is empty, that means the data input is not in the database
        if (sendToPage.isEmpty()) {
            response.sendError(4560, "Wrong login information.");
        }
        //If sendToPage==denied, then the user is in the database, but not yet approved
        else if(sendToPage.equals("denied")){
            response.sendError(4561, "You've submitted an application, but you have not yet been approved.");
        }
        //Otherwise, the login info was correct and we send them to the correct page
        else {
            response.sendRedirect(sendToPage);
        }
    }

    //Used to check the login credentials
    private String checkLogin(String email, String net_id, String jsp_page, String sendPage) {
        //Grab list of all applicants
        ArrayList<Applicant> list_of_applicants = DBConnect.getAllApplicants("approved");
        //Page that we want to send the user to
        //Iterate over applicants/users
        for (Applicant user: list_of_applicants){
            //Get email, netID, and approval status.
            String user_email = email.toLowerCase();
            String user_net_id = net_id.toLowerCase();
            int is_approved = user.getApproved();
            //Check email, netID, and approval status.
            if (user_email.equals(user.getEmail().toLowerCase()) &&
                    user_net_id.equals(user.getNetID().toLowerCase()) &&
                    is_approved == 1){
                //If they all are valid, send them to the correct page.
                return sendPage;
            }
            else if (user_email.equals(user.getEmail().toLowerCase()) &&
                    user_net_id.equals(user.getNetID().toLowerCase()) &&
                    is_approved == 0){
                //If they all are valid, send them to the correct page.
                return "denied";
            }
        }
        return "";
    }
}