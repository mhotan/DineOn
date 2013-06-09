package uw.cse.dineon.user;

import org.json.JSONException;
import org.json.JSONObject;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.ParseUtil;
import uw.cse.dineon.user.DiningSessionDownloader.DiningSessionGetCallback;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.parse.ParseQuery.CachePolicy;

/**
 * 
 * @author mhotan
 */
public class DineOnUserService extends Service {

	private static final String TAG = DineOnUserService.class.getSimpleName();

	private final IBinder mBinder = new DineOnUserBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
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
								return;
							}
							Intent intent = new Intent();
							intent.setAction(ACTION_LOCAL);
							sendBroadcast(intent);
						}
					});
			sessionDownloader.execute(CachePolicy.NETWORK_ELSE_CACHE);
		} 

		return Service.START_NOT_STICKY;
	}
}
