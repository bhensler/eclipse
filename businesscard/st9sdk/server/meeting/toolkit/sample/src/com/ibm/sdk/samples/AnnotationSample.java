package com.ibm.sdk.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.ibm.rtc.client.http.HttpResponseWrapper;
import com.ibm.rtc.client.meeting.annotations.AnnotationInfo;
import com.ibm.rtc.client.meeting.annotations.AnnotationInfo.AnnotationType;
import com.ibm.rtc.client.meeting.annotations.Annotations;
import com.ibm.rtc.client.meeting.annotations.AnnotationsException;
import com.ibm.rtc.client.meeting.annotations.AnnotationsListener;
import com.ibm.rtc.client.meeting.docshare.AbstractDocShareListener;
import com.ibm.rtc.client.meeting.docshare.DocInfo;
import com.ibm.rtc.client.meeting.docshare.DocShare;
import com.ibm.rtc.client.meeting.docshare.UploadInfo;
import com.ibm.rtc.client.meeting.projector.Projector;
import com.ibm.rtc.client.meeting.projector.Projector.MediaType;
import com.ibm.rtc.client.rtc4web.RealTimeSession;
import com.ibm.rtc.client.rtc4web.RealTimeSession.BatchMode;

/**
 * This sample simulates a live brainstorming session on a white board. 
 * Observe the sample in action by joining a real client to a pre-existing meeting room.
 * The value of this sample is seeing it run in the meeting room, then inspecting the code 
 * used to generate the page changes, strokes, and transformations such as move and resize.
 * 
 * Before running this sample you must:
 * Create a meeting room and join it. 
 * Use your browser (or your preferred program) to view the page source in the room. 
 * There will be a var called "MeetingIdSession".
 * The value of that var is the "ROOMID" argument for this sample.
 */

public class AnnotationSample
{
	int[][] A = 	{{20,90, 50,10, 80,90},{70,40, 20,60}};
	int[][] B = 	{{20,90, 20,10, 60,10, 80,20, 70,40, 40,50, 40,40, 80,60, 70,90, 30,80}};
	int[][] C = 	{{80,30, 80,20, 60,10, 30,10, 20,30, 20,70, 30,90, 60,90, 80,70}};
	int[][] D = 	{{30,10, 30,50, 20,90}, {20,20, 40,10, 60,10, 70,20, 80,70, 70,90, 50,90, 20,70}};
	int[][] E = 	{{30,10, 30,40, 20,90}, {20,20, 80,20},{20,50, 80,40},{20,90, 70,80}};
	int[][] F = 	{{30,10, 30,40, 20,90}, {20,20, 80,20},{20,50, 80,40}};
	int[][] G = 	{{70,30, 60,10, 30,10, 20,30,  20,70, 30,90, 60,90, 80,60},{50,70, 90,60}};
	int[][] H = 	{{30,10, 30,40, 20,90}, {20,50, 80,40}, {60,90, 70,10}};
	int[][] I = 	{{30,10, 30,40, 20,90}};
	int[][] J = 	{{50,10, 60,60, 60,80, 50,90, 30,90, 20,70},{20,20, 50,20, 80,10}};
	int[][] K = 	{{30,10, 30,40, 20,90}, {20,50, 80,10},{20,50, 70,90}};
	int[][] L = 	{{30,10, 30,40, 20,90}, {20,90, 70,80}};
	int[][] M = 	{{30,10, 30,40, 20,90}, {20,20, 50,40, 80,20}, {60,90, 70,10}};
	int[][] N = 	{{30,10, 30,40, 20,90}, {20,20, 70,90}, {70,90, 70,10}};
	int[][] O = 	{{80,30, 80,20, 60,10, 30,10, 20,30, 20,70, 30,90, 60,90, 80,70, 80,30}};
	int[][] P = 	{{10,90, 20,10, 60,10, 80,20, 70,40, 40,50, 20,40}};
	int[][] Q = 	{{80,30, 80,20, 60,10, 30,10, 20,30, 20,70, 30,90, 60,90, 80,70, 80,30},{50,50, 70,60, 90,90}};
	int[][] R = 	{{10,90, 20,10, 60,10, 80,20, 70,40, 40,50, 20,40}, {10,30, 70,90}};
	int[][] S = 	{{80,40, 70,20, 50,10, 30,10, 20,30, 30,50, 70,50, 80,60, 80,70, 60,90, 30,90, 20,80}};
	int[][] T = 	{{50,10, 50,90}, {20,20, 50,20, 80,10}};
	int[][] U = 	{{20,10, 20,50, 40,90, 60,90, 80,70, 80,10},{80,60, 90,90}};
	int[][] V = 	{{10,10, 20,50, 50,90, 70,40, 70,10}};
	int[][] W = 	{{20,10, 40,90, 50,60, 60,90, 80,10}};
	int[][] X = 	{{20,10, 80,90},{80,10, 20,90}};
	int[][] Y = 	{{20,10, 40,40, 50,40, 80,10, 60,40, 50,90}};
	int[][] Z = 	{{20,20, 70,10, 80,10, 50,30, 20,70, 30,80, 80,70}};

