package uw.cse.dineon.user.login.test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.bill.CurrentBillActivity;
import uw.cse.dineon.user.bill.CurrentOrderActivity;
import uw.cse.dineon.user.login.UserLoginActivity;
import uw.cse.dineon.user.restaurant.home.RestaurantHomeActivity;
import uw.cse.dineon.user.restaurantselection.RestaurantSelectionActivity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;

public class UserLoginActivityTest extends
		ActivityInstrumentationTestCase2<UserLoginActivity> {

	private UserLoginActivity mActivity;
	private DineOnUser dineOnUser;
	private EditText et_uname, et_passwd;
	private Instrumentation mInstrumentation;
	private Restaurant rest;
	
	public UserLoginActivityTest() {
		super(UserLoginActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ParseUser user = new ParseUser();
		user.setUsername("testUser");
		user.setPassword("12345");
		
		ParseUser restUser = new ParseUser();
		restUser.setUsername("testRestUser");
		restUser.setPassword("12345");
		
		dineOnUser = new DineOnUser(user);
		
		rest = new Restaurant(restUser);
		DiningSession ds = 
				new DiningSession(10, new Date(), dineOnUser.getUserInfo(), rest.getInfo());
		
		List<CurrentOrderItem> mi = TestUtility.createFakeOrderItems(3);
		Order one = new Order(1, dineOnUser.getUserInfo(), mi);
		ds.addPendingOrder(one);
		dineOnUser.setDiningSession(ds);
		Menu m = TestUtility.createFakeMenu();
		m.addNewItem(mi.get(0).getMenuItem());
		rest.getInfo().addMenu(m);
		this.setActivityInitialTouchMode(false);
		mInstrumentation = this.getInstrumentation();
	    Intent addEvent = new Intent();
	    setActivityIntent(addEvent);
	    
	    DineOnUserApplication.setDineOnUser(dineOnUser);
	    DineOnUserApplication.setCurrentDiningSession(ds);
	    
		mActivity = getActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testOnLoginWithDiningSession() {
		et_uname = (EditText) mActivity.findViewById(uw.cse.dineon.user.R.id.input_login_email);
		et_passwd = (EditText) mActivity.findViewById(R.id.input_login_password);
		int time = 5000;
		final DineOnUser DO = this.dineOnUser;
		mActivity.runOnUiThread(new Runnable() {

	      public void run() {
	          et_uname.setText("");
	          et_passwd.setText("");
              Button loginButton = (Button) mActivity.findViewById(R.id.button_login);
              loginButton.performClick();
	          }
		});

		ActivityMonitor monRia = this.mInstrumentation.addMonitor(
			RestaurantHomeActivity.class.getName(), null, false);
    	mActivity.startRestSelectionAct(DO);
	  	RestaurantHomeActivity rha = (RestaurantHomeActivity) 
	  			mInstrumentation.waitForMonitorWithTimeout(monRia, time);
	  	assertNotNull(rha);
	  	rha.finish();

	}
	
	public void testOnLoginWithOutDiningSession() {
		this.dineOnUser.setDiningSession(null);
		et_uname = (EditText) mActivity.findViewById(uw.cse.dineon.user.R.id.input_login_email);
		et_passwd = (EditText) mActivity.findViewById(R.id.input_login_password);
		int time = 5000;
		final DineOnUser DO = this.dineOnUser;
		mActivity.runOnUiThread(new Runnable() {

	      public void run() {
	          et_uname.setText("");
	          et_passwd.setText("");
              Button loginButton = (Button) mActivity.findViewById(R.id.button_login);
              loginButton.performClick();
	          }
		});

		ActivityMonitor monRsa = this.mInstrumentation.addMonitor(
			RestaurantSelectionActivity.class.getName(), null, false);
    	mActivity.startRestSelectionAct(DO);
	  	RestaurantSelectionActivity rsa = (RestaurantSelectionActivity) 
	  			mInstrumentation.waitForMonitorWithTimeout(monRsa, time);
	  	assertNotNull(rsa);
	  	rsa.finish();

	}
}
