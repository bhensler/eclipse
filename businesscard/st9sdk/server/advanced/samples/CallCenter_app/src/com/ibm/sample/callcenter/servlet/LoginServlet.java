/* ***************************************************************** */
/*                                                                   */
/* Licensed Materials - Property of IBM                              */
/*                                                                   */
/* 5724-E76, 5724-L21, 5724-i67, 5724-L64, 5655-M44                  */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2014  All Rights Reserved.           */
/*                                                                   */
/* US Government Users Restricted Rights - Use, duplication or       */
/* disclosure restricted by GSA ADP Schedule Contract with           */
/* IBM Corp.                                                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.sample.callcenter.servlet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.security.util.Base64Coder;
import com.ibm.sample.callcenter.model.Constants;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 2412200713360118136L;

	private static String HOME_PAGE = "/index.jsp";
	private static String ERROR_PAGE = "/logon.jsp?authentication=false";

	public void init(ServletConfig config) throws ServletException {

		super.init(config);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// If it is a get request forward to doPost()
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// Forward the request to the target named
		ServletContext context = getServletContext();

		// Get the username from the request
		String username = request.getParameter("username");
		// Get the password from the request
		String password = request.getParameter("password");
		
		// Add the LDAP username and password used to log in to the request
		request.getSession().setAttribute("USER", username);
		request.getSession().setAttribute("PASSWORD", password);
		
		String baseUrl = "http://"+request.getLocalName()+":"+request.getLocalPort();
		
		// Check if the user has permission to use Sametime Advanced 8.0 rest apis
		URL testURI = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.USER_FOLDER_URL + "/"+Constants.GLOBAL_ROOT_FOLDER);		
		
		HttpURLConnection connection = (HttpURLConnection)testURI.openConnection();
        String encodedAuthData = Base64Coder.base64Encode(request.getSession().getAttribute("USER") + ":" + request.getSession().getAttribute("PASSWORD"));
        
        connection.setRequestProperty("Authorization", "Basic " + encodedAuthData);
        connection.setRequestMethod("GET");
        connection.setAllowUserInteraction(true);
        connection.setUseCaches(false);
        connection.connect();

        // If no success then redirect to the logon page
        if (connection.getResponseCode() != 200)
        {
        	request.getSession().setAttribute("USER", null);
        	RequestDispatcher dispatcher = context.getRequestDispatcher(ERROR_PAGE);
    		dispatcher.forward(request, response);
        }
        else
        {
				
		// Forward the request to the home page
		RequestDispatcher dispatcher = context.getRequestDispatcher(HOME_PAGE);
		dispatcher.forward(request, response);
        }
	}

	// no need to destroy anything
	public void destroy() {
	}
}
