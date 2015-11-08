package com.ibm.sdk.samples;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.cookie.Cookie;

import com.ibm.json.java.JSONObject;
import com.ibm.rtc.client.http.HttpResponseWrapper;
import com.ibm.rtc.client.http.HttpServiceWrapper.Method;
import com.ibm.rtc.client.http.LoginException;
import com.ibm.rtc.client.meeting.docshare.DocInfo;
import com.ibm.rtc.client.meeting.docshare.DocShare;
import com.ibm.rtc.client.meeting.docshare.DocShareListener;
import com.ibm.rtc.client.meeting.room.AdminRoomServices;
import com.ibm.rtc.client.meeting.room.MeetingRoom;
import com.ibm.rtc.client.meeting.room.QueryControl;
import com.ibm.rtc.client.meeting.room.QueryControl.SortKeys;
import com.ibm.rtc.client.meeting.room.QueryResults;
import com.ibm.rtc.client.meeting.room.RoomException;
import com.ibm.rtc.client.meeting.room.RoomServices;
import com.ibm.rtc.client.rtc4web.RealTimeSession;
import com.ibm.rtc.client.rtc4web.RealTimeSessionException;

/**
 *  This sample demonstrates how to perform a basic series of tests on the meeting server.
 *  These tests can be helpful in identifying where a meeting server is broken,
 *  such as whether the server exists, is running, allows rooms to be created and joined,
 *  and whether documents can be shared.   
 *  
 *
 */
public class ValidatorSample
{
		
	private static  String ADMIN_PASSWORD	= null; //like "password";
	private static  String ADMIN_NAME		= null; //like "admin@company.com";
	private static  String USER_LIST = null; 		//like "USER_LIST";
	private static  String SERVER = null; 			//like "http://meetings.server.com:9080"
	
	private static  String PASSED	= "\t PASSED:";
	private static  String FAILED	= "\t FAILED:";
	
	private static final boolean showStackTrace = false;
	
	//This tells us what build date the code is running
	// static-20111105-0917
	Pattern pattern = Pattern.compile("static-\\d\\d\\d\\d\\d\\d\\d\\d-\\d\\d\\d\\d");
	Matcher matcher = pattern.matcher("");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm"); 	//20120306-1704
	
	public static void main(String[] arg) throws IOException, ParseException
	{
		SERVER = arg[0];			// like "http://server.company.com:9080";
		ADMIN_NAME	= arg[1];		// like "someone@company.com";
		ADMIN_PASSWORD = arg[2];	// like	"pa88word";
		if(arg.length > 3)
			USER_LIST = arg[3];		// like	"joe@company.com;adam@company.com;rob@company.com";
		
		ValidatorSample vs = new ValidatorSample();
		vs.run();
	}
	
	public void run() throws ParseException
	{
	
		getAllRoomsOnServer();
		deleteTestRooms();
	
		/** PINGS **/		
		testPing();		

		/** VERSIONS **/
		testVersion();
		
		/** LOGIN **/
		testLogin();
		
		/** CREATE **/
		testCreate();
				
		/** JOIN **/
		testJoin();
		
		/** DOCS **/
		testDocCreation();
		
		if(USER_LIST != null)
		{
			String[] users = USER_LIST.trim().split(";");
			testGetUsersRooms(SERVER, users);
		}
		deleteTestRooms();
	}
	
