package uw.cse.dineon.library;

import java.util.List;

import android.os.Bundle;
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
	private int fbToken;
	private List<UserInfo> friendList;
	private DiningSession diningSession;

	/**
	 *
	 */
	public User() {
		super();
		// TODO
	}

	/**
	 * Creates a User object from the given Parcel.
	 * 
	 * @param source Parcel of information in:
	 * 		RestaurantInfos, UserInfo, Reservations, int, Strings, String.
	 */
	public User(Parcel source) {
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
		pobj.add(User.FAVS, this.favs);
		pobj.add(User.FB_TOKEN, this.fbToken);
		pobj.add(User.USER_INFO, this.userInfo);
		pobj.add(User.FRIEND_LIST, this.friendList);
		pobj.add(User.RESERVES, this.reserves);
		pobj.add(User.DINING_SESSION, this.diningSession);
		//in case this storable is going to be used after the pack.
		this.setObjId(pobj.getObjectId());

		return pobj;
	}

	@Override
	public void unpackObject(ParseObject pobj) {
		this.setObjId(pobj.getObjectId());
		this.setFavs((List<RestaurantInfo>) pobj.get(User.FAVS));
		this.setFbToken(pobj.getInt(User.FB_TOKEN));
		this.setReserves((List<Reservation>) pobj.get(User.RESERVES));
		this.setUserInfo((UserInfo) pobj.get(User.USER_INFO));
		this.setFriendList((List<UserInfo>) pobj.get(User.FRIEND_LIST));
		this.setDiningSession((DiningSession) pobj.get(User.DINING_SESSION));
	}


	/**
	 * A Parcel method to describe the contents of the object
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Write the object to a parcel object
	 * @param the Parcel to write to and any set flags
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(favs);
		dest.writeParcelable(userInfo, flags);
		dest.writeTypedList(reserves);
		dest.writeInt(fbToken);
		dest.writeTypedList(friendList);
		dest.writeString(this.getObjId());

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


	//read an object back out of parcel
	private void readFromParcel(Parcel source) {
		source.readTypedList(favs, RestaurantInfo.CREATOR);
		this.setUserInfo((UserInfo)source.readParcelable(UserInfo.class.getClassLoader()));
		source.readTypedList(reserves, Reservation.CREATOR);
		this.setFbToken(source.readInt());
		source.readTypedList(friendList, UserInfo.CREATOR);
		this.setObjId(source.readString());
		this.setDiningSession((DiningSession)source.
				readParcelable(DiningSession.class.getClassLoader()));
	}
}
