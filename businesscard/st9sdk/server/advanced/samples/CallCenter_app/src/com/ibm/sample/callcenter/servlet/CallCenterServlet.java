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
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.sample.callcenter.manager.CallRecordManagerBean;
import com.ibm.sample.callcenter.manager.RestApiManagerBean;
import com.ibm.sample.callcenter.model.Constants;
import com.ibm.sample.callcenter.model.PersistentChatBean;

public class CallCenterServlet extends HttpServlet {
	
	private static final long serialVersionUID = 2412200716593322125L;

	private static String LOGON_PAGE = "/logon.jsp";
	
	private static String HOME_PAGE = "/index.jsp";
	
	private static String CUSTOMERS_PAGE = "/customers.jsp";
	
	private static String TECHNICAL_AREA_PAGE = "/technicalArea.jsp";
	
	private static String SEARCH_PAGE = "/search.jsp";
	
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

		// Create CallRecordManagerBean
		CallRecordManagerBean manager = new CallRecordManagerBean();
		
		// Servlet context object needed to forward control to another page
		ServletContext context = getServletContext();
		
		// Get the doAction parameter from the request
		String doAction = request.getParameter("doAction");

		if(null!=doAction)
		{
			if("logout".equals(doAction)) {
				request.getSession().setAttribute("USER", null);

				RequestDispatcher dispatcher = context.getRequestDispatcher(LOGON_PAGE);
				dispatcher.forward(request, response);
			}

			else if("closeCall".equals(doAction)) {

				// Get the call number
				String callNumberParam = request.getParameter("callNumberHidden");
				int callNumber = Integer.parseInt(callNumberParam);

				// close the call (i.e. change status to 0) represented by the call number
				manager.updateCallStatus(callNumber, Constants.CALL_STATUS_CLOSED);

				// redirect to the home page
				RequestDispatcher dispatcher = context.getRequestDispatcher(HOME_PAGE);
				dispatcher.forward(request, response);
			}
			
			else if("reopenCall".equals(doAction)) {
								
				// Get the call number
				String reopenCallNumberParam = request.getParameter("reopenCallNumberHidden");
				int callNumber = Integer.parseInt(reopenCallNumberParam);
				
				// reopen the call (i.e. change status to 1) represented by the call number
				manager.updateCallStatus(callNumber, Constants.CALL_STATUS_OPEN);

				// redirect to the home page
				RequestDispatcher dispatcher = context.getRequestDispatcher(HOME_PAGE);
				dispatcher.forward(request, response);
			}
			
			else if("createCustomer".equals(doAction)) {

				// Get the customer name
				String customerName = request.getParameter("customerName");

				manager.insertCustomer(customerName);

				// redirect to the home page
				RequestDispatcher dispatcher = context.getRequestDispatcher(CUSTOMERS_PAGE);
				dispatcher.forward(request, response);
			}

			else if("createTechnicalArea".equals(doAction)) {

				// Get the technicalArea
				String technicalArea = request.getParameter("technicalArea");

				manager.insertTechnicalArea(technicalArea);

				// redirect to the home page
				RequestDispatcher dispatcher = context.getRequestDispatcher(TECHNICAL_AREA_PAGE);
				dispatcher.forward(request, response);
			}	

			/*
			 * Calls related to the Rest API
			 */

			// Get the LDAP username and password from the session
			String username = (String) request.getSession().getAttribute("USER");
			String password = (String) request.getSession().getAttribute("PASSWORD");

			String baseUrl = "http://"+request.getLocalName()+":"+request.getLocalPort();

			RestApiManagerBean restApi = new RestApiManagerBean(baseUrl, username, password);

			/* 
			 * Create a new call by taking the following steps
			 * 1. Create Customer and TechnicalArea folders if they don't already exist.
			 * 2. Create a folder, chat room and a community for the Customer and Technical area if they don't already exist.
			 * 3. Create a new call record in the database.
			 */
			if("createNewCall".equals(doAction)) {
				
				// Get the customer name
				String customerNameParam = request.getParameter("customer_name");
				int customerNameInt = Integer.parseInt(customerNameParam);
				String customerName = manager.getCustomerName(customerNameInt);

				// Get the technical area
				String technicalAreaParam = request.getParameter("technical_area");
				int technicalAreaInt = Integer.parseInt(technicalAreaParam);
				String technicalArea = manager.getTechnicalAreaName(technicalAreaInt);
				
				// Get problem description
				String pbDesc = request.getParameter("problem_desc");

				// For folder creation, please make sre that the root folder '/ChatRooms' has manager permissions to all authenticated users.
				
				// Create a Customer folder which will have path '/ChatRooms/Customer' 
				restApi.createNewFolder("Customer");
				// Create a TechnicalArea folder which will have path '/ChatRooms/TechnicalArea'
				restApi.createNewFolder("TechnicalArea");
				
				// If customer name was 'Zeta Bank' then a new folder is created on the path '/ChatRooms/Customer/Zeta_Bank' and will contain
				// a chat room called Zeta Bank. Similarly a 'Server Crash' chat room and folder is created on '/ChatRooms/TechnicalArea/Server_Crash'
				
				//EXCEPTION
				PersistentChatBean customerChat = restApi.createChatRoom(customerName, "Customer");
				
				//EXCEPTION
				PersistentChatBean techAreaChat = restApi.createChatRoom(technicalArea, "TechnicalArea");

				// get current time for recording the call
				long millis = System.currentTimeMillis();

				// add a call having an Open status
				manager.insertCall(customerNameInt, technicalAreaInt, pbDesc, 
						Constants.CALL_STATUS_OPEN, new Timestamp(millis), customerChat, techAreaChat, username);
				// redirect to the home page
				RequestDispatcher dispatcher = context.getRequestDispatcher(HOME_PAGE);
				dispatcher.forward(request, response);
			}
			
			else if("removeCustomer".equals(doAction)){
				String custIdParam = request.getParameter("custRadio");
				int custId = Integer.parseInt(custIdParam);
				
				String hiddenRemoveSToptions = request.getParameter("hiddenRemoveSToptions");
				if("true".equals(hiddenRemoveSToptions)) {
					
					// get customer name
					String customerName = manager.getCustomerName(custId);
					
					//delete chat room, chat folder and comunity
					restApi.deleteChatRoom(customerName, "Customer");
				}
				
				// now delete the customer
				manager.deleteCustomer(custId);
				
				// redirect to the customers page
				RequestDispatcher dispatcher = context.getRequestDispatcher(CUSTOMERS_PAGE);
				dispatcher.forward(request, response);
			}
			
			else if("removeTechnicalArea".equals(doAction)){
				String techAreaIdParam = request.getParameter("techRadio");
				int techAreaId = Integer.parseInt(techAreaIdParam);
				
				String hiddenRemoveSToptions = request.getParameter("hiddenRemoveSToptions");
				if("true".equals(hiddenRemoveSToptions)) {
					
					// get technical area name
					String technicalAreaName = manager.getTechnicalAreaName(techAreaId);
					
					//delete chat room, chat folder and comunity
					restApi.deleteChatRoom(technicalAreaName, "TechnicalArea");
				}
				
				// now delete the technical area
				manager.deleteTechnicalArea(techAreaId);
				
				// redirect to the technical area page
				RequestDispatcher dispatcher = context.getRequestDispatcher(TECHNICAL_AREA_PAGE);
				dispatcher.forward(request, response);
			}
			
			// post question to a community
			else if("postQuestion".equals(doAction)) {

				// Get id of the comunity which is the same as Customer or Technical Area id in the database
				String communityName = request.getParameter("communityName");
				// Get type which can be Customer or Technical Area
				String type = request.getParameter("type");
				// Get a question string 
				String question = request.getParameter("questionInput");
				
				// depending on the type determine community name
				if("Customer".equals(type))
					communityName = "Cust"+communityName;
				else
					communityName = "TArea"+communityName;

				restApi.postQuestion(communityName, question);

				// redirect to the home page
				RequestDispatcher dispatcher = context.getRequestDispatcher(HOME_PAGE);
				dispatcher.forward(request, response);
			}	
		
			// rate a response of a question
			else if("rateResponse".equals(doAction)) {

				//	Get id of the comunity which is the same as Customer or Technical Area id in the database
				String communityName = request.getParameter("communityName");
				// Get type which can be Customer or Technical Area
				String type = request.getParameter("type");
				// Get the id of the response
				String reqIdParam = request.getParameter("reqId");
				int reqId = Integer.parseInt(reqIdParam);
				// Get the rating which will be an integer from 1 to 5
				String ratingParam = request.getParameter("rating");
				int rating = Integer.parseInt(ratingParam);

				// depending on the type determine community name
				if(type.equalsIgnoreCase("customer"))
					communityName = "Cust"+communityName;
				else
					communityName = "TArea"+communityName;

				restApi.rateResponse(communityName, reqId, rating);

				// Return a response which will close the browser pop up window
				PrintWriter out = response.getWriter();
				out.println("<script type=\"text/javascript\" language=\"JavaScript\">");
				out.println("self.close();");
				out.println("</script>");

			}
		
			// Perform search
			else if("doSearch".equals(doAction)) {

				// Passing request object so that all results are stored in a session
				restApi.search(request);

				// redirect to the search page
				RequestDispatcher dispatcher = context.getRequestDispatcher(SEARCH_PAGE);
				dispatcher.forward(request, response);
			}
		
			// clear search
			else if("clearSearch".equals(doAction)) {

				// if search was performed before then there will be a list of objects stored in the
				// session. Clean up will set it to null
				request.getSession().setAttribute(Constants.SEARCH_RESULTS, null);

				// redirect to the search page
				RequestDispatcher dispatcher = context.getRequestDispatcher(SEARCH_PAGE);
				dispatcher.forward(request, response);
			}
		
			// delete chat faq
			else if("deleteFaq".equals(doAction)) {

				// Get the chat faq Id which will be a complete URL
				String faqId = request.getParameter("faqId");
				// Get the actual faqId which will appear after the last forward slash
				String[] parsedId = faqId.split("/");
				faqId = parsedId[parsedId.length - 1];
				
				// delete the faq
				restApi.deleteChatFaq(faqId); 
				
				//do a cleanup from search results
				List searchResults = (List)request.getSession().getAttribute(Constants.SEARCH_RESULTS);
				if(null!=searchResults) {
					restApi.updateSearchResults(searchResults, faqId);
				}				

				// redirect to the search page
				RequestDispatcher dispatcher = context.getRequestDispatcher(SEARCH_PAGE);
				dispatcher.forward(request, response);
			}
		}
		else
			throw new ServletException();
	}

	// No need to destroy anything
	public void destroy() {
	}
}
