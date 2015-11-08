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

import java.util.Map;

import javax.swing.JOptionPane;

import com.ibm.collaboration.realtime.jsthelper.JSTCallback;

/**
 * This class implements the JSTCallback interface in order
 * to process Sametime Helper events.
 */
public class STHelperExampleEventHandler implements JSTCallback {
	//The current event related data.
	private String eventData[] = null;

	/**
	 * 	Convenience method which waits for up the specified max_time
	 *  in milliseconds for an event to be received from one of the
	 *  Sametime Helper JSTCallBack methods.
	 *
	 *  @param max_time the number of milliseconds to wait for an event
	 *  @return the current event related data
	 */
	public String[] waitForEventData(long max_time) {
		String data[] = null;

		try {
			synchronized (this) {
				wait(max_time);
				if (eventData != null) {
					data = eventData;
					eventData = null;
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;
	}

	/**
	 *  Call from the STHelper indicating that an eviction from the watch list
	 *  on the Sametime client has occurred.
	 *
	 *  The onEvictWatch will only be called in the case where the Sametime
	 *  client has been configured to limit the maximum number of simultaneous
	 *  contact watches.  For such a client, Least-Recently-Used tracking is
	 *  applied if a new watch is requested when the maximum has already been
	 *  reached:  the entry with the oldest LRU timestamp will have its watch
	 *  removed, and the OnEvictWatch will be distributed to let the STHelper
	 *  user know that presence information for that user is no longer
	 *  available.
	 *  The arrival of an onEvictWatch means presence info is not tracked,
	 *  but it does not invalidate the Sametime ID or display name.
	 *
	 *  @param partnerId contains the partnerId as detailed in getContacts
	 */
	public void onEvictWatch ( String partnerId ) {
		JOptionPane.showMessageDialog(null, "onEvictWatch", null,
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Result for one of the keys sent to a liveNameResolve request
	 *
	 * @param key
	 *            the key which resolved to the current results
	 * @param partnerInfo
	 *            Map of name-value pairs, see getContacts
	 */
	public void onLiveNameResolve(String key, Map partnerInfo) {
		JOptionPane.showMessageDialog(null, "onLiveNameResolve", null,
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Update the information on a partner who has a watch established
	 *
	 * @param partnerInfo
	 *            contains name-value pairs as detailed in getContacts
	 */
	public void onPersonUpdate(Map partnerInfo) {
		JOptionPane.showMessageDialog(null, "onPersonUpdate", null,
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Results for one request to directoryResolve
	 *
	 * @param key
	 *            the key which produced these results
	 * @param results
	 *            an array of Maps, each in turn a set of name-value pairs for
	 *            users who matched the search key
	 */
	public void onDirectoryResolve(String key, Map[] results) {
		JOptionPane.showMessageDialog(null, "onDirectoryResolve", null,
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Results for one request to getContacts
	 *
	 * @param groupName
	 *            the name of the groups getting contacts for
	 * @param results
	 *            an array of Strings, one for each contact
	 *
	 */
	public void onGetContacts(String groupName, String[] results) {
		synchronized (this) {
			eventData = results;
			notify();
		}
	}

	/**
	 * Results for one request to getGroups
	 *
	 * @param type
	 *            the type of group being requested ("all", "public", "private")
	 * @param results
	 *            an array of Strings, one for each map
	 */
	public void onGetGroups(String type, String[] results) {
		synchronized (this) {
			eventData = results;
			notify();
		}
	}

	/**
	 * User logged out
	 */
	public void onLogout() {
		JOptionPane.showMessageDialog(null, "onLogout", null,
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Call from STHelper indicating that Sametime functions are available
	 *
	 * @param statusCode
	 *            indicates first connect versus restored availability
	 */
	public void onSametimeAvailable(int code) {
		JOptionPane.showMessageDialog(null, "onSametimeAvailable", null,
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Call from STHelper indicating that Sametime functions are unavailable
	 */
	public void onSametimeUnavailable(int code) {
		JOptionPane.showMessageDialog(null, "onSametimeUnavailable", null,
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Call from STHelper for messages not fitting into one of the above
	 * categories
	 *
	 * @param messageType
	 *            the message type
	 * @param typeVersion
	 *            the type version
	 * @param map
	 *            the message argument map
	 */
	public void onMessage(String messageType, String typeVersion, Map map) {
		JOptionPane.showMessageDialog(null, "onMessage", null,
				JOptionPane.PLAIN_MESSAGE);
	}
}
