package uw.cse.dineon.library.test;

import com.parse.ParseException;
import com.parse.ParseObject;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.MenuItem;
import android.test.AndroidTestCase;

/**
 * Tests the MenuItem library class and makes sure that all
 * the fields are correctly set and returned.
 * 
 * @author Zach, glee23
 */
public class MenuItemTest extends AndroidTestCase {

	/**
	 * Default menu item. 
	 */
	private MenuItem testMenuItem;
	
	/**
	 * Asserts that the menu item has the correct fields stored. 
	 */
	public void testMenuItemIntDoubleStringString() {
		assertEquals(12, testMenuItem.getProductID());
		assertEquals(13.53, testMenuItem.getPrice());
		assertEquals("Fried Chicken", testMenuItem.getTitle());
		assertEquals("Chicken...that's been fried", testMenuItem.getDescription());
	}

	@Override
	protected void setUp() {
		testMenuItem = new MenuItem(12, 13.53, "Fried Chicken", "Chicken...that's been fried");
	}
	
	/**
	 * Asserts that a new menu item doesn't have an image.
	 */
	public void testGetImageInitial() {
		assertEquals(null, testMenuItem.getImage());
	}
	
	/**
	 * Asserts that setPrice correctly changes price.
	 */
	public void testSetPrice() {
		testMenuItem.setPrice(3.2);
		assertEquals(3.2, testMenuItem.getPrice());
	}

	/**
	 * Asserts that setProductID correctly sets the product id.
	 */
	public void testSetID() {
		testMenuItem.setProductID(11);
		assertEquals(11, testMenuItem.getProductID());
	}

	/**
	 * Asserts that getProductID correctly returns the product id.
	 */
	public void testGetID() {
		assertEquals(12, testMenuItem.getProductID());
	}

	/**
	 * Asserts that setPrice can't set a negative price.
	 * 
	 * Expected: IllegalArgumentException
	 */
	public void testSetNegativePrice() {
		try {
			testMenuItem.setPrice(-3.2);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;		
		}
	}

	/**
	 * Asserts that setDescription correctly sets the description.
	 */
	public void testSetDescription() {
		String testDescription = "Hello World";
		testMenuItem.setDescription(testDescription);
		assertEquals(testDescription, testMenuItem.getDescription());
	}

	/**
	 * Asserts that getDescription correctly returns the description.
	 */
	public void testGetDescription() {
		assertEquals("Chicken...that's been fried", testMenuItem.getDescription());
	}
	
	/**
	 * Asserts that the MenuItem stays the same when packed and
	 * unpacked.
	 */
	public void testPackAndUnpack() throws ParseException {
		
		ParseObject pObj = testMenuItem.packObject();
		MenuItem unPacked = new MenuItem(pObj);
		
		assertEquals(testMenuItem.getObjId(), unPacked.getObjId());
		assertEquals(testMenuItem.getPrice(), unPacked.getPrice());
		assertEquals(testMenuItem.getProductID(), unPacked.getProductID());
		assertEquals(testMenuItem.getTitle(), unPacked.getTitle());
		assertEquals(testMenuItem.getClass(), unPacked.getClass());
		assertEquals(testMenuItem.getImage(), unPacked.getImage());

	}
}
