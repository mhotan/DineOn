package uw.cse.dineon.user.bill;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.bill.CurrentBillFragment.PayBillListener;
import android.os.Bundle;
import android.view.Menu;
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
		setContentView(R.layout.activity_current_bill);
	}

	@Override
	public void payCurrentBill() {
		super.payBill();
		finish();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		// TODO If in landscape mode then user already sees the bill
		// So hide the fragments
		MenuItem paybillItem = menu.findItem(R.id.option_bill);
		if (paybillItem != null) {
			paybillItem.setEnabled(false);
			paybillItem.setVisible(false);
		}
		MenuItem checkInItem = menu.findItem(R.id.option_check_in);
		if (checkInItem != null) {
			checkInItem.setEnabled(false);
			checkInItem.setVisible(false);
		}
		MenuItem viewOrderItem = menu.findItem(R.id.option_view_order);
		if (viewOrderItem != null) {
			viewOrderItem.setEnabled(false);
			viewOrderItem.setVisible(false);
		}
		MenuItem searchItem = menu.findItem(R.id.option_search);
		searchItem.setEnabled(false);
		searchItem.setVisible(false);
		
		return true;
	}

	@Override
	public DiningSession getDiningSession() {
		return mUser.getDiningSession();
	}
}
