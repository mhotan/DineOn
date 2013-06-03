package uw.cse.dineon.library.util;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import uw.cse.dineon.library.CurrentOrderItem;
import uw.cse.dineon.library.CustomerRequest;
import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Menu;
import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.Order;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.image.DineOnImage;
import uw.cse.dineon.library.image.ImageIO;

import android.content.res.Resources;

import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * 
 * @author mhotan
 *
 */
public final class TestUtility {
	
	static final int WAIT_TIME = 120000; // 2 minutes wait time
	
	/**
	 * Hidden constructor.
	 */
	private TestUtility() { }

	/**
	 * Returns a fake order.
	 * 
	 * @param orderNum int tableID of order
	 * @param info UserInfo that placed the order
	 * @return a fake order
	 */
	public static Order createFakeOrder(int orderNum, UserInfo info) {
		Order newOrder = new Order(orderNum, info, createFakeOrderItems(5));
		newOrder.setObjId("order");
		return newOrder;
	}

	/**
	 * Returns a fake customer request.
	 * 
	 * @param info UserInfo creating a request
	 * @return a fake customer request
	 */
	public static CustomerRequest createFakeRequest(UserInfo info) {
		CustomerRequest req = new CustomerRequest("[fake] I want my food now!", 
				info, new GregorianCalendar().getTime());
		req.setObjId("request");
		return req;
	}
	
	/**
	 * Returns a fake list of order items.
	 * 
	 * @param qty int quantity of items to return
	 * @return a fake list of order items
	 */
	public static List<CurrentOrderItem> createFakeOrderItems(int qty) {
		List<CurrentOrderItem> items = new ArrayList<CurrentOrderItem>();
		int i = 0;
		for (MenuItem m : createFakeMenuItems(qty)) {
			CurrentOrderItem newItem = new CurrentOrderItem(m);
			newItem.setObjId("orderitem" + i);
			items.add(newItem);
			i++;
		}
		return items;
	}

	/**
	 * Returns a fake list of menu items.
	 * 
	 * @param qty int quantity of items to return
	 * @return a fake list of menu items
	 */
	public static List<MenuItem> createFakeMenuItems(int qty) {
		qty = Math.max(0, qty);
		List<MenuItem> items = new ArrayList<MenuItem>();
		for (int i = 0; i < qty; ++i) {
			MenuItem m = new MenuItem(
					qty + 1 + i, 3.99, "FakeMenuItem " + (i + 1), 
					"FakeMenuItemDescription " + (i + 1));	
			m.setObjId("menuitem" + i);
			items.add(m);
		}
		return items;
	}

	/**
	 * Returns a fake Dining Session.
	 * 
	 * @param user UserInfo to create session for
	 * @param restInfo RestaurantInfo to create session at
	 * @return a fake DiningSession
	 * @throws ParseException if infos are null
	 */
	public static DiningSession createFakeDiningSession(UserInfo user, 
					RestaurantInfo restInfo) throws ParseException {
		DiningSession ds = new DiningSession(1, 
				new GregorianCalendar().getTime(), 
				user, restInfo);
		ds.setObjId("session");
		return ds;
	}
	
	/**
	 * @return A Menu representing a menu
	 */
	public static Menu createFakeMenu() {
		Menu entreeMenu = new Menu("testEntrees");
		for (MenuItem item: createFakeMenuItems(3)) {
			entreeMenu.addNewItem(item);
		}
		entreeMenu.setObjId("menu");
		return entreeMenu;
	}
	
	/**
	 * Create a fake user for testing.
	 * @return a DineOnUser
	 */
	public static DineOnUser createFakeUser() {
		// create a user
		ParseUser user = new ParseUser();
		user.setObjectId("_marksuser");
		user.setUsername("testUser");
		user.setPassword("12345");
		user.setEmail("testUser@testUser.com");
		return new DineOnUser(user);
	}
	
	/**
	 * Create a fake restaurant for testing.
	 * @return a Restaurant
	 * @throws ParseException if user is invalid
	 */
	public static Restaurant createFakeRestaurant() throws ParseException {
		ParseUser restUser = new ParseUser();
		restUser.setUsername("testRestUser");
		restUser.setPassword("12345");
		restUser.setObjectId("_marksrest");
		return new Restaurant(restUser);
	}
	
	/**
	 * From a resource and id create a test image.
	 * @param res Resources to grab the image
	 * @param resId Resource ID of the image
	 * @return null if unable to locate image with correct id from resources
	 */
	public static DineOnImage getFakeImage(Resources res, int resId) {
		DineOnImage image;
		try {
			image = new DineOnImage(ImageIO.loadBitmapFromResource(res, resId));
		} catch (ParseException e) {
			return null;
		}
		image.setObjId("test_" + resId);
		return image;
	}
	
	/**
	 * Creates a fake restaurant for the given user.
	 * @param u ParseUser to create a restaurant for
	 * @return the new fake restaurant that is not saved to the cloud
	 * @throws ParseException if there's an error with the given ParseUser (i.e. it's null)
	 */
	public static Restaurant createFakeRestaurant(ParseUser u) throws ParseException {
		return createFakeRestaurant();
	}
	
}
