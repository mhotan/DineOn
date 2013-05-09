package uw.cse.dineon.restaurant.login;

import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.util.CredentialValidator;
import uw.cse.dineon.library.util.Utility;
import uw.cse.dineon.library.util.CredentialValidator.Resolution;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.R;
import uw.cse.dineon.restaurant.active.RestauarantMainActivity;
import uw.cse.dineon.restaurant.login.CreateNewAccountFragment.CreateNewAccountListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

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
	 * String representation of restaurant id.
	 */
	private String mRestaurantID;

	/**
	 * Progress bar dialog for showing user progress.
	 */
	private ProgressDialog mProgressDialog;

	/**
	 * Instance to myself.
	 */
	private Context thisCxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_account);

		thisCxt = this;
	}

	/**
	 * This automates the addition of the User Intent. Should never be called
	 * when mUser is null.
	 * @param intent Intent
	 */
	@Override
	public void startActivity(Intent intent) {
		if (DineOnConstants.DEBUG && mRestaurantID == null) {
			Toast.makeText(this, "Need to create or download a User",
					Toast.LENGTH_SHORT).show();
			return;
		}
		intent.putExtra(DineOnConstants.KEY_RESTAURANT, mRestaurantID);
		super.startActivity(intent);
	}

	@Override
	public void submitNewAccount(String username, String email, String pw,
			String pwRepeat) {
		// Handle the validation
		createProgressDialog();

		Resolution completeRes = CredentialValidator.validateAll(username, email, pw, pwRepeat);

		if (completeRes.isValid()) { // Valid passwords
			final ParseUser USER = new ParseUser();
			USER.setUsername(username);
			USER.setPassword(pw);
			USER.setEmail(email);
			USER.signUpInBackground(new SignUpCallback() {

				@Override
				public void done(ParseException e) {

					// stop the alert dialog
					destroyProgressDialog();

					if (e == null) {
						// They are logged in as a ParseUser
						final Restaurant REST = new Restaurant(USER);
						// Callback within a callback egh...
						// Save our new restaurant in the backgrounf
						REST.saveInBackGround(new SaveCallback() {
							@Override
							public void done(ParseException e) {
								if (e == null) { // Success
									// There exist a restaurant instance for this 
									mRestaurantID = REST.getObjId();
									startMainActivity();
								} else { // Fail to save
									// Alert dialog notifying user of exceptional case
									// Unable to save restaurant
									Utility.getFailedToCreateAccountDialog(
											e.getMessage(), thisCxt).show();
								}
							}
						});
					} else {
						// Sign up didn't succeed. Look at the ParseException
						// to figure out what went wrong
						Utility.getFailedToCreateAccountDialog(e.getMessage(), thisCxt).show();
					}

					// Stop the alert dialog

				}
			});
		} 
		else {
			Utility.getFailedToCreateAccountDialog(completeRes.getMessage(), thisCxt).show();
		}
	}

	/**
	 * Starts the Main activity for this restaurant.
	 */
	private void startMainActivity() {
		Intent i = new Intent(this, RestauarantMainActivity.class);
		startActivity(i);
	}

	// //////////////////////////////////////////////////////////////////////
	// /// UI Specific methods
	// //////////////////////////////////////////////////////////////////////

	/**
	 * Instantiates a new progress dialog and shows it on the screen.
	 */
	protected void createProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			return;
		}
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Getting you ready to DineOn!");
		mProgressDialog.setMessage("Logging in...");
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
