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
   <title>IBM Sametime 9 Example 2</title>
   <link type="text/css" rel="stylesheet" href="sametime.css" />
   
   <script type="text/javascript">
      // Settings for the proxy
      var stproxyConfig = {
         server: "http://<YourProxyServer>:9080",        // Replace with the appropriate server & port
         tunnelURI: "http://<ThisServer>/tunnel.html",   // The url of the openAjax Tunnel which must be on the same server as this file
         isConnectClient: false
      };

      djConfig = {
         parseOnLoad: true,
   		locale: "<%=request.getLocale().toString().replaceAll("_","-")%>"
      };
   </script>

   <script type="text/javascript" src="http://<YourProxyServer>:9080/stwebclient/latest/dojo.blue/dojo/dojo.js"></script>
   <script type="text/javascript" src="http://<YourProxyServer>:9080/stwebclient/latest/include.js?widget=livename&auto=false"></script>

   <script type="text/javascript">
      // Logged in correctly
      function loggedInOK() {
         // You can do other actions now that you are logged in
      }

      // Error callback
      function loginFailed(reason, error) {
         // Something has gone wrong: display some information
         alert("Error: " + reason + ": " + error);
      }

      // Log in anonymously
      function loginAnon() {
         stproxy.login.loginAsAnon("A.N.Other", stproxy.awareness.AVAILABLE,
                                   "I'm available", loggedInOK, loginFailed);
         return true;
      }

      // When the page is ready, call the function
      stproxy.addOnLoad(loginAnon);

   </script>

</head>
<body class="stbody tundra">
  <div class="stlogo">
    <img src="logo.png" />
  </div>
  <div class="stmain">
     <div class="stcoll">
       <h1>Example 2</h1>
       <p class="sttext">This example illustrates how to include LiveNames in your application.
                         It uses an anonymous login to make the LiveNames' status available.
       </p>
     </div>
     <div class="stcolr stproxy_widget">
        <!-- Replace with the IDs of the users you want included -->
        <div dojoType="sametime.LiveName" userId="<YourUser1>"></div>
        <div dojoType="sametime.LiveName" userId="<YourUser2>"></div>
        <div dojoType="sametime.LiveName" userId="<YourUser3>"></div>
     </div>
  </div>
</body>
</html>

