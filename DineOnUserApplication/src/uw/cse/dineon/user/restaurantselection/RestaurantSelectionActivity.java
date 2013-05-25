

package uw.cse.dineon.user.restaurantselection;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.LocatableStorable;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.restaurant.home.RestaurantHomeActivity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * TODO finish.
 * @author mhotan
 */
public class RestaurantSelectionActivity extends DineOnUserActivity implements 
RestaurantSelectionButtonsFragment.OnClickListener, // Listening for button actions
RestaurantListFragment.RestaurantListListener, //  Listening for List items
RestaurantInfoFragment.RestaurantInfoListener {
	
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
		
		mRestaurants = new ArrayList<RestaurantInfo>();
		
		// TODO for now get all restaurants
		ParseQuery query = new ParseQuery(RestaurantInfo.class.getSimpleName());
		queryForRestaurants(query);
	}
	
	/**
	 * Add the list of restaurant infos to the list.
	 */
	public void addListOfRestaurantInfos() {
		addRestaurantInfos(this.mRestaurants);
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ITEM_FILTER, 0, R.string.option_filter);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_FILTER:
			// TODO
			break;
		default:
			break;
		}
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
		i.putExtra(DineOnConstants.KEY_RESTAURANTINFO, restaurant);
		startActivity(i);
	}
	
	/**
	 * @param dsession DiningSession to change to
	 */
	public void diningSessionChangeActivity(DiningSession dsession) {
		Intent i = new Intent(thisActivity, RestaurantHomeActivity.class);
		i.putExtra(DineOnConstants.KEY_DININGSESSION, dsession);
		startActivity(i);
	}

	@Override
	public void onRestaurantFocusedOn(RestaurantInfo restaurant) {
		// TODO Auto-generated method stub
		
		FragmentManager fm = getSupportFragmentManager();
		RestaurantInfoFragment frag = 
				(RestaurantInfoFragment) fm.findFragmentById(R.id.restaurantInfo);
		// If the fragment already exists then just update its value
		if (frag != null && frag.isInLayout()) {
			frag.setRestaurantForDisplay(restaurant);
		} else {
			Intent i = new Intent(getApplicationContext(), RestaurantInfoActivity.class);	
			i.putExtra(RestaurantInfoActivity.EXTRA_RESTAURANT, restaurant);
			startActivity(i);
		}
	}

	/**
	 * Search for a restaurant by name.
	 * @param name name of restaurant
	 */
	public void onSearchForRestaurantByName(String name) {
		createProgressDialog();
		ParseQuery query = new ParseQuery(RestaurantInfo.class.getSimpleName());
		query.whereEqualTo(RestaurantInfo.NAME, name);
		queryForRestaurants(query);
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
			queryForRestaurants(query);
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
		// TODO
		//createProgressDialog();
		//ParseQuery query = new ParseQuery(RestaurantInfo.class.getSimpleName());
	}
	
	/**
	 * Query for restaurants using attributes set and populate selection list.
	 * on return
	 * @param query parse query object to query restaurants.
	 */
	public void queryForRestaurants(ParseQuery query) {
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
		query.setLimit(DineOnConstants.MAX_RESTAURANTS); 
		query.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					
					for (int i = 0; i < objects.size(); i++) {
						try {
							ParseObject p = objects.get(i);
							RestaurantInfo r = new RestaurantInfo(p);
							mRestaurants.add(r);
						} catch (ParseException e1) {
							Log.d(TAG, e1.getMessage());
						}
					}
					destroyProgressDialog();
					if (objects.size() == 0) {
						showNoRestaurantsDialog("Couldn't get restaurants");
					} else {
						addListOfRestaurantInfos();
					}
				} else { 
					destroyProgressDialog();
					showNoRestaurantsDialog("Problem getting restaurants:" + e.getMessage());
					Log.d(TAG, "No restaurants where found in the cloud.");
				}
			}
			
		});
	}

	@Override
	public void onMakeReservation(String reservation) {
		// TODO Auto-generated method stub

	}

	@Override
	public RestaurantInfo getCurrentRestaurant() {
		return currentRestaurant;
	}
	
	@Override
	public void setCurrentRestaurant(RestaurantInfo r) {
		currentRestaurant = r;
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
		mProgressDialog.setTitle("Loading your restaurants.");
		mProgressDialog.setMessage("Loading...");       
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
		AlertDialog.Builder b = new Builder(this);
		b.setTitle("Couldn't find any restaurants.");
		b.setMessage(message);
		b.setCancelable(true);
		b.setPositiveButton("Try Again", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).show();
	}
}
