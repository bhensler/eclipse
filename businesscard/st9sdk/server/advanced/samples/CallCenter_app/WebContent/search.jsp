<%-- Copyright IBM Corp. 2008, 2014  All Rights Reserved.           --%>

<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.List"%>
<%@ page import="com.ibm.sample.callcenter.model.SearchResultBean"%>
<%@ page import="com.ibm.sample.callcenter.model.Constants" %>
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
		<li><a href="myCalls.jsp" title="View my calls"><span>My Calls</span></a></li>
		<li><a href="customers.jsp" title="See all customers"><span>Customers</span></a></li>
		<li><a href="technicalArea.jsp" title="See all technical areas"><span>Technical Area</span></a></li>
		<li><a href="search.jsp" title="Search for chat rooms, faqs and transcripts" class="current"><span>Search</span></a></li>		
	</ul>
</div>

<form name="search_form" method="post" action="metasoft?doAction=doSearch">
	<table class="call_submit_table" cellspacing="5" align="center">
		<tbody><tr><td>
 		<b> Search for: </b>
 		<input type="text" id="inputField" name="inputField" size="40"/>
		<select name="category">
				<option value="all">All</option>
				<option value="chatRooms">Chat Rooms</option>
				<option value="chatRoomOwners">Chat Room Owners</option>
				<option value="chatRoomFaqs">Chat Room FAQs</option>
				<option value="chatRoomFaqAuthors">Chat Room FAQ Authors</option>
				<option value="chatRoomTranscripts">Chat Room Transcripts</option>
		</select>
		<input class="submit_button" type="submit" value="search"/>
		</td></tr></tbody>
	</table>
</form>

<%
	List searchResults = (List)request.getSession().getAttribute(Constants.SEARCH_RESULTS);
	if(null!=searchResults)
	{
%>
	<table cellpadding="2" cellspacing="0" align="center">
		<tbody>	
			<tr><td>
				<h4>Found <%=searchResults.size() %> matching results</h4>
			</td>
			<td>
				<form name="search_form" method="post" action="metasoft?doAction=clearSearch">
					<input class="submit_button" type="submit" value="Clear"/>
				</form>
			</td>
			</tr>
		</tbody>
	</table>					
 	<table cellpadding="2" cellspacing="0" align="center">				
<%	
		for(int i=0; i<searchResults.size(); i++)
		{
			SearchResultBean searchResultBean = (SearchResultBean)searchResults.get(i);
			String resultType = searchResultBean.getType();
			if("chatRooms".equals(resultType))
			{
			
%> 			
			<tr>
			<td><%=(i+1) %>.</td>
			<td>
			<table cellpadding="2" cellspacing="0">
				<tr>
					<td>Chat Room: <b><%=searchResultBean.getTitle() %></b></td>
					<td><a target="_blank" href='<%=searchResultBean.getId()%>'>Go to Chat Room</a></td>					
				</tr>
			</table>
			</td></tr>			

<%
			}
			else if("chatRoomTranscripts".equals(resultType))
			{
%>

			<tr>
			<td><%=(i+1) %>.</td>
			<td>
			<table cellpadding="2" cellspacing="0">
				<tr>
					<td>Transcript text: <b><%=searchResultBean.getContent() %></b></td>
					<td>by <%=searchResultBean.getAuthor() %> on  <%=searchResultBean.getPublished() %></td>
					<td><a onclick="return openTranscript('<%=searchResultBean.getId()%>','<%=searchResultBean.getTitle()%>','<%=searchResultBean.getTitle()%>');"
							href="#">View transcript
						</a></td>					
				</tr>
			</table>
			</td></tr>
<%
			}
			else if("chatRoomFaqs".equals(resultType))
			{
%>			
			<tr>
			<td><%=(i+1) %>.</td>			
			<td>
			<table cellpadding="2" cellspacing="0">
				<tr>
					<td>Chat room Faq question: <a onclick="return viewChatFaqDetails('<%=searchResultBean.getId() %>');"
													href="#"><b><%=searchResultBean.getTitle() %></b></a></td>	
				</tr>
				<tr>
					<td>Chat room Faq answer: <%=searchResultBean.getContent() %></td>
				</tr>
			</table>
			</td></tr>				
<%			
			}
		}		
	
%>
	</table>
<%
	}
	else
	{
 %>

	<p class="login_error">No results found.</p>
	
<%
	}
%>
</body>
</html>
