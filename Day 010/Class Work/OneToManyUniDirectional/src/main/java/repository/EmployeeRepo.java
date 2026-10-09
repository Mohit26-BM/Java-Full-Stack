
package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import entity.Employee;

public interface EmployeeRepo
        extends JpaRepository<Employee, Integer> {
}
