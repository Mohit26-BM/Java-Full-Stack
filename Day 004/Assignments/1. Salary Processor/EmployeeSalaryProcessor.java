public class EmployeeSalaryProcessor {

    public static void main(String[] args) {

        try {

            /*
            Creating different employee types.
            */

            SalaryEmployee permanentEmployee =
                    new PermanentSalaryEmployee(
                            101,
                            "Ravi",
                            50000
                    );

            SalaryEmployee contractEmployee =
                    new ContractSalaryEmployee(
                            102,
                            "Priya",
                            60000
                    );

            SalaryEmployee internEmployee =
                    new InternSalaryEmployee(
                            103,
                            "Amit",
                            20000
                    );


            /*
            Polymorphism
            Same parent reference is used for different
            employee objects.
            */

            System.out.println("PERMANENT EMPLOYEE");
            permanentEmployee.displayEmployeeDetails();

            System.out.println();
            System.out.println("-----------------------------");
            System.out.println();


            System.out.println("CONTRACT EMPLOYEE");
            contractEmployee.displayEmployeeDetails();

            System.out.println();
            System.out.println("-----------------------------");
            System.out.println();


            System.out.println("INTERN EMPLOYEE");
            internEmployee.displayEmployeeDetails();

            System.out.println();
            System.out.println("-----------------------------");


            /*
            Testing invalid input.
            */

            System.out.println();
            System.out.println("INVALID INPUT TEST");

            SalaryEmployee invalidEmployee =
                    new PermanentSalaryEmployee(
                            104,
                            "Neha",
                            -10000
                    );

        }
        catch (InvalidEmployeeDataException e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }
    }
}