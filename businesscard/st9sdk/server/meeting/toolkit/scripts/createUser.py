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
# This script creates a user with the passed in values. All values are required.
# Example usage: createUser.py userUid userPwd userCn userSn userEmail 
#
import sys,string

def checkForUser(userUid):
	existingUsers = AdminTask.searchUsers("[-uid " + userUid+"]")
	if existingUsers:
		userUniqueName="uid="+userUid+",o=defaultWIMFilebasedRealm"
		print "Deleting User User " + userUniqueName
		AdminTask.deleteUser("[-uniqueName " + userUniqueName+"]")

def createUser(userUid, userPwd, userCn, userSn, userMail):
	userProperties="[-uid " + userUid + " -password " + userPwd + " -confirmPassword " + userPwd + " -cn '" + userCn + "' -sn " +userSn+" -mail " + userMail+ "]"
	AdminTask.createUser (userProperties)
	AdminConfig.save();
	print "Created user "+userProperties

def processAndCreate(input):
	userName=input.split()
	userCn=input
	userSn=userName[1]
	userUid=input.lower().replace(' ','_')
	userMail=userUid +"@company.com"

	checkForUser(userUid)
	createUser(userUid, "sametime", userCn, userSn, userMail)

#x = 1
#input="Test User%d"
#while x < 100:
#	processAndCreate(input % x)
#	x += 1
processAndCreate("Rob Fox")
processAndCreate("Bill Quinn")
