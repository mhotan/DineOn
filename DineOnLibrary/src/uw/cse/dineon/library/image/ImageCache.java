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

	private static final String[] RELEVANT_COLUMNS = { 
		ImageSQLiteHelper.COLUMN_PARSEID,
		ImageSQLiteHelper.COLUMN_LAST_UDPATED,
		ImageSQLiteHelper.COLUMN_IMG
	};

	/**
	 * Creates a data source that can connect to the Database or Parse.
	 * @param context Context to create SQLiteHelper
	 */
	public ImageCache(Context context) {
		mSQLHelper = new ImageSQLiteHelper(context);
		// Create a date format that stays constant for data base writing and reading.
//	mDateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.US);
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
		mDb.close();
	}

	/**
	 * Adds an already saved image to the cache.
	 * Possibly does asynchronously call to retrieve the image from the cloud
	 * There for 
	 * @param image Image to add to Cache
	 */
	public void addToCache(DineOnImage image) {
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
	 * Attempts to get the image from the cache if it exists, else
	 * it will try to get it from the network.
	 * 
	 * Result will be passed through callback.
	 * 
	 * @param image to get Bitmap from.
	 * @param callback Callback to use on receipt.
	 */
	public void getImageFromCache(final DineOnImage image, final ImageGetCallback callback) {
		Cursor cursor = mDb.query(
				ImageSQLiteHelper.TABLE_IMAGES, 
				RELEVANT_COLUMNS,
				ImageSQLiteHelper.COLUMN_PARSEID + " = ?",
				new String[] {image.getObjId()}
				, null, null, null);

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

				// Notify the user we have completed 
				callback.onImageReceived(null, toReturn);
				
				// Update the time
				updateLastUsedTime(cursor.getString(0), TIMENOW);
				return;
			}
		} 
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
		int result = mDb.update(ImageSQLiteHelper.TABLE_IMAGES, 
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
	 * Adds an image contained in DineOnImage with corresponding Bitmap to 
	 * the database.
	 * @param image Image to add
	 * @param b Bitmap version of the image.
	 */
	private void addImageToDB(DineOnImage image, Bitmap b) {
		// We have all the data
		long lastAnything = image.getLastUpdatedTime().getTime();

		ContentValues values = new ContentValues();
		values.put(ImageSQLiteHelper.COLUMN_PARSEID, image.getObjId());
		values.put(ImageSQLiteHelper.COLUMN_LAST_UDPATED, lastAnything);
		values.put(ImageSQLiteHelper.COLUMN_LAST_USED, lastAnything);
		values.put(ImageSQLiteHelper.COLUMN_IMG, DineOnImage.bitmapToByteArray(b));
		long insertId = mDb.insert(ImageSQLiteHelper.TABLE_IMAGES, null, values);
		if (insertId == -1) {
			Log.e(TAG, 
					"Unable to \"insert\" SQLite database because of some unknown reason");
		}
	}

	/**
	 * Deletes this image from the Cache if it exists.
	 * @param image Image to delete.
	 */
	public void deleteImage(DineOnImage image) {
		mDb.delete(
				ImageSQLiteHelper.TABLE_IMAGES, 
				ImageSQLiteHelper.COLUMN_PARSEID + " = ?",
				new String[] {image.getObjId()});
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

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mCleaner = null;
		}

	}
}
