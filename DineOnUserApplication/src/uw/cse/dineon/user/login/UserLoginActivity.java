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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
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
	private static final int REQUEST_LOGIN_FACEBOOK = 0x2;
	
	public static final String EXTRA_FACEBOOK = "Login with facebook";

	/**
	 * Progress bar dialog for showing user progress
	 */
	private ProgressDialog mProgressDialog;
	/**
	 * Login to handle user attempts to login
	 */
	private DineOnLoginCallback mLoginCallback;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		ParseUser user = ParseUser.getCurrentUser();
		if (user != null) {
			// TODO Download the User Object
			if (DineOnConstants.DEBUG) {
				startRestSelectionAct(null);
			} 
			else {
				// Download User 
			}
			
		}
		
		mLoginCallback = new DineOnLoginCallback(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// Destroy any current running progress bars
		destroyProgressDialog();
		
		if (resultCode != RESULT_OK) {
			return;
		}

		// Valid return type
		if (requestCode == REQUEST_CREATE_NEW_ACCOUNT) {	
			if (data.getBooleanExtra(EXTRA_FACEBOOK, false)) {
				onLoginWithFacebook();
				return;
			}
			
			User user;
			if ((user = data.getParcelableExtra(DineOnConstants.KEY_USER)) != null) {
				startRestSelectionAct(user);
				return;
			}
		}
		else if (requestCode == REQUEST_LOGIN_FACEBOOK) {
			ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
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
		default:
			Log.w(TAG, "Unknown Options Item pressed");
		}
		return true;
	}

	// User interaction methods

	/**
	 * User login via email and pw
	 */
	@Override
	public void onLogin(String username, String password) {
		createProgressDialog();
		Resolution unResolution = CredentialValidator.isValidUserName(username);
		Resolution pwResolution = CredentialValidator.isValidPassword(password);

		StringBuffer buf = new StringBuffer();
		if (!unResolution.isValid()) {
			destroyProgressDialog();
			buf.append(unResolution.getMessage() + "\n");
		}
		if (!pwResolution.isValid()) {
			destroyProgressDialog();
			buf.append(pwResolution.getMessage() + "\n");
		}

		if (buf.length() > 0) {
			destroyProgressDialog();
			showAlertBadInput(buf.toString());
			return;
		}

		ParseUser.logInInBackground(username, password, mLoginCallback);
	} 

	/**
	 * User login via Facebook
	 */
	@Override
	public void onLoginWithFacebook() {
		// TODO Disable all the buttons so user does not
		// Monkey it
		createProgressDialog();
		// Replace actionbar with menu

		// Process the face book application
		ParseFacebookUtils.logIn(this, REQUEST_LOGIN_FACEBOOK, mLoginCallback);
	}


	@Override
	public void onLoginWithTwitter() {
		// TODO Auto-generated method stub
		DevelopTools.getUnimplementedDialog(this, null).show();
	}

	/**
	 * Starts an activity for a result to allow the user to start a new account.
	 */
	public void onCreateNewAccount() {
		Intent creatAccountIntent = new Intent(this, CreateNewAccountActivity.class);
		startActivityForResult(creatAccountIntent, REQUEST_CREATE_NEW_ACCOUNT);
	}

	/**
	 * Starts Restaurant selection activity with current
	 * TODO send a user instance through this bundle
	 * @param user
	 */
	private void startRestSelectionAct(User user) {
		Intent i = new Intent(this, RestaurantSelectionActivity.class);
		destroyProgressDialog();
		startActivity(i);
	}

	/**
	 * Instantiates a new progress dialog and shows it on the screen.
	 */
	private void createProgressDialog() {
//		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  
//		setProgressBarIndeterminateVisibility(true); 
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			return;
		}
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("DineOn Login");
	    mProgressDialog.setMessage("Logging in...");       
	    mProgressDialog.setIndeterminate(true);
	    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    mProgressDialog.show();
	    
	}

	/**
	 * Hides the progress dialog if there is one.
	 */
	private void destroyProgressDialog() {
//		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  
//		setProgressBarIndeterminateVisibility(false); 
		if(mProgressDialog != null && mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
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
	
	/**
	 * Custom callback to handle login results
	 * @author mhotan
	 */
	private class DineOnLoginCallback extends com.parse.LogInCallback {

		private final Context mContext;
		
		/**
		 * Creates a callback associated with this context
		 * @param ctx
		 */
		DineOnLoginCallback(Context ctx) {
			mContext = ctx;
		}
		
		@Override
		public void done(ParseUser user, ParseException e) {
			// Turn off progress bar
			destroyProgressDialog();
			
			if (user == null) {
				Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
				// TODO Re enable the screen					
				// TODO Toast the user that login was cancelled
				Toast.makeText(mContext, "Login failed", Toast.LENGTH_SHORT).show();
				return;
			} 
			
			// TODO
			// This method at this point needs to produce a User Instance
			User mUser; 
			if (user.isNew()) {
				Log.d(TAG, "User signed up and logged in through Facebook!");

				// Now we just need a user object
				if (!DineOnConstants.DEBUG) { 
					mUser = CreateNewAccountActivity.createNewUser(user);
				}
				// TODO Create a new User ParseObject and send to next activity
				// Associate that user to the cloud
				startRestSelectionAct(null);// Change null to valid User object
			} 
			else {
				Log.d(TAG, "User logged in through Facebook!");

				// Query for a user for the associated 
				
				startRestSelectionAct(null);// Change null to valid User object
				// TODO Extract the user's User ParseObject and send to next activity
			}
			
		}
	}

}
