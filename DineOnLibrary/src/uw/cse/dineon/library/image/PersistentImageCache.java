package uw.cse.dineon.library.image;

import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

/**
 * A Cache that is able to store persistent DineOnImages.
 * 
 * This cache has the capability of referencing the network or the cache to retrieve images.
 * The cache will have to reference the network to find if the file exists 
 * or if the cache is up to date.  If the cache is not up to date or does not have
 * the image then the a network call will be made to reference Parse.
 * 
 * In order to use this class the application must be initialized with Parse.
 * 
 * @author mhotan
 */
public final class PersistentImageCache {

	// Time before Image is marked as invalid.
	private static final long SECOND_MS = 1000;
	private static final long MINUTE_MS = SECOND_MS * 60;
	private static final long EXPIRATION_TIME = 604800000; // 1 week in ms
	private static final long OUTDATED_TIME = MINUTE_MS; // 1 minute

	private static final String TAG = PersistentImageCache.class.getSimpleName();

	private final ImageSQLiteHelper mSQLHelper;
	private final Calendar mCalendar;

	private CacheCleaner mCleaner;
	private SQLiteDatabase mDb;
	private AsyncImageAdder mLastAdder;
	
	private static PersistentImageCache mInstance;

	/**
	 * Returns an instance of this image cache, that can be shared  between activities.
	 * 
	 * @param applicationContext Application context to create this instance (if needed)
	 * @return Single instance of this cache
	 */
	public static PersistentImageCache getInstance(Context applicationContext) {
		if (mInstance == null) {
			mInstance = new PersistentImageCache(applicationContext);
		}
		return mInstance;
	}

	/**
	 * Columns to the database table that actual
	 * hold the values of the. 
	 *    Image
	 *    Expiration
	 *    Time when last updated
	 */
	private static final String[] RELEVANT_COLUMNS = { 
		ImageSQLiteHelper.COLUMN_PARSEID,
		ImageSQLiteHelper.COLUMN_LAST_UDPATED,
		ImageSQLiteHelper.COLUMN_IMG
	};

	/**
	 * Columns to the database that are used just for expiration.
	 */
	private static final String[] CONTAINS_ELEMENT = {
		ImageSQLiteHelper.COLUMN_PARSEID,
		ImageSQLiteHelper.COLUMN_LAST_UDPATED,
	};

	/**
	 * Creates a data source that can connect to the Database or Parse.
	 * @param context Context to create SQLiteHelper
	 */
	private PersistentImageCache(Context context) {
		mSQLHelper = new ImageSQLiteHelper(context);
		mCalendar = Calendar.getInstance();
		// Create a date format that stays constant for data base writing and reading.
	}

	/**
	 * Opens the current database.  If already no open no further action is taken
	 * Must call before referencing the cache
	 */
	public synchronized void open() {
		if (mDb != null && mDb.isOpen()) {
			Log.w(TAG, "Attempting to open an already open database");
			return;
		}
		mDb = mSQLHelper.getWritableDatabase();
	}

	/**
	 * Closes the current database.
	 */
	public synchronized void close() {
		// If there is a current thread processing still
		// set the thread to close the DB
		if (mDb == null || !mDb.isOpen()) {
			Log.w(TAG, "Attempting to close an already closed database");
			return;
		}
		
		// let the current adder finish its job
		if (mLastAdder != null) {
			mLastAdder.setCloseOnFinish(true);
		}
		
		mDb.close();
	}

