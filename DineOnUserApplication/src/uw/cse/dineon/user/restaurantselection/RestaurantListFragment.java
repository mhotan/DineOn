package uw.cse.dineon.user.restaurantselection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.user.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * TODO finish.
 * @author mhotan
 */
public class RestaurantListFragment extends ListFragment {

	private final String TAG = this.getClass().getSimpleName();

	private RestaurantListListener mListener;
	
	private RestaurantListAdapter mAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		List<RestaurantInfo> mRestaurants = null;
		
		if (mListener != null) {
			mRestaurants = mListener.getRestaurants();
		}
		
		mAdapter = new RestaurantListAdapter(this.getActivity(), mRestaurants);
		setListAdapter(mAdapter);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof RestaurantListListener) {
			mListener = (RestaurantListListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet RestaurantListFragment.RestaurantListListener");
		}
	}
	
	/**
	 * Add a restaurantInfo to the list.
	 * 
	 * @param info info to add.
	 */
	public void addRestaurantInfos(List<RestaurantInfo> infos) {
		//this.mAdapter.clear();
		for (RestaurantInfo r : infos)
			this.mAdapter.add(r);
		this.mAdapter.notifyDataSetChanged();
	}

	/**
	 * TODO finish.
	 * @author mhotan
	 */
	public interface RestaurantListListener {


		// TODO Place parameter preferable a "Restaurant" Instance


		/**
		 * TODO finish.
		 * @param restaurant String
		 */
		public void onRestaurantSelected(RestaurantInfo restaurant);

		/**
		 * TODO finish.
		 * @param restaurant String
		 */
		public void onRestaurantFocusedOn(RestaurantInfo restaurant);
		
		/**
		 * Get the restaurant info object.
		 * @return return the list of restaurant infos.
		 */
		public List<RestaurantInfo> getRestaurants();

	}

	/**
	 * Class that will help display custom list view items.
	 * TODO Change generic type from String to Restaurant
	 * @author mhotan
	 */
	private class RestaurantListAdapter extends ArrayAdapter<RestaurantInfo> {

		private final Context mContext;
		private final List<RestaurantInfo> mValues;

		/**
		 * This is a runtime mapping between "More Info buttons"
		 * and there respective restaurants.
		 * TODO Change String to restaurant;
		 * NOTE (MH): Not exactly sure if this works
		 */
		private final HashMap<View, RestaurantInfo> mInfoMapping;
		
		private final HashMap<View, RestaurantInfo> mRestaurantMapping;

		private final View.OnClickListener mButtonListener, mItemSelectedListener;

		/**
		 * 
		 * @param context Context
		 * @param values List of Strings
		 */
		public RestaurantListAdapter(Context context, List<RestaurantInfo> values) {
			super(context, R.layout.listitem_restaurant, values); // Use our custom row layout
			this.mContext = context;
			if (values != null) {
				this.mValues = new ArrayList<RestaurantInfo>(values);
			} else {
				this.mValues = new ArrayList<RestaurantInfo>();
			}
			mInfoMapping = new HashMap<View, RestaurantInfo>();
			mRestaurantMapping = new HashMap<View, RestaurantInfo>();
			mButtonListener = new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// Have to handle the case where 
					// Listener is not yet instantiated
					if (mListener != null) {
						// Make sure the mapping has the right value
						RestaurantInfo info = mInfoMapping.get(v);
						if (info == null) {
							Log.w(TAG, "Unable to find restaurant " 
									+ "that is attached to this view: " + v);
							return; //FAIL
						}
						
						// Potentially change the attributes of the view
						// To show focus on a particular restaurant
						
						mListener.onRestaurantFocusedOn(info);
					}
				}
			};
			
			mItemSelectedListener = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView restaurantView = (TextView) v.findViewById(R.id.label_restaurant_name);
					//String restName = restaurantView.getText().toString();
					
					RestaurantInfo restaurant = mRestaurantMapping.get(restaurantView);
					
					if (mListener != null && restaurant != null) {
						mListener.onRestaurantSelected(restaurant);
					}
				}
			};
		}
		
		@Override
		public void add(RestaurantInfo r) {
			//super.add(r);
			this.mValues.add(r);
		}

		@Override
		public View getView(int position, View covnertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.listitem_restaurant, parent, false);

			// TODO Here is where we adjust the contents of the list row
			// with attributes determined by the restaurant
			// Now we are just setting the text to be the name of restaurant
			
			TextView restLabel = (TextView) rowView.findViewById(R.id.label_restaurant_name);
			restLabel.setOnClickListener(mItemSelectedListener);
			
			// Get the restaurant name by associating with the position
			String name = mValues.get(position).getName();
			restLabel.setText(name);	

			// TODO Change the button to a more intuitive picture that describes "more info"
			Button moreInfoButton = (Button) rowView.findViewById(R.id.button_restaurant_info);

			// Add to the mapping so listeners can reference it later
			mInfoMapping.put(moreInfoButton, mValues.get(position));
			
			// add mapping to text view so listeners can reference later
			mRestaurantMapping.put(restLabel, mValues.get(position));

			moreInfoButton.setOnClickListener(mButtonListener);

			return rowView;
		}
	}

}
