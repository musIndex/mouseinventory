package edu.ucsf.mousedatabase.filters;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import javax.servlet.http.HttpServletRequestWrapper;

import edu.ucsf.mousedatabase.Log;

public class BasicFilter implements Filter {

    private List<String> adminList;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            Log.Info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            String host;
            try {
                host = new URI(httpRequest.getRequestURL().toString()).getHost();

                if (host.equals("localhost")) {
                    Log.Info("Access attempt on localhost... allowing.");
                    chain.doFilter(request, response);
                    return;
                }
                String userName = httpRequest.getHeader("X-MS-CLIENT-PRINCIPAL-NAME");
                String userId = httpRequest.getHeader("X-MS-CLIENT-PRINCIPAL-ID");

                Log.Info("Access attempt by <" + userName + ">, oid " + userId); 
                if (userId.isEmpty()) {
                    // No user info. Redirect to login.
                    httpResponse.sendRedirect("https://" + host + "/.auth/login/aad");
                    return;
                }

                if (adminList.contains(userId)) {
                    Log.Info("User is an admin. Allow access.");
                   
                    chain.doFilter(request, response);
                    return;
                }
                httpRequest.getRequestDispatcher("/accessDenied.jsp").forward(httpRequest, httpResponse);
                return;
            } catch (URISyntaxException error) {
                error.printStackTrace();
            }
        }
    }
   
    
    public void init(FilterConfig config) throws ServletException {
        adminList = Arrays.asList(System.getenv("ADMINISTRATOR_IDS").split(","));
        Log.Info("System env admins: " + System.getenv("ADMINISTRATOR_IDS"));
    }

    public void destroy() {
    }
}
