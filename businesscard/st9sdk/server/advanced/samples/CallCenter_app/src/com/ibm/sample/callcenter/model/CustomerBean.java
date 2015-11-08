/* ***************************************************************** */
/*                                                                   */
/* Licensed Materials - Property of IBM                              */
/*                                                                   */
/* 5724-E76, 5724-L21, 5724-i67, 5724-L64, 5655-M44                  */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2014  All Rights Reserved.           */
/*                                                                   */
/* US Government Users Restricted Rights - Use, duplication or       */
/* disclosure restricted by GSA ADP Schedule Contract with           */
/* IBM Corp.                                                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.sample.callcenter.model;

public class CustomerBean {
	
	//table column names
	public static final String COL_CUSTOMER_ID = "CUSTOMER_ID";
	
	public static final String COL_CUSTOMER_NAME = "CUSTOMER_NAME";
	
	private int customerId;

	private String customerName;

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
}
