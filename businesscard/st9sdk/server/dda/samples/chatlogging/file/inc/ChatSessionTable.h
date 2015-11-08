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


#ifndef __CHATSESSIONTABLE_H__
#define __CHATSESSIONTABLE_H__

#pragma warning(disable:4786) // identifier was truncated to '255' characters in the browser information

// Aug-19-2003 toolkit
#if !defined(UNIX_TOOLKIT_COMPILE)
#ifdef _UNIX 
#include "ubique.h" 
#endif
#else  // UNIX_TOOLKIT_COMPILE
#include "windows.h"
#endif // UNIX_TOOLKIT_COMPILE

#include <map>
#include <string>

#if defined(_WINDOWS) || defined(_CONSOLE) || defined(OS400)
using namespace std;
#else
USING_NAMESPACE(std);
#endif

#include "stDdaClApi.h"

class ChatSessionEntry;

// *****************************************************************
// The ChatSessionTable class is a manager of all open sessions.
// *****************************************************************
class ChatSessionTable
{
public:
	map<string, ChatSessionEntry *, less<string> > m_map;

public://Ctor & Dtor 
	ChatSessionTable();
	virtual ~ChatSessionTable(){};

public://methods
	int openChatSession(string key,string organization);
	int closeChatSession(string key);
	void closeAllChatSession(void);
	ChatSessionEntry* findChatSession(string key);

};


// *****************************************************************
// The ChatSessionEntry class represents a single session.
// *****************************************************************
class ChatSessionEntry
{
private:
	string m_key;
	string m_organization;
	static string path;
	string m_from;
	string m_to;
	int m_richTextCounter;
	bool m_isDisclaimerSent;

public:
	enum SessionType {CS_UNDETERMINED, CS_2WAY, CS_NWAY, CS_ANNOUNCEMENT};
private:
	SessionType m_sessionType;

public:

	FILE * sessionFile;
	static void createPath();
	static char set[10];
	static const char* getSet(void){return set;}

	string filePath;

private:
	FILE* imageFile;
	string imageFilePath;
	int imageFileCount;

public:
	ChatSessionEntry(string key,string organization);
	virtual ~ChatSessionEntry();

public:
	string getKey(void){return m_key;}; 
	string getFrom() { return m_from; };
	string getTo()   { return m_to; };
	int getRichTextCounter() {return m_richTextCounter;};
	bool isDisclaimerSent(){return m_isDisclaimerSent;};
	void setFrom(string from) { m_from = from; };
	void setTo(string to)     { m_to = to; };
	void incRichTextCounter() {m_richTextCounter++;};
	void disclaimerSent() {m_isDisclaimerSent = true;};
	SessionType getSessionType()  { return m_sessionType; };
	void determineSessionType(const StDdaClEntity* scope); 

	FILE* getSessionFile(void){return sessionFile;}; 
	FILE* getImageFile() { return imageFile; };
	const char* getImageFilePath() { return imageFilePath.c_str();};

	void getEntityType(const StClEntityType entity,char* entityType);

	bool openFile(FILE*& file, const char* fileName);

	bool closeFile(FILE*& file, const char* fileName);

	bool openImgFile();

	bool closeImgFile();

	int startSession();

	int endSession();

	int addUser(const StDdaClEntity * entity,
		const StDdaClEntity * scope);

	int removeUser(const StDdaClEntity * entity);

	int saveImage(unsigned long msgLen,
		const char* msg);

	int logDisclaimer(const StDdaClEntity*    sender, 
		const StDdaClEntity*    receiver);

	int message(bool                 isRT,
		const StDdaClEntity* sender,
		unsigned long        msgLen,
		const char*          msg,
		const StDdaClEntity* receiver,
		StDdaClSrvMsg**      srvMessages,
		unsigned long*       srvMessagesLen);

	int verifyMessageChanges(bool                 isRT,
		const StDdaClEntity* sender,
		unsigned long        msgLen,
		const char*          msg,
		const StDdaClEntity* receiver,
		StDdaClSrvMsg**      srvMessages,
		unsigned long*       srvMessagesLen);

private:
	int logUserToSessionFile(const StDdaClEntity* entity,
		const StDdaClEntity* scope,
		char*                entityType, // ST_DDA_MAX_NAME_LENGTH
		char*                scopeType); // ST_DDA_MAX_NAME_LENGTH

};

#endif //__CHATSESSIONTABLE_H__
