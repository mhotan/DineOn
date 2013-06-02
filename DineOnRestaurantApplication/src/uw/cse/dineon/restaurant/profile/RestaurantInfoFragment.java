package uw.cse.dineon.restaurant.profile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main view that allows the user to access and see their restaurant.
 * 
 * @author mhotan
 */
public class RestaurantInfoFragment extends Fragment {

//	private static final String TAG = RestaurantInfoFragment.class.getSimpleName();
	
	private static final int IMAGEVIEW_WIDTH = 250;
	private static final int IMAGEVIEW_HEIGHT = 250;
	private static final int IMAGEVIEW_WIDTH_PADDING = 30;
	private static final int IMAGEVIEW_HEIGHT_PADDING = 30;

	/**
	 * Listener for information change.
	 */
	private InfoChangeListener mListener;

	/**
	 * Context of the owner.
	 */
	private Context mOwner;

	/**
	 * Input handler for user input.
	 */
	private UserInputHandler mInputHandler;
	
	/**
	 * location to use for request restaurant.
	 */
	private Location mLastKnownLocation;
	
	/**
	 * Geocoder for finding location.
	 */
	private Geocoder mGeocoder;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLastKnownLocation = mListener.getLocation();
		mGeocoder = new Geocoder(getActivity());
	}
	
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
			getActivity().getActionBar().setTitle(info.getName());
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
	 * Stores this restaurant. 
	 * @param loc Location to use as restaurant location
	 */
	public void setLocationToUse(Location loc) {
		mLastKnownLocation = loc;
		if (mLastKnownLocation != null && mInputHandler != null) {
			mInputHandler.enableLocationButton();
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
		 * @return The RestaurantInfo object of this listener, or null if it doesn't exist
		 */
		RestaurantInfo getInfo();

		/**
		 * Returns the current activities last known location.
		 * @return Location of this restaurant.
		 */
		Location getLocation();
		
		/**
		 * Notifies the Activity that the restaurant info requested to be
		 * updated.
		 * 
		 * @param rest
		 *            Updated Restaurant Info
		 */
		void onRestaurantInfoUpdate(RestaurantInfo rest);
		
		/**
		 * Adds this image to the restaurant.
		 * @param b Bitmap to add to Restaurant as an Image
		 */
		void onAddImageToRestaurant(Bitmap b);

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
	private class UserInputHandler implements View.OnClickListener {

		/**
		 * Restaurant information to reference and alter.
		 */
		private final RestaurantInfo mInfo;

		// Buttons Specifically used for taking pictures of
		// the restaurant
		private final ImageButton mTakePicButton, mChoosePicButton, mDeleteButton, mLocateButton;

		// Save Button
		private final Button mSaveButton;

		/**
		 * All the edit text fields to update.
		 */
		private final TextView mAddressLine1, mAddressLine2, mAddressCity, mAddressState,
		mAddressZipCode, mPhoneInput, mHourInput;

		/**
		 * Gallery that holds all the images.
		 */
		private final LinearLayout mGallery;

		/**
		 * Have the view for the current selected Image.
		 */
		private View mCurrentSelected;

		private final AddToRestaurantCallback mAddToRestaurant;

		/**
		 * Given a view that contains all 4 of the image input buttons.
		 * 
		 * Creates a handler for all 4 buttons.
		 * 
		 * @param view Container view of all 4 buttons
		 * @param info Restaurant info to assign.
		 */
		UserInputHandler(View view, RestaurantInfo info) {
			// Restaurant info
			mInfo = info;
			
			// Buttons to receive user button input. 
			mTakePicButton = (ImageButton) view.findViewById(R.id.button_take_new_picture);
			mChoosePicButton = (ImageButton) view.findViewById(R.id.button_add_image_gallery);
			mDeleteButton = (ImageButton) view.findViewById(R.id.button_delete_image);
			mLocateButton = (ImageButton) view.findViewById(R.id.button_get_location);
			mSaveButton = (Button) view.findViewById(R.id.button_save_restaurant_info);

			// Get the input fields of this address
			mAddressLine1 = (TextView) view.findViewById(R.id.edittext_restaurant_address_line1);
			mAddressLine2 = (TextView) view.findViewById(R.id.edittext_restaurant_address_line2);
			mAddressCity = (TextView) view.findViewById(R.id.edittext_restaurant_address_city);
			mAddressState = (TextView) view.findViewById(R.id.edittext_restaurant_address_state);
			mAddressZipCode = (TextView) view.
					findViewById(R.id.edittext_restaurant_address_zipcode);
			mPhoneInput = (TextView) view.findViewById(R.id.edittext_restaurant_phone);
			mHourInput = (TextView) view.findViewById(R.id.edittext_restaurant_hours);
			mGallery = (LinearLayout) view.findViewById(R.id.gallery_restaurant_images);
			
			mAddToRestaurant = new AddToRestaurantCallback();
			
			// Populate the current field based off old data.
			populateAddressContent(info.getAddr());
			
			// Populate the phone number
			mPhoneInput.setText(info.getPhone());
			
			// Populate the 
			mHourInput.setText(info.getHours());
			
			mTakePicButton.setOnClickListener(this);
			mChoosePicButton.setOnClickListener(this);
			mDeleteButton.setOnClickListener(this);
			mSaveButton.setOnClickListener(this);
			mLocateButton.setOnClickListener(this);
			
			if (mLastKnownLocation == null) {
				disableLocationButton();
			} else {
				enableLocationButton();	
			}
		}
		
		/**
		 * Disable the location button.
		 */
		void disableLocationButton() {
			mLocateButton.setEnabled(false);
			mLocateButton.setImageResource(R.drawable.device_access_location_off);
		}
		
		/**
		 * Enable the location button.
		 */
		void enableLocationButton() {
			mLocateButton.setEnabled(true);
			mLocateButton.setImageResource(R.drawable.device_access_location_found);
		}
		
		/**
		 * Adds an actual image of the restaurant to. 
		 * @param restaurantImage Adds a view containing a reference image to the index
		 * @return id of the image in the view;
		 */
		public int addToGallery(View restaurantImage) {
			// Add the view to the current gallery
			mGallery.addView(restaurantImage);

			restaurantImage.setEnabled(true);
			// Set it so we can select the current image
			restaurantImage.setOnClickListener(this);

			// set the selected image to this image
			if (mCurrentSelected == null) {
				setSelected(restaurantImage);
			}
			return mGallery.indexOfChild(restaurantImage);
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
				// Handle image deletion
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
						
						// Update the next child.
						View nextChild = mGallery.getChildAt(0);
						if (nextChild != null) {
							setSelected(nextChild);
						}
					}
				});
				builder.setNegativeButton("Cancel", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
				
			} else if (v == mLocateButton) {
				// Use the geocoder.
				updateAddress();
 			} else if (v == mSaveButton) {
				// Build on the new data.
				mInfo.setAddr(fieldsToAddress());
				String phoneNum = PhoneNumberUtils.formatNumber(mPhoneInput.getText().toString());
				mInfo.setPhone(phoneNum);
				mPhoneInput.setText(phoneNum);
				mInfo.setHours(mHourInput.getText().toString());		
				mListener.onRestaurantInfoUpdate(mInfo);
			} 
		}
		
		/**
		 * Update address from geocoder.
		 */
		private void updateAddress() {
			try {
				List<Address> addresses = mGeocoder.getFromLocation(
						mLastKnownLocation.getLatitude(),
						mLastKnownLocation.getLongitude(), 1);
				if (addresses.size() >= 1) {
					Address add = addresses.get(0);
					// Set the new address 
					mInfo.setAddr(add);
					populateAddressContent(add);
				}
			} catch (IOException e) {
				disableLocationButton();
			}
		}
		
		/**
		 * Populates all the necessary views with the current data 
		 * of the address.
		 * @param add Address to populate views with.
		 */
		private void populateAddressContent(Address add) {
			String line1 = add.getAddressLine(0);
			String line2 = add.getAddressLine(1);
			String city = add.getLocality();
			String state = add.getAdminArea();
			String postal = add.getPostalCode();
			
			String empty = "";
			mAddressLine1.setText(line1 == null ? empty : line1);
			mAddressLine2.setText(line2 == null ? empty : line2);
			mAddressCity.setText(city == null ? empty : city);
			mAddressState.setText(state == null ? empty : state);
			mAddressZipCode.setText(postal == null ? empty : postal);
		}

		/**
		 * Takes all the field that represent the address.
		 * @return the Address currently represented on the screen.
		 */
		private Address fieldsToAddress() {
			String line1 = mAddressLine1.getText().toString().trim();
			String line2 = mAddressLine2.getText().toString().trim();
			String city = mAddressCity.getText().toString().trim();
			String state = mAddressState.getText().toString().trim();
			String postal = mAddressZipCode.getText().toString().trim();
			
			Address address = new Address(Locale.getDefault());
			String empty = "";
			if (line1 != null && !line1.equals(empty)) {
				address.setAddressLine(0, line1);
			}
			if (line2 != null && !line2.equals(empty)) {
				address.setAddressLine(1, line2);
			}
			if (city != null && !city.equals(empty)) {
				address.setLocality(city);
			}
			if (state != null && !state.equals(empty)) {
				address.setAdminArea(state);
			}
			if (postal != null && !postal.equals(empty)) {
				address.setPostalCode(postal);
			}
			return address;
		}
		
		/**
		 * Sets the restaurant Image as the currently selected image.
		 * If the current image is the default image then not action is needed.
		 * @param restaurantImage To set as selected.
		 */
		private void setSelected(View restaurantImage) {
			
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
