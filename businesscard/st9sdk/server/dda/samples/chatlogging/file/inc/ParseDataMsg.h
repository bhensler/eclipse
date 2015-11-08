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

#ifndef __PARSEDATAMSG_H__
#define __PARSEDATAMSG_H__

#define PARSE_MSG (ParseDataMsg::instance())


// *****************************************************************
// The ParseDataMsg class is responsible for:
// • Deciding if the data message contains an image
// • Deciding if the data message contains an attribute of rich text
// *****************************************************************

class ParseDataMsg
{
public: //Ctor + Dtor
	ParseDataMsg(void);
	~ParseDataMsg(void);

public:// methods
	const bool  isImageMsg(unsigned long dataType, 
		unsigned long dataSubType,
		unsigned long msgLen,
		const char *msg);
	const bool  isRichTextMsg(unsigned long dataType, 
		unsigned long dataSubType,
		unsigned long msgLen,
		const char *msg);

public://Single Tone
	static ParseDataMsg*   m_ptr;
	static ParseDataMsg& instance()
	{
		if(!m_ptr)
			m_ptr=new ParseDataMsg();
		return *m_ptr;
	}
};


#endif //__PARSEDATAMSG_H__
