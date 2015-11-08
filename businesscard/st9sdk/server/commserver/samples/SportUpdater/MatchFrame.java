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

import java.awt.*;
import java.awt.event.*;


/**
 * Frame: Show the UI for one game
 */
public class MatchFrame extends Frame 
{
	/**
	 * The owner sports client
	 */
	SportsClient m_sc;
	
	/**
	 * The Match ID
	 */
	String m_id;
	
	/**
	 * Home scores
	 */
	Label m_lblHomeScore;
	
	/**
	 * Visitors score
	 */
	Label m_lblVisitorsScore;
	
	/**
	 * Initial left position
	 */
	static int m_left = 10;
	
	/**
	 * Initial top position
	 */
	static int m_top = 10;
	
	/**
	 * CTor
	 */
	public MatchFrame(SportsClient client, String id)
	{
		m_sc = client;
		m_id = id;
		
		createUI();
	}
	
	private void createUI()
	{
		setLayout(new BorderLayout());
		
		m_lblHomeScore = new Label("Home:");
		add(m_lblHomeScore, BorderLayout.NORTH );
		
		m_lblVisitorsScore = new Label("Visitors:");
		add(m_lblVisitorsScore, BorderLayout.SOUTH );
		
		setSize(100, 100);
		setLocation( m_left, m_top );
		m_left += 20;
		m_top += 20;
		setVisible(true);
		addWindowListener(						new WindowAdapter()							{								public void windowClosing(WindowEvent event)							    {									m_sc.removeMatchFrame(m_id);									dispose();
							    }							}						);		
	}
	
	public void updateMatch(Match match)
	{
		// Do we have something to update?
		if ( match.getHomeTeamName().length() == 0 )
			return;
		
		// Title should be set only once
		if ( getTitle().length() == 0 )
		{
			setTitle( match.getHomeTeamName() + "Vs. " +  
									match.getVisitorsTeamName() );
		}
		
		// Update score
		String s = match.getHomeTeamName() + ": " + match.getHomeTeamScore();
		m_lblHomeScore.setText(s);
		
		s = match.getVisitorsTeamName() + ": " + match.getVisitorsTeamScore();
		m_lblVisitorsScore.setText(s);
	}
}