	/**
	 * Adds a bitmap to the cache associating it to an id.
	 * Neither the id nor the bitmap can be null.
	 * 
	 * 
	 * @param id id to associate to the image
	 * @param time Time that the image was last update in cloud storage
	 * @param bitmap to actually store in the data base.
	 */
	public synchronized void add(String id, Date time, Bitmap bitmap) {
		// Attempt to open if not already open
		open();

		// Since the cache is only used for performance optimization.
		// then don' fail hard.  try to keep the program running 
		// for as long as possible.

		// if we have it in the cache then ignore adding it.
		if (id == null) {
			Log.e(TAG, "Failed add Null id! no write to image database");
			return;
		}

		// if we have it in the cache then ignore adding it.
		if (time == null) {
			Log.e(TAG, "Failed add Null time! no write to image database");
			return;
		}

		// if we have it in the cache then ignore adding it.
		if (bitmap == null) {
			Log.e(TAG, "Failed add Null Bitmap! no write to image database");
			return;
		}

		// We already have a version in the cache.
		if (hasVersionInCache(id)) {
			// We are going to have to just update its values.
			updateImage(id, bitmap, time);
		} else {
			// The cache did not have any version of the image.
			long updateTimeMS = time.getTime();
			ContentValues values = new ContentValues();	
			// Place the most recent data to the row
			values.put(ImageSQLiteHelper.COLUMN_PARSEID, id);
			values.put(ImageSQLiteHelper.COLUMN_LAST_UDPATED, updateTimeMS);
			values.put(ImageSQLiteHelper.COLUMN_LAST_USED, mCalendar.getTime().getTime());
			values.put(ImageSQLiteHelper.COLUMN_IMG, DineOnImage.bitmapToByteArray(bitmap));

			if (mDb.insert(ImageSQLiteHelper.TABLE_IMAGES, null, values) == -1) {
				Log.e(TAG, "Error occured while writing database");
			}
		}
	}
	
	/**
	 * Adds this image in the background.
	 * @param id Id of the image to add
	 * @param time time the image was added
	 * @param bitmap Bitmap to add.
	 */
	public void addInBackground(String id, Date time, Bitmap bitmap) {
		// If this Database is set to close on complete background execution
		// Just let it close with out scheduling another add.
		if (mLastAdder != null && mLastAdder.isSetToCloseOnFinish()) {
			return;
		}
		
		mLastAdder = new AsyncImageAdder(id, time, bitmap);
		mLastAdder.execute();
	}

	/**
	 * Returns if there is a version of the image in the cache.
	 * @param imageId Id number of Image to look for
	 * @return if there is any version of the image in the database.
	 */
	private boolean hasVersionInCache(String imageId) {
		Cursor cursor = null;
		if (!mDb.isOpen()) {
			return false;
		}

		// Do a query for the image with correct parse id.
		cursor = mDb.query(
				ImageSQLiteHelper.TABLE_IMAGES, 
				CONTAINS_ELEMENT,
				ImageSQLiteHelper.COLUMN_PARSEID + " = ?",
				new String[] {imageId}
				, null, null, null);
		boolean hasVal = cursor.moveToFirst();
		cursor.close();
		return hasVal;
	}

	/**
	 * Completely synchronous call to get image from the persistent cache.
	 * 
	 * Result will be passed through callback.
	 * 
	 * @param id to get Bitmap from. 
	 * 		This method will return null if the image is out of date or not available.
	 * @param lastUpdateTime Time this image was last updated.
	 * @return null if image is outdated or not existent. Else the bitmap image.
	 */
	public synchronized Bitmap get(String id, Date lastUpdateTime) {
		Cursor cursor = null;

		// Check if the data base is still open.
		if (!mDb.isOpen()) {
			Log.w(TAG, "Database closed when attempted to GET image " + id);
			return null;
		}

		// Structure the query for the image with the associated Id number
		cursor = mDb.query(
				ImageSQLiteHelper.TABLE_IMAGES, 
				RELEVANT_COLUMNS,
				ImageSQLiteHelper.COLUMN_PARSEID + " = ?",
				new String[] {id}
				, null, null, null);

		// Default the bitmap to return to null 
		// which is fail state.
		Bitmap toReturn = null;

		// If we have something in the cache
		// Check if we are up to date.
		if (cursor.moveToFirst()) { // If we have the value in the cache

			boolean getFromCache = false;
			long lastUpdateMS = 0;

			// We have the image in the cache and but we have to compare if 
			// the last time it was updated is after our last time in the cache.
			lastUpdateMS = lastUpdateTime.getTime();
			long ourLastTime = cursor.getLong(1);

			// If our date precedes that date the image
			// was last updated then don't get it from the cache
			getFromCache = !isOutdated(ourLastTime, lastUpdateMS);

			// Check if our image is the most recent one 
			// on the server.
			if (getFromCache) { // If we have a recent copy
				byte[] byteImg = cursor.getBlob(2);
				toReturn = DineOnImage.byteArrayToBitmap(byteImg);
				String parseId = cursor.getString(0);
				// Update the time
				updateLastUsedTime(parseId, mCalendar.getTime());
				return toReturn;
			}
		} 

		// Release the resources;
		cursor.close();
		// Can be null or our value.
		return toReturn;
	}

