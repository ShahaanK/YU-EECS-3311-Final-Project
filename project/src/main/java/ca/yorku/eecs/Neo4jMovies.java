package ca.yorku.eecs;

import org.neo4j.driver.v1.types.Node;

import java.util.List;

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

	public void addActor(String actorName, String actorId) {

		try (Session session = driver.session()) {
			session.writeTransaction(tx -> tx.run("CREATE (a:actor { name: $actorName, actorId: $actorId })",
					Values.parameters("actorName", actorName, "actorId", actorId)));
			session.close();
		}
	}

	public void addMovie(String movieName, String movieId) {

		try (Session session = driver.session()) {
			session.writeTransaction(tx -> 
			tx.run("MERGE (m:movie {name: $movieName, movieId: $movieId})",
					Values.parameters("movieId", movieId, "movieName", movieName)));
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
	
	public void addAward(String award) {
		try (Session session = driver.session()) {
			session.writeTransaction(tx -> tx.run("CREATE (w:award {award: $award})",
					Values.parameters("award", award)));
			session.close();
		}
	}

	public void addAwardWinner(String award, String movieId) {
		try (Session session = driver.session()) {
			session.writeTransaction(tx -> 
			tx.run("MATCH (w: award {award: $award}), (m: movie {movieId: $movieId})\n"
					+ "RETURN award as award, movieId as movieId, EXISTS ((w)-[:WON_BY]-(m)) as addAwardWinner",
					Values.parameters("award", award, "movieId", movieId)));
			session.close();
		}
	}
	
	
	/*
	 * 
	 * GET METHODS
	 * 
	 */

	/*
	public Actor getActor(String actorId) {
		try(Session session = driver.session()){
			StatementResult info = session.writeTransaction(tx -> tx.run("MATCH (a:actor {actorId: $actorId} OPTIONAL MATCH (a)-[:ACTED_IN]->(m:Movie) RETURN a.actorId, a.name, COLLECT(m.movieId) AS movies"),
					(TransactionConfig) Values.parameters("actorId", actorId));
			session.close();

			if(info.hasNext()) {
				Record actor = info.next();
				return new Actor(actorId, actor.get("a.name").toString(), actor.get("movies").asList(Value::asString));
			}
			else {
				return null;
			}

		}
	}
	*/
	
	public void getActor(String actorId) {
		try(Session session = driver.session()){
			session.writeTransaction(tx -> 
			{
			tx.run("MATCH (a:actor {actorId: $actorId})\n"
					+ "OPTIONAL MATCH (a)-[:ACTED_IN]->(m:movie)\n"
					+ "RETURN a.actorId, a.name, COLLECT(m.movieId) AS movies",
					Values.parameters("actorId", actorId));
			
			return tx.run("MATCH (a:actor {actorId: $actorId})\n"
					+ "OPTIONAL MATCH (a)-[:ACTED_IN]->(m:movie)\n"
					+ "RETURN a.actorId, a.name, COLLECT(m.movieId) AS movies",
					Values.parameters("actorId", actorId)).single().get("_fields");
			});
			session.close();
		}
		
	}

	public void getMovie(String movieId) {
		
		try(Session session = driver.session()){
			session.writeTransaction(tx -> 
			{
			tx.run("MATCH (m:movie {id: $movieId})-[:ACTED_IN]-(a:Actor)\n"
					+ "RETURN m, collect(a.id) as actors",
					Values.parameters("movieId", movieId));
			
			return tx.run("MATCH (m:movie {id: $movieId})-[:ACTED_IN]-(a:Actor)\n"
					+ "RETURN m, collect(a.id) as actors",
					Values.parameters("movieId", movieId)).single().get("_fields");
			});
			session.close();
		}
	}

	public void hasRelationship(String actorId, String movieId) {

		try (Session session = driver.session()) {
			session.writeTransaction(tx -> 
			tx.run("MATCH (a: actor {actorId: $actorId}), (m: movie {movieId: $movieId})\n"
					+ "RETURN actorId as actorId, movieId as movieId, EXISTS ((a)-[:ACTED_IN]-(m)) as hasRelationship",
					Values.parameters("actorName", actorId, "movieName", movieId)));
			session.close();
		}

	}

	public void computeBaconNumber(String actorId) {
		try (Session session = driver.session()) {
			if (!actorId.equals("nm0000102")) {
				session.writeTransaction(tx -> 
				tx.run("MATCH p=shortestPath((a:actor {actorId: nm0000102})-[*]-(a:actor {actorId: $actorId}))\n"
						+ "RETURN length(p) as baconNumber",
						Values.parameters("actorId", actorId)));
				session.close();
			}
		}
	}

	public void computeBaconPath(String actorId) {
		try (Session session = driver.session()) {
			if (!actorId.equals("nm0000102")) {
				session.writeTransaction(tx -> 
				tx.run("MATCH p=shortestPath((a:actor {actorId: nm0000102})-[*]-(a:actor {actorId: $actorId}))\n"
						+ "RETURN p as baconPath",
						Values.parameters("actorId", actorId)));
				session.close();
			}
		}
	}
}
