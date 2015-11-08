package com.ibm.sdk.samples;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.ibm.rtc.client.http.LoginException;
import com.ibm.rtc.client.meeting.room.MeetingRoom;
import com.ibm.rtc.client.meeting.room.RoomException;
import com.ibm.rtc.client.meeting.room.RoomServices;

/**
 * 
 * This sample demonstrates fetching a room based on properties designed to return a unique result.
 *
 */
public class SearchRoomSample
{
	public static void main(String[] args) throws RoomException, IOException
	{
		String server = args[0];	// like "http://server.company.com:9080";
		String username=args[1];	// like "someone@company.com";
		String password=args[2];	// like	"pa88word";
		
		// Bind to a server.
		// note the server string, is a url string, with a port, like http://server.company.com:9080
		RoomServices roomServices = null;
		try 
		{
			roomServices = new RoomServices(server);
			roomServices.login(username,password);
			
			//Make up something random for the originId. It is the consumer's responsibility to ensure
			//the originId/originType pair is unique. It will lead to problems while searching otherwise.
			String originId = "STC"+System.currentTimeMillis();
			MeetingRoom room = new MeetingRoom("SearchRoomSample room");
			room.setOriginId(originId);
			room.setOriginType("myproduct");
			room.setPassword("password");
			// now lets create a room and save the roomId
			room = roomServices.createRoom(room);
			String tempRoomId = room.getId();
			if(tempRoomId!=null)
			{
				System.out.println("Room created successfully");
			}
			
			//Find the room
			ArrayList<MeetingRoom> result = roomServices.getRoomByOriginInfo("myproduct", originId); 
			if(result.size() > 0)
			{				
				for(MeetingRoom r: result)
				{					
					System.out.println("Room name: " + r.getRoomName());
					System.out.println("Description: " + r.getDescription());
					System.out.println("Owner: "+r.getOwner());
					System.out.println("Room Id: " + r.getId());					
					System.out.println("Permaname: " + r.getPermaName());
				}
			}
			
			System.out.println("Deleting room...");
			roomServices.deleteRoom(tempRoomId);
			System.out.println("Deleted room successfully");

		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
		catch(LoginException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
}
