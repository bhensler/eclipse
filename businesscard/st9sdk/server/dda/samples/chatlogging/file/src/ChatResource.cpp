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


// If Solaris we must force the File Descriptor to 65536
//ST_Linux_Port_Level0
#if defined(SOLARIS) || defined(LINUX)

// If it has already been defined undefine it.
#if defined(FD_SETSIZE)
#undef FD_SETSIZE
#endif

// Setting the Max Size of File Descriptor Set.
#define FD_SETSIZE 65536
#endif

#include <string.h>
#include <stdio.h>
#if !defined (_UNIX) // Feb 28 2002
#include <conio.h>
#endif
#include <stdlib.h>
#include <stdarg.h>

#include <sys/types.h>
#include <time.h>


#include <windows.h>


#include <ChatResource.h>

#if defined(_UNIX)
#include <utilities.h>
#endif

//Last include, to catch all required platform #ifdef'ed code
#include <StThreadSafe.h>


ChatResource* ChatResource::m_ptr = 0;


// *****************************************************************
// DESCRIPTION: ChatResource::ChatResource
// *****************************************************************
ChatResource::ChatResource()
{
	defaultPath[0] = '\0'; 
	controlPath[0] = '\0';
	iniFile[0] = '\0';
	tracePath[0] = '\0';
	createDefaultPath();
	readSrvMsgFlags();
}// end ChatResource::ChatResource

// *****************************************************************
// DESCRIPTION: ChatResource::readSrvMsgFlags
// Read flags for server messages support
// *****************************************************************
void ChatResource::readSrvMsgFlags()
{
	// init the variables
	attempToBanChats = false;
	attempToReplaceSubstrings = false;
	attempToDeleteMsgs = false;
	attempToSendChatDisclaimer = false;

	memset(banChatFrom, 0, ST_DDA_MAX_NAME_LENGTH);
	memset(banChatTo, 0, ST_DDA_MAX_NAME_LENGTH);
	memset(banChatWarning, 0, ST_DDA_MAX_NAME_LENGTH);
	memset(strToBeReplaced, 0, ST_DDA_MAX_NAME_LENGTH);
	memset(strToReplace, 0, ST_DDA_MAX_NAME_LENGTH);
	memset(strReplacedWarning, 0, ST_DDA_MAX_NAME_LENGTH);
	memset(strReplacedWarningRichText, 0, ST_DDA_MAX_NAME_LENGTH);
	memset(strForcesMsgDeletion, 0, ST_DDA_MAX_NAME_LENGTH);
	memset(warningMsgOnDeletion, 0, ST_DDA_MAX_NAME_LENGTH);
	memset(warningMsgOnDeletionRichText, 0, ST_DDA_MAX_NAME_LENGTH);
	memset(strChatDisclaimer, 0, ST_DDA_MAX_NAME_LENGTH);
	memset(strChatDisclaimerRichText, 0, ST_DDA_MAX_NAME_LENGTH);

	char buf[ST_DDA_MAX_NAME_LENGTH];
	memset(buf, 0, ST_DDA_MAX_NAME_LENGTH);

	if(parseConfigStr(CL_BAN_CHAT_BETWEEN_USERS, 
					banChatFrom, banChatTo)) 
	{
		attempToBanChats = true;
		readConfigStr(CL_BAN_CHAT_WARNING_MSG, banChatWarning);
	}

	if(parseConfigStr(CL_REPLACE_STRINGS_IN_MSG, 
					strToBeReplaced, strToReplace))
	{
		attempToReplaceSubstrings = true;
		readConfigStr(CL_REPLACE_STRINGS_IN_MSG_WARNING, 
					  strReplacedWarning);

		// read the rich text message version as well
		readConfigStr(CL_REPLACE_STRINGS_IN_MSG_WARNING_RICH_TEXT, 
						strReplacedWarningRichText);  
	}

	if(readConfigStr(CL_DELETE_MSG_WITH_STRING, 
						strForcesMsgDeletion)) 
	{
		attempToDeleteMsgs = true;
		readConfigStr(CL_WARNING_ON_DELETED_MSG, warningMsgOnDeletion);
		
		// read the rich text message version as well
		readConfigStr(CL_WARNING_ON_DELETED_MSG_RICH_TEXT, 
						warningMsgOnDeletionRichText);
	}

	if(readConfigStr(CL_CHAT_START_DISCLAIMER, strChatDisclaimer)) {
		attempToSendChatDisclaimer = true;

		// read the rich text message version as well
		readConfigStr(CL_CHAT_START_DISCLAIMER_RICH_TEXT, 
						strChatDisclaimerRichText);
	}

}// end ChatResource::readSrvMsgFlags

