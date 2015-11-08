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


//
//	Primary Goals:
//	1) cache results so files are only opened/read once
//	2) fix potential buffer overrun issues
//	3) flag potential mis-configuration issues (i.e. no default, or INI setting for keys)
//
//	FUTURE TTD
//	A) I only implemented the two primarily used methods (Int and String)... none of the
//  WRITE methods have been updated yet (they don't appear to be used in UNIX)
//	B) use a file status check to detect config changes and reload if necessary
//	to support dynamic configuration changes
//

#include "windows.h"
#include "pthread.h"
#include <string>
#include <map>

	// define for how to open files in text mode...
#if defined(OS400) || defined(OS390)
	// (Note that for OS/400, binary mode is needed so that characters are not translated
	// from ASCII to EBCDIC)
	#define READ_TEXT_MODE "rb"
	#define APPEND_PLUS_TEXT_MODE "a+b"
	#define WRITE_TEXT_MODE "wb"
        #undef tolower
        #define tolower			__tolower_a
        #undef toupper
        #define toupper			__toupper_a
        #undef atoi
        #define atoi			__atoi_a
#else
	// The "t" in fopen is not ANSI - it means to translate Ctrl-Z
	// to end of file
	#define READ_TEXT_MODE "rt"
	#define APPEND_PLUS_TEXT_MODE "a+t"
	#define WRITE_TEXT_MODE "w"
#endif


//
//	NOTE: This file must be able to stand alone by itself...
//	We can't use the windows.cpp "debug" class because it would
//	call back into these functions... renentrantly
//
//	Therefore, the following #defines can be used by a DEVELOPER
//	to toggle DEBUG fprints ON and OFF to the console if required.
//
//	The goal is to ship GOLD with no DEBUGGING enabled!
//
#define _WP_DEBUG_TRACE_
#define _WP_DEBUG_ERROR_

//ST_Linux_Port_Level0
#if defined(AIX) || defined(SOLARIS) || defined(OS400) || defined(LINUX)
typedef std::map<std::string, std::string> WPKeyValueMap;
typedef std::map<std::string, WPKeyValueMap *> WPSectionMap;
typedef std::map<std::string, WPSectionMap *> WPFilenameMap;
typedef std::map<std::string, time_t * > WPFileTimeMap;
#else
typedef std::map<std::string, std::string, less<std::string> > WPKeyValueMap;
typedef std::map<std::string, WPKeyValueMap *, less<std::string> > WPSectionMap;
typedef std::map<std::string, WPSectionMap *, less<std::string> > WPFilenameMap;
typedef std::map<std::string, time_t , less<std::string> > WPFileTimeMap;
#endif

typedef WPKeyValueMap::iterator WPKeyValueMapIterator;
typedef WPSectionMap::iterator WPSectionMapIterator;
typedef WPFilenameMap::iterator WPFilenameMapIterator;
typedef WPFileTimeMap::iterator WPFileTimeMapIterator;

#ifdef OS400
//H Halverson, we need a longer buffer, because the VMarguments line
//on iseries can get REALLY long
#define MAX_BUFFER_LENGTH 4096
#else
#define MAX_BUFFER_LENGTH 1024
#endif
#define TRIM_LEADING	0
#define TRIM_TRAILING	1 
#define TRIM_BOTH		2
#define TRIM_TOLOWER	3 
#define TRIM_SECTION	4 

//static char my_value[] = "value";
//static char my_key[] = "key";
//ST_Linux_Port_Level0
#if defined(LINUX)
static int wp_initialized = 0;
#else
static wp_initialized = 0;
#endif
static pthread_mutex_t	wp_mutex;

void wp_init()
{
	if (0 == wp_initialized)
	{
		wp_initialized = 1;
		pthread_mutex_init(&wp_mutex, NULL);
	}
}


void trim(char * my_buffer, int buffer_len, int mode)
{
	char *	dest_ptr;
	int index;
	int length = strlen(my_buffer);
	int non_space_detected_flag = FALSE;

	switch(mode)
	{
		case TRIM_BOTH:
		case TRIM_LEADING:
			//
			//	remove any leading blanks/spaces from string
			//	collapsing the buffer onto itself...
			//
			length = strlen(my_buffer);
			if (length > buffer_len)
			{
//				fprintf(stderr, "warning... line size:%d exceeds buffer size:%d\n", length, buffer_len);
				length = buffer_len;
			}

//			fprintf(stderr,"[%2d]%s", length, my_buffer);
			dest_ptr = my_buffer;
			for (index=0; index<length; index++)
			{
				if (FALSE == non_space_detected_flag)
				{
					if ((' ' == my_buffer[index]) || (0x09 == my_buffer[index]))
					{
						continue;	// skip over it...
					}
					non_space_detected_flag = TRUE;
				}
				*dest_ptr++ = my_buffer[index];
//				fprintf(stderr, "%02x ", my_buffer[index]);
			}
			*dest_ptr++ = 0;	// NULL terminate the buffer
//			fprintf(stderr, "\n");
			if (TRIM_BOTH == mode)
			{
				// intentional fall through...
			}
			else
			{
				break;
			}

		case TRIM_TRAILING:
			//
			//	remove any trailing spaces from string
			//
			length = strlen(my_buffer);
			for (index=length; index>0; index--)
			{
				if ((' ' == my_buffer[index-1])
					|| (0x09 == my_buffer[index-1])
					|| (0x0d == my_buffer[index-1])
					|| (0x0a == my_buffer[index-1]))
				{
					my_buffer[index-1] = 0;
				}
				else
				{
					break;
				}
			}

			length = strlen(my_buffer);
//			fprintf(stderr,"[%2d]%s\n", length, my_buffer);
			break;

		case TRIM_TOLOWER:
			length = strlen(my_buffer);
			for (index=0; index<length; index++)
			{
				my_buffer[index] = tolower((int)(my_buffer[index]));
			}
			break;

		case TRIM_SECTION:
			length = strlen(my_buffer);
			if (length > 3)
			{
				//
				//	replace the brackets with spaces, then trim the space...
				//

				if ('[' == my_buffer[0])
					my_buffer[0] = ' ';

				if (']' == my_buffer[length-1])
					my_buffer[length-1] = ' ';

				trim(my_buffer, buffer_len, TRIM_BOTH);
			}
			break;

		default:
			fprintf(stderr,"unsupported trim mode:%d\n", mode);
			break;
	}
}


