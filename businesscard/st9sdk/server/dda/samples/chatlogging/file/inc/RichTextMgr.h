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



#ifndef __RICHTEXTMGR_H__
#define __RICHTEXTMGR_H__

#define RT_MGR (RichTextMgr::instance())

#include <ChatSessionTable.h>
// *****************************************************************
// The RichTextMgr class is responsible for detect if rich text enabled
// for both clients in 2-way chat
// *****************************************************************

class RichTextMgr
{
public: public: //Ctor + Dtor
	RichTextMgr(void);
	~RichTextMgr(void);

public:// methods
	bool isRichTextEnabled	(/*in*/ChatSessionEntry* entry);

public://Single Tone
	static RichTextMgr*   m_ptr;
	static RichTextMgr& instance()
	{
		if(!m_ptr)
			m_ptr=new RichTextMgr();
		return *m_ptr;
	}
};

#endif //__RICHTEXTMGR_H__
