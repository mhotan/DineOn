package uw.cse.dineon.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uw.cse.dineon.library.LocatableStorable;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;

/**
 * This class allows a dining session to be downloaded in an asynchronous
 * background task and the result is saved to th static reference in the
 * DineOnUserApplication.
 * @author mtrathjen08
 *
 */
public class RestaurantInfoDownloader extends 
	AsyncTask<CachePolicy, ParseException, List<RestaurantInfo>> {

	private static final String TAG = RestaurantInfoDownloader.class.getSimpleName();

	/**
	 * Call back for completion of dining session download.
	 */
	private final RestaurantInfoDownLoaderCallback mCallback;

	/**
	 * Restaurant name to search parse.
	 */
	private String restName;
	
	/**
	 * List of restaurant ids of favorites.
	 */
	private String[] restIds;
	
	/**
	 * User location to search for nearby restaurants.
	 */
	private ParseGeoPoint userLoc;

	/**
	 * Message associated with parse exception generated.
	 */
	private String parseExceptionMessage;

	/**
	 * Creates a RestaurantInfoDownloader that retrieves the list of restaurant infos
	 * associated with the provided restaurant string name from parse.
	 * 
	 * @param restName string name of the restaurant of interest
	 * @param callback Callback to listen for events
	 */
	public RestaurantInfoDownloader(String restName, RestaurantInfoDownLoaderCallback callback) {
		if (restName == null) {
			throw new NullPointerException(TAG + "Can't have null restaurant name");
		}
		if (callback == null) {
			throw new NullPointerException(TAG + "Can't have null callback");
		}
		mCallback = callback;
		this.restName = restName;
		this.restIds = null;
		this.userLoc = null;
	}
	
	/**
	 * Creates a RestaurantInfoDownloader that retrieves the list of restaurant infos
	 * associated with the provided object ids from parse.
	 * 
	 * @param restIds array of object ids for restaurant favorites
	 * @param callback Callback to listen for events
	 */
	public RestaurantInfoDownloader(String[] restIds, RestaurantInfoDownLoaderCallback callback) {
		if (restIds == null) {
			throw new NullPointerException(TAG + "Can't have null restaurant ids");
		}
		if (callback == null) {
			throw new NullPointerException(TAG + "Can't have null callback");
		}
		mCallback = callback;
		this.restIds = restIds;
		this.restName = null;
		this.userLoc = null;
	}
	
	/**
	 * Creates a RestaurantInfoDownloader that retrieves the list of restaurant infos
	 * associated with the provided ParseGeoPoint location object.
	 * 
	 * @param userLoc location of the current user
	 * @param callback Callback to listen for events
	 */
	public RestaurantInfoDownloader(ParseGeoPoint userLoc, 
			RestaurantInfoDownLoaderCallback callback) {
		if (userLoc == null) {
			throw new NullPointerException(TAG + "Can't have null location");
		}
		if (callback == null) {
			throw new NullPointerException(TAG + "Can't have null callback");
		}
		mCallback = callback;
		this.userLoc = userLoc;
		this.restIds = null;
		this.restName = null;
	}
	
	// Background process.
	@Override
	protected List<RestaurantInfo> doInBackground(CachePolicy... params) {
		if (params[0] == null) {
			params[0] = CachePolicy.NETWORK_ELSE_CACHE;
		}
		CachePolicy policy = params[0];

		try {
			if (this.restName != null) {
				return getFromName(policy);
			} else if (this.restIds != null) {
				return getFromIDs(policy);
			} else if (this.userLoc != null) {
				return getFromLocation(policy);
			}
		} catch (ParseException e) {
			// If any error case happened at all send the error back
			onProgressUpdate(e);
		}
		return null;
	}

	/**
	 * Gets the RestaurantInfo/s associated with name.
	 * @param policy Cache Policy to use to retrieve RestaurantInfo/s
	 * @return List of RestaurantInfos on success, otherwise null
	 * @throws ParseException for request failure
	 */
	private List<RestaurantInfo> getFromName(CachePolicy policy) throws ParseException { 
		ParseQuery query = new ParseQuery(RestaurantInfo.class.getSimpleName());
		query.whereEqualTo(RestaurantInfo.NAME, this.restName);
		query.setLimit(DineOnConstants.MAX_RESTAURANTS);
		query.setCachePolicy(policy);
		List<ParseObject> restaurantObjects = query.find();
		List<RestaurantInfo> restInfos = new ArrayList<RestaurantInfo>();
		for (ParseObject p : restaurantObjects) {
			try {
				restInfos.add(new RestaurantInfo(p));
			} catch (ParseException ex) {
				Log.d(TAG, "Problem creating object" + ex.getMessage());
			}
		}
		return restInfos;
	}
	
	/**
	 * Gets the RestaurantInfo/s associated with object ids of favs.
	 * @param policy Cache Policy to use to retrieve RestaurantInfo/s
	 * @return List of RestaurantInfos on success, otherwise null
	 * @throws ParseException for request failure
	 */
	private List<RestaurantInfo> getFromIDs(CachePolicy policy) throws ParseException { 
		ParseQuery query = new ParseQuery(RestaurantInfo.class.getSimpleName());
		query.whereContainedIn("objectId", Arrays.asList(this.restIds));
		query.setLimit(DineOnConstants.MAX_RESTAURANTS);
		query.setCachePolicy(policy);
		List<ParseObject> restaurantObjects = query.find();
		List<RestaurantInfo> restInfos = new ArrayList<RestaurantInfo>();
		for (ParseObject p : restaurantObjects) {
			try {
				restInfos.add(new RestaurantInfo(p));
			} catch (ParseException ex) {
				Log.d(TAG, "Problem creating object" + ex.getMessage());
			}
		}
		return restInfos;
	}

	/**
	 * Gets the RestaurantInfo/s associated with user location.
	 * @param policy Cache Policy to use to retrieve RestaurantInfo/s
	 * @return List of RestaurantInfos on success, otherwise null
	 * @throws ParseException for request failure
	 */
	private List<RestaurantInfo> getFromLocation(CachePolicy policy) throws ParseException { 
		ParseQuery query = new ParseQuery(RestaurantInfo.class.getSimpleName());
		query.whereWithinMiles(LocatableStorable.LOCATION, 
				this.userLoc, 
				DineOnConstants.MAX_RESTAURANT_DISTANCE);
		query.setLimit(DineOnConstants.MAX_RESTAURANTS);
		query.setCachePolicy(policy);
		List<ParseObject> restaurantObjects = query.find();
		List<RestaurantInfo> restInfos = new ArrayList<RestaurantInfo>();
		for (ParseObject p : restaurantObjects) {
			try {
				restInfos.add(new RestaurantInfo(p));
			} catch (ParseException ex) {
				Log.d(TAG, "Problem creating object" + ex.getMessage());
			}
		}
		return restInfos;
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
	 * Called when the List of restaurant infos has been successfully received.
	 * @param result List of RestaurantInfos retreived from Parse
	 */
	@Override
	protected void onPostExecute(List<RestaurantInfo> result) {
		if (result == null) {
			Log.e(TAG, "Unable to download restaurant infos.");
			// No need to call on fail.
			mCallback.onFailToDownLoadRestaurantInfos(parseExceptionMessage);
			return;
		}
		mCallback.onDownloadedRestaurantInfos(result);
	}

	/**
	 * Interface used by user application to interact with downloader.
	 * @author mtrathjen08
	 */
	public interface RestaurantInfoDownLoaderCallback {

		/**
		 * Notifies caller that the List of RestaurantInfos requested failed.
		 * @param message Description of what failed
		 */
		void onFailToDownLoadRestaurantInfos(String message);

		/**
		 * Notifies the caller that the List of RestaurantInfos was successfully retrieved.
		 * @param infos Restaurant Infos retrieved from Parse
		 */
		void onDownloadedRestaurantInfos(List<RestaurantInfo> infos);

	} 

}

