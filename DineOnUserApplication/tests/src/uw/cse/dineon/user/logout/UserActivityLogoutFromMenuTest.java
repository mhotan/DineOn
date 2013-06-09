package uw.cse.dineon.user.logout;


import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.login.UserLoginActivity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

public class UserActivityLogoutFromMenuTest extends ActivityInstrumentationTestCase2<DineOnUserActivity>{
	
	DineOnUser dineOnUser;
	DineOnUserActivity mActivity;
	Instrumentation mInstr;
	
	public UserActivityLogoutFromMenuTest(){
		super(DineOnUserActivity.class);

	}
	
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		this.dineOnUser = TestUtility.createFakeUser();
		assertNotNull(this.dineOnUser);
		DineOnUserApplication.setDineOnUser(this.dineOnUser);
		this.setActivityInitialTouchMode(false);
	    Intent addEvent = new Intent();
	    setActivityIntent(addEvent);
		this.mInstr = this.getInstrumentation();
		this.mActivity = this.getActivity();

	}
	
	@Override
	protected void tearDown() throws Exception{
		super.tearDown();
		this.setActivity(null);

	}
	


	/**
	 * Tests to see if the menu buttons perform the correct action
	 * and return on back press
	 */
	public void testMenuOptionLogOut(){
		assertNotNull(this.mActivity);
		assertNotNull(DineOnUserApplication.getDineOnUser());
		this.mInstr.waitForIdleSync();
		this.mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		this.mInstr.waitForIdleSync();
		ActivityMonitor logMon = this.mInstr.addMonitor(
				UserLoginActivity.class.getName(), null, false);
		assertTrue(this.mInstr.invokeMenuActionSync(this.mActivity, R.id.option_logout, 0));

		UserLoginActivity ula = (UserLoginActivity)
				logMon.waitForActivityWithTimeout(5000);
		assertNotNull(ula);
		ula.finish();


	}
	
}
