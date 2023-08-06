package ca.yorku.eecs;

import org.neo4j.driver.v1.types.Node;

import java.util.List;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.Record;

public class Neo4jMovies {

	private Driver driver;
	private String URIdatabase;

	// introducing the database for the program
	// based on the videos provided on eClass:
	// https://www.youtube.com/playlist?list=PLIc63sqj_WAtxX-tYj7zyX9GRYT-IhUFw
	public Neo4jMovies() {
		URIdatabase = "bolt://localhost:7687";
		Config config = Config.build().withoutEncryption().build();
		driver = GraphDatabase.driver(URIdatabase, AuthTokens.basic("neo4j", "12345678"), config);
	}

	// here: methods

	public void addActor(String name, String actorId) {
		try (Session session = driver.session()) {
			session.writeTransaction(tx -> tx.run("CREATE (a:actor { name: $name, actorId: $actorId })",
					Values.parameters("name", name, "actorId", actorId)));
			session.close();
		}
	}

	public void addMovie(String movieName, String movieId) {

		try (Session session = driver.session()) {
			session.writeTransaction(tx -> 
			tx.run("MERGE (m:movie {id: $movieId, name: $movieName})",
					Values.parameters("movieId", movieId, "movieName", movieName)));
			session.close();
		}
	}

	public void addRelationship(String actorId, String movieId) {
		
		try(Session session = driver.session()){
			session.writeTransaction(tx -> tx.run("MERGE (a:Actor {actorId: $actorId} ) MERGE (m:Movie {movieId: $movieId}) MERGE (a)-[:ACTED_IN]->(m)"), 
					(TransactionConfig) Values.parameters("actorId", actorId, "movieId", movieId ));
			session.close();

		}
	}

	public Actor getActor(String actorId) {
		try(Session session = driver.session()){
			StatementResult info = session.writeTransaction(tx -> tx.run("MATCH (a:Actor {actorId: $actorId} OPTIONAL MATCH (a)-[:ACTED_IN]->(m:Movie) RETURN a.actorId, a.name, COLLECT(m.movieId) AS movies"),
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

	public Movie getMovie(String movieId) {
	    try (Session session = driver.session()) {
	        return session.readTransaction(new TransactionWork<Movie>() {
	            @Override
	            public Movie execute(Transaction tx) {
	                StatementResult result = tx.run("MATCH (m:Movie {id: $movieId})-[:ACTED_IN]-(a:Actor) RETURN m, collect(a.id) as actors",
	                                       Values.parameters("movieId", movieId));
	                
	                //if statement ensures that data processing only occurs when there's actual data present
	                if (!result.hasNext()) {
	                    return null; // or throw an exception if the movie doesn't exist
	                }

	                Record info = result.next();
	                
	                return new Movie(info.get("m").asNode().get("id").asString(), info.get("m").asNode().get("name").asString(), info.get("actors").asList(Value::asString));
	            }
	        });
	    }
	}

	public void hasRelationship() {

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
