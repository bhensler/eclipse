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



import java.awt.event.*;
import java.awt.*;

/**
 *	Panel to manage bids in auctions.
 *  Creation date: (10/2/2001 1:20:04 PM)
 * @author: Volker Juergensen
 */
 class AuctionManagerPanel extends Panel implements ActionListener
 {
  	 /**
	  * The auctioneer controlling the auction
	  */
	 private AuctionFrame m_auctionFrame;

	 /**
	  * Auction status panel
	  */
	 protected AuctionStatusPanel m_auctionStatus;

	 /**
	  * Sell button
	  */
	 private Button m_btnSell;

	 /**
	  * Start/Stop button
	  */
	 private Button m_btnStartStop;

     /**
      * BidManager constructor comment.
      */
     public AuctionManagerPanel(AuctionFrame frame)
     {
      	super();
        	
        m_auctionFrame = frame;
        
        Panel p = new Panel(new FlowLayout());

		// add button panel
		p.add(m_btnStartStop = new Button("Start"));
		p.add(m_btnSell = new Button("One"));

		this.setLayout(new BorderLayout());

		this.add("North",
			m_auctionStatus = new AuctionStatusPanel());
		this.add("South", p);

		// listeners for AuctionManager Buttons
		m_btnStartStop.addActionListener(this);
		m_btnSell.addActionListener(this);

		enableGuiItems(false);
     }
     
     AuctionStatusPanel getStatusPanel()
     {
         return m_auctionStatus;
     }
        
     /**
	 * Insert the method's description here.
	 * @param enable boolean
	 */
	protected void enableGuiItems(boolean enable)
	{
		m_btnStartStop.setEnabled(enable);
		m_btnSell.setEnabled(enable);
	}
	   
    /**
    * Invoked when an action occurs.
    */
    public void actionPerformed(ActionEvent event)
    {
       	if (event.getSource() == m_btnSell)
       	{
       		String label = m_btnSell.getLabel();

       		if (label.equals("One"))
       		{
       			System.out.println("ActionPerformed: One");
       			m_auctionFrame.setAuctionStatus(AuctionConstants.AUCTION_STATUS_ONE);
       		}
       		else if (label.equals("Two"))
       		{
       			System.out.println("ActionPerformed: Two");
       			m_auctionFrame.setAuctionStatus(AuctionConstants.AUCTION_STATUS_TWO);
            }
      		else if (label.equals("Sold"))
       		{
       			System.out.println("ActionPerformed: Sold");
       			m_auctionFrame.setAuctionStatus(AuctionConstants.AUCTION_STATUS_SOLD);
       		}
        }
       	else if (event.getSource() == m_btnStartStop)
           	{
       		String label = m_btnStartStop.getLabel();
       		if (label.equals("Start"))
       		{
       			System.out.println("ActionPerformed: Start");
       			m_auctionFrame.setAuctionStatus(AuctionConstants.AUCTION_STATUS_ACTIVE);
                m_auctionFrame.setCurrentBid(m_auctionFrame.getCurrentItem().getPrice());
       		}
       		else if (label.equals("Stop"))
       		{
       			System.out.println("ActionPerformed: Stop");
       			m_auctionFrame.setAuctionStatus(AuctionConstants.AUCTION_STATUS_INACTIVE);
                m_auctionFrame.getCurrentRoom().nextItem();
       		}
    	}
    }
     
     /**
     * Insert the method's description here.
     * Creation date: (10/5/2001 11:16:00 AM)
     * @param active boolean
     */
    public void statusChanged(int status)
    {
    	m_auctionStatus.statusChanged(status);
        
        switch (status)
    	{
    		case AuctionConstants.AUCTION_STATUS_INACTIVE :
    		{
    			m_btnStartStop.setLabel("Start");
    			m_btnSell.setEnabled(false);
    			m_auctionFrame.getCurrentRoom().setCurrentBid(0);
                //m_auctionStatus.bidChanged(0);
                break;
    		}
    		case AuctionConstants.AUCTION_STATUS_ACTIVE :
    		{
    			m_btnStartStop.setLabel("Stop");
    			m_btnSell.setLabel("One");				
    			m_btnSell.setEnabled(true);
    			break;
    		}
    		case AuctionConstants.AUCTION_STATUS_ONE :
    		{
    			m_btnSell.setLabel("Two");
    			break;
    		}
    		case AuctionConstants.AUCTION_STATUS_TWO :
    		{
    			m_btnSell.setLabel("Sold");
    			break;
    		}
    		case AuctionConstants.AUCTION_STATUS_SOLD :
    		{
    			m_btnSell.setLabel("One");
    			m_btnSell.setEnabled(false);

    			break;
    		}
    	}
    }
    
   
}
