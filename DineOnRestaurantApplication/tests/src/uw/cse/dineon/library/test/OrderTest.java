package uw.cse.dineon.library.test;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.UserInfo;
import android.test.AndroidTestCase;

import com.parse.ParseUser;

/**
 * Tests the library class Order
 * @author Zach
 *
 */
public class OrderTest extends AndroidTestCase {

	UserInfo testUInfo;
	List<CurrentOrderItem> testItems;
	MenuItem testItem;
	Order testOrder;
	ParseUser testUser;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		testUser = new ParseUser();
		testUser.setPassword("pass");
		
		testUInfo = new UserInfo(testUser);
		testUInfo.setObjId("tui");
		testItems = new ArrayList<CurrentOrderItem>();
		testItem = new MenuItem(24, 4.5, "Root Beer Float", "Ice cream and root beer");
		testItem.setObjId("ti");
		CurrentOrderItem mCOI = new CurrentOrderItem(testItem);
		mCOI.setObjId("coi");
		testItems.add(mCOI);
		testOrder = new Order(32, testUInfo, testItems);
		testOrder.setObjId("to");
	}
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
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
