package uw.cse.dineon.library.test;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.UserInfo;
import android.test.AndroidTestCase;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Tests the library class Order. This test class
 * makes sure all the fields are correctly set and
 * returned.
 * 
 * White box tests
 * 
 * @author Zach, glee23
 */
public class OrderTest extends AndroidTestCase {

	UserInfo testUInfo;
	List<MenuItem> testItems;
	MenuItem testItem;
	Order testOrder;
	ParseUser testUser;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		testUser = new ParseUser();
		testUser.setUsername("hello");
		testUser.setPassword("pass");
		
		testUInfo = new UserInfo(testUser);
		testUInfo.setObjId("tui");
		testItems = new ArrayList<MenuItem>();
		testItem = new MenuItem("24", 4.5, "Root Beer Float", "Ice cream and root beer");
		testItem.setObjId("ti");
		CurrentOrderItem mCOI = new CurrentOrderItem(testItem);
		mCOI.setObjId("coi");
		testItems.add(testItem);
		testOrder = new Order(32, testUInfo, testItems);
		testOrder.setObjId("to");
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Asserts that the Order correctly returns the table id.
	 */
	public void testGetTableID() {
		assertEquals(32, testOrder.getTableID());
	}
	
	/**
	 * Asserts that the Order correctly returns the user.
	 */
	public void testGetOriginalUser() {
		assertEquals(testUInfo, testOrder.getOriginalUser());
	}
	
	/**
	 * Asserts that the Order correctly returns an empty list
	 * when there aren't any menu items.
	 */
	public void testGetNoMenuItems() {
		testOrder = new Order(32, testUInfo, new ArrayList<MenuItem>());
		assertEquals(new ArrayList<CurrentOrderItem>(), testOrder.getMenuItems());
	}
	
	/**
	 * Asserts that the Order correctly returns the list of 
	 * menu items.
	 */
	public void testGetMenuItems() {
		assertEquals(testItems, testOrder.getMenuItems());
	}

	/**
	 * Asserts that the Order stays the same when packed and
	 * unpacked.
	 */
	public void testPackAndUnpack() throws ParseException {
		
		ParseObject pObj = testOrder.packObject();
		Order unPacked = new Order(pObj);
		
		assertEquals(testOrder.getObjId(), unPacked.getObjId());
		assertEquals(testOrder.getTableID(), unPacked.getTableID());
		assertEquals(testOrder.getClass(), unPacked.getClass());
		assertEquals(testOrder.getMenuItems(), unPacked.getMenuItems());
		assertEquals(testOrder.getOriginatingTime(), unPacked.getOriginatingTime());

	}


}
