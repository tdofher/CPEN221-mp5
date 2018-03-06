package ca.ece.ubc.cpen221.mp5.tests;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;

import java.util.List;
import java.util.Set;

import ca.ece.ubc.cpen221.mp5.RestaurantDB;
import ca.ece.ubc.cpen221.mp5.statlearning.Algorithms;
import ca.ece.ubc.cpen221.mp5.statlearning.FeatureFunction;
import ca.ece.ubc.cpen221.mp5.statlearning.LatFeat;
import ca.ece.ubc.cpen221.mp5.statlearning.LeastSquaresRegression;
import ca.ece.ubc.cpen221.mp5.statlearning.LongFeat;
import ca.ece.ubc.cpen221.mp5.statlearning.PriceFeat;
import ca.ece.ubc.cpen221.mp5.statlearning.StarFeat;

public class RegressionTest {

	private List<FeatureFunction> ffList;
	private RestaurantDB db;
	private FeatureFunction star;
	private FeatureFunction longi;
	private FeatureFunction lat;
	private FeatureFunction price;
	private String testUser = "cywLfetwd4k7gSu5ewNuhw";
	private Set<LeastSquaresRegression> lsrs;
	public final static String workingDir = System.getProperty("user.dir");
	public final static String RESTAURANT_DATA_FILEPATH = workingDir + File.pathSeparator + "data" + File.pathSeparator
			+ "restaurants.json";
	public final static String REVIEW_DATA_FILEPATH = workingDir + File.pathSeparator + "data" + File.pathSeparator
			+ "reviews.json";
	public final static String USER_DATA_FILEPATH = workingDir + File.pathSeparator + "data" + File.pathSeparator
			+ "users.json";


	@Before
	public void initialize() {

		//Works for Toren computer
		/*
		 * db = new RestaurantDB(
		 * "C:\\Users\\Toren\\workspace\\f16-mp5-tdofher\\data\\restaurants.json",
		 * "C:\\Users\\Toren\\workspace\\f16-mp5-tdofher\\data\\reviews.json",
		 * "C:\\Users\\Toren\\workspace\\f16-mp5-tdofher\\data\\users.json");
		 */

		db = new RestaurantDB(RESTAURANT_DATA_FILEPATH,REVIEW_DATA_FILEPATH,USER_DATA_FILEPATH);

		star = new StarFeat();
		longi = new LongFeat();
		lat = new LatFeat();
		price = new PriceFeat();
		ffList = new LinkedList<FeatureFunction>();
		lsrs = new HashSet<LeastSquaresRegression>();

		ffList.add(star);
		ffList.add(longi);
		ffList.add(price);
		ffList.add(lat);

		LeastSquaresRegression lsrStar = Algorithms.getPredictor(testUser, db, star);
		LeastSquaresRegression lsrPrice = Algorithms.getPredictor(testUser, db, price);
		LeastSquaresRegression lsrLat = Algorithms.getPredictor(testUser, db, lat);
		LeastSquaresRegression lsrLong = Algorithms.getPredictor(testUser, db, longi);

		lsrs.add(lsrStar);
		lsrs.add(lsrPrice);
		lsrs.add(lsrLat);
		lsrs.add(lsrLong);
	}

	@Test
	public void Test0() {
		double test = 37.8690986;
		assertEquals(lat.getFeature(db, "ipgnAjJ5TUBWGmGxxzoiGQ"), test, 0);
	}

	@Test
	public void Test1() {
		double test = 2;
		assertEquals(price.getFeature(db, "ipgnAjJ5TUBWGmGxxzoiGQ"), test, 0);
	}

	@Test
	public void Test2() {
		double test = -122.2596091;
		assertEquals(longi.getFeature(db, "ipgnAjJ5TUBWGmGxxzoiGQ"), test, 0);
	}

	@Test
	public void Test3() {
		double test = 3.5;
		assertEquals(star.getFeature(db, "ipgnAjJ5TUBWGmGxxzoiGQ"), test, 0);
	}

	@Test
	public void Test4() {
		assertTrue(lsrs.contains(Algorithms.getBestPredictor(testUser, db, ffList)));
	}

}