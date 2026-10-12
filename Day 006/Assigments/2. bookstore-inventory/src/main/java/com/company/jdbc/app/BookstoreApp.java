package com.company.jdbc.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.dao.AuthorDao;
import com.company.jdbc.dao.AuthorDaoImpl;
import com.company.jdbc.dao.BookDao;
import com.company.jdbc.dao.BookDaoImpl;
import com.company.jdbc.dao.CategoryDao;
import com.company.jdbc.dao.CategoryDaoImpl;
import com.company.jdbc.exception.AuthorNotFoundException;
import com.company.jdbc.exception.BookNotFoundException;
import com.company.jdbc.exception.CategoryNotFoundException;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.exception.DuplicateBookException;
import com.company.jdbc.exception.DuplicateCategoryException;
import com.company.jdbc.exception.EntityInUseException;
import com.company.jdbc.exception.InsufficientStockException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.Author;
import com.company.jdbc.model.Book;
import com.company.jdbc.model.Category;
import com.company.jdbc.model.RestockRequest;
import com.company.jdbc.service.AuthorService;
import com.company.jdbc.service.AuthorServiceImpl;
import com.company.jdbc.service.BookService;
import com.company.jdbc.service.BookServiceImpl;
import com.company.jdbc.service.CategoryService;
import com.company.jdbc.service.CategoryServiceImpl;

public class BookstoreApp {

    private static final String LINE =
            "------------------------------------------------------------"
            + "----------------------------------------------";

    private static BookService bookService;
    private static AuthorService authorService;
    private static CategoryService categoryService;

    public static void main(String[] args) {

        /*
        Manual dependency injection: DAO -> Service.
        */
        AuthorDao authorDao = new AuthorDaoImpl();
        CategoryDao categoryDao = new CategoryDaoImpl();
        BookDao bookDao = new BookDaoImpl();

        authorService = new AuthorServiceImpl(authorDao);
        categoryService = new CategoryServiceImpl(categoryDao);
        bookService = new BookServiceImpl(bookDao, authorDao, categoryDao);

        while (true) {

            showMenu();

            int choice = ConsoleInput.readPositiveInt("Enter your choice: ");

            if (choice == 14) {
                System.out.println("\nThank you for using the Bookstore Inventory.");
                return;
            }

            /*
            A database problem is reported and the menu continues
            instead of the program crashing.
            */
            try {

                handleChoice(choice);

            } catch (DataAccessException e) {

                System.out.println("\nDatabase Error: " + e.getMessage());

                if (e.getCause() != null) {
                    System.out.println("Cause: " + e.getCause().getMessage());
                }

            } catch (IllegalStateException e) {

                System.out.println("\nConfiguration Error: " + e.getMessage());
            }
        }
    }

    private static void showMenu() {

        System.out.println("\n========================================");
        System.out.println("      ONLINE BOOKSTORE INVENTORY");
        System.out.println("========================================");
        System.out.println(" BOOKS");
        System.out.println("  1. Add Book");
        System.out.println("  2. View All Books");
        System.out.println("  3. View Book Details");
        System.out.println("  4. Update Book");
        System.out.println("  5. Delete Book");
        System.out.println(" SEARCH");
        System.out.println("  6. Search by Title");
        System.out.println("  7. Search by Category / Price Range");
        System.out.println(" STOCK");
        System.out.println("  8. Increase Stock");
        System.out.println("  9. Decrease Stock");
        System.out.println(" 10. Bulk Restock (batch)");
        System.out.println(" 11. Low-Stock Report");
        System.out.println(" MANAGE");
        System.out.println(" 12. Authors");
        System.out.println(" 13. Categories");
        System.out.println(" 14. Exit");
        System.out.println("========================================");
    }

    private static void handleChoice(int choice) {

        switch (choice) {
            case 1:  addBook();             break;
            case 2:  viewAllBooks();        break;
            case 3:  viewBookDetails();     break;
            case 4:  updateBook();          break;
            case 5:  deleteBook();          break;
            case 6:  searchByTitle();       break;
            case 7:  searchByCategoryAndPrice(); break;
            case 8:  increaseStock();       break;
            case 9:  decreaseStock();       break;
            case 10: bulkRestock();         break;
            case 11: lowStockReport();      break;
            case 12: manageAuthors();       break;
            case 13: manageCategories();    break;
            default:
                System.out.println("\nInvalid choice. Please select 1 to 14.");
        }
    }

