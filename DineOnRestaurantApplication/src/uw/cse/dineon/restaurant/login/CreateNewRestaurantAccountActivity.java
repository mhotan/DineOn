package uw.cse.dineon.restaurant.login;

import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.util.CredentialValidator;
import uw.cse.dineon.library.util.CredentialValidator.Resolution;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.library.util.Utility;
import uw.cse.dineon.restaurant.DineOnRestaurantApplication;
import uw.cse.dineon.restaurant.R;
import uw.cse.dineon.restaurant.active.RestauarantMainActivity;
import uw.cse.dineon.restaurant.login.CreateNewAccountFragment.CreateNewAccountListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

/**
 * This class is in charge on instantiating a Restaurant object.
 * Then associate the restaurant object with this user.
 * @author mhotan
 */
public class CreateNewRestaurantAccountActivity extends FragmentActivity 
implements CreateNewAccountListener {

	private static final String TAG = CreateNewRestaurantAccountActivity.class.getSimpleName();

	/**
	 * Progress bar dialog for showing user progress.
	 */
	private ProgressDialog mProgressDialog;

	/**
	 * Instance to myself.
	 */
	private CreateNewRestaurantAccountActivity This;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_account);

		This = this;
	}

//	/**
//	 * This automates the addition of the User Intent. Should never be called
//	 * when mUser is null.
//	 * @param intent Intent
//	 */
//	@Override
//	public void startActivity(Intent intent) {
////		intent.putExtra(DineOnConstants.KEY_RESTAURANT, mRestaurant);
//		super.startActivity(intent);
//	}

	@Override
	public void submitNewAccount(String username, String email, String pw,
			String pwRepeat) {
		// Handle the validation
		createProgressDialog();

		Resolution completeRes = CredentialValidator.validateAll(username, email, pw, pwRepeat);

		if (completeRes.isValid() && !username.equals("")) { // Valid passwords
			final ParseUser USER = new ParseUser();
			USER.setUsername(username);
			USER.setPassword(pw);
			USER.setEmail(email);
			USER.signUpInBackground(new RestaurantSignUpCallback(USER));
		} 
		else {
			Utility.getFailedToCreateAccountDialog(completeRes.getMessage(), This).show();
		}
	}
	
	@Override
	protected void onPause() {
		destroyProgressDialog();
		super.onPause();
	}

	/**
	 * Private Helper Class to help create a new ParseUser.
	 * @author mhotan 
	 */
	private class RestaurantSignUpCallback extends SignUpCallback {

		private final ParseUser mCallbackParseUser;
		
		/**
		 * Creates a Restaurant Sign up callback.
		 * @param user User to sign up.
		 */
		public RestaurantSignUpCallback(ParseUser user) {
			mCallbackParseUser = user;
		}
		
		@Override
		public void done(ParseException e) {

			if (e == null) {
				// Download the Restaurant
				try {
					final Restaurant NEWREST = new Restaurant(mCallbackParseUser);
					NEWREST.saveInBackGround(new SaveCallback() {
						
						@Override
						public void done(ParseException e) {
							destroyProgressDialog();
							if (e == null) {
								DineOnRestaurantApplication.logIn(NEWREST);
								startMainActivity();
							} else {
								Utility.getFailedToCreateAccountDialog(
										e.getMessage(), This).show();
							}
						}
					});
				} catch (ParseException e1) {
					Utility.getFailedToCreateAccountDialog(
							"Need to be connected to internet", This).show();
				}
			} else {
				destroyProgressDialog();
				// Sign up didn't succeed. Look at the ParseException
				// to figure out what went wrong
				Utility.getFailedToCreateAccountDialog(e.getMessage(), This).show();
				
			}
		}
	}
	
	/**
	 * Starts the Main activity for this restaurant.
	 */
	private void startMainActivity() {
		Intent i = new Intent(this, RestauarantMainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	// //////////////////////////////////////////////////////////////////////
	// /// UI Specific methods
	// //////////////////////////////////////////////////////////////////////

	/**
	 * Instantiates a new progress dialog and shows it on the screen.
	 */
	protected void createProgressDialog() {
		if (mProgressDialog != null) {
			return;
		}
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Logging in...");
		mProgressDialog.setMessage("Getting you your own restaurant");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();
	}

	/**
	 * Hides the progress dialog if there is one.
	 */
	protected void destroyProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}
	

}
