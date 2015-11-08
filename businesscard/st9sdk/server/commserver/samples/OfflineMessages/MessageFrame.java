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

import com.lotus.sametime.core.types.STUser;

/**
 * The oflline message frame.
 *
 * @author Assaf Azaria, July 2001.
 */
class MessageFrame extends Frame implements ActionListener
{
    /**
     * Our parent frame.
     */
    private Client m_parent;

    /**
     * The text area.
     */
    private TextArea m_textArea;

    /**
     * The send button.
     */
    private Button m_btnSend;

    /**
     * The close button.
     */
    private Button m_btnClose;

    /**
     * The message nominee.
     */
    private STUser m_user;

    /**
     * Create a new message frame.
     */
    public MessageFrame(Client parent, STUser user)
    {
        super("Enter offline message");

        m_user = user;
        m_parent = parent;

        setBackground(Color.lightGray);

        Panel btnPanel = new Panel(new GridLayout(1,5));
        btnPanel.add(new Panel());
        btnPanel.add(new Panel());
        btnPanel.add(new Panel());
        btnPanel.add(m_btnSend = new Button("Send"));
        btnPanel.add(m_btnClose = new Button("Close"));

        setLayout(new BorderLayout());
        add("Center", m_textArea = new TextArea());
        add("South", btnPanel);

        m_btnSend.addActionListener(this);
        m_btnClose.addActionListener(this);

        addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent event)
                {
                    dispose();
                }

            });
    }

    /**
     * Button clicks.
     */
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == m_btnSend)
        {
            String message = m_textArea.getText();
            m_parent.sendOfflineMessage(m_user, message);
            dispose();
        }
        else if (event.getSource() == m_btnClose)
        {
            dispose();
        }
    }
}
