

package uw.cse.dineon.user.restaurantselection;

import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.restaurant.home.RestaurantHomeActivity;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseUser;

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

	private ParseUser /*Change to ParseUser*/ mUser;

	private String mCurRestaurant;
	
	private static final int MENU_ITEM_FILTER = 1234;

	//////////////////////////////////////////////////////////////////////
	////  Android specific 
	//////////////////////////////////////////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant_selection);

		mUser = ParseUser.getCurrentUser();
		Toast.makeText(this, "User: " + mUser.getUsername() + " logged in!", 
				Toast.LENGTH_SHORT).show();
		
		// Replace the Action bar title with a message letting the 
		// user know this is the restaurant selection page
		final ActionBar ACTION_BAR = getActionBar();
		if (ACTION_BAR != null) {
			ACTION_BAR.setTitle(R.string.actionbar_title_restaurant_selection);
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
		mCurRestaurant = restaurant;
		Toast.makeText(this, "Restaurant \"" + restaurant + "\" Selected", 
				Toast.LENGTH_SHORT).show();

		Intent i = new Intent(getApplicationContext(), RestaurantHomeActivity.class);	
		i.putExtra(RestaurantHomeActivity.EXTRA_RESTAURANT, mCurRestaurant);
		startActivity(i);
	}

	@Override
	public void onRestaurantFocusedOn(String restaurant) {
		// TODO Auto-generated method stub
		mCurRestaurant = restaurant;
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
	public String getCurrentRestaurant() {
		// TODO Auto-generated method stub
		return mCurRestaurant;
	}




}