	public void getAllRoomsOnServer()
	{
		System.out.println("getAllRoomsOnServer: "+SERVER);
		RoomServices roomServices = null;
		try
		{
			roomServices = new RoomServices(SERVER);
			roomServices.login(ADMIN_NAME,ADMIN_PASSWORD);
			
			QueryControl searchParams = new QueryControl();
			searchParams.setCount(100);
			searchParams.setSortKey(SortKeys.SORT_KEY_CREATED_DATE);
			QueryResults allRooms = roomServices.search("", searchParams);
			System.out.println("allRooms: "+(allRooms.getCount()+allRooms.getRemaining()));
			int i = 1;
			for(MeetingRoom room : allRooms.getResults())
			{
				System.out.println(i++ +" owner["+room.getOwnerName()+"] org["+room.getOrg()+"] id["+room.getId()+"] created["+(new Date(room.getCreatedDate()))+"]");
			}
			
			int remaining = allRooms.getRemaining();
			int start = 100;
			while(remaining > 0)
			{
				System.out.println("---");
				searchParams.setStart(start);
				searchParams.setCount(100);
				searchParams.setSortKey(SortKeys.SORT_KEY_CREATED_DATE);
			
				allRooms = roomServices.search("", searchParams);
				for(MeetingRoom room : allRooms.getResults())
				{
					System.out.println(i++ +" owner["+room.getOwnerName()+"] org["+room.getOrg()+"] id["+room.getId()+"] created["+(new Date(room.getCreatedDate()))+"]");
				}
				remaining = allRooms.getRemaining();
				start += 100;
			}

			roomServices.logout();
		} 
		catch (MalformedURLException e1) 
		{
			if(showStackTrace) e1.printStackTrace();
		}
		catch(RoomException e)
		{
			if(showStackTrace) e.printStackTrace();
		}
		catch(LoginException e)
		{
			if(showStackTrace) e.printStackTrace();
		}
		catch (IOException e)
		{
			if(showStackTrace) e.printStackTrace();
		}
	}	
	
	public void deleteTestRooms()
	{
		System.out.println("deleteTestRooms: "+SERVER);
		RoomServices roomServices = null;
		try
		{
			roomServices = new RoomServices(SERVER);
			roomServices.login(ADMIN_NAME,ADMIN_PASSWORD);
			
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
			if(showStackTrace) e.printStackTrace();
		}
		catch(LoginException e)
		{
			System.out.println(e.getMessage());
		}
		catch (IOException e)
		{
			if(showStackTrace) e.printStackTrace();
		}
	}	
	public void testLogin()
	{
		System.out.println("testLogin: "+SERVER);
		String result = null;
		RoomServices roomServices = null;
		try
		{
			roomServices = new RoomServices(SERVER);
			roomServices.login(ADMIN_NAME, ADMIN_PASSWORD);
			result = PASSED;
		} 
		catch (MalformedURLException e)
		{
			result = FAILED + "exception["+e.getClass().getSimpleName()+"] url["+SERVER+"]: "+e.getMessage();
			if(showStackTrace) e.printStackTrace();
		} 
		catch (LoginException e)
		{
			result = FAILED + "exception["+e.getClass().getSimpleName()+"] url["+SERVER+"]: "+e.getMessage();
			if(showStackTrace) e.printStackTrace();
		} 
		catch (IOException e)
		{
			result = FAILED + "exception["+e.getClass().getSimpleName()+"] url["+SERVER+"]: "+e.getMessage();
			if(showStackTrace) e.printStackTrace();
		}
		finally
		{
			if(roomServices != null)
			{
				try
				{
					roomServices.logout();
					roomServices.shutdown();
				}
				catch (IOException e)
				{
					if(showStackTrace) e.printStackTrace();
				}
				catch (LoginException e)
				{
					if(showStackTrace) e.printStackTrace();
				}
			}
		}
		System.out.println(result);
	}
	
	//Test whether a server is up
	public void testPing()
	{
		String result = PASSED;		

		try
		{
			URL url = new URL(SERVER);
			String host = url.getHost();
			System.out.println("testPing: "+host);
			InetAddress address = InetAddress.getByName(host);
			boolean isReachable = address.isReachable(3000);
			if(!isReachable)
			{
				result = FAILED + "server["+SERVER+"] not reachable";
			}
		}
		catch (UnknownHostException e)
		{
			result = FAILED + "exception["+e.getClass().getSimpleName()+"] url["+SERVER+"]: "+e.getMessage();
			if(showStackTrace) e.printStackTrace();
		}
		catch (IOException e)
		{
			result = FAILED + "exception["+e.getClass().getSimpleName()+"] url["+SERVER+"]: "+e.getMessage();
			if(showStackTrace) e.printStackTrace();
		}
		System.out.println(result);
	}

