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

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.sample.callcenter.model.ChatFaqBean;
import com.ibm.sample.callcenter.model.Constants;
import com.ibm.sample.callcenter.model.DomAtomParser;
import com.ibm.ws.security.util.Base64Coder;

public class ChatFaqApiUtil {

	public static ChatFaqBean getChatFaqDetails(HttpServletRequest request) 
	throws IOException, ParserConfigurationException, SAXException
	{

		String baseUrl = "http://"+request.getLocalName()+":"+request.getLocalPort();
		URL url = new URL(baseUrl  + Constants.CONTEXT_ROOT  + Constants.FAQ_URL + "/" + request.getParameter("faqId"));
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		String encodedAuthData = Base64Coder.base64Encode(request.getSession().getAttribute("USER") + ":" + request.getSession().getAttribute("PASSWORD"));

		connection.setRequestProperty("Authorization", "Basic " + encodedAuthData);
		connection.setRequestMethod("GET");
		connection.setAllowUserInteraction(true);
		connection.setUseCaches(false);
		connection.connect();

		ChatFaqBean chatFaqBean = null;

		if(connection.getResponseCode() == 200)
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document document = docBuilder.parse(connection.getInputStream());

			Element elements = document.getDocumentElement();
			NodeList nodeList = elements.getElementsByTagName("entry");

			chatFaqBean = new ChatFaqBean();
			if(null!=nodeList && null!=nodeList.item(0))
			{
				Element entry = (Element)nodeList.item(0);
				chatFaqBean.setId(DomAtomParser.getElementData(entry, "id"));
				chatFaqBean.setQuestion(DomAtomParser.getElementData(entry, "title"));

				Element author = DomAtomParser.getFirstElement(entry, "author");
				chatFaqBean.setAuthor(DomAtomParser.getElementData(author, "name"));

				chatFaqBean.setAnswer(DomAtomParser.getElementData(entry, "sta:answer"));
				chatFaqBean.setRating(DomAtomParser.getElementData(entry, "sta:rating"));    	      
			}
		}
		return chatFaqBean; 
	}
	
	public static boolean deleteFaq(String faqId, String baseUrl, String username, String password)
	throws ProtocolException, IOException {
		String encodedAuthData = Base64Coder.base64Encode(username + ":" + password);
		URL requestURL = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.FAQ_URL + "/" + faqId);
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
