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

#include <Image.h>
#include <stDdaClCodes.h>
#include <ChatResource.h>

Image* Image::m_ptr = 0;

// *****************************************************************
// DESCRIPTION: Image::Image
// *****************************************************************
Image::Image(void)
{
}

// *****************************************************************
// DESCRIPTION: Image::~Image
// *****************************************************************
Image::~Image(void)
{
}

// *****************************************************************
// DESCRIPTION: Image::save.
// This function saves the image to the appropriate image file.
// *****************************************************************
int Image::save(/*in*/ ChatSessionEntry* entry,
				/*in*/ const char* sessionId,
				/*in*/ const StDdaClEntity *sender, 
				/*in*/ unsigned long dataType, 
				/*in*/ unsigned long dataSubType, 
				/*in*/ unsigned long msgLen, 
				/*in*/ const char *msg, 
				/*in*/ const StDdaClEntity *receiver)
{
	int	rc = ST_DDA_API_OK;

	if(entry)
	{
		rc = entry->saveImage(msgLen, msg);
		char tmp[1000];
		memset(&tmp, 0,	1000);
		
		int	dbg	=	CHAT_RSC.getDebugFlagValue("BB_CL_TRACE");
		const	char*	fileName = entry->getImageFilePath();

		if(rc	!= ST_DDA_CL_DB_ERROR) 
		{
			sprintf(&tmp[0], "image	received and saved to: [%s]",	fileName);
			rc = stDdaClSessionMsg(sessionId,	sender,	strlen((const	char*)(&tmp)),	(const char*)(&tmp), receiver);
		}	
		else 
		{
			CHAT_RSC.print(dbg,"DB Error,	Can't	save image [%s]",	fileName);
		}
	}

	return rc;
} // end Image::save
