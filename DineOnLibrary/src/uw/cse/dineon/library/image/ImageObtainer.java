package uw.cse.dineon.library.image;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Helper class to take or select image via Android resources.
 * There are two current ways to get images for this application.
 * 
 * 1. Choose an existing image via the gallery
 * 2. Take a picture via the camera.
 * @author mhotan
 */
public final class ImageObtainer {

	/**
	 * Cannot initialize utility class.
	 */
	private ImageObtainer() {
		// F'n stupid check style
	}
	
	/**
	 * Any context that wishes to take an via the camera can call this method
	 * and the result will be passed back via the file or in the extras.
	 * 
	 * IE if Activity A calls launchTakePictureIntent(this, 1)
	 * then the user will be prompted to take a picture.
	 * 
	 * Upon conclusion A.onActivityResult(int requestCode, int resultCode, Intent data) is called
	 * requestCode will be equal to the argument passed in by A (1 in this example)
	 * 
	 * Upon successful selection then the resultCode == RESULT_OK
	 * and data.getData() will contain the Uri of the image.
	 * 
	 * @param activity Activity to launch intent.
	 * @param resultCode Result code that will be passed back to onActivityResult
	 * @param f File to save image at
	 */
	public static void launchTakePictureIntent(Activity activity, int resultCode, File f) {
		// Launch the intent to actually capture the image
		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f)); // Store the image in tis file
		activity.startActivityForResult(i, resultCode);
	}

	/**
	 * Any activity that wishes to choose an image from the gallery can call this method
	 * and the result will passed back via 
	 * onActivityResult(int requestCode, int resultCode, Intent data).
	 * The Uri of the image is data.getData();
	 * 
	 * IE if Activity A calls launchChoosePictureIntent(this, 1)
	 * then the user will be prompted to choose an image from the gallery
	 * 
	 * Upon successful selection then the resultCode == RESULT_OK
	 * and data.getData() will contain the Uri of the image.
	 * 
	 * @param activity Activity in which to launch intent from
	 * @param resultCode Result code that onActivityResult will use
	 */
	public static void launchChoosePictureIntent(Activity activity, int resultCode) {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		activity.startActivityForResult(photoPickerIntent, resultCode);
	}

}
