package uw.cse.dineon.restaurant.active;

import java.util.List;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.restaurant.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.QuickContactBadge;
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
public class DiningSessionDetailFragment extends ListFragment {

	private static final String TAG = DiningSessionDetailFragment.class.getSimpleName();

	private static final String DINING_SESSION = TAG + "_diningsession";

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
	private UserListAdapter mAdapter;

	/**
	 * Creates a dining session that is ready to rock.
	 * No need to call set Dining Session.
	 * @param ds Dining session to use
	 * @return Fragment to use.
	 */
	public static DiningSessionDetailFragment newInstance(DiningSession ds) {
		Bundle args = new Bundle();
		args.putParcelable(DINING_SESSION, ds);
		DiningSessionDetailFragment frag = new DiningSessionDetailFragment();
		frag.setArguments(args);
		return frag;
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
			// TODO 
			ab.setTitle("No Dining session selected");
		} else {
			ab.setTitle("Table: " + mDiningSession.getTableID());
			mAdapter = new UserListAdapter(getActivity(), mDiningSession.getUsers());
			setListAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle args = getArguments();
		
		// If the orientation changed or fragment temporarily died and 
		// came back
		if (savedInstanceState != null && savedInstanceState.containsKey(DINING_SESSION)) {
			mDiningSession = savedInstanceState.getParcelable(DINING_SESSION);
		} else if (args != null && args.containsKey(DINING_SESSION)) {
			mDiningSession = args.getParcelable(DINING_SESSION);
		}

		// Udpate the state
		update();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(DINING_SESSION, mDiningSession);
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
	 * Adds a user to this current view.
	 * @param user User to add
	 */
	public void addUser(UserInfo user) {
		if (mAdapter != null && user != null) {
			mAdapter.add(user);
		}
	}

	/**
	 * Removes User from view if user exists.
	 * @param user User to remove
	 */
	public void removeUser(UserInfo user) {
		if (mAdapter != null && user != null) {
			mAdapter.remove(user);
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

			private final QuickContactBadge mProfileImage;
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
				mProfileImage = (QuickContactBadge) v.findViewById(R.id.image_user_profile_image);
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
				mUserName.setText("  " + mUser.getName());

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

}
