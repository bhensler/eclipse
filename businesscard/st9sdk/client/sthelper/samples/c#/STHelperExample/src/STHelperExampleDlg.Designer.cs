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

namespace STHelperExample
{
    partial class STHelperExampleDlg
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.btnContacts = new System.Windows.Forms.Button();
            this.lblCaption = new System.Windows.Forms.Label();
            this.listContacts = new System.Windows.Forms.ListBox();
            this.SuspendLayout();
            // 
            // btnContacts
            // 
            this.btnContacts.Location = new System.Drawing.Point(294, 30);
            this.btnContacts.Name = "btnContacts";
            this.btnContacts.Size = new System.Drawing.Size(106, 23);
            this.btnContacts.TabIndex = 0;
            this.btnContacts.Text = "Get Contacts";
            this.btnContacts.UseVisualStyleBackColor = true;
            this.btnContacts.Click += new System.EventHandler(this.btnContacts_Click);
            // 
            // lblCaption
            // 
            this.lblCaption.AutoSize = true;
            this.lblCaption.Location = new System.Drawing.Point(29, 35);
            this.lblCaption.Name = "lblCaption";
            this.lblCaption.Size = new System.Drawing.Size(259, 13);
            this.lblCaption.TabIndex = 1;
            this.lblCaption.Text = "Press the button to fill the list with Sametime contacts:";
            // 
            // listContacts
            // 
            this.listContacts.FormattingEnabled = true;
            this.listContacts.Location = new System.Drawing.Point(32, 61);
            this.listContacts.Name = "listContacts";
            this.listContacts.Size = new System.Drawing.Size(368, 173);
            this.listContacts.TabIndex = 2;
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(426, 264);
            this.Controls.Add(this.listContacts);
            this.Controls.Add(this.lblCaption);
            this.Controls.Add(this.btnContacts);
            this.Name = "Form1";
            this.Text = "STHelperExample";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button btnContacts;
        private System.Windows.Forms.Label lblCaption;
        private System.Windows.Forms.ListBox listContacts;
    }
}