void load_file_contents(LPCSTR filename, WPSectionMap * section_map)
{
	int		index;
	int		length;
	char	my_buffer[MAX_BUFFER_LENGTH];
	char	my_key[MAX_BUFFER_LENGTH];
	char	my_value[MAX_BUFFER_LENGTH];
	WPKeyValueMap * my_key_value_map = NULL;
	char *	rptr;


	//	fprintf(stderr, "load_file_contents - entry... file:[%s]\n", filename);

	FILE * fp;

	fp = fopen(filename, READ_TEXT_MODE);
	if (NULL == fp)
	{
//		fprintf(stderr, "AAA error opening file:[%s] - errno:%d\n", filename, errno);

		char *dir;
		char cwd[2048];
		cwd[0] = 0;
		dir = getcwd(cwd, sizeof(cwd));
		if (!dir)
		{
//			fprintf(stderr, "WINUX ERROR load_file_contents - getcwd errno:%d\n", errno);
		}

		//
		//	try using CWD + SEPARATOR + filename...
		//
		strcat(cwd, "/");
		strcat(cwd, filename);
		fp = fopen(cwd, READ_TEXT_MODE);
		if (NULL == fp)
		{
//			fprintf(stderr, "WINUX ERROR load_file_contents - entry... error opening file:[%s] - errno:%d\n", cwd, errno);
			return;
		}
	}

	while(1)
	{
		rptr = fgets(my_buffer, MAX_BUFFER_LENGTH, fp);
//ST_Linux_Port_Level0
#if defined(LINUX)
#if defined(__s390x__)
        if(( NULL ==  rptr) || (EOF == (ptrdiff_t) rptr))
#else
        if(((int) NULL == (int) rptr) || (EOF == (int) rptr))
#endif
#else
        if ((NULL == (int) rptr) || (EOF == (int) rptr))
#endif
		{
			// End-Of-File... time to exit this loop...

//			fprintf(stderr, "\n\nfgets returned error rptr:%d... break...\n", rptr);
			break;
		}
		else
		{

			trim(my_buffer, MAX_BUFFER_LENGTH, TRIM_BOTH);

			if ('#' != my_buffer[0])
			{
				//
				//	locate first "equal" sign delimiter
				//
				int token_index = (-1);
				length = strlen(my_buffer);
				for (index=0; index<length; index++)
				{
					if ('=' == my_buffer[index])
					{
						token_index = index;
						break;
					}
				}

				if (token_index >= 0)
				{
					//	success...
//					fprintf(stderr,"token found at index:%d...\n", token_index);

					strncpy(my_key, my_buffer, token_index);
					my_key[token_index] = 0;
					trim(my_key, MAX_BUFFER_LENGTH, TRIM_BOTH);

					strncpy(my_value, (my_buffer+token_index+1), (length - token_index));
					my_value[(length - token_index)] = 0;
					trim(my_value, MAX_BUFFER_LENGTH, TRIM_BOTH);

//					fprintf(stderr,"my_key:[%s] my_value:[%s]\n", my_key, my_value);

					WPSectionMapIterator iterator;
					WPKeyValueMapIterator	keyIterator;

					if (NULL != my_key_value_map)
					{
						if (0 != strlen(my_value))
						{
//							fprintf(stderr,"adding key/value to map...\n");
							std::string my_key_string(my_key);
							std::string my_value_string(my_value);
							if((keyIterator = my_key_value_map->find(my_key_string))!= my_key_value_map->end())
							{
								if(my_value_string !=(*keyIterator).second)
								{
                                    my_key_value_map->erase(my_key_string);
									my_key_value_map->insert(WPKeyValueMap::value_type(my_key_string, my_value_string));
								}
							}
							else
							{
								my_key_value_map->insert(WPKeyValueMap::value_type(my_key_string, my_value_string));
							}
						}
						else
						{
//							fprintf(stderr,"value is NULL, not adding to map...\n");
						}
					}
					else
					{
//						fprintf(stderr,"no key/value map section available for key...\n");
					}

					int atoi_result = atoi(my_value);
                                        // fixed error logic
                                        if ((EINVAL == errno) && (0 == atoi_result))
					{
//						fprintf(stderr,"atoi_result:[%d]\n", atoi_result);
					}
				}
				else
				{
					//	failure... don't use this line of information...
					trim(my_buffer, MAX_BUFFER_LENGTH, TRIM_TOLOWER);
					if ((length > 3) && ('[' == my_buffer[0]) && (']' == my_buffer[length-1]))
					{
						trim(my_buffer, MAX_BUFFER_LENGTH, TRIM_SECTION);
//						fprintf(stderr,"-----<SECTION HEADER DETECTED>-----(%s)[%s]\n", my_buffer, filename);

						//
						//	Does this SECTION already exist in the map?
						//
						WPSectionMapIterator iterator;
						std::string my_string(my_buffer);
						iterator = section_map->find(my_string);
						if (iterator != section_map->end())
						{
//							fprintf(stderr,"section FOUND!\n");
							my_key_value_map = (*iterator).second;
						}
						else
						{
//							fprintf(stderr,"section NOT FOUND!\n");
							my_key_value_map = new WPKeyValueMap();
							section_map->insert(WPSectionMap::value_type(my_string, my_key_value_map));
						}
					}
					else
					{
//						fprintf(stderr,"no token found for string... [%s]\n", my_buffer);
//						fprintf(stderr,"\n");
					}
				}
			}
			else
			{
//				fprintf(stderr,"comment line... ignoring data on this line...\n");
			}
		}
	}

	if (0 != fclose(fp))
	{
		fprintf(stderr, "error closing file:[%s] - errno:%d\n", filename, errno);
	}

//	fprintf(stderr, "load_file_contents - exit\n");
}

static WPFilenameMap * Global_Filename_Map = NULL;
static WPFileTimeMap * Global_FileTime_Map = NULL;

void dump_profiles()
{
	WPFilenameMapIterator iterator;
	WPFileTimeMapIterator time_iterator;
	WPSectionMap * section_map = NULL;

	WPKeyValueMap *			key_value_map = NULL;
	WPSectionMapIterator	section_map_iterator;
	WPKeyValueMapIterator	kv_iterator;

	int rc = 0;

	rc = pthread_mutex_lock(&wp_mutex);

	// Clean Up Time Memory First
	time_iterator = Global_FileTime_Map->begin();

	while(time_iterator != Global_FileTime_Map->end())
	{
		delete (*time_iterator).second;

		Global_FileTime_Map->erase(time_iterator);

		time_iterator = Global_FileTime_Map->begin();
	}

	// Clean Up File Memory
	iterator = Global_Filename_Map->begin();

	while(iterator != Global_Filename_Map->end())
	{
		section_map = (*iterator).second;
		
		section_map_iterator = section_map->begin();

		while(section_map_iterator != section_map->end())
		{
			key_value_map = (*section_map_iterator).second;

			if (NULL != key_value_map)
			{
				kv_iterator = key_value_map->begin();
				while(kv_iterator != key_value_map->end())
				{
					key_value_map->erase(kv_iterator);

					kv_iterator = key_value_map->begin();
				}

				delete key_value_map;
			}
			
			section_map->erase(section_map_iterator);

			section_map_iterator = section_map->begin();
		}

		
		delete section_map;

		Global_Filename_Map->erase(iterator);

		iterator = Global_Filename_Map->begin();
	}

	rc = pthread_mutex_unlock(&wp_mutex);
}

