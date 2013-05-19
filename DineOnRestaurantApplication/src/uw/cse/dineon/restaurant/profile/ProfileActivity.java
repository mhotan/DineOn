package uw.cse.dineon.restaurant.profile;

import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.DineOnRestaurantActivity;
import uw.cse.dineon.restaurant.NotLoggedInFragment;
import uw.cse.dineon.restaurant.R;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * Activity that allows the user (Restaurant) to access and alter their menu
 * items and profile.
 * 
 * @author mhotan
 */
public class ProfileActivity extends DineOnRestaurantActivity implements
		TabListener, RestaurantInfoFragment.InfoChangeListener,
		MenuItemsFragment.MenuItemListener {

	private static final String TAG = ProfileActivity.class.getSimpleName();

	/**
	 * Keeps track of last tab position. This lets us gain a reference to which
	 * tab needs to be presented
	 */
	private int mLastTabPosition;

	public static final String LAST_FRAG_TAG = "LAST_FRAG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setContentView(R.layout.activity_restaurant_profile);

		// TODO Grab which action bar is selected
		mLastTabPosition = 1; // Let the tab be either the 0 or 1

		/*
		 * android.support.v4.app.FragmentTransaction ft =
		 * getSupportFragmentManager().beginTransaction();
		 * ft.setCustomAnimations(android.R.anim.fade_in,
		 * android.R.anim.fade_out); //ft.add(R.id.container_profile_fragment,
		 * frag); ft.commit();
		 */
		Fragment frag;
		if (isLoggedIn()) {
			// If logged in fill views appropriately
			// Set the actionbar with associated tabs
			ActionBar ab = getActionBar();
			ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			ab.setTitle(getRestaurant().getName());
			ab.setDisplayShowTitleEnabled(true);
			if (ab != null) { // Support older builds
				ab.addTab(ab.newTab()
						.setText(R.string.tab_actionbar_restaurant_profile)
						.setTabListener(this));
				ab.addTab(ab.newTab()
						.setText(R.string.tab_actionbar_restaurant_menuitems)
						.setTabListener(this));
			}

			// Obtain the most recently used Restaurant via intent or call
			// TODO Fix Fragment instantiation issues
			// frag = RestaurantInfoFragment.newInstance(new RestaurantInfo());
		} else {
			Log.w(TAG, "User not logged in cant show profile");
			frag = new NotLoggedInFragment();
		}

	}

	@Override
	public void updateUI() {

		super.updateUI();
		/*
		 * Fragment frag; android.support.v4.app.FragmentTransaction ft =
		 * getSupportFragmentManager() .beginTransaction();
		 * //ft.setCustomAnimations(android.R.anim.fade_in,
		 * android.R.anim.fade_out); frag =
		 * MenuItemsFragment.newInstance(getRestaurant().getInfo());
		 * ft.replace(android.R.id.content, frag); ft.commit();
		 */

		
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		android.view.MenuItem itemProfile = menu
				.findItem(R.id.item_restaurant_profile);
		// Already at profile page so remove the button
		if (itemProfile != null) { // If exists
			itemProfile.setEnabled(false);
			itemProfile.setVisible(false);
		}

		return true;
	}

	/*
	 * Tab Listener to bring up the correct fragment
	 */

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// Obtain a reference on which tab is being selected
		Log.v(TAG, "Tab selected!");
		int pos = tab.getPosition();
		int diff = pos - mLastTabPosition;

		// Ignore ft because it is not support fragment transaction
		android.support.v4.app.FragmentTransaction supFT = getSupportFragmentManager()
				.beginTransaction();

		Restaurant rest = getRestaurant();
		RestaurantInfo info = rest.getInfo();
		assert (info != null);

		Fragment frag = null;
		if (diff < 0) { // move to a tab that relatively left

			frag = RestaurantInfoFragment.newInstance(info);

			// Assign the animation where the fragment slides
			// in from the right
			supFT.setCustomAnimations(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} else { // move the tab relatively rights
			// TODO Correctly obtain the Restaurant

			frag = MenuItemsFragment.newInstance(info);

			// Assign the animation where the fragment slides
			// in from the
			supFT.setCustomAnimations(R.anim.slide_in_right,
					R.anim.slide_out_left);
		}

		// Update the position
		mLastTabPosition = pos;

		if (frag != null) {
			supFT.replace(android.R.id.content, frag, LAST_FRAG_TAG );
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

	// ////////////////////////////////////////////////////
	// // Following are fragment call backs that signify user interaction
	// ////////////////////////////////////////////////////

	@Override
	public void onMenuItemDeleted(MenuItem item) {
		Toast.makeText(this, "Delete not available yet", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onMenuItemAdded(MenuItem item) {
		// getRestaurant().saveInBackGround(new SaveCallback() {
		item.saveInBackGround(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {

					getRestaurant().getInfo().saveInBackGround(new SaveCallback() {

						@Override
						public void done(ParseException e) {
							notifyAllUsersOfRestaurantChange();
							Toast.makeText(getApplicationContext(),
									"Menu Item Added!", Toast.LENGTH_SHORT)
									.show();
						}

					});
				} else {
					Log.e(TAG, e.getMessage() + " #" + e.getCode());
					Log.d(TAG, getRestaurant().packObject().toString());
				}
			}
		});
	}

	@Override
	public void onMenuItemModified(MenuItem item) {
		getRestaurant().saveInBackGround(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					notifyAllUsersOfRestaurantChange();
					Toast.makeText(getApplicationContext(),
							"Menu Item Updated!", Toast.LENGTH_SHORT).show();
				} else {
					Log.e(TAG, e.getMessage() + " #" + e.getCode());
					Log.d(TAG, getRestaurant().packObject().toString());
				}
			}
		});
	}

	@Override
	public void onRestaurantInfoUpdate(RestaurantInfo rest) {
		rest.saveInBackGround(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					notifyAllUsersOfRestaurantChange();
					Toast.makeText(getApplicationContext(),
							"Restaurant Info Updated!", Toast.LENGTH_SHORT)
							.show();
				} else {
					Log.e(TAG, e.getMessage() + " #" + e.getCode());
					Log.d(TAG, getRestaurant().packObject().toString());
				}
			}
		});

	}

	/**
	 * @author cronus91
	 *
	 * @param <T> Fragment class to listen to
	 */
	public class TabListener<T extends Fragment> implements
			ActionBar.TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		/**
		 * Constructor used each time a new tab is created.
		 * 
		 * @param activity
		 *            The host Activity, used to instantiate the fragment
		 * @param tag
		 *            The identifier tag for the fragment
		 * @param clz
		 *            The fragment's Class, used to instantiate the fragment
		 */
		public TabListener(Activity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}

		/* The following are each of the ActionBar.TabListener callbacks */

		@SuppressWarnings("unused")
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {

			android.support.v4.app.FragmentTransaction supFT = getSupportFragmentManager()
					.beginTransaction();

			RestaurantInfo info = getRestaurant().getInfo();
			assert (info != null);

			Fragment frag;

			frag = RestaurantInfoFragment.newInstance(info);

			// Check if the fragment is already initialized
			if (mFragment == null) {
				// If not, instantiate and add it to the activity
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				supFT.add(android.R.id.content, mFragment, mTag);
			} else {
				// If it exists, simply attach it in order to show it
				supFT.attach(mFragment);
			}
			supFT.commit();
		}
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				// Detach the fragment, because another one is being attached
				android.support.v4.app.FragmentTransaction supFT = getSupportFragmentManager()
						.beginTransaction();
				supFT.detach(mFragment);
				supFT.commit();
			}
		}
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// User selected the already selected tab. Usually do nothing.
		}
	}

	@Override
	public RestaurantInfo getInfo() {
		return getRestaurant().getInfo();
	}

}
