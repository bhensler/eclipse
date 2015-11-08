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



#ifndef __CHATRESOURCE_H__
#define __CHATRESOURCE_H__

#include <stDdaClCodes.h>

#define CHAT_RSC (ChatResource::instance())

// *****************************************************************
// The ChatResource class is responsible for:
// •	Getting all the ini flags from chatlogging.ini
// •	Creating the default path for session files
// •	Printing debug messages to the trace file
// •	Getting the current time string
// *****************************************************************

class ChatResource
{
public: //Ctor + Dtor
	ChatResource();
	~ChatResource() {};

private://members
	char defaultPath[ST_DDA_MAX_NAME_LENGTH];
	char tracePath[ST_DDA_MAX_NAME_LENGTH];
	char controlPath[ST_DDA_MAX_NAME_LENGTH];
	char iniFile[ST_DDA_MAX_NAME_LENGTH];

	// flags for server messages
	bool attempToBanChats;
	char banChatFrom[ST_DDA_MAX_NAME_LENGTH];
	char banChatTo[ST_DDA_MAX_NAME_LENGTH];
	char banChatWarning[ST_DDA_MAX_NAME_LENGTH];

	bool attempToReplaceSubstrings;
	char strToBeReplaced[ST_DDA_MAX_NAME_LENGTH];
	char strToReplace[ST_DDA_MAX_NAME_LENGTH];
	char strReplacedWarning[ST_DDA_MAX_NAME_LENGTH];
	char strReplacedWarningRichText[ST_DDA_MAX_NAME_LENGTH];

	bool attempToDeleteMsgs;
	char strForcesMsgDeletion[ST_DDA_MAX_NAME_LENGTH];
	char warningMsgOnDeletion[ST_DDA_MAX_NAME_LENGTH];
	char warningMsgOnDeletionRichText[ST_DDA_MAX_NAME_LENGTH];

	bool attempToSendChatDisclaimer;
	char strChatDisclaimer[ST_DDA_MAX_NAME_LENGTH];
	char strChatDisclaimerRichText[ST_DDA_MAX_NAME_LENGTH];

private://methods
	void createDefaultPath(void);
	void createStrControlPath(void);
	void readSrvMsgFlags(void);
	bool readConfigStr(const char* configKey, char* buf);
	bool parseConfigStr(const char* configKey, char* left, char* right);

public:// methods
	const char* getDefaultPath(void);
	const char* getStrControlPath(void);
	void        print(int flag,const char* format, ...);
	int         getDebugFlagValue(const char* id);
	const char* getCurTime(void);
	const bool  isImageMsg(unsigned long dataType, 
		unsigned long dataSubType,
		unsigned long msgLen,
		const char *msg);
	const bool  isRichTextMsg(unsigned long dataType, 
		unsigned long dataSubType,
		unsigned long msgLen,
		const char *msg);

	bool isAttempToBanChats()             { return attempToBanChats; };
	bool isAttempToReplaceSubstrings()    { return attempToReplaceSubstrings; };
	bool isAttempToDeleteMsgs()           { return attempToDeleteMsgs; };
	bool isAttempToSendChatDisclaimer()   { return attempToSendChatDisclaimer; };
	const char* getBanChatFrom()          { return banChatFrom; };
	const char* getBanChatTo()            { return banChatTo; };
	const char* getBanChatWarning()       { return banChatWarning; };
	const char* getStrToBeReplaces()      { return strToBeReplaced; };
	const char* getStrToReplace()         { return strToReplace; };
	const char* getStrReplacedWarning(bool isRT)   { return (isRT) ? strReplacedWarningRichText : strReplacedWarning; };
	const char* getStrForceMsgDeletion()  { return strForcesMsgDeletion; };
	const char* getWarningMsgOnDeletion(bool isRT) { if(isRT) return warningMsgOnDeletionRichText; 
	else return warningMsgOnDeletion; };
	const char* getStrChatDisclaimer(bool isRT)    { if(isRT) return strChatDisclaimerRichText; 
	else return strChatDisclaimer;};  

public://Single Tone
	static ChatResource*   m_ptr;
	static ChatResource& instance()
	{
		if(!m_ptr)
			m_ptr=new ChatResource();
		return *m_ptr;
	}


};


#endif //__CHATRESOURCE_H__
