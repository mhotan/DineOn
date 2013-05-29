package uw.cse.dineon.user.restaurant.home;

import uw.cse.dineon.library.RestaurantInfo;

/**
 * General interface that mandated that any class that implements 
 * this can provide a non null Restaurant Info instance.
 * @author mhotan
 */
public interface RestaurantRetrievable {

	/**
	 * Returns a restaurant instance of interest.
	 * 
	 * Requires the returned instance not be null
	 * @return Restaurant Info instance
	 */
	RestaurantInfo getCurrentRestaurant();
	
}
