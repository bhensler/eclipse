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


#include <ChatSessionTable.h>
#include <ChatResource.h>
#include <stDdaClCodes.h>

// identifier was truncated to '255' characters in the browser information
#pragma warning(disable:4786)

extern int dbg;

string ChatSessionEntry::path;

char ChatSessionEntry::set[] = {'/','\\',':','*','?','\"','<','>','|',' '};


// *****************************************************************
//***************** ChatSessionTable class *************************
// *****************************************************************

// *****************************************************************
// DESCRIPTION: ChatSessionTable::ChatSessionTable
// *****************************************************************
ChatSessionTable::ChatSessionTable()
{
} // end ChatSessionTable::ChatSessionTable


// *****************************************************************
// DESCRIPTION: ChatSessionTable::openChatSession
// Use this function to check whetherthe session exists in the 
// chatSession table. 
// If it does not exist, create and start the session.
// *****************************************************************
int ChatSessionTable::openChatSession(string key,string organization)
{	
  if (findChatSession(key))
    return ST_DDA_CL_SESSION_ALREADY_EXISTS;

  // To add a new session to the chatSession table
  // use ChatSessionEntry.
  // 19-March-2002: need organization passed  	
  ChatSessionEntry* entry = new ChatSessionEntry(key, organization); 
  m_map.insert(map<string, ChatSessionEntry *, 
    less<string> >::value_type(key, entry));

  // Start the session.
  return entry->startSession();
} // end ChatSessionTable::openChatSession 


// *****************************************************************
// DESCRIPTION: ChatSessionTable::closeChatSession
// Use this function to close the existing session when it ends.
// *****************************************************************
int ChatSessionTable::closeChatSession(string key)
{
  ChatSessionEntry* entry = 0;
  int rc = ST_DDA_CL_SESSION_DOES_NOT_EXIST;

  // Find the session handler in the session table.
  map<string, ChatSessionEntry *,
    less<string> >::iterator iter = m_map.find(key);
  if (iter != m_map.end())
  {
    entry = (ChatSessionEntry *)((*iter).second);
    rc = entry->endSession();
    m_map.erase(iter);
  }
  else
  {
    // Print the debug message in the trace file 
    // if the session is not found.
    CHAT_RSC.print(dbg,"ChatSessionTable::closeChatSession: Session - %s doesn't exist ",key.c_str());
  }
  return rc;
} // end ChatSessionTable::closeChatSession


// *****************************************************************
// DESCRIPTION: ChatSessionTable::closeAllChatSession
// This function deletes all the entries from the ChatSession table.
// *****************************************************************
void ChatSessionTable::closeAllChatSession(void)
{

  CHAT_RSC.print(dbg,"ChatSessionTable::closeAllChatSession: processing...");
  m_map.clear();

} // end ChatSessionTable::closeAllChatSession


// *****************************************************************
// DESCRIPTION: ChatSessionTable::findChatSession
// This function finds and returns 
// the session handler, based on its sessionId in the session table.
// *****************************************************************
ChatSessionEntry* ChatSessionTable::findChatSession(string key)
{
  ChatSessionEntry* entry = 0;

  map<string, ChatSessionEntry *
    , less<string> >::iterator iter = m_map.find(key);

  if (iter != m_map.end())
    entry = (ChatSessionEntry *)((*iter).second);

  return entry;
} // end ChatSessionTable::findChatSession



//*************************************************************
//***************** ChatSessionEntry class ********************
//*************************************************************

// *****************************************************************
// DESCRIPTION: ChatSessionTable::createPath
// This function creates the path where all session files are to be
// created. This static function is called only once when the first
// session is opened.
// *****************************************************************
void ChatSessionEntry::createPath()
{
  if ( !path.empty())
    return;
  string controlPath = CHAT_RSC.getStrControlPath();

  if(controlPath.empty())
    controlPath = CHAT_RSC.getDefaultPath();

  path = controlPath;

  int length = path.size();
  //	if (!(path[length] == '/') || !(path[length] == '\\'))
  //   This are 2 bugs here....you need length-1 to point to the last char in the string and the logic is messed up too because the if statement will always be true the way it is written
#ifdef _UNIX
  if (!(path[length-1] == '/')) 	
    path.append("/");	    
#else
  if (!(path[length-1] == '\\')) 	
    path.append("\\");
#endif

  return;
} // end ChatSessionTable::createPath


