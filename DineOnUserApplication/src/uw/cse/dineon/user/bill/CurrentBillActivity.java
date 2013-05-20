package uw.cse.dineon.user.bill;

import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.R;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * Activity to maintain current user bill.
 */
public class CurrentBillActivity extends DineOnUserActivity {

	public static final String EXTRA_DININGSESSION = "DININGSESSION";
	
	public static final String EXTRA_SUBTOTALPRICE = "SUBTOTALPRICE";
	public static final String EXTRA_TAX = "TAX";
	public static final String EXTRA_TOTALPRICE = "TOTALPRICE";
	
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
			String subtotal = extras.getString(EXTRA_SUBTOTALPRICE);
			String tax = extras.getString(EXTRA_TAX);
			String total = extras.getString(EXTRA_TOTALPRICE);
			CurrentBillFragment frag = (CurrentBillFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_current_bill);
			frag.setBill(subtotal, tax);
		}
		
	}

}
