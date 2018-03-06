package ca.ece.ubc.cpen221.mp5.statlearning;

import ca.ece.ubc.cpen221.mp5.RestaurantDB;

/**
 * a class that returns the price rating of a restaurant
 * in a database
 */
public class PriceFeat implements FeatureFunction {
	//Rep Invariant: the restaurant_id is not null and exists within the database
	//Abstraction Function: represents the price score awarded to a restaurant
	/**
	 * Returns the price rating of a restaurant
	 * 
	 * @param rdb
	 *            the database object representing the yelp dataset
	 * @param restaurant_id
	 *            the restaurant for which we want the price rating
	 * @return the price rating
	 */
	public double getFeature(RestaurantDB rdb, String restaurant_id) {
		return (double) rdb.getRestaurantData().get(restaurant_id).price;
	}
}