package main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import configs.BookConfigs;
import springcoreexamples.model.Author;
import springcoreexamples.model.Book;
import springcoreexamples.model.Publisher;

public class TestSpring {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(BookConfigs.class);

        Book book1 = context.getBean("book1", Book.class);
        Book book2 = context.getBean("book2", Book.class);

        Author author = context.getBean(Author.class);
        Author author2 = context.getBean(Author.class);

        Publisher publisher = context.getBean(Publisher.class);
        Publisher publisher2 = context.getBean(Publisher.class);

        System.out.println("Book 1: " + book1);
        System.out.println("Book 2: " + book2);

        System.out.println("Author 1: " + author);
        System.out.println("Author 2: " + author2);

        System.out.println("Publisher 1: " + publisher);
        System.out.println("Publisher 2: " + publisher2);

        context.close();
    }
}
