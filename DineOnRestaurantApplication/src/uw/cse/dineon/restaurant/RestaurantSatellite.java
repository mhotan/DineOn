package uw.cse.dineon.restaurant;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Reservation;
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
 * and all the restaurants current customers.
 * 
 * This Receiver manages all the actions that are required by contract
 * 
 * @author mhotan
 */
public class RestaurantSatellite extends BroadcastReceiver {

	private static final String TAG = RestaurantSatellite.class.getSimpleName();

	/**
	 * Restaurant this station is associated to.
	 */
	private Restaurant mParseRestaurant;

	/**
	 * Intent filter to control which actions to listen for.
	 */
	private final IntentFilter mIF;

	/**
	 * The Channel associated with THIS Satellite.
	 */
	private String mChannel;

	/**
	 * The activity that the broadcast receiver is registers with.
	 */
	private DineOnRestaurantActivity mCurrentActivity;

	/**
	 * Enum for handling specific actions.
	 * @author mhotan
	 */
	private enum ACTION_OPTION {
		REQUEST_DINING_SESSION,
		REQUEST_ORDER,
		REQUEST_CUSTOMER_REQUEST,
		REQUEST_CHECK_OUT,
		REQUEST_RESERVATION,
		CHANGE_USER_INFO,
		NA
	}

	/**
	 * Creates and prepares a receiver for listening to
	 * actions.
	 */
	public RestaurantSatellite() {
		mIF = new IntentFilter();
		for (String action: DineOnConstants.RESTAURANT_ACTIONS) {
			mIF.addAction(action);
		}
	}

	/**
	 * Registers this activity and registers a channel for the inputed restaurant.
	 * After this notification (if any) may arise
	 * @param restaurant Restaurant to associate channel to
	 * @param activity Activity that will be registered
	 */
	public void register(Restaurant restaurant, 
			DineOnRestaurantActivity activity) {

		// Check for null values
		if (restaurant == null) {
			throw new IllegalArgumentException(
					"Null restaurant when registering broadcast receiver");
		}
		if (activity == null) {
			Log.w(TAG, "RestaurantSatelite attempted to register null activity");
			return;
		}

		// Establish the activity 
		mCurrentActivity = activity;

		// Establish a reference to the restaurant
		mParseRestaurant = restaurant;

		// Establish the channel to make 
		mChannel = ParseUtil.getChannel(mParseRestaurant.getInfo());

		// Registers this activity to this receiver
		mCurrentActivity.registerReceiver(this, mIF);

		// Subscribe to my channel so I can hear incoming messages
		PushService.subscribe(activity, 
				mChannel, 
				activity.getClass());
	}

	/**
	 * Turns off this receiver.
	 */
	public void unRegister() {
		if (mCurrentActivity == null) {
			return;
		}

		mCurrentActivity.unregisterReceiver(this);
//		PushService.unsubscribe(mCurrentActivity, mChannel);
		mCurrentActivity = null;
	}

	/**
	 * Sends a push notification to the customer.
	 * This notifies the customer that a Dining Session has been
	 * started and they are free to download it at the given pointer
	 * Note: This does not save the dining sessin it just simply assumes
	 * that its state is appropiate for a push.
	 * @param ds DiningSession instance to return
	 */
	public void confirmDiningSession(DiningSession ds) {
		if(ds.getUsers() == null || ds.getUsers().isEmpty()) {
			throw new IllegalArgumentException("User list is null/empty.");
		}
		if(ds.getObjId() == null) {
			throw new IllegalArgumentException("Forgot to save dining session.");
		}
		JSONObject jobj = new JSONObject();

		try {
			jobj.put(DineOnConstants.OBJ_ID, ds.getObjId());
			jobj.put(DineOnConstants.KEY_ACTION, DineOnConstants.ACTION_CONFIRM_DINING_SESSION);
		} catch (JSONException e) {
			Log.e(TAG, "Malformated JSON: " + jobj + " exception: " + e.getMessage());
			return;
		}

		List<UserInfo> users = ds.getUsers();
		for (UserInfo u : users) {
			ParseUtil.notifyApplication(
					jobj,
					ParseUtil.getChannel(u));
		}
	}

	/**
	 * Notify all the users of the dining session that the order was 
	 * placed correctly.
	 * @param ds Dining Session that was updated.
	 * @param order Order for reference.
	 */
	public void confirmOrder(DiningSession ds, Order order) {
		JSONObject jobj = new JSONObject();

		try {
			jobj.put(DineOnConstants.OBJ_ID, ds.getObjId());
			jobj.put(DineOnConstants.OBJ_ID_2, order.getObjId());
			jobj.put(DineOnConstants.KEY_ACTION, DineOnConstants.ACTION_CONFIRM_ORDER);
		} catch (JSONException e) {
			Log.e(TAG, "Malformated JSON: " + jobj + " exception: " + e.getMessage());
			return;
		}

		List<UserInfo> users = ds.getUsers();
		for (UserInfo u : users) {
			ParseUtil.notifyApplication(
					jobj,
					ParseUtil.getChannel(u));
		}
	}

