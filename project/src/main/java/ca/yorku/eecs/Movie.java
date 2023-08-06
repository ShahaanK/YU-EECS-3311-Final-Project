package ca.yorku.eecs;

import java.util.List;

public class Movie {
	private String id;
	private String name;
	private List<String> actors;

	public Movie(String id, String name, List<String> actorIds) {
		this.id = id;
		this.name = name;
		this.actors = actorIds;
	}

}
