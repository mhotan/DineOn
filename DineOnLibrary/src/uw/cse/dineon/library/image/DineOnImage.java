package uw.cse.dineon.library.image;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import uw.cse.dineon.library.TimeableStorable;
import uw.cse.dineon.library.image.ImageCache.ImageGetCallback;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * Abstract data type that represents an image.  Although this extends Storable
 * it is highly not advised to put this in an intent.  Due to size of images, this can take
 * a serious performance hit depending on the size of the image. 
 * @author Michael Hotan, mhotan@cs.washington.edu
 */
public class DineOnImage extends TimeableStorable {

	private static final String TAG = DineOnImage.class.getSimpleName();

	/**
	 * Static constants to send Parse data through.
	 */
	public static final String IMAGE = "dineOnImage";

	/**
	 * The ParseFile of this image.
	 */
	private ParseFile mImgFile;
	
	/**
	 * Synchronized constructor for a DineOnImage.
	 * Do not call on main thread.
	 * 
	 * @param b Bitmap to create image from
	 * @throws ParseException Error occured while saving the photo 
	 */
	public DineOnImage(Bitmap b) throws ParseException {
		super(DineOnImage.class);
		updateImage(b);
	}

	/**
	 * Create an image for the given bitmap.
	 * Bitmap cannot be null or an Illegal Argument Exception will occur.
	 * 
	 * Must have network connection for this to work.
	 * 
	 * @param b Bitmap to create file from.
	 * @param onSave process to call the image is finished updating.
	 */
	public DineOnImage(Bitmap b, SaveCallback onSave) {
		super(DineOnImage.class);
		updateImg(b, null);
	}

	/**
	 * Creates a DineOnImage based off of a parseobject. 
	 * @param po Parse Object to build from
	 * @throws ParseException if there is not internet connection
	 */
	public DineOnImage(ParseObject po) throws ParseException {
		super(po);
		mImgFile = po.getParseFile(IMAGE);
	}

	@Override
	public ParseObject packObject() {
		ParseObject po = super.packObject();
		po.put(IMAGE, mImgFile);
		return po;
	}

	/**
	 * Synchronized call to update the bitmap image on the current thread.
	 * This will save the image using the current thread.  Therefore asynchronous attributes
	 * have to be supported by client. 
	 * 
	 * @param b bitmap to update file to.
	 * @throws ParseException when there is no network connection.
	 */
	public void updateImage(Bitmap b) throws ParseException {
		if (b == null) {
			throw new IllegalArgumentException("DineOnImage Constructor, Bitmap cannot be null");
		}
		mImgFile = new ParseFile(bitmapToByteArray(b));
		mImgFile.save(); // Asyncronous call
	}
	
	/**
	 * Updates the image content of this image.
	 * If callback is null then the image is saved eventually at some time in the future
	 * @param b Bitmap to update to.
	 * @param onSave a callback which is notified once the object is saved.
	 */
	public void updateImg(Bitmap b, SaveCallback onSave) {
		if (b == null) {
			throw new IllegalArgumentException("DineOnImage Constructor, Bitmap cannot be null");
		}
		mImgFile = new ParseFile(bitmapToByteArray(b));
		if (onSave != null) {
			mImgFile.saveInBackground(onSave);
		} else {
			mImgFile.saveInBackground();
		}
	}

	/**
	 * Attempts to get the image asynchronously.  Returns results through callback.
	 * @param callback Callback to return a result to
	 */
	void getImageBitmap(final ImageGetCallback callback) {
		if (!mImgFile.isDataAvailable()) { // data is not available
			mImgFile.getDataInBackground(new GetDataCallback() {

				@Override
				public void done(byte[] img, ParseException e) {
					if (e == null) { // Success!
						Bitmap b = byteArrayToBitmap(img);
						callback.onImageReceived(null, b);
					} else { // Something bad happened.
						callback.onImageReceived(e, null);
					}
				}
			});
		} else { // We have the data.
			try {
				Bitmap b  = byteArrayToBitmap(mImgFile.getData());
				callback.onImageReceived(null, b);
			} catch (ParseException e) { 
				// Strange error happened
				Log.e(TAG, "Unable to get data for this Image when it exists " 
						+ "Exception: " + e.getMessage());
				callback.onImageReceived(e, null);
			}
		}
	}

	/**
	 * Gets the last time the object was updated.
	 * @return Date that this object was last updated
	 */
	public Date getLastUpdatedTime() {
		return mCompleteObject.getUpdatedAt();
	}

	/**
	 * Checks if the data is available locally.
	 * @return true if Image is available
	 */
	public boolean isDataAvailable() {
		return mImgFile.isDataAvailable();
	}

	/**
	 * Converts a valid Bitmap b to a byte array for storage.
	 * @param b Bitmap to convert
	 * @return Corresponding byte array.
	 */
	public static byte[] bitmapToByteArray(Bitmap b) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}

	/**
	 * Converts a valid byte array to a Bitmap.
	 * byte array must have been converted via bitmapToByteArray method.
	 * @param imgArray Array to convert
	 * @return Corresponding Bitmap.
	 */
	public static Bitmap byteArrayToBitmap(byte[] imgArray) {
		return BitmapFactory.decodeByteArray(imgArray, 0, imgArray.length);
	}

}
