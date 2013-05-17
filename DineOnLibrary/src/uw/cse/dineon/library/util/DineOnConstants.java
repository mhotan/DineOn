package uw.cse.dineon.library.util;

import java.text.DateFormat;
import java.util.Locale;

/**
 * Generalized wrapper class to hold constants that pertain to multiple modules 
 * with the applications.
 * @author mhotan
 */
public final class DineOnConstants {

	/**
	 * Hidden constructor.
	 */
	private DineOnConstants() { }

	/**
	 * Global constant that determines certain procedure flows.
	 * This is to allow developers to do different things 
	 * under different conditions
	 */
	public static final boolean DEBUG = true;

	// Parse Related Cosntants to access Parse API
	public static final String APPLICATION_ID = "10dCEpc2D8Rp7pC2uFaLCN7CtRcNSeZkz8d7eAaX";
	public static final String CLIENT_KEY = "4NAn0437HLZpDMa5v0gS6JeYT28Q0vmxW19qWdSw";


	public static final String FACEBOOK_APP_ID = "505185672873933";

	public static final String DINING_SESSION = "DiningSession";

	// Request Code for multiple Activity use
	public static final int REQUEST_CHECK_IN = 0x1;
	public static final int REQUEST_VIEW_CURRENT_ORDER = 0x2;
	public static final int REQUEST_PAY_BILL = 0x2;
	// some change

	/**
	 * This can be used as a generalized key for Bundles.
	 * If a RESTAURANT_OBJECT needs to be passed between activities
	 * or activity to fragment. Then an restaurant instance must be 
	 * placed into a Bundle
	 * 
	 * On the sender side it can be user like this: 
	 *    Bundle toSend = new Bundle();
	 *    toSend.putParcelable(KEY_RESTAURANT, <Restaurant Instance>);
	 *    then attach the bundle appropiately so that it gets sent to the next module
	 * 
	 * Then on the receiving side
	 * 	Fragment: if getArguments() != null, then getArguments().getParcelable(KEY_RESTAURANT);  
	 *	Activity: For Bundle b, b.getParcelable(KEY_RESTAURANT);
	 */
	public static final String KEY_RESTAURANT = "RESTAURANT";

	public static final String KEY_ACTION = "action";

	/**
	 * Reference KEY_RESTAURANT, but for Restaurant Info.
	 */
	public static final String KEY_RESTAURANTINFO = "RESTAURANTINFO";

	/**
	 * Reference KEY_RESTAURANT, but for Restaurant Info.
	 */
	public static final String KEY_USER = "USER";

	// Callback Key for braodcast receiver callbacks
	public static final String OBJ_ID = "objectId";
	public static final String OBJ_ID_2 = "objectIdTwo";
	public static final String TABLE_NUM = "TABLE_NUM";

	// Actions for broadcast resceivers
	private static final String ACTION_PREFIX = "uw.cse.dineon.user.";

	public static final String ACTION_REQUEST_DINING_SESSION = 
			ACTION_PREFIX + "REQUEST_DINING_SESSION";
	public static final String ACTION_REQUEST_ORDER = 
			ACTION_PREFIX + "REQUEST_ORDER";
	public static final String ACTION_REQUEST_CUSTOMER_REQUEST = 
			ACTION_PREFIX + "REQUEST_CUSTOMER_REQUEST";
	public static final String ACTION_REQUEST_RESERVATION = 
			ACTION_PREFIX + "REQUEST_RESERVATION";
	public static final String ACTION_REQUEST_CHECK_OUT = 
			ACTION_PREFIX + "REQUEST_CHECK_OUT";
	public static final String ACTION_CHANGE_USER_INFO = 
			ACTION_PREFIX + "CHANGE_USER_INFO";

	// For Restaurant
	public static final String[] RESTAURANT_ACTIONS = 
		{ACTION_REQUEST_DINING_SESSION,
		ACTION_REQUEST_ORDER, 
		ACTION_REQUEST_CUSTOMER_REQUEST,
		ACTION_REQUEST_RESERVATION,
		ACTION_REQUEST_CHECK_OUT,
		ACTION_CHANGE_USER_INFO
		};

	// For Customer
	public static final String ACTION_CONFIRM_DINING_SESSION = 
			ACTION_PREFIX + "CONFIRM_DINING_SESSION";
	public static final String ACTION_CONFIRM_ORDER = 
			ACTION_PREFIX + "CONFIRM_ORDER";
	public static final String ACTION_CONFIRM_CUSTOMER_REQUEST = 
			ACTION_PREFIX + "CONFIRM_CUSTOMER_REQUEST";
	public static final String ACTION_CONFIRM_RESERVATION = 
			ACTION_PREFIX + "CONFIRM_RESERVATION";
	public static final String ACTION_CHANGE_RESTAURANT_INFO = 
			ACTION_PREFIX + "CHANGE_RESTAURANT_INFO";

	public static final String[] CUSTOMER_ACTIONS = {
		ACTION_CONFIRM_DINING_SESSION,
		ACTION_CONFIRM_ORDER,
		ACTION_CONFIRM_ORDER,
		ACTION_CONFIRM_RESERVATION,
		ACTION_CHANGE_RESTAURANT_INFO,
	};

	/**
	 * Constant key for Parse extracting channel.
	 */
	public static final String PARSE_CHANNEL = "com.parse.Channel";

	/**
	 * Constant key for data of push notification.
	 */
	public static final String PARSE_DATA = "com.parse.Data";

	/**
	 * This is a channel prefix for Push Channels.
	 * IE channel for receiving a broadcast from a particular restaurant would be 
	 * 
	 */
	static final String CHANNEL_PREFIX = "uw_cse_dineon_";

	/**
	 * This date formatter is used for storing and sending dates
	 * in parse objects.  This same date formatter is used to 
	 * write dates to string and turn those strings
	 * back into Dates
	 */
	private static final DateFormat MDATEFORMAT = 
			DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.getDefault()); 

	/**
	 * Returns the Date format for use with both applications.
	 * @return Date format to use.
	 */
	public static DateFormat getCurrentDateFormat(){ 
		return MDATEFORMAT;
	}
	
}