WPSectionMap * wp_loadfile(LPCSTR filename)
{
	WPSectionMap * my_section_map = NULL;
	WPFilenameMapIterator iterator;
	WPFileTimeMapIterator time_iterator;
	time_t *		my_file_time;
	time_t *		current_time;
	int rc;

	wp_init();

	rc = pthread_mutex_lock(&wp_mutex);
	if (0 != rc)
	{
		fprintf(stderr, "wp_loadfile pthread_mutex_unlock failure  - errno:%d\n", errno);
	}

//	if (0 == strcmp(filename, "sametime.ini"))
//	{
//		fprintf(stderr, "wp_loadfile - entry...[%s]\n", filename);
		current_time = new time_t();
		time(current_time);
		std::string my_filename_string(filename);
		if(Global_Filename_Map == NULL)
		{
			Global_Filename_Map = new WPFilenameMap();
			Global_FileTime_Map = new WPFileTimeMap();
		}	
		iterator = Global_Filename_Map->find(my_filename_string);
		time_iterator = Global_FileTime_Map->find(my_filename_string);

		if (iterator != Global_Filename_Map->end())
		{
			// match found!
			my_section_map = (*iterator).second;
			my_file_time = (*time_iterator).second;

			// Match found however the match is older than 60 secs.
			// Dump it and reload, as the configuration may have
			// changed.
#if defined(OS400) 	/* Karen Eikenhorst 14Dec2006 - only check once per hour for i5/OS */
			if((my_filename_string.find("meetingserver.ini"))
				&&(difftime(*current_time,*my_file_time) > 3600.0))
#else
                        /* Karen Eikenhorst 14Dec2006 -- bug fixes: check for = meeting server.ini and allow for path appended to file name */
			if((my_filename_string.find("meetingserver.ini"))
				&&(difftime(*current_time,*my_file_time) > 60.0))
#endif
			{
				delete my_file_time;
				Global_FileTime_Map->erase(my_filename_string);
				
				(*Global_FileTime_Map)[my_filename_string] = current_time;
				load_file_contents(filename, my_section_map);
			}
//			fprintf(stderr, "wp_loadfile - match found!  ---CACHE--- 0x%08x\n", my_section_map);
		}
		else
		{
			// match not found!

			my_section_map = new WPSectionMap();
			(*Global_Filename_Map)[my_filename_string] = my_section_map;
			(*Global_FileTime_Map)[my_filename_string] = current_time;

//			fprintf(stderr, "wp_loadfile - match not found! - loading file... 0x%08x\n", my_section_map);
			load_file_contents(filename, my_section_map);
		}
//		fprintf(stderr, "wp_loadfile - exit [%s]\n", filename);
//	}

	rc = pthread_mutex_unlock(&wp_mutex);
	if (0 != rc)
	{
		fprintf(stderr, "wp_loadfile pthread_mutex_unlock failure  - errno:%d\n", errno);
	}

	return(my_section_map);
}


LPCSTR getValue(WPSectionMap * my_section_map, LPCSTR lpSectionName, LPCSTR lpKeyName)
{
	LPCSTR					my_value_to_return = NULL;
	WPKeyValueMap *			my_key_value_map = NULL;
	WPSectionMapIterator	iterator;
	WPKeyValueMapIterator	kv_iterator;
	char					my_buffer[MAX_BUFFER_LENGTH];
	int							rc;

	my_buffer[0] = 0;
	if (strlen(lpSectionName) < MAX_BUFFER_LENGTH)
	{
		strcpy(my_buffer, lpSectionName);
		trim(my_buffer, MAX_BUFFER_LENGTH, TRIM_TOLOWER);
	}

	rc = pthread_mutex_lock(&wp_mutex);
	if (0 != rc)
	{
		fprintf(stderr, "getValue pthread_mutex_lock failure  - errno:%d\n", errno);
	}

//	fprintf(stderr, "getValue - entry... size:%d\n", my_section_map->size());
//	iterator = my_section_map->begin();
//	while(1)
//	{
//		if (iterator == my_section_map->end())
//			break;
//		fprintf(stderr, "section name:[%s]\n", iterator->first.c_str());
//		iterator++;
//	}

	std::string my_buffer_string(my_buffer);
	iterator = my_section_map->find(my_buffer_string);
	if (iterator != my_section_map->end())
	{
//		fprintf(stderr,"getValue section FOUND in MAP!!!\n");

		my_key_value_map = (*iterator).second;

		if (NULL != my_key_value_map)
		{
			if (NULL != lpKeyName)
			{
				std::string my_lpKeyName_string(lpKeyName);
				kv_iterator = my_key_value_map->find(my_lpKeyName_string);
				if (kv_iterator != my_key_value_map->end())
				{
					my_value_to_return = (*kv_iterator).second.c_str();
				}
//				else
//				{
//					// DEBUGGING dump of all KeyValue pairs..
//					fprintf(stderr, "getValue - key:[%s] not found in MAP!\n", lpKeyName);
//					kv_iterator = my_key_value_map->begin();
//					while(1)
//					{
//						if (kv_iterator == my_key_value_map->end())
//							break;
//						fprintf(stderr, "key:[%s] value:[%s]\n",
//							kv_iterator->first.c_str(),
//							(*kv_iterator).second.c_str());
//						kv_iterator++;
//					}
//				}
			}
			else
			{
				fprintf(stderr, "getValue - lpKeyName is NULL!\n");
			}
		}
	}
	else
	{
		// return NULL since the section/key was not found...
//		fprintf(stderr, "getValue - section:[%s] not found in MAP!\n", lpSectionName);
//		fprintf(stderr, "getValue - section:[%s] not found in MAP!\n", my_buffer);
	}

//	fprintf(stderr, "getValue - exit - returning [%s]\n", my_value_to_return ?  my_value_to_return : "NULL");
	rc = pthread_mutex_unlock(&wp_mutex);
	if (0 != rc)
	{
		fprintf(stderr, "getValue pthread_mutex_unlock failure  - errno:%d\n", errno);
	}

	return (my_value_to_return);
}


