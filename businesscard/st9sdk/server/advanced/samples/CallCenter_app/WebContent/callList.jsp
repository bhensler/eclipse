<%-- Copyright IBM Corp. 2008, 2014  All Rights Reserved.           --%>

<%@ page import="java.util.List"%>
<%@ page import="com.ibm.sample.callcenter.model.Constants"%>
<%@ page import="com.ibm.sample.callcenter.model.CallBean"%>
<jsp:useBean id="callManager"
	class="com.ibm.sample.callcenter.manager.CallRecordManagerBean"
	scope="session" />	

<table class="main_body" cellspacing=0>
	<tr>
		<td>
			<h1>Open Calls</h1>
		</td>
		<td class="log_new_calls"><input class="submit_button"
			type="button" value="Log new call"
			onclick="logNewCall('newCall.jsp')" /></td>
	</tr>
</table>
<form name="my_calls_form" method="post" action="metasoft?doAction=closeCall">

<%
	List allCalls = callManager.loadAllCalls(null, Constants.CALL_STATUS_OPEN);
	int size = allCalls.size();
	if(size>0) {
	%>
<p>
<table class="call_table" align="center" width="97%" cellpadding="5" cellspacing="0">
	<tbody>
		<tr>
			<th class="row_header">Call number</th>
			<th class="row_header">Operator Name</th>
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
			for (int i = 0; i < allCalls.size(); i++) {
				CallBean call = (CallBean) allCalls.get(i);
		%>
		<tr onmouseover="this.className='highlight_on'" onmouseout="this.className='highlight_off'">
			<td  class="row_detail">
			<p><%=call.getCallId()%></p>
			</td>
			<td  class="row_detail">
			<p><%=call.getOperatorName()%></p>
			</td>
			<td  class="row_detail">
			<p><%=call.getCustomerId()%></p>
			</td>
			<td  class="row_detail">
			<p><%=call.getCallDescription()%></p>
			</td>			
			<td  class="row_detail">
			<p><%=call.getCustomerName()%></p>
			</td>
			<td  class="row_detail">
			<p><%=call.getTechinalArea()%></p>
			</td>
			<td class="row_detail status">
			<select name="callStatusSelection" onchange="return closeCall(this, '<%=call.getCallId()%>', 'callNumberHidden');">
				<option value="1">Open</option>
				<option value="0">Closed</option>
			</select><!--  call.getStatus() -->
			</td>
			<td  class="row_detail">
			<p><%=call.getDate()%></p>
			</td>
			<td  class="row_detail">
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
</table></p>
<%
	}
	else {
 	%>
	<p class="login_error">There are no calls.</p>
<% } %>
<input type="hidden" name="callNumberHidden" id="callNumberHidden" />
</form>

<%
	List allClosedCalls = callManager.loadAllCalls(null, Constants.CALL_STATUS_CLOSED);
	int closedCallListSize = allClosedCalls.size();
	if(closedCallListSize>0) 
	{
	%>
<form name="reopenCall_form" method="post" action="metasoft?doAction=reopenCall">	
<table class="main_body" cellspacing=0>
	<tr>
		<td>
			<h1>Closed Calls</h1>
		</td>
	</tr>
</table>
	
<table class="call_table" align="center" width="97%" cellpadding="5" cellspacing="0">
	<tbody>
		<tr>
			<th class="row_header">Call number</th>
			<th class="row_header">Operator Name</th>
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
			for (int i = 0; i < allClosedCalls.size(); i++) {
				CallBean call = (CallBean) allClosedCalls.get(i);
		%>
		<tr onmouseover="this.className='highlight_on'" onmouseout="this.className='highlight_off'">
			<td  class="row_detail">
			<p><%=call.getCallId()%></p>
			</td>
			<td  class="row_detail">
			<p><%=call.getOperatorName()%></p>
			</td>
			<td  class="row_detail">
			<p><%=call.getCustomerId()%></p>
			</td>
			<td  class="row_detail">
			<p><%=call.getCallDescription()%></p>
			</td>			
			<td  class="row_detail">
			<p><%=call.getCustomerName()%></p>
			</td>
			<td  class="row_detail">
			<p><%=call.getTechinalArea()%></p>
			</td>
			<td class="row_detail status">
			<select name="callStatusSelection" onchange="return reopenCall(this, '<%=call.getCallId()%>', 'reopenCallNumberHidden');">
				<option value="1">Reopen</option>
				<option value="0" selected>Closed</option>
			</select><!--  call.getStatus() -->
			</td>
			<td  class="row_detail">
			<p><%=call.getDate()%></p>
			</td>
			<td  class="row_detail">
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
<input type="hidden" name="reopenCallNumberHidden" id="reopenCallNumberHidden" />
</form>
<%
	}
%>
