package ca.ece.ubc.cpen221.mp5;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import standardClasses.Restaurant;
import standardClasses.Review;

//Code reuse from https://github.com/CPEN-221/example16.git
//This include general structure, methods and some comments
public class RestaurantDBServer {

	private RestaurantDB restaurantDB;
	private ServerSocket serverSocket;

	// Rep invariant: serverSocket != null
	// restaurantDB != null
	// Abstract functions:
	// Represents a multithread server containing a RestaurantDB

	/** Default port number where the server listens for connections. */
	public static final int RESTAURANT_PORT = 4949;
	//Works on Toren's computer
	/*public final static String RESTAURANT_DATA_FILEPATH = "C:\\Users\\Toren\\workspace\\f16-mp5-tdofher\\data\\restaurants.json";
	public final static String REVIEW_DATA_FILEPATH = "C:\\Users\\Toren\\workspace\\f16-mp5-tdofher\\data\\reviews.json";
	public final static String USER_DATA_FILEPATH = "C:\\Users\\Toren\\workspace\\f16-mp5-tdofher\\data\\users.json";
	*/
	//Works in Unix
	public final static String workingDir = System.getProperty("user.dir");
	public final static String RESTAURANT_DATA_FILEPATH = workingDir + File.pathSeparator + "data" + File.pathSeparator + "restaurants.json";
	public final static String REVIEW_DATA_FILEPATH = workingDir + File.pathSeparator + "data" + File.pathSeparator + "reviews.json";
	public final static String USER_DATA_FILEPATH = workingDir + File.pathSeparator + "data" + File.pathSeparator + "users.json";
	

	/**
	 * Make a RestaurantDBServer that listens for connections on port and
	 * create a RestaurantDB from the Yelp dataset given the paths of three
	 * files:
	 * <ul>
	 * <li>One that contains data about the restaurants;</li>
	 * <li>One that contains reviews of the restaurants;</li>
	 * <li>One that contains information about the users that submitted reviews.
	 * </li>
	 * </ul>
	 * The files contain data in JSON format.
	 * 
	 * @param port
	 *            port number, requires 0 <= port <= 65535
	 * @param restaurantPath
	 *            the path to a file containing restaurant objects in JSON
	 *            format
	 * @param reviewPath
	 *            the path to a file containing review objects in JSON format
	 * @param userPath
	 *            the path to a file containing user objects in JSON format
	 * @throws IOException
	 */
	public RestaurantDBServer(int port, String restaurantPath, String reviewPath, String userPath) throws IOException {
		serverSocket = new ServerSocket(port);
		this.restaurantDB = new RestaurantDB(restaurantPath, reviewPath, userPath);
	}

	/**
	 * Run the server, listening for connections and handling them.
	 * 
	 * @throws IOException
	 *             if the main server socket is broken
	 */
	public void serve() throws IOException {

		while (true) {
			// block until a client connects
			final Socket socket = serverSocket.accept();
			// create a new thread to handle that client
			Thread handler = new Thread(new Runnable() {
				public void run() {
					try {
						try {
							handle(socket);
						} finally {
							socket.close();
						}
					} catch (IOException ioe) {
						// this exception wouldn't terminate serve(),
						// since we're now on a different thread, but
						// we still need to handle it
						ioe.printStackTrace();
					}
				}
			});
			// start the thread
			handler.start();
		}
	}

