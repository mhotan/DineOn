package uw.cse.dineon.restaurant.test;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.active.RestauarantMainActivity;
import android.content.Intent;
import android.os.Parcelable;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

public class RestaurantMainActivityTest extends
ActivityInstrumentationTestCase2<RestauarantMainActivity> {

	private RestauarantMainActivity mActivity;
	private Restaurant r;

	private ParseUser mUser;

	private UserInfo mUI;

	private RestaurantInfo mRI;

	private CustomerRequest mRequest;
	private Order mOrder;
	private DiningSession testSession;
	
	int orderNum = 100;

	private static String menuItemText = "Fake Menu Item 1"; 
	private static String menuItemDescription = "Fake Menu Item 1 Description";

	public RestaurantMainActivityTest() throws ParseException {
		super(RestauarantMainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Parse.initialize(getInstrumentation().getTargetContext(), 
				"RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", 
				"wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
		mUser = ParseUser.logIn("glee23", "p");

		mUI = new UserInfo(mUser);
		mRI = new RestaurantInfo(mUser);
		mRequest = createFakeRequest(mUser);
		mOrder = createFakeOrder(mUser);
		testSession = createFakeDiningSession(mUser);
		r = createFakeRestaurant(mUser);
		r.addCustomerRequest(mRequest);
		r.addOrder(mOrder);
		r.addDiningSession(testSession);

		ArrayList<Parcelable> dSessions = new ArrayList<Parcelable>();
		dSessions.add(testSession);

		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				RestauarantMainActivity.class);
		intent.putExtra(DineOnConstants.KEY_USER, new DineOnUser(mUser));
		intent.putExtra(DineOnConstants.KEY_RESTAURANT, r);
		intent.putExtra(DineOnConstants.DINING_SESSION, dSessions);
		setActivityIntent(intent);

		setActivityInitialTouchMode(false);

		mActivity = getActivity();
	}

	private Restaurant createFakeRestaurant(ParseUser user) throws ParseException{
		Restaurant r = new Restaurant(user);
		return r;
	}

	private Order createFakeOrder(ParseUser user){
		return new Order(orderNum++, mUI, createFakeMenuItems());
	}

	private List<MenuItem> createFakeMenuItems(){
		MenuItem m = new MenuItem(1, 3.99, menuItemText, menuItemDescription);
		List<MenuItem> items = new ArrayList<MenuItem>();
		items.add(m);
		return items;
	}

	private CustomerRequest createFakeRequest(ParseUser user){
		return new CustomerRequest("I want my food now!", mUI, new GregorianCalendar().getTime());
	}

	private DiningSession createFakeDiningSession(ParseUser user) throws ParseException {
		return new DiningSession(1, 
				new GregorianCalendar().getTime(), 
				mUI, mRI);
	}

	@Override
	protected void tearDown() throws Exception {
		mRequest.deleteFromCloud();
		mOrder.deleteFromCloud();
		testSession.deleteFromCloud();
		mUI.deleteFromCloud();
		mRI.deleteFromCloud();
		r.deleteFromCloud();
		mActivity.finish();
		super.tearDown();
	}

	public void testOrderLayoutItemsPopulate() {
		TextView orderTitle = (TextView) mActivity.findViewById(uw.cse.dineon.restaurant.R.id.button_order_title);
		TextView orderTime = (TextView) mActivity.findViewById(uw.cse.dineon.restaurant.R.id.label_order_time);
		ImageButton arrowButton = (ImageButton) mActivity.findViewById(uw.cse.dineon.restaurant.R.id.button_expand_order);
		assertNotNull(orderTitle);
		assertNotNull(orderTime);
		assertNotNull(arrowButton);
	}

	public void testRequestLayoutItemsPopulate() {
		TextView requestTitle = (TextView) mActivity.findViewById(uw.cse.dineon.restaurant.R.id.label_request_title);
		TextView requestTime = (TextView) mActivity.findViewById(uw.cse.dineon.restaurant.R.id.label_request_time);
		ImageButton arrowButton = (ImageButton) mActivity.findViewById(uw.cse.dineon.restaurant.R.id.button_expand_request);
		assertNotNull(requestTitle);
		assertNotNull(requestTime);
		assertNotNull(arrowButton);
	}

}
