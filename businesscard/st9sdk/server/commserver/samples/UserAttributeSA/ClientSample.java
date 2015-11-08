import com.lotus.sametime.awareness.AttributeEvent;
import com.lotus.sametime.awareness.AttributeListener;
import com.lotus.sametime.awareness.AwarenessService;
import com.lotus.sametime.awareness.WatchList;
import com.lotus.sametime.community.CommunityService;
import com.lotus.sametime.community.LoginEvent;
import com.lotus.sametime.community.LoginListener;
import com.lotus.sametime.core.comparch.DuplicateObjectException;
import com.lotus.sametime.core.comparch.STSession;
import com.lotus.sametime.core.types.STAttribute;

/**
 * A simple client application that watches the user's attributes and enables 
 * to monitor user's attributes. Together with the User Attributes SA it 
 * demonstrates how user attributes can be manipulated by a server application. 
 * 
 * @author Amir Perlman, Jan 23, 2007
 *
 */
public class ClientSample implements LoginListener, AttributeListener {

	/**
	 * Sametime Session . 
	 */
	private STSession _session;
	
	/**
	 * Construct a new client instance 
	 * @param host
	 * @param loginName
	 * @param password
	 */
	public ClientSample(String host, String loginName, String password) {
		try {
			//Load components. 
			_session = new STSession("ClientSample" + this);
			_session.loadSemanticComponents();
			_session.start();
			
			
			CommunityService commSvc = (CommunityService) 
									   _session.getCompApi(CommunityService.COMP_NAME);
			commSvc.addLoginListener(this);
			commSvc.loginByPassword(host, loginName, password);
		} 
		catch (DuplicateObjectException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Client logged in. 
	 */
	public void loggedIn(LoginEvent event) {
		System.out.println("Client logged In");
		
		
		AwarenessService svc = (AwarenessService) 
		  						_session.getCompApi(AwarenessService.COMP_NAME);
		
		//Setup the filter to include the attribute we want to watch
		int[] attrFilter = { 778877 }; 
		svc.addToAttrFilter(attrFilter);
		
		//Create a watch list and add our self to the watch list. 
		WatchList watchList = svc.createWatchList();
		watchList.addAttrListener(this);
		
		watchList.addItem(event.getLogin().getMyUserInstance());
		
		
	}

	/**
	 * Client logged out. 
	 */
	public void loggedOut(LoginEvent event) {
		System.out.println("Client logged out");
	}

	
	/**
	 * @see AttributeListener#attrChanged(AttributeEvent) 
	 */
	public void attrChanged(AttributeEvent event) {
		
		//This simple app expect only one attribute change although 
		//the event may deliver more then one change at a time. 
		STAttribute attr = event.getAttributeList()[0]; 
		System.out.println("Attribute changed: " + attr.getKey() + 
						   " Value: " + attr.getString());
	}
	
	
	/**
	 * Startup a client that will a specific attribute that will
	 * be manipulated by the server application. 
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length < 3) {
			System.err.println("Error, command line parameters must include " +
					           "a server name, user's name and password.");
			return;
		}
		
		new ClientSample(args[0], args[1], args[2]);
	}
	
	//
	//Unimplemented methods - not used by this sample app
	//
	
	/**
	 * @see AttributeListener#attrChanged(AttributeEvent) 
	 */
	public void attrContentQueried(AttributeEvent event) {
	}

	/**
	 * @see AttributeListener#attrRemoved(AttributeEvent) 
	 */
	public void attrRemoved(AttributeEvent event) {
	}
	
	/**
	 * @see AttributeListener#queryAttrContentFailed(AttributeEvent) 
	 */
	public void queryAttrContentFailed(AttributeEvent event) {
	}
	
}
