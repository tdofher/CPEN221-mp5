package ca.ece.ubc.cpen221.mp5.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.BindException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.ece.ubc.cpen221.mp5.RestaurantDB;
import ca.ece.ubc.cpen221.mp5.RestaurantDBClient;
import ca.ece.ubc.cpen221.mp5.RestaurantDBServer;
import standardClasses.User;

//Start server before running test suite
/**
 * Runs a suite of test cases for the Simple Requests to the server. Includes
 * testing of necessary methods in RestaurantDB
 *
 */
public class RestaurantDBTest {

	RestaurantDB database;

	public final static String RESTAURANT_DATA_FILEPATH = RestaurantDBServer.RESTAURANT_DATA_FILEPATH;
	public final static String REVIEW_DATA_FILEPATH = RestaurantDBServer.REVIEW_DATA_FILEPATH;
	public final static String USER_DATA_FILEPATH = RestaurantDBServer.USER_DATA_FILEPATH;

	@Before
	public void createDB() {
		database = new RestaurantDB(RESTAURANT_DATA_FILEPATH, REVIEW_DATA_FILEPATH, USER_DATA_FILEPATH);

	}

	// Test invalid command
	@Test
	public void invalidCommand() throws IOException {
		RestaurantDBClient client = new RestaurantDBClient("localhost", RestaurantDBServer.RESTAURANT_PORT);
		client.sendRequest("MAKERESTAURANTSGREATAGAIN");
		String reply = client.getReply();
		assertEquals(reply, "ERR: INVALID COMMAND MAKERESTAURANTSGREATAGAIN");
	}

	// Test an empty command
	@Test
	public void blankCommand() throws IOException {
		RestaurantDBClient client = new RestaurantDBClient("localhost", RestaurantDBServer.RESTAURANT_PORT);
		client.sendRequest("");
		String reply = client.getReply();
		assertEquals(reply, "ERR: INVALID COMMAND ");
	}

	// Tests RANDOMREVIEW of a non-existent restaurant
	@Test
	public void testNoRestaurantFoundRandomReview() throws IOException {
		RestaurantDBClient client = new RestaurantDBClient("localhost", RestaurantDBServer.RESTAURANT_PORT);
		client.sendRequest("RANDOMREVIEW Cafe 1000");
		String reply = client.getReply();
		assertEquals(reply, "ERR: NO_RESTAURANT_FOUND");

	}

	// Tests RANDOMREVIEW of a duplicate restaurant
	@Test
	public void testMultipleRestaurants() throws IOException {
		RestaurantDBClient client = new RestaurantDBClient("localhost", RestaurantDBServer.RESTAURANT_PORT);
		client.sendRequest("RANDOMREVIEW Bongo Burger");
		String reply = client.getReply();
		assertEquals(reply, "ERR: MULTIPLE_RESTAURANTS");
	}

	// Tests RANDOMREVIEW for a unique restaurant
	@Test
	public void testRandomReviewWorks() throws IOException {
		RestaurantDBClient client = new RestaurantDBClient("localhost", RestaurantDBServer.RESTAURANT_PORT);
		client.sendRequest("RANDOMREVIEW Babette");
		String reply = client.getReply();
		assertTrue(reply.contains("6QZR4ToHKlse0yhqpU5ijg"));
	}

	// Tests GETRESTAURANT for a non-existent restaurant
	@Test
	public void testGetNonExistentRestaurant() throws IOException {
		RestaurantDBClient client = new RestaurantDBClient("localhost", RestaurantDBServer.RESTAURANT_PORT);
		client.sendRequest("GETRESTAURANT walawalawala");
		String reply = client.getReply();
		assertEquals(reply, "ERR: NO_RESTAURANT_FOUND");
	}

	// Tests GETRESTAURANT for an existent restaurant
	@Test
	public void testGetRestaurant() throws IOException {
		RestaurantDBClient client = new RestaurantDBClient("localhost", RestaurantDBServer.RESTAURANT_PORT);
		client.sendRequest("GETRESTAURANT gOp_w9qmLq6B8YRypTPp8g");
		String reply = client.getReply();

		assertEquals(reply, database.getRestaurantData().get("gOp_w9qmLq6B8YRypTPp8g").jsonObj);
	}

