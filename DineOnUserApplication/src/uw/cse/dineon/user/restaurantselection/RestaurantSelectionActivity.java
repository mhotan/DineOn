

package uw.cse.dineon.user.restaurantselection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uw.cse.dineon.library.LocatableStorable;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.restaurant.home.RestaurantHomeActivity;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * @author mhotan
 */
public class RestaurantSelectionActivity extends DineOnUserActivity implements 
RestaurantSelectionButtonsFragment.OnClickListener, // Listening for button actions
RestaurantListFragment.RestaurantListListener { //  Listening for List items

	private final String TAG = this.getClass().getSimpleName();

	public static final String EXTRA_USER = "USER";

	private static final int MENU_ITEM_FILTER = 1234;

	private List<RestaurantInfo> mRestaurants;

	private ProgressDialog mProgressDialog;

	private RestaurantInfo currentRestaurant;

	private RestaurantSelectionActivity thisActivity;


	//////////////////////////////////////////////////////////////////////
	////  Android specific 
	//////////////////////////////////////////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant_selection);

		this.thisActivity = this;

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
			onShowUserFavorites();
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
		return super.onCreateOptionsMenu(menu);
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
		ParseQuery query = new ParseQuery(RestaurantInfo.class.getSimpleName());
		query.whereEqualTo(RestaurantInfo.NAME, name);
		queryForRestaurants(query, "No restaurants match. Check spelling.");
	}

	@Override
	public void onShowNearbyRestaurants() {
		createProgressDialog();
		ParseQuery query = new ParseQuery(RestaurantInfo.class.getSimpleName());
		Location lastLoc = super.getLastKnownLocation();
		if (lastLoc != null) {
			query.whereWithinMiles(LocatableStorable.LOCATION, 
					new ParseGeoPoint(lastLoc.getLatitude(), lastLoc.getLongitude()), 
					DineOnConstants.MAX_RESTAURANT_DISTANCE);
			queryForRestaurants(query, 
					"There are no restaurants nearby. Your in the middle of nowhere.");
		} else {
			Toast.makeText(this, "You don't have location info stupid!", 
					Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Don't have current location info.");
			destroyProgressDialog();
		}
	}

	@Override
	public void onShowFriendsFavoriteRestaurants() {
		// TODO
		//createProgressDialog();
		//ParseQuery query = new ParseQuery(RestaurantInfo.class.getSimpleName());
	}

	@Override
	public void onShowUserFavorites() {
		createProgressDialog();
		ParseQuery query = new ParseQuery(RestaurantInfo.class.getSimpleName());
		String[] objIds = new String[DineOnUserApplication.getDineOnUser().getFavs().size()];
		List<RestaurantInfo> favs = DineOnUserApplication.getDineOnUser().getFavs();
		for (int i = 0; i < favs.size(); i++) {
			objIds[i] = favs.get(i).getObjId();
		}
		query.whereContainedIn("objectId", Arrays.asList(objIds));
		queryForRestaurants(query, "No restaurants in your favorites. Add some.");
	}

	/**
	 * Query for restaurants using attributes set and populate selection list.
	 * on return
	 * @param query parse query object to query restaurants.
	 * @param message message to display if no restaurant found.
	 */
	public void queryForRestaurants(ParseQuery query, final String message) {
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
		query.setLimit(DineOnConstants.MAX_RESTAURANTS); 
		query.findInBackground(getFindCallback(message));
	}

	@Override
	public RestaurantInfo getCurrentRestaurant() {
		return currentRestaurant;
	}

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

	/**
	 * Gets the FindCallback for when restaurants are found.
	 * @param message to show if no restaurants found
	 * @return the Callback object
	 */
	public FindCallback getFindCallback(final String message) {
		return new FindCallback() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					// Quickly notify the user if no restaurants are available
					if (objects.isEmpty()) {
						destroyProgressDialog();
						showNoRestaurantsDialog(message);
						return;
					}

					// Clear all the old restaurants because we got something new.
					mRestaurants.clear();

					// Each parse object represents one restaurant
					// Populate our list of restaurants with 
					for (ParseObject po: objects) {
						try {
							mRestaurants.add(new RestaurantInfo(po));
						} catch (ParseException e1) {
							Log.d(TAG, e1.getMessage());
						}
					}

					// Destroy the progress dialog.
					destroyProgressDialog();
					// notify the fragment of the change
					notifyFragment();
				} else { 
					destroyProgressDialog();
					showNoRestaurantsDialog("Problem getting restaurants:" + e.getMessage());
					Log.d(TAG, "No restaurants where found in the cloud.");
				}
			}

		};
	}
}