	int[] RED	=	{255, 0, 0};
	int[] GREEN	=	{0, 255, 0};
	int[] BLUE	=	{0, 0, 255};
	int[] BLACK	=	{0, 0, 0};
	int[] WHITE	=	{255, 255, 255};
	
	private static String SERVER;   	// like "http://server.company.com:9080";
	private static String USERNAME; 	// like "someone@company.com";
	private static String PASSWORD;		// like	"pa88word";
	private static String ROOMID;		// like "e6206473-56bd-4340-9ab2-2b6a6547ad1c";
	
	RealTimeSession room = null;
	Annotations 	annotations = null;
	DocShare 		docShare = null;
	DocInfo			document = null;

	String	uploadId = null;

	static boolean HTTP_CLIENT_DIAGS = false;
	
	private List<AnnotationInfo> getAnnotationsForStrokes(int xOffset, int yOffset, AnnotationInfo prototypeStroke, int[][] letterStrokes)
	{
		List<AnnotationInfo> annotationInfoList = new ArrayList<AnnotationInfo>();
		
		int[] buffer = null;
		AnnotationInfo annotationInfo = null;
		for (int r=0; r < letterStrokes.length; r++)
		{
			// clone the annotation
			annotationInfo = new AnnotationInfo(prototypeStroke);
			
			//	Note, as an optimization, this is a single pass which does:
			//	- we copy into the buffer
			//	- we adjust x, y - as needed based on offset adjustment
			//	- the buffer is initially set as a reference, but will be created new if needed.
			buffer = letterStrokes[r];
			if((xOffset > 0) || (yOffset > 0))
			{
				// prepare a buffer copy so we're NOT changing original letter stroke values
				buffer = new int[letterStrokes[r].length];
				for(int i = 0; i < letterStrokes[r].length; i++)
				{
					// do x, then increment, and do y
					buffer[i] 	= (xOffset > 0)? letterStrokes[r][i] + xOffset : letterStrokes[r][i];
					buffer[++i] = (yOffset > 0)? letterStrokes[r][i] + yOffset : letterStrokes[r][i];
				}	
			}
			
			// the buffer is either the original points (if no offset was needed), 
			// or a copy, which is adjusted, as needed. 
			annotationInfo.setPoints(buffer);
			
			// add it to our list
			annotationInfoList.add(annotationInfo);
		}
		return annotationInfoList;
	}
	
	public void pause(int milliSeconds)
	{
		try
		{
			Thread.sleep(milliSeconds);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}		
	}

