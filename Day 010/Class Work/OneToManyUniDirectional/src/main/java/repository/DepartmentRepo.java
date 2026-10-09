
package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import entity.Department;

public interface DepartmentRepo
        extends JpaRepository<Department, Integer> {
}
