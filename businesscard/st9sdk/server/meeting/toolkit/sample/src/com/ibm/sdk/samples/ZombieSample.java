package com.ibm.sdk.samples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import com.ibm.rtc.client.http.HttpResponseWrapper;
import com.ibm.rtc.client.http.LoginException;
import com.ibm.rtc.client.meeting.annotations.AnnotationInfo;
import com.ibm.rtc.client.meeting.annotations.Annotations;
import com.ibm.rtc.client.meeting.annotations.AnnotationsException;
import com.ibm.rtc.client.meeting.annotations.AnnotationInfo.AnnotationType;
import com.ibm.rtc.client.meeting.chat.Chat;
import com.ibm.rtc.client.meeting.chat.ChatMessage;
import com.ibm.rtc.client.meeting.chat.ChatMessageListener;
import com.ibm.rtc.client.meeting.docshare.AbstractDocShareListener;
import com.ibm.rtc.client.meeting.docshare.DocInfo;
import com.ibm.rtc.client.meeting.docshare.DocShare;
import com.ibm.rtc.client.meeting.projector.Projector;
import com.ibm.rtc.client.meeting.projector.ProjectorListener;
import com.ibm.rtc.client.meeting.projector.Projector.MediaType;
import com.ibm.rtc.client.meeting.room.MeetingRoom;
import com.ibm.rtc.client.meeting.room.RoomException;
import com.ibm.rtc.client.meeting.room.RoomServices;
import com.ibm.rtc.client.rtc4web.RealTimeSession;
/**
 * This sample demonstrates a "bot like" user who can do things based on chat messages
 * Join another real user into a room, and share a document.
 * The zombie will scribble on the slides ... and follows a few commands
 */
public class ZombieSample
{
	protected boolean zombieRunning = true;
	private static boolean zombieLeave = false;
	private boolean slidesAreShared = false;
	protected int currentPage = -1;

	int[][] A = 	{{20,90,  50,10, 80,90},{70,40, 20,60}};
	int[][] B = 	{{20,90,  20,10, 60,10, 80,20, 70,40, 40,50, 40,40, 80,60, 70,90, 30,80}};
	int[][] C = 	{{80,30,  80,20, 60,10, 30,10, 20,30, 20,70, 30,90, 60,90, 80,70}};
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
	
	final String SERVER; 	//= args[1]; 	// like "http://SERVER.company.com:9080";
	final String USERNAME; 	//= args[2]; 	// like "user@company.com";
	final String PASSWORD;	//= args[3]; 	// like "passw0rd";
	String ROOMID; 	//= args[0]; 	// like "e6206473-56bd-4340-9ab2-2b6a6547ad1c";
	
	RealTimeSession room = null;
	String documentId = null;
	Chat chat = null;
	Projector projector = null;
	Annotations annotations = null;
	DocShare docShare = null;
	

	static class HtmlStripper
	{
		final static Pattern pattern = Pattern.compile("\\<.*?>", Pattern.DOTALL);
		
		public static String stripHtml(String html)
		{
			String stripped = null;
			
			stripped = pattern.matcher(html).replaceAll("");
			// something like apache commons can be used to strip out the entity encodings
			// stripped = StringEscapeUtils.unescapeHtml(stripped);
			
			return stripped;
		}
	}
	
	public ZombieSample(String[] args)
	{
		SERVER 		= args[0]; 	// like "http://SERVER.company.com:9080";
		USERNAME 	= args[1]; 	// like "user@company.com";
		PASSWORD 	= args[2]; 	// like "passw0rd";
		ROOMID 		= args[3]; 	// like "e6206473-56bd-4340-9ab2-2b6a6547ad1c";
	}

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
			
			// send the strokes which form the letter, to the SERVER
			annotations.createAnnotations(letterStrokes);
			
