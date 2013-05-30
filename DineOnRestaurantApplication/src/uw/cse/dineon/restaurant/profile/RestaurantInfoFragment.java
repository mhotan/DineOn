package uw.cse.dineon.restaurant.profile;

import java.util.List;

import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.library.image.ImageCache.ImageGetCallback;
import uw.cse.dineon.library.image.ImageObtainable;
import uw.cse.dineon.restaurant.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main view that allows the user to access and see their restaurant.
 * 
 * 
 * @author mhotan
 */
public class RestaurantInfoFragment extends Fragment {

	private static final String TAG = RestaurantInfoFragment.class.getSimpleName();

	private static final int IMAGEVIEW_WIDTH = 250;
	private static final int IMAGEVIEW_HEIGHT = 250;
	private static final int IMAGEVIEW_WIDTH_PADDING = 30;
	private static final int IMAGEVIEW_HEIGHT_PADDING = 30;

	/**
	 * Listener for information change.
	 */
	private InfoChangeListener mListener;

	private Context mOwner;

	private UserInputHandler mInputHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		RestaurantInfo info = mListener.getInfo();
		// Check the view and its state and initialize appropriately
		View view;
		if (isValid(info)) {
			view = inflater.inflate(R.layout.fragment_restaurant_info_two,
					container, false);

			mInputHandler = new UserInputHandler(view, info);
			// Populate the gallery with all the current images
			populateGallery(info);

			return view;
		} else {
			// This fragment signifies there is no restaurant data.
			view = inflater.inflate(R.layout.fragment_empty, container, false);
			TextView errorMessage = (TextView) view
					.findViewById(R.id.label_error);
			errorMessage.setText("Illegal Dining Session: " + info);
			return view;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof InfoChangeListener) {
			mListener = (InfoChangeListener) activity;
			mOwner = activity;
		} else {
			throw new ClassCastException(
					activity.toString()
					+ " must implemenet RestaurantInfoFragment.InfoChangeListener");
		}
	}

	/**
	 * Adds an image to the fragment containing this view. 
	 * @param image Image to add.
	 */
	public void addImage(Bitmap image) {
		// Get the layout to contain the image.
		Context ctx = getActivity().getApplicationContext();
		// Get the layout for holding the image
		LinearLayout layout = getStanderdLinearLayout(ctx);
		ImageView imageViewToAdd = produceView(ctx, image);
		layout.addView(imageViewToAdd);
		mInputHandler.addToGallery(layout);
	}

	/**
	 * Replaces the current view with new image.
	 * 
	 * This method is mainl used as a callback for when the
	 * Fragment is waiting for the retrieval of an image.
	 * 
	 * @param container Container to place image
	 * @param image Image of the new images
	 */
	private void replaceWithImage(ViewGroup container, Bitmap image) {
		container.removeAllViews();
		container.addView(produceView(getActivity(), image));
		container.invalidate(); // Redraw eventually 
	}

	/**
	 * Gets a new instance of the correct layout of that is capable of holding an
	 * ImageView made with produceView.  This is particular to this fragment.
	 * @param ctx Context to create Layout in.
	 * @return LinearLayout that contain an ImageView
	 */
	private static LinearLayout getStanderdLinearLayout(Context ctx) {
		LinearLayout layout = new LinearLayout(ctx);
		layout.setLayoutParams(new LayoutParams(IMAGEVIEW_WIDTH, IMAGEVIEW_HEIGHT));
		layout.setGravity(Gravity.CENTER);
		return layout;
	}

	/**
	 * This produces an image view that contains the bitmap.
	 * @param ctx Context to create image view in
	 * @param b Bitmap to create a view with.  
	 * @return ImageView with image centered and cropped appropiately.
	 */
	private static ImageView produceView(Context ctx, Bitmap b) {
		ImageView imageView = new ImageView(ctx);
		imageView.setLayoutParams(new LayoutParams(
				IMAGEVIEW_WIDTH - IMAGEVIEW_WIDTH_PADDING, 
				IMAGEVIEW_HEIGHT - IMAGEVIEW_HEIGHT_PADDING));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageBitmap(b);
		return imageView;
	}

	/**
	 * Returns general image loading progess dialog.
	 * @param ctx Context to create progress bar in.
	 * @return Indeterminate progress bar.
	 */
	private static View getLoadingImageProgressDialog(Context ctx) {
		ProgressBar p = new ProgressBar(ctx);
		p.setIndeterminate(true);
		return p;
	}

	/**
	 * Populates the gallery initially with all the images of the restaruant.
	 * Neither gallery nor restaurant can be null.
	 * @param info Restaurant info to populate the view with.
	 */
	private void populateGallery(RestaurantInfo info) {

		List<DineOnImage> images = info.getImageList();
		for (DineOnImage image: images) {
			// Get the default layout
			LinearLayout container = getStanderdLinearLayout(getActivity());
			container.removeAllViews();

			// Get a place holder image before we download
			container.addView(getLoadingImageProgressDialog(getActivity()));
			mInputHandler.addToGallery(container);

			// Create our custom callback for downloading images.
			ImageRetriever placeHolderCallback = new ImageRetriever(container, image);

			// have the listener get the image and we respond
			// appropiately.
			mListener.onGetImage(image, placeHolderCallback);
		}
	}

	/**
	 * Class that we use to encapsulate the attempt of getting images and
	 * updating a specified view.
	 * @author mhotan
	 */
	private class ImageRetriever implements ImageGetCallback {

		private final ViewGroup mView;
		private final DineOnImage mToDownload;
		private final ImageRetriever thisDownloader;

		/**
		 * Prepare a download for a certain image to download.
		 * @param toFill View to fill
		 * @param toDownload Image to download.
		 */
		public ImageRetriever(ViewGroup toFill, DineOnImage toDownload) {
			mView = toFill;
			// Clear any memory of any onclick listeners
			mView.setOnClickListener(null);
			mToDownload = toDownload;
			thisDownloader = this;
		}

		@Override
		public void onImageReceived(Exception e, Bitmap b) {
			if (e == null) { 
				// successful retrieval of image
				// Replace the place holder with image of new restaurant image
				replaceWithImage(mView, b);
			} else {
				// This notifies that the image failed to download
				// An example would be no internet
				mView.removeAllViews();
				// Get an image that shows the image requested failed to DL
				ImageView failToDownloadView = new ImageView(getActivity());
				failToDownloadView.setImageResource(R.drawable.av_download);
				mView.addView(failToDownloadView);

				// Set it so the image clicked will reattempt to get the image.
				mView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// Show the fun spinny stuff showing the user we are trying the 
						// best we can.
						mView.removeAllViews();
						mView.addView(getLoadingImageProgressDialog(getActivity()));
						mView.invalidate();
						mListener.onGetImage(mToDownload, thisDownloader);
					}
				});
				mView.invalidate();
			}
		}

	}

	/**
	 * Returns whether the Restaurant Info instance is valid.
	 * 
	 * @param info
	 *            Restaurant Info to check
	 * @return true if RestaurantInfo can be displayed
	 */
	private boolean isValid(RestaurantInfo info) {
		return info != null;
	}

	/**
	 * Listener for this fragment to communicate back to its attached activity.
	 * 
	 * @author mhotan
	 */
	public interface InfoChangeListener extends ImageObtainable {

		/**
		 * Notifies the Activity that the restaurant info requested to be
		 * updated.
		 * 
		 * @param rest
		 *            Updated Restaurant Info
		 */
		void onRestaurantInfoUpdate(RestaurantInfo rest);

		/**
		 * @return The RestaurantInfo object of this listener, or null if it doesn't exist
		 */
		RestaurantInfo getInfo();

		/**
		 * Adds this image to the restaurant.
		 * @param b Bitmap to add to Restaurant as an Image
		 */
		void onAddImageToRestaurant(Bitmap b);

		/**
		 * The user wants to set the image at index i as the default image.
		 * @param i index of the image to set as default.
		 */
		void onSelectImageAsDefault(int i);

		/**
		 * Removes image at index.
		 * @param index Index of the image to remove
		 */
		void onImageRemoved(int index);
	}

	/**
	 * This listener handles the standard input for image saving, adding, and removing.
	 * 
	 * This handler also takes care of 
	 * 
	 * @author mhotan 
	 */
	private class UserInputHandler implements View.OnClickListener, OnCheckedChangeListener {

		private final CheckBox mDefaultCheck;
		private final ImageButton mTakePicButton, mChoosePicButton, mDeleteButton;
		private final Button mSaveButton;
		private final RestaurantInfo mInfo;
		private final TextView mPhoneInput, mAddressInput, mHourInput;
		private final LinearLayout mGallery;

		private View mCurrentDefault;
		private View mCurrentSelected;

		private final AddToRestaurantCallback mAddToRestaurant;

		/**
		 * Adds an actual image of the restaurant to. 
		 * @param restaurantImage Adds a view containing a reference image to the index
		 * @return id of the image in the view;
		 */
		public int addToGallery(View restaurantImage) {
			// Add the view to the current gallery
			mGallery.addView(restaurantImage);

			// Set it so we can select the current image
			restaurantImage.setOnClickListener(this);

			// set the selected image to this image
			if (mCurrentSelected == null) {
				setSelected(restaurantImage);
			}
			return mGallery.indexOfChild(restaurantImage);
		}

		/**
		 * Given a view that contains all 4 of the image input buttons.
		 * 
		 * Creates a handler for all 4 buttons.
		 * 
		 * @param view Container view of all 4 buttons
		 * @param info Restaurant info to assign.
		 */
		UserInputHandler(View view, RestaurantInfo info) {
			mInfo = info;
			mDefaultCheck = (CheckBox) view.findViewById(R.id.checkbox_is_default_image);
			mTakePicButton = (ImageButton) view.findViewById(R.id.button_take_new_picture);
			mChoosePicButton = (ImageButton) view.findViewById(R.id.button_add_image_gallery);
			mDeleteButton = (ImageButton) view.findViewById(R.id.button_delete_image);
			mSaveButton = (Button) view.findViewById(R.id.button_save_restaurant_info);
			mPhoneInput = (TextView) view.findViewById(R.id.edittext_restaurant_phone);
			mAddressInput = (TextView) view.findViewById(R.id.edittext_restaurant_address);
			mHourInput = (TextView) view.findViewById(R.id.edittext_restaurant_hours);
			mGallery = (LinearLayout) view.findViewById(R.id.gallery_restaurant_images);
			
			if (mDefaultCheck == null 
					|| mTakePicButton == null 
					|| mChoosePicButton == null 
					|| mDeleteButton == null) {
				throw new IllegalArgumentException(
						"Invalid view, doesn't contain all the relevant sub items.");
			} 

			mAddToRestaurant = new AddToRestaurantCallback();

			mDefaultCheck.setOnCheckedChangeListener(this);
			mTakePicButton.setOnClickListener(this);
			mChoosePicButton.setOnClickListener(this);
			mDeleteButton.setOnClickListener(this);
			mSaveButton.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (v == mTakePicButton) {
				// Request the activity to take a picture
				mListener.onRequestTakePicture(mAddToRestaurant);
			} else if (v == mChoosePicButton) {
				// Request to choose a picture from gallery
				mListener.onRequestGetPictureFromGallery(mAddToRestaurant);
			} else if (v == mDeleteButton) {
				// TODO Handle image deletion
				AlertDialog.Builder builder = new Builder(mOwner);
				builder.setTitle("Confirm image deletion");
				builder.setMessage("Are you sure you want to delete it?");
				builder.setCancelable(true);
				builder.setPositiveButton("Yes", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						int index = mGallery.indexOfChild(mCurrentSelected);
						mGallery.removeView(mCurrentSelected);
						mListener.onImageRemoved(index);
					}
				});
				builder.setNegativeButton("Cancel", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

			} else if (v == mSaveButton) {
				mInfo.setAddr(mAddressInput.getText().toString());
				mInfo.setPhone(mPhoneInput.getText().toString());
				mInfo.setHours(mHourInput.getText().toString());
				if (mCurrentDefault != null) {
					mInfo.setMainImage(mGallery.indexOfChild(mCurrentDefault));
				} else {
					mInfo.setMainImage(-1);
				}
				mListener.onRestaurantInfoUpdate(mInfo);
			} else { 
				setSelected(v);
			}
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (mDefaultCheck == buttonView) {
				// Check if there is an image selected.
				if (isChecked) {
					setDefaultImage(mCurrentSelected);
				} else { // Uncheck
					setDefaultImage(null); // No default image
				}
			}
		}

		/**
		 * Sets the inputted image as the default image.
		 * @param v View to set as default.
		 */
		private void setDefaultImage(View v) { 
			if (mCurrentDefault != null) {
				mCurrentDefault.setBackgroundColor(Color.TRANSPARENT);
			}
			mCurrentDefault = v;
			if (mCurrentDefault != null) {
				mCurrentDefault.setBackgroundColor(Color.RED);
			}
		}

		/**
		 * Sets the restaurant Image as the currently selected image.
		 * If the current image is the default image then not action is needed.
		 * @param restaurantImage To set as selected.
		 */
		private void setSelected(View restaurantImage) {
			mDefaultCheck.setChecked(false);
			
			// In the case where there is nothing at all in this gallery
			if (mCurrentDefault == null && mCurrentSelected == null) {
				setDefaultImage(restaurantImage);
				return;
			}
			
			// If the image is the current defaulted image then no change is needed
			if (restaurantImage == mCurrentDefault) {
				mDefaultCheck.setChecked(true);
				return;
			}

			// reset the background of the old selected image.
			if (mCurrentSelected != null) {
				mCurrentSelected.setBackgroundColor(Color.TRANSPARENT);
			}
			
			mCurrentSelected = restaurantImage;
			mCurrentSelected.setBackgroundColor(Color.GRAY);
		}

		/**
		 * This callback is used when the user wishes to add an image to the restaurant
		 * via take a photo or choose a photo.
		 * @author mhotan
		 */
		private class AddToRestaurantCallback implements ImageGetCallback {

			@Override
			public void onImageReceived(Exception e, Bitmap b) {
				if (e == null) {
					mListener.onAddImageToRestaurant(b);
				} else {
					String message = String.format(getResources().
							getString(R.string.message_unable_get_image));
					Toast.makeText(mOwner, message, Toast.LENGTH_SHORT).show();
				}
			}

		}
	}
}
