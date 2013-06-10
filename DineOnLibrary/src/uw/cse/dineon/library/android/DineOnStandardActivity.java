package uw.cse.dineon.library.android;

import java.io.File;
import java.net.URI;
import java.util.Date;

import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.library.image.ImageGetCallback;
import uw.cse.dineon.library.image.ImageIO;
import uw.cse.dineon.library.image.ImageObtainable;
import uw.cse.dineon.library.image.ImageObtainer;
import uw.cse.dineon.library.image.PersistentImageCache;
import uw.cse.dineon.library.util.DineOnConstants;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.Toast;

/**
 * This activity represents a base activity for all Dine On Related activities.
 * 
 * All shared code for activities between Applications should go in here
 * 
 * @author mhotan, mtrathjen08
 */
public class DineOnStandardActivity extends FragmentActivity implements ImageObtainable, Locatable {

	/**
	 * Standard tag for this specific activity.
	 */
	protected final String tag = this.getClass().getSimpleName();

	private static final String EXTRA_LOCATION = "_extra_location";
	private static final String EXTRA_FILE_URI = "_extra_file_uri";

	/**
	 * A reference to this activity.
	 */
	protected DineOnStandardActivity thisAct;

	/**
	 * Image cache for use in memory.
	 */
	private LruCache<String, Bitmap> mImageMemCache;

	/**
	 * Protected reference for ease of use.
	 * Don't be dumbass a null it out.
	 */
	private PersistentImageCache mPersImageCache;

	/**
	 * Temporary file for storing image.
	 */
	private File mTempFile;

	/**
	 * Holds a reference to the current GetImage Callback.
	 */
	private ImageGetCallback mGetImageCallback;

	/**
	 * Location Manager for location services.
	 */
	private LocationManager mLocationManager;

	/**
	 * Last received location from this activity. Initially null.
	 */
	private Location mLocation;
	
	/**
	 * True is location services is supported, else false.
	 */
	private boolean mLocationSupport;

	/////////////////////////////////////////////////////////////////////
	/////  Override Activity specific methods for correct behavior
	/////////////////////////////////////////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		thisAct = this;

		// Initialize the memory cache
		final int MAXMEMORY = (int) (Runtime.getRuntime().maxMemory() / 1024);
		// lets only use 1 / 8 the memory available
		final int CACHESIZE = MAXMEMORY / 6;