UINT WINAPI
GetPrivateProfileInt (
	LPCSTR	lpAppName,
	LPCSTR	lpKeyName,
	INT	nDefault,
	LPCSTR	lpFileName
	)
{
	char			szT[256];
	DWORD			dwRet;
	int				nValue;
	char			*p;
	WPSectionMap *	my_section_map = NULL;
	LPCSTR			my_value = NULL;

//	fprintf(stderr, "%05d GetPrivateProfileInt - entry... DEFAULT:%d lpFileName:[%s] lpAppName:[%s] lpKeyName:[%s]\n",
//		getpid(),
//		nDefault,
//		lpFileName ? lpFileName : "NULL",
//		lpAppName ? lpAppName : "NULL",
//		lpKeyName ? lpKeyName : "NULL");

	my_section_map = wp_loadfile(lpFileName);
	if (NULL != my_section_map)
	{
		// extract value for key...
		my_value = getValue(my_section_map, lpAppName, lpKeyName);

//		if (NULL != my_value)
//		{
//			fprintf(stderr, "VALUE RETURNED:[%s]\n", my_value);
//		}
//		else
//		{
//			fprintf(stderr, "VALUE IS NULL\n");
//		}
	}
	else
	{
		fprintf(stderr, "filename:[%s] has no SECTION map\n", lpFileName);
	}

	int atoi_result = 0;
	nValue = nDefault;
	if (NULL != my_value)
	{
	        errno=0;	/* Karen Eikenhorst - was always logging msg when my_value was 0 */
		atoi_result = atoi(my_value);
		// fixed error logic
		if ((EINVAL == errno) && (0 == atoi_result))
		{
			fprintf(stderr, "atoi_result failure...\n");
			atoi_result = -1;
		}
		else
		{
			nValue = atoi_result;
		}
	}


//	fprintf(stderr, "GetPrivateProfileInt - exit... [%s] nValue:%d\n", my_value ? my_value : "NULL", nValue);
//#ifdef _WP_DEBUG_TRACE_
//	fprintf(stderr, "GPPI(%05d) - [%s] s:[%s] k:[%s] d:[%d]\n",
//		nValue,
//		my_value ? my_value : "default",
//		lpAppName ? lpAppName : "NULL",
//		lpKeyName ? lpKeyName : "NULL",
//		nDefault);
//#endif

	return (UINT)nValue;
}


DWORD WINAPI
GetPrivateProfileString (
			 LPCSTR	lpAppName,
			 LPCSTR	lpKeyName,
			 LPCSTR	lpDefault,
			 LPSTR	lpReturnedString,
			 DWORD	nSize,
			 LPCSTR	lpFileName
			 )
{
	DWORD   dwLen = 0;		// Length of Key String to be returned.
	WPSectionMap *	my_section_map = NULL;
	LPCSTR			my_value = NULL;
	int				found = 0;

//	fprintf(stderr, "%05d GPPS sect:[%s] key[%s] file:[%s] nSize:%d\n",
//		getpid(), lpAppName, lpKeyName, lpFileName, nSize);

	if (NULL != lpFileName)
	{
		my_section_map = wp_loadfile(lpFileName);
		if (NULL != my_section_map)
		{
			// extract value for key...
			my_value = getValue(my_section_map, lpAppName, lpKeyName);
			found = 1;

//			if (NULL != my_value)
//			{
//				fprintf(stderr, "VALUE RETURNED:[%s]\n", my_value);
//			}
//			else
//			{
//				fprintf(stderr, "VALUE IS NULL\n");
//			}
		}
	}

	if (NULL == my_value)
	{
		found = 0;
		my_value = lpDefault;
	}

	if (NULL != lpReturnedString)
	{
		lpReturnedString[0] = 0;

		if (NULL != my_value)
		{
			if (strlen(my_value) <= nSize)
			{
					strcpy(lpReturnedString, my_value);
					dwLen = strlen(lpReturnedString);
			}
			else
			{
				fprintf(stderr, "buffer OVERRUN possibility detected!\n");
			}

		}
	}
	else
	{
		fprintf(stderr, "%05d GPPS sect:[%s] key[%s] file:[%s] nSize:%d\n", getpid(), lpAppName, lpKeyName, lpFileName, nSize);
		fprintf(stderr, "destination string is NULL!\n");
	}

//#ifdef _WP_DEBUG_TRACE_
//#ifdef _DEBUG
//	if ((NULL != lpDefault) && (0 == strlen(lpDefault)) && (0 == found))
//	{
//		fprintf(stderr, "GPPS  L:%d(%s) - [%s] s:[%s] k:[%s] d:[%s]\n",
//			dwLen,
//			lpReturnedString ? lpReturnedString : "NULL",
//			my_value ? my_value : "default",
//			lpAppName ? lpAppName : "NULL",
//			lpKeyName ? lpKeyName : "NULL",
//			lpDefault ? lpDefault : "NULL");
//	}
//#endif
//#endif

	return dwLen;
} // End of GetPrivateProfileString()


