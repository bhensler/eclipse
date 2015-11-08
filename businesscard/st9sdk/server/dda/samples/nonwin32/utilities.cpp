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
/* File: utilities.cpp                                               */
/*-------------------------------------------------------------------*/


#include <pthread.h>		
#include <assert.h>
#include "debug.h"
#include <sys/stat.h>
#include <stdlib.h>
#include <windows.h>
#include "utilities.h"

#define DIRECTORY_SEP "/"
#define PATH_SEP ":"


#ifdef OS400
#include "QNNINLDS.H"
#include <qusec.h>
#endif


//ST_Linux_Port_Level0
#if defined(AIX) || defined(SOLARIS) || defined(LINUX)
#include <unistd.h>
#endif

//Last include, to catch all required platform #ifdef'ed code
#include <StThreadSafe.h>


#if defined(OS400) || defined(OS390) 
extern "C" {
#endif

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
int FileExists(const char *file, int *isDir)
{
	int rc = 0;
	struct stat fileInfo;

	assert(file);	

	/* use stat (or stat64) to check for file */
	if (!stat(file, &fileInfo))
	{
		rc = 1;
		if (isDir)
		{
			if (S_ISDIR(fileInfo.st_mode))
			{
				*isDir = 1;
			}
			else
			{
				*isDir = 0;
			}
		}
	}
	return rc;
}


/*-------------------------------------------------------------*/
/* FindFileInPath                                              */
/* Finds the given file in either the current directory        */
/* or the PATH.  It first checks the current dir and if        */
/* doesn't find it there, it will walk the PATH looking        */
/* for that file.                                              */
/* if /xxx try finding based on root too                       */
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
int FindFileInPath(const char *file, char *qualifiedFilePath)
{
	int rc = -1;
	char buf[(MAX_PATH*4)];
	char cwd[(MAX_PATH*4)];
	char *dir;
	int	leadingSep = 0;

	if (NULL != qualifiedFilePath)
	{
		qualifiedFilePath[0] = 0;
	}

//	Debug debug1("FindFileInPath");
//	debug1.notify("entry... [%s] MAX_PATH:%d", file, (MAX_PATH*4));

	// first check for leading / and try to find based on root
	if(file[0] == DIRECTORY_SEP[0])
	{
		leadingSep = 1;
		if (FileExists(file, NULL))
		{
			if (NULL != qualifiedFilePath)
			{
				strcpy(qualifiedFilePath, file);
				rc = 0;	// found it
			}
			return rc;
		}
	}

	/* check the current directory ... */
	cwd[0] = 0;
	dir = getcwd(cwd, sizeof(cwd));
	if (!dir)
	{
		Debug debug("FindFileInPath");
		debug.error("getcwd errno:%d", errno);
	}
	else
	{
		char  path_buf[2048];
		char *path = sttsGetenv_r("PATH", path_buf, sizeof(path_buf));
		if (path)
		{
			strcat(cwd, PATH_SEP);
			if (strlen(path) < (MAX_PATH*4))
			{
				strcat(cwd, path);
			}
			else
			{
				Debug debug("FindFileInPath");
				debug.error("cwd path length exceeded... actual:%d  min:%d",
					strlen(path), (MAX_PATH*4));
				return rc;
			}
		}
		dir = strtok(cwd, PATH_SEP);

		while (rc && dir)
		{
			if (NULL != dir)
				strcpy(buf, dir);

			// JM 20000906: FIX: changed to strlen(buf) - 1
//			if (buf[strlen(buf)] != DIRECTORY_SEP[0])
			if ((buf[strlen(buf) - 1] != DIRECTORY_SEP[0]) && !leadingSep)
			{
				strcat(buf, DIRECTORY_SEP);
			}

			strncat(buf, file, sizeof(buf));

			if (FileExists(buf, NULL))
			{
				if (NULL != qualifiedFilePath)
				{
					strcpy(qualifiedFilePath, buf);
					rc = 0;	// found it
					dir = NULL;
				}
			}
			else
			{
				dir = strtok(NULL, PATH_SEP);	// call with NULL to get each subsequent token
				if (NULL == dir)
				{
					Debug debug("FindFileInPath");
					debug.notify("couldn't find [%s] in path or current dir...", file);
					qualifiedFilePath[0] = 0;
				}
			}
		}
	}

	return rc;
}

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
int GetDataDirectory(char * outDataDirectory)
{
	int rc = 0;
	static char szDataDir[MAX_PATH]="";

//	assert(outDataDirectory);
	//****************************************************************
	// Only look it up once...We should be called by the
	// windows unix layer at init time (single threaded at that time)
	// to get it initialized once
	//****************************************************************

	if (0 == strlen(szDataDir))
	{
		char szNotesINI[MAX_PATH]="";

		//*****************
		// Find notes.ini
		//*****************
		if (FindFileInPath("notes.ini", szNotesINI))
		{
			if (FindFileInPath("meetingserver.ini", szNotesINI))
			{
				Debug debug("::GetDataDirectory()");
				debug.error("Cannot find notes.ini or meetingserver.ini in PATH!\n");
				rc = -1;
			}
		}

		if (rc == 0)
		{
			DWORD dwLen;
			dwLen = GetPrivateProfileString("Notes",
						"Directory",
						"/NoDirectory=InNotes.iniFile",
						szDataDir,
						MAX_PATH,
						szNotesINI);

			// who needs this particular check?  which specific OS?
			if (0 == (strcmp(szDataDir,"/NoDirectory=InNotes.iniFile")))
			{
				Debug debug("GetDataDirectory()");
				debug.error("Unable to Determine Data Directory");
				rc = -1;
			}
		}
	}

	if (NULL != outDataDirectory)
	{
		strcpy(outDataDirectory, szDataDir);
	}
	else
	{
		Debug debug("GetDataDirectory()");
		debug.error("destination DIR is NULL - strcpy omitted ");
		rc = -1;
	}

	return rc;
} /* end function */


#if !defined(UNIX_TOOLKIT_COMPILE)
#if defined (EBCDIC_RTL)
#undef inet_ntoa_r				
int	__inet_ntoa_r_a(struct in_addr sockaddr,char * outstring,int outstringlen)
{
	int rc = inet_ntoa_r(sockaddr,outstring,outstringlen);
	__toascii_a(outstring, outstring);
	return(rc);
}
#endif

#ifdef OS400
#undef ftok
/*********************************************************************
*
*	Name     :	__ftok_a
*	Function :	ASCII front-end for ftok
*
*********************************************************************/
key_t __ftok_a(const char *path, int id)
{
	return ftok((const char *) __getEstring1_a(path), id );
}
#endif //OS400
#if defined(OS400) 
#undef strftime
size_t __strftime_a(char *strDest, size_t maxsize, const char *format, const struct tm *timeptr)
{
	char	eformat[200];
	char	*eformatp;
	size_t	lenformat, result;
	lenformat = strlen(format);
	if (lenformat < sizeof(eformat))
	{
		eformatp = &eformat[0];
	}
	else
	{
		eformatp = (char *)malloc(lenformat+1);
	}
	__toebcdic_a(eformatp,format);

	/* call real strftime now */
	result = strftime(strDest, maxsize, eformatp, timeptr);
	if (result)
	{
		/* convert results buffer to ascii */
		__toascii_a(strDest, strDest);
	}

	/* Free temp buffer if one was obtained							*/
	if (lenformat >= sizeof(eformat))
	{
		free(eformatp);
	}

	return result;
} /* strftime_a */

#undef stricmp
int __stricmp_a (const char * src, const char * dst)
{
	//	(if src is a subset of dst or visa-versa)
	size_t lsrc = strlen(src);
	size_t ldst = strlen(dst);

	int minlen = strlen(src) < strlen(dst) ? strlen(src) : strlen(dst);
	int i, ret = 0;

	if (minlen == 0)
	{
		ret = (int)(toupper(*src) - toupper(*dst));
	}
	else
	{
		for (i = 0; i < minlen; i++,++src,++dst)
		{
			ret = (int)(toupper(*src) - toupper(*dst));
			if (ret)
			{
				break;
			}
		}
	}

	if (ret < 0)
	{
		ret = -1 ;
	}
	else if (ret > 0)
	{
		ret = 1 ;
	}
	else
	{
		// ret is 0, change that if the strlens are different
		if (ldst > lsrc)
		{
			ret = 1;
		}
		else if (lsrc > ldst)
		{
			ret = -1;
		}
	}

	return( ret );
} /* stricmp_a */

#endif
#if defined(_UNIX)  

//--------------------------------------------------------------------
//                           reverse
// Purpose: Reversed the order of characters in the string inputted.
// Parameters: s - string inputted.
// Output: s - string with reversed chararcters
//--------------------------------------------------------------------
static void reverse(char s[])
{
	int c,i,j;

	size_t len = strlen(s);
	for (i=0,j=len-1; i<j; i++,j--)
	{
		c = s[i];
		s[i] = s[j];
		s[j] = c;
	} // endfor
}

//--------------------------------------------------------------------
//                           ltoa
// Purpose: To provide a long to ascii function that will be
//          compatable with windows version
// Parameters: n - Integer value to be converted to a string
//             s - Pointer to a character array where result is returned
//             radix - Radix in which the result is expressed
//                     (in the range 2-36)
// Returns: - a pointer to the string of digits contained in s
// Note: - If an error occurred this function will return NULL
//         i.e. inputting a radix out of range
//--------------------------------------------------------------------
#undef ltoa
char *__ltoa_a(long int n, char *s, int radix)
{
	if ((radix >= 2) && (radix <= 36))
	{
		long	i = 0, sign, offset;

		if ((sign = n) < 0)	// record sign
		{
			n = -n;	// make n positive
		} // end if

		// generate digits in reverse order
		do
		{
			offset = n % radix;
			if (offset < 10)
			{
				s[i++] = offset + '0';
			} // end if
			else
			{
				s[i++] = (offset - 10) % 26 + 'A';
			} // end else
		} while ((n /= radix) > 0);

		if (sign < 0)
		{
			s[i++] = '-';
		} // end if
		s[i] = '\0';
		reverse(s);
	} // end if
	else
	{
		s = NULL;
	} // end else

	return s;

} // ltoa

#ifdef OS400 
char *__ltoa_a2(long int n, char *s, int radix)
{
	if ((radix >= 2) && (radix <= 36))
	{
		long	i = 0, sign, offset;

		if ((sign = n) < 0)	// record sign
		{
			n = -n;	// make n positive
		} // end if

		// generate digits in reverse order
		do
		{
			offset = n % radix;
			if (offset < 10)
			{
				s[i++] = offset + '0';
			} // end if
			else
			{
				s[i++] = (offset - 10) % 26 + 'A';
			} // end else
		} while ((n /= radix) > 0);

		if (sign < 0)
		{
			s[i++] = '-';
		} // end if
		s[i] = '\0';
		reverse(s);
	} // end if
	else
	{
		s = NULL;
	} // end else

	return s;

} // ltoa
#endif // OS400


#ifdef OS400
#if !defined(NDEBUG)
/* this function __C_assrt() is called by the assert macro on OS400 and it formats the string that gets dumped out */
/* before the program is aborted when an assert fails.  It returns EBCDIC and we need a version that returns ASCII */
#undef _C_assrt
char * __ptr128  __C_assrt_a ( char * __ptr128 statement, char * __ptr128 filename, int linenumber)
{
	// the statement comes to us in ASCII - go figure...
	char eStatement[1024];
	__toebcdic_a(eStatement, statement);
	// call the real one
	char * strRC = _C_assrt(eStatement, filename, linenumber);
	// translate to ascii in line
	__toascii_a(strRC, strRC);
	// return that
	return strRC;

} /* __C_assrt_a */
#else
char *__C_assrt_a ( char *statement, char *filename, int linenumber)
{
	return "";
}
#endif // NDEBUG
#endif // OS400
#endif // _UNIX

#ifdef OS400

#pragma convert(37)
int getDominoRunPath(char * serverPath)
{
	Qus_EC_t	ec;
	ec.Bytes_Available = 0;
	ec.Bytes_Provided = sizeof(ec);
	QnninDominoEnv_t * envt;

    char buffer[4096];
    char serverPath_e[257];

    size_t bufSize = sizeof(buffer);

    /* Get the environment that was just set for this server */
    QnninGetDominoEnv
      (buffer,        /* Data Buffer                */
       (int*)&bufSize,  /* Length of Data Buffer      */
       "DENV0100",     /* Format name                */
       &ec);

    /* See if getenv worked */
    if (ec.Bytes_Available)
    { /* failed! */
	return 1;
    }
    else
    {
	envt = (QnninDominoEnv_t *) buffer;

	memset (serverPath_e, '\0', sizeof(serverPath_e));

	memcpy(serverPath_e,envt->RunPath,envt->LengthOfRunPath);

	__toascii_a(serverPath, serverPath_e);

	return 0;
    }

    return 1;
}
#endif

#endif  // UNIX_TOOLKIT_COMPILE

#if defined(OS400) || defined(OS390)
}
#endif