	/**
	 * Notify all the users of the dining session that the customer request
	 * was seen.
	 * @param ds Dining session in which customer request occured.
	 * @param request request to notify was accepted
	 */
	public void confirmCustomerRequest(DiningSession ds, CustomerRequest request) {
		JSONObject jobj = new JSONObject();

		try {
			jobj.put(DineOnConstants.OBJ_ID, ds.getObjId());
			jobj.put(DineOnConstants.OBJ_ID_2, request.getObjId());
			jobj.put(DineOnConstants.KEY_ACTION, DineOnConstants.ACTION_CONFIRM_CUSTOMER_REQUEST);
		} catch (JSONException e) {
			Log.e(TAG, "Malformated JSON: " + jobj + " exception: " + e.getMessage());
			return;
		}

		List<UserInfo> users = ds.getUsers();
		for (UserInfo u : users) {
			ParseUtil.notifyApplication(
					jobj,
					ParseUtil.getChannel(u));
		}
	}

	/**
	 * Notifies user of reservation confirmation.
	 * @param user user to notify
	 * @param res Reservation to confirm
	 */
	public void confirmReservation(UserInfo user, Reservation res) {
		JSONObject jobj = new JSONObject();

		try {
			jobj.put(DineOnConstants.OBJ_ID, res.getObjId());
			jobj.put(DineOnConstants.KEY_ACTION, DineOnConstants.ACTION_CONFIRM_ORDER);
		} catch (JSONException e) {
			Log.e(TAG, "Malformated JSON: " + jobj + " exception: " + e.getMessage());
			return;
		}

		ParseUtil.notifyApplication(
				jobj,
				ParseUtil.getChannel(user));
	}

	/**
	 * Notifies User associated with UserInfo that our
	 * Restaurant profile information has changed.  
	 * Note: this is intended to be called after the save of restaurant.
	 * @param restaurant restaurant object to notify update
	 * @param user User to notify
	 */
	public void notifyChangeRestaurantInfo(RestaurantInfo restaurant, UserInfo user) {
		JSONObject jobj = new JSONObject();

		try {
			jobj.put(DineOnConstants.OBJ_ID, restaurant.getObjId());
			jobj.put(DineOnConstants.KEY_ACTION, DineOnConstants.ACTION_CHANGE_RESTAURANT_INFO);
		} catch (JSONException e) {
			Log.e(TAG, "Malformated JSON: " + jobj + " exception: " + e.getMessage());
			return;
		}

		ParseUtil.notifyApplication(
				jobj,
				ParseUtil.getChannel(user));
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// Extract the channel they were sending to
		String theirChannel = intent.getExtras() == null ? null 
				: intent.getExtras().getString(DineOnConstants.PARSE_CHANNEL);

		// IF they don't have a channel are our activity died
		// Then exit this method
		if (theirChannel == null || mCurrentActivity == null) {
			Log.w(TAG, "[onReceive] Their channel: " + theirChannel 
					+ " Our activity: " + mCurrentActivity);
			return;
		}

		// As an assurance we assert their channel is equal to ours
		if (!theirChannel.equals(mChannel)) { 
			return;
		}

		// Get associated action
		String action = intent.getAction();

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
			mCurrentActivity.onFail(e.getMessage());
			// User has sent malformed data
			// What does it mean when we fail like this?
			return;
		} 

		// Get the second Argument
		String arg2 = null;
		try {
			arg2 = jobj.getString(DineOnConstants.OBJ_ID_2);
		} catch (JSONException e) {
			// Leave it at -1
			Log.e(TAG, "JSON Exception occured on push request.");
		}

		ParseQuery uInfo = new ParseQuery(UserInfo.class.getSimpleName());
		ParseQuery dsQuery = new ParseQuery(DiningSession.class.getSimpleName());
		uInfo.setCachePolicy(CachePolicy.NETWORK_ONLY);
		dsQuery.setCachePolicy(CachePolicy.NETWORK_ONLY);

		ParseQuery query;
		// Create the callback
		SatelliteGetCallback callback = new SatelliteGetCallback(
				mCurrentActivity, arg2);

