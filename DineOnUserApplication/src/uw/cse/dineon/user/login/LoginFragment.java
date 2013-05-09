package uw.cse.dineon.user.login;

import uw.cse.dineon.user.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Login View presented to users to use Facebook, Twitter, email and password credentials to log in.
 * @author mhotan
 */
public class LoginFragment extends android.support.v4.app.Fragment {

	/**
	 * Activity that reacts to user interactions.
	 */
	private OnLoginListener mListener;

	private EditText emailInput, passwordInput;
	
	private static ProgressDialog mProgressDialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login,
				container, false);

		// Associate the values of the input text boxes
		emailInput = (EditText) view.findViewById(R.id.input_login_email);
		passwordInput = (EditText) view.findViewById(R.id.input_login_password);

		// Assign login listener
		Button loginButton = (Button) view.findViewById(R.id.button_login);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String email = emailInput.getText().toString();
				String password = passwordInput.getText().toString();
				attemptLogin(email, password);
			}
		});
		Button facebookLoginButton = (Button) view.findViewById(R.id.button_facebook_login);
		facebookLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onLoginWithFacebook();
			}
		});
		//TODO Save for later
//		Button twitterLoginButton = (Button) view.findViewById(R.id.button_twitter_login);
//		twitterLoginButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mListener.onLoginWithTwitter();
//			}
//		});
		return view;
	}


	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnLoginListener) {
			mListener = (OnLoginListener) activity;
		} 
		else {
			throw new ClassCastException(activity.toString()
					+ " must implement LoginFragment.OnLoginListener");
		}
	}
	
	/**
	 * @return The email address that was forgotten
	 */
	public String getCurrentEmail() {
		return emailInput.getText().toString();
	}

	/**
	 * Attempt to login that can be accessed by external activities
	 * to login.
	 * @param username email of user
	 * @param password password of user
	 */
	public void attemptLogin(String username, String password) {
		// TODO Translate email and password to a single login credential
		mListener.onLogin(username, password);
	}

	/**
	 * Listener that is intended to allow any owning context of an 
	 * LoginFragment instance to react and communicate Login related request.
	 * All activities that uses a LoginFragment MUST implement this listener
	 *   
	 * @author mhotan
	 */
	public interface OnLoginListener {

		/**
		 * User attempts to Login via email and password.
		 * @param username username requested 
		 * @param password password to use
		 */
		void onLogin(String username, String password);
		
		/**
		 * User request to login with facebook.
		 */
		void onLoginWithFacebook();
		
		/**
		 * User request to login with twitter.
		 */
		void onLoginWithTwitter();
	}

}
