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
   <title>IBM Sametime 8.5.2 IFR 1 Example 5</title>
   <link type="text/css" rel="stylesheet" href="sametime.css" />
   <link rel="stylesheet" type="text/css" href="http://<YourProxyServer>:9080/stwebclient/latest/dojo.blue/sametime/themes/WebClientAll.css" />

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
   <script type="text/javascript" src="http://<YourProxyServer>:9080/stwebclient/latest/include.js?widget=widgets&auto=false"></script>

   <script type="text/javascript">
      // User ID and password
      var userID = "<YourUser>";
      var password = "<YourPassword>";

      // Error callback
      function loginFailed(reason, error) {
         // Something has gone wrong: display some information
         alert("Error: " + reason + ": " + error);
      }

      // OK or cancel pressed - hide the widget and show the button
      function handleComplete() {
         dojo.byId("aware").style.display = "none";
         dojo.byId("changeButton").style.display = "block";
      }

      // Click on the show button - display the widget and hide the button
      function handleButton() {
         dojo.byId("aware").style.display = "block";
         dojo.byId("changeButton").style.display = "none";
      }

      // Logged in - set up awareness button actions
      function loggedInOK() {
         var aware = new sametime.Awareness();
         dojo.byId("aware").appendChild(aware.domNode);
         dojo.connect(aware, "_ok", handleComplete);
         dojo.connect(aware, "onCancel", handleComplete);
         handleButton(); // Display it initially
      }

      // Log in to Sametime
      function loginUser() {
         // Replace with the appropriate user information
         stproxy.login.loginByPassword(userID, password,
                  stproxy.awareness.AVAILABLE, "I'm available", loggedInOK, loginFailed);
         return true;
      }

      stproxy.addOnLoad(loginUser);

   </script>

</head>
<body class="stbody tundra">
  <div class="stlogo">
    <img src="logo.png" />
  </div>
  <div class="stmain">
     <div class="stcoll">
       <h1>Example 5</h1>
       <p class="sttext">This example illustrates how to include a component on
                         the page to allow you change your presence status.
       </p>
     </div>
     <div class="stcolr stproxy_widget">
        <div id="changeButton"><input type="button" value="Change Awareness Status" onclick="handleButton();"/></div>
        <div id="aware"></div>
     </div>
  </div>
</body>
</html>

