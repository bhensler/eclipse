/* ***************************************************************** */
/*                                                                   */
/* Licensed Materials - Property of IBM                              */
/*                                                                   */
/* 5724-E76, 5724-L21, 5724-i67, 5724-L64, 5655-M44                  */
/*                                                                   */
/* Copyright IBM Corp. 2014  All Rights Reserved.                    */
/*                                                                   */
/* US Government Users Restricted Rights - Use, duplication or       */
/* disclosure restricted by GSA ADP Schedule Contract with           */
/* IBM Corp.                                                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.rtc.compliance.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.collaboration.realtime.event.EventStatus;
import com.ibm.collaboration.realtime.event.FilePostEvent;
import com.ibm.collaboration.realtime.event.FilePostEventStatus;
import com.ibm.collaboration.realtime.plugin.PluginException;
import com.ibm.collaboration.realtime.plugin.PluginFilePostHandler;
import com.ibm.collaboration.realtime.plugin.PluginStartupCallback;
import com.ibm.collaboration.realtime.plugin.User;

public class SampleFileHandler implements PluginFilePostHandler {

	private final static String PLUGIN_NAME =
		"com.ibm.rtc.compliance.sample.sampleFileHandlerPlugin";
	private static Logger _logger = Logger.getLogger(SampleFileHandler.class.getName());

	public void filePost(FilePostEvent event) {
		User u = event.getOriginatingUser();
		String uname = u.getID();
		if (u.isAnonymous())
			uname += " [Anon]";
		String filename = event.getFileName();
		if (_logger.isLoggable(Level.FINE)){
		_logger.fine("SampleFileHandler.filePost: \n" +
				" user = " + uname +
				"\n filename = " + filename);
		}
		if (filename.endsWith("B.txt")){
			event.setStatus(EventStatus.EVENT_BLOCKED_TYPE, "Filename ends in B.txt");
		} else {
			if ((filename.startsWith("file"))||(filename.startsWith("File"))){
				_logger.fine("File named file*.* will read, write+append scan message, set MODIFIED");
				File orig = event.getFile();
				scanFile(orig);
				event.setStatus(EventStatus.EVENT_MODIFIED_TYPE);
			}
			else if (filename.startsWith("readwrite")){
				_logger.fine("File named readwrite*.* will read, copy unchanged, set MODIFIED");
				File orig = event.getFile();
				readTrial(orig, true);
				event.setStatus(EventStatus.EVENT_MODIFIED_TYPE);
			}
			else if (filename.startsWith("readonly")){
				_logger.fine("File named readonly*.* will read, no copy, set OK");
				File orig = event.getFile();
				readTrial(orig, false);
				event.setStatus(EventStatus.EVENT_OK_TYPE);
			}
			else if (filename.startsWith("roDisclaimer")){
				_logger.fine("File named roDisclaimer*.* will read, no copy, but set MOD+Reason");
				File orig = event.getFile();
				readTrial(orig, false);
				event.setStatus(EventStatus.EVENT_MODIFIED_TYPE, "File roDisclaimer*.* is read, not written, and sets this disclaimer");
			}
			if (filename.endsWith("C.txt")){
				event.setReason("Filename ends in C, modify to CCC");
				filename = filename.substring(0, (filename.length() - 4));
				filename += "CC.txt";
				event.setFileName(filename);
				event.setStatus(EventStatus.EVENT_MODIFIED_TYPE);
			}
		}
	}

	private void scanFile(File target) {
		_logger.fine("enter scanFile");
		try {
			File parent = target.getParentFile();
			String targetName = target.getName();
			String rewriteName = targetName + ".rewrite";
			String oldName = targetName + ".old";
			File rewrite = new File(parent, rewriteName);
			FileInputStream in = new FileInputStream(target);
			FileOutputStream out = new FileOutputStream(rewrite);
			byte[] snippet = new byte[8];
			int sniplen = in.read(snippet);
			if (sniplen > 0)
				out.write(snippet, 0, sniplen);
			int len = 0;
			byte[] mainbuf = new byte[1024];
			while ((len = in.read(mainbuf)) > 0){
				out.write(mainbuf, 0, len);
			}
			if (sniplen > 0){
				String msg = "\nFile scanned, initial chars = ";
				out.write(msg.getBytes());
				out.write(snippet, 0, sniplen);
			}
			in.close();
			out.flush();
			out.close();
			File oldCp = new File(parent, oldName);
			boolean oldOk = target.renameTo(oldCp);
			if (_logger.isLoggable(Level.FINE)){
				_logger.fine("Rename " + Boolean.toString(oldOk) + " from " + targetName 
						+ " to " + oldName);
			}
			File targ2 = new File(parent, targetName);
			boolean success = rewrite.renameTo(targ2);
			if (!success)
				_logger.warning("Failed to rename temporary file in scanFile");
			oldCp.delete();
		}
		catch (Exception e){
			_logger.log(Level.SEVERE,"Error handling temporary files for file post", e);
		}
		_logger.fine("exiting scanFile");
	}

	private void readTrial(File target, boolean writeOut) {
		_logger.fine("enter readTrial");
		try {
			File parent = target.getParentFile();
			String rewriteName = target.getName() + ".rewrite";
			File rewrite = new File(parent, rewriteName);
			FileInputStream in = new FileInputStream(target);
			FileOutputStream out = new FileOutputStream(rewrite);
			byte[] snippet = new byte[8];
			int sniplen = in.read(snippet);
			if (sniplen > 0)
				out.write(snippet, 0, sniplen);
			int len = 0;
			byte[] mainbuf = new byte[1024];
			while ((len = in.read(mainbuf)) > 0){
				out.write(mainbuf, 0, len);
			}
			out.close();
			in.close();
			if (writeOut){
				boolean success = rewrite.renameTo(target);
				if (!success){
					_logger.warning("Failed to rename temporary file in readTrial");
				}
			}
		}
		catch (Exception e){
			_logger.log(Level.SEVERE,"Error handling temporary files for file post", e);
		}
		_logger.fine("exiting readTrial");
	}

	public void onFileDelete(FilePostEventStatus status) {
		User u = status.getOriginatingUser();
		String uname = u.getID();
		if (u.isAnonymous())
			uname += " [Anon]";
		String filename = status.getFileName();
		_logger.fine("SampleFileHandler.onFileDelete: \n" +
				" user = " + uname +
				"\n filename = " + filename);
	}

	public void onFilePost(FilePostEventStatus status) {
		User u = status.getOriginatingUser();
		String uname = u.getID();
		if (u.isAnonymous())
			uname += " [Anon]";
		String filename = status.getFileName();
		_logger.fine("SampleFileHandler.onFilePost: \n" +
				" user = " + uname +
				"\n filename = " + filename);
	}

	public void customPropertiesChanged(Properties properties) {
		// TODO Auto-generated method stub
		
	}

	public void init(Properties properties, PluginStartupCallback callback) {
		_logger.fine("SampleFileHandler init called");

		if (callback != null)
		{
			callback.pluginStarted(PLUGIN_NAME);
		}
		
		
	}

	public void terminate() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * If the room name contains the substring "Delete" then file deletions
	 * are prohibited EXCEPT if one of the current users in the room has
	 * an ID containing "wasadmin" or "7" - in which case the delete is
	 * permitted but marked MODIFY (for reason display)
	 */
	public void fileDelete(FilePostEvent event) {
		String roomName;
		String sessId = event.getSessionID();
		List currUsers = null;
		try {
			roomName = event.getSessionDetailUtil().getSessionName(sessId);
			if ((roomName != null) && (roomName.contains("Delete"))){
				_logger.fine("Restricted Delete room (name contains \"Delete\")");
				currUsers = event.getSessionDetailUtil().getCurrentUsers(sessId);
			}
		}
		catch (PluginException pe) {
			_logger.warning("PluginException trying to getRoomName/Users on sessionId = " + sessId);
		}
		if (currUsers != null)
		{
			boolean deletePermitted = false;
			Iterator it = currUsers.iterator();
			while (it.hasNext()){
				User u = (User)it.next();
				if (u != null){
					String userId = u.getID();
					if ((userId != null) &&
						 (userId.contains("wasadmin") || userId.contains("7")))
					{
						deletePermitted = true;
						break;
					}
				}
			}
			if (deletePermitted){
				event.setStatus(EventStatus.EVENT_OK_TYPE, "Delete given EVENT_OK (Reason display fine but not guaranteed)");
			} else {
				event.setStatus(EventStatus.EVENT_BLOCKED_TYPE,
						"DELETE BLOCKED -- user with ID containing \"wasadmin\" or \"7\" must be present to delete files (and deletion will give Event.OK)");
			}
		}
		else
		{
			event.setStatus(EventStatus.EVENT_MODIFIED_TYPE,
				"FileDeletion permitted - use a room with a name containing \"Delete\" to restrict deletions");
		}
		
		
	}

}
