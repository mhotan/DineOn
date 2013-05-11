package uw.cse.dineon.library;

import java.util.ArrayList;
import java.util.List;

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
	private List<RestaurantInfo> mFavRestaurants;

	/**
	 * List of current pending reservations.
	 */
	private List<Reservation> mReservations;

	/**
	 * Current List of friends.
	 */
	private List<UserInfo> mFriendsLists;

	/**
	 * Information associated with the User.
	 */
	private UserInfo mUserInfo;

	/**
	 * This is the dining session the team is currently involved in.
	 */
	private DiningSession mDiningSession;

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
		// Dining sessions are not instantiated until the user begins a dining session
	}

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
		//TODO Unpack All the list of storables
		mReservations = ParseUtil.toListOfStorables(
				Reservation.class, po.getList(RESERVATIONS)); 
		mFriendsLists = ParseUtil.toListOfStorables(
				UserInfo.class, po.getList(FRIEND_LIST)); 
		ParseObject currDiningSession = po.getParseObject(DINING_SESSION);
		if (currDiningSession != null) {
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
	 * @param favList list of restaurants
	 */
	public void setFavs(List<RestaurantInfo> favList) {
		this.mFavRestaurants = favList;
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
	 * @param info UserInfo
	 */
	public void setUserInfo(UserInfo info) {
		this.mUserInfo = info;
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
	 * @param reservations list of reservations
	 */
	public void setReserves(List<Reservation> reservations) {
		this.mReservations = reservations;
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
	 * @param friends list of strings
	 */
	public void setFriendList(List<UserInfo> friends) {
		this.mFriendsLists = friends;
	}

	/**
	 *
	 * @return dining session
	 */
	public DiningSession getDiningSession() {
		return mDiningSession;
	}

	/**
	 *
	 * @param diningSession The specified dining session
	 */
	public void setDiningSession(DiningSession diningSession) {
		this.mDiningSession = diningSession;
	}

	/**
	 * @return String name of User
	 */
	public String getName() {
		return mUserInfo.getName();
	}




	//	@Override
	//	public void unpackObject(ParseObject pobj) {
	//		this.setObjId(pobj.getObjectId());
	//		//		this.setFavs(ParseUtil.unpackListOfStorables(pobj.getParseObject(User.FAVS)));
	//		this.setFbToken(pobj.getInt(User.FB_TOKEN));
	//		//		this.setReserves((List<Reservation>) pobj.get(User.RESERVES));
	//		UserInfo info = new UserInfo();
	//		info.unpackObject((ParseObject) pobj.get(User.USER_INFO));
	//		this.setUserInfo(info);
	//
	//		// TODO FIX ME
	//		List<Storable> storable = 
	//				ParseUtil.unpackListOfStorables(pobj.getParseObject(User.FRIEND_LIST));
	//		List<UserInfo> friends = new ArrayList<UserInfo>(storable.size());
	//		for (Storable friend : storable) {
	//			friends.add((UserInfo) friend);
	//		}
	//		setFriendList(friends);
	//
	//		storable = ParseUtil.unpackListOfStorables(pobj.getParseObject(User.RESERVATIONS));
	//		List<Reservation> reservations = new ArrayList<Reservation>(storable.size());
	//		for (Storable reserve : storable) {
	//			reservations.add((Reservation) reserve);
	//		}
	//		setReserves(reservations);
	//
	//		Object ds = pobj.get(User.DINING_SESSION);
	//		this.setDiningSession((DiningSession)ds);
	//	}
	//
	//
	//	@Override
	//	public int describeContents() {
	//		return 0;
	//	}

	//	private List<RestaurantInfo> favs;
	//	private UserInfo userInfo;
	//	private List<Reservation> reserves;
	//	private int fbToken; //TODO Delete
	//	private List<UserInfo> friendList;
	//	private DiningSession diningSession;

	//	@Override
	//	public void writeToParcel(Parcel dest, int flags) {
	//		dest.writeString(this.getObjId());
	//		dest.writeParcelable(mUserInfo, flags);
	//		dest.writeTypedList(mFavRestaurants);
	//		dest.writeTypedList(mReservations);
	//		dest.writeTypedList(mFriendsLists);
	//		dest.writeParcelable(mDiningSession, flags);
	//		// TODO Make sure order consistency
	//		//		dest.writeTypedList(favs);
	////		dest.writeParcelable(userInfo, flags);
	////		dest.writeTypedList(reserves);
	////		dest.writeInt(fbToken);
	////		dest.writeTypedList(friendList);
	////		dest.writeString(this.getObjId());
	//
	//	}
	//
	//	/**
	//	 * Parcelable creator object of a User.
	//	 * Can create a User from a Parcel.
	//	 */
	//	public static final Parcelable.Creator<User> CREATOR = 
	//			new Parcelable.Creator<User>() {
	//
	//		@Override
	//		public User createFromParcel(Parcel source) {
	//			return new User(source);
	//		}
	//
	//		@Override
	//		public User[] newArray(int size) {
	//			return new User[size];
	//		}
	//	};
	//
	//
	//	/**
	//	 * Read object out of Parcel.
	//	 * @param source to read from
	//	 */
	//	private void readFromParcel(Parcel source) {
	//		this.setObjId(source.readString());
	//		this.setUserInfo((UserInfo)source.readParcelable(UserInfo.class.getClassLoader()));
	//		source.readTypedList(mFavRestaurants, RestaurantInfo.CREATOR);
	//		source.readTypedList(mReservations, Reservation.CREATOR);
	//		source.readTypedList(mFriendsLists, UserInfo.CREATOR);
	//		this.setDiningSession((DiningSession)source.readParcelable(
	//				DiningSession.class.getClassLoader()));
	//	}
}
