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



import com.lotus.sametime.core.types.*;
import com.lotus.sametime.core.constants.*;
import com.lotus.sametime.core.comparch.*;
import com.lotus.sametime.community.*;
import com.lotus.sametime.placessa.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;

/**
 * The frame to manage the auction.
 */
public class AuctionFrame extends Frame implements ActionListener, ItemListener
{
    //
    // Members.
    //
    
    /**
	 * The auction house server application.
	 */
    private AuctionHouse m_auctionHouse;
    
    /**
     * The current room.
     */
    private AuctionRoom m_currentRoom;
    
    /**
     * The listener to the currently watched room's auditorium.
     */
    private SectionHandler m_sectionHandler;
    
    /**
     * The table of available rooms.
     */
    private Hashtable m_rooms = new Hashtable();
    
    /**
	 * The list of anonymous users.
	 */
	private List m_anonymousAwarenessList;
    
    /**
     * The list of the people in the auditorium.
     */
    private List m_auditoriumAwarenessList;

	/**
     * The send button.
     */
	private Button m_btnSend;
    
    /**
     * The send text field.
     */
	private TextField m_tfSend;
    
    /**
     * The chat transcript.
     */
    protected TextArea m_taTranscript;
    
    /**
     * The choice of available rooms.
     */
    private Choice m_chRooms;
    
    /**
     * The bid manager panel.
     */
    private AuctionManagerPanel m_auctionManager;

    /**
     * Construct a new auction frame.
     */
    public AuctionFrame(AuctionHouse auctionHouse)
    {
        super("Auction House");
        m_auctionHouse = auctionHouse;

        m_sectionHandler = new SectionHandler();
        initUI();
    }
    
    /**
     * Add a new room to the list of available auction rooms.
     */
    void addRoom(AuctionRoom room)
    {
        m_rooms.put(room.getRoomName(), room);
        m_chRooms.add(room.getRoomName());
        
        // First room.
        if (m_currentRoom == null)
        {
            m_currentRoom = room;
            m_currentRoom.getAuditorium().addSectionListener(m_sectionHandler);
            m_currentRoom.getAnonymousSection().addSectionListener(m_sectionHandler);
            enableGuiItems(true);
            
            m_currentRoom.setWatched(true);
        }
    }
    
    /**
     * Add a new room to the list of available auction rooms.
     */
    void removeRoom(String roomName)
    {
        m_rooms.remove(roomName);
        m_chRooms.remove(roomName);
    }
    
    //
    // Helper functions.
    //
	
