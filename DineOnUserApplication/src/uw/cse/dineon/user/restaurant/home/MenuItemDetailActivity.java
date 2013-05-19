package uw.cse.dineon.user.restaurant.home;

import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.R;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * 
 * @author 
 *
 */
public class MenuItemDetailActivity extends DineOnUserActivity implements 
MenuItemDetailFragment.MenuItemDetailListener {

	public static final String TAG = MenuItemDetailActivity.class.getSimpleName();
	
	public static final String EXTRA_MENUITEM = "menuitem";
	
	private MenuItem mItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Need to check if Activity has been switched to landscape mode
		// If yes, finished and go back to the start Activity
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			finish();
			return;
		}
		setContentView(R.layout.activity_menuitem_detail);
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(EXTRA_MENUITEM)) {
			this.mItem = extras.getParcelable(EXTRA_MENUITEM);
		}
	}

	@Override
	public MenuItem getMenuItem() {
		// TODO Auto-generated method stub
		return this.mItem;
	}
}
