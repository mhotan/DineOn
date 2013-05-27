package uw.cse.dineon.user.bill;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.R;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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

	@Override
	public void doneWithOrder() {
		finish();
	}
			
}
