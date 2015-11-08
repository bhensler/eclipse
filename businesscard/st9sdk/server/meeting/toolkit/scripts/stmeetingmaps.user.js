// ==UserScript==
// @name        Map Reader Utility
// @namespace   redhatter1.lexdev.ibm.com
// @description Script for displaying the Maps associated with a Meeting Room
// @include     *.ibm.com/stmeetings/room/*
// @include     *.ibm.com:9080/stmeetings/room/*
// @version     1
// ==/UserScript==

//Author: Brian M McCarthy
// 7/6/12
//
//	Instructions for use:
//		First, add another 'include' statement above if you want to use this on a different domain.  
//		Install the Greasemonkey Add-on for Firefox (or Chrome, which is untested).  
//		Next, drag this script file into your Firefox window, and Greasemonkey should try to install it.
//		Alternatively, you may find directions online for importing Greasemonkey scripts.
//	
//		Make sure Greasemonkey is enabled and this script is turned on. 
//		Point your browser to a meeting room.
//		You can open and close the widget by pressing the 'm' key (by default).
//		After the widget appears, you may adjust the size of the widget.
//		The snapshot of Map Data displayed will update when you click the Refresh button.
//
//		Notes: This is purely a client-side modification. It does not interact with the server in any way.
//				The "include" should not allow any other webpage, as this script is not safe on malicious websites. 
//
//		Hotkey: The default hotkey to open and hide the widget is 'm'.
//				To rebind the hotkey, you have to look up the correct KeyCode value. 
//				You can find keycode values at https://developer.mozilla.org/en/DOM/KeyboardEvent
//

//These variables determine what hotkey opens and hides the widget 
var ST_WIDGETHOTKEYCODE = 77; 	// 77 is the keycode for 'm'. 
var ST_USESHIFT = false;  		//Determines whether the 'shift' modifer must be used to open the widget
var ST_USECTRL = false;			//Determines whether the 'ctrl' modifer must be used to open the widget
var ST_USEALT = false;			//Determines whether the 'alt' modifer must be used to open the widget
var ST_USEMETA = false;			//Determines whether the 'meta' modifer must be used to open the widget

