package edu.ucsf.mousedatabase.servlets;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.objects.Applicant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;


@WebServlet("/loggedInServlet")

public class LoggedInServlet extends HttpServlet {


    /*
    This is the function that occurs after the submit button is clicked.
    It's linked in the above line which reads "@WebServlet("/applicantServlet")"/
    This connects the class to the ability to be a servlet. It's referenced in the application
    class.
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String getPage = request.getParameter("page");
        String sendPage = "";

        if (getPage.equals("logout")){
            response.sendRedirect("about.jsp");
            LoginServlet.setAccess_granted(0);
        }
        //Create a map of login pages and where they should lead
        Map<String, String> pages = new HashMap<String, String>();
        pages.put("loggedIn_Submit Rodents","submission.jsp");
        pages.put("loggedIn_Registration","application.jsp");
        pages.put("loggedIn_Gene List","GeneReport.jsp");
        pages.put("loggedIn_Facility List","FacilityReport.jsp");
        pages.put("loggedIn_Holder List","HolderReport.jsp");
        pages.put("loggedIn_About","history.jsp");
        pages.put("loggedIn_Rodent Records","MouseReport.jsp");
        pages.put("submit_another","submission.jsp");
        pages.put("go_home","about.jsp");
        pages.put("contact","contact.jsp");

        //Iterate over the map to find the correct key-value pair
        for (Map.Entry<String, String> entry : pages.entrySet()) {
            if (getPage.equals(entry.getKey())) {
                sendPage = entry.getValue();
                response.sendRedirect(sendPage);
                LoginServlet.setAccess_granted(1);
                break;
            }
        }
    }
}