BOOL WINAPI
WritePrivateProfileString (
			   LPCSTR	lpAppName,
			   LPCSTR	lpKeyName,
			   LPCSTR	lpString,
			   LPCSTR	lpFileName
			   )
{
    DWORD dwLen;		// Length of Key String to be returned.
	WPSectionMap *	my_section_map = NULL;
	LPCSTR			my_value = NULL;

	fprintf(stderr, "WritePrivateProfileString - entry...\n");

	my_section_map = wp_loadfile(lpFileName);
	if (NULL != my_section_map)
	{
		// extract value for key...
		my_value = getValue(my_section_map, lpAppName, lpKeyName);

		if (NULL != my_value)
		{
			fprintf(stderr, "VALUE RETURNED:[%s]\n", my_value);
		}
		else
		{
			fprintf(stderr, "VALUE IS NULL\n");
		}
	}

    /*
     * If the caller did not pass a section name, return now.
     */
    if (lpAppName == NULL)
	{
		return FALSE;
	}

	// If the caller did not pass a key name ...
    /*
     * If the caller did not pass a key name,
     * write all of the keys for the specified section.
     */
	else if (lpKeyName == NULL)
	{
		dwLen = WritePrivateProfileSection (lpAppName, lpString, lpFileName);
	}

    /*
     * If the caller wants to update a specific key value ...
     */
	else
	{
		int		n;		// File Read Buffer Size
		int		n1;		//
		char*		psz;		// File Buffer
		char*		orig_psz;	// original address of File Buffer
		int		nBuffLen;	// File Buffer Size;
		BOOL		bInSection;	// Section found flag
		char*		psz1;		// Temp pointer for Key/Value parsing
		BOOL		bDone;		// Done Flag
		int		nAppNameSize;	// String size of lpAppName;
		int		nKeyNameSize;	// String size of lpKeyName;
		FILE*		pFile;
		FILE*		pTmpFile;
		BOOL		bCarriageReturn;	 
		BOOL		bNewLine;
		int		nCtrlChar;	// number of control characters for a record	

		/* Following code was changed to be portable*/

		/*
		 * Open the specified INI file.
		 * 
		 */
		pFile = fopen (lpFileName, READ_TEXT_MODE);
		if (pFile == NULL)
		{
			return FALSE;
		}

		/*
		 * Open a temporary file.
		 */
		pTmpFile = tmpfile();

		/*
		 * Initialize variables.
		 */
		nBuffLen = 512;
		orig_psz = (char*)malloc (nBuffLen);	
		psz = orig_psz;				
		bInSection = FALSE;
		bDone = FALSE;
		nAppNameSize = strlen (lpAppName);
		bCarriageReturn = FALSE;	
		bNewLine = FALSE;
		nCtrlChar = 0;			

#if defined(OS400) 
		// When the file was opened for append, the system positioned it to the end of the file. Rewind the file so that it is positioned to the beginning of the file.
		rewind(pFile);
#endif


		/*
		 * Read each record in the .INI file
		 * until the specified section and key are found.
		 */
		do
		{
			/*
			 * Get 512 bytes of the record at a time
			 * until the entire record has been read.
			 */
		    n = 0;
		    do
		    {
			// Note to future iSeries debuggers.  If the data contains a % sign (percent)
			// this code does not work properly and overwrites storage.  fgets only returns
			// data up to the %.  This code determines that there is more data to read
			// because it does not find and EOL char after %, but does not realloc the buffer
			// causing storage to be overwritten if the line is longer than 512.  This could
			// be fixed with redesign.  Our resolution was to replace the % with # in the file. 
			if (fgets (&psz[n], 512, pFile) == NULL) // Get up to 512 bytes of the record.
			{
			    // nbartley 6/13/2009 fgets does not clear the buffer from the prev read
			    psz[0] = 0;
			    n = 0;
			}
			else
			{
			    n = strlen (psz);		// Count the number of bytes read so far.
			}

			// 16sep2002 changed from n-1 to n+1
			if (n + 1 == nBuffLen)		// If the input buffer is too small,
			// add another 511 bytes to the input buffer.
			{
			    nBuffLen += 511;
			    orig_psz = (char *)realloc(orig_psz, nBuffLen);
			    psz = orig_psz;					
			}
		    } while (n != 0 && psz[n -1] != '\n');
			// (End of "Get the next record in the INI file")

		    while (psz[0]==' ')		// Remove leading blanks.
		    {
			psz+=1;
		    }   
		    n = strlen (psz);		// Count the number of bytes read so far.

			/*
			 * Replace the control characters that end the record, with nulls,
			 * then count the number of bytes in the remaining string.
			 */
			if (n > 1 && psz[n-2] == '\r')		// If the record ends with \r\n ...
			{
				bCarriageReturn = TRUE;		// Set this flag so that when
				// a new record is written, it will include a
				// CR control character before the NL character.
				nCtrlChar = 2;			// There are two control characters.
			} else				  // If the record ends with \n ...
			{
				nCtrlChar = 1;			// There is one control character.
			}
			n = strlen(psz);			// Count the bytes in the string.

			/*
			 * If this is the start of a section ...
			 */
			if (n > 1 && psz[0] == '[' && psz[n - 1 - nCtrlChar] == ']') 
			{

				/*
				 * If this is the start of the specified section,
				 * turn on the "in section" flag, then get the next record.
				 */
				if (nAppNameSize == (n - 2 - nCtrlChar) &&
					strncasecmp (lpAppName, &psz[1], nAppNameSize) == 0) 
				{
					bInSection = TRUE;			// Turn on the "in section" flag.
					nKeyNameSize = strlen (lpKeyName);	// Get the length of the key's name.
				}

				/*
				 * If this is the start of the section after the specified one,
				 * insert the new record before the next section.
				 */
				else if (bInSection)
				{
					bInSection = FALSE;		// Turn off "in section" flag
					fprintf(pTmpFile, "%s=%s", lpKeyName, lpString); // Append record
					if (bCarriageReturn)	
						fprintf(pTmpFile, "\r"); 
					fprintf(pTmpFile, "\n");	
					bDone = TRUE;		// Turn on "loop is done" flag
				}

			}

			/*
			 * If this record is in the specified section ...
			 */
			else if (bInSection)
			{

				/*
				 * If this is a key=value pair ...
				 */
				psz1 = strchr (psz, '=');	// Find the '=' character.
				if (psz1 != NULL)		// If found ...
				{

					/*
					 * If this is the specified key, reformat the record.
					 */
					n1 = psz1 - psz;		// Compute the length of the key's name.
					if (n1 == nKeyNameSize &&	// If this is the specified key ...
						strncasecmp (lpKeyName, psz, nKeyNameSize) == 0)
					{
						sprintf (psz, "%s=%s", lpKeyName, lpString); // Reformat the record.
						if (bCarriageReturn)	
							sprintf (psz, "%s\r", psz);	
						sprintf (psz, "%s\n", psz);	
						bDone = TRUE;		// Turn on the "loop is done" flag.
					}

				}

			}

			/*
			 * If control characters should be added, write them to the temporary file.
			 */
			if (bNewLine)
			{
				if (bCarriageReturn)		
					fputs ("\r", pTmpFile);	
				fputs ("\n", pTmpFile);
				bNewLine = FALSE;
			}

			/*
			 * If the record is blank, set a flag.
			 */
			if (psz[nCtrlChar-1] == '\n')
				bNewLine= TRUE;

			/*
			 * If the record is not blank, write it to the temporary file.
			 */
			else if (n != 0)
				fputs(psz, pTmpFile);

			// Loop until the specified keyword and its value have been
			// written to the temporary file.
		} while (!bDone && !feof (pFile));
		// (End of "Get the value for the given Section and key")

		/*
		 * If the specified section or keyword was not found ...
		 */
		if (!bDone)
		{

			/*
			 * If the section was not found, append a record for the new section.
			 */
			if (!bInSection)
			{

				// Write the control characters, then the new record,
				// then more control characters.
				if (bCarriageReturn)
					fprintf(pTmpFile, "\r");
				fprintf(pTmpFile, "\n[%s]", lpAppName);
				if (bCarriageReturn)
					fprintf(pTmpFile, "\r");
				fprintf(pTmpFile, "\n");
			}

			/* 
			 * Append a record for the new keyword and its value.
			 */
			// Write the new record, then the control characters.
			fprintf(pTmpFile, "%s=%s", lpKeyName, lpString);
			if (bCarriageReturn)
				fprintf(pTmpFile, "\r");
			fprintf(pTmpFile, "\n");

		}

		/*
		 * If the specified section or keyword was found,
		 * copy the remainder of the INI file to the temporary file.
		 */
		else
		{
			while (!feof (pFile))
			{
				if ((n = fread (psz, 1, nBuffLen, pFile)) != 0)
					fwrite (psz, 1, n, pTmpFile);
			}
		}

		/*
		 * Reset both file pointers.
			 * (The following code was changed to be portable - )
		 * ( - Changed APPEND_PLUS_TEXT_MODE to WRITE_TEXT_MODE)
		 */
		pFile = freopen (lpFileName, WRITE_TEXT_MODE, pFile);
		rewind (pTmpFile);

		/*
		 * Copy the temporary file back over the original.
		 */
		while (!feof (pTmpFile))
		{
			if ((n = fread (orig_psz, 1, nBuffLen, pTmpFile)) != 0)
				fwrite (orig_psz, 1, n, pFile);			
		}

		/*
		 * Clean up before returning to the caller.
		 */
		fclose(pTmpFile);
		fclose(pFile);
		free (orig_psz);		

	}

	fprintf(stderr, "WritePrivateProfileString - exit...\n");
    return TRUE; 
}


