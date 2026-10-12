package com.company.jdbc.app;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import com.company.jdbc.dao.DepartmentDao;
import com.company.jdbc.dao.DepartmentDaoImpl;
import com.company.jdbc.dao.EmployeeDao;
import com.company.jdbc.dao.EmployeeDaoImpl;
import com.company.jdbc.dao.EmployeeSortField;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.exception.DepartmentNotFoundException;
import com.company.jdbc.exception.DuplicateEmployeeException;
import com.company.jdbc.exception.EmployeeNotFoundException;
import com.company.jdbc.exception.InvalidEmployeeException;
import com.company.jdbc.model.Department;
import com.company.jdbc.model.Employee;
import com.company.jdbc.service.EmployeeService;
import com.company.jdbc.service.EmployeeServiceImpl;

public class EmployeeDirectoryApp {

    private static final Scanner scanner = new Scanner(System.in);

    /*
    "uuuu" + STRICT rejects impossible dates such as 31-02-2024.
    */
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    public static void main(String[] args) {

        /*
        Manual dependency injection: DAO -> Service.
        */
        EmployeeDao employeeDao = new EmployeeDaoImpl();
        DepartmentDao departmentDao = new DepartmentDaoImpl();

        EmployeeService employeeService =
                new EmployeeServiceImpl(employeeDao, departmentDao);

        while (true) {

            showMenu();

            int choice = readPositiveInt("Enter your choice: ");

            /*
            A database problem (MySQL stopped, wrong password ...)
            is reported and the menu continues instead of crashing.
            */
            try {

                switch (choice) {

                    case 1:
                        addEmployee(employeeService);
                        break;

                    case 2:
                        findEmployeeById(employeeService);
                        break;

                    case 3:
                        searchByDepartment(employeeService);
                        break;

                    case 4:
                        updateContact(employeeService);
                        break;

                    case 5:
                        deleteEmployee(employeeService);
                        break;

                    case 6:
                        listEmployeesPaged(employeeService);
                        break;

                    case 7:
                        displayDepartments(employeeService);
                        break;

                    case 8:
                        System.out.println(
                                "\nThank you for using the Employee Directory."
                        );
                        return;

                    default:
                        System.out.println(
                                "\nInvalid choice. Please select 1 to 8."
                        );
                }

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
        System.out.println("          EMPLOYEE DIRECTORY");
        System.out.println("========================================");
        System.out.println("1. Add Employee");
        System.out.println("2. Find Employee by ID");
        System.out.println("3. Search Employees by Department");
        System.out.println("4. Update Contact Information");
        System.out.println("5. Delete Employee");
        System.out.println("6. List All Employees (sorted, paged)");
        System.out.println("7. List Departments");
        System.out.println("8. Exit");
        System.out.println("========================================");
    }

    // =========================================================
    // MENU ACTIONS
    // =========================================================

    private static void addEmployee(EmployeeService employeeService) {

        System.out.println("\n========== Add Employee ==========");

        /*
        Raw text is passed to the service, which validates
        everything before the INSERT.
        */
        String code = readLine("Employee code (e.g. EMP016): ");
        String name = readLine("Employee name: ");
        String email = readLine("Email: ");
        String phone = readLine("Phone (10 digits, blank to skip): ");

        printDepartments(employeeService.getAllDepartments());
        int departmentId = readPositiveInt("Department ID: ");

        BigDecimal salary = readSalary("Salary: Rs. ");
        LocalDate joiningDate = readDate("Joining date (dd-MM-yyyy): ");

        Employee employee = new Employee(
                code,
                name,
                email,
                phone,
                departmentId,
                salary,
                joiningDate
        );

        try {

            int employeeId = employeeService.addEmployee(employee);

            System.out.println("\nEmployee added successfully.");
            System.out.println("Generated Employee ID: " + employeeId);

            /*
            Acceptance test: read the row back to verify it.
            */
            printEmployeeTable(
                    Collections.singletonList(
                            employeeService.findEmployeeById(employeeId)
                    )
            );

        } catch (InvalidEmployeeException e) {

            System.out.println("\nValidation Error: " + e.getMessage());

        } catch (DuplicateEmployeeException
                 | DepartmentNotFoundException
                 | EmployeeNotFoundException e) {

            System.out.println("\nError: " + e.getMessage());
        }
    }

    private static void findEmployeeById(EmployeeService employeeService) {

        System.out.println("\n========== Find Employee ==========");

        int employeeId = readPositiveInt("Employee ID: ");

        try {

            Employee employee =
                    employeeService.findEmployeeById(employeeId);

            printEmployeeDetails(employee);

        } catch (EmployeeNotFoundException e) {

            System.out.println("\n" + e.getMessage());
        }
    }

    private static void searchByDepartment(EmployeeService employeeService) {

        System.out.println("\n========== Search by Department ==========");

        printDepartments(employeeService.getAllDepartments());

        int departmentId = readPositiveInt("Department ID: ");

        try {

            List<Employee> employees =
                    employeeService.searchByDepartment(departmentId);

            if (employees.isEmpty()) {
                System.out.println("\nNo employees in this department.");
                return;
            }

            System.out.println(
                    "\n" + employees.size() + " employee(s) found:"
            );

            printEmployeeTable(employees);

        } catch (DepartmentNotFoundException e) {

            System.out.println("\nError: " + e.getMessage());
        }
    }

    private static void updateContact(EmployeeService employeeService) {

        System.out.println("\n========== Update Contact Information ==========");

        int employeeId = readPositiveInt("Employee ID: ");

        try {

            Employee current = employeeService.findEmployeeById(employeeId);

            System.out.println("\nCurrent details:");
            printEmployeeDetails(current);

            System.out.println("\nPress Enter to keep the current value.");

            String email = readLine("New email: ");
            String phone = readLine("New phone (10 digits): ");

            if (email.isEmpty()) {
                email = current.getEmail();
            }

            if (phone.isEmpty()) {
                phone = current.getPhone();
            }

            employeeService.updateContact(employeeId, email, phone);

            System.out.println("\nContact information updated successfully.");

            printEmployeeDetails(employeeService.findEmployeeById(employeeId));

        } catch (InvalidEmployeeException e) {

            System.out.println("\nValidation Error: " + e.getMessage());

        } catch (EmployeeNotFoundException
                 | DuplicateEmployeeException e) {

            System.out.println("\nError: " + e.getMessage());
        }
    }

    private static void deleteEmployee(EmployeeService employeeService) {

        System.out.println("\n========== Delete Employee ==========");

        int employeeId = readPositiveInt("Employee ID: ");

        try {

            Employee employee = employeeService.findEmployeeById(employeeId);

            printEmployeeDetails(employee);

            String answer =
                    readLine("\nDelete this employee? (y/n): ");

            if (!answer.equalsIgnoreCase("y")) {
                System.out.println("Delete cancelled.");
                return;
            }

            int before = employeeService.getTotalEmployees();

            employeeService.deleteEmployee(employeeId);

            int after = employeeService.getTotalEmployees();

            System.out.println("\nEmployee deleted successfully.");

            /*
            Acceptance test: exactly one row removed,
            all other employees remain.
            */
            System.out.println(
                    "Employees before: " + before
                    + " | after: " + after
            );

        } catch (EmployeeNotFoundException e) {

            System.out.println("\n" + e.getMessage());
        }
    }

    private static void listEmployeesPaged(EmployeeService employeeService) {

        System.out.println("\n========== All Employees ==========");

        System.out.println("Sort by: 1. Name   2. Joining date");
        EmployeeSortField sortField =
                readPositiveInt("Choice: ") == 2
                        ? EmployeeSortField.JOINING_DATE
                        : EmployeeSortField.NAME;

        System.out.println("Order:   1. Ascending   2. Descending");
        boolean ascending = readPositiveInt("Choice: ") != 2;

        int pageSize = readPositiveInt("Employees per page: ");

        int total = employeeService.getTotalEmployees();

        if (total == 0) {
            System.out.println("\nNo employees found.");
            return;
        }

        int totalPages = (total + pageSize - 1) / pageSize;
        int page = 1;

        while (true) {

            List<Employee> employees =
                    employeeService.getEmployeePage(
                            sortField, ascending, page, pageSize);

            System.out.println(
                    "\nPage " + page + " of " + totalPages
                    + "  (" + total + " employees, sorted by "
                    + sortField + (ascending ? " ASC" : " DESC") + ")"
            );

            printEmployeeTable(employees);

            String action =
                    readLine("[N]ext  [P]revious  [Q]uit: ").toUpperCase();

            if (action.equals("N")) {

                if (page < totalPages) {
                    page++;
                } else {
                    System.out.println("Already on the last page.");
                }

            } else if (action.equals("P")) {

                if (page > 1) {
                    page--;
                } else {
                    System.out.println("Already on the first page.");
                }

            } else if (action.equals("Q")) {

                return;

            } else {

                System.out.println("Please enter N, P or Q.");
            }
        }
    }

    private static void displayDepartments(EmployeeService employeeService) {

        System.out.println("\n========== Departments ==========");

        printDepartments(employeeService.getAllDepartments());
    }

    // =========================================================
    // OUTPUT HELPERS
    // =========================================================

    private static void printDepartments(List<Department> departments) {

        System.out.println("\nDepartments:");

        for (Department department : departments) {
            System.out.println("  " + department);
        }
    }

    private static void printEmployeeTable(List<Employee> employees) {

        System.out.println(
                "-------------------------------------------------------"
                + "-------------------------------------------------------"
        );

        System.out.println(String.format(
                "%-4s %-8s %-18s %-28s %-11s %-16s %12s  %s",
                "ID", "Code", "Name", "Email", "Phone",
                "Department", "Salary", "Joined"
        ));

        System.out.println(
                "-------------------------------------------------------"
                + "-------------------------------------------------------"
        );

        for (Employee employee : employees) {
            System.out.println(employee);
        }
    }

    private static void printEmployeeDetails(Employee employee) {

        System.out.println("-------------------------");
        System.out.println("Employee ID   : " + employee.getEmployeeId());
        System.out.println("Employee Code : " + employee.getEmployeeCode());
        System.out.println("Name          : " + employee.getEmployeeName());
        System.out.println("Email         : " + employee.getEmail());
        System.out.println("Phone         : "
                + (employee.getPhone() == null ? "-" : employee.getPhone()));
        System.out.println("Department    : "
                + (employee.getDepartmentName() == null
                        ? "-" : employee.getDepartmentName()));
        System.out.println("Salary        : "
                + (employee.getSalary() == null
                        ? "-" : "Rs. " + employee.getSalary()));
        System.out.println("Joining Date  : "
                + (employee.getJoiningDate() == null
                        ? "-" : employee.getJoiningDate().format(DATE_FORMAT)));
        System.out.println("-------------------------");
    }

    // =========================================================
    // INPUT HELPERS
    // These only check the FORMAT of the input.
    // Business validation is done in the service layer.
    // =========================================================

    private static String readLine(String message) {

        System.out.print(message);

        return scanner.nextLine().trim();
    }

    private static int readPositiveInt(String message) {

        while (true) {

            String input = readLine(message);

            try {

                int value = Integer.parseInt(input);

                if (value <= 0) {
                    System.out.println("Value must be greater than 0.");
                    continue;
                }

                return value;

            } catch (NumberFormatException e) {

                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static BigDecimal readSalary(String message) {

        while (true) {

            String input = readLine(message);

            try {

                return new BigDecimal(input);

            } catch (NumberFormatException e) {

                System.out.println("Please enter a valid amount, e.g. 55000.50");
            }
        }
    }

    private static LocalDate readDate(String message) {

        while (true) {

            String input = readLine(message);

            try {

                return LocalDate.parse(input, DATE_FORMAT);

            } catch (DateTimeParseException e) {

                System.out.println("Please enter a valid date as dd-MM-yyyy.");
            }
        }
    }
}
