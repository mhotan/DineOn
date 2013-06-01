package uw.cse.dineon.user.bill.test;

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
import uw.cse.dineon.user.bill.CurrentOrderActivity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class CurrentOrderActivityTest extends
		ActivityInstrumentationTestCase2<CurrentOrderActivity> {

	private CurrentOrderActivity mActivity;
	private DineOnUser dineOnUser;
	private Instrumentation mInstrumentation;
	
	Button incButton;
	Button decButton;
	TextView itemQuantity;
	ImageButton deleteButton;
	TextView label;
	TextView mSubtotal;
	TextView  mTax;
	TextView mTotal;
	Button mPlaceOrderButton;

	public CurrentOrderActivityTest() {
		super(CurrentOrderActivity.class);
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
		DineOnUserApplication.setCurrentOrder(TestUtility.createFakeOrderItems(1));
		
		Menu m = TestUtility.createFakeMenu();
		rest.getInfo().addMenu(m);
		
		// Initialize activity testing parameters
		this.setActivityInitialTouchMode(false);
		mInstrumentation = this.getInstrumentation();
	    Intent addEvent = new Intent();
	    setActivityIntent(addEvent);
	    
	    // initilize static data
	    DineOnUserApplication.setDineOnUser(dineOnUser);
	    DineOnUserApplication.setCurrentDiningSession(ds);
	    DineOnUserApplication.setRestaurantOfInterest(rest.getInfo());
	    
		mActivity = getActivity();
		
		incButton = (Button) this.mActivity.findViewById(R.id.button_increment_item);
		decButton = (Button) this.mActivity.findViewById(R.id.button_decrement_item);
		itemQuantity = (TextView) this.mActivity.findViewById(R.id.label_item_quantity);
		deleteButton = (ImageButton) this.mActivity.findViewById(R.id.button_delete);
		label = (TextView) this.mActivity.findViewById(R.id.label_order_item);
		mSubtotal = (TextView) this.mActivity.findViewById(R.id.value_subtotal);
		mTax = (TextView) this.mActivity.findViewById(R.id.value_tax);
		mTotal = (TextView) this.mActivity.findViewById(R.id.value_total);
		mPlaceOrderButton = (Button) this.mActivity.findViewById(R.id.button_place_order);
		
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testOnIncrementItemQuantity() {
		final CurrentOrderActivity RSA = this.mActivity; 
		RSA.runOnUiThread(new Runnable() {
	          public void run() {
	        	  incButton.performClick();
	          }
	      });
		mInstrumentation.waitForIdleSync();
		RSA.finish();
	}
	
	public void testOnDecrementItemQuantity() {
		final CurrentOrderActivity RSA = this.mActivity; 
		RSA.runOnUiThread(new Runnable() {
	          public void run() {
	        	  decButton.performClick();
	          }
	      });
		mInstrumentation.waitForIdleSync();
		RSA.finish();
	}
	
	public void testOnDeleteItem() {
		final CurrentOrderActivity RSA = this.mActivity; 
		RSA.runOnUiThread(new Runnable() {
	          public void run() {
	        	  deleteButton.performClick();
	          }
	      });
		mInstrumentation.waitForIdleSync();
		RSA.finish();
		
	}
}
