package springcoreexamples.model;

public class Publisher {
	String name;
	String location;
	String country;
	
	public Publisher() {
		super();
	}
	
	public Publisher(String name, String location, String country) {
		super();
		this.name = name;
		this.location = location;
		this.country = country;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	@Override
	public String toString() {
		return "Publisher [name=" + name + ", location=" + location + ", country=" + country + "]";
	}
}