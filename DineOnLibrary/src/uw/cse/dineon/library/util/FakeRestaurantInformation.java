package uw.cse.dineon.library.util;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.UserInfo;

import com.parse.ParseUser;

/**
 * FakeRestaurantInformation contains static methods to
 * return fake data for debugging purposes.
 * 
 * 
 * @author Garrett Lee
 *
 */
public final class FakeRestaurantInformation {

	/**
	 * To appease Checkstyle.
	 */
	private FakeRestaurantInformation() {

	}

	/**
	 * Returns a list of fake orders.
	 * 
	 * @param user the specified ParseUser
	 * @return a list of fake orders
	 */
	public static List<Order> getFakeOrders(ParseUser user) {
		List<Order> orders = new ArrayList<Order>();


		int[] firstOrderIndices = {0, 1, 2};
		int[] secondOrderIndices = {3, 4, 5};
		int[] thirdOrderIndices = {6, 7, 8};

		Order one = new Order(1, new UserInfo(user), 
				getFakeMenuItems(firstOrderIndices));
		Order two = new Order(2, new UserInfo(user), 
				getFakeMenuItems(secondOrderIndices));
		Order three = new Order(3, new UserInfo(user), 
				getFakeMenuItems(thirdOrderIndices));

		orders.add(one);
		orders.add(two);
		orders.add(three);

		return orders;
	}
	
	/**
	 * @return A Menu representing an Entree menu
	 */
	public static Menu getEntreeMenu() {
		Menu entreeMenu = new Menu("Entrees");
		for (MenuItem item: getFakeMenuItems()) {
			entreeMenu.addNewItem(item);
		}
		return entreeMenu;
	}

	/**
	 * Inorder to fabricate a restaurant there has to be certain fake 
	 * Menus.  One of my personal favorite menus is the Drink Menu.
	 * 
	 * @return a generic menu for drinks
	 */
	public static Menu getDrinkMenu() {
		Menu drinkMenu = new Menu("Drinks");
		for (MenuItem item: getDrinkMenuItems()) {
			drinkMenu.addNewItem(item);
		}
		return drinkMenu;
	}

	/**
	 * @return returns a list of Drink Items.
	 */
	public static List<MenuItem> getDrinkMenuItems() {
		List<MenuItem> drinks = new ArrayList<MenuItem>();
		int drinkCnt = 1;
		drinks.add(new MenuItem(drinkCnt++, 4.99, "Scotch Neat", "Age single malt scotch"));
		drinks.add(new MenuItem(drinkCnt++, 3.99, "Gin Martini", "Taste like pine needles"));
		drinks.add(new MenuItem(drinkCnt++, 4.99, "Kamikaze", "\"sophisticated\" " 
				+ "version of the original Kamikaze shooter that can cause quite a bit of damage"));
		drinks.add(new MenuItem(drinkCnt++, 3.99, "Manhattan", 
				"One of the finest and oldest cocktails"));
		drinks.add(new MenuItem(drinkCnt++, 3.99, "Margarita", 
				"Refreshing dring that will F you up"));
		// Add more here if you like
		return drinks;
	}

	/**
	 * Returns a list of fake menu items.
	 * 
	 * @return a list of fake menu items
	 */
	public static List<MenuItem> getFakeMenuItems() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		int itemCnt = 100;
		MenuItem steak = new MenuItem(itemCnt++, 12.99, "Steak", "A juicy hunk of meat.");
		MenuItem pasta = new MenuItem(itemCnt++, 9.99, "Fettuccine Alfredo", 
				"A creamy noodle dish served with broccoli and garlic bread.");
		MenuItem water = new MenuItem(itemCnt++, 0.00, "Water", 
				"Water from the tap.");
		MenuItem cheeseburger = new MenuItem(itemCnt++, 7.99, "Cheeseburger", 
				"A 1/4 pound burger with tillamook cheese.");
		MenuItem hamburger = new MenuItem(itemCnt++, 6.99, "Hamburger", 
				"A 1/4 pound burger.");
		MenuItem drink = new MenuItem(itemCnt++, 1.99, "Sprite", 
				"A fizzy lemon-lime soft drink.");
		MenuItem fries = new MenuItem(itemCnt++, 2.99, "Fries", "Fried potatoes.");
		MenuItem shake = new MenuItem(itemCnt++, 4.99, "Milkshake", 
				"Savory, drinkable blended ice cream.");

		MenuItem cockroaches = new MenuItem(itemCnt++, 4.50, "Hissing Cockroaches", 
				"Straight off of Fear Factor.");
		menuItems.add(steak);
		menuItems.add(pasta);
		menuItems.add(water);
		menuItems.add(cheeseburger);
		menuItems.add(hamburger);
		menuItems.add(drink);
		menuItems.add(fries);
		menuItems.add(shake);
		menuItems.add(cockroaches);
		return new ArrayList<MenuItem>(menuItems);
	}

	/**
	 * Returns a specific list of fake menu items.
	 * 
	 * @param indices an array of int indices representing
	 * the specified order items you want
	 * @return a list of fake menu items
	 */
	public static List<MenuItem> getFakeMenuItems(int[] indices) {
		List<MenuItem> menuItems = getFakeMenuItems();
		List<MenuItem> selectedItems = new ArrayList<MenuItem>();
		for (int i : indices) {
			if(i < menuItems.size() && i >= 0) {
				selectedItems.add(menuItems.get(i));
			}
		}

		return selectedItems;
	}

	/**
	 * Returns a specific list of fake requests.
	 * 
	 * @param user the specified ParseUser
	 * @return a list of fake requests
	 */
	public static List<CustomerRequest> getFakeRequests(ParseUser user) {
		List<CustomerRequest> requests = new ArrayList<CustomerRequest>();

		CustomerRequest one = new CustomerRequest("Water Refill", 
				new UserInfo(user), new GregorianCalendar().getTime());

		CustomerRequest two = new CustomerRequest("There is a fly in my soup.", 
				new UserInfo(user), new GregorianCalendar().getTime());

		CustomerRequest three = new CustomerRequest(
				"My glass fell on the ground and there's glass everywhere.", 
				new UserInfo(user), new GregorianCalendar().getTime());

		requests.add(one);
		requests.add(two);
		requests.add(three);

		return requests;
	}

}
