package uw.cse.dineon.restaurant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Simple view that shows user that they are not logged in.
 * provides the option to go to login screen
 * @author mhotan
 */
public class NotLoggedInFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_not_logged_in,
				container, false);
		// TODO Possibly fill with more detail
		return view;
	}
	
}
