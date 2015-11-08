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

// STHelperExampleEventHandler.h : Declaration of the CSTHelperExampleEventHandler

#pragma once
#include "resource.h"       // main symbols

#include "STHelperExample.h"


#if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
#error "Single-threaded COM objects are not properly supported on Windows CE platform, such as the Windows Mobile platforms that do not include full DCOM support. Define _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA to force ATL to support creating single-thread COM object's and allow use of it's single-threaded COM object implementations. The threading model in your rgs file was set to 'Free' as that is the only threading model supported in non DCOM Windows CE platforms."
#endif



// CSTHelperExampleEventHandler

//Define an ATL _ATL_FUNC_INFO for each of the _ISametimeHelperEvents
//dispinterface event functions.
_ATL_FUNC_INFO OnPersonUpdateInfo = { CC_STDCALL, VT_EMPTY, 6, { VT_BSTR, VT_BSTR, VT_BSTR, VT_I4,  VT_BSTR, VT_BSTR} };
_ATL_FUNC_INFO OnCapabilityInfo = {CC_STDCALL, VT_EMPTY, 3, {VT_BSTR, VT_BSTR, VT_BSTR}};
_ATL_FUNC_INFO OnDirectoryResolveInfo = { CC_STDCALL, VT_EMPTY, 2, {VT_BSTR, VT_VARIANT} };
_ATL_FUNC_INFO OnEvictWatchInfo = {CC_STDCALL, VT_EMPTY, 1, {VT_BSTR}};
_ATL_FUNC_INFO OnSametimeUnavailableInfo = {CC_STDCALL, VT_EMPTY, 1, {VT_I4}};

//Define constants for each of the _ISametimeHelperEvents
//ATL dispinterface event function ids.
static const long STHELPER_EVENT_DISPID_LIVENAME_RESOLVE     = 1;
static const long STHELPER_EVENT_DISPID_CAPABILITY           = 7;
static const long STHELPER_EVENT_DISPID_DIRECTORY_RESOLVE    = 3;
static const long STHELPER_EVENT_DISPID_EVICT_WATCH          = 8;
static const long STHELPER_EVENT_DISPID_SAMETIME_UNAVAILABLE = 4;

//Define a constant for each of the _ISametimeHelperEvents events.
static const unsigned int STHELPER_EVENT_LIVENAME_RESOLVE     = 1000;
static const unsigned int STHELPER_EVENT_CAPABILITY_EVENT     = STHELPER_EVENT_LIVENAME_RESOLVE + 1;
static const unsigned int STHELPER_EVENT_DIRECTORY_RESOLVE    = STHELPER_EVENT_CAPABILITY_EVENT + 1;
static const unsigned int STHELPER_EVENT_EVICT_WATCH          = STHELPER_EVENT_DIRECTORY_RESOLVE + 1;
static const unsigned int STHELPER_EVENT_SAMETIME_UNAVAILABLE = STHELPER_EVENT_EVICT_WATCH + 1;

