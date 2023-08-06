package ca.yorku.eecs;

import java.util.*;

public class Actor {
	private String actorId;
	private String name;
	private List<String> movies;
	
	public Actor(String actorId, String name, List<String> movies) {
		this.setActorId(actorId);
		this.setName(name);
		this.setMovies(movies);
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getMovies() {
		return movies;
	}

	public void setMovies(List<String> movies) {
		this.movies = movies;
	}
}
