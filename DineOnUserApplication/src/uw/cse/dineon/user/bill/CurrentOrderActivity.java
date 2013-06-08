package uw.cse.dineon.user.bill;

import java.util.HashMap;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.bill.CurrentBillFragment.PayBillListener;
import uw.cse.dineon.user.bill.CurrentOrderFragment.OrderUpdateListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Activity that allows the the user to see their order update listener.
 * @author mhotan
 */
public class CurrentOrderActivity extends DineOnUserActivity 
implements PayBillListener, OrderUpdateListener { 
	
	
	private final String TAG = "CurrentOrderActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_current_order);
		
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {		
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
	public void payCurrentBill() {
		super.payBill();
		finish();
	}

	@Override
	public void onIncrementItemOrder(uw.cse.dineon.library.MenuItem item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDecrementItemOrder(uw.cse.dineon.library.MenuItem item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRemoveItemFromOrder(uw.cse.dineon.library.MenuItem item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HashMap<uw.cse.dineon.library.MenuItem, CurrentOrderItem> getOrder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetCurrentOrder() {
		// TODO Auto-generated method stub
		
	}
			
}