			// save letter in case this needs to be selected later for a transform
			word.addAll(letterStrokes);
		}
		return word;
	}
	
	public void run() throws InterruptedException, LoginException, IOException, RoomException
	{		
		//		set pen color, etc.
		final AnnotationInfo prototypeStroke = new AnnotationInfo();
		prototypeStroke.setType(AnnotationType.hl);
		prototypeStroke.setDocId(documentId);
		prototypeStroke.setPageNumber(currentPage);
		prototypeStroke.setColor(RED);
		
		room = new RealTimeSession(SERVER, ROOMID, USERNAME);
		
		// create our chat & projector object
		chat = new Chat(room);
		projector = new Projector(room);
		annotations = new Annotations(room);
		docShare = new DocShare(room);
		
		// add a docShare listener, for page changes
		docShare.addListener(new AbstractDocShareListener(){
			@Override
			public void documentUpdate(String sid, String docId, String docKey, DocInfo docInfoObj) {
				currentPage = docInfoObj.getCurrentPage();
			}
		});

		// add our listeners
		chat.addListener(new ChatMessageListener() {			
			@Override
			public void onMessageUpdated(String sid, String messageId, ChatMessage messageInfoObj) {
				String html = messageInfoObj.getMessage();
				System.out.println("html: "+ html);
				String plainText = HtmlStripper.stripHtml(html);
				System.out.println("plainText: "+ plainText);
				
				if(plainText.contains("zombie stop")){
					zombieRunning = false;
				}
				else if(plainText.contains("zombie start")){
					zombieRunning = true;
					doScribble(prototypeStroke, (int[][][])null);
				}
				else if(plainText.contains("zombie say")){
					String sayStr = plainText.substring("zombie say".length()).trim();
					sayStr = sayStr.toLowerCase();
					System.out.println("Zombie asked to speak ["+sayStr+"]");
					
					int[][][] letters = new int[sayStr.length()][][];
					for(int i = 0; i < sayStr.length(); i++)
					{
						letters[i] = getDataForLetter(sayStr.charAt(i));
					}
					doScribble(prototypeStroke, letters);
				}
				else if(plainText.contains("zombie leave")){
					zombieLeave = true;
				}
			}
			
			private int[][] getDataForLetter(char charAt) {
				switch(charAt)
				{
				case 'a': return A;
				case 'b': return B;
				case 'c': return C;
				case 'd': return D;
				case 'e': return E;
				case 'f': return F;
				case 'g': return G;
				case 'h': return H;
				case 'i': return I;
				case 'j': return J;
				case 'k': return K;
				case 'l': return L;
				case 'm': return M;
				case 'n': return N;
				case 'o': return O;
				case 'p': return P;
				case 'q': return Q;
				case 'r': return R;
				case 's': return S;
				case 't': return T;
				case 'u': return U;
				case 'v': return V;
				case 'w': return W;
				case 'x': return X;
				case 'y': return Y;
				case 'z': return Z;
				}
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void onMessagesCleared(String sid) {
			}

			@Override
			public void handleError(RealTimeSession rtSession, HttpResponseWrapper response, Exception e) {
			}
		});

		projector.addListener(new ProjectorListener() {			
			@Override
			public void projectorChanged(Projector projector) {
				// if some one is sharing slides ... 
				if((projector.getPresenter() != null) && (projector.getMediaType().equals(MediaType.Slides)))
				{
					slidesAreShared = true;
					documentId = projector.getMediaId();
				}
				else
				{
					slidesAreShared = false;
				}
			}
		});
		
		room.setUser("ZOMBIE");
		HttpResponseWrapper join_response = room.join();
		System.out.println("response: "+join_response);
		room.startUpdates();
		
		int time = 0;
		while(!zombieLeave)
		{
			Thread.sleep(100);
			time = time + 100;
			
			if(time > 1000 * 10)
			{
				doScribble(prototypeStroke, (int[][][])null);				
				time = 0;
			}
		}
		
		// this does a clean, and complete clean-up
		room.shutdown();
	}

	private void doScribble(final AnnotationInfo prototypeStroke, int[][]... letters) {
		if(slidesAreShared && zombieRunning)
		{
			try {
				prototypeStroke.setDocId(documentId);
				prototypeStroke.setPageNumber(currentPage);
				if(letters == null)
					write(20, 20, prototypeStroke, B, R, A, I, N, S);
				else
					write(20, 20, prototypeStroke, letters);
					
				
				Thread.sleep(2000);
				
				annotations.deleteAllOnDocument(documentId);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException, LoginException, IOException, RoomException
	{
		ZombieSample zombie = new ZombieSample(args);
		zombie.run();
	}
}
