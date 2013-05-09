package uw.cse.dineon.restaurant;

import org.json.JSONException;
import org.json.JSONObject;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.ParseUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.PushService;

/**
 * This class manages the communication between the Restaurant
 * and the Customer.
 * 
 * This Receiver manages all the actions that are required by contract
 * 
 * @author mhotan
 */
public class RestaurantSatelite extends BroadcastReceiver {

	private static final String TAG = RestaurantSatelite.class.getSimpleName();

	/**
	 * Restaurant this station is associated to.
	 */
	private Restaurant mParseRestaurant;

	/**
	 * Intent filter to control which actions to hold.
	 */
	private final IntentFilter mIF;

	/**
	 * The Channel associated with THIS Satelite.
	 */
	private String mThisChannel;

	/**
	 * The activity that the broadcast receiver is registers with.
	 */
	private DineOnRestaurantActivity mCurrentActivity;

	/**
	 * TODO ???
	 */
	private String mRestaurantSessionChannel;

	/**
	 * Creates and prepares a receiver for listening to
	 * actions.
	 */
	public RestaurantSatelite() {
		mIF = new IntentFilter();
		mIF.addAction(DineOnConstants.ACTION_REQUEST_DINING_SESSION);
		mIF.addAction(DineOnConstants.ACTION_ORDER_PLACED);
		mIF.addAction(DineOnConstants.ACTION_CHECK_OUT);
		mIF.addAction(DineOnConstants.ACTION_CHANGE_USER_INFO);
		mIF.addAction(DineOnConstants.ACTION_CUSTOMER_REQUEST);
		mRestaurantSessionChannel = null;
	}

	/**
	 * Registers this activity and registers a channel for the inputed restaurant.
	 * After this notification (if any) may arise
	 * @param restaurant Restaurant to associate channel to
	 * @param dineOnRestaurantActivity Activity that will be registered
	 */
	public void register(Restaurant restaurant, 
			DineOnRestaurantActivity dineOnRestaurantActivity){

		// Check for null values
		if (restaurant == null) {
			throw new IllegalArgumentException(
					"Null restaurant when registering broadcast receiver");
		}
		if (dineOnRestaurantActivity == null) {
			Log.w(TAG, "RestaurantSatelite attempted to register null activity");
			return;
		}

		// Establish the activity 
		mCurrentActivity = dineOnRestaurantActivity;

		// Establish a reference to the restaurant
		mParseRestaurant = restaurant;

		// Establish the channel to make 
		mThisChannel = ParseUtil.getChannel(mParseRestaurant.getInfo());

		// Registers this activity to this receiver
		mCurrentActivity.registerReceiver(this, mIF);

		// Subscribe to my channel so I can hear incoming messages
		PushService.subscribe(dineOnRestaurantActivity, 
				mThisChannel, 
				dineOnRestaurantActivity.getClass());
	}

	/**
	 * Turns off this receiver
	 */
	public void unRegister() {
		if (mCurrentActivity == null) {
			return;
		}

		mCurrentActivity.unregisterReceiver(this);
		PushService.unsubscribe(mCurrentActivity, mThisChannel);
		mCurrentActivity = null;
	}

	/**
	 * Sends a push notification to the customer.
	 * This notifies the customer that a Dining Session has been
	 * started and they are free to download it at the given pointer
	 * @param ds DiningSession instance to return
	 * @param info User to return to
	 * @throws JSONException 
	 */
	public void confirmDiningSession(DiningSession ds, UserInfo info) throws JSONException{
		JSONObject jo = new JSONObject();
		jo.put(DineOnConstants.OBJ_ID, ds.getObjId());
		//TODO
	}

