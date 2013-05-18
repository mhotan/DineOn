package uw.cse.dineon.restaurant.active;

import java.util.ArrayList;

import uw.cse.dineon.restaurant.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This Fragment describes the detail of an order.
 * @author mhotan
 */
public class OrderDetailFragment extends Fragment implements OnClickListener {

	private TextView mTitle, mTableInput, mTakenTime;
	private ArrayAdapter<String/*Change to Menu item and quantity*/> mAdapter;
	private EditText mMessageInput;
	private ImageButton mSendMessageButton;

	private OrderDetailListener mListener;
	private String mOrder;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_order_detail,
				container, false);
		mOrder = null;

		mTitle = (TextView) view.findViewById(R.id.label_order_title_detail);
		mTableInput = (TextView) view.findViewById(R.id.label_order_table);
		mTakenTime = (TextView) view.findViewById(R.id.label_order_time);

		mMessageInput = (EditText) view.findViewById(R.id.edittext_message_block);
		mSendMessageButton = (ImageButton) view.findViewById(R.id.button_send_message_fororder);
		mSendMessageButton.setOnClickListener(this);

		mAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, new ArrayList<String>());
		ListView itemList = (ListView) view.findViewById(R.id.list_order);
		itemList.setAdapter(mAdapter);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OrderDetailListener) {
			mListener = (OrderDetailListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet OrderDetailFragment.OrderDetailListener");
		}
	}
	
	/**
	 * Updates the state of the view pending the whether there is a request.
	 */
	private void updateState() {
		if (mOrder == null) {
			mSendMessageButton.setEnabled(false);
		} else {
			mSendMessageButton.setEnabled(true);
		}
	}

	//////////////////////////////////////////////////////
	//// Following are public setters.  That Activities can use
	//// to set the values of what is showed to the user for this 
	//// fragment
	//////////////////////////////////////////////////////

	/**
	 * Sets the current display of this fragment to that of the 
	 * order requested.
	 * @param order order to present
	 */
	public void setOrder(/*TODO Replace with Order class*/ String order) {
		mOrder = order;
		
		// TODO Replace the fake the funk
		
		mTitle.setText("Order: " + order);
		mTableInput.setText("4");
		mTakenTime.setText("7:35pm");
		
		ArrayList<String> items = new ArrayList<String>();
		items.add("Fried Chicken Qty: " + 5);
		items.add("Sushi Qty: " + 1);
		items.add("Coke Qty: " + 2);
		
		// Leave generally as is.
		// Just change String to MenuItem or Convert Menu Item to a String
		mAdapter.clear();
		for (String item: items) {
			mAdapter.add(item);
		}
		mAdapter.notifyDataSetChanged();
		updateState();
	}

	//////////////////////////////////////////////////////
	//// Following is the interface in which activities
	//// that wish to attach this Fragment must implement
	//// Intended to use for user input
	//////////////////////////////////////////////////////

	/**
	 * Any activity who wished to attach OrderDetailFragment
	 * must implement this Listener.
	 * @author mhotan
	 */
	public interface OrderDetailListener {

		/**
		 * Call back that shows that the user wishes to send a message 
		 * to the customer pertaining that specific orders.
		 * @param order Order to associate message to
		 * @param message Message to send for this order
		 */
		public void sendMessage(
				/*TODO Replace with Order class*/String order, String message);
	}

	//////////////////////////////////////////////////////
	//// Following is handler for sending message back to user
	//////////////////////////////////////////////////////

	@Override
	public void onClick(View v) {
		String message = mMessageInput.getText().toString();
		if (message.length() == 0) {
			return;
		}
		// TODO Implement
		mListener.sendMessage("ORDER GOES HERE", message);
	}

}
