package uw.cse.dineon.library.image;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import uw.cse.dineon.library.util.DineOnConstants;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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

	private static final String TAG = ImageCache.class.getSimpleName();

	private final ImageSQLiteHelper mSQLHelper;
	private final java.text.DateFormat mDateFormat;
	private SQLiteDatabase mDb;
	private final Calendar mCalendar;

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
		mDateFormat = DineOnConstants.getCurrentDateFormat();
		mCalendar = new GregorianCalendar();
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
				ImageSQLiteHelper.COLUMN_PARSEID + " = " + image.getObjId(),
				null, null, null, null);
		try {
			// If we have something in the cache
			// Check if we are up to date.
			if (cursor.moveToFirst()) {
				// We have the image in the cache and but we have to compare if 
				// the last time it was updated is after our last time in the cache.
				Date lastUpdatedCloud = image.getLastUpdatedTime();
				String ourDateString = cursor.getString(1);
				Date ourDate = mDateFormat.parse(ourDateString);

				// Check if our image is the most recent one 
				// on the server.
				if (!ourDate.before(lastUpdatedCloud)) {
					byte[] byteImg = cursor.getBlob(2);
					Bitmap toReturn = DineOnImage.byteArrayToBitmap(byteImg);
					callback.onImageReceived(null, toReturn);
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
						// Got it from the cloud!
						addImageToDB(image, b);
					} // pass back the result.
					callback.onImageReceived(e, b);
				}
			});
		} catch (ParseException e) {
			String message = "We (Mike) Screwed the pooch parsing Strings into Dates";
			Log.e(TAG, message);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds an image contained in DineOnImage with corresponding Bitmap to 
	 * the database.
	 * @param image Image to add
	 * @param b Bitmap version of the image.
	 */
	private void addImageToDB(DineOnImage image, Bitmap b) {
		// We have all the data
		ContentValues values = new ContentValues();
		values.put(ImageSQLiteHelper.COLUMN_PARSEID, image.getObjId());
		values.put(ImageSQLiteHelper.COLUMN_LAST_UDPATED, 
				mDateFormat.format(image.getLastUpdatedTime()));
		values.put(ImageSQLiteHelper.COLUMN_LAST_USED, mDateFormat.format(mCalendar.getTime()));
		values.put(ImageSQLiteHelper.COLUMN_IMG, DineOnImage.bitmapToByteArray(b));
		long insertId = mDb.insert(ImageSQLiteHelper.TABLE_IMAGES, null, values);
		if (insertId == -1) {
			Log.e(TAG, 
					"Unable to \"insert\" SQLite database because of some unknown reason");
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
}
