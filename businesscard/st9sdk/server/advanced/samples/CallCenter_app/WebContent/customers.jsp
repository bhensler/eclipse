<%-- Copyright IBM Corp. 2008, 2014  All Rights Reserved.           --%>

<%@ page import="java.util.List"%>
<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ page import="java.util.List"%>
<%@ page import="com.ibm.sample.callcenter.model.CustomerBean"%>
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
<script type="text/javaScript" language="javaScript" src="javascript/callCenter.js"></script>
<jsp:include page="header.jsp" flush="false"></jsp:include>
<div id="mainMenu">
	<ul>
		<li><a href="index.jsp" title="View all calls"><span>All Calls</span></a></li>
		<li><a href="myCalls.jsp" title="View my calls"><span>My Calls</span></a></li>
		<li><a href="customers.jsp" title="See all customers" class="current"><span>Customers</span></a></li>
		<li><a href="technicalArea.jsp" title="See all technical areas"><span>Technical Area</span></a></li>
		<li><a href="search.jsp" title="Search for chat rooms, faqs and transcripts"><span>Search</span></a></li>
	</ul>
</div>

<form name="createCustomer_form" method="post" action="metasoft?doAction=createCustomer">
	<table class="call_submit_table" cellspacing="5" align="center">
		<tbody><tr><td>
			<b> Create new customer: </b>
			<input type="text" id="customerName" name="customerName"/>
			<input class="submit_button" type="submit" value="Create Customer"/>
		</td></tr></tbody>
	</table>
</form>

<%
	List allCustomers = callManager.getAllCustomers();
	int size = allCustomers.size();
	if(size>0) {
	%>
<form name="removeCustomersform" action="metasoft?doAction=removeCustomer" method="post">
<table class="call_table" align="center" width="97%" cellpadding="5" cellspacing="0">
	<tbody>
		<tr>
			<th class="row_header"></th>
			<th class="row_header">Customer Number</th>
			<th class="row_header">Customer Name</th>
		</tr>

		<%
			for (int i = 0; i < allCustomers.size(); i++) {
				CustomerBean customer = (CustomerBean) allCustomers.get(i);
		%>
		<tr onmouseover="this.className='highlight_on'" onmouseout="this.className='highlight_off'">
			<td  class="row_detail"><input type="radio" name="custRadio" value='<%=customer.getCustomerId()%>'/></td>
			<td  class="row_detail">
			<p><%=customer.getCustomerId()%></p>
			</td>
			<td  class="row_detail">
			<p><%=customer.getCustomerName()%></p>
			</td>
		</tr>
		<%
		}
		%>

	</tbody>
</table>
	<table align="center" cellpadding="5" cellspacing="0">
		<tr><td align="center">
			<p class="titleMessageNote">Please note: Removing a Customer will automatically result in removal of the call which is related to it.</p>
		</td></tr>		
		<tr><td align="center">
		<input class="submit_button" type="submit" value="Remove Customer" onclick="return removeSelected(this, document.removeCustomersform.custRadio, 'hiddenRemoveSToptions', 'customer');"/>
		<input type="hidden" name="hiddenRemoveSToptions" id="hiddenRemoveSToptions" value="false"/>
		</td></tr>
	</table>	
</form>
<%
	}
	else {
 	%>
	<p class="login_error">There are no customer.</p>
<% } %>

</body>
</html>
