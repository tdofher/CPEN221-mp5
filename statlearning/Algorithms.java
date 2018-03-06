package ca.ece.ubc.cpen221.mp5.statlearning;

import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ca.ece.ubc.cpen221.mp5.*;
import standardClasses.Restaurant;
import standardClasses.Review;

/**
 * some algorithms to reveal aspects of data given in a database
 */
public class Algorithms {
	//Rep Invariant: database is not null
	//Abstraction Function: represents neighbourhoods of restaurants based on location
	/**
	 * Computes k clusters of restaurants in a database
	 *
	 * @param db the database containing the restaurant
	 * @param k the number of clusters to create
	 * @return a list that contains each cluster of restaurants as a set
	 */
	public static List<Set<Restaurant>> kMeansClustering(int k, RestaurantDB db) {
		//Get max and min x and y locations		
		Set<Restaurant> rests = new HashSet<Restaurant>(db.getRestaurantData().values());
		List<Set<Restaurant>> clusters = new LinkedList<Set<Restaurant>>();
		
		if (k == 0) {
			clusters.add(rests);
			return clusters;
		}
		
		double minLong = 0;
		double minLat = 0;
		
		double maxLong = 0;
		double maxLat = 0;
		
		Boolean first = true;
		for (Restaurant rest : rests) {
			if (first){
				maxLong = rest.longitude;
				minLong = rest.longitude;
				
				maxLat = rest.latitude;
				maxLat = rest.latitude;
				
				first = false;
			}
			else {
				if (rest.latitude > maxLat){
					maxLat = rest.latitude;
				}
				if (rest.latitude < minLat){
					minLat = rest.latitude;
				}
				if (rest.longitude > maxLong){
					maxLong = rest.longitude;
				}
				if (rest.longitude < minLong){
					minLong = rest.longitude;
				}
			}
		}
		//Create k random nodes within that range
		Map<Restaurant, Integer> restNode = new HashMap<Restaurant, Integer>();
		
		for (Restaurant rest : rests) {
			restNode.put(rest, 0);
		}
		rests.clear();
		
		List<Double[]> nodes = new LinkedList<Double[]>();
		
		for (int i = 0 ; i < k ; i++) {
			Double[] node = {Math.random() * maxLat + minLat, Math.random() * maxLong + minLong};
			
			nodes.add(node);
		}
		
		//associate each restaurant to the closest node
		restNode = updateNodes(restNode, nodes);
		
		//while some nodes are not at the centroids
		while (!areAtCenter(restNode, nodes)) {
			//move each node to the centroid
			nodes = moveNodes(restNode, nodes);
			
			//associate each restaurant to the closest node
			restNode = updateNodes(restNode, nodes);
		}
		
		for (int i = 0 ; i < nodes.size() ; i++) {
			Set<Restaurant> cluster = new HashSet<Restaurant>();
		
			for (Restaurant rest : restNode.keySet()) {
				if (restNode.get(rest) == i) {
					cluster.add(rest);
				}
			}
			clusters.add(cluster);
		}
			
		return clusters;
	}
	
	/**
	 * maps each restaurant to the closest node, each node is mapped as a number corresponding to its position in the array
	 * 
	 * @param restNode the current map of restaurants and nodes
	 * @param nodes the list of nodes
	 * @return the updated map
	 */
	private static Map<Restaurant, Integer> updateNodes(Map<Restaurant, Integer> restNode, List<Double[]> nodes) {
		for (Restaurant rest : restNode.keySet()) {
			Double[] point = {rest.latitude, rest.longitude};
			
			int closestNode = 0;
			double distance = Math.sqrt(Math.pow(nodes.get(0)[0] - point[0], 2) + Math.pow(nodes.get(0)[1] - point[0], 1));
			int count = 0;
			
			for (Double[] node : nodes) {
				if (Math.sqrt(Math.pow(node[0] - point[0], 2) + Math.pow(node[1] - point[0], 1)) < distance) {
					closestNode = count;
				}				
				restNode.put(rest, closestNode);
				count++;
			}
		}
		return restNode;
	}
	
