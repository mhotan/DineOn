package uw.cse.dineon.restaurant;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONObject;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.Storable;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.DevelopTools;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.DineOnReceiver;
import uw.cse.dineon.library.util.ParseUtil;
import uw.cse.dineon.restaurant.login.RestaurantLoginActivity;
import uw.cse.dineon.restaurant.profile.ProfileActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

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

	protected static final String TAG = DineOnRestaurantActivity.class.getSimpleName();

	/**
	 * member that defines this restaurant user
	 * This encapsulated this user with this restaurant instance only
	 * Another Activity (like LoginActivity) does the work of 
	 * authenticating and creating an account.
	 * 
	 * Abstract Function
	 * 		thisRestaurant != null if and only if this user is logged in with a proper Restaurant account
	 */
	private Restaurant thisRestaurant;

	private DineOnReceiver rec;

	/*
	 * FragmentActivity specific methods
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create a broadcast receiver 
		// for listening for specific  
		try {
			// Set up the broadcast receiver for push notifications
			Map<String, String> m = new HashMap<String, String>();
			rec = DineOnReceiver.createDineOnReceiver(
					this.getClass().getMethod("onDiningSessionRequest", Map.class));
			
		} catch (NoSuchMethodException e) {
			// print out error msg
			Log.d(TAG, "Error: " + e.getMessage());
		}

		// Grab reference to the extras
		Bundle extras = getIntent().getExtras();

		Restaurant restaurant;

		if (DineOnConstants.DEBUG) {
			restaurant = DevelopTools.getDefaultRestaurant();
			setRestaurant(restaurant);
		} 
		else { // Regular Mode
			// Lets first check if the activity is being recreated after being
			// destroyed but there was an already existing restuarant
			if (savedInstanceState != null && (restaurant = 
					savedInstanceState.getParcelable(DineOnConstants.KEY_RESTAURANT)) != null) { 
				// Activity recreated
				setRestaurant(restaurant);
			} 
			else if (extras != null && (restaurant = extras.getParcelable(
					DineOnConstants.KEY_RESTAURANT)) != null) {
				// Activity started and created for the first time
				// Valid extras were passed into this
				setRestaurant(restaurant);
			} 
			// else Illegal state
			// No restaurant associated with this
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		IntentFilter iff = new IntentFilter("uw.cse.dineon.user.REQUEST_DINING_SESSION");
		PushService.subscribe(this, "uw_cse_dineon_" 
		+ ParseUser.getCurrentUser().getUsername(), this.getClass());
		this.registerReceiver(rec, iff);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		PushService.unsubscribe(this, "uw_cse_dineon_" + ParseUser.getCurrentUser().getUsername());
		this.unregisterReceiver(rec);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		// TODO Adjust dynamic attributes of the menu
		// Depending on the state of the current application
		// Adjust what is presented to the user		
		if (DineOnConstants.DEBUG) {
			return true;
		}
		
		if (!isLoggedIn()) {
			setMenuToNonUser(menu);
		}
		return true;
	}
	
	/**
	 * Given a menu set set this menu to show
	 * that the user is not logged in
	 * @param menu
	 */
	private void setMenuToNonUser(Menu menu) {
		MenuItem itemProfile = menu.findItem(R.id.item_restaurant_profile);
		if (itemProfile != null) {
			itemProfile.setEnabled(false);
			itemProfile.setVisible(false);
		}
		MenuItem itemLogout = menu.findItem(R.id.item_logout);
		if (itemLogout != null) {
			itemLogout.setEnabled(false);
			itemLogout.setVisible(false);
		}
		
		// Add a ability to log in
		MenuItem item = menu.add("Log in");
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				startLoginActivity();
				return false;
			}
		});
	}
	
	/**
	 * Start log in activity. 
	 */
	public void startLoginActivity() {
		Intent i = new Intent(this, RestaurantLoginActivity.class);
		startActivity(i);
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
		switch (item.getItemId()) {
		case R.id.item_restaurant_profile:
			// TODO Create Bundle with Restaurant inside
			startProfileActivity(null);
			break;
		case R.id.item_logout:
			// TODO Save the current restaurant
			// TODO Log out via Parse
			// TODO When we are done logging out of parse
			
			break;
		default:

		}
		return true;
	}
	
	/**
	 * Starts restaurant profile activity.
	 */
	public void startProfileActivity(Restaurant rest){
		Intent i = new Intent(this, ProfileActivity.class);
		if (rest != null) {
			i.putExtra(DineOnConstants.KEY_RESTAURANT, rest);
		}
		startActivity(i);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO Save state of all the fields of this activity
		// mRestaurant
		// Save to Parse then reference later
		savedInstanceState.putParcelable(DineOnConstants.KEY_RESTAURANT, thisRestaurant);

		super.onSaveInstanceState(savedInstanceState);
	}

	/**
	 * Sets the current state of this restaurant to the input.
	 * @param rest updated restaurant to set as this
	 */
	protected void setRestaurant(Restaurant rest){
		if (rest == null) {
			String message =  "Illegal Restaurant attempted to be set";
			Log.w(TAG, message);
			throw new IllegalArgumentException(message);
		}	
		thisRestaurant = rest;

		// TODO Update the Cloud with the updated restaurant instance
		// Perform asyncronous save to cloud of the updated state 
		// not very precedent
	}

	/**
	 * Returns the reference to the current Restaurant object associated with this user.
	 * 
	 * @return restaurant associated with this
	 */
	protected Restaurant getRestaurant(){
		return thisRestaurant;
	}

	/**
	 * Returns whether the user is logged in.
	 * This function can be used to determine the state
	 * of the application.
	 * @return whether a user is logged in
	 */
	protected boolean isLoggedIn() {
		// TODO Sync with Parse User to ensure
		// That the user is logged in via Parse
		// Then check if we have a associated restaurant
		if (thisRestaurant != null) {
			return true;
		}
		Log.w(TAG, "Restaurant instance associated with this user is null");
		return false;
	}

	/**
	 * A new dining sessin has been acquired.
	 * The restaurant must then update its state. 
	 * @param dSess Dining session to update our state with
	 */
	protected static void diningSessionAcquired(DiningSession dSess) {
		// TODO How to 
		// TODO Null check, Log error if null.
		// TODO Update our state
		// TODO Update UI
		
	}

	/*
	 * Brodcast receiver callback specific methods 
	 ************************************************************************/

	// attr :
	//		- userInfo : UserInfo.JSON
	//		- tableNum : int
	public static void onDiningSessionRequest(Map<String, String> attr) {
		Log.d("GOT_DINING_SESSION_REQUEST", "");
		try {
			// create a dining session
			JSONObject jObj = new JSONObject(attr.get("userInfo"));
			ParseObject user = new ParseObject("UserInfo");
			user.setObjectId(jObj.getString("objID"));
			user.put(UserInfo.NAME,jObj.getString(UserInfo.NAME));
			user.put(UserInfo.EMAIL, jObj.getString(UserInfo.EMAIL));
			user.put(UserInfo.PHONE, jObj.getString(UserInfo.PHONE));
	
			DiningSession newDS = new DiningSession(
					new LinkedList<Order>(), 
					0, 
					Integer.parseInt(attr.get("tableID")));
			UserInfo info = new UserInfo();
			info.unpackObject(user);
			newDS.addUser(info);
	
			// save to cloud
		
			ParseUtil.saveDataToCloud(newDS, 
					DineOnRestaurantActivity.class.
					getMethod("onSavedDiningSession", Boolean.class, String.class, Storable.class));
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			Log.d(TAG, "Error: " + e.getMessage());
		}
	}

	/**
	 * 
	 * @param success
	 * @param objID
	 * @param obj
	 */
	public static void onSavedDiningSession(Boolean success, String objID, Storable obj) {
		Log.d("SAVED_NEW_DINING_SESSION_REST", "");
		try {
		if (success) {
			// push notification for user
			DiningSession session = ((DiningSession)obj);
			Map<String, String> attr = new HashMap<String, String>();
			attr.put(DineOnConstants.OBJ_ID, objID);
			ParseUtil.notifyApplication(
					"uw.cse.dineon.user.CONFIRM_DINING_SESSION", 
					attr, 
					"uw_cse_dineon_" + session.getUsers().get(0).getName());
			
			diningSessionAcquired(session);
		} else {
			Log.d(TAG, "Error: A dining session couldn't be saved.");
		}
		} catch (Exception e) {
			Log.d(TAG, "Error: " + e.getMessage());
		}
	}


}
