/*
* Licensed Materials - Property of IBM
*
* L-MCOS-96LQPJ
*
* (C) Copyright IBM Corp. 2002, 2013. 
* All rights reserved.
*
* US Government Users Restricted Rights- Use, duplication or 
* disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
*/

#include <stDdaClCodes.h>
#include <ChatSessionTable.h>

#include <stdio.h>
#include <string.h>
#include <stDdaClApi.h>
#include <ChatResource.h>
#include <ParseDataMsg.h>
#include <Disclaimer.h>
#include <Image.h>
#include <RichTextMgr.h>
#include <globalLabels.h>


// You can use the dbg variable to enable or disable debugging 
// information. If dbg is equal to 1, CHAT_RSC.print() prints the debug
// message to the debug file. Otherwise, no debug information is printed
int dbg = CHAT_RSC.getDebugFlagValue("BB_CL_TRACE");

char *globalRetMsg = 0;
StDdaRetVal *globalRetCode = 0;

ChatSessionTable table;

// *****************************************************************
// DESCRIPTION: stDdaClInit
// This function initializes the chat logging library and
// verifies that it can handle the specified version of the SPI. 
// The calling application places the SPI version number in the 
// prVersion parameter. 
// The chat logging library adjusts its SPI version number to the 
// version of calling application.
// The return code indicates whether or not the initialization 
// succeeded.
// *****************************************************************
int ST_DDA_API
stDdaClInit( /*in/out*/ int* prVersion,
            /*in*/     int  initializedOutside,
            /*out*/	int* initializedInside,
            /*out*/    char* dirType,
            /*out*/    char* libVersion,
            /*out*/    StDdaRetVal* appRetCode,
            /*out*/    char* appRetMsg)
{
  if (dirType && libVersion) {
    strcpy(dirType, ST_DDA_API_FILE_TYPE);
    strcpy(libVersion, BUILD_NUMBER);
  }

  // Init debug mechanism. Inside the ChatSessionEntry::createPath
  // function, the SPI creates the path where all files for each 
  // session are to be created.
  ChatSessionEntry::createPath();

  globalRetCode = appRetCode;
  globalRetMsg = appRetMsg;
  *globalRetCode = ST_DDA_OK;

  if (*prVersion < ST_DDA_CL_LIB_VERSION) {
    CHAT_RSC.print(dbg,"The recived version - %d not suppoted, black support only version %d and up",
      *prVersion,ST_DDA_CL_LIB_VERSION);
    *globalRetCode = ST_DDA_ERROR;

    sprintf(globalRetMsg, "Version  - %d is not supported",
      *prVersion);
    return ST_DDA_API_VERSION_MISMATCH;
  }

  *prVersion = ST_DDA_CL_LIB_VERSION;

  //If the initialization successfully completes, use the 
  // initializedInside flag for the value that describes exactly what
  // actions were prepared during the initialization 
  //(for example, database initialization).
  (*initializedInside) |= initializedOutside;
  CHAT_RSC.print(dbg,"Initialization complete");
  return ST_DDA_API_OK;
} // end stDdaClInit

// *****************************************************************
// DESCRIPTION: stDdaClTerminate
// This function is responsible for closing all opened 
// sessions. It terminates any platform interface initialized by the 
// library. It uses a copy of the value of the initializedInside 
// parameter that was saved when stDdaClInit was called.
// Generally, the server application calls this function when it fails.
// *****************************************************************
void ST_DDA_API
stDdaClTerminate()
{
  CHAT_RSC.print(dbg,"stDdaClTerminate: processing...");

  // Inside closeAllChatSession, all opened sessions are closed.
  table.closeAllChatSession();

  CHAT_RSC.print(dbg,"stDdaClTerminate: end");
} // end stDdaClTerminate



// *****************************************************************
// DESCRIPTION: stDdaClTimerEvent
// The server application usually calls the stDdaClTimer function at
// regular intervals. Inside the stDdaClTimerEvent function, the SPI 
// has a chance to do some periodic actions, such as refreshing the 
// database or memory cache.
// *****************************************************************
void ST_DDA_API
stDdaClTimerEvent(void)
{
  CHAT_RSC.print(dbg,"stDdaClTimerEvent: do nothing...");
} // end stDdaClTimerEvent


