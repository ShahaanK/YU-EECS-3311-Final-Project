package ca.yorku.eecs;

import org.neo4j.driver.v1.*;

public class Neo4jMovies {

	private Driver driver;
	private String URIdatabase;

	//introducing the database for the program
	//based on the videos provided on eClass: https://www.youtube.com/playlist?list=PLIc63sqj_WAtxX-tYj7zyX9GRYT-IhUFw 
	public Neo4jMovies() {
		URIdatabase = "bolt://localhost:7687";
		Config config = Config.build().withoutEncryption().build();
		driver = GraphDatabase.driver(URIdatabase, AuthTokens.basic("neo4j", "12345678"), config);
	}

	//here: methods

	public void addActor() {

	}

	public void addMovie() {

	}
	public void addRelationship(String actorId, String movieId) {
		try(Session session = driver.session()){
			session.writeTransaction(tx -> tx.run("MERGE (a:Actor {actorId: $actorId} ) MERGE (m:Movie {movieId: $movieId}) MERGE (a)-[:ACTED_IN]->(m)"), (TransactionConfig) Values.parameters("actorId", actorId, "movieId", movieId ));
			
			session.close();
			
		}
	}
	public void getActor() {

	}
	public void getMovie() {

	}
	public void hasRelationship() {

	}
	public void computeBaconNumber() {

	}
	public void computeBaconPath() {

	}

}
