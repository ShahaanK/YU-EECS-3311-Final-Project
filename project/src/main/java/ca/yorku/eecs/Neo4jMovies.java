package ca.yorku.eecs;

import org.neo4j.driver.v1.types.Node;

import java.util.ArrayList;
import java.util.List;
import java.lang.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.Record;

public class Neo4jMovies {

	private Driver driver;
	private String URIdatabase;

	/*
	 * 
	 * INTRODUCING THE DATABASE
	 * based on the videos provided on eClass:
	 * https://www.youtube.com/playlist?list=PLIc63sqj_WAtxX-tYj7zyX9GRYT-IhUFw
	 * 
	 */

	public Neo4jMovies() {
		URIdatabase = "bolt://localhost:7687";
		Config config = Config.build().withoutEncryption().build();
		driver = GraphDatabase.driver(URIdatabase, AuthTokens.basic("neo4j", "12345678"), config);
	}



	/*
	 * 
	 * ADD METHODS
	 * 
	 */

	public void addActor(String name, String actorId) {

		try (Session session = driver.session()) {
			session.writeTransaction(tx -> tx.run("CREATE (a:actor { name: $name, actorId: $actorId })",
					Values.parameters("name", name, "actorId", actorId)));
			session.close();
		}
	}

	public void addMovie(String name, String movieId) {

		try (Session session = driver.session()) {
			session.writeTransaction(tx -> 
			tx.run("MERGE (m:movie {name: $name, movieId: $movieId})",
					Values.parameters("movieId", movieId, "name", name)));
			session.close();
		}
	}

	public void addRelationship(String actorId, String movieId) {

		try (Session session = driver.session()) {
			session.writeTransaction(tx -> tx.run("MATCH (a:actor {actorId: $actorId})\n"
					+ "MATCH (m:movie {movieId: $movieId})\n"
					+ "MERGE (a)-[:ACTED_IN]->(m)",
					Values.parameters("actorId", actorId, "movieId", movieId)));
			session.close();
		}
	}

	public void addAward(String awardId, String name) {
		try (Session session = driver.session()) {
			session.writeTransaction(tx -> tx.run("CREATE (w:award { name: $name, awardId: $awardId })",
					Values.parameters("name", name, "awardId", awardId)));
			session.close();
		}
	}

