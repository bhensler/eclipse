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

import com.lotus.sametime.core.util.NdrInputStream;
import com.lotus.sametime.core.util.NdrOutputStream;

import java.util.Vector;
import java.util.Enumeration;
import java.io.IOException;


/**
 *  Match: represent one match between two basketball teams
 */
public class Match
{
	/**
	 * The Match ID
	 */
	private String m_id;
	
	/**
	 * Home team name
	 */
	private String m_home;
	
	/**
	 * Visitors team name
	 */
	private String m_visitors;
	
	/**
	 * Home team score
	 */
	private int m_homeScore;
	
	/**
	 * Visitors score
	 */
	private int m_visitorsScore;
	
	/**
	 * The match listeners list
	 */
	Vector m_listeners = new Vector();
	
	/**
	 * CTor
	 */
	public Match()
	{
		m_id = new String(""); 
		m_home = new String(""); 
		m_visitors = new String(""); 
		m_homeScore = 0;
		m_visitorsScore = 0;
	}
	
	/**
	 * Add a MatchListener to our list
	 */
	public void addMatchListener(MatchListener listener)
	{
		// add the new listener
		m_listeners.addElement(listener);
		
		// Send a snapshot to the listener
		listener.matchUpdated(this);
	}
	
	/**
	 * Remove a match listener from our list
	 */
	public void removeMatchListener(MatchListener listener)
	{
		// Remove the listener
		m_listeners.removeElement(listener);
	}
	
	/**
	 * Send an update event to all listeners
	 */
	public void updateAllListeners()
	{
		MatchListener listener;
		Enumeration enumeration = m_listeners.elements();		
		while(enumeration.hasMoreElements())
		{
		    listener = (MatchListener) enumeration.nextElement();
			listener.matchUpdated(this);
		}
	}
	
	/**
	 * Get the match ID
	 */
	public String getID()
	{
		return m_id;
	}
	
	/**
	 * Set the match ID
	 */
	public void setID(String id)
	{
		m_id = id;
	}
	
	
	/**
	 * Get the home team name
	 */
	public String getHomeTeamName()
	{
		return m_home;
	}
	
	/**
	 * Set the home team name
	 */
	public void setHomeTeamName(String home)
	{
		m_home = home;
	}
	
	/**
	 * Get the visitors team name
	 */
	public String getVisitorsTeamName()
	{
		return m_visitors;
	}
	
	/**
	 * Set the visitors team name
	 */
	public void setVisitorsTeamName(String visitors)
	{
		m_visitors = visitors;
	}
	
	/**
	 * Get the home team score
	 */
	public int getHomeTeamScore()
	{
		return m_homeScore;
	}
	
	/**
	 * Set the home team score
	 */
	public void setHomeTeamScore(int score)
	{
		m_homeScore = score;
	}
	
	/**
	 * Get the visitors team score
	 */
	public int getVisitorsTeamScore()
	{
		return m_visitorsScore;
	}
	
	/**
	 * Set the visitors team score
	 */
	public void setVisitorsTeamScore(int score)
	{
		m_visitorsScore = score;
	}
	
	//
	// Streaming
	//
	
	/**
	 * Dump content to NDR
	 */
	public void dump(NdrOutputStream  ndr) throws IOException
	{
		// Write all our members into the NDR
		ndr.writeUTF(m_id);
		ndr.writeUTF(m_home);
		ndr.writeUTF(m_visitors);
		ndr.writeInt(m_homeScore);
		ndr.writeInt(m_visitorsScore);
	}
	
	/**
	 * Load content from NDR
	 */
	public void load(NdrInputStream  ndr) throws IOException
	{
		// Read all our members from the NDR
		m_id = ndr.readUTF();
		m_home = ndr.readUTF();
		m_visitors = ndr.readUTF();
		m_homeScore = ndr.readInt();
		m_visitorsScore = ndr.readInt();
	}
}
