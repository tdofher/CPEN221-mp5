package ca.ece.ubc.cpen221.mp5.statlearning;

import ca.ece.ubc.cpen221.mp5.RestaurantDB;

/**
 * A class that represents a least squares regression function
 */
public class LSRFunc implements LeastSquaresRegression {
	private final double a;
	private final double b;
	private final FeatureFunction ff;
	
	//Rep Invariant: no null values are passed
	//Abstraction Function: represents a formula that calculates the expected rating
	//						that a user will give a restaurant
	/**
	 * constructs a predictive function based on a users past ratings
	 * 
	 * @param a the mean of all y values - b * the mean of all x values
	 * @param b [sigma_i=0 (y_i - meany) * x_i] / [sigma_i=0 x_i^2]
	 * @param ff the feature function that we are basing the prediction on
	 */
	public LSRFunc(double a, double b, FeatureFunction ff) {
		this.a = a;
		this.b = b;
		this.ff = ff;
	}
	//Rep Invariant: no null values are passed and the restaurant_id wxists in the database
	//Abstraction Function: performs the calculation to predict a useers rating
	/**
	 * Compute the rating that a user will give a restaurant
	 *
	 * @param db the database representing the yelp data set
	 * @param yelpRestaurant
	 * @return the predicted rating
	 */
	public double lsrf(RestaurantDB db, String restaurant_id) {
		double rating = ff.getFeature(db, restaurant_id);
		
		return ((b * rating) - a);
	}
}