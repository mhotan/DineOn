package uw.cse.dineon.restaurant.test;

import uw.cse.dineon.restaurant.login.RestaurantLoginActivity;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

public class RestaurantLoginActivityTest extends
ActivityInstrumentationTestCase2<RestaurantLoginActivity> {

	private Activity mActivity;
	private EditText mNameText;
	private EditText mPassText;

	public RestaurantLoginActivityTest() {
		super(RestaurantLoginActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		mActivity = getActivity();

		mNameText = (EditText) mActivity.findViewById(
				uw.cse.dineon.restaurant.R.id.input_restaurant_login_name);
		mPassText = (EditText) mActivity.findViewById(
				uw.cse.dineon.restaurant.R.id.input_password);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEmailLoginUI() {
		mActivity.runOnUiThread(
				new Runnable() {
					public void run() {
						mNameText.requestFocus();

					} // end of run() method definition
				} // end of anonymous Runnable object instantiation
				); // end of invocation of runOnUiThread

		this.sendKeys("M E");
		this.sendKeys("AT");
		this.sendKeys("T E S T PERIOD C O M");

		assertEquals("me@test.com", mNameText.getText().toString());
	}

	public void testPasswordLoginUI() {
		mActivity.runOnUiThread(
				new Runnable() {
					public void run() {
						mPassText.requestFocus();

					} // end of run() method definition
				} // end of anonymous Runnable object instantiation
				); // end of invocation of runOnUiThread

		this.sendKeys("AT P A S S W O R D AT PERIOD");

		assertEquals("@password@.", mPassText.getText().toString());
	}
	
	public void testDeleteResume() {
		getInstrumentation().waitForIdleSync();
		mActivity.finish();
		mActivity = getActivity();
		assertNotNull(mActivity);
	}


}
