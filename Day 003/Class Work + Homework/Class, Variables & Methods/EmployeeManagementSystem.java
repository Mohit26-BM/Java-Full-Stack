class SalaryEmployee {

    int employeeId;
    String name;
    double basicSalary;

    static String companyName = "ABC Technologies";
    static int employeeCount = 0;

    SalaryEmployee() {
        employeeId = 0;
        name = "Unknown";
        basicSalary = 0;

        employeeCount++;
    }

    SalaryEmployee(int employeeId, String name, double basicSalary) {
        this.employeeId = employeeId;
        this.name = name;
        this.basicSalary = basicSalary;

        employeeCount++;
    }

    double calculateHRA() {
        return basicSalary * 0.20;
    }

    double calculateDA() {
        return basicSalary * 0.10;
    }

    double calculateGrossSalary() {
        return basicSalary + calculateHRA() + calculateDA();
    }

    void displayEmployeeDetails() {
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Name: " + name);
        System.out.println("Basic Salary: " + basicSalary);
        System.out.println("HRA: " + calculateHRA());
        System.out.println("DA: " + calculateDA());
        System.out.println("Gross Salary: " + calculateGrossSalary());
        System.out.println();
    }

    static void displayCompanyName() {
        System.out.println("Company Name: " + companyName);
    }

    static void displayEmployeeCount() {
        System.out.println("Employee Count: " + employeeCount);
    }
}

public class EmployeeManagementSystem {

    public static void main(String[] args) {

        SalaryEmployee.displayCompanyName();

        SalaryEmployee e1 = new SalaryEmployee();
        SalaryEmployee e2 = new SalaryEmployee(101, "Rahul", 50000);
        SalaryEmployee e3 = new SalaryEmployee(102, "Amit", 60000);
        SalaryEmployee e4 = new SalaryEmployee(103, "Priya", 45000);

        System.out.println("\nEmployee 1 Details:");
        e1.displayEmployeeDetails();

        System.out.println("Employee 2 Details:");
        e2.displayEmployeeDetails();

        System.out.println("Employee 3 Details:");
        e3.displayEmployeeDetails();

        System.out.println("Employee 4 Details:");
        e4.displayEmployeeDetails();

        SalaryEmployee.displayEmployeeCount();
    }
}