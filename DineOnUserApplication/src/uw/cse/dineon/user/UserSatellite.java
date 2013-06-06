package uw.cse.dineon.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Reservation;
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
 * This class manages the communication between the Customer. 
 * and a Restaurant associated by dining session
 * 
 * This Receiver manages all the actions that are required by contract
 * 
 * @author mhotan
 */
public class UserSatellite extends BroadcastReceiver {

	/**
	 * Logging tag.
	 */
	private static final String TAG = UserSatellite.class.getSimpleName();

	/**
	 * User this station associates to.
	 */

	/**
	 * Intent filter to control which actions to listen for.
	 */
	private final IntentFilter mIF;

	/**
	 * The channel associate with THIS Satellite.
	 */
	private String mChannel;

	/**
	 * The activity that the broadcast receiever registers with.
	 * All callback or listener methods are routed through this activity.
	 */
	private DineOnUserActivity mCurrentActivity;

	/**
	 * Enum classes that control the type of objects to download.
	 * @author mhotan
	 */
	private enum ACTION_OPTION {
		NA,
		INTIAL_DS_RECEIVED,
		RESTAURANT_INFO_CHANGE,
		CONFIRM_RESERVATION,
		CONFIRM_ORDER,
		CONFIRM_CUSTOMER_REQUEST }

	/**
	 * Creates and prepares this satellite for transmission
	 * and reception. 
	 */
	public UserSatellite() {
		mIF = new IntentFilter();
		for (String action: DineOnConstants.CUSTOMER_ACTIONS) {
			mIF.addAction(action);
		}
	}

