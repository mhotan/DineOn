package uw.cse.dineon.restaurant.profile;

import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.restaurant.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Main view that allows the user to access and see their restaurant.
 * @author mhotan
 */
public class RestaurantInfoFragment extends Fragment

{

	private static final String KEY_INFO = "restaurant info";
	
	/**
	 * Restaurant 
	 * @param info Restaurant Info to be prepared
	 * @return
	 */
	public RestaurantInfoFragment newInstance(RestaurantInfo info){
		// prepare a
		RestaurantInfoFragment frag = new RestaurantInfoFragment();
		Bundle args = new Bundle();
//		args.putParcelable(KEY_INFO, info);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_restaurant_info,
				container, false);


		
		
		return view;
	}

}
