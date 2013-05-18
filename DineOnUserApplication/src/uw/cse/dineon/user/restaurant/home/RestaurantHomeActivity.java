package uw.cse.dineon.user.restaurant.home;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.restaurantselection.RestaurantInfoFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * TODO finish.
 * @author mhotan
 */
public class RestaurantHomeActivity extends DineOnUserActivity
// Implement all the fragments callbacks
implements SubMenuFragment.MenuItemListListener, 
RestaurantInfoFragment.RestaurantInfoListener,
RestaurantHomeMainFragment.ReferenceDataListener {

	private final String TAG = this.getClass().getSimpleName();

	public static final String EXTRA_RESTAURANT = "restaurant"; 

	private RestaurantInfo mRestaurant;
	
	private DiningSession mDiningSession;

	//////////////////////////////////////////////////////////////////////
	////  Android specific 
	//////////////////////////////////////////////////////////////////////

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuItem item = menu.findItem(R.id.option_bill);
		item.setEnabled(true);
		item.setVisible(true);
		
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		
		// Attempt to get the restaurant name
		//ArrayList<Parcelable> extras = 
		//		getIntent().getParcelableArrayListExtra(DineOnConstants.DINING_SESSION);
		//if (extras != null) {
		//	DiningSession mDiningSession = (DiningSession) extras.get(0);
		//}
		
		if (extras != null) {
			if (extras.containsKey(DineOnConstants.KEY_DININGSESSION)) {
				mDiningSession = extras.getParcelable(DineOnConstants.KEY_DININGSESSION);
				mRestaurant = mDiningSession.getRestaurantInfo();
			} else if (extras.containsKey(DineOnConstants.KEY_RESTAURANTINFO)) {
				mRestaurant = extras.getParcelable(DineOnConstants.KEY_RESTAURANTINFO);
			}
		} else if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(DineOnConstants.KEY_DININGSESSION)) {
				mDiningSession = savedInstanceState.getParcelable(DineOnConstants.KEY_DININGSESSION);
				mRestaurant = mDiningSession.getRestaurantInfo();
			} else if (savedInstanceState.containsKey(DineOnConstants.KEY_RESTAURANTINFO)) {
				mRestaurant = savedInstanceState.getParcelable(DineOnConstants.KEY_RESTAURANTINFO);
			}
		}

		setContentView(R.layout.activity_restaurant_home);

		// TODO FIX: Extract the value of the restaurant
		// Pull out all its menus and information

		// Then Replace fragments as appropriately
	}

	//////////////////////////////////////////////////////////////////////
	////   Call backs for Fragment methods
	//////////////////////////////////////////////////////////////////////


	@Override
	public void onMakeReservation(String reservation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMenuItemFocusedOn(String menuItem) {
		// Attempt to find if the fragment exists
		// If it does update its contents
		// TODO Implement the ability to switch inbetween fragments
		MenuItemDetailFragment frag = (MenuItemDetailFragment) 
				getSupportFragmentManager().findFragmentById(R.id.fragment_menu_info_detail);
		if (frag != null && frag.isInLayout()) {
			frag.setMenuItem(menuItem);
		} else {
			// If it doesn't start a new activity 
			Intent i = new Intent(getApplicationContext(), MenuItemDetailActivity.class);
			i.putExtra(MenuItemDetailActivity.EXTRA_MENUITEM, menuItem);
			startActivity(i);
		}
	}

	@Override
	public void onRestaurantInfoRequested() {
		// TODO Auto-generated method stub
		// TODO Replace MenuItemDetailFragment with Restaurant Info Fragment
	}


	@Override
	public void onMenuItemIncremented(String menuItem) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMenuItemDecremented(String menuItem) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onViewCurrentBill() {
		// TODO Auto-generated method stub
		// TODO Take to Current Bill screen
	}

	@Override
	public RestaurantInfo getCurrentRestaurant() {
		// TODO Auto-generated method stub
		return mRestaurant;
	}

	@Override
	public void setCurrentRestaurant(RestaurantInfo r) {
		// TODO Auto-generated method stub
		mRestaurant = r;
	}

}