    // =========================================================
    // BOOKS
    // =========================================================

    private static void addBook() {

        System.out.println("\n========== Add Book ==========");

        String isbn = ConsoleInput.readLine("ISBN (10 or 13 digits): ");
        String title = ConsoleInput.readLine("Title: ");

        printAuthors(authorService.getAllAuthors());
        int authorId = ConsoleInput.readPositiveInt("Author ID: ");

        printCategories(categoryService.getAllCategories());
        int categoryId = ConsoleInput.readPositiveInt("Category ID: ");

        BigDecimal price = ConsoleInput.readMoney("Price: Rs. ");
        int stock = ConsoleInput.readNonNegativeInt("Opening stock: ");

        Book book = new Book(isbn, title, authorId, categoryId, price, stock);

        try {

            int bookId = bookService.addBook(book);

            System.out.println("\nBook added successfully. Book ID: " + bookId);

            printBookDetails(bookService.getBookById(bookId));

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());

        } catch (AuthorNotFoundException
                 | CategoryNotFoundException
                 | DuplicateBookException
                 | BookNotFoundException e) {

            System.out.println("\nError: " + e.getMessage());
        }
    }

    private static void viewAllBooks() {

        System.out.println("\n========== All Books ==========");

        printBookTable(bookService.getAllBooks());
    }

    private static void viewBookDetails() {

        System.out.println("\n========== Book Details ==========");

        int bookId = ConsoleInput.readPositiveInt("Book ID: ");

        try {

            printBookDetails(bookService.getBookById(bookId));

        } catch (BookNotFoundException e) {

            System.out.println("\n" + e.getMessage());
        }
    }

    private static void updateBook() {

        System.out.println("\n========== Update Book ==========");

        int bookId = ConsoleInput.readPositiveInt("Book ID: ");

        try {

            Book book = bookService.getBookById(bookId);

            printBookDetails(book);

            System.out.println("\nPress Enter to keep the current value.");
            System.out.println("(Use the Stock menu to change stock.)");

            String isbn = ConsoleInput.readLine("New ISBN: ");
            String title = ConsoleInput.readLine("New title: ");

            printAuthors(authorService.getAllAuthors());
            Integer authorId = ConsoleInput.readOptionalPositiveInt("New author ID: ");

            printCategories(categoryService.getAllCategories());
            Integer categoryId = ConsoleInput.readOptionalPositiveInt("New category ID: ");

            BigDecimal price = ConsoleInput.readOptionalMoney("New price: Rs. ");

            if (!isbn.isEmpty())      book.setIsbn(isbn);
            if (!title.isEmpty())     book.setTitle(title);
            if (authorId != null)     book.setAuthorId(authorId);
            if (categoryId != null)   book.setCategoryId(categoryId);
            if (price != null)        book.setPrice(price);

            bookService.updateBook(book);

            System.out.println("\nBook updated successfully.");

            printBookDetails(bookService.getBookById(bookId));

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());

        } catch (BookNotFoundException
                 | AuthorNotFoundException
                 | CategoryNotFoundException
                 | DuplicateBookException e) {

            System.out.println("\nError: " + e.getMessage());
        }
    }

    private static void deleteBook() {

        System.out.println("\n========== Delete Book ==========");

        int bookId = ConsoleInput.readPositiveInt("Book ID: ");

        try {

            printBookDetails(bookService.getBookById(bookId));

            if (!ConsoleInput.readYes("\nDelete this book? (y/n): ")) {
                System.out.println("Delete cancelled.");
                return;
            }

            bookService.deleteBook(bookId);

            System.out.println("\nBook deleted successfully.");

        } catch (BookNotFoundException e) {

            System.out.println("\n" + e.getMessage());
        }
    }

    // =========================================================
    // SEARCH
    // =========================================================

    private static void searchByTitle() {

        System.out.println("\n========== Search by Title ==========");

        String keyword = ConsoleInput.readLine("Part of the title: ");

        try {

            List<Book> books = bookService.searchByTitle(keyword);

            System.out.println("\n" + books.size()
                    + " book(s) with '" + keyword.trim() + "' in the title:");

            printBookTable(books);

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());
        }
    }

    private static void searchByCategoryAndPrice() {

        System.out.println("\n========== Search by Category / Price ==========");
        System.out.println("Press Enter to skip any filter.");

        printCategories(categoryService.getAllCategories());

        Integer categoryId =
                ConsoleInput.readOptionalPositiveInt("Category ID: ");

        BigDecimal minPrice =
                ConsoleInput.readOptionalMoney("Minimum price: Rs. ");

        BigDecimal maxPrice =
                ConsoleInput.readOptionalMoney("Maximum price: Rs. ");

        try {

            List<Book> books =
                    bookService.search(categoryId, minPrice, maxPrice);

            System.out.println("\n" + books.size() + " book(s) found:");

            printBookTable(books);

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());

        } catch (CategoryNotFoundException e) {

            System.out.println("\nError: " + e.getMessage());
        }
    }

    // =========================================================
    // STOCK
    // =========================================================

    private static void increaseStock() {

        System.out.println("\n========== Increase Stock ==========");

        int bookId = ConsoleInput.readPositiveInt("Book ID: ");
        int quantity = ConsoleInput.readPositiveInt("Quantity to add: ");

        try {

            int newStock = bookService.increaseStock(bookId, quantity);

            System.out.println("\nStock increased. New stock: " + newStock);

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());

        } catch (BookNotFoundException e) {

            System.out.println("\nError: " + e.getMessage());
        }
    }

    private static void decreaseStock() {

        System.out.println("\n========== Decrease Stock ==========");

        int bookId = ConsoleInput.readPositiveInt("Book ID: ");
        int quantity = ConsoleInput.readPositiveInt("Quantity to remove: ");

        try {

            int newStock = bookService.decreaseStock(bookId, quantity);

            System.out.println("\nStock decreased. New stock: " + newStock);

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());

        } catch (BookNotFoundException
                 | InsufficientStockException e) {

            System.out.println("\nRejected: " + e.getMessage());
        }
    }

    private static void bulkRestock() {

        System.out.println("\n========== Bulk Restock (Batch) ==========");
        System.out.println("Enter one book at a time. Leave Book ID blank to finish.");

        List<RestockRequest> requests = new ArrayList<>();

        while (true) {

            Integer bookId =
                    ConsoleInput.readOptionalPositiveInt("\nBook ID: ");

            if (bookId == null) {
                break;
            }

            int quantity = ConsoleInput.readPositiveInt("Quantity to add: ");

            requests.add(new RestockRequest(bookId, quantity));
        }

        if (requests.isEmpty()) {
            System.out.println("Nothing to restock.");
            return;
        }

        try {

            int[] updateCounts = bookService.bulkRestock(requests);

            System.out.println("\nBatch executed. Update counts:");
            System.out.println("-------------------------");

            for (int i = 0; i < requests.size(); i++) {

                RestockRequest request = requests.get(i);

                System.out.println(
                        "Book " + request.getBookId()
                        + "  +" + request.getQuantity()
                        + "  -> update count = " + updateCounts[i]
                );
            }

            System.out.println("-------------------------");

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());

        } catch (BookNotFoundException e) {

            System.out.println("\nError: " + e.getMessage()
                    + " The batch was not sent.");
        }
    }

    private static void lowStockReport() {

        System.out.println("\n========== Low-Stock Report ==========");

        int defaultThreshold = bookService.getDefaultLowStockThreshold();

        Integer threshold = ConsoleInput.readOptionalPositiveInt(
                "Threshold (Enter for default " + defaultThreshold + "): ");

        if (threshold == null) {
            threshold = defaultThreshold;
        }

        try {

            List<Book> books = bookService.getLowStockReport(threshold);

            System.out.println("\nBooks with stock BELOW " + threshold
                    + ": " + books.size());

            printBookTable(books);

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());
        }
    }

    // =========================================================
    // AUTHORS
    // =========================================================

    private static void manageAuthors() {

        while (true) {

            System.out.println("\n========== Authors ==========");
            System.out.println("1. List  2. Add  3. Update  4. Delete  5. Back");

            int choice = ConsoleInput.readPositiveInt("Choice: ");

            try {

                switch (choice) {

                    case 1:
                        printAuthors(authorService.getAllAuthors());
                        break;

                    case 2: {
                        String name = ConsoleInput.readLine("Author name: ");
                        int id = authorService.addAuthor(name);
                        System.out.println("Author added. ID: " + id);
                        break;
                    }

                    case 3: {
                        int id = ConsoleInput.readPositiveInt("Author ID: ");
                        System.out.println("Current: "
                                + authorService.getAuthorById(id).getAuthorName());
                        String name = ConsoleInput.readLine("New name: ");
                        authorService.updateAuthor(id, name);
                        System.out.println("Author updated.");
                        break;
                    }

                    case 4: {
                        int id = ConsoleInput.readPositiveInt("Author ID: ");
                        authorService.deleteAuthor(id);
                        System.out.println("Author deleted.");
                        break;
                    }

                    case 5:
                        return;

                    default:
                        System.out.println("Please select 1 to 5.");
                }

            } catch (InvalidInputException e) {

                System.out.println("Validation Error: " + e.getMessage());

            } catch (AuthorNotFoundException
                     | EntityInUseException e) {

                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // =========================================================
    // CATEGORIES
    // =========================================================

    private static void manageCategories() {

        while (true) {

            System.out.println("\n========== Categories ==========");
            System.out.println("1. List  2. Add  3. Update  4. Delete  5. Back");

            int choice = ConsoleInput.readPositiveInt("Choice: ");

            try {

                switch (choice) {

                    case 1:
                        printCategories(categoryService.getAllCategories());
                        break;

                    case 2: {
                        String name = ConsoleInput.readLine("Category name: ");
                        int id = categoryService.addCategory(name);
                        System.out.println("Category added. ID: " + id);
                        break;
                    }

                    case 3: {
                        int id = ConsoleInput.readPositiveInt("Category ID: ");
                        System.out.println("Current: "
                                + categoryService.getCategoryById(id).getCategoryName());
                        String name = ConsoleInput.readLine("New name: ");
                        categoryService.updateCategory(id, name);
                        System.out.println("Category updated.");
                        break;
                    }

                    case 4: {
                        int id = ConsoleInput.readPositiveInt("Category ID: ");
                        categoryService.deleteCategory(id);
                        System.out.println("Category deleted.");
                        break;
                    }

                    case 5:
                        return;

                    default:
                        System.out.println("Please select 1 to 5.");
                }

            } catch (InvalidInputException e) {

                System.out.println("Validation Error: " + e.getMessage());

            } catch (CategoryNotFoundException
                     | DuplicateCategoryException
                     | EntityInUseException e) {

                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // =========================================================
    // OUTPUT HELPERS
    // =========================================================

    private static void printBookTable(List<Book> books) {

        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }

        System.out.println(LINE);

        System.out.println(String.format(
                "%-4s %-14s %-32s %-18s %-12s %10s %6s",
                "ID", "ISBN", "Title", "Author", "Category", "Price", "Stock"
        ));

        System.out.println(LINE);

        for (Book book : books) {
            System.out.println(book);
        }

        System.out.println(LINE);
    }

    private static void printBookDetails(Book book) {

        System.out.println("-------------------------");
        System.out.println("Book ID  : " + book.getBookId());
        System.out.println("ISBN     : " + book.getIsbn());
        System.out.println("Title    : " + book.getTitle());
        System.out.println("Author   : " + book.getAuthorName());
        System.out.println("Category : " + book.getCategoryName());
        System.out.println("Price    : Rs. " + book.getPrice());
        System.out.println("Stock    : " + book.getStockQty());
        System.out.println("-------------------------");
    }

    private static void printAuthors(List<Author> authors) {

        System.out.println("\nAuthors:");

        for (Author author : authors) {
            System.out.println("  " + author);
        }
    }

    private static void printCategories(List<Category> categories) {

        System.out.println("\nCategories:");

        for (Category category : categories) {
            System.out.println("  " + category);
        }
    }
}