	/**
	 * For a image with Parse id parseID in the cloud update the time last updated.
	 * @param parseId String id of the image
	 * @param time time last updated.
	 */
	private void updateLastUsedTime(String parseId, Date time) {
		// Update our database with this Image's last used to NOW.
		ContentValues cv = new ContentValues();
		cv.put(ImageSQLiteHelper.COLUMN_LAST_USED, time.getTime());
		updateById(parseId, cv);
	}

	/**
	 *  Update the image at a particular parseId in the table. 
	 * 
	 * @param id id associated with the image of image in DB
	 * @param b Bitmap to store
	 * @param lastUpdatedTime Time image was last updated.
	 */
	private void updateImage(String id, Bitmap b, Date lastUpdatedTime) {
		ContentValues cv = new ContentValues();
		cv.put(ImageSQLiteHelper.COLUMN_IMG, DineOnImage.bitmapToByteArray(b));
		cv.put(ImageSQLiteHelper.COLUMN_LAST_USED, mCalendar.getTime().getTime());
		cv.put(ImageSQLiteHelper.COLUMN_LAST_UDPATED, lastUpdatedTime.getTime());
		updateById(id, cv);
	}

	/**
	 * Updates a specific value in the data base by parse Id.
	 * @param parseId Parse ID of the image to update
	 * @param cv Content values to update with
	 */
	private void updateById(String parseId, ContentValues cv) {
		String where = ImageSQLiteHelper.COLUMN_PARSEID + " = ?";
		int result = -1;
		result = mDb.update(ImageSQLiteHelper.TABLE_IMAGES, 
				cv, where, new String[] {parseId});

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
	 * Deletes this image from the Cache if it exists.
	 * @param id Image to delete.
	 */
	public synchronized void deleteImage(String id) {
		open(); // Open if needed.
		mDb.delete(
				ImageSQLiteHelper.TABLE_IMAGES, 
				ImageSQLiteHelper.COLUMN_PARSEID + " = ?",
				new String[] {id});
	}

	/**
	 * Deletes the image associated with id from the data base if needed.
	 * @param id Id of the image to be deleted.
	 */
	public synchronized void deleteIfOld(String id) {
		open(); // Open if needed.
		long now = mCalendar.getTime().getTime();

		Cursor cursor = mDb.query(ImageSQLiteHelper.TABLE_IMAGES,
				DELETE_COLUMNS, 
				ImageSQLiteHelper.COLUMN_PARSEID + " = ?",
				new String[] {id}, 
				null, null, null);
		// See if the time since last use is longer then expiration.
		if (now - cursor.getLong(1) > EXPIRATION_TIME) {
			deleteImage(id);
		}		
		cursor.close();
	}

	/**
	 * Single call that sparks an asycnronous cleaning of the cache.
	 * Good to call toward the end of an application instance. 
	 * 
	 * Cleans up all the images past an expiration time of last use.
	 */
	public void cleanUpCache() {
		// If there is already a cleaner running don't worry about it.
		if (mCleaner == null) { // If we are not current cleaning
			mCleaner = new CacheCleaner();
			mCleaner.execute();
		}
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
			// Get a complete table including ids and time of last use.
			Cursor cursor = mDb.query(ImageSQLiteHelper.TABLE_IMAGES,
					DELETE_COLUMNS, null, null, null, null, null);

			// Iterate through all files in the database
			while (!cursor.isAfterLast()) {

				deleteIfOld(cursor.getString(0));

				// Make sure we continue the iteration.
				cursor.moveToNext();
			}

			cursor.close();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mCleaner = null;
		}
	}
	
