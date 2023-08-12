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
			session.writeTransaction(tx -> tx.run("CREATE (a:actor { name: $name, id: $actorId })",
					Values.parameters("name", name, "actorId", actorId)));
			session.close();
		}
	}

	public void addMovie(String name, String movieId) {

		try (Session session = driver.session()) {
			session.writeTransaction(tx -> 
			tx.run("CREATE (m:movie {name: $name, id: $movieId})",
					Values.parameters("movieId", movieId, "name", name)));
			session.close();
		}
	}

	public void addRelationship(String actorId, String movieId) {

		try (Session session = driver.session()) {
			session.writeTransaction(tx -> tx.run("MATCH (a:actor {id: $actorId})\n"
					+ "MATCH (m:movie {id: $movieId})\n"
					+ "MERGE (a)-[:ACTED_IN]->(m)",
					Values.parameters("actorId", actorId, "movieId", movieId)));
			session.close();
		}
	}

	public void addAward(String name, String awardId) {
		try (Session session = driver.session()) {
			session.writeTransaction(tx -> tx.run("CREATE (w:award { name: $name, id: $awardId })",
					Values.parameters("name", name, "awardId", awardId)));
			session.close();
		}
	}

	public void addAwardWinner(String awardId, String movieId) {
		try (Session session = driver.session()) {
			session.writeTransaction(tx -> {
			tx.run("MATCH (w:award {id: $awardId})\n" 
					+ "MATCH (m:movie {id: $movieId})\n"
					+ "MERGE (w)-[:WON_BY]->(m)",
					Values.parameters("awardId", awardId, "movieId", movieId));
			StatementResult result = tx.run("MATCH (w:award {id: $awardId})\n" 
					+ "MATCH (m:movie {id: $movieId})\n"
					+ "MERGE (w)-[:WON_BY]->(m)",
					Values.parameters("awardId", awardId, "movieId", movieId));
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
				tx.run("MATCH (a:actor {id: $actorId})\n"
						+ "OPTIONAL MATCH (a)-[:ACTED_IN]->(m:movie)\n"
						+ "RETURN a.id, a.name, COLLECT(m.id) AS movies",
						Values.parameters("actorId", actorId));

				StatementResult result = tx.run("MATCH (a:actor {id: $actorId})\n"
						+ "OPTIONAL MATCH (a)-[:ACTED_IN]->(m:movie)\n"
						+ "RETURN a.id as actorId, a.name as name, COLLECT(m.id) AS movies",
						Values.parameters("actorId", actorId));
				Record record = result.next();

				JSONObject json = new JSONObject();
				try {
					json.put("actorId", record.get("actorId"));
					json.put("name", record.get("name"));
					json.put("movies", record.get("movies"));
				} catch (JSONException e) {
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
				tx.run("MATCH (m:movie {id: $movieId})\n"
						+ "OPTIONAL MATCH (a:actor)-[:ACTED_IN]->(m)\n"
						+ "RETURN m.name as name, m.id as movieId, collect(a.id) as actors",
						Values.parameters("movieId", movieId));

				StatementResult result = tx.run("MATCH (m:movie {id: $movieId})\n"
						+ "OPTIONAL MATCH (a:actor)-[:ACTED_IN]->(m)\n"
						+ "RETURN m.name as name, m.id as movieId, collect(a.id) as actors",
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
				tx.run("MATCH (a: actor {id: $actorId}), (m: movie {id: $movieId})\n"
						+ "RETURN (a).id as actorId, (m).id as movieId, EXISTS ((a)-[:ACTED_IN]-(m)) as hasRelationship",
						Values.parameters("actorId", actorId, "movieId", movieId));
				StatementResult result = tx.run("MATCH (a: actor {id: $actorId}), (m: movie {id: $movieId})\n"
						+ "RETURN (a).id as actorId, (m).id as movieId, EXISTS ((a)-[:ACTED_IN]-(m)) as hasRelationship",
						Values.parameters("actorId", actorId, "movieId", movieId));
				
				JSONObject json = new JSONObject();
				
				Utils utils = new Utils();
				
				try {
					Record record = result.next();
					json.put("actorId", record.get("actorId"));
					json.put("movieId", record.get("movieId"));
					json.put("hasRelationship", record.get("hasRelationship"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					System.out.print("{\n\t \"actorId\": " + json.get("actorId") + ",\n");
					System.out.print("\t \"movieId\": " + json.get("movieId") + ",\n");
					System.out.print("\t \"hasRelationship\": " + json.get("hasRelationship").toString().toLowerCase() + "\n}");

				} catch (Exception e) {
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
					tx.run("MATCH p=shortestPath((k:actor {id: $kevin})-[ACTED_IN*]-(k:actor {id: $actorId}))\n"
							+ "RETURN length(p) as baconNumber",
							Values.parameters("actorId", actorId, "kevin", "nm0000102"));
					StatementResult result = tx.run("MATCH p=shortestPath((k:actor {id: $kevin})-[ACTED_IN*]-(k:actor {id: $actorId}))\n"
							+ "RETURN length(p) as baconNumber",
							Values.parameters("actorId", actorId, "kevin", "nm0000102"));

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
					tx.run("MATCH p=shortestPath((a:actor {id: $actorId})-[ACTED_IN*]-(k:actor {id: 'nm0000102'}))\n"
							+ "RETURN nodes(p) as baconPath",
							Values.parameters("actorId", actorId));

					StatementResult result = tx.run("MATCH p=shortestPath((a:actor {id: $actorId})-[ACTED_IN*]-(k:actor {id: 'nm0000102'}))\n"
							+ "RETURN nodes(p) as baconPath",
							Values.parameters("actorId", actorId));

					return result;	

				});
				session.close();
			}
		}
	}
	
	public boolean actorIdExists(String actorId) {
	    try (Session session = driver.session()) {

	        if (actorId != null) {
	            StatementResult actorResult = session.writeTransaction(tx -> 
	                tx.run("MATCH (a:actor {id: $actorId})\n" +  
	                		"RETURN COUNT(a) > 0 as existingValue",
	                			Values.parameters("actorId", actorId)));
	            if (actorResult.single().get("existingValue").asBoolean()) {
	                return true;
	            }
	        }
	        return false;
	    }
	}
	
	public boolean movieIdExists(String movieId) {
	    try (Session session = driver.session()) {
	    	
	        if (movieId != null) {
	            StatementResult movieResult = session.writeTransaction(tx -> 
	                tx.run("MATCH (m:movie {id: $movieId})\n" + 
	                		"RETURN COUNT(m) > 0 as existingValue",
	                			Values.parameters("movieId", movieId)));
	            if (movieResult.single().get("existingValue").asBoolean()) {
	                return true;
	            }
	        }
	        return false;
	    }
	}
	
	public boolean awardIdExists(String awardId) {
	    try (Session session = driver.session()) {
	    	
	        if (awardId != null) {
	            StatementResult movieResult = session.writeTransaction(tx -> 
	                tx.run("MATCH (a:award {id: $awardId})\n" + 
	                		"RETURN COUNT(a) > 0 as existingValue",
	                			Values.parameters("awardId", awardId)));
	            if (movieResult.single().get("existingValue").asBoolean()) {
	                return true;
	            }
	        }
	        return false;
	    }
	}
	


}
