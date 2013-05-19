package uw.cse.dineon.user.bill.test;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import uw.cse.dineon.user.bill.CurrentOrderActivity;
import uw.cse.dineon.user.bill.CurrentOrderFragment;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.Button;

public class CurrentOrderActivityTest extends
		ActivityInstrumentationTestCase2<CurrentOrderActivity> {

	private CurrentOrderActivity mActivity;
	private Restaurant r;

	private ParseUser testUser;

	private UserInfo mUI;

	private RestaurantInfo mRI;
	
	private DineOnUser dineOnUser;
	
	private DiningSession testSession;
	
	int orderNum = 100;

	private static String menuItemText = "Fake Menu Item 1"; 
	private static String menuItemDescription = "Fake Menu Item 1 Description";
	
	public CurrentOrderActivityTest() {
		super(CurrentOrderActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Parse.initialize(null, "RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", "wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
		setActivityInitialTouchMode(false);
		
		testUser = ParseUser.logIn("zach", "zach");
		dineOnUser = new DineOnUser(testUser);
		
		mRI = new RestaurantInfo(testUser);
		r = createFakeRestaurant(testUser);

		testSession = createFakeDiningSession(testUser);
		r.addDiningSession(testSession);
		dineOnUser.setDiningSession(testSession);
		DineOnUserApplication.cachedUser = dineOnUser;
		DineOnUserApplication.setDineOnUser(dineOnUser);
		Intent addEvent = new Intent();
	    setActivityIntent(addEvent);
		mActivity = getActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}


	@SmallTest
	public void testOnClickRequestButton() {
		Button b = (Button) mActivity.findViewById(R.id.button_request);
		CurrentOrderFragment cof = (CurrentOrderFragment) mActivity.getSupportFragmentManager()
					.findFragmentById(R.id.fragment_current_order);
		
		cof.onClick(b);
		getInstrumentation().waitForIdleSync();
		
		this.sendKeys("T E S T SPACE R E Q U E S T");
		
		
		final View alertView = cof.getLayoutInflater(cof.getArguments()).inflate(
				R.layout.alert_build_request, null);
		cof.getDescription(alertView);
	}
	
	public void testOnPlaceOrder() {
		mActivity.onPlaceOrder("rs");
	}

	public void testOnRequestMade() {
		mActivity.onRequestMade("MORE WATER!!!!");
	}
	
	public void testDeleteResume() {
		getInstrumentation().waitForIdleSync();
		mActivity.finish();

		mActivity = getActivity();
	}

	
	private DiningSession createFakeDiningSession(ParseUser user) throws ParseException {
		return new DiningSession(1, 
				new GregorianCalendar().getTime(), 
				mUI, mRI);
	}
	
	private Restaurant createFakeRestaurant(ParseUser user) throws ParseException {
		Restaurant r = new Restaurant(user);
		r.addOrder(createFakeOrder(user));
		r.addCustomerRequest(createFakeRequest(user));
		r.addDiningSession(createFakeDiningSession(user));
		return r;
	}
	
	private Order createFakeOrder(ParseUser user) {
		return new Order(orderNum++, mUI, createFakeMenuItems());
	}

	private List<MenuItem> createFakeMenuItems() {
		MenuItem m = new MenuItem(1, 3.99, menuItemText, menuItemDescription);
		List<MenuItem> items = new ArrayList<MenuItem>();
		items.add(m);
		return items;
	}

	private CustomerRequest createFakeRequest(ParseUser user) {
		return new CustomerRequest("I want my food now!", mUI, new GregorianCalendar().getTime());
	}
	
}