// *****************************************************************
// DESCRIPTION: stDdaClSessionStarted
// This function is called when a new chat session is started in an 
// instant message or a Place (a meeting or n-way chat). Every time a
// new session is started, the server application can call one of the
// functions: stDdaClSessionStarted or stDdaClSessionStartedByOrgName.
// *****************************************************************
int ST_DDA_API
stDdaClSessionStarted(/*in*/ const char* sessionId)
{
  CHAT_RSC.print(dbg,"stDdaClSessionStarted: session <%s> processing..."
    ,sessionId);
  return stDdaClSessionStartedByOrgName(sessionId,"");
} // end stDdaClSessionStarted


// *****************************************************************
// DESCRIPTION: stDdaClSessionStartedByOrgName
// This function is called when a new chat session is started in an  instant message or a
// Place. // The server application calls the stDdaClSessionStartedByOrgName 
// function every time a new session is started. 
// This function is supported only in SPI version 12 and higher.
// *****************************************************************
int ST_DDA_API
stDdaClSessionStartedByOrgName(/*in*/ const char* sessionId,
                               /*in*/ const char* organization)
{
  int rc = ST_DDA_API_OK;
  CHAT_RSC.print(dbg,"stDdaClSessionStartedByOrgName: session <%s> processing..."
    ,sessionId);

  // The SPI then adds a new session to the chatSession table, opens
  // a new file, and writes the appropriate message. If an error 
  // occurs during this operation, the SPI prints a debug message and
  // returns the error code to the server application.
  rc=table.openChatSession(sessionId,organization);

  if(rc==ST_DDA_CL_DB_ERROR)
  {
    *globalRetCode=ST_DDA_FATAL;
    sprintf(globalRetMsg, "BB error - Can't open DB");
    CHAT_RSC.print(dbg,"stDdaClSessionStartedByOrgName: Can't open DB ");
  }
  else if(rc==ST_DDA_CL_SESSION_ALREADY_EXISTS)
  {
    *globalRetCode=ST_DDA_ERROR;
    sprintf(globalRetMsg, "BB error - session %s already exist",sessionId);
    CHAT_RSC.print(dbg,"stDdaClSessionStartedByOrgName: The session  - %s for organization %s is already exist",
      sessionId,organization);
  }

  CHAT_RSC.print(dbg,"stDdaClSessionStartedByOrgName: ended with error <0x%x>",rc);

  return rc;
} // end stDdaClSessionStartedByOrgName



// *****************************************************************
// DESCRIPTION: stDdaClSessionEnded
// The server application calls the stDdaClSessionEnded function every
// time an open session is ended.
// *****************************************************************
int ST_DDA_API stDdaClSessionEnded(const char* sessionId)
{
  CHAT_RSC.print(dbg,"stDdaClSessionEnded: session <%s> processing..."
    ,sessionId);

  // The SPI inserts the appropriate message to that session file and
  // removes the session entry from the chatSession table. If an error
  // occurs during this operation, the SPI prints a debug message and
  // returns the error code to the server application.
  int rc = table.closeChatSession(sessionId);
  if(rc > 0)
  {
    *globalRetCode=ST_DDA_ERROR;
    sprintf(globalRetMsg, "BB close session <%s>  error <0x%x>"
      ,sessionId,rc);
  }

  CHAT_RSC.print(dbg,"stDdaClSessionEnded: ended with error <0x%x>"
    ,rc);
  return rc;
} // end stDdaClSessionEnded


// *****************************************************************
// DESCRIPTION: stDdaClJoiningSession
// When a new entity joins an open session, the server application calls
// the stDdaClJoiningSession function 
// (stDdaClJoiningSessionWithSrvMsg in Sametime 8.0).
// *****************************************************************
int ST_DDA_API stDdaClJoiningSession(/*in*/ const char* sessionId, 
                                     /*in*/ const StDdaClEntity *entity,
                                     /*in*/ const StDdaClEntity *scope)
{
  return stDdaClJoiningSessionWithSrvMsg(sessionId, 
    entity, scope, 0, 0);
} // end stDdaClJoiningSession


// *****************************************************************
// DESCRIPTION: stDdaClLeavingSession
// When an entity leaves an open session, the server application calls
// the stDdaClLeavingSession function
// (stDdaClLeavingSessionWithSrvMsg in Sametime 8.0).
// *****************************************************************
int ST_DDA_API stDdaClLeavingSession(/*in*/ const char* sessionId, 
                                     /*in*/ const StDdaClEntity *entity)
{
  return stDdaClLeavingSessionWithSrvMsg(sessionId, entity, 0, 0);
} // end stDdaClUserLeftSession



