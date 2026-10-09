package model;

public class Movie {

    String id;
    String name;
    String director;
    double price;

    public Movie() {
    }

    public Movie(String id, String name, String director, double price) {
        this.id = id;
        this.name = name;
        this.director = director;
        this.price = price;
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

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Movie [id=" + id + ", name=" + name + ", director=" + director + ", price=" + price + "]";
    }
}