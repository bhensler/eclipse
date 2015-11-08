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

#ifndef __IMAGE_H__
#define __IMAGE_H__

#include <ChatSessionTable.h>
#include <stDdaClApi.h>

#define IMAGE (Image::instance())


// *****************************************************************
// The Image class is responsible for saving image to the appropriate
// image file.
// *****************************************************************
class Image
{
public: public: //Ctor + Dtor
	Image(void);
	~Image(void);

public:// methods
	int save(/*in*/ ChatSessionEntry* entry,
		/*in*/ const char* sessionId, 
		/*in*/ const StDdaClEntity *sender, 
		/*in*/ unsigned long dataType, 
		/*in*/ unsigned long dataSubType, 
		/*in*/ unsigned long msgLen, 
		/*in*/ const char *msg, 
		/*in*/ const StDdaClEntity *receiver);

public://Single Tone
	static Image*   m_ptr;
	static Image& instance()
	{
		if(!m_ptr)
			m_ptr=new Image();
		return *m_ptr;
	}
};

#endif //__IMAGE_H__
