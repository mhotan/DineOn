package uw.cse.dineon.user.restaurantselection;

import uw.cse.dineon.user.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * TODO
 * @author mhotan
 */
public class RestaurantSelectionButtonsFragment extends Fragment 
implements View.OnClickListener {

	private static final String TAG = RestaurantSelectionButtonsFragment.class.getSimpleName();
	
	/**
	 * Activity that will called when user selects buttons
	 * on this fragments view 
	 */
	private OnClickListener mListener;
	
	/**
	 * References to Buttons on this display
	 */
	private ImageButton _NearbyButton, _FriendsFavoritesButton, _UserFavoritesButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_restaurant_selection_buttons,
				container, false);
		
		// Assign buttons
		_NearbyButton = (ImageButton) view.findViewById(R.id.button_nearby_restaurants);
		_FriendsFavoritesButton = (ImageButton) view.findViewById(R.id.button_friends_favorites);
		_UserFavoritesButton = (ImageButton) view.findViewById(R.id.button_user_favorites);
		
		// Add listeners
		_NearbyButton.setOnClickListener(this);
		_FriendsFavoritesButton.setOnClickListener(this);
		_UserFavoritesButton.setOnClickListener(this);
		
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
	 * 
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
		default: 
			Log.e(TAG, "Error onClick with unrecognized view: " + v);
		}
		
	}
	
	/**
	 * TODO
	 * @author mhotan
	 */
	public interface OnClickListener {
		
		/**
		 * TODO
		 */
		public void onShowNearbyRestaurants(/*TODO: Potentially add Filter parameters*/);
		
		/**
		 * TODO
		 */
		public void onShowFriendsFavoriteRestaurants(/*TODO: Potentially and Filter parameters*/);
		
		/**
		 * TODO
		 */
		public void onShowUserFavorites(/*TODO: Potentially add Filter parameters*/);
		
	}


	

}
