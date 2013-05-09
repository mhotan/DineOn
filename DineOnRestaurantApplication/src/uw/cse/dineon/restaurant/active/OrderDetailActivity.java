package uw.cse.dineon.restaurant.active;

import uw.cse.dineon.restaurant.DineOnRestaurantActivity;
import uw.cse.dineon.restaurant.R;
import android.os.Bundle;

/**
 * An Activity that just shows the detail of an Order.
 * @author mhotan
 */
public class OrderDetailActivity extends DineOnRestaurantActivity implements
OrderDetailFragment.OrderDetailListener {

	public static final String EXTRA_ORDER = "order";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Need to check if Activity has been switched to landscape mode
		// If yes, finished and go back to the start Activity
//		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//			finish();
//			return;
//		}
		setContentView(R.layout.activity_order_details);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String order = extras.getString(EXTRA_ORDER);
			OrderDetailFragment frag = (OrderDetailFragment) getSupportFragmentManager().
					findFragmentById(R.id.fragment1);
			if (frag != null && frag.isInLayout()) {
				frag.setOrder(order);
			}
		}
	}

	@Override
	public void sendMessage(String order, String message) {
		// TODO Auto-generated method stub
		
	}

}
