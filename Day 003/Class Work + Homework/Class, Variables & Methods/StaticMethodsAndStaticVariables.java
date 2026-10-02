class StaticEmployee {

    int employeeId;
    String name;

    static int employeeCount = 0;

    StaticEmployee(int employeeId, String name) {
        this.employeeId = employeeId;
        this.name = name;

        employeeCount++;
    }

    static void displayEmployeeCount() {
        System.out.println("Total number of employees: " + employeeCount);
    }
}

public class StaticMethodsAndStaticVariables {

    public static void main(String[] args) {

        StaticEmployee e1 = new StaticEmployee(101, "Rahul");
        StaticEmployee e2 = new StaticEmployee(102, "Amit");
        StaticEmployee e3 = new StaticEmployee(103, "Priya");
        StaticEmployee e4 = new StaticEmployee(104, "Neha");
        StaticEmployee e5 = new StaticEmployee(105, "Rohan");

        System.out.println("Employee count: " + StaticEmployee.employeeCount);

        StaticEmployee.displayEmployeeCount();
    }
}