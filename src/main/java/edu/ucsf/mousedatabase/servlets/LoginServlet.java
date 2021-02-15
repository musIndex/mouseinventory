//package edu.ucsf.mousedatabase.servlets;
//
//import edu.ucsf.mousedatabase.DBConnect;
//import edu.ucsf.mousedatabase.objects.Applicant;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Map.Entry;
//
//
//@WebServlet("/loginServlet")
//
//public class LoginServlet extends HttpServlet {
//    private static String first_name;
//    private static String last_name;
//    private static String email;
//    private static String netID;
//    private static String AUF;
//    private static String Position;
//    private static int approved;
//
//    private static int access_granted = 0;
//    public static int getAccess_granted() {
//        return access_granted;
//    }
//    public static void setAccess_granted(int access_granted) {
//        LoginServlet.access_granted = access_granted;
//    }
//
//
//
//
//    /*
//    This is the function that occurs after the submit button is clicked.
//    It's linked in the above line which reads "@WebServlet("/applicantServlet")"/
//    This connects the class to the ability to be a servlet. It's referenced in the application
//    class.
//    */
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        //After the form is submitted, the values are passed as request parameters.
//        //Below, we get each parameter value that corresponds to an input.
//        String getEmail = request.getParameter("email");
//        String getNetID = request.getParameter("MSU NetID");
//        String getPage = request.getParameter("page");
//        String sendPage = "";
//
//        //Create a map of login pages and where they should lead
//        Map<String,String> pages = new HashMap<String,String>();
//        pages.put("applicationLoginRecords.jsp","MouseReport.jsp");
//        pages.put("applicationLoginSubmit.jsp","submission.jsp");
//        pages.put("applicationLoginGenes.jsp", "GeneReport.jsp");
//        pages.put("applicationLoginSearch.jsp", "search.jsp");
//
//        //Iterate over the map to find the correct key-value pair
//        for (Map.Entry<String,String> entry : pages.entrySet()){
//            if (getPage.equals(entry.getKey())){
//                sendPage=entry.getValue();
//                break;
//            }
//        }
//
//        //Check login information. We have four variables - email, netid,
//        //where we're coming from, and where we're going if it all checks out
//        String sendToPage = checkLogin(getEmail, getNetID, getPage, sendPage);
//
//        //If sendToPage is empty, that means the data input is not in the database
//        if (sendToPage.isEmpty()) {
//            response.sendRedirect(getPage);
//        }
//        //If sendToPage==denied, then the user is in the database, but not yet approved
//        else if(sendToPage.equals("denied")){
//            response.sendRedirect(getPage);
//        }
//        //Otherwise, the login info was correct and we send them to the correct page
//        else {
//            response.sendRedirect(sendToPage);
//            setAccess_granted(1);
//        }
//    }
//
//    //Used to check the login credentials
//    private String checkLogin(String email, String net_id, String jsp_page, String sendPage) {
//        //Grab list of all applicants
//        ArrayList<Applicant> list_of_applicants = DBConnect.getAllApplicants("approved");
//        //Page that we want to send the user to
//        //Iterate over applicants/users
//        String user_email = email.toLowerCase();
//        String user_net_id = net_id.toLowerCase();
//
//        for (Applicant user: list_of_applicants){
//            //Get email, netID, and approval status.
//            String applicant_email = user.getEmail().toLowerCase();
//            String applicant_net_id = user.getNetID().toLowerCase();
//            int is_approved = user.getApproved();
//            //Check email, netID, and approval status.
//            if (user_email.equals(applicant_email) &&
//                    user_net_id.equals(applicant_net_id) &&
//                    is_approved == 1){
//                //If they all are valid, send them to the correct page.
//                return sendPage;
//            }
//            else if (user_email.equals(applicant_email) &&
//                    user_net_id.equals(applicant_net_id) &&
//                    is_approved == 0){
//                //If they all are valid, send them to the correct page.
//                return "denied";
//            }
//        }
//        return "";
//    }
//}

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


@WebServlet("/loginServlet")

public class LoginServlet extends HttpServlet {
    private static String first_name;
    private static String last_name;
    private static String email;
    private static String netID;
    private static String AUF;
    private static String Position;
    private static int approved;
    private static String getEmail = "";
    private static String getNetID = "";

    public static String getEmail() {
        return getEmail;
    }

    public static String getNetID() {
        return getNetID;
    }

    private static int access_granted = 0;
    public static int getAccess_granted() {
        return access_granted;
    }
    public static void setAccess_granted(int access_granted) {
        LoginServlet.access_granted = access_granted;
    }




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
        String sendToPage = "";

