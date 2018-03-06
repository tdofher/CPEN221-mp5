package ca.ece.ubc.cpen221.mp5.statlearning;

import ca.ece.ubc.cpen221.mp5.RestaurantDB;

/**
 * a class that returns the latitude of a restaurant
 * in a database
 */
public class LatFeat implements FeatureFunction {
	//Rep Invariant: the restaurant_id is not null and exists within the database
	//Abstraction Function: represents the latitude of a restaurant
	/**
	 * Returns the latitude of a restaurant
	 * 
	 * @param rdb
	 *            the database object representing the yelp dataset
	 * @param restaurant_id
	 *            the restaurant for which we want the latitude
	 * @return the latitude
	 */
	public double getFeature(RestaurantDB rdb, String restaurant_id) {
		return rdb.getRestaurantData().get(restaurant_id).latitude;
	}
}
 