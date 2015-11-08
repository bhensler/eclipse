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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.sample.callcenter.model.ChatTranscriptBean;
import com.ibm.sample.callcenter.model.Constants;
import com.ibm.sample.callcenter.model.DomAtomParser;
import com.ibm.sample.callcenter.model.PersistentChatBean;
import com.ibm.ws.security.util.Base64Coder;

public class PersistentChatApiUtil {
	
	public static PersistentChatBean createChatRoom(String chatName, String path, String baseUrl, String username, String password)
	throws ProtocolException, IOException, SAXException, ParserConfigurationException{
		
		String encodedAuthData = Base64Coder.base64Encode(username + ":" + password);
		
		/*
		// A community can also be created by passing a createCommunity parameter in the URL
		URL requestURL = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.PERSISTENT_CHAT_URL+ "/global/"+ path +"/" + chatName+"?createCommunity=true");
		*/
		
		URL requestURL = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.PERSISTENT_CHAT_URL+ "/global/"+ path +"/" + chatName);
		HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
		connection.setRequestProperty("Authorization", "Basic " + encodedAuthData);
		connection.setRequestMethod("POST"); 
		connection.setAllowUserInteraction(true);
		connection.setUseCaches(false);
		connection.connect();
		int responseCode = connection.getResponseCode();
		
		PersistentChatBean persistentChatBean = null;
		
		if(responseCode == 200)
		{		
			// Get persistent chat and return it
			String folderPath = Constants.GLOBAL_ROOT_FOLDER+"/"+path;
			persistentChatBean = GlobalFolderApiUtil.getPersistentChatFromFolder(chatName, folderPath, baseUrl, username, password);
		}
		connection.disconnect();
		return persistentChatBean;		
	}
	
	public static boolean addTextToChatRoom(String text, String chatName, String path, String baseUrl, String username, String password)
	throws ProtocolException, IOException, SAXException, ParserConfigurationException{
		
		String encodedAuthData = Base64Coder.base64Encode(username + ":" + password);
		
		URL requestURL = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.PERSISTENT_CHAT_URL+ "/global/"+ path +"/" + chatName+"?entry="+text);
		HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
		connection.setRequestProperty("Authorization", "Basic " + encodedAuthData);
		connection.setRequestMethod("PUT"); 
		connection.setAllowUserInteraction(true);
		connection.setUseCaches(false);
		connection.connect();
		int responseCode = connection.getResponseCode();
				
		if(responseCode == 200)
		{		
			return true;
		}
		connection.disconnect();
		return false;
	}
	
	public static List getCustomerChatTranscript(HttpServletRequest request) throws IOException, ParserConfigurationException, SAXException
	{
		List <ChatTranscriptBean> transcripts = null;

		String baseUrl = "http://"+request.getLocalName()+":"+request.getLocalPort();
		URL url = new URL(baseUrl  + Constants.CONTEXT_ROOT  + Constants.PERSISTENT_CHAT_URL + Constants.PERSISTENT_CHAT_ID + "/" + request.getParameter("chatId"));
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		String encodedAuthData = Base64Coder.base64Encode(request.getSession().getAttribute("USER") + ":" + request.getSession().getAttribute("PASSWORD"));

		connection.setRequestProperty("Authorization", "Basic " + encodedAuthData);
		connection.setRequestMethod("GET");
		connection.setAllowUserInteraction(true);
		connection.setUseCaches(false);
		connection.connect();

		if(connection.getResponseCode() == 200)
		{

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document document = docBuilder.parse(connection.getInputStream());

			Element elements = document.getDocumentElement();
			NodeList nodeList = elements.getElementsByTagName("entry");

			transcripts = new ArrayList<ChatTranscriptBean>();
			for( int i=0; i<nodeList.getLength(); i++ )
			{
				Element entry = (Element)nodeList.item(i);
				
				String title = DomAtomParser.getElementData(entry, "title");
				Element category = DomAtomParser.getFirstElement(entry, "category");
				String term = DomAtomParser.getAttributeValue(category, "term");
				// only store messages and no participation entries
				if("Message".equals(term)) {
					ChatTranscriptBean chatBean = new ChatTranscriptBean();
					chatBean.setTitle(title);
					chatBean.setContent(DomAtomParser.getElementData(entry, "content"));
					chatBean.setUpdated(formatDate(DomAtomParser.getElementData(entry, "updated")));
					transcripts.add(chatBean);
				}
			}
		}
		connection.disconnect();
		return transcripts; 
	}
	
	public static String formatDate(String date) {
		String formatedDate = "";
		if(null==date)
			return formatedDate;
		
		String[] splitDate = date.split("T");
		formatedDate = "at "+ splitDate[1] + " on "+splitDate[0];
		return formatedDate;
		
	}
}