// *****************************************************************
// DESCRIPTION: ChatSessionEntry::ChatSessionEntry
// *****************************************************************
ChatSessionEntry::ChatSessionEntry(string key, string organization):
sessionFile(0),m_organization(organization),imageFile(0),
imageFileCount(0),m_richTextCounter(-1),m_isDisclaimerSent(false),
m_sessionType(CS_UNDETERMINED)
{	
  m_key=key;
  string temp;
  temp=key;
  temp+="_";
  temp.append(CHAT_RSC.getCurTime());
  temp.append(CL_SUFFIX);
  int length = temp.size() ;
  for( int  index = temp.find_first_of( ChatSessionEntry::getSet());
    index != -1;
    index = temp.find_first_of( ChatSessionEntry::getSet()))
  {
    temp[index] = '_';
  }

  filePath = path;
  filePath.append(temp);

} // end ChatSessionEntry::ChatSessionEntry

// *****************************************************************
// DESCRIPTION: ~ChatSessionEntry::ChatSessionEntry
// *****************************************************************
ChatSessionEntry::~ChatSessionEntry()
{
  if (sessionFile)
    fclose(sessionFile);
  if (imageFile)
    fclose(imageFile);
} // end ~ChatSessionEntry::ChatSessionEntry


// *****************************************************************
// DESCRIPTION: ChatSessionEntry::openFile
// Use this function to open the file for writing.
// *****************************************************************
bool ChatSessionEntry::openFile(FILE*& file, const char* fileName)
{
  CHAT_RSC.print(dbg,"ChatSessionEntry::openFile: file name <%s>"
    , fileName);

#ifdef OS400
#ifdef UNIX_TOOLKIT_COMPILE
  // the qadrt version of fopen does not support 
  // the codepage parameter
  file = fopen(fileName, "ab+");
#else
  file = fopen(fileName, "ab+, codepage=819");
#endif // UNIX_TOOLKIT_COMPILE
#else
  file = fopen(fileName, "ab+");
#endif
  CHAT_RSC.print(dbg,"ChatSessionEntry::openFile: ended with <%s>",
    file ?" true" : "false");

  if (!file)
    return false;
  return true;
} // end ChatSessionEntry::getFullPath

// *****************************************************************
// DESCRIPTION: ChatSessionEntry::closeFile
// Use this function to close file.
// *****************************************************************
bool ChatSessionEntry::closeFile(FILE*& file, const char* fileName)
{
  if (!file)
  {
    CHAT_RSC.print(dbg,"ChatSessionEntry::closeFile: file name <%s> cannot close the file - session null pointer", fileName);
    return false;
  }

  CHAT_RSC.print(dbg,"ChatSessionEntry::closeFile: file name <%s>",
    fileName);

  fclose(file);
  return true;
} // end ChatSessionEntry::closeFile

// *****************************************************************
// DESCRIPTION: ChatSessionEntry::openImgFile
// Use this function to open the image file
// *****************************************************************
bool ChatSessionEntry::openImgFile()
{
  // create image file name
  string tmp = filePath;
  if(tmp.length() > 4) {
    // remove the .dat suffix
    tmp = tmp.substr(0, tmp.length() - 4);
  }
  char countStr[10];
  memset(countStr, sizeof(char), 10);
  sprintf(countStr, "%d", imageFileCount);
  ++imageFileCount;

  tmp.append("_image_");
  tmp.append(countStr);
  tmp.append(".image");
  imageFilePath = tmp;

  return openFile(imageFile, getImageFilePath());
}// end ChatSessionEntry::openImgFile