    /**
	 * Set up the ui components.
	 */
	private void initUI()
	{
		setLayout(new BorderLayout());
        setBackground(Color.orange);

        Font bold = new Font("Dialog", Font.BOLD, 12);

		// Send messages area
		Panel sendPanel = new Panel(new BorderLayout());
		sendPanel.add("East", m_btnSend = new Button("Send"));
		sendPanel.add("Center", m_tfSend = new TextField());

		// Transcript area
		Panel transPanel = new Panel(new BorderLayout());
		transPanel.add("South", sendPanel);
		transPanel.add("Center", m_taTranscript = new TextArea(5, 40));

		m_taTranscript.setFont(new Font("Courier New", Font.PLAIN, 12));
		m_taTranscript.setForeground(Color.blue);
		m_taTranscript.setBackground(Color.white);
		m_taTranscript.setEnabled(true);
		m_taTranscript.setEditable(false);

		// AuctionManagerPanel
		m_auctionManager = new AuctionManagerPanel(this);
        Panel auctionPanel = new Panel(new BorderLayout());
        auctionPanel.add("North", m_chRooms = new Choice());
        auctionPanel.add("Center", m_auctionManager);
        m_chRooms.addItemListener(this);
            
        Label header = new Label();
		header.setFont(bold);
		header.setText("Transcript");
		transPanel.add("North", header);

		// Place Awareness List
		Panel listPanel = new Panel(new BorderLayout());

		m_anonymousAwarenessList = new List();
		m_auditoriumAwarenessList = new List();

		Panel anonp = new Panel(new BorderLayout());
		anonp.add("North", new Label("Anonymous"));
		anonp.add("Center", m_anonymousAwarenessList);
		anonp.setFont(bold);

		Panel audp = new Panel(new BorderLayout());
		audp.add("North", new Label("Auditorium"));
        audp.add("Center", m_auditoriumAwarenessList);
		audp.setFont(bold);
        
        listPanel.add("West", anonp);
		listPanel.add("East", audp);

		// Listeners
		m_btnSend.addActionListener(this);
		m_tfSend.addActionListener(this);

		// My own layout
		setLayout(new BorderLayout());
		add("East", listPanel);
		add("South", transPanel);
		add("West", auctionPanel);

		// Enable the gui items only after we enter to the place.
		enableGuiItems(false);

        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent event)
            {
                m_auctionHouse.exit();
                dispose();
            }
        });
    }
	
    /**
     * Set the frame state.
     */
	void enableGuiItems(boolean enable)
	{
		m_btnSend.setEnabled(enable);
		m_tfSend.setEnabled(enable);
		m_anonymousAwarenessList.setEnabled(enable);
        m_auditoriumAwarenessList.setEnabled(enable);
		m_tfSend.requestFocus();
        
        m_auctionManager.enableGuiItems(enable);
	}
    
    /**
	 * A text message was received.
	 */
	void write(String text)
	{
	    m_taTranscript.append(text + "\n");
        m_tfSend.requestFocus();
    }
    
    /**
     * Current bid was changed. update the UI.
     */
    AuctionStatusPanel getStatusPanel()
    {
        return m_auctionManager.m_auctionStatus;
    }
    /**
     * Current bid was changed. update the UI.
     */
    AuctionManagerPanel getAuctionPanel()
    {
        return m_auctionManager;
    }
    /**
     * Set the status of the current room.
     */
    void setAuctionStatus(int status)
    {
        m_currentRoom.setAuctionStatus(status);
    }
    
    void setCurrentBid(int bid)
    {
        m_currentRoom.setCurrentBid(bid);
    }
    
    public AuctionRoom getCurrentRoom()
    {
        return m_currentRoom;
    }
    
    public AuctionItem getCurrentItem()
    {
        return m_currentRoom.getCurrentItem();
    }
    
    //
    // Listeners.
    //
    
    /**
	 * Button clicks.
	 */
	public void actionPerformed(ActionEvent event)
	{
		if ((event.getSource() == m_btnSend) || 
            (event.getSource() == m_tfSend))
		{
			// Send the text to the place.
            m_currentRoom.getPlace().sendText(m_tfSend.getText());
			m_tfSend.setText("");
		}
    }
    
    /**
     * Choice value has changed.
     */
    public void itemStateChanged(ItemEvent event)
    {
        String roomName = m_chRooms.getSelectedItem();
        if (roomName.equals(m_currentRoom.getRoomName()))
        {
            return;
        }
            
        // Switch to the new room.
        switchRoom(roomName);
    }
    
    void switchRoom(String roomName)
    {
        if (m_currentRoom != null)
        {
            m_currentRoom.getAuditorium().removeSectionListener(m_sectionHandler);
            m_currentRoom.getAnonymousSection().removeSectionListener(m_sectionHandler);
            m_currentRoom.setWatched(false);
        }
        
        m_currentRoom = (AuctionRoom)m_rooms.get(roomName);
        m_currentRoom.setWatched(true);
        
        m_auctionManager.statusChanged(m_currentRoom.getAuctionStatus());
        m_auctionManager.m_auctionStatus.itemChanged(m_currentRoom.getCurrentItem());
        m_auctionManager.m_auctionStatus.bidChanged(m_currentRoom.getCurrentBid());
        m_anonymousAwarenessList.removeAll();
        m_auditoriumAwarenessList.removeAll();
        
        m_currentRoom.getAuditorium().addSectionListener(m_sectionHandler);
        m_currentRoom.getAnonymousSection().addSectionListener(m_sectionHandler);
    }
	
	/**
     * Section Listener.
     */
    class SectionHandler extends SectionAdapter    
	{        
		/**
         * Users have entered the section.
         * 
         * @param event The event object.
         * @see SectionEvent#getEnteredUsers
         */
        public void usersEntered(SectionEvent event)
        {
            // Find the appropriate list.
            Section section = event.getSection();
            List list = m_anonymousAwarenessList;
            if (section.equals(m_currentRoom.getAuditorium()))
            {
                list = m_auditoriumAwarenessList;
            }
            
            UserInPlace[] users = event.getEnteredUsers();
            for (int i = 0; i < users.length; i++)
            {
                list.add(users[i].getDisplayName());
            }
        }
    
        /**
         * A user has left the place.
         * 
         * @param event The event object.
         * @see SectionEvent#getDepartedUser
         */
        public void userLeft(SectionEvent event)
        {
            Section section = event.getSection();
            List list = m_anonymousAwarenessList;
            if (section.equals(m_currentRoom.getAuditorium()))
            {
                list = m_auditoriumAwarenessList;
            }
            
            list.remove(event.getDepartedUser().getDisplayName());
        }
    }
}
