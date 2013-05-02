package uw.cse.dineon.user.restaurantselection;

import uw.cse.dineon.user.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RestaurantInfoFragment extends Fragment {

	private RestaurantInfoListener mListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_restaurant_info,
				container, false);
		return view;
	}

	public void setRestaurantForDisplay(/*TODO Change to Restaurant data type*/
			String restaurantName) {
		TextView view = (TextView) getView().findViewById(R.id.label_restaurant_info);
		view.setText(restaurantName);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof RestaurantInfoListener) {
			mListener = (RestaurantInfoListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement MyListFragment.OnItemSelectedListener");
		}
	}
	
	public interface RestaurantInfoListener {
		
		public void onMakeReservation(String reservation);
		
		/**
		 * TODO change to Restaurant datatype
		 */
		public String getCurrentRestaurant();
		
	}
}
