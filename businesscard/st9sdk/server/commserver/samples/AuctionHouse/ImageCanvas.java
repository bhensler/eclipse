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



  // AWT
import java.awt.*;
import java.awt.image.*;

/**
 * Class to show a image of the Auction Item on screen
 */
class ImageCanvas extends Canvas implements ImageObserver
{
	/**
	 * Original Image
	 */
	private Image m_origimage;

	/**
	 * Current Image
	 */
	private Image m_curimage;

	/**
	 * Constructor to create the canvas
	 *
	 * @param image Image The image to be shown
	 */
	public ImageCanvas(Image image)
	{
		m_origimage = image;
		pickImage();
		setBounds(0, 0, 150, 150);
	}
	/**
	 * Paint the image
	 *
	 * @param g Graphics Graphics context to paint on
	 */
	public void paint(Graphics g)
	{
		g.drawImage(m_curimage, 0, 0, this);
	}
	/**
	 * Select the image
	 */
	public synchronized Image pickImage()
	{
		ImageProducer src = m_origimage.getSource();
		Image choice = m_origimage;

		if (m_curimage != choice)
			{
			if (m_curimage != null && m_curimage != m_origimage)
				{
				m_curimage.flush();
			}
			m_curimage = choice;
		}
		return choice;
	}
	/**
	 * Set a new Image
	 *
	 * @param image Image Image to be shown
	 */
	public void setImage(Image image)
	{
		m_origimage = image;
		pickImage();
		invalidate();
		repaint();
	}
	/**
	 * Set a new Image by URL
	 *
	 * @param url String URL to image
	 */
	public void setImage(String url)
	{
		Image im = Toolkit.getDefaultToolkit().getImage(url);
        setImage(im);
	}
}
