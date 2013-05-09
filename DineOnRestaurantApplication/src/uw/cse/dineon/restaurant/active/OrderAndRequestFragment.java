package uw.cse.dineon.restaurant.active;

import uw.cse.dineon.restaurant.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Shows a swipable user interface for the restaurant to
 * view their orders and request.
 * @author mhotan
 */
public class OrderAndRequestFragment extends Fragment {

	private static final String TAG = OrderAndRequestFragment.class.getSimpleName(); 

	private static final String[] CONTENT = {"Pending Orders" , "Pending Requests"};

	private OrderListFragment mOrderListFrag;
	private RequestListFragment mRequestListFrag;
	private OrderAndRequestAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pager_order_request,
				container, false);

		ViewPager pager = (ViewPager)view.findViewById(R.id.pager_orderorrequest);
	
		// Use activity's Fragment Manager
		mAdapter = new OrderAndRequestAdapter(getFragmentManager());
		pager.setAdapter(mAdapter);

		// TODO Need to figure a process of passing the restaurant we chose
		// in the previous process and send to this containing context
		// However we cant assume the previous activity is RestaurantSelectionActivity
		// That wont always be true

		return view;
	}

	/**
	 * Adapter that handles the fragments within the view pager.
	 * @author mhotan
	 */
	private class OrderAndRequestAdapter extends FragmentPagerAdapter {

		/**
		 * 
		 * @param fm FragmentManager
		 */
		public OrderAndRequestAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			// Make sure we are with bounds
			pos = Math.min(Math.max(pos, 0), CONTENT.length - 1);
			switch (pos) {
			case 0:
				return new OrderListFragment();
			default: // Position 1
				return new RequestListFragment();
			}
		}

		
		@Override
		public int getCount() {
			return CONTENT.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			position = Math.max(Math.min(position, CONTENT.length - 1), 0);
			return CONTENT[position];
		}
	}
}
