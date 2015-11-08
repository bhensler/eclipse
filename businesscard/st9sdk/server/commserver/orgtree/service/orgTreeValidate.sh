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
# Start the organization tree validator
CP=./orgtree.svc.impl.jar:./orgtree.svc.api.jar
java -classpath $CP com.ibm.collaboration.realtime.orgtree.OrgTreeValidator


