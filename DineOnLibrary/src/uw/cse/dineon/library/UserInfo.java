package uw.cse.dineon.library;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * @author Espeo196, Michael Hotan
 */
public class UserInfo extends Storable {

	private static final String TAG = UserInfo.class.getSimpleName();

	public static final String PARSEUSER = "parseUser";
	public static final String IMAGE_ID = "imageId";
	public static final String PROFILE_DESCRIPTION = "profileDescription";
	public static final String PHONE = "userPhone";
	public static final String NAME = "userName";

	private static final String UNDETERMINED = "Undetermined";

	private final ParseUser mUser;
	private final String mName;
	private String mPhone;
	private String mImageID;
	private String mProfileDescription;

	/**
	 * Default constructor.
	 * @param user ParseUser
	 */
	public UserInfo(ParseUser user) {
		super(UserInfo.class);
		mUser = user;
		mName = mUser.getUsername();
		mImageID = UNDETERMINED;
		mProfileDescription = UNDETERMINED;
		mPhone = UNDETERMINED;
		Log.e("Constructor called", mPhone);
	}

	/**
	 * Creates a UserInfo instance from this parse object.
	 * @param po PArseObject 
	 * @throws ParseException 
	 */
	public UserInfo(ParseObject po) throws ParseException {
		super(po);
		mUser = po.getParseUser(PARSEUSER).fetchIfNeeded();
		mName = po.getString(NAME);
		mImageID = po.getString(IMAGE_ID);
		mProfileDescription = po.getString(PROFILE_DESCRIPTION);
		mPhone = po.getString(PHONE);
	}



	@Override
	public ParseObject packObject() {
		ParseObject po = super.packObject();
		po.put(PARSEUSER, (ParseUser)mUser);
		po.put(NAME, mName);
		po.put(IMAGE_ID, (String)mImageID);
		po.put(PROFILE_DESCRIPTION, (String)mProfileDescription);
		po.put(PHONE, (String)mPhone);
		return po;
	}

	/**
	 * @return String user name
	 */
	public String getName() {
		return mUser.getUsername();
	}

	/**
	 * @return int user phone number
	 */
	public String getPhone() {
		return mPhone;
	}

	/**
	 * @param number int Phone number
	 */
	public void setPhone(String number) {
		this.mPhone = number;
	}

	/**
	 * @return String user email
	 */
	public String getEmail() {
		return mUser.getEmail();
	}

	/**
	 * @param email String
	 */
	public void setEmail(String email) {
		mUser.setEmail(email);
	}

	/**
	 * Creates a user info instance from the source.
	 * @param source Source to create from.
	 */
	public UserInfo(Parcel source) {
		super(source);
		mUser = new ParseUser();
		mUser.setObjectId(source.readString());
		mUser.fetchInBackground(new GetCallback() {

			@Override
			public void done(ParseObject o, ParseException e) {
				if (e != null) {
					Log.e(TAG, "Unable to fetch user");
				}
			}
		});
		mName = source.readString();
		mImageID = source.readString();
		mProfileDescription = source.readString();
		mPhone = source.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(mUser.getObjectId());
		dest.writeString(mName);
		dest.writeString(mImageID);
		dest.writeString(mProfileDescription);
		dest.writeString(mPhone);
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
}
