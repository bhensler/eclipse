import com.lotus.sametime.community.LoginEvent;
import com.lotus.sametime.community.LoginListener;
import com.lotus.sametime.community.ServerAppService;
import com.lotus.sametime.core.comparch.DuplicateObjectException;
import com.lotus.sametime.core.comparch.STSession;
import com.lotus.sametime.core.types.STAttribute;
import com.lotus.sametime.core.types.STUser;
import com.lotus.sametime.core.types.STUserInstance;
import com.lotus.sametime.core.util.connection.Connection;
import com.lotus.sametime.core.util.connection.SocketConnection;
import com.lotus.sametime.lookup.LookupService;
import com.lotus.sametime.lookup.ResolveEvent;
import com.lotus.sametime.lookup.ResolveListener;
import com.lotus.sametime.lookup.Resolver;
import com.lotus.sametime.onlinedirectory.OnlineDirectoryService;
import com.lotus.sametime.userattributesa.UserAttributeSAEvent;
import com.lotus.sametime.userattributesa.UserAttributeSAListener;
import com.lotus.sametime.userattributesa.UserAttributeSAService;
import com.lotus.sametime.userattributesa.UserAttributeSAServiceEvent;


/**
 * Sample SA which uses the User Attributes SA Service to manipulate 
 * user's attributes.  
 * 
 * @author Amir Perlman, Jan 23, 2007
 *
 */
public class UserAttribSASample implements UserAttributeSAListener, 
										   LoginListener, 
										   ResolveListener {
	
	/**
	 * Sametime Session holding the required services that will be used
	 * by this SA. 
	 */
	private STSession _session;
	
	/**
	 * User attribute SA Service. 
	 */
	private UserAttributeSAService _userAttribSvc;
	
	/**
	 * The user on which we will set the attribute. 
	 */
	private String _userName; 
	
	
	/**
	 * Construct a new SA 
	 */
	public UserAttribSASample(String server, String userName) {
		
		//Keep the user name as it will be needed later. 
		_userName = userName;
		
		try {
			//Load components. 
			_session = new STSession("UserAttribSASample" + this);
			String[] comps = { ServerAppService.COMP_NAME, 
					   		   OnlineDirectoryService.COMP_NAME,
					           UserAttributeSAService.COMP_NAME,
					           LookupService.COMP_NAME };
			_session.loadComponents(comps);
			_session.start();
			
			ServerAppService saSvc = (ServerAppService) 
								     _session.getCompApi(ServerAppService.COMP_NAME);
			
			_userAttribSvc = (UserAttributeSAService) 
							 _session.getCompApi(UserAttributeSAService.COMP_NAME);
			_userAttribSvc.addUserAttributeSAListener(this);
			
			//Login as a server app
			saSvc.addLoginListener(this);
			
			// Login as a server app
			//SPR #SQLI77U7XU - adding connectivity 
			Connection[] connections = { new SocketConnection(1516, 17000), };
			saSvc.setConnectivity(connections);
			int[] supportedServices = {};

			saSvc.loginAsServerApp(server, STUserInstance.LT_SERVER_APP, 
					              "User Attributes SA", supportedServices);
		} 
		catch (DuplicateObjectException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @see UserAttributeSAListener#attributeRemoved(UserAttributeSAEvent)
	 */
	public void attributeRemoved(UserAttributeSAEvent evt) {
		System.out.println("User attribute removed successfully.");
	}

	/**
	 * @see UserAttributeSAListener#attributeSet(UserAttributeSAEvent)
	 */
	public void attributeSet(UserAttributeSAEvent evt) {
		System.out.println("User attribute updated successfully.");
	}

	/**
	 * @see UserAttributeSAListener#removeAttributeFailed(UserAttributeSAEvent)
	 */
	public void removeAttributeFailed(UserAttributeSAEvent evt) {
		System.err.println("Failed to remove a user attribute.");
	}
	
	/**
	 * @see UserAttributeSAListener#serviceAvailable(UserAttributeSAServiceEvent)
	 */
	public void serviceAvailable(UserAttributeSAServiceEvent event) {
		System.out.println("User Attributes SA Service Available");
		//Wait for service available before attempting to user the service
		
		//Resolve the user name into STUser object
		LookupService lookupSvc = (LookupService) _session.getCompApi(LookupService.COMP_NAME);
		Resolver resolver = lookupSvc.createResolver(true, false, true, false);
		
		resolver.addResolveListener(this);
		resolver.resolve(_userName);
	}

	/**
	 * @see UserAttributeSAListener#serviceUnavailable(UserAttributeSAServiceEvent)
	 */
	public void serviceUnavailable(UserAttributeSAServiceEvent event) {
		System.out.println("User Attributes SA Service Unavailable");
	}

	/**
	 * @see UserAttributeSAListener#setAttributeFailed(UserAttributeSAEvent)
	 */
	public void setAttributeFailed(UserAttributeSAEvent evt) {
		System.err.println("Failed to Set attribute, reason: " + 
							Integer.toHexString(evt.getReason()));
	}
	
	/**
	 * @see LoginListener#loggedIn(LoginEvent)
	 */
	public void loggedIn(LoginEvent event) {
		System.out.println("Server Application Logged In");
	}

	/**
	 * @see LoginListener#loggedOut(LoginEvent)
	 */
	public void loggedOut(LoginEvent event) {
		System.err.println("Logged Out, reason:" + Integer.toHexString(event.getReason()));
	}

	/**
	 * @see ResolveListener#resolveConflict(ResolveEvent)
	 */
	public void resolveConflict(ResolveEvent event) {
		System.err.println("Error failed to resolve user uniquely");
	}

	/**
	 * @see ResolveListener#resolveFailed(ResolveEvent)
	 */
	public void resolveFailed(ResolveEvent event) {
		System.err.println("Error Failed to resolver user name");
	}

	/**
	 * @see ResolveListener#resolved(ResolveEvent)
	 */
	public void resolved(ResolveEvent event) {
		
		//Inlcude a time stamp in the attribute content so we will get an 
		//event each time it is updated
		java.util.Date d = new java.util.Date();
		STAttribute attr = new STAttribute(778877, "Some exciting new value" + 
											", Date: " + d.toString());
		
		System.out.println("User Resolved successfully, updating attribute: " + 
						   attr.getKey());
		_userAttribSvc.setAttribute((STUser) event.getResolved(), attr);
		
	}

	/**
	 * Startup a SA that will update a user's attribute
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length < 2) {
			System.err.println("Error, command line parameters must include " +
					           "a server name and a user's name.");
			return;
		}
		
		new UserAttribSASample(args[0], args[1]);
	}

}
