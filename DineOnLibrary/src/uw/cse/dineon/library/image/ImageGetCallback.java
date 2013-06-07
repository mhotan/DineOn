package uw.cse.dineon.library.image;

import android.graphics.Bitmap;

/**
 * An Image get callback for retrieving images from.
 * @author mhotan
 */
public interface ImageGetCallback {

	/**
	 * Callback that is generally used for retrieving the Bitmap
	 * image Asynchronously.
	 * 
	 * Error occurred when exception "e" is not null.
	 * 
	 * @param e exception that occurred or null if success
	 * @param b Bitmap if success.
	 */
	public void onImageReceived(Exception e, Bitmap b);
}
