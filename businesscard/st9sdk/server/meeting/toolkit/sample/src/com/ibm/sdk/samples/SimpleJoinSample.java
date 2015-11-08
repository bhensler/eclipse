package com.ibm.sdk.samples;

import java.io.IOException;

import com.ibm.rtc.client.http.HttpResponseWrapper;
import com.ibm.rtc.client.http.LoginException;
import com.ibm.rtc.client.rtc4web.RealTimeSession;


/* This sample demonstrates a very simple way to join a room as 
 * an authentic user. 
 * 
 */



public class SimpleJoinSample
{

	private static String SERVER;   	// like "http://server.company.com:9080";
	private static String USERNAME; 	// like "someone@company.com";
	private static String PASSWORD;		// like	"pa88word";
	private static String ROOMID;		// like "e6206473-56bd-4340-9ab2-2b6a6547ad1c";
	
	public static void main(String[] args) throws LoginException, IOException
	{		
		SERVER 		= args[0];	
		USERNAME	= args[1];
		PASSWORD 	= args[2];	
		ROOMID 		= args[3]; 
		
		RealTimeSession rtSession = new RealTimeSession(SERVER, ROOMID, USERNAME);
		rtSession.login(USERNAME, PASSWORD);
		
		HttpResponseWrapper resp = rtSession.join();
		System.out.println(resp);
		
		System.out.println("The user "+rtSession.getUser()+" has joined the room.");
		
		
		rtSession.logout();
		rtSession.shutdown();
		
	}

}
