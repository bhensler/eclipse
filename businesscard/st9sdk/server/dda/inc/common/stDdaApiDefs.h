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

#ifndef _STDDAAPIDEFS_H_
#define _STDDAAPIDEFS_H_

//*****************************************************************************
//*
//* ST Directory & Database Access API - general definitions
//*
//*****************************************************************************

//* for Windows definitions of API
#ifdef _WIN32
# define ST_DDA_API      __stdcall
# define ST_DDA_CALLBACK __stdcall
#else
# define ST_DDA_API
# define ST_DDA_CALLBACK
#endif

#define ST_DDA_MAX_NAME_LENGTH        256
#define ST_DDA_MAX_ID_LENGTH          256
#define ST_DDA_MAX_DESCRIPTION_LENGTH 256
#define ST_DDA_MAX_STR_LEN            1024

//*
//* definitions of APIs that need initialization
//* to help coordinate between separate DLLs
//*

#define ST_DDA_API_SYBASE    0x0001
#define ST_DDA_API_NOTES     0x0002
#define ST_DDA_API_LDAP      0x0004
#define ST_DDA_API_DB2       0x0008

#define ST_DDA_API_DUMMY_TYPE "dummy"
#define ST_DDA_API_FILE_TYPE  "file"
#define ST_DDA_API_NOTES_TYPE "notes"
#define ST_DDA_API_DB2_TYPE   "db2"
#define ST_DDA_API_LDAP_TYPE  "ldap"
#define ST_DDA_API_URS_TYPE   "urs"

#define ST_DDA_API_OK                   0x0000
#define ST_DDA_API_VERSION_MISMATCH     0x0001
#define ST_DDA_API_INVALID_PARAM        0x0002
#define ST_DDA_API_INTERNAL_ERROR       0x0003
#define ST_DDA_API_NOT_SUPPORTED        0x0004
#define ST_DDA_API_IGNORE_REQUEST		0x0005
#define ST_DDA_API_SSO_ERROR			0x0006
#define ST_DDA_USER_NOT_UNIQUE	        0x0007
#define ST_DDA_DIR_BAD_CONFIG			0x0008
#define ST_DDA_BAD_SEARCH_FILTER		0x0009

enum StDdaRetVal { 
  ST_DDA_OK, 
  ST_DDA_INFO, 
  ST_DDA_WARNING, 
  ST_DDA_ERROR, 
  ST_DDA_FATAL 
  };

/* Global variables to handle general DLL retern value & logging */
extern StDdaRetVal stDdaRetVal;
extern char stDdaRetStr[ST_DDA_MAX_STR_LEN] ;

/* Few macros to ease handleing of these global variables */
#define ST_DDA_RESET_RET_VARS { stDdaRetVal = ST_DDA_OK; stDdaRetStr[0] = 0; }
#define ST_DDA_RETURN_OK      stDdaRetVal = ST_DDA_OK;
#define ST_DDA_RETURN_INFO    stDdaRetVal = ST_DDA_INFO;
#define ST_DDA_RETURN_WARNING stDdaRetVal = ST_DDA_WARNING;
#define ST_DDA_RETURN_ERROR   stDdaRetVal = ST_DDA_ERROR;
#define ST_DDA_RETURN_FATAL   stDdaRetVal = ST_DDA_FATAL;
#define ST_DDA_RETURN_STR(s)  { \
    strncpy(stDdaRetStr, s, ST_DDA_MAX_STR_LEN-1); \
    stDdaRetStr[ST_DDA_MAX_STR_LEN-1] = 0; \
  }

#endif //_STDDAAPIDEFS_H_
