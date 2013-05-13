package uw.cse.dineon.library;

import java.text.ParseException;
import java.util.Date;

import uw.cse.dineon.library.util.DineOnConstants;
import android.util.Log;

import com.parse.ParseObject;

/**
 * An abstract representation of an object that can track its time of origination.
 * This can be used for having objects that know the date (including time)
 * of their origination.
 * 
 * @author mhotan
 */
public abstract class TimeableStorable extends Storable {
	
	private static final String TAG = TimeableStorable.class.getSimpleName();
	
	private static final String DATE = "dineonDate";
	
	/**
	 * Start Date for this instance.
	 */
	private final Date mDate;
	
	/**
	 * Creates a Timeable request that starts from the start date inputted
	 * if start date is null the the current date is used.
	 * The class name is the specific name of the instance to be created.
	 * This name is necessary for Parse
	 * @param clazz Classname to instantiate to
	 * @param startDate Start date of this instance
	 */
	public TimeableStorable(Class<?> clazz, Date startDate) {
		super(clazz);
		if (startDate == null) {
			startDate = new Date();
		}
		mDate = startDate;
	}
	
	/**
	 * Creates a new timeable instance with the current time 
	 * as its origination time.
	 * @param clazz Class name to instantiate to
	 */
	public TimeableStorable(Class<?> clazz) {
		this(clazz, null);
	} 
	
	/**
	 * Creates an isntance off a Parse Object.
	 * @param parseObject Parse object to build from
	 * @throws com.parse.ParseException 
	 */
	public TimeableStorable(ParseObject parseObject) throws com.parse.ParseException {
		super(parseObject);
		String dateString = parseObject.getString(DATE);
		Date temp = null;
		try {
			temp = DineOnConstants.MDATEFORMAT.parse(dateString);
		} catch (ParseException e) { // Java ParseException
			// This should never happen unless we use 
			// A different date formatter
			// to place in parse object and extract from parse objects
			Log.e(TAG, "Date Format failed to parse date string: " + dateString);
		}
		mDate = temp;
	}
	
	/**
	 * Returns the orginating time of this Storable.
	 * @return Date that this object originated at
	 */
	public Date getOriginatingTime() {
		return mDate;
	}
	
	/**
	 * Inserts this objects fields and returns the ParseObject
	 * representation.
	 * @return ParseObject that represents this.
	 */
	@Override
	public ParseObject packObject() {
		ParseObject po = super.packObject();
		po.put(DATE, DineOnConstants.MDATEFORMAT.format(mDate));
		return po;
	}
	

}
