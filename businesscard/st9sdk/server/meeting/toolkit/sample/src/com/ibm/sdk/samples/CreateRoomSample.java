package com.ibm.sdk.samples;

import java.io.IOException;
import java.net.MalformedURLException;

import com.ibm.rtc.client.http.LoginException;
import com.ibm.rtc.client.meeting.room.MeetingRoom;
import com.ibm.rtc.client.meeting.room.RoomException;
import com.ibm.rtc.client.meeting.room.RoomServices;

/**
 * This sample demonstrates creating rooms with properties using the SDK.
 *
 */
public class CreateRoomSample
{
	public static void main(String[] args) throws IOException
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
			
			//Make up something random for the originId. It is the client's responsibility to ensure
			//the originId/originType pair is unique. It will lead to problems while searching otherwise.
			String originId = "STC"+System.currentTimeMillis();
			MeetingRoom room = new MeetingRoom("sample room");
			room.setOriginId(originId); 
			room.setOriginType("myproduct");
			room.setPassword("password");
			// now lets create a room and save the roomId
			MeetingRoom meetingRoom = roomServices.createRoom(room);
			String tempRoomId = meetingRoom.getId();
			if(tempRoomId!=null)
			{
				System.out.println("Room created successfully");
			}		
			System.out.println("Deleting room...");
			roomServices.deleteRoom(tempRoomId);
			System.out.println("Deleted room successfully");
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
