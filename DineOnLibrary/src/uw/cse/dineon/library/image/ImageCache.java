package uw.cse.dineon.library.image;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

/**
 * This cache has the capability of referencing the network or the cache to retrieve images.
 * The cache will have to reference the network to find if the file exists 
 * or if the cache is up to date.  If the cache is not up to date or does not have
 * the image then the a network call will be made to reference Parse.
 * 
 * In order to use this class the application must be initialized with Parse.
 * 
 * @author mhotan
 */
public class ImageCache {

	// Time before Image is marked as invalid.
	private static final long SECOND_MS = 1000;
	private static final long MINUTE_MS = SECOND_MS * 60;
	private static final long EXPIRATION_TIME = 604800000; // 1 week in ms
	private static final long OUTDATED_TIME = MINUTE_MS; // 1 minute

	private static final String TAG = ImageCache.class.getSimpleName();

	private final ImageSQLiteHelper mSQLHelper;
	//	private final java.text.DateFormat mDateFormat;
	private CacheCleaner mCleaner;
	private SQLiteDatabase mDb;

	private AsyncImageAdder mLastAdder;

	private final Object mLock = new Object();

	private static final String[] RELEVANT_COLUMNS = { 
		ImageSQLiteHelper.COLUMN_PARSEID,
		ImageSQLiteHelper.COLUMN_LAST_UDPATED,
		ImageSQLiteHelper.COLUMN_IMG
	};

	private static final String[] CONTAINS_ELEMENT = {
		ImageSQLiteHelper.COLUMN_PARSEID,
		ImageSQLiteHelper.COLUMN_LAST_UDPATED,
	};

	/**
	 * Creates a data source that can connect to the Database or Parse.
	 * @param context Context to create SQLiteHelper
	 */
	public ImageCache(Context context) {
		mSQLHelper = new ImageSQLiteHelper(context);
		// Create a date format that stays constant for data base writing and reading.
	}

	/**
	 * Opens the current database.
	 * Must call before referencing the cache
	 */
	public void open() {
		mDb = mSQLHelper.getWritableDatabase();
	}

	/**
	 * Closes the current database.
	 */
	public void close() {
		// If there is a current thread processing still
		// set the thread to close the DB
		synchronized (mLock) {
			if (mLastAdder != null) {
				mLastAdder.setCloseOnFinish(true);
			}
		}
		mDb.close();
	}

	/**
	 * Adds an already saved image to the cache.
	 * Possibly does asynchronously call to retrieve the image from the cloud
	 * There for 
	 * @param image Image to add to Cache
	 */
	public void addToCache(DineOnImage image) {
		// if we have it in the cache then ignore adding it.
		if (hasRecentInCache(image)) {
			return;
		}
		final DineOnImage TOSAVE = image;
		TOSAVE.getImageBitmap(new ImageGetCallback() {

			@Override
			public void onImageReceived(Exception e, Bitmap b) {
				if (e != null) {
					// For images that can't be retrieve we just ignore
					Log.e(TAG, "Unable to add SQLite database because of Exception: " 
							+ e.getMessage());
					return;
				}
				addImageToDB(TOSAVE, b);
			}
		});
	}

	/**
	 * This method checks if there is a recents version of the image in
	 * the app.  if it does then true is returned.
	 * @param image Image to check for
	 * @return true if there is a recent version of image added, false otherwise
	 */
	public boolean hasRecentInCache(DineOnImage image) {
		Cursor cursor = null;
		synchronized (mLock) {
			cursor = mDb.query(
					ImageSQLiteHelper.TABLE_IMAGES, 
					CONTAINS_ELEMENT,
					ImageSQLiteHelper.COLUMN_PARSEID + " = ?",
					new String[] {image.getObjId()}
					, null, null, null);
		}
		if (!cursor.moveToFirst()) {
			return false;
		}
		long lastUpdated = cursor.getLong(1);
		cursor.close();
		return !isOutdated(lastUpdated, image.getLastUpdatedTime().getTime());
	}

