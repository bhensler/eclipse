//
// Licensed Materials - Property of IBM
//
// L-MCOS-96LQPJ
//
// (C) Copyright IBM Corp. 2007, 2013. All rights reserved.
//
// US Government Users Restricted Rights- Use, duplication or
// disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
//

// STHelperExampleEventHandler.cpp : Implementation of CSTHelperExampleEventHandler

#include "stdafx.h"
#include "STHelperExampleEventHandler.h"


// CSTHelperExampleEventHandler

void CSTHelperExampleEventHandler::OnPersonUpdate (BSTR key, BSTR partnerId, BSTR alias, long statusCode, BSTR statusText, BSTR location)
{
	MessageBox(NULL,L"OnPersonUpdate",L"OnPersonUpdate",MB_OK);
	return;
}

void CSTHelperExampleEventHandler::OnCapabilityEvent(BSTR partnerId, BSTR capabilityName, BSTR capabilityValue)
{
	MessageBox(NULL,L"OnCapabilityEvent",L"OnCapabilityEvent",MB_OK);
	return;
}

void CSTHelperExampleEventHandler::OnDirectoryResolve(BSTR key, VARIANT maps)
{
	MessageBox(NULL,L"OnDirectoryResolve",L"OnDirectoryResolve",MB_OK);
	return;
}

void CSTHelperExampleEventHandler::OnEvictWatch(BSTR partnerId)
{
	MessageBox(NULL,L"OnEvictWatch",L"OnEvictWatch",MB_OK);
	return;
}

void CSTHelperExampleEventHandler::OnSametimeUnavailable(long * status)
{
	//MessageBox(NULL,L"OnSametimeUnavailable",L"OnSametimeUnavailable",MB_OK);
	return;
}