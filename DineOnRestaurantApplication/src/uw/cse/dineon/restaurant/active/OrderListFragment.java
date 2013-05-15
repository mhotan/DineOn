package uw.cse.dineon.restaurant.active;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uw.cse.dineon.library.Order;
import uw.cse.dineon.restaurant.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Fragment represents a List of Orders that are pending
 * for this restaurant.
 * @author mhotan
 */
public class OrderListFragment extends ListFragment {

	private static final String TAG = OrderListFragment.class.getSimpleName();

	private OrderItemListener mListener;

	private ArrayAdapter<Order> mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		List<Order> orders = mListener.getCurrentOrders();
		if (orders == null) {
			orders = new ArrayList<Order>();
		}
		mAdapter = new OrderListAdapter(this.getActivity(), orders);
		setListAdapter(mAdapter);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OrderItemListener) {
			mListener = (OrderItemListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement OrderListFragment.OrderItemListener");
		}
	}

	//////////////////////////////////////////////////////
	//// Following are public setters.  That Activities Or parent Fragments can use
	//// to set the values of what is showed to the user for this 
	//// fragment TODO Change string to Order
	//////////////////////////////////////////////////////

	/**
	 * Adds order to this view.
	 * @param order String
	 */
	public void addOrder(Order order) {
		mAdapter.add(order);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Adds all the orders to this view.
	 * @param orders Collection of Strings
	 */
	public void addAll(Collection<Order> orders) {
		for (Order o: orders) {
			mAdapter.add(o);
		}
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Deletes this order if it finds it.
	 * @param order String
	 */
	public void deleteOrder(Order order) {
		mAdapter.remove(order);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Clears all orders.
	 */
	public void clearOrder() {
		mAdapter.clear();
		mAdapter.notifyDataSetChanged();
	}

	//////////////////////////////////////////////////////
	//// Following is the interface in which activities
	//// that wish to attach this Fragment must implement
	//// Intended to use for user input
	//////////////////////////////////////////////////////

	/**
	 * Mandatory interface for this fragment.
	 * @author mhotan
	 */
	public interface OrderItemListener {

		/**
		 * Notifies the progress of this order has changed.
		 * @param order order to reference
		 * @param progress progress of this order
		 */
		void onProgressChanged(Order order, int progress);

		/**
		 * User(Restaurant Employee) wants to see details pertaining this order.
		 * @param order order to reference
		 */
		void onRequestOrderDetail(Order order);

		/**
		 * Restaurant wants to notify customer that the order is complete.
		 * @param order order to reference
		 */
		void onOrderComplete(Order order);

		/**
		 * Used to get the most recent up to date list of items to show.
		 * if returns null then no list will be added
		 * @return List of items to show
		 */
		List<Order> getCurrentOrders();

	}

	//////////////////////////////////////////////////////
	//// Adapter to handle using listitems specific to 
	//// Showing Orders for restaurants
	//////////////////////////////////////////////////////

	/**
	 * Adapter to handle.
	 * @author mhotan
	 */
	private class OrderListAdapter extends ArrayAdapter<Order> {

		private final Context mContext;
		private final List<Order> mOrders;
		private final Map<View, Order> mViewToOrder;
		private final OrderItemListener mItemListener;
		private final OrderProgressListener mProgessListener;
		private int expanded = -1;

		/**
		 * Creates an adapter that manages the addition and layout of
		 * Orders.
		 * @param ctx Context
		 * @param orders List of strings
		 */
		public OrderListAdapter(Context ctx, List<Order> orders) {
			super(ctx, R.layout.listitem_restaurant_order_bot, orders);
			this.mContext = ctx;
			this.mOrders = orders;
			this.mViewToOrder = new HashMap<View, Order>();
			this.mItemListener = new OrderItemListener();
			this.mProgessListener = new OrderProgressListener();
		}

		/**
		 * Sets the arrow based on whether or not the list item
		 * is expanded or not.
		 * @param position The position of the list item to set
		 * @param arrowButton The specified arrow button to set
		 */
		private void setArrow(int position, ImageButton arrowButton) {
			if(position == expanded) {
				arrowButton.setImageResource(R.drawable.navigation_next_item);
			} else {
				arrowButton.setImageResource(R.drawable.navigation_expand);
			}
		}

		/**
		 * Toggles expansion of the given list item.
		 * Currently only one item can be expanded at a time
		 * @param position The position of the list item to toggle
		 */
		public void expand(int position) {

			if(expanded == position) { //Already expanded, collapse it
				expanded = -1;
			} else {
				expanded = position;
			}
			notifyDataSetChanged();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			View vwTop;
			View vwBot;

			final LinearLayout view;
			
			if(convertView == null) {
				view = new LinearLayout(mContext);
				view.setOrientation(LinearLayout.VERTICAL);
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				vwTop = inflater.inflate(R.layout.listitem_restaurant_order_top, null, true);
				vwBot = inflater.inflate(R.layout.listitem_restaurant_order_bot, null, true);
				view.addView(vwTop);
				view.addView(vwBot);
			} else {
				//Everything already created, just find them
				view = (LinearLayout) convertView;
				vwTop = view.findViewById(R.id.listitem_order_top);
				vwBot = view.findViewById(R.id.listitem_order_bot);
			}

			Order order = mOrders.get(position);

			// Reference the correct UI components and set up
			Button buttonCompleteOrder = (Button) view.findViewById(R.id.button_completed_order);
			TextView orderTitle = 
					(TextView) view.findViewById(R.id.button_order_title);
			int table = order.getTableID();
			
			if(table == -1) { // No Table
				orderTitle.setText(order.getOriginalUser().getName());
			} else {
				orderTitle.setText("Table " + order.getTableID() 
						+ " - " + order.getOriginalUser().getName());
			}
			
			SeekBar progressBar = (SeekBar) view.findViewById(R.id.seekbar_order_progress);
			progressBar.setMax(100);
			progressBar.setProgress(0);

			TextView time = (TextView) view.findViewById(R.id.label_order_time);
			time.setText(order.getOriginatingTime().toString());
			
			//Set up expand button
			ImageButton arrowButton = (ImageButton) vwTop.findViewById(R.id.button_expand_order);
			setArrow(position, arrowButton);

			// Add the onclick listener that listens 
			arrowButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					expand(position);

					ImageButton arrowButton = 
							(ImageButton) view.findViewById(R.id.button_expand_order);
					setArrow(position, arrowButton);
					//Right arrow case -- goes to details fragment
					if(expanded != position) {
						mListener.onRequestOrderDetail(mOrders.get(position));
					}				
				}
			});

			if(expanded != position) {
				vwBot.setVisibility(View.GONE);
			} else {
				vwBot.setVisibility(View.VISIBLE);
			}

			// Add to mapping to reference later
			mViewToOrder.put(buttonCompleteOrder, order);
			mViewToOrder.put(arrowButton, order);
			mViewToOrder.put(progressBar, order);

			// Add listener for reaction purposes
			buttonCompleteOrder.setOnClickListener(mItemListener);
			progressBar.setOnSeekBarChangeListener(mProgessListener);
			return view;
		}

		/**
		 * Listener for certain item of an order item view.
		 * @author mhotan
		 */
		private class OrderItemListener implements View.OnClickListener {

			@Override
			public void onClick(View v) {

				Order order = mViewToOrder.get(v);

				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.button_completed_order:
					mAdapter.remove(order);
					mListener.onOrderComplete(order);
					mAdapter.notifyDataSetChanged();
					break;
				default:
					break;
				}
			}
		}

		/**
		 * Listener for certain seekbar change regarding progress.
		 * @author mhotan
		 */
		private class OrderProgressListener implements SeekBar.OnSeekBarChangeListener {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				Order order = mViewToOrder.get(seekBar);
				mListener.onProgressChanged(order, progress);

				if (progress == seekBar.getMax()) {
					mAdapter.remove(order);
					mListener.onOrderComplete(order);
					mAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }

		}


	}
}
