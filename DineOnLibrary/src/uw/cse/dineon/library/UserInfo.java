package uw.cse.dineon.library;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

/**
 * @author Espeo196
 *
 */
public class UserInfo extends Storable implements Parcelable {
	public static final String NAME = "name";
	public static final String PHONE = "phone";
	public static final String EMAIL = "email";
	
	private String name;
	private int phone;
	private String email;

	/**
	 * Default constructor.
	 */
	public UserInfo() {
		//TODO
	}
	
	/**
	 * Creates a UserInfo object from the given Parcel.
	 * 
	 * @param source Parcel of information in:
	 * 		String (name), int, String (email), String(ObjID).
	 */
	public UserInfo(Parcel source) {
		readFromParcel(source);
	}
	
	
	/**
	 *
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @param n String name
	 */
	public void setName(String n) {
		this.name = n;
	}

	/**
	 *
	 * @return int
	 */
	public int getPhone() {
		return phone;
	}

	/**
	 *
	 * @param number int Phone number
	 */
	public void setPhone(int number) {
		this.phone = number;
	}

	/**
	 *
	 * @return String
	 */
	public String getEmail() {
		return email;
	}

	/**
	 *
	 * @param e String email
	 */
	public void setEmail(String e) {
		this.email = e;
	}


	@Override
	public ParseObject packObject() {
		ParseObject pobj = new ParseObject(this.getClass().getSimpleName());
		pobj.add(UserInfo.NAME, this.name);
		pobj.add(UserInfo.PHONE, this.phone);
		pobj.add(UserInfo.EMAIL, this.email);
		//in case this storable is going to be used after the pack.
		this.setObjId(pobj.getObjectId());
				
		return pobj;
	}

	@Override
	public void unpackObject(ParseObject pobj) {
		this.setObjId(pobj.getObjectId());
		this.setName(pobj.getString(UserInfo.NAME));
		this.setEmail(pobj.getString(UserInfo.EMAIL));
		this.setPhone(pobj.getInt(UserInfo.PHONE));
	}
	
	/**
	 * A Parcel method to describe the contents of the object.
	 * @return an int describing contents
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Write the object to a parcel object.
	 * @param dest Parcel to write to
	 * @param flags to change write settings
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeInt(phone);
		dest.writeString(email);
		dest.writeString(this.getObjId());
	}
	
	/**
	 * Parcelable creator object of a UserInfo.
	 * Can create a UserInfo from a Parcel.
	 */
	public static final Parcelable.Creator<UserInfo> CREATOR = 
			new Parcelable.Creator<UserInfo>() {

				@Override
				public UserInfo createFromParcel(Parcel source) {
					return new UserInfo(source);
				}

				@Override
				public UserInfo[] newArray(int size) {
					return new UserInfo[size];
				}
	};
			

	/**
	 * 
	 * @param source parcel to read from.
	 */
	//read an object back out of parcel
	private void readFromParcel(Parcel source) {
		this.setName(source.readString());
		this.setPhone(source.readInt());
		this.setEmail(source.readString());
		this.setObjId(source.readString());
	}
}
