/* ***************************************************************** */
/*                                                                   */
/* Licensed Materials - Property of IBM                              */
/*                                                                   */
/* 5724-E76, 5724-L21, 5724-i67, 5724-L64, 5655-M44                  */
/*                                                                   */
/* Copyright IBM Corp. 2006, 2014  All Rights Reserved.              */
/*                                                                   */
/* US Government Users Restricted Rights - Use, duplication or       */
/* disclosure restricted by GSA ADP Schedule Contract with           */
/* IBM Corp.                                                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.meeting.compliance.sample;




import java.util.Properties;


import com.ibm.meeting.compliance.event.EventType;
import com.ibm.meeting.compliance.event.LibraryEventStatus;
import com.ibm.meeting.compliance.plugin.PluginLibraryHandler;
import com.ibm.meeting.compliance.plugin.PluginStartupCallback;


public class SampleLibraryEventHandler implements PluginLibraryHandler 
{

	private final static String PLUGIN_NAME =
		"com.ibm.meeting.compliance.sample.sampleLibraryEventHandlerPlugin";
	
	
	public void customPropertiesChanged(Properties properties) 
	{
		// TODO Auto-generated method stub
		
	}

	public void init(Properties properties, PluginStartupCallback callback) {
		System.out.println("Enter SampleUserHandler.init");

		if (callback != null)
		{
			callback.pluginStarted(PLUGIN_NAME);
		}
		
	}

	public void terminate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLibraryEvent(LibraryEventStatus status)
	{
		if(status.getEventType() == EventType.LIBRARY_ITEM_ADDED)
		{
			System.out.println("On Library Event: Item Added");
		}
		else if(status.getEventType() == EventType.LIBRARY_ITEM_REMOVED)
		{
			System.out.println("On Library Event: Item Removed");
		}
		else if(status.getEventType() == EventType.LIBRARY_ITEM_SHARED)
		{
			System.out.println("On Library Event: Item Shared");
		}
		
		System.out.println("On Library Event: " + "name["+status.getEntryName()+"]" + " user["+status.getOriginatingUser().getId()+"]");
		
		
	}

}
