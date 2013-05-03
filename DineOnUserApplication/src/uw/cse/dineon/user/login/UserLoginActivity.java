package uw.cse.dineon.user.login;

import uw.cse.dineon.library.User;
import uw.cse.dineon.library.util.CredentialValidator;
import uw.cse.dineon.library.util.CredentialValidator.Resolution;
import uw.cse.dineon.library.util.DevelopTools;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.restaurantselection.RestaurantSelectionActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Initial activity that user is brought to in order to gain admittance.  
 * Activity that allows users to login via Email and Password, Facebook, or Twitter
 * <b>Application has the ability of connecting to facebook and twitter
 * <b>Once login is validated users will be taken to the restaurant selection activity
 * <b>User also have the ability to create a new account  
 * 
 * @author mhotan
 */
public class UserLoginActivity extends FragmentActivity implements 
LoginFragment.OnLoginListener {

	private static final String TAG = UserLoginActivity.class.getSimpleName();

	// Request code to create a new account
	private static final int REQUEST_CREATE_NEW_ACCOUNT = 0x1;

	/**
	 * Return code for  
	 */
	public static final String RETURN_CODE_LOGIN_CREDENTIALS = TAG + ":LOGIN_NEW_CREDENTIALS";

	/**
	 * 
	 */
	public static final String RETURN_CODE_LOGIN_3RDPARTY = TAG + ":LOGIN_3RD_PARTY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	protected void onResume(){
		super.onResume();	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CREATE_NEW_ACCOUNT) {

			User user;
			if ((user = data.getParcelableExtra(DineOnConstants.KEY_USER)) != null) {
				startRestSelectionAct(user);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Note that override this method does not mean the actualy 
		//  UI Menu is updated this is done manually
		//  See basic_menu under res/menu for ids
		inflater.inflate(R.menu.login_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.option_forgot_password: 
			// TODO Implement
			// TODO Implement
			DevelopTools.getUnimplementedDialog(this, null).show();
		case R.id.option_create_new_account:
			onCreateNewAccount();
			break;
		}
		return true;
	}

	// User interaction methods

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
						// TODO Change to asyncronous call to get the User instance
						startRestSelectionAct(null);
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

	@Override
	public void onLoginWithFacebook() {
		// TODO Auto-generated method stub
		DevelopTools.getUnimplementedDialog(this, null).show();
	}

	@Override
	public void onLoginWithTwitter() {
		// TODO Auto-generated method stub
		DevelopTools.getUnimplementedDialog(this, null).show();
	}


	/**
	 * Starts an activity for a result to allow the user to start a new account
	 */
	public void onCreateNewAccount() {
		// TODO Auto-generated method stub
		Intent creatAccountIntent = new Intent(this, CreateNewAccountActivity.class);
		startActivityForResult(creatAccountIntent, REQUEST_CREATE_NEW_ACCOUNT);
	}

	/**
	 * 
	 * @param user
	 */
	private void startRestSelectionAct(User user){
		Intent i = new Intent(this, RestaurantSelectionActivity.class);
		//		i.putExtra(RestaurantSelectionActivity.EXTRA_USER, loginCredentials);
		startActivity(i);
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