	/**
	 * returns true if all nodes are currently at the centroid of their respective cluster
	 * 
	 * @param restNode map of restaurants to nodes
	 * @param nodes list of node positions
	 * @return true if all nodes are at the centroid of their respective cluster and false otherwise
	 */
	private static Boolean areAtCenter(Map<Restaurant, Integer> restNode, List<Double[]> nodes) {
		for (int node = 0 ; node < nodes.size() ; node++) {
			
			double latsum = 0;
			double longsum = 0;
			double count = 0;
			
			for (Restaurant rest : restNode.keySet()) {
				if (restNode.get(rest).equals(node)) {
					latsum += rest.latitude;
					longsum += rest.longitude;
					count++;
				}
			}
			
			Double[] centroid = {latsum / count, longsum / count};
			
			if (!centroid.equals(nodes.get(node))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * moves all nodes to the centroid of their cluster
	 * 
	 * @param restNode map of restaurants to nodes
	 * @param nodes list of node locations
	 * @return new list of nodes
	 */
	private static List<Double[]> moveNodes(Map<Restaurant, Integer> restNode, List<Double[]> nodes) {
		List<Double[]> centroids = new LinkedList<Double[]>();
		
		for (int node = 0 ; node < nodes.size() ; node++) {
					
			double latsum = 0;
			double longsum = 0;
			double count = 0;
			
			for (Restaurant rest : restNode.keySet()) {
				if (restNode.get(rest).equals(node)) {
					latsum += rest.latitude;
					longsum += rest.longitude;
					count++;
				}
			}
			Double[] centroid = {latsum / count, longsum / count};
			
			centroids.add(centroid);			
		}
		return centroids;
	}
	
	@Override
	public String toString() {
		return "{\"\"}";
	}
	
	public static String convertClustersToJSON(List<Set<Restaurant>> clusters) {
		return clusters.toString();
	}
	
	//Rep Invariant: no passed values are null and user_id exists within the database
	//Abstraction Function: creates a formula for calculating the rating a user will give
	//						a restaurant based on the feature function passed
	/**
	 * returns a function that can be used to predict the rating that a user will give a restaurant
	 * 
	 * @param user_id
	 *            the id of the user that we want a prediction function for
	 * @param db
	 *            the database object that represents the yelp dataset
	 * @param featureFunction
	 *            the function that gives the value of the feature that we are basing our prediction on 
	 * @return a function that predicts the rating that user_id will give a restaurant
	 */
	public static LeastSquaresRegression getPredictor(String user_id, RestaurantDB db, FeatureFunction featureFunction) {
		double meanx = 0.0;
		double meany = 0.0;
		double Sxx = 0.0;
		double Syy = 0.0;
		double Sxy = 0.0;
		double sumx = 0.0;
		double sumy = 0.0;
		double a = 0.0;
		double b = 0.0;
		Set<Double[]> xVSy = new HashSet<Double[]>();
		
		//map all user reviews at a restaurant to the feature function of that restaurant
		for (Review rev : db.getReviewData().values()) {
			if (rev.user_id.equals(user_id)) {

				Double[] point = { featureFunction.getFeature(db, rev.business_id), Long.valueOf(rev.stars).doubleValue() };

				xVSy.add(point);
				sumx += point[0];
				sumy += point[1];
				
			}
		}
		if (xVSy.size() > 0) {
			meanx = sumx / xVSy.size();
			meany = sumy / xVSy.size();
		}
		//use the x and y coordinates to find all values
		for (Double[] point : xVSy) {
			Double pointx = point[0] - meanx;
			Sxx += Math.pow(pointx, 2);
			Syy += Math.pow((point[1] - meany), 2);
			Sxy += (point[1] - meany) * pointx;
			
		}
		
		if (Sxx != 0 && Syy != 0) {
			b = Sxy / Sxx;
			a = meany - b * meanx;		
		

			 return new LSRFunc(a, b, featureFunction);
		}

		return null;

	}
	
	//Rep Invariant: no values passed are null and user_id exists within the database
	//Abstraction function: creates the most precise formula for predicting the rating a user will 
	//						give a restaurant based on the feature functions in featureFunctionList
	/**
	 * Returns the best function that can be used to predict the rating a user will give a restaurant
	 * 
	 * @param user_id
	 *            the id of the user that we want a prediction function for
	 * @param db
	 *            the database object that represents the yelp dataset
	 * @param featureFunctionList
	 *            is a list of feature functions from which the best is selected
	 * @return the best function that predicts the rating that user_id will give a restaurant
	 */
	public static LeastSquaresRegression getBestPredictor(String user_id, RestaurantDB db, List<FeatureFunction> featureFunctionList) {
		double meanx = 0.0;
		double meany = 0.0;
		double Sxx = 0.0;
		double Syy = 0.0;
		double Sxy = 0.0;
		double sumx = 0.0;
		double sumy = 0.0;
		double R2 = 0.0;
		double a = 0.0;
		double b = 0.0;
		double R2Max = 0.0;
		Boolean flag = true;
		LeastSquaresRegression lsr = null;
		Set<Double[]> xVSy = new HashSet<Double[]>();
		
		//map all user reviews at a restaurant to the feature function of that restaurant
		for (FeatureFunction ff : featureFunctionList) {
			for (Review rev : db.getReviewData().values()) {
				if (rev.user_id.equals(user_id)) {

					Double[] point = { ff.getFeature(db, rev.business_id), Long.valueOf(rev.stars).doubleValue() };

					xVSy.add(point);
					sumx += point[0];
					sumy += point[1];
				}
			}
			if (xVSy.size() > 0) {
				meanx = sumx / xVSy.size();
				meany = sumy / xVSy.size();
			}
			//use the x and y coordinates to find all values
			for (Double[] point : xVSy) {
				Double pointx = point[0] - meanx;
				Sxx += Math.pow(pointx, 2);
				Syy += Math.pow((point[1] - meany), 2);
				Sxy += (point[1] - meany) * pointx;
			}
			//ensure we don't divide by 0 and check if r squared is the highest so far
			if (Sxx != 0 && Syy != 0) {
				R2 = Sxy / (Sxx*Syy);
				b = Sxy / Sxx;
				a = meany - b * meanx;
				if (flag || R2 > R2Max) {
					R2Max = R2;
					lsr = new LSRFunc(a, b, ff);
					flag = false;
				}
			}
		}
		return lsr;		
	}
}