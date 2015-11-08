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

// STHelperExampleDlg.cpp : implementation file
//

#include "stdafx.h"
#include "STHelperExample.h"
#include "STHelperExampleDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CSTHelperExampleDlg dialog




CSTHelperExampleDlg::CSTHelperExampleDlg(CWnd* pParent /*=NULL*/)
: CDialog(CSTHelperExampleDlg::IDD, pParent)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CSTHelperExampleDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_LISTCONTACTS, m_ListContacts);
}

BEGIN_MESSAGE_MAP(CSTHelperExampleDlg, CDialog)
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	//}}AFX_MSG_MAP
	ON_BN_CLICKED(IDC_GETCONTACTS, &CSTHelperExampleDlg::OnBnClickedGetcontacts)
END_MESSAGE_MAP()


// CSTHelperExampleDlg message handlers

BOOL CSTHelperExampleDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// Set the icon for this dialog. The framework does
	// this automatically when the application's main
	// window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon

    // Declare the STHelperExampleEventHandler pointer.
	ISTHelperExampleEventHandler *pSTHelperExampleEventHandler = NULL;

	// Create the STHelperExampleEventHandler interface to handle
	// the Sametime Helper related events.
	HRESULT hr = CoCreateInstance(
			CLSID_STHelperExampleEventHandler,
			NULL,
			CLSCTX_INPROC_SERVER,
			IID_ISTHelperExampleEventHandler,
			(void**) &pSTHelperExampleEventHandler);

	// Return for successful initialization, false otherwise.
	if (SUCCEEDED(hr))
	{
		return TRUE;
	}
	else
	{
		return FALSE;
	}
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CSTHelperExampleDlg::OnPaint()
{
	if (IsIconic())
	{
		CPaintDC dc(this); // device context for painting

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// Center icon in client rectangle
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Draw the icon
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

// The system calls this function to obtain the cursor to display while the user drags
//  the minimized window.
HCURSOR CSTHelperExampleDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}


void CSTHelperExampleDlg::OnBnClickedGetcontacts()
{
	getSametimeContacts();
}

//Method that uses the IBM Sametime Helper Library to request
//the list of IBM Sametime Connect Client contacts, and populate
//the UI List control with the returned list of contacts.
void CSTHelperExampleDlg::getSametimeContacts()
{
	//Sametime Helper object.
	STHelperLib::ISametimeHelperPtr pSTHelper;

	//Create the Sametime Helper object.
	HRESULT hr = pSTHelper.CreateInstance(__uuidof(STHelperLib::SametimeHelper));

	if (hr == S_OK)
	{
		//Sametime Group type.
		BSTR bstrGroupType = L"all";

		//Define array to hold Sametime Contact Group names.
		VARIANT vGroupsArray;
		vGroupsArray.vt = VT_VARIANT;

		//Invoke the Sametime Helper to populate the group names array.
		hr = pSTHelper->GetSametimeGroups(bstrGroupType, &vGroupsArray);

		if (hr == S_OK)
		{
			//Pointer for accessible array of groups.
			BSTR HUGEP *pbstrGroups;
			//Assign the groups array pointer.
			SAFEARRAY *psaGroups = vGroupsArray.parray;
			//Get a pointer to the elements of the groups array.
			hr = SafeArrayAccessData(psaGroups, (void HUGEP**)&pbstrGroups);

			if (hr == S_OK)
			{
				//Loop through the Sametime Contact Group names and retrieve
				//the Sametime Contact names for each group.
				for (ULONG i = 0; i < psaGroups->rgsabound->cElements; i++)
				{
					//Assign the current group name.
					BSTR bstrGroupName = pbstrGroups[i];

					//Define array to hold Sametime Contact Contact
					//names for the current group.
					VARIANT vContactsArray;
					vContactsArray.vt = VT_VARIANT;

					//Invoke the Sametime Helper to populate the contact
					//names array for the current group.
					hr = pSTHelper->GetContacts(bstrGroupName, &vContactsArray);

					if (hr == S_OK)
					{
						//Pointer for accessible array of groups.
						BSTR HUGEP *pbstrContacts;
						//Assign the array that has the current group contact names.
						SAFEARRAY *psaContacts = vContactsArray.parray;
						//Get a pointer to the elements of the contacts array.
						hr = SafeArrayAccessData(psaContacts, (void HUGEP**)&pbstrContacts);

						if (hr == S_OK)
						{
							//Loop through the Sametime Contact names and add each name
							//to the List control.
							for (ULONG j = 0; j < psaContacts->rgsabound->cElements; j++)
							{
								//Add the contact name to the List control.
								m_ListContacts.AddString(pbstrContacts[j]);
							}

							//Release the contacts array pointer.
							SafeArrayUnaccessData(psaContacts);
						}
					}
				}

				//Release the groups array pointer.
				SafeArrayUnaccessData(psaGroups);
			}
		}
	}
}