	/**
	 * Returns if there is a version of the image in the cache.
	 * @param image Image to look for
	 * @return if there is any version of the image in the database.
	 */
	private boolean hasVersionInCache(DineOnImage image) {
		Cursor cursor = null;
		synchronized (mLock) {
			if (!mDb.isOpen()) {
				return false;
			}
			
			cursor = mDb.query(
					ImageSQLiteHelper.TABLE_IMAGES, 
					CONTAINS_ELEMENT,
					ImageSQLiteHelper.COLUMN_PARSEID + " = ?",
					new String[] {image.getObjId()}
					, null, null, null);
		}
		boolean hasVal = cursor.moveToFirst();
		cursor.close();
		return hasVal;
	}

	/**
	 * Attempts to get the image from the cache if it exists, else
	 * it will try to get it from the network.
	 * 
	 * Result will be passed through callback.
	 * 
	 * @param image to get Bitmap from.
	 * @param callback Callback to use on receipt.
	 */
	public void getImageFromCache(final DineOnImage image, final ImageGetCallback callback) {
		Cursor cursor = null;
		synchronized (mLock) {
			cursor = mDb.query(
					ImageSQLiteHelper.TABLE_IMAGES, 
					RELEVANT_COLUMNS,
					ImageSQLiteHelper.COLUMN_PARSEID + " = ?",
					new String[] {image.getObjId()}
					, null, null, null);
		}

		final boolean HAVEINCACHE = cursor.moveToFirst();

		final long TIMENOW = Calendar.getInstance().getTime().getTime();

		// If we have something in the cache
		// Check if we are up to date.
		if (HAVEINCACHE) {

			boolean getFromCache = false;
			long lastUpdateMS = 0;

			// We have the image in the cache and but we have to compare if 
			// the last time it was updated is after our last time in the cache.
			lastUpdateMS = image.getLastUpdatedTime().getTime();
			long ourLastTime = cursor.getLong(1);

			// If our date precedes that date the image
			// was last updated then don't get it from the cache
			getFromCache = !isOutdated(ourLastTime, lastUpdateMS);

			// Check if our image is the most recent one 
			// on the server.
			if (getFromCache) {
				byte[] byteImg = cursor.getBlob(2);
				Bitmap toReturn = DineOnImage.byteArrayToBitmap(byteImg);
				String parseId = cursor.getString(0);
				cursor.close();

				// Notify the user we have completed 
				callback.onImageReceived(null, toReturn);

				// Update the time
				updateLastUsedTime(parseId, TIMENOW);
				return;
			}
		} 

		// Couldnt find image so close cursor.
		cursor.close();

		// Can reach here with two cases
		// Case 1. Our image's last updated value in the cache is before the one on the server
		// Case 2. We have never seen this image before.
		// In either case we have to attempt to get the latest copy
		// of the image. Upon successful retrieval save to Cache.
		// Always notify the callback what has happened
		image.getImageBitmap(new ImageGetCallback() {

			@Override
			public void onImageReceived(Exception e, Bitmap b) {
				if (e == null) {
					// Got a copy from the cloud.
					// Update our value if we have it
					if (HAVEINCACHE) {
						// Our cache got out of sync.
						// We have the image but it was really old.
						// So update the image with most recent version.
						// Giving it fresh times
						long newTime = image.getLastUpdatedTime().getTime();
						updateTimes(image.getObjId(), TIMENOW, newTime);
						updateImage(image.getObjId(), b);
					} else {
						addImageToDB(image, b);
					}
				} // pass back the result.
				callback.onImageReceived(e, b);
			}
		});
	}

	/**
	 * For a image with Parse id parseID in the cloud update the time last updated.
	 * @param parseId String id of the image
	 * @param time time last updated.
	 */
	private void updateLastUsedTime(String parseId, long time) {
		// Update our database with this Image's last used to NOW.
		ContentValues cv = new ContentValues();
		cv.put(ImageSQLiteHelper.COLUMN_LAST_USED, time);
		updateByParseId(parseId, cv);
	}

