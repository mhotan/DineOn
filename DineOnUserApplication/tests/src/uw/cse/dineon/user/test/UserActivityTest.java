package uw.cse.dineon.user.test;


import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.general.ProfileActivity;
import uw.cse.dineon.user.login.UserLoginActivity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

public class UserActivityTest extends ActivityInstrumentationTestCase2<DineOnUserActivity>{
	
	DineOnUser dineOnUser;
	DineOnUserActivity mActivity;
	Instrumentation mInstr;
	
	public UserActivityTest(){
		super(DineOnUserActivity.class);

	}
	
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		this.dineOnUser = TestUtility.createFakeUser();
		DineOnUserApplication.setDineOnUser(this.dineOnUser);
		this.setActivityInitialTouchMode(false);
		this.mInstr = this.getInstrumentation();
	    Intent addEvent = new Intent();
	    setActivityIntent(addEvent);
		this.mActivity = this.getActivity();
		assertNotNull(DineOnUserApplication.getDineOnUser());

	}
	
	@Override
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/**
	 * Tests to see if the menu buttons perform the correct action
	 * and return on back press
	 */
	public void testMenuOptionSearch(){
		assertNotNull(this.mActivity);
		assertNotNull(DineOnUserApplication.getDineOnUser());
		this.mInstr.waitForIdleSync();
		this.mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		this.mInstr.waitForIdleSync();
		this.mInstr.invokeMenuActionSync(mActivity, R.id.option_search, 0);
		this.mInstr.waitForIdleSync();
		this.mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
		this.mInstr.waitForIdleSync();
		assertNotNull(DineOnUserApplication.getDineOnUser());
		assertTrue(this.mActivity instanceof DineOnUserActivity);
		this.mActivity.finish();
		
	}
	
	/**
	 * Tests to see if the menu buttons perform the correct action
	 * and return on back press
	 */
	public void testMenuOptionFilter(){
		assertNotNull(this.mActivity);
		assertNotNull(DineOnUserApplication.getDineOnUser());
		this.mInstr.waitForIdleSync();
		this.mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		this.mInstr.waitForIdleSync();
		assertNotNull(DineOnUserApplication.getDineOnUser());
		this.mInstr.invokeMenuActionSync(mActivity, R.id.option_filter, 0);
		this.mInstr.waitForIdleSync();
		assertNotNull(DineOnUserApplication.getDineOnUser());
		this.mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
		this.mInstr.waitForIdleSync();
		
		assertTrue(this.mActivity instanceof DineOnUserActivity);
		this.mActivity.finish();

	}
	
	/**
	 * Tests to see if the menu buttons perform the correct action
	 * and return on back press
	 */
	public void testMenuOptionProfile(){
		assertNotNull(this.mActivity);
		this.mInstr.waitForIdleSync();
		this.mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		this.mInstr.waitForIdleSync();
		ActivityMonitor proMon = this.mInstr.addMonitor(
				ProfileActivity.class.getName(), null, false);
		this.mInstr.invokeMenuActionSync(mActivity, R.id.option_profile, 0);
		ProfileActivity pa = (ProfileActivity)
				proMon.waitForActivityWithTimeout(5000);
		assertNotNull(pa);
		assertNotNull(DineOnUserApplication.getDineOnUser());
		this.mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
		this.mInstr.waitForIdleSync();
		assertNotNull(DineOnUserApplication.getDineOnUser());
		assertTrue(this.mActivity instanceof DineOnUserActivity);
		this.mActivity.finish();
	}

	/**
	 * Tests to see if the menu buttons perform the correct action
	 * and return on back press
	 */
	public void testMenuOptionLogOut(){
		assertNotNull(this.mActivity);
		this.mInstr.waitForIdleSync();
		this.mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		this.mInstr.waitForIdleSync();
		ActivityMonitor logMon = this.mInstr.addMonitor(
				UserLoginActivity.class.getName(), null, false);
		this.mInstr.invokeMenuActionSync(this.mActivity, R.id.option_logout, 0);
		UserLoginActivity ula = (UserLoginActivity)
				logMon.waitForActivityWithTimeout(5000);
		assertNull(DineOnUserApplication.getDineOnUser());
		assertNotNull(ula);
		ula.finish();
	}
	
}
