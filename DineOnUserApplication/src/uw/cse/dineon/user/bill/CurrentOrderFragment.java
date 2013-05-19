package uw.cse.dineon.user.bill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.user.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 
 * @author mhotan
 */
public class CurrentOrderFragment 
extends Fragment implements OnClickListener {

	private static final String TAG = CurrentBillFragment.class.getSimpleName();

	/**
	 * an argument that can be used to pass this bundle explicit.
	 * order as a list of Strings that currently represent Menu items
	 */
	public static final String ARGUMENT_ORDER = "Order";

	/**
	 * Current adapter for holding values to store on our list.
	 */
	private OrderArrayAdapter mAdapter;

	/**
	 * Activity which serves as a Listener. 
	 */
	private OrderUpdateListener mListener;

	private TextView mSubtotal, mTax, mTotal;
	private Button mReqButton;
	private Button mPlaceOrderButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_view_order,
				container, false);

		// Attempt to extract argument if this fragment was created with them
		List<String> order;
		if (getArguments() != null && (order = getArguments().getStringArrayList(ARGUMENT_ORDER)) 
				!= null) {
			order = getArguments().getStringArrayList(ARGUMENT_ORDER);
		} else {
			order = new ArrayList<String>();
		}

			ListView listview = (ListView)view.findViewById(R.id.list_order);

		// Create the adapter to handles 
		mAdapter = new OrderArrayAdapter(this.getActivity(), order);
		listview.setAdapter(mAdapter);

		mSubtotal = (TextView) view.findViewById(R.id.value_subtotal);
		mTax = (TextView) view.findViewById(R.id.value_tax);
		mTotal = (TextView) view.findViewById(R.id.value_total);
		mPlaceOrderButton = (Button) view.findViewById(R.id.button_place_order);
		mPlaceOrderButton.setOnClickListener(this);
		mReqButton = (Button) view.findViewById(R.id.button_request);
		mReqButton.setOnClickListener(this);

		// TODO replace hard code value with value from order
		mSubtotal.setText("1,000,000.00");
		mTax.setText("100,000.00");
		mTotal.setText("1,100,000.00");

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OrderUpdateListener) {
			mListener = (OrderUpdateListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet CurrentOrderFragment.OrderUpdateListener");
		}
	}

	/**
	 * @param newItem TODO Replace with OrderItem
	 */
	public void addNewItem(String newItem) {
		mAdapter.add(newItem);

	}

	/**
	 * @param item MenuItem to remove from this
	 */
	public void removeItem(String item) {
		mAdapter.remove(item); // Remove the item
		mAdapter.notifyDataSetChanged(); // Notify the data set changed
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.button_request)
			getRequestDescription();
		else
			mListener.onPlaceOrder("ORDER REQUESTED PLACE OBJECT HERE!");
	}
	
	private void sendRequest(String str){
		if(getActivity() instanceof CurrentOrderActivity){
			CurrentOrderActivity act = (CurrentOrderActivity)getActivity();
			Log.v(TAG, "About to send Req");
			act.onRequestMade(str);
		}
	}
	
	private void getRequestDescription() {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("Add New Menu Item");
		alert.setMessage("Input Menu Item Details");
		final View alertView = getLayoutInflater(getArguments()).inflate(
				R.layout.alert_build_request, null);
		alert.setView(alertView);
		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface d, int arg1) {
				String desc = ((EditText) alertView
						.findViewById(R.id.input_request_description)).getText()
						.toString();
				sendRequest(desc);
			}
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//Do nothing
			}
		});
		alert.show();
	}

	
	/**
	 * Listener associated with this containing fragment.
	 * <b>This allows any containing activity to receive
	 * messages from this interface's Fragment.</b>
	 * TODO Add modify as see fit to communicate back to activity
	 * @author mhotan
	 */
	public interface OrderUpdateListener {

		/**
		 * User wishes place current order.
		 * @param order to place
		 */
		public void onPlaceOrder(/*Replace with order class*/String order);

		/**
		 * User wishes to increment the quantity of a particular item on their order.
		 * TODO Enforce assertion that item is actually placed in that order
		 * @param item Menu item to increment
		 * @param order to increment item on
		 */
		public void onIncrementItemOrder(/*Replace with item order class*/String item, 
				String order);

		/**
		 * User wishes to decrement the quantity of a particular item on their order.
		 * TODO Enforce assertion that item is actually placed in that order
		 * @param item Menu item to decrement
		 * @param order to increment item on
		 */
		public void onDecrementItemOrder(/*Replace with item order class*/String item, 
				String order);

		/**
		 * User wishes to remove a particular item on their order.
		 * TODO Enforce assertion that item is actually placed in that order
		 * @param item Menu item to remove
		 * @param order to have the item removed from
		 */
		public void onRemoveItemFromOrder(/*Replace with item order class*/String item, 
				String order);

	}

	/**
	 * Simple adapter that handles custom list item layout and 
	 * their interaction handlers
	 * TODO Change Type String to Order.
	 * TODO Change layout of item 
	 * @author mhotan
	 */
	private class OrderArrayAdapter extends ArrayAdapter<String> {

		/**
		 * Owning context.
		 */
		private final Context mContext;

		/**
		 * List of menu items.
		 * TODO Change String to MenuItem
		 */
		private final List<String> mItems;

		/**
		 * This is a runtime mapping between "More Info buttons"
		 * and there respective restaurants.
		 * TODO Change String to restaurant;
		 * NOTE (MH): Not exactly sure if this works
		 */
		private final HashMap<View, String> mViewToItem;

		/**
		 * Mapping to increment and decrement button
		 * to the text view it alters.
		 */
		private final Map<Button, TextView> mButtonToTextView;

		private final OnItemClickListener privateListener;

		/**
		 * Creates an array adapter to display a Order.
		 * @param ctx Context of owning activity
		 * @param order List of menu items to display TODO Change type String to MenuItem
		 */
		public OrderArrayAdapter(Context ctx, List<String> order) {
			super(ctx, R.layout.listitem_orderitem, order);

			mContext = ctx;
			mItems = new ArrayList<String>(order);

			mViewToItem = new HashMap<View, String>();
			mButtonToTextView = new HashMap<Button, TextView>();

			privateListener = new OnItemClickListener();
		}

		@Override
		public View getView(int position, View covnertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.listitem_orderitem, parent, false);

			// TODO Here is where we adjust the contents of the list row
			// with attributes determined by the order item

			// Get the Order Item by associating with the position
			/*TODO Change to Order item or menu item*/
			String itemName = mItems.get(position);	

			// TODO Set the current count of the item order to 
			// to what it was set to by the user
			// Right now its just a place holder
			int itemCount = 1; //TODO Fix appropiately

			// Assign the buttons to this order
			Button incButton = (Button) rowView.findViewById(R.id.button_increment_item);
			Button decButton = (Button) rowView.findViewById(R.id.button_decrement_item);
			TextView itemQuantity = (TextView) rowView.findViewById(R.id.label_item_quantity);
			ImageButton deleteButton = (ImageButton) rowView.findViewById(R.id.button_delete);
			TextView label = (TextView) rowView.findViewById(R.id.label_order_item);

			itemQuantity.setText("" + itemCount);

			// Place mapping from all the clickable view to the Order item
			mViewToItem.put(incButton, itemName);
			mViewToItem.put(decButton, itemName);
			mViewToItem.put(deleteButton, itemName);
			mViewToItem.put(label, itemName);

			// Place a mapping from the increment 
			// and decrement button to the Text View representing the
			// item quantity
			mButtonToTextView.put(incButton, itemQuantity);
			mButtonToTextView.put(decButton, itemQuantity);

			// Set the listeners for the buttons that alter the order state
			incButton.setOnClickListener(privateListener);
			decButton.setOnClickListener(privateListener);
			deleteButton.setOnClickListener(privateListener);
			// Set the listener when the user selects the item in the current order
			label.setOnClickListener(privateListener);

			return rowView;
		}

		/**
		 * Private click listener for this list items.
		 * TODO Can also be used by having a this object hold references for 
		 * all the Item
		 * @author mhotan
		 */
		private class OnItemClickListener implements View.OnClickListener {

			@Override
			public void onClick(View v) {

				String item = mViewToItem.get(v);
				int toAdd = -1;

				switch (v.getId()) {
				case R.id.button_increment_item:
					toAdd = 1;
				case R.id.button_decrement_item:
					TextView curValStr = mButtonToTextView.get(v);
					assert (curValStr != null);

					// Obtain the potential new Value
					Integer curVal = Integer.parseInt(curValStr.getText().toString());
					int newVal = Math.max(0, curVal + toAdd); // Can't have negative amounts

					// Notify the listener appropiately
					if (newVal - curVal > 0) {
						mListener.onIncrementItemOrder(item, "Order goes here");
					} else if (newVal - curVal < 0) {
						mListener.onDecrementItemOrder(item, "Order goes here");
					}

					mButtonToTextView.get(v).setText("" + newVal);

					break;
				case R.id.button_delete:
					mListener.onRemoveItemFromOrder(item, "Order goes here");
					// TODO Do something to remove this item
					break;
				case R.id.label_order_item:
					// TODO Add some way to show focus
					break;
				default:
					break;
				}
			}

		}

	}
}
