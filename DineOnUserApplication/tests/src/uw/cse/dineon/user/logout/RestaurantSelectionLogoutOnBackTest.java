package uw.cse.dineon.user.logout;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.login.UserLoginActivity;
import uw.cse.dineon.user.restaurantselection.RestaurantSelectionActivity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.Button;

public class RestaurantSelectionLogoutOnBackTest extends
		ActivityInstrumentationTestCase2<RestaurantSelectionActivity> {

	private DineOnUser dineOnUser;
	private RestaurantSelectionActivity mActivity;
	private RestaurantInfo testRInfo;
	private Instrumentation mInstrumentation;
	private long time = 5000;

	public RestaurantSelectionLogoutOnBackTest() {
		super(RestaurantSelectionActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// create a user
		dineOnUser = TestUtility.createFakeUser();
		
		// create a restaurant
		Restaurant rest = TestUtility.createFakeRestaurant();
		
		// create a dining session simulation
		DiningSession ds = TestUtility.createFakeDiningSession(
				dineOnUser.getUserInfo(), rest.getInfo());

		Order one = TestUtility.createFakeOrder(5, dineOnUser.getUserInfo());
		ds.addPendingOrder(one);
		dineOnUser.setDiningSession(ds);
		
		Menu m = TestUtility.createFakeMenu();
		rest.getInfo().addMenu(m);
			    
	    // initilize static data
		DineOnUserApplication.setDineOnUser(this.dineOnUser, null);
	    DineOnUserApplication.setCurrentDiningSession(ds);
	    DineOnUserApplication.setRestaurantOfInterest(rest.getInfo());
		// Initialize activity testing parameters
		this.setActivityInitialTouchMode(false);
		mInstrumentation = this.getInstrumentation();
		this.mActivity = this.getActivity();
		
		
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		this.setActivity(null);

		
	}


	
	public void testBackPressed(){
		assertNotNull(this.mActivity);
		this.mInstrumentation.waitForIdleSync();
		this.mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
		this.mInstrumentation.waitForIdleSync();
		AlertDialog ad = this.mActivity.getLogoutAlertDialog();
		final Button BN = ad.getButton(AlertDialog.BUTTON_NEGATIVE);
		assertNotNull(BN);
		this.mActivity.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				BN.performClick();				
			}
			
		});
		this.mInstrumentation.waitForIdleSync();
		ActivityMonitor logMon = this.mInstrumentation.addMonitor(UserLoginActivity.class.getName(), null, false);
		final Button BA = ad.getButton(AlertDialog.BUTTON_POSITIVE);
		assertNotNull(BA);
		this.mActivity.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				BA.performClick();				
			}
		});
		this.mInstrumentation.waitForIdleSync();
		UserLoginActivity ula = (UserLoginActivity) logMon.waitForActivityWithTimeout(5000);
		assertNotNull(ula);
		ula.finish();
		

	}

}