        //Create a map of login pages and where they should lead
        Map<String,String> pages = new HashMap<String,String>();
        pages.put("applicationLoginRecords.jsp","MouseReport.jsp");
        pages.put("applicationLoginSubmit.jsp","submission.jsp");
        pages.put("applicationLoginGenes.jsp", "GeneReport.jsp");
        pages.put("applicationLoginSearch.jsp", "search.jsp");
        pages.put("search_bar","search.jsp");
        pages.put("facility_search","FacilityReport.jsp");



        //Iterate over the map to find the correct key-value pair
        for (Map.Entry<String,String> entry : pages.entrySet()){
            if (getPage.equals(entry.getKey())){
                sendPage=entry.getValue();
                break;
            }
        }


        if (getPage.equals("search_bar")){
            String keyword = request.getParameter("search_terms");
            String begin = "search.jsp#searchterms=";
            String end ="&pagenum=1&search-source=search";
            response.sendRedirect(begin+keyword+end);
            setAccess_granted(1);
        }
        else if (getPage.equals("facility_search")){
            String begin = "MouseReport.jsp?facility_id=";
            String facilityID = request.getParameter("facilityid");
            response.sendRedirect(begin+facilityID);
            setAccess_granted(1);
        }
        else{
            //After the form is submitted, the values are passed as request parameters.
            //Below, we get each parameter value that corresponds to an input.
            getEmail = request.getParameter("email");
            getNetID = request.getParameter("MSU NetID");

            //Check login information. We have four variables - email, netid,
            //where we're coming from, and where we're going if it all checks out
            sendToPage = checkLogin(getEmail, getNetID, getPage, sendPage);

            //If sendToPage is empty, that means the data input is not in the database
            if (sendToPage.isEmpty()) {
                response.sendRedirect(getPage);
            }
            //If sendToPage==denied, then the user is in the database, but not yet approved
            else if(sendToPage.equals("denied")){
                response.sendRedirect(getPage);
            }
            //Otherwise, the login info was correct and we send them to the correct page
            else {
                response.sendRedirect(sendToPage);
                setAccess_granted(1);
            }
        }

    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String getPage = request.getParameter("page");

        if (getPage.equals("gene_search")){
            String orderby = request.getParameter("orderby");
            String begin = "GeneReport.jsp?orderby=";
            String end ="&";
            response.sendRedirect(begin+orderby+end);
            setAccess_granted(1);
        }
        else if (getPage.equals("records_search")){

            //--------------------------------------------------------------------------------------

            //-------------------------------------------------------------------------------------
            String orderby = "orderby="+ request.getParameter("orderby") + "&";
            String category = "mousetype_id=" + request.getParameter("mousetype_id")+"&";
            String creonly = "creonly="+request.getParameter("creonly")+"&";
            String page_num = "pagenum="+request.getParameter("pagenum");
            String limit = "limit="+request.getParameter("limit")+"&";
            String species = "is_rat"+request.getParameter("species")+"&";
            System.out.println(species);
            String begin = "MouseReport.jsp?";
            String end ="&";
            if (creonly.equals("creonly=1&")){
                response.sendRedirect(begin+orderby+category+creonly+limit+species+page_num+end);
                setAccess_granted(1);
            }
            else{
                response.sendRedirect(begin+orderby+category+limit+species+page_num+end);
                setAccess_granted(1);
            }
        }
    }

    //Used to check the login credentials
    private String checkLogin(String email, String net_id, String jsp_page, String sendPage) {
        //Grab list of all applicants
        ArrayList<Applicant> list_of_applicants = DBConnect.getAllApplicants("approved");
        //Page that we want to send the user to
        //Iterate over applicants/users
        String user_email = email.toLowerCase();
        String user_net_id = net_id.toLowerCase();

        for (Applicant user: list_of_applicants){
            //Get email, netID, and approval status.
            String applicant_email = user.getEmail().toLowerCase();
            String applicant_net_id = user.getNetID().toLowerCase();
            int is_approved = user.getApproved();
            //Check email, netID, and approval status.
            if (user_email.equals(applicant_email) &&
                    user_net_id.equals(applicant_net_id) &&
                    is_approved == 1){
                //If they all are valid, send them to the correct page.
                return sendPage;
            }
            else if (user_email.equals(applicant_email) &&
                    user_net_id.equals(applicant_net_id) &&
                    is_approved == 0){
                //If they all are valid, send them to the correct page.
                return "denied";
            }
        }
        return "";
    }
}