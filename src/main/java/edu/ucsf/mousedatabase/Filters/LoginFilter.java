package edu.ucsf.mousedatabase.Filters;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ucsf.mousedatabase.HTMLGeneration;
import edu.ucsf.mousedatabase.Log;

/**
 * Servlet Filter implementation class LoginFilter
 */
public class LoginFilter implements Filter {
	int indexOfCredential = 13;
	String keyName = "user_claims";
	String typeName = "http://schemas.microsoft.com/identity/claims/objectidentifier";
	String envName = "admins";

    /**
     * Default constructor. 
     */
    public LoginFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}
	
	Consumer<String> logConsumer = new Consumer<String>() {
		public void accept(String value) {
			Log.Info(value);
		}
	};

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		boolean loggedIn = false;
		
		HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
		String credential = System.getenv(envName);
		//String data = request.getAuthType();
		//Log.Info("user data is : " + data);
		String headerName = request.getHeaderNames().toString();
		Log.Info("user headerNames: " + headerName);
		//75da6698
		
        
        /*Map<String, Collection<String>> map = (Map<String, Collection<String>>) request.getUserPrincipal();
        for (Object key : map.keySet()) {
          Object value = map.get(key);
          if (value != null && value instanceof Collection) {
              Collection claims = (Collection) value;
              for (Object claim : claims) {
                  System.out.println(claims);
                  Log.Info(claims);
              }
          }
      }
        /*for (Object key : map.keySet()) {
        	if (key == keyName) {
        		Collection<String> values = map.get(key);
        		values.forEach(logConsumer);
        		
        		//String[] useableValues = (String[]) values.toArray();     		
        		
        		
        		
        		if (useableValues[indexOfCredential] == credential) { //may need to restructure this
        			loggedIn = true;
        		}
        	}
        }*/
        
        /*if(loggedIn) {
        	// pass the request along the filter chain
    		chain.doFilter(request, response);
        } else {
        	response.sendRedirect(HTMLGeneration.siteRoot + "accessDenied.jsp");
        }*/
        
        chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub

		Log.Info("Starting filter");
	}

}
