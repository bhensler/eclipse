package com.ibm.sdk.samples;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.ibm.rtc.client.http.LoginException;
import com.ibm.rtc.client.meeting.room.MeetingRoom;
import com.ibm.rtc.client.meeting.room.RoomException;
import com.ibm.rtc.client.meeting.room.RoomServices;

/**
 * This sample demonstrates fetching room information from the server 
 * using the Origin information. 
 *
 */
public class GetRoomInfoSample 
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
			
			//Find the room by origin info
			ArrayList<MeetingRoom> roomByOriginInfo = roomServices.getRoomByOriginInfo("myproduct", originId);
			MeetingRoom myroom = roomByOriginInfo.get(0);
			if(myroom!=null)
			{
				System.out.println("roomInfo:  "+ myroom.toString());
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
