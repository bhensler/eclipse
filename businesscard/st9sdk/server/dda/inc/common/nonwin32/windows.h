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


/*-------------------------------------------------------------------*/
/* File: windows.h                                                   */
/*                                                                    */
/*-------------------------------------------------------------------*/

#include <stdio.h>
#include <stdlib.h>
/* various defs needed */
#ifndef WIN32
#define LPCSTR const char *
#define LPSTR  char *
#define DWORD int
#define UINT unsigned int
#define INT int
#define WINAPI
#define BOOL int
#define TRUE 1
#define FALSE 0
#define strncasecmp  strnicmp 
#define MAX_PATH 1024
#define NULL 0
#define DIRECTORY_SEP "/"
#define PATH_SEP ":"
#define _MAX_PATH MAX_PATH
typedef unsigned long  HINSTANCE;
typedef void * LPVOID;

#ifdef NO_NAMESPACE
//  - add defines for various namespaces as needed
#define Aw
#define USING_NAMESPACE(x)
#define NAMESPACE_BEGIN(x)
#define NAMESPACE_END
#else
#define USING_NAMESPACE(x) using namespace x;
#define NAMESPACE_BEGIN(x) namespace x {
#define NAMESPACE_END }
#endif 
#endif


 
/* Get/WritePrivateProfile Functions */
UINT WINAPI

GetPrivateProfileInt (

	LPCSTR lpAppName,

	LPCSTR lpKeyName,

	INT nDefault,

	LPCSTR lpFileName

	);


DWORD WINAPI

GetPrivateProfileString (

	LPCSTR lpAppName,

	LPCSTR lpKeyName,

	LPCSTR lpDefault,

	LPSTR lpReturnedString,

	DWORD nSize,

	LPCSTR lpFileName

	);


BOOL WINAPI

WritePrivateProfileString (

	LPCSTR lpAppName,

	LPCSTR lpKeyName,

	LPCSTR lpString,

	LPCSTR lpFileName

	);


DWORD WINAPI

GetPrivateProfileSection (

	LPCSTR lpAppName,

	LPSTR lpReturnedString,

	DWORD nSize,

	LPCSTR lpFileName

	);


BOOL WINAPI

WritePrivateProfileSection (

	LPCSTR lpAppName,

	LPCSTR lpString,

	LPCSTR lpFileName

	);


DWORD WINAPI

GetPrivateProfileSectionNames (

	LPSTR lpszReturnBuffer,

	DWORD nSize,

	LPCSTR lpFileName

	);


// added new function to write out just the section name

BOOL WINAPI

WritePrivateProfileSectionName (

	LPCSTR lpAppName,

	LPCSTR lpFileName

	);


// added new function to key from ini file

BOOL WINAPI

RemovePrivateProfileString (

	LPCSTR lpAppName,

	LPCSTR lpKeyName,

	LPCSTR lpFileName

	);

