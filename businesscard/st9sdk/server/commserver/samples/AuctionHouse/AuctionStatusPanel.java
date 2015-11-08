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

/**
* Panel showing the current auction status
* Creation date: (10/2/2001 1:20:04 PM)
* @author: Volker Juergensen
*/
class AuctionStatusPanel extends Panel 
{
    /**
     * Label for current bid
     */
    private Label m_lblCurrentBid;

    /**
    * Label for auction active/inactive
    */
    private Label m_lblAuctionActive;

    /**
    * Label for auction 1-2-Sold
    */
    private Label m_lblAuctionStep;

    /**
    * Label for auction item title
    */
    private Label m_lblAuctionItemTitle;

    /**
    * URL to item picture
    */
    private String m_sAuctionItemUrl;

    /**
    * Label for minimum item price
    */
    private Label m_lblAuctionItemPrice;

    /**
     * Canvas for Picture
     */
    private ImageCanvas m_icAuctionItemImage;

    /**
     * Default Image
     */
    private Image m_imDefaultImage;
	
        	
    /**
     * Bidder constructor comment.
     */
    public AuctionStatusPanel()
    {
        // default image display
    	m_imDefaultImage = 
    		Toolkit.getDefaultToolkit().getImage("default.jpg");
        m_icAuctionItemImage = new ImageCanvas(m_imDefaultImage);
            
        // UI.
        initUI();
    }
            
    /**
     * Bid has changed.
     */
    public void bidChanged(int bid)
    {
    	m_lblCurrentBid.setText(new Integer(bid).toString());
    }
    
	/**
	 * Item title has changed.
	 */
	public void itemTitleChanged(String title)
    {
        m_lblAuctionItemTitle.setText(title);
    }
	
	/**
	 * Item url has changed.
	 */
	public void itemUrlChanged(String url)
    {
        m_sAuctionItemUrl = url;
    	m_icAuctionItemImage.setImage(m_sAuctionItemUrl);
    }
	
	/**
	 * Item proce has changed.
	 */
	public void itemPriceChanged(int price)
    {
        m_lblAuctionItemPrice.setText(new Integer(price).toString());
    }
	
	public void itemChanged(AuctionItem item)
	{
		itemPriceChanged(item.getPrice());
		itemTitleChanged(item.getTitle());
		itemUrlChanged(item.getPictureUrl());
	}
	

	/**
     * Init UI Components
     */
    protected void initUI()
    {
    	Panel ip = new Panel();
    	ip.setSize(150, 150);
    	ip.add(m_icAuctionItemImage);

    	Panel p = new Panel();
    	p.setLayout(new GridLayout(0, 2));

    	p.add(new Label("Item:"));
    	p.add(m_lblAuctionItemTitle = new Label("     "));
    	p.add(new Label("Min. Price:"));
    	p.add(m_lblAuctionItemPrice = new Label("     "));
    	p.add(new Label("Status:"));
    	p.add(m_lblAuctionActive = new Label("Not active   "));
    	p.add(new Label("Current Bid:"));
    	p.add(m_lblCurrentBid = new Label("                  "));
    	p.add(new Label("Step:"));
    	p.add(m_lblAuctionStep = new Label("          "));

    	this.setLayout(new BorderLayout());

    	this.add("West", ip);
    	this.add("East", p);
    }
                
    /**
     * Status has changed. 
     */
    public void statusChanged(int status)
    {
    	switch (status)
    	{
    		case AuctionConstants.AUCTION_STATUS_ACTIVE :
    		{
    			m_lblAuctionActive.setText("Active");
    			m_lblAuctionStep.setText("");
    			break;
    		}
    		case AuctionConstants.AUCTION_STATUS_INACTIVE :
    		{
    			m_lblAuctionActive.setText("Not active");
    			m_lblAuctionStep.setText("");
        				
                break;
    		}
        			
            case AuctionConstants.AUCTION_STATUS_ONE :
    		{
    			m_lblAuctionStep.setText("ONE...");
    			break;
    		}
    		case AuctionConstants.AUCTION_STATUS_TWO :
    		{
    			m_lblAuctionStep.setText("TWO...");
    			break;
    		}
    		case AuctionConstants.AUCTION_STATUS_SOLD :
    		{
    			m_lblAuctionStep.setText("SOLD...");
    			break;
    		}
        }
    }
 }
