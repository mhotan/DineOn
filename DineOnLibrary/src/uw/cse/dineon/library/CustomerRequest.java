/**
 * A CustomerRequest object to store requests from users such as "water refill"
 */
package uw.cse.dineon.library;

import java.util.Date;
import java.util.List;

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
	public static final String DESCRIPTION = "description";
	public static final String USER = "user";
	
	private final String mDescription;
	private final UserInfo mUserInfo;
	
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
	 * @param parseObject
	 */
	public CustomerRequest(ParseObject parseObject) {
		super(parseObject);
		mDescription = parseObject.getString(DESCRIPTION);
		mUserInfo = new UserInfo(parseObject.getParseObject(USER));
	}
	
	/**
	 * Creates a CustomerRequest object from the given Parcel.
	 * 
	 * @param source Parcel of information in:
	 * 		String (description), User, String (objID).
	 */
//	public CustomerRequest(Parcel source) {
//		readFromParcel(source);
//	}
	
//	/**
//	 * Helper to read from a Parcel.
//	 * @param source Parcel to read from
//	 */
//	private void readFromParcel(Parcel source) {
//		this.setDescription(source.readString());
//		this.setUser((User)source.readParcelable(User.class.getClassLoader()));
//		this.setObjId(source.readString());
//	}

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
	 * Inserts this objects fields and returns the ParseObject
	 * representation.
	 * @return ParseObject that represents this.
	 */
	public ParseObject packObject(){
		ParseObject po = super.packObject();
		po.put(DESCRIPTION, mDescription);
		po.put(USER, mUserInfo);
		return po;
	}
	
	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
//	@Override
//	public int describeContents() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeString(mDescription);
//		dest.writeParcelable(mUser, flags);
//		dest.writeString(this.getObjId());
//	}

//	/* (non-Javadoc)
//	 * @see uw.cse.dineon.library.Storable#packObject()
//	 */
//	@Override
//	public ParseObject packObject() {
//		ParseObject pobj = new ParseObject(this.getClass().getSimpleName());
//		pobj.add(CustomerRequest.DESCRIPTION, this.description);
//		pobj.add(CustomerRequest.USER, this.user);
//		//in case this storable is going to be used after the pack.
//		this.setObjId(pobj.getObjectId());
//				
//		return pobj;
//	}
//
//	/* (non-Javadoc)
//	 * @see uw.cse.dineon.library.Storable#unpackObject(com.parse.ParseObject)
//	 */
//	@Override
//	public void unpackObject(ParseObject pobj) {
//		this.setObjId(pobj.getObjectId());
//		this.setDescription(pobj.getString(CustomerRequest.DESCRIPTION));
//		this.setUser((User)pobj.get(CustomerRequest.USER));
//
//	}
//	
//	/**
//	 * Parcelable creator object of a CustomerRequest.
//	 * Can create a CustomerRequest from a Parcel.
//	 */
//	public static final Parcelable.Creator<CustomerRequest> CREATOR = 
//			new Parcelable.Creator<CustomerRequest>() {
//
//				@Override
//				public CustomerRequest createFromParcel(Parcel source) {
//					return new CustomerRequest(source);
//				}
//
//				@Override
//				public CustomerRequest[] newArray(int size) {
//					return new CustomerRequest[size];
//				}
//	};

}
