package uw.cse.dineon.restaurant.active;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.restaurant.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

	private static final String TAG = OrderDetailFragment.class.getSimpleName();

	private TextView mTitle, mTableInput, mTakenTime;
	private MenuItemAdapter mAdapter;
	private EditText mMessageInput;
	private ImageButton mSendMessageButton;
	private ListView mItemList;

	private OrderDetailListener mListener;
	
	private static String idLabel, qtyLabel;
	
	/**
	 * Obtain the reference to the current order.
	 */
	private Order mOrder;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mOrder = mListener.getOrder();
		idLabel = getString(R.string.id_label);
		qtyLabel = getString(R.string.qty_short);
		// Get the order and produce the display
		updateState();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Use view determined by this layout field.
		View view = inflater.inflate(R.layout.fragment_order_detail,
				container, false);

		// Extract the references to title fields
		mTitle = (TextView) view.findViewById(R.id.label_order_title_detail);
		mTableInput = (TextView) view.findViewById(R.id.label_order_table);
		mTakenTime = (TextView) view.findViewById(R.id.label_order_time);

		// Extract the input areas
		mMessageInput = (EditText) view.findViewById(R.id.edittext_message_block);
		mSendMessageButton = (ImageButton) view.findViewById(R.id.button_send_message_fororder);
		mSendMessageButton.setOnClickListener(this);
		mItemList = (ListView) view.findViewById(R.id.list_order);
		

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
			mSendMessageButton.setVisibility(View.GONE);
		} else {
			// Here we have an order so lets update the state
			mSendMessageButton.setVisibility(View.VISIBLE);
			mSendMessageButton.setEnabled(true);
			mTitle.setText(getString(R.string.order_placed_by) + mOrder.getOriginalUser().getName());
			mTableInput.setText(getString(R.string.table_label) + mOrder.getTableID());
			mTakenTime.setText(mOrder.getOriginatingTime().toString());
			mAdapter = new MenuItemAdapter(this.getActivity(), mOrder.getMenuItems());
			mItemList.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
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
	public void setOrder(Order order) {
		if (order == null) {
			Log.w(TAG, "Setting Order to null");
			return;
		}

		mOrder = order;
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
		 * @param user User to send message to
		 * @param message Message to send for this order
		 */
		public void sendShoutOut(UserInfo user, String message);
		
		/**
		 * Asks the containing activity for a reference to the order.
		 * The order 
		 * @return the current order to present.
		 */
		public Order getOrder();
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
		mListener.sendShoutOut(mOrder.getOriginalUser(), message);
	}

	/**
	 * View the relevant information of all the menu items.
	 * @author mhotan
	 */
	@SuppressWarnings("SIC_INNER_SHOULD_BE_STATIC")
	private static class MenuItemAdapter extends ArrayAdapter<CurrentOrderItem> {

		private final Context mContext;
		
		/**
		 * Creates an adapter for displaying menu items.
		 * @param context Owning context of this adapter
		 * @param items Items to add to the List view
		 */
		public MenuItemAdapter(Context context, List<CurrentOrderItem> items) {
			super(context, R.layout.listitem_menuitem_editable, items);
			mContext = context;			
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.listitem_menuitem_editable, null, true);

			CurrentOrderItem toShow = super.getItem(position);
			int qty = toShow.getQuantity();

			//Commented out for findbugs
			//			ImageView menuItemImage = (ImageView) 
			//					view.findViewById(R.id.image_thumbnail_menuitem);
			TextView menuItemTitle = (TextView) view.findViewById(R.id.label_menuitem_title);
			TextView menuItemDesc = (TextView) view.findViewById(R.id.label_menuitem_desc);
			TextView menuItemPrice = (TextView) view.findViewById(R.id.label_menuitem_price);
			ImageButton deleteButton = (ImageButton) view.findViewById(R.id.button_menuitem_delete);
			
			// Remove the delete button from restaurant context
			deleteButton.setEnabled(false);
			deleteButton.setVisibility(View.GONE);

			// TODO Download the Image Asyncronously

			menuItemTitle.setText(toShow.getMenuItem().getTitle());
			menuItemDesc.setText(idLabel + toShow.getMenuItem().getProductID());
			menuItemPrice.setText(qtyLabel + qty);

			return view;
		}

		@Override
		public int getCount() {
			return super.getCount();
		}
		
	}

	
	
}
