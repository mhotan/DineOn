package uw.cse.dineon.restaurant.profile;

import java.util.List;

import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.image.DineOnImage;
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
			populateGallery(getActivity(), info, mInputHandler, mListener);

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
		//		Bitmap toAdd = Bitmap.createScaledBitmap(
		//				image, 
		//				IMAGEVIEW_WIDTH - IMAGEVIEW_WIDTH_PADDING, 
		//				IMAGEVIEW_HEIGHT - IMAGEVIEW_HEIGHT_PADDING, 
		//				true);
		//		
		// If image is the first actual image of the restaurant
		// Remove the place holder

		// Get the layout to contain the image.
		Context ctx = getActivity().getApplicationContext();
		LinearLayout layout = getStanderdLinearLayout(ctx);
		ImageView imageViewToAdd = produceView(ctx, image);
		layout.addView(imageViewToAdd);
		mInputHandler.addRestaurantImage(layout, true);
	}

	/**
	 * Replaces the current view with new image.
	 * 
	 * This method is mainl used as a callback for when the
	 * Fragment is waiting for the retrieval of an image.
	 * 
	 * @param container Container to place image
	 * @param image Image of the new images
	 * @param index previously assigned index
	 */
	public void replaceWithImage(ViewGroup container, Bitmap image, int index) {
		container.removeAllViews();
		container.addView(produceView(this.getActivity(), image));
		// TODO Add to handler
		mInputHandler.setActiveAt(index, true);
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
	 * @param ctx Context build UI layouts
	 * @param info Restaurant info to populate the view with.
	 * @param handler Handler for user input
	 * @param listener for getting images
	 */
	private static void populateGallery(Context ctx, RestaurantInfo info, 
			UserInputHandler handler, InfoChangeListener listener) {

		List<DineOnImage> images = info.getImageList();
		for (int i = 0; i < images.size(); ++i) {
			// Get the default layout
			LinearLayout layout = getStanderdLinearLayout(ctx);
			layout.removeAllViews();
			// Get a place holder image before we download
			layout.addView(getLoadingImageProgressDialog(ctx));
			int id = handler.addRestaurantImage(layout, false);
			listener.getThisImage(images.get(i), layout, id);
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
	public interface InfoChangeListener {

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
		 * This handles grabbing the image.
		 * Once the listener obtains the image it should drop all the views within
		 * the layout and add produce the image view via 
		 * 
		 * Pseudo Code
		 * Retrieve image Bitmap b
		 * call replaceWithImage(layout, b, id)
		 * 
		 * @param image Image to be retrieved from the server
		 * @param layout Layout to drop view and add DineOnImage
		 * @param id of image to be downloaded.
		 */
		void getThisImage(DineOnImage image, final ViewGroup layout, final int id);

		/**
		 * Callback that signifies the User would like to take a picture
		 * and add it to the current state of the Restaurant Info.
		 */
		void onRequestTakePicture();

		/**
		 * The user is requesting to take an image from the gallery
		 * and add it to the restaurants images.
		 */
		void onRequestGetPictureFromGallery();

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
		private final TextView mPhoneInput, mAddressInput;
		private final LinearLayout mGallery;

		private View mCurrentDefault;
		private View mCurrentSelected;

		/**
		 * Adds an actual image of the restaurant to. 
		 * @param restaurantImage Adds a view containing a reference image to the index
		 * @param addListener flag that is set if we want this view to listen for events
		 * @return id of the image in the view;
		 */
		public int addRestaurantImage(View restaurantImage, boolean addListener) {
			mGallery.addView(restaurantImage);
			
			if (mCurrentDefault == null) {
				setDefaultImage(restaurantImage);
			}
			if (addListener) {
				restaurantImage.setOnClickListener(this);
			}
			
			return mGallery.indexOfChild(restaurantImage);
		}

		/**
		 * Set the active state of the position in the gallery.
		 * 
		 * @param index index to change 
		 * @param active set to true if we want to add listener, false remove listener
		 */
		public void setActiveAt(int index, boolean active) {
			View v = mGallery.getChildAt(index);
			if (v == null) {
				return;
			}
			if (active) {
				v.setOnClickListener(this);
			} else {
				v.setOnClickListener(null);
			}
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
			mGallery = (LinearLayout) view.findViewById(R.id.gallery_restaurant_images);

			if (mDefaultCheck == null 
					|| mTakePicButton == null 
					|| mChoosePicButton == null 
					|| mDeleteButton == null) {
				throw new IllegalArgumentException(
						"Invalid view, doesn't contain all the relevant sub items.");
			} 
			mDefaultCheck.setOnCheckedChangeListener(this);
			mTakePicButton.setOnClickListener(this);
			mChoosePicButton.setOnClickListener(this);
			mDeleteButton.setOnClickListener(this);
			mSaveButton.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (v == mTakePicButton) {
				mListener.onRequestTakePicture();
			} else if (v == mChoosePicButton) {
				mListener.onRequestGetPictureFromGallery();
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
				if (mCurrentDefault != null) {
					mInfo.setMainImage(mGallery.indexOfChild(mCurrentDefault));
				} else {
					mInfo.setMainImage(-1);
				}
				mListener.onRestaurantInfoUpdate(mInfo);
			} else { // user selected a restaurant image
				if (v == mCurrentDefault) { // If the default image is selected
					// Then do nothing
					return;
				}
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
		 * @param restaurantImage To set as selected.
		 */
		private void setSelected(View restaurantImage) {
			// release the last selected
			if (mCurrentSelected != null) {
				mCurrentSelected.setBackgroundColor(Color.TRANSPARENT);
			}
			// Do nothing if its also the default image
			if (mCurrentDefault != restaurantImage) {
				mCurrentSelected = restaurantImage;
				mCurrentSelected.setBackgroundColor(Color.GRAY);
				mDefaultCheck.setChecked(false);
			} else { // It is the default image
				mDefaultCheck.setChecked(true);
			}
		}
	}
}
