import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class EmployeeIdSupplier {

    public static void main(String[] args) {

        /*
        AtomicInteger maintains the employee ID sequence
        starting from 1001.
        */

        AtomicInteger employeeNumber = new AtomicInteger(1001);


        /*
        Supplier generates an employee ID
        without receiving any input.
        */

        Supplier<String> employeeIdSupplier =
                () -> "EMP" + employeeNumber.getAndIncrement();


        /*
        Generate five employee IDs
        */

        System.out.println(employeeIdSupplier.get());
        System.out.println(employeeIdSupplier.get());
        System.out.println(employeeIdSupplier.get());
        System.out.println(employeeIdSupplier.get());
        System.out.println(employeeIdSupplier.get());
    }
}