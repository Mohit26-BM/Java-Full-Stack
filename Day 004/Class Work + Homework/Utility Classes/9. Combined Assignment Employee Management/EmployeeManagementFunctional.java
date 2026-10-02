import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EmployeeManagementFunctional {

    public static void main(String[] args) {

        /*
        Creating 8 employees
        */

        EmployeeManagementData employee1 =
                new EmployeeManagementData(
                        "EMP101", "Ravi", "IT", 75000, 5);

        EmployeeManagementData employee2 =
                new EmployeeManagementData(
                        "EMP102", "Priya", "HR", 55000, 4);

        EmployeeManagementData employee3 =
                new EmployeeManagementData(
                        "EMP103", "Amit", "Finance", 68000, 6);

        EmployeeManagementData employee4 =
                new EmployeeManagementData(
                        "EMP104", "Neha", "Marketing", 45000, 3);

        EmployeeManagementData employee5 =
                new EmployeeManagementData(
                        "EMP105", "Rahul", "IT", 82000, 7);

        EmployeeManagementData employee6 =
                new EmployeeManagementData(
                        "EMP106", "Sneha", "Finance", 62000, 5);

        EmployeeManagementData employee7 =
                new EmployeeManagementData(
                        "EMP107", "Karan", "HR", 50000, 2);

        EmployeeManagementData employee8 =
                new EmployeeManagementData(
                        "EMP108", "Anjali", "IT", 90000, 8);


        /*
        Predicate
        Identifies employees whose salary is greater than 60000.
        */

        Predicate<EmployeeManagementData> highSalaryEmployee =
                employee -> employee.salary > 60000;


        /*
        Function
        Calculates the annual salary.
        */

        Function<EmployeeManagementData, Double> annualSalary =
                employee -> employee.salary * 12;


        /*
        Consumer
        Displays employee information.
        */

        Consumer<EmployeeManagementData> employeePrinter =
                employee -> {

                    System.out.println("ID: " + employee.id);
                    System.out.println("Name: " + employee.name);
                    System.out.println("Department: " + employee.department);
                    System.out.println("Salary: " + employee.salary);
                    System.out.println("Experience: "
                            + employee.experience + " years");
                };


        /*
        Supplier
        Generates a default employee.
        */

        Supplier<EmployeeManagementData> defaultEmployee =
                () -> new EmployeeManagementData(
                        "EMP999",
                        "New Employee",
                        "IT",
                        40000,
                        0
                );


        /*
        Displaying the default employee
        */

        EmployeeManagementData newEmployee =
                defaultEmployee.get();

        System.out.println("DEFAULT EMPLOYEE");

        employeePrinter.accept(newEmployee);

        System.out.println();


        /*
        Combined Flow
        Only employees with salary greater than 60000
        are processed.
        */

        System.out.println("HIGH SALARY EMPLOYEES");

        if (highSalaryEmployee.test(employee1)) {
            employeePrinter.accept(employee1);
            System.out.println("Annual Salary: "
                    + annualSalary.apply(employee1));
            System.out.println("-------------------------");
        }

        if (highSalaryEmployee.test(employee2)) {
            employeePrinter.accept(employee2);
            System.out.println("Annual Salary: "
                    + annualSalary.apply(employee2));
            System.out.println("-------------------------");
        }

        if (highSalaryEmployee.test(employee3)) {
            employeePrinter.accept(employee3);
            System.out.println("Annual Salary: "
                    + annualSalary.apply(employee3));
            System.out.println("-------------------------");
        }

        if (highSalaryEmployee.test(employee4)) {
            employeePrinter.accept(employee4);
            System.out.println("Annual Salary: "
                    + annualSalary.apply(employee4));
            System.out.println("-------------------------");
        }

        if (highSalaryEmployee.test(employee5)) {
            employeePrinter.accept(employee5);
            System.out.println("Annual Salary: "
                    + annualSalary.apply(employee5));
            System.out.println("-------------------------");
        }

        if (highSalaryEmployee.test(employee6)) {
            employeePrinter.accept(employee6);
            System.out.println("Annual Salary: "
                    + annualSalary.apply(employee6));
            System.out.println("-------------------------");
        }

        if (highSalaryEmployee.test(employee7)) {
            employeePrinter.accept(employee7);
            System.out.println("Annual Salary: "
                    + annualSalary.apply(employee7));
            System.out.println("-------------------------");
        }

        if (highSalaryEmployee.test(employee8)) {
            employeePrinter.accept(employee8);
            System.out.println("Annual Salary: "
                    + annualSalary.apply(employee8));
            System.out.println("-------------------------");
        }
    }
}