	/**
	 * 
	 * @param restaurant
	 * @param user
	 */
	public void notifyChangeRestaurantInfo(RestaurantInfo restaurant, UserInfo user) {
		// TODO
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// Extract the channel they were sending to
		String theirChannel = intent.getExtras() == null ? null :  
			intent.getExtras().getString(DineOnConstants.PARSE_CHANNEL);

		// IF they don't have a channel are our activity died
		// Then exit this method
		if (theirChannel == null || mCurrentActivity == null) {
			Log.w(TAG, "[onReceive] Their channel: " + theirChannel 
					+ " Our activity: " + mCurrentActivity);
			return;
		}

		// As an assurance we assert their channel is equal to ours
		if (theirChannel.equals(mThisChannel)) {
			// Extract the 
			String id = null;
			JSONObject jobj;
			try {
				jobj = new JSONObject(
						intent.getExtras().getString(DineOnConstants.PARSE_DATA));
				id = jobj.getString(DineOnConstants.OBJ_ID);
			} 
			catch (JSONException e) {
				Log.d(TAG, "Customer sent fail case: " + e.getMessage());
				// TODO handle failure to extract id
				// What does it mean when we fail like this?
				return;
			} 
			
			int tableNum = -1; //Default Value
			try {
				tableNum = jobj.getInt(DineOnConstants.TABLE_NUM);
			} catch (JSONException e) {
				// Leave it at -1
			}
			
			
			String action = intent.getAction();
			ParseQuery uInfo = new ParseQuery(UserInfo.class.getSimpleName());
			ParseQuery dsQuery = new ParseQuery(DiningSession.class.getSimpleName());
			uInfo.setCachePolicy(CachePolicy.NETWORK_ONLY);
			dsQuery.setCachePolicy(CachePolicy.NETWORK_ONLY);

			if (DineOnConstants.ACTION_REQUEST_DINING_SESSION.equals(action)) {
				// TODO Download UserInfo
				// Get the table ID
				final int tNum = tableNum;
				uInfo.getInBackground(id, new GetCallback() {
					public void done(ParseObject object, ParseException e) {
						if (e == null) {
							// Return updated user info
							mCurrentActivity.onUserCheckedIn(new UserInfo(object), tNum);
						} else {
							mCurrentActivity.onFail(e.getMessage());
						}
					}
				});
			} else if (DineOnConstants.ACTION_ORDER_PLACED.equals(action)) {
				// TODO Download Dining Session
				dsQuery.getInBackground(id, new GetCallback() {
					public void done(ParseObject object, ParseException e) {
						if (e == null) {
							// object will be your game score
							mCurrentActivity.onOrderPlaced(new DiningSession(object));
						} else {
							mCurrentActivity.onFail(e.getMessage());
						}
					}
				});
			} else if (DineOnConstants.ACTION_CUSTOMER_REQUEST.equals(action)) {
				// TODO Download Dining Session
				dsQuery.getInBackground(id, new GetCallback() {
					public void done(ParseObject object, ParseException e) {
						if (e == null) {
							// object will be your game score
							mCurrentActivity.onCustomerRequest(new DiningSession(object));
						} else {
							mCurrentActivity.onFail(e.getMessage());
						}
					}
				});
			} else if (DineOnConstants.ACTION_CHECK_OUT.equals(action)) {
				// TODO Download Dining Session
				dsQuery.getInBackground(id, new GetCallback() {
					public void done(ParseObject object, ParseException e) {
						if (e == null) {
							// object will be your game score
							mCurrentActivity.onCheckedOut(new DiningSession(object));
						} else {
							mCurrentActivity.onFail(e.getMessage());
						}
					}
				});
			} else if (DineOnConstants.ACTION_CHANGE_USER_INFO.equals(action)) {
				// TODO Download UserInfo
				uInfo.getInBackground(id, new GetCallback() {
					public void done(ParseObject object, ParseException e) {
						if (e == null) {
							// object will be your game score
							mCurrentActivity.onUserChanged(new UserInfo(object));
						} else {
							mCurrentActivity.onFail(e.getMessage());
						}
					}
				});
			} else {
				Log.w(TAG, "Unknown action received: " + action);
			}

		} 
		else if (mRestaurantSessionChannel != null && 
				mRestaurantSessionChannel.equals(theirChannel)) {
			// TODO Do something here that updates the state of the current Dining Session 

		}
	}



	/**
	 * Listener for Activities to implement to receive action.
	 * @author mhotan
	 */
	public interface SateliteListener {

		/**
		 * Notifies that a error occured.
		 * Most likely it was a network error
		 */
		void onFail(String message);

		/**
		 * User attempted to check in to restaurant 
		 * identified and register time.
		 * @param user User that attempted to CheckIn
		 */
		void onUserCheckedIn(UserInfo user, int tableID);
		
		/**
		 * Notifies that the current usr changed its state
		 * from what the restaurant previously knew about.
		 * @param user User that changed it state
		 */
		void onUserChanged(UserInfo user);
		
		/**
		 * Notifies that an order was placed from the User(s)
		 * associated with the attached dining session.
		 * @param session Dining session where order was placed
		 */
		void onOrderPlaced(DiningSession session);
		
		/**
		 * Notifies that a customer request was placed from the User(s)
		 * associated with the attached dining session.
		 * @param session Dining session where customer request was placed
		 */
		void onCustomerRequest(DiningSession session);
		
		/**
		 * Notifies Application the Users have checked out
		 * for associated dining session.
		 * @param session checked out dining session
		 */
		void onCheckedOut(DiningSession session);
	}

}
