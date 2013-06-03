package uw.cse.dineon.user.logout;

import com.parse.ParseUser;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.general.ProfileActivity;
import uw.cse.dineon.user.login.UserLoginActivity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivityEditLogOutTest extends 
			ActivityInstrumentationTestCase2<ProfileActivity> {

	private DineOnUser dineOnUser;
	private ProfileActivity mActivity;
	private Instrumentation mInstrumentation;

	public ProfileActivityEditLogOutTest() {
		super(ProfileActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dineOnUser = TestUtility.createFakeUser();
		dineOnUser.getUserInfo().setPhone("0-123-456-789");
		
		this.setActivityInitialTouchMode(false);
		mInstrumentation = this.getInstrumentation();
	    Intent addEvent = new Intent();
	    setActivityIntent(addEvent);
	    
	    DineOnUserApplication.setDineOnUser(dineOnUser);
	    
		mActivity = getActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		this.setActivity(null);

	}
	
	/**
	 * Ensures that the users data is correctly reflected in their profile.
	 * Also tests to make sure the transition to the edit menu works and log out
	 * with the menu in the profile activity.
	 * 
	 */
	public void testGetInfo() {
		assertNotNull(this.mActivity);
		this.mInstrumentation.waitForIdleSync();
		View v = this.mActivity.findViewById(R.id.label_profile_name);
		assertNotNull(v);
		assertTrue(v instanceof TextView);
		TextView tv = (TextView) v;
		assertEquals(this.dineOnUser.getName(),tv.getText());
		assertEquals(this.dineOnUser.getUserInfo().getName(),tv.getText());
		
		v = this.mActivity.findViewById(R.id.user_email_display);
		assertNotNull(v);
		assertTrue(v instanceof TextView);
		tv = (TextView) v;
		assertEquals(this.dineOnUser.getUserInfo().getEmail(),tv.getText());
		
		v = this.mActivity.findViewById(R.id.user_phone_display);
		assertNotNull(v);
		assertTrue(v instanceof TextView);
		tv = (TextView) v;
		assertEquals(this.dineOnUser.getUserInfo().getPhone(),tv.getText());
		mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		mInstrumentation.invokeMenuActionSync(mActivity, R.id.option_edit_profile, 0);		
		this.mInstrumentation.waitForIdleSync();
		assertNotNull(this.mActivity.findViewById(R.id.button_save_changes));
		ActivityMonitor logMon = this.mInstrumentation.addMonitor(
				UserLoginActivity.class.getName(), null, false);
		
		mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		mInstrumentation.invokeMenuActionSync(mActivity,R.id.option_logout, 0);

		
		UserLoginActivity ula = (UserLoginActivity)
				this.mInstrumentation.waitForMonitorWithTimeout(logMon, 1000);
		
		assertNotNull(ula);
		ula.finish();

	}


}
