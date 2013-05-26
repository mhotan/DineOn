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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * 
 * @author mhotan
 */
public class CurrentOrderActivity extends DineOnUserActivity { 
	
	
	private final String TAG = "CurrentOrderActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_current_order);
		
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
			startActivity(intent);
			break;
		default:
			break;
		}
		
		return true;
	}


	/**
	 * @param request String request description
	 */
	public void onRequestMade(String request) {
		UserInfo ui = new UserInfo(ParseUser.getCurrentUser());
		
		
		final CustomerRequest C_REQ = new CustomerRequest(request, ui);
		
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
			
}