// *****************************************************************
// DESCRIPTION: ChatResource::readConfigStr
// Read Configuration flag from the chatlogging.ini file
// *****************************************************************
bool ChatResource::readConfigStr(const char* configKey, char* buf)
{
	bool result = false;

	char tmp[ST_DDA_MAX_NAME_LENGTH];
	memset(tmp, 0, ST_DDA_MAX_NAME_LENGTH);

	int len = GetPrivateProfileString(CHAT_LOGGING_SECTION, configKey,
							0, tmp, ST_DDA_MAX_NAME_LENGTH, iniFile);
	if(len > 0) {
		result = true;
		strncpy(buf, tmp, len);
	}
	return result;
}// end ChatResource::readConfigStr

// *****************************************************************
// DESCRIPTION: ChatResource::parseConfigStr
// Parse configuration string into the left and the right parts 
// according to the delimiter, which can be comma, or comma with quotes
// *****************************************************************
bool ChatResource::parseConfigStr(const char* configKey, 
										char* left, 
										char* right)
{
	bool result = false;
	char tmp[ST_DDA_MAX_NAME_LENGTH];
	memset(tmp, 0, ST_DDA_MAX_NAME_LENGTH);

	if(readConfigStr(configKey, tmp)) {
		char comma[] = ",";
		char quotedComma[] = "\",\"";
		char* delim = quotedComma;
    
		//GetPrivateProfileString() which is used in
		//readConfigStr(configKey, tmp) does not remove opening and closing
		//quotes from read string in unix. quotes need to be remove manually.
#ifdef _UNIX
		if (tmp[0]=='"'){
			char t[ST_DDA_MAX_NAME_LENGTH];
			memset(t, 0, ST_DDA_MAX_NAME_LENGTH);
			strncpy (t,tmp+1,strlen(tmp)-1);
			strncpy (tmp,t,strlen(t));
			tmp[strlen(t)]='\0';
		}
		if (tmp[strlen(tmp)-1]=='"'){
			tmp[strlen(tmp)-1]='\0';
		}
#endif
		// try to find position of ","; if not found, try the regular 
		// comma as delimiter
		char* delimStart = strstr(tmp, delim);
		if(delimStart == NULL) {
			delim = comma;
			delimStart = strstr(tmp, delim);
		}

		if(delimStart != NULL) {
			// start of delimiter - start of buffer
			int strLen = delimStart - tmp ;
			strncpy(left, tmp, strLen);
			strLen = strlen(tmp) - strLen - strlen(delim);
			if(strLen > 0) {
				strncpy(right, delimStart + strlen(delim), strLen);
			}
			else {
				return result;
			}
			result = true;
		}
	}
	return result;
}// end ChatResource::readConfigStr