DWORD WINAPI
GetPrivateProfileSection (
			  LPCSTR	lpAppName,
			  LPSTR		lpReturnedString,
			  DWORD		nSize,
			  LPCSTR	lpFileName)
{
    FILE*	pFile;
    DWORD	dwLen;		// Length of Key String to be returned.
    int		n;		// File Read Buffer Size
    char*	orig_psz;	// File Buffer			
    char*	psz;		// temporary pointer to File Buffer   
    int		nBuffLen;	// File Buffer Size;
    BOOL	bInSection;	// Section found flag
    char*	psz1;		// Temp pointer for Key/Value parsing
    char*	psz2;		// Temp pointer to ReturnedString
    BOOL	bDone;		// Done Flag
    BOOL	bSectionFound;	// Loop termination flag
    int		nAppNameSize;	// String size of lpAppName;
	WPSectionMap *	my_section_map = NULL;
	LPCSTR			my_value = NULL;

	fprintf(stderr, "GetPrivateProfileSection - entry...\n");

	my_section_map = wp_loadfile(lpFileName);
	if (NULL != my_section_map)
	{
		// extract value for key...
		my_value = getValue(my_section_map, lpAppName, NULL);

		if (NULL != my_value)
		{
			fprintf(stderr, "VALUE RETURNED:[%s]\n", my_value);
		}
		else
		{
			fprintf(stderr, "VALUE IS NULL\n");
		}
	}

    dwLen = 0;

    // The following code was changed to be portable 

    /*
     * Open the specified .INI file.
     */
    pFile = fopen (lpFileName, READ_TEXT_MODE);
    if (pFile == NULL)		// If the file does not exist, return a null string.
    {
	lpReturnedString[0] = 0;
	return dwLen;
    }

    /* 
     * Initialize variables.
     */
    nBuffLen = 512;
    orig_psz = (char*)malloc (nBuffLen);		
    psz = orig_psz;					
    psz2 = lpReturnedString;	
    bInSection = FALSE;
    bDone = FALSE;
    bSectionFound = FALSE;
    nAppNameSize = strlen (lpAppName);

    /*
     * Read each record in the .INI file
     * until the specified section is found.
     */
    do
    {

        /*
         * Get 512 bytes of the record at a time
         * until the entire record has been read.
         */
	n = 0;				// n will be the number of bytes in the record.
	do
	{
	    fgets (&psz[n], 512, pFile); // Get up to 512 bytes of the record.
	    n = strlen (psz);		// Count the number of bytes read so far.
	    if (n+1 == nBuffLen)	// If the input buffer is too small,
					// add another 512 bytes to the input buffer.
	    {
		nBuffLen += 511;
		orig_psz = (char *) realloc(orig_psz, nBuffLen);	
		psz = orig_psz;						
	    }
	}
	while (psz[n -1] != '\n');	// If the end of the record has not been reached,
					// go back and get the next 512 bytes.
	while (psz[0]==' ')		 // Remove leading blanks.
	{
	    psz+=1;
	}	
	n = strlen(psz);

        /*
         * Replace the control characters that end the record, with nulls,
         * then count the number of bytes in the remaining string.
         */
	psz[n-1] = '\0';		// Replace the \n with \0.
	if (n > 1 && psz[n-2] == '\r')		// If the record ends with \r\n ...
	    psz[n-2] = '\0';		// Replace the \r with \0.
	n = strlen(psz);		// Count the bytes in the string.

        /*
         * If this is the start of a section ...
         */
	if (psz[0] == '[' && psz[n-1] == ']') 
	{

            /*
	     * If this is the start of the specified section,
	     * turn on the "in section" flag, then get the next record.
	     */
	    if (nAppNameSize == (n - 2) &&
		strncasecmp (lpAppName, &psz[1], nAppNameSize) == 0) 
	    {
		bInSection = TRUE;
		bSectionFound = TRUE;	// Show that the section was found.
	    }

	    /*
	     * If this is the start of the section after the specified one,
	     * end the loop.
	     */
	    else if (bInSection)
	    {
		bInSection = FALSE;	// Turn off the "in section" flag.
		bDone = TRUE;		// Turn on the "end of loop" flag.
	    }
	}

	/*
	 * If this record is in the specified section,
         * append the record to the return buffer.
	 */
	else if (bInSection)
	{

	    /*
	     * If this is a key=value pair ...
	     */
	    psz1 = strchr (psz, '=');	// Find the '=' character.
	    if (psz1 != NULL)		// If found ...
	    {
		//psz[n - 1] = 0;	// The string was terminated earlier.
		strncpy (&psz2[dwLen], psz, (nSize -2) - dwLen); // Append the record to the return buffer.
		psz2[dwLen+n] = '\0';	// Delimit the records with a null character.
		dwLen = dwLen + n + 1;	// Count the number of bytes being returned.
	    }

	}

    }
    while (!bDone && !feof (pFile));	// If all the keys in the specified section have not been found,
					// go back and get the next record.

    /*	
     * If the specified section was found,
     * terminate the string, and decrement the returned size by one
     * to account for the final delimiter.
     */
    if (bSectionFound)
    {
	dwLen--;
	lpReturnedString[dwLen + 1] = 0;
    }

    /*
     * If the specified section is not in the .INI file, return a null string.
    */
    else
    {
	lpReturnedString[0] = 0;
	dwLen = 0;
    }
    
    /*
     * Clean up before returning to the caller.
     */
    fclose(pFile);
    free (orig_psz);				

	fprintf(stderr, "GetPrivateProfileSection - exit...\n");
    return dwLen;
} // End of GetPrivateProfileSection()


BOOL WINAPI
WritePrivateProfileSection (
	LPCSTR	lpAppName,
	LPCSTR	lpString,
	LPCSTR	lpFileName
	)
{
	fprintf(stderr, "WritePrivateProfileSection - entry...\n");
	fprintf(stderr, "WritePrivateProfileSection - exit...\n");
	return FALSE;
}


