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

/*-------------------------------------------------------------------*/
/* File: utilities.h                                                 */
/*-------------------------------------------------------------------*/
#if !defined(_UTILITIES_H_)
#define _UTILITIES_H_
#ifdef OS390
#include "windows.h"
#else
#include <windows.h>
#include <unistd.h>
#endif

extern "C" { 

/*--------------------------------------------------------------------*/
/* FileExists                                                         */
/*     Checks if the given file name exists or not and if it does it  */
/*     will also return if it is a directory or not if you care.      */
/* PARAMS:                                                            */
/*     file - (in) pointer to the file to check for                   */
/*     isDir - (out) optional, may be NULL.  Pointer to int that will */
/*             be set to non-0 if the file is really a directory.     */
/*                                                                    */
/* RETURNS:                                                           */
/*     Non-0 - if the file exists                                     */
/*     0 if the file does not exist.                                  */
/*                                                                    */
/*--------------------------------------------------------------------*/
int FileExists(const char *file, int *isDir=NULL);


/*-------------------------------------------------------------*/
/* FindFileInPath                                              */
/* Finds the given file in either the current directory        */
/* or the PATH.  It first checks the current dir and if        */
/* doesn't find it there, it will walk the PATH looking        */
/* for that file.                                              */
/*                                                             */
/* Returns 0 if Successful and qualifiedFilePath will          */
/* contain the file name and path.                             */
/*                                                             */
/*     PARAMS:                                                 */
/*         file - (in) pointer to file to find.                */
/*         qualifiedFilePath - (out) pointer to buffer to hold */
/*               the qualified file name if we find it.        */
/*                                                             */
/*     RETURNS:                                                */
/*         0 if succesful                                      */
/*         non-0 ERROR code if we cannot find it or have error */
/*-------------------------------------------------------------*/
int FindFileInPath(const char *file, char *qualifiedFilePath);

/*---------------------------------------------------------------------*/
/* GetDataDirectory                                                    */
/* This routine returns the DataDirectory used by our Server.          */
/* For either a stand-alone or a overlayed SameTime server there is    */
/* always a notes.ini file for the Domino code. This Notes.ini will    */
/* contain a Directory= entry to point to the servers data directory.  */
/* We will look for the notes.ini first in the current dir and then    */
/* via the PATH environment variable.  If we find notes.ini, we look   */
/* at it for the Directory= statement to determine our data directory. */
/*     PARAMS:                                                         */
/*         outDataDirectory - (out) pointer to buffer to hold data dir */
/*     RETURNS:                                                        */
/*         0 if successful                                             */
/*         non-0 ERROR code if we fail                                 */
/*                                                                     */
/*     Notes:                                                          */
/*         outDataDirectory should be MAX_PATH big (1024)              */
/*---------------------------------------------------------------------*/
int GetDataDirectory(char *outDataDirectory);


#ifdef OS400
int getDominoRunPath(char *runPath);
#endif

} 
#endif
