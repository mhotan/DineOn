package uw.cse.dineon.user.bill;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * 
 * @author mhotan
 */
public class CurrentOrderActivity extends DineOnUserActivity implements
CurrentOrderFragment.OrderUpdateListener {
	
	private Button mReqButton;
	private final String TAG = "CurrentOrderActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_current_order);
		mReqButton = (Button) this.findViewById(R.id.button_request);
		mReqButton.setOnClickListener(new OrderButtonListener());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.with_paybilloption_menu, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {		
		// TODO If in landscape mode then user already sees the bill
		// So hide the fragments
		CurrentBillFragment fragment = (CurrentBillFragment)
				getSupportFragmentManager().findFragmentById(R.id.fragment_current_bill);
		MenuItem paybillItem = menu.findItem(R.id.option_paybill);
		if (fragment != null && fragment.isInLayout() /*|| TODO There is no pending bill*/) {
			paybillItem.setEnabled(false);
			paybillItem.setVisible(false);
		} else {
			paybillItem.setEnabled(true);
			paybillItem.setVisible(true);
		}
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
		case R.id.option_paybill:
			Intent intent = new Intent(getApplicationContext(),
					CurrentBillActivity.class);
			intent.putExtra(CurrentBillActivity.EXTRA_DININGSESSION, 
					"Dining session with accrued orders goes here");
			startActivityForResult(intent, DineOnConstants.REQUEST_PAY_BILL);
			break;
		case R.id.button_request:
			Toast.makeText(this, "Requested", 5000);
			break;
		default:
			break;
		}
		
		return true;
	}

	@Override
	public void onPlaceOrder(String order) {
		// TODO Auto-generated method stub

		// TODO Add order to dining session

		// Update the fragment by adding the adding session again

		CurrentBillFragment fragment = (CurrentBillFragment)
				getSupportFragmentManager().findFragmentById(R.id.fragment_current_bill);
		if (fragment != null && fragment.isInLayout()) {
			// Update the fragment
			fragment.setDiningSession(order);
		}
	}

	@Override
	public void onIncrementItemOrder(String item, String order) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDecrementItemOrder(String item, String order) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemoveItemFromOrder(String item, String order) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	public void onRequestMade() {
		UserInfo ui = new UserInfo(ParseUser.getCurrentUser());
		final CustomerRequest C_REQ = new CustomerRequest("Refill my water!!!", ui);
		final CurrentOrderActivity COACT = this;
		C_REQ.saveInBackGround(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e == null) {
					COACT.placeRequest(C_REQ);
				} else {
					Log.e(TAG, "Request did not save: " + e.getMessage());
				}
			}
		} );
				
	}
	
	/**
	 * 
	 * @author espeo196, jpmcneal
	 *
	 */
	public class OrderButtonListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			onRequestMade();
		}
		
	}

}
