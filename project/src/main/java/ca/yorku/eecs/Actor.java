package ca.yorku.eecs;

import java.util.*;

public class Actor {
	private String actorId;
	private String name;
	private List<String> movies;
	
	public Actor(String actorId, String name, List<String> movies) {
		this.actorId = actorId;
		this.name = name;
		this.movies = movies;
	}
}
