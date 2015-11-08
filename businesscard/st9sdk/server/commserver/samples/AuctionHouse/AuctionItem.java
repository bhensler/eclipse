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
 * An Auction Item to be sold.<p>
 * It consists of a title, an URL pointing to the image, and the price.
 *
 * @author: Volker Juergensen
 */
public class AuctionItem
{

	/**
	 * Auction Item title
	 */
	private String m_sTitle = "";

	/**
	 * Auction Item picture URL
	 */
	private String m_sPictureUrl = "";

	/**
	 * Auction Item minimal price
	 */
	private int m_iPrice = 0;

	/**
	 * AuctionItem constructor with all fields.
	 *
	 * @param title String Item title
	 * @param pictureURL String Item picture URL
	 * @param price int Minimum price
	 */
	public AuctionItem(String title, String pictureURL, int price)
	{
		super();
		setTitle(title);
		setPictureUrl(pictureURL);
		setPrice(price);
	}
	
    /**
	 * get picture URL
	 *
	 * @return java.lang.String
	 */
	public String getPictureUrl()
	{
		return m_sPictureUrl;
	}
	/**
	 * Get minimum Price
	 *
	 * @return int
	 */
	public int getPrice()
	{
		return m_iPrice;
	}
	/**
	 * Get title
	 *
	 * @return java.lang.String
	 */
	public String getTitle()
	{
		return m_sTitle;
	}
	/**
	 * Set picture URL
	 *
	 * @param newPictureUrl String
	 */
	public void setPictureUrl(String newPictureUrl)
	{
		m_sPictureUrl = newPictureUrl;
	}
	/**
	 * Set minimum price
	 *
	 * @param newPrice int
	 */
	public void setPrice(int newPrice)
	{
		m_iPrice = newPrice;
	}
	/**
	 * Set title
	 *
	 * @param newTitle String
	 */
	public void setTitle(String newTitle)
	{
		m_sTitle = newTitle;
	}
}
