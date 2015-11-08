//
// Licensed Materials - Property of IBM
//
// L-MCOS-96LQPJ
//
// (C) Copyright IBM Corp. 2007, 2013. All rights reserved.
//
// US Government Users Restricted Rights- Use, duplication or
// disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
//

package com.ibm.collaboration.realtime.sample.sthelper;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.ibm.collaboration.realtime.jsthelper.JSTHelper;

public class STHelperExampleDlg extends JFrame implements WindowListener {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JSplitPane jSplitPane = null;

	private JPanel jPanelTop = null;

	private JPanel jPanelBottom = null;

	private JLabel jLabel = null;

	private JButton jButton = null;

	private JList jList = null;

	private JScrollPane jScrollPane = null;

	private JSTHelper stHelper = null;

	// The Sametime Helper event handler.
	private STHelperExampleEventHandler stHelperEventHandler = null;

	/**
	 * This is the default constructor
	 */
	public STHelperExampleDlg() {
		super();
		initialize();
	}

	/**
	 * This method initializes jSplitPane
	 *
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPane.setDividerSize(0);
			jSplitPane.setDividerLocation(60);
			jSplitPane.setEnabled(true);
			jSplitPane.setTopComponent(getJPanelTop());
			jSplitPane.setBottomComponent(getJPanelBottom());
			jSplitPane.setVisible(true);
		}
		return jSplitPane;
	}

	/**
	 * This method initializes jPanelTop
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelTop() {
		if (jPanelTop == null) {
			jLabel = new JLabel();
			jLabel
					.setText("Press the button to fill the list with Sametime contacts:   ");
			jLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
			jPanelTop = new JPanel();
			jPanelTop.setLayout(new GridBagLayout());
			jPanelTop.add(jLabel, new GridBagConstraints());
			jPanelTop.add(getJButton(), new GridBagConstraints());
		}
		return jPanelTop;
	}

	/**
	 * This method initializes jPanelBottom
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelBottom() {
		if (jPanelBottom == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.insets = new Insets(5, 30, 30, 30);
			jPanelBottom = new JPanel();
			jPanelBottom.setLayout(new GridBagLayout());
			jPanelBottom.add(getJScrollPane(), gridBagConstraints1);
		}
		return jPanelBottom;
	}

	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Get Contacts");
			jButton.setFont(new Font("Dialog", Font.PLAIN, 11));
			jButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getSametimeContacts();
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jList
	 *
	 * @return javax.swing.JList
	 */
	private JList getJList() {
		if (jList == null) {
			jList = new JList();
			jList.setAutoscrolls(true);
			jList.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			jList.setModel(new DefaultListModel());
		}
		return jList;
	}

	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJList());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJSplitPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method performs the initialization.
	 *
	 * @return void
	 */
	private void initialize() {
		this.setSize(433, 297);
		this.setContentPane(getJContentPane());
		this.setTitle("STHelperExample");
		this.addWindowListener(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

		// Create the Sametime Helper object.
		stHelper = new JSTHelper();
		// Create the STHelperExampleEventHandler to handle
		// the Sametime Helper related events.
		stHelperEventHandler = new STHelperExampleEventHandler();
		// Add the STHelperExampleEventHandler
		// to the Sametime Helper object.
		stHelper.addSametimeCallback(stHelperEventHandler);
		// Connect the Sametime Helper.
		stHelper.connect();
	}

	/**
	 * This method performs the uninitialization.
	 *
	 * @return void
	 */
	private void unitialize() {
		// Disconnect the Sametime Helper.
		stHelper.disconnect();
		// Remove the STHelperExampleEventHandler
		// from the Sametime Helper object.
		stHelper.removeSametimeCallback(stHelperEventHandler);
		stHelper = null;
		stHelperEventHandler = null;
	}

	// Method that uses the IBM Sametime Helper Library to request
	// the list of IBM Sametime Connect Client contacts, and populate
	// the UI List control with the returned list of contacts.
	private void getSametimeContacts() {
		// Sametime Group type
		String strGroupType = "all";
		// Invoke the Sametime Helper to obtain the group names.
		stHelper.getGroups(strGroupType);

		// Wait for the event data to return the group names.
		String eventData[] = stHelperEventHandler.waitForEventData(60000);

		if (eventData != null) {
			// Assign a Vector to hold the list of contact names
			// to be used as the data for the UI List control.
			Vector contacts = new Vector();

			// Assign the array that has the Sametime Contact Group names.
			String strGroupNames[] = eventData;

			// Loop through the Sametime Contact Group names and retrieve
			// the Sametime Contact names for each group.
			for (int i = 0; i < strGroupNames.length; i++) {
				// Assign the current group name.
				String strGroupName = strGroupNames[i];
				// Invoke the Sametime Helper to obtain the contact
				// names for the current group.
				stHelper.getContacts(strGroupName);

				// Wait for the event data to return the contact names.
				eventData = stHelperEventHandler.waitForEventData(60000);

				if (eventData != null) {
					// Assign the current contact.
					String strContactNames[] = eventData;
					// Loop through the Sametime Contact names and add each name
					// to the Vector of contact names.
					for (int ct = 0; ct < strContactNames.length; ct++) {
						contacts.add(strContactNames[ct]);
					}
				}
			}

			// Set the UI List control data with the Vector
			// of contact names.
			getJList().setListData(contacts);
		}
	}

	public void windowActivated(WindowEvent evt) {
	}

	public void windowClosed(WindowEvent evt) {
	}

	public void windowClosing(WindowEvent evt) {
		// Catch the window closing event, and perform
		// any necessary unintiialization before
		// the application exits.
		unitialize();
	}

	public void windowDeactivated(WindowEvent evt) {
	}

	public void windowDeiconified(WindowEvent evt) {
	}

	public void windowIconified(WindowEvent evt) {
	}

	public void windowOpened(WindowEvent evt) {
	}
} // @jve:decl-index=0:visual-constraint="10,10"
