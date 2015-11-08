package com.ibm.sdk.samples;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import com.ibm.rtc.client.http.HttpResponseWrapper;
import com.ibm.rtc.client.meeting.room.MeetingRoom;
import com.ibm.rtc.client.meeting.room.RoomServices;
import com.ibm.rtc.client.rtc4web.RealTimeSession;
import com.ibm.rtc.client.rtc4web.RealTimeSessionEvent;
import com.ibm.rtc.client.rtc4web.RealTimeSessionListener;

/**************************
 * 
 * This sample shows there is only a subtle difference between accessing the server
 * anonymously and through a login. You can compare this to SampleApp.java to see that
 * the primary difference is simply not creating a room and not logging in, but joining
 * the room from the roomId. Note that an Anonymous user typically cannot search. 
 * 
 * 
 * Before running this sample you must:
 * Create a meeting room and join it. 
 * Use your browser (or your preferred program) to view the page source. 
 * There will be a var called "MeetingIdSession".
 * The value of that var is the "roomId" argument for this sample. 
 *
 */

public class AnonymousSample
{
	public static void main(String[] args) throws Exception
	{
		final String SERVER = args[0];		// like "http://server.company.com:9080";
		final String USERNAME = args[1];	// like "someone@company.com";
		final String ROOMID = args[2]; 		// like "6f2d3d61-8998-4b32-b647-2e737a63b38a";
		System.out.println("C roomId ["+ROOMID+"]");
		
		
		RealTimeSession room = new RealTimeSession(SERVER, ROOMID, USERNAME);
		//Note, we must set our "guest" name since we're anonymous
		room.setUser(USERNAME); 
		
		// before we join the room, lets bind a listener to receive updates
		room.addListener(new RealTimeSessionListener() 
		{
			public void handleEvents(ArrayList<RealTimeSessionEvent> events)
			{
				Date date = new Date();
				System.out.println(date.toString()+"\nE1 from server: \n"+Arrays.toString(events.toArray()));
			}
			public void handleEvent(RealTimeSessionEvent event) {
				System.out.println("E2 from server: \n"+event);
			}
			public void handleError(RealTimeSession rtSession, HttpResponseWrapper response, Exception e){
				System.out.println("E3 from server: \n"+response);
				e.printStackTrace();
			}
		});
		
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Press <Enter> to join the room");
		// wait for enter
		String line = null;
		do
		{
			line = console.readLine();
		}
		while(line == null);
		
		// now lets join the room
		System.out.println("F joining room");		
		HttpResponseWrapper joinResponse = room.join();
		System.out.println("join response: "+joinResponse);
		
		// now lets start the updates for receiving data
		System.out.println("G starting updates");
		room.startUpdates();
		room.putInMap("UserMap",USERNAME+".handRaise", "true");
		// now lets set some values in a map, in a simple loop
		/*
		for(int i = 0; i < 20; i++)
		{
			// now lets set some data, we should see this come back to us
			System.out.println("H setting a map value");
			room.putInMap("my.test.map", "myKey", "myValue"+System.currentTimeMillis());
			Thread.sleep(200);
		}
		*/
		
		System.out.println("Press <Enter> to leave the room");
		// wait for enter
		line = null;
		do
		{
			line = console.readLine();
		}
		while(line == null);
		
		
		// now lets stop updates and leave
		System.out.println("I leaving the room");
		room.stopUpdates();
		room.leave();
		
		// log out to be clean
		System.out.println("K logging out");
	}
}
