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
   <title>IBM Sametime 9 Example 9</title>
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

</head>

<body class="stbody tundra">
  <div class="stlogo">
    <img src="logo.png" />
  </div>
  <div class="stmain">
     <div class="stcoll">
       <h1>Example 9</h1>
       <p class="sttext">This example shows how to embed the entire web client into a web page</p>
     </div>
     <div class="stcolr stproxy_widget">
        <div style="width:330px;height:550px;border:1px solid black;" id="myClient"
           dojoType="sametime.WebClient">
        </div>
     </div>
  </div>
</body>
</html>

