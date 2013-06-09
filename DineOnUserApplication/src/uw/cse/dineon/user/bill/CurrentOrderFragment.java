package uw.cse.dineon.user.bill;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.image.ImageGetCallback;
import uw.cse.dineon.library.image.ImageObtainable;
import uw.cse.dineon.user.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Fragment that shows the state of the current Order.
 * @author mhotan
 */
public class CurrentOrderFragment extends Fragment {

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

	private NumberFormat mFormatter;

	/**
	 * Activity which serves as a Listener. 
	 */
	private OrderUpdateListener mListener;

	private TextView mSubtotal, mTax, mTotal;
	private Button mPlaceOrderButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Instantiate variable that are not view dependent
		// but instead are fragment instance dependent.
		this.mFormatter = NumberFormat.getCurrencyInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_view_order,
				container, false);

		mSubtotal = (TextView) view.findViewById(R.id.value_subtotal);
		mTax = (TextView) view.findViewById(R.id.value_tax);
		mTotal = (TextView) view.findViewById(R.id.value_total);
		mPlaceOrderButton = (Button) view.findViewById(R.id.button_place_order);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OrderUpdateListener) {
			mListener = (OrderUpdateListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement CurrentOrderFragment.OrderUpdateListener");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		updateOrder();
	}

	/**
	 * This call is explicitly notify the fragment
	 * to request to get the latest copy of the order
	 * to repopulate the screen.
	 */
	public void updateOrder() {
		Order order = mListener.getOrder();

		// If the total cost of the order is near 0.0 there is no way to pl
		// If there is no menu items in the 
		if (order.isEmpty()) {
			mPlaceOrderButton.setVisibility(View.GONE);
		} else {
			mPlaceOrderButton.setVisibility(View.VISIBLE);
		}

		mSubtotal.setText(mFormatter.format(order.getSubTotalCost()));
		mTax.setText(mFormatter.format(order.getTaxCost()));
		mTotal.setText(mFormatter.format(order.getTotalCost()));

		// TODO Create a set the adapter
		if (mAdapter != null) {
			mAdapter.notifyDataSetInvalidated();
		}
		mAdapter = new OrderArrayAdapter(getActivity(), order);
		// Attempt to extract argument if this fragment was created with them
		ListView listView = (ListView) getView().findViewById(R.id.list_order);
		listView.setAdapter(mAdapter);
	}

	/**
	 * @param item MenuItem to remove from this
	 */
	public void removeItem(MenuItem item) {
		mAdapter.removeMenuItem(item);
	}

	//////////////////////////////////////////////////////////////////////////
	////	Testing helper methods. Such BS that we had to be able to test
	//////////////////////////////////////////////////////////////////////////

	/**
	 * Return the subtotal for current order.
	 * @return subtotal
	 */
	public String getSubtotal() {
		return mSubtotal.getText().toString();
	}

	/**
	 * Return the tax for order.
	 * @return tax
	 */
	public String getTax() {
		return mTax.getText().toString();
	}

	/**
	 * Return the total for the order.
	 * @return total
	 */
	public String getTotal() {
		return mTotal.getText().toString();
	}

	/**
	 * Listener associated with this containing fragment.
	 * <b>This allows any containing activity to receive
	 * messages from this interface's Fragment.</b>
	 * TODO Add modify as see fit to communicate back to activity
	 * @author mhotan
	 */
	public interface OrderUpdateListener extends ImageObtainable {

		/**
		 * User wants to commit to the current pending order.
		 * After this the activity will attempt to save the current
		 * state of the order. Then push the request to the restaurant.
		 * The restaurant will then decide what to do with the order.
		 */
		public void onCommitPendingOrder();

		/**
		 * User wishes to increment the quantity of a particular item on their order.
		 * TODO Enforce assertion that item is actually placed in that order
		 * @param item Menu item to increment
		 */
		public void onIncrementItemOrder(MenuItem item);

		/**
		 * User wishes to decrement the quantity of a particular item on their order.
		 * TODO Enforce assertion that item is actually placed in that order
		 * @param item Menu item to decrement
		 */
		public void onDecrementItemOrder(MenuItem item);

		/**
		 * User wishes to remove a particular item on their order.
		 * TODO Enforce assertion that item is actually placed in that order
		 * @param item Menu item to remove
		 */
		public void onRemoveItemFromOrder(MenuItem item);

		/**
		 * Get the order to show in this fragment.
		 * @return hash map of items
		 */
		public Order getOrder();
	}

	/**
	 * Simple adapter that handles custom list item layout and 
	 * their interaction handlers.
	 * TODO Change layout of item 
	 * @author mhotan
	 */
	private class OrderArrayAdapter extends ArrayAdapter<CurrentOrderItem> {

		/**
		 * Owning context.
		 */
		private final Context mContext;

		/**
		 * This map is used for when the client code needs to alter the state of the adapter.
		 * The Adapter Knows about the state of the CurrentOrderItems
		 */
		private final Map<MenuItem, CurrentOrderItem> mItemMap;

		/**
		 * Creates an array adapter to display a Order.
		 * @param ctx Context of owning activity
		 * @param order Order to handle in the adapter
		 */
		public OrderArrayAdapter(Context ctx, Order order) {
			super(ctx, R.layout.listitem_orderitem, order.getMenuItems());
			mContext = ctx;
			mItemMap = new HashMap<MenuItem, CurrentOrderItem>();
		}

		@Override
		public View getView(int position, View covnertView, ViewGroup parent) {
			// Get the view to show the Items
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.listitem_orderitem, parent, false);

			// Here is where we adjust the contents of the list row
			// with attributes determined by the order item

			CurrentOrderItem item = super.getItem(position);	
			new OrderItemHandler(item.getMenuItem(), item.getQuantity(), rowView);

			mItemMap.put(item.getMenuItem(), item);
			return rowView;
		}

		/**
		 * Remove menu item from the adapter.
		 * @param item item to remove.
		 */
		void removeMenuItem(MenuItem item) {
			CurrentOrderItem orderItem = mItemMap.get(item);
			if (orderItem == null) { // If we dont have the 
				return;
			}

			// remove it from the map
			mItemMap.remove(item);
			// Remove Current order item from the adapters
			super.remove(orderItem);
			// Notify change in data set
			super.notifyDataSetChanged();
		}
	}

	/**
	 * This handlers all the input that is done for a specific view 
	 * in the list of all current order items in the order.
	 * @author mhotan
	 */
	private class OrderItemHandler implements View.OnClickListener {

		/**
		 * The Menu item in the order that this view has to track.
		 */
		private final MenuItem mItem;

		/**
		 * Context of the containing application.
		 */
		private final Context mContext;

		/**
		 * Current quantity tracked for the internal menu item. 
		 */
		private int mQuantity;

		// UI Components to maintain
		private final Button mIncrementButton, mDecrementButton;
		private final ImageButton mDeleteButton;
		private final ImageView mItemImageView;
		private final TextView mQuantityView;

		/**
		 * Creates a handler to handle input for a specific menu item.
		 * @param item Item to show
		 * @param qty Quantity to show for the item.
		 * @param rowView View that will present the order item
		 */
		OrderItemHandler(MenuItem item, int qty, View rowView) {
			mContext = getActivity();
			mItem = item;
			mQuantity = qty;

			TextView itemTitle = (TextView) rowView.findViewById(R.id.label_order_item);
			mItemImageView = (ImageView) rowView.findViewById(R.id.image_order_menu_item);
			mQuantityView = (TextView) rowView.findViewById(R.id.label_item_quantity);

			mIncrementButton = (Button) rowView.findViewById(R.id.button_increment_item);
			mDecrementButton = (Button) rowView.findViewById(R.id.button_decrement_item);
			mDeleteButton = (ImageButton) rowView.findViewById(R.id.button_delete);

			// Set the title appropiately
			itemTitle.setText(mItem.getTitle());
			mQuantityView.setText(String.valueOf(mQuantity));
			if (mItem.getImage() != null) {
				mListener.onGetImage(mItem.getImage(), new OrderImageGetCallback(mItemImageView));
			} else {
				// TODO make sure this works
				// mItemImageView.setVisibility(View.GONE);
			}

			mIncrementButton.setOnClickListener(this);
			mDecrementButton.setOnClickListener(this);
			mDeleteButton.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (v == mIncrementButton) {
				// Handle the incrementation of the order
				// If the listener allowed us to increment the value
				// then update our state.
				mListener.onIncrementItemOrder(mItem);

			} else if (v == mDecrementButton) {
				// Handle the incrementation of the order
				// If the listener allowed us to increment the value
				// then update our state.
				mListener.onDecrementItemOrder(mItem);
			} else if (v == mDeleteButton) {
				// Show prompt to confirm menu item deletion.
				mListener.onRemoveItemFromOrder(mItem);
			}

			Order order = mListener.getOrder();
			int qty = order.getQuantity(mItem);
			// if the quantity is 0 then get prompt user to see if they want
			// to delete this item from the current order.
			mQuantityView.setText(String.valueOf(qty));
		}	
	}



	/**
	 * Fragment get callback to poplate the view with the image of the menu item.
	 * @author mhotan
	 */
	private class OrderImageGetCallback implements ImageGetCallback {

		private final ImageView mContainer;

		/**
		 * Creates a callback that will populate the argument view
		 * only if a correct Bitmap is retrieved.
		 * @param view View to populate
		 */
		public OrderImageGetCallback(ImageView view) {
			if (view == null) {
				throw new IllegalArgumentException("Null view for Image get callback not allowed");
			}
			mContainer = view;
		}

		@Override
		public void onImageReceived(Exception e, Bitmap b) {
			if (e == null) {
				mContainer.setImageBitmap(b);
				return;
			}
			// Error occured getting the image.
			// Fail gracefully and remove the image view.
			mContainer.setVisibility(View.GONE);
		}


	}

}
