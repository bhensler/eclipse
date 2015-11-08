<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Copyright IBM Corp. 2009, 2014  All Rights Reserved.              --%>

<!--
 * L-MCOS-96LQPJ
-->
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   <title>IBM Sametime 9 Example 7</title>
   <link type="text/css" rel="stylesheet" href="sametime.css" />
   
   <script type="text/javascript">
      // Settings for the proxy
      var stproxyConfig = {
         server: "http://<YourProxyServer>:9080",      // Replace with the appropriate server & port
         tunnelURI: "http://<ThisServer>/tunnel.html", // The url of the openAjax Tunnel which must be on the same server as this file
         isConnectClient: false
      };

      djConfig = {
         parseOnLoad: true,
     	locale: "<%=request.getLocale().toString().replaceAll("_","-")%>"
      };
   </script>

   <script type="text/javascript" src="http://<YourProxyServer>:9080/stwebclient/latest/dojo.blue/dojo/dojo.js"></script>
   <script type="text/javascript" src="http://<YourProxyServer>:9080/stwebclient/latest/include.js?widget=widgets.js&auto=false"></script>

   <script type="text/javascript">
      // User ID and password
      var userID = "<YourUser>";
      var password = "<YourPassword>";

      // Error callback
      function loginFailed(reason, error) {
         // Something has gone wrong: display some information
         alert("Error: " + reason + ": " + error);
      }

      // Variables to manage the chat and chatmodel
      var chatModel = null;
      var chat      = null;

      // We MUST close the chatmodel
      function closeChat() {
         if (chatModel){
            chatModel.close();
         }
         if (chat){
         	if(chat.model){
				chat.model.close();
			}
            chat.destroy();
         }

         dojo.byId("myChat").style.display = "none";
         dojo.byId("closeButton").style.display = "none";
         dojo.byId("startButton").style.display = "block";
      }

      // After logging in, create the chat
      function startChat() {
         // Organise the display
         dojo.byId("myChat").style.display = "block";
         dojo.byId("startButton").style.display = "none";
         dojo.byId("closeButton").style.display = "block";
         // Get (or create) a chat model for the user
         
		var chatArgs = {
			"isAnonymous" : false,
			"isRichText" : true,
			"isEmbedded" :  true
		}
		
         chatModel = stproxy.getChatModel("<YourUser1>",chatArgs);
         // Create a new chat using the model
         chat = new sametime.Chat({model: chatModel},document.createElement("div"));
         dojo.byId("myChat").appendChild(chat.domNode);
      }

      // After logging in, do whatever else you need to do
      function loggedInOK() {
      	dojo.byId("startButton").disabled = false;
      }

      // Log in
      function loginUser() {
         // Replace with the appropriate user information
         stproxy.login.loginByPassword(userID, password,
                            stproxy.awareness.AVAILABLE, "I'm available",
                            loggedInOK, loginFailed);
         return true;
      }

      // When stproxy is ready, call the function
      stproxy.addOnLoad(loginUser);

      // When the UI is ready, enable the buttons
      dojo.addOnLoad(function() { dojo.byId("startButton").style.display = "block"; });

   </script>

</head>
<body class="stbody tundra">
  <div class="stlogo">
    <img src="logo.png" />
  </div>
  <div class="stmain">
     <div class="stcoll">
       <h1>Example 7</h1>
       <p class="sttext">This example illustrates how to embed a chat window on the page</p>
     </div>
     <div class="stcolr stproxy_widget">
        <div id="myChat" style="width: 100%; height: 500px; display: none;"></div>
        <input style="display:none;" type="button" id="closeButton" value="Close Chat" onclick="closeChat();" />
        <input style="display:none;" type="button" id="startButton" value="Start Chat" onclick="startChat();" disabled=true/>
     </div>
  </div>
</body>
</html>

