/*
 * Licensed Materials - Property of IBM
 *
 * L-MCOS-96LQPJ
 *
 * (C) Copyright IBM Corp. 2004, 2013. All rights reserved.
 *
 * US Government Users Restricted Rights- Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

/* File: stAuthTokenApi.cpp */

#include <windows.h>
#include <stdio.h>
#include <time.h>
#ifdef UNIX_TOOLKIT_COMPILE
#include <utilities.h>
#endif

#include <stAuthTokenApi.h>

//Last include, to catch all required platform #ifdef'ed code
#include <StThreadSafe.h>


#if defined(_WIN32) || defined(_UNIX)
BOOL WINAPI DllMain(HINSTANCE hinstDLL, DWORD fdwReason, LPVOID lpvReserved )
{
  return TRUE;
}
#endif

#define MESSAGE_SIZE                  2048
#define MSG_PREFIX                    "AuthToken"


// *****************************************************************
// DESCRIPTION: printMsg
// *****************************************************************
void printMsg(const char* format, ...)
{
  static char tracePath[ST_DDA_MAX_NAME_LENGTH];
  if (tracePath[0] == '\0') {
#ifdef WIN32
    char szFilename[_MAX_PATH];
    DWORD mod = GetModuleFileName(0, szFilename, sizeof(szFilename));
    if (!mod)
#else
    char szFilename[MAX_PATH];
    int mod = GetDataDirectory(szFilename);	
    if (mod)	// 0 is good
#endif
      return;
    strcpy(tracePath, szFilename);
#ifdef WIN32
    char* p = strrchr(tracePath, '\\');
    if (!p)
      return;
    strcpy(p, "\\trace\\StAuthToken.txt");
#else
    strcat(tracePath, "/trace/StAuthToken.txt");
#endif
  }

  char msg[MESSAGE_SIZE];

  char timeBuf[30];
  time_t t = time(0);

  {
    STTS_GUARD();
    strftime(timeBuf, 30,"%d/%b/%y, %H:%M:%S",localtime(&t));
  }

  sprintf(msg, "%-20.20s %s ",MSG_PREFIX, timeBuf);
#ifdef WIN32
  wvsprintf(msg+strlen(msg), format, (LPSTR)(&format+1));
  FILE* traceFile = fopen(tracePath, "a+");
#else
  vsprintf(msg+strlen(msg), format, (char **)(&format+1));
  FILE* traceFile = fopen(tracePath, "ab+");
#endif


  if(traceFile) {
    fprintf(traceFile, "%s\n", msg);
    fflush(traceFile);
    fclose(traceFile);
  }

} // end printMsg


int initializationsDoneHere = 0 ;

// *****************************************************************
// DESCRIPTION: stTokenInit
// *****************************************************************
int ST_DDA_API
stTokenInit( int initializedOutside,
             int* initializedInside )
{
  printMsg( "initializing token authentication module" ) ;
  return ST_DDA_API_OK ;
} // end stTokenInit

// *****************************************************************
// DESCRIPTION: stTokenTerminate
//
// terminate the Notes API if necessary
// *****************************************************************
void ST_DDA_API
stTokenTerminate()
{
  printMsg( "terminating token authentication module" ) ;
} // end stTokenTerminate

// *****************************************************************
// DESCRIPTION: stVerifyToken
// *****************************************************************
int ST_DDA_API
stVerifyToken( const char* loginName,
               const char* token,
               int tokenLength )
{
  return ST_DDA_API_OK ;
} // end stVerifyToken

// *****************************************************************
// DESCRIPTION: stGetToken
// *****************************************************************
int ST_DDA_API
stGetToken(const char* userId, char* token, int* tokenLength)
{
  return ST_DDA_API_OK ;
} // end stGetToken


int ST_DDA_API
stVerifyTokenAndExtractUserId(/*[in]*/const char* token,
                              /*[out]*/char* resultBuff,
                              /*[in]*/int resBuffMaxLen
						 )
{
  return ST_DDA_API_OK ;
}

int ST_DDA_API
stGetTokens( /*[in]*/   const char* userId,
			 /*[out]*/  TokenData tokenArr[],
			 /*[in]*/   const unsigned int &tokenArrSize,
			 /*[out]*/  unsigned int &numOfTokens)
{
 return ST_DDA_API_OK ;
}

/* End of file stAuthTokenApi.cpp */

