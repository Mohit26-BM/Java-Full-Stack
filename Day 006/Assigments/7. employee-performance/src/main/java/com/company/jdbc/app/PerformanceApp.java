package com.company.jdbc.app;

import java.math.BigDecimal;
import java.util.List;

import com.company.jdbc.dao.EmployeeDaoImpl;
import com.company.jdbc.dao.RatingBandDaoImpl;
import com.company.jdbc.dao.ReviewDaoImpl;
import com.company.jdbc.dao.ReviewSummaryDaoImpl;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.exception.EmployeeNotFoundException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.Employee;
import com.company.jdbc.model.PerformanceReview;
import com.company.jdbc.model.RatingBand;
import com.company.jdbc.model.ReviewSummary;
import com.company.jdbc.service.PerformanceService;
import com.company.jdbc.service.PerformanceServiceImpl;

public class PerformanceApp {

    private static final String LINE =
            "--------------------------------------------------------------------------";

    private static PerformanceService performanceService;

    public static void main(String[] args) {

        /*
        Manual dependency injection: DAO -> Service.
        */
        performanceService = new PerformanceServiceImpl(
                new EmployeeDaoImpl(),
                new ReviewDaoImpl(),
                new ReviewSummaryDaoImpl(),
                new RatingBandDaoImpl()
        );

        while (true) {

            showMenu();

            int choice = ConsoleInput.readPositiveInt("Enter your choice: ");

            if (choice == 6) {
                System.out.println("\nThank you for using Employee Performance.");
                return;
            }

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
        System.out.println("   EMPLOYEE PERFORMANCE (STORED PROCEDURE)");
        System.out.println("========================================");
        System.out.println("1. View Employees");
        System.out.println("2. Review Summary for One Employee");
        System.out.println("3. Review Summary for All Employees");
        System.out.println("4. Add Performance Review");
        System.out.println("5. View / Configure Rating Bands");
        System.out.println("6. Exit");
        System.out.println("========================================");
    }

    private static void handleChoice(int choice) {

        switch (choice) {
            case 1: viewEmployees();      break;
            case 2: summaryForOne();      break;
            case 3: summaryForAll();      break;
            case 4: addReview();          break;
            case 5: configureBands();     break;
            default:
                System.out.println("\nInvalid choice. Please select 1 to 6.");
        }
    }

    // =========================================================
    // MENU ACTIONS
    // =========================================================

    private static void viewEmployees() {

        System.out.println("\n========== Employees ==========");

        for (Employee employee : performanceService.getAllEmployees()) {
            System.out.println(employee);
        }
    }

    private static void summaryForOne() {

        System.out.println("\n========== Review Summary ==========");

        int employeeId = ConsoleInput.readPositiveInt("Employee ID (e.g. 101, or 107 for no reviews): ");

        try {

            ReviewSummary summary = performanceService.getReviewSummary(employeeId);

            System.out.println("\nCALL sp_employee_review_summary(" + employeeId
                    + ", @avg, @count, @latest, @band)");

            System.out.println("-------------------------");
            System.out.println("Employee        : " + summary.getEmployeeId()
                    + " - " + summary.getEmployeeName());

            // Every OUT parameter, exactly as received
            System.out.println("OUT average     : " + show(summary.getAverageRating()));
            System.out.println("OUT count       : " + summary.getReviewCount());
            System.out.println("OUT latest      : " + show(summary.getLatestPeriod()));
            System.out.println("OUT rating band : " + summary.getRatingBand());
            System.out.println("-------------------------");

            if (!summary.hasReviews()) {

                System.out.println("This employee has no performance reviews yet.");
                return;
            }

            System.out.println("\nReviews returned by the procedure's result set:");
            System.out.println(LINE);
            System.out.println(String.format("%-6s %-9s %6s  %-16s %s",
                    "ID", "Period", "Rating", "Reviewer", "Comments"));
            System.out.println(LINE);

            for (PerformanceReview review : summary.getReviews()) {
                System.out.println(review);
            }

            System.out.println(LINE);

        } catch (EmployeeNotFoundException e) {

            System.out.println("\n" + e.getMessage());
        }
    }

    private static void summaryForAll() {

        System.out.println("\n========== Review Summary: All Employees ==========");

        List<ReviewSummary> summaries = performanceService.getAllReviewSummaries();

        System.out.println(LINE);
        System.out.println(String.format("%-5s %-16s %8s %6s %-9s %s",
                "ID", "Employee", "Average", "Count", "Latest", "Band"));
        System.out.println(LINE);

        for (ReviewSummary summary : summaries) {

            System.out.println(String.format("%-5d %-16.16s %8s %6d %-9s %s",
                    summary.getEmployeeId(),
                    summary.getEmployeeName(),
                    show(summary.getAverageRating()),
                    summary.getReviewCount(),
                    show(summary.getLatestPeriod()),
                    summary.getRatingBand()));
        }

        System.out.println(LINE);
    }

    private static void addReview() {

        System.out.println("\n========== Add Performance Review ==========");

        int employeeId = ConsoleInput.readPositiveInt("Employee ID: ");
        String period = ConsoleInput.readLine("Review period (e.g. 2026-Q3): ");
        BigDecimal rating = ConsoleInput.readDecimal("Rating (1.00 - 5.00): ");
        String reviewer = ConsoleInput.readLine("Reviewer: ");
        String comments = ConsoleInput.readLine("Comments (optional): ");

        try {

            long reviewId = performanceService.addReview(
                    employeeId, period, rating, reviewer, comments);

            System.out.println("\nReview added. Review ID: " + reviewId);

            ReviewSummary summary = performanceService.getReviewSummary(employeeId);

            System.out.println("Updated summary: average " + show(summary.getAverageRating())
                    + ", count " + summary.getReviewCount()
                    + ", latest " + show(summary.getLatestPeriod())
                    + ", band " + summary.getRatingBand());

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());

        } catch (EmployeeNotFoundException e) {

            System.out.println("\n" + e.getMessage());
        }
    }

    private static void configureBands() {

        System.out.println("\n========== Rating Bands ==========");
        printBands();

        String band = ConsoleInput.readLine(
                "\nBand to change (Enter to go back): ");

        if (band.isEmpty()) {
            return;
        }

        BigDecimal minRating = ConsoleInput.readDecimal("New minimum average rating: ");

        try {

            performanceService.updateRatingBand(band, minRating);

            System.out.println("\nBand updated. The procedure uses it from the next call:");
            printBands();

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());
        }
    }

    // =========================================================
    // OUTPUT HELPERS
    // =========================================================

    private static void printBands() {

        System.out.println("-------------------------");
        System.out.println(String.format("%-22s %s", "Band", "Average >="));

        for (RatingBand band : performanceService.getRatingBands()) {
            System.out.println(String.format("%-22s %s",
                    band.getBandName(), band.getMinRating()));
        }

        System.out.println(String.format("%-22s %s", "NO_REVIEWS", "(review count = 0)"));
        System.out.println("-------------------------");
    }

    /*
    Shows SQL NULL as "NULL" so it is clear the OUT value was NULL.
    */
    private static String show(Object value) {
        return value == null ? "NULL" : value.toString();
    }
}
