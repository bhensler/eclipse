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
 * This sample demonstrates fetching room information from the server for a specific user.
 *
 */
public class GetRoomsByUserSample 
{
	public static void main(String[] args) throws RoomException, IOException
	{
		String server = args[0]; // like "http://server.company.com:9080";
		String username=args[1]; // like "someone@company.com";
		String password=args[2]; // like	"pa88word";
		
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
			MeetingRoom room = new MeetingRoom();
			room.setRoomName("sample room2");
			room.setPassword("password");
			room.setOriginId(originId);
			room.setOriginType("myproduct");
			// now lets create a room and save the roomId
			room = roomServices.createRoom(room);
			
			String tempRoomId = room.getId();
			if(tempRoomId!=null)
			{
				System.out.println("Room created successfully");
			}
			
			//Fetch all the user's rooms
			QueryResults myrooms = roomServices.getRoomsByOwner(username, QueryControl.getDefault());
			for(MeetingRoom meetingRoom: myrooms.getResults())
			{
				if(meetingRoom!=null)
				{
					System.out.println("*****************");
					System.out.println("Room name: " + meetingRoom.getRoomName());
					System.out.println("Description: " + meetingRoom.getDescription());
					System.out.println("Room Id: " + meetingRoom.getId());
					System.out.println("Permaname: " + meetingRoom.getPermaName());
				}
			}
			//Delete the room
			System.out.println("Deleting room...");
			roomServices.deleteRoom(tempRoomId);
			System.out.println("Deleted room successfully");
			roomServices.logout();
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
