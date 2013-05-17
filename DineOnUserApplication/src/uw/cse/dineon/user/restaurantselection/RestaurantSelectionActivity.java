

package uw.cse.dineon.user.restaurantselection;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.R;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
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
		
		createProgressDialog();
		
		mRestaurants = new ArrayList<RestaurantInfo>();
		
		ParseQuery query = new ParseQuery(RestaurantInfo.class.getSimpleName());
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		// TODO add attributes as filters are used
		// TODO Limit will need to change later
		query.setLimit(6); 
		query.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					
					for (int i = 0; i < objects.size(); i++) {
						try {
							ParseObject p = objects.get(i);
							RestaurantInfo r = new RestaurantInfo(p);
							mRestaurants.add(r);
							addRestaurantInfo(r);
						} catch (ParseException e1) {
							Log.d(TAG, e1.getMessage());
						}
					}
					destroyProgressDialog();
					if (objects.size() == 0)
						showNoRestaurantsDialog("No Restaurants found.");
				} else { 
					destroyProgressDialog();
					showNoRestaurantsDialog(e.getMessage());
					Log.d(TAG, "No restaurants found: " + e.getMessage());
				}
			}
			
		});
	}
	
	/**
	 * Add a new restaurant info object to the restaurant list.
	 * 
	 * @param info RestaurantInfo object to add to list.
	 */
	public void addRestaurantInfo(RestaurantInfo info) {
		// Update our UI for the new restaurant info
		FragmentManager fm = getSupportFragmentManager();
		RestaurantListFragment frag = 
				(RestaurantListFragment) fm.findFragmentById(R.id.restaurantList);
		// If fragment is in foreground add it to list
		if (frag != null && frag.isInLayout()) {
			frag.addRestaurantInfo(info);
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

	// Method inherited from Restaurant list listener 
	// for use when user selects a restaurant to focus on
	@Override
	public void onRestaurantSelected(String restaurant) {
		// TODO Auto-generated method stub
		// Continue on to next activity
		
		Toast.makeText(this, "Restaurant \"" + restaurant + "\" Selected", 
				Toast.LENGTH_SHORT).show();

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


	@Override
	public void onShowNearbyRestaurants() {
		// TODO Implement by communicating with the list fragment 
	}

	@Override
	public void onShowFriendsFavoriteRestaurants() {
		// TODO Implement by communicating with the list fragment 

	}

	@Override
	public void onShowUserFavorites() {
		// TODO Implement by communicating with the list fragment 

	}

	@Override
	public void onMakeReservation(String reservation) {
		// TODO Auto-generated method stub

	}

	@Override
	public RestaurantInfo getCurrentRestaurant() {
		// TODO Auto-generated method stub
		return null;
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
		mProgressDialog.setTitle("Getting Restaurants");
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
	 * Indicate that no restaurants were returned.
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
