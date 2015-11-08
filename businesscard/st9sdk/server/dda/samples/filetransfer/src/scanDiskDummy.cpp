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



/**
 * This file implemnets the API between the FileTransfer Sever Application 
 *	and the scan disk dll (black boxes)
 *
 * This implementation does not really check viruses on the file rather it return
 *	a respnse based on the file extension i.e.
 *		SCAN_ERR (1) if the file extension is .err
 *		VIRUS_FOUND (2) if the file extension is .vir
 *		NO_NEED_SCAN (3) if the file extension is is .non
 *		RC_OK (0) otherwise
 *
 * @author Yaron Reinharts mailto:yaronr@il.ibm.com  
 * @since May 17, 2006
 *
 * @owner Lilach Ofek mailto:liofek@il.ibm.com
 * @since May 17, 2006
 */

#include "stDdaApiDefs.h"
#include "scanDiskAPI.h"




using namespace std;
extern "C" {
int ST_DDA_API stDdaFtInit(ScanDiskInitParams *params);
int ST_DDA_API stDdaFtScanFile(ScanParaInfo* info);
void ST_DDA_API stDdaFtTerminate(void);
}

#ifdef OS400
extern "C" {
#endif

/**
 * Initizalize the dll
 */
int ST_DDA_API stDdaFtInit(ScanDiskInitParams *params)
{
	return 0;
}

/**
 * Implement the dummy scan file function
 * 
 * @param info the scan file information
 * 
 * @return	SCAN_ERR		(1) if file extension is err 
 *			VIRUS_FOUND		(2) if file extension is vir
 *			NO_NEED_SCAN	(3) if file extemsion is non
 *			RC_OK			(0) otherwise 
 */
int ST_DDA_API stDdaFtScanFile(ScanParaInfo* info)
{
	// Get the file name
	string filename=info->fileName;
	
	
	// Get the file extension (or the last three chars of the file name
	//	(or the file name if it is less than 3 chars length
	string extension =filename.substr(strlen(info->fileName)-3,3);

	if(!extension.compare("err") )
		return SCAN_ERR;
	if(!extension.compare("vir") )
		return VIRUS_FOUND;
	if(!extension.compare("non") )
		return NO_NEED_SCAN;


	return RC_OK;
}

/**
 * Terminate
 */
void ST_DDA_API stDdaFtTerminate(void)
{
	// Do nothing
}
#ifdef OS400
} // end of extern C
#endif
