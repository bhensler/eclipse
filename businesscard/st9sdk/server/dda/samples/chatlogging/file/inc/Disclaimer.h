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

#ifndef __DISCLAIMER_H__
#define __DISCLAIMER_H__

#include <ChatSessionTable.h>

#define DISCLAIMER (Disclaimer::instance())

// *****************************************************************
// The Disclaimer class is responsible for sending disclaimer message
// according to rich/plain text recipients mode.
//
// Disclaimer messages are treated differently in 2-way chat and
// in N-way chat.
// 
// For 2-way chat:
// In order to support rich text disclaimer server message, disclaimer
// messages should be sent from stDdaClSessionDataMsgWithSrvMsg  
// instead from stDdaClJoiningSessionWithSrvMsg since in the dataMsg
// function is the first time we know of the rich text capabilities
// of the client.
// 
// For N-way chat: 
// The treatment handling of disclaimer message for NWay chat is in
// stDdaClJoiningSessionWithSrvMsg, once a user has joined the place,
// a plain text disclaimer message then sent.
// 
// Note: Plain text disclaimer message for both 2-way and N-way chat
// could be implemented from stDdaClJoiningSessionWithSrvMsg. 
// This is not the way it is implementing in this sample.
// *****************************************************************
class Disclaimer
{
public:
	Disclaimer(void);
	~Disclaimer(void);

public:// methods
  int send( /*in*/  ChatSessionEntry* entry,
    /*in*/  const char*      msg,
    /*in*/  unsigned long    msgLen,
    /*out*/ StDdaClSrvMsg**  srvMessages,
    /*out*/ unsigned long*   srvMessagesLen);

public://Single Tone
	static Disclaimer*   m_ptr;
	static Disclaimer& instance()
	{
		if(!m_ptr)
			m_ptr=new Disclaimer();
		return *m_ptr;
	}
private:
  int send2Way(/*in*/ ChatSessionEntry* entry,
    /*in*/  const char*      msg,
    /*in*/  unsigned long    msgLen,
    /*out*/ StDdaClSrvMsg**  srvMessages,
    /*out*/ unsigned long*   srvMessagesLen);

};

#endif //__DISCLAIMER_H__
