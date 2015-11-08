package com.ibm.sdk.samples;

import java.io.IOException;
import java.util.ArrayList;

import com.ibm.rtc.client.http.HttpResponseWrapper;
import com.ibm.rtc.client.http.LoginException;
import com.ibm.rtc.client.meeting.room.MeetingRoom;
import com.ibm.rtc.client.meeting.room.QueryResults;
import com.ibm.rtc.client.meeting.room.RoomException;
import com.ibm.rtc.client.meeting.room.RoomServices;
import com.ibm.rtc.client.rtc4web.RealTimeSession;
import com.ibm.rtc.client.rtc4web.RealTimeSessionEvent;
import com.ibm.rtc.client.rtc4web.RealTimeSessionListener;
/**
 * This sample demonstrates several aspects of the SDK.
 * It demonstrates searching for room by strings, creating a room, and joining the room.
 * It also covers the creation of an event listener and putting values into the room maps. 
 *
 */
public class BasicSample
{
	public static void main(String[] args) throws RoomException, IOException
	{
		try
		{
			String server 	= args[0];	// like "http://server.company.com:9080";
			String username	= args[1];	// like "someone@company.com";
			String password = args[2];	// like	"pa88word";
			RoomServices roomServices = new RoomServices(server);
			roomServices.login(username, password);

			// Just for kicks, lets search for something that's probably NOT there
			QueryResults array = roomServices.search("xxx yyy zzz my test room");
			if(array != null)
				System.out.println("A found ["+array.getResults().size()+"] results");

			// Just for kicks, lets search for something that's probably there
			array = roomServices.search("test");
			String roomId = null;
			if(array != null)
			{
				System.out.println("B found ["+array.getResults().size()+"] results");

				// lets print the first entry
				if(array.getResults().get(0) != null)
					System.out.println(array.getResults().get(0));
			}

			// now lets create a room and save the roomId
			MeetingRoom tempRoomId = roomServices.createRoom("xxx yyy zzz my test room");
			if(tempRoomId != null)
				roomId = tempRoomId.getId();

			System.out.println("C roomId ["+roomId+"]");

			// now lets join the room we created
			// note we derive our RealTimeSession from the room services, so we don't need to re-login
			RealTimeSession room = roomServices.createRealTimeSession(roomId);

			// before we join the room, lets bind a listener to receive updates
			room.addListener(new RealTimeSessionListener() {
				
				public void handleEvents(ArrayList<RealTimeSessionEvent> events)
				{
					for(RealTimeSessionEvent event: events)
						handleEvent(event);
				}
				
				public void handleEvent(RealTimeSessionEvent event) {
					System.out.println("E1 from server: \n"+event);
				}
				public void handleError(RealTimeSession rtSession, HttpResponseWrapper response, Exception e){
					System.out.println("E2 from server: \n"+response);
					e.printStackTrace();
				}
			});

			// now lets join the room
			System.out.println("F joining room");
			HttpResponseWrapper response = room.join();
			System.out.println("join response: "+response);

			// now lets start the updates for receiving data
			System.out.println("G starting updates");
			room.startUpdates();

			// now lets set some values in a map, in a simple loop
			for(int i = 0; i < 20; i++)
			{
				// now lets set some data, we should see this come back to us
				System.out.println("H setting a map value");
				room.putInMap("my.test.map", "myKey", "myValue"+System.currentTimeMillis());				
			}

			// now lets stop updates and leave
			System.out.println("I leaving the room");
			room.stopUpdates();
			room.leave();

			// lets delete our room
			System.out.println("J deleting the room");
			roomServices.deleteRoom(roomId);

			// log out to be clean
			System.out.println("K logging out");
			roomServices.logout();
		}
		catch(LoginException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
