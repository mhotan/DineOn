

package uw.cse.dineon.user.restaurantselection;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.RestaurantInfoDownloader;
import uw.cse.dineon.user.RestaurantInfoDownloader.RestaurantInfoDownLoaderCallback;
import uw.cse.dineon.user.restaurant.home.RestaurantHomeActivity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseGeoPoint;
import com.parse.ParseQuery.CachePolicy;

/**
 * Activity to handle restaurant selection.
 * @author mhotan
 */
public class RestaurantSelectionActivity extends DineOnUserActivity implements 
RestaurantSelectionButtonsFragment.OnClickListener, // Listening for button actions
RestaurantListFragment.RestaurantListListener, //  Listening for List items
RestaurantInfoDownLoaderCallback { // Listen for restaurantinfos

	private final String TAG = this.getClass().getSimpleName();

	public static final String EXTRA_USER = "USER";

	private static final int MENU_ITEM_FILTER = 1234;

	private List<RestaurantInfo> mRestaurants;

	private ProgressDialog mProgressDialog;

	private RestaurantInfo currentRestaurant;
	
	private AlertDialog mAd;


	//////////////////////////////////////////////////////////////////////
	////  Android specific 
	//////////////////////////////////////////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant_selection);
		
		// Replace the Action bar title with a message letting the 
		// user know this is the restaurant selection page
		final ActionBar ACTION_BAR = getActionBar();
		if (ACTION_BAR != null) {
			ACTION_BAR.setTitle(R.string.actionbar_title_restaurant_selection);
		}

		// Clear out old restaurant of interest
		DineOnUserApplication.setRestaurantOfInterest(null);

		mRestaurants = DineOnUserApplication.getRestaurantList();
		// Free up the static memory
		DineOnUserApplication.clearResaurantList();
		if (mRestaurants == null) {
			// This activity was started for the first time.
			mRestaurants = new ArrayList<RestaurantInfo>();
			onShowNearbyRestaurants();
		}
	}

	@Override 
	protected void onSaveInstanceState(Bundle outState) {
		DineOnUserApplication.saveRestaurantList(mRestaurants);
		super.onSaveInstanceState(outState);
	}


	/**
	 * Add the list of restaurant infos to the list.
	 */
	public void addListOfRestaurantInfos() {
		addRestaurantInfos(this.mRestaurants);
		this.mRestaurants.clear();
	}

	/**
	 * Overrides the traditional on back and causes a alert to appear
	 * asking the user to confirm to avoid accidental logout/quit.
	 */
	@Override
	public void onBackPressed() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setMessage(R.string.string_log_out_alert);
		adb.setCancelable(true);
		final RestaurantSelectionActivity RSA = this;
		adb.setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				RSA.destroyLogoutAlert();
				RSA.startLoginActivity();
				
				
			}
			
		});
		
		adb.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				RSA.destroyLogoutAlert();
			}
			
		});
		this.mAd = adb.show();
	}
	
	/**
	 * Returns the alert dialog that is generated on back pressed.
	 * Not thread safe. Mainly for testing.
	 * 
	 * @return The instance of the alert dialog, null otherwise.
	 */
	public AlertDialog getLogoutAlertDialog() {
		return this.mAd;
	}
	
	/**
	 * Gets rid of alert box.
	 */
	public void destroyLogoutAlert() {
		if(this.mAd != null && this.mAd.isShowing()) {
			this.mAd.cancel();
		}
	}
	
	/**
	 * Add a new restaurant info object to the restaurant list.
	 * @param infos RestaurantInfo object to add to list.
	 */
	public void addRestaurantInfos(List<RestaurantInfo> infos) {
		// Update our UI for the new restaurant info
		FragmentManager fm = getSupportFragmentManager();
		RestaurantListFragment frag = 
				(RestaurantListFragment) fm.findFragmentById(R.id.restaurantList);
		// If fragment is in foreground add it to list
		if (frag != null && frag.isInLayout()) {
			frag.addRestaurantInfos(infos);
		}
	}

	/**
	 * Notifies the fragment state change.
	 */
	public void notifyFragment() {
		FragmentManager fm = getSupportFragmentManager();
		RestaurantListFragment frag = 
				(RestaurantListFragment) fm.findFragmentById(R.id.restaurantList);
		frag.notifyStateChange();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ITEM_FILTER, 0, R.string.option_filter);
		boolean temp = super.onCreateOptionsMenu(menu);
		this.disableMenuItem(menu, R.id.option_check_in);
		return temp;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean temp = super.onPrepareOptionsMenu(menu);
		this.disableMenuItem(menu, R.id.option_check_in);
		return temp;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case MENU_ITEM_FILTER:
