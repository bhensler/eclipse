Cached Resolve Sample
=====================

Overview
========
This sample demonstrates how to leverage any application which uses Sametime Resolver services by using this new component.  

The Cached Resolve Component is suitable for any external user identification. 

 
Cached Resolve Sample Content
=============================
The sample includes the following files:

1. CachedResolveExample.java	This file includes a sample application that uses the CachedResolve component.
2. RunSample.bat	            This file is a simple batch file for activating the sample program.
3. st.cached.resolve.properties	The CachedResolve component configuration file.


How It Works
============
The CachedResolveSample instantiates the CachedResolve as follows:
1.	Create the CachedResolve service:
_service =   CachedResolveComp.getInstance();

2.	Add itself as a listener to receive events:
_service.addListener(this); 

3.	Initiate the component:
_service.initiate();

The CachedResolve component need at least one logged-in STSession, for performing real resolve requests. 
The sample application creates such STSession using the createSTSession(); method.

The public void resolverAvailable() method is activated. When this event is received the application might use any of the CachedResolve
services, for example: Resolve a user name:
 	_comp.resolveUser(USER_NAME); 

The public void nameResolved(String externalUserIdentification, STId stId) method is activated when the user name was resolved. 
It receives the user's name and the resolved Sametime Id.

Running the Sample
==================
1. Customize the source:
   1.1. For Sametime server connection creation change the definitions of:
        public String _serverName = "****";
        public String _serverURL = "****";
        public int _port = 1516;

   1.2. For resolving a specific user, change the following method to use your users' names:
        private ArrayList<String> buildUsersList() {
			ArrayList<String> users = new ArrayList<String>();
			
			//This is the place to add the list of your example users
			//to resolve.
			users.add("aaaa@bbbbb");
			return users;
		}

2. Compile:
   Compile the CachedResolveExample.java to create the file CachedResolveExample.class

3. Set the CachedResolve component properties
   The provided configuration st.cached.resolve.properties includes the default values.
   
4. Customize the RunSample.bat
    4.1. Set the JAVA_HOME variable to your Java home directory. (Java 1.5)
    4.2. Set the TOOLKIT_JAR variable to the server toolkit jar (stcommsrvrtk.jar).
    4.3. Set the CACHED_RESOLVER_PROPERTIES_FILE_PATH variable to the CachedResolve configuration file. (st.cached.resolve.properties).

5. Run
   From the command line, activate RunSample by: RunSample.bat 



