<%@page import="java.net.URLDecoder,org.apache.commons.lang.StringEscapeUtils,com.ibm.lotus.realtime.ume.webui.RoomControllerServlet"%>
<%@page import="java.util.Properties"%>
<html>
<head>
<%
			//Code to detect the referrer
			String referrer = null;
			// Get the referrer url
			if (request.getSession().getAttribute("referrer") != null)
			{
				referrer = request.getSession().getAttribute("referrer").toString();
			}
			else if (request.getHeader("referer") != null)
			{
				referrer = request.getHeader("referer");	
			}
			else
			{
				// TODO: specify some sort of default page
				referrer = request.getContextPath();
			}
	
			// Remove the referrer session attribute now that it has been read
			request.getSession().removeAttribute(RoomControllerServlet.REFERRER);
			//If the referrer was not a room URL, then redirect back. Virtual No-op.
			if(!referrer.contains("stmeetings/room"))
			{
				response.sendRedirect(referrer);
			}
%>
<script type="text/javascript">

window.referrer = "<%=StringEscapeUtils.escapeJavaScript(referrer)%>";

//Joins a meeting room via POST. Creates a hidden form does a programmatic POST.
function doJoinMeetingRoom() 
{
	var destination = null;
	//Check to see if the referrer is a room URL. If so, it is the final destination we need to POST to.
	if(window.referrer.indexOf("room")!=-1)
	{
		destination = window.referrer;
	}
	if(destination!=null)
	{
		//Read the preset cookies for displayName, loginId, room password(if any). These can be set by hitting mock_post.jsp
		var displayName = getCookie("sampleDisplayName");
		var loginId = getCookie("sampleLoginId");
		var password = getCookie("samplePassword");
		if(typeof displayName != "undefined" && typeof loginId != "undefined" && displayName!=null && loginId!=null)
		{
			//Create a hidden form via JS. This is so we can POST to the room and load it.
			var form = document.createElement("form");
			form.setAttribute("method","post");
			//Set the action attribute to the room
			form.setAttribute("action", destination);
			var hiddenField = document.createElement("input");
			hiddenField.setAttribute("type", "hidden");
			hiddenField.setAttribute("name", "loginId");
			hiddenField.setAttribute("value", loginId);
			form.appendChild(hiddenField);
			

			var hiddenField2 = document.createElement("input");
			hiddenField2.setAttribute("type", "hidden");
			hiddenField2.setAttribute("name", "displayName");
			hiddenField2.setAttribute("value", displayName);
			form.appendChild(hiddenField2);

			var hiddenField3 = document.createElement("input");
			hiddenField3.setAttribute("type", "hidden");
			hiddenField3.setAttribute("name", "password");
			hiddenField3.setAttribute("value", password);

			form.appendChild(hiddenField3);
		}
		else
		{
			alert("Cannot forward to meeting room. Invalid parameters - displayName and/or loginId.");
			window.location = "../stmeetings";
		}
	}

    document.body.appendChild(form);   
	//Submit the form programmatically. This will load the meeting room.
    form.submit();
}

//Retrieves the cookie with the specified name
function getCookie(c_name)
{
	var i,x,y,ARRcookies=document.cookie.split(";");
	for (i=0;i<ARRcookies.length;i++)
	{
		x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
		y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
		x=x.replace(/^\s+|\s+$/g,"");
		if (x==c_name)
		{
			return unescape(y);
		}
	}
}
</script>
</head>
<body onload="setTimeout('doJoinMeetingRoom()',5000);">
	
	This is the custom login page that ships with the IBM Remote Client SDK. This page will do a POST to a meeting room and join it.
	
	Redirecting you to a meeting in 5 seconds (This delay can be removed or altered in the JSP). Please wait...
</body>
</html>