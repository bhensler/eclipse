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
   <title>IBM Sametime 9 Example 11</title>
   <link type="text/css" rel="stylesheet" href="sametime.css" />

   <script type="text/javascript">
      // Settings for the proxy
      var stproxyConfig = {
         // Replace with the appropriate server & port
         server: "http://<YourProxyServer>:9080",
         tunnelURI: "http://<ThisServer>/tunnel.html",
         isConnectClient: false
      };

      djConfig = {
        parseOnLoad: true,
  		locale: "<%=request.getLocale().toString().replaceAll("_","-")%>"
      };

   </script>
   <script src="http://<YourProxyServer>:9080/stwebclient/latest/include.js"></script>

   <script type="text/javascript">
      // User ID and password
      var userID = "<YourUser>";
      var password = "<YourPassword>";

      // Status message
      statusText = [ "Offline", "Available", "Away", "Do not disturb", "", "In meeting" ];

      // Error callback
      function somethingFailed(reason, error) {
         // Something has gone wrong: display some information
         alert("Error: " + reason + ": " + error);
      }

      // Extend the stproxy object with (dis)connect, just like Dojo
      stproxy.addOnLoad(function() {
		   stproxy.hitch = {

            // Hook a function into an event
			   connect: function(parent,child,call){
				   try{
					   scope = (parent || window);
					   var original = parent[child];
					   for(var x in scope){
						   if(scope[x] == parent[child]){
							   scope[x] = function(){
								   original.apply(this,arguments);
								   call.apply(this,arguments);
							   }
							   break;
						   }
					   }
					   return {"parent":parent,"child":child,"original":original};
				   } catch(e){
					   console.error("Error: stproxy.hitch.connect: "+e)
					   console.dir(arguments);
				   }
			   },//END of connect

            /// Remove the connected hook into an event
			   disconnect: function(obj){
				   try{
					   if(obj && obj.parent && obj.child && obj.original){
						   obj.parent[obj.child] = function(){
							   obj.original.apply(this,arguments);
						   }
					   }
				   } catch(e){
					   console.error("Error: stproxy.hitch.disconnect: "+e)
					   console.dir(arguments);
				   }
			   }//END of disconnect

		   } //END of hitch


	   });


      // After logging in, connect the user's livename so that the correct
      // status icon is displayed whenever the status changes.
      function loggedInOK() {
         var model = stproxy.getLiveNameModel("<YourOtherUser>", {"isInBuddyList":false, "forceWatchlist":true}); 
         var nameElem = document.getElementById("uName");
         nameElem.innerHTML = model.id;

         // Note that this uses connect so as to not interfere with other functions.
         stproxy.hitch.connect(model, "onUpdate", function() {
                        var elem = document.getElementById("stat");
                        elem.innerHTML = statusText[model.status];
                    });
      }


      // Logged out
      function loggedout() {
         alert("OK, I'm logged out");
      }

      // Log the user out
      function doLogout() {
         stproxy.login.logout(true, loggedout, somethingFailed);
      }

      // When stproxy is ready, log in
      stproxy.addOnLoad( function(){ 
         // Replace with the appropriate user information
         stproxy.login.loginByPassword(userID, password,
                            stproxy.awareness.AVAILABLE, "I'm available",
                            loggedInOK, somethingFailed);
      });


</script>
</head>
<body class="stbody">
  <div class="stlogo">
    <img src="logo.png" />
  </div>
  <div class="stmain">
     <div class="stcoll">
       <h1>Example 11</h1>
       <p class="sttext">This example illustrates how to create pseudo-LiveNames in your application,
                         without the use of Dojo.
       </p>
     </div>
     <div class="stcolr">
         <span id="stat" style="font-size:9px;font-style=italic;" ></span>
         &nbsp;
         <span id="uName" style="font-size:12px;font-weight:bold"></span>
     </div>
  </div>
</body>


</html>
