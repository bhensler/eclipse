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

#include <ParseDataMsg.h>

ParseDataMsg* ParseDataMsg::m_ptr = 0;

// *****************************************************************
// DESCRIPTION: ParseDataMsg::ParseDataMsg
// *****************************************************************
ParseDataMsg::ParseDataMsg(void)
{
}

// *****************************************************************
// DESCRIPTION: ParseDataMsg::~ParseDataMsg
// *****************************************************************
ParseDataMsg::~ParseDataMsg(void)
{
}

// *****************************************************************
// DESCRIPTION: ParseDataMsg::isImageMsg
// The ParseDataMsg::isImageMsg function checks if the data message contains an image.
// *****************************************************************
const bool ParseDataMsg::isImageMsg(unsigned long dataType, 
									unsigned long dataSubType,
									unsigned long msgLen,
									const char *msg)
{
	bool result = false;
	// check the data type and subtype
	if(dataType == 27191 && dataSubType == 0 && msgLen > 13) {
		// the image buffer prefix should be:
		// 0 4 100 97 116 97 0 5 105 109 97 103 101
		//      d   a  t  a       i   m  a   g   e
		if(msg[2] == 'd' 
			&& msg[3] == 'a'
			&& msg[4] == 't'
			&& msg[5] == 'a'
			&& msg[8] == 'i'
			&& msg[9] == 'm'
			&& msg[10] == 'a'
			&& msg[11] == 'g'
			&& msg[12] == 'e') 
		{
			result = true;      
		}
	}
	return result;
} // end ParseDataMsg::isImageMsg

// *****************************************************************
// DESCRIPTION: ParseDataMsg::isRichTextMsg
// This function checks if the data message contains rich text attribute.
// The detection of rich text is done by probing a data message.
// A data message consists of the following parameters:
// dataType, dataSubType, msg, msgLen
// 
// dataType == 27191
// dataSubType == 0
// msgLen == 17
// 
// If the 17 bytes of the message msg is formatted like so:
// 0 4 64 61 74 61 0 08 72 69 63 68 74 65 78 74 ee / df   
// (ASCII value of this msg starting from byte 2 to 5 is "data" and from 8 to 15 is "richtext")
// 
// Now we can determine if rich text is supported by the last byte, byte 16 (msg[16]):
// 0xEE  -  Supported
// 0xDF  - Unsupported
//
// Note, the reference to bytes position is relative to 0 which is the first byte.
// Returns true if rich text enabled
// *****************************************************************
const bool ParseDataMsg::isRichTextMsg(unsigned long dataType, 
									   unsigned long dataSubType,
									   unsigned long msgLen,
									   const char *msg)
{
	bool result = false;
	// check the data type
	if(dataType == 27191 && dataSubType == 0 && msgLen == 17) {
		// the image buffer prefix should be:
		// 0 4 64 61 74 61 0 08 72 69 63 68 74 65 78 74 ee / df
		//     d  a  t  a       r  i  c  h  t  e  x  t
		// EE - enabled
		// DF - disabled

		if(msg[2] == 'd' 
			&& msg[3] == 'a'
			&& msg[4] == 't'
			&& msg[5] == 'a'
			&& msg[8] == 'r'
			&& msg[9] == 'i'
			&& msg[10] == 'c'
			&& msg[11] == 'h'
			&& msg[12] == 't'
			&& msg[13] == 'e'
			&& msg[14] == 'x'
			&& msg[15] == 't') 
		{
			//char *ee = new char[1];
			//ee[0]= 0xee;
			if (msg[16] == (char)0xee){
				result = true;
			} else if (msg[16] == (char)0xdf){
				result = false;
			}
		}
	}
	return result;
} // end ParseDataMsg::isRichTextMsg