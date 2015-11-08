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

public class CallBean {
	
	//table column names
	public static final String COL_CALL_ID = "SA_CALL_ID";

	public static final String COL_STATUS = "STATUS";
	
	public static final String COL_DATE = "DATE";
	
	public static final String COL_CUSTOMER_CHAT_URL = "CUSTOMER_CHAT_URL";
	
	public static final String COL_TECH_AREA_CHAT_URL = "TECH_AREA_CHAT_URL";

	private int callId;
	
	private String callDescription;

	private String operatorName;
	
	private int customerId;

	private String customerName;
	
	private String technicalId;

	private String techinalArea;

	private String status;

	private String date;

	private String custChatRoomLink;
	
	private String custChatRoomId;

	private String techAreaChatRoomLink;

	private String techAreaChatRoomId;

	public int getCallId() {
		return callId;
	}

	public void setCallId(int callId) {
		this.callId = callId;
	}

	public String getCallDescription() {
		return callDescription;
	}
	
	public void setCallDescription(String callDescription) {
		this.callDescription = callDescription;
	}
	
	public String getCustChatRoomLink() {
		return custChatRoomLink;
	}

	public void setCustChatRoomLink(String custChatRoomLink) {
		this.custChatRoomLink = custChatRoomLink;
	}

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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTechAreaChatRoomLink() {
		return techAreaChatRoomLink;
	}

	public void setTechAreaChatRoomLink(String techAreaChatRoomLink) {
		this.techAreaChatRoomLink = techAreaChatRoomLink;
	}

	public String getTechinalArea() {
		return techinalArea;
	}

	public void setTechinalArea(String techinalArea) {
		this.techinalArea = techinalArea;
	}
	
	public String getTechnicalId() {
		return technicalId;
	}
	
	public void setTechnicalId(String technicalId) {
		this.technicalId = technicalId;
	}
	
	public String getCustChatRoomId() {
		return custChatRoomId;
	}

	public void setCustChatRoomId(String custChatRoomId) {
		this.custChatRoomId = custChatRoomId;
	}

	public String getTechAreaChatRoomId() {
		return techAreaChatRoomId;
	}

	public void setTechAreaChatRoomId(String techAreaChatRoomId) {
		this.techAreaChatRoomId = techAreaChatRoomId;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String toString() {
		return "CallId=" + callId+", CustomerId="+customerId+", CustomerName="+customerName+", Technical Area="+techinalArea+ 
			    ", Date="+date+", Status="+status+", custChatRoomLink"+custChatRoomLink+", techAreaChatRoomLink"+techAreaChatRoomLink+"\n";
	}

}
