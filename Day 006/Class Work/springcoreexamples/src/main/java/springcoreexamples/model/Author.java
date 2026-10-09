package springcoreexamples.model;

public class Author {
	String author;
	String country;
	String genre;
	public Author() {

		super();
	}
	public Author(String author, String genre, String country) {
		super();
		this.author = author;
		this.genre = genre;
		this.country = country;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	@Override
	public String toString() {
		return "Author [author=" + author + ", country=" + country + ", genre=" + genre + "]";
	}
	
}