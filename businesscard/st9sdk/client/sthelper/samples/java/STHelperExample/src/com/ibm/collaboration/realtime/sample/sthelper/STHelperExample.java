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

package com.ibm.collaboration.realtime.sample.sthelper;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class STHelperExample {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				new STHelperExampleDlg();
			}
		});
	}
}
