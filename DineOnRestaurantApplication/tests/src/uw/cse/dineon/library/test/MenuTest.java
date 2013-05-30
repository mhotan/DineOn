package uw.cse.dineon.library.test;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import android.test.AndroidTestCase;

public class MenuTest extends AndroidTestCase {

	List<MenuItem> testItems;
	MenuItem testItem;
	Menu testMenu;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testItems = new ArrayList<MenuItem>();
		testItem = new MenuItem(24, 4.5, "Root Beer Float", "Ice cream and root beer");
		testItem.setObjId("ti");
		testItems.add(testItem);
		testMenu = new Menu("beverages");
		testMenu.setObjId("tm");
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Asserts that the menu saves the correct title
	 * 
	 * White box
	 */
	public void testMenuString() {
		
		assertEquals("beverages", testMenu.getName());
		assertEquals(new ArrayList<MenuItem>(), testMenu.getItems());
	}

	/**
	 * Asserts that the menu correctly adds an item
	 * 
	 * White box
	 */
	public void testAddNewItem() {
		testMenu.addNewItem(testItem);
		assertEquals(testItems, testMenu.getItems());
	}

	/**
	 * Asserts that the menu correctly removes an item
	 * 
	 * White box
	 */
	public void testRemoveItem() {
		testMenu.addNewItem(testItem);
		testMenu.removeItem(testItem);
		assertEquals(new ArrayList<MenuItem>(), testMenu.getItems());
	}

}
