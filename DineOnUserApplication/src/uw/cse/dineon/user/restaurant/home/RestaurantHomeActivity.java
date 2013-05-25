package uw.cse.dineon.user.restaurant.home;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.restaurantselection.RestaurantInfoFragment;
import android.content.Intent;
import android.os.Bundle;

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
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		android.view.MenuItem item = menu.findItem(R.id.option_bill);
		item.setEnabled(true);
		item.setVisible(true);
		
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		
		if (extras != null && extras.containsKey(DineOnConstants.KEY_RESTAURANTINFO)) {
			this.mRestaurant = extras.getParcelable(DineOnConstants.KEY_RESTAURANTINFO);
		} else if (savedInstanceState != null 
				&& savedInstanceState.containsKey(DineOnConstants.KEY_RESTAURANTINFO)) {
			this.mRestaurant = savedInstanceState.getParcelable(DineOnConstants.KEY_RESTAURANTINFO);
		} else if (DineOnUserApplication.getCurrentDiningSession() != null) {
			this.mDiningSession = DineOnUserApplication.getCurrentDiningSession();
			this.mRestaurant = this.mDiningSession.getRestaurantInfo();
		}
		
		setContentView(R.layout.activity_restaurant_home);
	}

	//////////////////////////////////////////////////////////////////////
	////   Call backs for Fragment methods
	//////////////////////////////////////////////////////////////////////


	@Override
	public void onMakeReservation(String reservation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMenuItemFocusedOn(MenuItem menuItem) {
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
	public void onViewCurrentBill() {
		// TODO Auto-generated method stub
		// TODO Take to Current Bill screen
		
	}

	@Override
	public RestaurantInfo getCurrentRestaurant() {
		return this.mRestaurant;
	}

	@Override
	public void setCurrentRestaurant(RestaurantInfo r) {
		this.mRestaurant = r;
	}
}