// *****************************************************************
// DESCRIPTION: stDdaClSessionMsg
// This function is responsible for writing the text
// message that comes from the server application to the appropriate
// session file. (stDdaClSessionMsgWithSrvMsg in Sametime 8.0).
// *****************************************************************
int ST_DDA_API
stDdaClSessionMsg(/*in*/ const char* sessionId, 
                  /*in*/ const StDdaClEntity *sender, 
                  /*in*/ unsigned long msgLen, 
                  /*in*/ const char *msg, 
                  /*in*/ const StDdaClEntity *receiver)
{
  return stDdaClSessionMsgWithSrvMsg(sessionId, sender, msgLen, 
    msg, receiver, 0, 0);
} // end stDdaClSessionMsg


// *****************************************************************
// DESCRIPTION: stDdaClSessionDataMsg
// This function is responsible for writing the
// data message, which comes from the server application, to the 
// appropriate data file.
// (stDdaClSessionDataMsgWithSrvMsg in Sametime 8.0). 
// *****************************************************************
int ST_DDA_API
stDdaClSessionDataMsg(/*in*/ const char* sessionId, 
                      /*in*/ const StDdaClEntity *sender, 
                      /*in*/ unsigned long dataType, 
                      /*in*/ unsigned long dataSubType, 
                      /*in*/ unsigned long msgLen, 
                      /*in*/ const char *msg, 
                      /*in*/ const StDdaClEntity *receiver)
{
  return stDdaClSessionDataMsgWithSrvMsg(sessionId, sender, dataType,
    dataSubType, msgLen, msg, receiver, 0, 0);
} // end stDdaClSessionDataMsg


// *****************************************************************
// DESCRIPTION: stDdaClSessionPolicycheckFunc
// This function checks the chat between entity and toEntity is allowed.
// Using this function you can forbid the chat between two users in a
// "one on one" chat.
// Note: this functions does not work for n-way chats, 
// instead use stDdaClJoiningSession or stDdaClJoiningSessionWithSrvMsg.
// *****************************************************************
int ST_DDA_API
stDdaClSessionPolicycheckFunc(/*in*/ const StDdaClEntity *entity,
                              /*in*/ const StDdaClEntity *toEntity,
                              /*out*/ StDdaClSrvMsg** srvMessages,
                              /*out*/ unsigned long* srvMessagesLen)
{
  CHAT_RSC.print(dbg,"stDdaClSessionPolicycheckFunc: processing...");

  string from_user;
  string to_user;

  from_user = entity->id;
  to_user   = toEntity->id;

  if(CHAT_RSC.isAttempToBanChats()) 
  {
    if((from_user == CHAT_RSC.getBanChatFrom() 
      && to_user == CHAT_RSC.getBanChatTo()) 
      || (to_user == CHAT_RSC.getBanChatFrom() 
      && from_user == CHAT_RSC.getBanChatTo())) 
    {
      // the chat should be banned
      // TODO use error code - user is invalid or not trusted 
      //#define VPK_INVALID_USER                   0x80000011
      CHAT_RSC.print(dbg,"It is forbidden to open chat between %s and %s", 
        from_user.c_str(), to_user.c_str());

      if(strlen(CHAT_RSC.getBanChatWarning()) > 0) 
      {
        *srvMessagesLen = (unsigned long)1;
        *srvMessages = new StDdaClSrvMsg();
        (*srvMessages)->srvMsgType = ST_DDA_CL_TEXT_MSG;
        (*srvMessages)->srvMsgDestination = ST_DDA_CL_SENDER;
        int msgLen = strlen(CHAT_RSC.getBanChatWarning());
        char* buf = new char[msgLen + 1];
        memset(buf, 0, msgLen + 1);
        strncpy(buf, CHAT_RSC.getBanChatWarning(), msgLen);
        (*srvMessages)->msg = buf;
      }        
      return ST_DDA_SESSION_CREATION_DECLINED;
    }
  }

  CHAT_RSC.print(dbg,"stDdaClSessionPolicycheckFunc: ended ok");
  return ST_DDA_OK;
} // end stDdaClSessionPolicycheckFunc


