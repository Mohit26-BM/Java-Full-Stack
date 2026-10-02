import java.util.function.Consumer;

public class EmployeeNotification {

    public static void main(String[] args) {

        EmployeeNotificationData employee1 =
                new EmployeeNotificationData(
                        101, "Ravi", "ravi@example.com", "IT");

        EmployeeNotificationData employee2 =
                new EmployeeNotificationData(
                        102, "Priya", "priya@example.com", "HR");

        EmployeeNotificationData employee3 =
                new EmployeeNotificationData(
                        103, "Amit", "amit@example.com", "Finance");

        EmployeeNotificationData employee4 =
                new EmployeeNotificationData(
                        104, "Neha", "neha@example.com", "Marketing");


        /*
        1. Printing employee details
        */

        Consumer<EmployeeNotificationData> employeeDetails =
                employee -> {
                    System.out.println("ID: " + employee.id);
                    System.out.println("Name: " + employee.name);
                    System.out.println("Email: " + employee.email);
                    System.out.println("Department: " + employee.department);
                    System.out.println();
                };


        /*
        2. Simulating an email notification
        */

        Consumer<EmployeeNotificationData> notificationConsumer =
                employee -> System.out.println(
                        "Notification sent to "
                        + employee.name + " - "
                        + employee.email
                );


        /*
        3. Displaying department information
        */

        Consumer<EmployeeNotificationData> departmentConsumer =
                employee -> System.out.println(
                        employee.name
                        + " works in the "
                        + employee.department
                        + " department"
                );


        /*
        4. Printing a welcome message
        */

        Consumer<EmployeeNotificationData> welcomeConsumer =
                employee -> System.out.println(
                        "Welcome " + employee.name + "!"
                );


        System.out.println("EMPLOYEE DETAILS");

        employeeDetails.accept(employee1);
        employeeDetails.accept(employee2);
        employeeDetails.accept(employee3);
        employeeDetails.accept(employee4);


        System.out.println("EMAIL NOTIFICATIONS");

        notificationConsumer.accept(employee1);
        notificationConsumer.accept(employee2);
        notificationConsumer.accept(employee3);
        notificationConsumer.accept(employee4);


        System.out.println();
        System.out.println("DEPARTMENT INFORMATION");

        departmentConsumer.accept(employee1);
        departmentConsumer.accept(employee2);
        departmentConsumer.accept(employee3);
        departmentConsumer.accept(employee4);


        System.out.println();
        System.out.println("WELCOME MESSAGES");

        welcomeConsumer.accept(employee1);
        welcomeConsumer.accept(employee2);
        welcomeConsumer.accept(employee3);
        welcomeConsumer.accept(employee4);
    }
}