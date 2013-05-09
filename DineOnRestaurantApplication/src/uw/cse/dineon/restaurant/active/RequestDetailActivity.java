package uw.cse.dineon.restaurant.active;

import uw.cse.dineon.restaurant.DineOnRestaurantActivity;
import uw.cse.dineon.restaurant.R;
import android.os.Bundle;

/**
 * 
 * @author 
 *
 */
public class RequestDetailActivity extends DineOnRestaurantActivity implements
RequestDetailFragment.RequestDetailListener {

public static final String EXTRA_REQUEST = "request";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Need to check if Activity has been switched to landscape mode
		// If yes, finished and go back to the start Activity
//		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//			finish();
//			return;
//		}
		setContentView(R.layout.activity_request_details);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String request = extras.getString(EXTRA_REQUEST);
			RequestDetailFragment frag = (RequestDetailFragment) getSupportFragmentManager().
					findFragmentById(R.id.fragment1);
			if (frag != null && frag.isInLayout()) {
				frag.setRequest(request);
			}
		}
	}

	@Override
	public void onSendTaskToStaff(String request, String staff, String urgency) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSendMessage(String request, String message) {
		// TODO Auto-generated method stub
		
	}
	
}
