/**
 * A CustomerRequest object to store requests from users such as "water refill"
 */
package uw.cse.dineon.library;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * This class represents a physical customer request.
 * IE : 
 *   -"Waiter water please?"
 *   -"Whats the bartenders phone number?"
 * 
 * @author Zach, mhotan
 */
public class CustomerRequest extends TimeableStorable {
	public static final String DESCRIPTION = "requestDescription";
	public static final String USER = "dineonCustomer";

	private final String mDescription;
	private final UserInfo mUserInfo;
	private String mWaiter;
	
	/**
	 * 
	 * @param description description of the customer request
	 * @param info User infor associated to this request
	 * @param date Time that the request was placed
	 */
	public CustomerRequest(String description, UserInfo info, Date date) {
		super(CustomerRequest.class, date);
		this.mDescription = description;
		this.mUserInfo = info; 
	}

	/**
	 * A constructor that takes params for both fields.
	 * Sets this Customer Request time to originate at cosntruction time
	 * @param description Description of the specific request
	 * @param info User information
	 */
	public CustomerRequest(String description, UserInfo info) {	
		this(description, info, null); // null mean
	}

	/**
	 * Creates a Customer request shell of the parse object
	 * This should always be used when downloading from the ParseCloud.
	 * @param parseObject ParseObject
	 * @throws ParseException 
	 */
	public CustomerRequest(ParseObject parseObject) throws ParseException {
		super(parseObject);
		mDescription = parseObject.getString(DESCRIPTION);
		mUserInfo = new UserInfo(parseObject.getParseObject(USER));
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return mDescription;
	}

	/**
	 * @return the user
	 */
	public UserInfo getUserInfo() {
		return mUserInfo;
	}

	/**
	 * @param waiter to assign this request to
	 */
	public void setWaiter(String waiter) {
		mWaiter = waiter;
	}

	/**
	 * @return String waiter this request is assigned to
	 */
	public String getWaiter() {
		return mWaiter;
	}
	
	/**
	 * Inserts this objects fields and returns the ParseObject
	 * representation.
	 * @return ParseObject that represents this.
	 */
	@Override
	public ParseObject packObject() {
		ParseObject po = super.packObject();
		po.put(DESCRIPTION, mDescription);
		po.put(USER, mUserInfo.packObject());
		return po;
	}

	/**
	 * Creates a CustomerRequest object from the given Parcel.
	 * 
	 * @param source Parcel of information in:
	 * 		String (description), User, String (objID).
	 */
	protected CustomerRequest(Parcel source) {
		super(source);
		mDescription = source.readString();
		mUserInfo = source.readParcelable(UserInfo.class.getClassLoader());
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(mDescription);
		dest.writeParcelable(mUserInfo, flags);
	}

	/**
	 * Parcelable creator object of a CustomerRequest.
	 * Can create a CustomerRequest from a Parcel.
	 */
	public static final Parcelable.Creator<CustomerRequest> CREATOR = 
			new Parcelable.Creator<CustomerRequest>() {

		@Override
		public CustomerRequest createFromParcel(Parcel source) {
			return new CustomerRequest(source);
		}

		@Override
		public CustomerRequest[] newArray(int size) {
			return new CustomerRequest[size];
		}
	};

}
