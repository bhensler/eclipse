#!/bin/bash
# ***************************************************************** 
#                                                                   
# Licensed Materials - Property of IBM                              
#                                                                   
# 5724-J23                                                          
#                                                                   
# Copyright IBM Corp. 2011, 2014  All Rights Reserved.              
#                                                                   
# US Government Users Restricted Rights - Use, duplication or       
# disclosure restricted by GSA ADP Schedule Contract with           
# IBM Corp.                                                         
#                                                                   
# *****************************************************************
# Start the organization tree SA
# Set STJAVATK_JAR variable according to the location of the stcommsrvrtk.jar.  
# A copy of the jar can be found in the SDK package in the server/commserver/bin directory and copied into this directory.
STJAVATK_JAR=./stcommsrvrtk.jar
CP=./orgtree.svc.impl.jar:./orgtree.svc.api.jar:$STJAVATK_JAR
java -classpath $CP com.ibm.collaboration.realtime.orgtree.OrgTreeServerApp

