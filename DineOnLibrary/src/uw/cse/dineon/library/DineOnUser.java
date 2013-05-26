package uw.cse.dineon.library;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import uw.cse.dineon.library.util.ParseUtil;
import android.os.Parcel;
import android.os.Parcelable;

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
		// Dining sessions are not instantiated until the user begins a dining session
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
		//TODO Unpack All the list of storables
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

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeTypedList(mFavRestaurants);
		dest.writeTypedList(mReservations);
		dest.writeTypedList(mFriendsLists);
		dest.writeParcelable(mUserInfo, flags);

		// have to do some logic for checking if there is a dining session.
		if (mDiningSession != null) {
			dest.writeByte((byte) 1);
			dest.writeParcelable(mDiningSession, flags);
		} else {
			dest.writeByte((byte)0);
		}
	}
	
	/**
	 * Creates a Dine On User from a Parcel Source.
	 * @param source source to create user
	 */
	protected DineOnUser(Parcel source) {
		super(source);
		mFavRestaurants = new ArrayList<RestaurantInfo>();
		mReservations = new ArrayList<Reservation>();
		mFriendsLists = new ArrayList<UserInfo>();
		
		// Read in each list independently
		source.readTypedList(mFavRestaurants, RestaurantInfo.CREATOR);
		source.readTypedList(mReservations, Reservation.CREATOR);
		source.readTypedList(mFriendsLists, UserInfo.CREATOR);

		// Read my user info
		mUserInfo = source.readParcelable(UserInfo.class.getClassLoader());
		
		if (source.readByte() == 1) { // If there is a current dining session
			mDiningSession = source.readParcelable(DiningSession.class.getClassLoader());
		}
	}

	/**
	 * Parcelable creator object of a User.
	 * Can create a User from a Parcel.
	 */
	public static final Parcelable.Creator<DineOnUser> CREATOR = 
			new Parcelable.Creator<DineOnUser>() {

		@Override
		public DineOnUser createFromParcel(Parcel source) {
			return new DineOnUser(source);
		}

		@Override
		public DineOnUser[] newArray(int size) {
			return new DineOnUser[size];
		}
	};

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
	
	public boolean isFavorite(RestaurantInfo ri){
		return this.mFavRestaurants.contains(ri);
	}
}
