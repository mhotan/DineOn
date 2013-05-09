package uw.cse.dineon.library;

import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;

import com.parse.ParseObject;

/**
 * Reservation class that stores info for user reservations at restaurants.
 * @author Espeo196, mhotan
 */
public class Reservation extends TimeableStorable {
	
	// ID's used for easier parsing
	public static final String USER_INFO = "userInfo";
	public static final String REST_INFO = "restInfo";
//	public static final String START_TIME = "startTime";
//	public static final String CURR_SESSION = "currSession";
	
	private final UserInfo mUserInfo;			// info for user who made the reservation
	private final RestaurantInfo mRestInfo;	// info of restaurant reservation is at

	// TODO Confirm dining sessions are for start 
	// of a dining session not a reservation
	//	private DiningSession currSession;	// session for this reservation

	/**
	 * Creates a reservation at the associated date.
	 * @param userInfo user info to associate to reservation
	 * @param restInfo restaurant info to associate to reservation
	 * @param date Date of reservation
	 */
	public Reservation(UserInfo userInfo, RestaurantInfo restInfo, Date date) {
		super(Reservation.class, date);
		this.mUserInfo = userInfo;
		this.mRestInfo = restInfo;
	}
	
	/**
	 * Creates a Reservation object from the given Parcel.
	 * 
	 * @param po ParseObject 
	 */
	public Reservation(ParseObject po) {
		super(po);
		mUserInfo = new UserInfo(po.getParseObject(USER_INFO));
		mRestInfo = new RestaurantInfo(po.getParseObject(REST_INFO));
	}

	/**
	 * @return UserInfo
	 */
	public UserInfo getUserInfo() {
		return mUserInfo;
	}

//	/**
//	 * @param info UserInfo
//	 */
//	public void setUserInfo(UserInfo info) {
//		this.mUserInfo = info;
//	}

	/**
	 * @return RestaurantInfo
	 */
	public RestaurantInfo getRestInfo() {
		return mRestInfo;
	}

//	/**
//	 * @param info RestaurantInfo
//	 */
//	public void setRestInfo(RestaurantInfo info) {
//		this.mRestInfo = info;
//	}

	/**
	 * Packs this Reservation into a ParseObject to be stored.
	 * 
	 * @return ParseObject containing saved/packed data
	 */
	@Override
	public ParseObject packObject() {
		ParseObject po = super.packObject();
		po.put(Reservation.USER_INFO, mUserInfo.packObject());
		po.put(Reservation.REST_INFO, mRestInfo.packObject());
		return po;
	}
}


///**
//* Unpacks the given ParseObject into this Reservation setting
//* field values to the given data.
//* 
//* @param pobj ParseObject to be unpacked into a Reservation
//*/
//@Override
//public void unpackObject(ParseObject pobj) {
//	this.setObjId(pobj.getObjectId());
//	
//	UserInfo info = new UserInfo();
//	info.unpackObject((ParseObject) pobj.get(Reservation.USER_INFO));
//	this.setUserInfo(info);
//	
//	RestaurantInfo rInfo = new RestaurantInfo();
//	rInfo.unpackObject((ParseObject) pobj.getParseObject(Reservation.REST_INFO));
//	this.setRestInfo(rInfo);
//	
//	this.setStartTime((Time) pobj.get(Reservation.START_TIME));
//
//	DiningSession sess = new DiningSession();
//	sess.unpackObject((ParseObject) pobj.getParseObject(Reservation.CURR_SESSION));
//	this.setCurrSession(sess);
//}
//
//@Override
//public int describeContents() {
//	return 0;
//}
//
///**
//* Writes this Reservation to Parcel dest in the order:
//* UserInfo, RestaurantInfo, Time, DiningSession
//* to be retrieved at a later time.
//* 
//* @param dest Parcel to write Reservation data to.
//* @param flags int
//*/
//// NOTE: if you change the write order you must change the read order
//// below.
//@Override
//public void writeToParcel(Parcel dest, int flags) {
//	dest.writeParcelable(mUserInfo, flags);	
//	dest.writeParcelable(mRestInfo, flags);	
//	dest.writeString(startTime.toString()); // YYYYMMDDTHHMMSS format
//	dest.writeParcelable(currSession, flags);	
//}
//
///**
//* Helper method for updating Reservation with the data from a Parcel.
//* @param source Parcel containing data in the order:
//* 		UserInfo, RestaurantInfo, Time, DiningSession
//*/
//private void readFromParcel(Parcel source) {
//	source.readParcelable(UserInfo.class.getClassLoader());
//	source.readParcelable(RestaurantInfo.class.getClassLoader());
//	// TODO convert string to time?
//	source.readString(); // reads YYYYMMDDTHHMMSS format string
//	source.readParcelable(Reservation.class.getClassLoader());
//}
//
///**
//* Parcelable creator object of a Reservation.
//* Can create a Reservation from a Parcel.
//*/
//public static final Parcelable.Creator<Reservation> CREATOR = 
//		new Parcelable.Creator<Reservation>() {
//
//			@Override
//			public Reservation createFromParcel(Parcel source) {
//				return new Reservation(source);
//			}
//
//			@Override
//			public Reservation[] newArray(int size) {
//				return new Reservation[size];
//			}
//		};
