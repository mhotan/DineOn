package uw.cse.dineon.library;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

/**
 * The Parse cloud uses ParseObjects to store information in
 * the database. For any java object that we want to store
 * in the cloud, there needs to be a convenient way to
 * convert between ParseObjects and java objects.
 *
 * This abstract class provides the method definitions that are
 * required to convert ParseObjects and java objects.
 *
 * @author Jordan
 *
 */
public abstract class Storable implements Parcelable {


	private String objId;

	/**
	 * Inserts this objects fields and returns the ParseObject
	 * representation.
	 * @return ParseObject that represents this.
	 */
	public  abstract ParseObject packObject();

	/**
	 * Takes ParseObject and sets the appropriate fields of the
	 * instance of storable.
	 * @param pobj ParseObject that you want to convert to a Storable
	 */
	public abstract void unpackObject(ParseObject pobj);

	/**
	 *
	 * @return String objId
	 */
	public String getObjId() {
		return objId;
	}

	/**
	 *
	 * @param objId String
	 */
	public void setObjId(String objId) {
		this.objId = objId;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.getObjId());
	}
	
	/**
	 * Fills this instance with the values found in this parcel
	 * @param source
	 */
//	protected void readFromParcel(Parcel source) {
//		this.setObjId(source.readString());
//	} 
}
