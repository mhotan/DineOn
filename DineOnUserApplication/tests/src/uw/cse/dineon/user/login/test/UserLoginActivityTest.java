package uw.cse.dineon.user.login.test;

import java.util.Date;
import java.util.List;

import com.parse.Parse;
import com.parse.ParseUser;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.login.UserLoginActivity;
import android.app.Instrumentation;
import android.content.Intent;
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
		setActivityInitialTouchMode(false);
		
		ParseUser user = new ParseUser();
		user.setUsername("testUser");
		user.setPassword("12345");;
		
		ParseUser restUser = new ParseUser();
		restUser.setUsername("testRestUser");
		restUser.setPassword("12345");
		
		dineOnUser = new DineOnUser(user);
		
		Restaurant rest = new Restaurant(restUser);
		DiningSession ds = 
				new DiningSession(10, new Date(), dineOnUser.getUserInfo(), rest.getInfo());
		
		List<MenuItem> mi = TestUtility.getFakeMenuItems();
		Order one = new Order(1, dineOnUser.getUserInfo(), mi);
		ds.addPendingOrder(one);
		
		Menu m = TestUtility.getFakeMenu();
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
		
		mActivity.runOnUiThread(new Runnable() {
	          public void run() {
	              et_uname.setText("");
	              et_passwd.setText("");
	              Button loginButton = (Button) mActivity.findViewById(R.id.button_login);
	              loginButton.performClick();
	          }
	      });
		mInstrumentation.waitForIdleSync();
		final DineOnUser DU = this.dineOnUser; 
		mActivity.runOnUiThread(new Runnable() {
	          public void run() {
	        	  mActivity.startRestSelectionAct(DU);
	        	  
	          }
	      });
	    mInstrumentation.waitForIdleSync();
	}
	
}
