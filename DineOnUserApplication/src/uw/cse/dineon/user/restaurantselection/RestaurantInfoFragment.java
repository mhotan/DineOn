package uw.cse.dineon.user.restaurantselection;

import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.user.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Fragment that presents the information about a particular restaurant.
 * @author mhotan
 */
public class RestaurantInfoFragment extends Fragment {

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
		 * TODO change to Restaurant datatype
		 * @return null if no restaurant available, other wise the resaurant.
		 */
		RestaurantInfo getCurrentRestaurant();
		
		/**
		 * @param r RestaurantInfo
		 */
		void setCurrentRestaurant(RestaurantInfo r);
		
	}
}
