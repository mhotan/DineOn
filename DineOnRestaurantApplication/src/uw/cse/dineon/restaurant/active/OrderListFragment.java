package uw.cse.dineon.restaurant.active;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Fragment represents a List of Orders that are pending
 * for this restaurant
 * @author mhotan
 */
public class OrderListFragment extends ListFragment {

	private final String TAG = this.getClass().getSimpleName();

	private OrderItemListener mListener;

	//TODO change string to order
	private ArrayAdapter<String> mAdapter;


	private static final String KEY_LIST = "MY LIST";

	/**
	 * Creates a new customer list fragment.
	 * @param orders TODO Change to order class
	 * @return new fragment
	 */
	public static OrderListFragment newInstance(List<String> orders) {
		OrderListFragment frag = new OrderListFragment();
		ArrayList<String> mList = new ArrayList<String>();
		if (orders != null) {
			mList.addAll(orders);
		}

		Bundle args = new Bundle();
		args.putStringArrayList(KEY_LIST, mList);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<String> mOrders = getArguments() != null ? 
				getArguments().getStringArrayList(KEY_LIST) : null;
		if (mOrders == null){
			if (mListener != null)
				mOrders = mListener.getCurrentOrders();
			else
				mOrders = new ArrayList<String>(); // Empty
		}

		//TODO Create custom adapter to handle custom layoutss
		mAdapter = new OrderListAdapter(this.getActivity(), mOrders);
		setListAdapter(mAdapter);	
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OrderItemListener) {
			mListener = (OrderItemListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet OrderListFragment.OrderItemListener");
		}
	}

	//////////////////////////////////////////////////////
	//// Following are public setters.  That Activities Or parent Fragments can use
	//// to set the values of what is showed to the user for this 
	//// fragment TODO Change string to Order
	//////////////////////////////////////////////////////

	/**
	 * Adds order to this view
	 * @param order
	 */
	public void addOrder(String order){
		mAdapter.add(order);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Adds all the orders to this view
	 * @param orders
	 */
	public void addAll(Collection<String> orders){
		for (String o: orders)
			mAdapter.add(o);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Deletes this order if it finds it
	 * @param order
	 */
	public void deleteOrder(String order){
		mAdapter.remove(order);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Clears all orders
	 */
	public void clearOrder(){
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
		void onProgressChanged(String order, int progress);

		/**
		 * User(Restaurant Employee) wants to see details pertaining this order.
		 * @param order order to reference
		 */
		void onRequestOrderDetail(String order);

		/**
		 * Restaurant wants to notify customer that the order is complete.
		 * @param order order to reference
		 */
		void onOrderComplete(String order);

		/**
		 * Used to get the most recent up to date list of items to show.
		 * Cannot return null
		 * @return List of items to show
		 */
		List<String> getCurrentOrders();

	}

	//////////////////////////////////////////////////////
	//// Adapter to handle using listitems specific to 
	//// Showing Orders for restaurants
	//////////////////////////////////////////////////////

	/**
	 * Adapter to handle
	 * @author mhotan
	 */
	private class OrderListAdapter extends ArrayAdapter<String> {

		private final Context mContext;
		private final List<String> mOrders;
		private final Map<View, String> mViewToOrder;
		private final OrderItemListener mItemListener;
		private final OrderProgressListener mProgessListener;

		/**
		 * Creates an adapter that manages the addition and layout of
		 * Orders
		 * @param ctx
		 * @param items
		 */
		public OrderListAdapter(Context ctx, List<String> orders){
			super(ctx, R.layout.listitem_restaurant_order, orders);
			this.mContext = ctx;
			this.mOrders = orders;
			this.mViewToOrder = new HashMap<View, String>();
			this.mItemListener = new OrderItemListener();
			this.mProgessListener = new OrderProgressListener();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.listitem_restaurant_order, parent, false);

			//TODO Obtain the order at the position
			String order = mOrders.get(position);

			TextView title = (TextView) view.findViewById(R.id.label_order_title);
			title.setText(order);

			Button buttonCompleteOrder = (Button) view.findViewById(R.id.button_completed_order);
			ImageButton buttonGetDetails = (ImageButton) view.findViewById(R.id.button_order_detail);
			SeekBar progressBar = (SeekBar)view.findViewById(R.id.seekbar_order_progress);
			progressBar.setMax(100);
			progressBar.setProgress(0);

			// Add to mapping to reference later
			mViewToOrder.put(buttonCompleteOrder, order);
			mViewToOrder.put(buttonGetDetails, order);
			mViewToOrder.put(progressBar, order);

			// Add listener for reaction purposes
			buttonCompleteOrder.setOnClickListener(mItemListener);
			buttonGetDetails.setOnClickListener(mItemListener);
			progressBar.setOnSeekBarChangeListener(mProgessListener);
			return view;
		}

		/**
		 * Listener for certain item of an order item view
		 * @author mhotan
		 */
		private class OrderItemListener implements View.OnClickListener {

			@Override
			public void onClick(View v) {

				String order = mViewToOrder.get(v);

				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.button_completed_order:
					mAdapter.remove(order);
					mListener.onOrderComplete(order);
					mAdapter.notifyDataSetChanged();
					break;
				case R.id.button_order_detail:
					mListener.onRequestOrderDetail(order);
					break;
				}
			}
		}

		/**
		 * Listener for certain seekbar change regarding progress
		 * @author mhotan
		 */
		private class OrderProgressListener implements SeekBar.OnSeekBarChangeListener{

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				String order = mViewToOrder.get(seekBar);
				mListener.onProgressChanged(order, progress);
				
				if (progress == seekBar.getMax()) {
					mAdapter.remove(order);
					mListener.onOrderComplete(order);
					mAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}

		}
	}

}
