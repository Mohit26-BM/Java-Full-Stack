public class PermanentSalaryEmployee extends SalaryEmployee {

    private double basicSalary;

    /*
    Permanent employee salary:
    Basic Salary + 20% HRA + 10% DA
    */

    PermanentSalaryEmployee(int id, String name, double basicSalary)
            throws InvalidEmployeeDataException {

        super(id, name);

        if (basicSalary <= 0) {
            throw new InvalidEmployeeDataException(
                    "Basic salary must be greater than 0");
        }

        this.basicSalary = basicSalary;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    @Override
    public double calculateMonthlySalary() {

        double hra = basicSalary * 0.20;
        double da = basicSalary * 0.10;

        return basicSalary + hra + da;
    }
}