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

import com.lotus.sametime.community.Channel;
import com.lotus.sametime.community.ChannelEvent;
import com.lotus.sametime.community.ChannelListener;
import com.lotus.sametime.core.util.NdrInputStream;
import com.lotus.sametime.core.util.NdrOutputStream;
import com.lotus.sametime.core.types.*;
import com.lotus.sametime.core.constants.STError;
import com.lotus.sametime.core.constants.EncLevel;


import java.util.Vector;
import java.util.Enumeration;
import java.io.IOException;


/**
 *  UserHandler: Handle requests for one user
 */
public class UserHandler implements ChannelListener, MatchListener
{
	/**
	 * The owner updater
	 */
	private SportsUpdater m_updater;
	
	/**
	 * Our channel to the user
	 */
	private Channel m_cnl;
	
	/**
	 * List of subscribed matches
	 */
	private Vector m_matches = new Vector();
	
	/**
	 * CTor
	 */
	public UserHandler(SportsUpdater updater, ChannelEvent event)
	{
		// Save ref to parent
		m_updater = updater;
		
		 // Get the channel
        m_cnl = event.getChannel();
		
		// Add ourselve sa listener and accepth the channel
		m_cnl.addChannelListener(this);
		m_cnl.accept(EncLevel.ENC_LEVEL_DONT_CARE, null );
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
		// The user opens a channel to us, send a matches snapshot
		
		// Get a copy of all the matches from the upgrader
		Vector matches = m_updater.getMatches();
		
		// Dump the matches
		NdrOutputStream ndr = new NdrOutputStream();
		try
		{
			// Dump number of matches
			int i = matches.size();
			ndr.writeInt(i);
	
			// Dump all matches
			Match m;
			Enumeration enumeration = matches.elements();		
			while(enumeration.hasMoreElements())
			{
			    m = (Match) enumeration.nextElement();
				m.dump(ndr);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println("Error in Channel opened, could not dump matches");
			return;
		}		
		
		// Send the message
		m_cnl.sendMsg(SportsUpdater.MSG_AVAILABLE_MATCHES, 
											ndr.toByteArray(), false);
	}

    /**
     * Open request has failed.
     *
     * @param event The event object.
     */
    public void channelOpenFailed(ChannelEvent event)
	{
		System.out.println("channelOpenFailed");
		m_updater.removeUserHandler(this);
	}

    /**
     * The channel was closed.
     *
     * @param event The event object.
     */
    public void channelClosed(ChannelEvent event)
	{
		// Unsubscribe from all matches
		Match m;
		Vector temp = m_matches;
		Enumeration enumeration = temp.elements();		
		while(enumeration.hasMoreElements())
		{
		    m = (Match) enumeration.nextElement();
			m.removeMatchListener(this);
		}

		// Remove ourselves from the updater records
		m_updater.removeUserHandler(this);
	}

    /**
     * A message was received on the channel.
     *
     * @param event The event object.
     */
    public void channelMsgReceived(ChannelEvent event)
	{
		int msgType = event.getMessageType();
		switch ( msgType )
		{
			case SportsUpdater.MSG_SUBSCRIBE_2_MATCH:
				handleSubscribe(event, true);
				break;
			case SportsUpdater.MSG_UNSUBSCRIBE_FROM_MATCH:
				handleSubscribe(event, false);
				break;
			case SportsUpdater.MSG_AVAILABLE_MATCHES:
			case SportsUpdater.MSG_UPDATE_MATCH:
			default:
				System.out.println("Error in UserHandler::channelMsgReceived error in mdg type");
				break;
		};
	}
	
	/**
	 * Handle subscribe/unsubscribe requests
	 */
	private void handleSubscribe(ChannelEvent event, boolean subscribe)
	{
		String matchID;
		
		try
		{
			// This message only contains the match ID
			NdrInputStream ndr = new NdrInputStream(event.getData());
			matchID = ndr.readUTF();
		}
        catch (IOException e)
        {
            e.printStackTrace();
            event.getChannel().close(STError.ST_INVALID_DATA, null);
            return;
        }		
		
		// Subscribe/unsubscribe to this match
		subscribeToMatch(matchID, subscribe);
		
	}
	
	/**
	 * Subscribe/unsubscribe to a match
	 */
	private void subscribeToMatch(String matchID, boolean subscribe)
	{
		// Get a copy of all the matches from the updater
		Vector matches = m_updater.getMatches();
		Enumeration enumeration = matches.elements();		

		// Walk through all matches
		Match m;
		while(enumeration.hasMoreElements())
		{
			// Get next match
			 m = (Match) enumeration.nextElement();
			 
			 // Our match?
			 if ( m.getID().equals( matchID ) )
			 {
				 if ( subscribe )
				 {
					 // subscribe to the match and add it to our records
					 m.addMatchListener(this);
					 m_matches.addElement(m);
				 }
				 else
				 {
					 // Unsubscribe and remove the match from our records
					 m.removeMatchListener(this);
					 m_matches.removeElement(this);
				 }
			 }
		}
	}
	
	/** 
     * The match details has been changed
     * 
     * @param match The new match details
     */
    public void matchUpdated(Match match)
	{
		// Dump the match to a NDR
		NdrOutputStream ndr = new NdrOutputStream();
		try
		{
			match.dump(ndr);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println("Error dumping Match");
			return;
		}
		
		// Send the message
		if ( m_cnl.isOpen() )
		{
			m_cnl.sendMsg(SportsUpdater.MSG_UPDATE_MATCH, 
											ndr.toByteArray(), false);
		}
	}

}	
