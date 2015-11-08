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

package com.ibm.sample.callcenter.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import com.ibm.sample.callcenter.model.Constants;
import com.ibm.ws.security.util.Base64Coder;

public class CommunityApiUtil {
	
	public static boolean isCommunityExisting(String communityName, String baseUrl, String username, String password)
	throws ProtocolException, IOException{
		
		String encodedAuthData = Base64Coder.base64Encode(username + ":" + password);
		URL requestURL = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.COMMUNITY_URL+ "/"+ communityName);
		HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
		connection.setRequestProperty("Authorization", "Basic " + encodedAuthData);
		connection.setRequestMethod("GET"); 
		connection.setAllowUserInteraction(true);
		connection.setUseCaches(false);
		connection.connect();
		int responseCode = connection.getResponseCode();
		System.out.println("responseCode : "+responseCode);
		if(responseCode == 200)
			return true;
		
		return false;		
	}
	
	public static boolean createNewCommunity(String communityName, String baseUrl, String username, String password)
	throws ProtocolException, IOException{
		
		String encodedAuthData = Base64Coder.base64Encode(username + ":" + password);
		URL requestURL = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.COMMUNITY_URL+ "/"+ communityName+"?type=open&managers="+username);
		HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
		connection.setRequestProperty("Authorization", "Basic " + encodedAuthData);	
		
		connection.setRequestMethod("POST"); 
		connection.setAllowUserInteraction(true);
		connection.setUseCaches(false);
		connection.connect();
		int responseCode = connection.getResponseCode();
		
		if(responseCode == 200)
			return true;
		
		return false;		
	}
	
	public static boolean deleteCommunity(String communityName, String baseUrl, String username, String password)
	throws ProtocolException, IOException{
		
		String encodedAuthData = Base64Coder.base64Encode(username + ":" + password);
		URL requestURL = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.COMMUNITY_URL+ "/"+ communityName);
		HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
		connection.setRequestProperty("Authorization", "Basic " + encodedAuthData);
		connection.setRequestMethod("DELETE"); 
		connection.setAllowUserInteraction(true);
		connection.setUseCaches(false);
		connection.connect();
		int responseCode = connection.getResponseCode();
		
		if(responseCode == 200)
			return true;
		
		return false;		
	}
}