DWORD WINAPI
GetPrivateProfileSectionNames (
			       LPSTR	lpszReturnBuffer,
			       DWORD	nSize,
			       LPCSTR	lpFileName)
{
    FILE*	pFile;
    DWORD	dwLen;		// Length of Key String to be returned.
    int		n;		// File Read Buffer Size
    char*	orig_psz;	// File Buffer
    char*	psz;		// File Buffer
    int		nBuffLen;	// File Buffer Size;
    char*	psz1;		// Temp pointer for Key/Value parsing

	fprintf(stderr, "GetPrivateProfileSectionNames - entry...\n");

    dwLen = 0;

    // The following code was changed to be portable

    /*
     * Open the specified .INI file.
     */
    pFile = fopen (lpFileName, READ_TEXT_MODE);
    if (pFile == NULL)		// If the file does not exist, return a null string.
    {
	lpszReturnBuffer[0] = 0;
	return dwLen;
    }


    /* 
     * Initialize variables.
     */
    nBuffLen = 512;
    orig_psz = (char*)malloc (nBuffLen);	
    psz = orig_psz;
    psz1 = lpszReturnBuffer;

    /*
     * Read each record in the .INI file
     * until the specified section is found.
     */
    do
    {

        /*
         * Get 512 bytes of the record at a time
         * until the entire record has been read.
         */
	n = 0;				// n will be the number of bytes in the record.
	do
	{
	    fgets (&psz[n], 512, pFile); // Get up to 512 bytes of the record.

	    n = strlen (psz);		// Count the number of bytes read so far.
	    if (n + 1 == nBuffLen)	// If the input buffer is too small,
					// add another 512 bytes to the input buffer.
	    {
                // 25Feb2003 changed from 512 to 511
		nBuffLen += 511;
		orig_psz = (char*)realloc(orig_psz, nBuffLen);	
		psz = orig_psz;					
	    }
	}
	while (psz[n -1] != '\n');

	while (psz[0]==' ')		 // Remove leading blanks.
	{
	    psz+=1;
	}
	n = strlen(psz);		// Count the bytes in the string.

        /*
         * Replace the control characters that end the record, with nulls,
         * then count the number of bytes in the remaining string.
         */
	psz[n-1] = '\0';		// Replace the \n with \0.
	if (n > 1 && psz[n-2] == '\r')		// If the record ends with \r\n ...
	    psz[n-2] = '\0';		// Replace the \r with \0.
	n = strlen(psz);		// Count the bytes in the string.

        /*
         * If this is the start of a section,
         * append the record to the return buffer.
         */
	if (psz[0] == '[' && psz[n - 1] == ']') 
	{
	    strncpy (&psz1[dwLen], psz, (nSize -2) - dwLen);
	    dwLen = dwLen + n + 1;
	}
    }
    while (!feof (pFile));

    /*
     * If specified section was found, terminate the string and decrement
     * returned size by one to account for the final delimiter.
     */
    dwLen--;
    lpszReturnBuffer[dwLen + 1] = 0;

    /*
     * Clean up before returning to the caller.
     */
    fclose(pFile);
    free (orig_psz);				

	fprintf(stderr, "GetPrivateProfileSectionNames - exit...\n");
    return dwLen;
}


/* This routine adds a a [section name] line to the specified file */

BOOL WINAPI
WritePrivateProfileSectionName (
	LPCSTR	lpAppName,
	LPCSTR	lpFileName)
{
	fprintf(stderr, "WritePrivateProfileSectionName - entry...\n");

    /* If the caller did not pass a section name, return now.     */
    if (lpAppName == NULL)
    {
	return FALSE;
    }

    int		n;		// File Read Buffer Size
    char*	psz;		// File Buffer
    char*	orig_psz;	// original address of File Buffer
    int		nBuffLen;	// File Buffer Size;
    int		nAppNameSize;	// String size of lpAppName;
    FILE*	pFile;
    FILE*	pTmpFile;
    BOOL	bCarriageReturn;	 
    int		nCtrlChar;		

    /* Open the specified INI file.  */
    pFile = fopen (lpFileName, READ_TEXT_MODE);
    if (pFile == NULL)
    {
	return FALSE;
    }

    /* Open a temporary file.  	 */
    pTmpFile = tmpfile();

    /* Initialize temporary file */
    nBuffLen = 512;
    orig_psz = (char*)malloc (nBuffLen);	
    psz = orig_psz;				
    nAppNameSize = strlen (lpAppName);
    bCarriageReturn = FALSE;	
    nCtrlChar = 0;				


    /* Read each record until the specified section is found */
     do
     {
	/* Get 512 bytes of the record at a time until entire record has been read.  */
	 n = 0;
	 do
	 {
	     if (fgets (&psz[n], 512, pFile) == NULL) // Get up to 512 bytes of the record.
	     {
		 // nbartley 6/13/2009 fgets does not clear the buffer from the prev read
		 psz[0] = 0;
		 n = 0;
	     }
	     else
	     {
		 n = strlen (psz);		// Count the number of bytes read so far.
	     }

	     if (n + 1 == nBuffLen)		// If the input buffer is too small,
			// add another 511 bytes to the input buffer.
	     {
		 nBuffLen += 511;
		 orig_psz = (char *)realloc(orig_psz, nBuffLen);
		 psz = orig_psz;					
	     }
	 } while (n != 0 && psz[n -1] != '\n'); // End of "Get the next record in the INI file"

	 while (psz[0]==' ')		// Remove leading blanks.
	 {
	     psz+=1;
	 }   
	 n = strlen (psz);		// Count the number of bytes read so far.

	 /* Replace control characters at end of record with nulls and count bytes remaining */

	 if (n > 1 && psz[n-2] == '\r')		// If the record ends with \r\n ...
	 {
	     bCarriageReturn = TRUE;	/* Set this flag so that when new record is written
				        it will include a CR before the NL character. */
	     nCtrlChar = 2;		// There are two control characters.
	 }
	 else				// If the record ends with \n ...
	 {
	     nCtrlChar = 1;		// There is one control character.
	 }
	 n = strlen(psz);		// Count the bytes in the string.

	/* If this is a "section" record ...  */
	 if (n > 1 && psz[0] == '[' && psz[n - 1 - nCtrlChar] == ']') 
	 {
	     /* If this the specified section, turn on the "found" flag and return. */
	     if (nAppNameSize == (n - 2 - nCtrlChar) && strncasecmp (lpAppName, &psz[1], nAppNameSize) == 0) 
		 return TRUE;
	 }

	 /* If the record is blank, write out just the control characters.  */
	 if (psz[nCtrlChar-1] == '\n')
	 {
	     if (bCarriageReturn)
		 fprintf(pTmpFile, "\r");
	     fprintf(pTmpFile, "\n");
	 }

	/*  If the record is not blank, write it to the temporary file.  */
	 else if (n != 0)
	     fputs(psz, pTmpFile);

	// Loop until the specified section has been located or end of file.
     } while (!feof (pFile));

     // Write the new record, then the control characters.
     fprintf(pTmpFile, "\n[%s]", lpAppName);
     if (bCarriageReturn)
	 fprintf(pTmpFile, "\r");
     fprintf(pTmpFile, "\n");

     /* Reset both file pointers.  */
     pFile = freopen (lpFileName, WRITE_TEXT_MODE, pFile);
     rewind (pTmpFile);

     while (!feof (pTmpFile))
     {
	 if ((n = fread (orig_psz, 1, nBuffLen, pTmpFile)) != 0)
	     fwrite (orig_psz, 1, n, pFile);			
     }

     /* Clean up before returning to the caller.  */
     fclose(pTmpFile);
     fclose(pFile);
     free (orig_psz);		

	fprintf(stderr, "WritePrivateProfileSectionName - exit...\n");
    return TRUE; 
}


