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
import com.lotus.sametime.core.types.*;
import com.lotus.sametime.core.util.*;

import java.util.*;
import java.io.*;
import java.net.URL;


/**
 * An auction room.
 * 
 * @author Assaf Azaria.
 */
public class AuctionRoom 
{
    //
    // Members.
    //
    
    /**
     * The room name.
     */
    private String m_roomName;
    
   	/**
	 * Current Buyer
	 */
	private String m_currentBuyer;

    /**
     * The current item.
     */
    private AuctionItem m_currentItem;
    
    /**
	 * The current bid.
	 */
	protected int m_currentBid = 0;
    
    /**
     * The room's status (as defined in AuctionConstants class).
     */
    private int m_auctionStatus;
    
    /**
     * States whether this room is the current room under control.
     */
    private boolean m_isWatched;
    
    /**
     * The anonymous section.
     */
    private Section m_anonSection;
    
    /**
     * The aditorioum.
     */
    private Section m_auditorium;
    
    /**
     * The list of items for this room.
     */
    private Vector m_items = new Vector();
    
    /**
     * The my activity object.
     */
    private MyActivity m_myActivity;
    
    /**
     * The auction frame.
     */
    private AuctionFrame m_frame;
    
    /**
     * Construct a new auction room.
     * 
     * @param activity The object representing our activity in the place.
     * @param frame The auction frame.
     */
    public AuctionRoom(MyActivity activity, AuctionFrame frame)
    {
        m_myActivity = activity;
        
        // We can easily set a different room name.
        m_roomName = m_myActivity.getPlace().getName();
        m_frame = frame;
        
        // Load the items for this room.
        loadItems();
        
        // Add the appropriate listeners.
		m_myActivity.getPlace().addPlaceListener(new PlaceHandler());
        m_myActivity.getPlace().addIncomingMessageListener(new MessageHandler());
        m_myActivity.addIncomingMessageListener(new MessageHandler());
        
        setCurrentBid(0);
		setAuctionStatus(AuctionConstants.AUCTION_STATUS_INACTIVE);
    }
    
    /**
	 * next Item from the list
	 */
	protected AuctionItem nextItem()
	{
		AuctionItem it;
		if (!m_items.isEmpty())
		{
			it = (AuctionItem) m_items.elementAt(0);
			m_items.removeElementAt(0);
		}
		else
		{
			it = new AuctionItem("--", "default.jpg", 0);
		}

		setAuctionItem(it);
        
        if (m_isWatched)
        {
            m_frame.getStatusPanel().itemChanged(it);
        }
		return it;

	}
    
    /**
	 * Change the auction item.
	 *
	 * @param item AuctionItem
	 */
	protected void setAuctionItem(AuctionItem item)
	{
		m_currentItem = item;
        Place place = m_myActivity.getPlace();
        
		place.putAttribute(
			new STAttribute(
				AuctionConstants.AUCTION_ITEM_TITLE_ATTR,
				m_currentItem.getTitle()));

		place.putAttribute(
			new STAttribute(
				AuctionConstants.AUCTION_ITEM_URL_ATTR,
				m_currentItem.getPictureUrl()));

		place.putAttribute(
			new STAttribute(
				AuctionConstants.AUCTION_ITEM_PRICE_ATTR,
				m_currentItem.getPrice()));

		if (!item.getTitle().equals("--"))
			place.sendText(
				"New Auction Item activated: " + m_currentItem.getTitle());
	}
	
	
    /**
	 * Set the current status of the auction.
	 */
	protected void setAuctionStatus(int status)
	{
		int oldStatus = m_auctionStatus;

		m_auctionStatus = status;

		m_myActivity.getPlace().putAttribute
			(new STAttribute(AuctionConstants.AUCTION_STATUS_ATTR, 
							 status));
        
        String message = "";

		switch (m_auctionStatus)
			{
			case AuctionConstants.AUCTION_STATUS_ACTIVE :
				{
					if (oldStatus == AuctionConstants.AUCTION_STATUS_INACTIVE)
						message = "Auction activated.";
					break;
				}
			case AuctionConstants.AUCTION_STATUS_INACTIVE :
				{
					if (m_auctionStatus != oldStatus)
						message = "Auction stopped.";
					m_currentBuyer = null;
					break;
				}
			case AuctionConstants.AUCTION_STATUS_ONE :
				{
					message = "Step: ONE...";
					break;
				}
			case AuctionConstants.AUCTION_STATUS_TWO :
				{
					message = "Step: TWO...";
					break;
				}
			case AuctionConstants.AUCTION_STATUS_SOLD :
				{
					message = "Step: SOLD for " + m_currentBid + " to " + m_currentBuyer;

					break;
				}
			default :
				{
				}
		}

		if (!message.equals(""))
			m_myActivity.getPlace().sendText(message);
	}
	
