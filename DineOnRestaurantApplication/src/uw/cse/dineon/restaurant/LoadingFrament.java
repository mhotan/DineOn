package uw.cse.dineon.restaurant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Simple fragment that shows an indeterminant loading bar.
 * @author mhotan
 */
public class LoadingFrament extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_loading,
				container, false);
		// TODO Possibly fill with more detail
		return view;
	}
	
}
