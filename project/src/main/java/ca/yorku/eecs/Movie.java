package ca.yorku.eecs;

import java.util.List;

public class Movie {
	private String id;
	private String name;
	private List<String> actors;

	public Movie(String id, String name, List<String> actorIds) {
		this.setId(id);
		this.setName(name);
		this.setActors(actorIds);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getActors() {
		return actors;
	}

	public void setActors(List<String> actors) {
		this.actors = actors;
	}

}
