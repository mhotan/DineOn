package uw.cse.dineon.restaurant;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Storable;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.DineOnReceiver;
import uw.cse.dineon.library.util.ParseUtil;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

/**
 * General Fragment Activity class that pertains to a specific Restaurant
 * client.  Once the Restaurant logged in then they are allowed specific
 * information related to the restaurant
 * @author mhotan
 */
public class DineOnRestaurantActivity extends FragmentActivity {
	
	private static final String TAG = DineOnRestaurantActivity.class.getSimpleName();
	private DineOnReceiver rec;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			// Set up the broadcast receiver for push notifications
			Map<String, String> m = new HashMap<String, String>();
			rec = DineOnReceiver.createDineOnRecevier(
					this.getClass().getMethod("onDiningSessionRequest", m.getClass()), 
					this,
					this.getClass(), 
					"uw.cse.dineon.user.REQUEST_DINING_SESSION", 
					"uw.cse.dineon." + ParseUser.getCurrentUser().getUsername()); // restaurant name
			
		} catch (NoSuchMethodException e) {
			// print out error msg
			Log.d(TAG, "Error: " + e.getMessage());
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		IntentFilter iff = new IntentFilter("uw.cse.dineon.user.REQUEST_DINING_SESSION");
		PushService.subscribe(this, "push", DineOnRestaurantActivity.class);
		this.registerReceiver(rec, iff);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		this.unregisterReceiver(rec);
	}
	
	
	// attr :
	//		- userInfo : UserInfo.JSON
	//		- tableNum : int
	public static void onDiningSessionRequest(Map<String, String> attr) {
		Log.d("GOT_DINING_SESSION_REQUEST", "");
		
		// create a dining session
		ParseObject user = new ParseObject(attr.get("userInfo"));
	    
		DiningSession newDS = new DiningSession(
				new LinkedList<Order>(), 
				0, 
				Integer.parseInt(attr.get("tableID")));
		UserInfo info = new UserInfo();
		info.unpackObject(user);
		newDS.addUser(info);
		
		// save to cloud
		try {
			ParseUtil.saveDataToCloud(newDS, 
					DineOnRestaurantActivity.class.
					getMethod("onSavedDiningSession", Boolean.class, String.class));
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void onSavedDiningSession(Boolean success, String objID, Storable obj) {
		Log.d("SAVED_NEW_DINING_SESSION_REST", "");
		
		if (success) {
			// push notification for user
			DiningSession session = ((DiningSession)obj);
			Map<String, String> attr = new HashMap<String, String>();
			attr.put("objID", objID);
			ParseUtil.notifyApplication(
					"w.cse.dineon.user.CONFIRM_DINING_SESSION", 
					attr, 
					"uw.cse.dineon.user." + session.getUsers().get(0).getName());
		} else {
			Log.d(TAG, "Error: A dining session couldn't be saved.");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Note that override this method does not mean the actualy 
		//  UI Menu is updated this is done manually
		//  See basic_menu under res/menu for ids
		inflater.inflate(R.menu.basic_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		return true;

	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// TODO Save state of all the fields of this activity
		// mDiningSession
		// Save to Parse then reference later
	
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  // TODO Restore data
	  // mDiningSession
	}
}