// *****************************************************************
// DESCRIPTION: ChatSessionEntry::closeImgFile
// Use this function to close the image file.
// *****************************************************************
bool ChatSessionEntry::closeImgFile()
{
  return closeFile(imageFile /*getImageFile()*/, getImageFilePath());
} // end ChatSessionEntry::closeImgFile


// *****************************************************************
// DESCRIPTION: ChatSessionEntry::determineSessionType
// *****************************************************************
void ChatSessionEntry::determineSessionType(const StDdaClEntity* scope)
{
  // Determine session type, 1on1 or NWay chat or announcement.
  if (getSessionType() == ChatSessionEntry::CS_UNDETERMINED) {
	if(!scope) {
		if (strncmp(m_key.c_str(), "ANNOUNCE_", 9)==0){
			m_sessionType = CS_ANNOUNCEMENT;
			CHAT_RSC.print(dbg,"ChatSessionEntry::determineSessionType: session type is CS_ANNOUNCEMENT");
		}else{
			m_sessionType = CS_2WAY;
			CHAT_RSC.print(dbg,"ChatSessionEntry::determineSessionType: session type is CS_2WAY");
		}
	} 
	else if (scope->type == ST_DDA_CL_SESSION 
		&& (strncmp(scope->id, "PLACE", ST_DDA_MAX_NAME_LENGTH-1)==0)){
		m_sessionType = CS_NWAY;
		CHAT_RSC.print(dbg,"ChatSessionEntry::determineSessionType: session type is CS_NWAY");
	}
  }
}// end ChatSessionEntry::determineSessionType

// *****************************************************************
// DESCRIPTION: ChatSessionEntry::getEntityType
// Use this function to convert the 
// entity type to a suitable string.
// *****************************************************************
void ChatSessionEntry::getEntityType(const StClEntityType entity, char* entityType)
{
  CHAT_RSC.print(dbg, "ChatSessionEntry::getEntityType: entity <%d>", entity);

  if (!entityType) {
    CHAT_RSC.print(dbg, "ChatSessionEntry::getEntityType: empty entityType");
  }

  switch (entity) {
    case ST_DDA_CL_USER:
      strcpy(entityType, "User");
      break;
    case ST_DDA_CL_OFFLINE_USER:
      strcpy(entityType, "OfflineUser");
      break;
    case ST_DDA_CL_SECTION:
      strcpy(entityType, "Section");
      break;
    case ST_DDA_CL_ACTIVITY:
      strcpy(entityType, "Activity");
      break;
    case ST_DDA_CL_SESSION:
      strcpy(entityType, "Session");
      break;
    default:
      strcpy(entityType, "Unknown");
  }

  CHAT_RSC.print(dbg,"ChatSessionEntry::getEntityType: entityType <%s>", entityType);
} // end ChatSessionEntry::getEntityType

// *****************************************************************
// DESCRIPTION: ChatSessionEntry::startSession
// Use this function to write a "start" message to the appropriate
// session file.
// *****************************************************************
int ChatSessionEntry::startSession()
{
  CHAT_RSC.print(dbg,"startSession: session <%s>",getKey().c_str());

  if (!openFile(sessionFile, filePath.c_str())) {
    // If the session cannot open the session file, a debug message
    // is printed to the trace file 
    // and the function error code is returned.
    CHAT_RSC.print(dbg,"startSession: Can't create %s "
      ,filePath.c_str());
    return ST_DDA_CL_DB_ERROR;
  }

  CHAT_RSC.print(dbg,"startSession: before fprintf");
  int rc = fprintf(sessionFile, 
    "\n%s: Session %s started for organization %s",
    CHAT_RSC.getCurTime(), getKey().c_str(), m_organization.c_str());
  CHAT_RSC.print(dbg,"startSession: after fprintf");
  closeFile(sessionFile, filePath.c_str());
  CHAT_RSC.print(dbg,"startSession: ended with error <0x%x> ",rc);
  if (rc < 0) {
    return ST_DDA_API_INTERNAL_ERROR;
  }
  return ST_DDA_API_OK;
} // end ChatSessionEntry::startSession


