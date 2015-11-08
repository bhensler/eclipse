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

// STHelperExampleDlg.h : header file
//

#pragma once


// CSTHelperExampleDlg dialog
class CSTHelperExampleDlg : public CDialog
{
	// Construction
public:
	CSTHelperExampleDlg(CWnd* pParent = NULL);	// standard constructor

	// Dialog Data
	enum { IDD = IDD_STHELPEREXAMPLE_DIALOG };

protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support


	// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	virtual BOOL OnInitDialog();
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	DECLARE_MESSAGE_MAP()

public:
	afx_msg void OnBnClickedGetcontacts();

private:
	CListBox m_ListContacts;

private:
	//Method that uses the IBM Sametime Helper Library to request
	//the list of IBM Sametime Connect Client contacts, and populate
	//the UI List control with the returned list of contacts.
	void getSametimeContacts();
};
