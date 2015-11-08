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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.sample.callcenter.model.Constants;
import com.ibm.sample.callcenter.model.DomAtomParser;
import com.ibm.sample.callcenter.model.SkillTapBean;
import com.ibm.sample.callcenter.model.SkillTapResponseBean;
import com.ibm.ws.security.util.Base64Coder;

public class SkillTapApiUtil {
	
	public static boolean postQuestion(String communityName, String question, String baseUrl, String username, String password)
	throws ProtocolException, IOException{
		
		String questionParam = "question="+URLEncoder.encode(question, "UTF-8");
		String encodedAuthData = Base64Coder.base64Encode(username + ":" + password);
		URL requestURL = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.SKILLTAP_URL+ "/"+ communityName);
		HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
		connection.setRequestProperty("Authorization", "Basic " + encodedAuthData);
		connection.setRequestMethod("POST"); 
		connection.setDoOutput(true);
		OutputStream outputStream = connection.getOutputStream();
		outputStream.write(questionParam.getBytes("UTF-8"));
		connection.connect();
		int responseCode = connection.getResponseCode();
		
		if(responseCode == 200)
			return true;
		
		connection.disconnect();
		return false;		
	}
	
	public static List getSkillTapForCommunity(String communityName, String baseUrl, String username, String password)
	throws ParserConfigurationException, SAXException, IOException{
		
		String encodedAuthData = Base64Coder.base64Encode(username + ":" + password);
		URL requestURL = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.SKILLTAP_URL+ "/"+ communityName);
		HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
		connection.setRequestProperty("Authorization", "Basic " + encodedAuthData);
		connection.setRequestMethod("GET"); 
		connection.setAllowUserInteraction(true);
		connection.setUseCaches(false);
		connection.connect();
		int responseCode = connection.getResponseCode();
		
		List allSkillTap = null;
		if(responseCode == 200) {
			allSkillTap = new ArrayList();
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document document = docBuilder.parse(connection.getInputStream());

			Element elements = document.getDocumentElement();
			NodeList entries = elements.getElementsByTagName("entry");

			for( int i=0; i<entries.getLength(); i++ ) {
				Element entry = (Element)entries.item(i);
				SkillTapBean skillTapBean = new SkillTapBean();
				skillTapBean.setQuestion(DomAtomParser.getElementData(entry, "title"));
				skillTapBean.setReqId(DomAtomParser.getElementData(entry, "sta:reqid"));
				skillTapBean.setPublished(DomAtomParser.getElementData(entry, "published"));
				Element author = DomAtomParser.getFirstElement(entry, "author");
				skillTapBean.setAuthor(DomAtomParser.getElementData(author, "name"));
				skillTapBean.setEmail(DomAtomParser.getElementData(author, "email"));
				
				// Retrieve responses
				NodeList responseNodes = entry.getElementsByTagName("sta:skilltapResponse");
				if(null!=responseNodes && responseNodes.getLength()>0)
				{					
					List allSkillTapResponses = new ArrayList();
					for(int j=0; j<responseNodes.getLength(); j++) {
						Element responseElement = (Element)responseNodes.item(j);
						SkillTapResponseBean skillTapResponseBean = new SkillTapResponseBean();
						skillTapResponseBean.setAnswer(DomAtomParser.getElementData(responseElement, "sta:text"));
						skillTapResponseBean.setId(DomAtomParser.getElementData(responseElement, "id"));
						skillTapResponseBean.setRating(DomAtomParser.getElementData(responseElement, "sta:rating"));
						allSkillTapResponses.add(skillTapResponseBean);
					}
					skillTapBean.setResponses(allSkillTapResponses);
				}
				//TODO: For testing purposes, remove this block later
				else 
				{
					List allSkillTapResponses = new ArrayList();
					for(int j=0; j<2; j++) {
						SkillTapResponseBean skillTapResponseBean = new SkillTapResponseBean();
						skillTapResponseBean.setAnswer("Sample Answer");
						skillTapResponseBean.setId(""+(j+1));
						skillTapResponseBean.setRating("0.0");
						allSkillTapResponses.add(skillTapResponseBean);
					}
					skillTapBean.setResponses(allSkillTapResponses);					
				}
				allSkillTap.add(skillTapBean);
			}
		}
		
		connection.disconnect();
		return allSkillTap;		
	}
	
	public static boolean rateResponse(String communityName, int reqId, int rating, String baseUrl, String username, String password)
	throws ProtocolException, IOException{
		
		String encodedAuthData = Base64Coder.base64Encode(username + ":" + password);
		URL requestURL = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.SKILLTAP_URL+ "/"+ communityName+"?id=" + reqId + "&rating=" + rating);
		HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
		connection.setRequestProperty("Authorization", "Basic " + encodedAuthData);
		connection.setRequestMethod("PUT"); 
		connection.setAllowUserInteraction(true);
		connection.setUseCaches(false);
		connection.connect();
		int responseCode = connection.getResponseCode();
		
		if(responseCode == 200)
			return true;
		
		connection.disconnect();
		return false;		
	}
}
