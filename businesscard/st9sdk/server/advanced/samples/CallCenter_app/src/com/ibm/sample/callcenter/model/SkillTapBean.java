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

import java.util.List;

public class SkillTapBean {

	private String communityName;
	
	private String communityUrl;
	
	private String author;
	
	private String email;
	
	private String published;
	
	private String reqId;
	
	private String question;
	
	private List responses;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCommunityName() {
		return communityName;
	}

	public void setCommunityName(String communityName) {
		this.communityName = communityName;
	}

	public String getCommunityUrl() {
		return communityUrl;
	}

	public void setCommunityUrl(String communityUrl) {
		this.communityUrl = communityUrl;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
	}

	public String getQuestion() {
		return question;
	}
	
	public void setQuestion(String question) {
		this.question = question;
	}
	
	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public List getResponses() {
		return responses;
	}

	public void setResponses(List responses) {
		this.responses = responses;
	}
}
