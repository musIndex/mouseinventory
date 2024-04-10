package edu.ucsf.mousedatabase.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
//import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class AdminRoleFilter
 */
public class AdminRoleFilter implements Filter {

    /**
     * Default constructor. 
     */
    public AdminRoleFilter() {
        // TODO Auto-generated constructor stub
    }
    
    
    class UserRoleRequestWrapper extends HttpServletRequestWrapper {
   	 
  	  List<String> roles = null;
  	  HttpServletRequest realRequest;
  	   
  	  public UserRoleRequestWrapper(List<String> roles, HttpServletRequest request) {
  	    super(request);
  	    
  	    this.roles = roles;
  	    this.realRequest = request;

  	  }
  	  @Override
  	  public boolean isUserInRole(String role) {
  	    if (roles == null) {
  	      return this.realRequest.isUserInRole(role);
  	    }
  	    return roles.contains(role);
  	  }
  	}
	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		 if (request instanceof HttpServletRequest) {
	            HttpServletRequest httpRequest = (HttpServletRequest) request;
	            //HttpServletResponse httpResponse = (HttpServletResponse) response;
	            
		List<String> roles = null; 
		 roles = new ArrayList<>(Arrays.asList("administrator"));
		 chain.doFilter(new UserRoleRequestWrapper(roles, httpRequest), response);
		
	}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
