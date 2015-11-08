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

using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace STHelperExample
{
    class STHelperExampleEventHandler
    {
        //The Sametime Helper object.
        STHelperLib.SametimeHelper stHelper = null;

        public STHelperExampleEventHandler()
        {
            //Create the Sametime Helper object.
            stHelper = new STHelperLib.SametimeHelper();

            //Register the Sametime Helper event callbacks.
            //event _ISametimeHelperEvents_OnPersonUpdateEventHandler
            stHelper.OnPersonUpdate += new STHelperLib._ISametimeHelperEvents_OnPersonUpdateEventHandler(this.OnPersonUpdateEventHandler);

            //event _ISametimeHelperEvents_OnCapabilityEventEventHandler
            stHelper.OnCapabilityEvent += new STHelperLib._ISametimeHelperEvents_OnCapabilityEventEventHandler(this.OnCapabilityEventEventHandler);

            //event _ISametimeHelperEvents_OnDirectoryResolveEventHandler
            stHelper.OnDirectoryResolve += new STHelperLib._ISametimeHelperEvents_OnDirectoryResolveEventHandler(this.OnDirectoryResolveEventHandler);

            //event _ISametimeHelperEvents_OnEvictWatchEventHandler
            stHelper.OnEvictWatch += new STHelperLib._ISametimeHelperEvents_OnEvictWatchEventHandler(this.OnEvictWatchEventHandler);

            //event _ISametimeHelperEvents_OnSametimeUnavailableEventHandler
            stHelper.OnSametimeUnavailable += new STHelperLib._ISametimeHelperEvents_OnSametimeUnavailableEventHandler(this.OnSametimeUnavailableEventHandler);
        }

        //ST Helper event callbacks
        //event _ISametimeHelperEvents_OnPersonUpdateEventHandler
        public void OnPersonUpdateEventHandler(string key,
            string partnerId,
            string alias,
            int statusCode,
            string statusText,
            string location)
        {
            MessageBox.Show("OnPersonUpdateEventHandler");
        }

        //event _ISametimeHelperEvents_OnCapabilityEventEventHandler
        public void OnCapabilityEventEventHandler(string
            partnerId,
            string capabilityName,
            string capabilityValue)
        {
            MessageBox.Show("OnCapabilityEventEventHandler");
        }

        //event _ISametimeHelperEvents_OnDirectoryResolveEventHandler
        public void OnDirectoryResolveEventHandler(string key, object maps)
        {
            MessageBox.Show("OnDirectoryResolveEventHandler");
        }

        //event _ISametimeHelperEvents_OnEvictWatchEventHandler
        public void OnEvictWatchEventHandler(string partnerId)
        {
            MessageBox.Show("OnEvictWatchEventHandler");
        }

        //event _ISametimeHelperEvents_OnSametimeUnavailableEventHandler
        public void OnSametimeUnavailableEventHandler(int status)
        {
            //MessageBox.Show("OnSametimeUnavailableEventHandler");
        }
    }
}
