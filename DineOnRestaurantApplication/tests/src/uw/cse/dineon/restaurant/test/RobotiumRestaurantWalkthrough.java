package uw.cse.dineon.restaurant.test;

import uw.cse.dineon.restaurant.R;
import uw.cse.dineon.restaurant.active.RestauarantMainActivity;
import uw.cse.dineon.restaurant.login.RestaurantLoginActivity;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.jayway.android.robotium.solo.Solo;

/**
 * Robo test class that dynamically logs in and can perform actions in the main
 * Restaurant activity. See XXX tags for useful notes
 * 
 * 
 * 
 * @author Vince
 * 
 */
public class RobotiumRestaurantWalkthrough extends
		ActivityInstrumentationTestCase2<RestaurantLoginActivity> {

	public static final String LOGIN_NAME = "test34";
	public static final String PASSWORD = "test34";

	private Solo solo;
	private Activity mActivity;

	public RobotiumRestaurantWalkthrough() {
		super(RestaurantLoginActivity.class);
	}

	@Override
	public void setUp() {
		solo = new Solo(getInstrumentation(), getActivity());
		mActivity = getActivity();

	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	/**
	 * This method should be expanded (and possibly renamed appropriately) to
	 * interact with the entire activity.
	 */
	public void testLogIn() {
		solo.waitForActivity(RestauarantMainActivity.class, 3000);// wait for
																	// auto-login
		if (solo.getCurrentActivity().getClass() != RestauarantMainActivity.class) {
			Log.v("ROBO", solo.getCurrentActivity().getClass().getName());
			tryLogIn(); // Try to log in or make account
		}

		solo.waitForActivity(RestauarantMainActivity.class, 500);
		// we should not be in the main activity. if not, we have failed.
//		solo.assertCurrentActivity("Error, not in Restaurant Main Activity",
//				RestauarantMainActivity.class);

		// Now we're in the restaurant activity. Change what's past here to do
		// different stuff

	}

	private void tryLogIn() {
		EditText et = solo.getEditText("Your Restaurant Name");
		assertNotNull(et);
		solo.typeText(et, LOGIN_NAME);

		EditText et2 = solo.getEditText("Password");
		assertNotNull(et2);
		solo.typeText(et2, PASSWORD);

		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((Button) mActivity.findViewById(R.id.button_login))
						.performClick();
			}
		});
		getInstrumentation().waitForIdleSync();
		
		
		solo.waitForDialogToClose(1000); // wait for login validation
		// XXX good for waiting for logins, etc

		if (solo.searchText("Invalid Credentials")) {
			makeAccount();
		}

	}

	/**
	 * Private method to make an account if one does not exist under the
	 * specified credentials
	 */
	private void makeAccount() {
		solo.clickOnButton("Dismiss");//dismiss "invalid credentials"
		solo.waitForDialogToClose(500);
		solo.clickOnText("Create New Account");
		// XXX ^ use this to select action bar items via their id.
		// Type in new account info
		EditText et = solo.getEditText("Your Restaurant Name");
		assertNotNull(et);
		solo.typeText(et, LOGIN_NAME);

		et = solo.getEditText("Email Address");
		assertNotNull(et);
		solo.typeText(et, "test@example.com");

		et = solo.getEditText("Password");
		assertNotNull(et);
		solo.typeText(et, PASSWORD);

		et = solo.getEditText("Repeat Password");
		assertNotNull(et);
		solo.typeText(et, PASSWORD);
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((Button) mActivity.findViewById(R.id.button_create_account))
						.performClick();
			}
		});
		getInstrumentation().waitForIdleSync();
	}

}
