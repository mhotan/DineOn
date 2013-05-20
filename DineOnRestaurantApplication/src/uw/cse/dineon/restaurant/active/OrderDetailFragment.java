package uw.cse.dineon.restaurant.active;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.Utility;
import uw.cse.dineon.restaurant.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
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

	private static final String ORDER = TAG + "_order";

	private TextView mTitle, mTableInput, mTakenTime;
	private MenuItemAdapter mAdapter;
	private EditText mMessageInput;
	private ImageButton mSendMessageButton;
	private ListView mItemList;

	private OrderDetailListener mListener;
	private Order mOrder;

	/**
	 * Creates a dining session that is ready to rock.
	 * No need to call setOrder
	 * @param order Order to use
	 * @return Fragment to use.
	 */
	public static OrderDetailFragment newInstance(Order order) {
		Bundle args = new Bundle();
		args.putParcelable(ORDER, order);
		OrderDetailFragment frag = new OrderDetailFragment();
		frag.setArguments(args);
		return frag;
	}

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
		mItemList = (ListView) view.findViewById(R.id.list_order);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (mOrder != null) {
			return;
		}

		Bundle args = getArguments();
		if (savedInstanceState != null) {
			setOrder((Order)savedInstanceState.getParcelable(ORDER));
		} else if (args != null && args.containsKey(ORDER)) {
			setOrder((Order) args.getParcelable(ORDER));
		}
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(ORDER, mOrder);
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
			mTitle.setText("Order placed by: " + mOrder.getOriginalUser().getName());
			mTableInput.setText("" + mOrder.getTableID());
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
	private static class MenuItemAdapter extends ArrayAdapter<MenuItem> {

		private final Context mContext;

		/**
		 * List of Menu Items with their associated quantity.
		 */
		private final List<Pair<MenuItem, Integer>> mItems;

		/**
		 * Creates an adapter for displaying menu items.
		 * @param context Owning context of this adapter
		 * @param items Items to add to the List view
		 */
		public MenuItemAdapter(Context context, List<MenuItem> items) {
			super(context, R.layout.listitem_menuitem_editable);
			mContext = context;

			// Check for stupidity
			Map<MenuItem, Integer> occurences = Utility.toQuantityMap(items);

			mItems = new ArrayList<Pair<MenuItem, Integer>>(occurences.keySet().size());
			for (Map.Entry<MenuItem, Integer> entry : occurences.entrySet()) {
				mItems.add(new Pair<MenuItem, Integer>(entry.getKey(), entry.getValue()));
			}
		}

		/**
		 * Use for restoring state. No copies are made.
		 * Just a new lsit is returned
		 * @return a list of the current menu items
		 */
		ArrayList<Pair<MenuItem, Integer>> getCurrentItems() {
			return new ArrayList<Pair<MenuItem, Integer>>(mItems);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.listitem_menuitem_editable, null, true);

			MenuItem toShow = mItems.get(position).first;
			int qty = mItems.get(position).second;

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

			menuItemTitle.setText(toShow.getTitle());
			menuItemDesc.setText("ID: " + toShow.getProductID());
			menuItemPrice.setText("QTY: " + qty);

			return view;
		}

	}

}
