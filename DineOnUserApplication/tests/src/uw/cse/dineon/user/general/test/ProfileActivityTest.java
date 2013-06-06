package uw.cse.dineon.user.general.test;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.general.ProfileActivity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivityTest extends 
			ActivityInstrumentationTestCase2<ProfileActivity> {

	private DineOnUser dineOnUser;
	private ProfileActivity mActivity;
	private Instrumentation mInstrumentation;

	public ProfileActivityTest() {
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
	 * Ensures that the users data is correctly reflected in their edit profile.
	 * Also tests to make sure the transition to the profile menu works on back
	 * pressed and that the data is still consistent.
	 * 
	 */
	public void testOnUserInfoUpdate() {
		assertNotNull(this.mActivity);
		this.mInstrumentation.waitForIdleSync();
		mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		mInstrumentation.invokeMenuActionSync(mActivity, R.id.option_edit_profile, 0);		
		this.mInstrumentation.waitForIdleSync();
		
		View v = this.mActivity.findViewById(R.id.label_profile_name_edit);
		assertNotNull(v);
		
		TextView tv = (TextView) v;
		
		assertEquals(this.dineOnUser.getName(), tv.getText());
		assertEquals(this.dineOnUser.getUserInfo().getName(),tv.getText());
		
		v = this.mActivity.findViewById(R.id.button_save_changes);
		assertNotNull(v);
		Button save = (Button) v;
		final Button B = save;
		this.mActivity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				assertTrue(B.performClick());
			}
			
		});
		this.mInstrumentation.waitForIdleSync();
		
		mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);

		v = this.mActivity.findViewById(R.id.label_profile_name);
		assertNotNull(v);
		assertTrue(v instanceof TextView);
		tv = (TextView) v;
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
	
	}
}
