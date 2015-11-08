<%-- Copyright IBM Corp. 2008, 2014  All Rights Reserved.           --%>

<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
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
<script type="text/javaScript" language="javaScript" src="javascript/callCenter.js"></script>
<jsp:include page="header.jsp" flush="false"></jsp:include>


<div id="mainMenu">
	<ul>
		<li><a href="index.jsp" title="View all calls" class="current"><span>All Calls</span></a></li>
		<li><a href="myCalls.jsp" title="View my calls"><span>My Calls</span></a></li>
		<li><a href="customers.jsp" title="See all customers"><span>Customers</span></a></li>
		<li><a href="technicalArea.jsp" title="See all technical areas"><span>Technical Area</span></a></li>
		<li><a href="search.jsp" title="Search for chat rooms, faqs and transcripts"><span>Search</span></a></li>
	</ul>
</div>
	<jsp:include page="callList.jsp" flush="false"></jsp:include>
 </body>
</html>