	/**
	 * Handle one client connection. Returns when client disconnects.
	 * 
	 * @param socket
	 *            socket where client is connected
	 * @throws IOException
	 *             if connection encounters an error
	 */
	private void handle(Socket socket) throws IOException {
		System.err.println("client connected");

		// get the socket's input stream, and wrap converters around it
		// that convert it from a byte stream to a character stream,
		// and that buffer it so that we can read a line at a time
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		// similarly, wrap character=>bytestream converter around the
		// socket output stream, and wrap a PrintWriter around that so
		// that we have more convenient ways to write Java primitive
		// types to it.
		PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

		try {
			// each request is a single line containing a number
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				System.err.println("request: " + line);
				try {
					line = line.trim();
					// Handles RANDOMREVIEW requests
					// Checks to see is the command entered is RANDOMREVIEW
					if (line.contains("RANDOMREVIEW")) {
						// Make sure RANDOMREVIEW is not the only thing in the
						// line
						if (line.length() == "RANDOMREVIEW".length()) {
							out.print("ERR: NO_RESTAURANT_NAME_ENTERED");
							out.flush();
						}
						// Makes sure RANDOMREVIEW at beginning of line followed
						// by restaurant name
						if (line.substring(0, line.indexOf(" ")).equals("RANDOMREVIEW")) {
							out.print(getRandomReview(line.substring(line.indexOf(" ") + 1, line.length())) + "\n");
							out.flush();
						}
						out.print("ERR: 'RANDOMREVIEW'_NOT_AT_BEGINNING_OF_COMMAND" + "\n");
						out.flush();
					}

					// Handles GETRESTAURANT requests
					if (line.contains("GETRESTAURANT")) {
						if (line.length() == "GETRESTAURANT".length()) {
							out.print("ERR: NO_BUSINESS_ID_ENTERED");
							out.flush();
						}
						if (line.substring(0, line.indexOf(" ")).equals("GETRESTAURANT")) {
							out.print(getRestaurant(line.substring(line.indexOf(" ") + 1, line.length())) + "\n");
							out.flush();
						}
						out.print("ERR: 'GETRESTAURANT'_NOT_AT_BEGINNING_OF_COMMAND" + "\n");
						out.flush();
					}

					// Handles ADDUSER requests
					if (line.contains("ADDUSER")) {
						if (line.length() == "ADDUSER".length()) {
							out.print("ERR: NO_USER_ID_ENTERED");
							out.flush();
						}
						if (line.substring(0, line.indexOf(" ")).equals("ADDUSER")) {
							// Locks restaurantDB to prevent data-races from
							// writing to object
							synchronized (restaurantDB) {
								out.print(restaurantDB.addUser(line.substring(line.indexOf(" ") + 1, line.length()))
										+ "\n");
								out.flush();
							}
						}
						out.print("ERR: 'ADDUSER'_NOT_AT_BEGINNING_OF_COMMAND" + "\n");
						out.flush();
					}

					// Handles ADDRESTAURANT requests
					if (line.contains("ADDRESTAURANT")) {
						if (line.length() == "ADDRESTAURANT".length()) {
							out.print("ERR: INVALID_RESTAURANT_STRING");
							out.flush();
						}
						if (line.substring(0, line.indexOf(" ")).equals("ADDRESTAURANT")) {
							// Locks restaurantDB to prevent data-races from
							// writing to object
							synchronized (restaurantDB) {
								out.print(
										restaurantDB.addRestaurant(line.substring(line.indexOf(" ") + 1, line.length()))
												+ "\n");
								out.flush();
							}
						}
						out.print("ERR: 'ADDRESTAURANT'_NOT_AT_BEGINNING_OF_COMMAND" + "\n");
						out.flush();
					}

					// Handles ADDREVIEW requests
					if (line.contains("ADDREVIEW")) {
						if (line.length() == "ADDREVIEW".length()) {
							out.print("ERR: INVALID_REVIEW_STRING");
							out.flush();
						}
						if (line.substring(0, line.indexOf(" ")).equals("ADDREVIEW")) {
							// Locks restaurantDB to prevent data-races from
							// writing to object
							synchronized (restaurantDB) {
								out.print(restaurantDB.addReview(line.substring(line.indexOf(" ") + 1, line.length()))
										+ "\n");
								out.flush();
							}
						}
						out.print("ERR: 'ADDREVIEW'_NOT_AT_BEGINNING_OF_COMMAND" + "\n");
						out.flush();
					}

					else {
						// Handles all other requests
						out.print("ERR: INVALID COMMAND " + line + "\n");
						out.flush();
					}
				} catch (NumberFormatException e) {
					// complain about ill-formatted request
					System.err.println("reply: err");
					out.print("err\n");
				}
				// important! our PrintWriter is auto-flushing, but if it were
				// not:
				// out.flush();
			}
		} finally {
			out.close();
			in.close();
		}
	}

	/**
	 * Given a restaurant name returns a random review of that restaurant
	 * 
	 * @param restaurantName
	 *            the name of the restaurant that one would like to find a
	 *            random review for
	 * 
	 * @return a random Review object associated with that restaurant by
	 *         buisness_id in JSON format. If there is more than one restaurant
	 *         of that name returns "ERR: MULTIPLE_RESTAURANTS" if the
	 *         restaurant does not exist return "ERR: NO_RESAURANT_FOUND
	 */
	private String getRandomReview(String restaurantName) {
		List<standardClasses.Restaurant> listOfMatches = getMatchingRestaurants(restaurantName);
		if (listOfMatches.size() > 1) {
			return "ERR: MULTIPLE_RESTAURANTS";
		}
		if (listOfMatches.size() == 0) {
			return "ERR: NO_RESTAURANT_FOUND";
		}
		List<standardClasses.Review> listOfReviews = getReviewsOfRestaurant(listOfMatches.get(0).business_id);
		if (listOfReviews.size() == 0) {
			return "ERR: RESTAURANT_HAS_NO_REVIEWS";
		}
		Random random = new Random();
		int randomIndex = random.nextInt(listOfReviews.size() - 1);

		return listOfReviews.get(randomIndex).jsonObj;
	}

	/**
	 * Given a business_id returns the JSON string representing the restaurant
	 * with the corresponding business_id
	 * 
	 * @param business_id
	 *            the business_id of a restaurant
	 * @return the string representing the restaurant in JSON format. If no
	 *         Restaurant matches the business_id return "ERR:
	 *         NO_RESTAURANT_FOUND"
	 */
	private String getRestaurant(String business_id) {

		if (this.restaurantDB.getRestaurantData().values().parallelStream()
				.anyMatch(restaurant -> business_id.equals(restaurant.business_id))) {
			return restaurantDB.getRestaurantData().get(business_id).jsonObj;

		} else {
			return "ERR: NO_RESTAURANT_FOUND";
		}
	}

	/**
	 * Given a restaurant name returns a list of restaurants of the same name
	 * 
	 * @param restaurantName
	 *            the name of the restaurant
	 * @return the list of restaurants that have the same name
	 */
	private List<standardClasses.Restaurant> getMatchingRestaurants(String restaurantName) {
		Stream<standardClasses.Restaurant> matches = this.restaurantDB.getRestaurantData().values().parallelStream()
				.filter(restaurant -> restaurantName.equals(restaurant.name));
		List<standardClasses.Restaurant> listOfMatches = matches.collect(Collectors.toList());
		return listOfMatches;
	}

	/**
	 * Given a business_id returns all the review for the restaurant in
	 * restaurantDB with the corresponding business_id
	 * 
	 * @param business_id
	 *            the business_id of the restaurant to get review of
	 * @return the list of all the reviews for the restaurant
	 */
	private List<standardClasses.Review> getReviewsOfRestaurant(String business_id) {
		Stream<standardClasses.Review> matchingReviews = this.restaurantDB.getReviewData().values().parallelStream()
				.filter(review -> business_id.equals(review.business_id));
		List<standardClasses.Review> listOfReviews = matchingReviews.collect(Collectors.toList());
		return listOfReviews;
	}

	/**
	 * Start a RestaurantDBServer running on the default port with restaurantDB
	 * accessing files contained in data folder
	 */
	public static void main(String[] args) {
		try {
			RestaurantDBServer server = new RestaurantDBServer(RESTAURANT_PORT, RESTAURANT_DATA_FILEPATH,
					REVIEW_DATA_FILEPATH, USER_DATA_FILEPATH);
			server.serve();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
