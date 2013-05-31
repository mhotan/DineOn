package uw.cse.dineon.library.android;

import android.location.Location;

/**
 * Interface that defines an element that can be located.
 * 
 * Because location finding has to be aasynchronous and there are times where no
 * location is found any object that implements this interface does not have return a
 * valid Location.
 * 
 * @author mhotan, mtrathjen08
 */
public interface Locatable extends android.location.LocationListener {

	/**
	 * Return the last location updated by the location manager.
	 * This method allows access to any current reference to the location.
	 * 
	 * @return last known location or null if know location is known.
	 */
	public Location getLastKnownLocation();
	
	/**
	 * Request a single update from the containing element.
	 */
	public void requestLocationUpdates();
}
