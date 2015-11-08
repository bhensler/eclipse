Changes in ST9, since beta1 - August 2012
	over 50 significant fixes/changes/additions
	Upgrading status to GA
	Improvements to samples, JavaDoc and Developer's Guide.
	Added new packages
		- policy - for reading user policies
		- polling - added new Exception type and ability to send polls
		- projector - add new Exception type
		- summary - for getting temporal room data, used in generating reports
		- userinfo - for getting user into, and used in resolving userId's to other user info

Http
	Created generic base exception for all http based services, most former exceptions now derive from this
	Added ability to get HTTP body as JSON Array
	More control over Max threads, connections and connections per server
	Added some defaults if/when services do not set their content type, preventing bugs
	Allow piping responses directly to user defined input steams, to prevent buffer copies.
	

SeviceBase
	Clean-up login timer on reuse
	Allow userid to be set explicitly 
	Allow local cookie lookup via cookie name
	
Annotations
	method to get shared pointer image
	
AppShare 
	Added hosting capability
	Performance improvements

Messages
	improved error checking on some methods
	
Configuration
	Allow setting of configuration by admin user.

DocShare
	Allow creating blank documents
	Allow setting page orientation for blank pages
	Allow a file to be refreshed with a more recent version
	Allow file to be uploaded without conversion
	better error reporting
	new method to get filename from data structure
	
UrlShare
	re-engineered to be asynchronous
	allow URLs in library to be shared
	
Projector
	Allow method to end current presentation
	better error handling	

Changes in beta1 - August 2012 (since 8-Feb-2012 alpha5 release)
	over 30 significant fixes/changes/additions
	Upgrading status to beta1
	All samples re-worked to be more consistent
	Added 3 new samples: AdminSample, SsoLoginSample, ValidatorSample
	More details in the developers guide about RealTime support, and running the samples
		
Http
	fixed bugs related to fetching body as input stream
	can now deal with http responses not having content type
	set default to 16 total connections, 8 connections per server
	set default user agent to Apache-HttpClient/release
	handle invalid cookie, by removing related headers
	improved support for multi-byte character support in all uploads (ex: files names)
	more control over logging
	new low level API to inspect responses before continuing on

SeviceBase
	exposed new getCookie method
	support several new login by LtpaToken method

Annotations
	built-in pointer images and API support
	uses new batch change support

AppShare 
	cursor fetching
	URL for screen image fetching
	improved support for appshare cookies

Messages
	improved error checking on some methods
	
Configuration
	more flexible binding to any service base
	more details in exceptions
	a few more pre-defined configuration items

DocShare
	upload a file w/o conversion (ex: a file to share, not present)
	better error checking file deletion
	more details in exception classes
	more details available after upload, distinction between file name, and file alias
	improved support for multi-byte character support in all uploads (ex: files names)
	
Participants
	all user info, and user specific attributes now exposed
	more efficient handling of user data

UrlShare
	allow all URLs to be deleted
	more details in exception classes
	
Rtc4Web
	support batch changes against 8.5.2 IFR1 server
	allows other context (ex: for use with Sametime Proxy)
	new and improved methods to end the session
	new method for getting updates synchronously
	fixed bug in event delivery allowing mutation of event objects
	now handles the container removed event
	no longer deliver empty updates
	better and more efficient thread synchronization


Changes in the latest drop: alpha4
- Documented SSL support in the guide.
 Note: Code depending on these files will have to be recompiled.
2. Server changes to permit administrators to create unlimited number of rooms. 
3. The server side plug-in sample project is now source-based, and tested pre-built jar, that can be directly deployed.
4. The RoomException class now has more getters for distinct error types, and more information in Exception message
5. All SDK has been adapted to run under a Java5 SDK.
6. Changes to the "RoomParams" interface. Enumerations have been refactored, renamed etc..
7. Updates to the toolkit documentation about how to deploy,test and run server and client samples.

Changes in pre-latest Drop - alpha3
- customloginpageSample.html has been converted from HTML to JSP, the new page is customloginpageSample.jsp
- New file - mock_post.jsp - under "samples" to simulate a way to set cookies.  Makes it easy to change configuration values
- New constructor in RoomServices that takes a URL object for better error checking.
- All RoomServices constructors throw a MalformedURLException. Code will have to be recompiled.
- All methods dealing with HTTP I/O now throw an I/O exception, so communication issues can be distinguished from REST API issues. 
- Samples have changed to show deletion of room, and deal with new exceptions
- Updated guide showing how to deploy custom login sample and use it.
- MeetingRoom object has additional constructors.
- Login throws an exception if it fails. Code will have to be recompiled.
- MeetingRoom.get() method throws RoomException
- Added a new releasenotes.txt file under the "doc" folder of the SDK. It documents all the
current, known bugs with the SDK and other pertinent release information.

Changes in April 29 Drop - alpha2
0. More API changes to make it more consistent, conventional and simpler to use.
1. Additional setters/getters in MeetingRoom.java to retrieve library size, etc.
2. SearchQueryParams.java has been renamed as QueryControl. 
3. Enumerations formerly in SearchQueryParams have either been removed or refactored in the QueryControl class. 
4. Methods have been renamed/changed in the QueryControl class.
5. All searches now take the QueryControl class.
6. A default "singleton" QueryControl class is provided by static method.
7. Some methods have been renamed/changed in the QueryControl class.
8. More sort keys have been defined in the QueryControl.SORT_KEYS enumeration.
9. To improve ease of use, search type is now in the method name, no longer in the SearchQueryParams class.
10. Complete, and improved JavaDoc in all classes and packages.
11. Changes to the guide to reflect API changes.  Added info on creating unlimited rooms.