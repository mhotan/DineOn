package uw.cse.dineon.user.bill;

import java.util.List;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.bill.CurrentBillFragment.PayBillListener;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuInflater;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

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
	
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Note that override this method does not mean the actualy
		//  UI Menu is updated this is done manually
		//  See basic_menu under res/menu for ids
		inflater.inflate(R.menu.basic_menu, menu);
		//Hides the bill option
		final android.view.MenuItem ITEM = menu.findItem(R.id.option_bill);
		ITEM.setEnabled(false);
		ITEM.setVisible(false);

		final SearchView SEARCHVIEW = (SearchView) 
				menu.findItem(R.id.option_search).getActionView();

		// Enable the search widget in the action bar
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		if (searchManager != null) {
			SEARCHVIEW.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		}

		SEARCHVIEW.setIconified(true);
		SEARCHVIEW.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// Make the call to search for a particular restaurant
				onSearch(query);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) { // Do nothing
				return false;
			}
		});

		return true;
	}
}