	public void addAwardWinner(String awardId, String movieId) {
		try (Session session = driver.session()) {
			session.writeTransaction(tx -> {
			tx.run("MATCH (w: award {awardId: $awardId}), (m: movie {movieId: $movieId})\n"
					+ "RETURN (w).awardId as awardId, (m).movieId as movieId, EXISTS ((w)-[:WON_BY]-(m)) as addAwardWinner",
					Values.parameters("awardId", awardId, "movieId", movieId));
			StatementResult result = tx.run("MATCH (a: actor {actorId: $actorId}), (m: movie {movieId: $movieId})\n"
					+ "RETURN (w).awardId as awardId, (m).movieId as movieId, EXISTS ((w)-[:WON_BY]-(m)) as addAwardWinner",
					Values.parameters("awardId", awardId, "movieId", movieId));

			Record record = result.next();

			JSONObject json = new JSONObject();
			try {
				json.put("awardId", record.get("awardId"));
				json.put("movieId", record.get("movieId"));
				json.put("addAwardWinner", record.get("addAwardWinner"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				System.out.print("{\n\t \"awardId\": " + json.get("awardId") + ",\n");
				System.out.print("\t \"movieId\": " + json.get("movieId") + ",\n");
				System.out.print("\t \"addAwardWinner\": " + json.get("addAwardWinner").toString().toLowerCase() + "\n}");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;		
		});
			session.close();
		}
	}
		



	/*
	 * 
	 * GET METHODS
	 * 
	 */

	public void getActor(String actorId) {
		try(Session session = driver.session()){
			session.writeTransaction(tx -> 
			{
				tx.run("MATCH (a:actor {actorId: $actorId})\n"
						+ "OPTIONAL MATCH (a)-[:ACTED_IN]->(m:movie)\n"
						+ "RETURN a.actorId, a.name, COLLECT(m.movieId) AS movies",
						Values.parameters("actorId", actorId));

				StatementResult result = tx.run("MATCH (a:actor {actorId: $actorId})\n"
						+ "OPTIONAL MATCH (a)-[:ACTED_IN]->(m:movie)\n"
						+ "RETURN a.actorId as actorId, a.name as name, COLLECT(m.movieId) AS movies",
						Values.parameters("actorId", actorId));
				Record record = result.next();

				JSONObject json = new JSONObject();
				try {
					json.put("actorId", record.get("actorId"));
					json.put("name", record.get("name"));
					json.put("movies", record.get("movies"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					System.out.print("{\n\t \"actorId\": " + json.get("actorId") + ",\n");
					System.out.print("\t \"name\": " + json.get("name") + ",\n");
					System.out.print("\t \"movies\": [");
					List<Object> newList = new ArrayList<Object>(record.get("movies").asList());
					if (newList.size() > 1) {
						int i = 0;
						System.out.print("\n");
						while (i < newList.size()) {
							System.out.print("\t\t\"" + newList.get(i) + "\"");
							if (i + 1 != newList.size()) {
								System.out.print(",\n");
							}
							i++;
						}
						System.out.print("\n\t]\n}");
					}
					else if (newList.size() == 1) {
						System.out.print("\"" + newList.get(0) + "\"]\n}");
					}
					else {
						System.out.print("]\n}");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return result;
			});
			session.close();
		}

	}

	public void getMovie(String movieId) {

		try(Session session = driver.session()){
			session.writeTransaction(tx -> 
			{
				tx.run("MATCH (m:movie {movieId: $movieId})\n"
						+ "OPTIONAL MATCH (a:actor)-[:ACTED_IN]->(m)\n"
						+ "RETURN m.name as name, m.movieId as movieId, collect(a.actorId) as actors",
						Values.parameters("movieId", movieId));

				StatementResult result = tx.run("MATCH (m:movie {movieId: $movieId})\n"
						+ "OPTIONAL MATCH (a:actor)-[:ACTED_IN]->(m)\n"
						+ "RETURN m.name as name, m.movieId as movieId, collect(a.actorId) as actors",
						Values.parameters("movieId", movieId));

				Record record = result.next();

				JSONObject json = new JSONObject();
				try {
					json.put("movieId", record.get("movieId"));
					json.put("name", record.get("name"));
					json.put("actors", record.get("actors"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					System.out.print("{\n\t \"movieId\": " + json.get("movieId") + ",\n");
					System.out.print("\t \"name\": " + json.get("name") + ",\n");
					System.out.print("\t \"actors\": [");
					List<Object> newList = new ArrayList<Object>(record.get("actors").asList());

					if (newList.size() > 1) {
						int i = 0;
						System.out.print("\n");
						while (i < newList.size()) {
							System.out.print("\t\t\"" + newList.get(i) + "\"");
							if (i + 1 != newList.size()) {
								System.out.print(",\n");
							}
							i++;
						}
						System.out.print("\n\t]\n}");
					}
					else if (newList.size() == 1) {
						System.out.print("\"" + newList.get(0) + "\"]\n}");
					}
					else {
						System.out.print("]\n}");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return result;
			});
			session.close();
		}
	}

	public void hasRelationship(String actorId, String movieId) {

		try (Session session = driver.session()) {
			session.writeTransaction(tx -> {
				tx.run("MATCH (a: actor {actorId: $actorId}), (m: movie {movieId: $movieId})\n"
						+ "RETURN (a).actorId as actorId, (m).movieId as movieId, EXISTS ((a)-[:ACTED_IN]-(m)) as hasRelationship",
						Values.parameters("actorId", actorId, "movieId", movieId));
				StatementResult result = tx.run("MATCH (a: actor {actorId: $actorId}), (m: movie {movieId: $movieId})\n"
						+ "RETURN (a).actorId as actorId, (m).movieId as movieId, EXISTS ((a)-[:ACTED_IN]-(m)) as hasRelationship",
						Values.parameters("actorId", actorId, "movieId", movieId));

				Record record = result.next();

				JSONObject json = new JSONObject();
				try {
					json.put("actorId", record.get("actorId"));
					json.put("movieId", record.get("movieId"));
					json.put("hasRelationship", record.get("hasRelationship"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					System.out.print("{\n\t \"actorId\": " + json.get("actorId") + ",\n");
					System.out.print("\t \"movieId\": " + json.get("movieId") + ",\n");
					System.out.print("\t \"hasRelationship\": " + json.get("hasRelationship").toString().toLowerCase() + "\n}");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return result;		
			});
			session.close();
		}

	}

	public void computeBaconNumber(String actorId) {
		try (Session session = driver.session()) {
			if (!actorId.equals("nm0000102")) {
				session.writeTransaction(tx -> {
					tx.run("MATCH p=shortestPath((a:actor {actorId: $kevin})-[*]-(a:actor {actorId: $actorId}))\n"
							+ "RETURN length(p) as baconNumber",
							Values.parameters("actorId", actorId, "kevin", "nm0000102"));
					StatementResult result = tx.run("MATCH p=shortestPath((a:actor {actorId: $kevin})-[*]-(a:actor {actorId: $actorId}))\n"
							+ "RETURN length(p) as baconNumber",
							Values.parameters("actorId", actorId, "kevin", "nm0000102"));

					Record record = result.single();

					JSONObject json = new JSONObject();
					try {
						json.put("baconNumber", record.get("baconNumber"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						System.out.print("{\n\t \"baconNumber\": " + json.get("baconNumber") + "\n}");

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return result;		
				});
				session.close();
			}
		}
	}

	public void computeBaconPath(String actorId) {
		try (Session session = driver.session()) {
			if (!actorId.equals("nm0000102")) {
				session.writeTransaction(tx -> {
					tx.run("MATCH p=shortestPath((a:actor {actorId: $kevin})-[*]-(a:actor {actorId: $actorId}))\n"
							+ "RETURN p as baconPath",
							Values.parameters("actorId", actorId, "kevin", "nm0000102"));

					StatementResult result = tx.run("MATCH p=shortestPath((a:actor {actorId: $kevin})-[*]-(a:actor {actorId: $actorId}))\n"
							+ "RETURN p as baconPath",
							Values.parameters("actorId", actorId, "kevin", "nm0000102"));

					Record record = result.next();

					System.out.print("{\n\t \"baconPath\": [");
					List<Object> newList = new ArrayList<Object>(record.get("actors").asList());

					if (newList.size() > 1) {
						int i = 0;
						System.out.print("\n");
						while (i < newList.size()) {
							System.out.print("\t\t\"" + newList.get(i) + "\"");
							if (i + 1 != newList.size()) {
								System.out.print(",\n");
							}
							i++;
						}
						System.out.print("\n\t]\n}");
					}
					else if (newList.size() == 1) {
						System.out.print("\"" + newList.get(0) + "\"]\n}");
					}
					else {
						System.out.print("]\n}");
					}

					return result;	

				});
				session.close();
			}
		}
	}
}