	//This tests not the version of the server, but rather the date the code was built
	public void testVersion() throws ParseException
	{
		
		System.out.println("testVersion: "+SERVER);
		String result = FAILED;
		RoomServices roomServices = null;
		try
		{
			roomServices = new RoomServices(SERVER);
			
			// point to MS build info.
			// point to a bad room on purpose, just to get a badroom jsp, to get the static date.
			HttpResponseWrapper resp = roomServices.httpRequest(Method.GET, roomServices.getServer()+"/stmeetings/room/join/xyz123/");
			String body = resp.getBody(); //ie html body
			
			// iterate over the lines, and find the first thing that matches like: static-20111105-0917
			String match = null;
			String[] lines = body.split(System.getProperty("line.separator"));
			for(String line: lines)
			{
				matcher.reset(line);
				if(matcher.find())
				{
					// if found a match, format it to a date, print and break.
					match = matcher.group();
					Date date = dateFormat.parse(match.substring(7));
					System.out.println("\t MS: "+date);
					break;
				}
			}
			
			// point to SS build info.
			resp = roomServices.httpRequest(Method.GET, roomServices.getServer()+"/rtc/buildinfo.txt");
			body = resp.getBody();
			Date date = dateFormat.parse(body.trim());
			System.out.println("\t SS: "+date);

			result = PASSED;
		}
		catch (Exception e)
		{
			if(showStackTrace) e.printStackTrace();
			result = FAILED + "exception["+e.getClass().getSimpleName()+"] url["+SERVER+"]: "+e.getMessage();
		}
		System.out.println(result);
	}

	public void testCreate()
	{
		System.out.println("testCreate: "+SERVER);
		RoomServices roomServices = null;
		MeetingRoom createdRoom = null;
		String result = PASSED;
		try
		{
			Random rand = new Random();
			roomServices = new RoomServices(SERVER);
			roomServices.login(ADMIN_NAME, ADMIN_PASSWORD);
			createdRoom = roomServices.createRoom("test"+Long.toHexString(rand.nextLong()));
			//Change this to ignore the name, just check that something is returned
			if(createdRoom.getRoomName() == null)
			{
				result = FAILED + "Method getRoomName() returns null";
			}			
			createdRoom = roomServices.get(createdRoom.getId());
			if(createdRoom.getRoomName() == null)
			{
				result = FAILED + "Method getRoomName() returns null";
			}			

			roomServices.deleteRoom(createdRoom.getId());
		} 
		catch (MalformedURLException e)
		{
			result = FAILED + "exception["+e.getClass().getSimpleName()+"] url["+SERVER+"]: "+e.getMessage();
			if(showStackTrace) e.printStackTrace();
		} 
		catch (LoginException e)
		{
			result = FAILED + "exception["+e.getClass().getSimpleName()+"] url["+SERVER+"]: "+e.getMessage();
			if(showStackTrace) e.printStackTrace();
		} 
		catch (IOException e)
		{
			result = FAILED + "exception["+e.getClass().getSimpleName()+"] url["+SERVER+"]: "+e.getMessage();
			if(showStackTrace) e.printStackTrace();
		}
		catch (RoomException e)
		{
			result = FAILED + ((e.getHttpResponse() != null)?e.getHttpResponse():e.getMessage());
			if(showStackTrace) e.printStackTrace();
		}
		finally
		{
			if(roomServices != null)
			{
				try
				{
					roomServices.logout();
					roomServices.shutdown();
				}
				catch (IOException e)
				{
					if(showStackTrace) e.printStackTrace();
				}
				catch (LoginException e)
				{
					if(showStackTrace) e.printStackTrace();
				}
			}
		}
		System.out.println(result+((createdRoom == null)?"":createdRoom.toString()));
	}

