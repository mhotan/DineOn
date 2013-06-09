package uw.cse.dineon.user;

import org.json.JSONException;
import org.json.JSONObject;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Reservation;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.ParseUtil;
import uw.cse.dineon.user.DiningSessionDownloader.DiningSessionGetCallback;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;

/**
 * Service that handles the downloading of major items and then broadcasting
 * them to all the need to know activities.  So pretty much does all the heavy lifting
 * in the background and we dont have to worry about it dieing abruptly as much.
 * 
 * @author mhotan
 */
public class DineOnUserService extends Service {

	private static final String TAG = DineOnUserService.class.getSimpleName();

	private final IBinder mBinder = new DineOnUserBinder();

	private RestaurantInfo mLastChanged;
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	/**
	 * @return the restaurant that was last changed.
	 */
	public RestaurantInfo getLastChanged() {
		return mLastChanged;
	}

	/**
	 * Binder to let activities use this service.
	 * @author mhotan
	 */
	public class DineOnUserBinder extends Binder {

		/**
		 * Returns the instance of this service.
		 * @return Instance of this service
		 */
		DineOnUserService getService() {
			return DineOnUserService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Check if the user is in a state to retrieve messages.
		DineOnUser user = DineOnUserApplication.getDineOnUser();
		if (user == null) { // No need to continue
			Log.w(TAG, "Request called when service ");
			return Service.START_NOT_STICKY;
		} 

		String action = intent.getAction();
		String data = intent.getStringExtra(DineOnConstants.PARSE_DATA);
		String channel = intent.getStringExtra(DineOnConstants.PARSE_CHANNEL);

		if (!ParseUtil.getChannel(user.getUserInfo()).equals(channel)) {
			Log.w(TAG, "Channel Mismatch");
			return Service.START_NOT_STICKY;
		}

		String id = null;
		JSONObject jo;
		try {
			jo = new JSONObject(data);
			id = jo.getString(DineOnConstants.OBJ_ID);
		} catch (JSONException e) {
			Log.d(TAG, "Customer sent fail case: " + e.getMessage());
			return Service.START_NOT_STICKY;
		}

		String id2 = null;
		try {
			id2 = jo.getString(DineOnConstants.OBJ_ID_2);
		} catch (JSONException e) {
			Log.d(TAG, "ID 2 is null for action: " + action);
		}

		// Handle the cases where user needs to download dining session again.
		if (DineOnConstants.ACTION_CONFIRM_CUSTOMER_REQUEST.equals(action) 
				|| DineOnConstants.ACTION_CONFIRM_DINING_SESSION.equals(action) 
				|| DineOnConstants.ACTION_CONFIRM_ORDER.equals(action)) {

			final String ACTION_LOCAL;
			if (DineOnConstants.ACTION_CONFIRM_CUSTOMER_REQUEST.equals(action)) {
				ACTION_LOCAL = DineOnConstants.ACTION_CONFIRM_CUSTOMER_REQUEST_LOCAL;
			} else if (DineOnConstants.ACTION_CONFIRM_DINING_SESSION.equals(action)) {
				ACTION_LOCAL = DineOnConstants.ACTION_CONFIRM_DINING_SESSION_LOCAL;
			} else {
				ACTION_LOCAL = DineOnConstants.ACTION_CONFIRM_ORDER_LOCAL;
			}

			DiningSessionDownloader sessionDownloader = new DiningSessionDownloader(
					id, new DiningSessionGetCallback() {

						@Override
						public void done(DiningSession session, Exception e) {
							if (e != null) {
								Log.e(TAG, "Error retrieving dining session.");
								sendFailMessage(e.getMessage());
								return;
							}
							Intent intent = new Intent();
							intent.setAction(ACTION_LOCAL);
							sendBroadcast(intent);
						}
					});
			sessionDownloader.execute(CachePolicy.NETWORK_ELSE_CACHE);
		} // Restaurant information changed.
		else if (DineOnConstants.ACTION_CHANGE_RESTAURANT_INFO.equals(action)) { 
			
			new RestaurantInfoDownloader(id, new RestaurantInfoGetCallback() {
				
				@Override
				public void done(RestaurantInfo restaurantInfo, Exception e) {
					if (e != null) {
						Log.e(TAG, "Error retrieving Restaurant info.");
						return;
					}
					Intent intent = new Intent();
					intent.setAction(DineOnConstants.ACTION_CHANGE_RESTAURANT_INFO_LOCAL);
					sendBroadcast(intent);
				}
			}).execute(CachePolicy.NETWORK_ONLY);
		} 
		else if (DineOnConstants.ACTION_CONFIRM_RESERVATION.equals(action)) {
			
			new ReservationDownloader(id, new ReservationGetCallback() {
				
				@Override
				public void done(Reservation reservation, Exception e) {
					if (e != null) {
						Log.e(TAG, "Error retrieving Reservation confirmation.");
						return;
					}
					Intent intent = new Intent();
					intent.setAction(DineOnConstants.ACTION_CONFIRM_RESERVATION_LOCAL);
					
					sendBroadcast(intent);
				}
			});
		}

		return Service.START_NOT_STICKY;
	}
	
	/**
	 * Broadcast failure to any running activity.
	 * @param message Error message
	 */
	private void sendFailMessage(String message) {
		Intent intent = new Intent();
		intent.setAction(DineOnConstants.ACTION_FAIL_LOCAL);
		intent.putExtra(DineOnConstants.ARGUMENT, message);
		sendBroadcast(intent);
	}

