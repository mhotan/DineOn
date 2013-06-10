package uw.cse.dineon.restaurant.profile;

import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.restaurant.DineOnRestaurantActivity;
import uw.cse.dineon.restaurant.R;
import android.app.ActionBar;
import android.app.ProgressDialog;
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
	 * Last used fragment string. used to reference for fragment substitution.
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
			ab.setTitle(mRestaurant.getName());
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
		android.view.MenuItem itemMenu = menu
				.findItem(R.id.item_restaurant_menu);
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

		Restaurant rest = mRestaurant;
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
			supFT.replace(android.R.id.content, frag, LAST_FRAG_TAG);
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
		Toast.makeText(this, getString(R.string.not_implemented), 
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onAddMenuItemToMenu(uw.cse.dineon.library.Menu menu,
			MenuItem item) {
		// TODO Auto-generated method stub
		MenuItemSaver saver = new MenuItemSaver(menu, item);
		saver.execute();
	}

	@Override
	public void onAddMenu(uw.cse.dineon.library.Menu menu) {
		// TODO Auto-generated method stub
		MenuSaver saver = new MenuSaver(menu);
		saver.execute();
	}

	@Override
	public void onMenuItemModified(MenuItem item) {
		mRestaurant.saveInBackGround(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					notifyAllRestaurantChange();
					Toast.makeText(getApplicationContext(),
							getString(R.string.menu_item_updated), Toast.LENGTH_SHORT).show();
				} else {
					Log.e(TAG, e.getMessage() + " #" + e.getCode());
					Log.d(TAG, mRestaurant.packObject().toString());
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
							getString(R.string.rest_info_updated), Toast.LENGTH_SHORT)
							.show();
				} else {
					Log.e(TAG, e.getMessage() + " #" + e.getCode());
					Log.d(TAG, mRestaurant.packObject().toString());
				}
			}
		});
	}

	@Override
	public RestaurantInfo getInfo() {
		if (mRestaurant == null) {
			return null;
		}
		return mRestaurant.getInfo();
	}

	@Override
	public void onImageRemoved(int index) {
		mRestaurant.getInfo().removeAtIndex(index);
		mRestaurant.saveInBackGround(new SaveCallback() {

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
	 * @return returns a progress dialog to show while the image is saving.
	 */
	private ProgressDialog getSavingImageDialog() {
		return getSaveDialog(R.string.saving_new_image);
	}

	/**
	 * Returns a progress dialog for showing that a certain item is saving.
	 * @param messageResId Resource message id.
	 * @return Progress dialog 
	 */
	private ProgressDialog getSaveDialog(int messageResId) {
		ProgressDialog d = new ProgressDialog(this);
		d.setIndeterminate(true);
		d.setCancelable(false);
		d.setTitle(R.string.saving);
		d.setMessage(getResources().getString(messageResId));
		return d;
	}

	/**
	 * This class helps in saving an image to the restaurant. There must be a
	 * sequence of steps to take in order to add an image successfully
	 * 
	 * @author mhotan
	 */
	private class RestaurantImageCreator extends
	AsyncTask<Void, Void, DineOnImage> {

		// Bitmap we wish to save as a DineOnimage
		private final Bitmap mBitmap;
		private final ProgressDialog mDialog;

		/**
		 * Creates an asynchronous process to save images for this restaurant.
		 * 
		 * @param b
		 *            bitmap to save in background thread.
		 */
		public RestaurantImageCreator(Bitmap b) {
			if (b == null) {
				throw new NullPointerException(
						"AsynchronousImageSaver image cannot be null");
			}
			mBitmap = b;
			mDialog = getSavingImageDialog();
		}

		@Override
		protected void onPreExecute() {
			isWorkingBackground = true;
			invalidateOptionsMenu();
			mDialog.show();
		}

		@Override
		protected DineOnImage doInBackground(Void... params) {
			try {
				DineOnImage image = new DineOnImage(mBitmap);
				image.saveOnCurrentThread();
				// We are locking the user while we are saving.
				mRestaurant.addImage(image);
				mRestaurant.saveOnCurrentThread();
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
				addToCache(result.getObjId(), result.getLastUpdatedTime(), mBitmap);
			} else {
				Toast.makeText(This, 
						getString(R.string.unable_save_image), Toast.LENGTH_SHORT)
				.show();
			}

			// Stop the progress spinner
			isWorkingBackground = false;
			invalidateOptionsMenu();

			mDialog.dismiss();
		}
	}

	/**
	 * Saves a menu item to the menu of the restaurant sepcified in the background.
	 * @author mhotan
	 */
	private class MenuItemSaver extends AsyncTask<Void, Exception, Void> {

		private final uw.cse.dineon.library.Menu mMenu;
		private final MenuItem mItem;
		private Exception mException;
		private final ProgressDialog mDialog;

		
		
		/**
		 * Prepares this menu item to be added to the menu.
		 * @param menu Menu to add to.
		 * @param item Menu Item to add to.
		 */
		public MenuItemSaver(uw.cse.dineon.library.Menu menu, MenuItem item) {
			mMenu = menu;
			mItem = item;
			mDialog = getSavingImageDialog();
		}

		@Override
		protected void onPreExecute() {
			isWorkingBackground = true;
			invalidateOptionsMenu();
			mDialog.show();
		}


		@Override
		protected Void doInBackground(Void... params) {
			try {
				synchronousAddMenuItem(mMenu, mItem);
			} catch (ParseException e) {
				publishProgress(e);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Exception... progress) {
			mException = progress[0];
		}

		@Override
		protected void onPostExecute(Void result) {
			// Stop the progress spinner
			isWorkingBackground = false;
			invalidateOptionsMenu();
			mDialog.dismiss();

			if (mException != null) {
				Log.e(tag, "Exception occured while saving menu item");
				return;
			}

			// Update the fragment if it is available
			if (mItemsFragment != null) {
				mItemsFragment.addMenuItem(mMenu, mItem);
			}
		}
	}

	/**
	 * Attempts to a menuitem to menu synchronously.
	 * @param menu Menu to add to
	 * @param item item to add to menu
	 * @throws ParseException If error occured.
	 */
	private synchronized void synchronousAddMenuItem(
			uw.cse.dineon.library.Menu menu, MenuItem item) 
					throws ParseException {
		// Save the item
		item.saveOnCurrentThread();

		// Add it to the menu then save.
		if (!menu.hasMenuItem(item)) {
			menu.addNewItem(item);
			menu.saveOnCurrentThread();
		}
		
		RestaurantInfo info = mRestaurant.getInfo();
		if (!info.hasMenu(menu)) {
			info.addMenu(menu);
			info.saveOnCurrentThread();
		}
	}

	/**
	 * Saves a menu in the background and adds it to the restaurant.
	 * @author mhotan
	 */
	private class MenuSaver extends AsyncTask<Void, Exception, Void> { 

		private final uw.cse.dineon.library.Menu mMenu;
		private Exception mException;
		private final ProgressDialog mDialog;

		@Override
		protected void onPreExecute() {
			isWorkingBackground = true;
			invalidateOptionsMenu();
			mDialog.show();
		}

		/**
		 * Prepares this menu item to be added to the menu.
		 * @param menu Menu to add to.
		 */
		public MenuSaver(uw.cse.dineon.library.Menu menu) {
			mMenu = menu;
			mDialog = getSavingImageDialog();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				synchronousAddMenu(mMenu);
			} catch (ParseException e) {
				publishProgress(e);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Exception... progress) {
			mException = progress[0];
		}

		@Override
		protected void onPostExecute(Void result) {
			// Stop the progress spinner
			isWorkingBackground = false;
			invalidateOptionsMenu();
			mDialog.dismiss();

			if (mException != null) {
				Log.e(tag, "Exception occured while saving menu item");
				return;
			}

			// Update the fragment if it is available
			if (mItemsFragment != null) {
				mItemsFragment.addMenu(mMenu);
			}
		}
	}

	/**
	 * Saves a menu on the current running thread and then adds it to
	 * the restaurant.
	 * @param menu Menu to add.
	 * @throws ParseException Error occurred on save
	 */
	private synchronized void synchronousAddMenu(
			uw.cse.dineon.library.Menu menu) throws ParseException {
		menu.saveOnCurrentThread();
		RestaurantInfo info = mRestaurant.getInfo();
		if (!info.hasMenu(menu)) {
			info.addMenu(menu);
			info.saveOnCurrentThread();
		}
	}

	/**
	 * Creates a DineOnImage for MenuItem.
	 * 
	 * @author mhotan
	 */
	private class MenuItemImageCreator extends
	AsyncTask<Void, Void, DineOnImage> {

		private final Bitmap mBitmap;
		private final MenuItem mItem;
		private final ProgressDialog mDialog;

		/**
		 * Prepares the saving process.
		 * 
		 * @param item
		 *            Item to save
		 * @param b
		 *            Bitmap to use.
		 */
		public MenuItemImageCreator(MenuItem item, Bitmap b) {
			mBitmap = b;
			mItem = item;
			mDialog = getSavingImageDialog();
		}

		@Override
		protected void onPreExecute() {
			isWorkingBackground = true;
			invalidateOptionsMenu();
			mDialog.show();
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
				Log.e(TAG,
						"Unable to save image for menu item exception: "
								+ e.getMessage());
				return null; // Fail case
			}
		}

		@Override
		protected void onPostExecute(DineOnImage result) {
			if (result != null) {
				addToCache(result.getObjId(), result.getLastUpdatedTime(), mBitmap);
			} else {
				String message = getResources().getString(
						R.string.message_unable_get_image);
				Toast.makeText(This, message, Toast.LENGTH_SHORT).show();
			}
			// Stop the progress spinner
			isWorkingBackground = false;
			invalidateOptionsMenu();
			mDialog.dismiss();
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
