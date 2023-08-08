package ca.yorku.eecs;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
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
				handleGet(request);
				System.out.println("get");
			} else if (request.getRequestMethod().equals("PUT")) {
				handlePut(request);
				System.out.println("put");
			}
		} catch (Exception e) {
			e.printStackTrace();
			sendString(request, "Server error\n", 500);
		}

	}

	public void handleGet(HttpExchange request) throws IOException {

		Neo4jMovies neo4jmovies = new Neo4jMovies();

		URI uriFromRequest = request.getRequestURI();
		String pathFromRequest = uriFromRequest.getPath();
		String queryFromURI = uriFromRequest.getQuery();
		System.out.print(queryFromURI + "\n");

		try {

			Map<String, String> queryParameters = splitQuery(queryFromURI);


			try {
				if (pathFromRequest.equals("/api/v1/getActor")) {
					if (!queryParameters.containsKey("actorId")) {
						sendString(request, "400 BAD REQUEST\n", 400);
					}
					else {
						try {
							neo4jmovies.getActor(queryParameters.get("actorId").toString());
							sendString(request, "200 OK", 200);
						}
						catch (Exception e) {
							e.printStackTrace();
							sendString(request, "404 NOT FOUND", 404);
						}
					}
				}
				else if (pathFromRequest.equals("/api/v1/getMovie")) {
					if (!queryParameters.containsKey("movieId")) {
						sendString(request, "400 BAD REQUEST\n", 400);
					}
					else {
						try {
							neo4jmovies.getMovie(queryParameters.get("movieId").toString());
							sendString(request, "200 OK", 200);
						}
						catch (Exception e) {
							e.printStackTrace();
							sendString(request, "404 NOT FOUND", 404);
						}
					}
				}

				else if (pathFromRequest.equals("/api/v1/hasRelationship")) {
					if (!queryParameters.containsKey("actorId") || !queryParameters.containsKey("movieId")) {
						sendString(request, "400 BAD REQUEST\n", 400);
					}
					else {
						try {
							neo4jmovies.hasRelationship(queryParameters.get("actorId").toString(), 
									queryParameters.get("movieId").toString());
							sendString(request, "200 OK", 200);
						}
						catch (Exception e) {
							e.printStackTrace();
							sendString(request, "404 NOT FOUND", 404);
						}
					}
				}

				else if (pathFromRequest.equals("/api/v1/computeBaconNumber")) {
					if (!queryParameters.containsKey("actorId")) {
						sendString(request, "400 BAD REQUEST\n", 400);
					}
					else {
						try {
							neo4jmovies.computeBaconNumber(queryParameters.get("actorId").toString());
							sendString(request, "200 OK", 200);
						}
						catch (Exception e) {
							e.printStackTrace();
							sendString(request, "404 NOT FOUND", 404);
						}
					}
				}

				else if (pathFromRequest.equals("/api/v1/computeBaconPath")) {
					if (!queryParameters.containsKey("actorId")) {
						sendString(request, "400 BAD REQUEST\n", 400);
					}
					else {
						try {
							neo4jmovies.computeBaconPath(queryParameters.get("actorId").toString());
							sendString(request, "200 OK", 200);
						}
						catch (Exception e) {
							e.printStackTrace();
							sendString(request, "404 NOT FOUND", 404);
						}
					}
				}
			}


			catch (Exception e) {
				e.printStackTrace();
				sendString(request, "Server error\n", 500);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			sendString(request, "400 BAD REQUEST\n", 400);
		}
	}

	//Creating an instance of the Neo4jMovies class to use the public addActor, addMovie, and addRelationship methods
	private Neo4jMovies neo4jMovies;

	public Utils() {
		neo4jMovies = new Neo4jMovies();
	}

	// method to handle PUT requests and update the database
	public void handlePut(HttpExchange request) throws IOException {


		URI uriFromRequest = request.getRequestURI();
		String pathFromRequest = uriFromRequest.getPath();
		String body = convert(request.getRequestBody());

		System.out.print(body + "\n");
		System.out.print(pathFromRequest + "\n");


		try {
			JSONObject deserialized = new JSONObject(body);
			//add: check whether that actor exists
			if (pathFromRequest.equals("/api/v1/addActor")) {
				System.out.print("here1");
				if (!deserialized.has("name") || !deserialized.has("actorId")) {
					sendString(request, "400 BAD REQUEST\n", 400);
					return;
				}

				String name = deserialized.getString("name");
				String actorId = deserialized.getString("actorId");

				neo4jMovies.addActor(name, actorId);
				sendString(request, "200 OK\n", 200);

			} 
			else if (pathFromRequest.equals("/api/v1/addMovie")) {
				//add: check whether that movie exists
				if (!deserialized.has("name") || !deserialized.has("movieId")) {
					sendString(request, "400 BAD REQUEST\n", 400);
					return;
				}

				String movieName = deserialized.getString("name");
				String movieId = deserialized.getString("movieId");

				neo4jMovies.addMovie(movieName, movieId);
				sendString(request, "200 OK\n", 200);

			} 

			else if (pathFromRequest.equals("/api/v1/addRelationship")) {

				if (!deserialized.has("actorId") || !deserialized.has("movieId")) {
					sendString(request, "400 BAD REQUEST\n", 400);
					return;
				}
				String actorId = deserialized.getString("actorId");
				String movieId = deserialized.getString("movieId");

				neo4jMovies.addRelationship(actorId, movieId);
				sendString(request, "200 OK\n", 200);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			sendString(request, "400 BAD REQUEST\n", 400);
		} catch (Exception e) {
			e.printStackTrace();
			sendString(request, "500 INTERNAL SERVER ERROR\n", 500);
		}
	}

}