	public void testJoin( )
	{
		System.out.println("testJoin: "+SERVER);
		RoomServices roomServices = null;
		MeetingRoom createdRoom = null;
		String result = PASSED;
		RealTimeSession realTimeSession = null;
		try
		{
			Random rand = new Random();
			roomServices = new RoomServices(SERVER);
			roomServices.login(ADMIN_NAME, ADMIN_PASSWORD);
			createdRoom = roomServices.createRoom("test"+Long.toHexString(rand.nextLong()));
			
			realTimeSession = new RealTimeSession(SERVER, createdRoom.getId(), ADMIN_NAME);
			
			realTimeSession.login(ADMIN_NAME, ADMIN_PASSWORD);
			HttpResponseWrapper join = realTimeSession.join();
			if(join.getStatus() == 200)
			{
				realTimeSession.handleUpdate(realTimeSession.getUpdate(5));
				realTimeSession.handleUpdate(realTimeSession.getUpdate(5));
				realTimeSession.handleUpdate(realTimeSession.getUpdate(5));
				
				result += "\n";
				for(Cookie cookie:realTimeSession.getCookies())
				{
					result += "\t"+cookie+"\n";
				}
				realTimeSession.leave();
			}
			else
			{
				result = FAILED + join.toString();
			}
		} 
		catch (MalformedURLException e)
		{
			result = FAILED + e.getMessage();
			if(showStackTrace) e.printStackTrace();
		} 
		catch (LoginException e)
		{
			result = FAILED + e.getMessage();
			if(showStackTrace) e.printStackTrace();
		} 
		catch (IOException e)
		{
			result = FAILED + e.getMessage();
			if(showStackTrace) e.printStackTrace();
		}
		catch (RoomException e)
		{
			result = FAILED + ((e.getHttpResponse() != null)?e.getHttpResponse():e.getMessage());
			if(showStackTrace) e.printStackTrace();
		}
		catch (RealTimeSessionException e)
		{
			result = FAILED + ((e.getHttpResponse() != null)?e.getHttpResponse():e.getMessage());
			if(showStackTrace) e.printStackTrace();
		}
		finally
		{
			try
			{
				if(roomServices != null)
				{
					roomServices.deleteRoom(createdRoom.getId());
					roomServices.logout();
					roomServices.shutdown();
				}
			}
			catch (IOException e)
			{
				if(showStackTrace) e.printStackTrace();
			}
			catch (LoginException e)
			{
				if(showStackTrace) e.printStackTrace();
			} 
			catch (RoomException e)
			{
				if(showStackTrace) e.printStackTrace();
			}
			catch(Exception e)
			{
				if(showStackTrace) e.printStackTrace();
			}

		}
		System.out.println(result);
	}

