package ca.ece.ubc.cpen221.mp5.statlearning;

import ca.ece.ubc.cpen221.mp5.RestaurantDB;

/**
 * a class that returns the longitude of a restaurant
 * in a database
 */
public class LongFeat implements FeatureFunction {
	//Rep Invariant: the restaurant_id is not null and exists within the database
	//Abstraction Function: represents the longitude of a restaurant
	/**
	 * Returns the longitude of a restaurant
	 * 
	 * @param rdb
	 *            the database object representing the yelp dataset
	 * @param restaurant_id
	 *            the restaurant for which we want the longitude
	 * @return the longitude
	 */
	public double getFeature(RestaurantDB rdb, String restaurant_id) {
		return rdb.getRestaurantData().get(restaurant_id).longitude;
	}
}