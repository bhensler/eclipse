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
 * This sample demonstrates pagination with search results.
 *
 */
public class SearchPaginationSample
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
			
			String[] roomIds = new String[15];
			//Create 15 rooms
			for(int i=0;i<15;i++)
			{
				String roomName = "Sample" + i;
				//Create a room and save the id
				MeetingRoom room = roomServices.createRoom(roomName);
				roomIds[i]=room.getId();
				System.out.println("Created room " + roomName + " successfully");
			}
			
			//Search for rooms called "Sample". All the 15 rooms created above should be found
			QueryControl control = new QueryControl();
			int start = 1;
			int count = 10;
			int index = 1;
			QueryResults results = null;
			//Do this loop while there are still results to be returned
			do
			{
				control.setStart(start);
				control.setCount(count);
				System.out.println("Fetching page " +  index + " of rooms containing words \"Sample\" " + start + " to " + count);
				index++;
				results = roomServices.search("Sample", control);
				for(MeetingRoom room: results.getResults())
				{
					System.out.println("Room name: " + room.getRoomName());
				}
				//If the results contained less than the number asked for, this is the last set of results found. So
				//exit the program
				if(results.getResults().size() < count)
				{
					System.out.println("End of search results");
					break;
					
				}
				//Reset the counters to fetch the next set of results 1-10,11-20 etc...
				start = start + 10;
				count = count + 10;
				
			}while(!results.getResults().isEmpty());
			
			System.out.println("Deleting rooms");
			for(int i=0;i<15;i++)
			{
				roomServices.deleteRoom(roomIds[i]);
			}

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