	public void testDocCreation()
	{
		System.out.println("testDocCreation: "+SERVER);
		RoomServices roomServices = null;
		MeetingRoom createdRoom = null;
		String result = PASSED;
		RealTimeSession realTimeSession = null;
		try
		{
			Random rand = new Random();
			roomServices = new RoomServices(SERVER);
			
			roomServices.login(ADMIN_NAME, ADMIN_PASSWORD);
			createdRoom = roomServices.createRoom("test"+Long.toHexString(rand.nextLong()));
			
			realTimeSession = new RealTimeSession(SERVER, createdRoom.getId(), ADMIN_NAME);
			
			realTimeSession.login(ADMIN_NAME, ADMIN_PASSWORD);
			HttpResponseWrapper join = realTimeSession.join();
			if(join.getStatus() == 200)
			{
				DocShare docShare = new DocShare(realTimeSession);
				DocShareHandler docShareHandler = new DocShareHandler();
				docShare.addListener(docShareHandler);
				
				docShare.createBlankDocument("testDoc.rtf", 3, 11, 8);
				long start = System.currentTimeMillis();
				while(!docShareHandler.conversionComplete)
				{
					realTimeSession.handleUpdate(realTimeSession.getUpdate(5));
					if((System.currentTimeMillis() - start) > 1000*30)
					{
						break;
					}
				}
				
				if(docShareHandler.conversionComplete == false)
					result = FAILED;
				
				realTimeSession.leave();
			}
			else
			{
				result = FAILED + join.toString();
			}
		} 
		catch (Exception e)
		{
			try
			{
				HttpResponseWrapper resp = roomServices.httpRequest(Method.GET, roomServices.getServer()+"/stmeetings/policy?format=json");
				JSONObject bodyAsJson = resp.getBodyAsJson();
				if(bodyAsJson != null)
					bodyAsJson.serialize(System.out,true);
			} 
			catch (Exception e1)
			{
				result = FAILED + " exception["+e.getClass().getSimpleName()+"] url["+SERVER+"]: "+e.getMessage();
				if(showStackTrace) e.printStackTrace();
			}
		}
		finally
		{
			try
			{
				if(roomServices != null)
				{
					roomServices.deleteRoom(createdRoom.getId());
					roomServices.logout();
					roomServices.shutdown();
				}
			}
			catch (IOException e)
			{
				if(showStackTrace) e.printStackTrace();
			}
			catch (LoginException e)
			{
				if(showStackTrace) e.printStackTrace();
			} 
			catch (RoomException e)
			{
				if(showStackTrace) e.printStackTrace();
			}
			catch(Exception e)
			{
				if(showStackTrace) e.printStackTrace();
			}			
		}
		System.out.println(result);
	}	

	public void testGetUsersRooms(String server, String[] users)
	{
		System.out.println("testGetUsersRooms: "+server);
		AdminRoomServices roomServices = null;
		String result = PASSED;
		try
		{
			roomServices = new AdminRoomServices(server);
			roomServices.login(ADMIN_NAME, ADMIN_PASSWORD);
			
			for(String user: users)
			{
				QueryResults roomsByOwner = roomServices.getRoomsByOwner(user, QueryControl.getDefault());
				if(roomsByOwner.getResults().size() == 0)
				{
					System.out.println(FAILED +" NO ROOM for user ["+user+"]");
				}
				else
				{
					System.out.println(PASSED +" FOUND ROOM for user ["+user+"]");
				}
			}
		}
		catch (RoomException e)
		{
			result = FAILED + e.getMessage();
			if(showStackTrace) e.printStackTrace();
		} 		
		catch (MalformedURLException e)
		{
			result = FAILED + e.getMessage();
			if(showStackTrace) e.printStackTrace();
		} 
		catch (LoginException e)
		{
			result = FAILED + e.getMessage();
			if(showStackTrace) e.printStackTrace();
		} 
		catch (IOException e)
		{
			result = FAILED + e.getMessage();
			if(showStackTrace) e.printStackTrace();
		}
		finally
		{
			try
			{
				if(roomServices != null)
				{
					roomServices.logout();
					roomServices.shutdown();
				}
			}
			catch (IOException e)
			{
				if(showStackTrace) e.printStackTrace();
			}
			catch (LoginException e)
			{
				if(showStackTrace) e.printStackTrace();
			}
		}
		System.out.println(result);
	}	

	private final class DocShareHandler implements DocShareListener
	{
		boolean conversionComplete = false;
		
		public void handleError(RealTimeSession rtSession,	HttpResponseWrapper response, Exception e){}

		@Override
		public void documentUpdate(String sid, String docId, String docKey,	DocInfo docInfoObj) {}

		@Override
		public void documentRemoved(String sid, String docId) {}

		@Override
		public void documentConversionProgress(String sid, String docId, int pageCount, DocInfo docInfo)
		{
		}

		@Override
		public void documentConversionComplete(String sid, String docId, DocInfo docInfo)
		{
			conversionComplete = true;
		}

		@Override
		public void documentAdded(String sid, DocInfo docInfo) {}
	}
}
