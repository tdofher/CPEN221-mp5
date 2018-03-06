package ca.ece.ubc.cpen221.mp5;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import standardClasses.Restaurant;
import standardClasses.Review;
import standardClasses.User;
import standardClasses.Votes;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class represents the Restaurant Database. Internal Representation: This
 * is represented as a Map of user_ids to User objects, a Map of restaurant_ids
 * to Restaurants and a Map of review_ids to Reviews. These Maps can be read
 * using the getUserData(),getRestaurantData() and getReviewData() methods
 * respectively. Users can be created and added to the Map using
 * addUser(String), Restaurants can be created and added to the Map using
 * addRestaurant(String), Reviews can be created and added to the Map using
 * addReview(String).
 */

public class RestaurantDB {
	/** The map of buisness_ids to Restaurant Objects */
	private Map<String, Restaurant> restaurantDB;
	/** The map of review_ids to Review Objects */
	private Map<String, Review> reviewDB;
	/** The map of user_ids to User Objects */
	private Map<String, User> userDB;

	// Rep invariant:
	// no restaurantDB key or value is null
	// no Restaurant in restaurantDB has a null field
	// restaurantDB maps unique business_ids to the Restaurant with the same
	// unique business_id
	// no reviewDB key or value is null
	// no Review in reviewDB has a null field
	// reviewDB maps unique review_ids to the Review with the same unique
	// review_id
	// no userDB key or value is null
	// no User in userDB has a null field
	// reviewDB maps unique user_ids to the User with the same unique user_id
	//
	// Abstract Function:
	// represents a database of Users, Reviews and Restaurants

