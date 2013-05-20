package uw.cse.dineon.library.test;

import java.util.ArrayList;
import java.util.List;

import com.parse.Parse;
import com.parse.ParseUser;

import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.UserInfo;
import android.test.AndroidTestCase;

public class OrderTest extends AndroidTestCase {

	UserInfo testUInfo;
	List<MenuItem> testItems;
	MenuItem testItem;
	Order testOrder;
	ParseUser testUser;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Parse.initialize(this.getContext(), "RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", "wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
		
		testUser = new ParseUser();
		testUser.setUsername("tester1");
		testUser.setPassword("pass");
		testUser.signUp();
		testUser.save();
		
		testUInfo = new UserInfo(testUser);
		testItems = new ArrayList<MenuItem>();
		testItem = new MenuItem(24, 4.5, "Root Beer Float", "Ice cream and root beer");
		testItems.add(testItem);
		testOrder = new Order(32, testUInfo, testItems);
	}
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		testUser.delete();
	}

	/**
	 * Asserts that the Order correctly stores the expected data.
	 * 
	 * White box
	 */
	public void testOrderIntUserInfoListOfMenuItem() {
		assertEquals(testItems, testOrder.getMenuItems());
		assertEquals(testUInfo, testOrder.getOriginalUser());
		assertEquals(32, testOrder.getTableID());
		
	}



}
