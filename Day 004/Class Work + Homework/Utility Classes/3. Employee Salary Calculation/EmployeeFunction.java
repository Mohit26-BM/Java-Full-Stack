import java.util.function.Function;

public class EmployeeFunction {

    int id;
    String name;
    double basicSalary;

    EmployeeFunction(int id, String name, double basicSalary) {
        this.id = id;
        this.name = name;
        this.basicSalary = basicSalary;
    }

    public static void main(String[] args) {

        EmployeeFunction employee =
                new EmployeeFunction(101, "Rahul", 50000);

        Function<EmployeeFunction, String> employeeName =
                e -> e.name;

        Function<EmployeeFunction, Double> monthlySalary =
                e -> e.basicSalary;

        Function<EmployeeFunction, Double> annualSalary =
                e -> e.basicSalary * 12;

        Function<EmployeeFunction, Double> salaryWithBonus =
                e -> e.basicSalary + (e.basicSalary * 0.10);

        System.out.println("Employee ID: " + employee.id);
        System.out.println("Employee Name: "
                + employeeName.apply(employee));

        System.out.println("Monthly Salary: "
                + monthlySalary.apply(employee));

        System.out.println("Annual Salary: "
                + annualSalary.apply(employee));

        System.out.println("Salary with 10% Bonus: "
                + salaryWithBonus.apply(employee));
    }
}