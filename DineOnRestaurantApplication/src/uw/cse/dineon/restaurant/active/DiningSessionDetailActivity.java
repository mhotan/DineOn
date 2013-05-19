package uw.cse.dineon.restaurant.active;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.DineOnRestaurantActivity;
import uw.cse.dineon.restaurant.R;
import uw.cse.dineon.restaurant.active.DiningSessionDetailFragment.DiningSessionDetailListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
/**
 * An Activity that presents the details of a particular dining session.
 * 
 * @author mhotan
 */
public class DiningSessionDetailActivity extends DineOnRestaurantActivity 
implements DiningSessionDetailListener {

	/**
	 * Log tag.
	 */
	private static final String TAG = DiningSessionDetailActivity.class.getSimpleName();

	/**
	 * Extra to pass dining session with.
	 */
	public static final String EXTRA_DININGSESSION = TAG + "_diningsession";

	private DiningSession mDiningSession;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_diningsession_detail);

		// Grab reference to the extras
		Bundle extras = getIntent().getExtras();

		// Lets first check if the activity is being recreated after being
		// destroyed but there was an already existing restuarant
		if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_DININGSESSION)) { 
			// Activity recreated
			mDiningSession = savedInstanceState.getParcelable(EXTRA_DININGSESSION);
		} 
		else if (extras != null && extras.containsKey(
				DineOnConstants.KEY_RESTAURANT)) {
			// Activity started and created for the first time
			// Valid extras were passed into this
			mDiningSession = extras.getParcelable(EXTRA_DININGSESSION);
		}

		if (mDiningSession == null) { 
			return;
		}
		
		DiningSessionDetailFragment frag = 
				(DiningSessionDetailFragment) getSupportFragmentManager().
				findFragmentById(R.id.fragment1);
		if (frag != null && frag.isInLayout()) {
			frag.setDiningSession(mDiningSession);
		}
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_DININGSESSION, mDiningSession);
    }

	@Override
	public void sendShoutOut(UserInfo user, String message) {
		String log = "Restaurant wants to sent message \"" 
				+ message  + "\" to user " + user.getName();
		Log.d(TAG, log);
		Toast.makeText(this, log, Toast.LENGTH_SHORT).show();
	}
	
}
