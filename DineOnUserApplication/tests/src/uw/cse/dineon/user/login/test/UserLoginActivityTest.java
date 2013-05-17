package uw.cse.dineon.user.login.test;

import uw.cse.dineon.user.login.UserLoginActivity;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.EditText;

public class UserLoginActivityTest extends
		ActivityInstrumentationTestCase2<UserLoginActivity> {

	private Activity mActivity;
	private EditText mNameText;

	public UserLoginActivityTest() {
		super(UserLoginActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		mActivity = getActivity();
		
		mNameText = (EditText) mActivity.findViewById(uw.cse.dineon.user.R.id.input_login_email);
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testEmailLoginUI() {

	    mActivity.runOnUiThread(
	      new Runnable() {
	        public void run() {
	          mNameText.requestFocus();
	          mNameText.setText("t");
	        } // end of run() method definition
	      } // end of anonymous Runnable object instantiation
	    ); // end of invocation of runOnUiThread
	    
	    this.sendKeys(KeyEvent.KEYCODE_FORWARD_DEL);
	    this.sendKeys("M E");
	    this.sendKeys("AT");
	    this.sendKeys("T E S T PERIOD C O M");
	    	    
	    assertEquals("me@test.com", mNameText.getText().toString());
	}

}
