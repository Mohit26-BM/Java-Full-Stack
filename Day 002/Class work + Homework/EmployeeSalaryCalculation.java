import java.util.Scanner;

public class EmployeeSalaryCalculation {

    public static float getSalary(Scanner sc) {
        System.out.print("Enter your salary: ");
        return sc.nextFloat();
    }

    public static float calculateHRA(float salary) {
        return 0.20f * salary;
    }

    public static float calculateDA(float salary) {
        return 0.10f * salary;
    }

    public static float calculateTransport(float salary) {
        if (salary < 30000) {
            return 3000;
        }
        else {
            return 2000;
        }
    }

    public static float calculateGrossSalary(float salary, float hra, float da, float transport) {
        return salary + hra + da + transport;
    }

    public static void displaySalaryDetails(
            float salary,
            float hra,
            float da,
            float transport,
            float grossSalary) {

        System.out.println("Basic Salary: " + salary);
        System.out.println("HRA: " + hra);
        System.out.println("DA: " + da);
        System.out.println("Transport Allowance: " + transport);
        System.out.println("Gross Salary: " + grossSalary);
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        float salary = getSalary(sc);

        float hra = calculateHRA(salary);

        float da = calculateDA(salary);

        float transport = calculateTransport(salary);

        float grossSalary = calculateGrossSalary(
                salary, hra, da, transport
        );

        displaySalaryDetails(
                salary, hra, da, transport, grossSalary
        );

        sc.close();
    }
}
