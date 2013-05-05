package uw.cse.dineon.library;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * @author Espeo196
 *
 */
public class UserInfo extends Storable implements Parcelable {
	public static final String NAME = "name";
	public static final String PHONE = "phone";
	public static final String EMAIL = "email";
	
	private String name;
	private String phone;
	private String email;

	/**
	 * Default constructor.
	 */
	public UserInfo() {
		name = "";
		phone = "";
		email = "";
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
	 * Creates a new UserInfo based on a ParseUser.
	 * @param pu ParseUser to copy data from
	 */
	public UserInfo(ParseUser pu) {
		setEmail(pu.getEmail());
		setName(pu.getUsername());
		setPhone(""); // TODO
		setObjId(pu.getObjectId());
	}
	
	
	/**
	 * @return String user name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param n String name
	 */
	public void setName(String n) {
		this.name = n;
	}

	/**
	 * @return int user phone number
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param number int Phone number
	 */
	public void setPhone(String number) {
		this.phone = number;
	}

	/**
	 * @return String user email
	 */
	public String getEmail() {
		return email;
	}

	/**
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
		this.setPhone(pobj.getString(UserInfo.PHONE));
		this.setEmail(pobj.getString(UserInfo.EMAIL));
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(phone);
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
	 * Read an object back out of parcel.
	 * @param source parcel to read from.
	 */
	private void readFromParcel(Parcel source) {
		this.setName(source.readString());
		this.setPhone(source.readString());
		this.setEmail(source.readString());
		this.setObjId(source.readString());
	}
}
