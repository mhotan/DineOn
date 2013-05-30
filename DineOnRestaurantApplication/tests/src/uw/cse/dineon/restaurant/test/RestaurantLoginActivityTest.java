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

import com.parse.ParseUser;

public class RestaurantLoginActivityTest extends
ActivityInstrumentationTestCase2<RestaurantLoginActivity> {

	private static final int WAIT_LOGIN_TIME = 1000;
	
	private Activity mActivity;
	private EditText mNameText;
	private EditText mPassText;
	private LoginFragment mFragment;
	private Button mSubmit;
	private static final String fakeUserName = "fakeRestaurantLoginName";
	private static final String fakePassword = "fakeRestaurantLoginPassword";
	private ParseUser mUser;

	/**
	 * Creates a new RestaurantLoginActivityTest.
	 */
	public RestaurantLoginActivityTest() {
		super(RestaurantLoginActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		
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

		getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		
		// Create the fake user
		mUser = new ParseUser();
		mUser.setUsername(fakeUserName);
		mUser.setPassword(fakePassword);
		
		// Have to create the restaurant for this user
		Restaurant mRestaurant = new Restaurant(mUser);
	}
	
	

	@Override
	protected void tearDown() throws Exception {
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

	/**
	 * Asserts that a valid logged in user logs in.
	 * Commented out because it requires network interaction
	 */
	public void testLoginSucess() {
//		mActivity.runOnUiThread(new Runnable() {
//			public void run() {
//				mNameText.setText(fakeUserName);		
//			} // end of run() method definition
//		}); // end of invocation of runOnUiThread
//
//		
//		mActivity.runOnUiThread(new Runnable() {
//			public void run() {
//				mPassText.setText(fakePassword);		
//			} // end of run() method definition
//		}); // end of invocation of runOnUiThread
//		
//		mActivity.runOnUiThread(new Runnable() {
//			public void run() {
//				mSubmit.requestFocus();
//				mSubmit.performClick();		
//			} // end of run() method definition
//		});
//		
//		RestauarantMainActivity mainAct = (RestauarantMainActivity) 
//				mMonitor.waitForActivityWithTimeout(WAIT_TIME);
//		assertNotNull(mainAct);
//		mainAct.finish();
	}
	
	/**
	 * Asserts that a user can't log in without a username.
	 */
	public void testLoginNoUsernameFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				mPassText.setText(fakePassword);
				mSubmit.requestFocus();
				mSubmit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
		assertNull(startedActivity);
	}
	
	/**
	 * Asserts that a user can't log in without a password.
	 */
	public void testLoginNoPasswordFailure() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				RestauarantMainActivity.class.getName(), null, false);
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				mNameText.setText(fakeUserName);
				mSubmit.requestFocus();
				mSubmit.performClick();
			} // end of run() method definition
		});
		RestauarantMainActivity startedActivity = (RestauarantMainActivity) monitor
		        .waitForActivityWithTimeout(WAIT_LOGIN_TIME);
		assertNull(startedActivity);
	}
	
}
