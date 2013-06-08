package uw.cse.dineon.user.bill;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.library.image.ImageObtainable;
import uw.cse.dineon.library.image.ImageCache.ImageGetCallback;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * 
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
		
		// Extract the order from the activity using this fragment.
		Order order = mListener.getOrder();
		
		
		double totalPrice = order.getTotalCost();
		if(Math.abs(totalPrice - 0.0) < DineOnConstants.CURRENCY_EPSILON) {
			mPlaceOrderButton.setVisibility(View.GONE);
		} else {
			mPlaceOrderButton.setVisibility(View.VISIBLE);
		}
		
		mSubtotal.setText(mFormatter.format(totalPrice));
		mTax.setText(mFormatter.format(totalPrice * DineOnConstants.TAX));
		mTotal.setText(mFormatter.format(totalPrice * (1 + DineOnConstants.TAX)));
		
		// Attempt to extract argument if this fragment was created with them
		ListView listView = (ListView) getView().findViewById(R.id.list_order);
		listView.setAdapter(mAdapter);
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
		mAdapter = new 
	}
	
	/**
	 * @param newItem to add
	 */
	public void addNewItem(MenuItem newItem) {
		mAdapter.add(newItem);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * @param item MenuItem to remove from this
	 */
	public void removeItem(MenuItem item) {
		mAdapter.remove(item); // Remove the item
		mAdapter.notifyDataSetChanged(); // Notify the data set changed
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
		 * User wishes place current order.
		 * @param order to place
		 */
		public void onPlaceOrder(Order order);

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

		/**
		 * Once an order is placed clear the current order.
		 */
		public void resetCurrentOrder();
	}

	/**
	 * Simple adapter that handles custom list item layout and 
	 * their interaction handlers.
	 * TODO Change layout of item 
	 * @author mhotan
	 */
	private class OrderArrayAdapter extends ArrayAdapter<MenuItem> {

		/**
		 * Owning context.
		 */
		private final Context mContext;

		/**
		 * List of menu items.
		 */
		private final List<MenuItem> mItems;

		private final HashMap<MenuItem, CurrentOrderItem> mOrderMapping;

		/**
		 * This is a runtime mapping between "More Info buttons"
		 * and there respective restaurants.
		 * NOTE (MH): Not exactly sure if this works
		 */
		private final HashMap<View, MenuItem> mViewToItem;

		/**
		 * Mapping to increment and decrement button
		 * to the text view it alters.
		 */
		private final Map<Button, TextView> mButtonToTextView;

		private final OnItemClickListener privateListener;

		/**
		 * Creates an array adapter to display a Order.
		 * @param ctx Context of owning activity
		 * @param order List of menu items to display
		 * @param orderItems Map of MenuItems to their current order.
		 */
		public OrderArrayAdapter(Context ctx, List<MenuItem> order, 
				HashMap<MenuItem, CurrentOrderItem> orderItems) {
			super(ctx, R.layout.listitem_orderitem, order);

			mContext = ctx;
			mItems = new ArrayList<MenuItem>(order);

			mViewToItem = new HashMap<View, MenuItem>();
			mButtonToTextView = new HashMap<Button, TextView>();

			privateListener = new OnItemClickListener();

			mPlaceOrderButton.setOnClickListener(privateListener);
			//mReqButton.setOnClickListener(privateListener);

			this.mOrderMapping = new HashMap<MenuItem, CurrentOrderItem>(orderItems);
		}

		@Override
		public View getView(int position, View covnertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.listitem_orderitem, parent, false);

			// Here is where we adjust the contents of the list row
			// with attributes determined by the order item

			// Get the Order Item by associating with the position
			position = Math.max(0, Math.min(position, this.mItems.size() - 1));
			MenuItem item = mItems.get(position);	

			// Assign the buttons to this order
			final ImageView ITEMIMAGE = (ImageView) 
					rowView.findViewById(R.id.image_order_menu_item);
			Button incButton = (Button) rowView.findViewById(R.id.button_increment_item);
			Button decButton = (Button) rowView.findViewById(R.id.button_decrement_item);
			TextView itemQuantity = (TextView) rowView.findViewById(R.id.label_item_quantity);
			ImageButton deleteButton = (ImageButton) rowView.findViewById(R.id.button_delete);
			TextView label = (TextView) rowView.findViewById(R.id.label_order_item);

			// set the quantity
			int itemCount = this.mOrderMapping.get(item).getQuantity();
			itemQuantity.setText("" + itemCount);

			// set the label and description
			label.setText(item.getTitle());

			// Set the image of this order item
			DineOnImage image = item.getImage();
			if (image != null) {
				mListener.onGetImage(image, new ImageGetCallback() {
					
					@Override
					public void onImageReceived(Exception e, Bitmap b) {
						if (e == null) {
							// We got the image so set the bitmap
							ITEMIMAGE.setImageBitmap(b);
						}
					}
				});
			}
			
			// Place mapping from all the clickable view to the Order item
			mViewToItem.put(incButton, item);
			mViewToItem.put(decButton, item);
			mViewToItem.put(deleteButton, item);
			mViewToItem.put(label, item);
			mViewToItem.put(itemQuantity, item);

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

				MenuItem item = mViewToItem.get(v);
				int toAdd = -1;

				double priceChange = 0.0;
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
					priceChange = 0.0;
					if (newVal - curVal > 0) {
						mAdapter.mOrderMapping.get(item).incrementQuantity();
						priceChange = item.getPrice();
					} else if (newVal - curVal < 0) {
						mAdapter.mOrderMapping.get(item).decrementQuantity();
						priceChange = item.getPrice() * -1;
					}

					mButtonToTextView.get(v).setText("" + newVal);

					break;
				case R.id.button_delete:
					priceChange = mAdapter.mOrderMapping.get(item).getQuantity() 
					* item.getPrice() * -1;
					mListener.onRemoveItemFromOrder(item);
					mAdapter.remove(item);
					mAdapter.mOrderMapping.remove(item);
					mAdapter.mItems.remove(item);
					//mAdapter.mOrderMapping.remove(item);
					// TODO Do something to remove this item
					break;
				case R.id.label_order_item:
					// TODO Add some way to show focus
					break;

				case R.id.button_place_order:
					// check to see if user is currently in a dining session
					DiningSession session = DineOnUserApplication.getCurrentDiningSession();
					if (session != null) {

						List<CurrentOrderItem> items = 
								new ArrayList<CurrentOrderItem>(mAdapter.mOrderMapping.values());
						if(items.size() > 0) {
							// create and save the order
							final Order NEW_ORDER = new Order(session.getTableID(),  
									DineOnUserApplication.getUserInfo(), 
									items);

							// save the new order
							NEW_ORDER.saveInBackGround(new SaveCallback() {

								@Override
								public void done(ParseException e) {
									if (e == null) {
										// successful, send the push notification
										mListener.onPlaceOrder(NEW_ORDER);
										if (mListener instanceof Context) {
											Toast.makeText((Context) mListener, 
													"Your order was placed.", 
													Toast.LENGTH_SHORT).show();
										}
										mListener.doneWithOrder();

									} else {
										Log.d(TAG, "Couldn't save the new order: " 
												+ e.getMessage());
									}
								}
							});
						}


					} else {
						if (mListener instanceof Context) {
							Toast.makeText((Context)mListener, 
									"Must checkin before placing order.", 
									Toast.LENGTH_SHORT).show();
						}
					}

					break;

				default:
					break;
				}
				if (priceChange != 0.0) {
					mSubtotal = (TextView) mListView.findViewById(R.id.value_subtotal);
					mTax = (TextView) mListView.findViewById(R.id.value_tax);
					mTotal = (TextView) mListView.findViewById(R.id.value_total);
					double newTotal = priceChange 
							+ Double.parseDouble(mSubtotal.getText().toString().substring(1));
					mSubtotal.setText(mFormatter.format(newTotal));
					mTax.setText(mFormatter.format((newTotal * DineOnConstants.TAX)));
					mTotal.setText(mFormatter.format((newTotal * (1 + DineOnConstants.TAX))));
				}
			}

		}

	}
}
