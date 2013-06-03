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

	private Order mOrder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_details);
		
		// Assume that another activity (RestaurantMainActivity) has already set the value
		mOrder = mRestaurant.getTempOrder();

		if (mOrder == null) {
			Log.e(TAG, "Null order found");
			return;
		}

		OrderDetailFragment frag = (OrderDetailFragment) getSupportFragmentManager().
				findFragmentById(R.id.fragment1);
		if (frag != null && frag.isInLayout()) {
			frag.setOrder(mOrder);
		}
	}

	@Override
	public void sendShoutOut(UserInfo user, String message) {
		String log = getString(R.string.restaurant_part1)
				+ message  + getString(R.string.restaurant_part2) 
				+ user.getName();
		Log.d(TAG, log);
		Toast.makeText(this, log, Toast.LENGTH_SHORT).show();
	}

	@Override
	public Order getOrder() {
		return mOrder;
	}

}