		mImageMemCache = new LruCache<String, Bitmap>(CACHESIZE) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};

		mPersImageCache = PersistentImageCache.getInstance(getApplicationContext());
		mPersImageCache.open();

		this.mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		this.mLocation = null;
		try {
			requestLocationUpdates();
			this.mLocationSupport = true;
		} catch (IllegalArgumentException ex) {
			// The provider doesn't exist because its emulator
			Toast.makeText(this, "Location updates unsupported on this device.  " 
					+ "Are you on an emulator?", 
					Toast.LENGTH_SHORT).show();
			this.mLocationSupport = false;
		}

		// Attempt to restore old values only if they exist
		if (savedInstanceState != null) {
			mLocation = savedInstanceState.getParcelable(EXTRA_LOCATION);
			if (savedInstanceState.containsKey(EXTRA_FILE_URI)) {
				URI uri = (URI) savedInstanceState.getSerializable(EXTRA_FILE_URI);
				mTempFile = new File(uri);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mLocation != null) {
			outState.putParcelable(EXTRA_LOCATION, mLocation);
		}

		if (mTempFile != null) {
			outState.putSerializable(EXTRA_FILE_URI, mTempFile.toURI());
		}

	}

	@Override
	protected void onResume() {
		mPersImageCache.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		mPersImageCache.close();
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// For right now filter out any intents without a RESULT OK
		if (resultCode != RESULT_OK) {
			Log.d(tag, "User cancelled job for request code " + requestCode);
			return;
		}

		Uri uriForInfoFragment = null;
		switch (requestCode) {
		// Took a photo.
		case DineOnConstants.REQUEST_TAKE_PHOTO:
			uriForInfoFragment = Uri.fromFile(mTempFile);
			break;
			// Picked a Photo
		case DineOnConstants.REQUEST_CHOOSE_PHOTO:
			uriForInfoFragment = data.getData();
			break;
		default:
			Log.wtf(tag, "Unknown Photo Request");
		}

		// Only handle the image if there is a valid uri and
		// image callback to handle the callback
		if (uriForInfoFragment != null && mGetImageCallback != null) {
			getPhotoAndExecute(uriForInfoFragment, mGetImageCallback);
		}
	}

	/////////////////////////////////////////////////////////////////////
	/////  Image Obtainable specific methods
	/////////////////////////////////////////////////////////////////////

	@Override
	public void onRequestTakePicture(ImageGetCallback callback) {
		// Assign the right callback
		mGetImageCallback = callback;

		// Delete the old temporary file
		if (mTempFile != null) {
			boolean deleted = mTempFile.delete();
			if(!deleted) {
				Log.e(tag, "Old temporary file not deleted");
			}
		}

		// Create a new temporary file
		mTempFile = getTempImageFile();
		ImageObtainer.launchTakePictureIntent(this,
				DineOnConstants.REQUEST_TAKE_PHOTO, mTempFile);
	}

	@Override
	public void onRequestGetPictureFromGallery(ImageGetCallback callback) {
		// Assign the right callback
		mGetImageCallback = callback;
		ImageObtainer.launchChoosePictureIntent(this, DineOnConstants.REQUEST_CHOOSE_PHOTO);
	}

	@Override
	public void onGetImage(DineOnImage image, ImageGetCallback callback) {
		// This is a callback for this activity to be able to return images
		// to the user via a callback.
		// IE fragments will call onGetImage to return the image and further process
		// it via the callback
		getImage(image, callback);
	}

	/////////////////////////////////////////////////////////////////////
	/////  Getters that sub activities can use
	/////////////////////////////////////////////////////////////////////

	/**
	 * @return a new temporary image file for runtime storage.
	 */
	protected File getTempImageFile() {
		// Attempt to create a gosh darn file to write images to
		return new File(Environment.getExternalStorageDirectory(), "temp.jpg");
	}
	
	/**
	 * Add the current image to local cache.  This is a write through process 
	 * except long writes are done in a seperate thread.
	 * @param imageId Image id number to associate bitmap to
	 * @param updateTime Time the image was updated in the cloud
	 * @param bitmap Bitmap image to add to cache
	 */
	protected void addToCache(String imageId, Date updateTime, Bitmap bitmap) {
		if (bitmap == null) {
			Log.e(tag, "Can't have null image in the cache!");
			return;
		}
		if (imageId == null) {
			Log.e(tag, "Image Id number cannot to be null.  No add proceeded");
			return;
		}
		// Adding to memory is fast
		mImageMemCache.put(imageId, bitmap);
		// Adding to the persistent cache is slow so do it in the
		// background to enhance the user experience.
		mPersImageCache.addInBackground(imageId, updateTime, bitmap);
	}
	
	/**
	 * Attempt to get the image from the Memory Cache.
	 * @param image image to get from memory cache
	 * @return Bitmap associated with this image. null if no image exists
	 */
	private Bitmap getBitmapFromMemCache(DineOnImage image) {
		String id = image.getObjId();
		return mImageMemCache.get(id);
	}

	/**
	 * Attempt to get the Bitmap image from the persistent cache.
	 * @param image Image to retrieve.
	 * @return Bitmap Bitmap of the DineOnImage.
	 */
	private Bitmap getBitmapFromPersistentCache(DineOnImage image) {
		String id = image.getObjId();
		return mPersImageCache.get(id, image.getLastUpdatedTime());
	} 
	
	/**
	 * Attempts to get the image.
	 * This retrieval process goes through a multiple levels of local caching.
	 * If no recent local copy is available it will continue to make a network 
	 * call to attempt and retrieve the image.
	 * 
	 * Note:
	 * If the callback is null the image is retrieved and added to the cache if needed.
	 * @requires the image is not null
	 * @param image image to get.
	 * @param callback Callback to get back
	 */
	protected void getImage(final DineOnImage image, final ImageGetCallback callback) {
		if (callback == null) {
			return; // Cant call back to no one
		}

		if (image == null) {
			callback.onImageReceived(new RuntimeException("Null DineOnImage"), null);
			return;
		}

		// Check in memory cache
		Bitmap ret = getBitmapFromMemCache(image);
		if (ret != null) {
			callback.onImageReceived(null, ret);
			return;
		}
		Log.d(tag, "LRU Memory Cache miss for " + image);

		ret = getBitmapFromPersistentCache(image);
		if (ret != null) {
			callback.onImageReceived(null, ret);
			return;
		}
		Log.d(tag, "LRU Persistent Cache miss for " + image);
		
		final DineOnImage TOADD = image;
		TOADD.getImageBitmap(new ImageGetCallback() {
			
			@Override
			public void onImageReceived(Exception e, Bitmap b) {
				if (e == null) {
					addToCache(TOADD.getObjId(), TOADD.getLastUpdatedTime(), b);
					callback.onImageReceived(e, b);
					return;
				}
				Log.e(tag, "Cloud image miss for " + TOADD);
				callback.onImageReceived(e, null);
			}
		});
	}

	/////////////////////////////////////////////////////////////////////
	/////  Getters that sub activities can use
	/////////////////////////////////////////////////////////////////////

	/**
	 * Processes a photo to and passes back through callback. 
	 * 
	 * @param uri Uri for the image to download.
	 * @param callback Image callback to invoke
	 */
	private void getPhotoAndExecute(Uri uri, ImageGetCallback callback) {
		Bitmap b = ImageIO.loadBitmapFromURI(getContentResolver(), uri);
		callback.onImageReceived(null, b);
	}

	@Override
	public void onLocationChanged(Location location) {
		mLocation = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// Do nothing as of right now
	}

	@Override
	public void onProviderEnabled(String provider) {
		// Do nothing as of right now
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// Do nothing as of right now
	}

	@Override
	public Location getLastKnownLocation() {
		return mLocation;
	}

	@Override
	public void requestLocationUpdates() {
		this.mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, 
				this, 
				null);
		this.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
				DineOnConstants.MIN_LOCATION_UPDATE_INTERVAL_MILLIS, 
				DineOnConstants.MIN_LOCATION_UPDATE_DISTANCE_METERS, 
				this);
		// TODO add support for gps
	}

	@Override
	public boolean isLocationSupported() {
		return this.mLocationSupport;
	}
}
