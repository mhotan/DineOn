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
	 */
	@Override
	public void onClick(View v) {
		String username = mUsername.getText().toString();
		String email = mEmail.getText().toString();
		String pw = mPassword.getText().toString();
		String pwRepeat = mPasswordRepeat.getText().toString();
		mUsername.setText("");
		mEmail.setText("");
		mPassword.setText("");
		mPasswordRepeat.setText("");
		mListener.submitNewAccount(username, email, pw, pwRepeat);
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
		 */
		void submitNewAccount(
				String username, String email, String pw, String pwRepeat);

	}

}
