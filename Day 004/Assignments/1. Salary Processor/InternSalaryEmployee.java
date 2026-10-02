public class InternSalaryEmployee extends SalaryEmployee {

    private double stipend;

    /*
    Intern receives a fixed monthly stipend.
    */

    InternSalaryEmployee(int id, String name, double stipend)
            throws InvalidEmployeeDataException {

        super(id, name);

        if (stipend <= 0) {
            throw new InvalidEmployeeDataException(
                    "Intern stipend must be greater than 0");
        }

        this.stipend = stipend;
    }

    public double getStipend() {
        return stipend;
    }

    @Override
    public double calculateMonthlySalary() {
        return stipend;
    }
}