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

#include <Disclaimer.h>
#include <RichTextMgr.h>
#include <ChatResource.h>

int dbgFlag = CHAT_RSC.getDebugFlagValue("BB_CL_TRACE");
Disclaimer* Disclaimer::m_ptr = 0;

// *****************************************************************
// The disclaimer message that notifies the users that the chat is logged,
// should be displayed as the first message in the chat window. 
// Therefore, the chat logging implementation should send it as a server 
// message along with the first message that is sent to the participants, 
// after chat capabilities are determined.
// *****************************************************************

// *****************************************************************
// DESCRIPTION: Disclaimer::Disclaimer
// *****************************************************************
Disclaimer::Disclaimer(void)
{
}

// *****************************************************************
// DESCRIPTION: Disclaimer::~Disclaimer
// *****************************************************************
Disclaimer::~Disclaimer(void)
{
}


// *****************************************************************
// DESCRIPTION: Disclaimer::send2Way
// This function creates a disclaimer message to the sender and the receiver.
// Note: it appends the disclaimer messages to the existing server messages (srvMssages) 
// if they already exist.
// *****************************************************************
int Disclaimer::send2Way(/*in*/ ChatSessionEntry* entry,
                         /*in*/ const char* msg,
                         /*in*/ unsigned long msgLen,
                         /*out*/ StDdaClSrvMsg**   srvMessages,
                         /*out*/ unsigned long*    srvMessagesLen)
{
  unsigned long offset = *srvMessagesLen; // The offset of the existing messages
  StDdaClSrvMsg* msgArray = NULL;

  // If there were no previous server messages
  if (*srvMessagesLen == 0)
  {
    // If there was an original text message, create 3 messages
    // 1(disclaimer to sender)+1(disclaimer to receiver)+1(original message to receiver)
    // otherwise only 2 messages for the disclaimer
    int newLen = (msgLen>0)? 3:2;
    *srvMessages = new StDdaClSrvMsg[newLen];
    *srvMessagesLen = newLen;
  }
  else if (*srvMessagesLen>0) 
  {
    // If there were already server messages, add 2 more messages
    // Reallocate the server messages: add 2 more and 1 for the original text message
    // if exists
    unsigned long newLen = *srvMessagesLen + ((msgLen>0)? 3:2);
    StDdaClSrvMsg* newMsgArray = new StDdaClSrvMsg[newLen];
    
    // Copy old server message to the new 
    unsigned long i=0;
    for (i=0; i<*srvMessagesLen; i++)
    {
      StDdaClSrvMsg srvMsg = (*srvMessages)[i];
      newMsgArray[i] = srvMsg;
      //Copy the msg buffer
      int msgLen = strlen(srvMsg.msg);
      newMsgArray[i].msg = new char[msgLen+1];
      strcpy(newMsgArray[i].msg, srvMsg.msg);
    }

    // Delete original server message
    for (i = 0; i < *srvMessagesLen; i++) 
    {
      StDdaClSrvMsg* msg = (*srvMessages) + i;
      if(msg->msg != NULL) 
      {
        delete msg->msg;
        msg->msg = NULL;
      }
    }
    delete[] (*srvMessages);
    // Assign new buffer 
    *srvMessagesLen = newLen;
    *srvMessages = newMsgArray;
  }

  bool isRichText = RT_MGR.isRichTextEnabled(entry);
  // Disclaimer message for the sender
  ((*srvMessages)[offset]).srvMsgType = ST_DDA_CL_TEXT_MSG;
  ((*srvMessages)[offset]).srvMsgDestination = ST_DDA_CL_SENDER;
  int _msgLen = strlen(CHAT_RSC.getStrChatDisclaimer(isRichText));
  char* buf = new char[_msgLen + 1];
  memset(buf, 0, _msgLen + 1);
  strncpy(buf, CHAT_RSC.getStrChatDisclaimer(isRichText), _msgLen);
  ((*srvMessages)[offset]).msg = buf;

  // Disclaimer message for the receiver
  ((*srvMessages)[offset+1]).srvMsgType = ST_DDA_CL_TEXT_MSG;
  ((*srvMessages)[offset+1]).srvMsgDestination = ST_DDA_CL_RECEIVER;
  _msgLen = strlen(CHAT_RSC.getStrChatDisclaimer(isRichText));
  char* buf2 = new char[_msgLen + 1];
  memset(buf2, 0, _msgLen + 1);
  strncpy(buf2, CHAT_RSC.getStrChatDisclaimer(isRichText), _msgLen);
  ((*srvMessages)[offset+1]).msg = buf2;


  // Forward the original text message
  if (msgLen>0) 
  {
    ((*srvMessages)[offset+2]).srvMsgType = ST_DDA_CL_TEXT_MSG;
    ((*srvMessages)[offset+2]).srvMsgDestination = ST_DDA_CL_RECEIVER;
    char* buf = new char[msgLen + 1];
    memset(buf, 0, msgLen + 1);
    strncpy(buf, msg, msgLen);
    ((*srvMessages)[offset+2]).msg = buf;
  }

  entry->disclaimerSent();
  return ST_DDA_MSG_CHANGED;
}

// *****************************************************************
// DESCRIPTION: Disclaimer::send
// This function send disclaimer message, according to rich/plain
// text recipients mode.
// *****************************************************************
int Disclaimer::send(/*in*/ ChatSessionEntry* entry,
                     /*in*/  const char*      msg,
                     /*in*/  unsigned long    msgLen,
                     /*out*/ StDdaClSrvMsg**  srvMessages,
                     /*out*/ unsigned long*   srvMessagesLen)
{
  int rc = ST_DDA_API_OK;

  if (entry->getSessionType() == ChatSessionEntry::CS_2WAY) {
    if (!entry->isDisclaimerSent()){
      bool isRichText = RT_MGR.isRichTextEnabled(entry);
      if (strlen(CHAT_RSC.getStrChatDisclaimer(isRichText)) > 0) {
        return send2Way(entry, msg, msgLen, srvMessages, srvMessagesLen);
      }
    }
  } else if( (entry->getSessionType() == ChatSessionEntry::CS_NWAY) || (entry->getSessionType() == ChatSessionEntry::CS_ANNOUNCEMENT) ){
    // For NWay chats disclaimer sent to users when joining a session.
    // Rich text messages are not supported for NWay chat.
    bool isRichText = false;

    if (strlen(CHAT_RSC.getStrChatDisclaimer(isRichText)) > 0) {
      *srvMessagesLen = (unsigned long)1;

      StDdaClSrvMsg* msg = new StDdaClSrvMsg;
      *srvMessages = msg;

      msg->srvMsgType			= ST_DDA_CL_TEXT_MSG;
      msg->srvMsgDestination = ST_DDA_CL_SENDER;

      int msgLen = strlen(CHAT_RSC.getStrChatDisclaimer(isRichText));
      char* buf = new char[msgLen + 1];
      memset(buf, 0, msgLen + 1);
      strncpy(buf, CHAT_RSC.getStrChatDisclaimer(isRichText), msgLen);
      msg->msg = buf;

      rc = ST_DDA_MSG_CHANGED;
    }
  }else {
    // The chat session type is not determined.
    CHAT_RSC.print(dbgFlag,"Disclaimer::send Chat session type is not determined");
    rc = ST_DDA_API_INTERNAL_ERROR;
  }

  return rc;
}
// end Disclaimer::send