// *****************************************************************
// DESCRIPTION: stDdaClJoiningSessionWithSrvMsg
// *****************************************************************
int ST_DDA_API
stDdaClJoiningSessionWithSrvMsg(/*in*/ const char* sessionId, 
                                /*in*/ const StDdaClEntity *entity,
                                /*in*/ const StDdaClEntity *scope,
                                /*out*/ StDdaClSrvMsg** srvMessages,
                                /*out*/ unsigned long* srvMessagesLen)
{
  int rc = ST_DDA_API_OK;
  ChatSessionEntry* entry;
  CHAT_RSC.print(dbg,"stDdaClJoiningSessionWithSrvMsg: session <%s> processing...",sessionId);

  // The SPI tries to find a session handler in the chatSession table, 
  // based on the sessionId. If no error occurs, the appropriate 
  // message is written to the session file.
  entry=table.findChatSession(sessionId);

  if(entry)
  {
    entry->determineSessionType(scope);
    // The addUser function writes the proper message to 
    // the session file.
    rc=entry->addUser(entity,scope);

    if (rc == ST_DDA_CL_DB_ERROR) {

      *globalRetCode=ST_DDA_FATAL;
      sprintf(globalRetMsg, "BB error - Can't open DB");
      CHAT_RSC.print(dbg,"stDdaClJoiningSessionWithSrvMsg: Can't open DB ");    

    } else if(rc == ST_DDA_SESSION_CREATION_DECLINED) {
      // If the return code is ST_DDA_SESSION_CREATION_DECLINED
      // the SPI creates an appropriate session banning message
      // and returns the warning message and the appropriate 
      // error code to the application level.

      // TODO to send error msg to disable chat window
      // user not trusted 
      // #define VPK_INVALID_USER                   0x80000011
      CHAT_RSC.print(dbg,"stDdaClJoiningSessionWithSrvMsg: session creation banned ");
      if(strlen(CHAT_RSC.getBanChatWarning()) > 0) {

        *srvMessagesLen = (unsigned long)1;
        *srvMessages = new StDdaClSrvMsg();
        (*srvMessages)->srvMsgType = ST_DDA_CL_TEXT_MSG;

        // In case of chat banning the chat 
        // initializator is a RECEIVER
        (*srvMessages)->srvMsgDestination = ST_DDA_CL_RECEIVER;
        int msgLen = strlen(CHAT_RSC.getBanChatWarning());
        char* buf = new char[msgLen + 1];
        memset(buf, 0, msgLen + 1);
        strncpy(buf, CHAT_RSC.getBanChatWarning(), msgLen);
        (*srvMessages)->msg = buf;
      }

	} else if (CHAT_RSC.isAttempToSendChatDisclaimer() && 
	  (entry->getSessionType() == ChatSessionEntry::CS_NWAY || entry->getSessionType() == ChatSessionEntry::CS_ANNOUNCEMENT)&&
      (entity->type == ST_DDA_CL_USER 
      || entity->type == ST_DDA_CL_EXTERNAL_USER)){
			if (entry->isDisclaimerSent() && entry->getSessionType() == ChatSessionEntry::CS_ANNOUNCEMENT){
				return rc;
			}
			rc = DISCLAIMER.send(entry, NULL, 0, srvMessages, srvMessagesLen);
			if (entry->getSessionType() == ChatSessionEntry::CS_ANNOUNCEMENT){
				entry->disclaimerSent();
			}
			if (rc == ST_DDA_MSG_CHANGED) {
				// Log disclaimer sent to a joining user in Nway chat.
				int _rc = entry->logDisclaimer(entity, NULL);
				if (_rc != ST_DDA_API_OK) {
				rc = _rc;
				}
			}
	}
  } 
  else 
  {
    *globalRetCode=ST_DDA_ERROR;
    sprintf(globalRetMsg, "BB error - session %s  doesn't exist",sessionId);
    CHAT_RSC.print(dbg,"stDdaClJoiningSessionWithSrvMsg: Session - %s doesn't exist ",sessionId);
    return ST_DDA_CL_SESSION_DOES_NOT_EXIST;
  }

  CHAT_RSC.print(dbg,"stDdaClJoiningSessionWithSrvMsg: ended with return code <0x%x>",rc);
  return rc;
} // end stDdaClJoiningSessionWithSrvMsg

