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

import com.lotus.sametime.core.comparch.*;
import com.lotus.sametime.community.*;
import com.lotus.sametime.commui.*;
import com.lotus.sametime.awarenessui.list.AwarenessList;
import com.lotus.sametime.awarenessui.tree.STTreeAVController;
import com.lotus.sametime.awarenessui.av.AVController;
import com.lotus.sametime.guiutils.tree.*;
import com.lotus.sametime.awarenessui.*;
import com.lotus.sametime.core.util.NdrOutputStream;
import com.lotus.sametime.core.constants.*;
import com.lotus.sametime.core.types.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Vector;
import java.util.Enumeration;

/**
 * The client for the offline message sample.
 *
 * @author Assaf Azaria, July 2001.
 */
public class Client extends Frame implements LoginListener, ActionListener,
    ResolveViewListener
{
    /**
     * The service type of the offline message server app.
     */
    static final int SERVICE_TYPE = 0x80000055;

    /**
     * The session object.
     */
    private STSession m_session;

    /**
     * The channel service.
     */
    private ChannelService m_channelService;

    /**
     * The awareness list.
     */
    private AwarenessList m_list;

    /**
     * The 'add users' button.
     */
    private Button m_btnAddUsers;

    /**
     * The add dialog.
     */
    private AddDialog m_addDialog;

    /**
     * Construct a new client.
     */
    public Client(String name, String password, String hostName)
    {
        super("Offline Message Client");

        // Create and load the session of components.
        try
        {
            m_session = new STSession("OfflineMessagesClient");
            m_session.loadAllComponents();
            m_session.start();
        }
        catch(DuplicateObjectException e)
        {
            e.printStackTrace();
            exit();
        }

        // Get a reference to the needed components.
        m_channelService =
            (ChannelService)m_session.getCompApi(ChannelService.COMP_NAME);

        createUI();
        login(name, password, hostName);
    }

    /**
     * Set up the ui components.
     */
    void createUI()
    {
        setLayout(new BorderLayout());

        m_list = new AwarenessList(m_session, true);
        m_list.setBackground(Color.white);

        // We need to overide to default controller to add the 'offline message'
        // menu item.
        m_list.setController(new MyListController(m_list.getModel()));

        m_btnAddUsers = new Button("Add Users");
        Panel btnPanel = new Panel(new GridLayout(0,1));
        btnPanel.add(m_btnAddUsers);
        m_btnAddUsers.addActionListener(this);
        m_btnAddUsers.setEnabled(false);

        add("South", btnPanel);
        add("Center", m_list);

        addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent event)
                {
                    setVisible(false);
                    exit();
                }
            });
    }

    /**
     * Login to the sametime server.
     */
    void login(String name, String password, String hostName)
    {
        CommunityService commService
            = (CommunityService)m_session.getCompApi(CommunityService.COMP_NAME);

        commService.addLoginListener(this);
        commService.loginByPassword(hostName, name, password);
    }

    //
    // Login listener.
    //

    /**
     * Indicates that we were successfully logged in to the Sametime community.
     *
     * @param event The event object.
     * @see LoginEvent#getCommunity
     */
    public void loggedIn(LoginEvent event)
    {
        System.out.println("CLIENT: LoggedIn");

        m_btnAddUsers.setEnabled(true);
    }

    /**
     * Indicates that we were successfully logged out of the Sametime community, or a login
     * request was refused.
     *
     * @param event The event object.
     * @see LoginEvent#getReason
     * @see LoginEvent#getCommunity
     */
    public void loggedOut(LoginEvent event)
    {
        System.out.println("CLIENT: LoggedOut " + event.getReason());
        exit();
    }

    //
    // 'Add users' button events.
    //
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == m_btnAddUsers)
        {
            m_addDialog = new AddDialog(this, m_session, "Add Users");
            m_addDialog.addResolveViewListener(this);
            m_addDialog.setVisible(true);
        }
    }

    //
    // Resolve view listener, events from the add dialog.
    //

    /**
     * Indicates a resolve operation was successful.
     *
     * @param            event The event object.
     * @see              ResolveViewEvent#getUser
     */
    public void resolved(ResolveViewEvent event)
    {
        // Add the selected users to the tree.
        STUser[] usersToAdd = { event.getUser() };
        m_list.addUsers(usersToAdd);
    }

    /**
     * Indicates that a resolve request failed.
     *
     * @param            event The event object.
     * @see              ResolveViewEvent#getReason
     */
    public void resolveFailed(ResolveViewEvent event)
    {}


    /**
     * Send the given message to the offline message sa.
     *
     * @param user The currently offline receiver of the message.
     * @param message The message.
     */
    void sendOfflineMessage(STUser user, String message)
    {
        NdrOutputStream outStream = new NdrOutputStream();
        try
        {
            user.getId().dump(outStream);
            outStream.writeUTF(user.getName());
            outStream.writeUTF(message);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return;
        }

        Channel channel =
                m_channelService.createChannel(SERVICE_TYPE, 0, 0,
                                               EncLevel.ENC_LEVEL_ALL,
                                               outStream.toByteArray(), null);
        channel.open();
    }

    /**
     * Application entry point.
     */
    public static void main(String[] args)
    {
        if (args.length != 3)
        {
            System.err.println("Usage: Client [name] [password] [serverName]");
            System.exit(0);
        }

        Client client = new Client(args[0], args[1], args[2]);
        client.pack();
        client.setSize(200, 400);
        client.setVisible(true);
    }

    /**
     * Terminate the application.
     */
    void exit()
    {
        m_session.stop();
        m_session.unloadSession();
        System.exit(0);
    }

    /**
     * A custom controller. Adds the 'offline message' menu item.
     */
    class MyListController extends AVController implements ActionListener
    {
        /**
         * The menu item.
         */
        MenuItem m_offlineMessage;

        /**
         * A flag stating whether regular chat is enabled.
         */
        boolean m_chatEnabled;

        /**
         * Construct a new controller.
         */
        public MyListController(AwarenessModel model)
        {
            super(model);

            m_offlineMessage = new MenuItem("Offline message");
            m_offlineMessage.addActionListener(this);
        }

        /**
         * A menu item was picked.
         */
        public void actionPerformed(ActionEvent event)
        {
            if (event.getSource() == m_offlineMessage)
            {
                TreeNode node = (TreeNode)m_selectedNodes.firstElement();

                STUser user = (STUser)node.getKey();
                MessageFrame frame = new MessageFrame(Client.this, user);
                frame.pack();
                frame.setVisible(true);
            }
        }

        /**
        * Set the state of the popup menu items according to the
        * selected nodes.
        *
        * @param nodes The selected nodes.
        */
        protected void enablePopupItems(Vector nodes)
        {
            // We only add our menu in case a regular chat can't happen.
            if(!m_chatEnabled)
            {
                m_popup.removeAll();
                m_popup.add(m_offlineMessage);
            }
            else
            {
                super.enablePopupItems(nodes);
            }
        }

        /**
         * Handle the selection changed event.
         *
         * @param selectedNodes The newly selected nodes.
         */
        protected void handleSelectionChanged(Vector selectedNodes)
        {
            // initialize values
            m_chatEnabled = false;

            boolean onlineUser = false;
            // Check for offline users.
            for (Enumeration e = selectedNodes.elements(); e.hasMoreElements();)
            {
                AwarenessNode node = (AwarenessNode)e.nextElement();

                // Check if it's an online user.
                if(node.isOnline())
                {
                    onlineUser = true;
                    break;
                }
            }
            m_chatEnabled = onlineUser && m_messageEnabled;
        }
    }
}