BOOL WINAPI
RemovePrivateProfileString (
			   LPCSTR	lpAppName,
			   LPCSTR	lpKeyName,
			   LPCSTR	lpFileName
			   )
{
    DWORD dwLen;		// Length of Key String to be returned.
    int		n;		// File Read Buffer Size
    int		n1;		//
    char*	psz;		// File Buffer
    char*	orig_psz;	// original address of File Buffer
    int		nBuffLen;	// File Buffer Size;
    BOOL	bInSection;	// Section found flag
    char*	psz1;		// Temp pointer for Key/Value parsing
    BOOL	bDone;		// Done Flag
    int		nAppNameSize;	// String size of lpAppName;
    int		nKeyNameSize;	// String size of lpKeyName;
    FILE*	pFile;
    FILE*	pTmpFile;
    BOOL	bCarriageReturn;	 
    BOOL	bNewLine;
    int		nCtrlChar;	// number of control characters for a record	

	fprintf(stderr, "RemovePrivateProfileString - entry...\n");

    // If caller did not pass a section name, return now.
    if (lpAppName == NULL)
	return FALSE;

    // If caller did not pass a key name ...
    if (lpKeyName == NULL)
	    return FALSE;

    // Open specified INI file.
    pFile = fopen (lpFileName, READ_TEXT_MODE);
    if (pFile == NULL)
	return FALSE;
    
    // Open a temporary file.
    pTmpFile = tmpfile();

    // Initialize variables.
    nBuffLen = 512;
    orig_psz = (char*)malloc (nBuffLen);	
    psz = orig_psz;				
    bInSection = FALSE;
    bDone = FALSE;
    nAppNameSize = strlen (lpAppName);
    bCarriageReturn = FALSE;	
    bNewLine = FALSE;
    nCtrlChar = 0;			

#if defined(OS400) 
    // Rewind file so that it is positioned to the beginning of the file.
    rewind(pFile);
#endif

    // Read each record in .INI file until specified section and key are found.
    do
    {
        // Get 512 bytes of the record at a time until the entire record has been read.
	n = 0;
	do
	{
	    if (fgets (&psz[n], 512, pFile) == NULL) // Get up to 512 bytes of the record.
	    {
		// nbartley 6/13/2009 fgets does not clear the buffer from the prev read
		psz[0] = 0;
		n = 0;
	    }
	    else
		n = strlen (psz);		// Count the number of bytes read so far.

	    if (n + 1 == nBuffLen)		// If the input buffer is too small,
	       // add another 511 bytes to the input buffer.
	    {
		nBuffLen += 511;
		orig_psz = (char *)realloc(orig_psz, nBuffLen);
		psz = orig_psz;					
	    }
	} while (n != 0 && psz[n -1] != '\n'); 	// (End of "Get the next record in the INI file")
	while (psz[0]==' ')			// Remove leading blanks.
	{
	    psz+=1;
	}   
	n = strlen (psz);		// Count the number of bytes read so far.

        // Replace control characters at end of record, with nulls then count number
        // of bytes in remaining string.

	if (n > 1 && psz[n-2] == '\r')		// If the record ends with \r\n ...
	{
	    bCarriageReturn = TRUE;	// Set this flag so that when
				// a new record is written, it will include a
				// CR control character before the NL character.
	    nCtrlChar = 2;			// There are two control characters.
	}
	else				// If the record ends with \n ...
	{
	    nCtrlChar = 1;		// There is one control character.
	}
	n = strlen(psz);		// Count the bytes in the string.

	// If this is the start of a section ...
	if (n > 1 && psz[0] == '[' && psz[n - 1 - nCtrlChar] == ']') 
	{
	    // If this is start of specified section, turn on "in section" flag and get next record.
	    if (nAppNameSize == (n - 2 - nCtrlChar) && strncasecmp (lpAppName, &psz[1], nAppNameSize) == 0) 
	    {
		bInSection = TRUE;			// Turn on the "in section" flag.
		nKeyNameSize = strlen (lpKeyName);	// Get the length of the key name.
	    }
	    // If new section and already in specified section, then specified key is not in file 
	    else if (bInSection)
	    {
		bInSection = FALSE;		// Turn off "in section" flag
		bDone = TRUE;			// Turn on "loop is done" flag
	    }
	}

	// If this record is in the specified section ...
	else if (bInSection)
	{
	    // If this is a key=value pair ...
	    psz1 = strchr (psz, '=');	// Find the '=' character.
	    if (psz1 != NULL)		// If found ...
	    {
		n1 = psz1 - psz;		// Compute the length of the key name.
		if (n1 == nKeyNameSize && strncasecmp (lpKeyName, psz, nKeyNameSize) == 0)
		{
		    // If this is the specified key, do not copy the record to temp buffer 
		    bDone = TRUE;		// Turn on the "loop is done" flag.
		    n=0;			// do not want to copy this line
		}
	    }
	}
	// If control characters should be added, write them to temporary file.
	if (bNewLine)
	{
	    if (bCarriageReturn)		
		fputs ("\r", pTmpFile);	
	    fputs ("\n", pTmpFile);
	    bNewLine = FALSE;
	}
	// If record is blank, set a flag.
	if (psz[nCtrlChar-1] == '\n')
	    bNewLine= TRUE;

        // If record is not blank, write it to the temporary file.
	else if (n != 0)
	    fputs(psz, pTmpFile);

    // Loop until specified keyword found or end of file.	
    } while (!bDone && !feof (pFile)); 	

    // If specified section or keyword was not found ...
    if (!bDone)
    {
	return TRUE;
    }

    // If specified section and keyword was found, copy rest of INI file to temporary file.
    while (!feof (pFile))
    {
	if ((n = fread (psz, 1, nBuffLen, pFile)) != 0)
	    fwrite (psz, 1, n, pTmpFile);
    }

    // Reset both file pointers

    pFile = freopen (lpFileName, WRITE_TEXT_MODE, pFile);
    rewind (pTmpFile);

    // Copy temporary file back over the original.
    while (!feof (pTmpFile))
    {
	if ((n = fread (orig_psz, 1, nBuffLen, pTmpFile)) != 0)
	    fwrite (orig_psz, 1, n, pFile);			
    }

    // Clean up before returning
    fclose(pTmpFile);
    fclose(pFile);
    free (orig_psz);		

	fprintf(stderr, "RemovePrivateProfileString - exit...\n");
    return TRUE; 
}


