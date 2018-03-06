package ca.ece.ubc.cpen221.mp5.statlearning;

import ca.ece.ubc.cpen221.mp5.RestaurantDB;

/**
 * a class that returns the average stars awarded to a restaurant
 * in a database
 */
public class StarFeat implements FeatureFunction {
	//Rep Invariant: the restaurant_id is not null and exists within the database
	//Abstraction Function: represents the average number of stars awarded to a restaurant
	/**
	 * Returns the average stars of a restaurant
	 * 
	 * @param rdb
	 *            the database object representing the yelp dataset
	 * @param restaurant_id
	 *            the restaurant for which we want the average stars
	 * @return the number of stars given to the restaurant on average
	 */
	public double getFeature(RestaurantDB rdb, String restaurant_id) {
		return rdb.getRestaurantData().get(restaurant_id).stars;
	}
}