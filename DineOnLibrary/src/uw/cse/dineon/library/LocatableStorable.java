package uw.cse.dineon.library;

import android.location.Location;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * This represents the a Storable object that 
 * can contains a distinct location.  However, location
 * can be explicitly set.
 * @author mhotan
 */
public abstract class LocatableStorable extends Storable {

	/*
	 * AF:
	 * if mLocation is null, then this instance does not know
	 */
	
	/**
	 * General purpose reference to classname.
	 */
	private static final String CLASS_NAME = LocatableStorable.class.getSimpleName();

	// Key value helpers
	private static final String LOCATION = "LOCATION";

	/**
	 * Location of this storable entity.
	 */
	private ParseGeoPoint mLocation;

	/**
	 * Creates a locatable storable from the Location instance provided
	 * if loc is null the location will be undefined.
	 * @param clazz Class name of the Locatable Instance
	 * @param loc Location of the instance
	 */
	public LocatableStorable(Class<?> clazz, Location loc) { 
		super(clazz);
		if (loc != null) {
			updateLocation(loc.getLatitude(), loc.getLongitude());
		}
	}

	/**
	 * Creates a Locatable storable with currently unknown location.
	 * @param clazz Class name of the Locatable Instance
	 */
	public LocatableStorable(Class<?> clazz) {
		this(clazz, null);
	}

	/**
	 * Creates locatable object around a parse object that
	 * was packed by using packobject on a locatableStorabl instance.
	 * @param parseObject object to extract storable instance
	 */
	public LocatableStorable(ParseObject parseObject) {
		super(parseObject);
		mLocation = parseObject.getParseGeoPoint(LOCATION);
	}
	
	@Override
	public ParseObject packObject() {
		ParseObject po = super.packObject();
		po.put(LOCATION, mLocation);
		return po;
	}

	/**
	 * Sets the current location of this object.
	 * @param longitude longitude of this object
	 * @param latitude latitude of this object
	 */
	public void updateLocation(double longitude, double latitude) {
		if (mLocation == null) {
			mLocation = new ParseGeoPoint();
		}
		mLocation.setLatitude(latitude); 
		mLocation.setLongitude(longitude);
	}

	/**
	 * Returns the current location of this object.
	 * @return Location instance
	 */
	public Location getLocation() {
		if (mLocation == null) {
			return null;
		}
		Location l = new Location(CLASS_NAME);
		l.setLatitude(mLocation.getLatitude());
		l.setLongitude(mLocation.getLongitude());
		return l;
	}
	
	/**
	 * Clears all memory of this instance knowledge of its 
	 * location.
	 */
	public void deleteLocation() {
		mCompleteObject.remove(LOCATION);
		mCompleteObject.saveEventually();
	}

}
