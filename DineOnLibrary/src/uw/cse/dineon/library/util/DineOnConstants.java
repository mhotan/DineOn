package uw.cse.dineon.library.util;

/**
 * Generalized wrapper class to hold constants that pertain to multiple modules with the applications
 * @author mhotan
 */
public class DineOnConstants {
	
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
	
	// Request Code for multiple Activity use
	public final static int REQUEST_CHECK_IN = 0x1;
	public final static int REQUEST_VIEW_CURRENT_ORDER = 0x2;
	public final static int REQUEST_PAY_BILL = 0x2;
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
}
