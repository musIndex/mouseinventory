package edu.ucsf.mousedatabase.servlets;
import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.objects.Applicant;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class StatusServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    //In order for this to be an admin servlet, you NEED to have the following in the code
    //*****This constructor
    public StatusServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    //*****And this @see statement/
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */

    //If you don't have the above in your servlet, then it won't be visible to the admin page.
    //For a typical servlet that isn't dealing with admin information, you don't need these.
    //However if you are in admin information and the host can't find your code/servlet,
    //be sure to include the above statements (of course customized to your own servlet).

    //Function that is triggered after hitting the submit button
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //Get data from the input
        String getFirstName = request.getParameter("first_name");
        String getLastName = request.getParameter("last_name");
        String getEmail = request.getParameter("email");
        String getNetID = request.getParameter("net_id");
        String getAUFProtocol = request.getParameter("auf");
        String getPosition = request.getParameter("position");
        String getApproved = request.getParameter("approved");
        String getId = request.getParameter("identity");

        //Change the approval of a given user
        changeApproval(getId,getFirstName,getLastName,getEmail,getNetID,getAUFProtocol,getPosition,getApproved);
        //Reload the page
        //response.sendRedirect("applicationsList.jsp");
    }

    //Sends a trigger to DBConnect to change user approval status
    private void changeApproval(String id, String first_name, String last_name, String email, String net_id,
                                String auf, String position, String approval){

        //Grab all applicants
        ArrayList<Applicant> list_of_applicants = DBConnect.getAllApplicants("approved");
        //Iterate over each applicant
        for (Applicant user: list_of_applicants){
            //If the given id matches the user that is currently being iterated over
            if (Integer.parseInt(id) == user.getId()){
                //If approval is 0
                if (Integer.parseInt(approval) == 0){
                    //Change approval to 1
                    DBConnect.updateApplicantApproval(user,0);
                }
                //If the approval is 1
                else {
                    //Change approval to 0
                    DBConnect.updateApplicantApproval(user,1);
                }
            }
        }

    }

}

