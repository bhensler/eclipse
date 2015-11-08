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
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.sample.callcenter.model.Constants;
import com.ibm.sample.callcenter.model.DomAtomParser;
import com.ibm.sample.callcenter.model.SearchResultBean;
import com.ibm.ws.security.util.Base64Coder;

public class SearchApiUtil {
	
	public static boolean doSearch(HttpServletRequest request)
	throws ParserConfigurationException, SAXException, IOException{
		
		HttpSession session = request.getSession();
		String encodedAuthData = Base64Coder.base64Encode(session.getAttribute("USER") + ":" + session.getAttribute("PASSWORD"));
		String baseUrl = "http://"+request.getLocalName()+":"+request.getLocalPort();
		String input = request.getParameter("inputField");
		String category = request.getParameter("category");
		URL requestURL = new URL(baseUrl + Constants.CONTEXT_ROOT + Constants.SEARCH_URL + "?text=" + URLEncoder.encode(input, "UTF-8") + "&searchScope=" + category);
		HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
		connection.setRequestProperty("Authorization", "Basic " + encodedAuthData);
		connection.setRequestMethod("GET"); 
		connection.setAllowUserInteraction(true);
		connection.setUseCaches(false);
		connection.connect();
		int responseCode = connection.getResponseCode();
		
		if(responseCode == 200)
		{
			List searchResults = new ArrayList();
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document document = docBuilder.parse(connection.getInputStream());

			Element elements = document.getDocumentElement();
			NodeList entries = elements.getElementsByTagName("entry");

			if(null!=entries && entries.getLength()>0) {
				for( int i=0; i<entries.getLength(); i++ ) {
					Element entry = (Element)entries.item(i);
					Element categoryElem = DomAtomParser.getFirstElement(entry, "category");
					String term = DomAtomParser.getAttributeValue(categoryElem, "term");
					if("chatRooms".equals(term)) {
						SearchResultBean bean = new SearchResultBean();
						bean.setTitle(DomAtomParser.getElementData(entry, "title"));						
						bean.setType("chatRooms");
						NodeList nodeList = entry.getElementsByTagName("link");
						for(int j=0; j<nodeList.getLength(); j++) {
							Element link = (Element)nodeList.item(j);
							String href = DomAtomParser.getAttributeValue(link, "href");
							if(href.startsWith(baseUrl+"/orgcollab")) {
								// store the url
								bean.setId(href);
								break;
							}
						}						
						searchResults.add(bean);
					}
					else if("chatRoomTranscripts".equals(term)) {
						SearchResultBean bean = new SearchResultBean();
						bean.setTitle(DomAtomParser.getElementData(entry, "title"));
						bean.setContent(DomAtomParser.getElementData(entry, "content"));
						Element author = DomAtomParser.getFirstElement(entry, "author");
						bean.setAuthor(DomAtomParser.getElementData(author, "name"));
						bean.setEmail(DomAtomParser.getElementData(author, "email"));
						String updated = DomAtomParser.getElementData(entry, "updated");
						Date date = new Date(Long.parseLong(updated));
						DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
						bean.setPublished(df.format(date));
						String id = DomAtomParser.getElementData(entry, "id");
						String[] idSplit = id.split("/");
						bean.setId(idSplit[idSplit.length-1]);
						bean.setType("chatRoomTranscripts");
						searchResults.add(bean);
					}
					else if("chatRoomFaqs".equals(term)){
						SearchResultBean bean = new SearchResultBean();
						bean.setType("chatRoomFaqs");
						bean.setTitle(DomAtomParser.getElementData(entry, "title"));
						bean.setContent(DomAtomParser.getElementData(entry, "summary"));
						Element link = DomAtomParser.getFirstElement(entry, "link");
						String href = DomAtomParser.getAttributeValue(link, "href");
										
						String[] idSplit = href.split("/");
						bean.setId(idSplit[idSplit.length-1]);
						searchResults.add(bean);
					}
				}
				session.setAttribute(Constants.SEARCH_RESULTS, searchResults);
			}

			else {
				session.setAttribute(Constants.SEARCH_RESULTS, null);
				return false;
			}
		
			return true;
		}
		session.setAttribute(Constants.SEARCH_RESULTS, null);
		connection.disconnect();
		return false;		
	}
}
