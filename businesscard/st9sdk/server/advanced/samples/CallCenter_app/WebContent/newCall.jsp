<%-- Copyright IBM Corp. 2008, 2014  All Rights Reserved.           --%>

<?xml version="1.0" encoding="ISO-8859-1" ?>
<%
	String user = (String) request.getSession().getAttribute("USER");

	if(user == null)
	{
		response.sendRedirect(request.getContextPath()+"/logon.jsp");
	}
%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List"%>
<%@ page import="com.ibm.sample.callcenter.model.CustomerBean"%>
<%@ page import="com.ibm.sample.callcenter.model.TechnicalAreaBean"%>
<jsp:useBean id="callManager" class="com.ibm.sample.callcenter.manager.CallRecordManagerBean"
	scope="session" />	
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>MetaSoft Customer Support</title>
<link rel=stylesheet href="theme/style.css" type="text/css"/>
</head>
<body>
<script type="text/javaScript" language="javaScript" src="javascript/callCenter.js"></script>
<jsp:include page="header.jsp" flush="false"></jsp:include>
<table class="main_body" cellspacing=0>
		<tr>
			<td class="my_calls">
				<h1>Create New Call</h1>
			</td>
			<td class="log_new_calls">
				<input class="submit_button" type="button" value="Go Back" onclick="javascript:history.go(-1)"/>
			</td>
		</tr>
</table>

<table id="errorTable" align="center" cellpadding="10" cellspacing="0" style="display:none">
	<tbody>
		<tr>
			<td  colspan=2 class="error"> 
				<p id="errorMessage"></p>
			</td>
		</tr>
	</tbody>
</table>

<form name="create_new_call" method="post" action="metasoft?doAction=createNewCall">			
<table class="call_table" align="center" cellpadding="10" cellspacing="0" width="100%">
	<tbody>
		<tr>
			<th class="row_header"><label for="customer_name">Customer Name</label></th>
			<td  class="row_detail newcall_data">
				<select id="customer_name" name="customer_name">
					<option></option>
					<%
						List allCustomers = callManager.getAllCustomers();
						for (int i = 0; i < allCustomers.size(); i++) {
							CustomerBean customer = (CustomerBean) allCustomers.get(i);
					%>
						<option value='<%=customer.getCustomerId()%>'>
							<%=customer.getCustomerName()%>
						</option>					
					<%} %>
				</select>
			</td>
		</tr>
		<tr>
			<th class="row_header"><label for="technical_area">Technical Area</label></th>
			<td  class="row_detail newcall_data">
				<select id="technical_area" name="technical_area">
						<option></option>
					<%
						List allTechnicalAreas = callManager.getAllTechnicalAreas();
						for (int i = 0; i < allTechnicalAreas.size(); i++) {
							TechnicalAreaBean technicalArea = (TechnicalAreaBean) allTechnicalAreas.get(i);
					%>
						<option value='<%=technicalArea.getTechnicalId()%>'>
							<%=technicalArea.getTechinalArea()%>
						</option>					
					<%} %>
				</select>
			</td>
		</tr>
		<tr>
			<th class="row_header"><label for="problem_desc">Problem Description</label></th>
			<td  class="row_detail newcall_data">
				<textarea class="problem_textarea" id="problem_desc" name="problem_desc"></textarea>
			</td>
		</tr>
	</tbody>
</table>

<table class="call_submit_table" cellspacing="5" align="center">
	<tr>
		<td align="center">
			<input class="create_call_button" type="submit" value="Create New Call" 
				onclick="return validateParameters('customer_name', 'technical_area', 'problem_desc',
					'errorTable', 'errorMessage');"/>
		</td>
		<td align="center">
			<input class="create_call_button" type="button" value="Cancel" onclick="javascript:history.go(-1)"/>
		</td>
	</tr>
</table>

</form>
</body>
</html>
