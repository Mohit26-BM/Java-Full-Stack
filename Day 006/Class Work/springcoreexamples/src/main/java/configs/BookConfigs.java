package configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import springcoreexamples.model.Author;
import springcoreexamples.model.Book;
import springcoreexamples.model.Publisher;

@Configuration
public class BookConfigs {

    @Bean
    public Author author() {
        return new Author(
            "F. Scott Fitzgerald",
            "Novel",
            "USA"
        );
    }

    @Bean
    public Publisher publisher() {
        return new Publisher(
            "Scribner",
            "New York",
            "USA"
        );
    }

    @Autowired
    Author author;

    @Autowired
    Publisher publisher;

    @Bean(name = "book1")
    @Scope("prototype")
    public Book getBook1() {
        return new Book(
            "978-3-16-148410-0",
            "The Great Gatsby",
            author.getAuthor(),
            "1925",
            publisher.getName(),
            10.99
        );
    }

    @Bean(name = "book2")
    @Scope("prototype")
    public Book getBook2() {
        return new Book(
            "978-0-14-028333-4",
            "To Kill a Mockingbird",
            author.getAuthor(),
            "1960",
            publisher.getName(),
            12.99
        );
    }
}
