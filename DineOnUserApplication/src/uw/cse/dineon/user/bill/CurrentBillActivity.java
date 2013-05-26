package uw.cse.dineon.user.bill;

import java.util.List;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.bill.CurrentBillFragment.PayBillListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * Activity to maintain current user bill.
 */
public class CurrentBillActivity extends DineOnUserActivity
implements PayBillListener {

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
		
		calculateBill();
	}
	
	/**
	 * Calculate the bill for all orders in dining session.
	 */
	public void calculateBill() {
		List<Order> orders = DineOnUserApplication.getCurrentDiningSession().getOrders();
		double sum = 0.0;
		for (Order order : orders) {
			for (CurrentOrderItem item : order.getMenuItems()) {
				sum += item.getMenuItem().getPrice() * item.getQuantity();
			}
		}
		CurrentBillFragment fragment = (CurrentBillFragment)
				getSupportFragmentManager().findFragmentById(R.id.fragment_current_bill);
		fragment.setBill(sum, DineOnConstants.TAX);
	}

	@Override
	public void payCurrentBill() {
		super.payBill();
		finish();
	}
}
