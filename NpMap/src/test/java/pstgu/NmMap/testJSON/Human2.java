package pstgu.NmMap.testJSON;

import java.util.List;
import java.util.Map;

import pstgu.NmMap.application.Location;

public class Human2 {

	int id;
	String name;
	String description;

	List<Location> coordinates;

	public Human2(int id, String name, String description, List<Location> coordinates) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.coordinates = coordinates;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<Location> getCoordinates() {
		return coordinates;
	}

}
