package uw.cse.dineon.user.restaurantselection;

import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.checkin.IntentIntegrator;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * @author mhotan
 */
public class RestaurantSelectionButtonsFragment extends Fragment 
implements View.OnClickListener {

	private static final String TAG = RestaurantSelectionButtonsFragment.class.getSimpleName();
	
	/**
	 * Activity that will called when user selects buttons.
	 * on this fragments view 
	 */
	private OnClickListener mListener;
	
	/**
	 * References to Buttons on this display.
	 */
	private ImageButton mCheckInButton, mNearbyButton;
	private ImageButton mFriendsFavoritesButton, mUserFavoritesButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_restaurant_selection_buttons,
				container, false);
		
		// Assign buttons
		mNearbyButton = (ImageButton) view.findViewById(R.id.button_nearby_restaurants);
		mFriendsFavoritesButton = (ImageButton) view.findViewById(R.id.button_friends_favorites);
		mUserFavoritesButton = (ImageButton) view.findViewById(R.id.button_user_favorites);
		mCheckInButton = (ImageButton) view.findViewById(R.id.button_checkin);
		View checkInLine = (View) view.findViewById(R.id.checkinline);
		
		// Add listeners
		mNearbyButton.setOnClickListener(this);
		mFriendsFavoritesButton.setOnClickListener(this);
		mUserFavoritesButton.setOnClickListener(this);
		mCheckInButton.setOnClickListener(this);
		
		if(DineOnUserApplication.getCurrentDiningSession() != null) {
			mCheckInButton.setVisibility(View.GONE);
			checkInLine.setVisibility(View.GONE);
		} else {
			mCheckInButton.setVisibility(View.VISIBLE);
			checkInLine.setVisibility(View.VISIBLE);
		}
		
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof RestaurantSelectionButtonsFragment.OnClickListener) {
			mListener = (OnClickListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet RestaurantSelectionButtonsFragment.OnClickListener");
		}
	}

	/**
	 * @param v View
	 */
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.button_nearby_restaurants:
			mListener.onShowNearbyRestaurants();
			break;
		case R.id.button_friends_favorites:
			mListener.onShowFriendsFavoriteRestaurants();
			break;
		case R.id.button_user_favorites:
			mListener.onShowUserFavorites();
			break;
		case R.id.button_checkin:
			IntentIntegrator integrator = new IntentIntegrator(getActivity());
			integrator.initiateScan();
			break;
		default: 
			Log.e(TAG, "Error onClick with unrecognized view: " + v);
		}
		
	}
	
	/**
	 * Listener for Activity Callbacks.
	 * @author mhotan
	 */
	public interface OnClickListener {
		
		/**
		 * TODO finish.
		 */
		public void onShowNearbyRestaurants(/*TODO Potentially add Filter parameters*/);
		
		/**
		 * TODO finish.
		 */
		public void onShowFriendsFavoriteRestaurants(/*TODO Potentially and Filter parameters*/);
		
		/**
		 * TODO finish.
		 */
		public void onShowUserFavorites(/*TODO Potentially add Filter parameters*/);
		
	}


	

}
