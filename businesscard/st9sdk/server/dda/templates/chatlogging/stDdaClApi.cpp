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


#include "stDdaClCodes.h"

#include <stdio.h>
#include <string.h>
#include <stDdaClApi.h>

#define VP_CL_VERSION		"9.0.0"

char *globalRetMsg = 0;
StDdaRetVal *globalRetCode = 0;


// *****************************************************************
// DESCRIPTION: stDdaClInit
// *****************************************************************
int ST_DDA_API
stDdaClInit( /*in/out*/  int* prVersion,
             /*in*/     int  initializedOutside,
             /*out*/	   int* initializedInside,
             /*out*/    char* dirType,
             /*out*/    char* libVersion,
             /*out*/    StDdaRetVal* appRetCode,
             /*out*/    char* appRetMsg)
{
	if (dirType && libVersion) {
		strcpy(dirType, ST_DDA_API_DUMMY_TYPE);
		strcpy(libVersion, VP_CL_VERSION);
	}
	// init debug mechanism

  globalRetCode = appRetCode;
	globalRetMsg = appRetMsg;
	*globalRetCode = ST_DDA_OK;

	if (*prVersion < ST_DDA_CL_LIB_VERSION) 
  {
		*globalRetCode = ST_DDA_ERROR;
		sprintf(globalRetMsg, "Version  - %d is not supported", *prVersion);
		return ST_DDA_API_VERSION_MISMATCH;
	}
	*prVersion = ST_DDA_CL_LIB_VERSION;

	(*initializedInside) |= initializedOutside;
	return ST_DDA_API_OK;
} // end stDdaClInit



// *****************************************************************
// DESCRIPTION: stDdaClTerminate
// *****************************************************************
void ST_DDA_API
stDdaClTerminate()
{

} // end stDdaClTerminate



// *****************************************************************
// DESCRIPTION: stDdaClTimerEvent
// *****************************************************************
void ST_DDA_API
stDdaClTimerEvent(void)
{
} // end stDdaClTimerEvent

int ST_DDA_API
stDdaClSessionStarted(/*in*/ const char* sessionId)
{
	return ST_DDA_API_OK;

} 

// *****************************************************************
// DESCRIPTION: stDdaClSessionStartedByOrgName
// *****************************************************************
int ST_DDA_API
stDdaClSessionStartedByOrgName(/*in*/ const char* sessionId,
                               /*in*/ const char* organization)
{
	return ST_DDA_API_OK;

} // end stDdaClSessionStartedByOrgName


// *****************************************************************
// DESCRIPTION: stDdaSessionEnded
// *****************************************************************
int ST_DDA_API stDdaClSessionEnded(const char* sessionId)
{
	return ST_DDA_API_OK;
} // end stDdaSessionEnded

// *****************************************************************
// DESCRIPTION: stDdaClSessionPolicycheckFunc
// *****************************************************************
int ST_DDA_API
stDdaClSessionPolicycheckFunc(/*in*/ const StDdaClEntity *entity,
                              /*in*/ const StDdaClEntity *toEntity,
                              /*out*/ StDdaClSrvMsg** srvMessages,
                              /*out*/ unsigned long* srvMessagesLen)
{
  return ST_DDA_API_OK;
} // end stDdaClSessionPolicycheckFunc


// *****************************************************************
// DESCRIPTION: stDdaClUserJoinedSession
// *****************************************************************
int ST_DDA_API stDdaClJoiningSession(/*in*/ const char* sessionId, 
										   /*in*/ const StDdaClEntity *entity,
										   /*in*/ const StDdaClEntity *scope)
{
	return ST_DDA_API_OK;
} // end stDdaClUserJoinedSession


// *****************************************************************
// DESCRIPTION: stDdaClUserLeftSession
// *****************************************************************
int ST_DDA_API stDdaClLeavingSession(/*in*/ const char* sessionId, 
										/*in*/ const StDdaClEntity *entity)
{

  return ST_DDA_API_OK;
} // end stDdaClUserLeftSession



// *****************************************************************
// DESCRIPTION: stDdaSessionMsg
// *****************************************************************
int ST_DDA_API
stDdaClSessionMsg(/*in*/ const char* sessionId, 
                /*in*/ const StDdaClEntity *sender, 
                /*in*/ unsigned long msgLen, 
                /*in*/ const char *msg, 
                /*in*/ const StDdaClEntity *receiver)
{
return ST_DDA_API_OK;
} // end stDdaSessionMsg



// *****************************************************************
// DESCRIPTION: stDdaSessionMsg
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
return ST_DDA_API_OK;
} // end stDdaClSessionDataMsg

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
return ST_DDA_API_OK;
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
return ST_DDA_API_OK;
} // end stDdaClLeavingSessionWithSrvMsg

// *****************************************************************
// DESCRIPTION: stDdaClSessionMsgWithSrvMsg
// *****************************************************************
int ST_DDA_API
stDdaClSessionMsgWithSrvMsg(/*in*/ const char* sessionId, 
                /*in*/ const StDdaClEntity *sender, 
                /*in*/ unsigned long msgLen, 
                /*in*/ const char *msg, 
                /*in*/ const StDdaClEntity *receiver,
                /*out*/ StDdaClSrvMsg** srvMessages,
                /*out*/ unsigned long* srvMessagesLen)
{
return ST_DDA_API_OK;
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
return ST_DDA_API_OK;
} // end stDdaClSessionDataMsgWithSrvMsg

// *****************************************************************
// DESCRIPTION: stDdaClCleanSrvMsgs
// *****************************************************************
int ST_DDA_API
stDdaClCleanSrvMsgs(StDdaClSrvMsg* srvMessages,
                unsigned long srvMessagesLen)
{
return ST_DDA_API_OK;
} // end stDdaClCleanSrvMsgs
