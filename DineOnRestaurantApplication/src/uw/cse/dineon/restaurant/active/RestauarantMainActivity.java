package uw.cse.dineon.restaurant.active;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.util.DevelopTools;
import uw.cse.dineon.restaurant.DineOnRestaurantActivity;
import uw.cse.dineon.restaurant.LoadingFrament;
import uw.cse.dineon.restaurant.R;
import uw.cse.dineon.restaurant.active.DiningSessionListFragment.DiningSessionListListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

/**
 * This activity supports the main features for this restaurant
 * It presents a swipable interface for Restaurants to view 
 * 
 * 1. Pending Order
 * 2. Pending Requests
 * 3. Current Customers
 *  
 * @author mhotan
 */
public class RestauarantMainActivity extends DineOnRestaurantActivity implements
OrderListFragment.OrderItemListener,
RequestListFragment.RequestItemListener,
DiningSessionListListener {

	private static final String TAG = RestauarantMainActivity.class.getSimpleName(); 

	private static final String[] CONTENT = 
		{"Pending Orders" , "Pending Requests", "Current Sessions"};

	/**
	 * The pager widget, which handles animation and allows swiping horizontally to access previous
	 * and next wizard steps.
	 */
	private ViewPager mPager;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private ScreenSlidePagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant_main);

		mPager = (ViewPager) findViewById(R.id.pager_restaurant_main);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
	}

	@Override
	protected void updateUI() {
		super.updateUI();

		if (getRestaurant() == null) {
			Log.e(TAG, "Something messed up no restaurant available");
			return;
		}

		// Tells adapter to refresh.
		mPagerAdapter.notifyDataSetChanged();
	}

	@Override
	protected void addDiningSession(DiningSession session) {
		super.addDiningSession(session);

		// Update our UI for the current dining session
		Fragment f = mPagerAdapter.getCurrentFragment();
		if (f != null && f instanceof DiningSessionListFragment) {
			DiningSessionListFragment frag = (DiningSessionListFragment) f;
			frag.addDiningSession(session);
		}
	}

	@Override
	protected void removeDiningSession(DiningSession session) {
		// Update our UI for the current dining session
		Fragment f = mPagerAdapter.getCurrentFragment();
		if (f != null && f instanceof DiningSessionListFragment) {
			DiningSessionListFragment frag = (DiningSessionListFragment) f;
			frag.removeDiningSession(session);
		}

		super.removeDiningSession(session);
	}

	@Override
	protected void addOrder(Order order) {
		super.addOrder(order);

		// Update our UI for the current added Order 
		Fragment f = mPagerAdapter.getCurrentFragment();
		if (f != null && f instanceof OrderListFragment) {
			OrderListFragment frag = (OrderListFragment) f;
			frag.addOrder(order);
		}
	}

	@Override
	protected void completeOrder(Order order) {

		// Update our UI for the current added Order 
		Fragment f = mPagerAdapter.getCurrentFragment();
		if (f != null && f instanceof OrderListFragment) {
			OrderListFragment frag = (OrderListFragment) f;
			frag.deleteOrder(order);
		}

		super.completeOrder(order);
	}

	@Override 
	protected void addCustomerRequest(CustomerRequest request) {
		super.addCustomerRequest(request);

		// Update our UI for the current added Request
		Fragment f = mPagerAdapter.getCurrentFragment();
		if (f != null && f instanceof RequestListFragment) {
			RequestListFragment frag = (RequestListFragment) f;
			frag.addRequest(request);
		}	
	}

	@Override
	protected void removeCustomerRequest(CustomerRequest request) {

		// Update our UI for the current added Request
		Fragment f = mPagerAdapter.getCurrentFragment();
		if (f != null && f instanceof RequestListFragment) {
			RequestListFragment frag = (RequestListFragment) f;
			frag.deleteRequest(request);
		}	

		super.removeCustomerRequest(request);
	}


	//////////////////////////////////////////////////////////////////////
	////	Listener for OrderDetailFragment.OrderDetailListener
	////	For Fragment call backs  
	//////////////////////////////////////////////////////////////////////

	@Override
	public List<DiningSession> getCurrentSessions() {
		if (getRestaurant() != null) {
			return getRestaurant().getSessions();
		}
		Log.w(TAG, "[getCurrentSessions] Restaurant is null");
		return new ArrayList<DiningSession>();
	}

	@Override
	public List<CustomerRequest> getCurrentRequests() {
		if (getRestaurant() != null) {
			return getRestaurant().getCustomerRequests();
		}
		Log.w(TAG, "[getCurrentRequests] Restaurant is null");
		return new ArrayList<CustomerRequest>();
	}

	@Override
	public List<Order> getCurrentOrders() {
		if (getRestaurant() != null) {
			return getRestaurant().getPendingOrders();
		}
		Log.w(TAG, "[getCurrentOrders] Restaurant is null");
		return new ArrayList<Order>();
	}

	@Override
	public void onRequestRequestDetail(CustomerRequest request) {
		DevelopTools.getUnimplementedDialog(this, null);

		//		Intent intent = new Intent(getApplicationContext(),
		//				RequestDetailActivity.class);
		//		startActivity(intent);
	}

	@Override
	public void onRequestOrderDetail(Order order) {
		DevelopTools.getUnimplementedDialog(this, null);
		//		Intent intent = new Intent(getApplicationContext(),
		//				OrderDetailActivity.class);
		//		startActivity(intent);
	}


	@Override
	public void onAssignStaffToRequest(CustomerRequest request, String staff) {
		// TODO Implement Add a field in customer request
		// Assigns a staff to customer request.
		Log.i(TAG, "Staff: " + staff + " assigned to customer request.");
	}

	@Override
	public void onRemoveRequest(CustomerRequest request) {
		// Use this and the super class to appropiately remove
		// the customer request
		removeCustomerRequest(request);
	}	

	@Override
	public void onProgressChanged(Order order, int progress) {
		// TODO Implement
		Log.i(TAG, "Progress of order: " + order + " changed to " + progress);
	}

	@Override
	public void onOrderComplete(Order order) {
		// Use this and the super class to appropriately complete
		// the Order
		completeOrder(order);
	}

	//////////////////////////////////////////////////////////////////////
	////	Methods overriden from DineOnRestaurantActivity
	//////////////////////////////////////////////////////////////////////

	/**
	 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
	 * sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

		private Fragment mCurrent;

		/**
		 * This is a PageAdapter to control Restaurant displays
		 * that show orders, customer requests, and sessions.
		 * @param fragmentManager Fragment manager of this activity
		 */
		public ScreenSlidePagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) {
			Restaurant rest = getRestaurant();

			// There is no restaurant so show loading screen.
			if (rest == null) {
				return new LoadingFrament();
			}

			// Narrow in position
			position = Math.min(Math.max(position, 0), CONTENT.length - 1);

			Fragment f;
			switch (position) {
			case 0:
				f = new OrderListFragment();
				break;
			case 1:
				f = new RequestListFragment();
				break;
			default:
				f = new DiningSessionListFragment();
			}

			mCurrent = f;
			return mCurrent;
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

		/**
		 * Returns the a reference to the current fragment in focus. 
		 * @return Fragment user is looking at
		 */
		public Fragment getCurrentFragment() {
			return mCurrent;
		}
	}

}
