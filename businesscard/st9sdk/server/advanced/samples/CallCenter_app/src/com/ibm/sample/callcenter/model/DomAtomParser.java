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

package com.ibm.sample.callcenter.model;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class DomAtomParser {
	public static Element getFirstElement(Element element, String name)
	{
		NodeList nodeList = element.getElementsByTagName(name);
		return (Element)nodeList.item(0);
	}

	public static String getElementData(Element element, String name) 
	{
		Element localElement = getFirstElement(element, name);
		return getElementData(localElement);
	}

	public static String getElementData(Element node) 
	{
		StringBuffer stringBuffer = new StringBuffer();
		NodeList nodeList = node.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++)
		{
			Node localNode = nodeList.item(i);
			if (localNode instanceof Text)
				stringBuffer.append(localNode.getNodeValue());
		}
		return stringBuffer.toString();
	}

	public static String getAttributeValue(Element element, String attributeName)
	{
		return element.getAttribute(attributeName);
	}
}
