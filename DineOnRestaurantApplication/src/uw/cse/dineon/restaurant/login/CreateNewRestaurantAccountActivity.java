package uw.cse.dineon.restaurant.login;

import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.util.CredentialValidator;
import uw.cse.dineon.library.util.CredentialValidator.Resolution;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.R;
import uw.cse.dineon.restaurant.login.CreateNewAccountFragment.CreateNewAccountListener;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * This class is in charge on instantiating a Restaurant object.
 * Then associate the restaurant object with this user.
 * @author mhotan
 */
public class CreateNewRestaurantAccountActivity extends FragmentActivity 
implements CreateNewAccountListener {

	private static final String TAG = CreateNewRestaurantAccountActivity.class.getSimpleName();

	private Restaurant mRestaurant;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_account);
	}

	@Override
	public void submitNewAccount(String username, String email, String pw,
			String pwRepeat) {
		// Handle the validation
		Resolution completeRes = CredentialValidator.validateAll(username, email, pw, pwRepeat);

		if (completeRes.isValid()) { // Valid passwords
			ParseUser user = new ParseUser();
			user.setUsername(username);
			user.setPassword(pw);
			user.setEmail(email);
			user.signUpInBackground(new SignUpCallback() {

				@Override
				public void done(ParseException e) {
					if (e == null) {
						// Hooray! Let them use the app now.
						// TODO Create a new Restaurant object and save it to Parse.
						// Then return the Restaurant object to Login Activty for processing
						
						
						if (DineOnConstants.DEBUG) {
							returnResult(null);
						}
					} 
					else {
						// Sign up didn't succeed. Look at the ParseException
						// to figure out what went wrong
						showFailAlertDialog(e.getMessage());
					}
				}
			});
		} 
		else {
			showFailAlertDialog(completeRes.getMessage());
		}
	}

	/**
	 * Finish this activity and return to login.
	 * @param success
	 */
	private void returnResult(Restaurant restaurant){
		mRestaurant = restaurant;
		this.finish();
	}

	@Override
	public void finish(){
		// Send restaurant instance back
		if (!DineOnConstants.DEBUG) {
			Intent retIntent = new Intent();
			retIntent.putExtra(DineOnConstants.KEY_RESTAURANT, mRestaurant);
			setResult(RESULT_OK, retIntent);
		}
		super.finish();
	}

	private void showFailAlertDialog(String error){
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("Failed to create account");
		builder.setMessage(error);
		builder.setCancelable(true);
		builder.setPositiveButton("Try Again", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, "Check In Activity back pressed");
		super.onBackPressed();
	}

}
