package uw.cse.dineon.user.restaurant.home;

import java.util.List;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
		DineOnUser dou = DineOnUserApplication.getDineOnUser();
		DiningSession session = dou.getDiningSession();
		
		List<Menu> menus = null;
		if (session == null) {
			RestaurantInfo ofInterest =  DineOnUserApplication.getRestaurantOfInterest();
			if (ofInterest != null) {
				menus = ofInterest.getMenuList();
			}
		} else {
			menus = session.getRestaurantInfo().getMenuList();
		}
		
		if (menus != null) {
			for (Menu menu: menus) {
				for (MenuItem item : menu.getItems()) {
					if (item.getTitle().equals(itemName)) {
						mItem = item;
					}
				}
			}
		}
		
		if (mItem == null) {
			Toast.makeText(this, "Unable to load menu item to show details", 
					Toast.LENGTH_SHORT).show();
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
