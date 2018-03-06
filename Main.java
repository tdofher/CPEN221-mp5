package ca.ece.ubc.cpen221.mp5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;


public class Main {

	public static void main(String args[]) {

		long startTime = System.nanoTime();

		RestaurantDB res = new RestaurantDB("C:\\Users\\Toren\\workspace\\f16-mp5-tdofher\\data\\restaurants.json",
				"C:\\Users\\Toren\\workspace\\f16-mp5-tdofher\\data\\reviews.json",
				"C:\\Users\\Toren\\workspace\\f16-mp5-tdofher\\data\\users.json");

		System.out.println("Runtime for creating map: " + (System.nanoTime() - startTime) / 1000000 + "ms");

	}

}