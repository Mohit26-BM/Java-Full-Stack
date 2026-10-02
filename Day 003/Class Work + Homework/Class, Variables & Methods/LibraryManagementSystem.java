class LibraryBook {

    int bookId;
    String title;
    String author;
    double price;
    boolean isAvailable;

    static String libraryName = "City Central Library";
    static int totalBooks = 0;

    LibraryBook(int bookId, String title, String author, double price) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.price = price;
        this.isAvailable = true;

        totalBooks++;
    }

    void borrowBook() {
        if (isAvailable) {
            isAvailable = false;
            System.out.println("Book borrowed successfully.");
        } else {
            System.out.println("Book is already borrowed.");
        }
    }

    void returnBook() {
        if (!isAvailable) {
            isAvailable = true;
            System.out.println("Book returned successfully.");
        } else {
            System.out.println("Book is already available.");
        }
    }

    void displayBook() {
        System.out.println("Book ID: " + bookId);
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Price: " + price);

        if (isAvailable) {
            System.out.println("Availability: Available");
        } else {
            System.out.println("Availability: Borrowed");
        }

        System.out.println();
    }

    static void displayLibraryName() {
        System.out.println("Library Name: " + libraryName);
    }

    static void displayTotalBooks() {
        System.out.println("Total Books: " + totalBooks);
    }
}

public class LibraryManagementSystem {

    public static void main(String[] args) {

        LibraryBook.displayLibraryName();

        LibraryBook b1 = new LibraryBook(
                101, "Harry Potter and the Sorcerer's Stone", "J.K. Rowling", 500);

        LibraryBook b2 = new LibraryBook(
                102, "The Hobbit", "J.R.R. Tolkien", 450);

        LibraryBook b3 = new LibraryBook(
                103, "The Name of the Wind", "Patrick Rothfuss", 600);

        LibraryBook b4 = new LibraryBook(
                104, "One Piece", "Eiichiro Oda", 550);

        LibraryBook b5 = new LibraryBook(
                105, "Naruto", "Masashi Kishimoto", 400);

        LibraryBook b6 = new LibraryBook(
                106, "Demon Slayer", "Koyoharu Gotouge", 450);

        System.out.println("\nBook 1 Details:");
        b1.displayBook();

        System.out.println("Book 2 Details:");
        b2.displayBook();

        System.out.println("Book 3 Details:");
        b3.displayBook();

        System.out.println("Book 4 Details:");
        b4.displayBook();

        System.out.println("Book 5 Details:");
        b5.displayBook();

        System.out.println("Book 6 Details:");
        b6.displayBook();

        System.out.println("Borrowing Harry Potter:");
        b1.borrowBook();

        System.out.println("\nTrying to borrow Harry Potter again:");
        b1.borrowBook();

        System.out.println("\nReturning Harry Potter:");
        b1.returnBook();

        System.out.println("\nBorrowing Harry Potter again:");
        b1.borrowBook();

        System.out.println("\nBorrowing One Piece:");
        b4.borrowBook();

        System.out.println("\nReturning One Piece:");
        b4.returnBook();

        System.out.println("\nFinal Book Details:");

        b1.displayBook();
        b4.displayBook();

        LibraryBook.displayTotalBooks();
    }
}