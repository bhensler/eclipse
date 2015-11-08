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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.sample.callcenter.model.Constants;
import com.ibm.sample.callcenter.model.DomAtomParser;
import com.ibm.sample.callcenter.model.PersistentChatBean;
import com.ibm.ws.security.util.Base64Coder;

public class GlobalFolderApiUtil {

	public static boolean isFolderExisting(String folderName, String folderPath, String baseUrl, String username, String password)
	throws ParserConfigurationException, SAXException, IOException{
		
		String encodedAuthData = Base64Coder.base64Encode(username + ":" + password);
		URL requestURL = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.GLOBAL_FOLDER_URL + "/"+folderPath);
		HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
		connection.setRequestProperty("Authorization", "Basic " + encodedAuthData);
		connection.setRequestMethod("GET"); 
		connection.setAllowUserInteraction(true);
		connection.setUseCaches(false);
		connection.connect();

		boolean exists = false;

		int responseCode = connection.getResponseCode();
		if(responseCode == 200) {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document document = docBuilder.parse(connection.getInputStream());

			Element elements = document.getDocumentElement();
			NodeList entries = elements.getElementsByTagName("entry");

			//Check if this folder exists
			for( int i=0; i<entries.getLength(); i++ ) {
				Element category = DomAtomParser.getFirstElement((Element)entries.item(i), "category");
				String term = DomAtomParser.getAttributeValue(category, "term");
				if(term.equals("Folder")){
					String title = DomAtomParser.getElementData((Element)entries.item(i), "title");
					if(title.equalsIgnoreCase(folderName)) {
						exists = true;				
						break;
					}
				}
			}
		}
		return exists;
	}

	public static boolean createNewFolder(String folderName, String folderPath, String baseUrl, String username, String password)
	throws ProtocolException, IOException{
		
		String encodedAuthData = Base64Coder.base64Encode(username + ":" + password);
		URL requestURL = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.GLOBAL_FOLDER_URL+ "/"+ folderPath +"/" + folderName);
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
	
	/**
	 * Deletes the folder and also all of its subfolders and chat rooms. Please use it with caution.
	 * @param folderName
	 * @param folderPath
	 * @param baseUrl
	 * @param username
	 * @param password
	 * @return
	 * @throws ProtocolException
	 * @throws IOException
	 */
	public static boolean deleteFolder(String folderName, String folderPath, String baseUrl, String username, String password)
	throws ProtocolException, IOException{
		
		String encodedAuthData = Base64Coder.base64Encode(username + ":" + password);
		URL requestURL = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.GLOBAL_FOLDER_URL+ "/"+ folderPath + "/"+ folderName);
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
	
	public static PersistentChatBean getPersistentChatFromFolder(String chatName, String folderPath, String baseUrl, String username, String password)
	throws ParserConfigurationException, SAXException, IOException{
		
		String encodedAuthData = Base64Coder.base64Encode(username + ":" + password);
		URL requestURL = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.GLOBAL_FOLDER_URL + "/"+folderPath);
		HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
		connection.setRequestProperty("Authorization", "Basic " + encodedAuthData);
		connection.setRequestMethod("GET"); 
		connection.setAllowUserInteraction(true);
		connection.setUseCaches(false);
		connection.connect();

		PersistentChatBean persistentChatBean = new PersistentChatBean();
		
		int responseCode = connection.getResponseCode();
		if(responseCode == 200) {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document document = docBuilder.parse(connection.getInputStream());

			Element elements = document.getDocumentElement();
			NodeList entries = elements.getElementsByTagName("entry");

			/*
			 * The algorithm for getting persistent chat from a global folder atom feed 
			 * works as follows:
			 * 1) For each <entry> element, find the one whose <category> element is defined as;
			 * ...<category term="PersistentChat"></category>...
			 * 2) After finding that entry, look for its <link> element defined as;
			 * ...<link href="/orgcollab..."></link>...
			 * 3) Store this link in a PersistentChatBean object
			 * 4) Also from the entry extract the contents of its <id> element defined as;
			 * ...<id>/chat/id/...</id>...
			 * and by removing the "/chat/id/" from the id, the actual id of 36 chars in length 
			 * is extracted
			 * 5) Store this id in a PersistentChatBean object
			 */
			for( int i=0; i<entries.getLength(); i++ ) {
				Element entry = (Element)entries.item(i);
				Element category = DomAtomParser.getFirstElement(entry, "category");
				String term = DomAtomParser.getAttributeValue(category, "term");
				if("PersistentChat".equals(term)){
					String title = DomAtomParser.getElementData(entry, "title");
					if(chatName.equals(title)) {
						NodeList nodeList = entry.getElementsByTagName("link");
						for(int j=0; j<nodeList.getLength(); j++) {
							Element link = (Element)nodeList.item(j);
							String href = DomAtomParser.getAttributeValue(link, "href");
							if(href.startsWith("/orgcollab")) {
								// store the url
								persistentChatBean.setUrl(baseUrl + href);
								break;
							}
						}
						//store the id
						String id = DomAtomParser.getElementData(entry, "id");
						persistentChatBean.setId(id.substring(id.lastIndexOf("/")));
						break;
					}	
									
				}
			}
		}
		return persistentChatBean;
	}
	
	public static boolean isChatRoomExistingInfolder(String chatName, String folderPath, String baseUrl, String username, String password)
	throws ParserConfigurationException, SAXException, IOException{
		
		String encodedAuthData = Base64Coder.base64Encode(username + ":" + password);
		URL requestURL = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.GLOBAL_FOLDER_URL + "/"+folderPath);
		HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
		connection.setRequestProperty("Authorization", "Basic " + encodedAuthData);
		connection.setRequestMethod("GET"); 
		connection.setAllowUserInteraction(true);
		connection.setUseCaches(false);
		connection.connect();

		boolean exists = false;

		int responseCode = connection.getResponseCode();
		if(responseCode == 200) {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document document = docBuilder.parse(connection.getInputStream());

			Element elements = document.getDocumentElement();
			NodeList entries = elements.getElementsByTagName("entry");

			//Check if this folder exists
			for( int i=0; i<entries.getLength(); i++ ) {
				Element category = DomAtomParser.getFirstElement((Element)entries.item(i), "category");
				String term = DomAtomParser.getAttributeValue(category, "term");
				if("PersistentChat".equals(term)){
					String title = DomAtomParser.getElementData((Element)entries.item(i), "title");
					if(chatName.equals(title)) {
						exists = true;				
						break;
					}
				}
			}
		}
		connection.disconnect();
		return exists;
	}
}
