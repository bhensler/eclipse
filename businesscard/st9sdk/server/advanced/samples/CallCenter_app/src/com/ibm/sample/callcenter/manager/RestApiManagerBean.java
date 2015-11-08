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

package com.ibm.sample.callcenter.manager;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


import com.ibm.sample.callcenter.util.ChatFaqApiUtil;
import com.ibm.sample.callcenter.util.CommunityApiUtil;
import com.ibm.sample.callcenter.util.GlobalFolderApiUtil;
import com.ibm.sample.callcenter.util.PersistentChatApiUtil;
import com.ibm.sample.callcenter.util.SearchApiUtil;
import com.ibm.sample.callcenter.util.SkillTapApiUtil;
import com.ibm.sample.callcenter.model.Constants;
import com.ibm.sample.callcenter.model.PersistentChatBean;
import com.ibm.sample.callcenter.model.SearchResultBean;

public class RestApiManagerBean {
	
	private String baseUrl;
	private String username;
	private String password;
	
	public RestApiManagerBean() {
	}
	
	public RestApiManagerBean(String baseUrl, String username, String password) {
		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void createNewFolder(String folderName) {
		try {
			// Check if a folder with the given name already exists, if it doesn't then create a new one. The new
			// folder will be created under the root folder i.e. ChatRooms
			boolean folderExists = GlobalFolderApiUtil.isFolderExisting(folderName, Constants.GLOBAL_ROOT_FOLDER, baseUrl, username, password);
			if(!folderExists) {
				GlobalFolderApiUtil.createNewFolder(folderName, Constants.GLOBAL_ROOT_FOLDER, baseUrl, username, password);
			}

		}catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * A new chat room will be created inside a folder having the same name and the folder will in turn be placed under the
	 * Customer or TechinicalArea folder depending on the folderType. 
	 * @param chatRoomName
	 * @param folderType
	 * @return chatId
	 */
	
	public PersistentChatBean createChatRoom(String chatRoomName, String folderType) {
		
		PersistentChatBean persistentChatBean = null;
		
		try {
			
			//Step 1: Create Foler
			
			// Check if a folder with the given name already exists, if it doesn't then create a new one. The new
			// folder will be created under the root folder i.e. ChatRooms
			String folderPath = Constants.GLOBAL_ROOT_FOLDER;
			if("Customer".equals(folderType))
				folderPath += "/Customer";
			else if("TechnicalArea".equals(folderType))
				folderPath += "/TechnicalArea";
			else
				return persistentChatBean;
			
			// replacing whitespaces with underscore in chat room name so that it can be passed across the url
			chatRoomName = chatRoomName.replaceAll(" ", ".");
			boolean folderExists = GlobalFolderApiUtil.isFolderExisting(chatRoomName, folderPath, baseUrl, username, password);
			
			if(!folderExists) {
				GlobalFolderApiUtil.createNewFolder(chatRoomName, folderPath, baseUrl, username, password);
			}
			
			//Step 2: Create Chat Room and store it within the folder 

			// The title text for the chat room
			String titleText = "This chat room has been created using IBM Sametime Advanced 8.0 API for the purpose of tracking all conversations related to the ";
			
			// Set folderpath until the newly created folder
			if("Customer".equals(folderType)) {
				folderPath = "Customer/"+chatRoomName;
				titleText += "customer "+chatRoomName;
			}
			else if("TechnicalArea".equals(folderType)) {
				folderPath = "TechnicalArea/"+chatRoomName;
				titleText += "technical area "+chatRoomName;
			}
			
			String globalfolderPath = Constants.GLOBAL_ROOT_FOLDER+"/"+folderPath;
			boolean chatRoomExists = GlobalFolderApiUtil.isChatRoomExistingInfolder(chatRoomName, globalfolderPath, baseUrl, username, password);
			if(!chatRoomExists) {
				persistentChatBean = PersistentChatApiUtil.createChatRoom(chatRoomName, folderPath, baseUrl, username, password);
				//replaces whitespaces with %20 so that it can be passed using the url
				titleText = titleText.replaceAll(" ", "%20");
				// add some description of the chat room inside the chat transcript
			} else {
				persistentChatBean = GlobalFolderApiUtil.getPersistentChatFromFolder(chatRoomName, globalfolderPath, baseUrl, username, password);
			}
			
			//Step 3: Create community for the chat room
			
			
			//if(!CommunityApiUtil.isCommunityExisting(chatRoomName, baseUrl, username, password))
				{
					CommunityApiUtil.createNewCommunity(chatRoomName, baseUrl, username, password);
					}
			
		}catch (Exception e) {
			e.printStackTrace();
		}			
		
		return persistentChatBean;
	}
	
	/**
	 * deletes chat room, chat folder and community for the passed chatRoomName paramter
	 * @param chatRoomName
	 */
	public void deleteChatRoom(String chatRoomName, String folderType) {

		try {
			// Step 1: delete folder, which will also delete the chat room within it
			String folderPath = Constants.GLOBAL_ROOT_FOLDER;
			if("Customer".equals(folderType))
				folderPath += "/Customer";
			else
				folderPath += "/TechnicalArea";

			// replacing whitespaces with underscore in chat room name so that it can be passed across the url
			chatRoomName = chatRoomName.replaceAll(" ", "_");
			boolean folderExists = GlobalFolderApiUtil.isFolderExisting(chatRoomName, folderPath, baseUrl, username, password);
			if(folderExists) {
				GlobalFolderApiUtil.deleteFolder(chatRoomName, folderPath, baseUrl, username, password);
			}
			
			// step 2: delete community
			if(CommunityApiUtil.isCommunityExisting(chatRoomName, baseUrl, username, password))
				CommunityApiUtil.deleteCommunity(chatRoomName, baseUrl, username, password);

		}catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Post question to a community
	 * @param communityName
	 */
	public void postQuestion(String communityName, String question) {
		
		try {		
			SkillTapApiUtil.postQuestion(communityName, question, baseUrl, username, password);
		}catch (Exception e) {
			e.printStackTrace();
		}				
	}
	
	public List getSkillTapForCommunity(String communityName, String type) {
		
		if("Customer".equalsIgnoreCase(type))
			communityName = "Cust"+communityName;
		else
			communityName = "TArea"+communityName;
		
		try {		
			return SkillTapApiUtil.getSkillTapForCommunity(communityName, baseUrl, username, password);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean rateResponse(String communityName, int reqId, int rating) {
		
		try {		
			return SkillTapApiUtil.rateResponse(communityName, reqId, rating, baseUrl, username, password);
		}catch (Exception e) {
			e.printStackTrace();
		}	
		return false;
	}
	
	public boolean search(HttpServletRequest request) {

		try {		
			return SearchApiUtil.doSearch(request);
		}catch (Exception e) {
			e.printStackTrace();
		}	
		return false;
	}
	
	public boolean deleteChatFaq(String faqId) {

		try {		
			return ChatFaqApiUtil.deleteFaq(faqId, baseUrl, username, password);
		}catch (Exception e) {
			e.printStackTrace();
		}	
		return false;
	}
	
	public void updateSearchResults(List searchResults, String faqId) {
		Iterator it = searchResults.iterator();
		while(it.hasNext()) {
			SearchResultBean bean = (SearchResultBean) it.next();
			if(faqId.equals(bean.getId())) {
				it.remove();
				break;
			}
		}		
	}
	
}
