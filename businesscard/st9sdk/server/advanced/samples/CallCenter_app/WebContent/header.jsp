<%-- Copyright IBM Corp. 2008, 2014  All Rights Reserved.           --%>

<%
	String user = (String) request.getSession().getAttribute("USER");

	if(user == null)
	{
		response.sendRedirect(request.getContextPath()+"/logon.jsp");
	}
%>
<table class="main_body" cellspacing=0>
		<tr>
			<td class="title_bar" colspan="2">
				<div id="company_logo"><H1>MetaSoft Customer Support</H1></div>
				<div id="login_status"><H2>Welcome: <%=user.toUpperCase()%></H2></div>
			</td>
		</tr>
		<tr>
			<td class="cell_space" colspan=2></td>
		</tr>
		<tr>
			<td class="log_out" colspan=2>
				<form name="login_out_form" method="post" action="metasoft?doAction=logout">
					<input class="submit_button" type="submit" value="Log out"/>
				</form>
			</td>
		</tr>
		<tr>
			<td height="20px" colspan=2></td>
		</tr>
</table>