	/**
	 * Updates the times of a specific image in the data base with the specific times.
	 * If item does not exist then no effect will incur.
	 * @param parseId Parse Id of the image.
	 * @param lastUsed Time of last use to update to
	 * @param lastUpdated Time of last Updated to update to.
	 */
	private void updateTimes(String parseId, long lastUsed, long lastUpdated) {
		ContentValues cv = new ContentValues();
		cv.put(ImageSQLiteHelper.COLUMN_LAST_USED, lastUsed);
		cv.put(ImageSQLiteHelper.COLUMN_LAST_UDPATED, lastUpdated);
		updateByParseId(parseId, cv);
	}

	/**
	 *  Update the image at a particular parseId in the table. 
	 * 
	 * @param parseId Parse id of image in DB
	 * @param b Bitmap to store
	 */
	private void updateImage(String parseId, Bitmap b) {
		ContentValues cv = new ContentValues();
		cv.put(ImageSQLiteHelper.COLUMN_IMG, DineOnImage.bitmapToByteArray(b));
		updateByParseId(parseId, cv);
	}

	/**
	 * Updates a specific value in the data base by parse Id.
	 * @param parseId Parse ID of the image to update
	 * @param cv Content values to update with
	 */
	private void updateByParseId(String parseId, ContentValues cv) {
		String where = ImageSQLiteHelper.COLUMN_PARSEID + " = ?";
		int result = -1;
		synchronized (mLock) {
			result = mDb.update(ImageSQLiteHelper.TABLE_IMAGES, 
					cv, where, new String[] {parseId});
		}
		if (result <= 0) {
			Log.w(TAG, "Unable to update using updateByParseId");
		}
	}

	/**
	 * Compares our date vs the last updated date to check if our version is not to old.
	 * 
	 * @param ourDate Date that we save
	 * @param lastUpdated The date th
	 * @return true if our test is outdated compared to the last updated Date
	 */
	private static boolean isOutdated(long ourDate, long lastUpdated) {
		long timeDiff = lastUpdated - ourDate;
		return timeDiff > OUTDATED_TIME;
	}

	/**
	 * Adds an image contained in DineOnImage with corresponding Bitmap to 
	 * the database.
	 * @param image Image to add
	 * @param b Bitmap version of the image.
	 */
	private void addImageToDB(DineOnImage image, Bitmap b) {
		// Synchronize so that this operation is
		// atomic with respect to data base calls
		synchronized (mLock) {
			// Here we check if the database is even open
			// if it is then 
			if (!mDb.isOpen()) {
				return;
			}
			// If there was a previous last adder then 
			// don't let it close the DB
			if (mLastAdder != null) {
				mLastAdder.setCloseOnFinish(false);
			}
			mLastAdder = new AsyncImageAdder(image, b);
			mLastAdder.setCloseOnFinish(true);
		} 
		mLastAdder.execute();
	}

	/**
	 * Deletes this image from the Cache if it exists.
	 * @param image Image to delete.
	 */
	public void deleteImage(DineOnImage image) {
		synchronized (mLock) {
			mDb.delete(
					ImageSQLiteHelper.TABLE_IMAGES, 
					ImageSQLiteHelper.COLUMN_PARSEID + " = ?",
					new String[] {image.getObjId()});
		}
	}

	/**
	 * Single call that sparks an asycnronous cleaning of the cache.
	 * Good to call toward the end of an application instance.
	 */
	public void cleanUpCache() {
		// If there is already a cleaner running don't worry about it.
		if (mCleaner == null) {
			mCleaner = new CacheCleaner();
			mCleaner.execute();
		}
	}

	/**
	 * An Image get callback for retrieving images from.
	 * @author mhotan
	 */
	public interface ImageGetCallback {

