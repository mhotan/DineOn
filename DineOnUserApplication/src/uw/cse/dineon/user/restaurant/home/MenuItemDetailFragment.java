package uw.cse.dineon.user.restaurant.home;

import uw.cse.dineon.user.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MenuItemDetailFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_menuitem_detail,
				container, false);
		return view;
	}

	/**
	 * TODO
	 * @param item
	 */
	public void setMenuItem(/*Replace with MenuItem*/String item) {
		TextView view = (TextView) getView().findViewById(R.id.label_menuitem_details);
		view.setText(item);
	}

}