//First, wait for the page to load
window.addEventListener("load", function(){

//This can be considered a wrapper variable that contains all of the javascript objects on the page
var st_jso = window.content.document.defaultView.wrappedJSObject;

//Make sure the Dojo 'claro' CSS style is imported
var st_CSSNode = st_jso.document.createElement('style');
st_CSSNode.innerHTML = ' @import "http://ajax.googleapis.com/ajax/libs/dojo/1.5/dijit/themes/claro/claro.css";';
st_CSSNode.setAttribute('type', 'text/css');
st_jso.document.head.appendChild(st_CSSNode);

var st_Node = st_jso.document.createElement('div');
st_Node.setAttribute ('id', 'st_myContainer');
st_Node.setAttribute ('class', 'claro');
st_Node.innerHTML = '<button id="st_refButton" class="st_mapButton" type="button"  style="float:right;">Refresh</button>';
st_Node.style.display = 'none';
st_jso.document.body.appendChild(st_Node);

st_jso.dojo.require("dijit.Dialog");
var st_diagContainer = new st_jso.dijit.Dialog({title: "Meeting Room Maps", style: "width:800px;height:600px;resize:both;"},"st_myContainer");		
st_diagContainer.startup();

var st_rButton = new st_jso.dijit.form.Button({label: "Refresh", onClick: ButtonClickRefresh}, "st_refButton");
var st_tabContainer = null;

var st_contentStoreForDiff = new Array(); //used in highlighting new/different values after a refresh
var st_lineStoreForDiff = new Array();

var st_FocusedPane = "foo";

// This uses dojo.connect() to listen for events from rtc4web
// The events are used to determine which maps in the room are active
var st_ActiveMapsTitles = new Array();
var st_context =
{	
	listener: function(st_UpdateMsgs)
	{
	//{"sid":"dc77dbbe-36cd-4bad-845d-3d1280bac9e3","op":"change","cn":"UserMap","type":"map","key":"susanadams62@us.ibm.com","value":"susanadams62@us.ibm.com","time":1346185790184}
		if(st_UpdateMsgs.op !== "removeContainer" && st_UpdateMsgs.op != null)
		{
			st_ActiveMapsTitles[st_UpdateMsgs.cn]=1;			
		}
		else
		{
			st_ActiveMapsTitles[st_UpdateMsgs.cn]=0;			
		}
	
		//alert(st_UpdateMsgs.cn);
	},
}

st_jso.dojo.connect(st_jso.rtc4web.core.RTCUpdater, "handleUpdate", st_context, "listener" );

function ButtonClickAction (zEvent)
{	
		
	st_rButton.disabled = false;
	
	//If the widget is already open, destroy it
	if(st_diagContainer.open == true){ 
		
		st_Node.style.display='none';
		st_diagContainer.hide();		
	}
	else if(st_tabContainer == null){ //first time execution, create the widget
	
		st_Node.style.display='inline';
		
		// --- Grab the Map Data
		st_jso.dojo.require('dojox.html.entities');
		var st_x = st_jso.session.collections;
		var st_titles = new Array(); //contains the names of the Maps
		
		var st_tCount  = 0;
		var st_contents = new Array(); //contains the contents of the Maps	
		
		var st_temp=null;
		var st_keys=null;
		for(key in st_x){		
			st_temp = st_x[key];
			if(st_ActiveMapsTitles[key] && st_ActiveMapsTitles[key]  === 1)
			{
				//alert("success: " + key);
				st_titles[st_tCount] = key;
			}
			else
			{
				st_titles[st_tCount] = "st_inactive_map";
			}	
			st_contents[st_tCount] = " ";	  			
			if(st_temp.collection != null){			
				st_keys = st_temp.collection.getKeyList();
				st_contents[st_tCount] +="<table border=1 width='200'>";				
				for(var st_kk = 0; st_kk< st_keys.length; st_kk++){									
					if(st_jso.dojo.isObject(st_temp.collection.item(st_keys[st_kk]))){
						st_contents[st_tCount] += "<tr><td>"+st_keys[st_kk]+"</td><td>"+st_jso.dojox.html.entities.encode(st_jso.dojo.toJson(st_temp.collection.item(st_keys[st_kk])))+"</td></tr>";
						
						st_lineStoreForDiff[st_keys[st_kk]] = st_jso.dojox.html.entities.encode(st_jso.dojo.toJson(st_temp.collection.item(st_keys[st_kk])));
					}
					else{
						st_contents[st_tCount] += "<tr><td>"+st_keys[st_kk]+"</td><td>"+st_jso.dojox.html.entities.encode(st_temp.collection.item(st_keys[st_kk]))+"</td></tr>";
						
						st_lineStoreForDiff[st_keys[st_kk]] = st_jso.dojox.html.entities.encode(st_temp.collection.item(st_keys[st_kk]));
					}					
				}		  
				st_contents[st_tCount] +="</table>";
			}  
			st_tCount++;	  
		}		
		st_contentStoreForDiff = st_contents.slice(0); //creates a copy of the array
		
		// --- Finished Grabbing the Map Data
		
		 //--- Build TabContainer Widget ---		  
		st_jso.dojo.require("dijit.layout.TabContainer");
		st_jso.dojo.require("dijit.layout.ContentPane");
		
		st_diagContainer.show();
		
		st_tabContainer = new st_jso.dijit.layout.TabContainer({class: "claro", style:"height: 525px; width: 770px;"});		
		st_tabContainer.startup();
		
		for(var st_ii = 0; st_ii < st_titles.length; st_ii++){
			if(st_titles[st_ii] !== "st_inactive_map")
			{			
				st_tabContainer.addChild(new st_jso.dijit.layout.ContentPane({
					title:st_titles[st_ii],		
					content:st_contents[st_ii]
				}));
			}
		}
		
		st_rButton.placeAt(st_jso.document.getElementById('st_myContainer'));
		st_tabContainer.placeAt(st_jso.document.getElementById('st_myContainer'));
		st_tabContainer.resize({w: 800, h: 520});	//this fixes a display glitch
		
	}
	else{		
		st_Node.style.display='inline';
		st_diagContainer.show();
	}
}

//This function is called when the Refresh button is clicked. It will update the information in the Widget rather than creating a new one as the Show/Hide button does.
function ButtonClickRefresh (stEvent){
	
  if(st_tabContainer != null){	 
	
	// --- Grab the Current Map Data
	st_jso.dojo.require('dojox.html.entities');
	var st_x = st_jso.session.collections;
	var st_titles = new Array(); //contains the names of the Maps
	var st_tCount  = 0;
	var st_contents = new Array(); //contains the contents of the Maps	
	var st_contentsTemp = new Array(); //temp array used for the diff
	
	var st_temp = null;
	var st_keys = null;
	for(key in st_x){
		st_temp = st_x[key];
		if(st_ActiveMapsTitles[key] && st_ActiveMapsTitles[key] === 1)
		{
			//alert("success: " + key);
			st_titles[st_tCount] = key;
		}
		else
		{
			st_titles[st_tCount] = "st_inactive_map";
		}	
		st_contents[st_tCount] = " ";
		st_contentsTemp[st_tCount] = " ";
		if(st_temp.collection != null){
			st_keys = st_temp.collection.getKeyList();
			st_contents[st_tCount] += "<table border=1 width='200'>";
			st_contentsTemp[st_tCount] += "<table border=1 width='200'>";
			for(var st_kk = 0; st_kk< st_keys.length; st_kk++){			
				if(st_jso.dojo.isObject(st_temp.collection.item(st_keys[st_kk]))){
					st_contents[st_tCount] += "<tr><td>"+st_keys[st_kk]+"</td><td>"+st_jso.dojox.html.entities.encode(st_jso.dojo.toJson(st_temp.collection.item(st_keys[st_kk])))+"</td></tr>";
					
					if (st_lineStoreForDiff[st_keys[st_kk]] != st_jso.dojox.html.entities.encode(st_jso.dojo.toJson(st_temp.collection.item(st_keys[st_kk])))){
					
						st_contentsTemp[st_tCount] += "<tr><td><font color=red>"+st_keys[st_kk]+"</font></td><td><font color=red>"+st_jso.dojox.html.entities.encode(st_jso.dojo.toJson(st_temp.collection.item(st_keys[st_kk])))+"</font></td></tr>";
					}
					else{
						st_contentsTemp[st_tCount] += "<tr><td>"+st_keys[st_kk]+"</td><td>"+st_jso.dojox.html.entities.encode(st_jso.dojo.toJson(st_temp.collection.item(st_keys[st_kk])))+"</td></tr>";
					}
					
					
					st_lineStoreForDiff[st_keys[st_kk]] = st_jso.dojox.html.entities.encode(st_jso.dojo.toJson(st_temp.collection.item(st_keys[st_kk])));
					
					
				}
				else{
					st_contents[st_tCount] += "<tr><td>"+st_keys[st_kk]+"</td><td>"+st_jso.dojox.html.entities.encode(st_temp.collection.item(st_keys[st_kk]))+"</td></tr>";
					
					if (st_lineStoreForDiff[st_keys[st_kk]] != st_jso.dojox.html.entities.encode(st_temp.collection.item(st_keys[st_kk]))){
						
						st_contentsTemp[st_tCount] += "<tr><td><font color=red>"+st_keys[st_kk]+"</font></td><td><font color=red>"+st_jso.dojox.html.entities.encode(st_temp.collection.item(st_keys[st_kk]))+"</font></td></tr>";
					}
					else{
						st_contentsTemp[st_tCount] += "<tr><td>"+st_keys[st_kk]+"</td><td>"+st_jso.dojox.html.entities.encode(st_temp.collection.item(st_keys[st_kk]))+"</td></tr>";
					}
					st_lineStoreForDiff[st_keys[st_kk]] = st_jso.dojox.html.entities.encode(st_temp.collection.item(st_keys[st_kk]));
						
				}
			}		  
			st_contents[st_tCount] += "</table>";
		}  
		st_tCount++;	  
	}
	// --- Finished Grabbing the Map Data, moving on to updating the tabs
	
		
	if(st_tabContainer.hasChildren())
	{
		//get selected child		
		st_FocusedPane = st_tabContainer.selectedChildWidget.get('title');
	
	
		//emptying the tabContainer
		var st_childArr = st_tabContainer.getChildren();				
		for(var st_jj = 0;st_jj < st_childArr.length;st_jj++)
		{
			st_tabContainer.closeChild(st_childArr[st_jj]);
		}
		
		for(var st_ii = 0; st_ii < st_titles.length; st_ii++){
			if(st_titles[st_ii] !== "st_inactive_map")
			{
							
				if(st_contents[st_ii] != st_contentStoreForDiff[st_ii] )
				{
					st_tabContainer.addChild(new st_jso.dijit.layout.ContentPane({
						title:"<font color=red>"+st_titles[st_ii]+"</font>",		
						content:st_contentsTemp[st_ii] }));
				}
				else
				{
					st_tabContainer.addChild(new st_jso.dijit.layout.ContentPane({
					title:st_titles[st_ii],		
					content:st_contentsTemp[st_ii] }));
				}
			}
		}
		
		
		//Restoring the focus to the correct tab
		st_childArr = st_tabContainer.getChildren();		
		for(var st_kk = 0;st_kk < st_childArr.length;st_kk++)
		{
			if(st_childArr[st_kk].get('title') === st_FocusedPane)
			{
				st_tabContainer.selectChild(st_childArr[st_kk], true);
			}
			else if ("<font color=red>"+st_childArr[st_kk].get('title')+"</font>" === st_FocusedPane)
			{
				st_tabContainer.selectChild(st_childArr[st_kk], true);
			}
			else if (st_childArr[st_kk].get('title') === "<font color=red>"+st_FocusedPane+"</font>")
			{
				st_tabContainer.selectChild(st_childArr[st_kk], true);
			}
		}
	}
	
	st_contentStoreForDiff = st_contents.slice(0); //creates a copy of the array	
	
  }
  
}

//This Event Listener will open the widget if you press the correct key.
window.addEventListener('keydown', function(st_Event){
	if( (st_Event.keyCode == ST_WIDGETHOTKEYCODE) && (st_Event.shiftKey == ST_USESHIFT)
		&& (st_Event.altKey == ST_USEALT) && (st_Event.ctrlKey == ST_USECTRL) 
		&& (st_Event.metaKey == ST_USEMETA)){ 
			
		ButtonClickAction(1);
	}	
}, false);


//The following 3 Event Listeners deal with resizing the window
var st_MouseDown = false;
window.addEventListener("mousedown", function(){
	st_MouseDown = true;

}, false);
window.addEventListener("mouseup", function(){
	st_MouseDown = false;
	
}, false);
window.addEventListener("mousemove", function(){
	if(st_MouseDown==true && st_tabContainer)
	{
		var st_DialogNode = st_jso.document.getElementById('st_myContainer');
		st_tabContainer.resize({w: st_DialogNode.offsetWidth-30, h: st_DialogNode.offsetHeight-80});   
	
	}
}, false);



//--- Style our newly added elements using CSS. This is a Greasemonkey-specific function.
GM_addStyle ( (<><![CDATA[
		
    #st_myContainer {
        position:             	absolute;
        top:                    0;
        left:                   0;
        font-size:              14px;
        /*border:                 1px outset black;*/
        opacity:                0.90;
        z-index:                222; /*should not be raised much higher or menu breaks*/
        padding:                3px 5px;
		width:					600px;
		height:					600px;		
		/*overflow: 				auto;*/
		/*resize: 				both;*/
	}
	#st_PalletNode {		
        position:             	absolute;
        top:                    50;
        left:                   0;
        font-size:              14px;
		text-align:				left;		
		background:             none; 
        opacity:                0.90;
        z-index:                225; /*should not be raised much higher or menu breaks*/
        padding:                3px 5px;
		overflow: 				auto;		
    }
	#st_myContainer_underlay {
		display: 				none;
	}
	.st_mapButton {
        cursor:                 pointer;				
		float: 					none;
    }
    #st_myContainer p {
        color:                  black;
        background:             white;
		width:					2400px;
		height: 				2600px;
    }
]]></>).toString () );

}, false);


