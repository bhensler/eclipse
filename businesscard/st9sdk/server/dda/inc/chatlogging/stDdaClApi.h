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

#ifndef __STDDACLAPI_H__
#define __STDDACLAPI_H__

#include <stDdaApiDefs.h>

#define ST_DDA_CL_LIB_VERSION    16

#define ST_DDA_NO_ACTIVITY_TYPE    0

// *****************************************************************
// Common structures are use by the SPI methods
// *****************************************************************

// *****************************************************************
// StClEntityType
// This enumerator defines the different types of participants
// *****************************************************************
typedef enum {
  ST_DDA_CL_NO_TYPE,
  ST_DDA_CL_USER,
  ST_DDA_CL_SECTION,
  ST_DDA_CL_ACTIVITY,
  ST_DDA_CL_SESSION,
  ST_DDA_CL_EXTERNAL_USER,
  ST_DDA_CL_OFFLINE_USER
} StClEntityType;

// *****************************************************************
// StClSrvMsgDestination
// This enumerator defines different types of server message destination
// *****************************************************************
typedef enum {
  ST_DDA_CL_SENDER,
  ST_DDA_CL_RECEIVER
} StClSrvMsgDestination;

// *****************************************************************
// StClSrvMsgType
// This enumerator defines different types of server messages
// *****************************************************************
typedef enum {
  ST_DDA_CL_TEXT_MSG,
  ST_DDA_CL_DATA_MSG,
  ST_DDA_CL_SRV_MSG
} StClSrvMsgType;

// *****************************************************************
// StDdaClEntity
// The following structure describes a participant in a chat session
// *****************************************************************
struct StDdaClEntity {
  char id[ST_DDA_MAX_NAME_LENGTH];
  StClEntityType type;
};

// *****************************************************************
// StDdaClSrvMsg
// This structure defines the server message
// *****************************************************************
struct StDdaClSrvMsg {
  StClSrvMsgType srvMsgType;
  StClSrvMsgDestination srvMsgDestination;
  unsigned long msgLen;
  char* msg;
};

extern "C" {
int ST_DDA_API
stDdaClInit( /*in/out*/  int* prVersion,
             /*in*/     int  initializedOutside,
             /*out*/	   int* initializedInside,
             /*out*/    char* dirType,
             /*out*/    char* libVersion,
             /*out*/    StDdaRetVal* appRetCode,
             /*out*/    char* appRetMsg);

void ST_DDA_API
stDdaClTerminate();

void ST_DDA_API
stDdaClTimerEvent(void);

int ST_DDA_API
stDdaClSessionStarted(/*in*/ const char* sessionId);

int ST_DDA_API
stDdaClSessionStartedByOrgName(/*in*/ const char* sessionId,
                               /*in*/ const char* organization);

int ST_DDA_API
stDdaClSessionEnded(/*in*/ const char* sessionId);

int ST_DDA_API
stDdaClJoiningSession(/*in*/ const char* sessionId, 
                           /*in*/ const StDdaClEntity *entity,
                           /*in*/ const StDdaClEntity *scope);

int ST_DDA_API
stDdaClLeavingSession(/*in*/ const char* sessionId, 
                         /*in*/ const StDdaClEntity *entity);

int ST_DDA_API
stDdaClSessionMsg(/*in*/ const char* sessionId, 
                /*in*/ const StDdaClEntity *sender, 
                /*in*/ unsigned long msgLen, 
                /*in*/ const char *msg, 
                /*in*/ const StDdaClEntity *receiver);

int ST_DDA_API
stDdaClSessionDataMsg(/*in*/ const char* sessionId, 
                /*in*/ const StDdaClEntity *sender, 
                /*in*/ unsigned long dataType, 
                /*in*/ unsigned long dataSubType, 
                /*in*/ unsigned long msgLen, 
                /*in*/ const char *msg, 
                /*in*/ const StDdaClEntity *receiver);

int ST_DDA_API
stDdaClSessionPolicycheckFunc(/*in*/ const StDdaClEntity *entity,
                /*in*/ const StDdaClEntity *toEntity,
                /*out*/ StDdaClSrvMsg** srvMessages,
                /*out*/ unsigned long* srvMessagesLen);
                
                
int ST_DDA_API
stDdaClJoiningSessionWithSrvMsg(/*in*/ const char* sessionId, 
                           /*in*/ const StDdaClEntity *entity,
                           /*in*/ const StDdaClEntity *scope,
                           /*out*/ StDdaClSrvMsg** srvMessages,
                           /*out*/ unsigned long* srvMessagesLen);

int ST_DDA_API
stDdaClLeavingSessionWithSrvMsg(/*in*/ const char* sessionId, 
                         /*in*/ const StDdaClEntity *entity,
                         /*out*/ StDdaClSrvMsg** srvMessages,
                         /*out*/ unsigned long* srvMessagesLen);

int ST_DDA_API
stDdaClSessionMsgWithSrvMsg(/*in*/ const char* sessionId, 
                /*in*/ const StDdaClEntity *sender, 
                /*in*/ unsigned long msgLen, 
                /*in*/ const char *msg, 
                /*in*/ const StDdaClEntity *receiver,
                /*out*/ StDdaClSrvMsg** srvMessages,
                /*out*/ unsigned long* srvMessagesLen);

int ST_DDA_API
stDdaClSessionDataMsgWithSrvMsg(/*in*/ const char* sessionId, 
                /*in*/ const StDdaClEntity *sender, 
                /*in*/ unsigned long dataType, 
                /*in*/ unsigned long dataSubType, 
                /*in*/ unsigned long msgLen, 
                /*in*/ const char *msg, 
                /*in*/ const StDdaClEntity *receiver,
                /*out*/ StDdaClSrvMsg** srvMessages,
                /*out*/ unsigned long* srvMessagesLen);

int ST_DDA_API
stDdaClCleanSrvMsgs(StDdaClSrvMsg* srvMessages,
                unsigned long srvMessagesLen);

} /* end of extern C */

#endif //__STDDACLAPI_H__