class ATL_NO_VTABLE CSTHelperExampleEventHandler :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CSTHelperExampleEventHandler, &CLSID_STHelperExampleEventHandler>,
	public IDispatchImpl<ISTHelperExampleEventHandler, &IID_ISTHelperExampleEventHandler, &LIBID_STHelperExampleLib, /*wMajor =*/ 1, /*wMinor =*/ 0>,
	public IDispatchImpl<_ISametimeHelperEvents, &__uuidof(_ISametimeHelperEvents), &LIBID_STHelperLib, /* wMajor = */ 1>,

	//Add an ATL IDispEventSimpleImpl reference for each of the _ISametimeHelperEvents events.
	public IDispEventSimpleImpl<STHELPER_EVENT_LIVENAME_RESOLVE, CSTHelperExampleEventHandler,
	&__uuidof(::STHelperLib::_ISametimeHelperEvents)>,
	public IDispEventSimpleImpl<STHELPER_EVENT_CAPABILITY_EVENT, CSTHelperExampleEventHandler,
	&__uuidof(::STHelperLib::_ISametimeHelperEvents)>,
	public IDispEventSimpleImpl<STHELPER_EVENT_DIRECTORY_RESOLVE, CSTHelperExampleEventHandler,
	&__uuidof(::STHelperLib::_ISametimeHelperEvents)>,
	public IDispEventSimpleImpl<STHELPER_EVENT_EVICT_WATCH, CSTHelperExampleEventHandler,
	&__uuidof(::STHelperLib::_ISametimeHelperEvents)>,
	public IDispEventSimpleImpl<STHELPER_EVENT_SAMETIME_UNAVAILABLE, CSTHelperExampleEventHandler,
	&__uuidof(::STHelperLib::_ISametimeHelperEvents)>
{
public:
	CSTHelperExampleEventHandler()
	{
	}

	DECLARE_REGISTRY_RESOURCEID(IDR_STHELPEREXAMPLEEVENTHANDLER)

	BEGIN_COM_MAP(CSTHelperExampleEventHandler)
		COM_INTERFACE_ENTRY(ISTHelperExampleEventHandler)
		COM_INTERFACE_ENTRY2(IDispatch, _ISametimeHelperEvents)
		COM_INTERFACE_ENTRY(_ISametimeHelperEvents)
	END_COM_MAP()

	//Add the ATL SINK_MAP entries for the
	//ISametimeHelperEvents dispinterface events.
	BEGIN_SINK_MAP(CSTHelperExampleEventHandler)
		SINK_ENTRY_INFO(STHELPER_EVENT_LIVENAME_RESOLVE, __uuidof(::STHelperLib::_ISametimeHelperEvents),
		/*dispid*/ STHELPER_EVENT_DISPID_LIVENAME_RESOLVE, OnPersonUpdate, &OnPersonUpdateInfo)

		SINK_ENTRY_INFO(STHELPER_EVENT_CAPABILITY_EVENT, __uuidof(::STHelperLib::_ISametimeHelperEvents),
		/*dispid*/ STHELPER_EVENT_DISPID_CAPABILITY, OnCapabilityEvent, &OnCapabilityInfo)

		SINK_ENTRY_INFO(STHELPER_EVENT_DIRECTORY_RESOLVE, __uuidof(::STHelperLib::_ISametimeHelperEvents),
		/*dispid*/ STHELPER_EVENT_DISPID_DIRECTORY_RESOLVE, OnDirectoryResolve, &OnDirectoryResolveInfo)

		SINK_ENTRY_INFO(STHELPER_EVENT_EVICT_WATCH, __uuidof(::STHelperLib::_ISametimeHelperEvents),
		/*dispid*/ STHELPER_EVENT_DISPID_EVICT_WATCH, OnEvictWatch, &OnEvictWatchInfo)

		SINK_ENTRY_INFO(STHELPER_EVENT_SAMETIME_UNAVAILABLE, __uuidof(::STHelperLib::_ISametimeHelperEvents),
		/*dispid*/ STHELPER_EVENT_DISPID_SAMETIME_UNAVAILABLE, OnSametimeUnavailable, &OnSametimeUnavailableInfo)
	END_SINK_MAP()

	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
		//Create the Sametime Helper object.
		HRESULT hr = pSTHelper.CreateInstance(__uuidof(STHelperLib::SametimeHelper));

	    //Establish the ATL connections for the
		//ISametimeHelperEvents dispinterface events.
		hr = IDispEventSimpleImpl<STHELPER_EVENT_LIVENAME_RESOLVE, CSTHelperExampleEventHandler,
			&__uuidof(STHelperLib::_ISametimeHelperEvents)>::DispEventAdvise(pSTHelper);
		hr = IDispEventSimpleImpl<STHELPER_EVENT_CAPABILITY_EVENT, CSTHelperExampleEventHandler,
			&__uuidof(STHelperLib::_ISametimeHelperEvents)>::DispEventAdvise(pSTHelper);
		hr = IDispEventSimpleImpl<STHELPER_EVENT_DIRECTORY_RESOLVE, CSTHelperExampleEventHandler,
			&__uuidof(STHelperLib::_ISametimeHelperEvents)>::DispEventAdvise(pSTHelper);
		hr = IDispEventSimpleImpl<STHELPER_EVENT_EVICT_WATCH, CSTHelperExampleEventHandler,
			&__uuidof(STHelperLib::_ISametimeHelperEvents)>::DispEventAdvise(pSTHelper);
		hr = IDispEventSimpleImpl<STHELPER_EVENT_SAMETIME_UNAVAILABLE, CSTHelperExampleEventHandler,
			&__uuidof(STHelperLib::_ISametimeHelperEvents)>::DispEventAdvise(pSTHelper);
		return S_OK;
	}

	void FinalRelease()
	{
		//Break the ATL connections for the
		//ISametimeHelperEvents dispinterface events.
		HRESULT hr = IDispEventSimpleImpl<STHELPER_EVENT_LIVENAME_RESOLVE, CSTHelperExampleEventHandler,
			&__uuidof(STHelperLib::_ISametimeHelperEvents)>::DispEventUnadvise(pSTHelper);
		hr = IDispEventSimpleImpl<STHELPER_EVENT_CAPABILITY_EVENT, CSTHelperExampleEventHandler,
			&__uuidof(STHelperLib::_ISametimeHelperEvents)>::DispEventUnadvise(pSTHelper);
		hr = IDispEventSimpleImpl<STHELPER_EVENT_DIRECTORY_RESOLVE, CSTHelperExampleEventHandler,
			&__uuidof(STHelperLib::_ISametimeHelperEvents)>::DispEventUnadvise(pSTHelper);
		hr = IDispEventSimpleImpl<STHELPER_EVENT_EVICT_WATCH, CSTHelperExampleEventHandler,
			&__uuidof(STHelperLib::_ISametimeHelperEvents)>::DispEventUnadvise(pSTHelper);
		hr = IDispEventSimpleImpl<STHELPER_EVENT_SAMETIME_UNAVAILABLE, CSTHelperExampleEventHandler,
			&__uuidof(STHelperLib::_ISametimeHelperEvents)>::DispEventUnadvise(pSTHelper);
	}

private:
	//Sametime Helper object.
	STHelperLib::ISametimeHelperPtr pSTHelper;

	// _ISametimeHelperEvents Methods
public:
	STDMETHOD_(void, OnPersonUpdate)(BSTR key, BSTR partnerId, BSTR alias, long statusCode, BSTR statusText, BSTR location);
	STDMETHOD_(void, OnCapabilityEvent)(BSTR partnerId, BSTR capabilityName, BSTR capabilityValue);
	STDMETHOD_(void, OnDirectoryResolve)(BSTR key, VARIANT maps);
	STDMETHOD_(void, OnEvictWatch)(BSTR partnerId);
	STDMETHOD_(void, OnSametimeUnavailable)(long * status);
};

OBJECT_ENTRY_AUTO(__uuidof(STHelperExampleEventHandler), CSTHelperExampleEventHandler)
