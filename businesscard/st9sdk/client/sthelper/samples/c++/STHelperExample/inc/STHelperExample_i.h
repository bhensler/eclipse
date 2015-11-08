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

/* this ALWAYS GENERATED file contains the definitions for the interfaces */


 /* File created by MIDL compiler version 6.00.0366 */
/* at Tue Sep 04 14:04:55 2007
 */
/* Compiler settings for .\STHelperExample.idl:
    Oicf, W1, Zp8, env=Win32 (32b run)
    protocol : dce , ms_ext, c_ext
    error checks: allocation ref bounds_check enum stub_data
    VC __declspec() decoration level:
         __declspec(uuid()), __declspec(selectany), __declspec(novtable)
         DECLSPEC_UUID(), MIDL_INTERFACE()
*/
//@@MIDL_FILE_HEADING(  )

#pragma warning( disable: 4049 )  /* more than 64k source lines */


/* verify that the <rpcndr.h> version is high enough to compile this file*/
#ifndef __REQUIRED_RPCNDR_H_VERSION__
#define __REQUIRED_RPCNDR_H_VERSION__ 440
#endif

#include "rpc.h"
#include "rpcndr.h"

#ifndef __RPCNDR_H_VERSION__
#error this stub requires an updated version of <rpcndr.h>
#endif // __RPCNDR_H_VERSION__

#ifndef COM_NO_WINDOWS_H
#include "windows.h"
#include "ole2.h"
#endif /*COM_NO_WINDOWS_H*/

#ifndef __STHelperExample_i_h__
#define __STHelperExample_i_h__

#if defined(_MSC_VER) && (_MSC_VER >= 1020)
#pragma once
#endif

/* Forward Declarations */

#ifndef __ISTHelperExampleEventHandler_FWD_DEFINED__
#define __ISTHelperExampleEventHandler_FWD_DEFINED__
typedef interface ISTHelperExampleEventHandler ISTHelperExampleEventHandler;
#endif 	/* __ISTHelperExampleEventHandler_FWD_DEFINED__ */


#ifndef __STHelperExampleEventHandler_FWD_DEFINED__
#define __STHelperExampleEventHandler_FWD_DEFINED__

#ifdef __cplusplus
typedef class STHelperExampleEventHandler STHelperExampleEventHandler;
#else
typedef struct STHelperExampleEventHandler STHelperExampleEventHandler;
#endif /* __cplusplus */

#endif 	/* __STHelperExampleEventHandler_FWD_DEFINED__ */


/* header files for imported files */
#include "oaidl.h"
#include "ocidl.h"

#ifdef __cplusplus
extern "C"{
#endif

void * __RPC_USER MIDL_user_allocate(size_t);
void __RPC_USER MIDL_user_free( void * );

#ifndef __ISTHelperExampleEventHandler_INTERFACE_DEFINED__
#define __ISTHelperExampleEventHandler_INTERFACE_DEFINED__

/* interface ISTHelperExampleEventHandler */
/* [unique][helpstring][nonextensible][dual][uuid][object] */


EXTERN_C const IID IID_ISTHelperExampleEventHandler;

#if defined(__cplusplus) && !defined(CINTERFACE)

    MIDL_INTERFACE("463C8B2D-EAAC-4BD4-96B7-E7F5D00CC5AA")
    ISTHelperExampleEventHandler : public IDispatch
    {
    public:
    };

#else 	/* C style interface */

    typedef struct ISTHelperExampleEventHandlerVtbl
    {
        BEGIN_INTERFACE

        HRESULT ( STDMETHODCALLTYPE *QueryInterface )(
            ISTHelperExampleEventHandler * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ void **ppvObject);

        ULONG ( STDMETHODCALLTYPE *AddRef )(
            ISTHelperExampleEventHandler * This);

        ULONG ( STDMETHODCALLTYPE *Release )(
            ISTHelperExampleEventHandler * This);

        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )(
            ISTHelperExampleEventHandler * This,
            /* [out] */ UINT *pctinfo);

        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )(
            ISTHelperExampleEventHandler * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);

        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )(
            ISTHelperExampleEventHandler * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);

        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )(
            ISTHelperExampleEventHandler * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);

        END_INTERFACE
    } ISTHelperExampleEventHandlerVtbl;

    interface ISTHelperExampleEventHandler
    {
        CONST_VTBL struct ISTHelperExampleEventHandlerVtbl *lpVtbl;
    };



#ifdef COBJMACROS


#define ISTHelperExampleEventHandler_QueryInterface(This,riid,ppvObject)	\
    (This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define ISTHelperExampleEventHandler_AddRef(This)	\
    (This)->lpVtbl -> AddRef(This)

#define ISTHelperExampleEventHandler_Release(This)	\
    (This)->lpVtbl -> Release(This)


#define ISTHelperExampleEventHandler_GetTypeInfoCount(This,pctinfo)	\
    (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo)

#define ISTHelperExampleEventHandler_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo)

#define ISTHelperExampleEventHandler_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)

#define ISTHelperExampleEventHandler_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)


#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ISTHelperExampleEventHandler_INTERFACE_DEFINED__ */



#ifndef __STHelperExampleLib_LIBRARY_DEFINED__
#define __STHelperExampleLib_LIBRARY_DEFINED__

/* library STHelperExampleLib */
/* [helpstring][version][uuid] */


EXTERN_C const IID LIBID_STHelperExampleLib;

EXTERN_C const CLSID CLSID_STHelperExampleEventHandler;

#ifdef __cplusplus

class DECLSPEC_UUID("D5EC9242-33D8-41A4-A717-B1A021E27F5B")
STHelperExampleEventHandler;
#endif
#endif /* __STHelperExampleLib_LIBRARY_DEFINED__ */

/* Additional Prototypes for ALL interfaces */

/* end of Additional Prototypes */

#ifdef __cplusplus
}
#endif

#endif


