package uw.cse.dineon.restaurant.profile;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * Main view that allows the user to access and see their restaurant.
 * @author mhotan
 */
public class RestaurantInfoFragment extends Fragment {

	private List<View> imageViews;
	
	private InfoChangeListener mListener;

	/**
	 * Returns a fragment that will present the information present in the Restaurant Info object.
	 * @param info Restaurant Info to be prepared to present
	 * @return New image fragment.
	 */
	public static RestaurantInfoFragment newInstance(RestaurantInfo info) {
		// Prepare a Bundle argument
		// for starting an activity with
		RestaurantInfoFragment frag = new RestaurantInfoFragment();
		Bundle args = new Bundle();
//		args.putParcelable(DineOnConstants.KEY_RESTAURANTINFO, info);
		frag.setArguments(args);
		return frag;
	}

	@SuppressWarnings("unused")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// IF there are arguments 
		// then check if there is a restaurant info instance
		// info can be null
		final RestaurantInfo INFO = getArguments() != null ? (RestaurantInfo)
				getArguments().getParcelable(DineOnConstants.KEY_RESTAURANTINFO) : null;

		// Check the view and its state and intialize appropiately
		View view;
		if (isValid(INFO)) {
			view = inflater.inflate(R.layout.fragment_restaurant_info,
					container, false);

			// Reference the gallery to place images of this restaurant
			LinearLayout mGallery = (LinearLayout) view.
					findViewById(R.id.gallery_restaurant_images);
			CheckBox mCheckBox = (CheckBox) view.findViewById(R.id.checkbox_is_default_image);
			ImageButton mButtonAdd = (ImageButton) view.findViewById(R.id.button_add_new_image);
			ImageButton mButtonDelt = (ImageButton) view.findViewById(R.id.button_delete_image);
			final EditText M_PHONE_INPUT = 
					(EditText) view.findViewById(R.id.edittext_restaurant_phone);
			final EditText M_ADDRESS_INPUT = 
					(EditText) view.findViewById(R.id.edittext_restaurant_address);
			Button mSaveButton = (Button) view.findViewById(R.id.button_save_restaurant_info);
			TextView restName = (TextView) view.findViewById(R.id.label_restaurant_name);

			imageViews = new ArrayList<View>();

			// TODO Dynamically add the images from the restaurant Info instance
			// Set the default image to the be the first image
			// View imgView = insertPhoto(String pathToImage)
			// imgView.setOnClickListener(new ImageSelectListener(IMG ID));
			// This will set the selected back ground image
			// imageViews.add(imvView)
			// Should set all default images to white grey background

			// TODO For each image add the correct listener to listener to listen for the
			// user selects another image of focus.

			// TODO Set the default values to the current argument
			M_PHONE_INPUT.setText(INFO.getPhone());
			M_ADDRESS_INPUT.setText(INFO.getAddr());
			restName.setText(INFO.getName());
			

			// TODO Set listener for the buttons
			mSaveButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					INFO.setAddr(M_ADDRESS_INPUT.getText().toString());
					INFO.setPhone(M_PHONE_INPUT.getText().toString());
					
					
					
					INFO.saveInBackGround(new SaveCallback() {

						@Override
						public void done(ParseException arg0) {
							Toast.makeText(getActivity(), "Restaurant Info Updated!", 
									Toast.LENGTH_SHORT).show();
						}
						
					});
					// TODO Auto-generated method stub
					
				}
			});

			// Listener will then Alter the restaurant Info instance.

			// TODO set listener 
			return view;
		} 
		
		// 
		view = inflater.inflate(R.layout.fragment_empty, container, false);
		TextView errorMessage = (TextView) view.findViewById(R.id.label_error);
		errorMessage.setText("Illegal Dining Session: " + INFO);
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof InfoChangeListener) {
			mListener = (InfoChangeListener) activity;
		} 
		else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet RestaurantInfoFragment.InfoChangeListener");
		}
	}

	// TODO Add a customer listener to interact with this fragment

	/**
	 * Returns whether the Restaurant Info instance is valid.
	 * @param info Restaurant Info to check
	 * @return true if RestaurantInfo can be displayed
	 */
	private boolean isValid(RestaurantInfo info) {
		if (info == null) {
			return false;
		}
		
		if (DineOnConstants.DEBUG) {
			// TODO Implement
		}
		return true;
	}

	/**
	 * Listener for this fragment to communicate back to its attached activity.
	 * @author mhotan
	 */
	public interface InfoChangeListener {

		// process is completely replace the restaurant 

		/**
		 * Notifies the Activity that the restaurant info requested to be updated.
		 * @param rest Updated Restuarant Info
		 */
		void onRestaurantInfoUpdate(RestaurantInfo rest);

	}

	/**
	 * Listener for when the user selects on view containing image. 
	 * @author mhotan
	 */
	private class ImageSelectListener implements View.OnClickListener {

		/**
		 * Create a listener associated with this Image Id.
		 * @param imgId int
		 */
		public ImageSelectListener(int imgId) {
			mImgID = imgId;
		}

		/**
		 * Image ID that this view presents.
		 */
		private final int mImgID;

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}

		/**
		 * Gets the associated Image id for this restaurant image.
		 * @return int
		 */
		public int getImageID() {
			return mImgID;
		}

	}

	/**
	 * This provides ONE method of decoding an image to that is.
	 * @param path path of image file to download
	 * @return View
	 */
	View insertPhoto(String path) {
		// Decode the image as a 220 x 220 image
		//		Bitmap bm = decodeSampledBitmapFromUri(path, 220, 220);

		// Add a border around the image
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(250, 250));
		layout.setGravity(Gravity.CENTER);

		// Place the image in the center of the frame
		ImageView imageView = new ImageView(getActivity());
		imageView.setLayoutParams(new LayoutParams(220, 220));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		//		imageView.setImageBitmap(bm);

		layout.addView(imageView);
		return layout;
	}

}
