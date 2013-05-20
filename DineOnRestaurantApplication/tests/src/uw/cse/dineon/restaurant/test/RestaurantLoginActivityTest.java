package uw.cse.dineon.restaurant.test;

import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.restaurant.active.RestauarantMainActivity;
import uw.cse.dineon.restaurant.login.LoginFragment;
import uw.cse.dineon.restaurant.login.RestaurantLoginActivity;
import android.app.Activity;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseUser;

public class RestaurantLoginActivityTest extends
ActivityInstrumentationTestCase2<RestaurantLoginActivity> {

	private static final int WAIT_TIME = 10000;
	
	private Activity mActivity;
	private EditText mNameText;
	private EditText mPassText;
	private LoginFragment mFragment;
	private Button mSubmit;
	private static final String fakeUserName = "fakeLoginName";
	private static final String fakePassword = "fakeLoginPassword";
	private ParseUser mUser;
	private Restaurant mRestaurant;
	private ActivityMonitor mMonitor;

	public RestaurantLoginActivityTest() {
		super(RestaurantLoginActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		// initialize Parse
		Parse.initialize(getInstrumentation().getTargetContext(),
				"RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul",
				"wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
		
		setActivityInitialTouchMode(false);
		mActivity = getActivity();

		mFragment = (LoginFragment) getActivity().getSupportFragmentManager().
				findFragmentById(uw.cse.dineon.restaurant.R.id.fragment1);

		mNameText = (EditText) mActivity.findViewById(
				uw.cse.dineon.restaurant.R.id.input_restaurant_login_name);
		mPassText = (EditText) mActivity.findViewById(
				uw.cse.dineon.restaurant.R.id.input_password);

		mSubmit = (Button) mActivity.findViewById(
				uw.cse.dineon.restaurant.R.id.button_login);

		mMonitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		// Create the fake user
		mUser = new ParseUser();
		mUser.setUsername(fakeUserName);
		mUser.setPassword(fakePassword);
		mUser.signUp();
		
		// Have to create the restaurant for this user
		mRestaurant = new Restaurant(mUser);
		mRestaurant.saveOnCurrentThread();
	}
	
	

	@Override
	protected void tearDown() throws Exception {
		mRestaurant.deleteFromCloud();
		mUser.delete();
		mActivity.finish();
		super.tearDown();
	}

	/**
	 * Test if the components exists
	 */
	public void testComponentsExist() {
		assertNotNull(mActivity);
		assertNotNull(mFragment);
		assertNotNull(mNameText);
		assertNotNull(mPassText);
		assertNotNull(mSubmit);
	}

	public void testLoginSucess() {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				mNameText.setText(fakeUserName);		
			} // end of run() method definition
		}); // end of invocation of runOnUiThread

		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				mPassText.setText(fakePassword);		
			} // end of run() method definition
		}); // end of invocation of runOnUiThread
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				mSubmit.requestFocus();
				mSubmit.performClick();		
			} // end of run() method definition
		});
		
		RestauarantMainActivity mainAct = (RestauarantMainActivity) 
				mMonitor.waitForActivityWithTimeout(WAIT_TIME);
		assertNotNull(mainAct);
		mainAct.finish();
	}
}