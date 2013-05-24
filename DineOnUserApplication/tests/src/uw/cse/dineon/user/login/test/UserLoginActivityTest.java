package uw.cse.dineon.user.login.test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.parse.Parse;
import com.parse.ParseUser;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.TestUtility;
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
import android.widget.Button;
import android.widget.EditText;

public class UserLoginActivityTest extends
		ActivityInstrumentationTestCase2<UserLoginActivity> {

	private UserLoginActivity mActivity;
	private DineOnUser dineOnUser;
	private EditText et_uname, et_passwd;
	private Instrumentation mInstrumentation;
	public UserLoginActivityTest() {
		super(UserLoginActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Parse.initialize(null, "RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", "wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
		ParseUser.enableAutomaticUser();
		ParseUser user = new ParseUser();
		user.setUsername("testUser");
		user.setPassword("12345");
		
		ParseUser restUser = new ParseUser();
		restUser.setUsername("testRestUser");
		restUser.setPassword("12345");
		
		dineOnUser = new DineOnUser(user);
		
		Restaurant rest = new Restaurant(restUser);
		DiningSession ds = 
				new DiningSession(10, new Date(), dineOnUser.getUserInfo(), rest.getInfo());
		
		List<MenuItem> mi = TestUtility.createFakeMenuItems(3);
		Order one = new Order(1, dineOnUser.getUserInfo(), mi);
		ds.addPendingOrder(one);
		dineOnUser.setDiningSession(ds);
		Menu m = TestUtility.createFakeMenu();
		m.addNewItem(mi.get(0));
		rest.getInfo().addMenu(m);
		this.setActivityInitialTouchMode(false);
		mInstrumentation = this.getInstrumentation();
	    Intent addEvent = new Intent();
	    setActivityIntent(addEvent);
		mActivity = getActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testOnLogin() {
		et_uname = (EditText) mActivity.findViewById(uw.cse.dineon.user.R.id.input_login_email);
		et_passwd = (EditText) mActivity.findViewById(R.id.input_login_password);
		
		int time = 10000;
		ActivityMonitor monRsa = this.mInstrumentation.addMonitor(
				RestaurantSelectionActivity.class.getName(), null, false);
  	  	
		final DineOnUser DO = this.dineOnUser;
		mActivity.runOnUiThread(new Runnable() {
	          public void run() {
	              et_uname.setText("");
	              et_passwd.setText("");
	              Button loginButton = (Button) mActivity.findViewById(R.id.button_login);
	              loginButton.performClick();
	              mActivity.startRestSelectionAct(DO);

	          }
		});
		mInstrumentation.waitForIdleSync();
			
		RestaurantSelectionActivity resSelect = (RestaurantSelectionActivity) monRsa
				.waitForActivityWithTimeout(time);
		assertNotNull(resSelect);
		final RestaurantSelectionActivity RSA = resSelect; 
		resSelect.runOnUiThread(new Runnable() {
	          public void run() {
	        	  RSA.destroyProgressDialog();
	        	  List<RestaurantInfo> rlst = new LinkedList<RestaurantInfo>();
	        	  rlst.add(DO.getDiningSession().getRestaurantInfo());
	        	  RSA.addRestaurantInfos(rlst);
	          }
	      });
		mInstrumentation.waitForIdleSync();
		
		ActivityMonitor monRia = this.mInstrumentation.addMonitor(
				RestaurantHomeActivity.class.getName(), null, false);
		resSelect.runOnUiThread(new Runnable() {
	          public void run() {
	      		RSA.diningSessionChangeActivity(DO.getDiningSession());	        
	          }
	      });
		mInstrumentation.waitForIdleSync();
  	  	RestaurantHomeActivity rha = (RestaurantHomeActivity) monRia
  	  			.waitForActivityWithTimeout(time);
  	  	
  	  	assertNotNull(rha);
  	  	
  	  	android.support.v4.view.ViewPager pager = (android.support.v4.view.ViewPager) 
				rha.findViewById(uw.cse.dineon.user.R.id.pager_menu_info);
  	  	assertNotNull(pager);
  	  	
  	  	PagerAdapter adapter = pager.getAdapter(); 	
  	  	assertNotNull(adapter);
  	  	
  	  	final android.support.v4.view.ViewPager PAGER = pager;
  	  	rha.runOnUiThread(new Runnable() {
	          public void run() {
	        	  PAGER.setCurrentItem(1);
	          }
	      });
  	  	
		mInstrumentation.waitForIdleSync();
		
		ActivityMonitor monCurrOrder = this.mInstrumentation.addMonitor(
				CurrentOrderActivity.class.getName(), null, false);
		
		assertNotNull(monCurrOrder);
		final RestaurantHomeActivity RHA = rha; 
		rha.runOnUiThread(new Runnable() {
	          public void run() {
	        	  Intent i = new Intent(RHA.getApplicationContext(), CurrentOrderActivity.class);
	  			// Count all the elements that the user has currently selected
	  			RHA.startActivityForResult(i, DineOnConstants.REQUEST_VIEW_CURRENT_ORDER); 
	          }
	      });
	  	
		mInstrumentation.waitForIdleSync();
		
		CurrentOrderActivity coa = (CurrentOrderActivity) monCurrOrder
  	  			.waitForActivityWithTimeout(time);
  	  	
  	  	assertNotNull(coa);
  	  	
  	  ActivityMonitor monCurrBill = this.mInstrumentation.addMonitor(
  			CurrentBillActivity.class.getName(), null, false);
		
  	  	
  	  final CurrentOrderActivity COA = coa; 
		COA.runOnUiThread(new Runnable() {
	          public void run() {
	        	Intent intent = new Intent(COA.getApplicationContext(),
	      				CurrentBillActivity.class);
	      		intent.putExtra(CurrentBillActivity.EXTRA_DININGSESSION, 
	      				"Dining session with accrued orders goes here");
	      		COA.startActivityForResult(intent, DineOnConstants.REQUEST_PAY_BILL); 
	          }
	      });
		
		CurrentBillActivity cob = (CurrentBillActivity) monCurrBill
  	  			.waitForActivityWithTimeout(time);
  	  	
  	  	assertNotNull(cob);
  	  	
  	  	
	}
	
}
