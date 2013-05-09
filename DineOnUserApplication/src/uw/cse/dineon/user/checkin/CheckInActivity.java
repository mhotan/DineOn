package uw.cse.dineon.user.checkin;

import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @author mhotan
 * TODO
 */
public class CheckInActivity extends DineOnUserActivity 
implements CheckInFragment.CheckInListener {
	
	public static final String EXTRA_CHECKEDIN = "is checked in?";
	
	private boolean mCheckedIn;

	private static final String TAG = CheckInActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkin);
		mCheckedIn = false;
		
		
		
	}

	@Override
	public void onCheckInSuccess() {
		// TODO Auto-generated method stub
		mCheckedIn = true;
		
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
		
		//finish();
	}

	@Override
	public void onCheckInFail() {
		// TODO Auto-generated method stub
		mCheckedIn = false;
	}
	
	@Override
	public void finish() {
		Intent retIntent = new Intent();
		retIntent.putExtra(EXTRA_CHECKEDIN, mCheckedIn);
		super.finish();
		// Send the results back with an intent
	}
	
	@Override
	public void onBackPressed() {
		Log.d(TAG, "Check In Activity back pressed");
		super.onBackPressed();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	  IntentResult scanResult = 
			  IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
	  if (scanResult != null) {
		  // handle scan result
		  String contents = scanResult.getContents();
		  Log.d("ZXing", contents);
	  } else {
		  // else continue with any other code you need in the method
		  Log.d("ZXing", "Error getting the result");
	  }
	}
}