// *****************************************************************
// DESCRIPTION: ChatSessionEntry::endSession
// Use this function to write an "end" message to the appropriate
// session file.
// *****************************************************************
int ChatSessionEntry::endSession()
{
  CHAT_RSC.print(dbg,"endSession: session <%s>",getKey().c_str());

  if (!openFile(sessionFile, filePath.c_str()))
  {
    // If the session cannot open the session file, a debug 
    // message is printed to the trace file 
    // and the function error code is returned.
    CHAT_RSC.print(dbg,"endSession: Can't open %s "
      ,filePath.c_str());
    return ST_DDA_CL_DB_ERROR;
  }
  int rc = fprintf(sessionFile, "\n%s: Session ended "
    ,CHAT_RSC.getCurTime());
  int error = fprintf(sessionFile, "\n***************\n");
  closeFile(sessionFile, filePath.c_str());

  CHAT_RSC.print(dbg,"endSession: ended with error <0x%x> ",rc);

  if (rc<0 || error < 0)
    return ST_DDA_API_INTERNAL_ERROR;
  return ST_DDA_API_OK;	
} // end ChatSessionEntry::endSession

// *****************************************************************
// DESCRIPTION: ChatSessionEntry::logUserToSessionFile
// This method is used by the addUser method to add the joining user
// to the log file.
// *****************************************************************
int ChatSessionEntry::logUserToSessionFile(const StDdaClEntity* entity,
                                           const StDdaClEntity* scope,
                                           char*            entityType,
                                           char*            scopeType)
{
  char line[ST_DDA_MAX_STR_LEN];

  if (!openFile(sessionFile, filePath.c_str())) {
    // If the session cannot open the session file, a debug message 
    // is printed to the trace file 
    // and the function error code is returned
    CHAT_RSC.print(dbg,"addUser: Can't open %s ",filePath.c_str());
    return ST_DDA_CL_DB_ERROR;
  }

  if (scope) {
    // Create the string with the entity information and the scope
    // type. This string is written to the session file.
    getEntityType(scope->type, scopeType);
    sprintf(line, "\n%s: Add %sId %s to %sId %s",
      CHAT_RSC.getCurTime(),
      entityType,
      entity->id,
      scopeType,
      scope->id);
  }
  else {
    // Otherwise, create this string containing the time and entity
    // only. This string is written to the session file.
    sprintf(line, "\n%s: Add %sId %s ",
      CHAT_RSC.getCurTime(),
      entityType,
      entity->id);
  }

  int rc = fprintf(sessionFile,"%s",line);
  closeFile(sessionFile, filePath.c_str());
  CHAT_RSC.print(dbg,"addUser: ended with error <0x%x> ",rc);

  if (rc < 0)
    return ST_DDA_CL_DB_ERROR;
  return ST_DDA_API_OK;	

} // ChatSessionEntry::logUserToSessionFile

