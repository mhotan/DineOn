package uw.cse.dineon.user;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Storable;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.DineOnReceiver;
import uw.cse.dineon.library.util.ParseUtil;
import uw.cse.dineon.user.bill.CurrentOrderActivity;
import uw.cse.dineon.user.checkin.IntentIntegrator;
import uw.cse.dineon.user.checkin.QRCheckin;
import uw.cse.dineon.user.general.ProfileActivity;
import uw.cse.dineon.user.general.UserPreferencesActivity;
import uw.cse.dineon.user.login.UserLoginActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.parse.ParseUser;
import com.parse.PushService;

/**
 * Generl Fragment Activity class that pertains to a specific user.
 * Once the User is logged in then they are allowed specific access to
 * different regions of the application.
 *
 * In Particular their user specific preferences
 * @author mhotan
 */
public class DineOnUserActivity extends FragmentActivity {

	private static final String TAG = DineOnUserActivity.class.getSimpleName();
	private static final String CHANNEL = "uw_cse_dineon_" + ParseUser.getCurrentUser().getUsername();
	private static final String ACTION = "uw.cse.dineon.user.CONFIRM_DINING_SESSION"; 
	protected static DiningSession mDiningSession;

	private DineOnReceiver rec;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			// Set up the broadcast receiver for push notifications
			rec = DineOnReceiver.createDineOnRecevier(this.getClass().getMethod("onCheckInCallback", Map.class));
		} catch (NoSuchMethodException e) {
			// print out error msg
			Log.d(TAG, "Error: " + e.getMessage());
		}
	}
	
	

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter iff = new IntentFilter(ACTION);
		PushService.subscribe(this, CHANNEL,this.getClass());
		this.registerReceiver(rec, iff);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		this.unregisterReceiver(rec);
		PushService.unsubscribe(this, CHANNEL);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Note that override this method does not mean the actualy
		//  UI Menu is updated this is done manually
		//  See basic_menu under res/menu for ids
		inflater.inflate(R.menu.basic_menu, menu);
		//Hides the 
		final MenuItem item = menu.findItem(R.id.option_bill);
		item.setEnabled(false);
		item.setVisible(false);

		final Menu m = menu;

		//Sets the necessary onClickListeners for the menu
		//items with an action layout.
		List<MenuItem> customActionBarButtons = new ArrayList<MenuItem>();
		customActionBarButtons.add(menu.findItem(R.id.option_bill));
		customActionBarButtons.add(menu.findItem(R.id.option_check_in));
		
		setOnClick(m, customActionBarButtons);

		return true;
	}
	
	public void startLoginActivity() {
		Intent i = new Intent(this, UserLoginActivity.class);
		startActivity(i);
	}

	/**
	 * Creates the onClick listeners for the specified menu items.
	 * 
	 * @param m the parent menu
	 * @param items the list of MenuItems to create listeners for
	 */
	private void setOnClick(final Menu m, List<MenuItem> items) {
		for (final MenuItem item : items) {
			item.getActionView().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {   
					m.performIdentifierAction(item.getItemId(), 0);
				}
			});
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		switch (item.getItemId()) {
		case R.id.option_profile:
			i = new Intent(getApplicationContext(), ProfileActivity.class);
			break;
		case R.id.option_settings:
			i = new Intent(getApplicationContext(), UserPreferencesActivity.class);
			break;
		case R.id.option_check_in:
			IntentIntegrator integrator = new IntentIntegrator(this);
			integrator.initiateScan();
			break;
		case R.id.option_bill:
			i = new Intent(getApplicationContext(), CurrentOrderActivity.class);
			// Count all the elements that the user has currently selected
			startActivityForResult(i, DineOnConstants.REQUEST_VIEW_CURRENT_ORDER);
			break;
		case R.id.option_logout:
			// TODO: implement logout
			break;
		default:
			//Unknown
		}
		if (i != null) {
			startActivity(i);
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		QRCheckin.QRResult(requestCode, resultCode, intent);
	}

	/**
	 * This is the defined call back method for when the
	 * User attempts to check in to a restaurant.  Once the check
	 * @param session is a mapping from the arguments returned from the call
	 * 	DineOnConstants.OBJ_ID => Parse Object ID for Dining Session
	 */
	public static void onCheckInCallback(Map<String, String> session) {
		try {
			Log.d("CONFIRM_DINING_SESSION_FROM_REST", "");

			// Extract the object ID from the return map
			String objID = session.get("objectId");

			// Use Utility to call Parse and get the Dining Session instance
			if (objID == null || objID.isEmpty()) {
				// TODO Update the UI
				// Handle the fail case where no dining session
				// was created
			}

			// Then Bundle the Dining Session Instance into
			Method m = null;

			m = DineOnUserActivity.class.getMethod("onDiningSessionRecievedCallback",
					List.class);

			Map<String, String> attr = new HashMap<String, String>();
			attr.put("objectId", objID);

			ParseUtil.getDataFromCloud(DiningSession.class, m, attr);
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "Failed to invocate method onDiningSessionRecievedCallback()");
		}

	}

	/**
	 * This is a callback for when the Dining Session is recieved via local
	 * caching or.
	 * @param list List<Storable>
	 */
	public static void onDiningSessionRecievedCallback(List<Storable> list) {
		// Assert that the first item in the list is
		// is a DiningSession
		if (list != null && list.size() == 1) {
			throw new IllegalArgumentException("List returned is not valid: " + list);
		}
		mDiningSession = (DiningSession) list.get(0);

		// DEBUG:
		Log.d("GOT_DINING_SESSION_FROM_CLOUD", mDiningSession.getTableID() + "");

		// TODO Extract channel for push
		// TODO Register for the channel and start listening for updates
		// TODO Extract object id for restaurant

		// Bundle up dining session
		// Start RestaurantMainActivity with bundle
	}

	/**
	 * Saves the instance of the current DiningSession. Information can
	 * be recovered by using onRestoreInstanceState.
	 *
	 * @param savedInstanceState Bundle to store the current
	 * 		activity's data to.
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// bundle mDiningSession w/ our bundle method
		//		Bundle diningBundle = mDiningSession.bundle();

		// save entire bundle w/ key value, retrieve using this string
		//		savedInstanceState.putBundle("diningSession", diningBundle);
		super.onSaveInstanceState(savedInstanceState);
	}

	/**
	 * Restores an instance of a DiningSession from the given Bundle
	 * parameter.
	 *
	 * @param savedInstanceState Bundle that holds session information
	 * 		to be restored.
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		//		mDiningSession.unbundle(savedInstanceState.getBundle("diningSession"));
	}
}