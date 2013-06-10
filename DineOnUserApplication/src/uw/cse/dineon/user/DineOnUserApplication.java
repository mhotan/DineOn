package uw.cse.dineon.user;

import java.util.List;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.ParseUtil;
import uw.cse.dineon.user.restaurantselection.RestaurantSelectionActivity;
import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;


/**
 * Application for DineOn user side.
 */
public class DineOnUserApplication extends Application {

	protected static DineOnUser currentUser = null;
//	protected static HashMap<MenuItem, CurrentOrderItem> currentOrderMapping = 
//			new HashMap<MenuItem, CurrentOrderItem>();
	protected static RestaurantInfo restaurantOfInterest;
	protected static List<RestaurantInfo> restaurantInfos;

	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
		Parse.initialize(this, DineOnConstants.APPLICATION_ID, DineOnConstants.CLIENT_KEY);
		// TODO Initialize Twitter
		// https://www.parse.com/docs/android_guide#twitterusers-setup

		// TODO Initialize Facebook
		// https://www.parse.com/docs/android_guide#fbusers-setup		
		ParseFacebookUtils.initialize(DineOnConstants.FACEBOOK_APP_ID);

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();

		// If you would like all objects to be private by default, remove this line.
		defaultACL.setPublicReadAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);

		ParseInstallation.getCurrentInstallation().saveInBackground();
	}

	/**
	 * Sets cachedUser.
	 * @param user DineOnUser
	 * @param context Context to subsribe and unsubscribe to notification.
	 */
	public static void setDineOnUser(DineOnUser user, Context context) {	
		if (currentUser != null && !currentUser.equals(user) && context != null) {
			// Unsubscribe the current user
			PushService.unsubscribe(context, ParseUtil.getChannel(currentUser.getUserInfo()));
		}
		
		// Resassign user
		currentUser = user;

		if (currentUser != null && context != null) {
			// Subscribe to my channel so I can hear incoming messages
			PushService.subscribe(context, 
					ParseUtil.getChannel(currentUser.getUserInfo()), 
					RestaurantSelectionActivity.class);
		}
	}

	/**
	 * Returns cachedUser.
	 * @return cachedUser
	 */
	public static DineOnUser getDineOnUser() {
		return currentUser;
	}

	/**
	 * Return the userinfo for current user.
	 * @return user
	 */
	public static UserInfo getUserInfo() {
		return currentUser.getUserInfo();
	}

	/**
	 * Returns the current DiningSession. Null if no current dining session.
	 * @param session dining session for current user
	 */
	public static void setCurrentDiningSession(DiningSession session) {
		currentUser.setDiningSession(session);
		currentUser.saveInBackGround(null);
	}

	/**
	 * Returns the current DiningSession. Null if no current dining session
	 * @return the current DiningSession
	 */
	public static DiningSession getCurrentDiningSession() {
		return currentUser.getDiningSession();
	}

	//	/**
	//	 * Returns the current order.
	//	 * @return current order
	//	 */
	//	public static HashMap<MenuItem, CurrentOrderItem> getCurrentOrder() {
	//		return currentOrderMapping;
	//	}

	//	/**
	//	 * Set the current order.
	//	 * @param items list of current order items to save.
	//	 */
	//	public static void setCurrentOrder(List<CurrentOrderItem> items) {
	//		Iterator<CurrentOrderItem> it = items.iterator();
	//		while (it.hasNext()) {
	//			CurrentOrderItem item = it.next();
	//			currentOrderMapping.put(item.getMenuItem(), item);
	//		}
	//		Log.d("asfd", "h");
	//	}

	//	/**
	//	 * Reset the current order.
	//	 */
	//	public static void clearCurrentOrder() {
	//		currentOrderMapping.clear();
	//	}
	//	
	//	/**
	//	 * Increment the menuitem quantity in the current order.
	//	 * @param item to increment
	//	 */
	//	public static void incrementItemInCurrentOrder(MenuItem item) {
	//		if (!currentOrderMapping.containsKey(item)) {
	//			currentOrderMapping.put(item, new CurrentOrderItem(item));
	//		} else {
	//			currentOrderMapping.get(item).incrementQuantity();
	//		}
	//	}
	//	 
	//	/**
	//	 * Deccrement the menuitem quantity in the current order.
	//	 * @param item to decrement
	//	 */
	//	public static void decrementItemInCurrentOrder(MenuItem item) {
	//		if (currentOrderMapping.containsKey(item)) {
	//			currentOrderMapping.get(item).decrementQuantity();
	//		}
	//	}
	//	
	//	/**
	//	 * Remove item from the current order.
	//	 * @param item to remove
	//	 */
	//	public static void removeItemInCurrentOrder(MenuItem item) {
	//		if (currentOrderMapping.containsKey(item)) {
	//			currentOrderMapping.remove(item);
	//		}
	//	}

	/**
	 * This method sets the User restaurant of interest.  Some activities will check whether 
	 * the restaurant of interest is active.  To have no restaurant of interest set the info to 
	 * null.
	 * 
	 * @param info Restaurant to focus on
	 */
	public static void setRestaurantOfInterest(RestaurantInfo info) {
		restaurantOfInterest = info;
	}

	/**
	 * @return Returns restaurant of interest or null if there is not one.
	 */
	public static RestaurantInfo getRestaurantOfInterest() {
		return restaurantOfInterest;
	}

	/**
	 * Sets the current list of restaurant to focus on as this list.
	 * @param restaurants restaurants to set as currently selected list
	 */
	public static void saveRestaurantList(List<RestaurantInfo> restaurants) {
		restaurantInfos = restaurants;
	}

	/**
	 * Get saved restaurant list.
	 * @return The restaurant list previously saved
	 */
	public static List<RestaurantInfo> getRestaurantList() {
		return restaurantInfos;
	}

	/**
	 * Clear current restaurant lists.
	 */
	public static void clearResaurantList() {
		restaurantInfos = null;
	}
}
