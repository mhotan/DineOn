package uw.cse.dineon.library;

import com.parse.ParseObject;

import android.os.Bundle;
import android.text.format.Time;

/**
 * Reservation class that stores info for user reservations at restaurants.
 * @author Espeo196
 *
 */
public class Reservation extends Storable {
	private UserInfo userInfo;
	private RestaurantInfo restInfo;
	private Time startTime;
//	private DiningSession currSession;

	/**
	 *
	 */
	public Reservation() {
		// TODO
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
	 * @return RestaurantInfo
	 */
	public RestaurantInfo getRestInfo() {
		return restInfo;
	}

	/**
	 *
	 * @param info RestaurantInfo
	 */
	public void setRestInfo(RestaurantInfo info) {
		this.restInfo = info;
	}

	/**
	 *
	 * @return Time
	 */
	public Time getStartTime() {
		return startTime;
	}

	/**
	 *
	 * @param start Time
	 */
	public void setStartTime(Time start) {
		this.startTime = start;
	}

	/**
	 *
	 * @return DiningSession
	 */
	public DiningSession getCurrSession() {
		return currSession;
	}

	/**
	 *
	 * @param currSess DiningSession
	 */
	public void setCurrSession(DiningSession currSess) {
		this.currSession = currSess;
	}

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