		if (DineOnConstants.ACTION_REQUEST_DINING_SESSION.equals(action)) {
			// Get the User Info data
			query = new ParseQuery(UserInfo.class.getSimpleName());
			query.setCachePolicy(CachePolicy.NETWORK_ONLY);
			callback.setOption(ACTION_OPTION.REQUEST_DINING_SESSION);
			query.getInBackground(id, callback);
		} 
		else if (DineOnConstants.ACTION_REQUEST_ORDER.equals(action)) {
			// Get the Order Object
			query = new ParseQuery(Order.class.getSimpleName());
			query.setCachePolicy(CachePolicy.NETWORK_ONLY);
			callback.setOption(ACTION_OPTION.REQUEST_ORDER);
			query.getInBackground(id, callback);		
		} 
		else if (DineOnConstants.ACTION_REQUEST_CUSTOMER_REQUEST.equals(action)) {
			// Get the Customer Request Object
			query = new ParseQuery(CustomerRequest.class.getSimpleName());
			query.setCachePolicy(CachePolicy.NETWORK_ONLY);
			callback.setOption(ACTION_OPTION.REQUEST_CUSTOMER_REQUEST);
			query.getInBackground(id, callback);

		} 
		else if (DineOnConstants.ACTION_REQUEST_RESERVATION.equals(action)) {
			// Get the date in string format
			query = new ParseQuery(Reservation.class.getSimpleName());
			query.setCachePolicy(CachePolicy.NETWORK_ONLY);
			callback.setOption(ACTION_OPTION.REQUEST_RESERVATION);
			query.getInBackground(id, callback);
		} 
		else if (DineOnConstants.ACTION_REQUEST_CHECK_OUT.equals(action)) {
			// Get the current Dining Session instance
			query = new ParseQuery(DiningSession.class.getSimpleName());
			query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			callback.setOption(ACTION_OPTION.REQUEST_CHECK_OUT);
			query.getInBackground(id, callback);
		} 
		else if (DineOnConstants.ACTION_CHANGE_USER_INFO.equals(action)) {
			// Get the current user info instance
			query = new ParseQuery(UserInfo.class.getSimpleName());
			query.setCachePolicy(CachePolicy.NETWORK_ONLY);
			callback.setOption(ACTION_OPTION.CHANGE_USER_INFO);
			query.getInBackground(id, callback);
		}
	}

	/**
	 * Private GetCallback that encapsulates the processing of 
	 * different types of Gets.
	 * @author mhotan
	 */
	private static class SatelliteGetCallback extends GetCallback {

		private ACTION_OPTION mOption;

		private final SateliteListener mListener;

		private final String mSecondArg;

		/**
		 * Creates a callback that is able to respond to the user 
		 * once a download is complete.  Secondary arguments is a helper
		 * because some methods require a secondary ID.
		 * @param listener Listener for which method to call
		 * @param secondArgument Second string that contain object ID or formatted dates
		 */
		public SatelliteGetCallback(SateliteListener listener, String secondArgument) {
			mListener = listener;
			mOption = ACTION_OPTION.NA;
			mSecondArg = secondArgument;
		}

		/**
		 * Set option that determines appropiate action in response
		 * to user request.
		 * @param option Option to set
		 */
		public void setOption(ACTION_OPTION option) {
			mOption = option;
		}

		@Override
		public void done(ParseObject object, ParseException e) {
			if (e != null) {
				mListener.onFail(e.getMessage());
				return;
			}
			try {
				switch (mOption) {
				case CHANGE_USER_INFO:
					mListener.onUserChanged(new UserInfo(object));
					break;
				case REQUEST_CHECK_OUT:
					mListener.onCheckedOut(new DiningSession(object));
					break;
				case REQUEST_DINING_SESSION:
					int tableNum;
					try {
						tableNum = Integer.parseInt(mSecondArg);
					} catch (NumberFormatException e1) {
						tableNum = -1; // Bad number input
					}
					mListener.onUserCheckedIn(new UserInfo(object), tableNum);
					break;
				case REQUEST_CUSTOMER_REQUEST:
					mListener.onCustomerRequest(new CustomerRequest(object),
							mSecondArg);
					break;
				case REQUEST_RESERVATION:
					mListener.onReservationRequest(new Reservation(object));
					break;
				case REQUEST_ORDER:
					mListener.onOrderRequest(new Order(object), mSecondArg);
					break;
				default:
					if (DineOnConstants.DEBUG) {
						mListener.onFail("Unknown Request Recieved!");
					}
				}
			} catch (Exception e1) {
				mListener.onFail(e1.getMessage());
			}
		}
	}

	/**
	 * Listener for Activities to implement to receive action.
	 * @author mhotan
	 */
	interface SateliteListener { // Package level notification

		/**
		 * Notifies that a error occured.
		 * Most likely it was a network error
		 * @param message Failure message that generally describes problem.
		 */
		void onFail(String message);

		/**
		 * Notifies Application the Users have checked out
		 * for associated dining session.
		 * @param session checked out dining session
		 */
		void onCheckedOut(DiningSession session);

		/**
		 * User attempted to check in to restaurant 
		 * identified and register time.
		 * @param user User that attempted to CheckIn
		 * @param tableID Table ID that the user has checked in at.
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
		 * @param order Order to be added
		 * @param sessionID ID of the dining session to add to
		 */
		void onOrderRequest(Order order, String sessionID);

		/**
		 * Notifies that a customer request was placed from the User(s)
		 * associated with the attached dining session.
		 * @param request Customer request to add
		 * @param sessionID ID of the dining session to add to
		 */
		void onCustomerRequest(CustomerRequest request, String sessionID);

		/**
		 * Notifies that a customer request was placed from the User(s)
		 * associated with the attached dining session.
		 * @param reservation Date that user request.
		 */
		void onReservationRequest(Reservation reservation);


	}

}
