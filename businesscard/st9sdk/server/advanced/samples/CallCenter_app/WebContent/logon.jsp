<%-- Copyright IBM Corp. 2008, 2014  All Rights Reserved.           --%>

<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<link rel=stylesheet href="theme/style.css" type="text/css"/>
<script type="text/javaScript" language="javaScript" src="javascript/callCenter.js"></script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>MetaSoft Customer Support</title>
</head>
<body onload="setUsernameFocus('username')">
	<table class="main_body">
		<tr>
			<td class="title_bar">
				<div id="company_logo"><h1>MetaSoft Customer Support</h1></div>
				<div id="login_status"><h2>Logged Out</h2></div>
			</td>
		</tr>
		<% if (null != request.getParameter("authentication")){%>
		<tr>
			<td  colspan=2 class="login_error"> 
				ERROR : Invalid Username and/or Password. Please use your Sametime Advanced username and password
			</td>
		</tr>
		<%}%>
		<tr>
			<td class="details">
				<form name="loginForm" method="post" action="login">
				<table class="login_form">
				<tr>
					<td class="login_cell">
						<label for="username">User Name</label>
					</td>
					<td class="login_cell">
						<input class="login" type="text" id="username" name="username"/>
					</td>
				</tr>
				<tr>
					<td class="login_cell">
						<label for="password">Password</label>
					</td>
					<td class="login_cell">
						<input class="login password" type="password" id="password" name="password"/>
					</td>
				</tr>
				<tr>
					<td  class="login_cell" colspan="2">
						<input class="submit_button" type="submit" name="Enter" value="Login" />
					</td>
				</tr>
				</table>
				</form>
			</td>
		</tr>
	</table>
</body>
</html>
