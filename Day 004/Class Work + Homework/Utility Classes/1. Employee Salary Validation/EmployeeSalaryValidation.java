
public class EmployeeSalaryValidation {
	private int id;
    private String name;
    private String department;
    private double salary;
    
    public EmployeeSalaryValidation (int id, String name, String department, double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }
    public double getid() {
        return id;
    }

    public String getName() {
        return name;
    }
    public double getSalary() {
        return salary;
    }

    public String getDepartment() {
        return department;
    }
}
