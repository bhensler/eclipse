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
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace STHelperExample
{
    public partial class STHelperExampleDlg : Form
    {   
        // The Sametime Helper event handler.
        STHelperExampleEventHandler stHelperEventHandler = null;

        public STHelperExampleDlg()
        {
            InitializeComponent();

            // Create the STHelperExampleEventHandler interface to handle
            // the Sametime Helper related events.
            stHelperEventHandler = new STHelperExampleEventHandler();
       }

        private void btnContacts_Click(object sender, EventArgs e)
        {
            getSametimeContacts();
        }

        //Method that uses the IBM Sametime Helper Library to request
        //the list of IBM Sametime Connect Client contacts, and populate
        //the UI List control with the returned list of contacts.
        private void getSametimeContacts()
        {
            //Create the Sametime Helper object.
            STHelperLib.SametimeHelper stHelper = new STHelperLib.SametimeHelper();

            //Sametime Group type
            String strGroupType = "all";
            //Define an object to hold Sametime Contact Group names.
            Object objGroups = null;

            //Invoke the Sametime Helper to populate the group names object.
            stHelper.GetSametimeGroups(strGroupType, out objGroups);

            if (objGroups != null)
            {
                //Assign the array that has the Sametime Contact Group names.
                Array arrGroups = ((Array)objGroups);

                //Loop through the Sametime Contact Group names and retrieve
                //the Sametime Contact names for each group.
                for (int i = 0; i < arrGroups.Length; i++)
                {
                    //Assign the current group name.
                    String strGroupName = (String)arrGroups.GetValue(i);
                    //Define array to hold Sametime Contact Contact
                    //names for the current group
                    Object objContacts = null;

                    //Invoke the Sametime Helper to populate the contact
                    //names object for the current group.
                    stHelper.GetContacts(strGroupName, out objContacts);

                    if (objContacts != null)
                    {
                        //Assign the array that has the current group contact names. 
                        Array arrContacts = ((Array)objContacts);
                        //Loop through the Sametime Contact names and add each name
                        //to the List control.
                        for (int ct = 0; ct < arrContacts.Length; ct++)
                        {
                            //Assign the current contact.
                            String contact = arrContacts.GetValue(ct).ToString();
                            //Add the contact name to the List control.
                            listContacts.Items.Add(contact);
                        }
                    }
                }
            }
        }
    }
}