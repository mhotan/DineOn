package uw.cse.dineon.library;

import uw.cse.dineon.library.util.RepresentationException;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * The Parse cloud uses ParseObjects to store information in
 * the database. For any java object that we want to store
 * in the cloud, there needs to be a convenient way to
 * convert between ParseObjects and java objects.
 *
 * This abstract class provides the method definitions that are
 * required to convert ParseObjects and java objects.
 *
 * @author Jordan, Mike
 *
 */
public abstract class Storable {

	/**
	 * A reference to the parse object that completely
	 * mirrors the storable.
	 */
	protected final ParseObject mCompleteObject;

	/**
	 * Creates an empty storable with associated class name.
	 * @param clazz Particular class for storable
	 */
	public Storable(Class<?> clazz) {
		mCompleteObject = new ParseObject(clazz.getSimpleName());
		checkRep();
	}

	/**
	 * Creates a Storable object from a Parse Object.
	 * @param parseObject parse object to build from 
	 * @throws ParseException 
	 */
	public Storable(ParseObject parseObject) throws ParseException {
		if (parseObject == null) {
			throw new IllegalArgumentException("");
		}
		// Initially Parse Provides a pointer to value a value in the cloud
		// 		for nested classes.
		// We have to explicitly fetch the object if we need it.
		parseObject.fetchIfNeeded();

		mCompleteObject = parseObject;
		checkRep();
	}

	/**
	 * Inserts this objects fields and returns the ParseObject
	 * representation.
	 * @return ParseObject that represents this.
	 */
	public ParseObject packObject() {
		return mCompleteObject;
	}

	//	/**
	//	 * Takes ParseObject and sets the appropriate fields of the
	//	 * instance of storable.
	//	 * @param pobj ParseObject that you want to convert to a Storable
	//	 */
	//	public void unpackObject(ParseObject pobj){
	//		
	//	}

	/**
	 *
	 * @return String objId
	 */
	public String getObjId() {
		return mCompleteObject.getObjectId();
	}

	/**
	 * Save this Storable object in the background.
	 * Notifies completion or failure via the inputted callback
	 * If callback is null then no notification occurs 
	 * @param saveCallBack callback to handle the event on save completion or failure
	 */
	public void saveInBackGround(SaveCallback saveCallBack) {
		ParseObject po = this.packObject();
		if (saveCallBack != null) {
			po.saveInBackground(saveCallBack);
		} else {
			po.saveInBackground();
		}
	}

	/**
	 * Save this Storable object in the background eventually.
	 * Does not have resource preference
	 * Notifies completion or failure via the inputted callback
	 * If callback is null then no notification occurs 
	 * @param saveCallBack callback to handle the event on save completion or failure
	 */
	public void saveEventually(SaveCallback saveCallBack) {
		ParseObject po = this.packObject();
		if (saveCallBack != null) {
			po.saveEventually(saveCallBack);
		} else {
			po.saveEventually();
		}
	}

	/**
	 * Saves on current thread.
	 * NOTE Make sure you are in background thread
	 * @throws ParseException Could not download
	 */
	public void saveOnCurrentThread() throws ParseException{
		ParseObject po = this.packObject();
		po.save();
	}

	/**
	 * Checks representation invariant.
	 */
	private void checkRep() {
		if (mCompleteObject == null) {
			throw new RepresentationException("Null parse object for this storable instance");
		}
	}

	/**
	 * Deletes this instance off of the cloud.
	 * This deletion does not occur right away but instead 
	 * to eventually occur.
	 */
	public void deleteFromCloud() {
		mCompleteObject.deleteEventually();
	}

	/**
	 * Changes the string representation to ParseObejct rep.
	 * @return String of ParseObject
	 */
	@Override
	public String toString() {
		return mCompleteObject.toString();
	}
}


//	/**
//	 *
//	 * @param objId String
//	 */
//	public void setObjId(String objId) {
//		this.objId = objId;
//	}

//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeString(this.getObjId());
//	}
//	
//	/**
//	 * Fills this instance with the values found in this parcel
//	 * @param source
//	 */
//	protected void readFromParcel(Parcel source) {
//		this.setObjId(source.readString());
//	} 