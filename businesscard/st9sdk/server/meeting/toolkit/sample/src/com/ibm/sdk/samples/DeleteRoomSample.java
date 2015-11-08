package com.ibm.sdk.samples;

import java.io.IOException;
import java.net.MalformedURLException;

import com.ibm.rtc.client.http.LoginException;
import com.ibm.rtc.client.meeting.room.MeetingRoom;
import com.ibm.rtc.client.meeting.room.QueryControl;
import com.ibm.rtc.client.meeting.room.QueryResults;
import com.ibm.rtc.client.meeting.room.RoomException;
import com.ibm.rtc.client.meeting.room.RoomServices;

/**
 * This sample demonstrates deleting all rooms for a specific user.
 * You will want to create some rooms for the user first if you want to try running this sample.
 *
 */
public class DeleteRoomSample
{
	public static void main(String[] args) throws IOException
	{
		String server = 	args[0];	// like "http://server.company.com:9080";
		String username=	args[1];	// like "someone@company.com";
		String password=	args[2];	// like	"pa88word";
		
		// Bind to a server.
		// note the server string, is a url string, with a port, like http://server.company.com:9080
		RoomServices roomServices = null;
		try 
		{
			roomServices = new RoomServices(server);
			roomServices.login(username,password);
			
			QueryControl searchParams = new QueryControl();
			searchParams.setCount(100);

			QueryResults callersRooms = roomServices.getCallersRooms(searchParams);
			
			int i = 0;
			for(MeetingRoom room : callersRooms.getResults())
			{
				i++;				
				roomServices.deleteRoom(room.getId());
				System.out.println(i+" :deleted["+room.getId()+"]");
			}
			roomServices.logout();
		} 
		catch (MalformedURLException e1) 
		{
			e1.printStackTrace();
		}
		catch(RoomException e)
		{
			e.printStackTrace();
		}
		catch(LoginException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