	//		cursor.close();
	//
	//		// Can reach here with two cases
	//		// Case 1. Our image's last updated value in the cache is before the one on the server
	//		// Case 2. We have never seen this image before.
	//		// In either case we have to attempt to get the latest copy
	//		// of the image. Upon successful retrieval save to Cache.
	//		// Always notify the callback what has happened
	//		image.getImageBitmap(new ImageGetCallback() {
	//
	//			@Override
	//			public void onImageReceived(Exception e, Bitmap b) {
	//				if (e == null) {
	//					// Got a copy from the cloud.
	//					// Update our value if we have it
	//					if (HAVEINCACHE) {
	//						// Our cache got out of sync.
	//						// We have the image but it was really old.
	//						// So update the image with most recent version.
	//						// Giving it fresh times
	//						long newTime = image.getLastUpdatedTime().getTime();
	//						updateTimes(image.getObjId(), TIMENOW, newTime);
	//						updateImage(image.getObjId(), b);
	//					} else {
	//						addImageToDB(image, b);
	//					}
	//				} // pass back the result.
	//				callback.onImageReceived(e, b);
	//			}
	//		});
	
//	/**
//	 * This method checks if there is a recents version of the image in
//	 * the app.  if it does then true is returned.
//	 * 
//	 * This method checks for a recent version by comparing update vs stored time.
//	 * 
//	 * @param imageId Image ID to find in the data base
//	 * @param updatedTime Time the image was last updated
//	 * @return true if there is a recent version of image added, false otherwise
//	 */
//	private boolean hasRecentInCache(String imageId, Date updatedTime) {
//		Cursor cursor = null;
//		if (!mDb.isOpen()) {
//			return false;
//		}
//
//		cursor = mDb.query(
//				ImageSQLiteHelper.TABLE_IMAGES, 
//				CONTAINS_ELEMENT,
//				ImageSQLiteHelper.COLUMN_PARSEID + " = ?",
//				new String[] {imageId}
//				, null, null, null);
//		if (!cursor.moveToFirst()) {
//			return false;
//		}
//		long lastUpdated = cursor.getLong(1);
//		cursor.close();
//		return !isOutdated(lastUpdated, updatedTime.getTime());
//	}


	//	/**
	//	 * Adds an image contained in DineOnImage with corresponding Bitmap to 
	//	 * the database.
	//	 * @param image Image to add
	//	 * @param b Bitmap version of the image.
	//	 */
	//	private void addImageToDB(DineOnImage image, Bitmap b) {
	//		// Synchronize so that this operation is
	//		// atomic with respect to data base calls
	//		// Here we check if the database is even open
	//		// if it is then 
	//		if (!mDb.isOpen()) {
	//			return;
	//		}
	//		// If there was a previous last adder then 
	//		// don't let it close the DB
	//		if (mLastAdder != null) {
	//			mLastAdder.setCloseOnFinish(false);
	//		}
	//		mLastAdder = new AsyncImageAdder(image, b);
	//		mLastAdder.setCloseOnFinish(true);
	//	}
	
	//
	//	/**
	//	 * Updates the times of a specific image in the data base with the specific times.
	//	 * If item does not exist then no effect will incur.
	//	 * @param parseId Parse Id of the image.
	//	 * @param lastUsed Time of last use to update to
	//	 * @param lastUpdated Time of last Updated to update to.
	//	 */
	//	private void updateTimes(String parseId, long lastUsed, long lastUpdated) {
	//		ContentValues cv = new ContentValues();
	//		cv.put(ImageSQLiteHelper.COLUMN_LAST_USED, lastUsed);
	//		cv.put(ImageSQLiteHelper.COLUMN_LAST_UDPATED, lastUpdated);
	//		updateById(parseId, cv);
	//	}
	
		/**
		 * Class that adds image in the background.
		 * @author mhotan 
		 */
		private class AsyncImageAdder extends AsyncTask<Void, Void, Void> {
	
			private final String mId;
			private final Date mLastUpdateTime;
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
			
			/**
			 * Set to close on finish of background thread.
			 * @return If this adder is set to close on finish.
			 */
			public boolean isSetToCloseOnFinish() {
				return mCloseOnFinish;
			}
	
			/**
			 * Sets teh image adder to this dine on image 
			 * and bitmap.
			 * @param id id of the image to add in background
			 * @param updateTime Time the image was last updated in the cloud.
			 * @param bitmap Bitmap to add that belongs to image.
			 */
			public AsyncImageAdder(String id, Date updateTime, Bitmap bitmap) {
				mId = id;
				mLastUpdateTime = updateTime;
				mBitmap = bitmap;
				mCloseOnFinish = false;
			}
	
			@Override
			protected Void doInBackground(Void... params) {
				add(mId, mLastUpdateTime, mBitmap);
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

