package uw.cse.dineon.user.checkin;

import uw.cse.dineon.user.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * @author mhotan
 * TODO
 */
public class CheckInFragment extends Fragment {

	private final String TAG = this.getClass().getSimpleName();

	/**
	 * 
	 */
	private CheckInListener mListener;
	
	private Button mQRButton;
	private Spinner mRestaurantList;
	private EditText mRestaurantCode;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_checkin,
				container, false);
		
		CheckInClickListener l = new CheckInClickListener();
		mQRButton = (Button) view.findViewById(R.id.button_qr_code);
		//mValidateCheckInButton = (Button) view.findViewById(R.id.button_checkin_validate);
		mQRButton.setOnClickListener(l);
		//mValidateCheckInButton.setOnClickListener(l);
		
		String[] debugRestaurants = {"Marty's", "Sponge Bob's squishy eats", "HUB"};
		// TODO grab the closest restaurants
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, debugRestaurants);
		
		mRestaurantList = (Spinner) view.findViewById(R.id.spinner_restaurants);
		mRestaurantList.setAdapter(adapter);
		mRestaurantList.setOnItemSelectedListener(l);
		
		//mRestaurantCode = (EditText) view.findViewById(R.id.input_restaurant_code);
		
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof CheckInListener) {
			mListener = (CheckInListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet CheckInFragment.CheckInListener");
		}
	}

	/**
	 * @author mhotan
	 * 
	 */
	public interface CheckInListener {

		/**
		 * 
		 */
		public void onCheckInSuccess();

		/**
		 * 
		 */
		public void onCheckInFail();

	}
	
	/**
	 * 
	 * @author mhotan
	 */
	private class CheckInClickListener implements View.OnClickListener,
	OnItemSelectedListener {

		private String mRestaurant;
		
		/**
		 * Default constructor.
		 */
		public CheckInClickListener() {
			mRestaurant = null;
		}
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_qr_code :
				
				mListener.onCheckInSuccess();
				
				break;
			/*case R.id.button_checkin_validate:
				// TODO Handle Manual input
				mListener.onCheckInSuccess();
				break;
			*/
			default:
				break;
			}
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			mRestaurant = parent.getItemAtPosition(pos).toString();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	}
}
