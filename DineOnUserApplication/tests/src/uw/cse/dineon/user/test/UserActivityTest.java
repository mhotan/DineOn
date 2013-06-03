package uw.cse.dineon.user.test;


import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.ParseUtil;
import uw.cse.dineon.library.util.TestUtility;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.general.ProfileActivity;
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
	public void testMenuOptionSearch(){
		assertNotNull(this.mActivity);
		assertNotNull(DineOnUserApplication.getDineOnUser());
		this.mInstr.waitForIdleSync();
		this.mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		this.mInstr.waitForIdleSync();
		assertTrue(this.mInstr.invokeMenuActionSync(mActivity, R.id.option_search, 0));
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
	public void testMenuOptionProfile(){
		assertNotNull(this.mActivity);
		this.mInstr.waitForIdleSync();
		this.mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		this.mInstr.waitForIdleSync();
		ActivityMonitor proMon = this.mInstr.addMonitor(
				ProfileActivity.class.getName(), null, false);
		assertTrue(this.mInstr.invokeMenuActionSync(mActivity, R.id.option_profile, 0));
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
	 * Tests the on receive of the user satellite.
	 */
	public void testUserSat(){
		Intent i = new Intent();
		i.putExtra(DineOnConstants.PARSE_CHANNEL, ParseUtil.getChannel(this.dineOnUser.getUserInfo()));
		i.setAction("fake.action");
		i.putExtra(DineOnConstants.PARSE_DATA, "{" + DineOnConstants.OBJ_ID + ":awda}");
		this.mActivity.getSat().onReceive(this.mActivity, i);
		this.mActivity.finish();
	}
	
	/**
	 * Tests the on receive of the user satellite.
	 */
	public void testUserSatwithNullIntent(){
		Intent i = new Intent();
		this.mActivity.getSat().onReceive(this.mActivity, i);
		this.mActivity.finish();
	}
	
	/**
	 * Tests the on receive of the user satellite.
	 */
	public void testUserSatwithNullChannel(){
		Intent i = new Intent();
		i.setAction("fake.action");
		i.putExtra(DineOnConstants.PARSE_DATA, "{" + DineOnConstants.OBJ_ID + ":awda}");
		this.mActivity.getSat().onReceive(this.mActivity, i);
		this.mActivity.finish();
	}
	
	/**
	 * Tests the on receive of the user satellite.
	 */
	public void testUserSatwithBadChannel(){
		Intent i = new Intent();
		i.putExtra(DineOnConstants.PARSE_CHANNEL, "badchan");

		i.setAction("fake.action");
		i.putExtra(DineOnConstants.PARSE_DATA, "{" + DineOnConstants.OBJ_ID + ":awda}");
		this.mActivity.getSat().onReceive(this.mActivity, i);
		this.mActivity.finish();
	}

	
	/**
	 * Test to see if the Checkin Dialog Creates itself and destroys itself 
	 * properly
	 */
	public void testCheckinDialogCreationDestruction() {
		final DineOnUserActivity DUA = this.mActivity; 
		DUA.runOnUiThread(new Runnable() {
	          public void run() {
	        	  DUA.createProgressDialog("Hello", "Test World");
	          }
	      });
		DUA.runOnUiThread(new Runnable() {
	          public void run() {
	        	  DUA.destroyProgressDialog();
	          }
	      });
		mInstr.waitForIdleSync();
		DUA.finish();
	}
	
	/**
	 * Test that on a timeout the checkin error dialog pops up.
	 */
	public void testCheckinTimeoutDialogCreation() {
		final DineOnUserActivity DUA = this.mActivity; 
		DUA.runOnUiThread(new Runnable() {
	          public void run() {
	        	  DUA.createProgressDialog("Hello", "Test World");
	          }
	      });
		DUA.runOnUiThread(new Runnable() {
	          public void run() {
	        	  DUA.timerDelayRemoveDialog((long)500);
	          }
	      });
		mInstr.waitForIdleSync();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mActivity.finish();
	}
}