// *****************************************************************
// DESCRIPTION: stDdaClLeavingSessionWithSrvMsg
// *****************************************************************
int ST_DDA_API
stDdaClLeavingSessionWithSrvMsg(/*in*/ const char* sessionId, 
                                /*in*/ const StDdaClEntity *entity,
                                /*out*/ StDdaClSrvMsg** srvMessages,
                                /*out*/ unsigned long* srvMessagesLen)
{
  int rc = ST_DDA_API_OK;
  ChatSessionEntry* entry;
  CHAT_RSC.print(dbg,"stDdaClLeavingSessionWithSrvMsg: session <%s> processing..." ,sessionId);

  // The SPI tries to find the session handler in the chatSession
  // table, based on its sessionId. If no error occurs, the
  // appropriate message is written to the session file.
  entry=table.findChatSession(sessionId);
  if(entry)
  {
    // The removeUser function writes the appropriate message to the
    // session file.
    rc=entry->removeUser(entity);
    if(rc==ST_DDA_CL_DB_ERROR)
    {
      *globalRetCode=ST_DDA_FATAL;
      sprintf(globalRetMsg, "BB error - Can't open DB");
      CHAT_RSC.print(dbg,"stDdaClLeavingSessionWithSrvMsg: Can't open DB ");
    }		
  }
  else
  {
    // Otherwise, the appropriate debug message is printed in the debug file 
    // and an error code is returned to the server application.
    *globalRetCode=ST_DDA_ERROR;
    sprintf(globalRetMsg, "BB error - session %s  doesn't exist",sessionId);
    CHAT_RSC.print(dbg,"stDdaClLeavingSessionWithSrvMsg: Session - %s desn't exist ",sessionId);
    return ST_DDA_CL_SESSION_DOES_NOT_EXIST;
  }

  CHAT_RSC.print(dbg,"stDdaClLeavingSessionWithSrvMsg: ended with error <0x%x>",rc);
  return rc;
} // end stDdaClLeavingSessionWithSrvMsg

// *****************************************************************
// DESCRIPTION: stDdaClSessionMsgWithSrvMsg
// *****************************************************************
int ST_DDA_API
stDdaClSessionMsgWithSrvMsg(/*in*/ const char* sessionId, 
                            /*in*/ const StDdaClEntity *sender, 
                            /*in*/ unsigned long msgLen, 
                            /*in*/ const char* msg, 
                            /*in*/ const StDdaClEntity *receiver,
                            /*out*/ StDdaClSrvMsg** srvMessages,
                            /*out*/ unsigned long* srvMessagesLen)
{
  int rc = ST_DDA_API_OK;
  ChatSessionEntry* entry;

  CHAT_RSC.print(dbg,"stDdaClSessionMsgWithSrvMsg: session <%s> processing..." ,sessionId);
  // The SPI tries to find the session handler in the chatSession
  // table, based on its sessionId. If no error occurs, the
  // appropriate message is written to the session file
  entry=table.findChatSession(sessionId);

  if(entry)
  {
    // The message function writes the proper message to the session
    // file.
    rc=entry->message(RT_MGR.isRichTextEnabled(entry),sender,msgLen,
      msg,receiver, srvMessages, srvMessagesLen);


    if(rc==ST_DDA_CL_DB_ERROR)
    {
      *globalRetCode=ST_DDA_FATAL;
      sprintf(globalRetMsg, "BB error - Can't open DB");
      CHAT_RSC.print(dbg,"stDdaClSessionMsgWithSrvMsg: Can't open DB ");
    }
    else if(rc==ST_DDA_MSG_TOO_LONG)
    {
      *globalRetCode=ST_DDA_ERROR;
      sprintf(globalRetMsg, "BB error - Message too long!");
      CHAT_RSC.print(dbg,"stDdaClSessionMsgWithSrvMsg: Message too long!");
    }
    else if (rc == ST_DDA_OK || rc == ST_DDA_MSG_CHANGED)
    { 
      // Send a disclaimer message if this is the first text message
      // This type of disclaimer is only sent in 2-way chat
      if (entry->getSessionType() == ChatSessionEntry::CS_2WAY) 
      {
        int rcCode = ST_DDA_API_OK;
        if (rc == ST_DDA_MSG_CHANGED)
        {
          rcCode = DISCLAIMER.send(entry, NULL, 0, srvMessages, srvMessagesLen);
        }
        else 
        {
          // Add the original text message (msg) to the disclaimer messages.
          rcCode = DISCLAIMER.send(entry, msg, msgLen, srvMessages, srvMessagesLen);
        }
        if (rcCode == ST_DDA_MSG_CHANGED) 
        {
          //log send disclaimer message event for 2-way chat
          int _rc = entry->logDisclaimer(sender, receiver);
          if (_rc != ST_DDA_API_OK) 
          {
            rc = _rc;
          }
        }
        if (rcCode != ST_DDA_OK) {
          rc = rcCode;
        }
      }
    }
  }
  else
  {
    // Otherwise, the appropriate debug message is printed in the
    // trace file and an error code is returned to the 
    // server application.
    *globalRetCode=ST_DDA_ERROR;
    sprintf(globalRetMsg, "BB error - %s desn't exist ",sessionId);
    CHAT_RSC.print(dbg,"stDdaClSessionMsgWithSrvMsg: Session - %s desn't exist ",sessionId);
    return ST_DDA_CL_SESSION_DOES_NOT_EXIST;
  }

  CHAT_RSC.print(dbg,"stDdaClSessionMsgWithSrvMsg: ended with error <0x%x>",rc);
  return rc;
} // end stDdaClSessionMsgWithSrvMsg

