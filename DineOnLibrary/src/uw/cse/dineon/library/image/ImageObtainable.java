package uw.cse.dineon.library.image;

/**
 * Interface for activities to use to react image request.
 * @author mhotan
 */
public interface ImageObtainable {

	/**
	 * Callback that signifies the User would like to take a picture
	 * and add it to the current state of the Restaurant Info.
	 * 
	 * @param callback Callback to invoke when complete
	 */
	void onRequestTakePicture(ImageGetCallback callback);

	/**
	 * The user is requesting to take an image from the gallery
	 * and add it to the restaurants images.
	 * 
	 * @param callback Callback to invoke when complete
	 */
	void onRequestGetPictureFromGallery(ImageGetCallback callback);
	
	/**
	 * Gets an image from the listener.
	 * @param image Image to get.
	 * @param callback Callback to use when image is loaded.
	 */
	void onGetImage(DineOnImage image, ImageGetCallback callback);
	
}
