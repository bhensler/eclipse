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
import com.lotus.sametime.core.types.*;
import com.lotus.sametime.community.*;
import com.lotus.sametime.communityevents.*;
import com.lotus.sametime.core.util.connection.*;

import java.net.InetAddress;

/**
 * A sample of the community events service. Implements a hackers tracking mechanism.
 */
public class HackersCatcher implements LoginListener,
			UserLoginFailedListener,
			CommunityEventsServiceListener
{
	/**
     * The session object.
     */
    private STSession m_session;

	 /**
     * The server application service.
     */
    private ServerAppService m_saService;

	/**
	 * The Community events component
	 */
	CommunityEventsService m_ceService;
	
    private void run(String serverName)
	{
		// init the sametime session and load the services
		// First, we create a new session, that belongs uniquely to us.
		try
		{
			m_session = new STSession("" + this );
			String [] compNames  = { ServerAppService.COMP_NAME, 
								CommunityEventsService.COMP_NAME };
			
			m_session.loadComponents( compNames );
			
			m_saService = (ServerAppService)
				m_session.getCompApi(ServerAppService.COMP_NAME);
			m_ceService = (CommunityEventsService)
				m_session.getCompApi(CommunityEventsService.COMP_NAME);
		}
        catch (DuplicateObjectException e)
        {
			System.out.println("STSession or Components created twice.");
        }
		
		// start the session
		m_session.start();	
		 
		// Login to sametime
		m_saService.addLoginListener( this);		
        short loginType = STUserInstance.LT_SERVER_APP;
        
        // The default connection is configured to connect through the Sametime 
        // mux. We want to connect directly to the server, so we have to set 
        // the port explicitly.        
		Connection[] connections = {new SocketConnection(1516, 17000),};
        m_saService.setConnectivity(connections);
        m_saService.loginAsServerApp( serverName, loginType, "Hacker Catcher", null);	
    }
	
    /**
     * Logged in to Sametime.
     */
	public void loggedIn(LoginEvent event)	
    {
		m_ceService.addCommunityEventsServiceListener(this);
	}
    
    /**
     * Logged out from Sametime.
     */
    public void loggedOut(LoginEvent event)	
    {		
        m_ceService.removeCommunityEventsServiceListener(this);	
    }
	
    /**
     * The community events service is available. 
     */
	public void serviceAvailable(CommunityEventsServiceEvent event)
	{
		System.out.println("************** Start recording *************");
					
		m_ceService.addUserLoginFailedListener(this);
	}
    
    /**
     * The community events service is unavailable.
     */
    public void serviceUnavailable(CommunityEventsServiceEvent event)
	{
		System.out.println("************** finish recording *************");
		
		m_ceService.removeLoginFailedListener(this);
	}

    /**
     * A user has failed to login to Sametime.
     */
	public void userLoginFailed(UserLoginFailedEvent event)
	{
		String s = new String("login failed:");
		s += " Name=" + event.getLoginName();
		s += ", ip=" + event.getLoginIp();
		s += ", type=" + Integer.toHexString(event.getLoginType());
		s += ", reason=" + Integer.toHexString(event.getReason());
		
		System.out.println(s);
	}
	
	/**
	 * Entry point of the application
	 */
	public static void main(String[] args)
	{
		if ( args.length != 1 )
		{
			System.out.println("Usage: HackersCatcher serverName");
			System.exit(0);
		}

		new HackersCatcher().run(args[0]);
	}
}
