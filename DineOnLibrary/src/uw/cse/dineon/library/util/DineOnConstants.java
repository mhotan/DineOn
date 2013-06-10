package uw.cse.dineon.library.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;



/**
 * Generalized wrapper class to hold constants that pertain to multiple modules 
 * with the applications.
 * @author mhotan
 */
@SuppressWarnings("MS_SHOULD_BE_FINAL")
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
	public static boolean DEBUG = true;
	
	/**
	 * Constant that controls whether applications are in testing mode.
	 */
	public static boolean TESTING = false;

	// Parse Related Constants to access Parse API
	public static final String APPLICATION_ID = "10dCEpc2D8Rp7pC2uFaLCN7CtRcNSeZkz8d7eAaX";
	public static final String CLIENT_KEY = "4NAn0437HLZpDMa5v0gS6JeYT28Q0vmxW19qWdSw";


	public static final String FACEBOOK_APP_ID = "505185672873933";

	public static final String DINING_SESSION = "DiningSession";

	// Request Code for multiple Activity use
	public static final int REQUEST_CHECK_IN = 0x1;
	public static final int REQUEST_VIEW_CURRENT_ORDER = 0x1 << 1;
	public static final int REQUEST_PAY_BILL = 0x1 << 2;
	public static final int REQUEST_TAKE_PHOTO = 0x1 << 3;
	public static final int REQUEST_CHOOSE_PHOTO = 0x1 << 4;
	// some change

	public static final double CURRENCY_EPSILON = .01; // One penny
	
	/**
	 * Images stored on the parse cloud must maintain a maximum size.
	 * Images must be able to be viewed in both orientations.  This static constant
	 * represents the longest dimension a single image can have.  This dimension
	 * is not discriminate on whether its the image width or height.
	 * 
	 * In other words It maintains this invariant on our images:
	 * For unskewed image with height h and width w
	 * w <= LONGEST_IMAGE_DIMENSION && h <= LONGEST_IMAGE_DIMENSION
	 */
	public static final int LONGEST_IMAGE_DIMENSION = 400;
	
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

	/**
	 * Associated action tag for a Parse push notification.
	 * The corresponding value pertains to the requires action to take
	 * upon receipt of the push notification.
	 */
	public static final String KEY_ACTION = "action";

	/**
	 * Reference KEY_RESTAURANT, but for Restaurant Info.
	 */
	public static final String KEY_RESTAURANTINFO = "RESTAURANTINFO";

	/**
	 * Reference KEY_RESTAURANT, but for Restaurant Info.
	 */
	public static final String KEY_USER = "USER";
	
	public static final String KEY_DININGSESSION = "DININGSESSION";
	
	public static final Long MAX_RESPONSE_TIME = (long) 30000;
	
	/**
	 * Tax for determining price of orders.
	 */
	public static final double TAX = 0.08;
	
	/**
	 * Max number of restaurants to show on the restaurant selection screen.
	 */
	public static final int MAX_RESTAURANTS = 20;
	
	/**
	 * Near restaurants definition in miles.
	 */
	public static final double MAX_RESTAURANT_DISTANCE = 2.0;
	
	public static final long MIN_LOCATION_UPDATE_INTERVAL_MILLIS = 5 * 60 * 1000;
	public static final float MIN_LOCATION_UPDATE_DISTANCE_METERS = (float) (1.0 * 1609.34);

	// Callback Key for braodcast receiver callbacks
	public static final String OBJ_ID = "objectId";
	public static final String OBJ_ID_2 = "objectIdTwo";
	public static final String TABLE_NUM = "TABLE_NUM";

	// Actions for broadcast resceivers
	private static final String ACTION_PREFIX_USER = "uw.cse.dineon.user.";
	private static final String ACTION_PREFIX_RESTAURANT = "uw.cse.dineon.restaurant.";

	public static final String ACTION_REQUEST_DINING_SESSION = 
			ACTION_PREFIX_USER + "REQUEST_DINING_SESSION";
	public static final String ACTION_REQUEST_ORDER = 
			ACTION_PREFIX_USER + "REQUEST_ORDER";
	public static final String ACTION_REQUEST_CUSTOMER_REQUEST = 
			ACTION_PREFIX_USER + "REQUEST_CUSTOMER_REQUEST";
	public static final String ACTION_REQUEST_RESERVATION = 
			ACTION_PREFIX_USER + "REQUEST_RESERVATION";
	public static final String ACTION_REQUEST_CHECK_OUT = 
			ACTION_PREFIX_USER + "REQUEST_CHECK_OUT";
	public static final String ACTION_CHANGE_USER_INFO = 
			ACTION_PREFIX_USER + "CHANGE_USER_INFO";

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
			ACTION_PREFIX_USER + "CONFIRM_DINING_SESSION";
	public static final String ACTION_CONFIRM_ORDER = 
			ACTION_PREFIX_USER + "CONFIRM_ORDER";
	public static final String ACTION_CONFIRM_CUSTOMER_REQUEST = 
			ACTION_PREFIX_USER + "CONFIRM_CUSTOMER_REQUEST";
	public static final String ACTION_CONFIRM_RESERVATION = 
			ACTION_PREFIX_USER + "CONFIRM_RESERVATION";
	public static final String ACTION_CHANGE_RESTAURANT_INFO = 
			ACTION_PREFIX_USER + "CHANGE_RESTAURANT_INFO";
	public static final String ACTION_FAIL = 
			ACTION_PREFIX_USER + "FAIL";
	
	private static final String LOCAL = "_LOCAL";
	public static final String ACTION_CONFIRM_DINING_SESSION_LOCAL = 
			ACTION_CONFIRM_DINING_SESSION + LOCAL;
	public static final String ACTION_CONFIRM_ORDER_LOCAL = 
			ACTION_CONFIRM_ORDER + LOCAL;
	public static final String ACTION_CONFIRM_CUSTOMER_REQUEST_LOCAL = 
			ACTION_CONFIRM_CUSTOMER_REQUEST + LOCAL;
	public static final String ACTION_CONFIRM_RESERVATION_LOCAL = 
			ACTION_CONFIRM_RESERVATION + LOCAL;
	public static final String ACTION_CHANGE_RESTAURANT_INFO_LOCAL = 
			ACTION_CHANGE_RESTAURANT_INFO + LOCAL;
	public static final String ACTION_FAIL_LOCAL = ACTION_FAIL + LOCAL;
	
	public static final String[] CUSTOMER_ACTIONS = {
		ACTION_CONFIRM_DINING_SESSION_LOCAL,
		ACTION_CONFIRM_ORDER_LOCAL,
		ACTION_CONFIRM_ORDER_LOCAL,
		ACTION_CONFIRM_RESERVATION_LOCAL,
		ACTION_CHANGE_RESTAURANT_INFO_LOCAL,
		ACTION_FAIL_LOCAL
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

	public static final String ARGUMENT = "__argument";
	
	/**
	 * Returns the Date format for use with both applications.
	 * @return Date format to use.
	 */
	public static DateFormat getCurrentDateFormat() { 
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
	}
	
}
