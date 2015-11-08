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
   <title>IBM Sametime 9 Example 1</title>
   <link type="text/css" rel="stylesheet" href="sametime.css" />
</head>
<body class="stbody tundra">
  <div class="stlogo">
    <img src="logo.png" />
  </div>
  <div class="stmain">
     <div class="stcoll">
       <h1>Example 1</h1>
       <p class="sttext">This simple example illustrates how to launch the
                         pre-made web client from within an application.</p>
       <p class="stfootnote">Please note that you will need to allow popup
                         windows for this to work correctly.</p>
     </div>
     <div class="stcolr">
        <script type="text/javascript">
            function launchTheIMClient() {
               window.open("http://<YourProxyServer>:9080/stwebclient/popup.jsp",
                           "_blank",
                           "left=100px,top=100px,width=320,height=600,status=no,toolbar=no,directories=no,resizable=yes,scrollbars=no");
            }
        </script>
        <input type="button" onclick="launchTheIMClient();" value="Start" />Click the button to launch the client.
     </div>
  </div>
</body>
</html>