    /**
	 * Set the current bid attribute.
	 */
	protected void setCurrentBid(int bid)
	{
		m_currentBid = bid;
		
		m_myActivity.getPlace().putAttribute
            (new STAttribute(AuctionConstants.BID_ATTR_ID, bid));
	}
    
    //
    // Accessors.
    //
    
    /**
     * Get the room name.
     */
    protected String getRoomName()
    {
        return m_roomName;
    }
    
    /**
     * Get the auditorium section on which the bidders reside.
     */
    protected Section getAuditorium()
    {
        return m_auditorium;
    }
    
    protected AuctionItem getCurrentItem()
    {
        return m_currentItem;
    }
    
    /**
     * Get the section on which the anonymous users reside.
     */
    protected Section getAnonymousSection()
    {
        return m_anonSection;
    }
    
    /**
     * Get the place object that is associated with this room.
     */
    protected Place getPlace()
    {
        return m_myActivity.getPlace();
    }
    
    /**
     * Get current bid.
     */
    protected int getCurrentBid()
    {
        return m_currentBid;
    }
    
    /**
     * Get the status of this room.
     */
    protected int getAuctionStatus()
    {
        return m_auctionStatus;
    }
    
	/**
     * Set this room to be the currently watched room.
     */
    void setWatched(boolean watched)
    {
        m_isWatched = watched;
    }
    
	//
	// Helpers.
	//
	
    /**
     * Load the items for this room. The items are listed in a text file, with 
     * the same name as of the room.
     */
    void loadItems()
    {
        // Create the apropriate bundle.
        String fileName = m_roomName + ".properties";
        PropertyResourceBundle bundle = readBundle(fileName);

        // Read the items.
		int numItems = Integer.parseInt(bundle.getString("numItems"));
        for (int i = 1; i <= numItems; i++)
        {
			String itemTitle = bundle.getString("Item" + i + "Title");
			String itemURL = bundle.getString("Item" + i + "URL");
			int itemPrice =
				Integer.parseInt(bundle.getString("Item" + i + "Price"));

			AuctionItem item = new AuctionItem(itemTitle, itemURL, itemPrice);
			m_items.addElement(item);

			System.out.println(
				"New AuctionItem: " + "Room" + m_roomName + " " + 
                itemTitle + "//" + itemURL + "//" + itemPrice);
		}
    }
    
    /**
     * Read a property resource bundle from a propertyy file.
     *
     * @param file The name of the file to read from.
     */
    public PropertyResourceBundle readBundle(String file)
    {
        try 
        {
			FileInputStream inStream = new FileInputStream(file);
            PropertyResourceBundle bundle =
                new PropertyResourceBundle(inStream);
            inStream.close();
            return bundle;
        }
        catch (IOException e) 
        {
            return null;
        }
    }
    
    //
    // Listeners.
    //
	
    /**
     * Place Listener.
     */
    class PlaceHandler extends PlaceAdapter
    {        
		/** 
         * A new section was added to the place.
         * 
         * @param event The event object.
         * @see PlaceEvent#getSection
         */
        public void sectionAdded(PlaceEvent event)
        {
            Section section = event.getSection();
            
            if (!section.isStage())
            {
                if (m_anonSection == null)
                {
                    m_anonSection = section;
                    System.out.println("AnonSection: " + section.getMemberId());    
                }
                else if (m_auditorium == null)
                {
                    m_auditorium = section;
                    System.out.println("AuditoriumSection: " + section.getMemberId());    
                    
                    // and now we can add the room to the frame.
                    m_frame.addRoom(AuctionRoom.this);
                    nextItem();
                }
            }
        }
    
