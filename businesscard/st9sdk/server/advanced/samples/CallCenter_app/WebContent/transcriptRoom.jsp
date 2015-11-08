<%-- Copyright IBM Corp. 2008, 2014  All Rights Reserved.           --%>

<%@ page import="java.util.List"%>
<%@ page import="com.ibm.sample.callcenter.model.ChatTranscriptBean"%>
<%@ page import="com.ibm.sample.callcenter.util.PersistentChatApiUtil"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%
	String user = (String) request.getSession().getAttribute("USER");
	
	if(user == null)
	{
		response.sendRedirect(request.getContextPath()+"/logon.jsp");
	}
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>MetaSoft Customer Support</title>
<link rel=stylesheet href="theme/style.css" type="text/css"/>
</head>
	<body>
<table class="main_body" cellspacing=0>
		<tr>
			<td>
				<h1>Showing details of recent chat history</h1>
			</td>
			<td class="log_new_calls">
				<input class="submit_button" type="button" value="Close" onclick="javascript:self.close()"/>
			</td>
		</tr>
</table>
<%
	List allTranscripts = PersistentChatApiUtil.getCustomerChatTranscript(request);
	if(null!=allTranscripts && allTranscripts.size()>0)
	{
 %>		
	<div class="customer_chat_div" id="customer_chat_div" name="customer_chat_div">
		<table class="chat_transcript_container">
<%
		int max = allTranscripts.size();
		int i=0;
		if(max>5)
			i = max - 5; 
			
		while(i<max)
		{
			ChatTranscriptBean call = (ChatTranscriptBean) allTranscripts.get(i);
%>
			<tr id="transcript_row_<%=i%2%>">
				<td class="transcript_title">
					<%=call.getTitle()%>
				</td>
				<td class="transcript_content">
					<%=call.getContent()%>
				</td>
				<td class="transcript_date">
					<%=call.getUpdated()%>
				</td>
			</tr>
<%
			i++;
		}
%>
		</table>
		<script>
			var objDiv = document.getElementById("customer_chat_div");
			objDiv.scrollTop = objDiv.scrollHeight;
		</script>
	</div>
<%
	}
	else
	{		
%>
	<p class="login_error">There is no chat history for this chat room.</p>
<%
	}
 %>
	</body>
</html>
