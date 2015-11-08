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

public class TechnicalAreaBean {
	
	//table column names
	public static final String COL_TECHNICAL_ID = "TECHNICAL_ID";
	
	public static final String COL_TECHNICAL_AREA = "TECHNICAL_AREA";
	
	private int technicalId;

	private String techinalArea;

	public String getTechinalArea() {
		return techinalArea;
	}

	public void setTechinalArea(String techinalArea) {
		this.techinalArea = techinalArea;
	}

	public int getTechnicalId() {
		return technicalId;
	}

	public void setTechnicalId(int technicalId) {
		this.technicalId = technicalId;
	}
}
