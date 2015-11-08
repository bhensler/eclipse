package com.ibm.sdk.samples;

import java.io.IOException;
import java.util.List;
import org.apache.http.cookie.Cookie;
import com.ibm.rtc.client.http.LoginException;
import com.ibm.rtc.client.meeting.room.MeetingRoom;
import com.ibm.rtc.client.meeting.room.RoomException;
import com.ibm.rtc.client.meeting.room.RoomServices;

/**
 * 
 * This sample demonstrates logging in with an LTPA token, instead of a name+password. 
 * Much like a username and password, you must know the LTPA token value before you can log in with it.
 * If you wish, you can give this program the SERVER and the LtpaToken2value 
 * (which you can find by joining a room in the server and reading the room cookies),
 * OR you may give it the SERVER and a valid USERNAME and PASSWORD. In that case, the sample will log in 
 * normally and read the LtpaToken2 cookie to get a valid LtpaToken, and then log out and log back in
 * using the LtpaToken it acquired.  
 * 
 *  
 * This sample also demonstrates the use of HTTP tracing, which can be used in any SDK-based program.
 * Tracing is useful when debugging/troubleshooting during code development.
 * By default, every detail of each HTTP request and response is output to System.err.
 * To change the formatting (based on apache.commons.logging), refer to apache logging on-line. 
 * 
 * 
 */

public class SsoLoginSample
{
	//Turning on the logging properties
	static 
	{
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");		
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
	}
	
	public static void main(String[] args) throws IOException, LoginException, RoomException
	{
		String SERVER = args[0];					// like "http://server.company.com:9080";
		String USERNAME = null;
		String PASSWORD = null;
		String Ltpatoken2value = null;
		
		if(args.length > 2)	// Optional Arguments: either give username and password, or an LTPA token
		{
			USERNAME = args[1];			// like "someone@company.com";
			PASSWORD = args[2];			// like	"pa88word";
			
		}
		else
		{
			Ltpatoken2value = args[1];   // like "5csjZ...wvAPg==" this should be typically around 790 characters
		}
		
		if(Ltpatoken2value==null)
		{
			System.out.println("Ltpa Token not provided, logging in to get token...");
			RoomServices service = new RoomServices(SERVER);
			service.login(USERNAME, PASSWORD);			
			List<Cookie> cookies = service.getCookies();
			for(Cookie cookieIterator: cookies)
			{	
				//System.out.println("Cookie name: "+crisp.getName());
				if(cookieIterator.getName().equals("LtpaToken2"))
				{
					Ltpatoken2value = cookieIterator.getValue();
					//System.out.println("LTPA Token Val: \n"+Ltpatoken2value);
					break;
				}
			}
			
			service.logout();
			System.out.println("Logged out.");
		}
			
		// Set up RoomServices client without login
		RoomServices roomServices = new RoomServices(SERVER);
		roomServices.loginWithToken(Ltpatoken2value);
		MeetingRoom meetingRoom = roomServices.createRoom("LtpaTest room");
				
		//Ensure an authentic call works
		MeetingRoom searchResult = roomServices.get(meetingRoom.getId());
		if(searchResult != null){
			System.out.println("Created Room: "+searchResult);
			System.out.println("Deleting Room...");
			roomServices.deleteRoom(searchResult.getId());
		}	
	}
}