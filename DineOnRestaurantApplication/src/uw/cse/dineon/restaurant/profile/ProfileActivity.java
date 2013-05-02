package uw.cse.dineon.restaurant.profile;

import uw.cse.dineon.restaurant.DineOnRestaurantActivity;
import uw.cse.dineon.restaurant.R;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * Activity that allows the user (Restaurant) to access and alter their menu items and profile.
 * @author mhotan
 */
public class ProfileActivity extends DineOnRestaurantActivity implements TabListener {

	private static final String TAG = ProfileActivity.class.getSimpleName();
	
	/**
	 * Keeps track of last tab position.
	 */
	private int mLastTabPosition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	
		setContentView(R.layout.activity_restaurant_profile);
		
		// TODO Grab which action bar is selected
		mLastTabPosition = 0; // Let the tab be either the 0 or 1
		
		// Set the actiobar with associated tabs
		final ActionBar actionBar = getActionBar();
		if (actionBar != null) { // Support older builds
			actionBar.addTab(actionBar.newTab().
					setText(R.string.tab_actionbar_restaurant_profile).setTabListener(this));
			actionBar.addTab(actionBar.newTab().
					setText(R.string.tab_actionbar_restaurant_menuitems).setTabListener(this));
		}
	}

	/*
	 * Tab Listener to bring up the correct fragment
	 * */
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
//		Fragment fragment = new 
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Refresh the tab
	}


	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// As of May 1st cant think of anything to add here
	}
}
