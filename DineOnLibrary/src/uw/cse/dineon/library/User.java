package uw.cse.dineon.library;

import java.util.List;

import android.os.Bundle;

import com.parse.ParseObject;

/**
 * Basic User class.
 * @author Espeo196
 *
 */
public class User extends Storable {
	private List<RestaurantInfo> favs;
	private UserInfo userInfo;
	private List<Reservation> reserves;
	private int fbToken;
	private List<String> friendList;

	/**
	 *
	 */
	public User() {
		super();
		// TODO
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
	public List<String> getFriendList() {
		return friendList;
	}

	/**
	 *
	 * @param friends list of strings
	 */
	public void setFriendList(List<String> friends) {
		this.friendList = friends;
	}

	/**
	 * @return Bundle
	 */
	@Override
	public Bundle bundle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unbundle(Bundle b) {
		// TODO Auto-generated method stub
	}

	@Override
	public ParseObject packObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackObject(ParseObject pobj) {
		// TODO Auto-generated method stub
	}
}
