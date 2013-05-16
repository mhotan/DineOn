package uw.cse.dineon.library;

import uw.cse.dineon.library.util.RepresentationException;
import android.os.Parcel;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseACL;
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
	 * Tag for logging information.
	 */
	private final String tag = this.getClass().getSimpleName();

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
		ParseACL acl = new ParseACL();
		acl.setPublicReadAccess(true);
		acl.setPublicWriteAccess(true);
		mCompleteObject.setACL(acl);
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

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!o.getClass().equals(getClass())) {
			return false;
		}
		Storable s = (Storable) o;
		return s.mCompleteObject.equals(this.mCompleteObject);
	}

	@Override
	public int hashCode() {
		return mCompleteObject.hashCode();
	}

	/**
	 * Any class that  
	 * @author mhotan
	 */
	public interface StateChangeListener {

		public void onStateChanged(Storable s);

	}

	/**
	 * Cons
	 * @param in 
	 */
	protected Storable(Parcel in) {
		String id = in.readString();
		mCompleteObject = new ParseObject(this.getClass().getSimpleName());
		mCompleteObject.setObjectId(id);
		// Download this object asynchronously
		mCompleteObject.fetchIfNeededInBackground(new GetCallback() {

			@Override
			public void done(ParseObject object, ParseException e) {
				if (e != null) {
					Log.e(tag, "Unable to load object " + object + " because of " + e.getMessage());
				} else {
					// Successfully downloaded updates in the background
					// Updated the state of this object with the latest ParseObject
					// TODO Figure out a way to update the state of the current instance.
					//				updateState(object);				
				}
			}
		});
	}

	/**
	 * Updates the state of 
	 * @param object Object 
	 */
	//public abstract void updateState(ParseObject object);


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

}