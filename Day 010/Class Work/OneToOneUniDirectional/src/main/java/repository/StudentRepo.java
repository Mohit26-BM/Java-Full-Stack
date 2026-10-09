
package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import entity.StudentInformation;

public interface StudentRepo
        extends JpaRepository<StudentInformation, Integer> {
}
