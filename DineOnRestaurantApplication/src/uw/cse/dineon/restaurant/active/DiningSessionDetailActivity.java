package uw.cse.dineon.restaurant.active;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.UserInfo;
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

		// Assume that another activity (RestaurantMainActivity) has already set the value
		mDiningSession = mRestaurant.getTempDiningSession();
		
		if (mDiningSession == null) { 
			Log.e(TAG, "Null Dining session found");
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
	public void sendShoutOut(UserInfo user, String message) {
		String log = getString(R.string.restaurant_part1)
				+ message  + getString(R.string.restaurant_part2) + user.getName();
		Log.d(TAG, log);
		Toast.makeText(this, log, Toast.LENGTH_SHORT).show();
	}

	@Override
	public DiningSession getDiningSession() {
		return mDiningSession;
	}
	
}
