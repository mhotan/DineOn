package uw.cse.dineon.library.util;

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
	public static final String TABLE_NUM = "TABLE_NUM";
	
	// Actions for broadcast resceivers
	private static final String ACTION_PREFIX = "uw.cse.dineon.user.";
	
	// For Restaurant
	public static final String ACTION_REQUEST_DINING_SESSION = 
			ACTION_PREFIX + "REQUEST_DINING_SESSION";
	public static final String ACTION_ORDER_PLACED = ACTION_PREFIX + "ORDER_PLACED";
	public static final String ACTION_CHECK_OUT = ACTION_PREFIX + "CHECK_OUT";
	public static final String ACTION_CHANGE_USER_INFO = ACTION_PREFIX + "CHANGE_USER_INFO";
	public static final String ACTION_CUSTOMER_REQUEST = ACTION_PREFIX + "CUSTOMER_REQUEST";

	// For Customer
	public static final String ACTION_CONFIRM_DINING_SESSION = 
			ACTION_PREFIX + "CONFIRM_DINING_SESSION";
	public static final String ACTION_CHANGE_RESTAURANT_INFO = 
			ACTION_PREFIX + "CHANGE_RESTAURANT_INFO";

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
}
