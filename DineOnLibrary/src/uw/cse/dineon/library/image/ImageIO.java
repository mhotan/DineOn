package uw.cse.dineon.library.image;

import java.io.IOException;
import java.io.InputStream;

import uw.cse.dineon.library.util.DineOnConstants;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

/**
 * Helpers class to download images at certain file paths or Uris.
 * @author mhotan
 */
public final class ImageIO {

	//	private static final String IMAGE_EXTENSION = ".bmp";

	/**
	 * Can't Instantiate.
	 */
	private ImageIO() {
		// Stupid Check style
	}

	private static final String TAG = ImageIO.class.getSimpleName();

//	private static final String IMAGE_PREFIX = "DineOnImage_";
//	private static final String BITMAP_SUFFIX = ".bmp";
	//	
	//	/**
	//	 * Creates a temporary file from the context inputted.
	//	 * This File is only temporary and therefore has a time span.
	//	 * It will eventually to be removed
	//	 * @param ctx Context to grab temporary directory
	//	 * @return temporary file for image storage
	//	 * @throws IOException If there was an error creating a temp file
	//	 */
	//	public static File createImageFile(Context ctx) throws IOException {
	//	    // Create an image file name
	//	    String timeStamp = new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault()).
	//	    		format(Calendar.getInstance().getTime());
	//	    String imageFileName = IMAGE_PREFIX + timeStamp + "_";
	//	    File image = File.createTempFile(
	//	        imageFileName, 
	//	        BITMAP_SUFFIX, 
	//	        ctx.getCacheDir()
	//	    );
	//	    return image;
	//	}

	/**
	 * Gives the size of the image at the given uri.
	 * @param resolver Content resolver to establish access
	 * @param uri uri of the image
	 * @return Size of the image at Uri uri
	 * @throws IOException When an error occured while extracting the image
	 */
	private static Size getSizeOfImage(ContentResolver resolver, Uri uri) throws IOException {
		// Open an initial input stream to for either loading the image or the sizes
		// depending on the open 
		InputStream is = resolver.openInputStream(uri);
		BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
		bmpOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, bmpOptions);
		Size toReturn = new Size(bmpOptions.outWidth, bmpOptions.outHeight);
		is.close();
		return toReturn;
	}

	/**
	 * Get the size of an image.
	 * @param res Resource where image is fould
	 * @param resId Resource id of the image
	 * @return Size of the image.
	 */
	private static Size getSizeOfImage(Resources res, int resId) {
		BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
		bmpOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, bmpOptions);
		return new Size(bmpOptions.outWidth, bmpOptions.outHeight); 
	}

	/**
	 * Processes the bitmap stored at the corresponding uri as the standard
	 * format for our image.
	 * @param cr Content resolver for obtaining image from uri.
	 * @param uri URI path directed toward our image
	 * @return Scaled bitmap size that conforms to app specific requirements.
	 * @throws IOException When error occurred processing the bitmap 
	 */
	private static Size processBitmapScaledSize(ContentResolver cr, Uri uri) throws IOException {
		Size size = getSizeOfImage(cr, uri);
		return processBitmapSize(size);
	}

	/**
	 * Processes bitmap scaled size.
	 * @param res Resource
	 * @param resId Resource Id
	 * @return Scaled bitmap size that conforms to app specific requirements.
	 */
	private static Size processBitmapScaledSize(Resources res, int resId) {
		Size size = getSizeOfImage(res, resId);
		return processBitmapSize(size);
	}

	/**
	 * Process re scaling of size with our app specific dimensions.
	 * @param size Size to scale to.
	 * @return proportional and scaled size
	 */
	private static Size processBitmapSize(Size size) {
		int width, height;
		if (size.width < size.height) { // if portrait orientation.
			// Longest side is the height.
			height = DineOnConstants.LONGEST_IMAGE_DIMENSION;
			// get the width height ration.
			float ratio = (float) size.width / (float) size.height;
			// calculate the width with respect to the longest side
			// new width = (actual width / actual height) * max height
			width = Math.round(height * ratio);
		} else { // Landscape orientation
			// Longest side is the width
			width = DineOnConstants.LONGEST_IMAGE_DIMENSION;
			// get the Height to width ratio (actual height / actual width)
			float ratio = (float) size.height / (float) size.width;
			// Calculate the height with respect to the ration
			// new height = (actual height / actual width) * max width
			height = Math.round(width * ratio);
		}
		return new Size(width, height);
	}

	/**
	 * Uses resolver to load the Bitmap image stored at the path distinguished at uri.
	 * If a size is specified then the image will attempted to be loaded at that size
	 * @param resolver Content resolver that can extract the image
	 * @param uri URI that points to the image
	 * @return Bitmap image at desired size, or full size if size argument is invalid or null
	 */
	public static Bitmap loadBitmapFromURI(ContentResolver resolver, Uri uri) {
		Bitmap image = null;
		try {
			// Get the standerd size determince by a static constant
			Size size = processBitmapScaledSize(resolver, uri);

			// Open an initial input stream to for either loading the image or the sizes
			// depending on the open 
			InputStream is = resolver.openInputStream(uri);
			if (size != null && size.width > 0 && size.height > 0) {
				BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
				bmpOptions.inJustDecodeBounds = true;

				BitmapFactory.decodeStream(is, null, bmpOptions);
				int currHeight = bmpOptions.outHeight;
				int currWidth = bmpOptions.outWidth;

				is.close();
				InputStream is2 = resolver.openInputStream(uri);

				int sampleSize = 1;
				//use either width or height
				if (currWidth > currHeight) {
					sampleSize = Math.round((float)currHeight / (float)size.height);
				} else {
					sampleSize = Math.round((float)currWidth / (float)size.width);
				}
				bmpOptions.inSampleSize = sampleSize;
				bmpOptions.inJustDecodeBounds = false;

				//decode the file with restricted sizee
				image = BitmapFactory.decodeStream(is2, null, bmpOptions);
				is2.close();
			} else {
				image = BitmapFactory.decodeStream(is);
				is.close();
			}
			return image;
		} catch (IOException e) {
			Log.e(TAG, "Unable to load image, Exception: " + e);
			return image;
		}
	}

	/**
	 * Loads a bitmap scaled to this apps specific size requirements.
	 * @param res Resource to get the image
	 * @param resID Resource ID of image.
	 * @return scales bitmap image of this resource.
	 */
	public static Bitmap loadBitmapFromResource(Resources res, int resID) {
		// Get the standerd size determince by a static constant
		Size size = processBitmapScaledSize(res, resID);
		if (size != null && size.width > 0 && size.height > 0) {
			BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
			bmpOptions.inJustDecodeBounds = true;

			BitmapFactory.decodeResource(res, resID, bmpOptions);
			int currHeight = bmpOptions.outHeight;
			int currWidth = bmpOptions.outWidth;

			int sampleSize = 1;
			//use either width or height
			if (currWidth > currHeight) {
				sampleSize = Math.round((float)currHeight / (float)size.height);
			} else {
				sampleSize = Math.round((float)currWidth / (float)size.width);
			}
			bmpOptions.inSampleSize = sampleSize;
			bmpOptions.inJustDecodeBounds = false;

			//decode the file with restricted sizee
			return BitmapFactory.decodeResource(res, resID, bmpOptions);
		} else {
			return BitmapFactory.decodeResource(res, resID);
		}
	}
}
