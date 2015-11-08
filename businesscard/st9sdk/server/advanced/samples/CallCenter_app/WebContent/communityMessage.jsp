<%-- Copyright IBM Corp. 2008, 2014  All Rights Reserved.           --%>

<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.List"%>
<%@ page import="com.ibm.sample.callcenter.model.SkillTapBean"%>
<%@ page import="com.ibm.sample.callcenter.model.SkillTapResponseBean" %>
<% 
	String baseUrl = "http://" + request.getLocalName() + ":" + request.getLocalPort();
%>
	<jsp:useBean id="restApi" class="com.ibm.sample.callcenter.manager.RestApiManagerBean" scope="request">
		<jsp:setProperty name="restApi" property="baseUrl" value='<%=baseUrl %>' />
		<jsp:setProperty name="restApi" property="username" value='<%=request.getSession().getAttribute("USER")%>' />		
		<jsp:setProperty name="restApi" property="password" value='<%=request.getSession().getAttribute("PASSWORD")%>' />	
	</jsp:useBean>
	
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>MetaSoft Customer Support</title>
<link rel=stylesheet href="theme/style.css" type="text/css" />
</head>
<body>
<jsp:include page="header.jsp" flush="false"></jsp:include>
<script type="text/javaScript" language="javaScript" src="javascript/callCenter.js"></script>
<table class="main_body" cellspacing=0>
		<tr>
			<td>
				<h1>Send a message to the community</h1>
			</td>
			<td class="log_new_calls">
				<input class="submit_button" type="button" value="Go Back" onclick="javascript:history.go(-1)"/>
			</td>
		</tr>
</table>
<%
	String communityName = request.getParameter("communityName");
	String type = request.getParameter("type");	
 %>
<form name="askQuestion_form" method="post" action="metasoft?doAction=postQuestion">
	
<table class="call_submit_table" cellspacing="5" align="center">
	<tbody>
		<tr>
			<td>
				<p class="titleMessage">Ask a question from a community related to this <%=type %></p>
				<p class="titleMessageNote">Please note: if you wish to see this question appearing as an alert then you need to have your Sametime 8.0 Client running and you must be a member of this community.</p></td>
		</tr>		
		<tr>
			<td><textarea rows="2" cols="40" name="questionInput" id="questionInput"></textarea>
			</td>
		</tr>
		<tr>
			<td><input class="submit_button" type="submit"
				name="askQuestion" value="Ask question"/> 
				<input class="create_call_button" type="button" 
				value="Cancel" onclick="javascript:history.go(-1)"/>
			</td>
		</tr>
	</tbody>
</table>
	<input type="hidden" name="communityName" value='<%=communityName %>'/>
	<input type="hidden" name="type" value='<%=type %>'/>
</form>

<%
	List allSkillTap = restApi.getSkillTapForCommunity(communityName, type);
	if(null!=allSkillTap)
	{
		if(allSkillTap.size() > 0)
		{
 %>

	<table cellpadding="2" cellspacing="0" align="center">
		<tbody>	
			<tr><td>
				<p class="titleMessage">Previous questions and their responses</p>
			</td></tr>
		</tbody>
	</table>
	
<%
			for(int i=0; i<allSkillTap.size(); i++)
			{
				SkillTapBean questionBean = ((SkillTapBean)allSkillTap.get(i));
 %>
 			<table width="80%" align="center"><tr><td>
 				
 				<table width="100%" cellpadding="2" cellspacing="0" class="requestBox">
					<tbody>	
						<tr>
						<td class="messageTableRow">Request Id: </td>
						<td><%=questionBean.getReqId() %></td>
						</tr>
						<tr>
						<td class="messageTableRow">Question: </td>
						<td><%=questionBean.getQuestion() %></td>
						</tr>
						<tr>
						<td class="messageTableRow">Author: </td>
						<td><%=questionBean.getAuthor() %></td>
						</tr>
						<tr>
						<td class="messageTableRow">Published: </td>
						<td><%=questionBean.getPublished() %></td>
						</tr>
					</tbody>
				</table>
				
				</td></tr>

 				<%
 					List responses = questionBean.getResponses();
 					if(null!=responses && responses.size() > 0)
 					{
 					
 					%>
 					<tr><td>
						<b><%=responses.size() %> responses available</b> <a class="messageLinks" id="showLink<%=i %>" href="#" onclick="return toggleResponses(this, 'hideLink<%=i %>','responseTable<%=i %>', true);">Show</a>
 						<a class="messageLinks" id="hideLink<%=i %>" href="#" onclick="return toggleResponses(this, 'showLink<%=i %>', 'responseTable<%=i %>', false);" style="display:none">Hide</a></td></tr>
 					<tr><td>
								<table width="100%" cellpadding="2" cellspacing="0" id="responseTable<%=i %>" style="display:none"
									class="responseBox">
									<tbody>					
 					
 				<%
 						for(int j=0; j<responses.size(); j++)
 							{
		 						SkillTapResponseBean responseBean = ((SkillTapResponseBean)responses.get(j));
 				 %>
 
 									<tr><td>
 									<table cellpadding="2" cellspacing="0" class="responseInnerBox">
										<tr>
											<td class="messageTableRow">Response Id: </td>
											<td><%=responseBean.getId()%></td>
										</tr>
										<tr>
											<td class="messageTableRow">Answer: </td>
											<td><%=responseBean.getAnswer()%></td>
										</tr>
										<tr>
											<td class="messageTableRow">Rating: </td>
											<td><%=responseBean.getRating()%> <a class="messageLinks" onclick="return rateResponse('<%=communityName %>', '<%=type %>', '<%=questionBean.getReqId() %>');"
													href="#">Rate</a></td>
										</tr>
									</table></td></tr>	
<%										
						}
						%>
									</tbody>
								</table>
								</td>
								</tr>
								</table>
						<%
				}
			}
		}
	}
 %>

</body>
</html>
