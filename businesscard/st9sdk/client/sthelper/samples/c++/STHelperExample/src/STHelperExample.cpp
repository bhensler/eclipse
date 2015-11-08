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

// STHelperExample.cpp : Defines the class behaviors for the application.
//

#include "stdafx.h"
#include "STHelperExample.h"
#include "STHelperExampleDlg.h"
#include <initguid.h>
#include "STHelperExample_i.c"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CSTHelperExampleApp


class CSTHelperExampleModule :
	public CAtlMfcModule
{
public:
	DECLARE_LIBID(LIBID_STHelperExampleLib);
	DECLARE_REGISTRY_APPID_RESOURCEID(IDR_STHELPEREXAMPLE, "{27E8FCC7-7983-40BA-8666-3E59C9F6D122}");};

	CSTHelperExampleModule _AtlModule;

	BEGIN_MESSAGE_MAP(CSTHelperExampleApp, CWinApp)
		ON_COMMAND(ID_HELP, &CWinApp::OnHelp)
	END_MESSAGE_MAP()


	// CSTHelperExampleApp construction

	CSTHelperExampleApp::CSTHelperExampleApp()
	{
		// TODO: add construction code here,
		// Place all significant initialization in InitInstance
	}


	// The one and only CSTHelperExampleApp object

	CSTHelperExampleApp theApp;


	// CSTHelperExampleApp initialization

	BOOL CSTHelperExampleApp::InitInstance()
	{
		AfxOleInit();
		// Parse command line for standard shell commands, DDE, file open
		CCommandLineInfo cmdInfo;
		ParseCommandLine(cmdInfo);
#if !defined(_WIN32_WCE) || defined(_CE_DCOM)
		// Register class factories via CoRegisterClassObject().
		if (FAILED(_AtlModule.RegisterClassObjects(CLSCTX_LOCAL_SERVER, REGCLS_MULTIPLEUSE)))
			return FALSE;
#endif // !defined(_WIN32_WCE) || defined(_CE_DCOM)
		// App was launched with /Embedding or /Automation switch.
		// Run app as automation server.
		if (cmdInfo.m_bRunEmbedded || cmdInfo.m_bRunAutomated)
		{
			// Don't show the main window
			return TRUE;
		}
		// App was launched with /Unregserver or /Unregister switch.
		if (cmdInfo.m_nShellCommand == CCommandLineInfo::AppUnregister)
		{
			_AtlModule.UpdateRegistryAppId(FALSE);
			_AtlModule.UnregisterServer(TRUE);
			return FALSE;
		}
		// App was launched with /Register or /Regserver switch.
		if (cmdInfo.m_nShellCommand == CCommandLineInfo::AppRegister)
		{
			_AtlModule.UpdateRegistryAppId(TRUE);
			_AtlModule.RegisterServer(TRUE);
			return FALSE;
		}
		CWinApp::InitInstance();

		// Standard initialization
		// If you are not using these features and wish to reduce the size
		// of your final executable, you should remove from the following
		// the specific initialization routines you do not need
		// Change the registry key under which our settings are stored
		// TODO: You should modify this string to be something appropriate
		// such as the name of your company or organization
		SetRegistryKey(_T("Local AppWizard-Generated Applications"));

		CSTHelperExampleDlg dlg;
		m_pMainWnd = &dlg;
		INT_PTR nResponse = dlg.DoModal();

		// Now we will intilize COM
		HRESULT hr = CoInitialize(NULL);

		if (SUCCEEDED(hr))
		{
			return TRUE;
		}
		else
		{
			return FALSE;
		}
	}

	BOOL CSTHelperExampleApp::ExitInstance(void)
	{
#if !defined(_WIN32_WCE) || defined(_CE_DCOM)
		_AtlModule.RevokeClassObjects();
#endif

		// Uninitialize COM
		CoUninitialize();

		return CWinApp::ExitInstance();
	}
