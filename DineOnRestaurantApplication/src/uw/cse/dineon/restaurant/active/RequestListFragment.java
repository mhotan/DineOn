package uw.cse.dineon.restaurant.active;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uw.cse.dineon.restaurant.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A List fragment that contains pending Request
 * @author mhotan
 */
public class RequestListFragment extends ListFragment {

	private final String TAG = this.getClass().getSimpleName();

	private RequestItemListener mListener;

	//TODO change string to order
	private ArrayAdapter<String> mAdapter;

	/**
	 * Activity Created its on create
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		List<String> requests = mListener.getCurrentRequests();
		
		mAdapter = new RequestListAdapter(this.getActivity(), requests);
		setListAdapter(mAdapter);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof RequestItemListener) {
			mListener = (RequestItemListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet RequestListFragment.RequestItemListener");
		}
	}

	//////////////////////////////////////////////////////
	//// Following are public setters.  That Activities can use
	//// to set the values of what is showed to the user for this 
	//// fragment TODO Change string to Order
	//////////////////////////////////////////////////////

	/**
	 * Adds request to this view
	 * @param request
	 */
	public void addRequest(String request){
		mAdapter.add(request);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Adds all the requests to this view
	 * @param request
	 */
	public void addAll(Collection<String> request){
		for (String o: request)
			mAdapter.add(o);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Deletes this request if it finds it
	 * @param request
	 */
	public void deleteRequest(String request){
		mAdapter.remove(request);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Clears all requests
	 */
	public void clearRequest(){
		mAdapter.clear();
		mAdapter.notifyDataSetChanged();
	}

	//////////////////////////////////////////////////////
	//// Following is the interface in which activities
	//// that wish to attach this Fragment must implement
	//// Intended to use for user input
	//////////////////////////////////////////////////////

	/**
	 * Mandatory Listener for this Fragment class
	 * @author mhotan
	 */
	public interface RequestItemListener {

		/**
		 * Request detail information to be presented
		 * about the specific request
		 * @param request request to get detail 
		 */
		public void onRequestRequestDetail(String request);

		/**
		 * Assign the staffmember to handle the request
		 * @param request request to handle 
		 * @param staff staff member to assign to request
		 */
		public void onAssignStaffToRequest(String request, String staff);

		/**
		 * Dismisses the request.  Doesn't mean issue was tended to
		 * @param request request to dismiss
		 */
		public void onDismissRequest(String request);

		/**
		 * Removes a request. request is removed completely from this 
		 * list.  This is a notification method 
		 * @param request
		 */
		public void onRemoveRequest(String request);

		/**
		 * Used to get the most recent up to date list of items to show
		 * Cannot return null
		 * @return List of requests to show
		 */
		public List<String> getCurrentRequests();
		
	}

	//////////////////////////////////////////////////////
	//// Adapter to handle using list items specific to 
	//// Showing request for restaurants
	//////////////////////////////////////////////////////

	/**
	 * Adpater to handle request management and layout
	 * @author mhotan
	 */
	private class RequestListAdapter extends ArrayAdapter<String> {

		private final Context mContext;
		private final List<String> mRequests;
		private final ArrayList<String> mStaff;

		/**
		 * Creates an adapter that manages the addition and layout of
		 * Orders
		 * @param ctx
		 * @param items
		 */
		public RequestListAdapter(Context ctx, List<String> orders){
			super(ctx, R.layout.listitem_restaurant_request, orders);
			this.mContext = ctx;
			this.mRequests = orders;
			
			mStaff = new ArrayList<String>();
			mStaff.add("Bert");
			mStaff.add("Ernie");
			mStaff.add("Big Bird");
			mStaff.add("Elmo");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.listitem_restaurant_request, parent, false);
			
			String request = mRequests.get(position);
			
			TextView title = (TextView) view.findViewById(R.id.label_request_title);
			title.setText(request);
			
			ImageButton reqDetail = (ImageButton) view.findViewById(R.id.button_request_detail);
			ImageButton sendToStaff = (ImageButton) view.findViewById(R.id.button_send_to_staff);
			CheckBox dismissBox = (CheckBox) view.findViewById(R.id.checkBox_dismiss_request);
			Spinner staff = (Spinner) view.findViewById(R.id.spinner_staff_to_assign);
			
			AllAroundListener listener = new AllAroundListener(request,
					staff,
					sendToStaff,
					reqDetail,
					dismissBox);
			
			reqDetail.setOnClickListener(listener);
			sendToStaff.setOnClickListener(listener);
			dismissBox.setOnCheckedChangeListener(listener);
			
			return view;
		}

		/**
		 * Listener to handle the use of buttons on request items
		 * @author mhotan
		 */
		private class AllAroundListener implements 
		View.OnClickListener, CompoundButton.OnCheckedChangeListener {

			private final String mRequest; //TODO Change to Request class
			private final Spinner mStaff;
			private final ImageButton mAssignToStaff, mDetailButton;
			private final CheckBox mDismiss;
			private boolean mDelete;

			public AllAroundListener(
					String request,
					Spinner staff,
					ImageButton assignToStaff,
					ImageButton detailButton,
					CheckBox dismiss){

				mRequest = request;
				mStaff = staff;
				mAssignToStaff = assignToStaff;
				mDetailButton = detailButton;
				mDismiss = dismiss;
				mDelete = false;

				mAssignToStaff.setOnClickListener(this);
				mDetailButton.setOnClickListener(this);
				mDismiss.setOnCheckedChangeListener(this);
			}

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// Is checked == show delete button
				mDelete = isChecked;
				if (mDelete) {
					mAssignToStaff.setImageResource(R.drawable.discard_content);
					mListener.onDismissRequest(mRequest);
				} else {
					mAssignToStaff.setImageResource(R.drawable.add_staffmember);
				}
			}

			@Override
			public void onClick(View v) {
				if (v == mAssignToStaff) {
					if (mDelete) { // User wants to delete this request
						mAdapter.remove(mRequest);
						mListener.onRemoveRequest(mRequest);
						mAdapter.notifyDataSetChanged();
					} else { // User wants to assign this request to a staff member
						String staffMem = mStaff.getSelectedItem().toString();
						mListener.onAssignStaffToRequest(mRequest, staffMem);
					}

				} else if (v == mDetailButton) {
					// User just wants details about this request
					mListener.onRequestRequestDetail(mRequest);
				}
			}

		}

	}


}