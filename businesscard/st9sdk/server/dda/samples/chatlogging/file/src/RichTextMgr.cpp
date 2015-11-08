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

#include <RichTextMgr.h>

RichTextMgr* RichTextMgr::m_ptr = 0;

// *****************************************************************
// This class displays support both rich- and plain-text messages in
// PIM APIs that insert messages or modify messages.  
// Allow developer to set format of disclaimers, warnings, etc. by 
// specifying rich-text format for them.
//
// In order to determine if a chat session has rich text capabilities
// or not, the chat logging implementation should check if the first 
// messages that are passed on the chat session indicate that rich 
// text is used. 

// The protocol that is used between chat partners to agree on rich 
// text capabilities is described in:
// Appendix A. "Using the Java Toolkit to send Rich Text and Binary Data"
// of Sametime 8.x Integration Guide
// *****************************************************************

// *****************************************************************
// DESCRIPTION: RichTextMgr::RichTextMgr
// *****************************************************************
RichTextMgr::RichTextMgr(void)
{
}

// *****************************************************************
// DESCRIPTION: RichTextMgr::~RichTextMgr
// *****************************************************************
RichTextMgr::~RichTextMgr(void)
{
}

// *****************************************************************
// DESCRIPTION: RichTextMgr::isRichTextEnabled
// *****************************************************************
bool RichTextMgr::isRichTextEnabled(/*in*/ChatSessionEntry* entry)
{
	bool rc = false;

	if(entry)
	{
		// text counter by default = -1. In case in both size rich text enabled, the counter value is 1
		if(entry->getRichTextCounter() == 1){
			rc = true;
		}
	}

	return rc;
} // end - isRichTextEnabled
