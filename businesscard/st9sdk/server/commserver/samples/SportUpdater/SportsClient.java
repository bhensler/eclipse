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
import com.lotus.sametime.core.util.NdrOutputStream;
import com.lotus.sametime.core.util.NdrInputStream;
import com.lotus.sametime.core.constants.*;
import com.lotus.sametime.core.types.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 * SportsClient: The SportsUpdater client
 */
public class SportsClient extends Frame 
		implements LoginListener, ActionListener, ChannelListener
{
    /**
     * The session object.
     */
    private STSession m_session;
	
  	/**
	 * Our channel
	 */
	Channel m_cnl;

	/**	 * the messages list	 */	private List m_lst;
	
	/**
	 * The MatchFrame dictionary (for each subscribed match we have a frame)
	 */
	private Hashtable m_frames = new Hashtable();
	
	
	public SportsClient()
	{
		super("Sports Client");
	}
	
	private void run(String name, String password, String server)
	{
		// Create and load the session of components.
        try
        {
            m_session = new STSession("SportsUpdaterClient");
            m_session.loadAllComponents();
            m_session.start();
        }
        catch(DuplicateObjectException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
		
		// Login to the communiuty    
        login(name, password, server);	
		
	}
	
	/**
     * Login to the sametime server.
     */
    void login(String name, String password, String hostName)
    {
        CommunityService commService
            = (CommunityService)m_session.getCompApi(CommunityService.COMP_NAME);

        commService.addLoginListener(this);
        commService.loginByPassword(hostName, name, password);
    }
	
	
	
	//
    // Channel Service Listener.
    //

    /**
     * Indicates that we were successfully logged in to the Sametime community.
     * 
     * @param            event The event object.
     * @see              LoginEvent#getCommunity
     */
    public void loggedIn(LoginEvent event)	
	{
		 // Get a reference to the channel services.
        ChannelService channelService =
            (ChannelService)m_session.getCompApi(ChannelService.COMP_NAME);
		
		// Create a channel to the sports updater SA
		m_cnl = channelService.createChannel(SportsUpdater.SPORTS_SA_SERVICE_TYPE, 0, 0,
                                       EncLevel.ENC_LEVEL_ALL, null, null);
		
		// Listen to this channel
		m_cnl.addChannelListener(this);
		
		// Open, the channel
        m_cnl.open();
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
		exit();
	}
	
	//
	// ChannelListener
	//
	/**
	 * The channel was opened.
	 *
	 * @param event The event object.
	 */
	public void channelOpened(ChannelEvent event)
	{
		System.out.println("SportClent::channelOpened");
		
		// The channel is opened, create the UI
		createUI();	
	}
	
	/**
     * Open request has failed.
     *
     * @param event The event object.
     */
    public void channelOpenFailed(ChannelEvent event)
	{
		System.out.println("SportClent::channelOpenFailed");
		System.out.println("Server Application is probably no running");

		exit();
	}

    /**
     * The channel was closed.
     *
     * @param event The event object.
     */
    public void channelClosed(ChannelEvent event)
	{
		System.out.println("SportClent::channelClosed");
		System.out.println("Reason = " + event.getReason() );
		
		exit();
	}

    /**
     * A message was received on the channel.
     *
     * @param event The event object.
     */
    public void channelMsgReceived(ChannelEvent event)
	{
		// What kind of message did we get?
		int msgType = event.getMessageType();
		switch ( msgType )
		{
			case SportsUpdater.MSG_AVAILABLE_MATCHES:
				handleAvailableMatches(event);
				break;
			case SportsUpdater.MSG_UPDATE_MATCH:
				handleUpdateMatch(event);
				break;
			case SportsUpdater.MSG_SUBSCRIBE_2_MATCH:
			case SportsUpdater.MSG_UNSUBSCRIBE_FROM_MATCH:
			default:
				System.out.println("Error in SportsClient::channelMsgReceived error in mdg type");
				break;
		};
	}

	/**
	 * Handle matches to go request
	 */
	private void handleAvailableMatches(ChannelEvent event)
	{
		try
		{
			// Get the data
			NdrInputStream ndr = new NdrInputStream(event.getData());

			// How many matches do we have?
			int size = ndr.readInt();
		
			// Read all matches
			for (int i = 0; i < size; i++)
			{
				Match m = new Match();
				m.load(ndr);
				
				// Add each matches to our list
				addMatchToList(m);
			}
		}
        catch (IOException e)
        {
            e.printStackTrace();
            event.getChannel().close(STError.ST_INVALID_DATA, null);
            return;
        }

	}
	
	/**
	 * Handle matche was updated request
	 */
	private void handleUpdateMatch(ChannelEvent event)
	{
		// Get the data
		NdrInputStream ndr = new NdrInputStream(event.getData());

		// Read the match details
		Match m = new Match();
		try
		{
			m.load(ndr);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println("Error loading Match");
			return;
		}
			
		// Find the match frame
		MatchFrame f = (MatchFrame)m_frames.get( m.getID() );
		
		// Update frame
		f.updateMatch( m );
	}
	
	/**
	 * Send a subscribe/unsubscribe message
	 */
	private void subscribeToMatch(String id, boolean subscribe)
	{
		// Write the mathc ID on the NDR
		NdrOutputStream ndr = new NdrOutputStream();
		try
		{
			ndr.writeUTF(id);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println("Error in subscribeToMatch");
			return;
		}				
		
		// Are we subscribing or unsubscribing
		short cmd = subscribe ? SportsUpdater.MSG_SUBSCRIBE_2_MATCH : 
								SportsUpdater.MSG_UNSUBSCRIBE_FROM_MATCH;
		
		// Send the message
		m_cnl.sendMsg(cmd, ndr.toByteArray(), false);
	}
	
	/**
	 * Create the UI
	 */
	private void  createUI()
	{
		m_lst = new List();
		m_lst.addActionListener(this);
		add(m_lst);		setSize(200, 300);
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(
		( screen.width - getSize().width ) / 2,
		( screen.height - getSize().height ) / 2 );

		setVisible(true);
		addWindowListener(							new WindowAdapter()							{								public void windowClosing(WindowEvent event)							    {									exit();							    }							}						);	
	}
	
	/**
	 * User double click a list item
	 */
	public void actionPerformed(ActionEvent event)
    {
		// Get the line
		String s = event.getActionCommand();
		if ( s.length() == 0 )
			return;
		
		// Get the first token from the line (match ID)
		StringTokenizer st = new StringTokenizer(s);
		s = st.nextToken();
		
		// Ok, add a new MatchFrame to our dictionary
		addMatchFrame(s);
    }
	
	/**
	 * Add a Match header to our list of matches
	 */
	private void addMatchToList(Match m)
	{
		// Create a string for this match
		String s ;
		s = m.getID() +  " " + 	m.getHomeTeamName() + 
						" Vs. " + m.getVisitorsTeamName();
		
		// Add it to our list
		m_lst.add(s);
	}
	
	/**
	 * Add a new MatchFrame
	 */
	private synchronized void addMatchFrame(String matchID)
	{
		// Create a new MatchFrame and add it to our hashtable
		Hashtable temp = (Hashtable) m_frames.clone();
		temp.put( matchID, new MatchFrame(this, matchID) );
		m_frames = temp;
		
		// Subscribe to this match
		subscribeToMatch(matchID, true );
	}
	
	/**
	 * Remove th frame for this match from our record
	 */
	public synchronized void removeMatchFrame(String matchID)
	{
		// Remove it from out hashtable
		Hashtable temp = (Hashtable) m_frames.clone();
		temp.remove(matchID);
		m_frames = temp;
		
		// Unsubscribe to this match
		subscribeToMatch(matchID, false);
	}
	
	
	
	private void exit()
	{
		// Unload the Samertime components and session
		m_session.stop();
        m_session.unloadSession();
		
		// Cloase all frames
		MatchFrame f;
		Hashtable temp = (Hashtable)m_frames.clone();
		Enumeration enumeration = temp.elements();
		while(enumeration.hasMoreElements())
		{
		    f = (MatchFrame) enumeration.nextElement();
			f.dispose();
		}
		
		// Exit
		dispose();
		System.exit(0);
	}
	
	public static void main(String[] args)
	{
		 if (args.length != 3)
        {
            System.err.println("Usage: Client [name] [password] [serverName]");
            System.exit(0);
        }
		 
		new SportsClient().run(args[0], args[1], args[2]);
	}
}