// *****************************************************************
// DESCRIPTION: stDdaClSessionDataMsgWithSrvMsg
// *****************************************************************
int ST_DDA_API
stDdaClSessionDataMsgWithSrvMsg(/*in*/ const char* sessionId, 
                                /*in*/ const StDdaClEntity *sender, 
                                /*in*/ unsigned long dataType, 
                                /*in*/ unsigned long dataSubType, 
                                /*in*/ unsigned long msgLen, 
                                /*in*/ const char *msg, 
                                /*in*/ const StDdaClEntity *receiver,
                                /*out*/ StDdaClSrvMsg** srvMessages,
                                /*out*/ unsigned long* srvMessagesLen)
{
  int rc = ST_DDA_API_OK;

  CHAT_RSC.print(dbg,"stDdaClSessionDataMsgWithSrvMsg: type <%d> subtype <%d> ", dataType, dataSubType);
  ChatSessionEntry* entry;
  CHAT_RSC.print(dbg,"stDdaClSessionMsgWithSrvMsg: session <%s> processing...",sessionId);
  entry = table.findChatSession(sessionId);

  if (!entry)
  {
    *globalRetCode=ST_DDA_ERROR;
    sprintf(globalRetMsg, "BB error - session %s  doesn't exist",sessionId);
    CHAT_RSC.print(dbg,"stDdaClSessionDataMsgWithSrvMsg: Session - %s desn't exist ",sessionId);
    return ST_DDA_CL_SESSION_DOES_NOT_EXIST;
  }

  CHAT_RSC.print(dbg,"stDdaClSessionDataMsgWithSrvMsg: type <%d> subtype <%d> ", dataType, dataSubType);

  if(PARSE_MSG.isImageMsg(dataType, dataSubType, msgLen, msg)) {
    // The current implementation saves only the image data messages
    rc = IMAGE.save(entry,sessionId, sender, dataType, dataSubType,
      msgLen, msg, receiver);
  }
  else if(PARSE_MSG.isRichTextMsg(dataType, dataSubType, msgLen, msg)) {
    // The first messages that are passed on the chat session indicate
    // that rich text is used. 
    entry->incRichTextCounter();
  }

  CHAT_RSC.print(dbg,"stDdaClSessionDataMsgWithSrvMsg: ended with return code <0x%x>",rc);
  return rc;
} // end stDdaClSessionDataMsgWithSrvMsg

// *****************************************************************
// DESCRIPTION: stDdaClCleanSrvMsgs
// This function is called by the Chat Logging or
// Places Server Applications when it is necessary to delete the server
// messages. This function MUST release the memory of the previously
// allocated server messages objects.
// *****************************************************************
int ST_DDA_API
stDdaClCleanSrvMsgs(StDdaClSrvMsg* srvMessages,
                    unsigned long srvMessagesLen)
{
  int len = (int)srvMessagesLen;
  CHAT_RSC.print(dbg, "stDdaClCleanSrvMsgs: processing %d server messages" , len);

  if(srvMessages == NULL || srvMessagesLen == 0) {
    CHAT_RSC.print(dbg, "stDdaClCleanSrvMsgs: messages list is NULL or indicator is 0");
    return ST_DDA_API_OK;
  }

  for(int i = 0; i < srvMessagesLen; i++) 
  {
    StDdaClSrvMsg* msg = srvMessages + i;
    if(msg->srvMsgType == ST_DDA_CL_TEXT_MSG) 
    {
      if(msg->msg != NULL) 
      {
        delete [] msg->msg;
        msg->msg = NULL;
      }
    }
  }
  delete[] srvMessages;

  CHAT_RSC.print(dbg, "stDdaClCleanSrvMsgs: server messages deleted");
  return ST_DDA_API_OK;
} // end stDdaClCleanSrvMsgs