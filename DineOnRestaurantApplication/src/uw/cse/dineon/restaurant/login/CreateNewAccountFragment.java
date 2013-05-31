package uw.cse.dineon.restaurant.login;

import uw.cse.dineon.restaurant.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Fragment view for creating new account for the application.
 * @author mhotan
 */
public class CreateNewAccountFragment extends Fragment implements OnClickListener {

	private CreateNewAccountListener mListener;

	/**
	 * Input for all the values.
	 */
	private EditText mUsername, mEmail, mPassword, mPasswordRepeat;
	private EditText mCreditCard, mSecurityCode, mExpMo, mExpYr, mZip; 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_create_new_account,
				container, false);

		mUsername = (EditText) view.findViewById(R.id.input_createnewaccount_username);
		mEmail = (EditText) view.findViewById(R.id.input_createnewaccount_email);
		mPassword = (EditText) view.findViewById(R.id.input_createnewaccount_password);
		mPasswordRepeat = (EditText) view.findViewById(
				R.id.input_createnewaccount_repeat_password);
		mCreditCard = (EditText) view.findViewById(R.id.input_credit_card_number);
		mSecurityCode = (EditText) view.findViewById(R.id.input_security_code);
		mExpMo = (EditText) view.findViewById(R.id.input_expiration_month);
		mExpYr = (EditText) view.findViewById(R.id.input_expiration_year);
		mZip = (EditText) view.findViewById(R.id.input_zip_code);

		Button submit = (Button) view.findViewById(R.id.button_create_account);
		submit.setOnClickListener(this);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof CreateNewAccountListener) {
			mListener = (CreateNewAccountListener) activity;
		} 
		else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet " + this.getClass().getSimpleName() 
					+ ".CreateNewAccountListener");
		}
	}


	/**
	 * Handles the main click.
	 * @param v View
	 */
	@Override
	public void onClick(View v) {
		String username = mUsername.getText().toString().trim();
		String email = mEmail.getText().toString().trim();
		String pw = mPassword.getText().toString().trim();
		String pwRepeat = mPasswordRepeat.getText().toString().trim();
		String creditCard = mCreditCard.getText().toString().trim();
		String securityCode = mSecurityCode.getText().toString().trim();
		String expMo = mExpMo.getText().toString().trim();
		String expYr = mExpYr.getText().toString().trim();
		String zip = mZip.getText().toString().trim();
		mUsername.setText(username);
		mEmail.setText(email);
		mPassword.setText(pw);
		mPasswordRepeat.setText(pwRepeat);
		mListener.submitNewAccount(username, email, pw, pwRepeat, 
				creditCard, securityCode, expMo, expYr, zip);
	}

	/**
	 * Listener for activities to implement.
	 * @author mhotan
	 */
	public interface CreateNewAccountListener {

		/**
		 * user wants to use credentials to create account.
		 * @param username username wanted to use
		 * @param email email to use
		 * @param pw password 
		 * @param pwRepeat repeated password
		 * @param creditCard credit card 
		 * @param securityCode security code 
		 * @param expMo expiration month 
		 * @param expYr expiration year 
		 * @param zip zip code 
		 */
		void submitNewAccount(String username, String email, String pw,
				String pwRepeat, String creditCard, String securityCode, 
				String expMo, String expYr, String zip
			);

	}

}
