package uw.cse.dineon.restaurant.profile;

import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.restaurant.DineOnRestaurantActivity;
import uw.cse.dineon.restaurant.R;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
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

	/**
	 * Last used fragment string.  used to reference for fragment substitution.
	 */
	public static final String LAST_FRAG_TAG = "LAST_FRAG";

	private MenuItemsFragment mItemsFragment;
	private RestaurantInfoFragment mRestInfoFragment;
	
	private Context This;

	private boolean isWorkingBackground;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		This = this;

		mLastTabPosition = 1; // Let the tab be either the 0 or 1
		isWorkingBackground = false;
		if (!isLoggedIn()) {
			Log.w(TAG, "User not logged in cant show profile");
			return;
		}

		// If logged in fill views appropriately
		// Set the actionbar with associated tabs
		ActionBar ab = getActionBar();
		if (ab != null) { // Support older builds
			ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			ab.setTitle(getRestaurant().getName());
			ab.setDisplayShowTitleEnabled(true);
			ab.addTab(ab.newTab()
					.setText(R.string.tab_actionbar_restaurant_profile)
					.setTabListener(this));
			ab.addTab(ab.newTab()
					.setText(R.string.tab_actionbar_restaurant_menuitems)
					.setTabListener(this));
		}
	}

	@Override
	public void updateUI() {
		super.updateUI();
		invalidateOptionsMenu();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		android.view.MenuItem itemProfile = menu
				.findItem(R.id.item_restaurant_profile);
		// Already at profile page so remove the button
		// for profile and menu
		if (itemProfile != null) { // If exists
			itemProfile.setEnabled(false);
			itemProfile.setVisible(false);
		}
		android.view.MenuItem itemMenu = menu.findItem(R.id.item_restaurant_menu);
		if (itemMenu != null) {
			itemMenu.setEnabled(false);
			itemMenu.setVisible(false);
		}

		android.view.MenuItem itemDownload = menu.findItem(R.id.item_progress);
		if (itemDownload != null) {
			itemDownload.setVisible(isWorkingBackground);
		}

		return true;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// Obtain a reference on which tab is being selected
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

			// Ad hic singleton pattern for fragments
			if (mRestInfoFragment == null) {
				mRestInfoFragment = new RestaurantInfoFragment();
			}
			frag = mRestInfoFragment;
			// Assign the animation where the fragment slides
			// in from the right
			supFT.setCustomAnimations(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} else { // move the tab relatively rights

			if (mItemsFragment == null) {
				mItemsFragment = new MenuItemsFragment();
			}
			frag = mItemsFragment;

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
		// Nothing to really do.
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// Nothing to really do.
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
							notifyAllRestaurantChange();
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
					notifyAllRestaurantChange();
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
					notifyAllRestaurantChange();
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

	@Override
	public RestaurantInfo getInfo() {
		if (getRestaurant() == null) {
			return null;
		}
		return getRestaurant().getInfo();
	}

	@Override
	public void onImageRemoved(int index) {
		getRestaurant().getInfo().removeAtIndex(index);
		getRestaurant().saveInBackGround(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				notifyAllRestaurantChange();
			}
		});
	}

	@Override
	public void onAddImageToRestaurant(Bitmap b) {
		RestaurantImageCreator saver = new RestaurantImageCreator(b);
		saver.execute();
	}

	@Override
	public void onImageAddedToMenuItem(MenuItem item, Bitmap b) {
		MenuItemImageCreator creator = new MenuItemImageCreator(item, b);
		creator.execute();
	}

	/**
	 * This class helps in saving an image to the restaurant.
	 * There must be a sequence of steps to take in order to add an image successfully
	 * 
	 * @author mhotan
	 */
	private class RestaurantImageCreator extends AsyncTask<Void, Void, DineOnImage> {

		// Bitmap we wish to save as a DineOnimage
		private final Bitmap mBitmap;

		/**
		 * Creates an asynchronous process to save images for this restaurant.
		 * @param b bitmap to save in background thread.
		 */
		public RestaurantImageCreator(Bitmap b) {
			if (b == null) {
				throw new NullPointerException("AsynchronousImageSaver image cannot be null");
			}
			mBitmap = b;
		}

		@Override
		protected void onPreExecute() {
			isWorkingBackground = true;
			invalidateOptionsMenu();
		}

		@Override
		protected DineOnImage doInBackground(Void... params) {
			try {
				DineOnImage image = new DineOnImage(mBitmap);
				image.saveOnCurrentThread();
				mRestaurant.addImage(image);
				return image;
			} catch (ParseException e) {
				Log.e(TAG, "Unable to save image exception: " + e.getMessage());
				return null; // Fail case
			}
		}

		@Override 
		protected void onPostExecute(DineOnImage result) {
			if (result != null) {
				if (mRestInfoFragment != null) {
					mRestInfoFragment.addImage(mBitmap);
				}
				// Success add image to the cache
				addImageToCache(result, mBitmap);
			} else {
				Toast.makeText(This, "Unable to save image", Toast.LENGTH_SHORT).show();
			}

			// Stop the progress spinner
			isWorkingBackground = false;
			invalidateOptionsMenu();
		}
	}

	/**
	 * Creates a DineOnImage for MenuItem.
	 * @author mhotan
	 */
	private class MenuItemImageCreator extends AsyncTask<Void, Void, DineOnImage> {

		private final Bitmap mBitmap;
		private final MenuItem mItem;

		/**
		 * Prepares the saving process.
		 * @param item Item to save
		 * @param b Bitmap to use.
		 */
		public MenuItemImageCreator(MenuItem item, Bitmap b) {
			mBitmap = b;
			mItem = item;
		}

		@Override
		protected void onPreExecute() {
			isWorkingBackground = true;
			invalidateOptionsMenu();
		}

		@Override
		protected DineOnImage doInBackground(Void... arg0) {
			try {
				DineOnImage image = new DineOnImage(mBitmap);
				image.saveOnCurrentThread();
				mItem.setImage(image);
				mItem.saveOnCurrentThread();
				return image;
			} catch (ParseException e) {
				Log.e(TAG, "Unable to save image for menu item exception: " + e.getMessage());
				return null; // Fail case
			}
		}

		@Override 
		protected void onPostExecute(DineOnImage result) {
			if (result != null) {
				addImageToCache(result, mBitmap);
			} else {
				String message = getResources().
						getString(R.string.message_unable_get_image);
				Toast.makeText(This, message, Toast.LENGTH_SHORT).show();
			}
			// Stop the progress spinner
			isWorkingBackground = false;
			invalidateOptionsMenu();
		}
	}

	@Override
	public Location getLocation() {
		return getLastKnownLocation();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		if (mRestInfoFragment != null) {
			mRestInfoFragment.setLocationToUse(location);
		}
	}
}
