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

import com.lotus.sametime.placessa.*;
import java.io.*;
import java.awt.*;
import java.util.Vector;
import javax.swing.Timer;
import java.awt.event.*;

/**
 * Monitors a place.
 */
class PlaceHandler extends PlaceAdapter
{
    //
    // Activity types.
    //
    public final static int CHAT_ACTIVITY         = 0x9106;
    public final static int AUDIO_ACTIVITY        = 0x9103;
    public final static int VIDEO_ACTIVITY        = 0x9104;
    public final static int SCREEN_SHARE_ACTIVITY = 0x9102;
    public final static int WHITEBOARD_ACTIVITY   = 0x9101;
	
    /**
     * Our managed activity.
     */
    private MyActivity m_ourActivity;
    
    /**
     * The output frame.
     */
    private LogFrame m_frame;
    
    /**
     * The place time.
     */
    private Time m_time;
    
    /**
     * Does the place has chat activity.
     */
    private Boolean m_hasChat = new Boolean(false);
    
    /**
     * Does the place has audio activity.
     */
    private Boolean m_hasAudio = new Boolean(false);
    
    /**
     * Does the place has video activity.
     */
    private Boolean m_hasVideo = new Boolean(false);
    
    /**
     * Does the place has screan share activity.
     */
    private Boolean m_hasShare = new Boolean(false);
    
    /**
     * Does the place has white board activity.
     */
    private Boolean m_hasWb = new Boolean(false);
    
    
    /**
     * Constructor.
     */
    public PlaceHandler(MyActivity activity, LogFrame frame)
    {
        m_frame = frame;
        m_ourActivity = activity;
        m_ourActivity.getPlace().addPlaceListener(this);
        
        m_time = new Time(m_frame, this);
        m_frame.add(this);
    }
    
    
    /**
     * The place was left. 
     */
    public void placeLeft(PlaceEvent event)
    {
        System.out.println("Place destroyed " + event.getPlace().getName());
        m_time.stop();
    }
    
    /**
     * An activity was added to the place.
     */
    public void activityAdded(PlaceEvent event)
    {
        int type = event.getActivityType();
        switch (type)
        {
            case CHAT_ACTIVITY:
                 m_hasChat = new Boolean(true);
                 break;
            case AUDIO_ACTIVITY:
                 m_hasAudio = new Boolean(true);
                 break;
            case VIDEO_ACTIVITY:
                 m_hasVideo = new Boolean(true);
                 break;
            case SCREEN_SHARE_ACTIVITY:
                 m_hasShare = new Boolean(true);
                 break;
            case WHITEBOARD_ACTIVITY:
                 m_hasWb = new Boolean(true);
                 break;
        }
        m_frame.refreshAll();
    }
    
    //
    // Accessors.
    //
    
    /**
     * Get the place name.
     */
    public String getName()
    {
        return m_ourActivity.getPlace().getName();
    }
    
    /**
     * Get the time object of the place.
     */
    public Time getTime()
    {
        return m_time;
    }
    
    /**
     * @return Whether the place has chat activity.
     */
    public Boolean hasChat()
    {
        return m_hasChat;
    }

    /**
     * @return Whether the place has video activity.
     */
    public Boolean hasVideo()
    {
        return m_hasVideo;
    }    
    
    /**
     * @return Whether the place has audio activity.
     */
    public Boolean hasAudio()
    {
        return m_hasAudio;
    }
    
    /**
     * @return Whether the place has screan share activity.
     */
    public Boolean hasShare()
    {
        return m_hasShare;
    }
    
    /**
     * @return Whether the place has white board activity.
     */
    public Boolean hasWhiteboard()
    {
        return m_hasWb;
    }
}

/**
 * A wrapper on top of a timer. Formats the time in seconds to HH:MM:SS.
 */    
class Time implements ActionListener
{
    /**
     * The current time in seconds.
     */
    long m_timeInSeconds = 0;
    
    /**
     * The timer.
     */
    Timer m_timer;
    
    /**
     * The output frame.
     */
    LogFrame m_frame;
    
    /**
     * The place handler.
     */
    PlaceHandler m_place;
    
    /**
     * Constructor.
     */
    public Time(LogFrame frame, PlaceHandler handler)
    {
        m_frame = frame;
        m_place = handler;
        m_timer = new Timer(1000, this);
        m_timer.start();   
    }
    
    /**
     * Stop the timer.
     */
    public void stop()
    {
        m_timer.stop();
    }
    
    /**
     * Timer clicked.
     */
    public void actionPerformed(ActionEvent event)
    {
        m_timeInSeconds++;
        m_frame.refreshTime(m_place);
    }
    
    /**
     * Format the time to HH:MM:SS.
     */
    public String toString()
    {
        long seconds = m_timeInSeconds;
        long minutes = seconds/60;
        seconds = seconds%60;
        long hours = minutes/60;
        minutes = minutes%60;
        
        String hoursAsString = "" + hours;
        hoursAsString.trim();
        if (hoursAsString.length() == 1)
        {
            hoursAsString = "0" + hoursAsString;
        }
        
        String minutesAsString = "" + minutes;
        minutesAsString.trim();
        if (minutesAsString.length() == 1)
        {
            minutesAsString = "0" + minutesAsString;
        }
       
        String secondsAsString = "" + seconds;
        secondsAsString.trim();
        if (secondsAsString.length() == 1)
        {
            secondsAsString = "0" + secondsAsString;
        }
        
        return hoursAsString + ":" + minutesAsString + ":" + 
                 secondsAsString;
    }
}



