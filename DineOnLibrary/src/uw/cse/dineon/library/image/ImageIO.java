package uw.cse.dineon.library.image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.umd.cs.findbugs.annotations.CleanupObligation;

import uw.cse.dineon.library.util.DineOnConstants;
import android.content.ContentResolver;
import android.content.Context;
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

	private static final String IMAGE_PREFIX = "DineOnImage_";
	private static final String BITMAP_SUFFIX = ".bmp";
	
	/**
	 * Creates a temporary file from the context inputted.
	 * This File is only temporary and therefore has a time span.
	 * It will eventually to be removed
	 * @param ctx Context to grab temporary directory
	 * @return temporary file for image storage
	 * @throws IOException If there was an error creating a temp file
	 */
	public static File createImageFile(Context ctx) throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault()).
	    		format(Calendar.getInstance().getTime());
	    String imageFileName = IMAGE_PREFIX + timeStamp + "_";
	    File image = File.createTempFile(
	        imageFileName, 
	        BITMAP_SUFFIX, 
	        ctx.getCacheDir()
	    );
	    return image;
	}
	
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

	//	/**
	//	 * Gives the size of the image at the given file path.
	//	 * @param filePath File path of the image.
	//	 * @return Size of the image at the file path.
	//	 */
	//	public static Size getSizeOfImage(String filePath) {
	//		BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
	//		bmpOptions.inJustDecodeBounds = true;
	//		BitmapFactory.decodeFile(filePath, bmpOptions);
	//		return new Size(bmpOptions.outWidth, bmpOptions.outHeight);
	//	}
	//
	//	/**
	//	 * Gives the orientation of the image at the given uri.
	//	 * 
	//	 * Returned is Configuration.ORIENTATION_LANDSCAPE if landscape
	//	 * or Configuration.ORIENTATION_PORTRAIT if portrait.
	//	 * 
	//	 * @param resolver Content resolver to establish access
	//	 * @param uri Uri of the image
	//	 * @return Orientation of the image being downloaded
	//	 */
	//	public static int getOrientationOfImage(ContentResolver resolver, Uri uri) {
	//		Size current = getSizeOfImage(resolver, uri);
	//		if (current.width > current.height) {
	//			return Configuration.ORIENTATION_LANDSCAPE;
	//		}
	//		return Configuration.ORIENTATION_PORTRAIT;
	//	}
	//
	//	/**
	//	 * Gives the orientation of the image at the given file path.
	//	 * 
	//	 * Returned is Configuration.ORIENTATION_LANDSCAPE if landscape
	//	 * or Configuration.ORIENTATION_PORTRAIT if portrait.
	//	 * 
	//	 * @param filePath Filepath of image
	//	 * @return Orientation of the image being downloaded
	//	 */
	//	public static int getOrientationOfImage(String filePath) {
	//		Size current = getSizeOfImage(filePath);
	//		if (current.width > current.height) {
	//			return Configuration.ORIENTATION_LANDSCAPE;
	//		}
	//		return Configuration.ORIENTATION_PORTRAIT;
	//	}

	/**
	 * Processes the bitmap stored at the corresponding uri as the standard
	 * format for our image.
	 * @param cr Content resolver for obtaining image from uri.
	 * @param uri URI path directed toward our image
	 * @return Formatted Bitmap
	 * @throws IOException When error occurred processing the bitmap 
	 */
	private static Size processBitmap(ContentResolver cr, Uri uri) throws IOException {
		int width, height;
		Size size = getSizeOfImage(cr, uri);
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
			Size size = processBitmap(resolver, uri);

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

	//	/**
	//	 * Load an image from the designated relative or complete path.
	//	 * Relative path should be determined by the context of the caller
	//	 * generally it is safe to use absolute paths
	//	 * @param filePath Path to the image
	//	 * @param size Desired size of the image to be returned
	//	 * @return Image at desired size or full size
	//	 */
	//	public static Bitmap loadBitmapFromFilePath(String filePath, Size size) {
	//		if (size != null && size.width > 0 && size.height > 0) {
	//			// Obtain the original size of the bitmap image before scaling
	//			BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
	//			bmpOptions.inJustDecodeBounds = true;
	//			BitmapFactory.decodeFile(filePath, bmpOptions);
	//			int currHeight = bmpOptions.outHeight;
	//			int currWidth = bmpOptions.outWidth;
	//
	//			// Find the correct sample size
	//			int sampleSize = 1;
	//
	//			//use either width or height
	//			if ((currWidth > currHeight)) { // landscape
	//				sampleSize = Math.round((float)currHeight / (float)size.height);
	//			} else { // portrait
	//				sampleSize = Math.round((float)currWidth / (float)size.width);
	//			}
	//
	//			bmpOptions.inSampleSize = sampleSize;
	//			bmpOptions.inJustDecodeBounds = false;
	//			//decode the file with restricted sizee
	//			return BitmapFactory.decodeFile(filePath, bmpOptions);
	//		} else { // return full size image
	//			return BitmapFactory.decodeFile(filePath);
	//		}
	//	}

	//	/**
	//	 * Retrieves a unique temporary file to for storing Bitmap images.
	//	 * @param context Context to get where to store data
	//	 * @return Temporary file to use.
	//	 * @throws IOException Unable to initialize the file
	//	 */
	//	public static File getTempFile(Context context) throws IOException {
	//		File outputDir = context.getCacheDir();
	//		Date now = Calendar.getInstance().getTime();
	//		DateFormat format = DineOnConstants.getCurrentDateFormat();
	//		String nowStr = format.format(now);
	//		nowStr = nowStr.trim();
	//		nowStr = nowStr.replace(" ", "_");
	//		return File.createTempFile(nowStr, IMAGE_EXTENSION, outputDir);
	//	}
}