	/**
	 * Registers this activity and registers a channel for the inputed user.
	 * After this notifications (if any) may arise
	 * @param user User to associate this satellite to
	 * @param activity Activity that will be registered
	 */
	public void register(DineOnUser user, DineOnUserActivity activity) {

		// Check for null values
		if (user == null) {
			throw new IllegalArgumentException("User was null when registering " 
					+ "broadcast reciever.");
		}
		if (activity == null) {
			Log.w(TAG, "UserSatelite attempted to register null activity");
			return;
		}

		// Establish the activity 
		mCurrentActivity = activity;

		// Establish the channel to make 
		mChannel = ParseUtil.getChannel(DineOnUserApplication.getUserInfo());

		// Subscribe to my channel so I can hear incoming messages
		PushService.subscribe(activity, mChannel, activity.getClass());
		
		// Registers this activity to this receiver
		mCurrentActivity.registerReceiver(this, mIF);
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
	 * User inputed is requesting to check in the current restaurant.
	 * IE. General use case Restaurant customer "user" arrives at a restaurant "rest".
	 * user then attempts to check in to restaurant and table identified at "tableNum".  
	 * The user application will call this method requestCheckIn(user, tableNum, rest).  
	 * Response should then  
	 *  
	 * NOTE: This method does not do any saving. That is if you want to update the
	 * restaurant you must save your argument before you call this method
	 * 
	 * @param user User to associate check in request
	 * @param tableNum Table number to associate check in request to
	 * @param rest Restaurant 
	 */
	public void requestCheckIn(UserInfo user, int tableNum,  String rest) {
		Map<String, String> attr = new HashMap<String, String>();
		attr.put(DineOnConstants.OBJ_ID_2, "" + tableNum);
		attr.put(DineOnConstants.OBJ_ID, user.getObjId());
		notifyByAction(DineOnConstants.ACTION_REQUEST_DINING_SESSION, attr, rest);
	} 

	/**
	 * Notify the restaurant that an order was placed for this dining session.
	 * NOTE: This method does not do any saving. That is if you want to update the
	 * restaurant you must save your argument before you call this method
	 * @param session Dining Session that was updated
	 * @param order Order to request
	 * @param rest Restaurant to place order at
	 */
	public void requestOrder(DiningSession session, 
			Order order, 
			RestaurantInfo rest) {
		notifyByAction(DineOnConstants.ACTION_REQUEST_ORDER, 
				order.getObjId(), // Make sure they download the Order Object
				session.getObjId(), // Reference the Session ID
				rest.getName());
	}

	/**
	 * Notify the restaurant that a Customer Request was placed for this
	 * dining session.
	 * NOTE: This method does not do any saving. That is if you want to update the
	 * restaurant you must save your argument before you call this method
	 * @param session Saved DiningSession that has a new Customer Request
	 * @param request Customer Request to request
	 * @param rest Restaurant to send notification to.
	 */
	public void requestCustomerRequest(DiningSession session, 
			CustomerRequest request,
			RestaurantInfo rest) {
		notifyByAction(DineOnConstants.ACTION_REQUEST_CUSTOMER_REQUEST,
				request.getObjId(), // Make sure they download the Request Object
				session.getObjId(),
				rest.getName()); 
	}

	/**
	 * Notify the restaurant that a Customer Request was placed for this
	 * dining session.
	 * NOTE: This method does not do any saving. That is if you want to update the
	 * restaurant you must save your argument before you call this method
	 * @param reservation Saved DiningSession that has a new Customer Request
	 * @param rest Restaurant to send notification to.
	 */
	public void requestReservation(Reservation reservation,
			RestaurantInfo rest) {
		notifyByAction(DineOnConstants.ACTION_REQUEST_RESERVATION,
				reservation.getObjId(),
				rest.getName()); 
	}

	/**
	 * Notifies the restaurant that the user has successfully checked out.
	 * This method does not do any saving.
	 * NOTE: This method does not do any saving. That is if you want to update the
	 * restaurant you must save your argument before you call this method
	 * @param session Saved DiningSession that has been checked out.
	 * @param rest Restaurant to send notification to.
	 */
	public void requestCheckOut(DiningSession session, RestaurantInfo rest) {
		notifyByAction(DineOnConstants.ACTION_REQUEST_CHECK_OUT,
				session.getObjId(), rest.getName()); 
	}

	/**
	 * Notifies the Restaurant that user has changed some aspects about
	 * itself.  
	 * NOTE: This method does not do any saving. That is if you want to update the
	 * restaurant you must save your argument before you call this method
	 * @param user User that has already been saved.
	 * @param rest Restaurant to send notification to.
	 */
	public void notifyChangeUserInfo(UserInfo user, RestaurantInfo rest) {
		notifyByAction(DineOnConstants.ACTION_CHANGE_USER_INFO, user.getObjId(), rest.getName());
	}

	/**
	 * General notifier that tells the Restaurant associated with 
	 * info that a new object is ready for them to download.  The type
	 * of object to download is dictated by the action argument.  This
	 * "action" is a specification that is predetermined by Restaurant and
	 * Customer
	 * @param action Action to send to Restaurant
	 * @param id1 Object ID to notify restaurant for
	 * @param id2 Second Object ID to notify restaurant with  
	 * @param info Restaurant to associate to
	 */
	private void notifyByAction(String action, 
			String id1, String id2, String info) {
		// Have to check the pointers before sending a request 
		if (id1 == null) {
			throw new NullPointerException("[notifiyAction] id is null");
		}
		Map<String, String> attr = new HashMap<String, String>();
		attr.put(DineOnConstants.OBJ_ID, id1);
		attr.put(DineOnConstants.OBJ_ID_2, id2);
		notifyByAction(action, attr, info);
	}

	/**
	 * General notifier that tells the Restaurant associated with 
	 * info that a new object is ready for them to download.  The type
	 * of object to download is dictated by the action argument.  This
	 * "action" is a specification that is predetermined by Restaurant and
	 * Customer
	 * @param action Action to send to Restaurant
	 * @param id1 Object ID to notify restaurant for
	 * @param info Restaurant to associate to
	 */
	private void notifyByAction(String action, 
			String id1, String info) {
		// Have to check the pointers before sending a request 
		if (id1 == null) {
			throw new NullPointerException("[notifiyAction] id is null");
		}
		Map<String, String> attr = new HashMap<String, String>();
		attr.put(DineOnConstants.OBJ_ID, id1);
		notifyByAction(action, attr, info);
	}

	/**
	 * General notifier that sends a mapping of attributes to the restaurant.
	 * The type of reaction by the restaurant is dictated by the action argument.
	 * This "action" is a specification that is predetermined by Restaurant and
	 * Customer IE DineOnConstant.ACTION_...
	 * @param action Action to send to Restaurant
	 * @param attr Attributes to sent to the Restaurant.
	 * @param info Restaurant to associate to
	 */
	private void notifyByAction(String action, 
			Map<String, String> attr, String info) {
		if (action == null) {
			throw new NullPointerException("[notifiyAction] action is null");
		}
		if (info == null) {
			throw new NullPointerException("[notifiyAction] info is null");
		}

		JSONObject jobj = new JSONObject();
		try {
			jobj.put(DineOnConstants.KEY_ACTION, action);
			
			for (Entry<String, String> e : attr.entrySet()) {
				jobj.put(e.getKey(), e.getValue());				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Send IT!
		ParseUtil.notifyApplication(
				jobj,
				ParseUtil.getChannel(info));
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

		// They are sending to the wrong channel
		if (!theirChannel.equals(mChannel)) {
			return;
		}

		// Retrieve the action that the other satellite is requesting
		String action = intent.getAction();

		String id = null;
		JSONObject jo;
		try {
			jo = new JSONObject(
					intent.getExtras().getString(DineOnConstants.PARSE_DATA));
			id = jo.getString(DineOnConstants.OBJ_ID);
		} catch (JSONException e) {
			Log.d(TAG, "Customer sent fail case: " + e.getMessage());
			mCurrentActivity.onFail(e.getMessage());
			// Restaurant sent malformed data...
			// NOTE (MH) : What does it mean when we fail like this?
			return;
		}

		String id2 = null;
		try {
			id2 = jo.getString(DineOnConstants.OBJ_ID_2);
		} catch (JSONException e) {
			Log.d(TAG, "ID 2 is null for action: " + action);
		}

		// Prepare the queries that we might need 
		ParseQuery restInfo = new ParseQuery(RestaurantInfo.class.getSimpleName());
		ParseQuery dsQuery = new ParseQuery(DiningSession.class.getSimpleName());
		restInfo.setCachePolicy(CachePolicy.NETWORK_ONLY);
		dsQuery.setCachePolicy(CachePolicy.NETWORK_ONLY);

		ParseQuery query;
		SatelliteGetCallback callback = new SatelliteGetCallback(mCurrentActivity, id2);

		// Restaurant is confirming the dining session by returning a dining session.
		if (DineOnConstants.ACTION_CONFIRM_DINING_SESSION.equals(action)) {
			// Received the intial Dining Session
			query = new ParseQuery(DiningSession.class.getSimpleName());
			query.setCachePolicy(CachePolicy.NETWORK_ONLY);
			callback.setOption(ACTION_OPTION.INTIAL_DS_RECEIVED);
			query.getInBackground(id, callback);
		} 
		// Restaurant that we are currently associated to has changed some state
		else if (DineOnConstants.ACTION_CHANGE_RESTAURANT_INFO.equals(action)) {
			// WE received a updated Restaurant Info 
			query = new ParseQuery(RestaurantInfo.class.getSimpleName());
			query.setCachePolicy(CachePolicy.NETWORK_ONLY);
			callback.setOption(ACTION_OPTION.RESTAURANT_INFO_CHANGE);
			query.getInBackground(id, callback);
		}
		else if (DineOnConstants.ACTION_CONFIRM_ORDER.equals(action)) {
			// Attempt to get the Dining Session that was updated.
//			query = new ParseQuery(DiningSession.class.getSimpleName());
//			query.setCachePolicy(CachePolicy.NETWORK_ONLY);
//			callback.setOption(ACTION_OPTION.CONFIRM_ORDER);
//			query.getInBackground(id, callback);
			
			DiningSessionDownloader sessionDownloader = new DiningSessionDownloader(id);
			sessionDownloader.execute(CachePolicy.NETWORK_ELSE_CACHE);
		}
		else if (DineOnConstants.ACTION_CONFIRM_RESERVATION.equals(action)) {
			// Attempt to get the Reservation that was updated.
			query = new ParseQuery(Reservation.class.getSimpleName());
			query.setCachePolicy(CachePolicy.NETWORK_ONLY);
			callback.setOption(ACTION_OPTION.CONFIRM_RESERVATION);
			query.getInBackground(id, callback);
		}
		else if (DineOnConstants.ACTION_CONFIRM_CUSTOMER_REQUEST.equals(action)) {
			// Attempt to get the Dining Session that was updated.
//			query = new ParseQuery(DiningSession.class.getSimpleName());
//			query.setCachePolicy(CachePolicy.NETWORK_ONLY);
//			callback.setOption(ACTION_OPTION.CONFIRM_CUSTOMER_REQUEST);
//			query.getInBackground(id, callback);
			
			DiningSessionDownloader sessionDownloader = new DiningSessionDownloader(id);
			sessionDownloader.execute(CachePolicy.NETWORK_ELSE_CACHE);
		}
	}



	/**
	 * Private GetCallback that encapsulates the processing of 
	 * different types of Gets.
	 * @author mhotan
	 */
	private static class SatelliteGetCallback extends GetCallback {

		private ACTION_OPTION mOption;

		private final SatelliteListener mListener;

		private final String mSecondArg;

		/**
		 * Creates a callback that is able to respond to the user 
		 * once a download is complete.  Secondary arguments is a helper
		 * because some methods require a secondary ID.
		 * @param listener Listener for which method to call
		 * @param secondArgument Second string that contain object ID or formatted dates
		 */
		public SatelliteGetCallback(SatelliteListener listener, 
				String secondArgument) {
			mListener = listener;
			mOption = ACTION_OPTION.NA;
			mSecondArg = secondArgument;
		}

		/**
		 * Sets the appropriate action for reacting a restaurant response.
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
				case INTIAL_DS_RECEIVED: 
					mListener.onInitialDiningSessionReceived(
							new DiningSession(object));
					break;
				case RESTAURANT_INFO_CHANGE:
					mListener.onRestaurantInfoChanged(
							new RestaurantInfo(object));
					break;
				case CONFIRM_RESERVATION: 
					mListener.onConfirmReservation(new Reservation(object));
					break;
				case CONFIRM_ORDER:
					if (mSecondArg == null) {
						mListener.onFail("Restaurant failed to confirm your order");
						return;
					}
					mListener.onConfirmOrder(
							new DiningSession(object), mSecondArg);
					break;
				case CONFIRM_CUSTOMER_REQUEST:
					if (mSecondArg == null) {
						mListener.onFail("Restaurant failed to confirm your request");
						return;
					}
					mListener.onConfirmCustomerRequest(
							new DiningSession(object), mSecondArg);
					break;
				default:
					if (DineOnConstants.DEBUG) {
						mListener.onFail("Unknown Request Recieved!");
					}
				}
			} catch (ParseException e1) {
				mListener.onFail(e1.getMessage());
			}
		}
	}

	/**
	 * Listener for network callback from the Satellite.
	 * @author mhotan
	 */
	public interface SatelliteListener {

		/**
		 * Notifies that a error occured.
		 * Most likely it was a network error
		 * @param message Failure message that generally describes problem.
		 */
		void onFail(String message);

		/**
		 * Notifies Customer user that a Dining session has been established
		 * and returns it via this callback.
		 * 
		 * @param session DiningSession instance on success, null on failure 
		 * (null => Restaurant not accepting dining features)
		 */
		void onInitialDiningSessionReceived(DiningSession session);

		/**
		 * Notifies the user that the restaurant has changed its state.
		 * @param restaurant Restaurant that has recently changed
		 */
		void onRestaurantInfoChanged(RestaurantInfo restaurant);

		/**
		 * Notifies the user that the reservation requested was accepted.
		 * @param res Reservation object
		 */
		void onConfirmReservation(Reservation res);

		/**
		 * Notifies the customer that there previous request 
		 * to update the Dining Session has gone through.
		 * 
		 * @param ds Dining session that has been changed
		 * @param orderId ID of the Order that was updated
		 */
		void onConfirmOrder(DiningSession ds, String orderId);

		/**
		 * Notifies the customer that there previous request 
		 * to update the Dining Session has gone through.
		 * 
		 * @param ds Dining session that has been changed
		 * @param requestID ID of the Customer Request that was updated
		 */
		void onConfirmCustomerRequest(DiningSession ds, String requestID);

	}

}
