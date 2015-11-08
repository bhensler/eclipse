import com.lotus.sametime.core.types.STId;
import com.lotus.sametime.telephony.TelephonyStatus;
import com.lotus.sametime.telephonymanager.TelephonyAdapterComp;
import com.lotus.sametime.telephonymanager.api.TelephonyAdapterErrorEvent;
import com.lotus.sametime.telephonymanager.api.TelephonyAdapterListener;
import com.lotus.sametime.telephonymanager.api.TelephonyAdapterService;
import com.lotus.sametime.telephonymanager.exceptions.MissingConfigurationInfoException;

/**
 * Sample application which uses the TelephonyAdapter Component.
 * 
 *  The sample application demonstrates how to initiate the TelephonyAdapter component,
 *  register as a listener in order to get the component's notifications and issue the 
 *  following requests:
 *  1. Watch user sametime status changes.
 *  2. Publish telephony status changes.
 *  
 *  The TelephonySample implements the TelephonyAdapterListener in order to get the
 *  TelephonyAdapter component events.
 *  
 * Note: The TelephonyAdapter Component uses its own configuration file.
 * One of the configuration parameters is the Sametime server to login to.
 * For additional information please refer to the file st.telephony.adapter.properties
 * 
 * The example specific implementation:
 * -----------------------------------
 * Initiate the TelephonyAdapter component
 * Add watch on a user and receive its Sametime status.
 * Publish the user's telephony status to the Sametime server.
 * 
 * 
 * @author Einat Avikser, April 2010
 *
 */

public class TelephonySample implements TelephonyAdapterListener {

	/**
	 * The program name - this is required for printing purposes.
	 */
	private String _className = TelephonySample.class.getName();
	
	/**
	 * The user on which we will add watch on and publish its telephony status
	 */
	private static final String USER_TELEPHONY_ID = "****@*****";
	
	
	/**
	 * The Telephony Status to publish for the user.
	 * 
	 * Possible values are defined at com.lotus.sametime.telephony.TelephonyStatus :
	 *  TELEPHONE_STATUS_UNKNOWN = 0 
	 *  TELEPHONE_STATUS_AVAILABLE = 1 
	 *  TELEPHONE_STATUS_BUSY = 2 
	 *  TELEPHONE_STATUS_DO_NOT_DISTURB = 3 
	 *  TELEPHONE_STATUS_NOT_SUT_USER = 4 
	 *  
	 *  It is the client's implementation Responsibility to determine the visualization
	 *  of the various Telephony status values.
	 * 
	 */
	private static final int USER_TELEPHONY_STATUS = TelephonyStatus.TELEPHONE_STATUS_BUSY;
	
	
	/**
	 * The TelephonyAdapter service
	 */
	private TelephonyAdapterService _service = null;
	
	/**
	 * Construct a new TelephonySample object 
	 */
	public TelephonySample() {
	}
	
	/**
	 * The example initiation:
	 * 
	 * 1. Instantiate the Telephony Adapter.
	 * 2. Register the example program as listener in order to get the 
	 *    Telephony Adapter notifications.
	 * 3. Initiate the Telephony Adapter component, this will create the connection with the configured
	 *    Sametime server
	 *    
	 */
	
	public void init() {
		System.out.println(_className+": init started");
		
		//Instantiate the Telephony Adapter component.
		_service = TelephonyAdapterComp.getInstance();
		
		//Register the example program as listener
		_service.setListener(this);
		
		//Initiate the Telephony Adapter component.
		try {
			_service.initiate();
		} catch (MissingConfigurationInfoException e) {
			e.printStackTrace();
		}
	}

	/**
	 * communityAvailable
	 * The Sametime server is now available for adding watch on users and publishing the 
	 * telephony status of users
	 */
	public void communityAvailable() {
		
		System.out.println("communityAvailable");
		
		// Add watch on the telephony user
		watchUser();
		
		// Publish the telephony status of the telephony user
		publishTelephonyStatus();
		
		
	}
	
	/**
	 * communityNotAvailable
	 * The Sametime server is no longer available for adding watch on users and publishing the 
	 * telephony status of users
	 */
	public void communityNotAvailable() {
		System.out.println("communityNotAvailable");
	}
	
	/**
	 * Error has occurred while trying to add watch or 
	 * publish a telephony status
	 * @param event   The error event passed to the application
	 */
	public void errorEvent(TelephonyAdapterErrorEvent event) {
		System.out.println("errorEvent: "+ event.getDescription());
		}
	
	/**
	 * The Sametime status of a user whom we asked to add watch on,
	 * has changed
	 * @param telephonyUserId   The user identification at the using application side
	 * @param sametimeStatus    The new sametime status
	 */
	public void sametimeStatusChanged(String telephonyUserId, int sametimeStatus) {

		System.out.println("sametimeStatusChanged for user: "+ telephonyUserId +
				" Sametime status: " + sametimeStatus);
	}
	
	/**
	 * Event notification about name resolving
	 * @param userEmail   The email of the Sametime user
	 * @param userId      The Sametime user ID
	 */
	public void nameResolved(String userEmail, STId userId) {
		System.out.println("nameResolved for user: "+ userEmail +
				" Sametime Id: " + userId);
		}
	
	
	
	/**
	 * Add watch on the telephony user.
	 * Any Sametime status changes of the watched users will be notified by the 
	 * sametimeStatusChanged API.
	 */
	private void watchUser() {
 			_service.addWatch(USER_TELEPHONY_ID);
 			System.out.println("Add watch on user: " + USER_TELEPHONY_ID);
	}
	
	/**
	 * Publish a telephony status of the user
	 */

	private void publishTelephonyStatus() {
		_service.publishTelephonyStatus(USER_TELEPHONY_ID, USER_TELEPHONY_STATUS);
		System.out.println("Publish telephony status: "+ USER_TELEPHONY_STATUS + " for user: " + USER_TELEPHONY_ID);
	}
	
	
	/**
	 * Event notification about attribute modification
	 * @param userId           The user Sametime ID
	 * @param attributeValue   The byte array of the user logins and related values
	 */
	public void selectedUserAttributeChanged(STId userId, byte[] attributeValue) {
	}
	
	/**
	 * Event notification about attribute removal
	 * @param userId    The user Sametime ID
	 */
	public void selectedUserAttributeRemoved(STId userId) {
	}
	
	

	/**
	 * Example initiation
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("TelephonySample: main");
		
		//Create the example program object and activate it.
		TelephonySample sample = new TelephonySample();
		sample.init();
	}
}