	private List<AnnotationInfo> write(int x, int y, AnnotationInfo prototype, int[][]... letters) throws IOException, AnnotationsException
	{
		List<AnnotationInfo> word = new ArrayList<AnnotationInfo>();
		for(int[][] letter : letters)
		{
			List<AnnotationInfo> letterStrokes = getAnnotationsForStrokes(x, y, prototype, letter);
			
			// find the max X value, to help position the next letter
			int maxX = 0;
			for(AnnotationInfo a : letterStrokes)
			{
				int xWidth = a.getX()+a.getWidth();
				if(xWidth > maxX)
					maxX = xWidth;
			}
			x = maxX;
			
			// send the strokes which form the letter, to the server
			annotations.createAnnotations(letterStrokes);
			
			// simulate a pause between letters
			pause(500);
			
			// save letter in case this needs to be selected later for a transform
			word.addAll(letterStrokes);
		}
		return word;
	}
	
	/**
	 * @throws Exception
	 */
	public void runSample() throws Exception
	{
		RealTimeSession room = new RealTimeSession(SERVER, ROOMID, USERNAME);
		room.login(USERNAME,PASSWORD);
		
		//	Set to BatchMode.REAL_BATCH for use against server version 8.5.2 IFR or greater.  
		//	Set to BatchMode.PSEUDO_BATCH for use against server version 8.5.2 or less.  
		room.setBatchMode(BatchMode.REAL_BATCH);
		
		annotations = new Annotations(room);
		annotations.addListener(new AnnotationsListener()
		{
			// called if there is an async error
			@Override
			public void handleError(RealTimeSession rtSession, HttpResponseWrapper response, Exception e)
			{
				System.out.println("handleError: id["+rtSession.getSid()+"] resp["+response+"] exception["+e+"]");
			}
			
			// called if an annotation is removed/deleted
			@Override
			public void annotationRemoved(String sid, AnnotationInfo annotationInfoObj)
			{
				System.out.println("annotationRemoved: sid["+sid+"] annotationInfoObj");
			}
			
			// called if an annotation is added or changed
			@Override
			public void annotationChanged(String sid, AnnotationInfo annotationInfoObj)
			{
				System.out.println("annotationChanged: sid["+sid+"] annotationInfoObj["+annotationInfoObj+"]");
			}
			
			// called if all of the annotations are removed
			@Override
			public void allAnnotationsRemoved(String sid)
			{
				System.out.println("allAnnotationsRemoved: sid["+sid+"]");
			}
		});

		
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Press <Enter> to join the room");
		// wait for enter
		String lineIn = null;
		do
		{
			lineIn = console.readLine();
		}
		while(lineIn == null);
		System.out.println("starting");
		
		// now lets join the room
		System.out.println("joining room");		
		HttpResponseWrapper joinResponse = room.join();
		System.out.println("join response: "+joinResponse);

		
		docShare = new DocShare(room);
		Projector proj = new Projector(room);
				
		//realTimeSession.setUser("demo-user");		
		room.startUpdates();
		
		final CountDownLatch fileReady = new CountDownLatch(1);
		docShare.addListener(new AbstractDocShareListener()
		{
			public void documentConversionComplete(String sid, String docId, DocInfo docInfo)
			{
				if(docInfo.getId().equals(uploadId))
				{
					fileReady.countDown();
					document = docInfo;
				}
			}
		});
		UploadInfo uploadInfo = docShare.createBlankDocument("Engineering_Plan", 3, 21, 15);
		uploadId = uploadInfo.getId();
	
		if(!fileReady.await(60, TimeUnit.SECONDS))
		{
			System.err.println("file was not processed in 60s");
			System.exit(0);
		}

		proj.present(document.getId(), document.getFileName(), MediaType.Slides);
		
		//		set pen color, etc.
		AnnotationInfo prototypeStroke = new AnnotationInfo();
		prototypeStroke.setType(AnnotationType.hl);
		prototypeStroke.setDocId(document.getId());
		prototypeStroke.setPageNumber(1);
		prototypeStroke.setColor(BLACK);

		//		page1, write "Organization", in top left corner
		write(20, 20, prototypeStroke, O,R,G,A,N,I,Z,A,T,I,O,N);
		pause(2000);
		
		//		go to page 2
		//		page2, write "Schedule", in top left corner
		docShare.goToPage(document.getId(), 2);
		prototypeStroke.setPageNumber(2);
		write(20, 20, prototypeStroke, S,C,H,E,D,U,L,E);
		pause(2000);

		//		go to page 3
		//		page3, write "Tasks", in top left corner
		docShare.goToPage(document.getId(), 3);
		prototypeStroke.setPageNumber(3);
		write(20, 20, prototypeStroke, T,A,S,K,S);
		pause(2000);

		//		page back, to page 2 (see "Schedule" is showing)
		docShare.goToPage(document.getId(), 2);
		pause(2000);

		//		page back, to page 1 (see "Organization" is showing)
		docShare.goToPage(document.getId(), 1);
		pause(2000);

		//
		//		Hand off the pen, while on page 1 (Organization)
		//		on page1 (Organization), write "Hardware"  "Firmware" left/center screen
		prototypeStroke.setPageNumber(1);
		prototypeStroke.setColor(RED);
		List<AnnotationInfo> strokes1 = write(200, 300, prototypeStroke, H,A,R,D,W,A,R,E);
		List<AnnotationInfo> strokes2 = write(900, 300, prototypeStroke, F,I,R,M,W,A,R,E);
		List<AnnotationInfo> strokes3 = null;
		List<AnnotationInfo> strokes4 = null;
		
		pause(2000);
		
		//		select "Hardware" and "Firmware" and move them up, and to the right as a set.
		strokes1.addAll(strokes2);
		annotations.moveGroup(strokes1, 500, 150);

		//		This gives us space to write "Web" on the left, so we now have 3 columns
		//		Then Add "UI/Design" on the on the right.
		write(100, 150, prototypeStroke, W,E,B);
		pause(2000);
		
		// change pen color
		prototypeStroke.setColor(BLUE);

		//		Under Hardware, write:  "mechanic", "electric"
		strokes1 = write(600, 250, prototypeStroke, M,E,C,H,A,N,I,C,A,L);
		strokes2 = write(600, 350, prototypeStroke, E,L,E,C,T,R,I,C,A,L);
		pause(1000);
		
		// combine and resize these by 75%
		strokes1.addAll(strokes2);
		annotations.resizeGroup(strokes1, 0.75);
		pause(1000);
		
		// select and move
		annotations.moveGroup(strokes1, 500, 250);
		pause(1000);
		
		//		Under Firmware:  "OS", "Drivers", "Application"
		strokes1 = write(1300, 250, prototypeStroke, O,S);
		strokes2 = write(1300, 350, prototypeStroke, D,R,I,V,E,R,S);
		strokes3 = write(1300, 450, prototypeStroke, A,P,P,S);
		pause(1000);
		
		// combine and resize these
		strokes1.addAll(strokes2);
		strokes1.addAll(strokes3);
		annotations.resizeGroup(strokes1, 0.75);
		pause(1000);
		
		// select and move
		annotations.moveGroup(strokes1, 1200, 250);
		pause(1000);

		//		Under Web: "Front End", "Back End" "DB" "Architecture"
		strokes1 = write(100, 250, prototypeStroke, F,R,O,N,T);
		strokes2 = write(100, 350, prototypeStroke, B,A,C,K);
		strokes3 = write(100, 450, prototypeStroke, D,B);
		strokes4 = write(100, 550, prototypeStroke, A,R,C,H);
		pause(1000);

		// combine and resize these
		strokes1.addAll(strokes2);
		strokes1.addAll(strokes3);
		strokes1.addAll(strokes4);
		annotations.resizeGroup(strokes1, 0.75);

		pause(5000);

		//		page forward to page2 (Schedule)
		docShare.goToPage(document.getId(), 2);

		// adjust the pen
		prototypeStroke.setPageNumber(2);
		prototypeStroke.setColor(RED);

		// LINE
		List<AnnotationInfo> selectedLines = new ArrayList<AnnotationInfo>();
		prototypeStroke.setColor(BLACK);
		AnnotationInfo line = new AnnotationInfo(prototypeStroke);
		selectedLines.add(line);
		line.setPoints(new int[]{880,120, 870, 1200});
		annotations.createAnnotation(line);
		pause(1000);

		 
		// "JULY"
		prototypeStroke.setColor(RED);
		List<AnnotationInfo> selectedMonths = write(900, 150, prototypeStroke, J,U,L,Y);
		pause(1000);
		
		// LINE
		prototypeStroke.setColor(BLACK);
		line = new AnnotationInfo(prototypeStroke);
		selectedLines.add(line);
		line.setPoints(new int[]{1260,120,1260,1200});
		annotations.createAnnotation(line);
		pause(1000);

		// "AUG"
		prototypeStroke.setColor(RED);
		selectedMonths.addAll(write(1300, 150, prototypeStroke, A,U,G));
		pause(1000);

		// LINE
		prototypeStroke.setColor(BLACK);
		line = new AnnotationInfo(prototypeStroke);
		selectedLines.add(line);
		line.setPoints(new int[]{1600,120, 1613, 1200});
		annotations.createAnnotation(line);
		pause(1000);
		
		// "SEPT"
		prototypeStroke.setColor(RED);
		selectedMonths.addAll(write(1650, 150, prototypeStroke, S,E,P,T));
		pause(1000);

		// LINE
		prototypeStroke.setColor(BLACK);
		line = new AnnotationInfo(prototypeStroke);
		selectedLines.add(line);
		line.setPoints(new int[]{2000,120, 2000, 1200});
		annotations.createAnnotation(line);
		pause(1000);
		
		// Job Desc
		prototypeStroke.setColor(BLUE);
		List<AnnotationInfo> selectedWords = write(10,  250, prototypeStroke, J,O,B);
		selectedWords.addAll(write(310, 250, prototypeStroke, D,E,S,C));
		pause(1000);
		
		// ARROW
		prototypeStroke.setColor(BLACK);
		int[][] arrow = {{900,300, 1100,289},{1050,250, 1100,310}, {1050,350, 1100,306}};
		List<AnnotationInfo> selectedArrows = getAnnotationsForStrokes(0, 0, prototypeStroke, arrow);
		annotations.createAnnotations(selectedArrows);
		pause(1000);

		// Prod Desc
		prototypeStroke.setColor(BLUE);
		selectedWords.addAll(write(10,  400, prototypeStroke, P,R,O,D));
		selectedWords.addAll(write(400, 400, prototypeStroke, D,E,S,C));
		pause(1000);
		
		// ARROW
		prototypeStroke.setColor(BLACK);
		arrow = new int[][]{{1000,448, 1200,456},{1150,400, 1200,448}, {1150,500, 1200,453}};
		List<AnnotationInfo> tempArrows = getAnnotationsForStrokes(0, 0, prototypeStroke, arrow);
		annotations.createAnnotations(tempArrows);
		selectedArrows.addAll(tempArrows);
		pause(1000);

		// Resumes
		prototypeStroke.setColor(BLUE);
		selectedWords.addAll(write(10,  550, prototypeStroke, R,E,S,U,M,E,S));
		pause(1000);
		
		// ARROW
		prototypeStroke.setColor(BLACK);
		arrow = new int[][]{{1100,600, 1300,600},{1250,550, 1300,600}, {1250,650, 1300,600}};
		tempArrows = getAnnotationsForStrokes(0, 0, prototypeStroke, arrow);
		annotations.createAnnotations(tempArrows);
		selectedArrows.addAll(tempArrows);
		pause(1000);

		// Resumes
		prototypeStroke.setColor(BLUE);
		selectedWords.addAll(write(10,  700, prototypeStroke, I,N,T,E,R,V,I,E,W,S));
		pause(1000);

		// ARROW
		prototypeStroke.setColor(BLACK);
		arrow = new int[][]{{1200,750, 1400,750},{1350,700, 1400,750}, {1350,800, 1400,750}};
		tempArrows = getAnnotationsForStrokes(0, 0, prototypeStroke, arrow);
		annotations.createAnnotations(tempArrows);
		selectedArrows.addAll(tempArrows);
		pause(1000);

		// Offers
		prototypeStroke.setColor(BLUE);
		selectedWords.addAll(write(10,  850, prototypeStroke, O,F,F,E,R,S));
		pause(1000);

		// ARROW
		prototypeStroke.setColor(BLACK);
		arrow = new int[][]{{1300,900, 1500,900},{1450,850, 1500,900}, {1450,950, 1500,900}};
		tempArrows = getAnnotationsForStrokes(0, 0, prototypeStroke, arrow);
		annotations.createAnnotations(tempArrows);
		selectedArrows.addAll(tempArrows);
		pause(1000);

		// Start
		prototypeStroke.setColor(BLUE);
		selectedWords.addAll(write(10, 1000, prototypeStroke, S,T,A,R,T));
		pause(1000);
		
		// ARROW
		prototypeStroke.setColor(BLACK);
		arrow = new int[][]{{1400,1050, 1600,1050},{1550,1000, 1600,1050}, {1550,1100, 1600,1050}};
		tempArrows = getAnnotationsForStrokes(0, 0, prototypeStroke, arrow);
		annotations.createAnnotations(tempArrows);
		selectedArrows.addAll(tempArrows);
		pause(1000);

		//		Select all words (job desc -> start)
		//		page forward to page3 (Tasks)
		docShare.goToPage(document.getId(), 3);

		
		//	paste copy of all 6 words, by simply changing the page
		// note we also save a copy, before making the change, so we can later select all, and resize
		List<AnnotationInfo> copiedWords = new ArrayList<AnnotationInfo>();
		for(AnnotationInfo a: selectedWords)
		{
			// create a copy/clone, and save it, before it's pasted
			AnnotationInfo aCopy = new AnnotationInfo(a);
			aCopy.setPageNumber(3);
			copiedWords.add(aCopy);
		}
		
		// Paste the copied words onto page3
		// the "create" will set new sequence numbers, 
		// so these become new annotations on the 3rd page
		annotations.createAnnotations(copiedWords);		
		
		//		put initials "KB" by 1,2
		//		put sapna by 3,4
		//		put JB by 5,6
		prototypeStroke.setPageNumber(3);
		prototypeStroke.setColor(RED);
		write(800, 250, prototypeStroke, K,B);
		pause(1000);

		write(850, 400, prototypeStroke, K,B);
		pause(1000);
				
		write(800,  700, prototypeStroke, S,A,P,N,A);
		pause(1000);

		write(800, 1000, prototypeStroke, J,B);
		pause(2000);

		//		page back to page2 (Schedule)
		//		select everything except the word schedule, and resize it all, 66% of original size
		docShare.goToPage(document.getId(), 2);
		
		selectedWords.addAll(selectedMonths);
		selectedWords.addAll(selectedLines);
		selectedWords.addAll(selectedArrows);
		
		annotations.resizeGroup(selectedWords, 0.66);
				
		//
		//		WE'RE ALL DONE, NOW Wait, the clean-up, log out
		//
		pause(20000);
		
		if(uploadId != null)
		{
			annotations.deleteAllOnDocument(uploadId);
		}
		
		if(docShare != null)
		{
			docShare.deleteFile(uploadId);
		}
		
		room.shutdown();
		System.out.println("Sample Complete");
	}
	
	public static void main(String [] args) throws Exception
	{
		
		SERVER   = args[0];	// like "http://server.company.com:9080";
		USERNAME = args[1];	// like "someone@company.com";
		PASSWORD = args[2];	// like	"pa88word";
		ROOMID  = args[3];	// like "b0ec0012-4d27-4b45-b961-4eeac8ab6275";
		
		HTTP_CLIENT_DIAGS = false;
		
		if(HTTP_CLIENT_DIAGS)
		{
			System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
		}
		
		AnnotationSample sample = new AnnotationSample();
		sample.runSample();
	}
}