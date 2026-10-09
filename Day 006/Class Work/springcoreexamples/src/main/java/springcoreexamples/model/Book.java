package springcoreexamples.model;

public class Book {
	
	String isbn;
	String title;
	String author;
	String Year;
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getYear() {
		return Year;
	}

	public void setYear(String year) {
		Year = year;
	}

	String publisher;
	double price;
	
	public Book() {
		
	}

	public Book(String isbn, String title, String author, String year, String publisher, double price) {
		super();
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		Year = year;
		this.publisher = publisher;
		this.price = price;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Book [isbn=" + isbn + ", title=" + title + ", author=" + author + ", Year=" + Year + ", publisher="
				+ publisher + ", price=" + price + "]";
	}

	

}