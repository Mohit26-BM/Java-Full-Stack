import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CustomerLoanEligibility {

    public static void main(String[] args) {

        /*
        Creating customers
        */

        LoanCustomerData customer1 =
                new LoanCustomerData(
                        101, "Ravi", 28, 60000, 750, "EMPLOYED");

        LoanCustomerData customer2 =
                new LoanCustomerData(
                        102, "Priya", 25, 45000, 720, "EMPLOYED");

        LoanCustomerData customer3 =
                new LoanCustomerData(
                        103, "Amit", 19, 50000, 750, "EMPLOYED");

        LoanCustomerData customer4 =
                new LoanCustomerData(
                        104, "Neha", 30, 35000, 780, "EMPLOYED");

        LoanCustomerData customer5 =
                new LoanCustomerData(
                        105, "Rahul", 35, 70000, 650, "EMPLOYED");

        LoanCustomerData customer6 =
                new LoanCustomerData(
                        106, "Sneha", 29, 55000, 710, "SELF-EMPLOYED");


        /*
        Predicate for age
        */

        Predicate<LoanCustomerData> validAge =
                customer -> customer.age >= 21;


        /*
        Predicate for salary
        */

        Predicate<LoanCustomerData> validSalary =
                customer -> customer.salary >= 40000;


        /*
        Predicate for credit score
        */

        Predicate<LoanCustomerData> validCreditScore =
                customer -> customer.creditScore >= 700;


        /*
        Predicate for employment status
        */

        Predicate<LoanCustomerData> employed =
                customer -> customer.employmentStatus.equals("EMPLOYED");


        /*
        Combining all predicates
        */

        Predicate<LoanCustomerData> loanEligible =
                validAge
                .and(validSalary)
                .and(validCreditScore)
                .and(employed);


        /*
        Function to generate eligibility message
        */

        Function<LoanCustomerData, String> eligibilityMessage =
                customer -> {

                    if (loanEligible.test(customer)) {
                        return customer.name
                                + " is eligible for loan";
                    }

                    return customer.name
                            + " is not eligible for loan";
                };


        /*
        Consumer to print customer details
        */

        Consumer<LoanCustomerData> customerPrinter =
                customer -> {

                    System.out.println("ID: " + customer.id);
                    System.out.println("Name: " + customer.name);
                    System.out.println("Age: " + customer.age);
                    System.out.println("Salary: " + customer.salary);
                    System.out.println("Credit Score: "
                            + customer.creditScore);
                    System.out.println("Employment Status: "
                            + customer.employmentStatus);
                };


        /*
        Supplier to generate a default customer
        */

        Supplier<LoanCustomerData> defaultCustomer =
                () -> new LoanCustomerData(
                        999,
                        "New Customer",
                        25,
                        40000,
                        700,
                        "EMPLOYED"
                );


        /*
        Display customer details and eligibility message
        */

        System.out.println("CUSTOMER 1");
        customerPrinter.accept(customer1);
        System.out.println(eligibilityMessage.apply(customer1));
        System.out.println("-------------------------");


        System.out.println("CUSTOMER 2");
        customerPrinter.accept(customer2);
        System.out.println(eligibilityMessage.apply(customer2));
        System.out.println("-------------------------");


        System.out.println("CUSTOMER 3");
        customerPrinter.accept(customer3);
        System.out.println(eligibilityMessage.apply(customer3));
        System.out.println("-------------------------");


        System.out.println("CUSTOMER 4");
        customerPrinter.accept(customer4);
        System.out.println(eligibilityMessage.apply(customer4));
        System.out.println("-------------------------");


        System.out.println("CUSTOMER 5");
        customerPrinter.accept(customer5);
        System.out.println(eligibilityMessage.apply(customer5));
        System.out.println("-------------------------");


        System.out.println("CUSTOMER 6");
        customerPrinter.accept(customer6);
        System.out.println(eligibilityMessage.apply(customer6));
        System.out.println("-------------------------");


        /*
        Generate and display the default customer
        */

        LoanCustomerData newCustomer =
                defaultCustomer.get();

        System.out.println("DEFAULT CUSTOMER");
        customerPrinter.accept(newCustomer);
        System.out.println(eligibilityMessage.apply(newCustomer));
    }
}