/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* (C) Copyright IBM Corp. 2011                                      */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */


/*
 * Licensed Materials - Property of IBM
 *
 * L-MCOS-96LQPJ
 *
 * (C) Copyright IBM Corp. 2013. All rights reserved.
 *
 * US Government Users Restricted Rights- Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */


package com.userinfo.demobb;

import com.ibm.sametime.userinfo.userInfobbapi.UserInfoBlackBoxAPI;
import com.ibm.sametime.userinfo.userInfobbapi.RequestContext;
import com.ibm.sametime.userinfo.userInfobbapi.Response;
import com.ibm.sametime.userinfo.userInfobbapi.UserInfoException;
import com.ibm.sametime.userinfo.userInfobbapi.DetailItem;






/** 
 * Implementation for a Demo Black Box.
 * This implementation uses hard coded strings  as values for
 * the requested details.
 *  
 * Please attach the UserInfo.jar supplied in the installation as an
 * External jar in order to compile this sample.
 */
public class UserInfoDemoBB implements UserInfoBlackBoxAPI {

	
	
	public  UserInfoDemoBB(){
		
	}
	
	
	/**
	 *  initialization stage. Inits mambers , connection etc
	 */
	public void init() throws UserInfoException {
		System.out.println("Initializing Demo black box");

	}

	/**
	 * Handles a request for userdetails
	 * @param the request object that containd information about requested 
	 * details 
	 * @return a Response object that has been populated with values for the 
	 * requested details if such values exist
	 */
	public Response processRequest(RequestContext req) throws UserInfoException {
		Response response = null;
		if (req == null) {
			throw new UserInfoException("Invalid request - null value");
		}

		response = new Response();
		for (int j = 0; j < req.getReqDetails().length; j++) {
			String detailName = req.getReqDetails()[j];
			String val = getTextValue(detailName);
			if (val != null) {
				System.out.println("found value for " + detailName);
				DetailItem item = createNewItem(detailName, val); 
				response.setRetrievedDetail(item.getId(), item);
			}
		}
		if (response.getNumOfRetrievedDetails() > 0) {
			response.setUserFound(true);
		} else {
			System.out.println("No available data found for requested details");
		}

		return response;
	}

	
	
	
	/**
	 * 
	 * @param detailName the requested detail
	 * @return the value of that matches the detail that was requested
	 * or null if this detail name does not exist
	 * 
	 */
	private String getTextValue(String detailName){
		
		String answer=null;
		if(detailName.toLowerCase().equals("name")){
			answer="Demo Name";
		}
		else if(detailName.toLowerCase().equals("title")){
			answer="Demo Title";
		}
		else if(detailName.toLowerCase().equals("telephone")){
			answer="Demo Phone";
		}
		else if(detailName.toLowerCase().equals("location")){
			answer="Demo Location";
		}
		else if(detailName.toLowerCase().equals("mailaddress")){
			answer="demo@mail.com";
		}
		
		return answer;
	}
	
	
	
	
	
	
	/**
	 * Creates a new detailItem object and stores the values in the 
	 * appropriate fields.
	 * @param detailName the requested detail name
	 * @param value the fetched value
	 * @return
	 * DetailItem a new  object that stores the given information
	 */
	private DetailItem createNewItem(String detailName, String value){
		DetailItem respItem= new DetailItem();
		respItem.setId(detailName);
		respItem.setType("text/plain");
		respItem.setTextValue(value);
		return respItem;
	}
	
	
	
	
	
	
	
	/**
	 * terminating the black box operation.
	 * closes connection etc
	 */
	public void terminate() {
		System.out.println("Terminating Demo black box");
	}
	
	
	
	
		
			
		
	

}
