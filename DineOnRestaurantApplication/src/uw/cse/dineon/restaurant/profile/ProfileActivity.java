package uw.cse.dineon.restaurant.profile;

import java.io.File;
import java.io.IOException;

import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.library.image.ImageCache.ImageGetCallback;
import uw.cse.dineon.library.image.ImageIO;
import uw.cse.dineon.library.image.ImageObtainer;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.DineOnRestaurantActivity;
import uw.cse.dineon.restaurant.R;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
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
	private static final String LAST_FRAG_TAG = "LAST_FRAG";

	private MenuItemsFragment mItemsFragment;
	private RestaurantInfoFragment mRestInfoFragment;
	private File mTempFile;

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
		if (itemProfile != null) { // If exists
			itemProfile.setEnabled(false);
			itemProfile.setVisible(false);
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
			// TODO Correctly obtain the Restaurant

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
	public void onRequestTakePicture() {
		try {
			mTempFile = ImageIO.createImageFile(this);
			ImageObtainer.launchTakePictureIntent(this,
					DineOnConstants.REQUEST_TAKE_PHOTO, mTempFile);
		} catch (IOException e) {
			String message = "Unable to create a file to save your image";
			Log.e(TAG, message + " Exception " + e.getMessage());
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onRequestGetPictureFromGallery() {
		ImageObtainer.launchChoosePictureIntent(this, DineOnConstants.REQUEST_CHOOSE_PHOTO);
	}

	@Override
	public void onSelectImageAsDefault(int i) {
		// TODO set image at index I as  the default
	}

	@Override
	public void onImageRemoved(int index) {
		getRestaurant().getInfo().removeImageAt(index);
		getRestaurant().saveInBackGround(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				notifyAllRestaurantChange();
			}
		});
	}

	@Override
	public void getThisImage(DineOnImage image, final ViewGroup layout, final int id) {

		// Make a potentiall asyncronous call to download the image
		// from local storage or network.
		mImageCache.getImageFromCache(image, new ImageGetCallback() {

			@Override
			public void onImageReceived(Exception e, Bitmap b) {
				if (e != null) { // Error
					Log.e(TAG, "Unable to get image because e: " + e.getMessage());
					return;
				}
				// we go the picture.
				if (mRestInfoFragment == null) {
					Log.e(TAG, "Can't replace the correct view fragment is null");
					return;
				}

				mRestInfoFragment.replaceWithImage(layout, b, id);
			}
		});
	}

	/**
	 * Adds the photo to the restaurant. This method takes care of 
	 * all the image sizing and alignment.
	 * 
	 * This method will notify all the views in this activity that 
	 * need to know about a new photo.
	 * 
	 * This method will also handle the save of the new image
	 * While also adding it to the cache.
	 * 
	 * @param uri Uri for the image to download.
	 */
	private void addPhotoToRestaurant(Uri uri) {
		Bitmap b = ImageIO.loadBitmapFromURI(getContentResolver(), uri);	
		AsynchronousImageSaver saver = new AsynchronousImageSaver(b, getRestaurant());
		saver.execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK) {
			Log.d(TAG, "User cancelled job for request code " + requestCode);
			return;
		}

		Uri u = null;
		switch (requestCode) {
		// Took a photo.
		case DineOnConstants.REQUEST_TAKE_PHOTO:
			u = Uri.fromFile(mTempFile);
			mTempFile = null;
		case DineOnConstants.REQUEST_CHOOSE_PHOTO:
			u = data.getData();
			break;
		default:
			Log.w(TAG, "Unsupported operation occured onActivityResult");
		}

		if (u != null) {
			addPhotoToRestaurant(u);
		} else {
			Log.w(TAG, "Was not able to obtain a new image");
		}
	}

	/**
	 * This class helps in saving an image to the restaurant.
	 * There must be a sequence of steps to take in order to add an image successfully
	 * 
	 * @author mhotan
	 */
	private class AsynchronousImageSaver extends AsyncTask<Void, Void, DineOnImage> {

		private final Bitmap mBitmap;
		private final Restaurant mRestaurant;

		/**
		 * Creates an asynchronous process to save images for this restaurant.
		 * @param b bitmap to save in background thread.
		 * @param rest Restaurant to save image to.
		 */
		public AsynchronousImageSaver(Bitmap b, Restaurant rest) {
			if (b == null) {
				throw new NullPointerException("AsynchronousImageSaver image cannot be null");
			}
			mBitmap = b;
			mRestaurant = rest;
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
				mImageCache.addToCache(result);
				//				mBitmap.recycle();
			} else {
				Toast.makeText(This, "Unable to save image", Toast.LENGTH_SHORT).show();
			}
			isWorkingBackground = false;
			invalidateOptionsMenu();
		}

	}

}