	// Tests that addUser adds a user
	@Test
	public void testAddUserAddsUser() throws IOException {
		database.addUser("{\"name\": \"Sathish G.\"}");
		assertTrue(database.getUserData().values().parallelStream().anyMatch(user -> "Sathish G.".equals(user.name)));
	}

	// Tests that addUser returns "ERR: INVALID_USER_STRING" for a malformed
	// JSON string
	@Test
	public void testInvalidUser() {
		assertEquals("ERR: INVALID_USER_STRING", database.addUser("little jimmy"));
	}

	// Tests that addReview adds a review
	@Test
	public void testAddReviewAddsReview() throws IOException {
		database.addReview(
				"{\"business_id\": \"1CBs84C-a-cuA3vncXVSAw\", \"text\": \"Testing based on this text\", \"stars\": 2, \"user_id\": \"90wm_01FAIqhcgV_mPON9Q\", \"date\": \"2006-07-26\"}");
		assertTrue(database.getReviewData().values().parallelStream()
				.anyMatch(review -> "Testing based on this text".equals(review.text)));
	}

	// Tests that addReview returns "ERR: NO_SUCH_USER" for an invalid user
	@Test
	public void testNoUser() {
		assertEquals("ERR: NO_SUCH_USER", database.addReview(
				"{\"business_id\": \"1CBs84C-a-cuA3vncXVSAw\", \"text\": \"Testing based on this text\", \"stars\": 2, \"user_id\": \"Doug the frog\", \"date\": \"2006-07-26\"}"));

	}

	// Tests that addReview returns "ERR: NO_SUCH_RESTAURANT" for an invalid
	// user
	@Test
	public void testNoRestaurant() {
		assertEquals("ERR: NO_SUCH_RESTAURANT", database.addReview(
				"{\"business_id\": \"The Green Dragon\", \"text\": \"Testing based on this text\", \"stars\": 2, \"user_id\": \"_NH7Cpq3qZkByP5xR4gXog\", \"date\": \"2006-07-26\"}"));

	}

	// Tests that addRestaurant adds a Restaurant
	@Test
	public void testAddRestaurantAddsRestaurant() throws IOException {
		database.addRestaurant("{\"open\": true, \"url\": \"http://www.yelp.com/biz/cafe-3-berkeley\","
				+ "\"longitude\": 42.0, \"neighborhoods\": [\"Telegraph Ave\", \"UC Campus Area\"],"
				+ " \"name\": \"TorenTestCafe\", \"categories\": [\"Cafes\", \"Restaurants\"], "
				+ "\"state\": \"CA\", \"type\": \"business\", \"stars\": 2.0, \"city\": \"Berkeley\", \"full_address\": \"MyHouse\", "
				+ "\"review_count\": 9, \"photo_url\": \"http://s3-media1.ak.yelpcdn.com/bphoto/AaHq1UzXiT6zDBUYrJ2NKA/ms.jpg\", "
				+ "\"schools\": [\"University of California at Berkeley\"], \"latitude\": 6.0, \"price\": 1}");
		assertTrue(database.getRestaurantData().values().parallelStream()
				.anyMatch(restaurant -> "TorenTestCafe".equals(restaurant.name)));
	}

	// Test that adding a restaurant at the same latitude and longitude returns
	// "ERR: DUPLICATE_RESTAURANT"
	@Test
	public void testDuplicateRestaurant() throws IOException {
		assertEquals("ERR: DUPLICATE_RESTAURANT",
				database.addRestaurant("{\"open\": true, \"url\": \"http://www.yelp.com/biz/cafe-3-berkeley\","
						+ "\"longitude\": -122.260408, \"neighborhoods\": [\"Telegraph Ave\", \"UC Campus Area\"],"
						+ " \"name\": \"TorenTestCafe\", \"categories\": [\"Cafes\", \"Restaurants\"], "
						+ "\"state\": \"CA\", \"type\": \"business\", \"stars\": 2.0, \"city\": \"Berkeley\", \"full_address\": \"MyHouse\", "
						+ "\"review_count\": 9, \"photo_url\": \"http://s3-media1.ak.yelpcdn.com/bphoto/AaHq1UzXiT6zDBUYrJ2NKA/ms.jpg\", "
						+ "\"schools\": [\"University of California at Berkeley\"], \"latitude\" 37.867417, \"price\": 1}"));
	}
}
