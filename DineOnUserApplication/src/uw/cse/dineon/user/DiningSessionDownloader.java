package uw.cse.dineon.user;

import uw.cse.dineon.library.DiningSession;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;

/**
 * This class allows a dining session to be downloaded in an asynchronous
 * background task and the result is saved to th static reference in the
 * DineOnUserApplication.
 * @author mtrathjen08
 *
 */
public class DiningSessionDownloader extends AsyncTask<CachePolicy, ParseException, DiningSession> {

	private static final String TAG = DiningSessionDownloader.class.getSimpleName();

	/**
	 * ParseUser associated with Dining Session.
	 */
	private final ParseUser mParseUser;

//	/**
//	 * Call back for completion of dining session download.
//	 */
//	private final DiningSessionDownLoaderCallback mCallback;

	/**
	 * The dining session parse object ID of the Dining Session to find.
	 */
	private final String mSessionID;

	/**
	 * Message associated with parse exception generated.
	 */
	private String parseExceptionMessage;

//	/**
//	 * Creates a DiningSession Downloader that retrieves the Dining Session 
//	 * associated with the provided object id from Parse.
//	 * 
//	 * @param user ParseUser to use to download the dining session
//	 * @param callback Callback to listen for events
//	 */
//	public DiningSessionDownloader(ParseUser user, DiningSessionDownLoaderCallback callback) {
//		if (user == null) {
//			throw new NullPointerException(TAG + "Can't have null user");
//		}
//		if (callback == null) {
//			throw new NullPointerException(TAG + "Can't have null callback");
//		}
//		mParseUser = user;
//		mCallback = callback;
//		mSessionID = null;
//	}

	/**
	 * Creates a Dining Session Downloader that retrieves the dining session 
	 * given the Parse object id.
	 * @param id Object id of the Parse Dining Session
	 */
	public DiningSessionDownloader(String id) {
		if (id == null) {
			throw new NullPointerException(TAG + "Can't have null user");
		}
		mSessionID = id;
		mParseUser = null;
	}

	// Background process.
	@Override
	protected DiningSession doInBackground(CachePolicy... params) {
		if (params[0] == null) {
			params[0] = CachePolicy.NETWORK_ELSE_CACHE;
		}
		CachePolicy policy = params[0];

		try {
//			// Download by Parse User
//			if (mParseUser != null) {
//				mParseUser.fetchIfNeeded();
//				return getFromUser(policy);
//			} else {
				return getFromID(policy);
//			}
		} catch (ParseException e) {
			// If any error case happened at all send the error back
			onProgressUpdate(e);
		}
		return null;
	}

	/**
	 * Gets the Dining Session associated with session id.
	 * @param policy Cache Policy to use to retrieve Dining Session
	 * @return Dining Session on success, otherwise null
	 * @throws ParseException for request failure
	 */
	private DiningSession getFromID(CachePolicy policy) throws ParseException { 
		ParseQuery query = new ParseQuery(DiningSession.class.getSimpleName());
		query.setCachePolicy(policy);
		ParseObject sessionObject = query.get(mSessionID);
		return new DiningSession(sessionObject);
	}

	/**
	 * Called when a parse exception is returned during the request.
	 * @param pairs Exceptions returned from Parse request
	 */
	@Override
	protected void onProgressUpdate(ParseException... pairs) {
		ParseException exception = pairs[0];
		if (exception == null) {
			parseExceptionMessage = "Unknown Error";
			return;
		}
		parseExceptionMessage = exception.getMessage();
	}
	
	/**
	 * Called when the Dining Session has been successfully received.
	 * @param result Dining Session retreived from Parse
	 */
	@Override
	protected void onPostExecute(DiningSession result) {
		DineOnUserApplication.setCurrentDiningSession(result);
	
		
//		if (result == null) {
//			Log.e(TAG, "Unable to download dining session.");
//			// No need to call on fail.
//			mCallback.onFailToDownLoadDiningSession(parseExceptionMessage);
//			return;
//		}
//		mCallback.onDownloadedDiningSession(result);
	}

//	/**
//	 * Interface used by user application to interact with downloader.
//	 * @author mtrathjen08
//	 */
//	public interface DiningSessionDownLoaderCallback {
//
//		/**
//		 * Notifies caller that the Dining Session request failed.
//		 * @param message Description of what failed
//		 */
//		void onFailToDownLoadDiningSession(String message);
//
//		/**
//		 * Notifies the caller that the Dining Session was successfully retrieved.
//		 * @param session Dining Session retrieved from Parse
//		 */
//		void onDownloadedDiningSession(DiningSession session);
//
//	} 

}
