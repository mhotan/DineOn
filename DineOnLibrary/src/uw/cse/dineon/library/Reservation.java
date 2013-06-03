package uw.cse.dineon.library;

import java.util.Date;

import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * Reservation class that stores info for user reservations at restaurants.
 * @author Espeo196, mhotan
 */
public class Reservation extends TimeableStorable {

	// ID's used for easier parsing
	public static final String USER_INFO = "userInfo";
	public static final String REST_INFO = "restInfo";

	private final UserInfo mUserInfo;			// info for user who made the reservation
	private final RestaurantInfo mRestInfo;	// info of restaurant reservation is at

	/**
	 * Creates a reservation at the associated date.
	 * @param userInfo user info to associate to reservation
	 * @param restInfo restaurant info to associate to reservation
	 * @param date Date of reservation
	 */
	public Reservation(UserInfo userInfo, RestaurantInfo restInfo, Date date) {
		super(Reservation.class, date);
		if(restInfo == null) {
			throw new IllegalArgumentException(
					"Can't create a reservation with a null RestaurantInfo.");
		} else if(userInfo == null) {
			throw new IllegalArgumentException(
					"Can't create a reservation with a null UserInfo.");		
		}
		
		this.mUserInfo = userInfo;
		this.mRestInfo = restInfo;
	}

	/**
	 * Creates a Reservation object from the given Parcel.
	 * 
	 * @param po ParseObject 
	 * @throws ParseException 
	 */
	public Reservation(ParseObject po) throws ParseException {
		super(po);
		mUserInfo = new UserInfo(po.getParseObject(USER_INFO));
		mRestInfo = new RestaurantInfo(po.getParseObject(REST_INFO));
		
		if(mRestInfo == null) {
			throw new IllegalArgumentException(
					"Can't create a reservation with a null RestaurantInfo.");
		} else if(mUserInfo == null) {
			throw new IllegalArgumentException(
					"Can't create a reservation with a null UserInfo.");		
		}
	}

	/**
	 * @return UserInfo
	 */
	public UserInfo getUserInfo() {
		return mUserInfo;
	}

	/**
	 * @return RestaurantInfo
	 */
	public RestaurantInfo getRestInfo() {
		return mRestInfo;
	}

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

//	/**
//	 * Writes this Reservation to Parcel dest in the order:
//	 * UserInfo, RestaurantInfo, Time, DiningSession
//	 * to be retrieved at a later time.
//	 * 
//	 * @param dest Parcel to write Reservation data to.
//	 * @param flags int
//	 */
//	// NOTE: if you change the write order you must change the read order
//	// below.
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		super.writeToParcel(dest, flags);
//		dest.writeParcelable(mUserInfo, flags);	
//		dest.writeParcelable(mRestInfo, flags);	
//	}
//
//	/**
//	 * Helper method for updating Reservation with the data from a Parcel.
//	 * @param source Parcel containing data in the order:
//	 * 		UserInfo, RestaurantInfo, Time, DiningSession
//	 */
//	protected Reservation(Parcel source) {
//		super(source);
//		mUserInfo = source.readParcelable(UserInfo.class.getClassLoader());
//		mRestInfo = source.readParcelable(RestaurantInfo.class.getClassLoader());
//	}
//
//	/**
//	 * Parcelable creator object of a Reservation.
//	 * Can create a Reservation from a Parcel.
//	 */
//	public static final Parcelable.Creator<Reservation> CREATOR = 
//			new Parcelable.Creator<Reservation>() {
//
//		@Override
//		public Reservation createFromParcel(Parcel source) {
//			return new Reservation(source);
//		}
//
//		@Override
//		public Reservation[] newArray(int size) {
//			return new Reservation[size];
//		}
//	};
}