import java.util.function.Function;

public class EmployeeSalaryCalculation {

    public static void main(String[] args) {

        EmployeeData employee =
                new EmployeeData(101, "Rahul", 50000);

        Function<EmployeeData, String> employeeName =
                e -> e.name;

        Function<EmployeeData, Double> monthlySalary =
                e -> e.basicSalary;

        Function<EmployeeData, Double> annualSalary =
                e -> e.basicSalary * 12;

        Function<EmployeeData, Double> salaryWithBonus =
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