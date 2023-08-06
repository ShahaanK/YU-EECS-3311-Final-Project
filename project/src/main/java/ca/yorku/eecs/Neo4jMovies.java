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
			session.writeTransaction(tx -> tx.run("CREATE (a:Actor { name: $name, actorId: $actorId })",
					Values.parameters("name", name, "actorId", actorId)));
			session.close();
		}
	}

	public void addMovie(String name, String movieId) {

		try (Session session = driver.session()) {
			session.writeTransaction(tx -> tx.run("MERGE (m:Movie {id: $movieId, name: $movieName})",
					Values.parameters("movieId", movieId, "movieName", name)));
			session.close();
		}
	}

	public void addRelationship(String actorId, String movieId) {
		try (Session session = driver.session()) {
			session.writeTransaction(tx -> tx.run(
					"MERGE (a:Actor {actorId: $actorId} ) MERGE (m:Movie {movieId: $movieId}) MERGE (a)-[:ACTED_IN]->(m)"),
					(TransactionConfig) Values.parameters("actorId", actorId, "movieId", movieId));
			session.close();
		}
	}

	public void getActor() {

	}

	public Movie getMovie(String movieId) {
	    try (Session session = driver.session()) {
	        return session.readTransaction(new TransactionWork<Movie>() {
	            @Override
	            public Movie execute(Transaction tx) {
	                StatementResult result = tx.run("MATCH (m:Movie {id: $movieId})-[:ACTED_IN]-(a:Actor) RETURN m, collect(a.id) as actors",
	                                       Values.parameters("movieId", movieId));
	                
	                if (!result.hasNext()) {
	                    return null; // or throw an exception if the movie doesn't exist
	                }

	                Record record = result.next();
	                Node movieNode = record.get("m").asNode();
	                List<String> actorIds = record.get("actors").asList(Value::asString);
	                
	                return new Movie(movieNode.get("id").asString(), movieNode.get("name").asString(), actorIds);
	            }
	        });
	    }
	}

	public void hasRelationship() {

	}

	public void computeBaconNumber() {

	}

	public void computeBaconPath() {

	}

}
