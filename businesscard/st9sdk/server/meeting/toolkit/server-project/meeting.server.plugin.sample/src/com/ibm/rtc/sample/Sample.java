package com.ibm.rtc.sample;

import java.util.logging.Logger;

import com.ibm.rtc.polled.UpdateEvent;
import com.ibm.rtc.polled.ext.MessagingFilter;

/**
 * This is a sample server-side extention which shows how to hook
 * the case when a user leaves a meeting room, and 
 *
 */
public final class Sample implements MessagingFilter
{
	protected static final Logger logger = Logger.getLogger(Sample.class.getName());

	/* (non-Javadoc)
	 * @see com.ibm.rtc.polled.ext.MessagingFilter#isMessagingEnabled()
	 */
	public boolean isMessagingEnabled()
	{
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.rtc.polled.ext.MessagingFilter#filterMessagingEvent(com.ibm.rtc.polled.UpdateEvent)
	 */
	public boolean filterMessagingEvent(UpdateEvent updateEvent)
	{
		// always set this to false, since we're just using this as a hook
		// and we don't want to alter meeting server's behavior
		boolean filterMessagingEvent = false;

		logger.entering(this.getClass().getName(), "filterMessagingEvent", updateEvent);
		
		// lets hook the event that a user is leaving a meeting room
		// first check for UserMap, all other events will be ignored
		if(updateEvent.getContainerName().equals("UserMap"))
		{
			// next make sure this is special case, where the user's key and value are the same
			String key = updateEvent.getKey();
			String value = null;
			if(updateEvent.getValue()!=null)
			{
				value = updateEvent.getValue().toString();
			}
			if(value!=null && key.equals(value))
			{
				// next check that this is a removal all other operations will be ignored
				if(updateEvent.getOperation() == UpdateEvent.OP_REMOVE)
				{
					// USER LEAVING
					// this is to show the user's ID, room ID, and time of departure
					logger.info("user["+key+"] left the meeting room["+updateEvent.getSessionId()+"] at["+updateEvent.getTimeStamp()+"]");

					// BEGIN CUSTOM CODE
					/*
					 * NOTE:  If there is more work to do here, it should be done on a worker thread
					 * this thread should not be blocked if there is any latent I/O, etc.
					 */
					// END CUSTOM CODE
				}
				else if(updateEvent.getOperation() == UpdateEvent.OP_CHANGE)
				{
					// USER JOINING
					// this is to show the user's ID, room ID, and time of departure
					logger.info("user["+key+"] joined the meeting room["+updateEvent.getSessionId()+"] at["+updateEvent.getTimeStamp()+"]");

					// BEGIN CUSTOM CODE
					/*
					 * NOTE:  If there is more work to do here, it should be done on a worker thread
					 * this thread should not be blocked if there is any latent I/O, etc.
					 */
					// END CUSTOM CODE
				}
			}
		}		
		
		logger.exiting(this.getClass().getName(), "filterMessagingEvent", filterMessagingEvent);

		return filterMessagingEvent;
	}
}