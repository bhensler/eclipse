
import java.util.ArrayList;

import com.lotus.sametime.cachedresolvemanager.CachedResolveComp;
import com.lotus.sametime.cachedresolvemanager.api.CachedResolveErrorEvent;
import com.lotus.sametime.cachedresolvemanager.api.CachedResolveListener;
import com.lotus.sametime.cachedresolvemanager.api.CachedResolveService;
import com.lotus.sametime.community.LoginEvent;
import com.lotus.sametime.community.LoginListener;
import com.lotus.sametime.community.ServerAppService;
import com.lotus.sametime.communityevents.CommunityEventsService;
import com.lotus.sametime.core.comparch.DuplicateObjectException;
import com.lotus.sametime.core.comparch.STEvent;
import com.lotus.sametime.core.comparch.STSession;
import com.lotus.sametime.core.types.STId;
import com.lotus.sametime.core.types.STUserInstance;
import com.lotus.sametime.core.util.connection.Connection;
import com.lotus.sametime.core.util.connection.SocketConnection;
import com.lotus.sametime.lookup.LookupService;

	/**
	 * Sample application which uses the CachedResolve Component.
	 * 
	 *  The sample application demonstrates how to initiate the CachedResolve component,
	 *  provide the required Sametime server STSession objects and issue resolve requests.
	 *  
	 *  The CachedResolveExample implements the CachedResolveListener in order to get the
	 *  CachedResolve component events.
	 *  
	 *  The CachedResolveExample implements the LoginListener in order to handle the Sametime
	 *  server STSession object creation.
	 * 
	 * Note: The CachedResolve Component uses its own configuration file.
	 * For additional information please refer to the file st.cached.resolve.properties
	 * 
	 * 
	 * The example specific implementation:
	 * -----------------------------------
	 * Initiate the CachedResolve component
	 * Provide the required Sametime session
	 * Send several resolve requests
	 * Count the resolve answers (successful and failed)
	 * When all the resolve answers received: flush the cache memory
	 *                   to the CachedResolve persistent file.
	 * 
	 * 
	 * @author Tally Tsabary and Inna Manzon, January 2010
	 *
	 */

	public class CachedResolveExample  implements CachedResolveListener, LoginListener{
		
		/**
		 * Sametime server name
		 */
		public String _serverName = "*****";
		/**
		 * Sametime server URL
		 */
		public String _serverURL = "*****";
		/**
		 * Sametime server port
		 */
		public int _port = 1516;
		/**
		 * Sametime server active session 
		 */
		public STSession _stSession = null;
		
		/**
		 * Resolve counter - count the resolve requests that was sent.
		 */
		private int _resCnt;
		
		/*
		 * The CachedResolve component object.
		 */
		private CachedResolveService _service = null;
	 
		/**
		 * The program name - this is required for printing purposes.
		 */
		private String _className = CachedResolveExample.class.getName();
	    
	    
	    /**
	     * The CachedResolveExample constructor.
	     * 
	     */
	    public CachedResolveExample() {
	     }
		
		
		/**
		 * The example initiation:
		 * 
		 * 1. Instantiate the CachedResolve component.
		 * 2. Register the example program as listener in order to get the 
		 *    component's notifications.
		 * 3. Initiate the CachedResolve component.
		 * 4. Provide the Sametime server connection details
		 *    
		 *
		 */
		public void init()
	    {
			System.out.println(_className+": init started");
			
			//Instantiate the CachedResolve component.
			_service =   CachedResolveComp.getInstance();
			//Register the example program as listener
			_service.addListener(this); 
			//Initiate the CachedResolve component.
			_service.initiate();
			
			//Create the Sametime server connection details
			//When the STSession will be available the loggedIn()
			//method will be called by the LoginListener
			createSTSession();
	   }
		
		/**
		 * Create Sametime session STSession object.
		 * The CachedResolve uses the STSession for real resolve requests.
		 * 
		 */
		void createSTSession() {
			
			System.out.println(_className+": createSTSession started");
			
			//Init ST components & session.
			ServerAppService saService = initSTComponents();
			
	  		//Login to ST server.
			loginToServer(saService);
		}
		
		/**
		 * Initiate the Sametime server components, 
		 * creates a ST session, loads components and starts the session.
		 * 
		 *  @see STSession
		 */
		private ServerAppService initSTComponents() {
			
			System.out.println(_className+": initSTComponents started");
			
			//Create the Sametime session.
			try {
				_stSession = new STSession("CRLoader" + toString());
			}
			catch (DuplicateObjectException e) {
				System.out.println(_className+": initSTComponents STSession initiation: "+e.getMessage());
			}
			
			//Set the Sametime components.
			String [] compNames = {ServerAppService.COMP_NAME,
					LookupService.COMP_NAME,
					CommunityEventsService.COMP_NAME,
				};
			
			//Load the Sametime components.
			_stSession.loadComponents(compNames);
			
			//Get the server application service instance.
			ServerAppService saService = (ServerAppService) _stSession.getCompApi(ServerAppService.COMP_NAME);
			
	     	//Register as listener in order to be notifiyed when the STSession
			//created and connected to the Sametime server.
			saService.addLoginListener(this);
		
			//Start the Sametime session.
			_stSession.start();	
			return saService;
			
	  }
		
		/**
		 * Login to the Sametime server 
		 */
		private void loginToServer(ServerAppService saService) {
			System.out.println(_className+": loginToServer started");
			System.out.println(_className+": loginToServer about to connect: "+_serverURL+" port: "+_port);
			
			int socketConnectTimeout = 17000;
			short loginType = STUserInstance.LT_SERVER_APP;
			int[] services = {};
				
			Connection[] connections = { new SocketConnection(_port, socketConnectTimeout)};
			saService.setConnectivity(connections);
	 	    saService.loginAsServerApp(_serverURL, loginType, "CachedResolveExample", services);
			
		}


		
		/*
		 * ***********************************************
		 * The CachedResolveListener interface implementation.
		 * This interface is used in order to get notifications 
		 * from the CachedResolve component.
		 * ***********************************************
		 */
	 
		/**
		 * CachedResolve component notification:
		 * The CachedResolve component is ready to perform resolve requests.
		 * That notification arrive when there is at least one available STSession.  
		 */
		public void resolverAvailable() {
			System.out.println(_className+": resolverAvailable: resolve requests can be answered"); 
			performResolveRequests();
		}
		
		


		/**
		 * CachedResolve component notification:
		 * The CachedResolve component does not have any available STSession.
		 * Any following resolve request will be rejected, until the following resolverAvailable()
		 * notification.
		 */
		public void resolverNotAvailable() {
			System.out.println(_className+": resolverNotAvailable: resolve requests will be rejected until further notice"); 
		}
		
		
		
		/**
		 * CachedResolve component notification:
		 * The resolve request was answered successfully. 
		 * @param externalUserIdentification - the requested user
		 * @param stId - the resolved STId
		 */  
		public void nameResolved(String externalUserIdentification, STId stId, STEvent origEvent) {
			System.out.println(_className+": nameResolved: external user: "+
					externalUserIdentification+" was resolved as: "+stId); 
			
            //The example specific implementation counts the number of resolve answers.
			//When all the resolve answers arrive it flush the cache memory
			//to the CachedResolve persistent file.
			//In this section the successful resolve answers are counted.
			synchronized(_service) {
				_resCnt--;
				if (_resCnt == 0) {
					_service.flushMemoryToFile();
				}
			}
		}

	
		
		 /**
	     * The CachedResolve component encountered an error.
	     * @param event - the reported CachedResolve component event.
	     */
		public void errorEvent(CachedResolveErrorEvent event) {
			System.out.println(_className+": errorEvent: error: "+
					event.getExternalUserId()+ " desc= "+ event.getDescription());
			
			
			//The example specific implementation counts the number of resolve answers.
			//When all the resolve answers arrive it flush the cache memory
			//to the CachedResolve persistent file.
			//In this section the failing resolve answers are counted.
			
			if (event.getId() == CachedResolveErrorEvent.INTERNAL_ERROR_RESOLVING_USER || 
					event.getId() == CachedResolveErrorEvent.ERROR_USER_RESOLVING_IN_PROCESS ||
					event.getId() == CachedResolveErrorEvent.ERROR_RESOLVING_NONE_UNIQUE_USER) {
					synchronized(_service) {
						_resCnt--;
						if (_resCnt == 0) {
							_service.flushMemoryToFile();
						}
					}
				}
		}
	
		
		
		/**
		 * CachedResolve component notification:
		 * The Cache persistent file was updated successfully  
		 */
		public void flushMemoryToFileCompleted() {
			System.out.println(_className+": flushMemoryToFileCompleted: The Cache persistent file was updated successfully ");
			endOfExample();
		}

		/**
		 * CachedResolve component notification:
		 * The Cache persistent file was loaded successfully  
		 */
		public void loadFileToMemoryCompleted() {
			System.out.println(_className+": loadFileToMemoryCompleted: The Cache persistent file was loaded successfully ");
		}
		
		
		


		/**
		 * ***********************************************
		 * The CachedResolveService interface usage.
		 * The CachedResolve component provides the CachedResolveService API.
		 * 
		 * The major service is the resolveUser(externalUserId)
		 * The notification about the resolve request will be one of the following:
		 * 1. nameResolved(String externalUserIdentification, STId stId) 
		 *    will be activated upon successful resolve.
		 * 2. errorEvent(CachedResolveErrorEvent event)   
		 *    will be activated upon failed resolve. 
		 * 
		 * ***********************************************
		 */
		private void performResolveRequests() {
			System.out.println(_className+": performResolveRequests started");
			
			ArrayList users = buildUsersList();
			_resCnt = users.size();
			
			for (int i=0; i< _resCnt; i++){
				System.out.println(_className+": performResolveRequests. Resolve request for: "+users.get(i));
				_service.resolveUser((String) users.get(i));
				
			}
		
		}

		/**
		 * ***********************************************
		 * Build the list of user names that the example 
		 * will resolve.
		 * ***********************************************
		 */
		private ArrayList buildUsersList() {
			ArrayList users = new ArrayList();
			
			//This is the place to add the list of your example users
			//to resolve.
			
			users.add("****@*****.com");
			
			
			return users;
		}
		
		
		private void endOfExample() {
			System.out.println(_className+": endOfExample: The CachedResolve example ended. - exiting");
			System.exit(0);
		}

		
		/*
		 * ***********************************************
		 * The LoginListener interface implementation.
		 * This interface is used in order to get notifications 
		 * about Sametime server login and logout event.
		 * ***********************************************
		 */
		
	

		/*
		 * The CachedResolveExample application successfully logged-in to
		 * the provided Sametime server.
		 */
		public void loggedIn(LoginEvent event) {
			System.out.println(_className+": loggedIn started");
			System.out.println(_className+": loggedIn to: "+event.getHost());
	        
			// Set the logged-in STSession at the CachedResolve component,
			//  for performing real resolve requests.
			_service.addServerSession(_stSession, _serverName);
		}

		/*
		 * The CachedResolveExample application logged-out from 
		 * the provided Sametime server.
		 */
		public void loggedOut(LoginEvent event) {
			System.out.println(_className+": loggedOut started");
			System.out.println(_className+": loggedOut from: "+event.getHost());
			//	Remove the logged-out STSession from the CachedResolve component.
			_service.removeServerSession(_stSession, _serverName);
		}
		
		/**
		 * Example initiation
		 * @param args
		 */
	    public static void main (String[] args){
			
			System.out.println("CachedResolveExample: main");
			
			//Create the example program object and activate it.
			CachedResolveExample _crExample =  new CachedResolveExample();
		    _crExample.init();
	   }


	

}
