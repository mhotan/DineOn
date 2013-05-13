package uw.cse.dineon.restaurant.active;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.restaurant.DineOnRestaurantActivity;
import uw.cse.dineon.restaurant.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

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
CustomerListFragment.CustomerListener {

	private static final String TAG = RestauarantMainActivity.class.getSimpleName(); 

	private static final String[] CONTENT = 
		{"Pending Orders" , "Pending Requests", "Current Customers"};

	/**
	 * The pager widget, which handles animation and allows swiping horizontally to access previous
	 * and next wizard steps.
	 */
	private ViewPager mPager;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private ScreenSlidePagerAdapter mPagerAdapter;

	private List<String> mOrders;
	private List<CustomerRequest> mRequests;

	List<DiningSession> mCustomers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant_main);

		mPager = (ViewPager) findViewById(R.id.pager_restaurant_main);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);

		// TODO Fill and update appropiately
		mOrders = new ArrayList<String>();
		mRequests = new ArrayList<CustomerRequest>();
		mCustomers = new ArrayList<DiningSession>();

		// TODO remove default values

		String[] orders = {"Fried Chicken", "Peanut butter and Jelly", 
				"Fried Rice", "Chicken Noodle Soup", "Butte Balls"};
		for (String s: orders) {
			mOrders.add(s);
		}

		String[] customers = {"Batman", "Robin", "Superman", "Wonderwoman"};
		for (String s: customers) {
//			mCustomers.add(makeDummySession(s));
		}
	}
	
	/**
	 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
	 * sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

		private Fragment mCurrent;

		/**
		 * 
		 * @param fragmentManager Fragment manager of this activity
		 */
		public ScreenSlidePagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) {
			position = Math.min(Math.max(position, 0), CONTENT.length - 1);

			Fragment f;

			switch (position) {
			case 0:
				f = OrderListFragment.newInstance(mOrders);
				break;
			case 1:
				f = RequestListFragment.newInstance(mRequests);
				break;
			default:
				f = CustomerListFragment.newInstance(mCustomers);
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

	/**
	 * Updates the current fragment if it is in focus.
	 * 
	 * @param customer String
	 */
	//TODO Make this take a diningsession
	@SuppressWarnings({ "unused" })
	private void addCustomer(String customer) {
		Fragment f = mPagerAdapter.getCurrentFragment();
		if (f != null && f instanceof CustomerListFragment) {
			CustomerListFragment frag = (CustomerListFragment) f;
//			frag.addCustomer(makeDummySession(customer)); 
		}
//		mCustomers.add(makeDummySession(customer));
	}
	
	/**
	 * Updates the current fragment if it is in focus.
	 * @param order String
	 */
	private void addOrder(String order) {
		Fragment f = mPagerAdapter.getCurrentFragment();
		if (f != null && f instanceof OrderListFragment) {
			OrderListFragment frag = (OrderListFragment) f;
			frag.addOrder(order);
		}
		mOrders.add(order);
	}
	
	/**
	 * Updates the current fragment if it is in focus.
	 * @param request String
	 */
	private void addRequest(CustomerRequest request) {
		Fragment f = mPagerAdapter.getCurrentFragment();
		if (f != null && f instanceof RequestListFragment) {
			RequestListFragment frag = (RequestListFragment) f;
			frag.addRequest(request);
		}
		mRequests.add(request);
	}

	/**
	 * Wrapper to convert a string (user's name) into a
	 * DiningSession. Debug purposes only, should not
	 * use in production.
	 * 
	 * @param name Name of fake user to create inside session
	 * @return A dining session with one user
	 */
//	private DiningSession makeDummySession(String name){
//		DiningSession d = new DiningSession();
//		UserInfo ui = new UserInfo();
//		ui.setName(name);
//		ui.setEmail("user@example.com");
//		ui.setPhone("(123) 555-5050");
//		d.addUser(ui);
//		return d;
//	}

	//////////////////////////////////////////////////////////////////////
	////	Listener for OrderDetailFragment.OrderDetailListener
	////	For Fragment call backs  
	//////////////////////////////////////////////////////////////////////

	@Override
	public List<DiningSession> getCurrentUsers() {
		return mCustomers;
	}

	@Override
	public List<CustomerRequest> getCurrentRequests() {
		return mRequests;
	}

	@Override
	public List<String> getCurrentOrders() {
		return mOrders;
	}

	@Override
	public void onRequestRequestDetail(CustomerRequest request) {
		Intent intent = new Intent(getApplicationContext(),
				RequestDetailActivity.class);
		intent.putExtra(RequestDetailActivity.EXTRA_REQUEST, request.getDescription());
		startActivity(intent);
	}

	@Override
	public void onRequestOrderDetail(String order) {
		Intent intent = new Intent(getApplicationContext(),
				OrderDetailActivity.class);
		intent.putExtra(OrderDetailActivity.EXTRA_ORDER, order);
		startActivity(intent);
	}

	@Override
	public void onAssignStaffToRequest(CustomerRequest request, String staff) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDismissRequest(CustomerRequest request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemoveRequest(CustomerRequest request) {
		mRestaurant.removeCustomerRequest(request);
	}

	@Override
	public void onProgressChanged(String order, int progress) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onOrderComplete(String order) {
		// TODO Auto-generated method stub

	}
	
	//////////////////////////////////////////////////////////////////////
	////	Methods overriden from DineOnRestaurantActivity
	//////////////////////////////////////////////////////////////////////


}
