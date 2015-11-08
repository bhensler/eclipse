/* ***************************************************************** */
/*                                                                   */
/* Licensed Materials - Property of IBM                              */
/*                                                                   */
/* 5724-J23                                                          */
/*                                                                   */
/* Copyright IBM Corp. 2014  All Rights Reserved.                    */
/*                                                                   */
/* US Government Users Restricted Rights - Use, duplication or       */
/* disclosure restricted by GSA ADP Schedule Contract with           */
/* IBM Corp.                                                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.rtc.orgtree.sample;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import com.ibm.collaboration.realtime.orgtree.DefaultOrgNodeProvider;
/**
 * Sample showing how the DefaultOrgNodeProvider can be extended to customize OrgNode display names and exclusion logic.
 */
public class SampleOrgNodeProvider extends DefaultOrgNodeProvider {

	private static final Logger logger = Logger.getLogger(SampleOrgNodeProvider.class.getPackage().getName());
	//Any additional attributes we want to process must be added to the st.orgtree.properties "ldapSearchReturningAttribs" property.
	private static final String DISPLAY_ATTRIB_APPENDAGE = "telephoneNumber";
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.collaboration.realtime.orgtree.DefaultOrgNodeProvider#getDisplayName(int, javax.naming.directory.Attributes, java.lang.String)
	 */
	public String getDisplayName(int orgNodeType, Attributes attribs, String dn) {
		String displayName = super.getDisplayName(orgNodeType, attribs, dn);
		Attribute attrib = attribs.get(DISPLAY_ATTRIB_APPENDAGE);
		if (null != attrib) {
			String val = null;
			try {
				val = (String) attrib.get();
			} 
			catch (NamingException e) {
				if (logger.isLoggable(Level.INFO)) {
					logger.log(Level.INFO, "Exception retrieving "+DISPLAY_ATTRIB_APPENDAGE+" attribute", e);	
				}
			}
			if (null != val) {
				displayName = displayName + " ["+val+"]";
			}
		}
		return displayName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.collaboration.realtime.orgtree.DefaultOrgNodeProvider#exclude(java.lang.String, java.lang.String, java.lang.String[])
	 */
	public boolean exclude(String requestorContactId, String dn, String[] excludedDNs) {
		boolean exclude = super.exclude(requestorContactId, dn, excludedDNs);
		if (!exclude) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(MessageFormat.format("Checking whether to exclude directory entry [{0}] from requestor [{1}]", dn, requestorContactId));	
			}
			//Extended exclusion logic here
		}
		return exclude;
	}
}
