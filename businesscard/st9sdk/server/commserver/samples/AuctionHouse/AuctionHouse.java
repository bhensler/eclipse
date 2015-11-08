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



import com.lotus.sametime.placessa.*;
import com.lotus.sametime.community.*;
import com.lotus.sametime.core.comparch.STSession;
import com.lotus.sametime.core.comparch.DuplicateObjectException;
import com.lotus.sametime.core.types.*;
import com.lotus.sametime.core.util.connection.*;
import com.lotus.sametime.core.constants.*;
import com.lotus.sametime.core.util.*;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * The auction house activity.
 * 
 * @author Assaf Azaria, August 2001.
 */
public class AuctionHouse implements ActivityServiceListener
{
    /**
     * The session object.
     */  
	private STSession m_session;        
	
	/**
     * The activity service.
     */ 
	private ActivityService m_activityService;        
	
	/**
     * The places administration service.
     */
    private PlacesAdminService m_adminService;
    
	/**
     * The auction input frame.
     */
    private AuctionFrame m_frame;        
	
	/**
     * Before we exit the app we destroy all 3 places. This counter counts down the 
     * number of places left to be destructed.
     */    
	private int m_destructionCount = 3;
    
    /**
     * Construct a new Auction house activity.
     * 
     * @param hostName The name of the sametime server to connect to.
     */
    public AuctionHouse(String hostName)
    {
        // Create and load the session of components.
        try
        {
            m_session = new STSession("AuctionHouse");
			String [] compNames  = { ServerAppService.COMP_NAME, 
									 ActivityService.COMP_NAME, 
									 PlacesAdminService.COMP_NAME };
			
			m_session.loadComponents( compNames );
			
            m_session.start();
        }
        catch(DuplicateObjectException e)
        {
            e.printStackTrace();
            exit();
        }

        // Get a reference to the needed components.
        m_activityService = 
            (ActivityService)m_session.getCompApi(ActivityService.COMP_NAME);        
		m_activityService.addActivityServiceListener(this);
        m_adminService = 
            (PlacesAdminService)m_session.getCompApi(PlacesAdminService.COMP_NAME);        
		m_adminService.addPlacesAdminListener(new AdminHandler());
        
        login(hostName);    
	}    
    
	/**
     * Login to the sametime server as a server application.
     *
     * @param hostName The name of the host.
     */
    void login(String hostName)
    {
        ServerAppService saService =
            (ServerAppService)m_session.getCompApi(ServerAppService.COMP_NAME);

        short loginType = STUserInstance.LT_SERVER_APP;
        int[] supportedServices = { AuctionConstants.ACTIVITY_TYPE };

        // We don't want to connect through the mux.        
		Connection[] connections = {
            new SocketConnection(1516, 17000),
        };

        saService.setConnectivity(connections);

        saService.addLoginListener(new loginHandler());
        saService.loginAsServerApp(hostName, loginType, "AuctionHouse",
                                   supportedServices);
    }   
	
	/**
     * Terminate the application.
     */
    void exit()
    {
        // Kill the places.        
		if (m_adminService != null)
        {            
			m_adminService.destroyPlace(AuctionConstants.PLACE1_NAME,                                         AuctionConstants.PLACE_TYPE);
            m_adminService.destroyPlace(AuctionConstants.PLACE2_NAME, 
                                        AuctionConstants.PLACE_TYPE);            
			m_adminService.destroyPlace(AuctionConstants.PLACE3_NAME,                                         AuctionConstants.PLACE_TYPE);
        }
        else
        {   
			m_session.stop();
            m_session.unloadSession();
            System.exit(0);        
		}
    }

    //
    // Listeners.
    //
    
    /**
     * A request for an activity has come from a place. 
     * 
     * @param event The event object.
     * @see ActivityServiceEvent#getMyActivity
     * @see ActivityServiceEvent#getPlace
     */
    public void activityRequested(ActivityServiceEvent event)
    {
        MyActivity myActivity = event.getMyActivity();
        m_activityService.acceptActivity(myActivity, null);
        
        // Create an auction room object.
        AuctionRoom newRoom = new AuctionRoom(myActivity, m_frame);
    }
    
    /**
     * Login Listener.
     */
    class loginHandler implements LoginListener    
	{        
		/**
         * Indicates that we were successfully logged in to the Sametime          
         * community.
         *
         * @param event The event object.
         * @see LoginEvent#getCommunity
         */
         public void loggedIn(LoginEvent event)
         {
            System.out.println("AuctionHouse: LoggedIn");                        
			m_frame = new AuctionFrame(AuctionHouse.this);
            m_frame.pack();
            m_frame.setVisible(true);
        }