// *****************************************************************
// DESCRIPTION: ChatSessionEntry::addUser
// Use this function to write an "add user" 
// message to the appropriate session file.
// *****************************************************************
int ChatSessionEntry::addUser(const StDdaClEntity* entity,
                              const StDdaClEntity* scope)
{
  CHAT_RSC.print(dbg, "addUser: session <%s>", getKey().c_str());

  char line[ST_DDA_MAX_STR_LEN];
  char entityType[ST_DDA_MAX_NAME_LENGTH];
  char scopeType[ST_DDA_MAX_NAME_LENGTH];
  getEntityType(entity->type, entityType);

  if(m_from.length() == 0) {
    // If this is the first user to be added to the chat, 
    // update the m_from field. 
    m_from = entity->id;
  }
  else {
    // If this is the second user to be added, 
    // verify whether the chat should be banned.
    m_to = entity->id;
    if (CHAT_RSC.isAttempToBanChats()) {
      if ((m_from == CHAT_RSC.getBanChatFrom() 
        && m_to == CHAT_RSC.getBanChatTo()) 
        || 	(m_to == CHAT_RSC.getBanChatFrom() 
        && m_from == CHAT_RSC.getBanChatTo())) 
      {
        // the chat should be banned
        if (openFile(sessionFile, filePath.c_str())) {
          sprintf(line, "\n%s: Collaboration attempt between %s and %s was identified and forbidden",
            CHAT_RSC.getCurTime(),
            m_from.c_str(),
            m_to.c_str());
          int rc = fprintf(sessionFile,"%s", line);
          closeFile(sessionFile, filePath.c_str());
          if (rc < 0)
            return ST_DDA_CL_DB_ERROR;
        }
        CHAT_RSC.print(dbg,"It is forbidden to open chat between %s and %s", m_from.c_str(), m_to.c_str());
        return ST_DDA_SESSION_CREATION_DECLINED;
      }
    }
  }

  return logUserToSessionFile(entity, scope, entityType, scopeType);
} // end ChatSessionEntry::addUser


// *****************************************************************
// DESCRIPTION: ChatSessionEntry::removeUser
// Use this function to write a "remove user" message to the 
// appropriate session file. 
// If the session cannot open the session file, a debug message 
// is printed to the trace file and a function error code is returned.
// *****************************************************************
int ChatSessionEntry::removeUser(const StDdaClEntity *entity)
{
  CHAT_RSC.print(dbg,"removeUser: session <%s>",getKey().c_str());

  if (!openFile(sessionFile, filePath.c_str())){
    CHAT_RSC.print(dbg,"removeUser: Can't open %s ",filePath.c_str());
    return ST_DDA_CL_DB_ERROR;
  }
  char entityType[ST_DDA_MAX_NAME_LENGTH];;

  //Find the entity type string.
  getEntityType(entity->type, entityType);

  // Create the string that is written to the session file.
  char line[ST_DDA_MAX_STR_LEN];
  sprintf(line, "\n%s: Remove %sId %s ", CHAT_RSC.getCurTime(),
    entityType,	entity->id);

  int rc = fprintf(sessionFile,"%s", line);
  closeFile(sessionFile, filePath.c_str());
  CHAT_RSC.print(dbg,"removeUser: ended with error <0x%x> ",rc);

  if (rc < 0)
    return ST_DDA_API_INTERNAL_ERROR;
  return ST_DDA_API_OK;	
} // end ChatSessionEntry::removeUser


