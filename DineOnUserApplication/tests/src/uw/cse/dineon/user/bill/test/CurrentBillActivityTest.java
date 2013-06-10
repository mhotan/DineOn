package uw.cse.dineon.user.bill.test;

import java.text.NumberFormat;
import java.util.List;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.bill.CurrentBillActivity;
import uw.cse.dineon.user.bill.CurrentBillFragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

public class CurrentBillActivityTest extends
	ActivityInstrumentationTestCase2<CurrentBillActivity> {

	private CurrentBillActivity mActivity;
	private DineOnUser dineOnUser;
	private NumberFormat mFormatter = NumberFormat.getCurrencyInstance();
	
	
	public CurrentBillActivityTest() {
		super(CurrentBillActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		// create a user
		dineOnUser = TestUtility.createFakeUser();
		
		// create a restaurant
		Restaurant rest = TestUtility.createFakeRestaurant();
		
		// create a dining session simulation
		DiningSession ds = TestUtility.createFakeDiningSession(
				dineOnUser.getUserInfo(), rest.getInfo());

		Order one = TestUtility.createFakeOrder(1, dineOnUser.getUserInfo());
		ds.addPendingOrder(one);
		dineOnUser.setDiningSession(ds);
		
		// add am order to the current list
		dineOnUser.setMenuItemToOrder(TestUtility.createFakeMenuItems(1).get(0), 2);
		
		Menu m = TestUtility.createFakeMenu();
		rest.getInfo().addMenu(m);
		
		// Initialize activity testing parameters
		this.setActivityInitialTouchMode(false);
	    Intent addEvent = new Intent();
	    setActivityIntent(addEvent);
	    
	    // initilize static data
	    DineOnUserApplication.setDineOnUser(this.dineOnUser, null);
	    DineOnUserApplication.setCurrentDiningSession(ds);
	    DineOnUserApplication.setRestaurantOfInterest(rest.getInfo());
	    
		mActivity = getActivity();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		this.setActivity(null);

	}

	/**
	 * Test that the bill activity was started
	 */
	public void testOnCreateBillActivity() {
		assertNotNull(mActivity);
		this.mActivity.finish();
	}
	
	/**
	 * Test that the numbers displayed are the actual subtotal and total.
	 */
	public void testCalculateBillAccuracy() {
		// get the actual total for the pending order
		List<Order> orders = DineOnUserApplication.
				getCurrentDiningSession().getOrders();
		double sum = 0.0;
		for (Order order : orders) {
			for (CurrentOrderItem item : order.getMenuItems()) {
				sum += item.getMenuItem().getPrice() * item.getQuantity();
			}
		}
		
		// check that the amounts displayed are  whats expected
		CurrentBillFragment fragment = (CurrentBillFragment)
				this.mActivity.getSupportFragmentManager().
				findFragmentById(R.id.fragment_current_bill);
		assertNotNull(fragment);
		
		TextView mSubTotal = (TextView) this.mActivity.findViewById(R.id.value_order_total);
		TextView mTotalTax = (TextView) this.mActivity.findViewById(R.id.value_order_tax);
		TextView mTotal = (TextView) this.mActivity.findViewById(R.id.value_final_total);
		
		// get displayed numbers
		String subtotal = mSubTotal.getText().toString();
		String tax = mTotalTax.getText().toString();
		String total = mTotal.getText().toString();
		
		String expectedSum = mFormatter.format(sum);
		assertEquals(expectedSum, subtotal);

		String expectedTotal = mFormatter.format(Double.parseDouble(tax.substring(1)) + sum);
		assertEquals(expectedTotal, total);
		
	}
	
}