        /**
         * Indicates that we were successfully logged out of the Sametime          
         * community, or a login request was refused.
         *
         * @param event The event object.
         * @see LoginEvent#getReason
         * @see LoginEvent#getCommunity
         */
         public void loggedOut(LoginEvent event)
         {
            System.out.println("AuctionHouse: LoggedOut " +                               
							   event.getReason());
            m_session.stop();
            m_session.unloadSession();
            System.exit(0); 
         }    
	}
    
	/**
     * Admin Listener.
     */    
	class AdminHandler extends PlacesAdminAdapter    
	{
		/** 
         * The Places service is available.
         * 
         * @param event The event object.
         */
        public void serviceAvailable(PlacesAdminEvent event)
        {
            // Set our activity as the default activity.
            m_adminService.setDefaultActivity(AuctionConstants.PLACE_TYPE, 
                                              AuctionConstants.ACTIVITY_TYPE,
                                              null);
        }
    
        /**  
         * The Places service is unavailable.
         * 
         * @param event The event object.
         */
        public void serviceUnavailable(PlacesAdminEvent event)
        {
            System.out.println("Auction house: Admin service unavailable");
            exit();
        }
        
        /**
         * The 'Set default activity' operation succeded.
         * 
         * @param event The event object.
         * @see PlacesAdminEvent#getPlaceType
         */
        public void defaultActivitySet(PlacesAdminEvent event)
        {
            System.out.println("Auction house: Default activity set");        
            
            // Create the auction places.
            m_adminService.createPersistentPlace(AuctionConstants.PLACE1_NAME, 
                                                 AuctionConstants.PLACE1_NAME,
                                                 AuctionConstants.PLACE_TYPE,
                                                 AuctionConstants.PLACE_PASSWORD,
                                                 EncLevel.ENC_LEVEL_DONT_CARE);
            
            m_adminService.createPersistentPlace(AuctionConstants.PLACE2_NAME, 
                                                 AuctionConstants.PLACE2_NAME,
                                                 AuctionConstants.PLACE_TYPE,
                                                 AuctionConstants.PLACE_PASSWORD,
                                                 EncLevel.ENC_LEVEL_DONT_CARE);
            
            m_adminService.createPersistentPlace(AuctionConstants.PLACE3_NAME, 
                                                 AuctionConstants.PLACE3_NAME,
                                                 AuctionConstants.PLACE_TYPE,
                                                 AuctionConstants.PLACE_PASSWORD,
                                                 EncLevel.ENC_LEVEL_DONT_CARE);
                                                 
        }
        
        /**
         * The create persistent place operation failed.
         * 
         * @param event The event object.
         * @see PlacesAdminEvent#getPlaceName
         * @see PlacesAdminEvent#getPlaceType
         * @see PlacesAdminEvent#getReason
         */
        public void createPersistentPlaceFailed(PlacesAdminEvent event)
        {
            System.out.println("COULDN'T CREATE THE PLACE: " + event.getPlaceName() + 
                               " Reason code: " + event.getReason());
        }
        
        /**
         * The 'Set default activity' operation failed.
         * 
         * @param event The event object.
         * @see PlacesAdminEvent#getPlaceType
         * @see PlacesAdminEvent#getReason
         */
        public void setDefaultActivityFailed(PlacesAdminEvent event)
        {
            System.out.println("Auction house: Can't register as default" +
                               " activity" + event.getReason());
            exit();
        }
        
        /**
        * The destroy place operation succeeded.
        * 
         * @param event The event object.
         * @see PlacesAdminEvent#getPlaceName
         * @see PlacesAdminEvent#getPlaceType
         */
        public void placeDestroyed(PlacesAdminEvent event)
        {
            System.out.println("PLACE DESTROYED: " + event.getPlaceName());    
            
            // if all 3 places have been destroyed, we can leave.
            if (--m_destructionCount == 0)
            {
                m_session.stop();
                m_session.unloadSession();
                System.exit(0);
            }
        }
        
        /**
        * The destroy place operation failed.
        * 
        * @param event The event object.
        * @see PlacesAdminEvent#getPlaceName
        * @see PlacesAdminEvent#getReason
        */
        public void destroyPlaceFailed(PlacesAdminEvent event)
        {
            System.out.println("FAILED TO DESTROY PLACE: " + event.getPlaceName() +
                              " Reason: " + event.getReason());
        }
	}        
	
	/**
     * Application entry point.
     */
    public static void main(String[] args)
    {
        if (args.length != 1)
        {  
            System.err.println("Usage: AuctionHouse [serverName]");
            System.exit(0);
        } 

        new AuctionHouse(args[0]);
    }
	
}