// *****************************************************************
// DESCRIPTION: ChatSessionEntry::message
// Use this function to write a message to the appropriate session file.
// If the session cannot open the file, a debug message is printed to 
// the trace file and an error code is returned.
// *****************************************************************
int ChatSessionEntry::message(bool isRT,
                              const StDdaClEntity *sender, 
                              unsigned long msgLen,
                              const char* msg,
                              const StDdaClEntity *receiver,
                              StDdaClSrvMsg** srvMessages,
                              unsigned long* srvMessagesLen) //==Null - All
{
  int index;
  CHAT_RSC.print(dbg,"message: session <%s> len <%d>",
    getKey().c_str(),msgLen);

  if (!openFile(sessionFile, filePath.c_str())){
    CHAT_RSC.print(dbg,"message: Can't open %s ",filePath.c_str());
    return ST_DDA_CL_DB_ERROR;
  }

  if(!sender)
  {
    CHAT_RSC.print(dbg,"message: session <%s> - null pointer sender"
      ,getKey().c_str());
    return ST_DDA_API_INTERNAL_ERROR;
  }

  char line[ST_DDA_MAX_MSG_LEN];
  char entityType[ST_DDA_MAX_NAME_LENGTH];
  int rc = 0;

  // Find the entity type string.
  getEntityType(sender->type, entityType);

  CHAT_RSC.print(dbg,"message: before sprintf_1");

  //Create the string that is written to the session file.
  index = sprintf(line, "\n%s: Send Message from %sId %s ", 
    CHAT_RSC.getCurTime(),
    entityType,	sender->id);

  CHAT_RSC.print(dbg,"message: after sprintf_1");

  // Verify that the server should or should not send server messages. 
  // If such messages should be sent, return them to the higher level.
  int verifyResult = verifyMessageChanges(isRT,sender, msgLen, msg, 
    receiver, srvMessages, srvMessagesLen);
  if(verifyResult != ST_DDA_API_OK) {
    return verifyResult;
  }

  if (!receiver)
  {
    // If the entity is All (all users in this session), 
    // create this string to be written to the session file.
    CHAT_RSC.print(dbg,"message: before sprintf_2");
    index += sprintf(line + index, "to All ");
    CHAT_RSC.print(dbg,"message: after sprintf_2");  	
  }
  else
  {
    // Otherwise, find the receiver entity type string and create
    // this ID string to be written to the session file.
    getEntityType(receiver->type, entityType);
    CHAT_RSC.print(dbg,"message: before sprintf_3");
    index += sprintf(line + index, "to %sId %s ", entityType,receiver->id);
    CHAT_RSC.print(dbg,"message: after sprintf_3");
  }

  // Create the message string that is written to the session file.
  // 6Sep2003. iSeries has limit of 2046 on sprintf
#ifdef OS400
#define MAXSTRING_a 2046
  if (strlen(line) + msgLen < MAXSTRING_a)
  {
    index += sprintf(line + index, "msgLen: %d msg: %s ",msgLen,msg);
    // sprintf(line, "%s msgLen %id msg: %s ",line, msgLen, msg);
  }
  else
  {
    // need to split up the msg and log it in 2046 byte chunks 
    int copiedLen = MAXSTRING_a - strlen(line);
    char temp[MAXSTRING_a+1];
    strncpy(temp, msg, copiedLen);

    // write first 2046 chars
    index += sprintf(line + index, "%s", temp);
    for (int n = copiedLen; n < msgLen; n += MAXSTRING_a)
    {
      // write to file
      rc = fprintf(sessionFile,"%s",line);
      strcpy(line,"");
      strncpy(temp,"",MAXSTRING_a+1);
      strncpy(temp, &msg[copiedLen], MAXSTRING_a); 
      sprintf(line, "%s",temp);
      copiedLen += strlen(line);		
    }
  }
#else
  if ( strlen(line) + msgLen < ST_DDA_MAX_MSG_LEN )
    // This condition makes sure that we have enough place in 
    // line for the current message
  {
#ifdef ZLINUX64
    index += sprintf(line + index, "msgLen %ld msg: %s ",msgLen,msg);
#else
    index += sprintf(line + index, "msgLen %d msg: %s ",msgLen,msg);
#endif
    CHAT_RSC.print(dbg,"message: msg size is OK");
  }
  else
  {
    CHAT_RSC.print(dbg,"message:Fatal Error message is bigger then allocated buffer ");
    return ST_DDA_MSG_TOO_LONG;
  }
#endif

  CHAT_RSC.print(dbg,"message: before session fprintf message len <%d> line len <%d>",msgLen,strlen(line));
  rc = fprintf(sessionFile, "%s", line);
  CHAT_RSC.print(dbg,"message: after session fprintf ");

  closeFile(sessionFile, filePath.c_str());

  CHAT_RSC.print(dbg,"message: after closeFile");

  CHAT_RSC.print(dbg,"message: ended with error <0x%x> ",rc);

  if (rc < 0)
    return ST_DDA_API_INTERNAL_ERROR;
  return ST_DDA_API_OK;	
} // end ChatSessionEntry::message

