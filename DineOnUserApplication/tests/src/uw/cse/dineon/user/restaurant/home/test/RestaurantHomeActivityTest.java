package uw.cse.dineon.user.restaurant.home.test;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.restaurant.home.MenuItemDetailActivity;
import uw.cse.dineon.user.restaurant.home.RestaurantHomeActivity;
import uw.cse.dineon.user.restaurant.home.RestaurantHomeMainFragment.RestaurantMenuCategoryAdapter;
import uw.cse.dineon.user.restaurant.home.RestaurantInfoFragment;
import uw.cse.dineon.user.restaurant.home.SubMenuFragment;
import uw.cse.dineon.user.restaurantselection.RestaurantSelectionActivity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class RestaurantHomeActivityTest extends
		ActivityInstrumentationTestCase2<RestaurantHomeActivity> {

	private RestaurantHomeActivity mActivity;
	private DineOnUser dineOnUser;
	private Restaurant rest;
	private Instrumentation mInstrumentation;
	private long time = 5000;
	
	public RestaurantHomeActivityTest() {
		super(RestaurantHomeActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		// create a user
		dineOnUser = TestUtility.createFakeUser();
		
		// create a restaurant
		rest = TestUtility.createFakeRestaurant();
		
		// create a dining session simulation
		DiningSession ds = TestUtility.createFakeDiningSession(
				dineOnUser.getUserInfo(), rest.getInfo());

		Order one = TestUtility.createFakeOrder(5, dineOnUser.getUserInfo());
		ds.addPendingOrder(one);
		dineOnUser.setDiningSession(ds);
		
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
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		this.setActivity(null);
	}

	/**
	 * Test that the UI is going to MenuItemDetailActivity when a
	 * menuitem is focused on
	 */
	public void testOnMenuItemFocusedOn() {
		final MenuItem m = new MenuItem(1, 1, "Fries", "Yum");
		
		ActivityMonitor monRsa = this.mInstrumentation.addMonitor(
				MenuItemDetailActivity.class.getName(), null, false);
		
		final RestaurantHomeActivity RSA = this.mActivity; 
		RSA.runOnUiThread(new Runnable() {
	          public void run() {
	        	  RSA.onMenuItemFocusedOn(m);
	          }
	      });
		mInstrumentation.waitForIdleSync();
		
		MenuItemDetailActivity itemSelect = (MenuItemDetailActivity) monRsa
				.waitForActivityWithTimeout(time);
		assertNotNull(itemSelect);
		itemSelect.finish();
		this.mActivity.finish();
	}
	
	/** 
	 * Test that I can get the current restaurant of focus
	 */
	public void testGetCurrentRestaurant() {
		assertNotNull(this.mActivity.getCurrentRestaurant());
		this.mActivity.finish();
	}
	
	/**
	 * Test onBackPressed to make sure we're directed to the 
	 * RestaurantSelectionActivity
	 */
	public void testOnBackPressed() {
		ActivityMonitor monRha = this.mInstrumentation.addMonitor(
				RestaurantSelectionActivity.class.getName(), null, false);
		
		final RestaurantHomeActivity RHA = this.mActivity; 
		RHA.runOnUiThread(new Runnable() {
	          public void run() {
	        	  RHA.onBackPressed();
	          }
	      });
		
		RestaurantSelectionActivity resSelect = (RestaurantSelectionActivity) monRha
				.waitForActivityWithTimeout(time);
		assertNotNull(resSelect);
		resSelect.destroyProgressDialog();
		mInstrumentation.waitForIdleSync();
		resSelect.finish();
		
		mInstrumentation.waitForIdleSync();		
		this.mActivity.finish();
	}
	
	/**
	 * Test the swiping of tabs in the restaurant home screen.
	 */
	public void testInfoMenuTabsFragmentChange() {
		android.support.v4.view.ViewPager pager = (android.support.v4.view.ViewPager) 
				this.mActivity.findViewById(uw.cse.dineon.user.R.id.pager_menu_info);
		assertNotNull(pager);
		
		PagerAdapter adapter = pager.getAdapter();   
		assertNotNull(adapter);

		final android.support.v4.view.ViewPager PAGER = pager;
		this.mActivity.runOnUiThread(new Runnable() {
			public void run() {
				PAGER.setCurrentItem(1);
			}
		});	      
		mInstrumentation.waitForIdleSync(); 
		this.mActivity.runOnUiThread(new Runnable() {
			public void run() {
				PAGER.setCurrentItem(0);
			}
		});	      
		mInstrumentation.waitForIdleSync();
		this.mActivity.finish();
	}

	/**
	 * Pop up alert dialog and make sure its destroyed.
	 */
	public void testCustomRequestDialog() {
		android.support.v4.view.ViewPager pager = (android.support.v4.view.ViewPager) 
				this.mActivity.findViewById(uw.cse.dineon.user.R.id.pager_menu_info);
		assertNotNull(pager);
		
		PagerAdapter adapter = pager.getAdapter();   
		assertNotNull(adapter);

		final android.support.v4.view.ViewPager PAGER = pager;
		this.mActivity.runOnUiThread(new Runnable() {
			public void run() {
				PAGER.setCurrentItem(0);
			}
		});	
		mInstrumentation.waitForIdleSync();
		
		RestaurantMenuCategoryAdapter rmca = (RestaurantMenuCategoryAdapter)adapter;
		
		RestaurantInfoFragment rInfo = (RestaurantInfoFragment) rmca.getItem(0);
		assertNotNull(rInfo);
		View v = this.mActivity.findViewById(R.id.button_request);
		assertNotNull(v);
		
		final Spinner spinner = (Spinner) this.mActivity.findViewById(R.id.spinner_request_to_send);
		this.mActivity.runOnUiThread(new Runnable() {
			public void run() {
				spinner.setSelection(spinner.getAdapter().getCount() - 1);
			}
		});	
		
		mInstrumentation.waitForIdleSync();
		
		rInfo.destroyAlertDialog();
		this.mActivity.finish();
	}
	
	/**
	 * Test that produceImageView returns a valid ImageView.
	 */
	@SuppressWarnings("static-access")
	public void testProduceImageView() {
		android.support.v4.view.ViewPager pager = (android.support.v4.view.ViewPager) 
				this.mActivity.findViewById(uw.cse.dineon.user.R.id.pager_menu_info);
		assertNotNull(pager);
		
		PagerAdapter adapter = pager.getAdapter();   
		assertNotNull(adapter);

		final android.support.v4.view.ViewPager PAGER = pager;
		this.mActivity.runOnUiThread(new Runnable() {
			public void run() {
				PAGER.setCurrentItem(0);
			}
		});	
		mInstrumentation.waitForIdleSync();
		
		RestaurantMenuCategoryAdapter rmca = (RestaurantMenuCategoryAdapter)adapter;
		
		RestaurantInfoFragment rInfo = (RestaurantInfoFragment) rmca.getItem(0);
		assertNotNull(rInfo);
		View v = this.mActivity.findViewById(R.id.button_request);
		assertNotNull(v);
		
		ImageView mv1 = rInfo.produceView(this.mActivity, R.drawable.chili);
		assertNotNull(mv1);
	
		Bitmap b = BitmapFactory.decodeResource(this.mActivity.getResources(), R.drawable.chili);
		ImageView mv2 = rInfo.produceView(this.mActivity, b);
		assertNotNull(mv2);
			
		this.mActivity.finish();
	}
	
	/**
	 * Simulate a menuitem click and make sure it goes to the detail activity.
	 */
	public void testOnMenuItemClick() {
		android.support.v4.view.ViewPager pager = (android.support.v4.view.ViewPager) 
				this.mActivity.findViewById(uw.cse.dineon.user.R.id.pager_menu_info);
		assertNotNull(pager);
		
		PagerAdapter adapter = pager.getAdapter();   
		assertNotNull(adapter);

		final android.support.v4.view.ViewPager PAGER = pager;
		this.mActivity.runOnUiThread(new Runnable() {
			public void run() {
				PAGER.setCurrentItem(1);
			}
		});	
		mInstrumentation.waitForIdleSync();
		
		RestaurantMenuCategoryAdapter rmca = (RestaurantMenuCategoryAdapter)adapter;
		
		SubMenuFragment menu = (SubMenuFragment) rmca.getItem(1);
		assertNotNull(menu);
		final TextView v = (TextView)this.mActivity.findViewById(R.id.label_more_info_hint);
		assertNotNull(v);
		
		this.mActivity.runOnUiThread(new Runnable() {
			public void run() {
				assertTrue(v.performClick());
			}
		});	
		mInstrumentation.waitForIdleSync();
		
		ActivityMonitor mon = this.mInstrumentation.addMonitor(MenuItemDetailActivity.class.getName(), 
				null, false);
		
		final ImageButton imgBut = (ImageButton)this.mActivity.findViewById(
				R.id.button_about_menuitem);
		assertNotNull(imgBut);
		this.mActivity.runOnUiThread(new Runnable() {
			public void run() {
				assertTrue(imgBut.performClick());
			}
		});
		
		MenuItemDetailActivity detailAct = (MenuItemDetailActivity)
				mon.waitForActivityWithTimeout(5000);
		assertNotNull(detailAct);
		
		detailAct.finish();
		this.mActivity.finish();
	}
	
	/**
	 * Test that the correct restaurant info is being displayed.
	 */
	public void testInfoDisplay() {
		// get address
		TextView address = (TextView) this.mActivity.findViewById(R.id.label_restaurant_address);
		assertEquals(rest.getInfo().getReadableAddress(), address.getText().toString());
		// get hours
		TextView hours = (TextView) this.mActivity.findViewById(R.id.label_restaurant_hours);
		assertEquals(rest.getInfo().getHours(), hours.getText().toString());
		mInstrumentation.waitForIdleSync();
		this.mActivity.finish();
	}
}
