package uw.cse.dineon.restaurant.active;

import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.restaurant.DineOnRestaurantActivity;
import uw.cse.dineon.restaurant.R;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * An Activity that just shows the detail of an Order.
 * @author mhotan
 */
public class OrderDetailActivity extends DineOnRestaurantActivity implements
OrderDetailFragment.OrderDetailListener {

	private static final String TAG = OrderDetailActivity.class.getSimpleName();

	public static final String EXTRA_ORDER = TAG + "_order";

	private Order mOrder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_details);
		
		// Grab reference to the extras
		Bundle extras = getIntent().getExtras();

		// Lets first check if the activity is being recreated after being
		// destroyed but there was an already existing restuarant
		if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_ORDER)) { 
			// Activity recreated
			mOrder = savedInstanceState.getParcelable(EXTRA_ORDER);
		} 
		else if (extras != null && extras.containsKey(EXTRA_ORDER)) {
			// Activity started and created for the first time
			// Valid extras were passed into this
			mOrder = extras.getParcelable(EXTRA_ORDER);
		}

		if (mOrder == null) { 
			return;
		}

		OrderDetailFragment frag = (OrderDetailFragment) getSupportFragmentManager().
				findFragmentById(R.id.fragment1);
		if (frag != null && frag.isInLayout()) {
			frag.setOrder(mOrder);
		}
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_ORDER, mOrder);
    }

	@Override
	public void sendShoutOut(UserInfo user, String message) {
		String log = "Restaurant wants to sent message \"" 
				+ message  + "\" to user " + user.getName();
		Log.d(TAG, log);
		Toast.makeText(this, log, Toast.LENGTH_SHORT).show();
	}

}
