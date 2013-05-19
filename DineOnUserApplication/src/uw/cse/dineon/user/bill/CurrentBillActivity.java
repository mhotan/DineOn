package uw.cse.dineon.user.bill;

import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.R;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Activity to maintain current user bill.
 */
public class CurrentBillActivity extends DineOnUserActivity {

	public static final String EXTRA_DININGSESSION = "DININGSESSION";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Need to check if Activity has been switched to landscape mode
		// If yes, finished and go back to the start Activity
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			finish();
			return;
		}
		setContentView(R.layout.activity_current_bill);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String s = extras.getString(EXTRA_DININGSESSION);
			CurrentBillFragment frag = (CurrentBillFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_current_bill);
			frag.setDiningSession(s);
		}
		
	}

}
