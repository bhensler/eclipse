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

#ifndef __STAUTHTOKENAPI_H__
#define __STAUTHTOKENAPI_H__

/* File: stAuthTokenApi.h */

#include <stDdaApiDefs.h>
#include <stAuthTokenData.h>


/*****************************************************************************
*
* ST Token Authentication interface
*
*****************************************************************************/
/*
 This constant should be in use by the calling application
 if the DLL is dynamically searched by the application during
 run-time (rather than statically linking with its export
 library (stub library). This, of course, is only relevant for
 the Windows environment.
*/

// KSE 6/29/2001
#ifdef OS400
#define ST_TOKEN_AUTH_LIB_NAME "StAuthTkn"
#else
#define ST_TOKEN_AUTH_LIB_NAME "StAuthToken"
#endif

extern "C"
{
int ST_DDA_API
stVerifyToken( /*[in]*/   const char* loginName,
               /*[in]*/   const char* token,
               /*[in]*/   int tokenLength ) ;

int ST_DDA_API
stGetToken( /*[in]*/   const char* userId,
            /*[out]*/  char* token,
            /*[out]*/  int* tokenLength ) ;

// tokenArr should be already initialized with tokenArrSize TokenData objects
int ST_DDA_API
stGetTokens( /*[in]*/   const char* userId,
			 /*[out]*/  TokenData tokenArr[],
			 /*[in]*/   const unsigned int &tokenArrSize,
			 /*[out]*/  unsigned int &numOfTokens) ;

int ST_DDA_API
stTokenInit( /*in*/  int  initializedOutside,
             /*out*/ int* initializedInside ) ;

void ST_DDA_API
stTokenTerminate() ;

int ST_DDA_API
stVerifyTokenAndExtractUserId(/*[in]*/ const TokenData tokenArr[],
							  /*[in]*/ const unsigned int &numOfTokens,
                              /*[in/out]*/ char *userId);

} /* end of extern "C" */

/* End of file stAuthTokenApi.h */

#endif //__STAUTHTOKENAPI_H__
