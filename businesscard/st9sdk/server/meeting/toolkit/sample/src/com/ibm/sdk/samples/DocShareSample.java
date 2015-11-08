package com.ibm.sdk.samples;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import com.ibm.rtc.client.http.HttpResponseWrapper;
import com.ibm.rtc.client.meeting.docshare.AbstractDocShareListener;
import com.ibm.rtc.client.meeting.docshare.DocInfo;
import com.ibm.rtc.client.meeting.docshare.DocPage;
import com.ibm.rtc.client.meeting.docshare.DocShare;
import com.ibm.rtc.client.meeting.docshare.DocShareException;
import com.ibm.rtc.client.meeting.projector.Projector;
import com.ibm.rtc.client.meeting.projector.Projector.MediaType;
import com.ibm.rtc.client.meeting.projector.ProjectorListener;
import com.ibm.rtc.client.meeting.room.RoomException;
import com.ibm.rtc.client.rtc4web.RealTimeSession;

/**
 * Joins a room as an anonymous listener, and fetches slides
 * Requires console input (hitting Enter) to control.
 * Join in with a real client, and share a document and page through it. 
 * This sample will fetch the pages as you display them.
 * 
 * Note: This sample does not work with IBM JRE's. 
 *
 */
public class DocShareSample
{
	private static String SERVER = null;	// like "http://server.company.com:9080";
	private static String USERNAME = null;	// like "someone@company.com";
	static String ROOMID = null;			// like "e6206473-56bd-4340-9ab2-2b6a6547ad1c";

	public static void fetchPage(final RealTimeSession rtSession, final DocInfo docInfoObj) throws IOException, DocShareException, InterruptedException, InvocationTargetException
	{
		System.out.println("PRE-FETCH  PAGE:"+docInfoObj.getCurrentPage());
		
		DocPage resp = docInfoObj.fetchPage(rtSession, rtSession.getSid(), docInfoObj.getId(), docInfoObj.getCurrentPage(),false);
		
		System.out.println("POST-FETCH PAGE:"+resp);
	}

	public static void main(String[] args) throws IOException, RoomException
	{
		SERVER 	 = args[0];		// like "http://server.company.com";
		USERNAME = args[1];		// like "someone@company.com";		
		ROOMID 	 = args[2];		// like "e6206473-56bd-4340-9ab2-2b6a6547ad1c";

		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Press <Enter> To Start");
		// wait for enter
		String line = null;
		do
		{
			line = console.readLine();
		}
		while(line == null);
		System.out.println("starting");
		try
		{	
			final RealTimeSession room = new RealTimeSession(SERVER, ROOMID, USERNAME);
			//Note, we must set our "guest" name since we're anonymous
			room.setUser(USERNAME); 		
			
			final DocShare docShare = new DocShare(room);
			final Projector projector = new Projector(room);
			HttpResponseWrapper joinResponse = room.join();
			System.out.println("Join response: "+joinResponse);
			room.startUpdates();

			projector.addListener(new ProjectorListener() {
				public void projectorChanged(Projector projector)
				{
					System.out.println("projectorChanged: "+projector);
					//This is really telling us whether the projector is on
					if(projector.getPresenter() != null)
					{
						if(projector.getMediaType().equals(MediaType.Slides))
						{
							System.out.println("presenter or media change ...");
							DocInfo docInfoObj = docShare.getDocInfo(projector.getMediaId());
							try
							{
								fetchPage(room, docInfoObj);								
							} 
							catch (Exception e)
							{
								e.printStackTrace();
							} 
						}
					}
				}
			});

			docShare.addListener(new AbstractDocShareListener(){
				public void documentUpdate(String sid, String docId, String docKey, DocInfo docInfoObj)
				{
					if(docKey.equals("currentPage") && projector.getPresenter() != null)
					{
						System.out.println("page change ...");
						try
						{
							fetchPage(room, docInfoObj);								
						} 
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			});
			
			
			// wait for enter
			line = null;
			do
			{
				line = console.readLine();
			}
			
			while(line == null);
			System.out.println("stopping");
			room.shutdown();
		}
		finally
		{
			System.out.println("Sample Complete");
		}		
	}
}