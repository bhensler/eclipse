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

// STHelperExample.h : main header file for the PROJECT_NAME application
//

#pragma once

#ifndef __AFXWIN_H__
#error "include 'stdafx.h' before including this file for PCH"
#endif

#include "resource.h"		// main symbols
#include "STHelperExample_i.h"


// CSTHelperExampleApp:
// See STHelperExample.cpp for the implementation of this class
//

class CSTHelperExampleApp : public CWinApp
{
public:
	CSTHelperExampleApp();

	// Overrides
public:
	virtual BOOL InitInstance();

	// Implementation

	DECLARE_MESSAGE_MAP()
public:
	BOOL ExitInstance(void);
};

extern CSTHelperExampleApp theApp;