/* Copyright IBM Corp. 2008, 2014  All Rights Reserved.              */

	var MAX_PROBLEM_AREA_SIZE = 2048;

	function validateParameters(custNameId, techAreaId, pbDescId, errorTableId, errorMessageId) {

		var errorTable = document.getElementById(errorTableId);
		var errorMessage = document.getElementById(errorMessageId);		
				
		var custName = document.getElementById(custNameId);
		if (custName.value.length == 0) {
			errorTable.style.display = "";
			errorMessage.innerHTML="Error: You must select a Customer name.";
			return false;
		}
		else
			
		var techArea = document.getElementById(techAreaId);		
		if (techArea.value.length == 0) {
			errorTable.style.display = "";
			errorMessage.innerHTML="Error: You must select a Technical Area.";
			return false;
		}
		
		var pbDesc = document.getElementById(pbDescId);
		if (pbDesc.value.length > MAX_PROBLEM_AREA_SIZE) {
			errorTable.style.display = "";
			errorMessage.innerHTML="Error: The text has exceeded maximum size of 2048 characters.";
			return false;
		}
		
		// Everything ok
		return true;		
	}
	
	function closeCall(thisObj, callNumber, hiddenInputId) {
		var answer = confirm("Do you really want to close this call?")
		
		if(answer) {
			var hiddenInput = document.getElementById(hiddenInputId);
			hiddenInput.value = callNumber;
			thisObj.form.submit();
		}
		else
			thisObj.selectedIndex = 0;
	}
	
	function reopenCall(thisObj, callNumber, hiddenInputId) {
		var answer = confirm("Do you really want to reopen this call?")
		
		if(answer) {
			var hiddenInput = document.getElementById(hiddenInputId);
			hiddenInput.value = callNumber;
			thisObj.form.submit();
		}
		else
			thisObj.selectedIndex = 1;
	}
	
	function loadNewCall(chatUrl) {
		thisObj.form.submit();
	}
	
	function rateResponse(communityName, type, reqId)
	{
		rateResponseWindow = window.open('/CallCenter_app/responseRating.jsp?communityName=' + communityName + '&type=' + type + '&reqId=' + reqId+ '','Rate Response','width=400,height=200,toolbar=0,resizable=0');
		rateResponseWindow.focus();
		return false;
	}
		
	function openTranscript(chatId,name,chatRoomType)
	{
		transcriptWindow = window.open('/CallCenter_app/transcriptRoom.jsp?chatId=' + chatId + '&name=' + name + '&type=' + chatRoomType + '','Chat_Transcript','width=800,height=240,toolbar=0,resizable=0');
		transcriptWindow.focus();
		return false;
	}
	
	function logNewCall(url) {
		window.location = url;
	}
	
	function activateTab(id) {
	}
	
	function setUsernameFocus(elemId)
	{
		document.getElementById(elemId).focus();
	}
	
	function openComunityMessage(communityName, type)
	{
		window.location = "/CallCenter_app/communityMessage.jsp?communityName=" + communityName + "&type=" + type;
		return false;
	}
	
	function viewChatFaqDetails(faqId)
	{
		window.location = '/CallCenter_app/chatFaqDetails.jsp?faqId=' + faqId;
		return false;
	}
	
	function toggleResponses(thisObj, otherLinkId, tableId, flag) 
	{
		var table = document.getElementById(tableId);
		var otherLink = document.getElementById(otherLinkId);
		if (flag)
			table.style.display = "";
		else 
			table.style.display = "none";	
		thisObj.style.display = "none";
		otherLink.style.display = "";
		return false;
	}

	function removeSelected(thisObj, list, hiddenRemoveSToptionsId, callerType)
	{
		var flag = false;
		for (i = 0; i < list.length; i++) {
			if (list[i].checked) {
				flag = true;
				break;
			}				
		}
		if (!flag) {
			alert("You must select a "+callerType+" from the list.");
			return false;
		}
		
		var hiddenRemoveSToptions = document.getElementById(hiddenRemoveSToptionsId);
		var answer = confirm("Do you also want to remove the associated chat room, folder and community for this "+callerType+"? \n You will lose all of the content which can't be recovered." );
		
		if (answer) 
			hiddenRemoveSToptions.value="true";
			
		thisObj.form.submit();
	}
