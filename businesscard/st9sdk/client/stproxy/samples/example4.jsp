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
   <title>IBM Sametime 9 Example 4</title>
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
   <script type="text/javascript" src="http://<YourProxyServer>:9080/stwebclient/latest/include.js?widget=widgets&auto=false"></script>

   <script type="text/javascript">
      // Error callback
      function generalErrorCallback(reason, error) {
         // Something has gone wrong: display some information
         alert("Error: " + reason + ": " + error);
      }

      ///////////////////////////////////////////////////////////////////////////////
      // Extend the context menu
      ///////////////////////////////////////////////////////////////////////////////
      stproxy.uiControl.addLiveNameMenuPlugin({
         id: "mymenu",
         label: "Add to in-page list",

         // Should this menu-entry be displayed?
         isShowEntry: function(widgets) {
            // 1. We only want to add people, not groups
            // 2. It must be from QuickFind
            // 3. We only process one at a time
            this.widgets = widgets;
            return (this.widgets.length == 1 && this.widgets[0].isLiveName
                                             && this.widgets[0].isInQuickFind);
         },

         // This happens when we click
         onClick: function(evt) {
            var liveName = new sametime.LiveName({"userId": this.widgets[0].userId});
            dojo.byId("myNamesList").appendChild(liveName.domNode);
         }
      });

      ///////////////////////////////////////////////////////////////////////////////
      // Log in and do the usual work - the above extension should just work
      ///////////////////////////////////////////////////////////////////////////////

      // User ID and password
      var userID = "<YourUser>";
      var password = "<YourPassword>";

      // After login, initialise the QuickFind widget on the page
      function loggedInOK() {
         // This will only work if we have completed the login
         if (stproxy.isLoggedIn) { // Not really needed
            var quickfind = new sametime.QuickFind();
            dojo.byId("quickFindLocation").appendChild(quickfind.domNode);
         }
      }

      function loginUser() {
         // Replace with the appropriate user information
         stproxy.login.loginByPassword(userID, password,
              stproxy.awareness.AVAILABLE, "Hello, I'm available",
              loggedInOK, generalErrorCallback);
         return true;
      }

      // Set the login to happen when the page is loaded
      stproxy.addOnLoad(loginUser);


   </script>

</head>
<body class="stbody tundra">
  <div class="stlogo">
    <img src="logo.png" />
  </div>
  <div class="stmain">
     <div class="stcoll">
       <h1>Example 4</h1>
       <p class="sttext">This example illustrates how to include a QuickFind component on the page. It
                         also shows how to extend the context menu with a new function.</p>
     </div>
     <div class="stcolr stproxy_widget">
        <div id="quickFindLocation"></div>
        <br /><br />
        <div id="myNamesList" style="border:1px solid black;" ></div>
     </div>
  </div>
</body>
</html>

