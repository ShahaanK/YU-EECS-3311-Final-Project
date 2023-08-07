package ca.yorku.eecs;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ca.yorku.eecs.Neo4jMovies;


//added implements HttpHandler
public class Utils implements HttpHandler {
	// use for extracting query params
	public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
					URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}
		return query_pairs;
	}

	// one possible option for extracting JSON body as String
	public static String convert(InputStream inputStream) throws IOException {

		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			return br.lines().collect(Collectors.joining(System.lineSeparator()));
		}
	}

	// another option for extracting JSON body as String
	public static String getBody(HttpExchange he) throws IOException {
		InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
		BufferedReader br = new BufferedReader(isr);

		int b;
		StringBuilder buf = new StringBuilder();
		while ((b = br.read()) != -1) {
			buf.append((char) b);
		}

		br.close();
		isr.close();

		return buf.toString();
	}

	// added from the example
	private void sendString(HttpExchange request, String data, int restCode) throws IOException {
		request.sendResponseHeaders(restCode, data.length());
		OutputStream os = request.getResponseBody();
		os.write(data.getBytes());
		os.close();
	}

	public void handle(HttpExchange request) throws IOException {

		try {
			if (request.getRequestMethod().equals("GET")) {
				getBody(request);
				System.out.println("get");
			} else if (request.getRequestMethod().equals("PUT")) {
				getBody(request);
				System.out.println("put");
			}
		} catch (Exception e) {
			e.printStackTrace();
			sendString(request, "Server error\n", 500);
		}

	}

	public void handleGet(HttpExchange request) throws IOException {
		String body = Utils.convert(request.getRequestBody());
		try {
			JSONObject deserialized = new JSONObject(body);

			String actorId, actorName, movieId, movieName;

			if (request.getRequestMethod().equals("GET") && (deserialized.length() == 1 || deserialized.length() == 2)
					&& (deserialized.has("actorId") || deserialized.has("actorName") || deserialized.has("movieId")
							|| deserialized.has("movieName"))) {
				actorId = deserialized.has("actorId") ? deserialized.getString("actorId") : null;
				actorName = deserialized.has("actorName") ? deserialized.getString("actorName") : null;
				movieId = deserialized.has("movieId") ? deserialized.getString("movieId") : null;
				movieName = deserialized.has("movieName") ? deserialized.getString("movieName") : null;
			} else {
				request.sendResponseHeaders(400, -1);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Creating an instance of the Neo4jMovies class to use the public addActor, addMovie, and addRelationship methods
	private Neo4jMovies neo4jMovies;
	
	public Utils() {
		neo4jMovies = new Neo4jMovies();
	}
	
	
	// booleam function that checks if the request is a PUT method
	private boolean isPutMethod(HttpExchange request) {
		return "PUT".equalsIgnoreCase(request.getRequestMethod());
	}

	// method to handle PUT requests and update the database
	public void handlePut(HttpExchange request) throws IOException {
		String path = request.getRequestURI().getPath();
		String body = Utils.convert(request.getRequestBody());

		try {
			JSONObject deserialized = new JSONObject(body);

			if (path.equals("/actor")) {
				// Validate and extract actor details
				if (!deserialized.has("name") || !deserialized.has("actorId")) {
					request.sendResponseHeaders(400, -1); // Bad Request
					return;
				}
				String name = deserialized.getString("name");
				String actorId = deserialized.getString("actorId");

				// Update actor
				neo4jMovies.addActor(name, actorId);
				request.sendResponseHeaders(200, -1); // OK

			} else if (path.equals("/movie")) {
				// Validate and extract movie details
				if (!deserialized.has("movieName") || !deserialized.has("movieId")) {
					request.sendResponseHeaders(400, -1); // Bad Request
					return;
				}
				String movieName = deserialized.getString("movieName");
				String movieId = deserialized.getString("movieId");

				// Update movie
				neo4jMovies.addMovie(movieName, movieId);
				request.sendResponseHeaders(200, -1); // OK

			} else if (path.equals("/relationship")) {
				// Validate and extract relationship details
				if (!deserialized.has("actorId") || !deserialized.has("movieId")) {
					request.sendResponseHeaders(400, -1); // Bad Request
					return;
				}
				String actorId = deserialized.getString("actorId");
				String movieId = deserialized.getString("movieId");

				// Update relationship
				neo4jMovies.addRelationship(actorId, movieId);
				request.sendResponseHeaders(200, -1); // OK

			} else {
				request.sendResponseHeaders(404, -1); // Not Found
			}

		} catch (JSONException e) {
			// Handle any JSON parsing exceptions
			e.printStackTrace();
			request.sendResponseHeaders(400, -1); // Bad Request
		} catch (Exception e) {
			// Handle other exceptions
			e.printStackTrace();
			request.sendResponseHeaders(500, -1); // Internal Server Error
		}
	}

}