package uw.cse.dineon.restaurant.active;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.animation.ExpandAnimation;
import uw.cse.dineon.library.image.ImageObtainable;
import uw.cse.dineon.restaurant.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * Fragment represents a List of Orders that are pending
 * for this restaurant.
 * @author glee23
 */
public class OrderListFragment extends ListFragment {

	private static final String TAG = OrderListFragment.class.getSimpleName();

	/**
	 * Listener for fragment interactions.
	 */
	private OrderItemListener mListener;

	/**
	 * Adapter that controls the income of Orders.
	 */
	private OrderListAdapter mAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		List<Order> orders = mListener.getPendingOrders();
		if (orders == null) {
			Log.e(TAG, "List of order retrieved is null");
			orders = new ArrayList<Order>();
		}
		
		//We can assert that the listener has a non null list of dining sessions
		mAdapter = new OrderListAdapter(getActivity(), orders);
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
	 * @author glee23
	 */
	public interface OrderItemListener extends ImageObtainable {

		/**
		 * Notifies the progress of this order has changed.
		 * @param order order to reference
		 * @param progress progress of this order
		 */
		void onProgressChanged(Order order, int progress);

		/**
		 * Restaurant wants to notify customer that the order is complete.
		 * @param order order to reference
		 */
		void onOrderComplete(Order order);

		/**
		 * User(Restaurant Employee) wants to see details pertaining this order.
		 * @param order order to reference
		 */
		void onOrderSelected(Order order);

		/**
		 * Returns a non null list of orders to present.
		 * @return return the list of orders.
		 */
		List<Order> getPendingOrders();

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

		/**
		 * Creates an adapter that manages the addition and layout of
		 * Orders.
		 * @param ctx Context
		 * @param orders List of strings
		 */
		public OrderListAdapter(Context ctx, List<Order> orders) {
			super(ctx, R.layout.listitem_restaurant_order_top, orders);
			this.mContext = ctx;
		}

		@Override
		public void add(Order o) {
			super.add(o);
			this.notifyDataSetChanged();
		}

		@Override
		public void addAll(Collection<? extends Order> collection) {
			super.addAll(collection);
			notifyDataSetChanged();
		}

		@Override
		public void clear() {
			super.clear();
			this.notifyDataSetChanged();
		}

		@SuppressWarnings("BC_UNCONFIRMED_CAST")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			// Establish a reference to the top and bottom of ex
			View vwTop;
			View vwBot;

			LinearLayout layoutView = null;

			if(convertView == null) {
				layoutView = new LinearLayout(mContext);
				layoutView.setOrientation(LinearLayout.VERTICAL);
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				vwTop = inflater.inflate(R.layout.listitem_restaurant_order_top, null, true);
				vwBot = inflater.inflate(R.layout.listitem_restaurant_order_bot, null, true);
				layoutView.addView(vwTop);
				layoutView.addView(vwBot);
				convertView = layoutView;
			} else {
				//Everything already created, just find them
				vwTop = convertView.findViewById(R.id.listitem_order_top);
				vwBot = convertView.findViewById(R.id.listitem_order_bot);
			}

			Order orderToShow = getItem(position);

			// For every restaurant to present create a handler for the order;
			// We are creating this view for the very first time
			if (layoutView != null) {
				// Create a handler just for the restaurant.
				new OrderHandler(orderToShow, vwTop, vwBot);
			}
			return convertView;
		}

		/**
		 * Listener for certain item of an order item view.
		 * @author mhotan
		 */
		private class OrderHandler implements OnClickListener, OnSeekBarChangeListener {

			private final Order mOrder;
			private final ImageView mExpandDown;
			private final ImageButton mPickOrder;
			private final View mTop, mBottom;

			/**
			 * Build this handler from the Order and its corresponding views.
			 * 
			 * @param order Order to associate to.
			 * @param top Top view for the order.
			 * @param bottom bottom view for the order.
			 */
			public OrderHandler(Order order, View top, View bottom) {
				mOrder = order;

				mTop = top;
				mBottom = bottom;

				// Get a reference to all the top pieces 
				final ImageView ORDERIMAGE = (ImageView) 
						mTop.findViewById(R.id.image_order_thumbnail);
				TextView orderTitle = 
						(TextView) mTop.findViewById(R.id.label_order_title);
				mExpandDown = (ImageView) 
						mTop.findViewById(R.id.button_expand_order);
				TextView time = (TextView) mTop.findViewById(R.id.label_order_time);
				mPickOrder = (ImageButton) mBottom.findViewById(R.id.button_proceed);	
				
				// Get a reference to all the bottom pieces
				Button buttonCompleteOrder = (Button) 
						mBottom.findViewById(R.id.button_completed_order);

				SeekBar progressBar = (SeekBar) mBottom.findViewById(R.id.seekbar_order_progress);

				//Populate
				int table = order.getTableID();

				if(table == -1) { // No Table
					orderTitle.setText(order.getOriginalUser().getName());
				} else {
					orderTitle.setText(getString(R.string.table) + order.getTableID() 
							+ getString(R.string.dash) + order.getOriginalUser().getName());
				}

				progressBar.setMax(100);
				progressBar.setProgress(0);

				time.setText(order.getOriginatingTime().toString());

				// Add listeners for reaction purposes
				buttonCompleteOrder.setOnClickListener(this);
				progressBar.setOnSeekBarChangeListener(this);
				
				// Set the image of this order
//				DineOnImage image = order.getMainImage();
//				if (image != null) {
//					mListener.onGetImage(image, new ImageGetCallback() {
//
//						@Override
//						public void onImageReceived(Exception e, Bitmap b) {
//							if (e == null) {
//								// We got the image so set the bitmap
//								ORDERIMAGE.setImageBitmap(b);
//							}
//						}
//					});
//				}

				// Set the bottom view to initial to be invisible
				mBottom.setVisibility(View.GONE);

				mTop.setOnClickListener(this);
				mPickOrder.setOnClickListener(this);
			}

			@Override
			public void onClick(View v) {

				if (v == mTop || v == mPickOrder) { 
					int bottomVisibility = mBottom.getVisibility();
					// Expand the bottom view if it is not shown
					// Hide the expand down button.
					if (bottomVisibility == View.GONE) {
						mExpandDown.setVisibility(View.GONE);
					} else if (bottomVisibility == View.VISIBLE) {
						mExpandDown.setVisibility(View.VISIBLE);
					}

					// Expand the animation
					ExpandAnimation expandAni = new ExpandAnimation(mBottom, 500);
					mBottom.startAnimation(expandAni);

				} else if (v.getId() == R.id.button_completed_order) {
					mAdapter.remove(mOrder);
					mListener.onOrderComplete(mOrder);
					mAdapter.notifyDataSetChanged();
				}
				
				if (v == mPickOrder) {
					mListener.onOrderSelected(mOrder);
				}
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mListener.onProgressChanged(mOrder, progress);

				if (progress == seekBar.getMax()) {
					mAdapter.remove(mOrder);
					mListener.onOrderComplete(mOrder);
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
