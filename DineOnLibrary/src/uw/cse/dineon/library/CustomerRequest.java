/**
 * A CustomerRequest object to store requests from users such as "water refill"
 */
package uw.cse.dineon.library;

import com.parse.ParseObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Zach
 *
 */
public class CustomerRequest extends Storable implements Parcelable {
	public static final String DESCRIPTION = "description";
	public static final String USER = "user";
	
	private String description;
	private User user;
	
	/**
	 * A constructor that takes params for both fields.
	 * @param descrip string
	 * @param user User
	 */
	public CustomerRequest(String descrip, User user) {
		this.description = descrip;
		this.user = user;
	}
	
	/**
	 * Creates a CustomerRequest object from the given Parcel.
	 * 
	 * @param source Parcel of information in:
	 * 		String (description), User, String (objID).
	 */
	public CustomerRequest(Parcel source) {
		readFromParcel(source);
	}
	
	/**
	 * Helper to read from a Parcel.
	 * @param source Parcel to read from
	 */
	private void readFromParcel(Parcel source) {
		this.setDescription(source.readString());
		this.setUser((User)source.readParcelable(User.class.getClassLoader()));
		this.setObjId(source.readString());
		
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(description);
		dest.writeParcelable(user, flags);
		dest.writeString(this.getObjId());

	}

	/* (non-Javadoc)
	 * @see uw.cse.dineon.library.Storable#packObject()
	 */
	@Override
	public ParseObject packObject() {
		ParseObject pobj = new ParseObject(this.getClass().getSimpleName());
		pobj.add(CustomerRequest.DESCRIPTION, this.description);
		pobj.add(CustomerRequest.USER, this.user);
		//in case this storable is going to be used after the pack.
		this.setObjId(pobj.getObjectId());
				
		return pobj;
	}

	/* (non-Javadoc)
	 * @see uw.cse.dineon.library.Storable#unpackObject(com.parse.ParseObject)
	 */
	@Override
	public void unpackObject(ParseObject pobj) {
		this.setObjId(pobj.getObjectId());
		this.setDescription(pobj.getString(CustomerRequest.DESCRIPTION));
		this.setUser((User)pobj.get(CustomerRequest.USER));

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
