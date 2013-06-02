package uw.cse.dineon.user.restaurant.home;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.library.image.ImageCache.ImageGetCallback;
import uw.cse.dineon.library.image.ImageObtainable;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.checkin.IntentIntegrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * Fragment that presents the information about a particular restaurant.
 * @author mhotan
 */
public class RestaurantInfoFragment extends Fragment implements OnClickListener {

	private static final int IMAGEVIEW_WIDTH = 250;
	private static final int IMAGEVIEW_HEIGHT = 250;

	private static final String TAG = RestaurantInfoFragment.class.getSimpleName(); 

	private RestaurantInfoListener mListener;

	private RestaurantInfo mRestaurant;

	private AlertDialog mAlert;

	// UI Components
	private TextView mRestNameLabel, mAddressLabel, mHoursLabel;
	private RatingBar mRatingBar;
	private LinearLayout mGallery;
	private ImageButton mReqButton;
	private ImageView mDefaultImage;
	private TextView messageHeader;
	private Spinner mSpinner;
	private ArrayList<String> mRequests;
	private ImageButton mCheckInButton;

	@Override
	public void onCreate(Bundle onSavedInstance) {
		super.onCreate(onSavedInstance);
		setRetainInstance(true);

		// Get the list from the activity.
		mRestaurant = mListener.getCurrentRestaurant();

		mAlert = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_restaurant_info,
				container, false);

		mRestNameLabel = (TextView) view.findViewById(R.id.label_restaurant_info);
		mAddressLabel = (TextView) view.findViewById(R.id.label_restaurant_address);
		mHoursLabel = (TextView) view.findViewById(R.id.label_restaurant_hours);
		mRatingBar = (RatingBar) view.findViewById(R.id.ratingbar_restaurant);
		mGallery = (LinearLayout) view.findViewById(R.id.gallery_restaurant_images);
		mDefaultImage = (ImageView) view.findViewById(R.id.image_restaurant_placeholder);
		mSpinner = (Spinner) view.findViewById(R.id.spinner_request_to_send);
		mCheckInButton = (ImageButton) view.findViewById(R.id.button_checkin);
		mCheckInButton.setOnClickListener(this);
		View checkInLine = (View) view.findViewById(R.id.checkinline);

		if(DineOnUserApplication.getCurrentDiningSession() != null) {
			mCheckInButton.setVisibility(View.GONE);
			checkInLine.setVisibility(View.GONE);
		} else {
			mCheckInButton.setVisibility(View.VISIBLE);
			checkInLine.setVisibility(View.VISIBLE);
		}

		String[] requests = getActivity().getResources().
				getStringArray(R.array.request_list);
		mRequests = new ArrayList<String>();
		for (String r: requests) {
			mRequests.add(r);
		}		

