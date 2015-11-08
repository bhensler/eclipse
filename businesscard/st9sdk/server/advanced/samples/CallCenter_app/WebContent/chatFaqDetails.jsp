<%-- Copyright IBM Corp. 2008, 2014  All Rights Reserved.           --%>

<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.ibm.sample.callcenter.util.ChatFaqApiUtil" %>    
<%@ page import="com.ibm.sample.callcenter.model.ChatFaqBean" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>MetaSoft Customer Support</title>
<link rel=stylesheet href="theme/style.css" type="text/css" />
</head>
<body>
<jsp:include page="header.jsp" flush="false"></jsp:include>

	<table class="main_body" cellspacing=0>
			<tr>
				<td>
					<h1>Details of the Faq</h1>
				</td>
				<td class="log_new_calls">
					<input class="submit_button" type="button" value="Go Back" onclick="javascript:history.go(-1)"/>
				</td>
			</tr>
	</table>

<%
	ChatFaqBean bean = ChatFaqApiUtil.getChatFaqDetails(request);
 %>
	<table align="center">
		<tbody>
			<tr><td>
				<table class="call_submit_table" cellspacing="5" align="center">
					<tbody>
						<tr>
							<td align="left">Question:</td>
							<td align="left"><%=bean.getQuestion() %></td>				
						</tr>
						<tr>
							<td align="left">Answer:</td>
							<td align="left"><%=bean.getAnswer() %></td>				
						</tr>
						<tr>
							<td align="left">Author:</td>
							<td align="left"><%=bean.getAuthor() %></td>				
						</tr>
						<tr>
							<td align="left">Rating:</td>
							<td align="left"><%=bean.getRating() %></td>				
						</tr>
					</tbody>
				</table>
			</td></tr>
			<tr><td>
				<table>
					<tbody>
						<tr>
							<td>
								<form name="search_form" method="post" action="metasoft?doAction=deleteFaq">
									<input class="submit_button" type="submit" value="Delete Faq"/>
									<input type="hidden" name="faqId" id="faqId" value="<%=bean.getId() %>"/>
								</form>
							</td>
							<td>
								<input class="submit_button" type="button" value="Cancel" onclick="javascript:history.go(-1)"/>
							</td>
						</tr>
					</tbody>
				</table>
			</td></tr>
		</tbody>
	</table>
</body>
</html>
