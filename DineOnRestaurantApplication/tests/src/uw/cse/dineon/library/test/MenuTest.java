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
		testItems.add(testItem);
		testMenu = new Menu("beverages");
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPackObject() {
		//TODO fail("Not yet implemented");
	}

	public void testMenuString() {
		
		assertEquals("beverages", testMenu.getName());
		assertEquals(new ArrayList<MenuItem>(), testMenu.getItems());
	}

	public void testAddNewItem() {
		testMenu.addNewItem(testItem);
		assertEquals(testItems, testMenu.getItems());
	}

	public void testRemoveItem() {
		testMenu.addNewItem(testItem);
		testMenu.removeItem(testItem);
		assertEquals(new ArrayList<MenuItem>(), testMenu.getItems());
	}

}