		messageHeader = (TextView) view.findViewById(R.id.label_message_waiter_header);
		mSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_list_item_1, mRequests));

		mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				String request = (String) mSpinner.getSelectedItem();
				if(request.equals("Custom Request")) {
					getRequestDescription();
					mSpinner.setSelection(0);
				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				return;
			}
		});

		mReqButton = (ImageButton) view.findViewById(R.id.button_request);
		mReqButton.setOnClickListener(this);

		// if not in a dining session w/ this restaurant do not display
		if(DineOnUserApplication.getCurrentDiningSession() == null 
				|| !DineOnUserApplication.getCurrentDiningSession().
				getRestaurantInfo().getName().equals(mRestaurant.getName())) {
			mReqButton.setVisibility(View.GONE);
			mSpinner.setVisibility(View.GONE);
			messageHeader.setVisibility(View.GONE);

		} else {
			mReqButton.setVisibility(View.VISIBLE);
			mSpinner.setVisibility(View.VISIBLE);
			messageHeader.setVisibility(View.VISIBLE);
		}

		// Update the display
		setRestaurantForDisplay(mRestaurant);

		View v = view.findViewById(R.id.button_user_favorites);
		if(v != null && v instanceof ImageButton) {
			this.determineFavorite((ImageButton) v, this.mRestaurant);
		} else {
			Log.d(TAG, "Favorites button not found.");
		}

		return view;
	}

	/**
	 * Sets display features for this fragment to this argument.
	 * @param restaurant Restaurant to present
	 */
	public void setRestaurantForDisplay(RestaurantInfo restaurant) {
		if (restaurant == null) {
			Log.e(TAG, "Unable to populate fragment with null restaurant");
			return;
		}

		this.mRestaurant = restaurant;

		mRestNameLabel.setText(mRestaurant.getName());
		mAddressLabel.setText(mRestaurant.getReadableAddress());
		mHoursLabel.setText(mRestaurant.getHours());

		// TODO Fix this so it is not random.
		mRatingBar.setNumStars(5);
		mRatingBar.setMax(mRatingBar.getNumStars());
		mRatingBar.setRating((float)(Math.random() * mRatingBar.getNumStars()));

		// Dump the current views in the gallery
		mGallery.removeAllViews();

		List<DineOnImage> images = mRestaurant.getImageList();
		for (DineOnImage image : images) {
			// Create a view for each image.
			final Context CTX = getActivity();
			final ViewGroup CONTAINER = getStandardLinearLayout(CTX); 
			// Fill with place holder
			CONTAINER.addView(getLoadingImageProgressDialog(CTX));
			mGallery.addView(CONTAINER);
			// Ask the listener to get the image
			mListener.onGetImage(image, new ImageGetCallback() {

				@Override
				public void onImageReceived(Exception e, Bitmap b) {
					// Upon completion remove all the views
					CONTAINER.removeAllViews();
					if (e == null) {
						// Add a view for each image
						CONTAINER.addView(produceView(CTX, b));
					} else {
						// Produce a view for unable to dl
						CONTAINER.addView(
								produceView(CTX, R.drawable.restaurant_photo_placeholder));	
					}
					CONTAINER.invalidate();
				}
			});
		}

		if(images.size() != 0) {
			mDefaultImage.setVisibility(View.GONE);
		} else {
			mDefaultImage.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof RestaurantInfoListener) {
			mListener = (RestaurantInfoListener) activity;
		} 
		else {
			throw new ClassCastException(activity.toString()
					+ " must implement MyListFragment.OnItemSelectedListener");
		}
	}

	/**
	 * Determines whether the given restaurant is a favorite of the current user.
	 * @param ib ImageButton
	 * @param ri RestaurantInfo 
	 */
	public void determineFavorite(ImageButton ib, RestaurantInfo ri) {
		DineOnUser dou = DineOnUserApplication.getDineOnUser();
		if(dou == null) {
			Log.d(TAG, "Your DineOnUser was null.");
			return;
		}
		if(this.mRestaurant == null) {
			Log.d(TAG, "The RestaurantInfo was null for some reason.");
			return;
		}
		assignFavoriteImageResource(ib, dou, ri);
		ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageButton ib = (ImageButton) v;
				if(ib.getTag().equals("notFavorite")) {
					favoriteRestaurant(mRestaurant);
				} else if(ib.getTag().equals("isFavorite")) {
					unFavoriteRestaurant(mRestaurant);
				}	
			}
		});

	}

	/**
	 * Assigns the given restaurant as a favorite of the given user.
	 * @param ib ImageButton
	 * @param dou DineOnUser to add a favorite to
	 * @param ri RestaurantInfo to add to the user's favorites
	 */
	public void assignFavoriteImageResource(ImageButton ib,
			DineOnUser dou,
			RestaurantInfo ri) {
		if(!dou.isFavorite(ri)) {
			ib.setImageResource(R.drawable.addtofavorites);
			ib.setTag("notFavorite");
		} else {
			ib.setImageResource(R.drawable.is_favorite);
			ib.setTag("isFavorite");
		}
	}

	/**
	 * Add restaurant as a favorite to the current user.
	 * @param ri RestaurantInfo
	 */
	public void favoriteRestaurant(RestaurantInfo ri) {
		DineOnUserApplication.getDineOnUser().addFavorite(ri);
		ImageButton ib = (ImageButton) this.getView().findViewById(R.id.button_user_favorites);
		if(ib != null) {
			this.assignFavoriteImageResource(ib, DineOnUserApplication.getDineOnUser(), ri);
		}
		DineOnUserApplication.getDineOnUser().saveInBackGround(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if(e != null) {
					Log.d(TAG, "the callback for saving favorited failed.\n" + e.getMessage());
				}
			}
		});
	}

	/**
	 * Delete restaurant from current user's favorite list.
	 * @param ri RestaurantInfo
	 */
	public void unFavoriteRestaurant(RestaurantInfo ri) {
		DineOnUserApplication.getDineOnUser().removeFavorite(ri);
		ImageButton ib = (ImageButton) this.getView().findViewById(R.id.button_user_favorites);
		if(ib != null) {
			this.assignFavoriteImageResource(ib, DineOnUserApplication.getDineOnUser(), ri);
		}
		DineOnUserApplication.getDineOnUser().saveInBackGround(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if(e != null) {
					Log.d(TAG, "the callback for saving unfavorited failed.\n" + e.getMessage());
				}
			}
		});
	}

	/**
	 * Gets a new instance of the correct layout of that is capable of holding an
	 * ImageView made with produceView.  This is particular to this fragment.
	 * @param ctx Context to create Layout in.
	 * @return LinearLayout that contain an ImageView
	 */
	public static LinearLayout getStandardLinearLayout(Context ctx) {
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
	public static ImageView produceView(Context ctx, Bitmap b) {
		ImageView imageView = new ImageView(ctx);
		imageView.setLayoutParams(new LayoutParams(
				IMAGEVIEW_WIDTH, 
				IMAGEVIEW_HEIGHT));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageBitmap(b);
		return imageView;
	}

	/**
	 * This produces an image view that contains the bitmap.
	 * @param ctx Context to create image view in
	 * @param resId Resource id of image
	 * @return ImageView with image centered and cropped appropiately.
	 */
	public static ImageView produceView(Context ctx, int resId) {
		ImageView imageView = new ImageView(ctx);
		imageView.setLayoutParams(new LayoutParams(
				IMAGEVIEW_WIDTH, 
				IMAGEVIEW_HEIGHT));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageResource(resId);
		return imageView;
	}

	/**
	 * Returns general image loading progess dialog.
	 * @param ctx Context to create progress bar in.
	 * @return Indeterminate progress bar.
	 */
	public static View getLoadingImageProgressDialog(Context ctx) {
		ProgressBar p = new ProgressBar(ctx);
		p.setIndeterminate(true);
		return p;
	}

	/**
	 * Interface for Activity callbacks.
	 * @author mhotan
	 */
	public interface RestaurantInfoListener extends ImageObtainable, RestaurantRetrievable {

		/**
		 * Notifies activity that user request to make a reservation.
		 * @param reservation Reservation the user.
		 */
		void onMakeReservation(String reservation);

	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.button_request) {
			String request = (String) mSpinner.getSelectedItem();
			if(request.equals("Custom Request")) {
				getRequestDescription();
			} else {
				sendRequest(request);
			}

		} else if(v.getId() == R.id.button_checkin) {
			IntentIntegrator integrator = new IntentIntegrator(getActivity());
			integrator.initiateScan();
		}
	}

	/**
	 * Helper that brings up alert box for sending customer requests.
	 */
	private void getRequestDescription() {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("Send a Request");
		alert.setMessage("Send a request to the restaurant.");
		final View AV = getLayoutInflater(getArguments()).inflate(
				R.layout.alert_build_request, null);
		alert.setView(AV);
		alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface d, int arg1) {
				String desc = ((EditText) AV
						.findViewById(R.id.input_request_description)).getText()
						.toString();
				sendRequest(desc);
			}
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// Do nothing
			}
		});
		this.mAlert = alert.show();
	}

	/**
	 * Destroy the alert dialog if in view.
	 */
	public void destroyAlertDialog() {
		if (this.mAlert != null) {
			this.mAlert.cancel();
			this.mAlert = null;
		}
	}

	/**
	 * @param str String request to send to Restaurant.
	 */
	private void sendRequest(String str) {
		if(getActivity() instanceof RestaurantHomeActivity) {
			RestaurantHomeActivity act = (RestaurantHomeActivity) getActivity();
			act.onRequestMade(str);
		}
	}


}
