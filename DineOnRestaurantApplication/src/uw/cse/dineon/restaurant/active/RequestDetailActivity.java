package uw.cse.dineon.restaurant.active;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.DineOnRestaurantActivity;
import uw.cse.dineon.restaurant.R;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * This activity is used in portrait mode. 
 * @author mhotan
 */
public class RequestDetailActivity extends DineOnRestaurantActivity implements
RequestDetailFragment.RequestDetailListener {

	public static final String EXTRA_REQUEST = "request";

	private CustomerRequest mRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_details);
		
		// Grab reference to the extras
		Bundle extras = getIntent().getExtras();

		// Lets first check if the activity is being recreated after being
		// destroyed but there was an already existing restuarant
		if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_REQUEST)) { 
			// Activity recreated
			mRequest = savedInstanceState.getParcelable(EXTRA_REQUEST);
		} 
		else if (extras != null && extras.containsKey(
				DineOnConstants.KEY_RESTAURANT)) {
			// Activity started and created for the first time
			// Valid extras were passed into this
			mRequest = extras.getParcelable(EXTRA_REQUEST);
		}

		if (mRequest == null) { 
			return;
		}

		RequestDetailFragment frag = (RequestDetailFragment) getSupportFragmentManager().
				findFragmentById(R.id.fragment1);
		if (frag != null && frag.isInLayout()) {
			frag.setRequest(mRequest);
		}
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_REQUEST, mRequest);
    }

	@Override
	public void onSendTaskToStaff(CustomerRequest request, String staff, String urgency) {
		// TODO Auto-generated method stub
		request.setWaiter(staff);
	}

	@Override
	public void sendShoutOut(UserInfo user, String message) {
		String log = "Restaurant wants to sent message \"" 
				+ message  + "\" to user " + user.getName();
		Log.d(TAG, log);
		Toast.makeText(this, log, Toast.LENGTH_SHORT).show();
	}

}
