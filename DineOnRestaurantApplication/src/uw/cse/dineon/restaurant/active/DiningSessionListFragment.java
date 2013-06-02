package uw.cse.dineon.restaurant.active;

import java.util.Collection;
import java.util.List;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.animation.ExpandAnimation;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * For displaying current restaurant customers.
 * TODO Improve and complete
 * @author mhotan
 * @author blasv
 */
public class DiningSessionListFragment extends ListFragment {

	/**
	 * For Use with Log functions.
	 */
	private static final String TAG = DiningSessionListFragment.class.getSimpleName();

	//An activity that implements the required listener functions
	private DiningSessionListListener mListener;

	private static final String SESSIONS = TAG + "_sessions";

	private DiningSessionListAdapter mAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		//We can assert that the listener has a non null list of dining sessions
		mAdapter = new DiningSessionListAdapter(getActivity(), mListener.getCurrentSessions());
		setListAdapter(mAdapter);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof DiningSessionListListener) {
			mListener = (DiningSessionListListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet CurrentCustomersFragment.CustomerListener");
		}
	}

	/**
	 * @param session DiningSession
	 */
	public void addDiningSession(DiningSession session) {
		if (mAdapter != null) {
			mAdapter.add(session);
		}
	}

	/**
	 * 
	 * @param session DiningSession
	 */
	public void removeDiningSession(DiningSession session) {
		if (mAdapter != null) {
			mAdapter.remove(session);
		}
	}

	/**
	 * Mandatory interface for this fragment.
	 * @author mhotan
	 */
	public interface DiningSessionListListener {

		/**
		 * User request detail about a particular dining session.
		 * @param ds Dining session of focus
		 */
		void onDiningSessionSelected(DiningSession ds);
		
		/**
		 * Used to get the most recent up to date list of items to show.
		 * Cannot return null
		 * @return List of sessions to show
		 */
		public List<DiningSession> getCurrentSessions();

	}

	/**
	 * List adapter for holding dining sessions.
	 * @author glee23
	 */
	private class DiningSessionListAdapter extends ArrayAdapter<DiningSession> {

		private Context mContext;

		/**
		 * Constructs a new UserList Adapter.
		 * 
		 * @param ctx The current context
		 * @param sessions The list of users to display
		 */
		public DiningSessionListAdapter(Context ctx, List<DiningSession> sessions) {
			super(ctx, R.layout.listitem_restaurant_request_top, sessions);
			mContext = ctx;
		}

		@Override
		public void add(DiningSession d) {
			super.add(d);
			this.notifyDataSetChanged();
		}

//		@Override
//		public void addAll(Collection<? extends DiningSession> collection) {
//			super.addAll(collection);
//			notifyDataSetChanged();
//		}
//
//		@Override
//		public void clear() {
//			super.clear();
//			this.notifyDataSetChanged();
//		}

		@SuppressWarnings("BC_UNCONFIRMED_CAST")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View vwTop;
			View vwBot;

			LinearLayout layoutView = null;

			if (convertView == null) {
				//Initialize and verticalize parent container viewgroup
				layoutView = new LinearLayout(mContext);
				layoutView.setOrientation(LinearLayout.VERTICAL);
				LayoutInflater inflater = 
						(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				vwTop = inflater.inflate(R.layout.listitem_restaurant_user_top, null, true);
				vwBot = inflater.inflate(R.layout.listitem_restaurant_user_bot, null, true);
				layoutView.addView(vwTop);
				layoutView.addView(vwBot);
				convertView = layoutView;
			} else {
				//Everything already created, just find them
				vwTop = convertView.findViewById(R.id.listitem_user_top);
				vwBot = convertView.findViewById(R.id.listitem_user_bot);
			}

			DiningSession sessionToShow = getItem(position);

			// For every restaurant to present create a handler for the session;
			// We are creating this view for the very first time
			if (layoutView != null) {
				// Create a handler just for the session.
				new DiningSessionHandler(sessionToShow, vwTop, vwBot);
			}
			return convertView;

		}

		/**
		 * Listener for certain item of a customer request view.
		 * @author glee23
		 */
		private class DiningSessionHandler implements OnClickListener {

			private final DiningSession mDiningSession;
			private final ImageView mExpandDown;
			private final Button mPickSession;
			private final View mTop, mBottom;

			/**
			 * Build this handler from the DiningSession and its corresponding views.
			 * 
			 * @param session DiningSession to associate to.
			 * @param top Top view for the session.
			 * @param bottom bottom view for the session.
			 */
			public DiningSessionHandler(DiningSession session, View top, View bottom) {
				mDiningSession = session;

				mTop = top;
				mBottom = bottom;

				// Get a reference to all the top pieces 
				final ImageView SESSIONIMAGE = (ImageView) 
						mTop.findViewById(R.id.image_user_thumbnail);
				TextView title = (TextView) mTop.findViewById(R.id.label_user_name);
				TextView dateText = (TextView) mTop.findViewById(R.id.label_checkin_time);
				mExpandDown = (ImageView) 
						mTop.findViewById(R.id.button_expand_user);
				mPickSession = (Button) mBottom.findViewById(R.id.button_proceed);	

				// Get a reference to all the bottom pieces
				TextView orderHeader = (TextView) 
						mBottom.findViewById(R.id.label_user_order_header);
				TextView orderText = (TextView) mBottom.findViewById(R.id.label_user_orders);

				List<Order> orders = mDiningSession.getOrders();
				List<UserInfo> infolist = mDiningSession.getUsers();

				//Populate
				dateText.setText(mDiningSession.getOriginatingTime().toString());

				if(orders == null) {
					orderHeader.setVisibility(View.GONE);
					orderText.setVisibility(View.GONE);
				} else {

					if(orders.size() == 0) {
						orderText.setVisibility(View.GONE);
						orderHeader.setText(getString(R.string.no_orders));
					} else {
						orderText.setVisibility(View.VISIBLE);
						orderHeader.setText(getString(R.string.label_orders));
					}
				}

				//Displays Order information as a string
				StringBuffer buf = new StringBuffer();

				for (Order o : orders) {

					int tableID = o.getTableID();
					if(tableID != -1) {
						buf.append(getString(R.string.table));
						buf.append(tableID);
					}

					buf.append(getString(R.string.newline));
					buf.append(o.getOriginatingTime().toString());
					buf.append(getString(R.string.newline) + getString(R.string.newline));
				}
				if(orders.size() != 0) {
					buf.delete(buf.length() - 2, buf.length());
				}

				orderText.setText(buf.toString());

				String name;

				if(infolist != null && infolist.size() > 0 && infolist.get(0) != null) {
					UserInfo ui = infolist.get(0);
					name = ui.getName();
				} else {
					Log.w(TAG, getString(R.string.cant_retrieve_name));
					name = getString(R.string.no_customer);
				}

				title.setText(name);

				mTop.setOnClickListener(this);
				mPickSession.setOnClickListener(this);
				
				// Set the image of this session
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
			}

			@Override
			public void onClick(View v) {

				if (v == mTop || v == mPickSession) { 
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

				}

				if (v == mPickSession) {
					mListener.onDiningSessionSelected(mDiningSession);
				}
			}

		}

	}
}