package uw.cse.dineon.library;

import java.io.File;

import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.library.image.ImageCache;
import uw.cse.dineon.library.image.ImageIO;
import uw.cse.dineon.library.image.ImageObtainable;
import uw.cse.dineon.library.image.ImageObtainer;
import uw.cse.dineon.library.image.ImageCache.ImageGetCallback;
import uw.cse.dineon.library.util.DineOnConstants;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * This activity represents a base activity for all Dine On Related activities.
 * 
 * All shared code for activities between Applications should go in here
 * 
 * @author mhotan
 */
public class DineOnStandardActivity extends FragmentActivity implements ImageObtainable {

	/**
	 * Standard tag for this specific activity.
	 */
	protected final String tag = this.getClass().getSimpleName();

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
	private ImageCache mPersImageCache;

	/**
	 * Temporary file for storing image.
	 */
	private File mTempFile;

	/**
	 * Holds a reference to the current GetImage Callback.
	 */
	private ImageGetCallback mGetImageCallback;

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
		final int CACHESIZE = MAXMEMORY / 8;

		mImageMemCache = new LruCache<String, Bitmap>(CACHESIZE) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};
		
		mPersImageCache = new ImageCache(this);
		mPersImageCache.open();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mPersImageCache.open();
	}

	@Override
	protected void onPause() {
		mPersImageCache.close();
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

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
		case DineOnConstants.REQUEST_CHOOSE_PHOTO:
			uriForInfoFragment = data.getData();
			break;
		default:
			Log.w(tag, "Unsupported operation occured onActivityResult");
		}

		if (uriForInfoFragment != null && mGetImageCallback != null) {
			getPhotoAndExecute(uriForInfoFragment, mGetImageCallback);
		} else {
			Log.w(tag, "Was not able to obtain a new image");
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
			mTempFile.delete();
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
	 * Memory cache to upload image.
	 * @param image image to get from memory cache
	 * @return Bitmap associated with this image. 
	 */
	protected Bitmap getBitmapFromMemCache(DineOnImage image) {
		String id = image.getObjId();
		return mImageMemCache.get(id);
	}
	
	/**
	 * Attempts to get the image as fast as possible.
	 * @param image image to get.
	 * @param callback Callback to get back
	 */
	protected void getImage(final DineOnImage image, final ImageGetCallback callback) {

		// Check in memory cache
		Bitmap ret = getBitmapFromMemCache(image);
		if (ret != null) {
			callback.onImageReceived(null, ret);
			return;
		}

		// Check in SQL database or network
		mPersImageCache.getImageFromCache(image, new ImageGetCallback() {

			@Override
			public void onImageReceived(Exception e, Bitmap b) {
				if (e == null) {
					addImageToCache(image, b);
					callback.onImageReceived(null, b);
				} else {
					callback.onImageReceived(e, null);
				}
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

	/**
	 * Adds an image to cache replacing old version if it exists.
	 * @param image image to associate bitmap to
	 * @param bitmap Bitmap to use.
	 */
	protected void addImageToCache(DineOnImage image, Bitmap bitmap) {
		String id = image.getObjId();
		mImageMemCache.put(id, bitmap);
		mPersImageCache.addToCache(image);
	}
}
