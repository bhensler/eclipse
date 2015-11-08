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
   <title>IBM Sametime 9 Example 8</title>
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

   <script type="text/javascript" src="http://<YourProxyServer>:9080/stwebclient/latest/include.js"></script>
   <script type="text/javascript">

      // Error callback
      function loginFailed(reason, error) {
         // Something has gone wrong: display some information
         alert("Error: " + reason + ": " + error);
      }


      // After logging in, we need to set up the presence behavior
      function loggedIn() {
         var liveNameModel = stproxy.getLiveNameModel("<YourUser1>", {"isInBuddyList":false});

         // When the model changes, execute this function
         liveNameModel.onUpdate = function() {
            // Find the livename element
            var elem = document.getElementById("myLiveName");
            // this=the liveNameModel
            // Set the string to the value of the displayname if it exists
            if (typeof this.displayname != "undefined") {
               elem.innerHTML = this.displayName;
            } else {
               elem.innerHTML = this.id;
            }
            // If the user is available, text should be blue on yellow...
            if (this.status == stproxy.awareness.AVAILABLE) {
               elem.style.color = "blue";
               elem.style.backgroundColor = "yellow";
            } else { // ... otherwise yellow on blue
               elem.style.color = "yellow";
               elem.style.backgroundColor = "blue";
            }
         }

         return true;
      }

      // Anonymous login
      function loginAnon() {
         stproxy.login.loginAsAnon("A.N.Other", stproxy.awareness.AVAILABLE, "I'm available",
                                   loggedIn, loginFailed);
      }

      stproxy.addOnLoad(loginAnon);

   </script>

</head>
<body class="stbody tundra">
  <div class="stlogo">
    <img src="logo.png" />
  </div>
  <div class="stmain">
     <div class="stcoll">
       <h1>Example 8</h1>
       <p class="sttext">This example illustrates how to customise LiveNames in your application, by
                         utilising the LiveNameModel. It uses an anonymous login to make the LiveNames' status available.
       </p>
       <p class="stfootnote">This example does not use the Dojo Toolkit, and illustrates the potential for
                         managing users' presence awareness using only the basic functions of the library.</p>
     </div>
     <div class="stcolr">
        <p class="stfootnote">Offline users have blue background with yellow text
                         and online users have yellow background with blue text.</p>
        <div id="myLiveName" style="width:100%; height:100%;"></div>
     </div>
  </div>
</body>
</html>

