package uw.cse.dineon.library.test;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import android.test.AndroidTestCase;

/**
 * Tests for the library class Menu. This test class
 * makes sure all the fields are correctly set and 
 * returned.
 * 
 * White box tests
 * @author Zach, glee23
 *
 */
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
	 */
	public void testGetName() {
		assertEquals("beverages", testMenu.getName());
		assertEquals(new ArrayList<MenuItem>(), testMenu.getItems());
	}

	/**
	 * Asserts that the menu is empty to begin with
	 */
	public void testGetItemsInitial() {
		assertEquals(new ArrayList<MenuItem>(), testMenu.getItems());
	}
	
	/**
	 * Asserts that the menu correctly adds an item
	 */
	public void testAddNewItem() {
		testMenu.addNewItem(testItem);
		assertEquals(testItems, testMenu.getItems());
	}

	/**
	 * Asserts that the menu correctly removes an item
	 */
	public void testRemoveItem() {
		testMenu.addNewItem(testItem);
		testMenu.removeItem(testItem);
		assertEquals(new ArrayList<MenuItem>(), testMenu.getItems());
	}
	
	/**
	 * Asserts that the menu correctly reflects 
	 * that it has an item in its list.
	 */
	public void testHasMenuItem() {
		testMenu.addNewItem(testItem);
		assertTrue(testMenu.hasMenuItem(testItem));
	}
	
	/**
	 * Asserts that the menu correctly reflects 
	 * that it doesn't have an item not in its list.
	 */
	public void testDoesntHaveMenuItem() {
		assertFalse(testMenu.hasMenuItem(testItem));
	}

}