// *****************************************************************
// DESCRIPTION: ChatSessionEntry::verifyMessageChanges
// Use this function to check if user message caused any server
// messages to be sent.
// *****************************************************************
int ChatSessionEntry::verifyMessageChanges(bool isRT,
                                           const StDdaClEntity * sender,
                                           unsigned long msgLen,
                                           const char* msg,
                                           const StDdaClEntity * receiver,
                                           StDdaClSrvMsg** srvMessages,
                                           unsigned long* srvMessagesLen)
{
  int result = ST_DDA_API_OK;

  if(CHAT_RSC.isAttempToDeleteMsgs()) {
    if(strstr(msg, CHAT_RSC.getStrForceMsgDeletion()) != NULL) {
      if(strlen(CHAT_RSC.getWarningMsgOnDeletion(isRT)) > 0) {
        *srvMessagesLen = (unsigned long)1;
        *srvMessages = new StDdaClSrvMsg();
        (*srvMessages)->srvMsgType = ST_DDA_CL_TEXT_MSG;
        (*srvMessages)->srvMsgDestination = ST_DDA_CL_SENDER;
        int msgLen = strlen(CHAT_RSC.getWarningMsgOnDeletion(isRT));
        char* buf = new char[msgLen + 1];
        memset(buf, 0, msgLen + 1);
        strncpy(buf, CHAT_RSC.getWarningMsgOnDeletion(isRT), msgLen);
        (*srvMessages)->msg = buf;
      }
      return ST_DDA_MSG_CHANGED;
    }
  }

  if(CHAT_RSC.isAttempToReplaceSubstrings()) {
    if(strstr(msg, CHAT_RSC.getStrToBeReplaces()) != NULL) {
      // replace the occurences of filtered pattern by 
      // the replacing pattern
      const char* strToSearch = CHAT_RSC.getStrToBeReplaces();
      const char* strToReplace = CHAT_RSC.getStrToReplace();

      char* newMsg = NULL;
      char* buffer = new char[strlen(msg)+1];
      strcpy(buffer, msg);

      do{
        // Calculate new length for the result buffer.
        int bufLen = strlen(buffer); 
        if(strlen(strToReplace) != strlen(strToSearch)) {
          bufLen = bufLen + strlen(strToReplace) - strlen(strToSearch);
        }

        newMsg = new char[bufLen + 1];
        memset(newMsg, 0, bufLen + 1);

        const char *p = strstr(buffer, strToSearch);
        if(p != NULL) {
          // Construct the new string with the replaced pattern
          strncpy(newMsg, buffer, p - buffer);
          sprintf(newMsg + (p - buffer), "%s%s", strToReplace, p + strlen(strToSearch));
        } 
        else {
          delete[] buffer;
          delete[] newMsg;
          buffer = NULL;
          newMsg = NULL;
          return ST_DDA_API_INTERNAL_ERROR;
        }

        delete[] buffer;
        buffer = new char[strlen(newMsg)+1];
        strcpy(buffer, newMsg);
        delete[] newMsg;

        //Repeat untill there is nothing to replace
      } while(strstr(buffer, strToSearch) != 0);
      if (strlen(CHAT_RSC.getStrReplacedWarning(isRT)) > 0) {
        StDdaClSrvMsg* msgArray2 = new StDdaClSrvMsg[2];
        *srvMessages = msgArray2;
        *srvMessagesLen = (unsigned long)2;

        // The replaced string
        msgArray2[0].srvMsgType = ST_DDA_CL_TEXT_MSG;
        msgArray2[0].srvMsgDestination = ST_DDA_CL_RECEIVER;
        msgArray2[0].msg = buffer;

        // Send a warning message to the sender, 
        // notifying that the original message was changed.
        int warnMsgLen = strlen(CHAT_RSC.getStrReplacedWarning(isRT));
        char* warningMsg = new char[warnMsgLen + 1];
        memset(warningMsg, 0, warnMsgLen +1);
        strncpy(warningMsg, CHAT_RSC.getStrReplacedWarning(isRT)
          , warnMsgLen);

        msgArray2[1].srvMsgType = ST_DDA_CL_TEXT_MSG;
        msgArray2[1].srvMsgDestination = ST_DDA_CL_SENDER;
        msgArray2[1].msg = warningMsg;

        return ST_DDA_MSG_CHANGED;
      } else {
        // If there is no warning message to the sender, 
        // send only the replaced string to the receiver.
        *srvMessagesLen = (unsigned long)1;
        *srvMessages = new StDdaClSrvMsg();
        (*srvMessages)->srvMsgType = ST_DDA_CL_TEXT_MSG;
        (*srvMessages)->srvMsgDestination = ST_DDA_CL_RECEIVER;
        (*srvMessages)->msg = buffer;

        return ST_DDA_MSG_CHANGED;
      }
    }
  }
  return result;
}// end ChatSessionEntry::verifyMessageChanges

