package uw.cse.dineon.user.restaurant.home;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.R;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 * @author 
 *
 */
public class MenuItemDetailActivity extends DineOnUserActivity implements 
MenuItemDetailFragment.MenuItemDetailListener {

	public static final String TAG = MenuItemDetailActivity.class.getSimpleName();
	
	public static final String EXTRA_MENUITEM_NAME = "menuitem";
	
	private MenuItem mItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		String itemName = null;
		if (extras != null && extras.containsKey(EXTRA_MENUITEM_NAME)) {
			itemName = extras.getString(EXTRA_MENUITEM_NAME);
		} else if (savedInstanceState != null 
				&& savedInstanceState.containsKey(EXTRA_MENUITEM_NAME)) {
			itemName = extras.getString(EXTRA_MENUITEM_NAME);
		}
		
		// Ugly way to find the item
		DiningSession session = mUser.getDiningSession();
		if (session != null) {
			for (Menu menu: session.getRestaurantInfo().getMenuList()) {
				for (MenuItem item : menu.getItems()) {
					if (item.getTitle().equals(itemName)) {
						mItem = item;
					}
				}
			}
		}
		
		if (mItem == null) {
			Log.e(TAG, "Unable to load menu item to show details");
			return;
		}
		
		setContentView(R.layout.activity_menuitem_detail);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(EXTRA_MENUITEM_NAME, mItem.getTitle());
	}

	@Override
	public MenuItem getMenuItem() {
		return mItem;
	}
}
