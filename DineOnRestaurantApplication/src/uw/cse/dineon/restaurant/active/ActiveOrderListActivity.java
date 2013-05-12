package uw.cse.dineon.restaurant.active;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.restaurant.DineOnRestaurantActivity;
import uw.cse.dineon.restaurant.R;
import android.content.Intent;
import android.os.Bundle;

/**
 * 
 * @author 
 *
 */
public class ActiveOrderListActivity extends DineOnRestaurantActivity
implements OrderDetailFragment.OrderDetailListener,
RequestDetailFragment.RequestDetailListener,
OrderListFragment.OrderItemListener,
RequestListFragment.RequestItemListener {

	private static final String FRAG_TAG = "Tag for finding fragment for details";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_active_orders);

		//	TODO Figure out which restaurant I am
		//  Update all the values for this particular restaurant from the cloud

		// TODO Remove Test cases

	}

	//////////////////////////////////////////////////////////////////////
	////	Listener for OrderDetailFragment.OrderDetailListener
	////	For Fragment call backs  
	//////////////////////////////////////////////////////////////////////

	// Following two methods control the view of the activity

	@Override
	public void onRequestRequestDetail(String request) {
		// TODO Auto-generated method stub
//		if (!Utility.isPaneSplitable(this)) {
			// We are in portrait mode
			Intent intent = new Intent(getApplicationContext(),
					RequestDetailActivity.class);
			intent.putExtra(RequestDetailActivity.EXTRA_REQUEST, request);
			startActivity(intent);
//		} else {
//
//			// Use the right kind of fragment
//			RequestDetailFragment reqFrag = new RequestDetailFragment(); 
//			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//
//			// Attempt to find the fragment if it exist
//			Fragment frag = getSupportFragmentManager().findFragmentByTag(FRAG_TAG);
//			if (frag == null || !frag.isInLayout()) {
//				ft.add(reqFrag, FRAG_TAG);
//			} else 
//				ft.replace(R.id.container_order_request, reqFrag, FRAG_TAG);
//			ft.commit(); 
//			reqFrag.setRequest(request);
//		}
	}

	@Override
	public void onRequestOrderDetail(String order) {
		// TODO Auto-generated method stub
//		if (!Utility.isPaneSplitable(this)) {
			// We are in portrait mode
			Intent intent = new Intent(getApplicationContext(),
					OrderDetailActivity.class);
			intent.putExtra(OrderDetailActivity.EXTRA_ORDER, order);
			startActivity(intent);
//		} else {
//			// Use the right kind of fragment
//			OrderDetailFragment ordFrag = new OrderDetailFragment(); 
//			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//
//			// Attempt to find the fragment if it exist
//			Fragment frag = getSupportFragmentManager().findFragmentByTag(FRAG_TAG);
//			if (frag == null || !frag.isInLayout()) {
//				ft.add(ordFrag, FRAG_TAG);
//			} else 
//				ft.replace(R.id.container_order_request, ordFrag, FRAG_TAG);
//			ft.commit(); 
//			ordFrag.setOrder(order);
//		}
	}

	@Override
	public List<String> getCurrentRequests() {
		// TODO Auto-generated method stub
		String[] requests = {"Water Please", "Waiter Needed", "There is a booger in my soup"};
		List<String> reqList = new ArrayList<String>();
		for (String s: requests) {
			reqList.add(s);
		}
		return reqList;
	}

	@Override
	public List<String> getCurrentOrders() {
		String[] orders = {"Fried Chicken", "Peanut butter and Jelly", "Fried Rice", 
				"Chicken Noodle Soup", "Butte Balls"};
		List<String> ordList = new ArrayList<String>();
		for (String s: orders) {
			ordList.add(s);
		}
		return ordList;
	}

	@Override
	public void sendMessage(String order, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendMessage(String request, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendTaskToStaff(String request, String staff, String urgency) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAssignStaffToRequest(String request, String staff) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDismissRequest(String request) {
		// TODO Auto-generated method stub

	}



	@Override
	public void onRemoveRequest(String request) {
		// TODO Auto-generated method stub

	}



	@Override
	public void onProgressChanged(String order, int progress) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onOrderComplete(String order) {
		// TODO Auto-generated method stub

	}





}
