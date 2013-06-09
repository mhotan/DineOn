package uw.cse.dineon.user.bill;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.bill.CurrentBillFragment.PayBillListener;
import uw.cse.dineon.user.bill.CurrentOrderFragment.OrderUpdateListener;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * Activity that allows the the user to see their order update listener.
 * @author mhotan
 */
public class CurrentOrderActivity extends DineOnUserActivity 
implements PayBillListener, OrderUpdateListener { 
	
	private final String TAG = CurrentOrderActivity.class.getSimpleName();
	
	private Order mPending;
	
	private ProgressDialog mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_current_order);
		
		if (!mUser.hasPendingOrder()) {
			Log.e(TAG, TAG + " called but there is no active dining session and there is " 
					+ "no pending order");
			// If there is an error in the state of the application.
			// This activity relies
			finish();
		}
		mPending = mUser.getPendingOrder();
		
		if (mPending == null) {
			throw new RuntimeException(TAG 
					+ ": User return null pending order when it shouldn't be");
		}
		
		mDialog = getProgressDialog();
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
	
	/**
	 * Update the fragments views if they exists.
	 */
	private void udpateFragments() {
		FragmentManager manager = getSupportFragmentManager();
		
		// Update the order fragment if it exists.
		Fragment fragment = manager.findFragmentById(R.id.fragment_current_order);
		if (fragment != null && fragment instanceof CurrentOrderFragment) {
			CurrentOrderFragment orderFrag = (CurrentOrderFragment) fragment;
			orderFrag.updateOrder();
		}
		
		// Update the bill fragment if it exists
		fragment = manager.findFragmentById(R.id.fragment_current_bill);
		if (fragment != null && fragment instanceof CurrentBillFragment) {
			CurrentBillFragment billFrag = (CurrentBillFragment) fragment;
			billFrag.updateBill(getDiningSession());
		}
		
	}

	@Override
	public void payCurrentBill() {
		super.payBill();
		finish();
	}

	@Override
	public void onCommitPendingOrder() {
		// To user the
		mDialog.show();
		final Order TOORDER = mPending;
		TOORDER.saveInBackGround(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if (e == null) {
					DiningSession session = mUser.getDiningSession();
					mSat.requestOrder(session, TOORDER, session.getRestaurantInfo());
				} else {
					 onFailToSaveOrder();
				}
			}
		});
	}
	
	/**
	 * Failed to save order.
	 */
	private void onFailToSaveOrder() {
		Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show();
		mDialog.hide();
	}

	@Override
	public void onIncrementItemOrder(uw.cse.dineon.library.MenuItem item) {
		mPending.setItemQuantity(item, mPending.getQuantity(item) + 1);
		udpateFragments();
	}

	@Override
	public void onDecrementItemOrder(uw.cse.dineon.library.MenuItem item) {
		int newQty = mPending.getQuantity(item) - 1;
		if (newQty <= 0) {
			getDeletePromptDialog(item).show();
		} else {
			mUser.setMenuItemToOrder(item, newQty);
		}
		udpateFragments();
	}

	@Override
	public void onRemoveItemFromOrder(uw.cse.dineon.library.MenuItem item) {
		getDeletePromptDialog(item).show();
	}

	@Override
	public Order getOrder() {
		// We can assert not null.
		return mPending;
	}

	@Override
	public DiningSession getDiningSession() {
		// We can assert not null.
		return mUser.getDiningSession();
	}	
	
	@Override
	public void onConfirmOrder(DiningSession ds, String orderId) {
		super.onConfirmOrder(ds, orderId);
		// If success 
		mDialog.hide();
		mPending = mUser.getPendingOrder();
		// Make sure we update after assigning to pending order
		udpateFragments();
		invalidateOptionsMenu();
	}
	
	/**
	 * An alert dialog the user can use to delete 
	 * menu items from the pending order.
	 * @param todelete Menu item to delete.
	 * @return Alert Dialog to show.
	 */
	private AlertDialog getDeletePromptDialog(final uw.cse.dineon.library.MenuItem todelete) {
		AlertDialog.Builder builder = new Builder(this);
		String title = getResources().getString(
				R.string.string_format_remove_item_title, todelete.getTitle());
		builder.setTitle(title);
		String message = getResources().getString(
				R.string.string_format_remove_item_message, todelete.getTitle());
		builder.setMessage(message);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mPending.setItemQuantity(todelete, 0);
				udpateFragments();
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return builder.create(); // Finally show the alert dialog
	}
	
	/**
	 * Return loading proress dialog.
	 * @return Progress Dialog 
	 */
	private ProgressDialog getProgressDialog() {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setTitle("Processing Order...");
		return dialog;
	}
}
