/*
 * Licensed Materials - Property of IBM
 *
 * L-MCOS-96LQPJ
 *
 * (C) Copyright IBM Corp. 2013. All rights reserved.
 *
 * US Government Users Restricted Rights- Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */


#ifndef __SCAN_DISK_API_H__
#define __SCAN_DISK_API_H__

#include <stDdaApiDefs.h>
#include <fstream>


/**
 * This file defines the API between the FileTransfer Sever Application 
 *	and the scan disk dll (black boxes)
 *
 *
 * @author Yaron Reinharts mailto:yaronr@il.ibm.com  
 * @since May 17, 2006
 *
 * @owner Lilach Ofek mailto:liofek@il.ibm.com
 * @since May 17, 2006
 */


// define max user name length
#define MAX_USER_NAME_LENGTH 256

// define max path length
#define MAX_PATH_NAME_SIZE 256

// define max buffer size
#define MAX_BUF_SIZE 256

// define current version for further compatibility
#define ST_DDA_FT_LIB_VERSION    12

/**
 * Define the scan disk dll name
 */
#ifdef OS400
#define SCAN_DISK_DLL_NAME "STFILETRAN"
#else
#define SCAN_DISK_DLL_NAME "stfiletransfer"
#endif


/**
 * Method names in scandisk dll 
 */
#define SCAN_DISK_INIT_NAME "stDdaFtInit"
#define SCAN_DISK_SCAN_FILE_NAME "stDdaFtScanFile"
#define SCAN_DISK_TERMINATE_NAME "stDdaFtTerminate"


/**
 * typedef the scan disk dll init parameters
 */
typedef struct _ScanDiskInitParams 
{
	// The ScanFile version
	int prVersion;

} ScanDiskInitParams;


/**
 * typedef st session needed by the scan disk for statistics
 */
typedef struct _STSession
{
	// sender name
	char sender_usename[MAX_USER_NAME_LENGTH];		

	// receiver name
	char  receiver_username[MAX_USER_NAME_LENGTH];	

	// session ID (channel ID)
	unsigned long  session_id;						
} STSession;


/**
 * typedef the scan disk dll parameters
 */
typedef struct _ScanDiskInfo
{
	// pointer to the scanned file
	std::fstream* file;							
	
	// file name
	char fileName[MAX_PATH_NAME_SIZE];			
	
	// file size (bytes)
	int fileSize;		

	// fstream size	(bytes)
	int	fstreamSize;	

	// // virus type
	int	virustype;	

	// virus description
	char virustypedescription[MAX_BUF_SIZE];	

	// trend needs it for some kind of traces	
	STSession session;							
} ScanParaInfo;



	
/**
 * typedef the scan disk dll init method
 */
typedef int (ST_DDA_CALLBACK* DLL_FUNC_ST_DDA_FT_INIT) 
				(ScanDiskInitParams *params);

/**
 * Define return values for scan file
 */
enum VirusScanRC
{
	RC_OK        = 0, 
	SCAN_ERR     = 1,
	VIRUS_FOUND  = 2,
	NO_NEED_SCAN = 3
};

/**
 * typedef the scan disk dll scanFile method
 */
typedef int (ST_DDA_CALLBACK* DLL_FUNC_ST_DDA_FT_SCANFILE)
		(ScanParaInfo *info);


/**
 * typedef the scan disk dll terminate method
 */
typedef void (ST_DDA_CALLBACK* DLL_FUNC_ST_DDA_FT_TERMINATE) (void);


/**
 * typedef a structure which holds references to the scaDisk API methods
 */
typedef struct _StDdaFtCallbacks
{
		DLL_FUNC_ST_DDA_FT_INIT initFunc;
		DLL_FUNC_ST_DDA_FT_TERMINATE termFunc;
		DLL_FUNC_ST_DDA_FT_SCANFILE scanFileFunc;
} StDdaFtCallbacks;

#endif //__SCAN_DISK_API_H__
