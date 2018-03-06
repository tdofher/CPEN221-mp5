package ca.ece.ubc.cpen221.mp5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Scanner;

//Code reuse from class FibonacciClient at https://github.com/CPEN-221/example16.git 
//including methods, comments and function
/**
 * RestaurantDB Client is a client that sends requests to the RestaurantDBServer
 * and interprets its replies. A new RestaurantDBClient is "open" until the
 * close() method is called, at which point it is "closed" and may not be used
 * further.
 */
public class RestaurantDBClient {
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	// Rep invariant: socket, in, out != null

	/**
	 * Make a RestaurantDBClient and connect it to a server running on hostname
	 * at the specified port.
	 * 
	 * @throws IOException
	 *             if can't connect
	 */
	public RestaurantDBClient(String hostname, int port) throws IOException {
		socket = new Socket(hostname, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	/**
	 * Send a request to the server. Requires this is "open".
	 * 
	 * @param command
	 *            the request to be executed by the server
	 * @throws IOException
	 *             if network or server failure
	 */
	public void sendRequest(String command) throws IOException {
		out.print(command + "\n");
		out.flush(); // important! make sure command actually gets sent
	}

	/**
	 * Get a reply from the next request that was submitted. Requires this is
	 * "open".
	 * 
	 * @return the requested information, depending on the command
	 * @throws IOException
	 *             if network or server failure
	 */
	public String getReply() throws IOException {
		String reply = in.readLine();
		if (reply == null) {
			throw new IOException("connection terminated unexpectedly");
		}

		try {
			return reply;
		} catch (NumberFormatException nfe) {
			throw new IOException("misformatted reply: " + reply);
		}
	}

	/**
	 * Closes the client's connection to the server. This client is now
	 * "closed". Requires this is "open".
	 * 
	 * @throws IOException
	 *             if close fails
	 */
	public void close() throws IOException {
		in.close();
		out.close();
		socket.close();
	}

	/**
	 * Creates a new RestaurantDB client connected to RESTAURANT_PORT on the
	 * localhost. Reads user inputed commands to be outputted to the server
	 * until the user enters CLOSE
	 */
	public static void main(String[] args) {
		try {
			RestaurantDBClient client = new RestaurantDBClient("localhost", RestaurantDBServer.RESTAURANT_PORT);

			// From
			// http://javadevnotes.com/java-tutorial-read-input-from-console
			Scanner scanner = new Scanner(System.in);

			System.out.print("Enter a command (enter 'CLOSE' to terminate): ");
			String command = scanner.nextLine();
			// Keeps asking user for commands until they choose the close
			// command
			while (command.equals("CLOSE") == false) {
				// System.out.println("Your command is: " + command);
				client.sendRequest(command);
				String y = client.getReply();
				System.out.println(y);
				System.out.print("Enter a command (enter 'CLOSE' to terminate): ");
				command = scanner.nextLine();
			}

			client.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
