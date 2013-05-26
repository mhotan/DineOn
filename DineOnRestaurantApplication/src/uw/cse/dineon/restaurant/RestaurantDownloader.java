package uw.cse.dineon.restaurant;

import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;

/**
 * Helper class for downloading Restaurants.  This helper class
 * will cover two common cases.
 * 
 * 1. Given a ParseUser that already has a restaurant association,
 * 		find and return that restaurant.
 * 
 * 2. Given a Restaurant Parse object ID download the restaurant.
 * Given a Parse User that is associated with that restaurant
 * 
 * 3. To allow developers to have User and Restaurant accounts if 
 * 		a current ParseUser exists but has no restaurant Account then
 * 		create a new account for that user.
 * 
 * Constructers explicitly 
 * 
 * @author mhotan
 */
public class RestaurantDownloader extends AsyncTask<CachePolicy, ParseException, Restaurant> {

	private static final String TAG = RestaurantDownloader.class.getSimpleName();

	/**
	 * ParseUser associated to this restaurant.
	 */
	private final ParseUser mParseUser;

	/**
	 * Call back to listen for download events.
	 */
	private final RestaurantDownLoaderCallback mCallback;

	/**
	 * The Restaurant ID of the Restaurant to find.
	 */
	private final String mRestaurantID;

	private String parseExceptionMessage;

	/**
	 * Creates a Restaurant Downloader that explicitly looks for a Restaurant
	 * for this ParseUser. 
	 * 
	 * The ParseUser argument must have all the correct attributes fille already.
	 * 
	 * @param user ParseUser to use to download the restaurant
	 * @param callback Callback to listen for events
	 */
	public RestaurantDownloader(ParseUser user, RestaurantDownLoaderCallback callback) {
		if (user == null) {
			throw new NullPointerException(TAG + "Can't have null user");
		}
		if (callback == null) {
			throw new NullPointerException(TAG + "Can't have null callback");
		}
		mParseUser = user;
		mCallback = callback;
		mRestaurantID = null;
	}

	/**
	 * Creates a Restaurant Downloader that explicitly looks for a Restaurant
	 * by object id.
	 * @param id ParseUser to use to download the restaurant 
	 * @param callback Callback to listen for events
	 */
	public RestaurantDownloader(String id, RestaurantDownLoaderCallback callback) {
		if (id == null) {
			throw new NullPointerException(TAG + "Can't have null user");
		}
		if (callback == null) {
			throw new NullPointerException(TAG + "Can't have null callback");
		}
		mRestaurantID = id;
		mCallback = callback;
		mParseUser = null;
	}

	// Background process.
	@Override
	protected Restaurant doInBackground(CachePolicy... params) {
		if (params[0] == null) {
			params[0] = CachePolicy.NETWORK_ELSE_CACHE;
		}
		CachePolicy policy = params[0];

		try {
			// Download by Parse User
			if (mParseUser != null) {
				mParseUser.fetchIfNeeded();
				return getFromUser(policy);
			} else {
				return getFromID(policy);
			}
		} catch (ParseException e) {
			// If any error case happened at all send the error back
			onProgressUpdate(e);
		}
		return null;
	}

	/**
	 * Gets the Restaurant assoiated with ID.
	 * @param policy Cache Policy to user to attempt to download Restaurant
	 * @return Restaurant on success nothing other wise
	 * @throws ParseException For any error that occured
	 */
	private Restaurant getFromID(CachePolicy policy) throws ParseException { 
		ParseQuery query = new ParseQuery(Restaurant.class.getSimpleName());
		query.setCachePolicy(policy);
		ParseObject restObject = query.get(mRestaurantID);
		return new Restaurant(restObject);
	}

	/**
	 * Helper method that retrieves an unique restaurant instance
	 * for a particular user.
	 * 
	 * This is ease of reading
	 * 
	 * @param policy Cache Policy to user to attempt to download Restaurant
	 * @return Restaurant on success, null otherwise
	 * @throws ParseException For an error occured while communicating with parse
	 */
	private Restaurant getFromUser(CachePolicy policy) throws ParseException {
		// Here we already have a Parse User
		// We still don't know if we have a restaurant for this user.
		if (mParseUser.isAuthenticated()) {
			// Now find the restaurant via ParseQuery
			ParseQuery inner = new ParseQuery(RestaurantInfo.class.getSimpleName());
			inner.setCachePolicy(policy);
			inner.whereEqualTo(RestaurantInfo.PARSEUSER, mParseUser);
			ParseQuery query = new ParseQuery(Restaurant.class.getSimpleName());
			query.whereMatchesQuery(Restaurant.INFO, inner);
			query.setCachePolicy(policy);
			ParseObject restaurantObject = null;

			// Failed to find restaurant.
			try {
				restaurantObject = query.getFirst();
			} catch (ParseException e) {
				restaurantObject = null;
			}

			// Couldn't find a restaurant for that user
			if (restaurantObject == null) {
				// Here a Parse user exist but they havent created a restaurant account yet
				// If not in Debug mode then notify the User 
				// that a restaurant 
				onProgressUpdate(new ParseException(
						"Invalid Account. We have you in our database" 
								+ " but we need you need to create a Restaurant.",
								new Throwable()));
				return null;
			} // Found a restaurant for that user
			else {
				return new Restaurant(restaurantObject);
			}
		} else {
			// We have a completely new User
			mParseUser.signUp();
			// If this fails this throws an exception
			Restaurant newRest = new Restaurant(mParseUser);
			newRest.saveOnCurrentThread();
			return newRest;
		}
	}

	// This is only call when there is an error
	@Override
	protected void onProgressUpdate(ParseException... pairs) {
		ParseException exception = pairs[0];
		if (exception == null) {
			parseExceptionMessage = "Unknown Error";
			return;
		}
		parseExceptionMessage = exception.getMessage();
	}

	@Override
	protected void onPostExecute(Restaurant result) {
		if (result == null) {
			Log.e(TAG, "Unable to download restaurant.");
			// No need to call on fail.
			mCallback.onFailToDownLoadRestaurant(parseExceptionMessage);
			return;
		}
		mCallback.onDownloadedRestaurant(result);
	}

	/**
	 * Interface that allows more specific communication.
	 * @author mhotan
	 */
	public interface RestaurantDownLoaderCallback {

		/**
		 * Notifies the Main thread handler that the download failed.
		 * @param message Description of what failed
		 */
		void onFailToDownLoadRestaurant(String message);

		/**
		 * Notifies the Main thread handler that the downlaod succeeded.
		 * @param rest Result restaurant.
		 */
		void onDownloadedRestaurant(Restaurant rest);

	} 

}
