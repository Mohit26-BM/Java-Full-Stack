public abstract class SalaryEmployee {

    private int id;
    private String name;

    /*
    Encapsulation
    Employee data is private and accessed through getters.
    */

    SalaryEmployee(int id, String name)
            throws InvalidEmployeeDataException {

        if (id <= 0) {
            throw new InvalidEmployeeDataException(
                    "Employee ID must be greater than 0");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new InvalidEmployeeDataException(
                    "Employee name cannot be empty");
        }

        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /*
    Abstract method
    Each employee type provides its own salary calculation.
    */

    public abstract double calculateMonthlySalary();

    public double calculateAnnualSalary() {
        return calculateMonthlySalary() * 12;
    }

    public void displayEmployeeDetails() {

        System.out.println("Employee ID: " + id);
        System.out.println("Employee Name: " + name);
        System.out.println("Employee Type: "
                + getClass().getSimpleName());
        System.out.println("Monthly Compensation: "
                + calculateMonthlySalary());
        System.out.println("Annual Compensation: "
                + calculateAnnualSalary());
    }
}