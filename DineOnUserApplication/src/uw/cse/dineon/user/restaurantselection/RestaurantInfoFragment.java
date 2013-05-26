package uw.cse.dineon.user.restaurantselection;

import com.parse.ParseException;
import com.parse.SaveCallback;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Fragment that presents the information about a particular restaurant.
 * @author mhotan
 */
public class RestaurantInfoFragment extends Fragment {

	private final String TAG = this.getClass().getSimpleName(); 
	
	private RestaurantInfoListener mListener;
	
	private RestaurantInfo mRestaurant;
	
	@Override
	public void onCreate(Bundle onSavedInstance) {
		super.onCreate(onSavedInstance);
		Bundle extras = getArguments();
		if (extras != null && extras.containsKey(RestaurantInfoActivity.EXTRA_RESTAURANT)) {
			mRestaurant = extras.getParcelable(RestaurantInfoActivity.EXTRA_RESTAURANT);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_restaurant_info,
				container, false);
		
		if (this.mRestaurant != null) {
			// set the title
			TextView title = (TextView) view.findViewById(R.id.label_restaurant_info);
			title.setText(this.mRestaurant.getName());
			
			// set the address
			TextView address = (TextView) view.findViewById(R.id.label_restaurant_address);
			address.setText(this.mRestaurant.getAddr());
			
			// TODO need to add hours field to RestaurantInfo set the hours
			//TextView hours = (TextView) getView().findViewById(R.id.label_restaurant_hours);
			//hours.setText(this.mRestaurant.getHours());

			// TODO set the ratings
			
			// TODO set the image
			
			View v =  view.findViewById(R.id.button_user_favorites);
			if(v != null && v instanceof ImageButton) {
				this.determineFavorite((ImageButton) v, this.mRestaurant); 
			} else {
				Log.d(TAG, "Button not found.");
			}
		}
		
		return view;
	}

	/**
	 * Sets display features for this fragment to this argument.
	 * @param restaurant Restaurant to present
	 */
	public void setRestaurantForDisplay(RestaurantInfo restaurant) {
		TextView view = (TextView) getView().findViewById(R.id.label_restaurant_info);
		view.setText(restaurant.getName());
		
		this.mRestaurant = restaurant;
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
				}
				else if(ib.getTag().equals("isFavorite")) {
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
	 * Interface for Activity callbacks.
	 * @author mhotan
	 */
	public interface RestaurantInfoListener {
		
		/**
		 * Notifies activity that user request to make a reservation.
		 * @param reservation Reservation the user.
		 */
		void onMakeReservation(String reservation);
		
		/**
		 * Returns the current Restaurant to be displayed.
		 * @return null if no restaurant available, other wise the resaurant.
		 */
		RestaurantInfo getCurrentRestaurant();
		
		/**
		 * @param r RestaurantInfo
		 */
		void setCurrentRestaurant(RestaurantInfo r);
		
	}
	
}
