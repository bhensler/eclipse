/* ***************************************************************** */
/*                                                                   */
/* Licensed Materials - Property of IBM                              */
/*                                                                   */
/* 5724-E76, 5724-L21, 5724-i67, 5724-L64, 5655-M44                  */
/*                                                                   */
/* Copyright IBM Corp. 2014  All Rights Reserved.                    */
/*                                                                   */
/* US Government Users Restricted Rights - Use, duplication or       */
/* disclosure restricted by GSA ADP Schedule Contract with           */
/* IBM Corp.                                                         */
/*                                                                   */
/* ***************************************************************** */

/*
 * Created on 26-Mar-2008
 *
 * 
 */
package com.ibm.stadvanced.samples.ldapadaptor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xerces.impl.dv.util.Base64;

import java.io.ByteArrayOutputStream;
import com.ibm.rtc.extensions.PictureData;
import com.ibm.rtc.extensions.UserInformation;
import com.ibm.rtc.extensions.UserInformationProvider;

/**
 *
 * A sample class to demonstrate how to write an extension for the UserInformationProvider extension point
 */
public class LdapUserInformationProvider implements UserInformationProvider {

	private static String SAMETIME_INFO_URL_TEMPLATE = "/servlet/UserInfoServlet?operation=3&userid={0}";
	private static String EMAIL_MAP_KEY = "MailAddress";
	private static String BUSINESS_MAP_KEY = "Company";
	private static String PHONE_MAP_KEY = "Telephone";
	private static String IMAGE_MAP_KEY = "ImagePath";
	private static String TITLE_MAP_KEY = "Title";
	private static String NAME_MAP_KEY = "Name";
	private static String IMAGE_KEY = "Photo";
	
	private static final Logger s_logger = Logger.getLogger(LdapUserInformationProvider.class.getName());
	
	/* (non-Javadoc)
	 * @see com.ibm.rtc.extensions.UserInformationProvider#getUserInformation(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public UserInformation getUserInformation(String loginId, String vmmId,
			String emailAddress, String displayName) {
		if (s_logger.isLoggable(Level.FINER)) {
			s_logger.entering(LdapUserInformationProvider.class.getName(), "getUserInformation", new Object[]{loginId, vmmId, emailAddress, displayName});
		}
		Map<String, String> SametimeUserInfo = getSametimeUserInfo(loginId);
		UserInformation user = new UserInformation();
		user.setDisplayName(SametimeUserInfo.get(NAME_MAP_KEY));
		user.setBusinessAddress(SametimeUserInfo.get(BUSINESS_MAP_KEY));
		user.setEmailAddress(SametimeUserInfo.get(EMAIL_MAP_KEY));
		user.setTelephoneNumber(SametimeUserInfo.get(PHONE_MAP_KEY));
		user.setTitle(SametimeUserInfo.get(TITLE_MAP_KEY));
		String picPath = SametimeUserInfo.get(IMAGE_MAP_KEY);
		user.setPictureUrl(picPath);
		// picture data
		if (picPath == null) {
			String picString = SametimeUserInfo.get(IMAGE_KEY);
			byte[] picture = Base64.decode(picString);
			user.setPictureData(new PictureData(picture, "image/jpeg"));
		} else {
			user.setPictureData(getPicData(picPath));
		}
		if (s_logger.isLoggable(Level.FINER)) {
			s_logger.exiting(LdapUserInformationProvider.class.getName(), "getUserInformation", user);
		}
		return user;
	}
	
	/**
	 * @param picPath
	 * @return
	 */
	private PictureData getPicData(String picPath) {
		URL picUrl;
		PictureData pictureData = null;
		try {
			picUrl = new URL(picPath);
		
		ByteArrayOutputStream baout = new ByteArrayOutputStream(16 * 1024);
		BufferedInputStream imageStream;
		
			imageStream = new BufferedInputStream(picUrl.openStream());
			int read;
			byte buffer [] = new byte[1024];
			while ((read = imageStream.read(buffer)) != -1) {
				baout.write(buffer, 0, read);
			}
			pictureData = new PictureData(baout.toByteArray(), "image/jpeg"); 
			
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		return pictureData;

		
	}

	/**
	 * @param loginId
	 * @return
	 */
	private Map<String, String> getSametimeUserInfo(String loginId) {
		if (s_logger.isLoggable(Level.FINER)) {
			s_logger.entering(LdapUserInformationProvider.class.getName(), "getSametimeUserInfo", loginId);
		}
		String line;
		Map<String, String> entries = new HashMap<String, String>();
		try {
			String host = getSametimeHost();
			
			/*
			 * connect to the sametime server UserInfo service and retrieve info for the user id
			 */
			URL url = new URL(MessageFormat.format("http://"+host+SAMETIME_INFO_URL_TEMPLATE, new Object [] {URLEncoder.encode(loginId, "UTF-8")}));
			URLConnection c = url.openConnection();
			c.connect();
			
			/*
			 * The call returns an xml document which needs to be parsed
			 */
			
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			
			in.readLine();													//skip <?xml version...
			in.readLine(); 													//skip <userinfo> tag
			in.readLine(); 													//skip userid tag
			
			while ((line = in.readLine()) != null) {
				int key = line.indexOf("=\"")+2;
				int endKey = line.indexOf("\" ");
				int value = line.indexOf('>') +1;		//to help remove start  <field name=... >
				int endTag = line.indexOf("</");		//to remove </field>

				if(key!= 1 && endTag != -1)
				{
					String key_st = line.substring(key,endKey).trim();
					String value_st = line.substring(value,endTag).trim();
					entries.put(key_st,value_st);
				}
			}
			in.close();	
			
		} catch (MalformedURLException iognore) {
			/* ignore - will not happen */
			
		} catch (UnsupportedEncodingException ueex) {
			/* ignore - will not happen */
		
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		if (s_logger.isLoggable(Level.FINER)) {
			s_logger.exiting(LdapUserInformationProvider.class.getName(), "getSametimeUserInfo", entries);
		}
		return entries;
	}

	/**
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private String getSametimeHost() throws FileNotFoundException, IOException {
		if (s_logger.isLoggable(Level.FINER)) {
			s_logger.entering(LdapUserInformationProvider.class.getName(), "getSametimeHost");
		}
		Properties props = new Properties();
		props.load(getClass().getResourceAsStream("/ldapadaptor.properties"));
		String hostPath = props.getProperty("sametime.server.hostname");		
		if (s_logger.isLoggable(Level.FINER)) {
			s_logger.exiting(LdapUserInformationProvider.class.getName(), "getSametimeHost");
		}
		return hostPath;
	}


	public static void main(String[] args)
	{
		
		if(args.length != 1)
		{
			System.out.println("Usage: LdapUserInformationProvider <userid>");
		}
		LdapUserInformationProvider ldup = new LdapUserInformationProvider();
		UserInformation user1 = ldup.getUserInformation("user1", "", "", "");
		System.out.println(user1.getDisplayName());
		System.out.println(user1.getTelephoneNumber());

	}
}
