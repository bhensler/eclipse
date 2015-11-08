package com.ibm.sdk.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ibm.rtc.client.http.HttpResponseWrapper;
import com.ibm.rtc.client.http.LoginException;
import com.ibm.rtc.client.meeting.room.MeetingRoom;
import com.ibm.rtc.client.meeting.room.RoomException;
import com.ibm.rtc.client.meeting.room.RoomServices;
import com.ibm.rtc.client.rtc4web.RealTimeSession;
/**
 * This sample demonstrates how to join multiple users to a room. 
 * Press <Enter> to remove the users from the room. 
 *
 */
public class MultiJoinSample
{
	private static final boolean DEBUG_PRINTS 	= true;
	private static final long	 JOIN_DELAY		= 2000;

	public static void main(String[] args) throws InterruptedException, RoomException, LoginException, IOException
	{
		final String SERVER = args[0]; // like "http://server.company.com:9080";
		final String USERNAME = args[1];	// like "someone@company.com";
		final String PASSWORD = args[2];	// like	"pa88word";
		String roomId = args[3]; // like "e6206473-56bd-4340-9ab2-2b6a6547ad1c";
		
		Random random = new Random();		
		RoomServices roomServices = new RoomServices(SERVER);
		//roomServices.login(USERNAME, PASSWORD);
		if (roomId == null)
		{
			MeetingRoom tempRoomId = roomServices.createRoom("MegaJoin Room");
			if(tempRoomId != null)
				roomId = tempRoomId.getId();
		}
		
		int count = 5;
		if(args.length >= 5)
			count = Integer.parseInt(args[4]);		
		
		final Set<RealTimeSession> userList = new ConcurrentSkipListSet<RealTimeSession>();
		final ExecutorService executor = Executors.newFixedThreadPool(8);
		
		// setup a timer to update these users
		java.util.Timer timer = new Timer();
		
		try
		{
			// create the user's unique real-time session, log-in, and join
			for(int i = 1; i < count; i++) 
			{
				// adjust the name and password of the user
				final String userName = String.format("johnsmith%d@company.com", i);
				final RealTimeSession rtSession = new RealTimeSession(SERVER, roomId, userName);
				
				executor.submit(new Runnable()
				{
					public void run()
					{
						try
						{
							// note, no log-in, these are anonymous users
							HttpResponseWrapper resp = rtSession.join();
							
							if(rtSession.isJoined())
							{
								// keep a reference to this user, so we can later leave & shut down
								userList.add(rtSession);
								if (DEBUG_PRINTS)
									System.out.println("JOINED["+Thread.currentThread().getId()+"] userName["+rtSession.getUser()+"] userId["+rtSession.getUserId()+"] resp["+resp.toString()+"]");
							}
							else
							{
								if (DEBUG_PRINTS)
									System.out.println("JOIN FAILED["+resp.getBodyAsJson()+"]: "+userName);
							}
						} 
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				
				if(JOIN_DELAY > 0)
					Thread.sleep(JOIN_DELAY);
			}
			
			timer.scheduleAtFixedRate(new TimerTask()
			{
				public void run()
				{
					for(RealTimeSession rtSession: userList)
					{
						try 
						{
							rtSession.abortUpdate();
							if (DEBUG_PRINTS)
								System.out.println("KEEP ALIVE["+Thread.currentThread().getId()+"]: "+rtSession.getUserId());
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			}, 5000, 30000);
			
						
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Press <Enter> to terminate");
			// wait for enter
			String line = null;
			do
			{
				line = console.readLine();
			}
			while(line == null);
			
			// have all the user's leave
			for(RealTimeSession leavingUser: userList)
			{
				userList.remove(leavingUser);

				leavingUser.shutdown();
				
				if (DEBUG_PRINTS)
					System.out.println("LEFT:["+Thread.currentThread().getId()+"]"+leavingUser.getUser());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			// stop the timer
			timer.purge();
			timer.cancel();
			
			if (args[3] == null)
			{
				System.out.println("Deleting room...");
				roomServices.deleteRoom(roomId);
				System.out.println("Deleted room successfully");
			}
			roomServices.logout();
			
			// stop the thread pool
			executor.shutdownNow();
		}
	}
}
