package uw.cse.dineon.library.image;

import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

/**
 * Helpers class to download images at certain file paths or Uris.
 * @author mhotan
 */
public final class ImageIO {

	/**
	 * Can't Instantiate.
	 */
	private ImageIO() {
		// Stupid Check style
	}

	private static final String TAG = ImageIO.class.getSimpleName();

	/**
	 * Gives the size of the image at the given uri.
	 * @param resolver Content resolver to establish access
	 * @param uri uri of the image
	 * @return Size of the image at Uri uri
	 */
	public static Size getSizeOfImage(ContentResolver resolver, Uri uri) {
		// Open an initial input stream to for either loading the image or the sizes
		// depending on the open 
		try {
			InputStream is = resolver.openInputStream(uri);
			BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
			bmpOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, null, bmpOptions);
			Size toReturn = new Size(bmpOptions.outWidth, bmpOptions.outHeight);
			is.close();
			return toReturn;
		} catch (IOException e) {
			Log.e(TAG, "decodeSize:" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gives the size of the image at the given file path.
	 * @param filePath File path of the image.
	 * @return Size of the image at the file path.
	 */
	public static Size getSizeOfImage(String filePath) {
		BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
		bmpOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, bmpOptions);
		return new Size(bmpOptions.outWidth, bmpOptions.outHeight);
	}

	/**
	 * Gives the orientation of the image at the given uri.
	 * 
	 * Returned is Configuration.ORIENTATION_LANDSCAPE if landscape
	 * or Configuration.ORIENTATION_PORTRAIT if portrait.
	 * 
	 * @param resolver Content resolver to establish access
	 * @param uri Uri of the image
	 * @return Orientation of the image being downloaded
	 */
	public static int getOrientationOfImage(ContentResolver resolver, Uri uri) {
		Size current = getSizeOfImage(resolver, uri);
		if (current.width > current.height) {
			return Configuration.ORIENTATION_LANDSCAPE;
		}
		return Configuration.ORIENTATION_PORTRAIT;
	}

	/**
	 * Gives the orientation of the image at the given file path.
	 * 
	 * Returned is Configuration.ORIENTATION_LANDSCAPE if landscape
	 * or Configuration.ORIENTATION_PORTRAIT if portrait.
	 * 
	 * @param filePath Filepath of image
	 * @return Orientation of the image being downloaded
	 */
	public static int getOrientationOfImage(String filePath) {
		Size current = getSizeOfImage(filePath);
		if (current.width > current.height) {
			return Configuration.ORIENTATION_LANDSCAPE;
		}
		return Configuration.ORIENTATION_PORTRAIT;
	}

	/**
	 * Uses resolver to load the Bitmap image stored at the path distinguished at uri.
	 * If a size is specified then the image will attempted to be loaded at that size
	 * @param resolver Content resolver that can extract the image
	 * @param uri URI that points to the image
	 * @param size Desired size of the image
	 * @return Bitmap image at desired size, or full size if size argument is invalid or null
	 */
	public static Bitmap loadBitmapFromURI(ContentResolver resolver, Uri uri, Size size) {
		Bitmap image = null;
		try {
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
			Log.e(TAG, "Exception when reading: " + e);
			return image;
		}
	}

	/**
	 * Load an image from the designated relative or complete path.
	 * Relative path should be determined by the context of the caller
	 * generally it is safe to use absolute paths
	 * @param filePath Path to the image
	 * @param size Desired size of the image to be returned
	 * @return Image at desired size or full size
	 */
	public static Bitmap loadBitmapFromFilePath(String filePath, Size size) {
		if (size != null && size.width > 0 && size.height > 0) {
			// Obtain the original size of the bitmap image before scaling
			BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
			bmpOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, bmpOptions);
			int currHeight = bmpOptions.outHeight;
			int currWidth = bmpOptions.outWidth;

			// Find the correct sample size
			int sampleSize = 1;

			//use either width or height
			if ((currWidth > currHeight)) { // landscape
				sampleSize = Math.round((float)currHeight / (float)size.height);
			} else { // portrait
				sampleSize = Math.round((float)currWidth / (float)size.width);
			}

			bmpOptions.inSampleSize = sampleSize;
			bmpOptions.inJustDecodeBounds = false;
			//decode the file with restricted sizee
			return BitmapFactory.decodeFile(filePath, bmpOptions);
		} else { // return full size image
			return BitmapFactory.decodeFile(filePath);
		}
	}

//	/**
//	 * 
//	 * @param options
//	 * @param reqWidth
//	 * @param reqHeight
//	 * @return 
//	 */
//	public static int calculateInSampleSize(
//			BitmapFactory.Options options, int reqWidth, int reqHeight) {
//		// Raw height and width of image
//		final int height = options.outHeight;
//		final int width = options.outWidth;
//		int inSampleSize = 1;
//
//		if (height > reqHeight || width > reqWidth) {
//
//			// Calculate ratios of height and width to requested height and width
//			final int heightRatio = Math.round((float) height / (float) reqHeight);
//			final int widthRatio = Math.round((float) width / (float) reqWidth);
//
//			// Choose the smallest ratio as inSampleSize value, this will guarantee
//			// a final image with both dimensions larger than or equal to the
//			// requested height and width.
//			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//		}
//
//		return inSampleSize;
//	}
}
