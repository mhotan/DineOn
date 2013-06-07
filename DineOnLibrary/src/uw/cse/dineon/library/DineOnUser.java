package uw.cse.dineon.library;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import uw.cse.dineon.library.util.ParseUtil;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Basic User class.
 * @author Espeo196
 *
 */
public class DineOnUser extends Storable {
	public static final String FAVORITE_RESTAURANTS = "favs";
	public static final String USER_INFO = "userInfo";
	public static final String RESERVATIONS = "reserves";
	public static final String FB_TOKEN = "fbToken";
	public static final String FRIEND_LIST = "friendList";
	public static final String DINING_SESSION = "diningSession";

	/**
	 * Current list of Restaurant Information. 
	 */
	private final List<RestaurantInfo> mFavRestaurants;

	/**
	 * List of current pending reservations.
	 */
	private final List<Reservation> mReservations;

	/**
	 * Current List of friends.
	 */
	private final List<UserInfo> mFriendsLists;

	/**
	 * Information associated with the User.
	 */
	private final UserInfo mUserInfo;

	/**
	 * This is the dining session the team is currently involved in.
	 */
	private DiningSession mDiningSession;
	
	/**
	 * This is a order instance that only lives through the lifetime of the 
	 * application.  
	 * 
	 * There can only exist a pending order if there exists a dining session
	 * 	In other words
	 * 		mPendingOrder == null if and only if mDiningSession == null.
	 * 	therfore 
	 * 		mPendingOrder != null if and only if mDiningSession != null
	 */
	private Order mPendingOrder;

	/**
	 * Constructs a DineOnUser from a ParseUser.
	 * @param user to get data from.
	 */
	public DineOnUser(ParseUser user) {
		super(DineOnUser.class); 
		mUserInfo = new UserInfo(user);
		mFavRestaurants = new ArrayList<RestaurantInfo>();
		mReservations = new ArrayList<Reservation>();
		mFriendsLists = new ArrayList<UserInfo>();
		mDiningSession = null;
		mPendingOrder = null;
	}

	private static final String EMPTY_DS = "NULL";
	
	/**
	 * Creates a user from a parse object.
	 * @param po Parse Object to use to build
	 * @throws ParseException 
	 */
	public DineOnUser(ParseObject po) throws ParseException {
		super(po);
		mUserInfo = new UserInfo(po.getParseObject(USER_INFO));
		mFavRestaurants = ParseUtil.toListOfStorables(
				RestaurantInfo.class, po.getList(FAVORITE_RESTAURANTS)); 
		mReservations = ParseUtil.toListOfStorables(
				Reservation.class, po.getList(RESERVATIONS)); 
		mFriendsLists = ParseUtil.toListOfStorables(
				UserInfo.class, po.getList(FRIEND_LIST)); 
		ParseObject currDiningSession = po.getParseObject(DINING_SESSION);
		if (currDiningSession != null && !EMPTY_DS.equals(currDiningSession.getObjectId())) {
			mDiningSession = new DiningSession(currDiningSession);
		} 
	}



	@Override
	public ParseObject packObject() {
		ParseObject pobj = super.packObject();
		pobj.put(USER_INFO, (ParseObject)mUserInfo.packObject());
		pobj.put(FAVORITE_RESTAURANTS, ParseUtil.toListOfParseObjects(mFavRestaurants));
		pobj.put(RESERVATIONS, ParseUtil.toListOfParseObjects(mReservations));
		pobj.put(FRIEND_LIST, ParseUtil.toListOfParseObjects(mFriendsLists));
		if (mDiningSession != null) {
			pobj.put(DineOnUser.DINING_SESSION, this.mDiningSession.packObject());			
		} else {
			pobj.put(DineOnUser.DINING_SESSION, JSONObject.NULL);
		}

		return pobj;
	}


	/**
	 * @param restInfo RestaurantInfo to add if not null
	 */
	public void addFavorite(RestaurantInfo restInfo) {
		if (restInfo != null) {
			mFavRestaurants.add(restInfo);
		}
	}

	/**
	 * @param restInfo RestaurantInfo to remove
	 */
	public void removeFavorite(RestaurantInfo restInfo) {
		mFavRestaurants.remove(restInfo);
	}

	/**
	 * @param res Reservation
	 */
	public void addReservation(Reservation res) {
		if (res != null) {
			mReservations.add(res);
		}
	}

	/**
	 *
	 * @param res Reservation
	 */
	public void removeReservation(Reservation res) {
	}

	/**
	 *
	 * @return List of Restaurants
	 */
	public List<RestaurantInfo> getFavs() {
		return mFavRestaurants;
	}

	/**
	 *
	 * @return UserInfo
	 */
	public UserInfo getUserInfo() {
		return mUserInfo;
	}

	/**
	 *
	 * @return List of reservations
	 */
	public List<Reservation> getReserves() {
		return mReservations;
	}

	/**
	 *
	 * @return list of strings
	 */
	public List<UserInfo> getFriendList() {
		return mFriendsLists;
	}

	/**
	 *
	 * @return dining session
	 */
	public DiningSession getDiningSession() {
		return mDiningSession;
	}
	
	/**
	 * @return true if there is a non empty pending order
	 */
	public boolean hasPendingOrder() {
		return mPendingOrder != null && !mPendingOrder.isEmpty();
	}

	/**
	 * Sets the current dining session for this user.
	 * @param diningSession The specified dining session
	 */
	public void setDiningSession(DiningSession diningSession) {
		this.mDiningSession = diningSession;
		
		// Maintain our invariant
		// No dining session means no pending order
		if (mDiningSession == null) {
			mPendingOrder = null;
		} else {
			// Create an empty order to add to later
			mPendingOrder = new Order(mDiningSession.getTableID(), mUserInfo);
		} 
	}

	/**
	 * Sets the current menu item quantity in the current pending
	 * order if it exists.  If quantity is non positive then
	 * the menu item is removed.
	 * 
	 * @param item Item to set quantity to.
	 * @param qty Quantity to set order to
	 * @return true if pending order exist false otherwise
	 */
	public boolean setMenuItemToOrder(MenuItem item, int qty) {
		if (mPendingOrder == null) {
			return false;
		}
		mPendingOrder.setItemQuantity(item, qty);
		return true;
	}
	
	/**
	 * 
	 * @param item item to remove from the order
	 * @return true if pending order exist false otherwise
	 */
	public boolean removeItemFormOrder(MenuItem item) {
		if (mPendingOrder == null) {
			return false;
		}
		mPendingOrder.setItemQuantity(item, 0);
		return true;
	}
	
	/**
	 * @return String name of User
	 */
	public String getName() {
		return mUserInfo.getName();
	}

	@Override
	public void deleteFromCloud() {
		for (Reservation res: mReservations) {
			res.deleteFromCloud();
		}
		mUserInfo.deleteFromCloud();
		if (mDiningSession != null) {
			mDiningSession.deleteFromCloud();
		}
	}
	
	/**
	 * 
	 * @param ri RestaurantInfo
	 * @return true if restaurant is a favorite of the user
	 */
	public boolean isFavorite(RestaurantInfo ri) {
		return this.mFavRestaurants.contains(ri);
	}
}
