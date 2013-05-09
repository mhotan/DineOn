package uw.cse.dineon.restaurant.profile;

import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.DineOnRestaurantActivity;
import uw.cse.dineon.restaurant.NotLoggedInFragment;
import uw.cse.dineon.restaurant.R;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;

/**
 * Activity that allows the user (Restaurant) to access and alter their menu items and profile.
 * @author mhotan
 */
public class ProfileActivity extends DineOnRestaurantActivity 
implements TabListener,
RestaurantInfoFragment.InfoChangeListener,
MenuItemsFragment.MenuItemListener {

	private static final String TAG = ProfileActivity.class.getSimpleName();

	/**
	 * Keeps track of last tab position.
	 * This lets us gain a reference to which tab needs to be presented
	 */
	private int mLastTabPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_restaurant_profile);

		// TODO Grab which action bar is selected
		mLastTabPosition = 0; // Let the tab be either the 0 or 1

		Fragment frag;
		if (isLoggedIn() || DineOnConstants.DEBUG) { // If logged in fill views appropriately
			// Set the actiobar with associated tabs
			final ActionBar actionBar = getActionBar();
			if (actionBar != null) { // Support older builds
				actionBar.addTab(actionBar.newTab().
						setText(R.string.tab_actionbar_restaurant_profile).setTabListener(this));
				actionBar.addTab(actionBar.newTab().
						setText(R.string.tab_actionbar_restaurant_menuitems).setTabListener(this));
			}

			// Obtain the most recently used Restaurant via intent or call
			// TODO Fix Fragment instantiation issues
//			frag = RestaurantInfoFragment.newInstance(new RestaurantInfo());
		} 
		else {
			Log.w(TAG, "User not logged in cant show profile");
			frag = new NotLoggedInFragment();
		}
		
		android.support.v4.app.FragmentTransaction ft = 
				getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
//		ft.add(R.id.container_profile_fragment, frag);
		ft.commit();
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		super.onPrepareOptionsMenu(menu);

		android.view.MenuItem itemProfile = menu.findItem(R.id.item_restaurant_profile);
		// Already at profile page so remove the button
		if (itemProfile != null) { // If exists
			itemProfile.setEnabled(false);
			itemProfile.setVisible(false);
		}

		return true;
	}

	/*
	 * Tab Listener to bring up the correct fragment
	 * */

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// Obtain a reference on which tab is being selected
		int pos = tab.getPosition();
		int diff = pos - mLastTabPosition;

		// Ignore ft because it is not support fragment transaction 

		// get the support fragment transactions
		android.support.v4.app.FragmentTransaction supFT = 
				getSupportFragmentManager().beginTransaction();
		
		RestaurantInfo info = getRestaurant().getInfo();
		
		Fragment frag = null;
		if (diff < 0) { // move to a tab that relatively left 

			frag = RestaurantInfoFragment.newInstance(info);

			// Assign the animation where the fragment slides
			// in from the right
			supFT.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		} 
		else if (diff > 0) { // move the tab relatively rights
			// TODO Correctly obtain the Restaurant

			frag = MenuItemsFragment.newInstance(info);

			// Assign the animation where the fragment slides
			// in from the 
			supFT.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
		}

		// Update the position
		mLastTabPosition = pos;

		if (frag != null) {
			supFT.replace(R.id.container_profile_fragment, frag);
			supFT.commit();
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Refresh the tab
	}


	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// As of May 1st cant think of anything to add here
	}

	//////////////////////////////////////////////////////
	//// Folliing are fragment call backs that signify user interaction
	//////////////////////////////////////////////////////

	@Override
	public void onMenuItemDeleted(MenuItem item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMenuItemAdded(MenuItem item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMenuItemModified(MenuItem item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRestaurantInfoUpdate(RestaurantInfo rest) {
		// TODO Auto-generated method stub

	}
}
