package uw.cse.dineon.library;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.util.ParseUtil;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

/**
 * Basic User class.
 * @author Espeo196
 *
 */
public class User extends Storable implements Parcelable {
	public static final String FAVS = "favs";
	public static final String USER_INFO = "userInfo";
	public static final String RESERVES = "reserves";
	public static final String FB_TOKEN = "fbToken";
	public static final String FRIEND_LIST = "friendList";
	public static final String DINING_SESSION = "diningSession";

	private List<RestaurantInfo> favs;
	private UserInfo userInfo;
	private List<Reservation> reserves;
	private int fbToken; //TODO Delete
	private List<UserInfo> friendList;
	private DiningSession diningSession;

	/**
	 * Default constructor.
	 */
	public User() {
		super();
		// TODO
		favs = new ArrayList<RestaurantInfo>();
		reserves = new ArrayList<Reservation>();
		friendList = new ArrayList<UserInfo>();
	}

	/**
	 * Creates a User object from the given Parcel.
	 * 
	 * @param source Parcel of information in:
	 * 		RestaurantInfos, UserInfo, Reservations, int, Strings, String.
	 */
	public User(Parcel source) {
		this();
		readFromParcel(source);
	}

	/**
	 *
	 * @param restInfo RestaurantInfo
	 */
	public void addFavorite(RestaurantInfo restInfo) {
	}

	/**
	 *
	 * @param restInfo RestaurantInfo
	 */
	public void removeFavorite(RestaurantInfo restInfo) {
	}

	/**
	 *
	 * @param res Reservation
	 */
	public void addReservation(Reservation res) {
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
		return favs;
	}

	/**
	 *
	 * @param favList list of restaurants
	 */
	public void setFavs(List<RestaurantInfo> favList) {
		this.favs = favList;
	}

	/**
	 *
	 * @return UserInfo
	 */
	public UserInfo getUserInfo() {
		return userInfo;
	}

	/**
	 *
	 * @param info UserInfo
	 */
	public void setUserInfo(UserInfo info) {
		this.userInfo = info;
	}

	/**
	 *
	 * @return List of reservations
	 */
	public List<Reservation> getReserves() {
		return reserves;
	}

	/**
	 *
	 * @param reservations list of reservations
	 */
	public void setReserves(List<Reservation> reservations) {
		this.reserves = reservations;
	}

	/**
	 *
	 * @return int
	 */
	public int getFbToken() {
		return fbToken;
	}

	/**
	 *
	 * @param fbTok int
	 */
	public void setFbToken(int fbTok) {
		this.fbToken = fbTok;
	}

	/**
	 *
	 * @return list of strings
	 */
	public List<UserInfo> getFriendList() {
		return friendList;
	}

	/**
	 *
	 * @param friends list of strings
	 */
	public void setFriendList(List<UserInfo> friends) {
		this.friendList = friends;
	}

	/**
	 *
	 * @return dining session
	 */
	public DiningSession getDiningSession() {
		return diningSession;
	}

	/**
	 *
	 * @param diningSession The specified dining session
	 */
	public void setDiningSession(DiningSession diningSession) {
		this.diningSession = diningSession;
	}


	@Override
	public ParseObject packObject() {
		ParseObject pobj = new ParseObject(this.getClass().getSimpleName());
		pobj.add(User.FAVS, ParseUtil.packListOfStorables(favs));
		pobj.add(User.FB_TOKEN, this.fbToken);
		pobj.add(User.USER_INFO, this.userInfo.packObject());
		pobj.add(User.FRIEND_LIST, ParseUtil.packListOfStorables(friendList));
		pobj.add(User.RESERVES, ParseUtil.packListOfStorables(reserves));
		if (this.diningSession != null) {
			pobj.add(User.DINING_SESSION, this.diningSession.packObject());
		} else {
			pobj.add(User.DINING_SESSION, null);
		}
		//in case this storable is going to be used after the pack.
		this.setObjId(pobj.getObjectId());

		return pobj;
	}

	@Override
	public void unpackObject(ParseObject pobj) {
		this.setObjId(pobj.getObjectId());
//		this.setFavs(ParseUtil.unpackListOfStorables(pobj.getParseObject(User.FAVS)));
		this.setFbToken(pobj.getInt(User.FB_TOKEN));
//		this.setReserves((List<Reservation>) pobj.get(User.RESERVES));
		UserInfo info = new UserInfo();
		info.unpackObject((ParseObject) pobj.get(User.USER_INFO));
		this.setUserInfo(info);
		
		// TODO FIX ME
		List<Storable> storable = 
				ParseUtil.unpackListOfStorables(pobj.getParseObject(User.FRIEND_LIST));
		List<UserInfo> friends = new ArrayList<UserInfo>(storable.size());
		for (Storable friend : storable) {
			friends.add((UserInfo) friend);
		}
		setFriendList(friends);
		
		storable = ParseUtil.unpackListOfStorables(pobj.getParseObject(User.RESERVES));
		List<Reservation> reservations = new ArrayList<Reservation>(storable.size());
		for (Storable reserve : storable) {
			reservations.add((Reservation) reserve);
		}
		setReserves(reservations);
		
		Object ds = pobj.get(User.DINING_SESSION);
		this.setDiningSession((DiningSession)ds);
	}


	@Override
	public int describeContents() {
		return 0;
	}

//	private List<RestaurantInfo> favs;
//	private UserInfo userInfo;
//	private List<Reservation> reserves;
//	private int fbToken; //TODO Delete
//	private List<UserInfo> friendList;
//	private DiningSession diningSession;
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.getObjId());
		dest.writeParcelable(userInfo, flags);
		dest.writeTypedList(favs);
		dest.writeTypedList(reserves);
		dest.writeTypedList(friendList);
		dest.writeParcelable(diningSession, flags);
		// TODO Make sure order consistency
		//		dest.writeTypedList(favs);
//		dest.writeParcelable(userInfo, flags);
//		dest.writeTypedList(reserves);
//		dest.writeInt(fbToken);
//		dest.writeTypedList(friendList);
//		dest.writeString(this.getObjId());

	}

	/**
	 * Parcelable creator object of a User.
	 * Can create a User from a Parcel.
	 */
	public static final Parcelable.Creator<User> CREATOR = 
			new Parcelable.Creator<User>() {

		@Override
		public User createFromParcel(Parcel source) {
			return new User(source);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};


	/**
	 * Read object out of Parcel.
	 * @param source to read from
	 */
	private void readFromParcel(Parcel source) {
		this.setObjId(source.readString());
		this.setUserInfo((UserInfo)source.readParcelable(UserInfo.class.getClassLoader()));
		source.readTypedList(favs, RestaurantInfo.CREATOR);
		source.readTypedList(reserves, Reservation.CREATOR);
		source.readTypedList(friendList, UserInfo.CREATOR);
		this.setDiningSession((DiningSession)source.readParcelable(
				DiningSession.class.getClassLoader()));
	}
}
