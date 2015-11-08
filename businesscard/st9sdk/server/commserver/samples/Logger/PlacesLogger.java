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
import com.lotus.sametime.placessa.*;
import com.lotus.sametime.core.types.*;
import com.lotus.sametime.core.util.connection.*;

import java.awt.*;


/**
 * A sample of the places sa toolkit. Monitors the places that are created and
 * logs their operations.
 */
public class PlacesLogger implements LoginListener, ActivityServiceListener
{
    //
    // Constants.
    //

    /**
     * The activity type we support.
     */
	// For some reason, the places sa doesn't support global activities. 
    static final int ACTIVITY_TYPE = 0x55;

    /**
     * The type of places we log.
     */
    static final int PLACE_TYPE = 0;

    //
    // Members.
    //
    /**
     * The session object.
     */
    private STSession m_session;

    /**
     * The places admin service.
     */
    private PlacesAdminService m_adminService;

    /**
     * The activity service.
     */
    private ActivityService m_activityService;

    /**
     * The output frame.
     */
    private LogFrame m_frame;

    /**
     * Constructor.
     *
     * @param hostName The name of the server to connect to.
     */
    public PlacesLogger(String hostName)
    {
        // Create and load the session of components.
        try
        {
            m_session = new STSession("PlacesLogger");

			String [] compNames  = { ServerAppService.COMP_NAME, 
				ActivityService.COMP_NAME, PlacesAdminService.COMP_NAME };
			m_session.loadComponents( compNames );
			
            m_session.start();
        }
        catch(DuplicateObjectException e)
        {
            e.printStackTrace();
            exit();
        }

        // Get a reference to the needed components.
        m_adminService
            = (PlacesAdminService)m_session.getCompApi(PlacesAdminService.COMP_NAME);
        m_adminService.addPlacesAdminListener(new AdminHandler());

        m_activityService
            = (ActivityService)m_session.getCompApi(ActivityService.COMP_NAME);
        m_activityService.addActivityServiceListener(this);

        login(hostName);

        // Create the frame.
        m_frame = new LogFrame();
        m_frame.pack();
        m_frame.setVisible(true);
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
        int[] supportedServices = { ACTIVITY_TYPE };
		
		// Server applications login directly to the server, and not through
        // the mux. So we can't use the default port.
        Connection[] connections = {
            new SocketConnection(1516, 17000),
        };
        saService.setConnectivity(connections);

        saService.addLoginListener(this);
        saService.loginAsServerApp(hostName, loginType, "PlacesLogger",
                                   supportedServices);
    }

    /**
     * Terminate the application.
     */
    void exit()
    {
        m_session.stop();
        m_session.unloadSession();
        System.exit(0);
    }

    //
    // Places Activity Listener
    //
    /**
     * A request for an activity as come from a place.
     *
     * @param event The event object.
     * @see PlacesActivityEvent#getManagedActivity
     * @see PlacesActivityEvent#getPlace
     */
    public void activityRequested(ActivityServiceEvent event)
    {
        // A place was created and our activity is requested.
        Place place = event.getPlace();

        System.out.println("Place: " + place.getName());
        m_activityService.acceptActivity(event.getMyActivity(), null);

        new PlaceHandler(event.getMyActivity(), m_frame);
    }

    //
    // Login Listener.
    //

     /**
     * Indicates that we were successfully logged in to the Sametime community.
     *
     * @param event The event object.
     * @see LoginEvent#getCommunity
     */
    public void loggedIn(LoginEvent event)
    {
        System.out.println("Logger: LoggedIn");
    }

    /**
     * Indicates that we were successfully logged out of the Sametime community, or a login
     * request was refused.
     *
     * @param event The event object.
     * @see LoginEvent#getReason
     * @see LoginEvent#getCommunity
     */
    public void loggedOut(LoginEvent event)
    {
        System.out.println("Logger: LoggedOut" + event.getReason());
        exit();
    }


    //
    // Admin Listener.
    //

    /**
     * Handles places admin events.
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
            // Set our activity as a default activity for the place type we
            // are interested in.
            System.out.println("AdminService available");
            m_adminService.setDefaultActivity(PLACE_TYPE, ACTIVITY_TYPE, null);
        }

        /**
        * The Places server is unavailable.
        *
        * @param event The event object.
        */
        public void serviceUnavailable(PlacesAdminEvent event)
        {
            System.out.println("AdminService un available");
            exit();
        }
        /**
        * The 'Set default activity' operation succeded.
        *
        * @param event The event object.
        * @see PlacesAdminEvent#getPlaceType
        * @see PlacesAdminEvent#getActivityType
        * @see PlacesAdminEvent#getActivityData
        */
        public void defaultActivitySet(PlacesAdminEvent event)
        {
            System.out.println("Default activity set");
        }

        /**
        * The 'Set default activity' operation failed.
        *
        * @param event The event object.
        * @see PlacesAdminEvent#getPlaceType
        * @see PlacesAdminEvent#getActivityType
        * @see PlacesAdminEvent#getActivityData
        * @see PlacesAdminEvent#getReason
        */
        public void setDefaultActivityFailed(PlacesAdminEvent event)
        {
            System.out.println("Set default activity failed");
            exit();
        }
    }


    /**
     * Application entry point.
     */
    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.err.println("Usage: PlacesLogger [serverName]");
            System.exit(0);
        }

        new PlacesLogger(args[0]);
    }

}
