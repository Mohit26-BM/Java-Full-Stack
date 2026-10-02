import java.util.function.Predicate;

public class EmployeePredicateDemo {

    public static void main(String[] args) {

        EmployeeSalaryValidation e1 =
                new EmployeeSalaryValidation(101, "Ravi", "IT", 60000);

        EmployeeSalaryValidation e2 =
                new EmployeeSalaryValidation(102, "Priya", "HR", 45000);

        EmployeeSalaryValidation e3 =
                new EmployeeSalaryValidation(103, "Arun", "IT", 75000);

        EmployeeSalaryValidation e4 =
                new EmployeeSalaryValidation(104, "Kiran", "Finance", 50000);

        EmployeeSalaryValidation e5 =
                new EmployeeSalaryValidation(105, "Meena", "Sales", 35000);

        Predicate<EmployeeSalaryValidation> salaryPredicate =
                employee -> employee.getSalary() >= 50000;

        System.out.println("Employees with salary >= 50000:");

        if (salaryPredicate.test(e1)) {
            System.out.println(e1.getName() + " - " + e1.getSalary());
        }

        if (salaryPredicate.test(e2)) {
            System.out.println(e2.getName() + " - " + e2.getSalary());
        }

        if (salaryPredicate.test(e3)) {
            System.out.println(e3.getName() + " - " + e3.getSalary());
        }

        if (salaryPredicate.test(e4)) {
            System.out.println(e4.getName() + " - " + e4.getSalary());
        }

        if (salaryPredicate.test(e5)) {
            System.out.println(e5.getName() + " - " + e5.getSalary());
        }
    }
}