package uw.cse.dineon.restaurant.active;

import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.restaurant.R;
import android.app.ActionBar;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A fragment that shows the details of a particular dining session.
 * The dining session must be explicitly set using 
 * setDiningSession(Dining Session) method. This is because there are cases
 * where the fragment is statically defined in the layout xml.
 * 
 * Right now we are just showing a list of all the current users.
 * 
 * @author mhotan
 */
public class DiningSessionDetailFragment extends Fragment {

	private static final String TAG = DiningSessionDetailFragment.class.getSimpleName();

	/**
	 * Local reference to the dining session in focus.
	 */
	private DiningSession mDiningSession;

	/**
	 * Event listener.
	 */
	private DiningSessionDetailListener mListener;

	/**
	 * Adapter to control view of users.
	 */
	private UserListAdapter mUserAdapter;
	private OrderListAdapter mOrderAdapter;

	/**
	 * Independent list views for this view.
	 */
	private ListView mOrderList;
	private ListView mUserList;
	private TextView mOrderHeader;
	
	private static String tableLabel;
	private static String menuItemLabel;
	private static String qtyLabel;
	private static String newLine;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mDiningSession = mListener.getDiningSession();
		tableLabel = getString(R.string.table_label);
		menuItemLabel = getString(R.string.menu_item_label);
		qtyLabel = getString(R.string.qty_label);
		newLine = getString(R.string.new_line);
		// Update the state
		update();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View view = inflater.inflate(R.layout.fragment_diningsession_detail,
				container, false);