		/**
		 * Callback that notifies the client whether the image was downloaded
		 * or retrieved successfully.
		 * 
		 * Error occurred when exception "e" is not null.
		 * 
		 * @param e exception that occurred or null if success
		 * @param b Bitmap if success.
		 */
		public void onImageReceived(Exception e, Bitmap b);
	}

	private static final String[] DELETE_COLUMNS = {
		ImageSQLiteHelper.COLUMN_ID,
		ImageSQLiteHelper.COLUMN_LAST_USED,
	};

	/**
	 * Helper class that cleans up the data base in the background.
	 * @author mhotan 
	 */
	private class CacheCleaner extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			synchronized (mLock) {
				Cursor cursor = mDb.query(ImageSQLiteHelper.TABLE_IMAGES,
						DELETE_COLUMNS, null, null, null, null, null);

				// Current time
				long now = Calendar.getInstance().getTime().getTime();

				// Iterate through all files in the database
				while (!cursor.isAfterLast()) {

					// Get the last used date and compare with the current date
					long lastUsed = cursor.getLong(1);

					// See if the time since last use is longer then expiration.
					if (now - lastUsed > EXPIRATION_TIME) {
						mDb.delete(ImageSQLiteHelper.TABLE_IMAGES,
								ImageSQLiteHelper.COLUMN_ID + " = " + cursor.getLong(0),
								null);
					}

					// Make sure we continue the iteration.
					cursor.moveToNext();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mCleaner = null;
		}
	}

	/**
	 * Class that adds image in the background.
	 * @author mhotan 
	 */
	private class AsyncImageAdder extends AsyncTask<Void, Void, Void> {

		private final DineOnImage mImage;
		private final Bitmap mBitmap;

		private boolean mCloseOnFinish;

		/**
		 * Sets whether this task will close the database one this back ground
		 * activity finishes.
		 * @param close true if you want this thread to close the database on close
		 * 	false other wise
		 */
		public void setCloseOnFinish(boolean close) {
			mCloseOnFinish = close;
		}

//		/**
//		 * Shows if the thread is set to close.
//		 * @return if thread will close the database on complete.
//		 */
//		public boolean isSetToClose(){
//			return mCloseOnFinish;
//		}

		/**
		 * Sets teh image adder to this dine on image 
		 * and bitmap.
		 * @param image image to add in background
		 * @param bitmap Bitmap to add that belongs to image.
		 */
		public AsyncImageAdder(DineOnImage image, Bitmap bitmap) {
			mImage = image;
			mBitmap = bitmap;
		}

		@Override
		protected Void doInBackground(Void... params) {
			long lastAnything = mImage.getLastUpdatedTime().getTime();
			ContentValues values = new ContentValues();	

			// Place the most recent data to the row
			values.put(ImageSQLiteHelper.COLUMN_LAST_UDPATED, lastAnything);
			values.put(ImageSQLiteHelper.COLUMN_LAST_USED, lastAnything);
			values.put(ImageSQLiteHelper.COLUMN_IMG, DineOnImage.bitmapToByteArray(mBitmap));

			// We have all the data
			if (hasVersionInCache(mImage)) {
				updateByParseId(mImage.getObjId(), values);
			} else {
				values.put(ImageSQLiteHelper.COLUMN_PARSEID, mImage.getObjId());
				long id = -1;

				// Fine grain locking over database insertion
				synchronized (mLock) {
					if (!mDb.isOpen()) {
						Log.e(TAG, "Cannot add image in closed database");
						return null;
					}
					
					id = mDb.insert(ImageSQLiteHelper.TABLE_IMAGES, null, values);
				}

				if (id == -1) {
					Log.e(TAG, 
							"Unable to \"insert\" SQLite " 
									+ "database because of some unknown reason");
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mCloseOnFinish) {
				mDb.close();
			}
			mLastAdder = null;
		}
	}
}

