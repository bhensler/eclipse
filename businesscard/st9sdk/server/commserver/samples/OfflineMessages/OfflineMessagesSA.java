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
import com.lotus.sametime.core.constants.STError;
import com.lotus.sametime.core.util.NdrInputStream;
import com.lotus.sametime.core.util.connection.*;
import com.lotus.sametime.communityevents.*;
import com.lotus.sametime.community.*;
import com.lotus.sametime.token.*;

import java.io.IOException;
import java.util.Hashtable;


/**
 * The 'offline messages' server application.
 *
 * @author Assaf Azaria, July 2001.
 */
public class OfflineMessagesSA implements LoginListener, UserLoginListener,
    ChannelServiceListener
{
    /**
     * Our service type.
     */
    static final int SERVICE_TYPE = 0x80000055;

    /**
     * The session object.
     */
    private STSession m_session;

    /**
     * The community events component.
     */
    private CommunityEventsService m_commEvents;

    /**
     * The list of users we watch.
     */
    private Hashtable m_watchedUsers = new Hashtable();

    /**
     * The name of the server to connect to.
     */
    private String m_hostName;

    /**
     * Constructor.
     *
     * @param hostName The name of the server to connect to.
     */
    public OfflineMessagesSA(String hostName)
    {
        // Create and load the session of components.
        try
        {
            m_session = new STSession("OfflineMessages");

			String [] compNames  = { ServerAppService.COMP_NAME, 
					CommunityEventsComp.COMP_NAME, SATokenComp.COMP_NAME };
			m_session.loadComponents( compNames );

            m_session.start();
        }
        catch(DuplicateObjectException e)
        {
            e.printStackTrace();
            exit();
        }

        m_hostName = hostName;

        // Get a reference to the needed components.
        m_commEvents =
            (CommunityEventsService)m_session.getCompApi(CommunityEventsService.COMP_NAME);
        m_commEvents.addUserLoginListener(this);

        ChannelService channelService =
            (ChannelService)m_session.getCompApi(ChannelService.COMP_NAME);
        channelService.addChannelServiceListener(this);

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
        int[] supportedServices = { SERVICE_TYPE };

        // Server applications login directly to the server, and not through
        // the mux. So we can't use the default port.
        Connection[] connections = {
            new SocketConnection(1516, 17000),
        };
        saService.setConnectivity(connections);

        saService.addLoginListener(this);
        saService.loginAsServerApp(hostName, loginType, "OfflineMessages",
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
        // Get the incoming data.
        Channel cnl = event.getChannel();
        try
        {
            Channel channel = event.getChannel();

            // Check the service type of the event to make sure it is an offline message service type
            if (channel.getServiceType() == SERVICE_TYPE)
            {
                NdrInputStream inStream = new NdrInputStream(cnl.getCreateData());
    
                STId receiverId = new STId(inStream);
                String receiverName = inStream.readUTF();
                String message = inStream.readUTF();
    
                STUser receiver = new STUser(receiverId, receiverName, "");
                UsersHandler handler = new UsersHandler(m_session, m_hostName,
                                                      cnl.getRemoteInfo(), receiver,
                                                      message);
    
                m_watchedUsers.put(receiverId.getId(), handler);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            cnl.close(STError.ST_INVALID_DATA, null);
            return;
        }
        // No need for the channel anymore.
        cnl.close(STError.ST_OK, null);
    }

    //
    // User Login Listener.
    //
    /**
     * Indicates that a user was successfully logged in to the Sametime
     *	community.
     *
     * @param event The event object.
     * @see UserLoginEvent#getUserInstance
     */
	public void userLoggedIn (UserLoginEvent  event)
    {
        // Check if we are interested in this user.
        STUser user = event.getUserInstance();
        Object o = m_watchedUsers.remove(user.getId().getId());
        if (o != null)
        {
            ((UsersHandler)o).receiverOnline();
        }
    }

	/**
     * Indicates that a user was logged out from the Sametime community.
     *
     * @param event The event object.
     * @see UserLoginEvent#getUserInstance
     */
	public void userLoggedOut(UserLoginEvent  event)
    {}


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
        System.out.println("SA: LoggedIn");
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
        System.out.println("SA: LoggedOut " + event.getReason());
        exit();
    }

    /**
     * Application entry point.
     */
    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.err.println("Usage: OfflineMessagesSA [serverName]");
            System.exit(0);
        }

        new OfflineMessagesSA(args[0]);
    }
}
