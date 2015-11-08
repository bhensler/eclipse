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
objNameString = AdminControl.completeObjectName('WebSphere:type=ConfigurationMBean,*') 
import  javax.management  as  mgmt 
objName =  mgmt.ObjectName(objNameString) 
parms = ['meetingroomcenter.customLoginPage', '']
signature = ['java.lang.String', 'java.lang.String'] 
AdminControl.invoke_jmx(objName, 'set', parms, signature)