package uw.cse.dineon.library.test;

import uw.cse.dineon.library.MenuItem;
import android.test.AndroidTestCase;

/**
 * Tests the MenuItem library class
 * @author Zach
 *
 */
public class MenuItemTest extends AndroidTestCase {

	/**
	 * Asserts that the menu item has the correct fields stored
	 * 
	 * White box
	 */
	public void testMenuItemIntDoubleStringString() {
		MenuItem testMenuItem = new MenuItem(12, 13.53, "Fried Chicken", "Chicken...that's been fried");
		assertEquals(12, testMenuItem.getProductID());
		assertEquals(13.53, testMenuItem.getPrice());
		assertEquals("Fried Chicken", testMenuItem.getTitle());
		assertEquals("Chicken...that's been fried", testMenuItem.getDescription());
	}
	
	/**
	 * Asserts that setPrice correctly changes price
	 * 
	 * White box
	 */
	public void testSetPrice() {
		MenuItem testMenuItem = new MenuItem(12, 13.53, "Fried Chicken", "Chicken...that's been fried");
		testMenuItem.setPrice(3.2);
		assertEquals(3.2, testMenuItem.getPrice());
	}
	
}
