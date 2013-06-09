package uw.cse.dineon.user.login;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.UserInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;

/**
 * This class downloads the DineOnUser associated with the user 
 * that is currently attempting to log in.
 * @author mtrathjen08
 *
 */
public class DineOnUserDownloader extends 
	AsyncTask<CachePolicy, ParseException, DineOnUser> {

	private final String TAG = DineOnUserDownloader.class.getSimpleName();

	/**
	 * Call back for completion of dining session download.
	 */
	private final DineOnUserDownloaderCallback mCallback;

	/**
	 * Message associated with parse exception generated.
	 */
	private String parseExceptionMessage;
	
	/**
	 * ParseUser associated with DineOnUser.
	 */
	private ParseUser mUser;

	/**
	 * Creates a RestaurantInfoDownloader that retrieves the list of restaurant infos
	 * associated with the provided restaurant string name from parse.
	 * 
	 * @param user ParseUser connected to DineOnUser
	 * @param callback Callback to listen for events
	 */
	public DineOnUserDownloader(ParseUser user, DineOnUserDownloaderCallback callback) {
		if (user == null) {
			throw new NullPointerException(TAG + "Can't have null parse user");
		}
		if (callback == null) {
			throw new NullPointerException(TAG + "Can't have null callback");
		}
		this.mCallback = callback;
		this.mUser = user;
	}
	
	// Background process.
	@Override
	protected DineOnUser doInBackground(CachePolicy... params) {
		if (params[0] == null) {
			params[0] = CachePolicy.NETWORK_ELSE_CACHE;
		}
		CachePolicy policy = params[0];

		try {
			if (this.mUser != null) {
				return getFromParseUser(policy);
			}
		} catch (ParseException e) {
			// If any error case happened at all send the error back
			onProgressUpdate(e);
		}
		return null;
	}

	/**
	 * Gets the DineOn User with associated ParseUser object.
	 * @param policy Cache Policy to use to retrieve DineOn User
	 * @return DineOn Use on success, otherwise null
	 * @throws ParseException for request failure
	 */
	private DineOnUser getFromParseUser(CachePolicy policy) throws ParseException { 
		ParseQuery inner = new ParseQuery(UserInfo.class.getSimpleName());
		inner.whereEqualTo(UserInfo.PARSEUSER, this.mUser);
		ParseQuery query = new ParseQuery(DineOnUser.class.getSimpleName());
		query.whereMatchesQuery(DineOnUser.USER_INFO, inner);
		ParseObject user = query.getFirst();
		return new DineOnUser(user);
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
	 * Called when the DineOn User has been successfully received.
	 * @param result DineOn User retreived from Parse
	 */
	@Override
	protected void onPostExecute(DineOnUser result) {
		if (result == null) {
			Log.e(TAG, "Unable to download dineon user.");
			// No need to call on fail.
			mCallback.onFailToDownLoadDineOnUser(parseExceptionMessage);
			return;
		}
		mCallback.onDownloadedDineOnUser(result);
	}

	/**
	 * Interface used by user application to interact with downloader.
	 * @author mtrathjen08
	 */
	public interface DineOnUserDownloaderCallback {

		/**
		 * Notifies caller that the DineOn User requested failed.
		 * @param message Description of what failed
		 */
		void onFailToDownLoadDineOnUser(String message);

		/**
		 * Notifies the caller that the DineOn User was successfully retrieved.
		 * @param user DineOn User retrieved from Parse
		 */
		void onDownloadedDineOnUser(DineOnUser user);

	} 
}