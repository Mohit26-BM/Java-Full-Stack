/*
EMPLOYEE MANAGEMENT SYSTEM

This program demonstrates:
1. Identifying classes and objects
2. Encapsulation
3. Inheritance
4. Polymorphism
5. Abstraction
6. Code reuse and reduced duplication
7. Extensibility for future employee types

             CompanyEmployee
                    |
       ┌────────────┼────────────┐
       ↓            ↓            ↓
 Permanent      Contract       Intern
 Employee       Employee       Employee
                    |
                    ↓
              Consultant
               Employee


*/

abstract class CompanyEmployee {

    /*
    ENCAPSULATION

    Employee data is declared private so that it cannot
    be directly accessed from outside this class.
    */
    private int employeeId;
    private String name;
    private String department;

    /*
    CONSTRUCTOR

    Initializes the common information of every employee.
    */
    CompanyEmployee(int employeeId, String name, String department) {
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
    }

    /*
    ENCAPSULATION

    Getter methods provide controlled access to private data.
    */
    public int getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    /*
    ENCAPSULATION

    Setter methods allow controlled modification of
    employee information.
    */
    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    /*
    CODE REUSE

    This common method is written only once in the parent
    class instead of being duplicated in every employee class.
    */
    public void displayDetails() {
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Name: " + name);
        System.out.println("Department: " + department);
    }

    /*
    ABSTRACTION

    The parent class defines that every employee must
    calculate a salary, but does not specify how.

    Each child class provides its own implementation.
    */
    abstract double calculateSalary();
}


/*
INHERITANCE

PermanentEmployee inherits common properties and methods
from CompanyEmployee.
*/
class PermanentEmployee extends CompanyEmployee {

    private double monthlySalary;

    PermanentEmployee(int employeeId, String name,
                      String department, double monthlySalary) {

        super(employeeId, name, department);
        this.monthlySalary = monthlySalary;
    }

    /*
    POLYMORPHISM

    Permanent employees calculate salary differently
    from other types of employees.
    */
    @Override
    double calculateSalary() {
        return monthlySalary;
    }
}


/*
INHERITANCE

ContractEmployee inherits from CompanyEmployee.
*/
class ContractEmployee extends CompanyEmployee {

    private double monthlySalary;
    private double contractBonus;

    ContractEmployee(int employeeId, String name,
                     String department, double monthlySalary,
                     double contractBonus) {

        super(employeeId, name, department);
        this.monthlySalary = monthlySalary;
        this.contractBonus = contractBonus;
    }

    /*
    POLYMORPHISM

    Contract employees calculate salary using salary
    plus a contract bonus.
    */
    @Override
    double calculateSalary() {
        return monthlySalary + contractBonus;
    }
}


/*
INHERITANCE

InternEmployee inherits from CompanyEmployee.
*/
class InternEmployee extends CompanyEmployee {

    private double stipend;

    InternEmployee(int employeeId, String name,
                   String department, double stipend) {

        super(employeeId, name, department);
        this.stipend = stipend;
    }

    /*
    POLYMORPHISM

    Interns receive a stipend instead of a regular salary.
    */
    @Override
    double calculateSalary() {
        return stipend;
    }
}


/*
INHERITANCE

ConsultantEmployee inherits from CompanyEmployee.
*/
class ConsultantEmployee extends CompanyEmployee {

    private double hourlyRate;
    private int hoursWorked;

    ConsultantEmployee(int employeeId, String name,
                       String department, double hourlyRate,
                       int hoursWorked) {

        super(employeeId, name, department);
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    /*
    POLYMORPHISM

    Consultant salary is calculated using hourly rate
    multiplied by the number of hours worked.
    */
    @Override
    double calculateSalary() {
        return hourlyRate * hoursWorked;
    }
}


/*
MAIN CLASS

This class creates objects and demonstrates the complete
Employee Management System.
*/
public class EmployeeManagementDemo {

    public static void main(String[] args) {

        /*
        OBJECTS

        Four different employee objects are created.

        The reference type is CompanyEmployee while the
        actual objects are different employee types.

        This demonstrates POLYMORPHISM.
        */
        CompanyEmployee e1 =
                new PermanentEmployee(101, "Rahul", "IT", 70000);

        CompanyEmployee e2 =
                new ContractEmployee(102, "Amit", "Finance",
                                     50000, 5000);

        CompanyEmployee e3 =
                new InternEmployee(103, "Priya", "HR", 20000);

        CompanyEmployee e4 =
                new ConsultantEmployee(104, "Rohan",
                                       "Operations", 1000, 40);

        /*
        DISPLAY COMMON EMPLOYEE DETAILS

        displayDetails() is inherited from CompanyEmployee.
        */
        System.out.println("EMPLOYEE MANAGEMENT SYSTEM");
        System.out.println();

        e1.displayDetails();

        /*
        POLYMORPHISM

        The calculateSalary() method that executes depends
        on the actual object type.
        */
        
        System.out.println("Salary: " + e1.calculateSalary());
        System.out.println();

        e2.displayDetails();
        System.out.println("Salary: " + e2.calculateSalary());
        System.out.println();

        e3.displayDetails();
        System.out.println("Salary: " + e3.calculateSalary());
        System.out.println();

        e4.displayDetails();
        System.out.println("Salary: " + e4.calculateSalary());
    }
}