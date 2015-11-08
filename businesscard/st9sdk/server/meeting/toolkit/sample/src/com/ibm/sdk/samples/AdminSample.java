package com.ibm.sdk.samples;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.ibm.rtc.client.meeting.room.AdminRoomServices;
import com.ibm.rtc.client.meeting.room.MeetingRoom;


/**
 * Demonstrates Admin tasks:
 * -create a room on behalf of another user 
 * -delete all of a user's rooms (without logging in as that user)
 * 
 * Requirements: 
 * 		The OTHERUSER argument should be a user that does not
 * 		have any rooms you wish to keep.
 * 
 * 		The ADMINUSER argument must be a sametime meeting server account
 * 		that has administrator status on the meeting server 
 * 		specified by the SERVER argument. 
 * 	
 * 
 */


public class AdminSample {

	
	public static void main(String[] args) {
		final String SERVER = args[0];		// like "http://server.company.com:9080"
		final String ADMINUSER = args[1];	// like "admin@company.com", must be an admin
		final String PASSWORD = args[2]; 	// like	"pa88word"
		final String OTHERUSER = args[3];	// like "someone@company.com", this user will have a room created for them
		final String NAME = args[3];		// like "Joe Smith", this is the OTHERUSER's common name
		try
		{
			AdminRoomServices adminRoomServices = new AdminRoomServices(SERVER);
			adminRoomServices.login(ADMINUSER, PASSWORD);
		
			//Creating the room, the owner will be OTHERUSER
			MeetingRoom room = adminRoomServices.createRoomOnBehalfOf("AdminSample room", OTHERUSER);
			System.out.println("Room name: "+room.getRoomName());
						
			//Now we will see that OTHERUSER, not ADMINUSER, is the owner of the room
			String roomId = room.getId();
			MeetingRoom roomInfo = adminRoomServices.get(roomId);							
			System.out.println("Room Owner: "+roomInfo.getOwner());
		
			
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Press <Enter> to delete the user's rooms");
			// wait for <Enter>
			String line = null;
			do
			{
				line = console.readLine();
			}
			while(line == null);
			
			//This cleans up the room we made, and also demonstrates a common administrative task
			//AdminRoomServices also allows you to delete an individual room using roomId and roomName,
			// without being the owner of the room. 
			adminRoomServices.deleteAllRoomsForOwner(OTHERUSER, NAME);
			
			System.out.println("Rooms Deleted");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