	/**
	 * Restaurant Info downlaoder.
	 * @author mhotan
	 */
	private class RestaurantInfoDownloader extends 
	AsyncTask<CachePolicy, Exception, RestaurantInfo> {

		private Exception mException;
		private final String mId;
		private final RestaurantInfoGetCallback mCallback;

		/**
		 * 
		 * @param id id of restaurant info object to download.
		 * @param callback Callback once complete
		 */
		public RestaurantInfoDownloader(String id, RestaurantInfoGetCallback callback) {
			mId = id;
			mCallback = callback;
		}

		@Override
		protected RestaurantInfo doInBackground(CachePolicy... params) {
			try {
				if (params[0] == null) {
					params[0] = CachePolicy.NETWORK_ELSE_CACHE;
				}
				CachePolicy policy = params[0];
				return getFromID(policy);
			} catch (Exception e) {
				// If any error case happened at all send the error back
				onProgressUpdate(e);
			}
			// Error
			return null;
		}
		
		/**
		 * Called when a parse exception is returned during the request.
		 * @param pairs Exceptions returned from Parse request
		 */
		@Override
		protected void onProgressUpdate(Exception... pairs) {
			Exception exception = pairs[0];
			if (exception == null) {
				mException = new Exception("Unknown Error");
				return;
			}
			mException = exception;
		}
		
		/**
		 * Called when the Restaurant Info has been successfully received.
		 * @param result Restaurant Info retreived from Parse
		 */
		@Override
		protected void onPostExecute(RestaurantInfo result) {
			if (mException != null) {
				mLastChanged = result;
			}
			if (mCallback != null) {
				mCallback.done(result, mException);
			}
		}

		/**
		 * Gets the Dining Session associated with session id.
		 * @param policy Cache Policy to use to retrieve Dining Session
		 * @return Dining Session on success, otherwise null
		 * @throws ParseException for request failure
		 */
		private RestaurantInfo getFromID(CachePolicy policy) throws ParseException { 
			ParseQuery query = new ParseQuery(RestaurantInfo.class.getSimpleName());
			query.setCachePolicy(policy);
			ParseObject sessionObject = query.get(mId);
			
			// This calls takes a long ass time.
			return new RestaurantInfo(sessionObject);
		}
	}
	
	/**
	 * Callback for restaurant infos.
	 * @author mhotan
	 */
	public interface RestaurantInfoGetCallback {
		
		/**
		 * Called on completion of dining session retrieval.
		 * @param restaurantInfo Restaurant info retrieved
		 * @param e Exception if error occured else null
		 */
		void done(RestaurantInfo restaurantInfo, Exception e);
		
	} 
	
	/**
	 * 
	 * @author mhotan
	 *
	 */
	private class ReservationDownloader extends AsyncTask<CachePolicy, Exception, Reservation> {
		
		private Exception mException;
		private final String mId;
		private final ReservationGetCallback mCallback;

		/**
		 * 
		 * @param id id of restaurant info object to download.
		 * @param callback Callback once complete
		 */
		public ReservationDownloader(String id, ReservationGetCallback callback) {
			mId = id;
			mCallback = callback;
		}

		@Override
		protected Reservation doInBackground(CachePolicy... params) {
			try {
				if (params[0] == null) {
					params[0] = CachePolicy.NETWORK_ELSE_CACHE;
				}
				CachePolicy policy = params[0];
				return getFromID(policy);
			} catch (Exception e) {
				// If any error case happened at all send the error back
				onProgressUpdate(e);
			}
			// Error
			return null;
		}
		
		/**
		 * Called when a parse exception is returned during the request.
		 * @param pairs Exceptions returned from Parse request
		 */
		@Override
		protected void onProgressUpdate(Exception... pairs) {
			Exception exception = pairs[0];
			if (exception == null) {
				mException = new Exception("Unknown Error");
				return;
			}
			mException = exception;
		}
		
		/**
		 * Called when the Restaurant Info has been successfully received.
		 * @param result Restaurant Info retreived from Parse
		 */
		@Override
		protected void onPostExecute(Reservation result) {
			DineOnUser user = DineOnUserApplication.getDineOnUser();
			if (mCallback != null && user != null) {
				user.addReservation(result);
				mCallback.done(result, mException);
			}
		}

		/**
		 * Gets the Dining Session associated with session id.
		 * @param policy Cache Policy to use to retrieve Dining Session
		 * @return Dining Session on success, otherwise null
		 * @throws ParseException for request failure
		 */
		private Reservation getFromID(CachePolicy policy) throws ParseException { 
			ParseQuery query = new ParseQuery(Reservation.class.getSimpleName());
			query.setCachePolicy(policy);
			ParseObject sessionObject = query.get(mId);
			
			// This calls takes a long ass time.
			return new Reservation(sessionObject);
		}
		
	}
	
	/**
	 * 
	 * @author mhotan
	 */
	private interface ReservationGetCallback {
		
		/**
		 * Callend on completion of reservation download.
		 * @param reservation Reservation that was downloaded, null if error
		 * @param e null if no error, otherwise exception that occured
		 */
		void done(Reservation reservation, Exception e);
	}
	
	
}
