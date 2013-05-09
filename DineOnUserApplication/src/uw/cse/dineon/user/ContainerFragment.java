package uw.cse.dineon.user;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Blank fragment for just space filling purposes.
 * @author mhotan
 */
public class ContainerFragment extends DialogFragment {

	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
		  	View view = inflater.inflate(R.layout.fragment_empty,
		  			container, false);
		  	return view;
	    }

}
