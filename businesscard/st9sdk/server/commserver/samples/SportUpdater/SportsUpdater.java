/*
 * Licensed Materials - Property of IBM
 *
 * L-MCOS-96LQPJ
 *
 * (C) Copyright IBM Corp. 2002, 2013. All rights reserved.
 *
 * US Government Users Restricted Rights- Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

import com.lotus.sametime.core.comparch.*;
import com.lotus.sametime.community.*;
import com.lotus.sametime.core.types.*;
import com.lotus.sametime.core.util.connection.*;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Random;

/**
 * SportsUpdater: The sports updater server application
 */
public class SportsUpdater extends Thread
	implements 	LoginListener, 
				ChannelServiceListener
{
	// Message types
	public static final short MSG_AVAILABLE_MATCHES = 1;
	public static final short MSG_SUBSCRIBE_2_MATCH = 2;
	public static final short MSG_UPDATE_MATCH = 3;
	public static final short MSG_UNSUBSCRIBE_FROM_MATCH = 4;	
	
	/**
     * Our service type.
     */
    public static final int SPORTS_SA_SERVICE_TYPE = 0x64;
	
	/**
     * The session object.
     */
    private STSession m_session;

	 /**
     * The server application service.
     */
    private ServerAppService m_saService;
		
	/**
	 * The matches list
	 */
	Vector m_matches = new Vector();
	
	/**
	 * The handles user list
	 */
	Vector m_users = new Vector();
	
	/**
	 *  Random to make some changes in the matches 
	 */
	Random m_rnd = new Random();

	
	/**
	 * Initialize all variables
	 */
	private void initialize(String serverName)
	{
		// init the sametime session and objects
        try
        {
			// First, we create a new session, that belongs uniquely to us.
		    m_session = new STSession("" + this );
			
			// Load components, we only need ServerAppService for login and channel
			String [] compNames  = { ServerAppService.COMP_NAME };
			m_session.loadComponents( compNames );	
			m_saService = (ServerAppService) m_session.getCompApi
				(ServerAppService.COMP_NAME);
			
			// start the session
			m_session.start();	
        }
        catch (DuplicateObjectException e)
        {
			System.out.println("STSession or Components created twice.");
			e.printStackTrace();
            System.exit(1);
        }
				
		// Now we can login
		login(serverName);		
	}
	
	/**
	 * login to Sametime as server application
	 */
	private void login(String serverName)
	{
		// Add ourselves as login listener to the community 
		m_saService.addLoginListener( this);		
		
		// Login to sametime as a server application, pass our ServiceType
        int[] supportedServices = { SPORTS_SA_SERVICE_TYPE };

		// Server applications login directly to the server, and not through
        // the mux. So we can't use the default port.
        Connection[] connections = {
            new SocketConnection(1516, 17000),
        };
		
        m_saService.setConnectivity(connections);
		short loginType = STUserInstance.LT_SERVER_APP;
		m_saService.loginAsServerApp( serverName, loginType, 
								"Sports Updater", supportedServices);	
	}
	
	
	//
    // Implementing LoginListener.
    //

    /**
     * Indicates that we were successfully logged in to the Sametime community.
     * 
     * @param            event The event object.
     * @see              LoginEvent#getCommunity
     */
    public void loggedIn(LoginEvent event)	
	{
		// Read the matches database
		readMatchesDB();
		
		// Start the updating thread
		start();
		
		// Get a ref to the channel service
		ChannelService channelService =
            (ChannelService)m_session.getCompApi(ChannelService.COMP_NAME);
        channelService.addChannelServiceListener(this);
	}
    
    /**
     * Indicates that we were successfully logged out of the Sametime community,
     * or a login request was refused.
     * 
     * @param            event The event object.
     * @see              LoginEvent#getReason
     * @see              LoginEvent#getCommunity
     */
	public void loggedOut(LoginEvent event)	
	{	
		System.out.println("Loged out, reason = " + event.getReason() );
	}
	
	//
    // Channel Service Listener.
    //
    /**
     * A channel was received. If a 'channel received' notification is not
     * handled by any listener (handling means accepting it, closing it,
     * or explicitly putting it in a pending state) it will be closed, to
     * make sure we have no zombie channels.
     *
     * @param event The event object.
     */
    public void channelReceived(ChannelEvent event)
    {
    	
    	if (event.getChannel().getServiceType() == SPORTS_SA_SERVICE_TYPE) {// SPR #WBLU7VR93B
    		// Another user wants to subscribe, 
    		// Create a user handler for this user
    		UserHandler user = new UserHandler(this, event);
	   
    		// Add it to our list
    		addUserHandler(user);
    	}
    }
	
	

	/**
	 * Read the matches from a database (hord coded in the sample)
	 */
	private void readMatchesDB()
	{
		// In a real world application
		// This is where we would probably create the Match database
		// after reading it from the web (using webservices for example)
		// But, in order to simplify this sample we'll just create the 
		//		matches hard coded
		Match m = new Match();
		m.setID("01");
		m.setHomeTeamName("Philadelphia");
		m.setVisitorsTeamName("Miami");
		m_matches.addElement(m);
		
		m = new Match();
		m.setID("02");
		m.setHomeTeamName("New Jersay");
		m.setVisitorsTeamName("Indiana");
		m_matches.addElement(m);
				
		m = new Match();
		m.setID("03");
		m.setHomeTeamName("Houston");
		m.setVisitorsTeamName("San Antonio");
		m_matches.addElement(m);
		
		m = new Match();
		m.setID("04");
		m.setHomeTeamName("Phoenix");
		m.setVisitorsTeamName("Seattle");
		m_matches.addElement(m);
		
		m = new Match();
		m.setID("05");
		m.setHomeTeamName("Chicago");
		m.setVisitorsTeamName("Denver");
		m_matches.addElement(m);m = new Match();
		
		m = new Match();
		m.setID("06");
		m.setHomeTeamName("Boston");
		m.setVisitorsTeamName("Orlando");
		m_matches.addElement(m);
		
		m = new Match();
		m.setID("07");
		m.setHomeTeamName("New York");
		m.setVisitorsTeamName("Washington");
		m_matches.addElement(m);
		
		m = new Match();
		m.setID("08");
		m.setHomeTeamName("Milwaukee");
		m.setVisitorsTeamName("Detroit");
		m_matches.addElement(m);

		m = new Match();
		m.setID("09");
		m.setHomeTeamName("Toronto");
		m.setVisitorsTeamName("Charlote");
		m_matches.addElement(m);

		m = new Match();
		m.setID("10");
		m.setHomeTeamName("Atlanta");
		m.setVisitorsTeamName("Cleveland");
		m_matches.addElement(m);

		m = new Match();
		m.setID("11");
		m.setHomeTeamName("Minnesota");
		m.setVisitorsTeamName("Dallas");
		m_matches.addElement(m);
		
		m = new Match();
		m.setID("12");
		m.setHomeTeamName("Utah");
		m.setVisitorsTeamName("Memphis");
		m_matches.addElement(m);

		m = new Match();
		m.setID("13");
		m.setHomeTeamName("LA Lakers");
		m.setVisitorsTeamName("Sacramento");
		m_matches.addElement(m);
		
		m = new Match();
		m.setID("14");
		m.setHomeTeamName("Golden State");
		m.setVisitorsTeamName("Portland");
		m_matches.addElement(m);
	}
	
	/**
	 * Get current matches
	 */
	public Vector getMatches()
	{						 
		// Return a clone to the real matches
		return (Vector)m_matches.clone();
	}
	
	/**
	 * The updating thread
	 */
	public void run() 
	{
		while ( true )
		{
			// Check for changes in the database
			refresh();

			// go to sleep for a while
			try 
			{
				sleep(1000);
			}
			catch( InterruptedException exc)
			{
			}
		}
	}
	
	/**
	 * Check for changes in the matches database (and send updates if needed)
	 */
	private void refresh()
	{
		// In a real world application
		// This is where we would updated the matches by reading the current 
		//	score, match status etc from the web
		// to simplify this sample we will just use a random refresh
		Match m;
		Enumeration enumeration = m_matches.elements();		
		while(enumeration.hasMoreElements())
		{
		    m = (Match) enumeration.nextElement();
			refreshMatch(m);
		}
	}

	/**
	 * Check for changes in the match and send update if needed
	 */
	private void refreshMatch(Match m)
	{
		
		// Do we want to change this match at all
		if ( (m_rnd.nextInt() % 3) != 0 )
			return;
		
		// Randomally add between zero to three points to home team
		if ( (m_rnd.nextInt() % 3 == 0 ) )
			m.setHomeTeamScore( m.getHomeTeamScore() + 2 );
		else if ( (m_rnd.nextInt() % 5 == 0 ) )
			m.setHomeTeamScore( m.getHomeTeamScore() + 1 );
		else if ( (m_rnd.nextInt() % 5 == 0 ) )
			m.setHomeTeamScore( m.getHomeTeamScore() + 3 );

		// Randomally add between zero to three points to visitors
		if ( (m_rnd.nextInt() % 3 == 0 ) )
			m.setVisitorsTeamScore( m.getVisitorsTeamScore() + 2 );
		else if ( (m_rnd.nextInt() % 5 == 0 ) )
			m.setVisitorsTeamScore( m.getVisitorsTeamScore() + 1 );
		else if ( (m_rnd.nextInt() % 5 == 0 ) )
			m.setVisitorsTeamScore( m.getVisitorsTeamScore() + 3 );
		
		m.updateAllListeners();
	}
	
	
	/**
	 * Add a user handler from our users list
	 */
	public void addUserHandler(UserHandler user)
	{
		// add the new handler
		m_users.addElement(user);
	}
	
	/**
	 * Remove a user handler from our users list
	 */
	public void removeUserHandler(UserHandler user)
	{
		// remove the user listener
		m_users.removeElement(user);
	}
	
	
	/**
	 * Entry point of the application
	 */
	public static void main(String[] args)
	{
		if ( args.length != 1 )
		{
			System.out.println("Usage: SportUpdater serverName");
			System.exit(0);
		}

		new SportsUpdater().initialize(args[0]);
	}
}