		mOrderList = (ListView) view.findViewById(R.id.list_view_orders);
		mUserList = (ListView) view.findViewById(R.id.list_view_users);
		mOrderHeader = (TextView) view.findViewById(R.id.label_user_orders);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof DiningSessionDetailListener) {
			mListener = (DiningSessionDetailListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement DiningSessionDetailFragment.DiningSessionDetailListener");
		}
	}


	/**
	 * This is a call the signifies the fragment to update its
	 * view based on the current state.  
	 * 
	 * IE if the Dining session exists then present that dining session
	 * to the user.
	 */
	private void update() {
		ActionBar ab = getActivity().getActionBar();
		if (mDiningSession == null) {
			Log.e(TAG, "Current Dining session is null");
		} else {
			ab.setTitle("Table " + mDiningSession.getTableID());
			mUserAdapter = new UserListAdapter(getActivity(), mDiningSession.getUsers());
			mOrderAdapter = new OrderListAdapter(getActivity(), mDiningSession.getOrders());

			if(mDiningSession.getOrders().size() == 0) {
				mOrderHeader.setVisibility(View.GONE);
			} else {
				mOrderHeader.setVisibility(View.VISIBLE);
			}

			mUserList.setAdapter(mUserAdapter);
			mOrderList.setAdapter(mOrderAdapter);
			mUserAdapter.notifyDataSetChanged();
			mOrderAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Adds a user to this current view.
	 * @param user User to add
	 */
	public void addUser(UserInfo user) {
		if (mUserAdapter != null && user != null) {
			mUserAdapter.add(user);
		}
	}

	/**
	 * Removes User from view if user exists.
	 * @param user User to remove
	 */
	public void removeUser(UserInfo user) {
		if (mUserAdapter != null && user != null) {
			mUserAdapter.remove(user);
		}
	}

	//////////////////////////////////////////////////////
	//// Following are public setters.  That Activities Or parent Fragments can use
	//// to set the values of what is showed to the user for this 
	//////////////////////////////////////////////////////

	/**
	 * Sets the current focused dining session.
	 * @param ds Dining session to reference
	 */
	public void setDiningSession(DiningSession ds) {
		mDiningSession = ds;
		update();
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
	public interface DiningSessionDetailListener {

		/**
		 * Sends a message to user defined.
		 * @param user User to send message to.
		 * @param message Message to send user.
		 */
		void sendShoutOut(UserInfo user, String message);

		/**
		 * Returns the current dining session to focus on.
		 * @return The current dining session.
		 */
		DiningSession getDiningSession();

	}

	//////////////////////////////////////////////////////
	//// Adapter to handle the presentation of specific 
	//// User presentations.
	//////////////////////////////////////////////////////

	/**
	 * Adapter that controls the displays of user information
	 * for this particular dining session.
	 * @author mhotan
	 */
	@SuppressWarnings("SIC_INNER_SHOULD_BE_STATIC")
	private class UserListAdapter extends ArrayAdapter<UserInfo> {

		private final Context mContext;
		private List<UserInfo> mUsers;

		/**
		 * Creates a List adapter to show UserInfo.
		 * @param context Context to assign to.
		 * @param users Users to associate to adapter.
		 */
		public UserListAdapter(Context context, List<UserInfo> users) {
			super(context, R.layout.listitem_userinfo, users);
			mContext = context;
			mUsers = users;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.listitem_userinfo, null, true);

			new UserInfoListItemListener(view, mUsers.get(position));
			return view;
		}

		/**
		 * user list item listener to react to user callbacks.
		 * @author mhotan
		 */
		private class UserInfoListItemListener implements 
		OnClickListener {

			private final ImageView mProfileImage;
			private final TextView mUserName;
			private final ImageButton mSendButton;
			private final EditText mMessage;
			private final UserInfo mUser;

			/**
			 * Connects a view to a user.  This class reacts to interactions
			 * between the view and the user.
			 * 
			 * View must be of type R.layout.listitem_userinfo
			 * 
			 * @param v View to connect to User
			 * @param user User to connect to
			 */
			public UserInfoListItemListener(View v, UserInfo user) {
				mProfileImage = (ImageView) v.findViewById(R.id.image_user_profile_image);
				mUserName = (TextView) v.findViewById(R.id.label_username);
				mSendButton = (ImageButton) v.findViewById(R.id.button_send_shout_out);
				mMessage = (EditText) v.findViewById(R.id.input_shout_out);
				mUser = user;

				if (mProfileImage == null 
						|| mUserName == null
						|| mSendButton == null
						|| mMessage == null) {
					throw new IllegalArgumentException(
							"Invalid view (" + v + "), Not a userinfo view");
				}
				if (mUser == null) {
					throw new IllegalArgumentException(
							TAG + ":[UserInfoListItemListener] User is null");
				}

				// TODO Download Profile Image.
				// mProfileImage.setImage blah blah

				// Set the title of this box to be the user name
				mUserName.setText(mUser.getName());

				// Set the send button on click listener
				mSendButton.setOnClickListener(this);
			}

			@Override
			public void onClick(View v) {
				// Do a check for which item was clicked
				if (v == mSendButton) {
					String message = mMessage.getText().toString();
					if (!(message == null || message.isEmpty())) {
						mListener.sendShoutOut(mUser, message);
					}
				}
			}
		}
	}

	//////////////////////////////////////////////////////
	//// Adapter to handle the presentation of specific 
	//// User presentations.
	//////////////////////////////////////////////////////

	/**
	 * Adapter that controls the displays of order information
	 * for this particular dining session.
	 * @author glee23
	 */
	@SuppressWarnings("SIC_INNER_SHOULD_BE_STATIC")
	private static class OrderListAdapter extends ArrayAdapter<Order> {

		private final Context mContext;
		private List<Order> mOrders;

		/**
		 * Creates a List adapter to show UserInfo.
		 * @param context Context to assign to.
		 * @param orders a List of orders to associate to adapter.
		 */
		public OrderListAdapter(Context context, List<Order> orders) {
			super(context, R.layout.listitem_diningsession_order, orders);
			mContext = context;
			mOrders = orders;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.listitem_diningsession_order, null, true);

			new OrderListItemListener(view, mOrders.get(position));
			return view;
		}

		/**
		 * user list item listener to react to user callbacks.
		 * @author glee23
		 */
		private class OrderListItemListener {

			private final TextView mOrderTitle;
			private final TextView mOrderContents;
			private final TextView mOrderTime;
			private final Order mOrder;

			/**
			 * Connects a view to an order.  This class reacts to interactions
			 * between the view and the order.
			 * 
			 * View must be of type R.layout.listitem_diningsession_order
			 * 
			 * @param v View to connect to User
			 * @param o Order to populate the view with
			 */
			public OrderListItemListener(View v, Order o) {
				mOrderContents = (TextView) v.findViewById(R.id.label_order_contents);
				mOrder = o;
				mOrderTitle = (TextView) v.findViewById(R.id.label_order_title);
				mOrderTime = (TextView) v.findViewById(R.id.label_order_time);
				if (mOrder == null || mOrderContents == null) {
					throw new IllegalArgumentException(
							"Invalid view (" + v + "), Not a order view");
				}
				
				mOrderTitle.setText(tableLabel + mOrder.getTableID());
				List<CurrentOrderItem> items = mOrder.getMenuItems();
				//Displays menu items as a string
				StringBuffer buf = new StringBuffer();

				mOrderTime.setText(mOrder.getOriginatingTime().toString());
				for (CurrentOrderItem item : items) {
					buf.append(menuItemLabel + item.getMenuItem().getTitle() + newLine);
					buf.append(qtyLabel + item.getQuantity() + newLine + newLine);
				}
				if(items.size() != 0) {
					buf.delete(buf.length() - 2, buf.length());
				}

				mOrderContents.setText(buf.toString());

			}

		}
	}

}
