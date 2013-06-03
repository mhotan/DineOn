package uw.cse.dineon.library.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uw.cse.dineon.library.DineOnUser;
import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.library.Reservation;
import uw.cse.dineon.library.Restaurant;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.UserInfo;
import uw.cse.dineon.library.util.TestUtility;
import android.test.AndroidTestCase;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Tests library class DineOnUser.
 * Makes sure all the fields are correctly
 * stored and returned.
 * 
 * White box tests
 * @author glee23
 *
 */
public class DineOnUserTest extends AndroidTestCase {

	private ParseUser mUser;
	private DineOnUser dUser;
	private Restaurant mRestaurant;
	
	protected void setUp() throws Exception {
		Parse.initialize(this.getContext(), "RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", "wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");

		mUser = new ParseUser();
		mUser.setUsername("name");
		dUser = new DineOnUser(mUser);
		mRestaurant = TestUtility.createFakeRestaurant();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * Asserts that a new DineOnUser
	 * is created with a set ParseUser
	 * and blank other fields.
	 */
	public void testParseUserConstructor() {
		assertEquals(new ArrayList<RestaurantInfo>(), dUser.getFavs());
		assertEquals(new ArrayList<UserInfo>(), dUser.getFriendList());
		assertEquals(new ArrayList<Reservation>(), dUser.getReserves());
		assertEquals("name", dUser.getName());
		assertEquals(null, dUser.getDiningSession());
	}
	
	/**
	 * Asserts that DineOnUser
	 * correctly adds a reservation.
	 */
	public void testAddReservation() throws ParseException {
		Reservation res = new Reservation(dUser.getUserInfo(), 
				mRestaurant.getInfo(), getDate());
		dUser.addReservation(res);
		List<Reservation> reservations = new ArrayList<Reservation>();
		reservations.add(res);
		assertEquals(1, dUser.getReserves().size());
	}
	
	/**
	 * Asserts that DineOnUser
	 * correctly retrieves reservations.
	 */
	public void testGetReservations() throws ParseException {
		assertEquals(0, dUser.getReserves().size());
		Reservation res = new Reservation(dUser.getUserInfo(), 
				mRestaurant.getInfo(), getDate());
		dUser.addReservation(res);
		List<Reservation> reservations = new ArrayList<Reservation>();
		reservations.add(res);
		assertEquals(1, dUser.getReserves().size());
	}
	
	/**
	 * Asserts that DineOnUser
	 * correctly adds a favorite restaurant.
	 */
	public void testAddFavorite() {
		RestaurantInfo mRI = mRestaurant.getInfo();
		dUser.addFavorite(mRI);
		List<RestaurantInfo> favs = new ArrayList<RestaurantInfo>();
		favs.add(mRI);
		assertEquals(1, dUser.getFavs().size());
		assertEquals(mRI.getName(), dUser.getFavs().get(0).getName());
	}
	
	/**
	 * Asserts that DineOnUser
	 * correctly retrieves favorite restaurants.
	 */
	public void testGetFavorites() {
		assertEquals(0, dUser.getFavs().size());
		RestaurantInfo mRI = mRestaurant.getInfo();
		dUser.addFavorite(mRI);
		List<RestaurantInfo> favs = new ArrayList<RestaurantInfo>();
		favs.add(mRI);
		assertEquals(1, dUser.getFavs().size());
		assertEquals(mRI.getName(), dUser.getFavs().get(0).getName());
	}
	
	/**
	 * Asserts that DineOnUser
	 * correctly retrieves its user info.
	 */
	public void testGetUserInfo() {
		UserInfo testUserInfo = new UserInfo(mUser);
		assertEquals(testUserInfo.getName(), dUser.getUserInfo().getName());
		assertEquals(testUserInfo.getEmail(), dUser.getUserInfo().getEmail());
		assertEquals(testUserInfo.getPhone(), dUser.getUserInfo().getPhone());
	}
	
	/**
	 * Asserts that DineOnUser
	 * correctly saves its specified dining session.
	 */
	public void testSetDiningSession() throws ParseException {
		DiningSession d = TestUtility.createFakeDiningSession(
				dUser.getUserInfo(), mRestaurant.getInfo());
		dUser.setDiningSession(d);
		assertEquals(d, dUser.getDiningSession());
	}
	
	/**
	 * Asserts that DineOnUser
	 * correctly returns its dining session.
	 */
	public void testGetDiningSession() throws ParseException {
		assertEquals(null, dUser.getDiningSession());
		DiningSession d = TestUtility.createFakeDiningSession(
				dUser.getUserInfo(), mRestaurant.getInfo());
		dUser.setDiningSession(d);
		assertEquals(d, dUser.getDiningSession());
	}
	
	/**
	 * Asserts that the DineOnUser stays the same when packed and
	 * unpacked.
	 */
	public void testPackAndUnpack() throws ParseException {
		
		ParseObject pObj = dUser.packObject();
		DineOnUser unPacked = new DineOnUser(pObj);
		assertEquals(dUser.getName(), unPacked.getName());
		assertEquals(dUser.getObjId(), unPacked.getObjId());
		assertEquals(dUser.getDiningSession(), unPacked.getDiningSession());
		assertEquals(dUser.getFavs(), unPacked.getFavs());
		assertEquals(dUser.getFriendList(), unPacked.getFriendList());
		assertEquals(dUser.getReserves(), unPacked.getReserves());
		assertEquals(dUser.getClass(), unPacked.getClass());
	}
	
	/**
	 * Returns the current date.
	 * @return the current date
	 */
	private Date getDate() {
		Calendar c = Calendar.getInstance();
		return c.getTime();
	}
	

}
