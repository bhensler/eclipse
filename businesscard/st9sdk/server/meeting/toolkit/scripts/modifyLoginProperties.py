# ***************************************************************** 
#                                                                   
# Licensed Materials - Property of IBM                              
#                                                                   
# 5724-E76, 5724-L21, 5724-i67, 5724-L64, 5655-M44                  
#                                                                   
# Copyright IBM Corp. 2011  All Rights Reserved.                    
#                                                                   
# US Government Users Restricted Rights - Use, duplication or       
# disclosure restricted by GSA ADP Schedule Contract with           
# IBM Corp.                                                         
#                                                                   
# ***************************************************************** 

#
# This script modifies the internal wimconfig.xml file to return mail as the user principal. The order of the login properties
# matters. NOTE: THIS SCRIPT WILL GENERATE A WARNING BECAUSE THE MODIFICATION DOES NOT CORRESPOND TO THE XSD FILE USED BY
# WEBSPHERE. BUT THE CHANGE TAKES AND DOES NOT CAUSE ANY CONFIGURATION ISSUES. IGNORE THE WARNING. 
#
import sys
adminUid=sys.argv[0]
adminEmail=adminUid + "@ibm.com"
print "Updating InternalFileRepository login properties to return mail. Please IGNORE warning generated by script about config"
AdminTask.updateIdMgrRepository ('[-id InternalFileRepository -loginProperties mail;uid]')
AdminConfig.save()
#Update the admin user to have an e-mail address. Otherwise, all server auth CLI operations
# will fail because of the above modification to wimconfig.xml
print "Updating admin user to have an e-mail" + adminEmail
AdminTask.updateUser ('[-uniqueName uid=wasadmin,o=defaultWIMFileBasedRealm -mail '+ adminEmail + ']')
AdminConfig.save()
print "Done.Restart All servers for change to propagate"