        /**
        * The place was left.
        * 
        * @param event The event object.
        * @see PlaceEvent#getPlace
        * @see PlaceEvent#getReason
        */
        public void placeLeft(PlaceEvent event)
        {
            System.out.println("Auction House: Left the place " + 
                               event.getReason());
            m_frame.removeRoom(m_roomName);
        }
    
        /**
         * An attribute was changed.
         */        
		public void attributeChanged(PlaceMemberEvent event)
	    {
			int value = event.getAttribute().getInt();
		
		    switch (event.getAttributeKey())
			{
			    case AuctionConstants.BID_ATTR_ID :
				{
					if (m_isWatched)
                    {
                        m_frame.getStatusPanel().bidChanged(value);
                    }
					break;
				}
			case AuctionConstants.AUCTION_STATUS_ATTR :
				{
					if (m_isWatched)
                    {
                        //m_frame.getStatusPanel().statusChanged(value);
					    m_frame.getAuctionPanel().statusChanged(value);
                    }
					break;
				}
			case AuctionConstants.AUCTION_ITEM_TITLE_ATTR :
				{
					m_frame.getStatusPanel().itemTitleChanged(
						event.getAttribute().getString());
					break;
				}
			case AuctionConstants.AUCTION_ITEM_URL_ATTR :
				{
					m_frame.getStatusPanel().itemUrlChanged(event.getAttribute().getString());
					break;
				}
			case AuctionConstants.AUCTION_ITEM_PRICE_ATTR :
				{
					m_frame.getStatusPanel().itemPriceChanged(event.getAttribute().getInt());
					break;
				}
			default :
				{
					System.out.println("Auctioneer::AttributeChanged: Not consumed");
				}

            }
        }
	    
        /**
         * An attribute was removed.
         */
        public void attributeRemoved(PlaceMemberEvent event)
	    {
		    System.out.println("AttributeRemoved" + event.getAttribute());
	    }
        
        /**
         * An attempt to set an attribute was failed.
         */
	    public void putAttributeFailed(PlaceMemberEvent event)
	    {
		    System.out.println("ChangeAttributeFailed: " + event.getReason());
	    }
    }
	
    /**
     * Incoming messages listener.
     */
    class MessageHandler extends IncomingMessageAdapter
    {
        /** 
         * A data message was received by this member.
         * 
         * @param event The event object.
         * @see MessageEvent#getSender
         * @see MessageEvent#getReceiver
         * @see MessageEvent#getData
         * @see MessageEvent#getDataType
         */
        public void dataReceived(MessageEvent event)
        {
 	        // A bid offer has arrived.
            System.out.println("DataReceived" + event.getData());

	        UserInPlace sender = (UserInPlace)event.getSender();
            String senderName = sender.getDisplayName();
            
            String sAmount = new String(event.getData());
	        int amount = new Integer(sAmount).intValue();

	        String sResponse = "";

	        if (amount > m_currentBid)
		    {
		        m_myActivity.getPlace().sendText("Bid from: " + senderName + 
                                                 " is: " + sAmount);
		        sResponse = "Bid accepted";
                
                // remember the last bidder
			    m_currentBuyer = senderName;

                // Set attributes
		        setCurrentBid(amount);
		        
                // in case we already have a call
			    if (m_auctionStatus != AuctionConstants.AUCTION_STATUS_ACTIVE)
				    setAuctionStatus(AuctionConstants.AUCTION_STATUS_ACTIVE);
            }
	        else
		    {
		        sResponse = "Bid was rejected because it was too small";
	        }
            sender.sendText(sResponse);
        }
        
        /**
	    * A text message was received.
	    */
	    public void textReceived(MessageEvent event)
	    {
		    System.out.println("textReceived");

		    PlaceMember sender = event.getSender();
		    String senderName = "";
            if (sender instanceof UserInPlace)
			{
			    senderName = ((UserInPlace) sender).getDisplayName();
			}
            else if (sender instanceof Activity)
            {
                senderName = "AuctionHouse";
            }
           
            String text = "Room: " + m_roomName + " Sender " + senderName + "          " + 
						  event.getText();
            m_frame.write(text);    
        }   
	}
}
