package edu.ucsf.mousedatabase.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.*;

import edu.ucsf.mousedatabase.Log;
import edu.ucsf.mousedatabase.MGIConnect;
import edu.ucsf.mousedatabase.objects.MGIResult;

/**
 * This is a JSON wrapper around the MGI data API.
 */
public class MgiDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MgiDataServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("serial")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String query = request.getParameter("query");
		
		try
		{
			if (query== null)
			{
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
				
			if (query.equals("allele_properties"))
			{
				String accIdParam = request.getParameter("acc_id");
				String expectedTypeName = request.getParameter("expected_type_name");
				if (accIdParam == null || expectedTypeName == null)
				{
					response.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}
				try
				{
					Integer.parseInt(accIdParam);
				}
				catch(Exception ex)
				
				{
					response.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}
				
				final int accessionId = Integer.parseInt(accIdParam);
				MGIResult result = null;
				if (expectedTypeName.equals("allele"))
				{
					result = MGIConnect.DoMGIAlleleQuery(Integer.toString(accessionId));
				} else if (expectedTypeName.equals("transgene")) {
					result = MGIConnect.DoMGITransgeneQuery(Integer.toString(accessionId));
				} else {
					response.getWriter().write("expected_type_name must be 'allele' or 'transgene'");
					response.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}
				
				
				Properties data;
				if (result.isValid())
				{
					HashMap<Integer,Properties> props = MGIConnect.getPropertiesFromAlleleMgiID(new ArrayList<Integer>(){{add(accessionId);}}, 0);
					data = props.get(accessionId);
					data.setProperty("is_valid", "true");
				}
				else
				{
					data = new Properties();
					data.setProperty("is_valid", "false");
					data.setProperty("error_string", result.getErrorString());
				}
				Gson gson = new Gson();
				gson.toJson(data, response.getWriter());
				response.setStatus(HttpServletResponse.SC_OK);
			}
			else
			{
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		}
		catch(Exception e)
		{
			Log.Error("Error responding to mgidataservlet request",e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