//			// TODO
//			break;
//		default:
//			break;
//		}
		return super.onOptionsItemSelected(item);
	}

	//////////////////////////////////////////////////////////////////////
	////   Call backs for Fragment methods
	//////////////////////////////////////////////////////////////////////

	@Override
	public void onRestaurantSelected(RestaurantInfo restaurant) {
		// Continue on to next activity
		Intent i = new Intent(this, RestaurantHomeActivity.class);
		// send over the restaurantInfo
		DineOnUserApplication.setRestaurantOfInterest(restaurant);
		startActivity(i);
	}

	//	/**
	//	 * @param dsession DiningSession to change to
	//	 */
	//	public void diningSessionChangeActivity(DiningSession dsession) {
	//		Intent i = new Intent(thisActivity, RestaurantHomeActivity.class);
	//		i.putExtra(DineOnConstants.KEY_DININGSESSION, dsession);
	//		startActivity(i);
	//	}

	@Override
	protected void onSearch(String query) {
		onSearchForRestaurantByName(query);
	}

	/**
	 * Search for a restaurant by name.
	 * @param name name of restaurant
	 */
	public void onSearchForRestaurantByName(String name) {
		createProgressDialog();
//		ParseQuery query = new ParseQuery(RestaurantInfo.class.getSimpleName());
//		query.whereEqualTo(RestaurantInfo.NAME, name);
//		queryForRestaurants(query, "No restaurants match. Please check spelling.");
		
		RestaurantInfoDownloader sessionDownloader = new RestaurantInfoDownloader(name, this);
		sessionDownloader.execute(CachePolicy.NETWORK_ELSE_CACHE);
	}

	@Override
	public void onShowNearbyRestaurants() {
		createProgressDialog();
//		ParseQuery query = new ParseQuery(RestaurantInfo.class.getSimpleName());
		Location lastLoc = super.getLastKnownLocation();
		if (lastLoc != null) {
//			query.whereWithinMiles(LocatableStorable.LOCATION, 
//					new ParseGeoPoint(lastLoc.getLatitude(), lastLoc.getLongitude()), 
//					DineOnConstants.MAX_RESTAURANT_DISTANCE);
//			queryForRestaurants(query, 
//					"There are no restaurants nearby.");
			
			RestaurantInfoDownloader sessionDownloader = 
					new RestaurantInfoDownloader(new ParseGeoPoint(lastLoc.getLatitude(), 
							lastLoc.getLongitude()), this);
			sessionDownloader.execute(CachePolicy.NETWORK_ELSE_CACHE);
			
		} else {
			Toast.makeText(this, "Your device does not support Location finding", 
					Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Don't have current location info.");
			destroyProgressDialog();
		}
	}

	@Override
	public void onShowFriendsFavoriteRestaurants() {
		// TODO
		onShowUserFavorites();
	}

	@Override
	public void onShowUserFavorites() {
		createProgressDialog();
//		ParseQuery query = new ParseQuery(RestaurantInfo.class.getSimpleName());
		String[] objIds = new String[DineOnUserApplication.getDineOnUser().getFavs().size()];
		List<RestaurantInfo> favs = DineOnUserApplication.getDineOnUser().getFavs();
		for (int i = 0; i < favs.size(); i++) {
			objIds[i] = favs.get(i).getObjId();
		}
//		query.whereContainedIn("objectId", Arrays.asList(objIds));
//		queryForRestaurants(query, "No restaurants in your favorites. Please do a search.");
		
		RestaurantInfoDownloader sessionDownloader = 
				new RestaurantInfoDownloader(objIds, this);
		sessionDownloader.execute(CachePolicy.NETWORK_ELSE_CACHE);
	}

//	/**
//	 * Query for restaurants using attributes set and populate selection list.
//	 * on return
//	 * @param query parse query object to query restaurants.
//	 * @param message message to display if no restaurant found.
//	 */
//	public void queryForRestaurants(ParseQuery query, final String message) {
//		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
//		query.setLimit(DineOnConstants.MAX_RESTAURANTS); 
//		query.findInBackground(getFindCallback(message));
//	}

//	@Override
//	public RestaurantInfo getCurrentRestaurant() {
//		return currentRestaurant;
//	}

	@Override
	public List<RestaurantInfo> getRestaurants() {
		return mRestaurants;
	}

	/**
	 * Instantiates a new progress dialog and shows it on the screen.
	 */
	public void createProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			return;
		}
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Getting restaurants.");
		mProgressDialog.setMessage("Searching...");       
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();
	}

	/**
	 * Hides the progress dialog if there is one.
	 */
	public void destroyProgressDialog() {
		if(mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	/**
	 * Show bad input alert message for logging in.
	 * @param message message to show
	 */
	public void showNoRestaurantsDialog(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
//
//	/**
//	 * Gets the FindCallback for when restaurants are found.
//	 * @param message to show if no restaurants found
//	 * @return the Callback object
//	 */
//	public FindCallback getFindCallback(final String message) {
//		return new FindCallback() {
//
//			@Override
//			public void done(List<ParseObject> objects, ParseException e) {
//				if (e == null) {
//					
//				} else { 
//					destroyProgressDialog();
//					showNoRestaurantsDialog("Problem getting restaurants:" + e.getMessage());
//					Log.d(TAG, "No restaurants where found in the cloud.");
//				}
//			}
//
//		};
//	}

	@Override
	public void onFailToDownLoadRestaurantInfos(String message) {
		// TODO Auto-generated method stub
		destroyProgressDialog();
		showNoRestaurantsDialog("Problem getting restaurants:" + message);
		Log.d(TAG, "No restaurants where found in the cloud.");
	}

	@Override
	public void onDownloadedRestaurantInfos(List<RestaurantInfo> infos) {
		// Quickly notify the user if no restaurants are available
		if (infos.isEmpty()) {
			destroyProgressDialog();
			showNoRestaurantsDialog("No restaurant were found.");
			return;
		}

		// Clear all the old restaurants because we got something new.
		mRestaurants.clear();
		
		// Put the restaurant with a current dining session at top of the list
		if (mUser.getDiningSession() != null) {
			mRestaurants.add(mUser.getDiningSession().getRestaurantInfo());
		}

		// Each parse object represents one restaurant
		// Populate our list of restaurants with 
		for (RestaurantInfo info: infos) {
			mRestaurants.add(info);
		}

		// Destroy the progress dialog.
		destroyProgressDialog();
		// notify the fragment of the change
		notifyFragment();
	}
}
