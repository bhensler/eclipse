<%-- Copyright IBM Corp. 2008, 2014  All Rights Reserved.           --%>

<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ page import="java.util.List"%>
<%@ page import="com.ibm.sample.callcenter.model.Constants"%>
<%@ page import="com.ibm.sample.callcenter.model.CallBean"%>
<jsp:useBean id="callManager"
	class="com.ibm.sample.callcenter.manager.CallRecordManagerBean"
	scope="session" />
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>MetaSoft Customer Support</title>
<link rel=stylesheet href="theme/style.css" type="text/css"/>
</head>
<body>
<jsp:include page="header.jsp" flush="false"></jsp:include>
<script type="text/javaScript" language="javaScript" src="javascript/callCenter.js"></script>
<div id="mainMenu">
	<ul>
		<li><a href="index.jsp" title="View all calls"><span>All Calls</span></a></li>
		<li><a href="myCalls.jsp" title="View my calls" class="current"><span>My Calls</span></a></li>
		<li><a href="customers.jsp" title="See all customers"><span>Customers</span></a></li>
		<li><a href="technicalArea.jsp" title="See all technical areas"><span>Technical Area</span></a></li>
		<li><a href="search.jsp" title="Search for chat rooms, faqs and transcripts"><span>Search</span></a></li>
	</ul>
</div>

<table class="main_body" cellspacing=0>
	<tr>
		<td class="log_new_calls"><input class="submit_button"
			type="button" value="Log new call"
			onclick="logNewCall('newCall.jsp')" /></td>
	</tr>
</table>

<form name="my_calls_form" method="post" action="metasoft?doAction=closeCall">
<%
	List allCalls = callManager.loadAllCalls((String) request.getSession().getAttribute("USER"), Constants.CALL_STATUS_OPEN);
	int size = allCalls.size();
	if(size>0) {
	%>
<p>
<table class="call_table" align="center" width="97%" cellpadding="5"
	cellspacing="0">
	<tbody>
		<tr>
			<th class="row_header">Call number</th>
			<th class="row_header">Cutomer ID</th>
			<th class="row_header">Problem Description</th>
			<th class="row_header">Customer Name</th>
			<th class="row_header">Techical Area</th>
			<th class="row_header status">Status</th>
			<th class="row_header">Date</th>
			<th class="row_header">Customer Options</th>
			<th class="row_header">Technical Area Options</th>
		</tr>

		<%
			for (int i = 0; i < size; i++) {
				CallBean call = (CallBean) allCalls.get(i);
		%>
		<tr onmouseover="this.className='highlight_on'"
			onmouseout="this.className='highlight_off'">
			<td class="row_detail">
			<p><%=call.getCallId()%></p>
			</td>
			<td class="row_detail">
			<p><%=call.getCustomerId()%></p>
			</td>
			<td class="row_detail">
			<p><%=call.getCallDescription()%></p>
			</td>
			<td class="row_detail">
				<p><%=call.getCustomerName()%></p>
			</td>
			<td class="row_detail">
				<p><%=call.getTechinalArea()%> </p>			
			</td>
			<td class="row_detail status">
			<select name="callStatusSelection"
				onchange="return closeCall(this, '<%=call.getCallId()%>', 'callNumberHidden');">
				<option value="1">Open</option>
				<option value="0">Closed</option>
			</select><!--  call.getStatus() -->
			</td>
			<td class="row_detail">
			<p><%=call.getDate()%></p>
			</td>
			<td class="row_detail">
			<table class="transcript_chat_button">
				<tr>
					<td class="chat_image">
						<a class="messageLinks" onclick="return openTranscript('<%=call.getCustChatRoomId()%>','<%=call.getCustomerName()%>','Customer');"
							href="#" title="View recent chat history upto last five chat entries">Recent chat history
						</a>
					</td>
				</tr>
				<tr>
					<td class="chat_image">
						<a class="messageLinks" target="_blank" href='<%=call.getCustChatRoomLink()%>' title="Go to the chat room for this Customer">
							Persistent chat
						</a>
					</td>	
				</tr>
				<tr>
					<td class="chat_image">												
						<a class="messageLinks" onclick="return openComunityMessage('<%=call.getCustomerId()%>', 'Customer');"
						href="#" title="Ask questions and rate responses from community related to this Customer">
							Message to community
						</a>
					</td>
				</tr>
			</table>			
			</td>			
			<td class="row_detail">
			<table class="transcript_chat_button">
				<tr>
					<td class="chat_image">
						<a class="messageLinks" onclick="return openTranscript('<%=call.getTechAreaChatRoomId()%>','<%=call.getTechinalArea()%>','Technical Area');"
							href="#" title="View recent chat history upto last five chat entries">Recent chat history
						</a>
					</td>
				</tr>
				<tr>
					<td class="chat_image">
						<a class="messageLinks" target="_blank" href='<%=call.getTechAreaChatRoomLink()%>' title="Go to the chat room for this Technical Area">
							Persistent chat
						</a>
					</td>	
				</tr>
				<tr>
					<td class="chat_image">												
						<a class="messageLinks" onclick="return openComunityMessage('<%=call.getTechnicalId()%>', 'Technical Area');"
							href="#" title="Ask questions and rate responses from community related to this Technical Area">
							Message to community
						</a>
					</td>
				</tr>
			</table>			
			</td>
		</tr>
		<%
		}
		%>

	</tbody>
</table>
</p>
<%
	}
	else {
 	%>
<p class="login_error">There are no calls in your queue.</p>
<% } %> <input type="hidden" name="callNumberHidden" id="callNumberHidden" />
</form>

</body>
</html>
