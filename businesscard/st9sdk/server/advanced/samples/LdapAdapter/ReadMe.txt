Ldap Adaptor

Description:
This sample demonstrates how to extend the UserInformationProvider extension point.
In the sample, user information is retrieved from a Sametime server ldap using the UserInfo service.

In order to run the application you need to specify your sametime server hostname in the userinfoapp.properties
file. Then it can be installed as a plugin on your sametime advanced server where it will be used as a source of
user information or it can be run as a seperate java app on the command line:  

	java com.ibm.stadvanced.samples.ldapadaptor.LdapUserInformationProvider <userid>
	
	
To build the project you will need to create in your Eclipse based development 
environment a classpath variable named 

	SAMETIME_ADVANCED_INSTALL_DIR
	
and set its value to the install path of Sametime Advanced. This will assure
that all the relevant dependencies are available to the project. Alternatively,
you can remove the references to this variable from the project classpath and 
manually add the following jar files to the project classpath:

	<AppServer>/optionalLibraries/rtc/orgcollab.extensions-8.0.jar