// *****************************************************************
// DESCRIPTION: ChatResource::createDefaultPath
// This function creates the default path for session files and 
// the path for the chatlogging.ini file and the trace file.
// *****************************************************************
void ChatResource::createDefaultPath()
{
	if (defaultPath[0] == '\0') 
	{
		char s[ST_DDA_MAX_NAME_LENGTH];
		char szFilename[MESSAGE_SIZE];
		// 21aug2002 - just get server data directory
#if defined(_UNIX)
		GetDataDirectory(szFilename);
		strcat(szFilename, "/");
		strcpy(defaultPath, szFilename);
#else
		DWORD mod = GetModuleFileName(0, szFilename, 
									  sizeof(szFilename));
		if (!mod)
			return ;

		strcpy(s,szFilename);
		char* i = strrchr(s,'\\');

		if(i == NULL)
			strcpy(s,'\0');
		else
		{
			strncpy(defaultPath,s,(i - s + 1));
			defaultPath[i - s + 1 ]='\0';
		}
		if (s[0] == '\0')
		{
			strcpy(defaultPath,"./");
			defaultPath[2]='\0';
		}

#endif  
		if( iniFile[0] == '\0')
		{
			strcpy(iniFile,defaultPath);
			strcat(iniFile,CL_INI_FILE_NAME);
		}

		if( tracePath[0] == '\0')
		{
			strcpy(tracePath,defaultPath);
			strcat(tracePath,CL_TRACE_FILE_NAME);
		}

		strcat(defaultPath,DEFAULT_LIB_PATH);

	}

	return;
} // end ChatResource::createDefaultPath

// *****************************************************************
// DESCRIPTION: ChatResource::getDefaultPath
// This function returns the default path.
// If the default path does not exist, it is created first.
// *****************************************************************
const char* ChatResource::getDefaultPath(void)
{
	if( defaultPath[0] == '\0')
		createDefaultPath();
	return defaultPath;

} // end ChatResource::getDefaultPath

// *****************************************************************
// DESCRIPTION: ChatResource::createControlPath
// This function returns the control path where the session files are
// created. If the control path does not exist, it is created first.
// *****************************************************************
void ChatResource::createStrControlPath()
{
	createDefaultPath();
	char buf[256] = "";
	GetPrivateProfileString(LIBRARY_SECTION,CL_LIBRARY_PATH,0, 
											buf, 256, iniFile);
	strcpy(controlPath,buf);
	return;
} // end ChatResource::createControlPath

// *****************************************************************
// DESCRIPTION: ChatResource::getControlPath
// *****************************************************************
const char* ChatResource::getStrControlPath()
{
	if (controlPath[0] == '\0') 
		createStrControlPath();
	return controlPath;
} // end ChatResource::getControlPath

// *****************************************************************
// DESCRIPTION: ChatResource::getDebugFlagValue
// This function returns the debug flag value.
// *****************************************************************
int ChatResource::getDebugFlagValue(const char* id)
{
	return GetPrivateProfileInt(DEBUG_SECTION, id, 0, iniFile);

} // end ChatResource::getDebugFlagValue


// *****************************************************************
// DESCRIPTION: ChatResource::print
// This function creates the debug message and writes it to the 
// trace file.
// *****************************************************************
void ChatResource::print(int flag,const char* format, ...)
{
	if(flag)
	{
		char msg[MESSAGE_SIZE];

		sprintf(msg, "%-20.20s %s ",CL_PREFIX_NAME, getCurTime());
#ifdef _WIN32
		wvsprintf(msg+strlen(msg), format, (LPSTR)(&format+1));
#else
		// Feb 28 2002
		va_list args;
		va_start( args, format );
		(void) vsprintf( msg+strlen( msg ), format, args );
		va_end( args );
#endif

		FILE* const traceFile = fopen(tracePath, "a+");
		if(traceFile)
		{
			fprintf(traceFile, "%s\n", msg);
			fflush(traceFile);
			fclose(traceFile);
		}
	}

	return;
} // end ChatResource::print

// *****************************************************************
// DESCRIPTION: ChatResource::getCurTime
// This function creates and returns the current time string
// *****************************************************************
const char* ChatResource::getCurTime(void)
{
	static char buf[30];
	time_t t = time(0);	

	{
		STTS_GUARD();
		strftime(buf, 30,"%d/%b/%y, %H:%M:%S",localtime(&t));
	}

	return buf;
} // end ChatResource::getCurTime