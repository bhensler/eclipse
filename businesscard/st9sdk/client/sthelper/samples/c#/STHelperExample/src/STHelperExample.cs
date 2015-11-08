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
using System.Windows.Forms;

namespace STHelperExample
{
    static class STHelperExample
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new STHelperExampleDlg());
        }
    }
}