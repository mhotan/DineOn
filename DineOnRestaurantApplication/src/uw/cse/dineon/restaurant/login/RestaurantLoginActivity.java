package uw.cse.dineon.restaurant.login;

import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.util.CredentialValidator;
import uw.cse.dineon.library.util.CredentialValidator.Resolution;
import uw.cse.dineon.library.util.DevelopTools;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.DineOnRestaurantActivity;
import uw.cse.dineon.restaurant.R;
import uw.cse.dineon.restaurant.active.RestauarantMainActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Login Activity for Restaurant users.  Usually sets up the initial
 * @author mhotan
 */
public class RestaurantLoginActivity extends DineOnRestaurantActivity 
implements LoginFragment.OnLoginListener {

	private final String TAG = this.getClass().getSimpleName();

	// Request code to create a new account
	private static final int REQUEST_CREATE_NEW_ACCOUNT = 0x1;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}
	
	/**
	 * Given a valid restaurant, proceed to next activity.
	 * @param thisRest
	 */
	private void goToRestaurantMain(Restaurant thisRest) {
		Intent i = new Intent(this, RestauarantMainActivity.class);
		// If debug go straight to the activity
		if (DineOnConstants.DEBUG) {
			startActivity(i);
			return;
		}
		
		// Real process we must assert there is a restaurant to use
		if (thisRest == null) {
			throw new IllegalArgumentException(
					"Cannot go to RestaurantMainActivity with null reference");
		}
		i.putExtra(DineOnConstants.KEY_RESTAURANT, thisRest);
		startActivity(i);
	}

	/**
	 * Attempts to create new account. 
	 * Uses another activity to creat this account.
	 */
	private void createNewAccount() {
		Intent i = new Intent(this, CreateNewRestaurantAccountActivity.class);
		startActivityForResult(i, REQUEST_CREATE_NEW_ACCOUNT);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Note that override this method does not mean the actually 
		//  UI Menu is updated this is done manually
		//  See basic_menu under res/menu for ids
		inflater.inflate(R.menu.login_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.option_create_new_account) {
			//TODO Implement
			createNewAccount();
		} 
		else if (id == R.id.option_forgot_password) {
			//TODO Implement
			DevelopTools.getUnimplementedDialog(this, null).show();
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		if (requestCode == REQUEST_CREATE_NEW_ACCOUNT) {
			if (DineOnConstants.DEBUG) {
				goToRestaurantMain(null);
			}
			
			Restaurant rest;
			if ((rest = data.getParcelableExtra(DineOnConstants.KEY_RESTAURANT)) != null) {
				// Successfully created a new account
				goToRestaurantMain(rest);
			}
			showAlertBadInput("Failed to log in");
			Log.w(TAG, "Failed to create account");
		}
	}

	@Override
	public void onLogin(String username, String password) {
		Resolution unResolution = CredentialValidator.isValidUserName(username);
		Resolution pwResolution = CredentialValidator.isValidPassword(password);

		StringBuffer buf = new StringBuffer();
		if (!unResolution.isValid()) {
			buf.append(unResolution.getMessage() + "\n");
		}
		if (!pwResolution.isValid()) {
			buf.append(pwResolution.getMessage() + "\n");
		}

		if (buf.length() > 0) {
			showAlertBadInput(buf.toString());
			return;
		}

		ParseUser.logInInBackground(username, password, new LogInCallback() {

			@Override
			public void done(ParseUser user, ParseException e) {
				if (user != null) {
					// Successfuly logged in
					if (DineOnConstants.DEBUG) {
						goToRestaurantMain(null);
					}
					// TODO Download the current restaurant associated
					// with this user from Parse.
					// when complete call goToRestaurantMain(Restaurant) 
				} else {
					// Signup failed. Look at the ParseException to see what happened.
					showAlertBadInput(e.getMessage());
				}
			}
		});
	}

	/**
	 * Show bad input alert message for logging in.
	 * @param message
	 */
	public void showAlertBadInput(String message){
		AlertDialog.Builder b = new Builder(this);
		b.setTitle("Failed to login");
		b.setMessage(message);
		b.setCancelable(true);
		b.setPositiveButton("Try Again", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).show();
	}

}
