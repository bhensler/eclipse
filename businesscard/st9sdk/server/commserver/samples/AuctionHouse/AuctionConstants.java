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



/**
 * Constants for auction house sample.<p>
 *
 * @author: Volker Juergensen
 */
class AuctionConstants
{
	/**
	 * First place name.
	 */
    public static final String PLACE1_NAME = "Room11";
    
    /**
     * Second place name.
     */
    public static final String PLACE2_NAME = "Room22";
    
    /**
     * Third place name.
     */
    public static final String PLACE3_NAME = "Room33";
    
    /**
     * The type of place we use.
     */
    public static final int PLACE_TYPE = 0x55;
    
    /**
     * Our activity type.
     */
    public static final int ACTIVITY_TYPE = 0x61;
    
    /**
	 * The place password. Usually, you would distribute it to
	 * the users and prompt for it when they try to enter.
	 */
	public static final String PLACE_PASSWORD = "secret";

	/**
	 * Status: Auction stopped
	 */
	public static final int AUCTION_STATUS_INACTIVE = 0;

	/**
	 * Status: Auction active
	 */
	public static final int AUCTION_STATUS_ACTIVE = 1;

	/**
	 * Status: Auction status "ONE"
	 */
	public static final int AUCTION_STATUS_ONE = 2;

	/**
	 * Status: Auction status "TWO"
	 */
	public static final int AUCTION_STATUS_TWO = 3;

	/**
	 * Status: Auction status "SOLD"
	 */
	public static final int AUCTION_STATUS_SOLD = 4;

	/**
	 * Auction place status attribute.<p>
	 * AuctionStatus ID: 100000+10
	 */
	public static final int AUCTION_STATUS_ATTR = 100000 + 10;

	/**
	 * Auction place current bid attribute.<p>
	 * Bid ID: 100000+20
	 */
	public static final int BID_ATTR_ID = 100000 + 20;

	/**
	 * Auction place current item title attribute.<p>
	 * Item Title ID: 100000+30
	 */
	public static final int AUCTION_ITEM_TITLE_ATTR = 100000 + 30;

	/**
	 * Auction place current item url attribute.<p>
	 * Item URL ID: 100000+31
	 */
	public static final int AUCTION_ITEM_URL_ATTR = 100000 + 31;

	/**
	 * Auction place current item minimum price attribute.<p>
	 * Item Price ID: 100000+32
	 */
	public static final int AUCTION_ITEM_PRICE_ATTR = 100000 + 32;

	/**
	 * Auction sendData used for bidding.<p>
	 * Bid Data ID: 100000 + 40
	 */
	public static final int BID_DATA_TYPE = 100000 + 40;

}
