package uw.cse.dineon.user.restaurantselection;

import java.util.Collection;
import java.util.List;

import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.animation.ExpandAnimation;
import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.library.image.ImageCache.ImageGetCallback;
import uw.cse.dineon.library.image.ImageObtainable;
import uw.cse.dineon.user.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * List of Restaurants the user can choose from.  This just presents a view that the user 
 * @author mhotan
 */
public class RestaurantListFragment extends ListFragment {

	private final String TAG = this.getClass().getSimpleName();

	private RestaurantListListener mListener;
	
	private RestaurantListAdapter mAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		List<RestaurantInfo> mRestaurants = mListener.getRestaurants();
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
	 * @param infos info to add.
	 */
	public void addRestaurantInfos(List<RestaurantInfo> infos) {
		this.mAdapter.clear();
		for (RestaurantInfo r : infos) {
			this.mAdapter.add(r);
		}
	}
	
	/**
	 * Clears the current list of restaurants.
	 */
	public void clearRestaurants() {
		mAdapter.clear();
	}
	
	/**
	 * Notifies this fragment that the state has just changed.
	 */
	public void notifyStateChange() {
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * For any activity that wishes to contain this fragment then that
	 * activity must implement this interface.
	 * @author mhotan
	 */
	public interface RestaurantListListener extends ImageObtainable {

		/**
		 * 
		 * @param restaurant Restaurant that was chosen
		 */
		public void onRestaurantSelected(RestaurantInfo restaurant);
		
		/**
		 * Returns a non null list of restaurant info to present.
		 * @return return the list of restaurant infos.
		 */
		public List<RestaurantInfo> getRestaurants();

	}

	/**
	 * Class that will help display custom list view items of Restaurants.
	 * 
	 * Given a list of restaurant information instances produce a list displaying all the 
	 * restaraunts.  Each item allows the user to select 
	 * 
	 * @author mhotan
	 */
	private class RestaurantListAdapter extends ArrayAdapter<RestaurantInfo> {

		private final Context mContext;

		/**
		 * 
		 * 
		 * @param context Context 
		 * @param values List of Strings
		 */
		public RestaurantListAdapter(Context context, List<RestaurantInfo> values) {
			super(context, R.layout.listitem_restaurant_top, values); // Use our custom row layout
			this.mContext = context;
		}
		
		@Override
		public void add(RestaurantInfo r) {
			super.add(r);
//			this.mValues.add(r);
			this.notifyDataSetChanged();
		}
		
		@Override
		public void addAll(Collection<? extends RestaurantInfo> collection) {
			super.addAll(collection);
			notifyDataSetChanged();
		}
		
		@Override
		public void clear() {
			super.clear();
//			this.mValues.clear();
			this.notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// Establish a reference to the top and bottom of ex
			View vwTop;
			View vwBot;
			
			LinearLayout layoutView = null; 
			if (convertView == null) { // Created for the first time.
				layoutView = new LinearLayout(mContext);
				layoutView.setOrientation(LinearLayout.VERTICAL);
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				vwTop = inflater.inflate(R.layout.listitem_restaurant_top, null, true);
				vwBot = inflater.inflate(R.layout.listitem_restaurant_bottom, null, true);
				layoutView.addView(vwTop);
				layoutView.addView(vwBot);
				convertView = layoutView;
			} else { // We already have a view for this layout
				vwTop = convertView.findViewById(R.id.restaurant_top);
				vwBot = convertView.findViewById(R.id.restaurant_bottom);
			}
			
			// We have a rsetaurant info to show.
			RestaurantInfo restToShow = getItem(position);

			// For every restaurant to present create a handler for the restaurant;
			// We are creating this view for the very first time
			if (layoutView != null) {
				// Create a handler just for the restaurant.
				new RestaurantHandler(restToShow, vwTop, vwBot);
			}

			return convertView;
		}
		
		/**
		 * Restaurant handler that manages the user input for the views that
		 * correspond to a particular restaurant.
		 * @author mhotan
		 */
		private class RestaurantHandler implements OnClickListener {

			private final RestaurantInfo mInfo;
			private final ImageButton mExpandDownButton, mPickRestaurant;
			private final View mTop, mBottom;
			
			/**
			 * Build this handler from Restaurant info and its corresponging views.
			 * 
			 * @param info Restaurant to associate to.
			 * @param top Top view for the restaurant.
			 * @param bottom bottom view for the restaurant.
			 */
			public RestaurantHandler(RestaurantInfo info, View top, View bottom) {
				mInfo = info;
				
				mTop = top;
				mBottom = bottom;
				
				// Get a reference to all the top pieces 
				final ImageView RESTIMAGE = (ImageView) 
						mTop.findViewById(R.id.image_restaurant_thumbnail);
				TextView restaurantNameView = (TextView) 
						mTop.findViewById(R.id.label_restaurant_title);
				mExpandDownButton = (ImageButton) 
						mTop.findViewById(R.id.button_expand_down);
				
				// Get a reference to all the bottom pieces
				TextView restaurantAddressView = (TextView) 
						mBottom.findViewById(R.id.label_restaurant_address);
				TextView restaurantPhoneView = (TextView) 
						mBottom.findViewById(R.id.label_restaurant_phone);
				TextView restaurantHoursView = (TextView) 
						mBottom.findViewById(R.id.label_restaurant_hours);
				mPickRestaurant = (ImageButton) mBottom.findViewById(R.id.button_proceed);
				
				// Set the values that will never change.
				restaurantNameView.setText(mInfo.getName());
				restaurantAddressView.setText(mInfo.getAddr());
				restaurantHoursView.setText(
						mContext.getResources().getString(R.string.hours_unknown));
				restaurantPhoneView.setText(mInfo.getPhone());
				
				// Set the image of this restaurant
				DineOnImage image = info.getMainImage();
				if (image != null) {
					mListener.onGetImage(image, new ImageGetCallback() {
						
						@Override
						public void onImageReceived(Exception e, Bitmap b) {
							if (e == null) {
								// We got the image so set the bitmap
								RESTIMAGE.setImageBitmap(b);
							}
						}
					});
				}
				
				// Set the bottom view to initial to be invisible
				mBottom.setVisibility(View.GONE);
				
				mTop.setOnClickListener(this);
				mPickRestaurant.setOnClickListener(this);
			}
			
			@Override
			public void onClick(View v) {
				if (v == mTop) { 
					int bottomVisibility = mBottom.getVisibility();
					// Expand the bottom view if it is not shown
					// Hide the expand down button.
					if (bottomVisibility == View.GONE) {
						mExpandDownButton.setVisibility(View.GONE);
					} else if (bottomVisibility == View.VISIBLE) {
						mExpandDownButton.setVisibility(View.VISIBLE);
					}
					
					// Expand the animation
					ExpandAnimation expandAni = new ExpandAnimation(mBottom, 500);
					mBottom.startAnimation(expandAni);
							
				} else if (v == mPickRestaurant) {
					mListener.onRestaurantSelected(mInfo);
				}
				
			}
			
			
			
		}
	}

}
