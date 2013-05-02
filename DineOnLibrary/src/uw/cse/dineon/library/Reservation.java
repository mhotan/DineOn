package uw.cse.dineon.library;

import com.parse.ParseObject;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;

/**
 * Reservation class that stores info for user reservations at restaurants.
 * @author Espeo196
 *
 */
public class Reservation extends Storable implements Parcelable {
	
	// ID's used for easier parsing
	public static final String USER_INFO = "userInfo";
	public static final String REST_INFO = "restInfo";
	public static final String START_TIME = "startTime";
	public static final String CURR_SESSION = "currSession";
	
	private UserInfo userInfo;			// info for user who made the reservation
	private RestaurantInfo restInfo;	// info of restaurant reservation is at
	private Time startTime;				// start time of the reservation
	private DiningSession currSession;	// session for this reservation

	/**
	 * Creates a new object to keep track of a reservation.
	 * 
	 * @param userInfo info of reserved user
	 * @param restInfo info for restaurant holding the reservation
	 * @param start Time of the reservation's start
	 * @param currSession DiningSession that the user is engaged in
	 */
	public Reservation(UserInfo userInfo, RestaurantInfo restInfo, Time start, 
				DiningSession currSession) {
		this.userInfo = userInfo;
		this.restInfo = restInfo;
		this.startTime = start;
		this.currSession = currSession;
	}

	/**
	 * Creates a Reservation object from the given Parcel.
	 * 
	 * @param source Parcel of information in:
	 * 		UserInfo, RestaurantInfo, Time, DiningSession
	 * 		order.
	 */
	public Reservation(Parcel source) {
		readFromParcel(source);
	}

	/**
	 * @return UserInfo
	 */
	public UserInfo getUserInfo() {
		return userInfo;
	}

	/**
	 * @param info UserInfo
	 */
	public void setUserInfo(UserInfo info) {
		this.userInfo = info;
	}

	/**
	 * @return RestaurantInfo
	 */
	public RestaurantInfo getRestInfo() {
		return restInfo;
	}

	/**
	 * @param info RestaurantInfo
	 */
	public void setRestInfo(RestaurantInfo info) {
		this.restInfo = info;
	}

	/**
	 * @return Time
	 */
	public Time getStartTime() {
		return startTime;
	}

	/**
	 * @param start Time
	 */
	public void setStartTime(Time start) {
		this.startTime = start;
	}

	/**
	 * @return DiningSession
	 */
	public DiningSession getCurrSession() {
		return currSession;
	}

	/**
	 * @param currSess DiningSession
	 */
	public void setCurrSession(DiningSession currSess) {
		this.currSession = currSess;
	}

	/**
	 * Packs this Reservation into a ParseObject to be stored.
	 * 
	 * @return ParseObject containing saved/packed data
	 */
	@SuppressLint("UseValueOf")
	@SuppressWarnings("static-access")
	@Override
	public ParseObject packObject() {
		ParseObject pobj = new ParseObject(this.getClass().getSimpleName());
		pobj.add(this.USER_INFO, this.userInfo);
		pobj.add(this.REST_INFO, this.restInfo);
		pobj.add(this.START_TIME, this.startTime);
		pobj.add(this.CURR_SESSION, this.currSession);

		//in case this storable is going to be used after the pack.
		this.setObjId(pobj.getObjectId());
		
		return pobj;
	}

	/**
	 * Unpacks the given ParseObject into this Reservation setting
	 * field values to the given data.
	 * 
	 * @param pobj ParseObject to be unpacked into a Reservation
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	@Override
	public void unpackObject(ParseObject pobj){
		this.setObjId(pobj.getObjectId());
		this.setUserInfo((UserInfo) pobj.get(this.USER_INFO));
		this.setRestInfo((RestaurantInfo) pobj.get(this.REST_INFO));
		this.setStartTime((Time) pobj.get(this.START_TIME));
		this.setCurrSession((DiningSession) pobj.get(this.CURR_SESSION));

	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	/**
	 * Writes this Reservation to Parcel dest in the order:
	 * UserInfo, RestaurantInfo, Time, DiningSession
	 * to be retrieved at a later time.
	 * 
	 * @param dest Parcel to write Reservation data to.
	 * @param flags int
	 */
	// NOTE: if you change the write order you must change the read order
	// below.
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(userInfo, flags);	
		dest.writeParcelable(restInfo, flags);	
		dest.writeString(startTime.toString()); // YYYYMMDDTHHMMSS format
		dest.writeParcelable(currSession, flags);	
	}
	
	/**
	 * Helper method for updating Reservation with the data from a Parcel.
	 * @param source Parcel containing data in the order:
	 * 		UserInfo, RestaurantInfo, Time, DiningSession
	 */
	private void readFromParcel(Parcel source) {
		source.readParcelable(UserInfo.class.getClassLoader());
		source.readParcelable(RestaurantInfo.class.getClassLoader());
		// TODO convert string to time?
		source.readString(); // reads YYYYMMDDTHHMMSS format string
		source.readParcelable(Reservation.class.getClassLoader());
	}
	
	/**
	 * Parcelable creator object of a Reservation.
	 * Can create a Reservation from a Parcel.
	 */
	public static final Parcelable.Creator<Reservation> CREATOR = 
			new Parcelable.Creator<Reservation>() {

				@Override
				public Reservation createFromParcel(Parcel source) {
					return new Reservation(source);
				}

				@Override
				public Reservation[] newArray(int size) {
					return new Reservation[size];
				}
			};
}
