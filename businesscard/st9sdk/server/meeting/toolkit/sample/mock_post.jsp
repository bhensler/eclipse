<%@page import="com.ibm.rtc.config.Configuration"%>
<html>
<head>
<%
String staticContent = getServletContext().getInitParameter("staticContent");
//This section exists to receive a POST to self from  the form to set the configuration value in the HTML below
if(request.getMethod().equals("POST"))
{ 
	System.out.println("POST received");
	String customLoginPage = null;
	customLoginPage = request.getParameter("customLoginPage");

	System.out.println("customLoginPage[" + customLoginPage + "]");
	//Reset the config value
	if(customLoginPage == null)
	{
		Configuration.getConfiguration().set("meetingroomcenter.customLoginPage", "");
	}
	else if(!customLoginPage.equals(" "))
	{
		Configuration.getConfiguration().set("meetingroomcenter.customLoginPage", customLoginPage);
		System.out.println("New config value:" + Configuration.getConfiguration().getString("meetingroomcenter.customLoginPage",""));
	}
	else
	{
		System.out.println("Custom login value not set because it was either NULL or was empty");
	}
	
}
%>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		
		<title></title>

		
		<script type="text/javascript" src="<%=staticContent%>/dojo/dojo.js" djConfig="parseOnLoad: true, popup: true"></script>
		<script type="text/javascript">
		dojo.addOnLoad(function()
		{
				window.loginForm = dojo.byId("loginForm");
		});
		
		//Enables or disables the custom login page text box.
		function toggleCustomLoginTxtField()
		{
			var customLoginForm = dojo.byId("setCustomPage");
			var enableTxt = customLoginForm.enableCustomLoginPage.checked;
			if(enableTxt)
			{
				customLoginForm.customLoginPage.removeAttribute("disabled");		
			}
			else
			{
				customLoginForm.customLoginPage.setAttribute("disabled",false);
			}
			
		}
		
		//Sets the cookies after user presses "Submit" on the form. We don't really submit the form. Instead,
		//we simply set some cookies locally and exit.
			function processLogin()
			{
				var formValues = dojo.byId("loginForm");
				if(formValues.displayName.value!=null && formValues.loginId.value!=null)
				{
					document.cookie = "sampleDisplayName=" + formValues.displayName.value + "; path=/";
					document.cookie = "sampleLoginId=" + formValues.loginId.value + "; path=/";
					if(formValues.password!=null)
					{
						document.cookie = "samplePassword=" + formValues.password.value + "; path=/";
					}
					return false;
				}
				return false;
			}
		</script>
</head>
<body>
<div>
<form dojoType="dijit.form.Form" id="loginForm" name="loginForm" enctype="application/x-www-form-urlencoded" onsubmit="processLogin()"> 
	<h1 id="loginFormTitle">Sample POST into meeting room</h1>
	<p> The intent of this sample is to demonstrate a POST with some parameters to join a meeting room without requiring user input. This sample will set some cookies, namely the room URL the user wishes to join, the user's display name, loginId and the room password, if any. Then, when the user attempts to join a meeting room anonymously, these cookies will be read by the custom login page and forward onto the meeting room automatically. This sample is for demonstration
	purposes only and should be replaced by other third party mechanisms to set state information.
	</p>
	<p>
	<b>Note: The custom login page needs to be configured separately.</b>
	</p>
	<div>
		<label for="displayName" style="width:200px"><b>Display name:</b><i>(Preferred display name of user)</i></label>
		<input dojoType="dijit.form.ValidationTextBox" required="true" id="displayName" name="displayName"/>
	</div>
	<p></p>
	<div>
		<label for="loginId"><b>loginId:</b><i>(Distinct Id by which user is referenced)</i></label>
	<input dojoType="dijit.form.ValidationTextBox" required="true" id="loginId" class="lotusText" name="loginId"/>
	</div>
	<p></p>
	<div>
		<label for="password"><b>Room password:</b><i>(Password if the room has any)</i></label>
	<input dojoType="dijit.form.TextBox" id="password" class="lotusText" name="password"/>
	</div>
	<p></p>
	<div class="lotusBtnContainer">
		<input type="submit"  value="Submit"/>
		<input type="button" value="Cancel"/>
	</div>
</form>
<form dojoType="dijit.form.Form" id="setCustomPage" name="setCustomPage" method="post" action="mock_post.jsp" enctype="application/x-www-form-urlencode">
	<h1>Custom login page configuration</h1>
	<p> The following form will set the configuration value on the meeting server for the custom login page. The assumption is that the file will reside in the "stmeetings" context along
	with other meetings files. So an entry in the field will be something like: "custompage.html" or "customPage.jsp". Note that entering invalid values may cause problems.
	<b>To DISABLE the custom login page: Uncheck the checkbox below and press SUBMIT. The meetings default login page will be used. </b>
	</p>
	<div>
		<input dojoType="dijit.form.RadioButton"  type="radio" name="configSetting" id="enableCustomLoginPage" 
		onclick="toggleCustomLoginTxtField();"/>
		<label for="enableCustomLoginPage">Enable custom login page</label>
		
	</div>
	<div>
		<label for="customLoginPage" style="width:200px"><b>Custom login page:</b></label>
		<input dojoType="dijit.form.ValidationTextBox" required="true" disabled="true" id="customLoginPage" class="lotusText" name="customLoginPage"/>
		
	</div>
		<div>
		<input dojoType="dijit.form.RadioButton" checked="checked" type="radio" name="configSetting" id="clearCustomLogin" onclick="toggleCustomLoginTxtField();"/>
		<label for="clearCustomLogin">Disable custom login page</label>
		
	</div>
	<div>
		<input type="submit" id="submitCustomLoginForm" value="Submit"/>
		
	</div>
</form>
</div>

<body>
</html>