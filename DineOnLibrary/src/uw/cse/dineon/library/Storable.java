package uw.cse.dineon.library;

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
public abstract class Storable {


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
}
