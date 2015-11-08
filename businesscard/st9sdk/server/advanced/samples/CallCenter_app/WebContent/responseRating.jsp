<%-- Copyright IBM Corp. 2008, 2014  All Rights Reserved.           --%>

<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>MetaSoft Customer Support</title>
<link rel=stylesheet href="theme/style.css" type="text/css" />
</head>
<body>
<%
	String communityName = request.getParameter("communityName");
	String type = request.getParameter("type");	
	String reqId = request.getParameter("reqId");	
 %>
<form name="askQuestion_form" method="post" action="metasoft?doAction=rateResponse">
	
<table class="call_submit_table" cellspacing="5" align="center">
	<tbody>
		<tr>
			<td><h4>Rate this response</h4></td>
		</tr>
		<tr>
			<td>
				<select name="rating">
				<option value="1">1. Poor</option>
				<option value="2">2. Alright</option>
				<option value="3">3. Good</option>
				<option value="4">4. Very Good</option>
				<option value="5">5. Excellent</option>								
			</select>
			</td>
		</tr>
		<tr>
			<td><input class="submit_button" type="submit"
				name="rateResponse" value="OK" /> 
				<input class="create_call_button" type="button" 
				value="Cancel" onclick="javascript:self.close()"/>
			</td>
		</tr>
	</tbody>
</table>
	<input type="hidden" name="communityName" value='<%=communityName %>'/>
	<input type="hidden" name="type" value='<%=type %>'/>
	<input type="hidden" name="reqId" value='<%=reqId %>'/>
</form>
</body>
</html>
