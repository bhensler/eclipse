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

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.lotus.sametime.core.types.*;
import com.lotus.sametime.community.*;
import com.lotus.sametime.core.comparch.*;
import com.lotus.sametime.token.*;
import com.lotus.sametime.im.*;
import com.lotus.sametime.core.constants.*;

/**
 * Handles a single IM request to an offline user. Whenever the offline user
 * logs on, we do a light login as the sender, send an IM in his name and
 * immediately logout to allow the reply to be received by the real sender.
 *
 * @author Assaf Azaria, July 2001.
 */
public class UsersHandler implements LoginListener, TokenServiceListener2
{
    /**
     * The user session.
     */
    private STSession m_session;

    /**
     * The light login service.
     */
    private LightLoginService m_loginService;

    /**
     * The token service.
     */
    private SATokenService m_tokenService;

    /**
     * The im service.
     */
    InstantMessagingService m_imService;

    /**
     * The message sender.
     */
    private STUser m_sender;

    /**
     * The message offline receiver.
     */
    private STUser m_receiver;

    /**
     * The message.
     */
    private String m_message;

    /**
     * The name of the server we are working with.
     */
    String m_serverName;

    /**
     * The server app login.
     */
    ServerAppService m_mainLogin;

    /**
     * Construct a new user request handler.
     *
     * @param saSession  The session of the server application.
     * @param serverName The name of the server to login to.
     * @param sender     The sender of the message.
     * @param receiver   The receiver of the message.
     * @param message    The actual message.
     */
    public UsersHandler(STSession saSession, String serverName, STUser sender,
                        STUser receiver, String message)
    {
        m_sender   = sender;
        m_receiver = receiver;
        m_message  = message;
        m_serverName = serverName;


        // We need the sender's token for login.
        m_tokenService
            = (SATokenService)saSession.getCompApi(SATokenService.COMP_NAME);
        m_tokenService.addTokenServiceListener(this);

        m_mainLogin
            = (ServerAppService)saSession.getCompApi(ServerAppService.COMP_NAME);
    }


    /**
     * The receiver has gone online. Get the sender token, login and
     * send the waiting message.
     */
    void receiverOnline()
    {
        // First, create a session for the user.
        try
        {
            m_session = new STSession("OfflineMessageUser" + this);
            new STBase(m_session);
            new ImComp(m_session);
            m_session.start();
        }
        catch(DuplicateObjectException e)
        {
            e.printStackTrace();
            return;
        }

        m_loginService
            = (LightLoginService)m_session.getCompApi(LightLoginService.COMP_NAME);

        m_imService
            = (InstantMessagingService)m_session.getCompApi(InstantMessagingService.COMP_NAME);

        // Now, try to get his token.
        m_tokenService.generateTokens(m_sender);
    }

    //
    // Token Service Listener.
    //

    /**
     * ome or more login tokens were generated successfully.
     *
     * @param            event The event object.
     * @see              TokenEvent#getToken
     */
    public void tokensGenerated(TokenEvent event)
    {
        // Now, we login as the sender.
        m_loginService.setLoginType(STUserInstance.LT_LIGHT_CLIENT_USER);
        m_loginService.addLoginListener(this);

        // A light client has to provide its ip explicitly, giving null as ip
        // prevents the server from disconnecting the real sender.
        Token[] tokens = event.getTokens();
        try {
        	// we login using the first token
			m_loginService.loginByTokens(tokens[0].getLoginName(),
			                            tokens, m_mainLogin,
			                            InetAddress.getByAddress(new byte[]{0, 0, 0, 0}));		
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    }

    /**
     * A generate tokens  request failed.
     *
     * @param            event The event object.
     * @see              TokenEvent#getReason
     */
    public void generateTokensFailed(TokenEvent event)
    {
        System.out.println("SA: Generate tokens failed " + event.getReason());
        cleanUp();
    }

    /**
     * Token Service is available.
     */
    public void serviceAvailable(TokenEvent event)
    {
        System.out.println("SA: TokenService available");
    }

     //
     // Login Listener.
     //
     /**
     * Indicates that we were successfully logged in to the Sametime community.
     *
     * @param event The event object.
     * @see LoginEvent#getCommunity
     */
    public void loggedIn(LoginEvent event)
    {
        System.out.println("SA: Handler LoggedIn");

        // Finally we can send the message.
        Im im = m_imService.createIm(m_receiver, EncLevel.ENC_LEVEL_ALL, 1);
        im.addImListener(new ImHandler());
		
		// Wait a while to let the receiver initialize. 
		try {
			Thread.currentThread().sleep(5000);
		}catch (InterruptedException e){
		};
        im.open();
    }

    /**
     * Indicates that we were successfully logged out of the Sametime community,
     * or a login request was refused.
     *
     * @param event The event object.
     * @see LoginEvent#getReason
     * @see LoginEvent#getCommunity
     */
    public void loggedOut(LoginEvent event)
    {
        int reason = event.getReason();
        System.out.println("SA:Handler LoggedOut" + reason);

        if (STError.VpkFailed(reason))
        {
            cleanUp();
        }
    }

    /**
     * Im events handler.
     */
    class ImHandler extends ImAdapter
    {
        public void imOpened(ImEvent event)
        {
           // Send the message and leave asap.
			try{
				Thread.currentThread().sleep(2000);
			}catch(InterruptedException e) {}
			
            event.getIm().sendText(true, m_message);
			
			// wait for a while to let the client wake up.
			try{
				Thread.currentThread().sleep(5000);
			}catch(InterruptedException e) {}
			m_loginService.logout();
            cleanUp();
        }

        public void openImFailed(ImEvent event)
        {
            System.out.println("SA HANDLER: Couldn't open IM " +
                                event.getReason());
            cleanUp();
        }
    }

    /**
     * Finished processing, clean up.
     */
    private void cleanUp()
    {
        m_session.stop();
        m_session.unloadSession();
    }


	public void generateTokenFailed(TokenEvent event) {
		// TODO Auto-generated method stub
		
	}


	public void tokenGenerated(TokenEvent event) {
		// TODO Auto-generated method stub
		
	}
}
