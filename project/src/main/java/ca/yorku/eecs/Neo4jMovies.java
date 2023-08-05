package ca.yorku.eecs;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

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

}
