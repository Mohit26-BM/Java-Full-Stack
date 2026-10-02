class Employee {

    int employeeId;
    String name;
    double salary;
    String department;

    Employee() {
        employeeId = 0;
        name = "Unknown";
        salary = 0;
        department = "Not Assigned";
    }

    Employee(int employeeId, String name) {
        this.employeeId = employeeId;
        this.name = name;
        salary = 0;
        department = "Not Assigned";
    }

    Employee(int employeeId, String name, double salary, String department) {
        this.employeeId = employeeId;
        this.name = name;
        this.salary = salary;
        this.department = department;
    }

    void displayEmployee() {
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Name: " + name);
        System.out.println("Salary: " + salary);
        System.out.println("Department: " + department);
        System.out.println();
    }
}

public class ConstructorOverLoading {

    public static void main(String[] args) {

        Employee e1 = new Employee();

        Employee e2 = new Employee(101, "Rahul");

        Employee e3 = new Employee(102, "Amit", 50000, "IT");

        e1.displayEmployee();
        e2.displayEmployee();
        e3.displayEmployee();
    }
}


/*Concept*/

/*
Why this is constructor overloading
The Employee class has three constructors:

// 1. No parameters
Employee()

// 2. Two parameters
Employee(int employeeId, String name)

// 3. Four parameters
Employee(int employeeId, String name, double salary, String department)

Java knows which constructor to call based on the arguments you provide.
Employee e1 = new Employee();

Calls the default constructor.
Employee e2 = new Employee(101, "Rahul");

Calls the two-parameter constructor.
Employee e3 = new Employee(102, "Amit", 50000, "IT");

Calls the four-parameter constructor.
The key concept is:
Same constructor name, different number or types of parameters = Constructor Overloading.

*/