	/**
	 * Create a database from the Yelp dataset given the names of three files:
	 * <ul>
	 * <li>One that contains data about the restaurants;</li>
	 * <li>One that contains reviews of the restaurants;</li>
	 * <li>One that contains information about the users that submitted reviews.
	 * </li>
	 * </ul>
	 * The files contain data in JSON format.
	 *
	 * @param restaurantJSONfilename
	 *            the filename for the restaurant data
	 * @param reviewsJSONfilename
	 *            the filename for the reviews
	 * @param usersJSONfilename
	 *            the filename for the users
	 */
	public RestaurantDB(String restaurantJSONfilename, String reviewsJSONfilename, String usersJSONfilename) {

		Map<String, Restaurant> restaurantDB = new HashMap<String, Restaurant>();
		try (Stream<String> restaurantStream = Files.lines(Paths.get(restaurantJSONfilename))) {
			restaurantStream.map(RestaurantDB::getRestaurant).forEach(res -> restaurantDB.put(res.business_id, res));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.restaurantDB = restaurantDB;

		Map<String, Review> reviewDB = new HashMap<String, Review>();
		try (Stream<String> reviewStream = Files.lines(Paths.get(reviewsJSONfilename))) {
			reviewStream.map(RestaurantDB::getReviews).forEach(rev -> reviewDB.put(rev.review_id, rev));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.reviewDB = reviewDB;

		Map<String, User> userDB = new HashMap<String, User>();
		try (Stream<String> userStream = Files.lines(Paths.get(usersJSONfilename))) {
			userStream.map(RestaurantDB::getUser).forEach(user -> userDB.put(user.user_id, user));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.userDB = userDB;

	}

	/**
	 * 
	 * @return the Map of buisness_ids to Restaurants
	 */
	public Map<String, Restaurant> getRestaurantData() {
		return restaurantDB;
	}

	/**
	 * 
	 * @return the Map of review_ids to Reviews
	 */
	public Map<String, Review> getReviewData() {
		return reviewDB;
	}

	/**
	 * 
	 * @return the Map of user_ids to Users
	 */
	public Map<String, User> getUserData() {
		return userDB;
	}
	
	/**
	 * Converts a JSON line representing a Restaurant object to a java
	 * Restaurant object. Stores each of the values that are mapped to by the
	 * JSON object as variables with names as the key name.
	 * 
	 * @param fromJSON
	 *            a line from a JSON file formated as a Restaurant object, no
	 *            keys are null or map to null.
	 * @return a Restaurant object representing the restaurant in the JSON line
	 */
	public static Restaurant getRestaurant(String fromJSON) {
		Restaurant res = new Restaurant();
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(fromJSON);
			JSONObject jsonObject = (JSONObject) obj;

			res.jsonObj = fromJSON;
			res.open = (boolean) jsonObject.get("open");
			res.url = (String) jsonObject.get("url");
			res.longitude = (double) jsonObject.get("longitude");

			res.neighborhoods = new HashSet<String>();
			JSONArray neighborhoods = (JSONArray) jsonObject.get("neighborhoods");
			Iterator<String> hoodsIterator = neighborhoods.iterator();
			while (hoodsIterator.hasNext()) {
				res.neighborhoods.add(hoodsIterator.next());
			}

			res.business_id = (String) jsonObject.get("business_id");
			res.name = (String) jsonObject.get("name");

			res.categories = new HashSet<String>();
			JSONArray categories = (JSONArray) jsonObject.get("categories");
			Iterator<String> catIterator = categories.iterator();
			while (catIterator.hasNext()) {
				res.categories.add(catIterator.next());

			}

			res.state = (String) jsonObject.get("state");
			res.type = (String) jsonObject.get("type");
			res.stars = (double) jsonObject.get("stars");
			res.city = (String) jsonObject.get("city");
			res.full_address = (String) jsonObject.get("full_address");
			res.review_count = (Long) jsonObject.get("review_count");
			res.photo_url = (String) jsonObject.get("photo_url");

			res.schools = new HashSet<String>();
			JSONArray schools = (JSONArray) jsonObject.get("schools");
			Iterator<String> schoolsIterator = schools.iterator();
			while (schoolsIterator.hasNext()) {
				res.schools.add(schoolsIterator.next());
			}

			res.latitude = (double) jsonObject.get("latitude");
			res.price = (Long) jsonObject.get("price");

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Given information about a restaurant creates a new Restaurant as a JSON
	 * and adds this to restaurantDB, mutating restaurantDB
	 * 
	 * @param restaurant
	 *            a string formated as a JSON object containing data about the
	 *            restaurant
	 * 
	 * @return a string representing the Restaurant object as a JSON. Returns
	 *         "ERR: INVALID_RESTAURANT_STRING if name does not exist or the
	 *         JSON is improperly formatted. Returns "ERR: DUPLICATE_RESTAURANT"
	 *         if a restaurant already exists at that location, longitude and
	 *         latitude or full address
	 */
	public String addRestaurant(String restaurantInfo) {

		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(restaurantInfo);
			JSONObject jsonObject = (JSONObject) obj;
			if (jsonObject.get("name") == null || jsonObject.get("longitude") == null
					|| jsonObject.get("neighborhoods") == null || jsonObject.get("categories") == null
					|| jsonObject.get("state") == null || jsonObject.get("city") == null
					|| jsonObject.get("full_address") == null || jsonObject.get("schools") == null
					|| jsonObject.get("latitude") == null || jsonObject.get("price") == null) {
				return "ERR: INVALID_RESTAURANT_STRING";
			}

			if (restaurantDB.values().parallelStream()
					.anyMatch(restaurant -> jsonObject.get("full_address").equals(restaurant.full_address))
					|| (restaurantDB.values().parallelStream()
							.anyMatch(restaurant -> jsonObject.get("longitude").equals(restaurant.longitude))
							&& restaurantDB.values().parallelStream()
									.anyMatch(restaurant -> jsonObject.get("latitude").equals(restaurant.latitude)))) {
				return "ERR: DUPLICATE_RESTAURANT";
			}

			jsonObject.put("open", true);
			jsonObject.put("type", "buisness");
			int idNum = 0;
			String restaurantIDBase = "MP5TDOBrest";
			while (userDB.containsKey(restaurantIDBase + idNum)) {
				idNum++;
			}
			String restaurantID = restaurantIDBase + idNum;
			jsonObject.put("business_id", restaurantID);
			jsonObject.put("url", "http://www.yelp.com/biz/" + jsonObject.get("name"));
			jsonObject.put("photo_url", "http://s3-media2.ak.yelpcdn.com/bphoto/" + restaurantID + ".jpeg");
			jsonObject.put("stars", 0.0);
			jsonObject.put("review_count", 0);

			restaurantDB.put(restaurantID, getRestaurant(jsonObject.toString()));

			return jsonObject.toString();

		} catch (ParseException e) {
			return "ERR: INVALID_RESTAURANT_STRING";
		}
	}

	/**
	 * Converts a line of a JSON file representing a Review into a java Review
	 * object
	 * 
	 * @param fromJSON
	 *            a line from a JSON formatted as a Review object, no keys are
	 *            null
	 * @return a Review object representing the same Review as the JSON line
	 */
	public static Review getReviews(String fromJSON) {

		Review rev = new Review();
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(fromJSON);
			JSONObject jsonObject = (JSONObject) obj;

			rev.jsonObj = fromJSON;
			rev.type = (String) jsonObject.get("type");
			rev.business_id = (String) jsonObject.get("business_id");
			rev.user_id = (String) jsonObject.get("user_id");
			rev.stars = (long) jsonObject.get("stars");

			// Extract the vote object contained in the review data
			Votes voteObj = new Votes();
			JSONObject votes = (JSONObject) jsonObject.get("votes");
			voteObj.cool = (Long) votes.get("cool");
			voteObj.useful = (Long) votes.get("useful");
			voteObj.funny = (Long) votes.get("funny");
			rev.votes = voteObj;

			rev.review_id = (String) jsonObject.get("review_id");
			rev.text = (String) jsonObject.get("text");
			rev.user_id = (String) jsonObject.get("user_id");
			rev.date = (String) jsonObject.get("date");

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return rev;
	}

	/**
	 * Given information about a review creates a new Review as a JSON and adds
	 * this to reviewDB, mutating reviewDB
	 * 
	 * @param reviewInfo
	 *            a string formated as a JSON object containing information
	 *            about the review
	 * @return a string representing the Review object as a JSON. Returns "ERR:
	 *         INVALID_REVIEW_STRING" if there is not enough information about
	 *         the review or the review is improperly formatted. Returns "ERR:
	 *         NO_SUCH_USER" if the user_id of the input JSON does not exist for
	 *         any user in the database. Return "ERR: NO_SUCH_RESTAURANT" if the
	 *         business_id for the input JSON does not exist for any restaurant
	 *         in the database
	 */
	public String addReview(String reviewInfo) {

		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(reviewInfo);
			JSONObject jsonObject = (JSONObject) obj;
			if (jsonObject.get("business_id") == null || jsonObject.get("user_id") == null
					|| jsonObject.get("text") == null || jsonObject.get("date") == null
					|| jsonObject.get("stars") == null) {
				return "ERR: INVALID_REVIEW_STRING";
			}
			if (userDB.values().parallelStream()
					.anyMatch(user -> jsonObject.get("user_id").equals(user.user_id)) == false) {
				return "ERR: NO_SUCH_USER";
			}
			if (restaurantDB.values().parallelStream()
					.anyMatch(restaurant -> jsonObject.get("business_id").equals(restaurant.business_id)) == false) {
				return "ERR: NO_SUCH_RESTAURANT";
			}

			//Create a unique review_id
			int idNum = 0;
			String reviewIDBase = "MP5TDOBreview";
			while (userDB.containsKey(reviewIDBase + idNum)) {
				idNum++;
			}
			String reviewID = reviewIDBase + idNum;
			jsonObject.put("review_id", reviewID);
			jsonObject.put("type", "review");

			//A new review will have 0 votes
			JSONObject votes = new JSONObject();
			votes.put("funny", 0);
			votes.put("useful", 0);
			votes.put("cool", 0);
			jsonObject.put("votes", votes);

			reviewDB.put(reviewID, getReviews(jsonObject.toString()));

			return jsonObject.toString();

		} catch (ParseException e) {
			return "ERR: INVALID_REVIEW_STRING";
		}

	}
	

	/**
	 * Converts a line of a JSON file representing a User into a java User
	 * object
	 * 
	 * @param fromJSON
	 *            a line from a JSON formatted as a User object, no keys are
	 *            null
	 * @return a User object representing the same User as the JSON line
	 */
	public static User getUser(String fromJSON) {

		User user = new User();
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(fromJSON);
			JSONObject jsonObject = (JSONObject) obj;

			user.url = (String) jsonObject.get("url");

			Votes voteObj = new Votes();
			JSONObject votes = (JSONObject) jsonObject.get("votes");
			voteObj.cool = (Long) votes.get("cool");
			voteObj.useful = (Long) votes.get("useful");
			voteObj.funny = (Long) votes.get("funny");
			user.votes = voteObj;

			user.review_count = (Long) jsonObject.get("review_count");
			user.type = (String) jsonObject.get("type");
			user.user_id = (String) jsonObject.get("user_id");
			user.name = (String) jsonObject.get("name");
			user.average_stars = (double) jsonObject.get("average_stars");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return user;
	}

	/**
	 * Given a name creates a new user as a JSON and adds this to userDB,
	 * mutating userDB
	 * 
	 * @param userName
	 *            the name of the user to be created
	 * @return a string representing the user object as a JSON. Returns "ERR:
	 *         INVALID_USER_STRING if name does not exist or the JSON name is
	 *         improperly formatted
	 */
	public String addUser(String userName) {
		String name;
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(userName);
			JSONObject jsonObject = (JSONObject) obj;
			name = (String) jsonObject.get("name");
			if (name == null) {
				return "ERR: INVALID_USER_STRING";
			}

		} catch (ParseException e) {
			return "ERR: INVALID_USER_STRING";
		}

		// Used
		// https://www.mkyong.com/java/json-simple-example-read-and-write-json/
		JSONObject userJSON = new JSONObject();
		userJSON.put("name", name);
		userJSON.put("average_stars", 0.0);
		JSONObject votes = new JSONObject();
		votes.put("funny", 0);
		votes.put("useful", 0);
		votes.put("cool", 0);
		userJSON.put("votes", votes);
		userJSON.put("type", "user");
		userJSON.put("review_count", 0);
		int idNum = 0;
		String userIDBase = "MP5TDOBuser";
		while (userDB.containsKey(userIDBase + idNum)) {
			idNum++;
		}
		String userID = userIDBase + idNum;
		userJSON.put("user_id", userID);
		userJSON.put("url", "http://www.yelp.com/user_details?userid=" + userID);

		userDB.put(userID, getUser(userJSON.toString()));

		return userJSON.toString();
	}

}