// *****************************************************************
// DESCRIPTION: ChatSessionEntry::saveImage
// Use this function to save the image data message.
// *****************************************************************
int ChatSessionEntry::saveImage(unsigned long msgLen, const char* msg)
{
  int rc = 0;
  CHAT_RSC.print(dbg,"saveImage: session <%s> image len <%d>",
    getKey().c_str(), msgLen);

  if (!openImgFile()){
    CHAT_RSC.print(dbg,"message: Can't open %s ",filePath.c_str());
    return ST_DDA_CL_DB_ERROR;
  }

  CHAT_RSC.print(dbg,"saveImage: before session fprintf");
  fwrite((const void*)(msg + 13), sizeof(char), msgLen - 13, 
    getImageFile());
  CHAT_RSC.print(dbg,"saveImage: after session fprintf ");

  closeImgFile();

  CHAT_RSC.print(dbg,"saveImage: after closeFile");

  CHAT_RSC.print(dbg,"saveImage: ended with error <0x%x> ",rc);

  if (rc < 0)
    return ST_DDA_API_INTERNAL_ERROR;
  return ST_DDA_API_OK;	
}  // end ChatSessionEntry::saveImage

// *****************************************************************
// DESCRIPTION: ChatSessionEntry::logDisclaimer
// This function send a disclaimer 
// messages to the entering user if needed
// *****************************************************************
int ChatSessionEntry::logDisclaimer(/*in*/const StDdaClEntity* sender, 
                                    /*in*/const StDdaClEntity* receiver)
{
  if (!sender) {
    CHAT_RSC.print(dbg,"logDisclaimer: failed to log disclaimer, invalid argument");
    return ST_DDA_API_INTERNAL_ERROR;
  }

  // At this point the Chat Session type should be determined
  if (getSessionType() == CS_UNDETERMINED) {
    CHAT_RSC.print(dbg,"logDisclaimer: failed to log disclaimer, Chat session is undetermined.");
    return ST_DDA_API_INTERNAL_ERROR;
  }

  if (!openFile(sessionFile, filePath.c_str())) {
    CHAT_RSC.print(dbg,"logDisclaimer: failed to log disclaimer, can't open log file for writing [%s]",
      filePath.c_str());
    return ST_DDA_CL_DB_ERROR;
  }

  if (getSessionType() == CS_2WAY) {
    fprintf(sessionFile, "\n%s: Chat started between users %s and %s, a disclaimer message was sent.",
      CHAT_RSC.getCurTime(),
      sender->id,
      receiver->id);  
  } 
  else if (getSessionType() == CS_NWAY) {
    fprintf(sessionFile, "\n%s: User %s joined the chat, a disclaimer message was sent.",
      CHAT_RSC.getCurTime(),
      sender->id);
  }else if (getSessionType() == CS_ANNOUNCEMENT){
    fprintf(sessionFile, "\n%s: Announcement set from sender %s, a disclaimer message was sent.",
      CHAT_RSC.getCurTime(),
      sender->id);  
  }

  if (!closeFile(sessionFile, filePath.c_str())) {
    CHAT_RSC.print(dbg,"logDisclaimer: failed to log disclaimer, can't close the log file [%s]", filePath.c_str());
    return ST_DDA_CL_DB_ERROR;
  }

  return ST_DDA_API_OK;
}// end ChatSessionEntry::logDisclaimer
